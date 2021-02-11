package com.daoshengwanwu.android.task;


import android.content.Context;
import android.os.SystemClock;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;

import com.daoshengwanwu.android.model.ShareData;
import com.daoshengwanwu.android.model.item.UserItem;
import com.daoshengwanwu.android.page.ContactPage;
import com.daoshengwanwu.android.page.Page;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;


public class RegLoadUsersTask extends Task {
    private final Context mContext;
    private final Pattern mPattern;
    private final Set<UserItem> mLoadedUserItems = new HashSet<>();
    private final OnUsersInfoLoadFinishedListener mListener;

    private boolean mHasBackwordScrollFinished = false;


    public RegLoadUsersTask(Context context, Pattern pattern, OnUsersInfoLoadFinishedListener listener) {
        super(TaskId.TASK_LOAD_USERS_BY_REG);

        mContext = context;
        mPattern = pattern;
        mListener = listener;
    }

    @Override
    public void execute(@NonNull AccessibilityNodeInfo rootInfo) {
        if (mContext == null || mPattern == null || mListener == null) {
            return;
        }

        Page curPage = Page.generateFrom(rootInfo);
        if (!(curPage instanceof ContactPage)) {
            return;
        }

        ContactPage contactPage = (ContactPage) curPage;
        mLoadedUserItems.addAll(contactPage.findAllMatchUsers(mPattern));
        SystemClock.sleep(25);
        if (!mHasBackwordScrollFinished && !contactPage.performBackwordScrollListView()) {
            mHasBackwordScrollFinished = true;
        }

        if (mHasBackwordScrollFinished && !contactPage.performForwardingScrollListView()) {
            mListener.onUsersInfoLoadFinished(mLoadedUserItems);
            ShareData.getInstance().clearData();
        }
    }


    public interface OnUsersInfoLoadFinishedListener {
        void onUsersInfoLoadFinished(Set<UserItem> labelUsersInfo);
    }
}
