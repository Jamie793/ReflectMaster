package com.jamiexu.app.reflectmaster.j.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ViewConstructor")
public class ViewLineView extends View implements OnTouchListener {

    //click start
    private long startTime;
    private Context context;
    private List<View> views;
    private Canvas canvas;
    private Paint paint;
    private ViewLineClickListener viewLineClickListener;


    public ViewLineView(Context context, List<View> views, ViewLineClickListener viewLineClickListener) {
        super(context);
        this.context = context;
        this.views = views;
        this.viewLineClickListener = viewLineClickListener;
        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        setOnTouchListener(this);
        this.setBackground(null);
    }


    private int getStatusBarHeight() {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);

        return height;
    }

    @Override
    public boolean onTouch(View p1, MotionEvent p2) {
        int action = p2.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                startTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_UP:
                long time = System.currentTimeMillis() - startTime;
                if (time < 500) {
                    Rect rect = new Rect();
                    int x = (int) p2.getRawX();
                    int y = (int) p2.getRawY();
                    List<View> result = new ArrayList<>();

                    //碰撞检测
                    for (int i = views.size() - 1; i >= 0; i -= 1) {
                        View v = views.get(i);
                        v.getGlobalVisibleRect(rect);
                        if (x >= rect.left && x <= rect.right && y >= rect.top && y <= rect.bottom) {
                            result.add(v);
                        }
                    }
                    this.viewLineClickListener.onClick(this,result);
                } else if (time > 500) {
                    this.viewLineClickListener.onLongClick(this);
                }
                break;
        }
        return true;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        this.canvas = canvas;
        for (View v : views) {
            if (v instanceof ViewGroup)
                paint.setColor(Color.BLUE);
            else
                paint.setColor(Color.GREEN);
            @SuppressLint("DrawAllocation")
            Rect rect = new Rect();
            v.getGlobalVisibleRect(rect);
            rect.top -= getStatusBarHeight();
            rect.bottom -= getStatusBarHeight();
            canvas.drawRect(rect, paint);
        }
        super.onDraw(canvas);
    }

}
