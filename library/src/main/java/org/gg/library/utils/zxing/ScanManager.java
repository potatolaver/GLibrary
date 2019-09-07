package org.gg.library.utils.zxing;

import android.os.Handler;
import android.os.HandlerThread;
import android.view.TextureView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageAnalysisConfig;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;

public class ScanManager {

    private static volatile ScanManager instance = null;
    private PreviewConfig mPreViewConfig;
    private ImageAnalysisConfig mAnalysisConfig;
    private Preview mPreview;
    private ImageAnalysis mAnalysis;

    private ScanManager() { }

    public static ScanManager getInstance() {
        if (instance == null) {
            synchronized (ScanManager.class) {
                if (instance == null) {
                    instance = new ScanManager();
                }
            }
        }
        return instance;
    }

    public ScanManager bindToLifecycle(AppCompatActivity activity) {
        CameraX.bindToLifecycle(activity, mPreview, mAnalysis);
        return this;
    }

    public void scan(TextureView textureView, QRcodeAnalyzer.ResultCallback callback) {
        textureView.post(() -> {
            mPreViewConfig = new PreviewConfig.Builder()
                    .setTargetRotation(textureView.getDisplay().getRotation())
                    .setLensFacing(CameraX.LensFacing.BACK)
                    .build();
            HandlerThread thread = new HandlerThread("scan thread");
            thread.start();
            mAnalysisConfig = new ImageAnalysisConfig.Builder()
                    .setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)
                    .setCallbackHandler(new Handler(thread.getLooper()))
                    .setTargetRotation(textureView.getDisplay().getRotation())
                    .setLensFacing(CameraX.LensFacing.BACK)
                    .build();
            mPreview = AutoFitPreviewBuilder.Companion.build(mPreViewConfig, textureView);
            mAnalysis = new ImageAnalysis(mAnalysisConfig);
            mAnalysis.setAnalyzer(new QRcodeAnalyzer(callback));
        });
    }
}
