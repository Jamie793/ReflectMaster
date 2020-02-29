package android.support.v4.app.reflectmaster.Adapter;

import android.content.Context;
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
        viewHolder.btn_selection.setOnClickListener((v)->{
            String packages = MainActivity.sharedPreferences.getString("packages","");
            MainActivity.sharedPreferences.edit().putString("packages",packages+";"+apkInfo.getPackageName()).apply();
        });
        viewHolder.btn_open.setOnClickListener((v)->{
            System.out.println(123);
        });
        viewHolder.btn_info.setOnClickListener((v)->{
            System.out.println(123);
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
