package com.jamiexu.app.reflectmaster.j;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.KeyEvent;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Entry implements IXposedHookLoadPackage {
    public static String id;

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) {
        XSharedPreferences sharedPreferences = new XSharedPreferences("com.jamiexu.app.reflectmaster", "package");
        sharedPreferences.makeWorldReadable();
        sharedPreferences.reload();

        String[] s = sharedPreferences.getString("packages", "").split(";");

        boolean is = false;
        for (String g : s) {
            if (g.equals(lpparam.packageName)) {
                XposedBridge.log("Hook packagename is:" + g);
                is = true;
                break;
            }
        }

        if (!is) {
            return;
        }

        MasterUtils.isFloating = sharedPreferences.getBoolean("float", true);
        MasterUtils.newThread = sharedPreferences.getBoolean("newthread", false);

        XposedBridge.log("aim hooked");

        XposedHelpers.findAndHookMethod("android.app.Activity", lpparam.classLoader, "onCreate", Bundle.class, new HOnCreate(lpparam));
        XposedHelpers.findAndHookMethod("android.app.Activity", lpparam.classLoader, "onResume", new HOnResume());
        XposedHelpers.findAndHookMethod("android.app.Dialog", lpparam.classLoader, "onKeyDown", int.class, KeyEvent.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                int keyCode = (int) param.args[0];
                Dialog dialog = (Dialog) param.thisObject;
                if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
//                    new FWindow(MasterUtils.nowAct, dialog);
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
                    new FWindow(lpparam, param);
                }
            }
        });
    }


}
