package android.support.v4.app;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.support.v4.app.reflectmaster.Utils.Utils;
import android.view.WindowManager;

import java.io.File;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HOnCreate extends XC_MethodHook {

    private XC_LoadPackage.LoadPackageParam lpparam;


    WindowManager wm;


    public HOnCreate(XC_LoadPackage.LoadPackageParam lpparam) {
        this.lpparam = lpparam;

    }

    @Override
    protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param){

    }

    @Override
    protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
        XposedBridge.log(lpparam.packageName + " has hook by F:" + param.thisObject.getClass().getSimpleName());
        XposedBridge.log(Environment.getExternalStorageDirectory().toString() + "/ReflectMaster/lib/libluajava.so");
        XposedBridge.log(((Context)param.thisObject).getApplicationInfo().nativeLibraryDir+"/libluajava.so");
        Utils.copyFile(Environment.getExternalStorageDirectory().toString() + "/ReflectMaster/lib/libluajava.so",((Context)param.thisObject).getApplicationInfo().nativeLibraryDir+"/libluajava.so");
        Utils.copyFile(Environment.getExternalStorageDirectory().toString() + "/ReflectMaster/lib/libffi.so",((Context)param.thisObject).getApplicationInfo().nativeLibraryDir+"/libffi.so");


        Registers.nowAct = (Activity) param.thisObject;
        FWindow win = new FWindow(lpparam, param);
        super.afterHookedMethod(param);
    }


}
