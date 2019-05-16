package com.daoshengwanwu.android;


import android.accessibilityservice.AccessibilityService;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class AuxiliaryService extends AccessibilityService {
    // 标志着当前正在进行着的任务
    private int mRunningTask = Task.NONE;

    // 群发相关
    private String mCurSendingTarget = null;
    private final Set<String> mAlreadySendMsgSet = new HashSet<>();

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
        if (event.getEventType() != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED &&
            event.getEventType() != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {

            return;
        }

        AccessibilityNodeInfo source = event.getSource();
        AccessibilityNodeInfo rootInfo = getRootInActiveWindow();

        if (source == null || rootInfo == null) {
            return;
        }

        switch (mRunningTask) {
            case Task.NONE: {

            } break;

            case Task.TASK_CLEAN: {

            } break;

            case Task.TASK_FORWARDING: {

            } break;
        }

//        List<AccessibilityNodeInfo> rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/aw4");
//        if (rst != null && rst.size() > 0) {
//            boolean isHasContactView = false;
//            for (AccessibilityNodeInfo info : rst) {
//                if ("最常使用".equals(info.getText().toString()) || "联系人".equals(info.getText().toString())) {
//                    isHasContactView = true;
//                }
//            }
//            if (!isHasContactView) {
//                return;
//            }
//
//            mCurTarget = null;
//            rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/ld");
//            for (AccessibilityNodeInfo info : rst) {
//                if ("标签: 群发".equals(info.getText().toString())) {
//                    String nickName = info.getParent().getChild(0).getText().toString();
//                    if (mAlreadySendMsgSet.contains(nickName)) {
//                        continue;
//                    }
//
//                    boolean success = info.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
//
//                    Log.d(TAG, "发现要群发的对象: " + nickName + ",尝试点击的结果： " + success);
//
//                    if (success) {
//                        mCurTarget = nickName;
//                        mAlreadySendMsgSet.add(nickName);
//                        return;
//                    }
//                }
//            }
//
//            return;
//        }
//
//        rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/bq");
//        if (rst != null && rst.size() > 0) {
//            mAlreadySendMsgSet.clear();
//        }
//
//        rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/ko");
//        boolean isNameMatch = false;
//        for (AccessibilityNodeInfo info : rst) {
//            if (mCurTarget != null && mCurTarget.equals(info.getText().toString())) {
//                isNameMatch = true;
//            }
//        }
//        if (isNameMatch) {
//            rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/ami");
//            if (rst != null && rst.size() > 0) {
//                AccessibilityNodeInfo etNode = rst.get(0);
//                Bundle arguments = new Bundle();
//                String clipboardMessage = getClipboardMessage();
//                arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, mCurTarget.charAt(0) + "老师，" + clipboardMessage);
//                boolean success = clipboardMessage.length() > 0 && etNode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
//                if (success) {
//                    rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/amp");
//                    if (rst != null && rst.size() > 0) {
//                        AccessibilityNodeInfo sendBtn = rst.get(0);
//                        success = sendBtn.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                        if (success) {
//                            rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/km");
//                            if (rst != null && rst.size() > 0) {
//                                AccessibilityNodeInfo backBtn = rst.get(0);
//                                backBtn.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                            }
//                        }
//                    }
//                }
//            }
//
//        }
    }

    private String getClipboardMessage() {
        ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData data = cm.getPrimaryClip();

        return data == null ? "" : data.getItemAt(0).toString();
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

        return Page.PAGE_UNKNOWN;
    }

    private boolean isWechatPage(AccessibilityNodeInfo rootInfo) {

    }

    private boolean isContactPage(AccessibilityNodeInfo rootInfo) {

    }

    private boolean isExplorePage(AccessibilityNodeInfo rootInfo) {

    }

    private boolean isSelfPage(AccessibilityNodeInfo rootInfo) {

    }

    private boolean isSearchForwardingPage(AccessibilityNodeInfo rootInfo) {

    }


    private static final class Page {
        public static final int PAGE_UNKNOWN = -1;
        public static final int PAGE_WECHAT = 0;
        public static final int PAGE_CONTACT = 1;
        public static final int PAGE_EXPLORE = 2;
        public static final int PAGE_SELF = 3;
        public static final int PAGE_SEARCH_FORWARDING = 4;
    }

    private static final class Task {
        public static final int NONE = -1;
        public static final int TASK_FORWARDING = 0;
        public static final int TASK_CLEAN = 1;
    }
}
