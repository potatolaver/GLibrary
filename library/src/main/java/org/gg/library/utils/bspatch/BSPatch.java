package org.gg.library.utils.bspatch;

import java.io.File;
import java.io.IOException;

public class BSPatch {

    private static final String TAG = "BSPatch";

    static {
        System.loadLibrary("patch");
    }

    private static native void patchNative(String oldApk, String patch, String output);

    public static void patch(String oldApk, String newApk, String patch) {
        try {
            File output = new File(newApk);
            if (!output.exists()) {
                if (output.createNewFile()) {
                    patchNative(oldApk, patch, newApk);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 第一次安装到手机 安装包很可能被删除 所以第一次更新做一次全量更新 并且把apk文件保存到/files/apk目录
    // -> 判断/file/apk目录下是否有当前版本的apk, 如果有, 做增量更新  如果没有, 做全量更新

    // 1. 请求服务器检查更新 带版本参数 version_code=
    // 2. 服务器检查版本 如果服务器版本 > 参数版本 生成参数版本 - 服务器版本差分包的json
    // 3. 客户端获取响应json 请求json中差分包的url
    // 4. 使用DownloadManager下载差分包 下载目录为 cacheDir()/patch/*.patch
    // 5. 下载完成后, 获取差分包的路径 作为patch的参数
    // 6. 调用BSPatch.patch()方法 创建合成包文件 目录为cacheDir()/patch/*.apk 文件路径作为output参数
    // 7. 开启线程开始合成, 合成完成后通过回调将新apk文件返回
}
