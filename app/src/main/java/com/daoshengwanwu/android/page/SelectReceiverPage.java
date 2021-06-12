package com.daoshengwanwu.android.page;


import android.os.SystemClock;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.daoshengwanwu.android.model.UserGroup;
import com.daoshengwanwu.android.util.ActionPerformer;
import com.daoshengwanwu.android.util.CustomCollectionUtils;
import com.daoshengwanwu.android.util.SharedPreferencesUtils;

import java.util.List;


public class SelectReceiverPage extends Page {
    private AccessibilityNodeInfo mListViewInfo;
    private AccessibilityNodeInfo mBackLinearLayout;


    //================================================================================
    //============================= Common Start =====================================
    //================================================================================
    public static SelectReceiverPage generateFrom(@NonNull AccessibilityNodeInfo rootInfo) {
        SelectReceiverPage page = new SelectReceiverPage();

        page.bindData(rootInfo);

        return page;
    }

    @Override
    public String getNextImportViewDescription() {
        return null;
    }

    @Override
    public boolean isImportViewResourceIdNameCaptured() {
        return false;
    }

    @Override
    public boolean captureImportViewResourceIdName(@NonNull AccessibilityEvent event) {
        return false;
    }

    @Override
    public void saveAllImportViewResourceIdName() {

    }

    @Override
    public void restoreImportViewResourceIdNameFromCache() {

    }

    public SelectReceiverPage() {
        super(PageId.PAGE_SELECT_RECEIVER, "选择接收人");
    }

    @Override
    public void bindData(AccessibilityNodeInfo rootInfo) {
        if (rootInfo == null) {
            return;
        }

        // ListView
        List<AccessibilityNodeInfo> infos = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/hhi");
        if (CustomCollectionUtils.isListEmpty(infos)) {
            return;
        }
        mListViewInfo = infos.get(0);

        // 后退按钮LinearLayout
        infos = rootInfo.findAccessibilityNodeInfosByText("com.tencent.mm:id/eh");
        if (CustomCollectionUtils.isListEmpty(infos)) {
            return;
        }
        mBackLinearLayout = infos.get(0);
    }

    @Override
    protected SharedPreferencesUtils.STRING_CACHE getCacheEnumInstance() {
        return SharedPreferencesUtils.STRING_CACHE.SELECT_RECEIVER_PAGE_FEATURE;
    }

    public void performUpScroll(@Nullable AccessibilityNodeInfo rootInfo) {
        if (mListViewInfo == null && rootInfo != null) {
            bindData(rootInfo);
        }

        if (mListViewInfo == null) {
            return;
        }

        ActionPerformer.performAction(mListViewInfo, AccessibilityNodeInfo.ACTION_SCROLL_FORWARD, "向上滑动ListView");
    }

    public void performBack(@Nullable AccessibilityNodeInfo rootInfo) {
        if (mBackLinearLayout == null && rootInfo != null) {
            bindData(rootInfo);
        }

        if (mBackLinearLayout == null) {
            return;
        }

        ActionPerformer.performAction(mBackLinearLayout, AccessibilityNodeInfo.ACTION_CLICK, "点击返回上一页");
    }

    public void performSelectMatchUsers(UserGroup userGroup) {
        SystemClock.sleep(100L);

        if (userGroup == null) {
            return;
        }

        // title TextView
        List<AccessibilityNodeInfo> infos = mListViewInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/ir3");
        for (AccessibilityNodeInfo info : infos) {
            AccessibilityNodeInfo relativeNodeInfo = info.getParent();
            if (relativeNodeInfo == null) {
                continue;
            }

            // CheckBox
            List<AccessibilityNodeInfo> rst = relativeNodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/hhc");
            if (CustomCollectionUtils.isListEmpty(rst)) {
                continue;
            }

            AccessibilityNodeInfo checkboxInfo = rst.get(0);
            String fullName = ActionPerformer.getText(info, "获取群发列表界面item姓名");
            if (userGroup.containsFullNickNameItem(fullName) && !checkboxInfo.isChecked()) {
                if (ActionPerformer.performAction(relativeNodeInfo, AccessibilityNodeInfo.ACTION_CLICK, "点击群发界面好友item")) {
                    userGroup.removeUserItem(fullName);
                }
            }
        }
    }
}
