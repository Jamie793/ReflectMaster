package android.support.v4.app;

import android.app.Activity;

import de.robv.android.xposed.XC_MethodHook;

public class HOnResume extends XC_MethodHook {

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        super.afterHookedMethod(param);

        Registers.nowAct = (Activity) param.thisObject;


    }
}
