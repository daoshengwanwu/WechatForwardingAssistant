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

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class WechatPage extends Page {
    private String mContactId;
    private AccessibilityNodeInfo mContactTabInfo;


    @Override
    public String getNextImportViewDescription() {
        if (TextUtils.isEmpty(mContactId) || "null".equals(mContactId)) {
            return "通讯录Tab";
        }

        return "success";
    }

    @Override
    public boolean isImportViewResourceIdNameCaptured() {
        return !TextUtils.isEmpty(mContactId) && !"null".equals(mContactId);
    }

    @Override
    public boolean captureImportViewResourceIdName(@NonNull AccessibilityEvent event) {
        if (event == null || event.getEventType() != AccessibilityEvent.TYPE_VIEW_CLICKED) {
            return false;
        }

        final AccessibilityNodeInfo info = event.getSource();
        if (info == null) {
            SingleSubThreadUtil.showToast(AuxiliaryService.getServiceInstance(), "请回到信息列表界面再次点击截取", Toast.LENGTH_SHORT);
            return false;
        }

        if (TextUtils.isEmpty(mContactId) || "null".equals(mContactId)) {
            AccessibilityNodeInfo i = findFirstChild(info, "android.widget.ImageView");
            if (i != null) {
                mContactId = i.getViewIdResourceName();

                return mContactId != null;
            } else {
                return false;
            }
        }

        return true;
    }

    @Override
    public void saveAllImportViewResourceIdName() {
        final String idStr = TextUtils.join(",", new String[] { mContactId });
        SharedPreferencesUtils.STRING_CACHE.WECHAT_PAGE_VIEW_RESOURCE_ID_NAME.put(idStr);
    }

    @Override
    public void restoreImportViewResourceIdNameFromCache() {
        final String idStr = SharedPreferencesUtils.STRING_CACHE.WECHAT_PAGE_VIEW_RESOURCE_ID_NAME.get("");
        if (TextUtils.isEmpty(idStr)) {
            return;
        }

        String[] splitStr = idStr.split(",");

        if (splitStr.length >= 1) {
            mContactId = splitStr[0];
        }
    }

    public WechatPage() {
        super(PageId.PAGE_WECHAT, "消息列表");
    }

    @Override
    public void bindData(@NotNull AccessibilityNodeInfo rootInfo) {
        List<AccessibilityNodeInfo> rst;

        // 导航栏的ImageView
        rst = rootInfo.findAccessibilityNodeInfosByViewId(mContactId);
        if (CustomCollectionUtils.isListEmpty(rst) || rst.size() != 4) {
            return;
        }

        mContactTabInfo = rst.get(1).getParent();
    }

    @Override
    protected SharedPreferencesUtils.STRING_CACHE getCacheEnumInstance() {
        return SharedPreferencesUtils.STRING_CACHE.WECHAT_PAGE_FEATURE;
    }

    public boolean switchToContactPage() {
        return ActionPerformer.performAction(
                mContactTabInfo,
                AccessibilityNodeInfo.ACTION_CLICK,
                "微信界面点击联系人tab");
    }
}
