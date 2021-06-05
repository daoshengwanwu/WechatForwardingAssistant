package com.daoshengwanwu.android.task;


import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;

import com.daoshengwanwu.android.FloatWindowManager;


public class LoadPageFeatureTask extends Task {
    private final FloatWindowManager mFloatWindowManager = FloatWindowManager.getInstance();


    public LoadPageFeatureTask() {
        super(TaskId.TASK_LOAD_PAGE_FEATURE);
    }

    @Override
    public void execute(@NonNull AccessibilityNodeInfo rootInfo) {

    }

    
    public interface OnLoadPageFeatureFinishedListener {
        void onLoadPageFeatureFinished();
    }
}
