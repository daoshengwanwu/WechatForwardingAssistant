package com.daoshengwanwu.android.task;


import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;


public abstract class Task {
    private final TaskId mTaskId;


    public abstract void execute(@NonNull AccessibilityNodeInfo rootInfo);

    public Task(TaskId taskId) {
        mTaskId = taskId;
    }

    public TaskId getTaskId() {
        return mTaskId;
    }


    public enum TaskId {
        NONE,
        TASK_FORWARDING,
        TASK_LOAD_LABEL_USERS,
        TASK_CLEAN,
        TASK_YES,
        TASK_SELECT_RECEIVER;
    }
}
