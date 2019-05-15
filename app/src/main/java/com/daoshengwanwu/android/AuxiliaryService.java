package com.daoshengwanwu.android;


import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class AuxiliaryService extends AccessibilityService {
    private static final String TAG = "AuxiliaryService";

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
                        mAlreadySendMsgSet.add(nickName);
                        return;
                    }
                }
            }
        } else {
            rst = rootInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/bq");
            if (rst != null && rst.size() > 0) {
                mAlreadySendMsgSet.clear();
            }
        }
    }

    @Override
    public void onInterrupt() {

    }
}
