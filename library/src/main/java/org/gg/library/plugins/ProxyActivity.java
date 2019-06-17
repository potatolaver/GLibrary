package org.gg.library.plugins;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.Nullable;

public class ProxyActivity extends PublicActivity {

    private PluginAPK mPluginAPK;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String className = getIntent().getStringExtra("className");
        mPluginAPK = PluginManager.getInstance().getPluginAPK();
        if (mPluginAPK != null) {
            try {
                Class<?> clazz = mPluginAPK.getClassLoader().loadClass(className);
                Object object = clazz.newInstance();
                if (object instanceof IPlugin) {
                    IPlugin plugin = (IPlugin) object;
                    plugin.attach(this);
                    plugin.onCreate(null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Resources getResources() {
        return mPluginAPK != null ? mPluginAPK.getResources() : super.getResources();
    }

    @Override
    public AssetManager getAssets() {
        return mPluginAPK != null ? mPluginAPK.getAssetManager() : super.getAssets();
    }

    @Override
    public ClassLoader getClassLoader() {
        return mPluginAPK != null ? mPluginAPK.getClassLoader() : super.getClassLoader();
    }
}
