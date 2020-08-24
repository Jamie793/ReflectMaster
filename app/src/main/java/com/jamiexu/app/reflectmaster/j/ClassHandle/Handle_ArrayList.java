package com.jamiexu.app.reflectmaster.j.ClassHandle;

import android.annotation.SuppressLint;
import android.content.Context;
import com.jamiexu.app.reflectmaster.j.FieldWindow;
import com.jamiexu.app.reflectmaster.j.widget.WindowList;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;

public class Handle_ArrayList extends ClassHandle {

    ArrayList list;

    @SuppressLint("SetTextI18n")
    @Override
    public void handle(LinearLayout layout) {
        Button button = new Button(context);
        button.setText("View List");
        button.setOnClickListener(p1 -> showList());
        layout.addView(button);
    }

    Context context;
    Object obj;

    public Handle_ArrayList(Context context, Object obj) {
        super(context, obj);
        this.context = context;
        this.obj = obj;
        list = (ArrayList) obj;
    }


    void showList() {
        final WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        WindowList wl = new WindowList(context, wm);
        List<String> st = new ArrayList<String>();

        for (Object o : list) st.add(o.toString());
        wl.setItems(st);
        wl.setListener((p1, p2, p3, p4) -> FieldWindow.newWindow(null, null, context, list.get(p3), wm));
        wl.setTitle("ArrayListHandle,len:" + list.size());
        wl.show();

    }
}
