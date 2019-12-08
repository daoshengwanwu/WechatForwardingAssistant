package com.daoshengwanwu.android.page;


import android.text.TextUtils;
import android.view.accessibility.AccessibilityNodeInfo;
import androidx.annotation.NonNull;

import com.daoshengwanwu.android.util.ActionPerformer;
import com.daoshengwanwu.android.util.CustomCollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class FriendPage extends Page {
    private List<FriendItem> mFriendItems = new ArrayList<>();
    private AccessibilityNodeInfo mCommentContainerInfo;
    private AccessibilityNodeInfo mCommentInfo;
    private AccessibilityNodeInfo mYesInfo;


    public static boolean isSelf(AccessibilityNodeInfo rootInfo) {
        // 第一个有id的FrameLayout
        List<AccessibilityNodeInfo> rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/fdg");
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

        // 三个点ImageView
        List<AccessibilityNodeInfo> tDotInfos = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/eyz");
        // title TextView
        List<AccessibilityNodeInfo> titleInfos = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/baj");

        int i = 1;
        while (i < tDotInfos.size()&& i < titleInfos.size()) {
            mFriendItems.add(new FriendItem(titleInfos.get(i), tDotInfos.get(i)));
            i++;
        }

        //点击三个按钮后弹出的LinearLayout
        List<AccessibilityNodeInfo> rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/nk");
        if (!CustomCollectionUtils.isListEmpty(rst)) {
            mCommentContainerInfo = rst.get(0);
        } else {
            mCommentContainerInfo = null;
        }

        //赞TextView
        rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/eym");
        if (!CustomCollectionUtils.isListEmpty(rst)) {
            mYesInfo = rst.get(0);
        } else {
            mYesInfo = null;
        }

        //评论TextView
        rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/eyp");
        if (!CustomCollectionUtils.isListEmpty(rst)) {
            mCommentInfo = rst.get(0);
        } else {
            mCommentInfo = null;
        }
    }

    public void performClickTDotIfNeed(AccessibilityNodeInfo rootInfo) {
        bindData(rootInfo);

        if (mCommentContainerInfo != null &&
                mYesInfo != null &&
                (ActionPerformer.getText(mYesInfo, "朋友圈界面获取赞按钮的text").equals("赞") ||
                ActionPerformer.getText(mYesInfo, "朋友圈界面获取赞按钮的text").equals("取消")) &&
                mCommentInfo != null &&
                ActionPerformer.getText(mCommentInfo, "评论Info获取Text").equals("评论")) {

            return;
        }

        if (mFriendItems == null || mFriendItems.size() <= 0) {
            return;
        }

        ActionPerformer.performAction(
                mFriendItems.get(0).tDotInfo,
                AccessibilityNodeInfo.ACTION_CLICK,
                "朋友圈界面点击三个点");
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
