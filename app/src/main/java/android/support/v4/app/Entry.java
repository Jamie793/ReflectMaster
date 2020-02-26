package android.support.v4.app;

import android.app.Activity;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
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

//        File path2 = new File(new File(lpparam.appInfo.sourceDir).getParent() + "/lib/arm");


//        XposedHelpers.findAndHookMethod(System.class, "loadLibrary", String.class, new XC_MethodReplacement() {
//            @Override
//            protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
//                if (methodHookParam.args[0].toString().contains("luajava")) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                        System.loadLibrary(methodHookParam.args[0].toString());
//                    } else {
//                        System.loadLibrary(Environment.getDownloadCacheDirectory().toString() + "/ReflectMaster/lib/libluajava.so");
//                    }
//                }
//                return null;
//            }

//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                super.beforeHookedMethod(param);
//                if (param.args[0].toString().contains("luajava")) {
//                    if()
//                }
//            }
//        });


        Registers.isUseWindowSearch = sp.getBoolean("windowsearch", false);
        Registers.isFloating = sp.getBoolean("float", true);
        Registers.newThread = sp.getBoolean("newthread", false);


        id = sp.getString("fid", "");
        statu = sp.getInt("statu", 0);
        register = sp.getString("register", "");

        XposedBridge.log("aim hooked");
        Registers.windowSize = sp.getInt("width", 700);
        Registers.rotate = sp.getBoolean("rotate", true);
        XposedBridge.log("set Window size:" + Registers.windowSize);


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
                    FWindow win = new FWindow(Registers.nowAct, dialog);
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
                    Registers.nowAct = (Activity) param.thisObject;
                    FWindow win = new FWindow(lpparam, param);

                }
            }
        });
    }


}
