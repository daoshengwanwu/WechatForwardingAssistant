package com.daoshengwanwu.android.task;


import android.content.Context;
import android.os.SystemClock;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.daoshengwanwu.android.model.item.UserItem;
import com.daoshengwanwu.android.page.LabelMembersPage;
import com.daoshengwanwu.android.page.Page;
import com.daoshengwanwu.android.util.SingleSubThreadUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

        mToForwardingSet.addAll(lPage.getUserItems(mLabelTitle));

        boolean success = lPage.scrollListView_forward();
        if (!success) {
            mOnLabelUsersInfoLoadFinishedListener.onLabelUsersInfoLoadFinished(mToForwardingSet);
            mIsTaskFinished = true;
            lPage.back();
        }

        SystemClock.sleep(50);
    }


    public interface OnLabelUsersInfoLoadFinishedListener {
        void onLabelUsersInfoLoadFinished(Set<UserItem> labelUsersInfo);
    }
}
