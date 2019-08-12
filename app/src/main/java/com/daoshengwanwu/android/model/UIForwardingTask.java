package com.daoshengwanwu.android.model;


import java.util.UUID;


public class UIForwardingTask {
    private UserGroup mUserGroup;
    private ForwardingContent mForwardingContent;
    private String mTaskName;
    private UUID mUUID = UUID.randomUUID();


    public UserGroup getUserGroup() {
        return mUserGroup;
    }

    public void setUserGroup(UserGroup userGroup) {
        mUserGroup = userGroup;
    }

    public ForwardingContent getForwardingContent() {
        return mForwardingContent;
    }

    public void setForwardingContent(ForwardingContent forwardingContent) {
        mForwardingContent = forwardingContent;
    }

    public String getTaskName() {
        return mTaskName;
    }

    public void setTaskName(String taskName) {
        mTaskName = taskName;
    }

    public UIForwardingTask(UserGroup userGroup, ForwardingContent forwardingContent, String taskName) {
        mUserGroup = userGroup;
        mForwardingContent = forwardingContent;
        mTaskName = taskName;
    }

    public UUID getId() {
        return mUUID;
    }
}
