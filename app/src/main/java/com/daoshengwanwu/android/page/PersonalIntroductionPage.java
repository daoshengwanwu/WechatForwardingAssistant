package com.daoshengwanwu.android.page;


import android.view.accessibility.AccessibilityNodeInfo;
import com.daoshengwanwu.android.model.item.UserItem;
import com.daoshengwanwu.android.util.CustomCollectionUtils;

import java.util.List;
import java.util.Set;


public class PersonalIntroductionPage extends Page {
    private AccessibilityNodeInfo mBackInfo;
    private AccessibilityNodeInfo mLabelInfo;
    private AccessibilityNodeInfo mSendMessageInfo;


    public static PersonalIntroductionPage generateFrom(AccessibilityNodeInfo rootInfo) {
        PersonalIntroductionPage personalIntroductionPage = new PersonalIntroductionPage();

        personalIntroductionPage.bindData(rootInfo);

        return personalIntroductionPage;
    }

    private PersonalIntroductionPage() {
        super(PageId.PAGE_PERSONAL_INTRODUCTION);
    }

    @Override
    public void bindData(AccessibilityNodeInfo rootInfo) {
        List<AccessibilityNodeInfo> rst;

        rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/kw");
        if (CustomCollectionUtils.isListEmpty(rst)) {
            throw new RuntimeException("無法找到PersonalIntroduction頁面的後退按鈕");
        }
        mBackInfo = rst.get(0);

        rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/dq7");
        if (CustomCollectionUtils.isListEmpty(rst)) {
            throw new RuntimeException("無法找到PersonalIntroduction頁面的Label按鈕");
        }
        mLabelInfo = rst.get(0);

        rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/ct");
        if (CustomCollectionUtils.isListEmpty(rst)) {
            throw new RuntimeException("無法找到PersonalIntroduction頁面的發消息按鈕");
        }
        mSendMessageInfo = rst.get(0).getParent();
    }

    public String getLabelText() {
        return mLabelInfo.getText() + "";
    }

    public void performBack() {
        mBackInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
    }

    public boolean performClickSendMessageInfo() {
        return mSendMessageInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
    }

    public boolean isLabelTextInToForwardingSet(Set<UserItem> toForwardingSet) {
        if (toForwardingSet == null) {
            return false;
        }

        for (UserItem userItem : toForwardingSet) {
            if (getLabelText().equals(userItem.fullNickName)) {
                return true;
            }
        }

        return false;
    }
}
