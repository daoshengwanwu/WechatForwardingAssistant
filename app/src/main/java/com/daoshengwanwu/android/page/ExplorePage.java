package com.daoshengwanwu.android.page;


import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;

import com.daoshengwanwu.android.util.ActionPerformer;
import com.daoshengwanwu.android.util.CustomCollectionUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class ExplorePage extends Page {
    private AccessibilityNodeInfo mContactTabInfo;


    public static boolean isSelf(@NonNull AccessibilityNodeInfo rootInfo) {
        // 发现页面左上角title的TextView
        List<AccessibilityNodeInfo> rst = rootInfo.findAccessibilityNodeInfosByViewId("android:id/text1");
        if (CustomCollectionUtils.isListEmpty(rst)) {
            return false;
        }

        AccessibilityNodeInfo titleInfo = rst.get(0);
        String title = ActionPerformer.getText(titleInfo, "发现界面获取title");

        return title.startsWith("发现");
    }

    public static ExplorePage generateFrom(AccessibilityNodeInfo rootInfo) {
        ExplorePage page = new ExplorePage();

        page.bindData(rootInfo);

        return page;
    }

    private ExplorePage() {
        super(PageId.PAGE_EXPLORE);
    }

    @Override
    public void bindData(@NotNull AccessibilityNodeInfo rootInfo) {
        List<AccessibilityNodeInfo> rst;

        // 导航栏的Item的ImageView
        rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/cio");
        if (CustomCollectionUtils.isListEmpty(rst) || rst.size() != 4) {
            return;
        }

        mContactTabInfo = rst.get(1).getParent();
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
