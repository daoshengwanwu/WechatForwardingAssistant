package com.daoshengwanwu.android.model;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.daoshengwanwu.android.model.item.UserItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;


public class UserGroup {
    private final UUID mUUID;
    private String mGroupName;
    private final Set<UserItem> mMergeSet = new HashSet<>();
    private final List<UserItem> mUserItemList = new ArrayList<>();


    UserGroup(String groupName) {
        mUUID = UUID.randomUUID();
        mGroupName = groupName;
    }

    public UserGroup(UserGroup group) {
        mUUID = UUID.fromString(group.mUUID.toString());
        mGroupName = group.mGroupName;
        mUserItemList.addAll(group.getUserItems());
    }

    public boolean containsFullNickNameItem(@Nullable String fullNickName) {
        if (fullNickName == null) {
            return false;
        }

        for (UserItem item : mUserItemList) {
            if (fullNickName.equals(item.fullNickName)) {
                return true;
            }
        }

        return false;
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

    public void removeUserItem(String fullNickName)  {
        if (fullNickName == null) {
            return;
        }

        Iterator<UserItem> itemIterator = mUserItemList.iterator();
        while (itemIterator.hasNext()) {
            UserItem item = itemIterator.next();
            if (fullNickName.equals(item.fullNickName)) {
                itemIterator.remove();
                return;
            }
        }
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
