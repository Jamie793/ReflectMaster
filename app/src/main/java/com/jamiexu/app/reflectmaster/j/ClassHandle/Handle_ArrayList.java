package com.jamiexu.app.reflectmaster.j.ClassHandle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.jamiexu.app.reflectmaster.j.FieldWindow;
import com.jamiexu.app.reflectmaster.j.widget.WindowList;

import java.util.ArrayList;
import java.util.List;

public class Handle_ArrayList extends ClassHandle {

    private ArrayList<Object> list;
    private Context context;
    private Object object;

    @SuppressLint("SetTextI18n")
    @Override
    public void handle(LinearLayout layout) {
        Button button = new Button(context);
        button.setText("View List");
        button.setOnClickListener(p1 -> showList());
        layout.addView(button);
    }


    public Handle_ArrayList(Context context, Object object) {
        super(context, object);
        this.context = context;
        this.object = object;
        list = new ArrayList<>((List<?>) object);
    }


    void showList() {
        final WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        WindowList wl = new WindowList(context, wm);
        List<String> st = new ArrayList<>();

        for (Object o : list) st.add(o.toString());
        wl.setItems(st);
        wl.setListener((p1, p2, p3, p4) -> FieldWindow.newWindow(null, null, context, list.get(p3), wm));
        wl.setTitle("ArrayListHandle,len:" + list.size());
        wl.show();

    }
}
