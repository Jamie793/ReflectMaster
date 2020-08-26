package com.jamiexu.app.reflectmaster.j;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.jamiexu.app.reflectmaster.LuaDexLoaders;
import com.jamiexu.app.reflectmaster.MainActivity;
import com.jamiexu.app.reflectmaster.factory.LuaExecutorFactory;
import com.jamiexu.app.reflectmaster.j.Adapter.FieldAdapter;
import com.jamiexu.app.reflectmaster.j.Adapter.RegisterAdapter;
import com.jamiexu.app.reflectmaster.j.ClassHandle.Handle_ArrayList;
import com.jamiexu.app.reflectmaster.j.ClassHandle.Handle_ImageView;
import com.jamiexu.app.reflectmaster.j.ClassHandle.Handle_Set;
import com.jamiexu.app.reflectmaster.j.ClassHandle.Handle_TextView;
import com.jamiexu.app.reflectmaster.j.ClassHandle.Handle_View;
import com.jamiexu.app.reflectmaster.j.ClassHandle.Handle_ViewGroup;
import com.jamiexu.app.reflectmaster.j.Data.ReflectData;
import com.jamiexu.app.reflectmaster.j.reflectmaster.Utils.Utils;
import com.jamiexu.app.reflectmaster.j.widget.SaveFileDialog;
import com.jamiexu.app.reflectmaster.j.widget.WindowList;
import com.jamiexu.utils.file.FileUtils;
import com.luajava.LuaException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import dalvik.system.DexClassLoader;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class FieldWindow extends Window implements OnItemClickListener, OnItemLongClickListener {

//  Update by Jamiexu 2020-08-25

    private Button undeclared;
    private List<String> names = new ArrayList<>();
    private LuaExecutor luaExecutor;
    private LuaDexLoaders luaDexLoader;
    private ListView listView;
    private Field[] fields;
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private boolean isDdeclear;
    private FieldAdapter fieldAdapter;
    private Class<?> superCls;


    public FieldWindow(XC_LoadPackage.LoadPackageParam lpparam, XC_MethodHook.MethodHookParam param, Context act, Object object) {
        super(lpparam, param, act, object);
        ClassLoader classLoader = act.getClassLoader();
        this.windowManager = (WindowManager) act.getSystemService(Context.WINDOW_SERVICE);
        this.layoutParams = new WindowManager.LayoutParams();
        this.layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;
        this.layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;

        this.luaExecutor = LuaExecutorFactory.newInstance(act, HOnCreate.hOnCreate);
        this.luaDexLoader = new LuaDexLoaders(act);
    }

    public static void newWindow(final XC_LoadPackage.LoadPackageParam lpparam, final XC_MethodHook.MethodHookParam param, final Context act, final Object object, final WindowManager wm) {

        try {
            if (object.getClass().getCanonicalName().endsWith(("[]")) && !MasterUtils.isBaseArray(object.getClass().getCanonicalName())) {
                Toast.makeText(act, "这是一个数组来的", Toast.LENGTH_SHORT).show();


                final Object[] ob = (Object[]) object;

                int len = ob.length;
                if (len > 30) {
                    len = 30;
                    Toast.makeText(act, "长度过大，只显示前30个", Toast.LENGTH_SHORT).show();

                }
                String[] arr = new String[len + 1];
                arr[0] = "直接查看数组";
                for (int i = 0; i < ob.length; i += 1) {
                    arr[i] = i + " " + MasterUtils.getObjectString(ob[i]);
                }
                WindowList list = new WindowList(act, wm);
                list.setItems(arr);
                list.setListener((p1, p2, position, p4) -> {
                    if (position == 0)
                        newFieldWindow(lpparam, param, act, object, wm);
                    else
                        newFieldWindow(lpparam, param, act, ob[position - 1], wm);
                });
                list.setTitle("选择其中一个对象，总共有:" + ob.length);
                list.show();


            } else
                newFieldWindow(lpparam, param, act, object, wm);

        } catch (IllegalArgumentException e) {
            Toast.makeText(act, e.toString(), Toast.LENGTH_SHORT).show();


        }


    }


    @SuppressLint("ShowToast")
    public static void newFieldWindow(XC_LoadPackage.LoadPackageParam lpparam, XC_MethodHook.MethodHookParam param, Context act, Object object, WindowManager wm) {
        final WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.type = WindowManager.LayoutParams.TYPE_APPLICATION;

        try {

            FieldWindow newf = new FieldWindow(lpparam, param, act, object);
            newf.show(wm, lp);
        } catch (IllegalArgumentException e) {
            Toast.makeText(act, e.toString(), Toast.LENGTH_SHORT);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void show(final WindowManager amanager, final WindowManager.LayoutParams lpp) {
        //初始化布局
        if (object == null) {
            Toast.makeText(act, "is null....", Toast.LENGTH_SHORT).show();
        }

        final LinearLayout layout = new LinearLayout(act);
        layout.setBackgroundColor(0xFF303030);
        layout.setOrientation(LinearLayout.VERTICAL);

        final ActionWindow actionWindow = new ActionWindow(act, this.windowManager, this.layoutParams, layout);
        layout.addView(actionWindow.getActionBar());


        Button menuButton = new Button(this.act);
        PopupMenu popupMenu = new PopupMenu(this.act, menuButton);
        Menu menu = popupMenu.getMenu();
        menu.add("STL");
        menu.add("SRL");
        menuButton.setTextColor(Color.WHITE);
        menuButton.setText("M");
        menuButton.setBackground(null);
        menuButton.setOnClickListener((v) -> {
            popupMenu.show();
        });
        popupMenu.setOnMenuItemClickListener(item -> {
            WindowList windowList = new WindowList(act, windowManager);
            RegisterAdapter registerAdapter = null;
            windowList.getListView().setDividerHeight(10);
            switch (item.getTitle().toString()) {
                case "STL":
                    windowList.setTitle("   TemporaryRegister Len:" + MasterUtils.objects.size());
                    registerAdapter = new RegisterAdapter(act, MasterUtils.objects);
                    windowList.setAdaptet(registerAdapter);
                    break;
                case "SRL":
                    windowList.setTitle("   HashRegister Len:" + MasterUtils.hashMap.size());
                    registerAdapter = new RegisterAdapter(act, MasterUtils.hashMap);
                    windowList.setAdaptet(registerAdapter);
                    break;
            }
            RegisterAdapter finalRegisterAdapter = registerAdapter;
            windowList.setListener((parent, view, position, id) -> {
                assert finalRegisterAdapter != null;
                Object dataItem = finalRegisterAdapter.getItem(position);
                if (dataItem instanceof String) {
                    ReflectData reflectData = MasterUtils.hashMap.get(dataItem);
                    FieldWindow.newWindow(null, null, reflectData.getContext(),
                            reflectData.getObject(), windowManager);
                } else {
                    ReflectData reflectData = MasterUtils.objects.get(position);
                    FieldWindow.newWindow(null, null, reflectData.getContext(),
                            reflectData.getObject(), windowManager);
                }
            });
            windowList.setOnItemLongClickListener((parent, view, position, id) -> {
                assert finalRegisterAdapter != null;
                Object dataItem = finalRegisterAdapter.getItem(position);
                if (dataItem instanceof String) {
                    MasterUtils.hashMap.remove(dataItem);
                    windowList.setTitle("   HashRegister Len:" + MasterUtils.hashMap.size());
                    finalRegisterAdapter.setItems(MasterUtils.hashMap);
                } else {
                    MasterUtils.objects.remove(position);
                    windowList.setTitle("   TemporaryRegister Len:" + MasterUtils.objects.size());
                    finalRegisterAdapter.setItems(MasterUtils.objects);
                }
                finalRegisterAdapter.refreshData();
                return true;
            });
            windowList.show();
            return true;
        });
        actionWindow.addView(menuButton);


        this.undeclared = new Button(act);
        this.undeclared.setTextColor(Color.WHITE);
        this.undeclared.setBackground(null);
        this.undeclared.setOnClickListener(p1 -> {
            Utils.showToast(this.act, this.isDdeclear + "", 0);
            if (this.isDdeclear) {
                this.fields = object.getClass().getDeclaredFields();
                this.fieldAdapter.setFields(this.fields);
                this.fieldAdapter.notifyDataSetInvalidated();
                this.isDdeclear = false;
                undeclared.setText("A");
            } else {
                this.fields = object.getClass().getFields();
                this.fieldAdapter.setFields(this.fields);
                this.fieldAdapter.notifyDataSetInvalidated();
                this.isDdeclear = true;
                this.undeclared.setText("P");
            }


        });
        actionWindow.addView(undeclared);


        final TextView clsname = new TextView(act);
        clsname.setText("Current：" + object.getClass().getCanonicalName());
        clsname.setOnClickListener(view -> {
            try {
                if (this.superCls == null) {
                    this.superCls = this.object.getClass().getSuperclass();
                } else {
                    if (!this.superCls.getCanonicalName().equals("java.lang.Object"))
                        this.superCls = this.superCls.getSuperclass();
                    else
                        Utils.showToast(this.act, "Nothing...", Toast.LENGTH_SHORT);
                }
                assert this.superCls != null;
                this.fields = this.superCls.getDeclaredFields();
                this.object = this.superCls.newInstance();
                this.isDdeclear = true;
                this.undeclared.setText("P");
                this.fieldAdapter = new FieldAdapter(this.act, this.fields, object);
                this.listView.setAdapter(this.fieldAdapter);
                clsname.setText("Current：" + this.superCls.getCanonicalName());
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        });


        clsname.setOnLongClickListener(v -> {
            Utils.writeClipboard(act, clsname.getText().toString().replace("Current：", ""));
            Utils.showToast(act, "Copied", 0);
            return true;
        });

        clsname.setTextColor(0xFF909090);
        layout.addView(clsname);


        LinearLayout buttonLayout = new LinearLayout(act);
        buttonLayout.setBackgroundColor(0xFF303030);

        Button metbod = new Button(act);
        metbod.setText("M");
        metbod.setOnClickListener(p1 -> {
            MethodWindow mw = new MethodWindow(lpparam, param, act, object);
            mw.show(this.windowManager, this.layoutParams);
        });
        buttonLayout.addView(metbod);


        metbod = new Button(act);
        metbod.setText("F");
        metbod.setOnClickListener(p1 -> {
            ConstructorWindow mw = new ConstructorWindow(lpparam, param, act, object);

            mw.show(this.windowManager, this.layoutParams);
        });
        buttonLayout.addView(metbod);

        Button add = new Button(act);
        add.setText("ST");
        add.setOnClickListener(p1 -> MasterUtils.add(act, object));
        buttonLayout.addView(add);

        Button adds = new Button(act);
        adds.setText("SR");
        adds.setOnClickListener(p1 -> {
            MasterUtils.addHashMap(act, object);
        });
        buttonLayout.addView(adds);


        if (object instanceof Drawable || object instanceof Bitmap) {
            Button viewImage = new Button(act);
            viewImage.setText("View IMG");
            viewImage.setOnClickListener(p1 -> {
                ImageWindow img = new ImageWindow(this.act, lpparam, param, act, object);
                img.show(this.windowManager, this.layoutParams);
            });
            buttonLayout.addView(viewImage);
        } else if ("byte[]".equals(object.getClass().getCanonicalName())) {

            Button operation = new Button(act);
            operation.setText("Write");
            operation.setOnClickListener(v -> {
                String path = MainActivity.BASE_PATH + System.currentTimeMillis() + ".bin";
                try {
                    FileOutputStream os = new FileOutputStream(path);
                    os.write((byte[]) object);
                    os.flush();
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Utils.showToast(act, "数据已保存到目录：" + path, Toast.LENGTH_SHORT);
            });

            operation.setOnLongClickListener(v -> {
                new SaveFileDialog(act, "保存数据：", "保存路径",
                        (path, fileOutputStream) -> {
                            try {
                                fileOutputStream.write((byte[]) object);
                                Utils.showToast(act, "数据已保存到路径:" +
                                        path, Toast.LENGTH_SHORT);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }).show();
                return true;
            });

            buttonLayout.addView(operation);
        } else if (object instanceof List)
            new Handle_ArrayList(act, object).handle(buttonLayout);
        else if (object instanceof ViewGroup)
            new Handle_ViewGroup(act, object).handle(buttonLayout);
        else if (object instanceof ImageView)
            new Handle_ImageView(act, object).handle(buttonLayout);
        else if (object instanceof TextView)
            new Handle_TextView(act, object).handle(buttonLayout);
        else if (object instanceof Set) {
            new Handle_Set(act, object).handle(buttonLayout);
        } else if (object instanceof View) {
            new Handle_View(act, object).handle(buttonLayout);
        }


        Button button = new Button(act);
        button.setText("LUAT");
        button.setOnClickListener(p1 ->

        {
            ScriptWindow sc = new ScriptWindow(lpparam, param, act, object, null);
            sc.show(this.windowManager, null);
        });
        buttonLayout.addView(button);


        button = new Button(act);
        button.setText("MR");
        button.setOnClickListener(p1 ->

        {
            WindowManager am = (WindowManager) act.getSystemService(Context.WINDOW_SERVICE);
            loadLuaScriptButton();
        });
        buttonLayout.addView(button);

        button = new Button(act);
        button.setText("FC");
        button.setOnClickListener(p1 ->
                findClass());
        buttonLayout.addView(button);

        HorizontalScrollView ho = new HorizontalScrollView(act);
        ho.addView(buttonLayout);
        layout.addView(ho);

        EditText editText = new EditText(act);
        editText.setHint("Filter fields...");
        editText.setTextSize(14);
        editText.setWidth(layout.getWidth());
        editText.setHintTextColor(Color.WHITE);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0)
                    listView.clearTextFilter();
                listView.setFilterText(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        layout.addView(editText);


        this.listView = new

                ListView(act);
        this.listView.setTextFilterEnabled(true);
        this.listView.setFastScrollEnabled(true);
        this.listView.setOnItemClickListener(this);
        this.listView.setOnItemLongClickListener(this);
        this.listView.setBackgroundColor(0xFF303030);
        this.listView.setDividerHeight(15);

        this.fields = object.getClass().

                getFields();
        this.undeclared.setText("P");
        this.isDdeclear = true;
        this.fieldAdapter = new

                FieldAdapter(act, this.fields, object);
        this.listView.setAdapter(this.fieldAdapter);


        layout.addView(this.listView);


        View line = new View(act);
        ViewGroup.LayoutParams he = new ViewGroup.LayoutParams(-1, 9);
        line.setBackgroundColor(Color.BLUE);
        line.setLayoutParams(he);

        this.windowManager.addView(layout, this.layoutParams);
    }

    private void findClass() {

        EditWindow editWindow = new EditWindow(lpparam, param, act, "输入完整类名", "android.app.Activity");
        editWindow.setListener(str -> {

            try {
                Class<?> cls = HOnCreate.lpparam.classLoader.loadClass(str);
                Toast.makeText(act, cls + "", Toast.LENGTH_SHORT).show();
                if (cls == null && lpparam != null) cls = lpparam.classLoader.loadClass(str);
                try {
                    assert cls != null;
                    FieldWindow.newFieldWindow(lpparam, param, act, cls.newInstance(), this.windowManager);
                } catch (Exception e) {
                    Toast.makeText(act, e.toString(), Toast.LENGTH_SHORT).show();
                }
            } catch (ClassNotFoundException e) {
                Toast.makeText(act, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        editWindow.show(this.windowManager, this.layoutParams);
    }


    private void loadLuaScriptButton() {
        names.clear();
        File file = new File(Utils.BASEPATH + "/script");
        if (file.exists()) {
            File[] files = file.listFiles();
            for (File fi : files) {
                String name = fi.getName();
                if (fi.isFile() && (name.endsWith(".lua") || name.endsWith(".luaj"))) {
                    if (name.endsWith(".lua"))
                        names.add(name.replace(".lua", ""));
                }

            }

        }
        WindowList listView = new WindowList(act, this.windowManager);
        listView.setItems(names);
        listView.setTitle("    Lua脚本");
        listView.setListener((adapterView, view, i, l) -> {
            this.luaExecutor.executeLua(act, FileUtils.getString(Utils.BASEPATH + "/script/" + names.get(i) + ".lua"));
        });

        listView.setOnItemLongClickListener((adapterView, view, i, l) -> {
            ScriptWindow sc = new ScriptWindow(lpparam, param, act, object, FileUtils.getString(Utils.BASEPATH + "/script/" + names.get(i) + ".lua"));
            sc.show(this.windowManager, null);
            return true;
        });

        listView.show();


    }

    @Override
    public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
        try {
            newWindow(lpparam, param, act, ((Field) p1.getItemAtPosition(p3)).get(object), this.windowManager);


        } catch (Exception e) {
            Toast.makeText(act, e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onItemLongClick(final AdapterView<?> p1, View p2, final int p3, long p4) {

        WindowManager am = (WindowManager) act.getSystemService(Context.WINDOW_SERVICE);
        WindowList wlist = new WindowList(act, am);
        wlist.setTitle("变量操作");
        wlist.setItems(new String[]{"编辑", "临时保存起来", "添加到寄存器", "复制变量名称", "复制类名和变量名称", "复制变量名称和类型"/*,"持久化修改(构造函数后使用while(true)不断修改}"*/});
        wlist.setListener((adap, view, posi, l) -> {

            Field m = (Field) p1.getItemAtPosition(p3);
            if (posi == 0) {
                if (!m.isAccessible()) m.setAccessible(true);
                EditFieldWindow ew = new EditFieldWindow(lpparam, param, act, object, fields[p3]);
                ew.show(this.windowManager, this.layoutParams);
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
                String s = m.getName() + " " + m.getType().getName();
                Utils.writeClipboard(act, s);
                Toast.makeText(act, "复制成功:" + s, Toast.LENGTH_SHORT).show();
            } else {
                Utils.writeClipboard(act, m.getDeclaringClass().getCanonicalName() + "'," + "'" + m.toGenericString() + "'");
                Toast.makeText(act, "复制成功:" + m.toGenericString(), Toast.LENGTH_SHORT).show();
            }

        });
        wlist.show();


        return true;
    }

    public ArrayList<ClassLoader> getClassLoaders() {
        // TODO: Implement this method
        return luaDexLoader.getClassLoaders();
    }

    public HashMap<String, String> getLibrarys() {
        return luaDexLoader.getLibrarys();
    }

    public DexClassLoader loadDex(String path) throws LuaException {
        return luaDexLoader.loadDex(path);
    }


}
