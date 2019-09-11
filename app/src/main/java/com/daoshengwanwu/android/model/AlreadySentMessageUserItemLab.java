package com.daoshengwanwu.android.model;


import com.daoshengwanwu.android.model.item.UserItem;

import java.util.HashSet;
import java.util.Set;


public class AlreadySentMessageUserItemLab {
    private static AlreadySentMessageUserItemLab sInstance = new AlreadySentMessageUserItemLab();


    public static AlreadySentMessageUserItemLab getInstance() {
        return sInstance;
    }


    private Set<UserItem> mAlreadSentSet = new HashSet<>();


    public void add(UserItem userItem) {
        mAlreadSentSet.add(userItem);
    }

    public void clear() {
        mAlreadSentSet.clear();
    }

    public boolean contains(UserItem item) {
        return mAlreadSentSet.contains(item);
    }
}
