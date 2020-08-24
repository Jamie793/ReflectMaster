package com.jamiexu.app.reflectmaster.j.Adapter;

/**
 * Created by formatfa on 18-5-12.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ConstructorAdapter extends BaseAdapter implements Filterable {

    private Context context;
    private Constructor<?>[] methods;

    private Constructor<?>[] rawMethod;


    public void setMethods(Constructor[] ms) {
        this.methods = ms;
        rawMethod = ms;
    }

    public ConstructorAdapter(Context context, Constructor<?>[] fields) {
        this.context = context;
        this.methods = fields;

        rawMethod = methods;
    }

    @Override
    public int getCount() {
        // TODO: Implement this method
        return methods.length;
    }

    @Override
    public Object getItem(int p1) {
        // TODO: Implement this method
        return methods[p1];
    }

    @Override
    public long getItemId(int p1) {
        // TODO: Implement this method
        return methods[p1].hashCode();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int p1, View p2, ViewGroup p3) {
        LinearLayout layout = new LinearLayout(context);


        layout.setOrientation(LinearLayout.VERTICAL);
        TextView name = new TextView(context);
        TextView params = new TextView(context);

        name.setText("Name：" + methods[p1].getName());

        StringBuilder sb = new StringBuilder();
        for (Type p : methods[p1].getGenericParameterTypes()) {
            sb.append(p.toString()).append(";");
        }


        params.setText("ParamterType：" + sb.toString());
        if (!methods[p1].isAccessible()) methods[p1].setAccessible(true);

        name.setTextColor(Color.GREEN);
        params.setTextColor(Color.WHITE);
        layout.addView(name);
        if (sb.length() != 0)
            layout.addView(params);
        return layout;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                FilterResults results = new FilterResults();
                List<Constructor<?>> temp = new ArrayList<>();
                for (Constructor<?> method : rawMethod) {
                    if (method.getName().toUpperCase().contains(charSequence.toString().toUpperCase()))
                        temp.add(method);


                }
                results.values = temp.toArray(new Constructor<?>[0]);
                results.count = temp.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

                methods = (Constructor<?>[]) filterResults.values;
                if (filterResults.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }
}

