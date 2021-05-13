package com.daoshengwanwu.android.util;


import android.text.TextUtils;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.daoshengwanwu.android.model.PageFeature;


public class PageUtils {
    // 一个界面的特征包含的ID数量
    private static final int PAGE_FEATURE_IDENTITY_COUNT = Integer.MAX_VALUE;


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
}
