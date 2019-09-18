package com.daoshengwanwu.android.util;


import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;


public class ActionPerformer {
    private static final String TAG = "ActionPerformer";


    public static boolean performAction(AccessibilityNodeInfo info, int action, String des) {
        return performAction(info, action, null, des);
    }

    public static boolean performAction(AccessibilityNodeInfo info, int action, Bundle args, String des) {
        if (info == null) {
            Log.d(TAG, "performAction: 失败\n\tinfo 传入为null\n");
            return false;
        }

        if (TextUtils.isEmpty(des)) {
            Log.d(TAG, "performAction: 失败\n\tdes 传入为空\n\tinfo: " + info + "\n");
            return false;
        }

        boolean success;
        if (args == null) {
            success = info.performAction(action);
        } else {
            success = info.performAction(action, args);
        }

        if (success) {
            return true;
        }

        Log.d(TAG, "performAction: " + des + ": 失败\n\tinfo: " +
                info.toString() + "\n\taction: " + action + "\n\targs: " + args + "\n");

        return false;
    }

    public static String getText(AccessibilityNodeInfo info, String des) {
        if (info == null) {
            Log.d(TAG, "getText: 失败: info为null" + des);
            return "";
        }

        return info.getText() == null ? "" : info.getText().toString();
    }
}
