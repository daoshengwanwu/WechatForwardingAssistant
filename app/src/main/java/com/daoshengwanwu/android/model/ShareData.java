package com.daoshengwanwu.android.model;


import android.content.Context;
import com.daoshengwanwu.android.model.item.UserItem;
import com.daoshengwanwu.android.task.ForwardingTask;
import com.daoshengwanwu.android.task.LoadLabelUsersTask;
import com.daoshengwanwu.android.task.Task;

import java.util.Set;


public class ShareData {
    private static final ShareData sInstance = new ShareData();


    private Task mActiveTask = null;
    private OnDataChangedListener mListener;


    private ShareData() {

    }

    public static ShareData getInstance() {
        return sInstance;
    }

    public void clearData() {
        mActiveTask = null;

        if (mListener != null) {
            mListener.onDataChanged();
        }
    }

    public Task getActiveTask() {
        return mActiveTask;
    }

    public void activeForwardingTask(final Context context, Set<UserItem> userItems, final String content) {
        mActiveTask = new ForwardingTask(context, userItems, content, new ForwardingTask.OnForwardingTaskFinishedListener() {
                @Override
                public void onForwardingTaskFinished() {
                    clearData();
                }
            });
    }

    public void activeLoadToForwardingTask(final Context context, String labelTitle, LoadLabelUsersTask.OnLabelUsersInfoLoadFinishedListener listener) {
        mActiveTask = new LoadLabelUsersTask(context, labelTitle, listener);
    }

    public void setDataChangedListener(OnDataChangedListener listener) {
        mListener = listener;
    }


    public interface OnDataChangedListener {
        void onDataChanged();
    }
}
