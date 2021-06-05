package com.daoshengwanwu.android.model;


import android.content.Context;
import android.widget.Toast;

import com.daoshengwanwu.android.activity.ForwardingProcessActivity;
import com.daoshengwanwu.android.task.CleanTask;
import com.daoshengwanwu.android.task.ForwardingTask;
import com.daoshengwanwu.android.task.LoadLabelUsersTask;
import com.daoshengwanwu.android.task.LoadPageFeatureTask;
import com.daoshengwanwu.android.task.RegLoadUsersTask;
import com.daoshengwanwu.android.task.SelectReceiverTask;
import com.daoshengwanwu.android.task.Task;
import com.daoshengwanwu.android.task.YesTask;
import com.daoshengwanwu.android.util.SharedPreferencesUtils;
import com.daoshengwanwu.android.util.SingleSubThreadUtil;

import java.util.List;
import java.util.regex.Pattern;


public class ShareData {
    private static final ShareData sInstance = new ShareData();

    public boolean mIsForwardingPause = false;
    private Task mActiveTask = null;


    private ShareData() {

    }

    public static ShareData getInstance() {
        return sInstance;
    }

    public void clearData() {
        mActiveTask = null;
    }

    public Task getActiveTask() {
        if (mIsForwardingPause) {
            return null;
        }

        return mActiveTask;
    }

    public void activeForwardingTask(
            final ForwardingProcessActivity context,
            UserGroup group,
            List<Pattern> regPatterns,
            final String content,
            final int bundleSize,
            final int pauseTime,
            final int deltaTime,
            ForwardingTask.OnForwardingTaskFinishedListener listener) {

        mActiveTask = new ForwardingTask(context, group, regPatterns, bundleSize, pauseTime, deltaTime, content, listener);
    }

    public void activeSelectReceiverTask(final Context context, UserGroup group) {
        mActiveTask = new SelectReceiverTask(group, context);
        SingleSubThreadUtil.showToast(context, "激活选择收信人任务", Toast.LENGTH_LONG);
    }

    public void activeLoadPageFeautresTask(final Context context) {
        if (mActiveTask != null) {
            SingleSubThreadUtil.showToast(context, "不可同时开启两个任务", Toast.LENGTH_LONG);
            return;
        }

        mActiveTask = new LoadPageFeatureTask(new LoadPageFeatureTask.OnLoadPageFeatureFinishedListener() {
            @Override
            public void onLoadPageFeatureFinished() {
                mActiveTask = null;
                SharedPreferencesUtils.BOOLEAN_CACHE.WECHAT_ASSISTANT_INITED.put(true);
            }
        });

        SingleSubThreadUtil.showToast(context, "开始执行初始化微信助手任务", Toast.LENGTH_LONG);
    }

    public void activeYesTask() {
        mActiveTask = new YesTask();
    }

    public void stopYesTask() {
        if (mActiveTask != null && mActiveTask.getTaskId() == Task.TaskId.TASK_YES) {
            mActiveTask = null;
        }
    }

    public void activeCleanTask() {
        mActiveTask = new CleanTask();
    }

    public void stopCleanTask() {
        if (mActiveTask != null && mActiveTask.getTaskId() == Task.TaskId.TASK_CLEAN) {
            mActiveTask = null;
        }
    }

    public void stopForwardingTask() {
        mActiveTask = null;
    }

    public void pauseForwardingTask() {
        mIsForwardingPause = true;
    }

    public void resumeForwardingTask() {
        mIsForwardingPause = false;
    }

    public void activeLoadToForwardingTask(final Context context, String labelTitle, LoadLabelUsersTask.OnLabelUsersInfoLoadFinishedListener listener) {
        mActiveTask = new LoadLabelUsersTask(context, labelTitle, listener);
    }

    public void activeLoadForwardingUserWithRegTask(final Context context, Pattern pattern, RegLoadUsersTask.OnUsersInfoLoadFinishedListener listener) {
        mActiveTask = new RegLoadUsersTask(context, pattern, listener);
    }
}
