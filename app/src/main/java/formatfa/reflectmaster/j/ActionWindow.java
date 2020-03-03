package formatfa.reflectmaster.j;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class ActionWindow {
    private Context context;
    private WindowManager manager;
    private WindowManager.LayoutParams lp;
    private View view;
    private int startX, startY, nowX, nowY;
    private ActionSearchCallback searchCallback;
    private LinearLayout rootLayout;
    private LinearLayout contain;
    private Button test;
    private Button close;
    private Button move;
    private Button resize;
    private EditText search;

    public ActionSearchCallback getSearchCallback() {
        return searchCallback;
    }

    public void setSearchCallback(ActionSearchCallback searchCallback) {
        this.searchCallback = searchCallback;
    }

    public ActionWindow(Context context, WindowManager manager, WindowManager.LayoutParams lp, View view) {

        this.context = context;
        this.manager = manager;
        this.lp = lp;
        this.view = view;
        init(false);
    }

    public ActionWindow(Context context, WindowManager manager, WindowManager.LayoutParams lp, View view, boolean search) {
        this.context = context;
        this.manager = manager;
        this.lp = lp;
        this.view = view;
        init(search);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init(boolean search) {
        if (lp.width == 0) lp.width = context.getResources().getDisplayMetrics().widthPixels;
        if (lp.height == 0) lp.height = context.getResources().getDisplayMetrics().heightPixels;
        rootLayout = new LinearLayout(context);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setBackgroundColor(0xFFDDDADA);
        contain = new LinearLayout(context);

        test = new Button(context);
        test.setText("Zoom");
        test.setTextColor(0xFFFFFFFF);
        test.setOnTouchListener(new ResizsListener(false));
        test.setBackground(null);

        close = new Button(context);
        close.setText("close");
        close.setTextColor(0xFFFFFFFF);
        close.setOnClickListener(p1 -> manager.removeView(view));
        close.setBackground(null);


        move = new Button(context);
        move.setText("Drag");
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

        resize = new Button(context);
        resize.setText("++");
        resize.setOnTouchListener(new ResizsListener(true));
        contain.addView(test);
        contain.addView(move);
        contain.addView(close);
        contain.setBackgroundColor(0xFF2196F3);
        rootLayout.addView(contain);
        if (search) {
            this.search = new EditText(context);
            this.search.setTextColor(0xFFFF4081);
            this.search.setHint("Input filter...");
            this.search.setHintTextColor(0xFFFF4081);
            this.search.setBackgroundColor(0xFFFFFFFF);


            this.search.addTextChangedListener(new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {

                    if (searchCallback != null)
                        searchCallback.onTextChange(ActionWindow.this.search, editable.toString());
                }
            });
            rootLayout.addView(this.search);
        }
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
