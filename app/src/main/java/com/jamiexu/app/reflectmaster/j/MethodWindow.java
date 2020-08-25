package com.jamiexu.app.reflectmaster.j;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jamiexu.app.reflectmaster.j.Adapter.MethodAdapter;
import com.jamiexu.app.reflectmaster.j.reflectmaster.Utils.Utils;
import com.jamiexu.app.reflectmaster.j.widget.WindowList;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MethodWindow extends Window implements OnItemClickListener {

//    Update by Jamiexu 2020-08-25

    private Method[] methods;
    private WindowManager wm;
    private WindowManager.LayoutParams lp;
    private ListView list;
    private MethodAdapter adapter;
    private boolean isDeclared;

    public MethodWindow(XC_LoadPackage.LoadPackageParam lpparam, XC_MethodHook.MethodHookParam param, Context act, Object object) {
        super(lpparam, param, act, object);
    }

    @Override
    public void onItemClick(final AdapterView<?> p1, View p2, final int p3, long p4) {
        WindowManager am = (WindowManager) act.getSystemService(Context.WINDOW_SERVICE);
        WindowList wlist = new WindowList(act, am);
        wlist.setTitle("方法操作");
        wlist.setItems(new String[]{"运行", "临时保存起来", "添加到寄存器", "复制函数名称", "复制类名和函数名", "复制类和函数名(hook脚本使用)"});
        wlist.setListener((adap, view, posi, l) -> {

            Method m = (Method) p1.getItemAtPosition(p3);
            if (posi == 0) {
                if (!m.isAccessible()) m.setAccessible(true);
                runMethod(m);
            } else if (posi == 1) {
                m.setAccessible(true);
                MasterUtils.add(act, m);
            } else if (posi == 2) {
                m.setAccessible(true);
                MasterUtils.addHashMap(act, m);
            } else if (posi == 3) {
                Utils.writeClipboard(act, m.toGenericString());
                Toast.makeText(act, "复制成功:" + m.toGenericString(), Toast.LENGTH_SHORT).show();

            } else if (posi == 4) {
                Utils.writeClipboard(act, m.getDeclaringClass().getCanonicalName() + "'," + "'" + m.toGenericString() + "'");
                Toast.makeText(act, "复制成功:" + m.toGenericString(), Toast.LENGTH_SHORT).show();
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append(m.getDeclaringClass().getCanonicalName());
                sb.append(" ");
                sb.append(m.getName());
                for (Class<?> clz : m.getParameterTypes()) {
                    sb.append(" ");
                    sb.append(clz.getCanonicalName());

                }
                Utils.writeClipboard(act, sb.toString());
                Toast.makeText(act, "复制成功:" + m.toGenericString(), Toast.LENGTH_SHORT).show();
            }
        });
        wlist.show();

    }

    //运行函数
    @SuppressLint("SetTextI18n")
    void runMethod(final Method m) {

        ArrayList<EditText> editTextArrayList = new ArrayList<>();
        ArrayList<String> typeArrayList = new ArrayList<>();
        ArrayList<Object> valueArrayList = new ArrayList<>();

        WindowManager windowManager = (WindowManager) act.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.width = 400;
        layoutParams.height = 400;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;

        final LinearLayout layout = new LinearLayout(act);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(0xFF303030);
        ActionWindow actionWindow = new ActionWindow(act, wm, lp, layout);
        layout.addView(actionWindow.getActionBar());

        Class<?>[] paramterTypes = m.getParameterTypes();
        for (Class<?> c : paramterTypes) {
            EditText editText = new EditText(this.act);
            editText.setHint("输入参数值");
            editText.setWidth(lp.width);
            editTextArrayList.add(editText);
            typeArrayList.add(c.getCanonicalName());
            layout.addView(editText);
        }
        Button button = new Button(this.act);
        button.setText("RUN");
        button.setTextColor(Color.WHITE);
        button.setWidth(lp.width);
        button.setBackgroundColor(0xFF2196F3);
        button.setOnClickListener(v -> {
            for (int i = 0; i < editTextArrayList.size(); i++) {
                valueArrayList.add(MasterUtils.parseValue(typeArrayList.get(i), editTextArrayList.get(i).
                        getText().toString()));
            }

            Utils.showToast(act, Arrays.toString(valueArrayList.toArray(new Object[0])),
                    Toast.LENGTH_SHORT);
            try {
                Object result = m.invoke(object, valueArrayList.toArray(new Object[0]));
                if (result == null) {
                    Utils.showToast(act, "result is void...", Toast.LENGTH_SHORT);
                } else {
                    FieldWindow.newFieldWindow(lpparam, param, act, result, windowManager);
                    Utils.showToast(act, "result is opened", Toast.LENGTH_SHORT);
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            valueArrayList.clear();
        });


        layout.addView(button);
        windowManager.addView(layout, layoutParams);
    }



    @SuppressLint("SetTextI18n")
    @Override
    public void show(final WindowManager manager, WindowManager.LayoutParams lpq) {
        wm = manager;
        this.lp = new WindowManager.LayoutParams();
        lp.type = WindowManager.LayoutParams.TYPE_APPLICATION;

        lp.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        final LinearLayout root = new LinearLayout(act);
        root.setBackgroundColor(0xFF303030);
        root.setOrientation(LinearLayout.VERTICAL);

        ActionWindow acw = new ActionWindow(act, manager, lp, root);
        root.addView(acw.getActionBar());

        TextView title = new TextView(act);
        title.setText("    All Methods");
        title.setTextSize(16);
        title.setWidth(root.getWidth());
        title.setTextColor(Color.WHITE);
        root.addView(title);

        EditText editText = new EditText(act);
        editText.setHint("Filter methods...");
        editText.setTextSize(14);
        editText.setWidth(root.getWidth());
        editText.setHintTextColor(Color.WHITE);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0)
                    list.clearTextFilter();
                list.setFilterText(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        root.addView(editText);


        Button undeclare = new Button(act);
        undeclare.setText("P");
        undeclare.setTextColor(Color.WHITE);
        undeclare.setBackground(null);
        undeclare.setOnClickListener(p1 -> {
            if (this.isDeclared) {
                undeclare.setText("A");
                this.methods = this.object.getClass().getDeclaredMethods();
                this.adapter.setMethods(this.methods);
                this.adapter.notifyDataSetChanged();
                this.isDeclared = false;
            } else {
                undeclare.setText("P");
                this.methods = this.object.getClass().getMethods();
                this.adapter.setMethods(this.methods);
                this.adapter.notifyDataSetChanged();
                this.isDeclared = true;
            }
        });
        acw.addView(undeclare);


        list = new ListView(act);
        list.setTextFilterEnabled(true);
        list.setFastScrollEnabled(true);
        list.setOnItemClickListener(this);
        this.methods = this.object.getClass().getMethods();
        this.adapter = new MethodAdapter(this.act, this.methods);
        this.isDeclared = true;
        list.setAdapter(this.adapter);
        list.setDividerHeight(15);
        root.addView(list);

        manager.addView(root, lp);
    }


}
