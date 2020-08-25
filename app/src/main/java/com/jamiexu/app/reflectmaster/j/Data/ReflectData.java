package com.jamiexu.app.reflectmaster.j.Data;

import android.content.Context;

public class ReflectData {
    public Context context;
    public Object object;

    public ReflectData(Context context, Object object) {
        this.context = context;
        this.object = object;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
