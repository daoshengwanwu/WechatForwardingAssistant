package com.daoshengwanwu.android.page;


import android.text.TextUtils;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;

import com.daoshengwanwu.android.model.item.UserItem;
import com.daoshengwanwu.android.util.ActionPerformer;
import com.daoshengwanwu.android.util.CustomCollectionUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;


public class ContactPage extends Page {
    private AccessibilityNodeInfo mListInfo;
    private List<AccessibilityNodeInfo> mContactInfos;


    public static boolean isSelf(@NonNull AccessibilityNodeInfo rootInfo) {
        // 左上角通讯录TextView
        List<AccessibilityNodeInfo> rst = rootInfo.findAccessibilityNodeInfosByViewId("android:id/text1");
        if (CustomCollectionUtils.isListEmpty(rst)) {
            return false;
        }

        AccessibilityNodeInfo titleInfo = rst.get(0);
        String title = ActionPerformer.getText(titleInfo, "ContactPage.isSelf::getTitle");

        return title.startsWith("通讯录");
    }

    public static ContactPage generateFrom(AccessibilityNodeInfo rootInfo) {
        ContactPage page = new ContactPage();

        page.bindData(rootInfo);

        return page;
    }

    private ContactPage() {
        super(PageId.PAGE_CONTACT);
    }

    @Override
    public void bindData(@NotNull AccessibilityNodeInfo rootInfo) {
        // 每一个item的有字的View
        mContactInfos = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/ft6");

        // ListView
        List<AccessibilityNodeInfo> rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/h4");
        if (!CustomCollectionUtils.isListEmpty(rst)) {
            mListInfo = rst.get(0);
        }
    }

    public boolean performForwardingScrollListView() {
        return ActionPerformer.performAction(
                mListInfo,
                AccessibilityNodeInfo.ACTION_SCROLL_FORWARD,
                "联系人界面执行FORWARD滑动事件");
    }

    public boolean performBackwordScrollListView() {
        return ActionPerformer.performAction(
                mListInfo,
                AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD,
                "联系人界面执行BACKWARD滑动事件");
    }

    public FindResult findFirstInfoInSpecificSet(List<UserItem> list) {
        if (mContactInfos == null) {
            return null;
        }

        for (AccessibilityNodeInfo info : mContactInfos) {
            String title = ActionPerformer.getText(info, "ContactPage.findFirstInfoInSpecificSet.getTitle");
            UserItem item = getByFullNickname(list, title);
            if (item != null) {
                return new FindResult(info.getParent().getParent(), item);
            }
        }

        return null;
    }

    public List<UserItem> findAllMatchUsers(Pattern pattern) {
        if (pattern == null || mContactInfos == null) {
            return new ArrayList<>();
        }

        List<UserItem> result = new ArrayList<>();
        for (AccessibilityNodeInfo info : mContactInfos) {
            String title = ActionPerformer.getText(info, "ContactPage.findAllMatchUsers");
            if (TextUtils.isEmpty(title)) {
                continue;
            }

            if (pattern.matcher(title).matches()) {
                result.add(new UserItem(title, ""));
            }
        }

        return result;
    }

    public List<FindResult> findAllInfo(List<UserItem> list, List<Pattern> regs) {
        if (mContactInfos == null) {
            return new ArrayList<>();
        }

        List<FindResult> results = new ArrayList<>();

        for (AccessibilityNodeInfo info : mContactInfos) {
            String title = ActionPerformer.getText(info, "ContactPage.findAllInfo");

            UserItem item;
            item = getByFullNickname(list, title);
            if (item == null && matches(title, regs)) {
                item = new UserItem(title, "");
            }

            if (item != null) {
                results.add(new FindResult(info.getParent().getParent(), item));
            }
        }

        return results;
    }

    private boolean matches(String title, List<Pattern> regs) {
        if (TextUtils.isEmpty(title) || regs == null) {
            return false;
        }

        for (Pattern pattern : regs) {
            try {
                if (pattern.matcher(title).matches()) {
                    return true;
                }
            } catch (Throwable e) {
                // ignore
            }
        }

        return false;
    }

    private boolean contains(Set<UserItem> userItemSet, String fullNickname) {
        if (userItemSet == null || fullNickname == null) {
            return false;
        }

        for (UserItem item : userItemSet) {
            if (fullNickname.equals(item.fullNickName)) {
                return true;
            }
        }

        return false;
    }

    private UserItem getByFullNickname(List<UserItem> userItems, String fullNickname) {
        if (userItems == null || fullNickname == null) {
            return null;
        }

        for (UserItem item : userItems) {
            if (item.fullNickName.equals(fullNickname)) {
                return item;
            }
        }

        return null;
    }


    public static final class FindResult {
        @NonNull public final AccessibilityNodeInfo info;
        @NonNull public final UserItem item;


        public FindResult(AccessibilityNodeInfo info, UserItem item) {
            this.info = info;
            this.item = item;
        }
    }
}
