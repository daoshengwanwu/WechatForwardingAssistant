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
    private int mRunningTask = ShareData.Task.NONE;

    // 群发相关
    private boolean mIsLabelVerification = false;
    private boolean mIsToForwardingSetLoaded = false;
    private String mCurSendingTarget = null;
    private final Set<String> mAlreadySendMsgSet = new HashSet<>();
    private final Set<String> mToForwardingSet = new HashSet<>();
    private int mCurLoadIndex = 0;

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

    private void performForwarding(AccessibilityEvent event, AccessibilityNodeInfo rootInfo,
                                   AccessibilityNodeInfo sourceInfo, int curPage) {

        if (!mIsToForwardingSetLoaded) {
            performLoadForwardingSet(event, rootInfo, sourceInfo, curPage);
            return;
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
            String labelText = labelInfo.getText().toString();
            if (!mShareData.getLabel().equals(labelText)) {
                Toast.makeText(this, "当前标签页面与群发指定的不符，请切换到：" +
                        mShareData.getLabel() + "：标签页面", Toast.LENGTH_SHORT).show();
                return;
            }

            mIsLabelVerification = true;
        }

        rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/kw");
        if (isListEmpty(rst)) {
            return;
        }

        AccessibilityNodeInfo backInfo = rst.get(0);

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

    // TODO:: 完成这个方法
    private boolean isLabelMembersPage(AccessibilityNodeInfo rootInfo) {
        return false;
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
    }
}
