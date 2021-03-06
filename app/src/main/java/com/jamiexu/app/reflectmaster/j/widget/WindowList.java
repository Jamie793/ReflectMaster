package com.jamiexu.app.reflectmaster.j.widget;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jamiexu.app.reflectmaster.j.ActionWindow;
import com.jamiexu.app.reflectmaster.j.Adapter.TextAdapter;
import com.jamiexu.app.reflectmaster.j.Data.ReflectData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WindowList implements OnItemClickListener, AdapterView.OnItemLongClickListener {


    private Context context;
    private WindowManager manager;
    private LinearLayout layout;
    private ListView lv;
    private TextView titleview;
    private OnItemClickListener listener;
    private String title;
    private ListAdapter adapter;
    private AdapterView.OnItemLongClickListener onLongClickListener;
    private WindowManager.LayoutParams layoutParam;


    public WindowList(Context context, WindowManager manager) {
        this.context = context;
        this.manager = manager;
        layoutParam = new WindowManager.LayoutParams();
        layoutParam.x = 0;
        layoutParam.y = 0;
        layoutParam.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParam.type = WindowManager.LayoutParams.TYPE_APPLICATION;

        layout = new LinearLayout(context);
        layout.setBackgroundColor(Color.DKGRAY);
        layout.setOrientation(LinearLayout.VERTICAL);


        ActionWindow ar = new ActionWindow(context, manager, layoutParam, layout);
        layout.addView(ar.getActionBar());
        titleview = new TextView(context);
        titleview.setTextColor(Color.WHITE);
        layout.addView(titleview);

        LinearLayout buttonLayout = new LinearLayout(context);
        buttonLayout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(buttonLayout);

        lv = new ListView(context);
        lv.setFastScrollEnabled(true);
        lv.setOnItemClickListener(this);
        lv.setOnItemLongClickListener(this);
        layout.addView(lv);

    }

    public ListView getListView() {
        return lv;
    }

    public void setAdaptet(ListAdapter l) {
        adapter = l;
    }

    public void setItems(String[] items) {
        TextAdapter ta = new TextAdapter(context, items);
        setAdaptet(ta);
    }

    public void setItmes(ArrayList<ReflectData> objectArrayList) {
        ArrayList<String> arrayList = new ArrayList<>();
        for (ReflectData reflectData : objectArrayList) {
            arrayList.add(reflectData.getObject() + "");
        }
        setItems(arrayList.toArray(new String[0]));
    }

    public void setItmes(HashMap<String, ReflectData> reflectDataHashMap) {
        ArrayList<String> arrayList = new ArrayList<>();
        for (Map.Entry<String, ReflectData> reflectDataEntry : reflectDataHashMap.entrySet()) {
            arrayList.add("Name:" + reflectDataEntry.getKey() + "\nValue:" + reflectDataEntry.
                    getValue().getObject() + "");
        }
        setItems(arrayList.toArray(new String[0]));
    }


    public void setItems(List<String> items) {
        setItems(items.toArray(new String[0]));
    }


    public void show() {
        show(500, 500);
    }


    public void show(int w, int h) {
        layoutParam.width = w;
        layoutParam.height = h;

        titleview.setBackgroundColor(Color.BLUE);
        titleview.setTextSize(14);
        if (title != null) titleview.setText(title);
        if (adapter != null) lv.setAdapter(adapter);
        manager.addView(layout, layoutParam);
    }

    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
        if (listener != null) {
            listener.onItemClick(p1, p2, p3, p4);
        }

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (this.onLongClickListener != null)
            this.onLongClickListener.onItemLongClick(parent, view, position, id);
//        manager.removeView(layout);
        return true;
    }
}
