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
import com.daoshengwanwu.android.service.AuxiliaryService;
import com.daoshengwanwu.android.util.ActionPerformer;
import com.daoshengwanwu.android.util.SingleSubThreadUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;


public class ForwardingTask extends Task {
    private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());

    private final ForwardingProcessActivity mContext; // 群发进度管理界面
    private final List<UserItem> mToForwardingList; // 要群发的用户列表
    private final List<String> mAlreadySentList = new ArrayList<>(); // 已经发送过的用户列表

    private final int mOriginCount; // 群发分组最一开始的size，也就是ForwardingTask实例创建的时候，群组的大小
    private final int mBundleSize; // 每mBundleSize暂停一次
    private final int mPauseTime; // 每mBundleSize暂停mPauseTime
    private final int mDeltaTime; // 两次发送消息间，最小间隔时间

    private UserItem mCurSendingTarget; // 当前正在发送的用户
    private int mCurScrollDirection = Direction.FORWARD; // 当前列表滑动方向
    private boolean mIsTaskFinished = false; // 群发是否执行完毕
    private boolean mIsForwrdingAlreadyStarted = false; // 是否已经提醒过群发3s内开始
    private String mContent = ""; // 要群发的内容
    private OnForwardingTaskFinishedListener mListener; // 监听群发进度的监听器
    private boolean mAlreadyPause; // 是否已经自动暂停过
    private long mLastForwardingTime; // 上一次发送消息的时间戳
    private Set<String> mSkipTitles = new HashSet<>();


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
        mBundleSize = bundleSize;
        mPauseTime = pauseTime;
        mDeltaTime = deltaTime;
        mToForwardingList = group.getUserItems();

        mContent = content;
        mListener = listener;
        mOriginCount = group.getUserItems().size();
    }

    @Override
    public void execute(@NonNull AccessibilityNodeInfo rootInfo) {
        if (mIsTaskFinished) {
            return;
        }

        final Page page = Page.generateFrom(rootInfo);
        if (page == null) {
            return;
        }

        if (!mIsForwrdingAlreadyStarted &&
                (page.getPageId() == Page.PageId.PAGE_WECHAT ||
                 page.getPageId() == Page.PageId.PAGE_CONTACT ||
                 page.getPageId() == Page.PageId.PAGE_EXPLORE)) {

            SingleSubThreadUtil.showToast(mContext,
                    "将在3秒后执行群发任务，如想撤销请离开微信界面并在设置中关闭群发工具", Toast.LENGTH_LONG);

            SystemClock.sleep(3000);

            mIsForwrdingAlreadyStarted = true;
            execute(AuxiliaryService.getServiceInstance().getRootInActiveWindow());
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
                if (alreadySent(result)) {
                    continue;
                }

                if (mSkipTitles.contains(result.item.fullNickName)) {
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
                    mSkipTitles.clear();
                    SystemClock.sleep(80);
                    return;
                }

                mCurScrollDirection = Direction.BACKWARD;
                execute(AuxiliaryService.getServiceInstance().getRootInActiveWindow());
            } else {
                if (contactPage.performBackwordScrollListView()) {
                    mSkipTitles.clear();
                    SystemClock.sleep(80);
                    return;
                }

                mCurScrollDirection = Direction.FORWARD;
                execute(AuxiliaryService.getServiceInstance().getRootInActiveWindow());
            }

            return;
        }

        if (page.getPageId()== Page.PageId.PAGE_PERSONAL_INTRODUCTION) {
            PersonalIntroductionPage personalIntroductionPage = (PersonalIntroductionPage) page;

            final String pageLabel = personalIntroductionPage.getLabelText();

            if (mCurSendingTarget == null) {
                return;
            }

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
                mSkipTitles.add(title);
                chatPage.performBack();
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

    private boolean alreadySent(ContactPage.FindResult result) {
        for (String name : mAlreadySentList) {
            try {
                if (name != null && name.equals(result.item.fullNickName)) {
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
