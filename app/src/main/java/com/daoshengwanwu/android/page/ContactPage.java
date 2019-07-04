package com.daoshengwanwu.android.page;


import android.view.accessibility.AccessibilityNodeInfo;
import androidx.annotation.NonNull;
import com.daoshengwanwu.android.model.item.UserItem;
import com.daoshengwanwu.android.util.CustomCollectionUtils;

import java.util.List;
import java.util.Set;


public class ContactPage extends Page {
    private AccessibilityNodeInfo mListInfo;
    private List<AccessibilityNodeInfo> mContactInfos;


    public static ContactPage generateFrom(AccessibilityNodeInfo rootInfo) {
        ContactPage page = new ContactPage();

        page.bindData(rootInfo);

        return page;
    }

    private ContactPage() {
        super(PageId.PAGE_CONTACT);
    }

    @Override
    public void bindData(AccessibilityNodeInfo rootInfo) {
        mContactInfos = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/o1");

        List<AccessibilityNodeInfo> rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/n3");
        if (CustomCollectionUtils.isListEmpty(rst)) {
            throw new RuntimeException("没有找到联系人界面的ListView程序意外终止");
        }
        mListInfo = rst.get(0);
    }

    public boolean performForwardingScrollListView() {
        return mListInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
    }

    public boolean performBackwordScrollListView() {
        return mListInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
    }

    public FindResult findFirstInfoInSpecificSet(Set<UserItem> set) {
        if (mContactInfos == null) {
            return null;
        }

        for (AccessibilityNodeInfo info : mContactInfos) {
            String title = info.getText() + "";
            if (contains(set, title)) {
                return new FindResult(info.getParent(), title);
            }
        }

        return null;
    }

    private boolean contains(Set<UserItem> userItemSet, String fullNickname) {
        if (userItemSet == null || fullNickname == null) {
            return false;
        }

        for (UserItem item : userItemSet) {
            if (fullNickname.equals(item.fullNickName)) {
                return true;
            }
        }

        return false;
    }


    public static final class FindResult {
        @NonNull public final AccessibilityNodeInfo info;
        @NonNull public final String text;


        public FindResult(AccessibilityNodeInfo info, String text) {
            this.info = info;
            this.text = text;
        }
    }
}