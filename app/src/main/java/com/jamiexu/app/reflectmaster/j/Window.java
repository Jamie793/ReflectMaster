package com.jamiexu.app.reflectmaster.j;

import android.content.Context;
import android.view.WindowManager;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public abstract class Window {
    XC_LoadPackage.LoadPackageParam lpparam;

    XC_MethodHook.MethodHookParam param;
    public Context act;

    Object object;

    public Window(XC_LoadPackage.LoadPackageParam lpparam, XC_MethodHook.MethodHookParam param, Context act, Object object) {
        this.lpparam = lpparam;
        this.param = param;
        this.object = object;
        this.act = act;
    }

    public abstract void show(WindowManager manager, WindowManager.LayoutParams lpl);


}
