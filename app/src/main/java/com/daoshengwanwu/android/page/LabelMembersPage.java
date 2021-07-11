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

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class LabelMembersPage extends Page {
    private String mBackId;
    private String mListViewId;
    private String mLabelId;
    private String mTextId;

    private AccessibilityNodeInfo mBackInfo;
    private AccessibilityNodeInfo mListInfo;
    private AccessibilityNodeInfo mLabelInfo;
    private List<AccessibilityNodeInfo> mTextInfos;


    @Override
    public String getNextImportViewDescription() {
        if (TextUtils.isEmpty(mBackId) || "null".equals(mBackId)) {
            return "标签名编辑框";
        }

        if (TextUtils.isEmpty(mListViewId) || "null".equals(mListViewId)) {
            return "列表项";
        }

        if (TextUtils.isEmpty(mLabelId) || "null".equals(mLabelId)) {
            return "标签名编辑框";
        }

        if (TextUtils.isEmpty(mTextId) || "null".equals(mTextId)) {
            return "列表项";
        }

        return "success";
    }

    @Override
    public boolean isImportViewResourceIdNameCaptured() {
        return !TextUtils.isEmpty(mBackId) && !"null".equals(mBackId) &&
                !TextUtils.isEmpty(mListViewId) && !"null".equals(mListViewId) &&
                !TextUtils.isEmpty(mLabelId) && !"null".equals(mLabelId) &&
                !TextUtils.isEmpty(mTextId) && !"null".equals(mTextId);
    }

    @Override
    public boolean captureImportViewResourceIdName(@NonNull AccessibilityEvent event) {
        if (event == null || event.getEventType() != AccessibilityEvent.TYPE_VIEW_CLICKED) {
            return false;
        }

        if (TextUtils.isEmpty(mBackId) || "null".equals(mBackId)) {
            AccessibilityNodeInfo i = findFromRootWithDesc(AuxiliaryService.getServiceInstance().getRootInActiveWindow(), "返回");
            if (i != null) {
                mBackId = i.getParent().getViewIdResourceName();

                return mBackId != null;
            } else {
                return false;
            }
        }

        final AccessibilityNodeInfo info = event.getSource();
        if (info == null) {
            SingleSubThreadUtil.showToast(AuxiliaryService.getServiceInstance(), "请回到标签界面再次点击截取", Toast.LENGTH_LONG);
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
        } else if (TextUtils.isEmpty(mLabelId) || "null".equals(mLabelId)) {
            AccessibilityNodeInfo i = findFirstChild(info, "android.widget.EditText");
            if (i != null) {
                mLabelId = i.getViewIdResourceName();

                return mLabelId != null;
            } else {
                return false;
            }
        } else if (TextUtils.isEmpty(mTextId) || "null".equals(mTextId)) {
            AccessibilityNodeInfo i = findFirstChild(info, "android.widget.TextView");
            if (i != null) {
                mTextId = i.getViewIdResourceName();

                return mTextId != null;
            } else {
                return false;
            }
        }

        return true;
    }

    @Override
    public void saveAllImportViewResourceIdName() {
        final String idStr = TextUtils.join(",", new String[] {mBackId, mListViewId, mLabelId, mTextId});
        SharedPreferencesUtils.STRING_CACHE.LABEL_PAGE_VIEW_RESOURCE_ID_NAME.put(idStr);
    }

    @Override
    public void restoreImportViewResourceIdNameFromCache() {
        final String idStr = SharedPreferencesUtils.STRING_CACHE.LABEL_PAGE_VIEW_RESOURCE_ID_NAME.get("");
        if (TextUtils.isEmpty(idStr)) {
            return;
        }

        String[] splitStr = idStr.split(",");

        if (splitStr.length >= 1) {
            mBackId = splitStr[0];
        }

        if (splitStr.length >= 2) {
            mListViewId = splitStr[1];
        }

        if (splitStr.length >= 3) {
            mLabelId = splitStr[2];
        }

        if (splitStr.length >= 4) {
            mTextId = splitStr[3];
        }
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
        super(PageId.PAGE_LABEL_MEMBERS, "编辑标签");
    }

    @Override
    public void bindData(@NotNull AccessibilityNodeInfo rootInfo) {
        List<AccessibilityNodeInfo> rst;
        // 标签名字EditText
        rst = rootInfo.findAccessibilityNodeInfosByViewId(mLabelId);
        if (!CustomCollectionUtils.isListEmpty(rst)) {
            mLabelInfo = rst.get(0);
        }

        // 返回按钮的LinearLayout
        rst = rootInfo.findAccessibilityNodeInfosByViewId(mBackId);
        if (!CustomCollectionUtils.isListEmpty(rst)) {
            mBackInfo = rst.get(0);
        }

        // ListView
        rst = rootInfo.findAccessibilityNodeInfosByViewId(mListViewId);
        if (!CustomCollectionUtils.isListEmpty(rst)) {
            mListInfo = rst.get(0);
        }

        // 每一个item图片下边的TextView
        rst = rootInfo.findAccessibilityNodeInfosByViewId(mTextId);
        mTextInfos = rst;
    }

    @Override
    protected SharedPreferencesUtils.STRING_CACHE getCacheEnumInstance() {
        return SharedPreferencesUtils.STRING_CACHE.LABEL_MEMBERS_PAGE_FEATURE;
    }
}
