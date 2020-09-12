package com.jamiexu.app;

import android.content.Context;

import com.jamiexu.app.reflectmaster.MainActivity;

/**
 @author Jamiexu/Jamie793
 @version 1.0
 @date 2020/9/12
 @time 13:02
 @blog https://blog.jamiexu.cn
 **/

public class J {
    static {
        System.loadLibrary("JReflectMasterC");
    }

    public static native void init(MainActivity context);

    public static native void cf(String f,String t);

    public static native void i(Context j);

}
