package com.daoshengwanwu.android.page;


import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.daoshengwanwu.android.model.PageFeature;
import com.daoshengwanwu.android.util.PageUtils;
import com.daoshengwanwu.android.util.SharedPreferencesUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;


public abstract class Page {
    private static final Gson GSON = new Gson();
    private static final List<Page> ALL_PAGE_INSTANCE = new ArrayList<>();


    @NonNull public static Page generateFrom(@NonNull AccessibilityNodeInfo rootInfo) {
        switch (whichPage(rootInfo)) {
            case PAGE_LABEL_MEMBERS: {
                return LabelMembersPage.generateFrom(rootInfo);
            }

            case PAGE_CHAT: {
                return ChatPage.generateFrom(rootInfo);
            }

            case PAGE_CONTACT: {
                return ContactPage.generateFrom(rootInfo);
            }

            case PAGE_EXPLORE: {
                return ExplorePage.generateFrom(rootInfo);
            }

            case PAGE_PERSONAL_INTRODUCTION: {
                return PersonalIntroductionPage.generateFrom(rootInfo);
            }

            case PAGE_WECHAT: {
                return WechatPage.generateFrom(rootInfo);
            }

            case PAGE_FRIEND: {
                return FriendPage.generateFrom(rootInfo);
            }

            case PAGE_SELECT_RECEIVER: {
                return SelectReceiverPage.generateFrom(rootInfo);
            }
        }

        return UnknownPage.generateFrom(rootInfo);
    }

    @NonNull
    private static PageId whichPage(@NonNull AccessibilityNodeInfo rootInfo) {
        // TODO::
        return PageId.PAGE_WECHAT;
//        if (LabelMembersPage.isSelf(rootInfo)) {
//            return PageId.PAGE_LABEL_MEMBERS;
//        } else if (ChatPage.isSelf(rootInfo)) {
//            return PageId.PAGE_CHAT;
//        } else if (ContactPage.isSelf(rootInfo)) {
//            return PageId.PAGE_CONTACT;
//        } else if (ExplorePage.isSelf(rootInfo)) {
//            return PageId.PAGE_EXPLORE;
//        } else if (PersonalIntroductionPage.isSelf(rootInfo)) {
//            return PageId.PAGE_PERSONAL_INTRODUCTION;
//        } else if (WechatPage.isSelf(rootInfo)) {
//            return PageId.PAGE_WECHAT;
//        } else if (FriendPage.isSelf(rootInfo)) {
//            return PageId.PAGE_FRIEND;
//        } else if (SelectReceiverPage.isSelf(rootInfo)) {
//            return PageId.PAGE_SELECT_RECEIVER;
//        }
//
//        return PageId.PAGE_UNKNOWN;
    }


    @NonNull private final PageId mPageId;
    @NonNull private final String mPageName;

    @Nullable private PageFeature mPageFeature;


    public abstract void bindData(@NonNull AccessibilityNodeInfo rootInfo);
    protected abstract SharedPreferencesUtils.STRING_CACHE getCacheEnumInstance();

    protected void saveToSharedPreferences() {
        if (mPageFeature == null) {
            return;
        }

        final String featureStr = GSON.toJson(mPageFeature);
        getCacheEnumInstance().put(featureStr);
    }

    protected void restoreFromSharedPreferences() {
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

    @Nullable
    public PageFeature getPageFeature() {
        return mPageFeature;
    }

    public boolean isPageReady() {
        return mPageFeature != null;
    }

    public boolean isSelf(@Nullable final AccessibilityNodeInfo rootInfo) {
        if (rootInfo == null) {
            return false;
        }

        final PageFeature feature = PageUtils.gatherPageFeatures(rootInfo);
        if (feature == null) {
            return false;
        }

        return feature.equalsExt(mPageFeature);
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
