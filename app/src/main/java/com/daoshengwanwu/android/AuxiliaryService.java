package com.daoshengwanwu.android;


import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class AuxiliaryService extends AccessibilityService {
    private static final String TAG = "AuxiliaryService";

    private Handler mHandler = new Handler();

    // 标志着当前正在进行着的任务
    private int mRunningTask = ShareData.Task.NONE;

    // 群发相关
    private boolean mIsForwrdingAlreadyStarted = false;
    private boolean mIsAlreadyDelayed3Seconds = false;
    private boolean mIsLabelVerification = false;
    private boolean mIsToForwardingSetLoaded = false;
    private String mCurSendingTarget = null;
    private int mCurScrollDirection = Direction.FORWARD;
    private final Set<String> mAlreadySendMsgSet = new HashSet<>();
    private final Set<String> mToForwardingSet = new HashSet<>();

    // 记录清理相关

    // 与UI共享的数据
    private ShareData mShareData = ShareData.getInstance();

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        Toast.makeText(this, "成功启动 " +
                getResources().getString(R.string.app_name) + ".", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Toast.makeText(this, getResources().getString(R.string.app_name) +
                " 已关闭，如需再次使用请在设置中重新开启本插件.", Toast.LENGTH_LONG).show();

        return super.onUnbind(intent);
    }


    @Override
    public void onInterrupt() {
        //do nothing
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        mRunningTask = mShareData.getActiveTask();
        if (mRunningTask == ShareData.Task.NONE) {
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

        int curPage = whereAmI(rootInfo);
        if (curPage == Page.PAGE_UNKNOWN) {
            return;
        }

        switch (mRunningTask) {
            case ShareData.Task.TASK_CLEAN: {
                performClean(event, rootInfo, source, curPage);
            } return;

            case ShareData.Task.TASK_FORWARDING: {
                performForwarding(event, rootInfo, source, curPage);
            } return;

            default: break;
        }
    }

    private void performForwarding(final AccessibilityEvent event, final AccessibilityNodeInfo rootInfo,
                                   final AccessibilityNodeInfo sourceInfo, final int curPage) {

        if (!mIsToForwardingSetLoaded) {
            performLoadForwardingSet(event, rootInfo, sourceInfo, curPage);
            return;
        }

        if (!mIsForwrdingAlreadyStarted &&
                (curPage == Page.PAGE_WECHAT || curPage == Page.PAGE_CONTACT || curPage == Page.PAGE_EXPLORE)) {

            mIsForwrdingAlreadyStarted = true;
            Toast.makeText(this, "将在3秒后开始执行群发任务，如想撤销请返回桌面", Toast.LENGTH_SHORT).show();

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mIsAlreadyDelayed3Seconds = true;
                    mCurScrollDirection = Direction.FORWARD;
                    mAlreadySendMsgSet.clear();
                    forwardingMessage(event, rootInfo, sourceInfo, curPage);
                }
            }, 3000);
        } else if (mIsAlreadyDelayed3Seconds) {
            forwardingMessage(event, rootInfo, sourceInfo, curPage);
        }
    }

    private void forwardingMessage(AccessibilityEvent event, AccessibilityNodeInfo rootInfo,
                                   AccessibilityNodeInfo sourceInfo, int curPage) {

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
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    info.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    mCurSendingTarget = title;
                    return;
                }
            }

            rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/n3");
            if (isListEmpty(rst)) {
                Toast.makeText(this, "请手动滑动列表以再次触发群发", Toast.LENGTH_SHORT).show();
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
                forwardingMessage(event, rootInfo, sourceInfo, curPage);
            } else {
                if (rst.get(0).performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD)) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return;
                }

                Toast.makeText(this, "群发任务完成", Toast.LENGTH_SHORT).show();
                mToForwardingSet.clear();
                mIsForwrdingAlreadyStarted = false;
                mIsAlreadyDelayed3Seconds = false;
                mIsToForwardingSetLoaded = false;
                mIsLabelVerification = false;
                mRunningTask = ShareData.Task.NONE;
                mShareData.clearData();
            }

            return;
        }

        if (curPage == Page.PAGE_PERSONAL_INTRODUCTION) {
            rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/kw");
            if (isListEmpty(rst)) {
                Toast.makeText(this, "请手动返回微信通讯录界面", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this, "请手动返回微信通讯录页面", Toast.LENGTH_SHORT).show();
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
                    mCurSendingTarget.charAt(0) + "老师, " + mShareData.getContent());

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

    private void performLoadForwardingSet(AccessibilityEvent event, AccessibilityNodeInfo rootInfo,
                                          AccessibilityNodeInfo sourceInfo, int curPage) {

        if (curPage != Page.PAGE_LABEL_MEMBERS) {
            mIsLabelVerification = false;
            return;
        }

        List<AccessibilityNodeInfo> rst;
        if (!mIsLabelVerification) {
            // 验证下标签是否和界面中指定的相同
            rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/l3");
            if (isListEmpty(rst)) {
                return;
            }
            AccessibilityNodeInfo labelInfo = rst.get(0);
            String labelText = String.valueOf(labelInfo.getText());
            if (!mShareData.getLabel().equals(labelText)) {
                Toast.makeText(this, "当前标签页面与群发指定的不符，请切换到：" +
                        mShareData.getLabel() + "：标签页面", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "目标信息已读取完毕", Toast.LENGTH_SHORT).show();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(AuxiliaryService.this,
                            "目标信息：\n" + mToForwardingSet  +
                                    "\n 共 " + mToForwardingSet.size() + " 人", Toast.LENGTH_LONG).show();
                }
            }, 2000);

            Log.d(TAG, "performLoadForwardingSet: toSet: " +
                    mToForwardingSet + "\n 共 " + mToForwardingSet.size() + " 人");
        }
    }

    private void performClean(AccessibilityEvent event, AccessibilityNodeInfo rootInfo,
                              AccessibilityNodeInfo sourceInfo, int curPage) {

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

    private void clearForwardingState() {
        mAlreadySendMsgSet.clear();
        mCurSendingTarget = null;
        mIsToForwardingSetLoaded = false;
        mToForwardingSet.clear();
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
