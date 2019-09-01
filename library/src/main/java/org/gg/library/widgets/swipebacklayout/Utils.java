package org.gg.library.widgets.swipebacklayout;

import android.app.Activity;
import android.app.ActivityOptions;
import android.os.Build;
import android.text.TextUtils;

import java.lang.reflect.Method;

/**
 * Created by Chaojun Wang on 6/9/14.
 */
public class Utils {

    private Utils() { }

    /**
     * Convert a translucent themed Activity
     * {@link android.R.attr#windowIsTranslucent} to a fullscreen opaque
     * Activity.
     * <p>
     * Call this whenever the background of a translucent Activity has changed
     * to become opaque. Doing so will allow the {@link android.view.Surface} of
     * the Activity behind to be released.
     * <p>
     * This call has no effect on non-translucent activities or on activities
     * with the {@link android.R.attr#windowIsFloating} attribute.
     */
    public static void convertActivityFromTranslucent(Activity activity) {
//        OkReflect.on(activity).call("convertFromTranslucent");
        try {
            Method method = Activity.class.getDeclaredMethod("convertFromTranslucent");
            method.setAccessible(true);
            method.invoke(activity);
        } catch (Throwable ignored) { }
    }

    /**
     * Convert a translucent themed Activity
     * {@link android.R.attr#windowIsTranslucent} back from opaque to
     * translucent following a call to
     * {@link #convertActivityFromTranslucent(Activity)} .
     * <p>
     * Calling this allows the Activity behind this one to be seen again. Once
     * all such Activities have been redrawn
     * <p>
     * This call has no effect on non-translucent activities or on activities
     * with the {@link android.R.attr#windowIsFloating} attribute.
     */
    public static void convertActivityToTranslucent(Activity activity) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Method options = Activity.class.getDeclaredMethod("getActivityOptions");
                options.setAccessible(true);
                Method method = Activity.class.getDeclaredMethod("convertToTranslucent", getListener(), ActivityOptions.class);
                method.setAccessible(true);
                method.invoke(activity, null, options.invoke(activity));
            } else {
                Method method = Activity.class.getDeclaredMethod("convertToTranslucent", getListener());
                method.setAccessible(true);
                method.invoke(activity, new Object[]{null});
            }
        } catch (Throwable ignored) { }
    }

    private static Class<?> getListener() {
        Class<?>[] classes = Activity.class.getDeclaredClasses();
        for (Class clazz : classes) {
            if (TextUtils.equals(clazz.getSimpleName(), "TranslucentConversionListener")) {
                return clazz;
            }
        }
        return null;
    }
}
