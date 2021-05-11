package com.daoshengwanwu.android.activity;


import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.daoshengwanwu.android.util.WindowUtils;

import java.util.concurrent.atomic.AtomicInteger;


/**
 * 主要是用于请求权限的Activity，只有1像素大小且全透明，存在于屏幕start|top
 * 请求悬浮框权限由于需要startForResult方式开启设置Activity所以单独写了一个静态方法
 * 方便非Activity类去请求权限
 */
public class TranslucentOnePixelActivity extends AppCompatActivity {
    private static final String TAG = "TranslucentOnePixelActi";
    private static final String EXTRA_ACTION = "extra_action";
    private static final String EXTRA_PERMISSIONS = "extra_permissions";
    private static final String EXTRA_REQUEST_CODE = "extra_request_code";

    private static final AtomicInteger sRequestCode = new AtomicInteger(0);
    private static final SparseArray<OnRequestPermissionResultListener> sOnRequestPermissionResultListeners = new SparseArray<>();
    private static final SparseArray<OnOverlayPermissionResultListener> sOnOverlayPermissionResultListeners = new SparseArray<>();
    private static final SparseArray<OnScreenCapturePermissionResultListener> sOnScreenCapturePermissionResultListeners = new SparseArray<>();


    public static void launchForRequestPermission(
            @NonNull Context context,
            @NonNull OnRequestPermissionResultListener listener,
            @NonNull String[] permissions) {

        if (permissions.length <= 0) {
            return;
        }

        int requestCode = getNextRequestCode();

        synchronized (sOnRequestPermissionResultListeners) {
            sOnRequestPermissionResultListeners.put(requestCode, listener);
        }

        Intent intent = newRequestPermissionIntent(context, requestCode, permissions);
        try {
            context.startActivity(intent);
        } catch (Throwable e) {
            Log.d(TAG, "launchForRequestPermission: start activity failed: " + e);
        }
    }

    public static void launchForRequestOverlayPermission(
            @NonNull Context context,
            @NonNull OnOverlayPermissionResultListener listener) {

        int requestCode = getNextRequestCode();

        synchronized (sOnOverlayPermissionResultListeners) {
            sOnOverlayPermissionResultListeners.put(requestCode, listener);
        }

        Intent intent = newOverlayPermissionIntent(context, requestCode);
        try {
            context.startActivity(intent);
        } catch (Throwable e) {
            Log.d(TAG, "launchForRequestOverlayPermission: start activity failed: " + e);
        }
    }

    public static void launchForScreenCapturePermission(
            @NonNull Context context,
            @NonNull OnScreenCapturePermissionResultListener listener) {

        int requestCode = getNextRequestCode();

        synchronized (sOnScreenCapturePermissionResultListeners) {
            sOnScreenCapturePermissionResultListeners.put(requestCode, listener);
        }

        Intent intent = newScreenCapturePermissionIntent(context, requestCode);
        try {
            context.startActivity(intent);
        } catch (Throwable e) {
            Log.d(TAG, "launchForScreenCapturePermission: start activity failed: " + e);
        }
    }

    private static Intent newOverlayPermissionIntent(
            @NonNull Context context,
            int requestCode) {

        Intent intent = new Intent(context,TranslucentOnePixelActivity.class);

        intent.putExtra(EXTRA_ACTION, Action.ACTION_OVERLAY_PERMISSION);
        intent.putExtra(EXTRA_REQUEST_CODE, requestCode);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        return intent;
    }

    private static Intent newScreenCapturePermissionIntent(
            @NonNull Context context,
            int requestCode) {

        Intent intent = new Intent(context, TranslucentOnePixelActivity.class);

        intent.putExtra(EXTRA_ACTION, Action.ACTION_SCREEN_CAPTURE_PERMISSION);
        intent.putExtra(EXTRA_REQUEST_CODE, requestCode);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        return intent;
    }

    private static Intent newRequestPermissionIntent(
            @NonNull Context context,
            int requestCode,
            @NonNull String[] permissions) {

        Intent intent = new Intent(context, TranslucentOnePixelActivity.class);

        intent.putExtra(EXTRA_ACTION, Action.ACTION_REQUEST_PERMISSION);
        intent.putExtra(EXTRA_PERMISSIONS, permissions);
        intent.putExtra(EXTRA_REQUEST_CODE, requestCode);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        return intent;
    }

