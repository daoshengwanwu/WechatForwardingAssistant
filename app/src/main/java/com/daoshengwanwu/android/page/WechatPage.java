package com.daoshengwanwu.android.page;


import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;

import com.daoshengwanwu.android.util.ActionPerformer;
import com.daoshengwanwu.android.util.CustomCollectionUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class WechatPage extends Page {
    private AccessibilityNodeInfo mContactTabInfo;


    private WechatPage() {
        super(PageId.PAGE_WECHAT);
    }


    public static boolean isSelf(@NonNull AccessibilityNodeInfo rootInfo) {
        // 消息记录界面的左上角标题TextView
        List<AccessibilityNodeInfo> rst = rootInfo.findAccessibilityNodeInfosByViewId("android:id/text1");
        if (CustomCollectionUtils.isListEmpty(rst)) {
            return false;
        }

        AccessibilityNodeInfo titleInfo = rst.get(0);
        String title = ActionPerformer.getText(titleInfo, "WechatPage isSelf 中 getText()");

        return title.startsWith("微信");
    }

    @NonNull public static WechatPage generateFrom(@NonNull AccessibilityNodeInfo rootInfo) {
        WechatPage page = new WechatPage();

        page.bindData(rootInfo);

        return page;
    }


    @Override
    public void bindData(@NotNull AccessibilityNodeInfo rootInfo) {
        List<AccessibilityNodeInfo> rst;

        // 导航栏的ImageView
        rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/cio");
        if (CustomCollectionUtils.isListEmpty(rst) || rst.size() != 4) {
            return;
        }

        mContactTabInfo = rst.get(1).getParent();
    }

    public boolean switchToContactPage() {
        return ActionPerformer.performAction(
                mContactTabInfo,
                AccessibilityNodeInfo.ACTION_CLICK,
                "微信界面点击联系人tab");
    }
}
