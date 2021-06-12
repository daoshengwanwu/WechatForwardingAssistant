package com.daoshengwanwu.android.page;


import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;

import com.daoshengwanwu.android.util.ActionPerformer;
import com.daoshengwanwu.android.util.CustomCollectionUtils;
import com.daoshengwanwu.android.util.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.List;


public class FriendPage extends Page {
    private List<FriendItem> mFriendItems = new ArrayList<>();
    private AccessibilityNodeInfo mCommentContainerInfo;
    private AccessibilityNodeInfo mCommentInfo;
    private AccessibilityNodeInfo mYesInfo;


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


    public FriendPage() {
        super(PageId.PAGE_FRIEND, "朋友圈");
    }

    @Override
    public void bindData(@NonNull AccessibilityNodeInfo rootInfo) {
        mFriendItems.clear();

        // 三个点ImageView
        List<AccessibilityNodeInfo> tDotInfos = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/kn");
        // title TextView
        List<AccessibilityNodeInfo> titleInfos = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/fzg");

        int i = 1;
        while (i < tDotInfos.size()&& i < titleInfos.size()) {
            mFriendItems.add(new FriendItem(titleInfos.get(i), tDotInfos.get(i)));
            i++;
        }

        //点击三个按钮后弹出的LinearLayout
        List<AccessibilityNodeInfo> rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/j_");
        if (!CustomCollectionUtils.isListEmpty(rst)) {
            mCommentContainerInfo = rst.get(0);
        } else {
            mCommentContainerInfo = null;
        }

        //赞TextView
        rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/kb");
        if (!CustomCollectionUtils.isListEmpty(rst)) {
            mYesInfo = rst.get(0);
        } else {
            mYesInfo = null;
        }

        //评论TextView
        rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/jp");
        if (!CustomCollectionUtils.isListEmpty(rst)) {
            mCommentInfo = rst.get(0);
        } else {
            mCommentInfo = null;
        }
    }

    @Override
    protected SharedPreferencesUtils.STRING_CACHE getCacheEnumInstance() {
        return SharedPreferencesUtils.STRING_CACHE.FRIEND_PAGE_FEATURE;
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
