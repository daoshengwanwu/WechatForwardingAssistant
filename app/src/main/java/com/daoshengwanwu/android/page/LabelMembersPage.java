package com.daoshengwanwu.android.page;


import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.daoshengwanwu.android.model.item.UserItem;
import com.daoshengwanwu.android.util.CustomCollectionUtils;
import com.daoshengwanwu.android.util.SingleSubThreadUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class LabelMembersPage extends Page {
    private AccessibilityNodeInfo mBackInfo;
    private AccessibilityNodeInfo mListInfo;
    private AccessibilityNodeInfo mLabelInfo;
    private List<AccessibilityNodeInfo> mTextInfos;


    public static boolean isSelf(@NonNull AccessibilityNodeInfo rootInfo) {
        List<AccessibilityNodeInfo> rst = rootInfo.findAccessibilityNodeInfosByViewId("android:id/text1");
        if (CustomCollectionUtils.isListEmpty(rst)) {
            return false;
        }
        AccessibilityNodeInfo titleInfo = rst.get(0);
        String title = String.valueOf(titleInfo.getText());
        if (!"编辑标签".equals(title)) {
            return false;
        }

        rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/ki");
        if (CustomCollectionUtils.isListEmpty(rst)) {
            return false;
        }
        AccessibilityNodeInfo saveBtnInfo = rst.get(0);
        title = String.valueOf(saveBtnInfo.getText());

        return "保存".equals(title) && saveBtnInfo.isClickable();
    }

    @NonNull public static LabelMembersPage generateFrom(@NonNull Context context, @NonNull AccessibilityNodeInfo rootInfo) {
        LabelMembersPage page = new LabelMembersPage();

        try {
            page.bindData(rootInfo);
        } catch (RuntimeException e) {
            SingleSubThreadUtil.showToast(context, "出现意外错误，为避免行为不可控，程序自行终止", Toast.LENGTH_LONG);
            SystemClock.sleep(3000);
            throw e;
        }

        return page;
    }

    public String getLabelText() {
        return mLabelInfo == null ? "" : mLabelInfo.getText() + "";
    }

    public Set<UserItem> getUserItems() {
        Set<UserItem> rst = new HashSet<>();

        for (AccessibilityNodeInfo info : mTextInfos) {
            String title = info.getText() + "";
            if (!TextUtils.isEmpty(title)) {
                rst.add(new UserItem(title));
            }
        }

        return rst;
    }

    public boolean scrollListView_forward() {
        return mListInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
    }

    public void back() {
        mBackInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
    }


    private LabelMembersPage() {
        super(PageId.PAGE_LABEL_MEMBERS);
    }

    @Override
    public void bindData(AccessibilityNodeInfo rootInfo) {
        List<AccessibilityNodeInfo> rst;
        rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/l3");
        if (!CustomCollectionUtils.isListEmpty(rst)) {
            mLabelInfo = rst.get(0);
        }

        rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/kw");
        if (!CustomCollectionUtils.isListEmpty(rst)) {
            mBackInfo = rst.get(0);
        }

        rst = rootInfo.findAccessibilityNodeInfosByViewId("android:id/list");
        if (!CustomCollectionUtils.isListEmpty(rst)) {
            mListInfo = rst.get(0);
        }

        rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/e42");
        mTextInfos = rst;

        if (mBackInfo == null || mListInfo == null || mTextInfos == null) {
            throw new RuntimeException("出现意外错误，为避免行为不可控，程序自行终止");
        }
    }
}
