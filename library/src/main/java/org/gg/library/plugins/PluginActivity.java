package org.gg.library.plugins;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class PluginActivity extends AppCompatActivity implements IPlugin {

    private PublicActivity mProxyActivity;

    @Override
    public void attach(PublicActivity proxyActivity) {
        this.mProxyActivity = proxyActivity;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onCreate(Bundle saveInstanceState) { }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onResume() {
        mProxyActivity.onResume();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onPause() {
        mProxyActivity.onPause();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onDestroy() {
        mProxyActivity.onDestroy();
    }

    @Override
    public void setContentView(int layoutResID) {
        mProxyActivity.setContentView(layoutResID);
    }

    @Override
    public void setTheme(int resid) {
        mProxyActivity.setTheme(resid);
    }
}
