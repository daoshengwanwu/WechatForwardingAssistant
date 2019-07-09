package com.daoshengwanwu.android.model;


import android.content.Context;
import com.daoshengwanwu.android.model.item.UserItem;
import com.daoshengwanwu.android.task.ForwardingTask;
import com.daoshengwanwu.android.task.LoadLabelUsersTask;
import com.daoshengwanwu.android.task.Task;

import java.util.Set;


public class ShareData {
    private static final ShareData sInstance = new ShareData();

    private String mLabel = "";
    private String mContent = "";
    private Task mActiveTask = null;

    private OnDataChangedListener mListener;

    private Set<UserItem> userItems = null;


    private ShareData() {

    }

    public static ShareData getInstance() {
        return sInstance;
    }

    public void clearData() {
        mLabel = "";
        mContent = "";
        mActiveTask = null;

        if (mListener != null) {
            mListener.onDataChanged();
        }
    }

    public boolean isActiveForwarding() {
        return mActiveTask instanceof ForwardingTask;
    }

    public String getLabel() {
        return mLabel;
    }

    public String getContent() {
        return mContent;
    }

    public Task getActiveTask() {
        return mActiveTask;
    }

    public void activeForwarding(final Context context, String label, final String content) {
        Task task = null;
        if (userItems == null) {
            task = new LoadLabelUsersTask(context, label, new LoadLabelUsersTask.OnLabelUsersInfoLoadFinishedListener() {
                @Override
                public void onLabelUsersInfoLoadFinished(Set<UserItem> labelUsersInfo) {
                    mActiveTask = new ForwardingTask(context, labelUsersInfo, content, new ForwardingTask.OnForwardingTaskFinishedListener() {
                        @Override
                        public void onForwardingTaskFinished() {
                            clearData();
                        }
                    });
                }
            });
        } else {
            task = new ForwardingTask(context, userItems, content, new ForwardingTask.OnForwardingTaskFinishedListener() {
                @Override
                public void onForwardingTaskFinished() {
                    clearData();
                }
            });
        }

        mActiveTask = task;
        mLabel = label;
        mContent = content;

        if (mListener != null) {
            mListener.onDataChanged();
        }
    }

    public void setDataChangedListener(OnDataChangedListener listener) {
        mListener = listener;
    }


    public interface OnDataChangedListener {
        void onDataChanged();
    }
}
