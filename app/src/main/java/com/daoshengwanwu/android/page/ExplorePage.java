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


public class ExplorePage extends Page {
    private String mContactTabId;

    private AccessibilityNodeInfo mContactTabInfo;


    @Override
    public String getNextImportViewDescription() {
        if (TextUtils.isEmpty(mContactTabId) || "null".equals(mContactTabId)) {
            return "通讯录Tab";
        }

        return "success";
    }

    @Override
    public boolean isImportViewResourceIdNameCaptured() {
        return !TextUtils.isEmpty(mContactTabId) && !"null".equals(mContactTabId);
    }

    @Override
    public boolean captureImportViewResourceIdName(@NonNull AccessibilityEvent event) {
        if (event == null || event.getEventType() != AccessibilityEvent.TYPE_VIEW_CLICKED) {
            return false;
        }

        final AccessibilityNodeInfo info = event.getSource();
        if (info == null) {
            SingleSubThreadUtil.showToast(AuxiliaryService.getServiceInstance(), "请回到发现界面再次点击捕获", Toast.LENGTH_LONG);
            return false;
        }

        if (TextUtils.isEmpty(mContactTabId) || "null".equals(mContactTabId)) {
            AccessibilityNodeInfo i = findFirstChild(info, "android.widget.ImageView");
            if (i != null) {
                mContactTabId = i.getViewIdResourceName();

                return mContactTabId != null;
            } else {
                return false;
            }
        }

        return true;
    }

    @Override
    public void saveAllImportViewResourceIdName() {
        final String idStr = TextUtils.join(",", new String[] {mContactTabId});
        SharedPreferencesUtils.STRING_CACHE.EXPLORE_PAGE_VIEW_RESOURCE_ID_NAME.put(idStr);
    }

    @Override
    public void restoreImportViewResourceIdNameFromCache() {
        final String idStr = SharedPreferencesUtils.STRING_CACHE.EXPLORE_PAGE_VIEW_RESOURCE_ID_NAME.get("");
        if (TextUtils.isEmpty(idStr)) {
            return;
        }

        String[] splitStr = idStr.split(",");

        if (splitStr.length >= 1) {
            mContactTabId = splitStr[0];
        }
    }

    public ExplorePage() {
        super(PageId.PAGE_EXPLORE, "发现");
    }

    @Override
    public void bindData(@NotNull AccessibilityNodeInfo rootInfo) {
        List<AccessibilityNodeInfo> rst;

        // 导航栏的Item的ImageView
        rst = rootInfo.findAccessibilityNodeInfosByViewId(mContactTabId);
        if (CustomCollectionUtils.isListEmpty(rst) || rst.size() != 4) {
            return;
        }

        mContactTabInfo = rst.get(1).getParent();
    }

    @Override
    protected SharedPreferencesUtils.STRING_CACHE getCacheEnumInstance() {
        return SharedPreferencesUtils.STRING_CACHE.EXPLORE_PAGE_FEATURE;
    }

    public boolean switchToContactPage() {
        if (mContactTabInfo == null) {
            return false;
        }

        return ActionPerformer.performAction(
                mContactTabInfo,
                AccessibilityNodeInfo.ACTION_CLICK,
                "发现界面点击联系人tab");
    }
}
