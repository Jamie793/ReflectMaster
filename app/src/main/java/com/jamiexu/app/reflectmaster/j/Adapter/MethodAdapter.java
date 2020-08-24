package com.jamiexu.app.reflectmaster.j.Adapter;

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

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MethodAdapter extends BaseAdapter implements Filterable {

    private Context context;
    private Method[] methods;
    private Method[] rawMethod;


    public void setMethods(Method[] methods) {
        this.methods = methods;
        this.rawMethod = methods;
    }

    public MethodAdapter(Context context, Method[] methods) {
        this.context = context;
        this.methods = methods;
        this.rawMethod = methods;
    }

    @Override
    public int getCount() {
        // TODO: Implement this method
        return this.methods.length;
    }

    @Override
    public Object getItem(int p1) {
        // TODO: Implement this method
        return this.methods[p1];
    }

    @Override
    public long getItemId(int p1) {
        // TODO: Implement this method
        return this.methods[p1].hashCode();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int p1, View p2, ViewGroup p3) {
        LinearLayout layout = new LinearLayout(context);


        layout.setOrientation(LinearLayout.VERTICAL);
        TextView name = new TextView(context);
        TextView params = new TextView(context);
        TextView returntype = new TextView(context);

        returntype.setText("ReturnType：" + this.methods[p1].getGenericReturnType());
        name.setText("Name:" + this.methods[p1].getName());

        StringBuilder sb = new StringBuilder();
        for (Type p : this.methods[p1].getGenericParameterTypes()) {
            sb.append(p.toString()).append(";");
        }
        params.setText("ParamterType：" + sb.toString());
        name.setTextColor(Color.GREEN);
        params.setTextColor(Color.WHITE);
        returntype.setTextColor(Color.WHITE);
        layout.addView(name);
        if (sb.length() != 0)
            layout.addView(params);
        layout.addView(returntype);
        return layout;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                FilterResults results = new FilterResults();
                List<Method> temp = new ArrayList<>();
                for (Method method : rawMethod) {
                    if (method.getName().toUpperCase().contains(charSequence.toString().toUpperCase()))
                        temp.add(method);
                }
                results.values = temp.toArray(new Method[0]);
                results.count = temp.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                methods = (Method[]) filterResults.values;
                if (filterResults.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }
}
