package com.daoshengwanwu.android.page;


import android.os.Bundle;
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
    private AccessibilityNodeInfo mMaxSelectDialogTextViewInfo;
    private List<AccessibilityNodeInfo> mCheckBoxInfos;


    //================================================================================
    //============================= Common Start =====================================
    //================================================================================
    public static boolean isSelf(@NonNull AccessibilityNodeInfo rootInfo) {
        boolean firstJudgement;

        //第一个有id的FrameLayout
        List<AccessibilityNodeInfo> rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/iew");
        if (CustomCollectionUtils.isListEmpty(rst)) {
            firstJudgement = false;
        } else {
            AccessibilityNodeInfo desInfo = rst.get(0).getParent();
            if (desInfo == null) {
                firstJudgement = false;
            } else {
                String description = String.valueOf(desInfo.getContentDescription());
                firstJudgement = description.startsWith("当前所在页面,与") && description.endsWith("的聊天");
            }
        }

        boolean secondJudgement = false;
        // 最多可选择99条信息dialog的textview
        rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/ffh");
        if (!CustomCollectionUtils.isListEmpty(rst)) {
            AccessibilityNodeInfo nodeInfo = rst.get(0);
            secondJudgement = nodeInfo != null &&
                    "最多可选择99条消息".equals(ActionPerformer.getText(nodeInfo, "获取最多选择99条信息弹框的文字"));
        }

        return firstJudgement || secondJudgement;
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

        // 后退LinearLayout
        rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/eh");
        if (!CustomCollectionUtils.isListEmpty(rst)) {
            mBackInfo = rst.get(0);
        }

        if (mBackInfo == null) {
            rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/un");
            if (!CustomCollectionUtils.isListEmpty(rst)) {
                mBackInfo = rst.get(0);
            }
        }

        // 联系人名字TextView
        rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/ipt");
        if (!CustomCollectionUtils.isListEmpty(rst)) {
            mTitleInfo = rst.get(0);
        }

        // EditText
        rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/auj");
        if (!CustomCollectionUtils.isListEmpty(rst)) {
            mEditTextInfo = rst.get(0);
        }

        if (mEditTextInfo == null) {
            rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/auj");
            if (!CustomCollectionUtils.isListEmpty(rst)) {
                mEditTextInfo = rst.get(0);
            }
        }

        if (mEditTextInfo == null) {
            rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/auj");
            if (!CustomCollectionUtils.isListEmpty(rst)) {
                mEditTextInfo = rst.get(0);
            }
        }

        // 发送按钮
        rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/ay5");
        if (!CustomCollectionUtils.isListEmpty(rst)) {
            mSendingBtnInfo = rst.get(0);
        }

        // 多选之后的CheckBox
        mCheckBoxInfos = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/aue");

        // 最多可选择99条信息dialog的textview
        rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/ffh");
        if (!CustomCollectionUtils.isListEmpty(rst)) {
            mMaxSelectDialogTextViewInfo = rst.get(0);
        }
    }
    //================================================================================
    //============================= Common End =======================================
    //================================================================================


    public void performBack() {
        ActionPerformer.performAction(mBackInfo, AccessibilityNodeInfo.ACTION_CLICK, "聊天界面点击back");
    }

    public boolean setEditTextText(String text) {
        if (mEditTextInfo == null) {
            return false;
        }

        final CharSequence className = mEditTextInfo.getClassName();
        if (className == null || className.toString().equals("android.widget.FrameLayout")) {
            mEditTextInfo = mEditTextInfo.getChild(0);
        }

        if (mEditTextInfo == null) {
            return false;
        }

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

    public boolean isWithMaxCheckDialog() {
        return mMaxSelectDialogTextViewInfo != null &&
                "最多可选择99条消息".equals(ActionPerformer.getText(mMaxSelectDialogTextViewInfo, "获取最多选择99条信息弹框的文字"));
    }

    public String getTitle() {
        return ActionPerformer.getText(mTitleInfo, "ChatPage.getTitle()");
    }

    public void performAllCheck() {
        if (mCheckBoxInfos != null) {
            for (AccessibilityNodeInfo info : mCheckBoxInfos) {
                if (info.isChecked()) {
                    break;
                }

                AccessibilityNodeInfo parent = info.getParent();
                if (parent == null) {
                    return;
                }

                int childCount = parent.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    AccessibilityNodeInfo childI = parent.getChild(i);
                    if (childI == null) {
                        break;
                    }

                    if ("android.view.View".equals(childI.getClassName() + "")) {
                        ActionPerformer.performAction(
                                childI,
                                AccessibilityNodeInfo.ACTION_CLICK,
                                "聊天界面执行点击事件for check");
                    }
                }
            }
        }
    }
}
