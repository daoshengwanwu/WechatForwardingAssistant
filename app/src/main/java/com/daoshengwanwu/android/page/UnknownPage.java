package com.daoshengwanwu.android.page;


import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;

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

    @Override
    public String getNextImportViewDescription() {
        return "";
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


    public UnknownPage() {
        super(PageId.PAGE_UNKNOWN, "未知");
    }
}
