package com.jamiexu.app.reflectmaster.j;

import android.content.Context;
import android.graphics.Color;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by Jamiexu on 2020-08-25.
 */


public class EditWindow extends Window {
    private String msg, text;
    private EditWindowListener listener;


    public EditWindow(XC_LoadPackage.LoadPackageParam lpparam, XC_MethodHook.MethodHookParam param, Context act, String msg, String text) {
        super(lpparam, param, act, null);
        this.msg = msg;
        this.text = text;
    }

    public EditWindowListener getListener() {
        return listener;
    }

    public void setListener(EditWindowListener listener) {
        this.listener = listener;
    }

    @Override
    public void show(final WindowManager windowManager, WindowManager.LayoutParams layoutParams) {
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;

        final LinearLayout layout = new LinearLayout(act);
        layout.setBackgroundColor(0xFF303030);
        layout.setOrientation(LinearLayout.VERTICAL);

        final ActionWindow actionWindow = new ActionWindow(this.act, windowManager, layoutParams, layout);
        layout.addView(actionWindow.getActionBar());

        TextView msg = new TextView(act);
        msg.setTextColor(Color.GREEN);
        msg.setText(this.msg);
        layout.addView(msg);


        final EditText value = new EditText(act);
        value.setTextColor(Color.WHITE);
        value.setHintTextColor(Color.WHITE);
        value.setHint("输入类名");
        if (text != null)
            value.setText(text);
        layout.addView(value);


        Button button = new Button(act);
        button.setText("确定");
        button.setTextColor(Color.WHITE);
        button.setBackgroundColor(0xFF2196F3);
        button.setOnClickListener(p1 -> {
            if (listener != null) {
                listener.onEdited(value.getText().toString());
            }
            windowManager.removeView(layout);
        });
        layout.addView(button);


        windowManager.addView(layout, layoutParams);

    }

    public interface EditWindowListener {
        void onEdited(String str);
    }

}
