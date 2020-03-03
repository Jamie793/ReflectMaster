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

}
