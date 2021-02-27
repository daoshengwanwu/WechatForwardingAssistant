package com.daoshengwanwu.android.task;


import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.daoshengwanwu.android.activity.ForwardingProcessActivity;
import com.daoshengwanwu.android.model.ShareData;
import com.daoshengwanwu.android.model.UserGroup;
import com.daoshengwanwu.android.model.item.UserItem;
import com.daoshengwanwu.android.page.ChatPage;
import com.daoshengwanwu.android.page.ContactPage;
import com.daoshengwanwu.android.page.ExplorePage;
import com.daoshengwanwu.android.page.Page;
import com.daoshengwanwu.android.page.PersonalIntroductionPage;
import com.daoshengwanwu.android.page.WechatPage;
import com.daoshengwanwu.android.util.ActionPerformer;
import com.daoshengwanwu.android.util.SingleSubThreadUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;


public class ForwardingTask extends Task {
    private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());

    private ForwardingProcessActivity mContext; // 群发进度管理界面
    private List<UserItem> mToForwardingList; // 指定的要群发的用户
    private List<String> mAlreadySentList = new ArrayList<>(); // 已经发送过的人的名单
    private UserGroup mUserGroup;
    private UserItem mCurSendingTarget; // 当前正在发送的用户
    private int mCurScrollDirection = Direction.FORWARD;
    private boolean mIsTaskFinished = false;
    private boolean mIsForwrdingAlreadyStarted = false;
    private String mContent = "";
    private OnForwardingTaskFinishedListener mListener;
    private boolean mAlreadyPause;
    private long mLastForwardingTime;
    private final int mOriginCount;
    private final int mBundleSize;
    private final int mPauseTime;
    private final int mDeltaTime;


    public ForwardingTask(
            @NonNull ForwardingProcessActivity context,
            @NonNull UserGroup group,
            List<Pattern> regPatterns,
            int bundleSize,
            int pauseTime,
            int deltaTime,
            String content,
            OnForwardingTaskFinishedListener listener) {

        super(TaskId.TASK_FORWARDING);

        mContext = context;
        mUserGroup = group;
        mBundleSize = bundleSize;
        mPauseTime = pauseTime;
        mDeltaTime = deltaTime;
        mToForwardingList = mUserGroup.getUserItems();

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

        if (!mAlreadyPause && (mOriginCount - mToForwardingList.size() == 1) && mToForwardingList.size() > 0) {
            ShareData.getInstance().pauseForwardingTask();
            SingleSubThreadUtil.showToast(mContext, "已自动暂停", Toast.LENGTH_LONG);
            mAlreadyPause = true;
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

            ContactPage.FindResult findResult = null;
            final List<ContactPage.FindResult> findResults = contactPage.findAllInfo(mToForwardingList);
            for (ContactPage.FindResult result : findResults) {
                if (containsResult(result)) {
                    continue;
                }

                findResult = result;
                break;
            }

            if (findResult != null) {
                SystemClock.sleep(80);
                if (ActionPerformer.performAction(findResult.info, AccessibilityNodeInfo.ACTION_CLICK, "对联系人界面的item执行点击")) {
                    mCurSendingTarget = findResult.item;
                    return;
                }
            }

            if (mToForwardingList.isEmpty()) {
                SingleSubThreadUtil.showToast(mContext, "群发任务完成", Toast.LENGTH_SHORT);
                mIsTaskFinished = true;
                if (mListener != null) {
                    mListener.onForwardingTaskFinished();
                }

                return;
            }

            if (mCurScrollDirection == Direction.FORWARD) {
                if (contactPage.performForwardingScrollListView()) {
                    SystemClock.sleep(80);
                    return;
                }

                mCurScrollDirection = Direction.BACKWARD;
                execute(rootInfo);
            } else {
                if (contactPage.performBackwordScrollListView()) {
                    SystemClock.sleep(80);
                    return;
                }

                mCurScrollDirection = Direction.FORWARD;
                execute(rootInfo);
            }

            return;
        }

        if (page.getPageId()== Page.PageId.PAGE_PERSONAL_INTRODUCTION) {
            PersonalIntroductionPage personalIntroductionPage = (PersonalIntroductionPage) page;

            final String pageLabel = personalIntroductionPage.getLabelText();

            if (!pageLabel.contains(mCurSendingTarget.labelText) && !TextUtils.isEmpty(mCurSendingTarget.labelText)) {
                removeUserItemByFullnickname(mCurSendingTarget.fullNickName);
                personalIntroductionPage.performBack();
                return;
            }

            if (!personalIntroductionPage.performClickSendMessageInfo()) {
                personalIntroductionPage.bindData(rootInfo);
                personalIntroductionPage.performClickSendMessageInfo();
            }

            return;
        }

        if (page.getPageId() == Page.PageId.PAGE_CHAT) {
            ChatPage chatPage = (ChatPage) page;

            String title = chatPage.getTitle();
            if (mCurSendingTarget == null || !title.equals(mCurSendingTarget.fullNickName)) {
                return;
            }

            if (!chatPage.setEditTextText(getToSendText())) {
                return;
            }

            long delta = System.currentTimeMillis() - mLastForwardingTime;
            if (delta < mDeltaTime * 1000L) {
                SystemClock.sleep(mDeltaTime * 1000L - delta);
            }

            if (!chatPage.performClickSendButn(rootInfo)) {
                return;
            }

            mLastForwardingTime = System.currentTimeMillis();
            mToForwardingList.remove(mCurSendingTarget);
            mAlreadySentList.add(mCurSendingTarget.fullNickName);
            mCurSendingTarget = null;
            chatPage.performBack();

            int alreadySendNum = mOriginCount - mToForwardingList.size();
            if (mBundleSize > 0 && alreadySendNum % mBundleSize == 0) {
                mContext.pauseForwarding();
                SingleSubThreadUtil.showToast(mContext, "已自动暂停, 将在" + mPauseTime + "秒后自动恢复，或者手动点击继续", Toast.LENGTH_LONG);

                MAIN_HANDLER.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mContext.resumeForwarding();
                    }
                }, mPauseTime * 1000L);
            }
        }
    }

    private boolean containsResult(ContactPage.FindResult result) {
        for (String name : mAlreadySentList) {
            try {
                if (name != null && name.startsWith(result.item.fullNickName)) {
                    return true;
                }
            } catch (Throwable e) {
                // ignore
            }
        }

        return false;
    }

    private String getToSendText() {
        return mContent
                .replaceAll("(?<!\\\\)xing", mCurSendingTarget.surname)
                .replaceAll("(?<!\\\\)name", mCurSendingTarget.name)
                .replaceAll("\\\\name", "name")
                .replaceAll("\\\\xing", "xing");
    }

    private void removeUserItemByFullnickname(String fullNickname) {
        Iterator<UserItem> itemIterator = mToForwardingList.iterator();

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
