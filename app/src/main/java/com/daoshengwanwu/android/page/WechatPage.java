package com.daoshengwanwu.android.page;


import android.view.accessibility.AccessibilityNodeInfo;
import androidx.annotation.NonNull;
import com.daoshengwanwu.android.util.CustomCollectionUtils;

import java.util.List;


public class WechatPage extends Page {
    private AccessibilityNodeInfo mContactTabInfo;


    private WechatPage() {
        super(PageId.PAGE_WECHAT);
    }


    @NonNull public static WechatPage generateFrom(@NonNull AccessibilityNodeInfo rootInfo) {
        WechatPage page = new WechatPage();

        page.bindData(rootInfo);

        return page;
    }


    @Override
    public void bindData(AccessibilityNodeInfo rootInfo) {
        List<AccessibilityNodeInfo> rst;

        rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/d99");
        if (CustomCollectionUtils.isListEmpty(rst) || rst.size() != 4) {
            throw new RuntimeException("在WechatPage页面没有找到ContactTabInfo，程序意外终止");
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
