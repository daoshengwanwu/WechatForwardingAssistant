package com.daoshengwanwu.android.task;


import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.daoshengwanwu.android.FloatWindowManager;
import com.daoshengwanwu.android.R;
import com.daoshengwanwu.android.model.PageFeature;
import com.daoshengwanwu.android.page.Page;
import com.daoshengwanwu.android.util.PageUtils;
import com.daoshengwanwu.android.util.SingleSubThreadUtil;

import java.util.ArrayList;
import java.util.List;


public class LoadPageFeatureTask extends Task implements View.OnClickListener {
    private static final String TAG = "LoadPageFeatureTask";


    private final Context mApplicationContext;
    private final OnLoadPageFeatureFinishedListener mListener;
    private final List<Page> mNotInitedPageList = new ArrayList<>(Page.getAllPages().values());
    private final FloatWindowManager mFloatWindowManager = FloatWindowManager.getInstance();

    private AccessibilityNodeInfo mLastRootInfo = null;
    private Page mCurGatherPage = null;
    private boolean mIsShowToast = false;


    public LoadPageFeatureTask(@NonNull final Context context,
                               @NonNull final OnLoadPageFeatureFinishedListener listener) {

        super(TaskId.TASK_LOAD_PAGE_FEATURE);

        if (listener == null) {
            throw new RuntimeException("listener cannot be null");
        }

        mListener = listener;
        mFloatWindowManager.setButtonOnClickListener(this);
        mFloatWindowManager.setNextButtonOnClickListener(this);
        mApplicationContext = context.getApplicationContext();
    }

    @Override
    public void execute(@NonNull AccessibilityNodeInfo rootInfo) {
        if (rootInfo == null) {
            return;
        }

        mLastRootInfo = rootInfo;

        if (mCurGatherPage != null) {
            mFloatWindowManager.setText("请到" + mCurGatherPage.getPageName() + "页面截取特征" + " (" + mCurGatherPage.featureCount() + ")");
            return;
        }

        if (!mNotInitedPageList.isEmpty()) {
            mCurGatherPage = mNotInitedPageList.remove(0);
            execute(rootInfo);
            return;
        }

        if (!mIsShowToast) {
            SingleSubThreadUtil.showToast(mApplicationContext, "所有界面特征截取完毕", Toast.LENGTH_LONG);
            mFloatWindowManager.setText("所有界面特征已获取完毕");

            mIsShowToast = true;
        } else {
            final PageFeature feature = PageUtils.gatherPageFeatures(rootInfo);
            Log.d("abcdefg", "execute: feature: " + feature);

            Page page = Page.generateFrom(rootInfo);
            if (page.getPageId() == Page.PageId.PAGE_UNKNOWN) {
                mFloatWindowManager.setText("无法判断当前是哪个界面");

                return;
            }

            mFloatWindowManager.setText("当前是" + page.getPageName() + "界面");
        }
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (id == R.id.tv_btn) {
            onCaptureButtonClick();
        } else if (id == R.id.tv_next) {
            onNextButtonClick();
        }
    }

    private void onCaptureButtonClick() {
        final AccessibilityNodeInfo lastNodeInfo = mLastRootInfo;
        if (lastNodeInfo == null) {
            SingleSubThreadUtil.showToast(mApplicationContext, "请到微信界面中再点击截取", Toast.LENGTH_LONG);
            return;
        }

        if (mCurGatherPage == null) {
            SingleSubThreadUtil.showToast(mApplicationContext, "所有特征已截取完毕，无需再次截取", Toast.LENGTH_LONG);
            return;
        }

        final PageFeature feature = PageUtils.gatherPageFeatures(lastNodeInfo);
        if (feature == null) {
            Log.d("abcdefg", "onClick: 截取到的feature为null");
            SingleSubThreadUtil.showToast(mApplicationContext, "截取到的feature为null", Toast.LENGTH_LONG);
        } else {
            mCurGatherPage.mergePageFeature(feature);
            mCurGatherPage.saveToSharedPreferences();

            Log.d("abcdefg", "onClick: 截取特征成功：\n" + mCurGatherPage);

            SingleSubThreadUtil.showToast(mApplicationContext,
                    mCurGatherPage.getPageName() + "界面特征：\n" + mCurGatherPage.getPageFeature(), Toast.LENGTH_SHORT);

            final int curFeatureCount = mCurGatherPage.featureCount();
            mFloatWindowManager.setText("请到" + mCurGatherPage.getPageName() + "页面截取特征" + " (" + curFeatureCount + ")");
        }
    }

    private void onNextButtonClick() {
        mCurGatherPage = null;
    }


    public interface OnLoadPageFeatureFinishedListener {
        void onLoadPageFeatureFinished();
    }
}
