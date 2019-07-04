package com.daoshengwanwu.android.service;


import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;
import com.daoshengwanwu.android.R;
import com.daoshengwanwu.android.task.Task;
import com.daoshengwanwu.android.util.SingleSubThreadUtil;

import java.util.List;


public class AuxiliaryService extends AccessibilityService {
    private static final String TAG = "AuxiliaryService";


    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        SingleSubThreadUtil.showToast(this, "成功启动 " +
                getResources().getString(R.string.app_name) + ".", Toast.LENGTH_SHORT);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Toast.makeText(this, "getResources().getString(R.string.app_name) +\n" +
                "                \" 已关闭，如需再次使用请在设置中重新开启本插件.\"", Toast.LENGTH_SHORT).show();

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

    private String getToSendContent() {
        String commonContent = mShareData.getContent();
        String xing = mCurSendingTarget.charAt(0) + "";
        String name = mCurSendingTarget.split("-")[0];

        commonContent = commonContent.replaceAll("xing", xing);
        commonContent = commonContent.replaceAll("name", name);

        return commonContent;
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
}
