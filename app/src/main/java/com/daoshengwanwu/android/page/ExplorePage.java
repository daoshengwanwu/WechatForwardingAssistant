package com.daoshengwanwu.android.page;


import android.view.accessibility.AccessibilityNodeInfo;
import androidx.annotation.NonNull;
import com.daoshengwanwu.android.util.CustomCollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class ExplorePage extends Page {
    private AccessibilityNodeInfo mContactTabInfo;


    public static boolean isSelf(@NonNull AccessibilityNodeInfo rootInfo) {
        List<AccessibilityNodeInfo> rst = rootInfo.findAccessibilityNodeInfosByViewId("android:id/text1");
        if (CustomCollectionUtils.isListEmpty(rst)) {
            return false;
        }

        AccessibilityNodeInfo titleInfo = rst.get(0);
        String title = String.valueOf(titleInfo.getText());

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

        rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/sh");
        if (CustomCollectionUtils.isListEmpty(rst) || rst.size() != 4) {
            throw new RuntimeException("在ExplorePage页面没有找到ContactTabInfo，程序意外终止");
        }

        mContactTabInfo = rst.get(1).getParent();
    }

    public boolean switchToContactPage() {
        if (mContactTabInfo == null) {
            return false;
        }

        return mContactTabInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
    }
}
