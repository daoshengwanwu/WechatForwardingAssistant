package com.daoshengwanwu.android.page;


import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;

import com.daoshengwanwu.android.util.ActionPerformer;
import com.daoshengwanwu.android.util.CustomCollectionUtils;
import com.daoshengwanwu.android.util.SharedPreferencesUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class WechatPage extends Page {
    @NonNull public static WechatPage generateFrom(@NonNull AccessibilityNodeInfo rootInfo) {
        WechatPage page = new WechatPage();

        page.bindData(rootInfo);

        return page;
    }


    private AccessibilityNodeInfo mContactTabInfo;


    public WechatPage() {
        super(PageId.PAGE_WECHAT, "消息列表");
    }

    @Override
    public void bindData(@NotNull AccessibilityNodeInfo rootInfo) {
        List<AccessibilityNodeInfo> rst;

        // 导航栏的ImageView
        rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/dtx");
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
