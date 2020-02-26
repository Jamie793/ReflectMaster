package android.support.v4.app;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

public class HOnApp extends XC_MethodHook {

    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        super.beforeHookedMethod(param);
        XposedBridge.log("App is lucher..");
        XposedBridge.hookAllMethods(ClassLoader.class, "loadClass", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                String a = param.args[0].toString();
                if(a.contains("formatfa.reflectmaster")){
                    param.args[0] = "JamieXu";
                }else if(a.contains("de.robv.android.xposed")){
                    param.args[0] = "JamieXu";
                }
            }
        });


        XposedBridge.hookAllMethods(Object.class, "equals", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                String a = param.args[0].toString();
                if(a.contains("formatfa.reflectmaster")){
                    param.args[0] = "JamieXu";
                }else if(a.contains("de.robv.android.xposed")){
                    param.args[0] = "JamieXu";
                }
            }
        });


        XposedBridge.hookAllMethods(String.class, "contains", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                String a = param.args[0].toString();
                if(a.contains("formatfa.reflectmaster")){
                    param.args[0] = "JamieXu";
                }else if(a.contains("de.robv.android.xposed")){
                    param.args[0] = "JamieXu";
                }
            }
        });


        XposedBridge.hookAllMethods(String.class, "index", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                String a = param.args[0].toString();
                if(a.contains("formatfa.reflectmaster")){
                    param.args[0] = "JamieXu";
                }else if(a.contains("de.robv.android.xposed")){
                    param.args[0] = "JamieXu";
                }
            }
        });

    }
}
