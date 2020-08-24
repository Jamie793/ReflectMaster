package com.jamiexu.app.reflectmaster.j;

/**
 * Created by Jamiexu on 2020
 * -08-24.
 */


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jamiexu.app.reflectmaster.j.Adapter.ConstructorAdapter;
import com.jamiexu.app.reflectmaster.j.reflectmaster.Utils.Utils;
import com.jamiexu.app.reflectmaster.j.widget.WindowList;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

;

public class ConstructorWindow extends Window implements AdapterView.OnItemClickListener {
    private Object[] values;
    private EditText[] valuesEdit = null;
    private Constructor<?>[] methods;
    private WindowManager windowManager;
    private WindowManager.LayoutParams lp;
    private ActionWindow acw;
    private ListView list;
    private ConstructorAdapter adapter;
    private boolean isDeclared;

    public ConstructorWindow(XC_LoadPackage.LoadPackageParam lpparam,
                             XC_MethodHook.MethodHookParam param, Context act, Object object) {
        super(lpparam, param, act, object);
    }

    @Override
    public void onItemClick(final AdapterView<?> p1, View p2, final int p3, long p4) {
        WindowManager am = (WindowManager) act.getSystemService(Context.WINDOW_SERVICE);
        WindowList wlist = new WindowList(act, am);
        wlist.setTitle("函数操作");
        wlist.setItems(new String[]{"运行", "临时保存起来", "添加到寄存器", "复制函数名称", "复制类名和函数名", "复制类和函数名(hook脚本使用)"});
        wlist.setListener((adap, view, posi, l) -> {

            Constructor<?> m = (Constructor<?>) p1.getItemAtPosition(p3);
            if (posi == 0) {
                if (!m.isAccessible()) m.setAccessible(true);
                runMethod(m);
            } else if (posi == 1) {
                if (!m.isAccessible()) m.setAccessible(true);
                MasterUtils.add(act, m);
            } else if (posi == 2) {
                if (!m.isAccessible()) m.setAccessible(true);
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

    void runMethod(final Constructor<?> m) {
        values = new Object[]{};
        m.getParameterTypes();
        WindowManager am = (WindowManager) act.getSystemService(Context.WINDOW_SERVICE);
        final LinearLayout l = new LinearLayout(act);
        l.setBackgroundColor(Color.BLACK);
        ActionWindow ac = new ActionWindow(act, this.windowManager, lp, l);


        l.setOrientation(LinearLayout.VERTICAL);
        l.addView(ac.getActionBar());

        if (m.getParameterTypes().length > 0) {
            values = new Object[m.getParameterTypes().length];

            valuesEdit = new EditText[m.getParameterTypes().length];
            int p = 0;
            for (Class type : m.getParameterTypes()) {
                valuesEdit[p] = new EditText(act);
                valuesEdit[p].setTextColor(Color.RED);
                valuesEdit[p].setHint(type.getCanonicalName());
                valuesEdit[p].setOnLongClickListener(new Listener(valuesEdit[p]));
                p += 1;

            }
        }
        Button run = new Button(act);
        run.setText("运行");
        run.setOnClickListener(p1 -> {

            if (m.getParameterTypes().length > 0)
                for (int i = 0; i < m.getParameterTypes().length; i += 1) {
                    Class c = m.getParameterTypes()[i];
                    String s = valuesEdit[i].getText().toString();
                    if (s.startsWith("$F")) {
                        values[i] = MasterUtils.objects.get(Integer.parseInt(s.substring(2)));
                    } else {
                        switch (c.getCanonicalName()) {
                            case "int":
                                int va = Integer.parseInt(s);
                                values[i] = va;
                                break;
                            case "boolean":
                                if (s.equals("true")) values[i] = true;
                                else values[i] = false;
                                break;
                            case "long":
                                long lo = Long.parseLong(s);
                                values[i] = lo;
                                break;
                            default:
                                values[i] = s;
                        }

                    }


                }

            Object result = null;
            try {
                result = m.newInstance(values);
            } catch (Exception e) {
                Toast.makeText(act, e.toString(), Toast.LENGTH_SHORT).show();
            }

            this.windowManager.removeView(l);
            LogUtils.loge("构造:" + m.getName() + " result:" + result);

            if (result == null) {
                Toast.makeText(act, "result is null", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(act, result.getClass().getCanonicalName() + " ,正在打开结果,result is" + result, Toast.LENGTH_SHORT).show();
                FieldWindow.newWindow(lpparam, param, act, result, this.windowManager);


            }
        });
        l.addView(run);
        lp.width = 400;
        lp.height = 400;
        lp.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        this.windowManager.addView(l, lp);
    }

    private void runMethod_showVar(final EditText edit) {

        WindowList elist = new WindowList(act, this.windowManager);
        elist.setTitle("selece var");
        List<String> name = new ArrayList<String>();
        for (Object o : MasterUtils.objects) {

            name.add(o.getClass().getCanonicalName());

        }
        elist.setItems(name);
        elist.setListener(new AdapterView.OnItemClickListener() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
                edit.setText("$F" + p3);

            }
        });
        elist.show();

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void show(final WindowManager manager, WindowManager.LayoutParams lpq) {
        this.windowManager = manager;
        this.lp = new WindowManager.LayoutParams();
        lp.type = WindowManager.LayoutParams.TYPE_APPLICATION;
        lp.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        final LinearLayout root = new LinearLayout(act);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(0xFF303030);

        acw = new ActionWindow(act, manager, lp, root);
        root.addView(acw.getActionBar());

        TextView title = new TextView(act);
        title.setText("    All Constructors");
        title.setTextSize(16);
        title.setWidth(root.getWidth());
        title.setTextColor(Color.WHITE);
        root.addView(title);

        EditText editText = new EditText(act);
        editText.setHint("Filter constructors...");
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

        list = new ListView(act);
        list.setDividerHeight(15);
        list.setTextFilterEnabled(true);
        list.setFastScrollEnabled(true);
        LinearLayout buttonLayout = new LinearLayout(act);

        Button undeclare = new Button(act);
        undeclare.setText("P");
        undeclare.setTextColor(Color.WHITE);
        undeclare.setBackground(null);
        undeclare.setOnClickListener(p1 -> {
            if (this.isDeclared) {
                methods = object.getClass().getDeclaredConstructors();
                adapter.setMethods(methods);
                adapter.notifyDataSetChanged();
                this.isDeclared = false;
                undeclare.setText("P");
            } else {
                methods = object.getClass().getConstructors();
                adapter.setMethods(methods);
                adapter.notifyDataSetChanged();
                this.isDeclared = true;
                undeclare.setText("A");
            }

        });
        acw.addView(undeclare);


        HorizontalScrollView ho = new HorizontalScrollView(act);
        ho.addView(buttonLayout);
        root.addView(ho);


        methods = object.getClass().getConstructors();
        this.isDeclared = true;
        adapter = new ConstructorAdapter(act, methods);
        list.setAdapter(adapter);
        list.setOnItemClickListener(this);
        root.addView(list);
        manager.addView(root, lp);

    }


    class Listener implements View.OnLongClickListener {

        @Override
        public boolean onLongClick(View p1) {
            runMethod_showVar(edit);
            return true;
        }


        EditText edit;

        public Listener(EditText edit) {
            this.edit = edit;
        }

        public void onClick(View p1) {

        }


    }


}
