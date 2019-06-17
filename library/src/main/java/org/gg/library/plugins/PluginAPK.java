package org.gg.library.plugins;

import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;

import dalvik.system.DexClassLoader;

// 插件APK的实体对象
public class PluginAPK {

    private PackageInfo mPackageInfo;
    private Resources mResources;
    private AssetManager mAssetManager;
    private DexClassLoader mClassLoader;

    public PluginAPK(PackageInfo packageInfo,
                     Resources resources,
                     DexClassLoader classLoader) {

        mPackageInfo = packageInfo;
        mResources = resources;
        mAssetManager = resources.getAssets();
        mClassLoader = classLoader;
    }

    public PackageInfo getPackageInfo() {
        return mPackageInfo;
    }

    public void setPackageInfo(PackageInfo packageInfo) {
        mPackageInfo = packageInfo;
    }

    public Resources getResources() {
        return mResources;
    }

    public void setResources(Resources resources) {
        mResources = resources;
    }

    public AssetManager getAssetManager() {
        return mAssetManager;
    }

    public void setAssetManager(AssetManager assetManager) {
        mAssetManager = assetManager;
    }

    public DexClassLoader getClassLoader() {
        return mClassLoader;
    }

    public void setClassLoader(DexClassLoader classLoader) {
        mClassLoader = classLoader;
    }
}
