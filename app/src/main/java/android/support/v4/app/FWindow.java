package android.support.v4.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.Image;
import android.os.Environment;
import android.support.v4.app.Adapter.ObjectAdapter;
import android.support.v4.app.reflectmaster.Utils.Utils;
import android.support.v4.app.widget.ReflectView2;
import android.support.v4.app.widget.ViewLineClickListener;
import android.support.v4.app.widget.ViewLineView;
import android.support.v4.app.widget.WindowList;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class FWindow {

    private XC_LoadPackage.LoadPackageParam lpparam;
    private XC_MethodHook.MethodHookParam param;
    private Context act;
    private Object obj;
    private int startX = 0, startY = 0, nowX = 0, nowY = 0;
    private LinearLayout layout;
    private WindowManager.LayoutParams layoutParam = new WindowManager.LayoutParams();
    private View floating;
    private WindowManager wm;
    private boolean isMenu = true;


    public FWindow(XC_LoadPackage.LoadPackageParam lpparam, XC_MethodHook.MethodHookParam method) {
        if (!MasterUtils.isFloating) return;
        this.lpparam = lpparam;
        this.param = method;
        act = (Context) method.thisObject;
        obj = act;
        init();
    }

//    public FWindow(Context activity, Dialog dialog) {
//        if (!MasterUtils.isFloating) return;
//        act = activity;
//        obj = act;
//        this.dialog = dialog;
//        if (dialog != null)
//            Toast.makeText(act, "创建Dialog窗口", Toast.LENGTH_SHORT).show();
//        else
//            Toast.makeText(act, "创建测试窗口", Toast.LENGTH_SHORT).show();
//
//        init();
//    }

    public FWindow(XC_LoadPackage.LoadPackageParam lpparam, XC_MethodHook.MethodHookParam method, Object object) {
        if (!MasterUtils.isFloating) return;
        this.lpparam = lpparam;
        this.param = method;
        try {
            Method getActivity = (object.getClass().getDeclaredMethod("getActivity", new Class[]{}));
            this.act = (Activity) getActivity.invoke(object, new Object[]{});
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.obj = object;
        init();
    }


    private Button newButton(Context context) {
        Button button = new Button(context);
        button.setBackgroundColor(Color.WHITE);
        button.setTextColor(Color.BLACK);
        return button;
    }

    private void getscreensize(WindowManager.LayoutParams lp) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        lp.width = displayMetrics.widthPixels / 2;
        lp.height = (displayMetrics.heightPixels / 4) * 3;
    }




    @SuppressLint("SetTextI18n")
    private void init() {
//        ResourceLoader loader
        wm = (WindowManager) act.getSystemService(Context.WINDOW_SERVICE);
        layoutParam.width = 500;
        layoutParam.height = 500;
        layoutParam.x = 0;
        layoutParam.y = 0;
        layoutParam.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParam.type = WindowManager.LayoutParams.TYPE_APPLICATION;
        layoutParam.format = PixelFormat.RGBA_8888;
//
//
        layout = new LinearLayout(act);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(Color.rgb(255, 250, 250));

        TextView text = new TextView(act);
        layout.addView(text);
        text.setText(act.getClass().getName());
        text.setOnClickListener((v) -> Utils.writeClipboard(act, text.getText().toString()));
        text.setTextColor(Color.BLACK);
        text.setOnClickListener(p1 -> {
            showActivity();
        });

        Button field = newButton(act);

        field.setText("当前Activity");

        layout.addView(field);
        field.setOnClickListener(p1 -> {
            FieldWindow fw = new FieldWindow(lpparam, param, act, obj);
            fw.show(wm, layoutParam);
        });


//        Button dialogbutton = null;
//        if (dialog != null) {
//            dialogbutton = newButton(act);
//
//            dialogbutton.setText("当前Dialog");
//
//            layout.addView(dialogbutton);
//            dialogbutton.setOnClickListener(new OnClickListener() {
//
//                @Override
//                public void onClick(View p1) {
//
//                    FieldWindow fw = new FieldWindow(lpparam, param, act, dialog);
//                    fw.show(wm, layoutParam);
//                }
//            });
//        }
        Button res = newButton(act);

        res.setText("View获取(子)");

        layout.addView(res);
        res.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View p1) {
                wm.removeView(layout);
//                loadViews(false);

            }


        });
//		Button res2 =new Button(act);
//
//		res2.setText("View获取2");
//		res2.setTextColor(Color.RED);
//		layout.addView(res2);
//		res2.setOnClickListener(new OnClickListener(){
//
//				@Override
//				public void onClick(View p1)
//				{
//					wm.removeView(layout);
//					loadViews(true);
//
//				}
//
//
//			});
//
        Button myfield = newButton(act);

        myfield.setText("我的变量");

        layout.addView(myfield);
        myfield.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View p1) {
                showObjects("我的变量", MasterUtils.objects);

            }
        });
//		Button service =new Button(act);
//
//		service.setText("Service");
//		service.setTextColor(Color.RED);
//		layout.addView(service);
//		service.setOnClickListener(new OnClickListener(){
//
//				@Override
//				public void onClick(View p1)
//				{
//					showObjects(",服务",MasterUtils.serviceobjects);
//
//				}
//			});
//
        Button hide = newButton(act);
        hide.setText("隐藏");
        layout.addView(hide);
        hide.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View p1) {
                swicthWindow(false);
            }
        });

        Button exit = newButton(act);

        exit.setText("退出");

        layout.addView(exit);
        exit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View p1) {
                swicthWindow(true);
            }
        });

        swicthWindow(false);


