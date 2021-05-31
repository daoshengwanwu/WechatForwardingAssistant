package com.daoshengwanwu.android.util;


import android.app.ActivityManager;
import android.content.Context;
import android.os.Process;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;


/**
 * @author baihaoran
 * @date 2020-03-02
 * @time 12:34
 */
public class ProcessUtils {
    private static volatile String sCurrentProcessName;


    public static boolean isMainProcess(@NonNull Context context) {
        final String packageName = context.getPackageName();
        final String currentProcessName = getCurrentProcessName(context);

        return packageName.equals(currentProcessName);
    }

    @Nullable
    public static String getCurrentProcessName(@NonNull Context context) {
        final ActivityManager activityManager = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager == null) {
            return null;
        }

        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfos = activityManager.getRunningAppProcesses();
        if (runningAppProcessInfos == null) {
            return null;
        }

        for (ActivityManager.RunningAppProcessInfo info : runningAppProcessInfos) {
            if (info.pid == Process.myPid()) {
                return info.processName;
            }
        }

        return null;
    }
}
