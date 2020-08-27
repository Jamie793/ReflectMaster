package com.jamiexu.app;

import android.content.Context;

import com.jamiexu.app.reflectmaster.MainActivity;

public class J {
    static {
        System.loadLibrary("JReflectMasterC");
    }

    public static native void init(MainActivity context);

    public static native void cf(String f,String t);

    public static native void i(Context j);



}
