package com.daoshengwanwu.android.page;


import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;
import androidx.annotation.NonNull;
import com.daoshengwanwu.android.util.ActionPerformer;
import com.daoshengwanwu.android.util.CustomCollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class ChatPage extends Page {
    private AccessibilityNodeInfo mBackInfo;
    private AccessibilityNodeInfo mTitleInfo;
    private AccessibilityNodeInfo mEditTextInfo;
    private AccessibilityNodeInfo mSendingBtnInfo;
    private List<AccessibilityNodeInfo> mCheckBoxInfos;


    //================================================================================
    //============================= Common Start =====================================
    //================================================================================
    public static boolean isSelf(@NonNull AccessibilityNodeInfo rootInfo) {
        List<AccessibilityNodeInfo> rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/f2z");
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
    public void bindData(@NotNull AccessibilityNodeInfo rootInfo) {
        List<AccessibilityNodeInfo> rst;

        rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/l3");
        if (CustomCollectionUtils.isListEmpty(rst)) {
            throw new RuntimeException("没有找到聊天页面的后退按钮");
        }
        mBackInfo = rst.get(0);

        rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/l5");
        if (CustomCollectionUtils.isListEmpty(rst)) {
            throw new RuntimeException("沒有找到聊天頁面的title info");
        }
        mTitleInfo = rst.get(0);

        rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/aom");
        if (!CustomCollectionUtils.isListEmpty(rst)) {
            mEditTextInfo = rst.get(0);
        }

        rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/aot");
        if (!CustomCollectionUtils.isListEmpty(rst)) {
            mSendingBtnInfo = rst.get(0);
        }

        mCheckBoxInfos = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/a9");
    }
    //================================================================================
    //============================= Common End =======================================
    //================================================================================


    public void performBack() {
        ActionPerformer.performAction(mBackInfo, AccessibilityNodeInfo.ACTION_CLICK, "聊天界面点击back");
    }

    public boolean setEditTextText(String text) {
        Bundle arguments = new Bundle();
        arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text);

        return ActionPerformer.performAction(
                mEditTextInfo,
                AccessibilityNodeInfo.ACTION_SET_TEXT,
                arguments,
                "聊天界面设置输入框内容");
    }

    public boolean performClickSendButn(AccessibilityNodeInfo rootInfo) {
        if (mSendingBtnInfo == null) {
            bindData(rootInfo);
        }

        return ActionPerformer.performAction(mSendingBtnInfo, AccessibilityNodeInfo.ACTION_CLICK, "聊天界面点击发送按钮");
    }

    public boolean isWithCheckBox() {
        return mCheckBoxInfos != null && mCheckBoxInfos.size() > 0;
    }

    public String getTitle() {
        return mTitleInfo.getText() + "";
    }

    public void performAllCheck() {
        if (mCheckBoxInfos != null) {
            for (AccessibilityNodeInfo info : mCheckBoxInfos) {
                if (!info.isChecked()) {
                    AccessibilityNodeInfo parent = info.getParent();
                    if (parent == null) {
                        return;
                    }

                    if (parent.getChildCount() == 3 || parent.getChildCount() == 4) {
                        parent.getChild(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    } else if (parent.getChildCount() == 5){
                        parent.getChild(1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                }
            }
        }
    }
}
