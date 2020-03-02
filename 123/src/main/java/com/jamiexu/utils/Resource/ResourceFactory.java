package com.jamiexu.utils.Resource;

import android.content.Context;

import java.util.HashMap;

public class ResourceFactory {
    private static final HashMap<String, ResourceInfo> resourceInfoHashMap = new HashMap<>();
    private static InstalledLoader installedLoader;
    private static UnInstallLoader unInstallLoader;


    public static ResourceLoader getInstance(Context context, int status) {
        ResourceLoader abstractResourceLoader = null;
        if (status == 0) {
            if (installedLoader == null)
                installedLoader = new InstalledLoader(context, resourceInfoHashMap);
            abstractResourceLoader = installedLoader;
        } else if (status == 1) {
            if (installedLoader == null)
                unInstallLoader = new UnInstallLoader(context, resourceInfoHashMap);
            abstractResourceLoader = unInstallLoader;
        }
        return abstractResourceLoader;
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





}