//		floating.setOnClickListener(new OnClickListener(){
//
//				@Override
//				public void onClick(View p1)
//				{
//					
//				}
//			});


    }

    private void showActivity() {
        final SharedPreferences sp = act.getSharedPreferences("actlast", act.MODE_PRIVATE);

        PackageManager pm = act.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(act.getPackageName(), pm.GET_ACTIVITIES);

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
                    String labei = act.getResources().getString(id);
                    name.add(labei);
                } else {
                    name.add(actname);
                }
            }
            int lastClick = -1;
            lastClick = sp.getInt("select", -1);
            WindowList wlist = new WindowList(act, wm);
            wlist.setItems(name);
            wlist.setTitle("Activity启动");

            wlist.setListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
                    sp.edit().putInt("select", p3).commit();
                    try {
                        Intent i = new Intent(act, act.getClassLoader().loadClass(aname.get(p3)));
                        act.startActivity(i);
                    } catch (ClassNotFoundException e) {
                        Toast.makeText(act, e.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            });
            wlist.show();
            if (lastClick != -1) {
                wlist.getListView().setSelection(lastClick);
            }

        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(act, e.toString(), Toast.LENGTH_LONG).show();

        }


    }

    private void showObjects(String title, final List<Object> obs) {
        if (obs.size() == 0) {
            Toast.makeText(act, "没有对象。", Toast.LENGTH_SHORT).show();
            return;
        }
        WindowList wlist = new WindowList(act, wm);
//		String[] names = new String[obs.size()];
//		for(int i = 0;i<obs.size();i+=1)
//		{
//			if(obs.get(i)==null)
//				names[i]="null";
//			else
//				names[i]="v"+i+"  "+obs.get(i).getClass().getCanonicalName();
//		}
//
        ObjectAdapter oba = new ObjectAdapter(act, obs);

        //wlist.setItems(names);
        wlist.setAdaptet(oba);
        wlist.setTitle(title);
        wlist.setListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
                FieldWindow.newWindow(lpparam, param, act, obs.get(p3), wm);

            }
        });
        wlist.show(-2, -2);

    }

    private View getFLoat(Context context) {
        String resourcePath = Environment.getExternalStorageDirectory().toString() + "/reflectmaster/a.apk";
        AssetManager mAsset = null;
        try {
            mAsset = AssetManager.class.newInstance();
            @SuppressLint("PrivateApi")
            Method method = mAsset.getClass().getDeclaredMethod("addAssetPath", String.class);
            method.setAccessible(true);
            method.invoke(mAsset, resourcePath);
            Resources pluginResources = new Resources(mAsset, this.act.getResources().getDisplayMetrics(), this.act.getResources().getConfiguration());
            PackageInfo packageInfo = this.act.getPackageManager().getPackageArchiveInfo(resourcePath, PackageManager.GET_ACTIVITIES);
            int float_0 = pluginResources.getIdentifier("float_0", "layout", packageInfo.packageName);
            XmlResourceParser xmlResourceParser = pluginResources.getXml(float_0);
            return LayoutInflater.from(context).inflate(xmlResourceParser, null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }


    private void swicthWindow(boolean isExit) {
        if (isMenu) {
            if (floating != null)
                wm.removeView(layout);
            layoutParam.width = 100;
            layoutParam.height = 100;
            if (!isExit) {
                if (floating == null) {
                    ImageView imageView = new ImageView(act);
                    imageView.setImageBitmap(BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().toString()+"/ReflectMaster/icon.png"));
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

                                    wm.updateViewLayout(floating, layoutParam);
                                    startX = nowX;
                                    startY = nowY;
                                    break;
                                case MotionEvent.ACTION_UP:

                                    nowX = (int) p2.getRawX();
                                    nowY = (int) p2.getRawY();
                                    //floating.setRotate(false);
                                    //Log.d("ReflectView2",""+Math.pow( Math.abs( nowX-x) ,2)+Math.pow( Math.abs( nowY-y),2));
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
                wm.addView(floating, layoutParam);
            }
            isMenu = false;

        } else {
            if (floating != null)
                wm.removeView(floating);
            layoutParam.width = 400;
            layoutParam.height = -2;
            if (!isExit)
                wm.addView(layout, layoutParam);
            isMenu = true;
        }
    }


    private void showViewsLine(Window window, boolean includeViewGroup) {
        List<View> list = getAllChildViews(window.getDecorView());
        for (int i = 0; i < list.size(); i += 1) {
            if (includeViewGroup == true)
                if (list.get(i) instanceof ViewGroup) {
                    list.remove(i);
                    i = 0;
                }
        }

        final WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.type = lp.TYPE_APPLICATION;
        lp.flags = lp.FLAG_NOT_FOCUSABLE;
        lp.width = -1;
        lp.height = -1;

        lp.format = PixelFormat.RGBA_8888;

        final ViewLineView vlv = new ViewLineView(act, list);
        vlv.setListener(new ViewLineClickListener() {

            @Override
            public void onClick(ViewLineView obj, List<View> views) {
                List<Object> r = new ArrayList<>();
                for (View view : views) r.add(view);
                //if(r.size()>0)
                //	{
                //if(!(views.get(0) instanceof  ViewGroup))
                //views.get(0).setBackgroundColor(Color.RED);
                // }
                showObjects("点击范围内的views", r);
            }


            @Override
            public void onLongClick(ViewLineView obj) {
                wm.removeView(vlv);
                wm.addView(layout, layoutParam);
            }
        });

        wm.addView(vlv, lp);
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

//    private void loadViews(boolean p0) {
//        if (dialog != null)
//            showViewsLine(dialog.getWindow(), p0);
//        else
//            showViewsLine(MasterUtils.nowAct.getWindow(), p0);
//    }


}
