package org.gg.library.utils.glide;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

public class ProgressResponseBody extends ResponseBody {

    private BufferedSource mBufferedSource;
    private ResponseBody mResponseBody;
    private ProgressListener mListener;

    ProgressResponseBody(String url, ResponseBody body) {
        mResponseBody = body;
        mListener = ProgressInterceptor.LISTENER_MAP.get(url);
    }

    @Override
    public long contentLength() {
        return mResponseBody.contentLength();
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return mResponseBody.contentType();
    }

    @NotNull
    @Override
    public BufferedSource source() {
        if (mBufferedSource == null) {
            mBufferedSource = Okio.buffer(new ProgressSource(mResponseBody.source()));
        }
        return mBufferedSource;
    }

    private class ProgressSource extends ForwardingSource {

        long mTotalBytesRead = 0;
        int mCurrentProgress;

        ProgressSource(Source source) {
            super(source);
        }

        @Override
        public long read(@NotNull Buffer sink, long byteCount) throws IOException {
            long bytesRead = super.read(sink, byteCount);
            long fullLength = mResponseBody.contentLength();
            if (bytesRead == -1) {
                mTotalBytesRead = fullLength;
            } else {
                mTotalBytesRead += bytesRead;
            }
            int progress = (int) (100f * mTotalBytesRead / fullLength);
            if (mListener != null && progress != mCurrentProgress) {
                mListener.onProgress(progress);
            }
            if (mListener != null && mTotalBytesRead == fullLength) {
                mListener = null;
            }
            mCurrentProgress = progress;
            return bytesRead;
        }
    }
}
