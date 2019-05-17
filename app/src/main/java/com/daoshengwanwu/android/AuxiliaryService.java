package com.daoshengwanwu.android;


import android.accessibilityservice.AccessibilityService;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
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

    // 标志着当前正在进行着的任务
    private int mRunningTask = Task.NONE;

    // 群发相关
    private String mCurSendingTarget = null;
    private final Set<String> mAlreadySendMsgSet = new HashSet<>();
    private boolean mIsMoreEverClicked = false;

    // 记录清理相关


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
        Log.d(TAG, "onAccessibilityEvent: eventType" + event.getEventType());
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

        if (mRunningTask == Task.TASK_FORWARDING &&
                curPage != Page.PAGE_SEARCH_FORWARDING && curPage != Page.PAGE_CHAT) {

            mRunningTask = Task.NONE;
        }

        switch (mRunningTask) {
            case Task.NONE: {
                if (isForwardingTaskFuse(rootInfo)) {
                    mRunningTask = Task.TASK_FORWARDING;
                    clearForwardingState();
                    onAccessibilityEvent(event);
                } else if (isCleanTaskFuse(rootInfo)) {
                    mRunningTask = Task.TASK_CLEAN;
                    onAccessibilityEvent(event);
                }
            } return;

            case Task.TASK_CLEAN: {

            } return;

            case Task.TASK_FORWARDING: {
                if (curPage == Page.PAGE_SEARCH_FORWARDING) {
                    List<AccessibilityNodeInfo> backRst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/l0");
                    if (backRst == null || backRst.size() != 1) {
                        mRunningTask = Task.NONE;
                        clearForwardingState();
                        return;
                    }

                    List<AccessibilityNodeInfo> labelRst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/ld");
                    if (labelRst == null || labelRst.size() <= 0) {
                        mRunningTask = Task.NONE;
                        clearForwardingState();
                        return;
                    }

                    List<AccessibilityNodeInfo> listviewRst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/bxr");
                    if (listviewRst == null || listviewRst.size() != 1) {
                        mRunningTask = Task.NONE;
                        clearForwardingState();
                        return;
                    }

                    AccessibilityNodeInfo backInfo = backRst.get(0);
                    AccessibilityNodeInfo listviewInfo = listviewRst.get(0);

                    for (AccessibilityNodeInfo info : labelRst) {
                        if (!"标签: 群发".equals(String.valueOf(info.getText()))) {
                            continue;
                        }

                        AccessibilityNodeInfo parent = info.getParent();
                        if (parent == null) {
                            continue;
                        }

                        AccessibilityNodeInfo nickNameInfo = parent.getChild(0);
                        if (nickNameInfo.getText() == null){
                            continue;
                        }

                        String nickName = nickNameInfo.getText().toString();
                        if (TextUtils.isEmpty(nickName) || mAlreadySendMsgSet.contains(nickName)) {
                            continue;
                        }

                        boolean success = parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        if (success) {
                            mCurSendingTarget = nickName;
                            return;
                        }
                    }

                    List<AccessibilityNodeInfo> moreRst = rootInfo.findAccessibilityNodeInfosByText("更多联系人");
                    if (isListEmpty(moreRst)) {
                        boolean success = listviewInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                        if (success) {
                            return;
                        }

                        mRunningTask = Task.NONE;
                        clearForwardingState();
                        backInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        Toast.makeText(this, "已完成转发任务.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    AccessibilityNodeInfo moreInfo = moreRst.get(0).getParent();
                    if (!mIsMoreEverClicked) {
                        boolean success = moreInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        if (success) {
                            mIsMoreEverClicked = true;
                        } else {
                            Toast.makeText(this, "请尝试手动滑动一下屏幕.", Toast.LENGTH_SHORT).show();
                        }

                        return;
                    } else {
                        backInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        Toast.makeText(this, "已完成转发任务.", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    List<AccessibilityNodeInfo> rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/km");
                    if (rst == null || rst.size() <= 0) {
                        Toast.makeText(this, "请手动返回上一个页面.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    AccessibilityNodeInfo backInfo = rst.get(0);

                    rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/ko");
                    if (rst == null || rst.size() <= 0) {
                        if (!backInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
                            Toast.makeText(this, "请手动返回上一个页面.", Toast.LENGTH_SHORT).show();
                        }
                        return;
                    }

                    AccessibilityNodeInfo nickNameInfo = rst.get(0);
                    if (!mCurSendingTarget.equals(String.valueOf(nickNameInfo.getText()))) {
                        if (!backInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
                            Toast.makeText(this, "请手动返回上一个页面.", Toast.LENGTH_SHORT).show();
                        }
                        return;
                    }

                    rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/ami");
                    if (rst == null || rst.size() <= 0) {
                        if (!backInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
                            Toast.makeText(this, "请手动返回上一个页面.", Toast.LENGTH_SHORT).show();
                        }
                        return;
                    }

                    AccessibilityNodeInfo edittextInfo = rst.get(0);

                    String clipboardMsg = getClipboardMessage();
                    if (TextUtils.isEmpty(clipboardMsg)) {
                        if (!backInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
                            Toast.makeText(this, "请手动返回上一个页面.", Toast.LENGTH_SHORT).show();
                        }
                        return;
                    }

                    Bundle data = new Bundle();
                    data.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                            mCurSendingTarget.charAt(0) + "老师，" + clipboardMsg);
                    boolean success = edittextInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, data);
                    if (!success) {
                        if (!backInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
                            Toast.makeText(this, "请手动返回上一个页面.", Toast.LENGTH_SHORT).show();
                        }
                        return;
                    }

                    rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/amp");
                    if (rst == null || rst.size() <= 0) {
                        if (!backInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
                            Toast.makeText(this, "请手动返回上一个页面.", Toast.LENGTH_SHORT).show();
                        }
                        return;
                    }

                    AccessibilityNodeInfo sendbtnInfo = rst.get(0);
                    success = sendbtnInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    if (success) {
                        mAlreadySendMsgSet.add(mCurSendingTarget);
                    }
                    if (!backInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
                        Toast.makeText(this, "请手动返回上一个页面.", Toast.LENGTH_SHORT).show();
                    }
                }
            } return;

            default: break;
        }
    }

    private String getClipboardMessage() {
        ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData data = cm.getPrimaryClip();

        return data == null ? "" : data.getItemAt(0).getText().toString();
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

        return Page.PAGE_UNKNOWN;
    }

    private boolean isWechatPage(AccessibilityNodeInfo rootInfo) {
        List<AccessibilityNodeInfo> rst = rootInfo.findAccessibilityNodeInfosByViewId("android:id/text1");
        if (isListEmpty(rst)) {
            return false;
        }

        AccessibilityNodeInfo titleInfo = rst.get(0);
        if (titleInfo.getText() == null) {
            return false;
        }

        String title = titleInfo.getText().toString();

        return title.startsWith("微信");
    }

    private boolean isContactPage(AccessibilityNodeInfo rootInfo) {
        List<AccessibilityNodeInfo> rst = rootInfo.findAccessibilityNodeInfosByViewId("android:id/text1");
        if (isListEmpty(rst)) {
            return false;
        }

        AccessibilityNodeInfo titleInfo = rst.get(0);
        if (titleInfo.getText() == null) {
            return false;
        }

        String title = titleInfo.getText().toString();

        return title.startsWith("通讯录");
    }

    private boolean isExplorePage(AccessibilityNodeInfo rootInfo) {
        List<AccessibilityNodeInfo> rst = rootInfo.findAccessibilityNodeInfosByViewId("android:id/text1");
        if (isListEmpty(rst)) {
            return false;
        }

        AccessibilityNodeInfo titleInfo = rst.get(0);
        if (titleInfo.getText() == null) {
            return false;
        }

        String title = titleInfo.getText().toString();

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
        if (edittextInfo.getText() == null) {
            return false;
        }

        String searchContent = edittextInfo.getText().toString();
        if (!"群发".equals(searchContent)) {
            return false;
        }

        rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/aw4");
        if (isListEmpty(rst)) {
            return false;
        }

        for (AccessibilityNodeInfo info : rst) {
            if (info.getText() == null) {
                continue;
            }

            String title = info.getText().toString();
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

    private boolean isChatWithCheckboxPage(AccessibilityNodeInfo rootInfo) {
        return false;
    }

    private boolean isForwardingTaskFuse(AccessibilityNodeInfo rootInfo) {
        return isSearchForwardingPage(rootInfo);
    }

    private boolean isCleanTaskFuse(AccessibilityNodeInfo rootInfo) {
        return false;
    }

    private boolean isListEmpty(List lst) {
        return lst == null || lst.size() <= 0;
    }

    private void clearForwardingState() {
        mAlreadySendMsgSet.clear();
        mIsMoreEverClicked = false;
        mCurSendingTarget = null;
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
    }

    private static final class Task {
        public static final int NONE = -1;
        public static final int TASK_FORWARDING = 0;
        public static final int TASK_CLEAN = 1;
    }
}
