package com.jamiexu.app.reflectmaster.j.ClassHandle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jamiexu.app.reflectmaster.j.ImageWindow;
import com.jamiexu.app.reflectmaster.j.WindowUtils;

public class Handle_ImageView extends ClassHandle {

    private Context act;
    private Object obj;

    public Handle_ImageView(Context act, Object obj) {
        super(act, obj);
        this.act = act;
        this.obj = obj;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void handle(LinearLayout layout) {
        Button button = new Button(context);
        button.setText("View Img");
        button.setOnClickListener(p1 -> {
            Drawable draw = ((ImageView) obj).getDrawable();
            if(draw == null)
                return;
            ImageWindow iw = new ImageWindow(this.context,null, null, act, draw);
            iw.show(WindowUtils.getWm(act), WindowUtils.getLp());

        });
        layout.addView(button);
    }

}
