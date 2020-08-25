package com.jamiexu.app.reflectmaster.j;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jamiexu.app.reflectmaster.j.reflectmaster.Utils.Utils;

import java.lang.reflect.Field;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class EditFieldWindow extends Window {

    private Field field;
    private static int TYPE_EDIT = 0;

    EditFieldWindow(XC_LoadPackage.LoadPackageParam lpparam, XC_MethodHook.MethodHookParam param, Context act, Object object, Field thiz) {
        super(lpparam, param, act, object);
        field = thiz;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void show(final WindowManager windowManager, WindowManager.LayoutParams layoutParams) {
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        final LinearLayout layout = new LinearLayout(this.act);
        layout.setBackgroundColor(0xFF303030);
        layout.setOrientation(LinearLayout.VERTICAL);

        ActionWindow actionWindow = new ActionWindow(this.act, windowManager, layoutParams, layout);
        layout.addView(actionWindow.getActionBar());

        TextView name = new TextView(act);
        name.setTextColor(Color.GREEN);
        name.setText("Name:" + field.getName());
        layout.addView(name);

        final EditText value = new EditText(act);
        value.setTextColor(Color.WHITE);
        value.setHintTextColor(Color.WHITE);
        value.setHint(field.getType().getCanonicalName());
        try {
            value.setText(String.valueOf(field.get(object)));
        } catch (IllegalAccessException | IllegalArgumentException ignored) {
        }

        layout.addView(value);


        Button button = new Button(this.act);
        button.setBackgroundColor(0xFF2196F3);
        button.setTextColor(Color.WHITE);
        button.setText("修改");
        button.setOnClickListener(p1 -> {
            Object result = MasterUtils.parseValue(field.getType().getCanonicalName(),
                    value.getText().toString());
            try {
                field.set(object, result);
            } catch (IllegalAccessException | IllegalArgumentException e) {
                Toast.makeText(act, "set value err:" + e.toString(), Toast.LENGTH_SHORT).show();
            }
            Utils.showToast(act, "修改完成", Toast.LENGTH_SHORT);
            windowManager.removeView(layout);
        });
        layout.addView(button);

        Button button2 = new Button(this.act);
        button2.setText("持久修改");
        button2.setBackgroundColor(0xFF2196F3);
        button2.setTextColor(Color.WHITE);
        button2.setOnClickListener(p1 -> {
            new Thread(() -> {
                while (true) {
                    try {
                        Object result = MasterUtils.parseValue(field.getType().getCanonicalName(),
                                value.getText().toString());
                        field.set(object, result);
                    } catch (IllegalAccessException | IllegalArgumentException e) {
                        Utils.showToast(act, "set value err:" + e.toString(), Toast.LENGTH_SHORT);
                    }
                }
            }).start();
            Toast.makeText(act, "持续化修改中", Toast.LENGTH_SHORT).show();
            windowManager.removeView(layout);
        });
        layout.addView(button2);

        windowManager.addView(layout, layoutParams);
    }
}
