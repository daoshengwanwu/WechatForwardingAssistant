package com.daoshengwanwu.android.page;


import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.daoshengwanwu.android.model.item.UserItem;
import com.daoshengwanwu.android.service.AuxiliaryService;
import com.daoshengwanwu.android.util.ActionPerformer;
import com.daoshengwanwu.android.util.CustomCollectionUtils;
import com.daoshengwanwu.android.util.CustomTextUtils;
import com.daoshengwanwu.android.util.SharedPreferencesUtils;
import com.daoshengwanwu.android.util.SingleSubThreadUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;


public class ContactPage extends Page {
    private String mListViewId;
    private String mListViewItemId;

    private AccessibilityNodeInfo mListInfo;
    private List<AccessibilityNodeInfo> mContactInfos;


    @Override
    public String getNextImportViewDescription() {
        if (TextUtils.isEmpty(mListViewId) || "null".equals(mListViewId)) {
            return "联系人项";
        }

        if (TextUtils.isEmpty(mListViewItemId) || "null".equals(mListViewItemId)) {
            return "联系人项";
        }

        return "success";
    }

    @Override
    public boolean isImportViewResourceIdNameCaptured() {
        return !TextUtils.isEmpty(mListViewId) && !"null".equals(mListViewId) &&
                !TextUtils.isEmpty(mListViewItemId) && !"null".equals(mListViewItemId);
    }

    @Override
    public boolean captureImportViewResourceIdName(@NonNull AccessibilityEvent event) {
        if (event == null || event.getEventType() != AccessibilityEvent.TYPE_VIEW_CLICKED) {
            return false;
        }

        final AccessibilityNodeInfo info = event.getSource();
        if (info == null) {
            SingleSubThreadUtil.showToast(AuxiliaryService.getServiceInstance(), "请回到通讯录界面再次点击截取", Toast.LENGTH_SHORT);
            return false;
        }

        if (TextUtils.isEmpty(mListViewId) || "null".equals(mListViewId)) {
            AccessibilityNodeInfo i = findFirstParent(info, "android.widget.ListView");
            if (i != null) {
                mListViewId = i.getViewIdResourceName();

                return mListViewId != null;
            } else {
                return false;
            }
        } else if (TextUtils.isEmpty(mListViewItemId) || "null".equals(mListViewItemId)) {
            AccessibilityNodeInfo i = findFirstChild(info, "android.widget.TextView");
            if (i != null) {
                mListViewItemId = i.getViewIdResourceName();

                return mListViewItemId != null;
            } else {
                return false;
            }
        }

        return true;
    }

    @Override
    public void saveAllImportViewResourceIdName() {
        final String idStr = TextUtils.join(",", new String[] {mListViewId, mListViewItemId});
        SharedPreferencesUtils.STRING_CACHE.CONTACT_PAGE_VIEW_RESOURCE_ID_NAME.put(idStr);
    }

    @Override
    public void restoreImportViewResourceIdNameFromCache() {
        final String idStr = SharedPreferencesUtils.STRING_CACHE.CONTACT_PAGE_VIEW_RESOURCE_ID_NAME.get("");
        if (TextUtils.isEmpty(idStr)) {
            return;
        }

        String[] splitStr = idStr.split(",");

        if (splitStr.length >= 1) {
            mListViewId = splitStr[0];
        }

        if (splitStr.length >= 2) {
            mListViewItemId = splitStr[1];
        }
    }

    public ContactPage() {
        super(PageId.PAGE_CONTACT, "通讯录");
    }

    @Override
    public void bindData(@NotNull AccessibilityNodeInfo rootInfo) {
        // 每一个item的有字的View
        mContactInfos = rootInfo.findAccessibilityNodeInfosByViewId(mListViewItemId);

        // ListView
        List<AccessibilityNodeInfo> rst = rootInfo.findAccessibilityNodeInfosByViewId(mListViewId);
        if (!CustomCollectionUtils.isListEmpty(rst)) {
            mListInfo = rst.get(0);
        }
    }

    @Override
    protected SharedPreferencesUtils.STRING_CACHE getCacheEnumInstance() {
        return SharedPreferencesUtils.STRING_CACHE.CONTACT_PAGE_FEATURE;
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
            UserItem item = getByInfoTitle(list, title);
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

    public List<FindResult> findAllInfo(List<UserItem> list) {
        if (mContactInfos == null) {
            return new ArrayList<>();
        }

        List<FindResult> results = new ArrayList<>();
        for (AccessibilityNodeInfo info : mContactInfos) {
            String title = ActionPerformer.getText(info, "ContactPage.findAllInfo");
            title = CustomTextUtils.getValidRemarkName(title);

            UserItem item;
            item = getByInfoTitle(list, title);

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

    private UserItem getByInfoTitle(List<UserItem> userItems, String title) {
        if (userItems == null || title == null) {
            return null;
        }

        UserItem fuzzyMatch = null; // 模糊匹配
        UserItem fullMatch = null; // 全量匹配
        for (UserItem item : userItems) {
            if (item.fullNickName == null) {
                continue;
            }

            if (fullMatch == null && item.fullNickName.equals(title)) {
                fullMatch = item;
            }

            if (fuzzyMatch == null && item.fullNickName.startsWith(title)) {
                fuzzyMatch = item;
            }
        }

        if (fullMatch != null) {
            return fullMatch;
        }

        return fuzzyMatch;
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
