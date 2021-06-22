package com.daoshengwanwu.android.util;


import android.text.TextUtils;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.daoshengwanwu.android.model.PageFeature;


public class PageUtils {
    // 一个界面的特征包含的ID数量
    private static final int PAGE_FEATURE_IDENTITY_COUNT = Integer.MAX_VALUE;


    public static AccessibilityNodeInfo findChild(AccessibilityNodeInfo info, String className, int index) {
        FindChildResult result = findChildInner(info, className, index);
        if (result != null && result.count == index) {
            return result.info;
        }

        return null;
    }

    private static FindChildResult findChildInner(AccessibilityNodeInfo info, String className, int index) {
        if (info == null || TextUtils.isEmpty(className) || index < 1) {
            return new FindChildResult(null, 0);
        }

        int total = 0;
        if (className.equals(info.getClassName() == null ? null : info.getClassName().toString())) {
            if (index == 1) {
                return new FindChildResult(info, 1);
            } else {
                total = 1;
            }
        }

        int childSize = info.getChildCount();
        if (childSize <= 0) {
            return new FindChildResult(null, total);
        }

        for (int i = 0; i < childSize; i++) {
            AccessibilityNodeInfo child = info.getChild(i);
            FindChildResult result = findChildInner(child, className, index - total);

            if (result == null) {
                continue;
            }

            if (result.count == index - total) {
                return new FindChildResult(result.info, index);
            } else {
                total += result.count;
            }
        }


        return new FindChildResult(null, total);
    }

    public static AccessibilityNodeInfo findFirstClickableParent(@NonNull AccessibilityNodeInfo info) {
        while (info != null) {
            if (info.isClickable()) {
                return info;
            }

            info = info.getParent();
        }

        return null;
    }

    /**
     * 获取界面的特征，以List返回
     * @param rootInfo 界面的根AccessibilityInfo
     * @return 界面特征，如果失败返回null
     */
    @Nullable
    public static PageFeature gatherPageFeatures(@NonNull final AccessibilityNodeInfo rootInfo) {
        if (rootInfo == null) {
            return null;
        }

        final PageFeature feature = new PageFeature();
        gatherPageFeaturesInner(rootInfo, feature);

        return feature;
    }

    private static void gatherPageFeaturesInner(@NonNull final AccessibilityNodeInfo info,
                                                @NonNull final PageFeature feature) {

        if (info == null || feature == null ||
                feature.size() >= PAGE_FEATURE_IDENTITY_COUNT) {

            return;
        }

        if ("android.widget.EditText".equals(feature.getLastFeatureClassName())) {
            return;
        }

        feature.addViewFeature(info);

        final String className = info.getClassName() == null ? null : info.getClassName().toString();
        if (TextUtils.isEmpty(className)) {
            return;
        }

        if ("androidx.recyclerview.widget.RecyclerView".equals(className) ||
            "android.widget.ListView".equals(className) ||
            "android.support.v7.widget.RecyclerView".equals(className)) {

            return;
        }

        final int childCount = info.getChildCount();
        if (childCount <= 0) {
            return;
        }

        for (int i = 0; i < childCount; i++) {
            final AccessibilityNodeInfo child = info.getChild(i);
            gatherPageFeaturesInner(child, feature);
        }
    }


    private static class FindChildResult {
        AccessibilityNodeInfo info;
        int count;


        FindChildResult(AccessibilityNodeInfo info, int count) {
            this.info = info;
            this.count = count;
        }
    }
}
