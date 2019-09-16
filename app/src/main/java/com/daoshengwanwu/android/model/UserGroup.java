package com.daoshengwanwu.android.model;


import androidx.annotation.NonNull;
import com.daoshengwanwu.android.model.item.UserItem;

import java.util.*;


public class UserGroup {
    private final UUID mUUID;
    private String mGroupName;
    private final Set<UserItem> mMergeSet = new HashSet<>();
    private final List<UserItem> mUserItemList = new ArrayList<>();


    UserGroup(String groupName) {
        mUUID = UUID.randomUUID();
        mGroupName = groupName;
    }

    UserGroup(UserGroup group) {
        mUUID = UUID.fromString(group.mUUID.toString());
        mGroupName = group.mGroupName;
        mUserItemList.addAll(group.getUserItems());
    }

    public void mergeUserItems(UserGroup group) {
        mMergeSet.clear();
        mMergeSet.addAll(mUserItemList);
        mMergeSet.addAll(group.mUserItemList);

        mUserItemList.clear();
        mUserItemList.addAll(mMergeSet);

        mMergeSet.clear();

        sortUserItems();
    }

    public void mergeUserItems(Set<UserItem> userItems) {
        mMergeSet.clear();
        mMergeSet.addAll(mUserItemList);
        mMergeSet.addAll(userItems);

        mUserItemList.clear();
        mUserItemList.addAll(mMergeSet);

        mMergeSet.clear();

        sortUserItems();
    }

    public List<UserItem> getUserItems() {
        return mUserItemList;
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
        for (UserItem item : mUserItemList) {
            if (item.fullNickName.equals(fullNickname) && item.labelText.equals(labelText)) {
                return item;
            }
        }

        return null;
    }

    public void removeUserItem(String fullNickname, String labelText) {
        mUserItemList.remove(getUserItem(fullNickname, labelText));
    }

    public int size() {
        return mUserItemList.size();
    }

    @NonNull
    @Override
    public String toString() {
        return mGroupName + mUserItemList.toString();
    }

    private void sortUserItems() {
        Collections.sort(mUserItemList);
    }
}
