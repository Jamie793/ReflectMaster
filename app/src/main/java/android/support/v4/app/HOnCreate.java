package android.support.v4.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.support.v4.app.Utils.FileUtils;
import android.view.WindowManager;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import formatfa.reflectmaster.Utils.Utils;

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
//        XposedBridge.log(lpparam.packageName + " has hook by F:" + param.thisObject.getClass().getSimpleName());
//        XposedBridge.log(Environment.getExternalStorageDirectory().toString() + "/ReflectMaster/lib/libluajava.so");
//        XposedBridge.log(((Context)param.thisObject).getApplicationInfo().nativeLibraryDir+"/libluajava.so");

        String luajavaPath = ((Context) param.thisObject).getApplicationInfo().dataDir + "/app_lib/libluajava.so";


        XposedBridge.log("SO_PATH=>" + ((Context) param.thisObject).getApplicationInfo().dataDir + "/app_lib/libluajava.so");
        FileUtils.copyFile(Environment.getExternalStorageDirectory().toString() + "/ReflectMaster/lib/libluajava.so",
                luajavaPath, false, true);

        FileUtils.copyFile(Environment.getExternalStorageDirectory().toString() + "/ReflectMaster/lib/libffi.so",
                ((Context) param.thisObject).getApplicationInfo().dataDir + "/app_lib/libffi.so", false, true);

        FileUtils.putString(Environment.getExternalStorageDirectory().toString() + "/ReflectMaster/currentPackage.txt",luajavaPath);
//        XposedBridge.log(com.ja.);
        Utils.setLuaJavaSoPath(luajavaPath);

        MasterUtils.nowAct = (Activity) param.thisObject;
        new FWindow(lpparam, param);
        super.afterHookedMethod(param);
    }


}
