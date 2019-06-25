package com.daoshengwanwu.android.page;


import android.view.accessibility.AccessibilityNodeInfo;
import androidx.annotation.NonNull;


public abstract class Page {
    private final PageId mPageId;


    public static Page generateFrom(@NonNull AccessibilityNodeInfo rootInfo) {
        // TODO::
        return null;
    }

    private static PageId whichPage(@NonNull AccessibilityNodeInfo rootInfo) {
        // TODO::
    }


    protected Page(PageId pageId) {
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
