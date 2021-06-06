package com.daoshengwanwu.android.page;


import android.view.accessibility.AccessibilityNodeInfo;

import com.daoshengwanwu.android.util.SharedPreferencesUtils;

import org.jetbrains.annotations.NotNull;


public class UnknownPage extends Page {
    @Override
    public void bindData(@NotNull AccessibilityNodeInfo rootInfo) {
        //do nothing
    }

    @Override
    protected SharedPreferencesUtils.STRING_CACHE getCacheEnumInstance() {
        return null;
    }

    public static UnknownPage generateFrom(AccessibilityNodeInfo rootInfo) {
        return new UnknownPage();
    }


    private UnknownPage() {
        super(PageId.PAGE_UNKNOWN, "未知");
    }
}
