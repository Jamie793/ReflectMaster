package com.jamiexu.app.reflectmaster.factory;

import android.content.Context;

import com.jamiexu.app.reflectmaster.j.LuaExecutor;
import com.jamiexu.app.reflectmaster.j.Window;

import java.util.HashMap;

public class LuaExecutorFactory {
    private static HashMap<Context, LuaExecutor> hashMap = new HashMap<>();

    public static LuaExecutor newInstance(Context context, Window jf) {
        LuaExecutor L;
        if (hashMap.containsKey(context))
            L = hashMap.get(context);
        else {
            L = new LuaExecutor(context, jf);
            hashMap.put(context, L);
        }
        return L;
    }

    public static LuaExecutor newInstance(Context context) {
        LuaExecutor L;
        if (hashMap.containsKey(context))
            L = hashMap.get(context);
        else {
            L = new LuaExecutor(context, null);
            hashMap.put(context, L);
        }
        return L;
    }
}
