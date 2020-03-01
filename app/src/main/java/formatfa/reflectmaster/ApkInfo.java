package formatfa.reflectmaster;

import android.graphics.drawable.Drawable;

public class ApkInfo {

    private String title, packageName;
    private Drawable icon;

    public ApkInfo(String title, String packageName, Drawable icon) {
        this.title = title;
        this.packageName = packageName;
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

}
