package com.daoshengwanwu.android.page;


import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.daoshengwanwu.android.model.PageFeature;
import com.daoshengwanwu.android.util.ActionPerformer;
import com.daoshengwanwu.android.util.PageUtils;
import com.daoshengwanwu.android.util.SharedPreferencesUtils;
import com.google.gson.Gson;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;


public abstract class Page {
    private static final Gson GSON = new Gson();

    private static volatile Map<PageId, Page> sAllPageMap;


    public static Map<PageId, Page> getAllPages() {
        if (sAllPageMap == null) {
            synchronized (Page.class) {
                if (sAllPageMap == null) {
                    sAllPageMap = newPageListInstanceLocked();
                }
            }
        }

        return sAllPageMap;
    }

    public static boolean isAllPagesReady() {
        final Collection<Page> allPages = getAllPages().values();
        for (Page page : allPages) {
            if (!page.isPageReady()) {
                return false;
            }
        }

        return true;
    }

    @Nullable
    public static Page getPage(@NonNull final PageId pageId) {
        return getAllPages().get(pageId);
    }

    @NonNull
    public static Page generateFrom(@NonNull AccessibilityNodeInfo rootInfo) {
        final Page page = whichPage(rootInfo);

        if (page.getPageId() == PageId.PAGE_UNKNOWN) {
            return page;
        }

        page.bindData(rootInfo);
        return page;
    }

    private static Map<PageId, Page> newPageListInstanceLocked() {
        final Map<PageId, Page> result = new LinkedHashMap<>();

        result.put(PageId.PAGE_WECHAT, new WechatPage().restoreFromSharedPreferences());
        result.put(PageId.PAGE_CHAT, new ChatPage().restoreFromSharedPreferences());
        result.put(PageId.PAGE_EXPLORE, new ExplorePage().restoreFromSharedPreferences());
        result.put(PageId.PAGE_CONTACT, new ContactPage().restoreFromSharedPreferences());
        result.put(PageId.PAGE_PERSONAL_INTRODUCTION, new PersonalIntroductionPage().restoreFromSharedPreferences());
        result.put(PageId.PAGE_LABEL_MEMBERS, new LabelMembersPage().restoreFromSharedPreferences());
//        result.put(PageId.PAGE_FRIEND, new FriendPage().restoreFromSharedPreferences());
//        result.put(PageId.PAGE_SELECT_RECEIVER, new SelectReceiverPage().restoreFromSharedPreferences());


        return result;
    }

    @NonNull
    private static Page whichPage(@NonNull AccessibilityNodeInfo rootInfo) {
        final Map<PageId, Page> allPageMap = getAllPages();
        for (Map.Entry<PageId, Page> entry : allPageMap.entrySet()) {
            final Page page = entry.getValue();
            if (page.isSelf(rootInfo)) {
                return page;
            }
        }

        return new UnknownPage();
    }


    @NonNull private final PageId mPageId;
    @NonNull private final String mPageName;

    @Nullable private PageFeature mPageFeature;


    public abstract String getNextImportViewDescription();
    public abstract boolean isImportViewResourceIdNameCaptured();
    public abstract boolean captureImportViewResourceIdName(@NonNull final AccessibilityEvent event);
    public abstract void saveAllImportViewResourceIdName();
    public abstract void restoreImportViewResourceIdNameFromCache();
    public abstract void bindData(@NonNull AccessibilityNodeInfo rootInfo);
    protected abstract SharedPreferencesUtils.STRING_CACHE getCacheEnumInstance();

    public void saveToSharedPreferences() {
        saveAllImportViewResourceIdName();

        if (mPageFeature == null) {
            return;
        }

        final String featureStr = GSON.toJson(mPageFeature);
        getCacheEnumInstance().put(featureStr);
    }

    public Page restoreFromSharedPreferences() {
        restoreImportViewResourceIdNameFromCache();

        final String featureStr = getCacheEnumInstance().get("{}");
        PageFeature feature = null;
        try {
            feature = GSON.fromJson(featureStr, PageFeature.class);
        } catch (Throwable e) {
            // ignore
        }

        if (feature != null) {
            mPageFeature = feature;
        }

        return this;
    }

    public AccessibilityNodeInfo findFromRootWithDesc(AccessibilityNodeInfo info, String desc) {
        if (info == null) {
            return null;
        }

        while (info.getParent() != null) {
            info = info.getParent();
        }

        return findFirstChildWithDesc(info, desc);
    }

    public AccessibilityNodeInfo findFromRootWithText(AccessibilityNodeInfo info, String text) {
        if (info == null) {
            return null;
        }

        while (info.getParent() != null) {
            info = info.getParent();
        }

        return findFirstChildWithText(info, text);
    }

