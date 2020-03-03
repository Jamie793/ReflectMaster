package com.jamiexu.utils;

import android.annotation.SuppressLint;
import android.util.Base64;

public class Base64Utils {

    public static String encode(byte[] bytes) {
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    public static byte[] decode(String str) {
        return Base64.decode(str, Base64.DEFAULT);
    }

    @SuppressLint("NewApi")
    public static String encodes(byte[] byts) {
        return ConvertUtils.bytesToHexs(java.util.Base64.getEncoder().encode(byts));
    }

    @SuppressLint("NewApi")
    public static byte[] decodes(String str) {
        return java.util.Base64.getDecoder().decode(ConvertUtils.hexsToBytes(str));
    }

}
