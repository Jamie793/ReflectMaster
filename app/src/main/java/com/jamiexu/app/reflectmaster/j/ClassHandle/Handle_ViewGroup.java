package com.jamiexu.app.reflectmaster.j.ClassHandle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.jamiexu.app.reflectmaster.j.FieldWindow;
import com.jamiexu.app.reflectmaster.j.widget.WindowList;

import java.util.ArrayList;
import java.util.List;

public class Handle_ViewGroup extends ClassHandle {
    private Context context;
    private Object obj;

    public Handle_ViewGroup(Context context, Object obj) {
        super(context, obj);
        this.context = context;
        this.obj = obj;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void handle(LinearLayout layout) {
        Button button = new Button(context);
        button.setText("View");
        button.setOnClickListener(p1 -> showList());
        layout.addView(button);
    }

    private void showList() {
        final WindowManager wm = (WindowManager) context.getSystemService(context.WINDOW_SERVICE);

        WindowList wl = new WindowList(context, wm);
        List<String> st = new ArrayList<String>();

        ViewGroup g = (ViewGroup) obj;
        final List<View> views = new ArrayList<View>();

        for (int i = 0; i < g.getChildCount(); i += 1) {

            views.add(g.getChildAt(i));
            st.add(g.getChildAt(i).getClass().getCanonicalName());

        }

        wl.setItems(st);
        wl.setListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
                FieldWindow.newWindow(null, null, context, views.get(p3), wm);
            }
        });
        wl.setTitle("ViewGroupHandle,len:" + views.size());
        wl.show();

    }

}
