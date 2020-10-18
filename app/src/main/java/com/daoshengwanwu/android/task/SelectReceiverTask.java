package com.daoshengwanwu.android.task;


import android.content.Context;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.daoshengwanwu.android.model.ShareData;
import com.daoshengwanwu.android.model.UserGroup;
import com.daoshengwanwu.android.page.Page;
import com.daoshengwanwu.android.page.SelectReceiverPage;
import com.daoshengwanwu.android.util.SingleSubThreadUtil;


public class SelectReceiverTask extends Task {
    private final Context mApplicationContext;
    private final UserGroup mToSelectGroup;


    public SelectReceiverTask(@NonNull UserGroup toSelectGroup, @NonNull Context context) {
        super(TaskId.TASK_SELECT_RECEIVER);

        mToSelectGroup = new UserGroup(toSelectGroup);
        mApplicationContext = context.getApplicationContext();
    }

    @Override
    public void execute(@NonNull AccessibilityNodeInfo rootInfo) {
        Page page = Page.generateFrom(rootInfo);
        if (page.getPageId() != Page.PageId.PAGE_SELECT_RECEIVER) {
            return;
        }

        if (mToSelectGroup.size() <= 0) {
            SingleSubThreadUtil.showToast(mApplicationContext, "已选择完成", Toast.LENGTH_LONG);
            ShareData.getInstance().clearData();
            return;
        }

        SelectReceiverPage selectReceiverPage = (SelectReceiverPage) page;
        selectReceiverPage.performSelectMatchUsers(mToSelectGroup);
        selectReceiverPage.performUpScroll(rootInfo);
    }
}
