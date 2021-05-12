package com.daoshengwanwu.android.task;


import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.daoshengwanwu.android.util.PageUtils;

import java.util.List;


public class LoadPageFeatureTask extends Task {
    public LoadPageFeatureTask() {
        super(TaskId.TASK_LOAD_PAGE_FEATURE);
    }

    @Override
    public void execute(@NonNull AccessibilityNodeInfo rootInfo) {
        // 1. 首先确认有没有悬浮窗权限, 如果没有悬浮窗权限则跳转至开启悬浮窗权限界面
        // 2. 如果有悬浮窗权限则显示出悬浮窗
        // 3. 依次提示进入到不同界面进行特征捕获，直至所有界面捕获完成，本Task结束

        final List<String> curPageFeatures = PageUtils.gatherPageFeatures(rootInfo);
        Log.d("abcdefg", "execute: page feature: " + curPageFeatures + "\n\n\n\n");
    }

    
    public interface OnLoadPageFeatureFinishedListener {
        void onLoadPageFeatureFinished();
    }
}
