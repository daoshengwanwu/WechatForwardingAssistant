package com.daoshengwanwu.android.model;


import android.content.Context;
import com.daoshengwanwu.android.task.ForwardingTask;
import com.daoshengwanwu.android.task.LoadLabelUsersTask;
import com.daoshengwanwu.android.task.Task;


public class ShareData {
    private static final ShareData sInstance = new ShareData();

    private boolean mIsForwardingPause = false;
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
