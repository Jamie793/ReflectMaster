package com.jamiexu.app.reflectmaster.j;

import android.app.Activity;
import android.os.Bundle;
import android.system.Os;
import android.view.KeyEvent;

import com.jamiexu.app.reflectmaster.MainActivity;
import com.jamiexu.utils.file.FileUtils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Entry implements IXposedHookLoadPackage {
    public static String id;
    public static XSharedPreferences sharedPreferences;
    public static boolean isFirst = false;

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) {
        sharedPreferences = new XSharedPreferences("com.jamiexu.app.reflectmaster", "package");
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


        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            try {
                PrintWriter printWriter = new PrintWriter(new FileOutputStream(MainActivity.BASE_PATH + "error.log"));
                e.printStackTrace(printWriter);
                printWriter.flush();
                printWriter.close();
                XposedBridge.log("ReflectMasterCrashError=>" + FileUtils.getString(MainActivity.BASE_PATH + "error.log"));
                System.exit(0);
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }

        });

        MasterUtils.isFloating = sharedPreferences.getBoolean("float", true);
        MasterUtils.newThread = sharedPreferences.getBoolean("newthread", false);

        XposedBridge.log("aim hooked");

        XposedHelpers.findAndHookMethod("android.app.Activity", lpparam.classLoader, "onCreate", Bundle.class, new HOnCreate(lpparam));
        XposedHelpers.findAndHookMethod("android.app.Activity", lpparam.classLoader, "onResume", new HOnResume());


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
