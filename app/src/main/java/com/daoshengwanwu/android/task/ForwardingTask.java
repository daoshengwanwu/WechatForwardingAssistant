package com.daoshengwanwu.android.task;


import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.daoshengwanwu.android.model.item.UserItem;
import com.daoshengwanwu.android.page.*;
import com.daoshengwanwu.android.util.SingleSubThreadUtil;

import java.util.Iterator;
import java.util.Set;


public class ForwardingTask extends Task {
    private Context mContext;
    private Set<UserItem> mToForwardingSet;
    private UserItem mCurSendingTarget;
    private int mCurScrollDirection = Direction.FORWARD;
    private boolean mIsTaskFinished = false;
    private boolean mIsForwrdingAlreadyStarted = false;


    public ForwardingTask(@NonNull Context context, @NonNull Set<UserItem> toForwardingSet) {
        super(TaskId.TASK_FORWARDING);

        mContext = context;
        mToForwardingSet = toForwardingSet;
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

            if (!personalIntroductionPage.isLabelTextInToForwardingSet(mToForwardingSet)) {
                removeUserItemByFullnickname(personalIntroductionPage.getLabelText());
                personalIntroductionPage.performBack();
                return;
            }

            personalIntroductionPage.performClickSendMessageInfo();

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

            if (chatPage.performClickSendButn()) {
                mToForwardingSet.remove(mCurSendingTarget);
            }

            chatPage.performBack();
        }
    }

    private String getToSendText() {
        // TODO::
        return "";
        /*
        String commonContent = mShareData.getContent();
        String xing = mCurSendingTarget.charAt(0) + "";
        String name = mCurSendingTarget.split("-")[0];

        commonContent = commonContent.replaceAll("xing", xing);
        commonContent = commonContent.replaceAll("name", name);

        return commonContent;
         */
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
}
