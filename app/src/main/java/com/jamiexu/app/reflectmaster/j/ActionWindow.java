package com.jamiexu.app.reflectmaster.j;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class ActionWindow {

    //  Update by Jamiexu 2020-08-25


    private Context context;
    private WindowManager manager;
    private WindowManager.LayoutParams lp;
    private View view;
    private int startX, startY, nowX, nowY;
    private LinearLayout rootLayout;
    private LinearLayout contain;


    public ActionWindow(Context context, WindowManager manager, WindowManager.LayoutParams lp, View view) {

        this.context = context;
        this.manager = manager;
        this.lp = lp;
        this.view = view;
        init();
    }


    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    private void init() {
        if (lp.width == 0) lp.width = context.getResources().getDisplayMetrics().widthPixels;
        if (lp.height == 0) lp.height = context.getResources().getDisplayMetrics().heightPixels;
        rootLayout = new LinearLayout(context);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setBackgroundColor(0xFFDDDADA);
        this.contain = new LinearLayout(context);

        Button test = new Button(context);
        test.setText("Z");
        test.setTextColor(0xFFFFFFFF);
        test.setOnTouchListener(new ResizsListener(false));
        test.setBackground(null);

        Button close = new Button(context);
        close.setText("C");
        close.setTextColor(0xFFFFFFFF);
        close.setOnClickListener(p1 -> manager.removeView(view));
        close.setBackground(null);


        Button move = new Button(context);
        move.setText("D");
        move.setTextColor(0xFFFFFFFF);
        move.setBackground(null);


        move.setOnTouchListener((p1, p2) -> {

            switch (p2.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX = (int) p2.getRawX();
                    startY = (int) p2.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    nowX = (int) p2.getRawX();
                    nowY = (int) p2.getRawY();

                    lp.x += nowX - startX;
                    lp.y += nowY - startY;

                    startX = nowX;
                    startY = nowY;
                    manager.updateViewLayout(view, lp);
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
            return false;
        });


        contain.addView(test);
        contain.addView(move);
        contain.addView(close);
        contain.setBackgroundColor(0xFF2196F3);
        rootLayout.addView(contain);

    }

    public void addView(View view) {
        this.contain.addView(view);
    }

    public LinearLayout getActionBar() {
        return rootLayout;
    }

    class ResizsListener implements OnTouchListener {
        boolean isJia;

        public ResizsListener(boolean isJia) {
            this.isJia = isJia;
        }

        @Override
        public boolean onTouch(View p1, MotionEvent p2) {


            switch (p2.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX = (int) p2.getRawX();
                    startY = (int) p2.getRawY();
                    break;


                case MotionEvent.ACTION_MOVE:
                    nowX = (int) p2.getRawX();
                    nowY = (int) p2.getRawY();


                    lp.width += nowX - startX;
                    lp.height += nowY - startY;

                    if (lp.width <= 200) lp.width = 200;
                    if (lp.height <= 200) lp.height = 200;

                    startX = nowX;
                    startY = nowY;
                    manager.updateViewLayout(view, lp);

                    break;


                case MotionEvent.ACTION_UP:
                    break;


            }
            return false;
        }

    }


}
