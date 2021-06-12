package com.daoshengwanwu.android.page;


import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;

import com.daoshengwanwu.android.model.item.UserItem;
import com.daoshengwanwu.android.util.ActionPerformer;
import com.daoshengwanwu.android.util.CustomCollectionUtils;
import com.daoshengwanwu.android.util.CustomTextUtils;
import com.daoshengwanwu.android.util.SharedPreferencesUtils;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class LabelMembersPage extends Page {
    private AccessibilityNodeInfo mBackInfo;
    private AccessibilityNodeInfo mListInfo;
    private AccessibilityNodeInfo mLabelInfo;
    private List<AccessibilityNodeInfo> mTextInfos;


    @NonNull public static LabelMembersPage generateFrom(@NonNull AccessibilityNodeInfo rootInfo) {
        LabelMembersPage page = new LabelMembersPage();

        page.bindData(rootInfo);

        return page;
    }

    @Override
    public String getNextImportViewDescription() {
        return null;
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

    public String getLabelText() {
        return ActionPerformer.getText(mLabelInfo, "标签界面获取LabelText");
    }

    public Set<UserItem> getUserItems(String labelText) {
        Set<UserItem> rst = new HashSet<>();

        for (AccessibilityNodeInfo info : mTextInfos) {
            String title = ActionPerformer.getText(info, "标签界面获取item的标签");
            title = CustomTextUtils.getValidRemarkName(title);
            if (!TextUtils.isEmpty(title)) {
                rst.add(new UserItem(title, labelText));
            }
        }

        return rst;
    }

    public boolean scrollListView_forward() {
        return ActionPerformer.performAction(
                mListInfo,
                AccessibilityNodeInfo.ACTION_SCROLL_FORWARD,
                "标签界面执行FORWARD滚动事件");
    }

    public void back() {
        ActionPerformer.performAction(
                mBackInfo,
                AccessibilityNodeInfo.ACTION_CLICK,
                "标签界面点击后退按钮");
    }

    public LabelMembersPage() {
        super(PageId.PAGE_LABEL_MEMBERS, "标签详情");
    }

    @Override
    public void bindData(@NotNull AccessibilityNodeInfo rootInfo) {
        List<AccessibilityNodeInfo> rst;
        // 标签名字EditText
        rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/bxz");
        if (!CustomCollectionUtils.isListEmpty(rst)) {
            mLabelInfo = rst.get(0);
        }

        // 返回按钮的LinearLayout
        rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/eh");
        if (!CustomCollectionUtils.isListEmpty(rst)) {
            mBackInfo = rst.get(0);
        }

        // ListView
        rst = rootInfo.findAccessibilityNodeInfosByViewId("android:id/list");
        if (!CustomCollectionUtils.isListEmpty(rst)) {
            mListInfo = rst.get(0);
        }

        // 每一个item图片下边的TextView
        rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/h8q");
        mTextInfos = rst;
    }

    @Override
    protected SharedPreferencesUtils.STRING_CACHE getCacheEnumInstance() {
        return SharedPreferencesUtils.STRING_CACHE.LABEL_MEMBERS_PAGE_FEATURE;
    }
}
