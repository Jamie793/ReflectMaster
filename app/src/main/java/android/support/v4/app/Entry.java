package android.support.v4.app;

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
        XSharedPreferences sharedPreferences = new XSharedPreferences("formatfa.reflectmaster", "package");
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

        MasterUtils.isUseWindowSearch = sharedPreferences.getBoolean("windowsearch", false);
        MasterUtils.isFloating = sharedPreferences.getBoolean("float", true);
        MasterUtils.newThread = sharedPreferences.getBoolean("newthread", false);


        id = sharedPreferences.getString("fid", "");
        int statu = sharedPreferences.getInt("statu", 0);
        String register = sharedPreferences.getString("register", "");

        XposedBridge.log("aim hooked");
        MasterUtils.windowSize = sharedPreferences.getInt("width", 700);
        MasterUtils.rotate = sharedPreferences.getBoolean("rotate", true);
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
