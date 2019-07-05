package com.daoshengwanwu.android.model;


import com.daoshengwanwu.android.task.ForwardingTask;
import com.daoshengwanwu.android.task.Task;


public class ShareData {
    private static final ShareData sInstance = new ShareData();

    private String mLabel = "";
    private String mContent = "";
    private Task mActiveTask = null;

    private OnDataChangedListener mListener;


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

    public void activeForwarding(String label, String content) {
        mActiveTask = new ForwardingTask(null, null);
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
