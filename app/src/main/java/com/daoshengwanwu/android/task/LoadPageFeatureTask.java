package com.daoshengwanwu.android.task;


import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;

import com.daoshengwanwu.android.FloatWindowManager;
import com.daoshengwanwu.android.model.PageFeature;
import com.daoshengwanwu.android.util.PageUtils;


public class LoadPageFeatureTask extends Task {
    private final FloatWindowManager mFloatWindowManager = FloatWindowManager.getInstance();


    public LoadPageFeatureTask() {
        super(TaskId.TASK_LOAD_PAGE_FEATURE);
    }

    @Override
    public void execute(@NonNull AccessibilityNodeInfo rootInfo) {
        if (!mFloatWindowManager.isShown()) {
            mFloatWindowManager.show();
        }

        // 1. 首先确认有没有悬浮窗权限, 如果没有悬浮窗权限则跳转至开启悬浮窗权限界面
        // 2. 如果有悬浮窗权限则显示出悬浮窗
        // 3. 依次提示进入到不同界面进行特征捕获，直至所有界面捕获完成，本Task结束
        final PageFeature curPageFeatures = PageUtils.gatherPageFeatures(rootInfo);
        Log.d("abcdefg", "execute: page feature: " + curPageFeatures);
    }

    
    public interface OnLoadPageFeatureFinishedListener {
        void onLoadPageFeatureFinished();
    }
}
