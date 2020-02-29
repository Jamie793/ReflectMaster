package formatfa.reflectmaster;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.reflectmaster.Adapter.ApkAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ApkLoadAsync extends AsyncTask {
    private Context context;
    private ListView listView;
    private ArrayList<ApkInfo> apkInfos;

    public ApkLoadAsync(Context context, ListView listView) {
        this.context = context;
        this.listView = listView;
        this.apkInfos = new ArrayList<>();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected Object doInBackground(Object[] p1) {
        PackageManager packageManager = this.context.getPackageManager();
        boolean isSys = MainActivity.sharedPreferences.getBoolean("sysapp", false);
        // Return a List of all packages that are installed on the device.
        List<PackageInfo> packages = packageManager.getInstalledPackages(0);
        for (PackageInfo packageInfo : packages) {
            // 判断系统/非系统应用
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) // 非系统应用
            {
                this.apkInfos.add(new ApkInfo(
                        packageInfo.applicationInfo.loadLabel(packageManager).toString(),
                        packageInfo.packageName,
                        packageInfo.applicationInfo.loadIcon(packageManager)));
            } else {
                // 系统应用
                if (isSys) {
                    this.apkInfos.add(new ApkInfo(
                            packageInfo.applicationInfo.loadLabel(packageManager).toString(),
                            packageInfo.packageName,
                            packageInfo.applicationInfo.loadIcon(packageManager)));
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object result) {
        ApkAdapter apkAdapter = new ApkAdapter(this.context,this.apkInfos);
        this.listView.setAdapter(apkAdapter);
        super.onPostExecute(result);
    }


}
