package org.gg.glibrary;

import android.content.Context;
import android.content.pm.ApplicationInfo;

public class ChannelUtils {

    public static String getChannelName(Context context) {
        ApplicationInfo info = context.getApplicationContext().getApplicationInfo();
        return info.metaData.getString("ATMAN_CHANNEL");
    }
}
