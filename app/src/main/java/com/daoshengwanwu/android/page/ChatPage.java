package com.daoshengwanwu.android.page;


import android.os.Bundle;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;

import com.daoshengwanwu.android.util.ActionPerformer;
import com.daoshengwanwu.android.util.CustomCollectionUtils;
import com.daoshengwanwu.android.util.CustomTextUtils;
import com.daoshengwanwu.android.util.SharedPreferencesUtils;

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
    public static ChatPage generateFrom(AccessibilityNodeInfo rootInfo) {
        ChatPage page = new ChatPage();

        page.bindData(rootInfo);

        return page;
    }

    public ChatPage() {
        super(PageId.PAGE_CHAT, "聊天");
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

    @Override
    protected SharedPreferencesUtils.STRING_CACHE getCacheEnumInstance() {
        return SharedPreferencesUtils.STRING_CACHE.CHAT_PAGE_FEATURE;
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
        String title = ActionPerformer.getText(mTitleInfo, "ChatPage.getTitle()");
        title = CustomTextUtils.getValidRemarkName(title);

        return title;
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
