package android.support.v4.app.reflectmaster.Adapter;

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

import java.util.ArrayList;

import formatfa.reflectmaster.ApkInfo;
import formatfa.reflectmaster.MainActivity;
import formatfa.reflectmaster.R;

public class ApkAdapter extends BaseAdapter implements Filterable {

    private Context context;
    private ArrayList<ApkInfo> apkInfos;

    public ApkAdapter(Context context, ArrayList<ApkInfo> apkInfos) {
        this.context = context;
        this.apkInfos = apkInfos;
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

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ApkInfo apkInfo = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(this.context).inflate(R.layout.item_apk, parent, false);
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
                MainActivity.SELECTED_APK_LIST.add(apkInfo.getPackageName());
                viewHolder.btn_selection.setText("UNSELECT");
            }
            MainActivity.saveSelectedApk();
        });

        viewHolder.btn_open.setOnClickListener((v) -> {
            this.context.startActivity(this.context.getPackageManager().getLaunchIntentForPackage(apkInfo.getPackageName()));
        });

        viewHolder.btn_info.setOnClickListener((v) -> {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package",apkInfo.getPackageName(),null));
            this.context.startActivity(intent);
        });


        return view;
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    class ViewHolder {
        ImageView img_icon;
        TextView tv_title;
        Button btn_selection, btn_open, btn_info;
    }
}
