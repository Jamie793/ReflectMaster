package com.jamiexu.app.reflectmaster.j;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jamiexu.app.reflectmaster.j.Data.ReflectData;
import com.jamiexu.utils.reflect.ReflectUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MasterUtils {

    //  Update by Jamiexu 2020-08-25


    @SuppressLint("StaticFieldLeak")
    public static Activity nowAct;
    public static boolean isFloating = true, newThread = false;
    public static List<ReflectData> objects = new ArrayList<>();
    public static HashMap<String, ReflectData> hashMap = new HashMap<>();
    public static List<Object> serviceobjects = new ArrayList<>();


    public static void add(Context context, Object obj) {
        if (objects.size() > 50) return;
        if (objects.contains(obj)) {
            return;
        }
        for (Object o : objects) {
            if (o == null) objects.remove(o);
        }
        int p = objects.size();

        objects.add(new ReflectData(context, obj));
        if (context != null)
            Toast.makeText(context, "添加到临时存储器v" + p + "成功！", Toast.LENGTH_SHORT).show();
    }

    private static void add(Context context, String name, Object obj) {
        if (hashMap.containsKey(name))
            if (context != null) {
                Toast.makeText(context, "添加失败，寄存器名称已存在", Toast.LENGTH_SHORT).show();
                return;
            }

        hashMap.put(name, new ReflectData(context, obj));
        if (context != null)
            Toast.makeText(context, "添加到临时存储器：" + name + " 成功！", Toast.LENGTH_SHORT).show();
    }


    public static Object get(int i) {
        return objects.get(i).getObject();
    }

    public static Object get(String key) {
        return Objects.requireNonNull(hashMap.get(key)).getObject();
    }


    public static void addHashMap(Context context, Object o) {
        EditText editText = new EditText(context);
        editText.setHint("保存的名称");
        new AlertDialog.Builder(context)
                .setTitle("添加寄存器")
                .setView(editText)
                .setPositiveButton("添加", (dialog, which) -> {
                    String name = editText.getText().toString().trim();
                    if (name.length() == 0) {
                        Toast.makeText(context, "请输入寄存器名称", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    MasterUtils.add(context, name, o);
                }).show();
    }


    public static void addService(Context context, Object obj) {

        for (Object o : serviceobjects) {
            if (o == null) serviceobjects.remove(o);
        }
        int p = serviceobjects.size();

        serviceobjects.add(obj);
        if (context != null)
            Toast.makeText(context, "添加一个新启动的Service到v" + p + "成功！", Toast.LENGTH_SHORT).show();
    }


    public static String getObjectString(Object obj) {
        String result = null;
        if (obj != null) {
            String clz = obj.getClass().getCanonicalName();
            if (clz != null)
                switch (clz) {
                    case "java.lang.String":
                    case "java.lang.Boolean":
                    case "java.lang.Integer":
                    case "java.lang.Long":
                    case "java.lang.Short":
                    case "java.lang.Character":
                    case "char":
                    case "byte":
                        result = obj + "";
                        break;
                    default:
                        if (obj instanceof TextView) {
                            result = ((TextView) obj).getText().toString();
                        } else if (obj instanceof Button) {
                            result = ((Button) obj).getText().toString();
                        } else if (obj instanceof EditText) {
                            result = ((EditText) obj).getText().toString();
                        }
                }
        } else
            result = "null";
        return result;
    }

    public static Object parseValue(String clas, String value) {
        Object result = null;
        if (value.contains("$st")) {
            int index = Integer.parseInt(value.replaceAll(":\\$(\\w)", "").substring(3));
            if (index < MasterUtils.objects.size() && index >= 0) {
                ReflectData object = MasterUtils.objects.get(index);
                if (object.getObject().getClass().isAssignableFrom(Field.class)) {
                    Field field = (Field) object.getObject();
                    result = ReflectUtils.getFieldValue(field, object.getContext());
                }
            }
        } else if (value.contains("$sr")) {
            String key = value.substring(3).replaceAll(":\\$(\\w)", "");
            if (MasterUtils.hashMap.containsKey(key)) {
                ReflectData object = MasterUtils.hashMap.get(key);
                if (Objects.requireNonNull(object).getObject().getClass().isAssignableFrom(Field.class)) {
                    Field field = (Field) object.getObject();
                    result = ReflectUtils.getFieldValue(field, object.getContext());
                }
            }
        } else if (value.contains("$null")) {
        } else {
            switch (clas) {
                case "java.lang.CharSequence":
                case "java.lang.String":
                    result = value;
                    break;
                case "int":
                case "java.lang.Integer":
                    result = Integer.valueOf(value);
                    break;
                case "boolean":
                case "java.lang.Boolean":
                    result = (value.equals("true") || value.equals("t") || value.equals("1"));
                    break;
                case "char":
                case "java.lang.Character":
                    result = (char) Integer.parseInt(value);
                case "byte":
                case "java.lang.Byte":
                    result = (byte) Integer.parseInt(value);
                case "double":
                case "java.lang.Double":
                    result = Double.valueOf(value);
                case "float":
                case "java.lang.Float":
                    result = Float.valueOf(value);
                case "long":
                case "java.lang.Long":
                    result = Long.parseLong(result + "");
                    break;
            }
        }

        if (value.contains(":$")) {
            String suffix = value.substring(value.lastIndexOf(":$") + 2);
            switch (suffix) {
                case "s":
                    result += "";
                    break;
                case "i":
                    result = Integer.parseInt(result + "");
                    break;
                case "z":
                    String r = result + "";
                    result = r.equals("t") || r.equals("1") || r.equals("true");
                    break;
                case "c":
                    result = (char) Integer.parseInt(result + "");
                    break;
                case "l":
                    result = Long.parseLong(result + "");
                    break;
                case "f":
                    result = Float.parseFloat(result + "");
                    break;
                case "d":
                    result = Double.parseDouble(result + "");
                    break;

            }
        }
        return result;
    }

    public static boolean isBaseArray(String cancio) {


        return "int[]".equals(cancio) || "byte[]".equals(cancio) || "short[]".equals(cancio) || "long[]".equals(cancio) || "char[]".equals(cancio);
    }
}
