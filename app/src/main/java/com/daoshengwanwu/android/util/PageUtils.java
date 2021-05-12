package com.daoshengwanwu.android.util;


import android.text.TextUtils;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;


public class PageUtils {
    // 一个界面的特征包含的ID数量
    private static final int PAGE_FEATURE_IDENTITY_COUNT = 10;


    /**
     * 获取界面的特征，以List返回
     * @param rootInfo 界面的根AccessibilityInfo
     * @return 界面特征，如果失败返回null
     */
    @Nullable
    public static List<String> gatherPageFeatures(@NonNull final AccessibilityNodeInfo rootInfo) {
        if (rootInfo == null) {
            return null;
        }

        final List<String> result = new ArrayList<>();
        gatherPageFeaturesInner(rootInfo, result);

        return result;
    }

    private static void gatherPageFeaturesInner(@NonNull final AccessibilityNodeInfo info,
                                         @NonNull final List<String> outViewIdentityNames) {

        if (info == null || outViewIdentityNames == null ||
                outViewIdentityNames.size() >= PAGE_FEATURE_IDENTITY_COUNT) {

            return;
        }

        final String viewIdResourceName = info.getViewIdResourceName();
        if (!TextUtils.isEmpty(viewIdResourceName)) {
            outViewIdentityNames.add(viewIdResourceName);
        }

        final int childCount = info.getChildCount();
        if (childCount <= 0) {
            return;
        }

        for (int i = 0; i < childCount; i++) {
            gatherPageFeaturesInner(info.getChild(i), outViewIdentityNames);
        }
    }
}
