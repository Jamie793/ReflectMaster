package com.jamiexu.app.reflectmaster.j;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;

import android.view.WindowManager;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import com.jamiexu.app.reflectmaster.Utils.Utils;

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

        Utils.setLuaJavaSoPath(luajavaPath + "/libluajava.so");
        MasterUtils.nowAct = (Activity) param.thisObject;
        new FWindow(lpparam, param);
        super.afterHookedMethod(param);
    }




}
