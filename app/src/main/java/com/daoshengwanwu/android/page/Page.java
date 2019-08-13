package com.daoshengwanwu.android.page;


import android.view.accessibility.AccessibilityNodeInfo;
import androidx.annotation.NonNull;


public abstract class Page {
    @NonNull private final PageId mPageId;


    public abstract void bindData(@NonNull AccessibilityNodeInfo rootInfo);

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
        }

        return UnknownPage.generateFrom(rootInfo);
    }

    @NonNull private static PageId whichPage(@NonNull AccessibilityNodeInfo rootInfo) {
        if (LabelMembersPage.isSelf(rootInfo)) {
            return PageId.PAGE_LABEL_MEMBERS;
        } else if (ChatPage.isSelf(rootInfo)) {
            return PageId.PAGE_CHAT;
        } else if (ContactPage.isSelf(rootInfo)) {
            return PageId.PAGE_CONTACT;
        } else if (ExplorePage.isSelf(rootInfo)) {
            return PageId.PAGE_EXPLORE;
        } else if (PersonalIntroductionPage.isSelf(rootInfo)) {
            return PageId.PAGE_PERSONAL_INTRODUCTION;
        } else if (WechatPage.isSelf(rootInfo)) {
            return PageId.PAGE_WECHAT;
        }

        return PageId.PAGE_UNKNOWN;
    }

    protected Page(@NonNull PageId pageId) {
        mPageId = pageId;
    }

    @NonNull public final PageId getPageId() {
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
