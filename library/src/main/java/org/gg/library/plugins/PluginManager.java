package org.gg.library.plugins;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;

import java.io.File;

import dalvik.system.DexClassLoader;

public class PluginManager {

    private static final String TAG = "PluginManager";

    /*================单例================*/
    private PluginManager() { }

    public static PluginManager getInstance() { return Singleton.MANAGER; }

    private static class Singleton {
        private static final PluginManager MANAGER = new PluginManager();
    }
    /*====================================*/

    private Context mContext;
    private PluginAPK mPluginAPK;

    public void init(Context context) {
        this.mContext = context.getApplicationContext();
    }

    // 加载APK文件
    public void loadApk(String apkPath) {
        PackageInfo packageInfo = mContext.getPackageManager().getPackageArchiveInfo(
                apkPath, PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES);
        if (packageInfo != null) {
            DexClassLoader loader = createDexClassLoader(apkPath);
            AssetManager am = createAssetManager(apkPath);
            Resources resources = createResources(am);
            mPluginAPK = new PluginAPK(packageInfo, resources, loader);
        } else {
            Log.e(TAG, Thread.currentThread().getName() + " -> PackageInfo = null");
        }
    }

    // 创建DexClassLoader
    private DexClassLoader createDexClassLoader(String apkPath) {
        File file = mContext.getDir("dex", Context.MODE_PRIVATE);
        return new DexClassLoader(
                apkPath, file.getAbsolutePath(), null, mContext.getClassLoader());
    }

    // 创建AssetManager
    private AssetManager createAssetManager(String apkPath) {
        try {
            AssetManager am = AssetManager.class.newInstance();// 反射 PrivateApi
            AssetManager.class.getDeclaredMethod("addAssetPath", String.class).invoke(am, apkPath);
            return am;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 创建Resources加载资源文件
    private Resources createResources(AssetManager am) {
        Resources res = mContext.getResources();
        return new Resources(am, res.getDisplayMetrics(), res.getConfiguration());
    }

    public PluginAPK getPluginAPK() {
        return mPluginAPK;
    }
}
