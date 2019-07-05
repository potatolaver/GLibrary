package org.gg.library.utils.update;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.io.File;

public class UpdateManager {

    private final int PART = 0;
    private final int FULL = 1;

    private int mVersionCode = 0;

    // 程序启动 请求更新
    private void requestUpdate(Context context) {
        // 检查file/apk/文件夹是否存在
        File dir = context.getFilesDir();
        File file = new File(dir.getAbsolutePath(), "apk");
        if (!file.exists() || !file.isDirectory()) {
            if (file.mkdirs()) {
                getApkFile(context, file);
            }
        } else {
            getApkFile(context, file);
        }
    }

    private void getApkFile(Context context, File apkDir) {
        File[] files = apkDir.listFiles();
        File apkFile = null;
        if (files != null && files.length > 0) {
            for (File f : files) {
                if (f.getName().endsWith(".apk")) {
                    apkFile = f;
                    break;
                }
            }
            if (apkFile != null) { // apk 存在
                PackageManager pm = context.getPackageManager();
                PackageInfo info = pm.getPackageArchiveInfo(apkFile.getAbsolutePath(), 0);
                mVersionCode = info.versionCode;
                update(mVersionCode);
            } else { // apk 不存在
                update(context);
            }
        } else { // apk 不存在
            update(context);
        }
    }

    private void update(int versionCode) { // 请求服务器
        callUpdate(versionCode, PART);
    }

    private void update(Context context) {
        try {
            String packageName = context.getPackageName();
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            int versionCode = info.versionCode;
            callUpdate(versionCode, FULL);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void callUpdate(int versionCode, int type) {

    }
}
