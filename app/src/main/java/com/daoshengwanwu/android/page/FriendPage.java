package com.daoshengwanwu.android.page;


import android.text.TextUtils;
import android.view.accessibility.AccessibilityNodeInfo;
import androidx.annotation.NonNull;
import com.daoshengwanwu.android.util.CustomCollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class FriendPage extends Page {
    private List<FriendItem> mFriendItems = new ArrayList<>();


    public static boolean isSelf(AccessibilityNodeInfo rootInfo) {
        List<AccessibilityNodeInfo> rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/f2z");
        if (CustomCollectionUtils.isListEmpty(rst)) {
            return false;
        }

        String des = rst.get(0).getParent().getContentDescription() + "";
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
        mFriendItems.clear();

        List<AccessibilityNodeInfo> tDotInfos = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/eop");
        List<AccessibilityNodeInfo> titleInfos = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/b9i");

        int i = 1;
        while (i < tDotInfos.size() && i < titleInfos.size()) {
            mFriendItems.add(new FriendItem(titleInfos.get(i), tDotInfos.get(i)));
            i++;
        }
    }

    public void performClickTDotIfNeed(AccessibilityNodeInfo rootInfo) {
        List<FriendItem> ori = mFriendItems;
        mFriendItems = new ArrayList<>();
        bindData(rootInfo);

        if (equalsList(ori, mFriendItems)) {
            return;
        }

        for (FriendItem item : mFriendItems) {
            item.tDotInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            return;
        }
    }

    private boolean equalsList(List<FriendItem> ori, List<FriendItem> last) {
        if (ori.size() != last.size()) {
            return false;
        }

        for (int i = 0; i < ori.size(); i++) {
            FriendItem oriItem = ori.get(i);
            FriendItem lastItem = last.get(i);

            if (!(oriItem.titleInfo.getText() + "").equals(lastItem.titleInfo.getText() + "")) {
                return false;
            }
        }

        return true;
    }


    private class FriendItem {
        public AccessibilityNodeInfo titleInfo;
        public AccessibilityNodeInfo tDotInfo;


        public FriendItem(AccessibilityNodeInfo titleInfo, AccessibilityNodeInfo tDotInfo) {
            this.titleInfo = titleInfo;
            this.tDotInfo = tDotInfo;
        }
    }
}
