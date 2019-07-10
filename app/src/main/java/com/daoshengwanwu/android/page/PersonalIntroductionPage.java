package com.daoshengwanwu.android.page;


import android.text.TextUtils;
import android.view.accessibility.AccessibilityNodeInfo;
import androidx.annotation.NonNull;
import com.daoshengwanwu.android.model.item.UserItem;
import com.daoshengwanwu.android.util.CustomCollectionUtils;

import java.util.List;
import java.util.Set;


public class PersonalIntroductionPage extends Page {
    private AccessibilityNodeInfo mBackInfo;
    private AccessibilityNodeInfo mLabelInfo;
    private AccessibilityNodeInfo mSendMessageInfo;
    private AccessibilityNodeInfo mTitleInfo;


    public static boolean isSelf(@NonNull AccessibilityNodeInfo rootInfo) {
        //title
        List<AccessibilityNodeInfo> rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/b7d");
        if (CustomCollectionUtils.isListEmpty(rst)) {
            return false;
        }

        //头像
        rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/b7c");
        if (CustomCollectionUtils.isListEmpty(rst)) {
            return false;
        }

        rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/b7l");
        if (CustomCollectionUtils.isListEmpty(rst)) {
            return false;
        }
        String wxidText = String.valueOf(rst.get(0).getText());
        if (TextUtils.isEmpty(wxidText) || !wxidText.startsWith("微信号:")) {
            return false;
        }

        return true;
    }

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

        rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/la");
        if (CustomCollectionUtils.isListEmpty(rst)) {
            throw new RuntimeException("無法找到PersonalIntroduction頁面的後退按鈕");
        }
        mBackInfo = rst.get(0);

        rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/dvd");
        if (!CustomCollectionUtils.isListEmpty(rst)) {
            mLabelInfo = rst.get(0);
        }

        rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/cv");
        if (CustomCollectionUtils.isListEmpty(rst)) {
            throw new RuntimeException("無法找到PersonalIntroduction頁面的發消息按鈕");
        }
        mSendMessageInfo = rst.get(0).getParent();

        rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/b7d");
        if (CustomCollectionUtils.isListEmpty(rst)) {
            throw new RuntimeException("PersonalIntroduction頁面title info 無法找到");
        }
        mTitleInfo = rst.get(0);
    }

    public String getTitle() {
        return mTitleInfo.getText() + "";
    }

    public String getLabelText() {
        return mLabelInfo == null ? "" : mLabelInfo.getText() + "";
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
            if (getTitle().equals(userItem.fullNickName)) {
                return true;
            }
        }

        return false;
    }
}
