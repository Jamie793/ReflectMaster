package com.jamiexu.app.reflectmaster.j;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MasterUtils {

    //  Update by Jamiexu 2020-08-25


    @SuppressLint("StaticFieldLeak")
    public static Activity nowAct;
    public static boolean isFloating = true, newThread = false;
    public static List<Object> objects = new ArrayList<>();
    public static HashMap<String, Object> hashMap = new HashMap<>();
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

        objects.add(obj);
        if (context != null)
            Toast.makeText(context, "添加到临时存储器v" + p + "成功！", Toast.LENGTH_SHORT).show();
    }

    private static void add(Context context, String name, Object obj) {
        if (hashMap.containsKey(name))
            if (context != null) {
                Toast.makeText(context, "添加失败，寄存器名称已存在", Toast.LENGTH_SHORT).show();
                return;
            }

        hashMap.put(name, obj);
        if (context != null)
            Toast.makeText(context, "添加到临时存储器：" + name + " 成功！", Toast.LENGTH_SHORT).show();
    }


    public static Object get(int i) {
        return objects.get(i);
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


    //将基本类型的字符转换为对象,寄存器的值 startwith $Fnumber ,like $F0
    public static Object baseTypeParse(String conciaType, String value) {
        Object result = null;
        if (value.startsWith("$F")) {

            result = objects.get(Integer.parseInt(value.substring(2)));

            return result;
        }
        switch (conciaType) {
            case "int":
                result = Integer.parseInt(value);
                break;
            case "boolean":
                if ("true".equals(value))
                    result = true;
                else
                    result = false;
                break;
            case "long":
                result = Long.parseLong(value);
                break;
            case "byte":
                result = Byte.parseByte(value);
                break;


            default:
                result = value;


        }
        return result;
    }

    public static Class parseClass(String clz, ClassLoader loader) throws ClassNotFoundException {
        Class result;
        switch (clz) {

            case "int":
                result = int.class;
                break;
            case "boolean":
                result = boolean.class;
                break;
            case "long":
                result = long.class;
                break;
            case "short":
                result = boolean.class;
                break;
            case "char":
                result = char.class;
                break;
            case "byte":
                result = byte.class;
                break;
            default:
                result = loader.loadClass(clz);
        }
        return result;
    }


    public static boolean isBaseArray(String cancio) {


        return "int[]".equals(cancio) || "byte[]".equals(cancio) || "short[]".equals(cancio) || "long[]".equals(cancio) || "char[]".equals(cancio);
    }
}