    private static int getNextRequestCode() {
        return sRequestCode.getAndIncrement();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        OnRequestPermissionResultListener listener;
        synchronized (sOnRequestPermissionResultListeners) {
            listener = sOnRequestPermissionResultListeners.get(requestCode);
            if (listener == null) {
                finish();
                return;
            }

            sOnRequestPermissionResultListeners.delete(requestCode);
        }

        listener.onRequestPermissionResult(permissions, grantResults);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeSelfOnePixelAndFullTranslucent();

        int action = getIntent().getIntExtra(EXTRA_ACTION, -1);
        if (action < 0) {
            finish();
            return;
        }

        switch (action) {
            case Action.ACTION_OVERLAY_PERMISSION: {
                executeOverlayPermission();
            } return;

            case Action.ACTION_REQUEST_PERMISSION: {
                executeRequestPermissions();
            } return;

            case Action.ACTION_SCREEN_CAPTURE_PERMISSION: {
                executeScreenCapturePermission();
            } return;

            default: finish();
        }
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        int action = getIntent().getIntExtra(EXTRA_ACTION, -1);
        if (action < 0) {
            finish();
            return;
        }

        switch (action) {
            case Action.ACTION_OVERLAY_PERMISSION: {
                handleOverlayPermission(requestCode);
            } return;

            case Action.ACTION_SCREEN_CAPTURE_PERMISSION: {
                handleScreenCapturePermission(requestCode, resultCode, data);
            } return;

            default: break;
        }

        finish();
    }

    private void handleOverlayPermission(final int requestCode) {
        OnOverlayPermissionResultListener listener;
        synchronized (sOnOverlayPermissionResultListeners) {
            listener = sOnOverlayPermissionResultListeners.get(requestCode);
            if (listener == null) {
                finish();
                return;
            }

            sOnOverlayPermissionResultListeners.delete(requestCode);
        }

        try {
            listener.onOverlayPermissionResult(WindowUtils.hasOverlayWindowPermission(this));
        } catch (Throwable e) {
            Log.e(TAG, "handleOverlayPermission failed", e);
        }
        finish();
    }

    private void handleScreenCapturePermission(int requestCode, int resultCode, @Nullable Intent data) {
        OnScreenCapturePermissionResultListener listener;
        synchronized (sOnScreenCapturePermissionResultListeners) {
            listener = sOnScreenCapturePermissionResultListeners.get(requestCode);
            if (listener == null) {
                finish();
                return;
            }

            sOnScreenCapturePermissionResultListeners.delete(requestCode);
        }

        try {
            listener.onScreenCapturePermissionResult(resultCode, data);
        } catch (Throwable e) {
            Log.e(TAG, "handleScreenCapturePermission failed", e);
        }

        finish();
    }

    private void makeSelfOnePixelAndFullTranslucent() {
        Window window = getWindow();

        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.START | Gravity.TOP;
        params.width = 1;
        params.height = 1;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        params.alpha = 0f;

        window.setAttributes(params);
    }

    private void executeRequestPermissions() {
        Intent intent = getIntent();

        int requestCode = intent.getIntExtra(EXTRA_REQUEST_CODE, -1);
        if (requestCode < 0) {
            finish();
            return;
        }

        String[] permissions = intent.getStringArrayExtra(EXTRA_PERMISSIONS);
        if (permissions == null ||
            permissions.length <= 0 ||
            Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {

            finish();
            return;
        }

        requestPermissions(permissions, requestCode);
    }

    private void executeScreenCapturePermission() {
        int requestCode = getIntent().getIntExtra(EXTRA_REQUEST_CODE, -1);
        if (requestCode < 0) {
            finish();
            return;
        }

        MediaProjectionManager mediaProjectionManager =
                (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);

        if (mediaProjectionManager == null) {
            finish();
            return;
        }

        Intent intent = mediaProjectionManager.createScreenCaptureIntent();
        try {
            startActivityForResult(intent, requestCode);
        } catch (Throwable e) {
            // ignore
        }
    }

    private void executeOverlayPermission() {
        int requestCode = getIntent().getIntExtra(EXTRA_REQUEST_CODE, -1);
        if (requestCode < 0 || Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            finish();
            return;
        }

        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + getPackageName()));

        try {
            startActivityForResult(intent, requestCode);
        } catch (Throwable e) {
            // ignore
        }
    }


    private static final class Action {
        private static final int ACTION_REQUEST_PERMISSION = 0;
        private static final int ACTION_OVERLAY_PERMISSION = 1;
        private static final int ACTION_SCREEN_CAPTURE_PERMISSION = 2;
    }

    public interface OnRequestPermissionResultListener {
        void onRequestPermissionResult(String[] permissions, int[] grantResults);
    }

    public interface OnOverlayPermissionResultListener {
        void onOverlayPermissionResult(boolean isGranted);
    }

    public interface OnScreenCapturePermissionResultListener {
        void onScreenCapturePermissionResult(int resultCode, @Nullable Intent data);
    }
}
