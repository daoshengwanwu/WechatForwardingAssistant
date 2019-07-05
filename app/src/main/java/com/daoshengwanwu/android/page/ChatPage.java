package com.daoshengwanwu.android.page;


import android.os.Bundle;
import android.view.accessibility.AccessibilityNodeInfo;
import androidx.annotation.NonNull;
import com.daoshengwanwu.android.util.CustomCollectionUtils;

import java.util.List;


public class ChatPage extends Page {
    private AccessibilityNodeInfo mBackInfo;
    private AccessibilityNodeInfo mTitleInfo;
    private AccessibilityNodeInfo mEditTextInfo;
    private AccessibilityNodeInfo mSendingBtnInfo;


    public static boolean isSelf(@NonNull AccessibilityNodeInfo rootInfo) {
        List<AccessibilityNodeInfo> rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/ev2");
        if (CustomCollectionUtils.isListEmpty(rst)) {
            return false;
        }

        AccessibilityNodeInfo desInfo = rst.get(0).getParent();
        if (desInfo == null) {
            return false;
        }

        String description = String.valueOf(desInfo.getContentDescription());
        return description.startsWith("当前所在页面,与") && description.endsWith("的聊天");
    }

    public static ChatPage generateFrom(AccessibilityNodeInfo rootInfo) {
        ChatPage page = new ChatPage();

        page.bindData(rootInfo);

        return page;
    }

    private ChatPage() {
        super(PageId.PAGE_CHAT);
    }

    @Override
    public void bindData(AccessibilityNodeInfo rootInfo) {
        List<AccessibilityNodeInfo> rst;

        rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/km");
        if (CustomCollectionUtils.isListEmpty(rst)) {
            throw new RuntimeException("没有找到聊天页面的后退按钮");
        }
        mBackInfo = rst.get(0);

        rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/ko");
        if (CustomCollectionUtils.isListEmpty(rst)) {
            throw new RuntimeException("沒有找到聊天頁面的title info");
        }
        mTitleInfo = rst.get(0);

        rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/ami");
        if (CustomCollectionUtils.isListEmpty(rst)) {
            throw new RuntimeException("沒有找到聊天頁面的EditText info");
        }
        mEditTextInfo = rst.get(0);

        rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/amp");
        if (CustomCollectionUtils.isListEmpty(rst)) {
            throw new RuntimeException("沒有找到聊天頁面的發送按鈕info");
        }
        mSendingBtnInfo = rst.get(0);
    }

    public String getTitle() {
        return mTitleInfo.getText() + "";
    }

    public void performBack() {
        mBackInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
    }

    public boolean setEditTextText(String text) {
        Bundle arguments = new Bundle();
        arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text);
        return mEditTextInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
    }

    public boolean performClickSendButn() {
        return mSendingBtnInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
    }
}
