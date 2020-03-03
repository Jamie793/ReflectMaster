package com.jamiexu.utils.Resource;

import android.content.res.Resources;

public class ResourceInfo {
    private Resources resources;
    private ClassLoader classLoader;

    public ResourceInfo(Resources resources, ClassLoader classLoader) {
        this.resources = resources;
        this.classLoader = classLoader;
    }

    public Resources getResources() {
        return resources;
    }

    public void setResources(Resources resources) {
        this.resources = resources;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
}
