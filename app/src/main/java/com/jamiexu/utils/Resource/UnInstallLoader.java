package com.jamiexu.utils.Resource;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import dalvik.system.DexClassLoader;

public class UnInstallLoader extends ResourceLoader {
    private Context context;
    private HashMap<String,ResourceInfo> resourceInfoHashMap;

    public UnInstallLoader(Context context, HashMap<String,ResourceInfo> resourceInfoHashMap) {
        this.context = context;
        this.resourceInfoHashMap = resourceInfoHashMap;
    }

    public PackageInfo getPackageInfo(String path) {
        PackageInfo packageInfo = this.context.getPackageManager().getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
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


    public Resources loadResource(String path) {
        String packageName = getPackageInfo(path).packageName;
        ResourceInfo resourceInfo = this.resourceInfoHashMap.get(packageName);
        Resources resources = null;
        if (resourceInfo != null)
            resources = resourceInfo.getResources();

        if (resources == null) {
            try {
                AssetManager assetManager = AssetManager.class.newInstance();
                Class<?> clas = assetManager.getClass();
                Method method = clas.getDeclaredMethod("addAssetPath", String.class);
                method.setAccessible(true);
                method.invoke(assetManager, path);
                resources = new Resources(assetManager, this.context.getResources().getDisplayMetrics(), this.context.getResources().getConfiguration());
                DexClassLoader dexClassLoader = new DexClassLoader(path, this.context.getDir("odex", 0).toString(), this.context.getApplicationInfo().nativeLibraryDir, this.context.getClass().getClassLoader());
                this.resourceInfoHashMap.put(packageName, new ResourceInfo(resources, dexClassLoader));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return resources;
    }
}
