package org.gg.library.plugins;

import android.os.Bundle;

public interface IPlugin {

    void attach(PublicActivity proxyActivity);

    void onCreate(Bundle saveInstanceState);

    void onResume();

    void onPause();

    void onDestroy();
}
