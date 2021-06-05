package com.daoshengwanwu.android.task;


import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.daoshengwanwu.android.FloatWindowManager;
import com.daoshengwanwu.android.model.PageFeature;
import com.daoshengwanwu.android.util.PageUtils;
import com.daoshengwanwu.android.util.SingleSubThreadUtil;


public class LoadPageFeatureTask extends Task implements View.OnClickListener {
    private static final String TAG = "LoadPageFeatureTask";


    private final Context mApplicationContext;
    private final OnLoadPageFeatureFinishedListener mListener;
    private final FloatWindowManager mFloatWindowManager = FloatWindowManager.getInstance();

    private AccessibilityNodeInfo mLastRootInfo = null;


    public LoadPageFeatureTask(@NonNull final Context context,
                               @NonNull final OnLoadPageFeatureFinishedListener listener) {

        super(TaskId.TASK_LOAD_PAGE_FEATURE);

        if (listener == null) {
            throw new RuntimeException("listener cannot be null");
        }

        mListener = listener;
        mFloatWindowManager.setButtonOnClickListener(this);
        mApplicationContext = context.getApplicationContext();
    }

    @Override
    public void execute(@NonNull AccessibilityNodeInfo rootInfo) {
        if (rootInfo == null) {
            return;
        }

        mLastRootInfo = rootInfo;
    }

    @Override
    public void onClick(View v) {
        final AccessibilityNodeInfo lastNodeInfo = mLastRootInfo;
        if (lastNodeInfo == null) {
            SingleSubThreadUtil.showToast(mApplicationContext, "请到微信界面中再点击截取", Toast.LENGTH_LONG);
            return;
        }

        final PageFeature feature = PageUtils.gatherPageFeatures(lastNodeInfo);
        if (feature == null) {
            Log.d("abcdefg", "onClick: 截取到的feature为null");
            SingleSubThreadUtil.showToast(mApplicationContext, "截取到的feature为null", Toast.LENGTH_LONG);
        } else {
            Log.d("abcdefg", "onClick: 截取特征成功：\n" + feature);
            SingleSubThreadUtil.showToast(mApplicationContext, "当前界面特征：\n" + feature, Toast.LENGTH_LONG);
        }
    }


    public interface OnLoadPageFeatureFinishedListener {
        void onLoadPageFeatureFinished();
    }
}
