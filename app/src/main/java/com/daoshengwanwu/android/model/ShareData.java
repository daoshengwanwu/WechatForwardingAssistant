package com.daoshengwanwu.android.model;


import com.daoshengwanwu.android.task.Task;


public class ShareData {
    private static final ShareData sInstance = new ShareData();

    private String mLabel = "";
    private String mContent = "";
    private int mActiveTask = Task.NONE;

    private OnDataChangedListener mListener;


    private ShareData() {

    }

    public static ShareData getInstance() {
        return sInstance;
    }

    public void clearData() {
        mLabel = "";
        mContent = "";
        mActiveTask = Task.NONE;

        if (mListener != null) {
            mListener.onDataChanged();
        }
    }

    public boolean isActiveForwarding() {
        return mActiveTask == Task.TASK_FORWARDING;
    }

    public String getLabel() {
        return mLabel;
    }

    public String getContent() {
        return mContent;
    }

    public int getActiveTask() {
        return mActiveTask;
    }

    public void activeForwarding(String label, String content) {
        mActiveTask = Task.TASK_FORWARDING;
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
