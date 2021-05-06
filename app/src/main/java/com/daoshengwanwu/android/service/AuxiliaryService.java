package com.daoshengwanwu.android.service;


import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.daoshengwanwu.android.R;
import com.daoshengwanwu.android.model.ShareData;
import com.daoshengwanwu.android.task.Task;
import com.daoshengwanwu.android.util.SingleSubThreadUtil;


public class AuxiliaryService extends AccessibilityService {
    private static final int WHAT_BREATH_INTERVAL = 58363;


    private AccessibilityEvent mLastEvent = null;
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

        mMainHandler.sendEmptyMessageDelayed(WHAT_BREATH_INTERVAL, 500);
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
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED &&
            event.getEventType() != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED &&
            event.getEventType() != AccessibilityEvent.TYPE_VIEW_CLICKED) {

            return;
        }

        mLastEvent = AccessibilityEvent.obtain(event);

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
