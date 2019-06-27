package com.daoshengwanwu.android.page;


import android.view.accessibility.AccessibilityNodeInfo;


public class UnknownPage extends Page {
    public static UnknownPage generateFrom(AccessibilityNodeInfo rootInfo) {
        return new UnknownPage();
    }


    private UnknownPage() {
        super(PageId.PAGE_UNKNOWN);
    }
}
