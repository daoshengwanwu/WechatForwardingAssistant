package com.daoshengwanwu.android.task;


import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.daoshengwanwu.android.model.item.UserItem;
import com.daoshengwanwu.android.page.Page;

import java.util.List;
import java.util.Set;


public class LoadLabelUsersTask extends Task {
    private boolean mIsLabelVerification = false;

    private OnLabelUsersInfoLoadFinishedListener mOnLabelUsersInfoLoadFinishedListener;


    protected LoadLabelUsersTask(@NonNull OnLabelUsersInfoLoadFinishedListener listener) {
        super(TaskId.TASK_LOAD_LABEL_USERS);

        mOnLabelUsersInfoLoadFinishedListener = listener;
    }

    @Override
    public void execute(@NonNull AccessibilityNodeInfo rootInfo) {
        Page curPage = Page.generateFrom(rootInfo);

        if (curPage.getPageId() != Page.PageId.PAGE_LABEL_MEMBERS) {
            mIsLabelVerification = false;
            return;
        }

        List<AccessibilityNodeInfo> rst;
        if (!mIsLabelVerification) {
            // 验证下标签是否和界面中指定的相同
            rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/l3");
            if (isListEmpty(rst)) {
                showToast("当前标签页面与群发指定的不符，请切换到：" +
                        mShareData.getLabel() + "：标签页面", Toast.LENGTH_SHORT);
                Log.d(TAG, "performLoadForwardingSet: 当前标签页面与指定的标签：" + mShareData.getLabel() + " :不一致");
                return;
            }
            AccessibilityNodeInfo labelInfo = rst.get(0);
            String labelText = String.valueOf(labelInfo.getText());
            if (!mShareData.getLabel().equals(labelText)) {
                showToast("当前标签页面与群发指定的不符，请切换到：" +
                        mShareData.getLabel() + "：标签页面", Toast.LENGTH_SHORT);
                Log.d(TAG, "performLoadForwardingSet: 当前标签页面与指定的标签：" + mShareData.getLabel() + " :不一致");
                return;
            }

            mIsLabelVerification = true;
            mToForwardingSet.clear();
        }

        rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/kw");
        if (isListEmpty(rst)) {
            return;
        }
        AccessibilityNodeInfo backInfo = rst.get(0);

        rst = rootInfo.findAccessibilityNodeInfosByViewId("android:id/list");
        if (isListEmpty(rst)) {
            return;
        }
        AccessibilityNodeInfo listInfo = rst.get(0);

        rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/e42");
        for (AccessibilityNodeInfo textInfo : rst) {
            String title = String.valueOf(textInfo.getText());
            if (!TextUtils.isEmpty(title)) {
                mToForwardingSet.add(title);
            }
        }

        boolean success = listInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
        if (!success) {
            mIsLabelVerification = false;
            mIsToForwardingSetLoaded = true;
            backInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            showToast("目标信息已读取完毕", Toast.LENGTH_SHORT);
            showToast("目标信息：\n" + mToForwardingSet  + "\n 共 " +
                    mToForwardingSet.size() + " 人", 2000, Toast.LENGTH_SHORT);

            Log.d(TAG, "performLoadForwardingSet: toSet: " +
                    mToForwardingSet + "\n 共 " + mToForwardingSet.size() + " 人");
        }

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public interface OnLabelUsersInfoLoadFinishedListener {
        void onLabelUsersInfoLoadFinished(Set<UserItem> labelUsersInfo);
    }
}
