package com.jamiexu.app.reflectmaster.j.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jamiexu.app.reflectmaster.j.HOnCreate;
import com.jamiexu.app.reflectmaster.j.MasterUtils;
import com.jamiexu.utils.reflect.ReflectUtils;

import java.lang.reflect.Field;
import java.util.List;

public class ObjectAdapter extends BaseAdapter {
    private Context context;
    private List<View> views;

    public ObjectAdapter(Context context, List<View> views) {
        this.context = context;
        this.views = views;
    }


    public void setViews(List<View> views) {
        this.views = views;
    }

    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public Object getItem(int p1) {
        return views.get(p1);
    }

    @Override
    public long getItemId(int p1) {
        return views.get(p1).hashCode();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public View getView(int p1, View p2, ViewGroup p3) {
        LinearLayout layout = new LinearLayout(this.context);
        layout.setOrientation(LinearLayout.VERTICAL);

        TextView name = new TextView(this.context);
        name.setTextColor(Color.GREEN);

        TextView property = new TextView(this.context);
        property.setTextColor(Color.WHITE);


        TextView value = new TextView(this.context);
        value.setTextColor(Color.WHITE);


        View view = views.get(p1);
        name.setText("Name:" + view.getClass().getCanonicalName());
        String v = MasterUtils.getObjectString(view);

        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);
        @SuppressLint("ResourceType")
        String stringBuilder = "Hex-ID:0x" + String.format("%08x", view.getId()).toUpperCase();
        try {
            Field[] fields = ReflectUtils.getAllFields(Class.forName(HOnCreate.lpparam.packageName
                    + ".R"));
            for (Field f : fields) {
                if (Integer.parseInt((String) ReflectUtils.getFieldValue(f, view)) == view.getId()) {
                    stringBuilder += "\n" + "ID-Name:" + f.getName();
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (view.getContentDescription() != null && view.getContentDescription().length() != 0 &&
                view.getContentDescription().toString().equals("null"))
            stringBuilder += "\n" + "Content-Desc:" + view.getContentDescription();

        stringBuilder += "\n" + "Bounds:" +
                String.format("[T%d,L%d][B%d,R%d]", rect.top, rect.left, rect.bottom,
                        rect.right);
        property.setText(stringBuilder);

        if (v != null)
            value.setText("Value:" + MasterUtils.getObjectString(view));

        layout.addView(name);
        layout.addView(property);
        if (v != null && !v.equals("null"))
            layout.addView(value);
        return layout;
    }
}
