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


    public static boolean isSelf(@NonNull AccessibilityNodeInfo rootInfo) {
        List<AccessibilityNodeInfo> rst = rootInfo.findAccessibilityNodeInfosByViewId("android:id/text1");
        if (CustomCollectionUtils.isListEmpty(rst)) {
            return false;
        }

        AccessibilityNodeInfo titleInfo = rst.get(0);
        String title = String.valueOf(titleInfo.getText());

        return title.startsWith("通讯录");
    }

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
            UserItem item = getByFullNickname(set, title);
            if (item != null) {
                return new FindResult(info.getParent(), item);
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

    private UserItem getByFullNickname(Set<UserItem> userItems, String fullNickname) {
        if (userItems == null || fullNickname == null) {
            return null;
        }

        for (UserItem item : userItems) {
            if (item.fullNickName.equals(fullNickname)) {
                return item;
            }
        }

        return null;
    }


    public static final class FindResult {
        @NonNull public final AccessibilityNodeInfo info;
        @NonNull public final UserItem item;


        public FindResult(AccessibilityNodeInfo info, UserItem item) {
            this.info = info;
            this.item = item;
        }
    }
}
