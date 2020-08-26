package com.jamiexu.app.reflectmaster.j.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jamiexu.app.reflectmaster.j.Data.ReflectData;

import java.util.ArrayList;
import java.util.HashMap;

public class RegisterAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> stringArrayList = new ArrayList<>();
    private ArrayList<ReflectData> reflectDataArrayList;
    private HashMap<String, ReflectData> reflectDataHashMap;

    public RegisterAdapter(Context context, ArrayList<ReflectData> reflectDataArrayList) {
        this.context = context;
        this.reflectDataArrayList = reflectDataArrayList;
    }

    public RegisterAdapter(Context context, HashMap<String, ReflectData> reflectDataHashMap) {
        this.context = context;
        this.reflectDataHashMap = reflectDataHashMap;
        this.stringArrayList.addAll(this.reflectDataHashMap.keySet());
    }


    public void refreshData(){
        this.notifyDataSetChanged();
    }

    public void setItems(ArrayList<ReflectData> reflectDataArrayList){
        this.reflectDataArrayList = reflectDataArrayList;
    }

    public void setItems(HashMap<String, ReflectData> reflectDataHashMap){
        this.reflectDataHashMap = reflectDataHashMap;
        this.stringArrayList.clear();
        this.stringArrayList.addAll(this.reflectDataHashMap.keySet());
    }

    @Override
    public int getCount() {
        int size = this.stringArrayList.size();
        if (size == 0 && this.reflectDataArrayList != null)
            size = this.reflectDataArrayList.size();
        return size;

    }

    @Override
    public Object getItem(int position) {
        Object item = null;
        if (this.stringArrayList.size() != 0) {
            item = this.stringArrayList.get(position);
        } else if (this.reflectDataArrayList != null) {
            item = this.reflectDataArrayList.get(position);
        }
        return item;
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @SuppressLint({"RtlHardcoded", "SetTextI18n"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LinearLayout linearLayout = new LinearLayout(this.context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        TextView textView = new TextView(this.context);
        textView.setTextSize(16);
        textView.setGravity(Gravity.CENTER | Gravity.LEFT);
        textView.setTextColor(Color.GREEN);

        TextView textView2 = new TextView(this.context);
        textView2.setTextSize(16);
        textView2.setGravity(Gravity.CENTER | Gravity.LEFT);
        textView2.setTextColor(Color.WHITE);

        Object item = getItem(position);
        if (item instanceof String) {
            textView.setText("Name:" + item);
            textView2.setText("Value:" + this.reflectDataHashMap.get(item));
        } else {
            textView.setText(item + "");
        }

        linearLayout.addView(textView);
        if (textView2.toString().trim().length() != 0)
            linearLayout.addView(textView2);

        return linearLayout;
    }
}
