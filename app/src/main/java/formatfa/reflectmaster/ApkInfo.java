package formatfa.reflectmaster;

import android.graphics.drawable.Drawable;

public class ApkInfo {

    private String title, packageName, installPath, dataPath;
    private Drawable icon;

    public ApkInfo(String title, String packageName, String installPath, String dataPath, Drawable icon) {
        this.title = title;
        this.packageName = packageName;
        this.installPath = installPath;
        this.dataPath = dataPath;
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getInstallPath() {
        return installPath;
    }

    public void setInstallPath(String installPath) {
        this.installPath = installPath;
    }

    public String getDataPath() {
        return dataPath;
    }

    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }
}
