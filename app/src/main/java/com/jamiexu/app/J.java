package com.jamiexu.app;

import com.jamiexu.app.reflectmaster.MainActivity;

public class J {
    static {
        System.loadLibrary("JReflectMasterC");
    }

    public static native void init(MainActivity context);
}
