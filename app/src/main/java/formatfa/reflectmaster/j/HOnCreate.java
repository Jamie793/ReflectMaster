package formatfa.reflectmaster.j;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Environment;

import com.jamiexu.utils.FileUtils;

import android.view.WindowManager;

import java.io.File;

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

        String luajavaPath = ((Context) param.thisObject).getApplicationInfo().dataDir + "/app_lib";

        String cpu = formatfa.reflectmaster.j.reflectmaster.Utils.Utils.getCpu();
        XposedBridge.log("CPU=>" + cpu);

        if (!new File(luajavaPath).exists()) {
            File[] files = new File(Environment.getExternalStorageDirectory().toString() + "/ReflectMaster/lib/" + cpu).listFiles();
            for (File f : files) {
                FileUtils.copyFile(f.getAbsolutePath(), luajavaPath + "/" + f.getName(), false, true);
            }
        }

        Utils.setLuaJavaSoPath(luajavaPath + "/libluajava.so");

        MasterUtils.nowAct = (Activity) param.thisObject;
        new FWindow(lpparam, param);
        super.afterHookedMethod(param);
    }


}
