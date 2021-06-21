package com.daoshengwanwu.android.page;


import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.daoshengwanwu.android.service.AuxiliaryService;
import com.daoshengwanwu.android.util.ActionPerformer;
import com.daoshengwanwu.android.util.CustomCollectionUtils;
import com.daoshengwanwu.android.util.SharedPreferencesUtils;
import com.daoshengwanwu.android.util.SingleSubThreadUtil;

import java.util.List;


public class PersonalIntroductionPage extends Page {
    private String mBackId;
    private String mLabelId;
    private String mSendMessageId;

    private AccessibilityNodeInfo mBackInfo;
    private AccessibilityNodeInfo mLabelInfo;
    private AccessibilityNodeInfo mSendMessageInfo;


    @Override
    public String getNextImportViewDescription() {
        if (TextUtils.isEmpty(mBackId) || "null".equals(mBackId)) {
            return "后退按钮";
        }

        if (TextUtils.isEmpty(mSendMessageId) || "null".equals(mSendMessageId)) {
            return "发消息按钮";
        }

        if (TextUtils.isEmpty(mLabelId) || "null".equals(mLabelId)) {
            return "标签";
        }

        return "已完成";
    }

    @Override
    public boolean isImportViewResourceIdNameCaptured() {
        return !TextUtils.isEmpty(mBackId) && !"null".equals(mBackId) &&
                !TextUtils.isEmpty(mLabelId) && !"null".equals(mLabelId) &&
                !TextUtils.isEmpty(mSendMessageId) && !"null".equals(mSendMessageId);
    }

    @Override
    public boolean captureImportViewResourceIdName(@NonNull AccessibilityEvent event) {
        if (event == null || event.getEventType() != AccessibilityEvent.TYPE_VIEW_CLICKED) {
            return false;
        }

        if (TextUtils.isEmpty(mBackId) || "null".equals(mBackId)) {
            AccessibilityNodeInfo i = findFromRootWithDesc(AuxiliaryService.getServiceInstance().getRootInActiveWindow(), "返回");
            if (i != null) {
                mBackId = i.getParent().getViewIdResourceName();

                return mBackId != null;
            } else {
                return false;
            }
        } else if (TextUtils.isEmpty(mSendMessageId) || "null".equals(mSendMessageId)) {
            AccessibilityNodeInfo i = findFromRootWithText(AuxiliaryService.getServiceInstance().getRootInActiveWindow(), "发消息");
            if (i != null) {
                mSendMessageId = i.getViewIdResourceName();

                return mSendMessageId != null;
            } else {
                return false;
            }
        }

        final AccessibilityNodeInfo info = event.getSource();
        if (info == null) {
            SingleSubThreadUtil.showToast(AuxiliaryService.getServiceInstance(), "请回到标签界面再次点击截取", Toast.LENGTH_SHORT);
            return false;
        }

        if (TextUtils.isEmpty(mLabelId) || "null".equals(mLabelId)) {
            AccessibilityNodeInfo i = findFirstChild(info, "android.widget.TextView");
            if (i != null) {
                mLabelId = i.getViewIdResourceName();

                return mLabelId != null;
            } else {
                return false;
            }
        }

        return true;
    }

    @Override
    public void saveAllImportViewResourceIdName() {
        final String idStr = TextUtils.join(",", new String[] {mBackId, mSendMessageId, mLabelId});
        SharedPreferencesUtils.STRING_CACHE.PERSONAL_INTRODUCTION_PAGE_VIEW_RESOURCE_ID_NAME.put(idStr);
    }

    @Override
    public void restoreImportViewResourceIdNameFromCache() {
        final String idStr = SharedPreferencesUtils.STRING_CACHE.PERSONAL_INTRODUCTION_PAGE_VIEW_RESOURCE_ID_NAME.get("");
        if (TextUtils.isEmpty(idStr)) {
            return;
        }

        String[] splitStr = idStr.split(",");

        if (splitStr.length >= 1) {
            mBackId = splitStr[0];
        }

        if (splitStr.length >= 2) {
            mSendMessageId = splitStr[1];
        }

        if (splitStr.length >= 3) {
            mLabelId = splitStr[2];
        }
    }

    public PersonalIntroductionPage() {
        super(PageId.PAGE_PERSONAL_INTRODUCTION, "个人简介");
    }

    @Override
    public void bindData(@NonNull AccessibilityNodeInfo rootInfo) {
        List<AccessibilityNodeInfo> rst;

        //后退LinearLayout
        rst = rootInfo.findAccessibilityNodeInfosByViewId(mBackId);
        if (!CustomCollectionUtils.isListEmpty(rst)) {
            mBackInfo = rst.get(0);
        }

        // 标签TextView
        rst = rootInfo.findAccessibilityNodeInfosByViewId(mLabelId);
        if (!CustomCollectionUtils.isListEmpty(rst)) {
            mLabelInfo = rst.get(0);
        }

        // 发送消息TextView
        rst = rootInfo.findAccessibilityNodeInfosByViewId(mSendMessageId);
        if (!CustomCollectionUtils.isListEmpty(rst)) {
            mSendMessageInfo = rst.get(0);
        }
    }

    @Override
    protected SharedPreferencesUtils.STRING_CACHE getCacheEnumInstance() {
        return SharedPreferencesUtils.STRING_CACHE.PERSONAL_INTRODUCTION_PAGE_FEATURE;
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
}
