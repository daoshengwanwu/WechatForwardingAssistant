package com.daoshengwanwu.android.service;


import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;
import com.daoshengwanwu.android.R;
import com.daoshengwanwu.android.task.Task;

import java.util.List;


public class AuxiliaryService extends AccessibilityService {
    private static final String TAG = "AuxiliaryService";

    private Handler mHandler;
    private HandlerThread mHandlerThread = new HandlerThread("HandlerThread");


    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
        showToast("成功启动 " + getResources().getString(R.string.app_name) + ".", Toast.LENGTH_SHORT);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Toast.makeText(this, "getResources().getString(R.string.app_name) +\n" +
                "                \" 已关闭，如需再次使用请在设置中重新开启本插件.\"", Toast.LENGTH_SHORT).show();

        mHandler = null;
        mHandlerThread.quitSafely();

        return super.onUnbind(intent);
    }

    @Override
    public void onInterrupt() {
        //do nothing
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Task curActivatedTask = getCurActivatedTask();

        if (curActivatedTask == null) {
            return;
        }

        if (event.getEventType() != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED &&
            event.getEventType() != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED &&
            event.getEventType() != AccessibilityEvent.TYPE_VIEW_CLICKED) {

            return;
        }

        AccessibilityNodeInfo source = event.getSource();
        AccessibilityNodeInfo rootInfo = getRootInActiveWindow();

        if (source == null || rootInfo == null) {
            return;
        }

        curActivatedTask.execute(rootInfo);
    }

    private Task getCurActivatedTask() {
        // TODO::
        return null;
    }

    private void performLoadForwardingSet(AccessibilityEvent event, AccessibilityNodeInfo rootInfo,
                                          AccessibilityNodeInfo sourceInfo, int curPage) {


    }

    private void performForwarding(final AccessibilityEvent event, final AccessibilityNodeInfo rootInfo,
                                   final AccessibilityNodeInfo sourceInfo, final int curPage) {

        if (!mIsToForwardingSetLoaded) {
            return;
        }

        if (!mIsForwrdingAlreadyStarted &&
                (curPage == Page.PAGE_WECHAT || curPage == Page.PAGE_CONTACT || curPage == Page.PAGE_EXPLORE)) {

            showToast("将在3秒后执行群发任务，如想撤销请离开微信界面并在设置中关闭群发工具", Toast.LENGTH_LONG);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.d(TAG, "forwardingMessage: 线程sleep失败");
            }

            mIsForwrdingAlreadyStarted = true;
            onAccessibilityEvent(event);
        }

        List<AccessibilityNodeInfo> rst;

        if (curPage == Page.PAGE_WECHAT || curPage == Page.PAGE_EXPLORE) {
            rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/d99");
            if (isListEmpty(rst) || rst.size() != 4) {
                return;
            }

            rst.get(1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);

            return;
        }

        if (curPage == Page.PAGE_CONTACT) {
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

            if (mToForwardingSet.isEmpty()) {
                showToast("群发任务完成", Toast.LENGTH_SHORT);
                mToForwardingSet.clear();
                mIsForwrdingAlreadyStarted = false;
                mIsToForwardingSetLoaded = false;
                mIsLabelVerification = false;
                mShareData.clearData();
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

    private void performClean(AccessibilityEvent event, AccessibilityNodeInfo rootInfo,
                              AccessibilityNodeInfo sourceInfo, int curPage) {

    }

    private String getToSendContent() {
        String commonContent = mShareData.getContent();
        String xing = mCurSendingTarget.charAt(0) + "";
        String name = mCurSendingTarget.split("-")[0];

        commonContent = commonContent.replaceAll("xing", xing);
        commonContent = commonContent.replaceAll("name", name);

        return commonContent;
    }

    private int whereAmI(AccessibilityNodeInfo rootInfo) {
        if (isWechatPage(rootInfo)) {
            return Page.PAGE_WECHAT;
        }

        if (isContactPage(rootInfo)) {
            return Page.PAGE_CONTACT;
        }

        if (isExplorePage(rootInfo)) {
            return Page.PAGE_EXPLORE;
        }

        if (isSelfPage(rootInfo)) {
            return Page.PAGE_SELF;
        }

        if (isSearchForwardingPage(rootInfo)) {
            return Page.PAGE_SEARCH_FORWARDING;
        }

        if (isChatPage(rootInfo)) {
            return Page.PAGE_CHAT;
        }

        if (isChatWithCheckboxPage(rootInfo)) {
            return Page.PAGE_CHAT_WITH_CHECKBOX;
        }

        if (isLabelMembersPage(rootInfo)) {
            return Page.PAGE_LABEL_MEMBERS;
        }

        if (isPersonalIntroductionPage(rootInfo)) {
            return Page.PAGE_PERSONAL_INTRODUCTION;
        }

        return Page.PAGE_UNKNOWN;
    }

    private boolean isWechatPage(AccessibilityNodeInfo rootInfo) {
        List<AccessibilityNodeInfo> rst = rootInfo.findAccessibilityNodeInfosByViewId("android:id/text1");
        if (isListEmpty(rst)) {
            return false;
        }

        AccessibilityNodeInfo titleInfo = rst.get(0);
        String title = String.valueOf(titleInfo.getText());

        return title.startsWith("微信");
    }

    private boolean isContactPage(AccessibilityNodeInfo rootInfo) {
        List<AccessibilityNodeInfo> rst = rootInfo.findAccessibilityNodeInfosByViewId("android:id/text1");
        if (isListEmpty(rst)) {
            return false;
        }

        AccessibilityNodeInfo titleInfo = rst.get(0);
        String title = String.valueOf(titleInfo.getText());

        return title.startsWith("通讯录");
    }

    private boolean isExplorePage(AccessibilityNodeInfo rootInfo) {
        List<AccessibilityNodeInfo> rst = rootInfo.findAccessibilityNodeInfosByViewId("android:id/text1");
        if (isListEmpty(rst)) {
            return false;
        }

        AccessibilityNodeInfo titleInfo = rst.get(0);
        String title = String.valueOf(titleInfo.getText());

        return title.startsWith("发现");
    }

    private boolean isSelfPage(AccessibilityNodeInfo rootInfo) {
        return false;
    }

    private boolean isSearchForwardingPage(AccessibilityNodeInfo rootInfo) {
        List<AccessibilityNodeInfo> rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/l3");
        if (isListEmpty(rst)) {
            return false;
        }

        AccessibilityNodeInfo edittextInfo = rst.get(0);
        String searchContent = String.valueOf(edittextInfo.getText());
        if (!"群发".equals(searchContent)) {
            return false;
        }

        rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/aw4");
        if (isListEmpty(rst)) {
            return false;
        }

        for (AccessibilityNodeInfo info : rst) {
            String title = String.valueOf(info.getText());
            if ("联系人".equals(title) || "最常使用".equals(title)) {
                return true;
            }
        }

        return false;
    }

    private boolean isChatPage(AccessibilityNodeInfo rootInfo) {
        List<AccessibilityNodeInfo> rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/ev2");
        if (isListEmpty(rst)) {
            return false;
        }

        AccessibilityNodeInfo desInfo = rst.get(0).getParent();
        if (desInfo == null) {
            return false;
        }

        String description = String.valueOf(desInfo.getContentDescription());
        return description.startsWith("当前所在页面,与") && description.endsWith("的聊天");
    }

    private boolean isLabelMembersPage(AccessibilityNodeInfo rootInfo) {
        List<AccessibilityNodeInfo> rst = rootInfo.findAccessibilityNodeInfosByViewId("android:id/text1");
        if (isListEmpty(rst)) {
            return false;
        }
        AccessibilityNodeInfo titleInfo = rst.get(0);
        String title = String.valueOf(titleInfo.getText());
        if (!"编辑标签".equals(title)) {
            return false;
        }

        rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/ki");
        if (isListEmpty(rst)) {
            return false;
        }
        AccessibilityNodeInfo saveBtnInfo = rst.get(0);
        title = String.valueOf(saveBtnInfo.getText());
        if (!"保存".equals(title) || !saveBtnInfo.isClickable()) {
            return false;
        }

        return true;
    }

    private boolean isPersonalIntroductionPage(AccessibilityNodeInfo rootInfo) {
        List<AccessibilityNodeInfo> rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/b4m");
        if (isListEmpty(rst)) {
            return false;
        }

        rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/b4n");
        if (isListEmpty(rst)) {
            return false;
        }

        rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/b4t");
        if (isListEmpty(rst)) {
            return false;
        }
        String nickName = String.valueOf(rst.get(0).getText());
        if (TextUtils.isEmpty(nickName) || !nickName.startsWith("昵称:")) {
            return false;
        }

        rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/b4v");
        if (isListEmpty(rst)) {
            return false;
        }
        String wxidText = String.valueOf(rst.get(0).getText());
        if (TextUtils.isEmpty(wxidText) || !wxidText.startsWith("微信号:")) {
            return false;
        }

        return true;
    }

    private boolean isChatWithCheckboxPage(AccessibilityNodeInfo rootInfo) {
        return false;
    }

    private boolean isListEmpty(List lst) {
        return lst == null || lst.size() <= 0;
    }

    private void showToast(final String text, long delay, final int length) {
        if (mHandler == null) {
            return;
        }

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(AuxiliaryService.this, text, length).show();
            }
        }, delay);
    }

    private void showToast(String text, int length) {
        showToast(text, 0, length);
    }

    private static final class Page {
        public static final int PAGE_UNKNOWN = -1;
        public static final int PAGE_WECHAT = 0;
        public static final int PAGE_CONTACT = 1;
        public static final int PAGE_EXPLORE = 2;
        public static final int PAGE_SELF = 3;
        public static final int PAGE_SEARCH_FORWARDING = 4;
        public static final int PAGE_CHAT = 5;
        public static final int PAGE_CHAT_WITH_CHECKBOX = 6;
        public static final int PAGE_LABEL_MEMBERS = 7;
        public static final int PAGE_PERSONAL_INTRODUCTION = 8;
    }

    private static final class Direction {
        public static final int FORWARD = 0;
        public static final int BACKWARD = 1;
    }
}