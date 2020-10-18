package com.daoshengwanwu.android.model;


import android.content.Context;
import android.widget.Toast;

import com.daoshengwanwu.android.task.CleanTask;
import com.daoshengwanwu.android.task.ForwardingTask;
import com.daoshengwanwu.android.task.LoadLabelUsersTask;
import com.daoshengwanwu.android.task.SelectReceiverTask;
import com.daoshengwanwu.android.task.Task;
import com.daoshengwanwu.android.task.YesTask;
import com.daoshengwanwu.android.util.SingleSubThreadUtil;


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
            final Context context,
            UserGroup group,
            final String content,
            ForwardingTask.OnForwardingTaskFinishedListener listener) {

        mActiveTask = new ForwardingTask(context, group, content, listener);
    }

    public void activeSelectReceiverTask(final Context context, UserGroup group) {
        mActiveTask = new SelectReceiverTask(group, context);
        SingleSubThreadUtil.showToast(context, "激活选择收信人任务", Toast.LENGTH_LONG);
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
}
