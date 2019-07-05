package org.gg.library.utils.zxing

import android.graphics.ImageFormat
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer


/**
 * @author hglf [hglf](https://github.com/hotstu)
 * @desc
 * @since 6/10/19
 */
class QRcodeAnalyzer(private val callback: ResultCallback) : ImageAnalysis.Analyzer {

    private val reader: MultiFormatReader = MultiFormatReader().apply {
        val map = mapOf<DecodeHintType, Collection<BarcodeFormat>>(
            Pair(DecodeHintType.POSSIBLE_FORMATS, arrayListOf(BarcodeFormat.QR_CODE))
        )
        setHints(map)
    }

    private var analyze = true

    override fun analyze(image: ImageProxy, rotationDegrees: Int) {
//        Log.e("QRcodeAnalyzer", Thread.currentThread().name)
        if (analyze) {
            //YUV_420 is normally the input type here, but other YUV types are also supported in theory
            if (ImageFormat.YUV_420_888 != image.format
                && ImageFormat.YUV_422_888 != image.format
                && ImageFormat.YUV_444_888 != image.format) {
                Log.e("BarcodeAnalyzer", "expect YUV, now = ${image.format}")
                return
            }
            val buffer = image.planes[0].buffer
            val data = ByteArray(buffer.remaining())
            val height = image.height
            val width = image.width
            buffer.get(data)
            //TODO 调整crop的矩形区域，目前是全屏（全屏有更好的识别体验，但是在部分手机上可能OOM）
            val source = PlanarYUVLuminanceSource(data, width, height, 0, 0, width, height, false)
            val bitmap = BinaryBitmap(HybridBinarizer(source))
            try {
                val result = reader.decode(bitmap)
                callback.onResult(result.text)
                analyze = false
            } catch (e: NotFoundException) {
                // ignore
            }
        }
    }

    interface ResultCallback {
        fun onResult(result: String)
    }
}
