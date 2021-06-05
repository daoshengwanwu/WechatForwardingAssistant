package com.daoshengwanwu.android.task;


import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;

import com.daoshengwanwu.android.FloatWindowManager;


public class LoadPageFeatureTask extends Task {
    private final OnLoadPageFeatureFinishedListener mListener;
    private final FloatWindowManager mFloatWindowManager = FloatWindowManager.getInstance();


    public LoadPageFeatureTask(@NonNull final OnLoadPageFeatureFinishedListener listener) {
        super(TaskId.TASK_LOAD_PAGE_FEATURE);

        if (listener == null) {
            throw new RuntimeException("listener cannot be null");
        }

        mListener = listener;
    }

    @Override
    public void execute(@NonNull AccessibilityNodeInfo rootInfo) {

    }

    
    public interface OnLoadPageFeatureFinishedListener {
        void onLoadPageFeatureFinished();
    }
}
