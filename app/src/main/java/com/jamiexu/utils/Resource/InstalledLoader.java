package com.jamiexu.utils.Resource;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;

import java.util.HashMap;

public class InstalledLoader extends ResourceLoader {
    private Context context;
    private HashMap<String, ResourceInfo> resourceInfoHashMap;

    public InstalledLoader(Context context, HashMap<String, ResourceInfo> resourceInfoHashMap) {
        this.context = context;
        this.resourceInfoHashMap = resourceInfoHashMap;
    }

    public PackageInfo getPackageInfo(String packageName) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = this.context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo;
    }

    public int getResourceID(String packageName, String name, String sourceType) {
        int id = -1;
        ResourceInfo resourceInfo = this.resourceInfoHashMap.get(packageName);
        if (resourceInfo == null)
            return id;
        try {
            Class<?> clas = resourceInfo.getClassLoader().loadClass(packageName + ".R$" + sourceType);
            id = (int) clas.getField(name).get(null);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return id;
    }

    public Resources getResource(String packageName) {
        ResourceInfo resourceInfo = this.resourceInfoHashMap.get(packageName);
        Resources resources = null;
        if (resourceInfo != null)
            resources = resourceInfo.getResources();

        if (resources == null) {
            try {
                Context context = this.context.createPackageContext(packageName, Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
                resources = context.getResources();
                this.resourceInfoHashMap.put(packageName, new ResourceInfo(resources, context.getClassLoader()));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return resources;
    }

}
