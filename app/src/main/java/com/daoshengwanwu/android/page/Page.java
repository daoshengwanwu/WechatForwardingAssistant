package com.daoshengwanwu.android.page;


import android.view.accessibility.AccessibilityNodeInfo;
import androidx.annotation.NonNull;


public abstract class Page {
    private final PageId mPageId;


    public abstract void bindData(AccessibilityNodeInfo rootInfo);


    @NonNull public static Page generateFrom(@NonNull AccessibilityNodeInfo rootInfo) {
        // TODO::
        switch (whichPage(rootInfo)) {
            case PAGE_LABEL_MEMBERS: {
                return LabelMembersPage.generateFrom(rootInfo);
            }
        }

        return UnknownPage.generateFrom(rootInfo);
    }

    @NonNull public static String getInfoText(AccessibilityNodeInfo info) {
        if (info == null) {
            return "";
        }

        return info.getText() + "";
    }

    private static PageId whichPage(@NonNull AccessibilityNodeInfo rootInfo) {
        // TODO::
        if (LabelMembersPage.isSelf(rootInfo)) {
            return PageId.PAGE_LABEL_MEMBERS;
        }

        return PageId.PAGE_UNKNOWN;
    }


    protected Page(@NonNull PageId pageId) {
        mPageId = pageId;
    }

    public final PageId getPageId() {
        return mPageId;
    }


    public enum PageId {
        PAGE_UNKNOWN,
        PAGE_WECHAT,
        PAGE_CONTACT,
        PAGE_EXPLORE,
        PAGE_SELF,
        PAGE_SEARCH_FORWARDING,
        PAGE_CHAT,
        PAGE_CHAT_WITH_CHECKBOX,
        PAGE_LABEL_MEMBERS,
        PAGE_PERSONAL_INTRODUCTION;
    }
}
