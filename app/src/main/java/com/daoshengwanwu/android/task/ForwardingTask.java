package com.daoshengwanwu.android.task;


import android.content.Context;
import android.os.SystemClock;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.daoshengwanwu.android.model.item.UserItem;
import com.daoshengwanwu.android.page.ContactPage;
import com.daoshengwanwu.android.page.ExplorePage;
import com.daoshengwanwu.android.page.Page;
import com.daoshengwanwu.android.page.WechatPage;
import com.daoshengwanwu.android.util.SingleSubThreadUtil;

import java.util.Set;


public class ForwardingTask extends Task {
    private Context mContext;
    private Set<UserItem> mToForwardingSet;
    private boolean mIsTaskFinished = false;
    private boolean mIsForwrdingAlreadyStarted = false;


    protected ForwardingTask(@NonNull Context context, @NonNull Set<UserItem> toForwardingSet) {
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

            rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/o1");
            for (AccessibilityNodeInfo info : rst) {
                String title = String.valueOf(info.getText());
                if (mToForwardingSet.contains(title)) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (!info.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
                        Log.d(TAG, "forwardingMessage: 点击通讯录item：" + title + " 失败");
                        break;
                    }

                    mCurSendingTarget = title;
                    return;
                }
            }

            AccessibilityNodeInfo info = contactPage.findFirstInfoInSpecificSet(mToForwardingSet);
            if (info != null) {
                SystemClock.sleep(100);
                if (info.performAction(AccessibilityNodeInfo.ACTION_CLICK)) {

                }
            }

            if (mToForwardingSet.isEmpty()) {
                SingleSubThreadUtil.showToast(mContext, "群发任务完成", Toast.LENGTH_SHORT);
                mIsTaskFinished = true;
                return;
            }

            rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/n3");
            if (isListEmpty(rst)) {
                Log.d(TAG, "forwardingMessage: 没有找到通讯录页面的ListView");
                return;
            }

            if (mCurScrollDirection == Direction.FORWARD) {
                if (rst.get(0).performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return;
                }

                mCurScrollDirection = Direction.BACKWARD;
                onAccessibilityEvent(event);
            } else {
                if (rst.get(0).performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD)) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return;
                }

                mCurScrollDirection = Direction.FORWARD;
                onAccessibilityEvent(event);
            }

            return;
        }

        if (curPage == Page.PAGE_PERSONAL_INTRODUCTION) {
            rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/kw");
            if (isListEmpty(rst)) {
                Log.d(TAG, "forwardingMessage: 没有找到Personal页面的后退按钮");
                return;
            }
            AccessibilityNodeInfo backInfo = rst.get(0);

            rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/dq7");
            if (isListEmpty(rst)) {
                backInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                return;
            }
            String labelText = String.valueOf(rst.get(0).getText());
            if (!labelText.contains(mShareData.getLabel())) {
                mToForwardingSet.remove(mCurSendingTarget);
                backInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                return;
            }

            rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/ct");
            if (isListEmpty(rst)) {
                backInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                return;
            }
            rst.get(0).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);

            return;
        }

        if (curPage == Page.PAGE_CHAT) {
            rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/km");
            if (isListEmpty(rst)) {
                Log.d(TAG, "forwardingMessage: 没有找到聊天页面的后退按钮");
                return;
            }
            AccessibilityNodeInfo backInfo = rst.get(0);

            rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/ko");
            if (isListEmpty(rst)) {
                backInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                return;
            }
            String title = String.valueOf(rst.get(0).getText());
            if (!title.equals(mCurSendingTarget)) {
                backInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                return;
            }

            rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/ami");
            if (isListEmpty(rst)) {
                backInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                return;
            }
            AccessibilityNodeInfo etInfo = rst.get(0);

            Bundle arguments = new Bundle();
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                    getToSendContent());

            if (!etInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)) {
                backInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                return;
            }

            rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/amp");
            if (isListEmpty(rst)) {
                backInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                return;
            }
            if (rst.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
                mToForwardingSet.remove(mCurSendingTarget);
            }

            backInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
    }
}
