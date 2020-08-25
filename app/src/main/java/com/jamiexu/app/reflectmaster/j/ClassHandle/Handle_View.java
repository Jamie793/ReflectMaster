package com.jamiexu.app.reflectmaster.j.ClassHandle;

import android.annotation.SuppressLint;
import android.content.Context;

import com.jamiexu.app.reflectmaster.j.FieldWindow;
import com.jamiexu.app.reflectmaster.j.WindowUtils;
import com.jamiexu.utils.reflect.ReflectUtils;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Handle_View extends ClassHandle {

    private Context context;
    private Object obj;

    public Handle_View(Context context, Object obj) {
        super(context, obj);
        this.context = context;
        this.obj = obj;
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void handle(LinearLayout layout) {
        Button viewClickListener = new Button(context);
        viewClickListener.setText("View listener");
        viewClickListener.setOnClickListener(v -> {
            View view = (View) obj;
            try {
                @SuppressLint("DiscouragedPrivateApi")
                Method getListenerInfo = ReflectUtils.getMethod(View.class,"getListenerInfo");
                assert getListenerInfo != null;
                getListenerInfo.setAccessible(true);
                @SuppressLint("PrivateApi")
                Field filed_OnClickListener = Class.forName("android.view.View$ListenerInfo").getDeclaredField("mOnClickListener");
                Object mListnerInfo = getListenerInfo.invoke(view);

                Object mOnClickListener = filed_OnClickListener.get(mListnerInfo);
                FieldWindow w = new FieldWindow(null, null, context, mOnClickListener);


                w.show(WindowUtils.getWm(context), WindowUtils.getLp());


            } catch (Exception e) {
                Toast.makeText(context, "读取点击事件异常:" + e.toString(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }


        });
        layout.addView(viewClickListener);
    }


}
