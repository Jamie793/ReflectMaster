package android.support.v4.app.Utils;

import android.util.Base64;

public class Base64Utils {

    public static String encode(byte[] bytes){
        return Base64.encodeToString(bytes,Base64.DEFAULT);
    }

    public static byte[] decode(String str){
        return Base64.decode(str,Base64.DEFAULT);
    }

}
