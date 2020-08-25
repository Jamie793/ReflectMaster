package com.jamiexu.app.reflectmaster.j;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jamiexu.app.reflectmaster.j.Adapter.ObjectAdapter;
import com.jamiexu.app.reflectmaster.j.reflectmaster.Utils.Utils;
import com.jamiexu.app.reflectmaster.j.widget.ViewLineClickListener;
import com.jamiexu.app.reflectmaster.j.widget.ViewLineView;
import com.jamiexu.app.reflectmaster.j.widget.WindowList;

import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class FWindow {

    private Object object;
    private View floating;
    private Context activity;
    private LinearLayout layout;
    private boolean isMenu = true;
    private WindowManager windowManager;
    private XC_MethodHook.MethodHookParam param;
    private XC_LoadPackage.LoadPackageParam lpparam;
    private int startX = 0, startY = 0, nowX = 0, nowY = 0;
    private WindowManager.LayoutParams layoutParam = new WindowManager.LayoutParams();


    public FWindow(XC_LoadPackage.LoadPackageParam lpparam, XC_MethodHook.MethodHookParam method) {
        this(lpparam, method, null);
    }

    public FWindow(XC_LoadPackage.LoadPackageParam lpparam, XC_MethodHook.MethodHookParam method, Activity activity) {
        if (!MasterUtils.isFloating) return;
        this.lpparam = lpparam;
        this.param = method;
        if (activity != null) {
            this.activity = activity;
            this.object = activity;
        }
        init();
    }


    private Button newButton(Context context) {
        Button button = new Button(context);
        button.setBackgroundColor(Color.WHITE);
        button.setTextColor(Color.BLACK);
        return button;
    }


    @SuppressLint("SetTextI18n")
    private void init() {
        this.windowManager = (WindowManager) this.activity.getSystemService(Context.WINDOW_SERVICE);
        layoutParam.width = 500;
        layoutParam.height = 500;
        layoutParam.x = 0;
        layoutParam.y = 0;
        layoutParam.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParam.type = WindowManager.LayoutParams.TYPE_APPLICATION;
        layoutParam.format = PixelFormat.RGBA_8888;

        layout = new LinearLayout(this.activity);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(Color.rgb(255, 250, 250));

        TextView text = new TextView(this.activity);
        layout.addView(text);
        text.setText(this.activity.getClass().getName());
        text.setOnClickListener((v) -> Utils.writeClipboard(this.activity, text.getText().toString()));
        text.setTextColor(Color.BLACK);
        text.setOnClickListener(p1 -> {
            showActivity();
        });

        Button field = newButton(this.activity);
        field.setText("Current Activity");
        layout.addView(field);
        field.setOnClickListener(p1 -> {
            FieldWindow fw = new FieldWindow(lpparam, param, this.activity, this.object);
            fw.show(this.windowManager, layoutParam);
        });


        Button res = newButton(this.activity);
        res.setText("View views");
        layout.addView(res);
        res.setOnClickListener(p1 -> {
            this.windowManager.removeView(layout);
            loadViews(false);
        });

        Button hide = newButton(this.activity);
        hide.setText("Hide");
        layout.addView(hide);
        hide.setOnClickListener(p1 -> swicthWindow(false));

        Button exit = newButton(this.activity);
        exit.setText("Exit");
        layout.addView(exit);
        exit.setOnClickListener(p1 -> swicthWindow(true));

        swicthWindow(false);
    }

    private void showActivity() {
        final SharedPreferences sp = this.activity.getSharedPreferences("actlast", Context.MODE_PRIVATE);

        PackageManager pm = this.activity.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(this.activity.getPackageName(), PackageManager.GET_ACTIVITIES);

            ActivityInfo[] acs = info.activities;
            List<String> name = new ArrayList<String>();
            final List<String> aname = new ArrayList<String>();

            for (ActivityInfo a : acs) {
                int id = a.labelRes;
                String actname;
                if (a.name.startsWith(".")) {
                    actname = a.packageName + a.name;
                } else {
                    actname = a.name;
                }
                aname.add(actname);
                if (id != 0) {
                    String labei = this.activity.getResources().getString(id);
                    name.add(labei);
                } else {
                    name.add(actname);
                }
            }
            int lastClick = -1;
            lastClick = sp.getInt("select", -1);
            WindowList wlist = new WindowList(this.activity, this.windowManager);
            wlist.setItems(name);
            wlist.setTitle("Activity启动");
            wlist.setListener((p1, p2, p3, p4) -> {
                sp.edit().putInt("select", p3).apply();
                try {
                    Intent i = new Intent(this.activity, this.activity.getClassLoader().loadClass(aname.get(p3)));
                    this.activity.startActivity(i);
                } catch (ClassNotFoundException e) {
                    Toast.makeText(this.activity, e.toString(), Toast.LENGTH_LONG).show();
                }
            });
            wlist.show();
            if (lastClick != -1) {
                wlist.getListView().setSelection(lastClick);
            }

        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(this.activity, e.toString(), Toast.LENGTH_LONG).show();

        }


    }

    private void showObjects(final List<View> views) {
        if (views.size() == 0) {
            Toast.makeText(this.activity, "Nothing", Toast.LENGTH_SHORT).show();
            return;
        }
        WindowList wlist = new WindowList(this.activity, this.windowManager);
        ObjectAdapter objectAdapter = new ObjectAdapter(this.activity, views);
        wlist.setAdaptet(objectAdapter);
        wlist.setTitle("   Tap Views");
        wlist.getListView().setDividerHeight(15);
        wlist.setListener((p1, p2, p3, p4) ->
                FieldWindow.newWindow(lpparam, param, this.activity, views.get(p3),
                        this.windowManager));
        wlist.show(-2, -2);

    }

    private void swicthWindow(boolean isExit) {
        if (isMenu) {
            if (floating != null)
                this.windowManager.removeView(layout);
            layoutParam.width = 100;
            layoutParam.height = 100;
            if (!isExit) {
                if (floating == null) {
                    ImageView imageView = new ImageView(this.activity);
                    imageView.setImageBitmap(BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().toString() + "/ReflectMaster/icon.png"));
                    floating = imageView;
                    floating.setOnTouchListener(new OnTouchListener() {
                        int x;
                        int y;

                        @Override
                        public boolean onTouch(View p1, MotionEvent p2) {
                            int actipn = p2.getAction();
                            switch (actipn) {
                                case MotionEvent.ACTION_DOWN:
                                    startX = (int) p2.getRawX();
                                    startY = (int) p2.getRawY();
                                    x = startX;
                                    y = startY;
                                    break;
                                case MotionEvent.ACTION_MOVE:
                                    nowX = (int) p2.getRawX();
                                    nowY = (int) p2.getRawY();
                                    layoutParam.x += nowX - startX;
                                    layoutParam.y += nowY - startY;
                                    windowManager.updateViewLayout(floating, layoutParam);
                                    startX = nowX;
                                    startY = nowY;
                                    break;
                                case MotionEvent.ACTION_UP:
                                    nowX = (int) p2.getRawX();
                                    nowY = (int) p2.getRawY();
                                    if (Math.sqrt(Math.pow(Math.abs(nowX - x), 2) + Math.pow(Math.abs(nowY - y), 2)) < 170)
                                        swicthWindow(false);
                                    else {
                                    }
                                    break;
                            }
                            return true;
                        }
                    });

                }
                this.windowManager.addView(floating, layoutParam);
            }
            isMenu = false;
        } else {
            if (floating != null)
                this.windowManager.removeView(floating);
            layoutParam.width = 400;
            layoutParam.height = -2;
            if (!isExit)
                this.windowManager.addView(layout, layoutParam);
            isMenu = true;
        }
    }


    private void showViewsLine(Window window, boolean includeViewGroup) {
        List<View> list = getAllChildViews(window.getDecorView());
        for (int i = 0; i < list.size(); i += 1) {
            if (includeViewGroup)
                if (list.get(i) instanceof ViewGroup) {
                    list.remove(i);
                    i = 0;
                }
        }

        final WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.type = WindowManager.LayoutParams.TYPE_APPLICATION;
        lp.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        lp.width = -1;
        lp.height = -1;

        lp.format = PixelFormat.RGBA_8888;

        final ViewLineView vlv = new ViewLineView(this.activity, list, new ViewLineClickListener() {
            @Override
            public void onClick(ViewLineView obj, List<View> views) {
                showObjects(views);
            }

            @Override
            public void onLongClick(ViewLineView obj) {
                windowManager.removeView(obj);
                windowManager.addView(layout, layoutParam);
            }
        });

        this.windowManager.addView(vlv, lp);
    }

    private List<View> getAllChildViews(View view) {
        List<View> allchildren = new ArrayList<View>();
        if (view instanceof ViewGroup) {
            ViewGroup vp = (ViewGroup) view;
            for (int i = 0; i < vp.getChildCount(); i++) {
                View viewchild = vp.getChildAt(i);
                allchildren.add(viewchild);
                //再次 调用本身（递归）
                allchildren.addAll(getAllChildViews(viewchild));
            }
        }
        return allchildren;
    }

    private void loadViews(boolean p0) {
        showViewsLine(MasterUtils.nowAct.getWindow(), p0);
    }


}
