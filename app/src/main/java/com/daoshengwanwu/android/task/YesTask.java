package com.daoshengwanwu.android.task;


import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;

import com.daoshengwanwu.android.page.FriendPage;
import com.daoshengwanwu.android.page.Page;


public class YesTask extends Task {
    private FriendPage mFriendPage;


    public YesTask() {
        super(TaskId.TASK_YES);
    }

    @Override
    public void execute(@NonNull AccessibilityNodeInfo rootInfo) {
        Page page = Page.generateFrom(rootInfo);
        if (page.getPageId() != Page.PageId.PAGE_FRIEND) {
            return;
        }

        if (mFriendPage == null) {
            mFriendPage = (FriendPage) page;
        }

        mFriendPage.performClickTDotIfNeed(rootInfo);
    }
}
