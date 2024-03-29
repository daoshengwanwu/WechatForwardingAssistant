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
        TASK_LOAD_IMPORT_VIEW_RESOURCE_ID,
        TASK_FORWARDING,
        TASK_LOAD_LABEL_USERS,
        TASK_CLEAN,
        TASK_YES,
        TASK_SELECT_RECEIVER,
        TASK_LOAD_PAGE_FEATURE,
        TASK_LOAD_USERS_BY_REG;
    }
}
