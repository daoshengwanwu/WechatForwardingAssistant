package com.daoshengwanwu.android.page;


import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;

import com.daoshengwanwu.android.util.ActionPerformer;
import com.daoshengwanwu.android.util.CustomCollectionUtils;
import com.daoshengwanwu.android.util.SharedPreferencesUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class ExplorePage extends Page {
    private AccessibilityNodeInfo mContactTabInfo;


    public static ExplorePage generateFrom(AccessibilityNodeInfo rootInfo) {
        ExplorePage page = new ExplorePage();

        page.bindData(rootInfo);

        return page;
    }

    private ExplorePage() {
        super(PageId.PAGE_EXPLORE, "发现");
    }

    @Override
    public void bindData(@NotNull AccessibilityNodeInfo rootInfo) {
        List<AccessibilityNodeInfo> rst;

        // 导航栏的Item的ImageView
        rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/dtx");
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
