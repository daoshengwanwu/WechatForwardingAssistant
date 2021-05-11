package com.daoshengwanwu.android.task;


import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;


public class LoadPageFeatureTask extends Task {
    public LoadPageFeatureTask() {
        super(TaskId.TASK_LOAD_PAGE_FEATURE);
    }

    @Override
    public void execute(@NonNull AccessibilityNodeInfo rootInfo) {
        // 1. 首先确认有没有悬浮窗权限, 如果没有悬浮窗权限则跳转至开启悬浮窗权限界面
        // 2. 
    }

    
    public interface OnLoadPageFeatureFinishedListener {
        void onLoadPageFeatureFinished();
    }
}
