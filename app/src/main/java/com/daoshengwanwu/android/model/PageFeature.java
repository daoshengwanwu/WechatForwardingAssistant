package com.daoshengwanwu.android.model;


import android.text.TextUtils;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.daoshengwanwu.android.util.ActionPerformer;
import com.daoshengwanwu.android.util.CustomTextUtils;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;


public class PageFeature {
    private final LinkedHashSet<ViewFeature> viewFeatures = new LinkedHashSet<>();

    private String lastFeatureClassName;


    public void addViewFeature(final AccessibilityNodeInfo info) {
        if (info == null) {
            return;
        }

        final String viewIdResourceName = info.getViewIdResourceName();
        final String text = ActionPerformer.getText(info, "获取特征->获取text");
        final String className = info.getClassName() == null ? "" : info.getClassName().toString();
        final boolean isWeak;
        if (CustomTextUtils.canStringParseToIntger(text)) {
            isWeak = true;
        } else {
            isWeak = false;
        }

        if (!TextUtils.isEmpty(viewIdResourceName)) {
            viewFeatures.add(new ViewFeature(viewIdResourceName, text, isWeak));
            lastFeatureClassName = className;
        }
    }

    public String getLastFeatureClassName() {
        return lastFeatureClassName;
    }

    public LinkedHashSet<ViewFeature> getViewFeatures() {
        return viewFeatures;
    }

    public int size() {
        return viewFeatures.size();
    }

    @NonNull
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder("\n<page feature start>");

        for (ViewFeature feature : viewFeatures) {
            builder.append("\n\t").append(feature.toString());
        }

        builder.append("\n<page feature end>\n\n");

        return builder.toString();
    }

    public boolean equalsExt(@Nullable PageFeature target) {
        final Set<ViewFeature> targetFeatures = new LinkedHashSet<>(target.viewFeatures);
        for (ViewFeature feature : viewFeatures) {
            targetFeatures.remove(feature);
        }

        for (ViewFeature feature : targetFeatures) {
            if (!feature.isWeak) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof  PageFeature)) {
            return false;
        }

        final PageFeature target = (PageFeature)obj;
        return viewFeatures.equals(target.viewFeatures);
    }

    @Override
    public int hashCode() {
        return viewFeatures.hashCode();
    }

    private static final class ViewFeature {
        private static final Set<String> ID_FEATURE_NEED_TEXT = new HashSet<>();


        static {
            ID_FEATURE_NEED_TEXT.add("android:id/text1");
        }


        public final String viewIdResourcesName;
        public final String text;
        public final boolean isWeak;


        private ViewFeature(String viewIdResourcesName, String text, boolean isWeak) {
            this.viewIdResourcesName = viewIdResourcesName;
            this.text = CustomTextUtils.getViewFeatureText(text);
            this.isWeak = isWeak;
        }

        private boolean isFeatureNeedWithText() {
            if (viewIdResourcesName == null) {
                return false;
            }

            return ID_FEATURE_NEED_TEXT.contains(viewIdResourcesName);
        }

        @NonNull
        @Override
        public String toString() {
            return "{id: " + viewIdResourcesName + ", text: " +
                    '"' + text + '"' + (isFeatureNeedWithText() ? " (特征)" : "") + "}" +
                    (isWeak ? " (弱特征)" : "");
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (!(obj instanceof ViewFeature)) {
                return false;
            }

            final ViewFeature feature = (ViewFeature) obj;
            if (viewIdResourcesName == null) {
                if (feature.viewIdResourcesName != null) {
                    return false;
                }
            } else {
                if (!viewIdResourcesName.equals(feature.viewIdResourcesName)) {
                    return false;
                }
            }

            if (!isFeatureNeedWithText()) {
                return true;
            }

            if (text == null) {
                return feature.text == null;
            } else {
                return text.equals(feature.text);
            }
        }

        @Override
        public int hashCode() {
            int hashCode = 0;
            if (viewIdResourcesName != null) {
                hashCode += viewIdResourcesName.hashCode();
            }

            if (isFeatureNeedWithText() && text != null) {
                hashCode += 31 * text.hashCode();
            }

            return hashCode;
        }
    }
}
