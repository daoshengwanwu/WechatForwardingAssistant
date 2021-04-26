package com.daoshengwanwu.android.model;


import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.UUID;


public class UIForwardingTask {
    private List<UserGroup> mUserGroupList;
    private UserGroup mMergeUserGroup;
    private ForwardingContent mForwardingContent;
    private String mTaskName;
    private UUID mUUID = UUID.randomUUID();


    public List<UserGroup> getUserGroupList() {
        return mUserGroupList;
    }

    public UserGroup getMergeUserGroup() {
        return mMergeUserGroup;
    }

    public void setUserGroupList(@NonNull final Context context, @NonNull final List<UserGroup> userGroupList) {
        if (userGroupList == null || context == null) {
            return;
        }

        String mergeGroupName = "";
        for (UserGroup userGroup : userGroupList) {
            mergeGroupName += userGroup.getGroupName() + ",";
        }

        if (mergeGroupName.length() > 0) {
            mergeGroupName = mergeGroupName.substring(0, mergeGroupName.length() - 1);
        }

        if (TextUtils.isEmpty(mergeGroupName)) {
            mergeGroupName = "empty merge group";
        }

        mUserGroupList = userGroupList;
        mMergeUserGroup = new UserGroup(mergeGroupName);
        for (UserGroup userGroup : userGroupList) {
            mMergeUserGroup.mergeUserItems(userGroup);
        }

        UserGroupLab.getInstance().updateGroup(context, mMergeUserGroup);
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

    public UIForwardingTask(Context context, List<UserGroup> userGroupList, ForwardingContent forwardingContent, String taskName) {
        mForwardingContent = forwardingContent;
        mTaskName = taskName;

        setUserGroupList(context, userGroupList);
    }

    public UUID getId() {
        return mUUID;
    }
}
