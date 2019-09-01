package org.gg.glibrary;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import org.gg.library.utils.EasyActivityResult;
import org.gg.library.widgets.StateLayout;
import org.gg.library.widgets.swipebacklayout.SwipeBackActivityBase;
import org.gg.library.widgets.swipebacklayout.SwipeBackActivityHelper;
import org.gg.library.widgets.swipebacklayout.SwipeBackLayout;
import org.gg.library.widgets.swipebacklayout.Utils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public abstract class BaseActivity extends AppCompatActivity implements SwipeBackActivityBase {

    protected SwipeBackActivityHelper mHelper;
    protected Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.NormalAppTheme);
        getWindow().getDecorView().setBackgroundResource(setDecorViewBackgroundResId());
        if (needTranslucent()) {
            QMUIStatusBarHelper.translucent(this);
        }
        if (needDarkStatusBar()) {
            QMUIStatusBarHelper.setStatusBarLightMode(this);
        }
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        if (needSwipeBack()) {
            mHelper = new SwipeBackActivityHelper(this);
            mHelper.onActivityCreate();
        }
        setContentView(setLayoutResId());

        if (hasToolbar()) {
            mToolbar = findViewById(R.id.toolbar);
            if (needTranslucent()) {
                fixTranslucentToolbar(mToolbar);
            }
            setSupportActionBar(mToolbar);
            mToolbar.setNavigationOnClickListener(v -> finish());
            if (toolbarHasCustomTitleOrNoTitle()) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
        }

        init();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (needSwipeBack()) {
            mHelper.onPostCreate();
            mHelper.setBackground(setDecorViewBackgroundResId());
        }
    }

    @Override
    public <T extends View> T findViewById(int id) {
        if (needSwipeBack()) {
            T t = super.findViewById(id);
            if (t == null && mHelper != null) {
                return (T) mHelper.findViewById(id);
            }
            return t;
        } else {
            return super.findViewById(id);
        }
    }

    @Override
    public SwipeBackLayout getSwipeBackLayout() {
        if (needSwipeBack()) {
            return mHelper.getSwipeBackLayout();
        } else {
            return null;
        }
    }

    @Override
    public void setSwipeBackEnable(boolean enable) {
        if (needSwipeBack()) {
            getSwipeBackLayout().setEnableGesture(enable);
        }
    }

    @Override
    public void scrollToFinishActivity() {
        if (needSwipeBack()) {
            Utils.convertActivityFromTranslucent(this);
            getSwipeBackLayout().scrollToFinishActivity();
        }
    }

//    protected void putMemberIdnSign(String id, String sign) {
//        SLApp.getSP().edit().putString("memberId", id).putString("memberSign", sign).apply();
//    }
//
//    protected String getMemberId() {
//        return SLApp.getSP().getString("memberId", null);
//    }
//
//    protected String getSign() {
//        return SLApp.getSP().getString("memberSign", null);
//    }

    /**
     * 处理透明状态栏主题的工具栏高度 (适用于各种ViewGroup)
     */
    protected <T extends ViewGroup> void fixTranslucentToolbar(T t) {
        T.MarginLayoutParams params = (T.MarginLayoutParams) t.getLayoutParams();
        params.topMargin = getStatusbarHeight();
        t.setLayoutParams(params);
    }

    protected void showContent(StateLayout stateLayout) {
//        if (stateLayout.getCurrentState() != 8605) {
        stateLayout.changeState(StateLayout.STATE_CONTENT);
//        }
    }

    protected void showLoading(StateLayout stateLayout) {
//        if (stateLayout.getCurrentState() != 8602) {
        stateLayout.changeState(StateLayout.STATE_LOADING);
//        }
    }

    protected void showError(StateLayout stateLayout) {
//        if (stateLayout.getCurrentState() != 8604) {
        stateLayout.changeState(StateLayout.STATE_ERROR);
//        }
    }

    protected void showEmpty(StateLayout stateLayout) {
//        if (stateLayout.getCurrentState() != 8603) {
        stateLayout.changeState(StateLayout.STATE_EMPTY_CONTENT);
//        }
    }

    protected int setDecorViewBackgroundResId() {
        return R.drawable.common_placeholder;
    }

    protected int dp2px(int dp) {
        return QMUIDisplayHelper.dp2px(this, dp);
    }

    protected int getScreenHeight() {
        return QMUIDisplayHelper.getScreenHeight(this);
    }

    protected int getStatusbarHeight() {
        return QMUIStatusBarHelper.getStatusbarHeight(this);
    }

    private Toast mToast;

    protected void toast(String message) {
        if (mToast == null) {
            mToast = Toast.makeText(this.getApplicationContext(), message, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(message);
        }
        mToast.show();
    }


    /**
     * 是否需要黑色字体的状态栏
     * 默认返回false 如果需要, 则重写该方法返回true
     */
    protected boolean needDarkStatusBar() {
        return false;
    }

    /**
     * 是否需要透明状态栏
     * 默认返回true 如果不需要, 则重写该方法返回false
     */
    protected boolean needTranslucent() {
        return true;
    }

    protected boolean toolbarHasCustomTitleOrNoTitle() {
        return true;
    }

    /**
     * 是否需要侧滑返回
     * 默认返回true 如果不需要, 则重写该方法返回false
     */
    protected boolean needSwipeBack() {
        return true;
    }

    /**
     * 是否有toolbar
     */
    protected abstract boolean hasToolbar();

    protected abstract int setLayoutResId();

    protected abstract void init();

    @Override // EasyAndroid中转
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 进行返回信息过滤派发
        EasyActivityResult.dispatch(this, requestCode, resultCode, data);
    }

    private QMUITipDialog mDialog;

    public void showLoadingDialog() {
        if (mDialog == null) {
            QMUITipDialog.Builder builder = new QMUITipDialog.Builder(this)
                    .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING);
            mDialog = builder.create(false);
        }
        if (!mDialog.isShowing()) {
            mDialog.show();
        }
    }

    public void dismissLoadingDialog() {
        if (mDialog != null && mDialog.isShowing() && (!isFinishing())) {
            mDialog.dismiss();
        }
    }

    protected void putSP(String key, String value) {
        SPUtils.putSP(this, key, value);
    }

    protected String getSP(String key) {
        return SPUtils.getSP(this, key);
    }
}
