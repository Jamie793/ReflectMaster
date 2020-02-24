package formatfa.reflectmaster;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Environment;

import com.androlua.LuaApplication;
import com.androlua.LuaDexClassLoader;
import com.androlua.LuaResources;
import com.androlua.LuaUtil;
import com.luajava.LuaException;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import dalvik.system.DexClassLoader;

public class LuaDexLoaders {
    private static HashMap<String, LuaDexClassLoader> dexCache = new HashMap<String, LuaDexClassLoader>();
    private ArrayList<ClassLoader> dexList = new ArrayList<ClassLoader>();
    private HashMap<String, String> libCache = new HashMap<String, String>();

    private Context mContext;

    private String luaDir;

    private AssetManager mAssetManager;

    private LuaResources mResources;
    private Resources.Theme mTheme;
    private String odexDir;

    public LuaDexLoaders(Context context) {
        mContext = context;
        luaDir = Environment.getExternalStorageDirectory().toString() + "/ReflectMaster/dex";//context.getLuaDir();
//            LuaApplication app = LuaApplication.getInstance();
//            localDir = app.getLocalDir();
        odexDir = context.getApplicationInfo().dataDir;
    }

    public Resources.Theme getTheme() {
        return mTheme;
    }

    public ArrayList<ClassLoader> getClassLoaders() {
        // TODO: Implement this method
        return dexList;
    }

    public LuaDexClassLoader loadApp(String pkg) {
        try {
            LuaDexClassLoader dex = dexCache.get(pkg);
            if (dex == null) {
                PackageManager manager = mContext.getPackageManager();
                ApplicationInfo info = manager.getPackageInfo(pkg, 0).applicationInfo;
                dex = new LuaDexClassLoader(info.publicSourceDir, LuaApplication.getInstance().getOdexDir(), info.nativeLibraryDir, mContext.getClassLoader());
                dexCache.put(pkg, dex);
            }
            if (!dexList.contains(dex)) {
                dexList.add(dex);
            }
            return dex;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void loadLibs() throws LuaException {
        File[] libs = new File(luaDir+"/libs").listFiles();
        if (libs == null)
            return;
        for (File f : libs) {
            if (f.isDirectory())
                continue;
            if (f.getAbsolutePath().endsWith(".so"))
                loadLib(f.getName());
            else
                loadDex(f.getAbsolutePath());
        }
    }

    public void loadLib(String name) throws LuaException {
        String fn = name;
        int i = name.indexOf(".");
        if (i > 0)
            fn = name.substring(0, i);
        if (fn.startsWith("lib"))
            fn = fn.substring(3);
        String libDir = mContext.getDir(fn, Context.MODE_PRIVATE).getAbsolutePath();
        String libPath = libDir + "/lib" + fn + ".so";
        File f = new File(libPath);
        if (!f.exists()) {
            f = new File(luaDir + "/libs/lib" + fn + ".so");
            if (!f.exists())
                throw new LuaException("can not find lib " + name);
            LuaUtil.copyFile(luaDir + "/libs/lib" + fn + ".so", libPath);

        }
        libCache.put(fn, libPath);
    }

    public HashMap<String, String> getLibrarys() {
        return libCache;
    }


    public DexClassLoader loadDex(String path) throws LuaException {
        LuaDexClassLoader dex = dexCache.get(path);
        if (dex == null)
            dex = loadApp(path);
        if (dex == null) {
            String name = path;


            if(name.indexOf("/")!=-1){
                if(new File(name).exists()){
                    String id = LuaUtil.getFileMD5(path);
                    if (id != null && id.equals("0"))
                        id = name;
                    dex = dexCache.get(id);

                    if (dex == null) {
                        dex = new LuaDexClassLoader(path, odexDir, mContext.getApplicationInfo().nativeLibraryDir, mContext.getClassLoader());
                        dexCache.put(id, dex);
                    }
                }
            }else {


                if (path.charAt(0) != '/')
                    path = luaDir + "/" + path;


                if (!new File(path).exists())
                    if (new File(path + ".dex").exists())
                        path += ".dex";
                    else if (new File(path + ".jar").exists())
                        path += ".jar";
                    else
                        throw new LuaException(path + " not found");
                String id = LuaUtil.getFileMD5(path);
                if (id != null && id.equals("0"))
                    id = name;
                dex = dexCache.get(id);

                if (dex == null) {
                    dex = new LuaDexClassLoader(path, odexDir, mContext.getApplicationInfo().nativeLibraryDir, mContext.getClassLoader());
                    dexCache.put(id, dex);
                }
            }
        }

        if (!dexList.contains(dex)) {
            dexList.add(dex);
            path = dex.getDexPath();
            if (path.endsWith(".jar"))
                loadResources(path);
        }
        return dex;
    }

    public void loadResources(String path) {
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
            int ok = (int) addAssetPath.invoke(assetManager, path);
            if (ok == 0)
                return;
            mAssetManager = assetManager;
            Resources superRes = mContext.getResources();
            mResources = new LuaResources(mAssetManager, superRes.getDisplayMetrics(),
                    superRes.getConfiguration());
            mResources.setSuperResources(superRes);
            mTheme = mResources.newTheme();
            mTheme.setTo(mContext.getTheme());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public AssetManager getAssets() {
        return mAssetManager;
    }

    public Resources getResources() {
        return mResources;
    }


}