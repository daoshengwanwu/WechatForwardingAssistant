package com.daoshengwanwu.android.util;


import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.widget.Toast;


public class SingleSubThreadUtil {
    private static final HandlerThread HANDLER_THREAD = new HandlerThread("handler_thread");

    private static  Handler sHandler;


    static {
        HANDLER_THREAD.start();
        sHandler = new Handler(HANDLER_THREAD.getLooper());
    }


    public static void showToast(Context context, String text, int length) {
        showToast(context, text, length, 0);
    }

    public static void showToast(final Context context, final String text, final int length, long delay) {
        sHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, text, length).show();
            }
        }, delay);
    }

    public static void quitThread() {
        HANDLER_THREAD.quitSafely();
    }
}
