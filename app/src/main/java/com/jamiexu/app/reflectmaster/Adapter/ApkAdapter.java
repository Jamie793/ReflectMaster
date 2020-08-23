package com.jamiexu.app.reflectmaster.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.jamiexu.app.reflectmaster.ApkInfo;
import com.jamiexu.app.reflectmaster.MainActivity;
import com.jamiexu.app.reflectmaster.R;

public class ApkAdapter extends BaseAdapter implements Filterable {

    private Context context;
    private ArrayList<ApkInfo> apkInfos, bak_apkInfos;
    private static Filter filter;

    public ApkAdapter(Context context, ArrayList<ApkInfo> apkInfos) {
        this.context = context;
        this.apkInfos = apkInfos;
        this.bak_apkInfos = apkInfos;
    }

    @Override
    public int getCount() {
        return this.apkInfos.size();
    }

    @Override
    public ApkInfo getItem(int position) {
        return this.apkInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Filter getFilter() {
        if (filter == null)
            filter = new ListFilter();
        return filter;
    }

    @SuppressLint({"SetTextI18n", "SdCardPath"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ApkInfo apkInfo = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(this.context).inflate(R.layout.item_apk, parent,
                    false);
            viewHolder = new ViewHolder();
            viewHolder.img_icon = view.findViewById(R.id.item_apk_icon);
            viewHolder.tv_title = view.findViewById(R.id.item_apk_title);
            viewHolder.btn_selection = view.findViewById(R.id.item_apk_selection);
            viewHolder.btn_open = view.findViewById(R.id.item_apk_open);
            viewHolder.btn_info = view.findViewById(R.id.item_apk_info);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.img_icon.setImageDrawable(apkInfo.getIcon());
        viewHolder.tv_title.setText(apkInfo.getTitle());

        if (MainActivity.SELECTED_APK_LIST.contains(apkInfo.getPackageName()))
            viewHolder.btn_selection.setText("UNSELECT");
        viewHolder.btn_selection.setOnClickListener((v) -> {
            if (MainActivity.SELECTED_APK_LIST.contains(apkInfo.getPackageName())) {
                MainActivity.SELECTED_APK_LIST.remove(apkInfo.getPackageName());
                viewHolder.btn_selection.setText("SELECT");
            } else {
                String cpu = com.jamiexu.app.reflectmaster.j.reflectmaster.Utils.Utils.getCpu();
                if (MainActivity.isRoot()) {
                    String luajavaPath = apkInfo.getDataPath() + "/app_lib/" + "/libJamieReflectMasterluajava.so";
                    if (!new File(luajavaPath).exists()) {
                        MainActivity.copyFile("/sdcard/ReflectMaster/lib/" + cpu + "/libluajava.so",
                                luajavaPath);
                    }
                }
                MainActivity.SELECTED_APK_LIST.add(apkInfo.getPackageName());
                viewHolder.btn_selection.setText("UNSELECT");
            }
            MainActivity.saveSelectedApk();
        });

        viewHolder.btn_open.setOnClickListener((v) -> {
            this.context.startActivity(this.context.getPackageManager().getLaunchIntentForPackage(
                    apkInfo.getPackageName()));
        });

        viewHolder.btn_info.setOnClickListener((v) -> {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", apkInfo.getPackageName(), null));
            this.context.startActivity(intent);
        });


        return view;
    }

    class ViewHolder {
        ImageView img_icon;
        TextView tv_title;
        Button btn_selection, btn_open, btn_info;
    }

    class ListFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            List<ApkInfo> apkInfos = new ArrayList<>();
            if (constraint.length() == 0) {
                apkInfos = ApkAdapter.this.bak_apkInfos;
            } else {
                for (ApkInfo apkInfo : ApkAdapter.this.apkInfos) {
                    if (apkInfo.getTitle().contains(constraint.toString()) || apkInfo.getPackageName().contains(constraint.toString())) {
                        apkInfos.add(apkInfo);
                    }
                }
            }
            filterResults.values = apkInfos;
            filterResults.count = apkInfos.size();
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ApkAdapter.this.apkInfos = (ArrayList<ApkInfo>) results.values;
            if (results.count > 0)
                notifyDataSetChanged();
            else
                notifyDataSetInvalidated();
        }
    }
}
