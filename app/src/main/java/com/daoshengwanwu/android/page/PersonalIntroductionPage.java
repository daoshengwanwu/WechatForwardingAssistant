package com.daoshengwanwu.android.page;


import android.text.TextUtils;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;

import com.daoshengwanwu.android.model.item.UserItem;
import com.daoshengwanwu.android.util.ActionPerformer;
import com.daoshengwanwu.android.util.CustomCollectionUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;


public class PersonalIntroductionPage extends Page {
    private AccessibilityNodeInfo mBackInfo;
    private AccessibilityNodeInfo mLabelInfo;
    private AccessibilityNodeInfo mSendMessageInfo;
    private AccessibilityNodeInfo mTitleInfo;


    public static boolean isSelf(@NonNull AccessibilityNodeInfo rootInfo) {
        //title,也就是显示备注或者昵称的View
        List<AccessibilityNodeInfo> rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/b28");
        if (CustomCollectionUtils.isListEmpty(rst)) {
            return false;
        }

        //头像ImageView
        rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/b1y");
        if (CustomCollectionUtils.isListEmpty(rst)) {
            return false;
        }

        //微信id TextView
        rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/b2f");
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
    public void bindData(@NotNull AccessibilityNodeInfo rootInfo) {
        List<AccessibilityNodeInfo> rst;

        //后退LinearLayout
        rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/dm");
        if (!CustomCollectionUtils.isListEmpty(rst)) {
            mBackInfo = rst.get(0);
        }

        // 标签TextView
        rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/b03");
        if (!CustomCollectionUtils.isListEmpty(rst)) {
            mLabelInfo = rst.get(0);
        }

        // 发送消息TextView
        rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/g6f");
        if (!CustomCollectionUtils.isListEmpty(rst)) {
            mSendMessageInfo = rst.get(0).getParent();
        }

        // title,也就是显示备注或者昵称的View
        rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/b28");
        if (!CustomCollectionUtils.isListEmpty(rst)) {
            mTitleInfo = rst.get(0);
        }
    }

    public String getTitle() {
        return ActionPerformer.getText(mTitleInfo, "PersonalIntroductionPage.getTitle()");
    }

    public String getLabelText() {
        return ActionPerformer.getText(mLabelInfo, "PersonalIntroductionPage.getLabelText()");
    }

    public void performBack() {
        ActionPerformer.performAction(
                mBackInfo,
                AccessibilityNodeInfo.ACTION_CLICK,
                "PersonalIntroductionPage 执行点击后退按钮");
    }

    public boolean performClickSendMessageInfo() {
        return ActionPerformer.performAction(
                mSendMessageInfo,
                AccessibilityNodeInfo.ACTION_CLICK,
                "PersonalIntroductionPage 点击发消息按钮");
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
