package com.daoshengwanwu.android;


import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Region;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.daoshengwanwu.android.activity.TranslucentOnePixelActivity;
import com.daoshengwanwu.android.model.ShareData;
import com.daoshengwanwu.android.util.SingleSubThreadUtil;
import com.daoshengwanwu.android.util.ViewTreeObserverUtils;
import com.daoshengwanwu.android.util.WindowUtils;


public class FloatWindowManager {
    private static volatile Context sApplicationContext = null;
    private static volatile FloatWindowManager sInstance = null;


    public static void init(@NonNull final Context context) {
        if (context == null || context.getApplicationContext() == null) {
            return;
        }

        if (sApplicationContext == null) {
            synchronized (FloatWindowManager.class) {
                if (sApplicationContext == null) {
                    sApplicationContext = context.getApplicationContext();
                }
            }
        }
    }

    public static FloatWindowManager getInstance() {
        if (sInstance == null) {
            synchronized (FloatWindowManager.class) {
                if (sInstance == null) {
                    sInstance = new FloatWindowManager(sApplicationContext);
                }
            }
        }

        return sInstance;
    }


    private final Context mApplicationContext;
    private final WindowManager mWindowManager;
    private final FrameLayout mFloatWindowRootView;
    private final RelativeLayout mFloatWindowContainer;
    private final TextView mOutputTextTV;
    private final TextView mInputButtonTV;
    private final TextView mNextButtonTV;

    private final ViewTreeObserverUtils.OnComputeInternalInsetsListener mComputeInsetsListener;


    public void show() {
        final boolean hasOverlayPermission = checkPermissionAndIfNotGrantedThenRequestIt(new Runnable() {
            @Override
            public void run() {
                show();
            }
        });

        if (!hasOverlayPermission) {
            return;
        }

        addViewFloatWindowManagerRoot();
    }

    public void setButtonOnClickListener(@Nullable final View.OnClickListener listener) {
        mInputButtonTV.setOnClickListener(listener);
    }

    public void setNextButtonOnClickListener(@Nullable final View.OnClickListener listener) {
        mNextButtonTV.setOnClickListener(listener);
    }

    public void setText(String text) {
        if (TextUtils.isEmpty(text)) {
            mOutputTextTV.setText("");
        } else {
            mOutputTextTV.setText(text);
        }
    }

    public void hide() {
        removeViewFloatWindowManagerRoot();
    }

    public boolean isShown() {
        return mFloatWindowRootView.getParent() != null;
    }

    private void addViewFloatWindowManagerRoot() {
        if (isShown()) {
            return;
        }

        final WindowManager.LayoutParams params = generateRootWindowLayoutParams();
        mWindowManager.addView(mFloatWindowRootView, params);

        final ViewTreeObserver observer = mFloatWindowRootView.getViewTreeObserver();
        if (!observer.isAlive()) {
            return;
        }

        ViewTreeObserverUtils.addOnComputeInternalInsetsListener(observer, mComputeInsetsListener);
    }

    private void removeViewFloatWindowManagerRoot() {
        if (!isShown()) {
            return;
        }

        final ViewTreeObserver observer = mFloatWindowRootView.getViewTreeObserver();
        if (observer.isAlive()) {
            ViewTreeObserverUtils.removeOnComputeInternalInsetsListener(observer, mComputeInsetsListener);
        }

        mWindowManager.removeView(mFloatWindowRootView);
    }

    @NonNull
    private WindowManager.LayoutParams generateRootWindowLayoutParams() {
        WindowManager.LayoutParams params = generateCommonWindowLayoutParams();

        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;

        return params;
    }

    @NonNull
    private WindowManager.LayoutParams generateCommonWindowLayoutParams() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        } else {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }

        params.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        params.format = PixelFormat.TRANSLUCENT;
        params.gravity = (Gravity.START | Gravity.TOP);

        return params;
    }

    private boolean checkPermissionAndIfNotGrantedThenRequestIt(@Nullable final Runnable runnable) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                WindowUtils.hasOverlayWindowPermission(mApplicationContext)) {

            return true;
        }

        TranslucentOnePixelActivity.launchForRequestOverlayPermission(mApplicationContext,
                new TranslucentOnePixelActivity.OnOverlayPermissionResultListener() {

            @Override
            public void onOverlayPermissionResult(boolean isGranted) {
                if (!isGranted) {
                    SingleSubThreadUtil.showToast(mApplicationContext, "请开启悬浮窗权限", Toast.LENGTH_LONG);
                } else if (runnable != null) {
                    runnable.run();
                }
            }
        });

        return false;
    }

    private FloatWindowManager(@NonNull final Context context) {
        if (context == null || context.getApplicationContext() == null) {
            throw new RuntimeException("FloatWindowManager: context == null || " +
                    "context.getApplicationContext() == null, please init " +
                    "FloatWindowManager first(by FloatWindowManager.init(Context)");

        }

        mApplicationContext = context.getApplicationContext();
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        final LayoutInflater inflater = LayoutInflater.from(mApplicationContext);
        mFloatWindowRootView = (FrameLayout) inflater.inflate(R.layout.layout_float_window_root, null);
        mFloatWindowContainer = mFloatWindowRootView.findViewById(R.id.rl_float_window_container);
        mOutputTextTV = mFloatWindowRootView.findViewById(R.id.tv_alert);
        mInputButtonTV = mFloatWindowRootView.findViewById(R.id.tv_btn);
        mNextButtonTV = mFloatWindowRootView.findViewById(R.id.tv_next);

        mComputeInsetsListener = new ViewTreeObserverUtils.OnComputeInternalInsetsListener() {
            @Override
            public void onComputeInternalInsets(ViewTreeObserverUtils.InternalInsetsInfo inoutInfo) {
                final int left = (int) mFloatWindowContainer.getX();
                final int top = (int) mFloatWindowContainer.getTop();
                final int right = left + mFloatWindowContainer.getWidth();
                final int bottom = top + mFloatWindowContainer.getHeight();

                final Region region = new Region(left, top, right, bottom);
                inoutInfo.updateRegion(region);
                inoutInfo.setTouchableInsets(ViewTreeObserverUtils.InternalInsetsInfo.TOUCHABLE_INSETS_REGION);
            }
        };
    }
}
