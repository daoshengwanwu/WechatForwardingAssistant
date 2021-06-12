package com.daoshengwanwu.android.service;


import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.daoshengwanwu.android.R;
import com.daoshengwanwu.android.model.ShareData;
import com.daoshengwanwu.android.task.Task;
import com.daoshengwanwu.android.util.SingleSubThreadUtil;


public class AuxiliaryService extends AccessibilityService {
    private static final String TAG = "AuxiliaryService.TAG";
    private static final int WHAT_BREATH_INTERVAL = 58363;

    private static volatile AuxiliaryService sInstance = null;


    @Nullable
    public static AuxiliaryService getServiceInstance() {
        return sInstance;
    }

    @Nullable
    public static AccessibilityEvent getLastEvent() {
        final AuxiliaryService serviceInstance = getServiceInstance();
        if (serviceInstance == null){
            return null;
        }

        return serviceInstance.mLastEvent;
    }

    @Nullable
    public static AccessibilityEvent getLastViewClickEvent() {
        final AuxiliaryService serviceInstance = getServiceInstance();
        if (serviceInstance == null) {
            return null;
        }

        return serviceInstance.mLastViewClickEvent;
    }

    public static boolean isAccessibilitySettingsOn(@NonNull final Context context) {
        final String serviceName = context.getPackageName() + "/" + AuxiliaryService.class.getCanonicalName();
        Log.i(TAG, "service:" + serviceName);

        int accessibilityEnabled;
        try {
            accessibilityEnabled = Settings.Secure.getInt(context.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);

            Log.v(TAG, "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "Error finding setting, default accessibility to not found: " + e.getMessage());
            accessibilityEnabled = 0;
        }

        if (accessibilityEnabled == 1) {
            Log.v(TAG, "***ACCESSIBILITY IS ENABLED*** -----------------");

            TextUtils.SimpleStringSplitter stringColonSplitter = new TextUtils.SimpleStringSplitter(':');
            String settingValue = Settings.Secure.getString(context.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);

            if (settingValue != null) {
                stringColonSplitter.setString(settingValue);
                while (stringColonSplitter.hasNext()) {
                    String accessibilityService = stringColonSplitter.next();

                    Log.v(TAG, "-------------- > accessibilityService :: " + accessibilityService + " " + serviceName);
                    if (accessibilityService.equalsIgnoreCase(serviceName)) {
                        Log.v(TAG, "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }

        } else {
            Log.v(TAG, "***ACCESSIBILITY IS DISABLED***");
        }

        return false;
    }


    private volatile AccessibilityEvent mLastEvent = null;
    private volatile AccessibilityEvent mLastViewClickEvent = null;
    private final Handler mMainHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_BREATH_INTERVAL: {
                    final AccessibilityEvent lastEvent = mLastEvent;

                    if (lastEvent != null) {
                        onAccessibilityEvent(lastEvent);
                    }

                    mMainHandler.sendEmptyMessageDelayed(WHAT_BREATH_INTERVAL, 1000);
                } break;

                default: break;
            }
        }
    };


    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        SingleSubThreadUtil.showToast(this, "成功启动 " +
                getResources().getString(R.string.app_name) + ".", Toast.LENGTH_SHORT);

        // 暂时停止轮询
        // mMainHandler.sendEmptyMessageDelayed(WHAT_BREATH_INTERVAL, 500);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Toast.makeText(this, getResources().getString(R.string.app_name) +
                "已关闭，如需再次使用请在设置中重新开启本插件.", Toast.LENGTH_SHORT).show();

        mMainHandler.removeMessages(WHAT_BREATH_INTERVAL);

        return super.onUnbind(intent);
    }

    @Override
    public void onInterrupt() {
        //do nothing
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = this;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        sInstance = null;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED &&
            event.getEventType() != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED &&
            event.getEventType() != AccessibilityEvent.TYPE_VIEW_CLICKED) {

            return;
        }

        mLastEvent = AccessibilityEvent.obtain(event);
        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED) {
            mLastViewClickEvent = AccessibilityEvent.obtain(event);
        }

        final Task curActivatedTask = getCurActivatedTask();
        if (curActivatedTask == null) {
            return;
        }

        final AccessibilityNodeInfo rootInfo = getRootInActiveWindow();

        if (rootInfo == null) {
            return;
        }

        curActivatedTask.execute(rootInfo);
    }

    private Task getCurActivatedTask() {
        return ShareData.getInstance().getActiveTask();
    }
}
