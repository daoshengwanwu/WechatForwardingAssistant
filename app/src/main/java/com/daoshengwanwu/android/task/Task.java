package com.daoshengwanwu.android.task;


import android.view.accessibility.AccessibilityNodeInfo;
import androidx.annotation.NonNull;


public abstract class Task {
    private final TaskId mTaskId;


    public abstract void execute(@NonNull AccessibilityNodeInfo rootInfo);

    protected Task(TaskId taskId) {
        mTaskId = taskId;
    }

    public TaskId getTaskId() {
        return mTaskId;
    }


    public enum TaskId {
        NONE,
        TASK_FORWARDING,
        TASK_LOAD_LABEL_INFO,
        TASK_CLEAN;
    }
}
