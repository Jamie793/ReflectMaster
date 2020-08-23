package com.jamiexu.app.reflectmaster.j;

import android.app.Activity;

import de.robv.android.xposed.XC_MethodHook;

public class HOnResume extends XC_MethodHook {

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        super.afterHookedMethod(param);

        MasterUtils.nowAct = (Activity) param.thisObject;


    }
}
