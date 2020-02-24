package formatfa.reflectmaster;

import android.content.Context;

import com.androlua.LuaApplication;

public class RFApplication extends LuaApplication {
    public static StaticClass staticClass;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        staticClass = new StaticClass();
    }
}
