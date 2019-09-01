package org.gg.glibrary;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.QMUIProgressBar;

import org.gg.library.utils.glide.ProgressInterceptor;

import java.io.File;
import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import pl.droidsonroids.gif.GifDrawable;

public class CommonFragment extends Fragment {

    private static final String TAG = "CommonFragment";

    private QMUIProgressBar mProgressBar;
    private Handler mHandler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_compress, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ImageView imageView = view.findViewById(R.id.imageview);
        mProgressBar = view.findViewById(R.id.progressbar);

        mHandler = new Handler();

        String url = "http://wx1.sinaimg.cn/mw690/92e8647aly1g4y3l9igu3g206b0b8kjw.gif";

        File file = new File(Environment.getExternalStorageDirectory(), "gif.gif");

        int position = getArguments().getInt("position");

        if (position == 0) {
//            Glide.with(getContext())
//                    .asGif()
//                    .load(file)
//                    .into(imageView);

            ProgressInterceptor.addListener(url, progress -> mHandler.post(() -> mProgressBar.setProgress(progress)));
            Glide.with(getContext())
                    .asFile()
                    .load(url)
                    .fitCenter()
                    .listener(new RequestListener<File>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<File> target, boolean isFirstResource) {
                            ProgressInterceptor.removeListener(url);
                            mProgressBar.setVisibility(View.INVISIBLE);
                            mProgressBar.setProgress(0, false);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(File resource, Object model, Target<File> target, DataSource dataSource, boolean isFirstResource) {
                            ProgressInterceptor.removeListener(url);
                            mProgressBar.setVisibility(View.INVISIBLE);
                            mProgressBar.setProgress(0, false);
                            return false;
                        }
                    })
                    .into(new CustomTarget<File>() {
                        @Override
                        public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                            try {
                                GifDrawable drawable = new GifDrawable(resource);
                                int height = drawable.getIntrinsicHeight();
                                int width = drawable.getIntrinsicWidth();
                                int screenWidth = QMUIDisplayHelper.getScreenWidth(getContext());
                                ViewGroup.LayoutParams params = imageView.getLayoutParams();
                                params.width = screenWidth;
                                params.height = screenWidth * height / width;
                                imageView.setImageDrawable(drawable);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) { }
                    });
        }
    }
}
