package com.daoshengwanwu.android.model;


import androidx.annotation.NonNull;
import com.daoshengwanwu.android.model.item.UserItem;

import java.util.*;


public class UserGroup {
    private final UUID mUUID;
    private String mGroupName;
    private final Set<UserItem> mUserItems = new HashSet<>();


    UserGroup(String groupName) {
        mUUID = UUID.randomUUID();
        mGroupName = groupName;
    }

    UserGroup(UserGroup group) {
        mUUID = UUID.fromString(group.mUUID.toString());
        mGroupName = group.mGroupName;
        mUserItems.addAll(group.getUserItems());
    }

    public void mergeUserItems(UserGroup group) {
        mUserItems.addAll(group.getUserItems());
    }

    public void mergeUserItems(Set<UserItem> userItems) {
        mUserItems.addAll(userItems);
    }

    public Set<UserItem> getUserItems() {
        return mUserItems;
    }

    public UUID getUUID() {
        return mUUID;
    }

    public String getGroupName() {
        return mGroupName;
    }

    public void setGroupName(String groupName) {
        mGroupName = groupName;
    }

    public UserItem getUserItem(String fullNickname, String labelText) {
        for (UserItem item : mUserItems) {
            if (item.fullNickName.equals(fullNickname) && item.labelText.equals(labelText)) {
                return item;
            }
        }

        return null;
    }

    public void removeUserItem(String fullNickname, String labelText) {
        mUserItems.remove(getUserItem(fullNickname, labelText));
    }

    public int size() {
        return mUserItems.size();
    }

    @NonNull
    @Override
    public String toString() {
        return mGroupName + mUserItems.toString();
    }
}
