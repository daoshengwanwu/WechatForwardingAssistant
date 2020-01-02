package com.daoshengwanwu.android.task;


import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;

import com.daoshengwanwu.android.page.ChatPage;
import com.daoshengwanwu.android.page.Page;


public class CleanTask extends Task {
    private boolean mIsMax = false;


    public CleanTask() {
        super(TaskId.TASK_CLEAN);
    }

    @Override
    public void execute(@NonNull AccessibilityNodeInfo rootInfo) {
        Page page = Page.generateFrom(rootInfo);
        if (page.getPageId() != Page.PageId.PAGE_CHAT) {
            return;
        }

        ChatPage chatPage = (ChatPage) page;
        if (!chatPage.isWithCheckBox()) {
            mIsMax = chatPage.isWithMaxCheckDialog();

            return;
        }

        if (!mIsMax) {
            chatPage.performAllCheck();
        }
    }
}