    public AccessibilityNodeInfo findFirstChildWithText(AccessibilityNodeInfo info, String text) {
        if (info == null || TextUtils.isEmpty(text)) {
            return null;
        }

        String infoText = info.getText() == null ? "" : info.getText().toString();
        if (text.equals(infoText)) {
            return info;
        }

        int childCount = info.getChildCount();
        for (int i = 0; i < childCount; i++) {
            AccessibilityNodeInfo child = info.getChild(i);
            AccessibilityNodeInfo iInfo = findFirstChildWithText(child, text);
            if (iInfo != null) {
                return iInfo;
            }
        }

        return null;
    }

    public AccessibilityNodeInfo findFirstChildWithDesc(AccessibilityNodeInfo info, String desc) {
        if (info == null || TextUtils.isEmpty(desc)) {
            return null;
        }

        String infoDesc = info.getContentDescription() == null ? "" : info.getContentDescription().toString();
        if (desc.equals(infoDesc)) {
            return info;
        }

        int childCount = info.getChildCount();
        for (int i = 0; i < childCount; i++) {
            AccessibilityNodeInfo child = info.getChild(i);
            AccessibilityNodeInfo iInfo = findFirstChildWithDesc(child, desc);
            if (iInfo != null) {
                return iInfo;
            }
        }

        return null;
    }

    public AccessibilityNodeInfo findFirstClickable(AccessibilityNodeInfo info) {
        if (info == null) {
            return null;
        }

        if (info.isClickable()) {
            return info;
        }

        int childSize = info.getChildCount();
        for (int i = 0; i < childSize; i++) {
            AccessibilityNodeInfo child = info.getChild(i);
            AccessibilityNodeInfo clickableInfo = findFirstClickable(child);
            if (clickableInfo != null) {
                return clickableInfo;
            }
        }

        return null;
    }

    public AccessibilityNodeInfo findFirstParent(AccessibilityNodeInfo info, String className) {
        if (info == null || TextUtils.isEmpty(className)) {
            return null;
        }

        if (className.equals(info.getClassName() == null ? null : info.getClassName().toString())) {
            return info;
        }

        AccessibilityNodeInfo parentInfo = info.getParent();
        return findFirstParent(parentInfo, className);
    }

    public AccessibilityNodeInfo findFirstChild(AccessibilityNodeInfo info, String className) {
        return findFirstChild(info, className, true);
    }

    public AccessibilityNodeInfo findFirstChild(AccessibilityNodeInfo info, String className, boolean isFirstInvoke) {
        if (info == null || TextUtils.isEmpty(className)) {
            return null;
        }

        if (className.equals(info.getClassName() == null ? null : info.getClassName().toString())) {
            return info;
        }

        int childSize = info.getChildCount();
        if (childSize <= 0) {
            if (isFirstInvoke) {
                final AccessibilityNodeInfo parentInfo = info.getParent();
                return findFirstChild(parentInfo, className, false);
            }
        } else {
            for (int i = 0; i < childSize; i++) {
                AccessibilityNodeInfo child = info.getChild(i);
                AccessibilityNodeInfo iInfo = findFirstChild(child, className, false);
                if (iInfo != null) {
                    return iInfo;
                }
            }
        }

        return null;
    }

    protected Page(@NonNull PageId pageId, @NonNull String pageName) {
        mPageId = pageId;
        mPageName = pageName;
    }

    @NonNull public final String getPageName() {
        return mPageName;
    }

    @NonNull public final PageId getPageId() {
        return mPageId;
    }

    public void setPageFeautre(@NonNull final PageFeature pageFeautre) {
        mPageFeature = pageFeautre;
    }

    public void mergePageFeature(@NonNull final PageFeature pageFeature) {
        if (pageFeature == null) {
            return;
        }

        if (mPageFeature == null) {
            setPageFeautre(pageFeature);
            return;
        }

        mPageFeature.addAllFeature(pageFeature);
    }

    @Nullable
    public PageFeature getPageFeature() {
        return mPageFeature;
    }

    public int featureCount() {
        if (mPageFeature == null) {
            return 0;
        }

        return mPageFeature.size();
    }

    public boolean isPageReady() {
        return mPageFeature != null &&
                mPageFeature.getViewFeatures() != null &&
                !mPageFeature.getViewFeatures().isEmpty() &&
                isImportViewResourceIdNameCaptured();

    }

    public boolean isSelf(@Nullable final AccessibilityNodeInfo rootInfo) {
        if (rootInfo == null) {
            return false;
        }

        final PageFeature feature = PageUtils.gatherPageFeatures(rootInfo);
        if (feature == null) {
            return false;
        }

        return feature.containsIn(mPageFeature);
    }


    public enum PageId {
        PAGE_UNKNOWN,
        PAGE_WECHAT,
        PAGE_CONTACT,
        PAGE_EXPLORE,
        PAGE_SELF,
        PAGE_CHAT,
        PAGE_LABEL_MEMBERS,
        PAGE_PERSONAL_INTRODUCTION,
        PAGE_SELECT_RECEIVER,
        PAGE_FRIEND;
    }
}
