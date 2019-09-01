package org.gg.glibrary;

import android.content.Context;
import android.content.SharedPreferences;

public class SPUtils {

    public static void putSP(Context context, String key, String value) {
        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences(key, Context.MODE_PRIVATE);
        preferences.edit().putString(key, value).apply();
    }

    public static String getSP(Context context, String key) {
        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences(key, Context.MODE_PRIVATE);
        return preferences.getString(key, "");
    }
}
