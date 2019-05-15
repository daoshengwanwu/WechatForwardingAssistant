package com.daoshengwanwu.android;


import android.accessibilityservice.AccessibilityService;
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
    private static final String TAG = "AuxiliaryService";

    private String mCurTarget = null;
    private final Set<String> mAlreadySendMsgSet = new HashSet<>();


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
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED &&
            event.getEventType() != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {

            return;
        }

        Log.d(TAG, "onAccessibilityEvent: 接收到事件");
        AccessibilityNodeInfo source = event.getSource();
        if (source == null) {
            return;
        }

        AccessibilityNodeInfo rootInfo = getRootInActiveWindow();
        if (rootInfo == null) {
            Toast.makeText(this,
                    getResources().getString(R.string.failure_text_get_root_info), Toast.LENGTH_LONG).show();

            return;
        }

        List<AccessibilityNodeInfo> rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/aw4");
        if (rst != null && rst.size() > 0) {
            boolean isHasContactView = false;
            for (AccessibilityNodeInfo info : rst) {
                if ("最常使用".equals(info.getText().toString()) || "联系人".equals(info.getText().toString())) {
                    isHasContactView = true;
                }
            }
            if (!isHasContactView) {
                return;
            }

            mCurTarget = null;
            rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/ld");
            for (AccessibilityNodeInfo info : rst) {
                if ("标签: 群发".equals(info.getText().toString())) {
                    String nickName = info.getParent().getChild(0).getText().toString();
                    if (mAlreadySendMsgSet.contains(nickName)) {
                        continue;
                    }

                    boolean success = info.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);

                    Log.d(TAG, "发现要群发的对象: " + nickName + ",尝试点击的结果： " + success);

                    if (success) {
                        mCurTarget = nickName;
                        mAlreadySendMsgSet.add(nickName);
                        return;
                    }
                }
            }

            return;
        }

        rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/bq");
        if (rst != null && rst.size() > 0) {
            mAlreadySendMsgSet.clear();
        }

        rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/ko");
        boolean isNameMatch = false;
        for (AccessibilityNodeInfo info : rst) {
            if (mCurTarget != null && mCurTarget.equals(info.getText().toString())) {
                isNameMatch = true;
            }
        }
        if (isNameMatch) {
            rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/ami");
            if (rst != null && rst.size() > 0) {
                AccessibilityNodeInfo etNode = rst.get(0);
                Bundle arguments = new Bundle();
                arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, "android sendmessage test");
                boolean success = etNode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
                if (success) {
                    rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/amp");
                    if (rst != null && rst.size() > 0) {
                        AccessibilityNodeInfo sendBtn = rst.get(0);
                        success = sendBtn.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        if (success) {
                            rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/km");
                            if (rst != null && rst.size() > 0) {
                                AccessibilityNodeInfo backBtn = rst.get(0);
                                backBtn.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            }
                        }
                    }
                }
            }

        }
    }

    @Override
    public void onInterrupt() {

    }
}
