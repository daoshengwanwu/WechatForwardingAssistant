package com.daoshengwanwu.android.task;


import android.content.Context;
import android.os.SystemClock;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.daoshengwanwu.android.model.ShareData;
import com.daoshengwanwu.android.model.UserGroup;
import com.daoshengwanwu.android.model.item.UserItem;
import com.daoshengwanwu.android.page.*;
import com.daoshengwanwu.android.util.SingleSubThreadUtil;

import java.util.Iterator;
import java.util.Set;


public class ForwardingTask extends Task {
    private Context mContext;
    private Set<UserItem> mToForwardingSet;
    private UserGroup mUserGroup;
    private UserItem mCurSendingTarget;
    private int mCurScrollDirection = Direction.FORWARD;
    private boolean mIsTaskFinished = false;
    private boolean mIsForwrdingAlreadyStarted = false;
    private String mContent = "";
    private OnForwardingTaskFinishedListener mListener;
    private int mOriginCount;


    public ForwardingTask(
            @NonNull Context context,
            @NonNull UserGroup group,
            String content,
            OnForwardingTaskFinishedListener listener) {

        super(TaskId.TASK_FORWARDING);

        mContext = context;
        mUserGroup = group;
        mToForwardingSet = mUserGroup.getUserItems();
        mContent = content;
        mListener = listener;
        mOriginCount = group.getUserItems().size();
    }

    @Override
    public void execute(@NonNull AccessibilityNodeInfo rootInfo) {
        if (mIsTaskFinished) {
            return;
        }

        Page page = Page.generateFrom(rootInfo);

        if (!mIsForwrdingAlreadyStarted &&
                (page.getPageId() == Page.PageId.PAGE_WECHAT ||
                 page.getPageId() == Page.PageId.PAGE_CONTACT ||
                 page.getPageId() == Page.PageId.PAGE_EXPLORE)) {

            SingleSubThreadUtil.showToast(mContext,
                    "将在3秒后执行群发任务，如想撤销请离开微信界面并在设置中关闭群发工具", Toast.LENGTH_LONG);

            SystemClock.sleep(3000);
            mIsForwrdingAlreadyStarted = true;
            execute(rootInfo);
            return;
        }

        if (mOriginCount - mToForwardingSet.size() == 1) {
            ShareData.getInstance().pauseForwardingTask();
            SingleSubThreadUtil.showToast(mContext, "已自动暂停", Toast.LENGTH_LONG);
            mOriginCount = -1;
            return;
        }

        if (page.getPageId() == Page.PageId.PAGE_WECHAT) {
            WechatPage wechatPage = (WechatPage) page;
            wechatPage.switchToContactPage();

            return;
        }

        if (page.getPageId() == Page.PageId.PAGE_EXPLORE) {
            ExplorePage explorePage = (ExplorePage) page;
            explorePage.switchToContactPage();

            return;
        }

        if (page.getPageId() == Page.PageId.PAGE_CONTACT) {
            ContactPage contactPage = (ContactPage) page;

            ContactPage.FindResult findResult = contactPage.findFirstInfoInSpecificSet(mToForwardingSet);
            if (findResult != null) {
                SystemClock.sleep(100);
                if (findResult.info.performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
                    mCurSendingTarget = findResult.item;
                }
            }

            if (mToForwardingSet.isEmpty()) {
                SingleSubThreadUtil.showToast(mContext, "群发任务完成", Toast.LENGTH_SHORT);
                mIsTaskFinished = true;
                if (mListener != null) {
                    mListener.onForwardingTaskFinished();
                }
                return;
            }

            if (mCurScrollDirection == Direction.FORWARD) {
                if (contactPage.performForwardingScrollListView()) {
                    SystemClock.sleep(200);
                    return;
                }

                mCurScrollDirection = Direction.BACKWARD;
                execute(rootInfo);
            } else {
                if (contactPage.performBackwordScrollListView()) {
                    SystemClock.sleep(200);
                    return;
                }

                mCurScrollDirection = Direction.FORWARD;
                execute(rootInfo);
            }

            return;
        }

        if (page.getPageId()== Page.PageId.PAGE_PERSONAL_INTRODUCTION) {
            PersonalIntroductionPage personalIntroductionPage = (PersonalIntroductionPage) page;

            if (!personalIntroductionPage.getLabelText().contains(mCurSendingTarget.labelText)) {
                removeUserItemByFullnickname(mCurSendingTarget.fullNickName);
                personalIntroductionPage.performBack();
                return;
            }

            if (!personalIntroductionPage.performClickSendMessageInfo()) {
                personalIntroductionPage.bindData(rootInfo);
                execute(rootInfo);
            }

            return;
        }

        if (page.getPageId() == Page.PageId.PAGE_CHAT) {
            ChatPage chatPage = (ChatPage) page;

            String title = chatPage.getTitle();
            if (!title.equals(mCurSendingTarget.fullNickName)) {
                chatPage.performBack();
                return;
            }

            if (!chatPage.setEditTextText(getToSendText())) {
                chatPage.performBack();
                return;
            }

            if (chatPage.performClickSendButn(rootInfo)) {
                mToForwardingSet.remove(mCurSendingTarget);
            }

            chatPage.performBack();
        }
    }

    private String getToSendText() {
        return mContent.replaceAll("xing", mCurSendingTarget.surname).replaceAll("name", mCurSendingTarget.name);
    }

    private void removeUserItemByFullnickname(String fullNickname) {
        Iterator<UserItem> itemIterator = mToForwardingSet.iterator();

        while (itemIterator.hasNext()) {
            UserItem item = itemIterator.next();
            if (item.fullNickName.equals(fullNickname)) {
                itemIterator.remove();
                return;
            }
        }
    }

    private static final class Direction {
        public static final int FORWARD = 0;
        public static final int BACKWARD = 1;
    }

    public interface OnForwardingTaskFinishedListener {
        void onForwardingTaskFinished();
    }
}
