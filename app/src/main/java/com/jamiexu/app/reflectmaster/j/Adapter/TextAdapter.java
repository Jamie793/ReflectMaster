package com.jamiexu.app.reflectmaster.j.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.graphics.Color;

public class TextAdapter extends BaseAdapter {

    Context context;
    String[] items;

    public TextAdapter(Context context, String[] items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {

        return items.length;
    }

    @Override
    public Object getItem(int p1) {
        // TODO: Implement this method
        return items[p1];
    }

    @Override
    public long getItemId(int p1) {
        // TODO: Implement this method
        return items[p1].hashCode();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int p1, View p2, ViewGroup p3) {
        TextView tv = new TextView(context);
//        tv.setHeight(60);
        tv.setTextSize(16);
        tv.setGravity(Gravity.CENTER | Gravity.LEFT);
        tv.setText("  " + items[p1]);
        tv.setTextColor(Color.WHITE);
        // TODO: Implement this method
        return tv;
    }

}
