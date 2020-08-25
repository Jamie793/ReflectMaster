package com.jamiexu.app.reflectmaster.j.ClassHandle;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Color;
import android.content.ClipboardManager;

import com.jamiexu.app.reflectmaster.j.WindowUtils;
import com.jamiexu.app.reflectmaster.j.reflectmaster.Utils.Utils;
import com.jamiexu.app.reflectmaster.j.widget.EditWindow;
import com.jamiexu.app.reflectmaster.j.widget.EditWindowListener;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class Handle_TextView extends ClassHandle {


    private Context context;
    private Object obj;
    private TextView text;

    public Handle_TextView(Context context, Object obj) {
        super(context, obj);
        this.context = context;
        this.obj = obj;
        text = (TextView) obj;


    }

    @SuppressLint("SetTextI18n")
    @Override
    public void handle(LinearLayout layout) {
        String s = text.getText().toString();
        if (s.length() > 50) s = s.substring(25);
        Button button = new Button(context);
        button.setText("Copy text");
        button.setOnClickListener(p1 -> {
            Utils.writeClipboard(this.context, text.getText().toString());
        });

        layout.addView(button);
    }


}
