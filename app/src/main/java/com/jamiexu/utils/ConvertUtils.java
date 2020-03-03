package com.jamiexu.utils;

public class ConvertUtils {

    public static String bytesToHexs(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            stringBuilder.append(String.format("%02x", bytes[i] & 0xFF));
        }
        return stringBuilder.toString();
    }

    public static byte[] hexsToBytes(String hexs) {
        int len = hexs.length() / 2;
        byte[] bytes = new byte[len];
        for (int i = 0; i < hexs.length(); i=i+2) {
            bytes[i / 2] = (byte) Integer.parseInt(hexs.substring(i, i + 2), 16);
        }
        return bytes;
    }

}
