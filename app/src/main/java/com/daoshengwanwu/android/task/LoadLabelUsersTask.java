package com.daoshengwanwu.android.task;


import android.content.Context;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.daoshengwanwu.android.model.item.UserItem;
import com.daoshengwanwu.android.page.LabelMembersPage;
import com.daoshengwanwu.android.page.Page;
import com.daoshengwanwu.android.util.SingleSubThreadUtil;

import java.util.HashSet;
import java.util.Set;


public class LoadLabelUsersTask extends Task {
    private boolean mIsTaskFinished = false;
    private boolean mIsLabelVerification = false;
    private String mLabelTitle;
    private Context mContext;
    private Set<UserItem> mToForwardingSet = new HashSet<>();

    private OnLabelUsersInfoLoadFinishedListener mOnLabelUsersInfoLoadFinishedListener;


    public LoadLabelUsersTask(
            @NonNull Context context,
            @NonNull String labelTitle,
            @NonNull OnLabelUsersInfoLoadFinishedListener listener) {

        super(TaskId.TASK_LOAD_LABEL_USERS);

        mContext = context;
        mLabelTitle = labelTitle;
        mOnLabelUsersInfoLoadFinishedListener = listener;
    }

    @Override
    public void execute(@NonNull AccessibilityNodeInfo rootInfo) {
        if (mIsTaskFinished) {
            return;
        }

        Page page = Page.generateFrom(rootInfo);

        if (page.getPageId() != Page.PageId.PAGE_LABEL_MEMBERS) {
            mIsLabelVerification = false;
            return;
        }

        LabelMembersPage lPage = (LabelMembersPage) page;

        if (!mIsLabelVerification) {
            // 验证下标签是否和界面中指定的相同
            String labelText = lPage.getLabelText();
            if (!mLabelTitle.equals(labelText)) {
                SingleSubThreadUtil.showToast(mContext, "当前标签页面与群发指定的不符，请切换到：" +
                        mLabelTitle + "：标签页面", Toast.LENGTH_SHORT);
                return;
            }

            mIsLabelVerification = true;
            mToForwardingSet.clear();
        }

        mToForwardingSet.addAll(lPage.getUserItems());

        boolean success = lPage.scrollListView_forward();
        if (!success) {
            mOnLabelUsersInfoLoadFinishedListener.onLabelUsersInfoLoadFinished(mToForwardingSet);
            mIsTaskFinished = true;
            lPage.back();
            SingleSubThreadUtil.showToast(mContext, "目标信息已读取完毕", Toast.LENGTH_SHORT);
            SingleSubThreadUtil.showToast(mContext, "目标信息：\n" + mToForwardingSet  + "\n 共 " +
                    mToForwardingSet.size() + " 人", 2000, Toast.LENGTH_SHORT);
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
