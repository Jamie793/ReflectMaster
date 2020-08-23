package com.jamiexu.app.reflectmaster.j;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.widget.Toast;

import com.androlua.LuaDexClassLoader;
import com.androlua.LuaThread;
import com.jamiexu.app.reflectmaster.j.reflectmaster.Utils.Utils;
import com.jamiexu.utils.ReflectUtils;
import com.luajava.JavaFunction;
import com.luajava.LuaException;
import com.luajava.LuaState;
import com.luajava.LuaStateFactory;

import java.io.File;
import java.util.HashMap;

import dalvik.system.DexClassLoader;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class LuaExecutor {

    private LuaState L;
    private Context context;
    private final StringBuilder output;
    private final HashMap<String, LuaDexClassLoader> dexCache = new HashMap<>();

    @SuppressLint("UnsafeDynamicallyLoadedCode")
    public LuaExecutor(Context activity, Object jf) {
        XposedBridge.log("LuaJavaSOPath=>" + com.jamiexu.app.reflectmaster.Utils.Utils.getLuaJavaSoPath());
        this.context = activity;
        this.output = new StringBuilder();
        L = LuaStateFactory.newLuaState(com.jamiexu.app.reflectmaster.Utils.Utils.getLuaJavaSoPath());
        L.openLibs();
        L.pushJavaObject(activity);
        L.setGlobal("this");
        L.pushJavaObject(activity);
        L.setGlobal("activity");
        L.pushJavaObject(jf);
        L.setGlobal("jf");
        L.pushJavaObject(this);
        L.setGlobal("jl");
        L.pushJavaObject(com.jamiexu.utils.reflect.ReflectUtils.class);
        L.setGlobal("jr");
        try {
            L.pushJavaObject(ReflectUtils.getStaticField(Class.forName("com.jamiexu.app.reflectmaster.j.MasterUtils"), "objects"));
            L.setGlobal("ju");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }



        L.getGlobal("package");
        L.pushString(Environment.getExternalStorageDirectory().toString() + "/ReflectMaster/lua/?.lua");
        L.setField(-2, "path");
        L.pop(1);
        initLuaFunction();
        //        XposedBridge.log("LuaJava=>Init successfult");
    }




    private void initLuaFunction() {
        JavaFunction print = new JavaFunction(L) {
            @Override
            public int execute() {
                for (int i = 2; i <= L.getTop(); i++) {
                    int type = L.type(i);
                    String stype = L.typeName(type);
                    String val = null;
                    if (stype.equals("userdata")) {
                        Object obj = null;
                        try {
                            obj = L.toJavaObject(i);
                        } catch (LuaException e) {
                            e.printStackTrace();
                        }
                        if (obj != null)
                            val = obj.toString();
                    } else if (stype.equals("boolean")) {
                        val = L.toBoolean(i) ? "true" : "false";
                    } else {
                        val = L.toString(i);
                    }
                    if (val == null)
                        val = stype;
                    output.insert(0, val);
                    output.insert(0, "\t");
                }

                output.insert(0, "\n");
                return 0;
            }
        };
        print.register("print");


        JavaFunction set = new JavaFunction(L) {
            @Override
            public int execute() {
                LuaThread thread = null;
                try {
                    thread = (LuaThread) L.toJavaObject(2);
                } catch (LuaException e) {
                    e.printStackTrace();
                }

                try {
                    thread.set(L.toString(3), L.toJavaObject(4));
                } catch (LuaException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        };
        set.register("set");


        JavaFunction call = new JavaFunction(L) {
            @Override
            public int execute() {
                LuaThread thread = null;
                try {
                    thread = (LuaThread) L.toJavaObject(2);
                } catch (LuaException e) {
                    e.printStackTrace();
                }

                int top = L.getTop();
                if (top > 3) {
                    Object[] args = new Object[top - 3];
                    for (int i = 4; i <= top; i++) {
                        try {
                            args[i - 4] = L.toJavaObject(i);
                        } catch (LuaException e) {
                            e.printStackTrace();
                        }
                    }
                    thread.call(L.toString(3), args);
                } else if (top == 3) {
                    thread.call(L.toString(3));
                }

                return 0;
            }

        };
        call.register("call");
    }


    public LuaDexClassLoader loadApp(String pkg) {
        try {
            LuaDexClassLoader dex = dexCache.get(pkg);
            if (dex == null) {
                PackageManager manager = this.context.getPackageManager();
                ApplicationInfo info = manager.getPackageInfo(pkg, 0).applicationInfo;
                dex = new LuaDexClassLoader(info.publicSourceDir, this.context.getDir("odex", 0).toString(), info.nativeLibraryDir, this.context.getClassLoader());
                dexCache.put(pkg, dex);
            }
            return dex;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    public DexClassLoader load(String path) throws LuaException {
        LuaDexClassLoader dex = dexCache.get(path);
        if (dex == null)
            dex = loadApp(path);
        if (dex == null) {
            if (!new File(path).exists())
                if (new File(path + ".dex").exists())
                    path += ".dex";
                else if (new File(path + ".jar").exists())
                    path += ".jar";
                else
                    throw new LuaException(path + " not found");
            dex = dexCache.get(path);

            if (dex == null) {
                dex = new LuaDexClassLoader(path, this.context.getDir("odex", 0).toString(), this.context.getApplicationInfo().nativeLibraryDir, this.context.getClassLoader());
                dexCache.put(path, dex);
            }
        }
        return dex;
    }


    public void executeLua(Context activity, String code) {
        try {
            if (MasterUtils.newThread) {
                new Thread(() -> {
                    try {
                        exeLua(code);
                    } catch (LuaException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else
                exeLua(code);
        } catch (LuaException e) {
            output.insert(0, e.toString());
        }

        if (output.length() > 0) {
            new AlertDialog.Builder(activity)
                    .setTitle("运行结果：")
                    .setMessage(output.toString())
                    .setPositiveButton("确定", null)
                    .setNegativeButton("复制", (dialog, which) -> {
                        Utils.writeClipboard(activity, output.toString());
                        Toast.makeText(activity, "已复制到剪切板", Toast.LENGTH_SHORT).show();
                    }).setNeutralButton("清空", (dialog, which) -> output.setLength(0)).show().setCancelable(false);
        }

    }


    private String exeLua(String src) throws LuaException {
        L.setTop(0);
        int ok = L.LloadString(src);
        if (ok == 0) {
            L.getGlobal("debug");
            L.getField(-1, "traceback");
            L.remove(-2);
            L.insert(-2);
            ok = L.pcall(0, 0, -2);
            if (ok == 0) {
                String res = output.toString();
                return res;
            }
        }
        throw new LuaException(errorReason(ok) + ": " + L.toString(-1));
        //return null;

    }


//    public void imp(String name) {
//        sourceCode.append("luajava.loaded[\"" + name + "\"] = jc.getClass().getClassLoader().loadClass(\"" + name + "\")");
//        sourceCode.append("import \"" + name + "\"");
//    }

    private String errorReason(int error) {
        switch (error) {
            case 4:
                return "Out of memory";
            case 3:
                return "Syntax error";
            case 2:
                return "Runtime error";
            case 1:
                return "Yield error";
        }
        return "Unknown error " + error;
    }


    public Object doFile(String filePath, Object[] args) {
        int ok = 0;
        try {
            //if (filePath.charAt(0) != '/')
            filePath = Environment.getExternalStorageDirectory().toString() + "/ReflectMaster/lua/" + filePath + ".lua";

            L.setTop(0);
            ok = L.LloadFile(filePath);

            if (ok == 0) {
                L.getGlobal("debug");
                L.getField(-1, "traceback");
                L.remove(-2);
                L.insert(-2);
                int l = args.length;
                for (int i = 0; i < l; i++) {
                    L.pushObjectValue(args[i]);
                }
                ok = L.pcall(l, 1, -2 - l);
                if (ok == 0) {
                    return L.toJavaObject(-1);
                }
            }
            throw new LuaException(errorReason(ok) + ": " + L.toString(-1));
        } catch (LuaException e) {
            output.insert(0, e.toString());
        }

        return null;
    }


//    public void add(Context context,Object object){
//        MasterUtils.add(context,object);
//    }


}
