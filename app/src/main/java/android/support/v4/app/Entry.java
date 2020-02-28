package android.support.v4.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import formatfa.reflectmaster.MainActivity;

public class Entry implements IXposedHookLoadPackage {


    static final String PACKAGENAME = "formatfa.android.f.reflectmaster";

    public static String id;
    private static String register;
    private static int statu;

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XSharedPreferences sp = new XSharedPreferences("formatfa.reflectmaster", "package");
        sp.reload();

        String[] s = sp.getString(MainActivity.KEY, "").split(",");
        boolean is = false;
        for (String g : s) {

            if (g.equals(lpparam.packageName)) {
//
                is = true;
                break;
            }
        }
        if (!is) {
            return;
        }


        MasterUtils.isUseWindowSearch = sp.getBoolean("windowsearch", false);
        MasterUtils.isFloating = sp.getBoolean("float", true);
        MasterUtils.newThread = sp.getBoolean("newthread", false);


        id = sp.getString("fid", "");
        statu = sp.getInt("statu", 0);
        register = sp.getString("register", "");

        XposedBridge.log("aim hooked");
        MasterUtils.windowSize = sp.getInt("width", 700);
        MasterUtils.rotate = sp.getBoolean("rotate", true);
        XposedBridge.log("set Window size:" + MasterUtils.windowSize);


        LogUtils.loge("the aim app had hook");
//        XposedHelpers.findAndHookMethod("android.app.Application",lpparam.classLoader, "attach", Context.class,new HOnApp());
        XposedHelpers.findAndHookMethod("android.app.Activity", lpparam.classLoader, "onCreate", Bundle.class, new HOnCreate(lpparam));
        XposedHelpers.findAndHookMethod("android.app.Activity", lpparam.classLoader, "onResume", new HOnResume());
        XposedHelpers.findAndHookMethod("android.app.Dialog", lpparam.classLoader, "onKeyDown", int.class, KeyEvent.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                int keyCode = (int) param.args[0];
                Dialog dialog = (Dialog) param.thisObject;


                if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                    FWindow win = new FWindow(MasterUtils.nowAct, dialog);
                    //win.setDialog(dialog);

                }
            }
        });


        XposedHelpers.findAndHookMethod("android.app.Activity", lpparam.classLoader, "onKeyDown", int.class, KeyEvent.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                int keyCode = (int) param.args[0];


                if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                    MasterUtils.nowAct = (Activity) param.thisObject;
                    FWindow win = new FWindow(lpparam, param);

                }
            }
        });
    }


}
