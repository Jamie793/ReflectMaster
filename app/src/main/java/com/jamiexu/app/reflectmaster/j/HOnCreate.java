package com.jamiexu.app.reflectmaster.j;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;

import android.view.WindowManager;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import com.jamiexu.app.reflectmaster.Utils.Utils;
import com.jamiexu.app.reflectmaster.factory.LuaExecutorFactory;
import com.jamiexu.utils.file.FileUtils;

import java.io.File;

public class HOnCreate extends XC_MethodHook {

    private XC_LoadPackage.LoadPackageParam lpparam;


    WindowManager wm;


    public HOnCreate(XC_LoadPackage.LoadPackageParam lpparam) {
        this.lpparam = lpparam;

    }

    @Override
    protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) {

    }

    @SuppressLint("UnsafeDynamicallyLoadedCode")
    @Override
    protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {

        String luajavaPath = ((Context) param.thisObject).getApplicationInfo().dataDir + "/app_lib";

        String cpu = com.jamiexu.app.reflectmaster.j.reflectmaster.Utils.Utils.getCpu();
        XposedBridge.log("CPU=>" + cpu);

        Utils.setLuaJavaSoPath(luajavaPath + "/libJamieReflectMasterluajava.so");
        MasterUtils.nowAct = (Activity) param.thisObject;
        if (!Entry.isFirst) {
            initLua((Context) param.thisObject, lpparam.packageName);
        }
        new FWindow(lpparam, param);
        super.afterHookedMethod(param);
    }


    public void initLua(Context context, String packageName) {
        XposedBridge.log("ReflectMater=>init script");
        File file = new File(com.jamiexu.app.reflectmaster.j.reflectmaster.Utils.Utils.BASEPATH + "/script");
        if (file.exists()) {
            File[] files = file.listFiles();
            for (File fi : files) {
                if (!fi.getName().endsWith(".lua"))
                    continue;
                String line = FileUtils.getLineString(fi.toString(), 1);

                if (line.contains("--packagename:")) {
                    line = line.substring(14);
                    line = line.trim();

                    if (line.equals(packageName) || line.equals("*")) {
                        String line2 = FileUtils.getLineString(fi.toString(), 2);

                        if (line2.contains("--main:")) {
                            line2 = line2.substring(7);
                            if (line2.trim().equals((context.getClass().getCanonicalName()))) {
                                Entry.isFirst = true;
                                XposedBridge.log("ReflectMater=>start run script");
                                String code = FileUtils.getString(fi.toString());
                                LuaExecutor luaExecutor = LuaExecutorFactory.newInstance(context);
                                luaExecutor.executeLua(context, code);
                            }
                        } else {
                            Entry.isFirst = true;
                            XposedBridge.log("ReflectMater=>start run script");
                            String code = FileUtils.getString(fi.toString());
                            LuaExecutor luaExecutor = LuaExecutorFactory.newInstance(context);
                            luaExecutor.executeLua(context, code);
                        }
                    }
                }
            }
        }
    }
}



