package com.daoshengwanwu.android.service;


import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;
import com.daoshengwanwu.android.R;
import com.daoshengwanwu.android.task.Task;
import com.daoshengwanwu.android.util.SingleSubThreadUtil;


public class AuxiliaryService extends AccessibilityService {
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        SingleSubThreadUtil.showToast(this, "成功启动 " +
                getResources().getString(R.string.app_name) + ".", Toast.LENGTH_SHORT);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Toast.makeText(this, "getResources().getString(R.string.app_name) +\n" +
                "                \" 已关闭，如需再次使用请在设置中重新开启本插件.\"", Toast.LENGTH_SHORT).show();

        return super.onUnbind(intent);
    }

    @Override
    public void onInterrupt() {
        //do nothing
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Task curActivatedTask = getCurActivatedTask();

        if (curActivatedTask == null) {
            return;
        }

        if (event.getEventType() != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED &&
            event.getEventType() != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED &&
            event.getEventType() != AccessibilityEvent.TYPE_VIEW_CLICKED) {

            return;
        }

        AccessibilityNodeInfo source = event.getSource();
        AccessibilityNodeInfo rootInfo = getRootInActiveWindow();

        if (source == null || rootInfo == null) {
            return;
        }

        curActivatedTask.execute(rootInfo);
    }

    private Task getCurActivatedTask() {
        // TODO::
        return null;
    }
}
