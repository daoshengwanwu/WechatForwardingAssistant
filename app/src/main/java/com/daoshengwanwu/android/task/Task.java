package com.daoshengwanwu.android.task;


public abstract class Task {
    private TaskId mTaskId;


    public abstract void execute();

    protected Task(TaskId taskId) {
        mTaskId = taskId;
    }

    public enum TaskId {
        NONE,
        TASK_FORWARDING,
        TASK_LOAD_LABEL_INFO,
        TASK_CLEAN;
    }
}
