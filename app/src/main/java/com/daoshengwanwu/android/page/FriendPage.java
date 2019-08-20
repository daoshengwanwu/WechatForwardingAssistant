package com.daoshengwanwu.android.page;


import android.text.TextUtils;
import android.view.accessibility.AccessibilityNodeInfo;
import androidx.annotation.NonNull;
import com.daoshengwanwu.android.util.CustomCollectionUtils;

import java.util.List;


public class FriendPage extends Page {
    private List<AccessibilityNodeInfo> mTDotIVInfos;


    public static boolean isSelf(AccessibilityNodeInfo rootInfo) {
        List<AccessibilityNodeInfo> rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/f2z");
        if (CustomCollectionUtils.isListEmpty(rst)) {
            return false;
        }

        String des = rst.get(0).getContentDescription() + "";
        if (TextUtils.isEmpty(des) || "null".equals(des)) {
            return false;
        }

        return "当前所在页面,朋友圈".equals(des);
    }

    public static FriendPage generateFrom(AccessibilityNodeInfo rootInfo) {
        FriendPage page = new FriendPage();

        page.bindData(rootInfo);

        return page;
    }


    protected FriendPage() {
        super(PageId.PAGE_FRIEND);
    }

    @Override
    public void bindData(@NonNull AccessibilityNodeInfo rootInfo) {
        mTDotIVInfos = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/eop");
    }

    public void performAllTDotClick() {
        if (mTDotIVInfos == null) {
            return;
        }

        for (AccessibilityNodeInfo info : mTDotIVInfos) {
            info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
    }
}
