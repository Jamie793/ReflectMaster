package com.jamiexu.app.reflectmaster.j;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;

import com.jamiexu.app.reflectmaster.LuaDexLoaders;
import com.jamiexu.app.reflectmaster.Utils.Utils;
import com.jamiexu.app.reflectmaster.factory.LuaExecutorFactory;
import com.jamiexu.utils.file.FileUtils;
import com.luajava.LuaException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import dalvik.system.DexClassLoader;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HOnCreate extends XC_MethodHook {
    public static LuaDexLoaders luaDexLoader;
    public static XC_LoadPackage.LoadPackageParam lpparam;
    public static HOnCreate hOnCreate;
    public static MethodHookParam beforeHookedMethod;
    public static MethodHookParam afterHookedMethod;


    public HOnCreate(XC_LoadPackage.LoadPackageParam lpparam) {
        HOnCreate.lpparam = lpparam;
        hOnCreate = this;
    }

    @Override
    protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) {
        beforeHookedMethod = param;
    }

    @SuppressLint("UnsafeDynamicallyLoadedCode")
    @Override
    protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
        afterHookedMethod = param;
        if (luaDexLoader == null)
            luaDexLoader = new LuaDexLoaders((Context) param.thisObject);


        String luajavaPath = ((Context) param.thisObject).getApplicationInfo().dataDir + "/app_lib";

        String cpu = com.jamiexu.app.reflectmaster.j.reflectmaster.Utils.Utils.getCpu();
        XposedBridge.log("CPU=>" + cpu);

        Utils.setLuaJavaSoPath(luajavaPath + "/libJamieReflectMasterluajava.so");
        MasterUtils.nowAct = (Activity) param.thisObject;

        FWindow jf = new FWindow(lpparam, param);
        if (!Entry.isFirst) {
            initLua((Context) param.thisObject, lpparam.packageName, jf);
        }
        super.afterHookedMethod(param);
    }


    public void initLua(Context context, String packageName, FWindow jf) {
        XposedBridge.log("ReflectMater=>init script");
        File file = new File(com.jamiexu.app.reflectmaster.j.reflectmaster.Utils.Utils.BASEPATH + "/script");
        if (file.exists()) {
            File[] files = file.listFiles();
            for (File fi : files) {
                if (!fi.getName().endsWith(".lua"))
                    continue;
                String line = FileUtils.getLineString(fi.toString(), 1);

                if (line.contains("--PackageName:")) {
                    line = line.substring(14);
                    line = line.trim();

                    if (line.equals(packageName) || line.equals("*")) {
                        String line2 = FileUtils.getLineString(fi.toString(), 2);

                        if (line2.contains("--Main:")) {
                            line2 = line2.substring(7);
                            if (line2.trim().equals((context.getClass().getCanonicalName()))) {
                                Entry.isFirst = true;
                                XposedBridge.log("ReflectMater=>start run script");
                                String code = FileUtils.getString(fi.toString());
                                LuaExecutor luaExecutor = LuaExecutorFactory.newInstance(context, this);
                                luaExecutor.executeLua(context, code);
                            }
                        } else {
                            Entry.isFirst = true;
                            XposedBridge.log("ReflectMater=>start run script");
                            String code = FileUtils.getString(fi.toString());
                            LuaExecutor luaExecutor = LuaExecutorFactory.newInstance(context, this);
                            luaExecutor.executeLua(context, code);
                        }
                    }
                }
            }
        }
    }


    public HashMap<String, String> getLibrarys() {
        return luaDexLoader.getLibrarys();
    }

    public DexClassLoader loadDex(String path) throws LuaException {
        return luaDexLoader.loadDex(path);
    }

    public ArrayList<ClassLoader> getClassLoaders() {
        // TODO: Implement this method
        return luaDexLoader.getClassLoaders();
    }

}



