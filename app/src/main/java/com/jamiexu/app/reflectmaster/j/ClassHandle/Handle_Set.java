package com.jamiexu.app.reflectmaster.j.ClassHandle;

import android.annotation.SuppressLint;
import android.content.Context;
import com.jamiexu.app.reflectmaster.j.FieldWindow;
import com.jamiexu.app.reflectmaster.j.MasterUtils;
import com.jamiexu.app.reflectmaster.j.widget.WindowList;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by formatfa on 18-4-22.
 */

public class Handle_Set extends ClassHandle {

    private Set set;

    public Handle_Set(Context context, Object obj) {
        super(context, obj);
        this.context = context;
        this.obj = obj;
        set = (Set) obj;

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void handle(LinearLayout layout) {


        Button button = new Button(context);
        button.setText("View Set");
        button.setOnClickListener(p1 -> showList());
        layout.addView(button);

    }

    private void showList() {
        final WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        WindowList wl = new WindowList(context, wm);
        List<String> st = new ArrayList<>();
        final List<Object> objects = new ArrayList<>();
        final Iterator<?> iterator = set.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            Object ob = iterator.next();
            st.add(i + " " + MasterUtils.getObjectString(ob));
            objects.add(ob);
        }
        wl.setItems(st);
        wl.setListener((p1, p2, p3, p4) -> FieldWindow.newWindow(null, null, context, objects.get(p3), wm));
        wl.setTitle("   Set Len:" + objects.size());
        wl.show();

    }
}
