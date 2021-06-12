package com.daoshengwanwu.android.page;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.daoshengwanwu.android.service.AuxiliaryService;
import com.daoshengwanwu.android.util.ActionPerformer;
import com.daoshengwanwu.android.util.CustomCollectionUtils;
import com.daoshengwanwu.android.util.CustomTextUtils;
import com.daoshengwanwu.android.util.SharedPreferencesUtils;
import com.daoshengwanwu.android.util.SingleSubThreadUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class ChatPage extends Page {
    private String mBackId;
    private String mTitleId;
    private String mEditTextId;
    private String mSendingBtnId;
//    private String mMaxSelectDialogTVId;
    private String mCheckBoxId;

    private AccessibilityNodeInfo mBackInfo;
    private AccessibilityNodeInfo mTitleInfo;
    private AccessibilityNodeInfo mEditTextInfo;
    private AccessibilityNodeInfo mSendingBtnInfo;
//    private AccessibilityNodeInfo mMaxSelectDialogTextViewInfo;
    private List<AccessibilityNodeInfo> mCheckBoxInfos;


    //================================================================================
    //============================= Common Start =====================================
    //================================================================================
    @Override
    public boolean captureImportViewResourceIdName(@NonNull AccessibilityEvent event) {
        if (event == null || event.getEventType() != AccessibilityEvent.TYPE_VIEW_CLICKED) {
            return false;
        }

        final AccessibilityNodeInfo info = event.getSource();
        if (info == null) {
            SingleSubThreadUtil.showToast(AuxiliaryService.getServiceInstance(), "请回到聊天界面再次点击截取", Toast.LENGTH_SHORT);
            return false;
        }

        if (TextUtils.isEmpty(mBackId)) {
            AccessibilityNodeInfo i = findFirstClickable(info);
            if (i != null) {
                mBackId = i.getViewIdResourceName();

                return mBackId != null;
            } else {
                return false;
            }
        } else if (TextUtils.isEmpty(mTitleId)) {
            AccessibilityNodeInfo i = findFirstChild(info, "android.widget.TextView");
            if (i != null) {
                mTitleId = i.getViewIdResourceName();

                return mTitleId != null;
            } else {
                return false;
            }
        } else if (TextUtils.isEmpty(mEditTextId)) {
            AccessibilityNodeInfo i = findFirstChild(info, "android.widget.EditText");
            if (i != null) {
                mEditTextId = i.getViewIdResourceName();

                return mEditTextId != null;
            } else {
                return false;
            }
        } else if (TextUtils.isEmpty(mSendingBtnId)) {
            AccessibilityNodeInfo i = findFirstChild(info, "android.widget.Button");
            if (i != null) {
                mSendingBtnId = i.getViewIdResourceName();

                return mSendingBtnId != null;
            } else {
                return false;
            }
        }
//        else if (TextUtils.isEmpty(mMaxSelectDialogTVId)) {
//            AccessibilityNodeInfo i = findFirstChild(info, "android.widget.TextView");
//            if (i != null) {
//                mMaxSelectDialogTVId = i.getViewIdResourceName();
//
//                return mMaxSelectDialogTVId != null;
//            }
//        }
        else if (TextUtils.isEmpty(mCheckBoxId)) {
            AccessibilityNodeInfo i = findFirstChild(info, "android.widget.CheckBox");
            if (i != null) {
                mCheckBoxId = i.getViewIdResourceName();

                return mCheckBoxId != null;
            } else {
                return false;
            }
        }

        return true;
    }

    @Override
    public String getNextImportViewDescription() {
        if (TextUtils.isEmpty(mBackId)) {
            return "后退按钮";
        }

        if (TextUtils.isEmpty(mTitleId)) {
            return "标题";
        }

        if (TextUtils.isEmpty(mEditTextId)) {
            return "输入框";
        }

        if (TextUtils.isEmpty(mSendingBtnId)) {
            return "发送按钮";
        }

//        if (TextUtils.isEmpty(mMaxSelectDialogTVId)) {
//            return "最大消息弹窗按钮";
//        }

        if (TextUtils.isEmpty(mCheckBoxId)) {
            return "多选窗";
        }

        return "所有Id已捕获完毕";
    }

    @Override
    public boolean isImportViewResourceIdNameCaptured() {
        return !TextUtils.isEmpty(mBackId) &&
                !TextUtils.isEmpty(mTitleId) &&
                !TextUtils.isEmpty(mEditTextId) &&
                !TextUtils.isEmpty(mSendingBtnId) &&
//                !TextUtils.isEmpty(mMaxSelectDialogTVId) &&
                !TextUtils.isEmpty(mCheckBoxId);
    }

    @Override
    public void saveAllImportViewResourceIdName() {
        final String idStr = TextUtils.join(",", new String[] {mBackId, mTitleId, mEditTextId, mSendingBtnId, mCheckBoxId});
        SharedPreferencesUtils.STRING_CACHE.CHAT_PAGE_VIEW_RESOURCE_ID_NAME.put(idStr);
    }

    @Override
    public void restoreImportViewResourceIdNameFromCache() {
        final String idStr = SharedPreferencesUtils.STRING_CACHE.CHAT_PAGE_VIEW_RESOURCE_ID_NAME.get("");
        if (TextUtils.isEmpty(idStr)) {
            return;
        }

        String[] splitStr = idStr.split(",");

        if (splitStr.length >= 1) {
            mBackId = splitStr[0];
        }

        if (splitStr.length >= 2) {
            mTitleId = splitStr[1];
        }

        if (splitStr.length >= 3) {
            mEditTextId = splitStr[2];
        }

        if (splitStr.length >= 4) {
            mSendingBtnId = splitStr[3];
        }

        if (splitStr.length >= 5) {
            mCheckBoxId = splitStr[4];
        }
    }

    public ChatPage() {
        super(PageId.PAGE_CHAT, "聊天");
    }

    @Override
    public void bindData(@NotNull AccessibilityNodeInfo rootInfo) {
        if (!isImportViewResourceIdNameCaptured()) {
            return;
        }

        List<AccessibilityNodeInfo> rst;

        // 后退LinearLayout
        rst = rootInfo.findAccessibilityNodeInfosByViewId(mBackId);
        if (!CustomCollectionUtils.isListEmpty(rst)) {
            mBackInfo = rst.get(0);
        }

        // 联系人名字TextView
        rst = rootInfo.findAccessibilityNodeInfosByViewId(mTitleId);
        if (!CustomCollectionUtils.isListEmpty(rst)) {
            mTitleInfo = rst.get(0);
        }

        // EditText
        rst = rootInfo.findAccessibilityNodeInfosByViewId(mEditTextId);
        if (!CustomCollectionUtils.isListEmpty(rst)) {
            mEditTextInfo = rst.get(0);
        }

        // 发送按钮
        rst = rootInfo.findAccessibilityNodeInfosByViewId(mSendingBtnId);
        if (!CustomCollectionUtils.isListEmpty(rst)) {
            mSendingBtnInfo = rst.get(0);
        }

        // 多选之后的CheckBox
        mCheckBoxInfos = rootInfo.findAccessibilityNodeInfosByViewId(mCheckBoxId);

        // 最多可选择99条信息dialog的textview
//        rst = rootInfo.findAccessibilityNodeInfosByViewId(mMaxSelectDialogTVId);
//        if (!CustomCollectionUtils.isListEmpty(rst)) {
//            mMaxSelectDialogTextViewInfo = rst.get(0);
//        }
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
//        return mMaxSelectDialogTextViewInfo != null &&
//                "最多可选择99条消息".equals(ActionPerformer.getText(mMaxSelectDialogTextViewInfo, "获取最多选择99条信息弹框的文字"));
        return false;
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
