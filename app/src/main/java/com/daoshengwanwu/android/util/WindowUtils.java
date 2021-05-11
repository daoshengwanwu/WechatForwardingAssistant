package com.daoshengwanwu.android.util;


import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.view.WindowManager;

import androidx.annotation.NonNull;


/**
 * @author baihaoran
 * @date 2020-01-16
 * @time 20:48
 */
public class WindowUtils {
    public static boolean hasOverlayWindowPermission(@NonNull Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        return Settings.canDrawOverlays(context);
    }

    public static int getScreenWidth(@NonNull Context context) {
        if (context == null || context.getApplicationContext() == null) {
            return -1;
        }

        final WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return windowManager.getDefaultDisplay().getWidth();
    }
}
