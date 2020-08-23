package com.jamiexu.app.reflectmaster.j.Adapter;

import android.content.Context;
import android.graphics.Color;
import com.jamiexu.app.reflectmaster.j.MasterUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class ObjectAdapter extends BaseAdapter {

    Context context;
    List<Object> objects;

    public void setObjects(List<Object> objects) {
        this.objects = objects;
    }

    public ObjectAdapter(Context context, List<Object> objects) {
        this.context = context;
        this.objects = objects;
    }


    @Override
    public int getCount() {

        return objects.size();
    }

    @Override
    public Object getItem(int p1) {

        return objects.get(p1);
    }

    @Override
    public long getItemId(int p1) {

        return objects.get(p1).hashCode();
    }

    @Override
    public View getView(int p1, View p2, ViewGroup p3) {
        LinearLayout layout = new LinearLayout(context);

        layout.setOrientation(LinearLayout.VERTICAL);
        TextView name = new TextView(context);
        TextView type = new TextView(context);
        TextView value = new TextView(context);

        //name.setText(objects.getName());

        Object ob = objects.get(p1);
        if (ob != null) {
            name.setText(objects.get(p1).getClass().getCanonicalName());
        }

           value.setText(MasterUtils.getObjectString(ob));


        name.setTextColor(Color.RED);
        //type.setTextColor(Color.GREEN);
        value.setTextColor(Color.WHITE);
        layout.addView(name);
        //layout.addView(type);
        layout.addView(value);
        return layout;
    }

}