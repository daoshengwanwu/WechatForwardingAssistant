package com.daoshengwanwu.android.task;


import android.content.Context;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.daoshengwanwu.android.FloatWindowManager;
import com.daoshengwanwu.android.R;
import com.daoshengwanwu.android.page.Page;
import com.daoshengwanwu.android.service.AuxiliaryService;
import com.daoshengwanwu.android.util.SingleSubThreadUtil;

import java.util.ArrayList;


public class LoadImportViewResourceIdNameTask extends Task implements View.OnClickListener {
    private final Context mApplicationContext;
    private final OnLoadImportViewResourceIdFinishListener mListener;
    private final ArrayList<Page> mToLoadPages = new ArrayList<>();
    private final FloatWindowManager mFloatWindowManager = FloatWindowManager.getInstance();

    private Page mCurLoadPage = null;
    private AccessibilityEvent mLastLockViewClickEvent = null;


    public LoadImportViewResourceIdNameTask(@NonNull final Context context,
                                            @NonNull final OnLoadImportViewResourceIdFinishListener listener) {

        super(TaskId.TASK_LOAD_IMPORT_VIEW_RESOURCE_ID);

        mApplicationContext = context.getApplicationContext();
        mListener = listener;

        mToLoadPages.addAll(Page.getAllPages().values());
        mFloatWindowManager.setButtonOnClickListener(this);
        mFloatWindowManager.setNextButtonOnClickListener(this);
        mFloatWindowManager.setCaptureTVText("锁定");
        mFloatWindowManager.setNextTVText("捕获");
    }

    @Override
    public void execute(@NonNull AccessibilityNodeInfo rootInfo) {
        if (mCurLoadPage == null && mToLoadPages.isEmpty()) {
            mListener.onLoadImportViewResourceIdFinished();
            return;
        }

        if (mCurLoadPage == null) {
            mCurLoadPage = mToLoadPages.remove(0);
        }

        if (mCurLoadPage.isImportViewResourceIdNameCaptured()) {
            mCurLoadPage = null;
            execute(rootInfo);
            return;
        }

        String toastText = "请到" + mCurLoadPage.getPageName() + "点击 " + mCurLoadPage.getNextImportViewDescription() + " 视图, 之后点击截取按钮";
        mFloatWindowManager.setText(toastText);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.tv_btn) {
            onCaptureButtonClick();
        } else if (viewId == R.id.tv_next) {
            onNextButtonClick();
        }
    }

    private void onCaptureButtonClick() {
        final AccessibilityEvent lastViewClickEvent = AuxiliaryService.getLastViewClickEvent();
        if (lastViewClickEvent == null) {
            SingleSubThreadUtil.showToast(mApplicationContext, "无法锁定，请按提示点击视图后重试", Toast.LENGTH_SHORT);
            return;
        }

        mLastLockViewClickEvent = lastViewClickEvent;
        SingleSubThreadUtil.showToast(mApplicationContext, "锁定成功, 请确保当前处于" +
                mCurLoadPage.getPageName() + "然后点击捕获", Toast.LENGTH_SHORT);

    }

    private void onNextButtonClick() {
        if (mCurLoadPage == null) {
            SingleSubThreadUtil.showToast(mApplicationContext, "已捕获所有页面的视图Id", Toast.LENGTH_SHORT);
            return;
        }

        if (mLastLockViewClickEvent == null) {
            SingleSubThreadUtil.showToast(mApplicationContext, "清先锁定再点击捕获", Toast.LENGTH_SHORT);
            return;
        }

        if (mCurLoadPage.captureImportViewResourceIdName(mLastLockViewClickEvent)) {
            mCurLoadPage.saveAllImportViewResourceIdName();

            SingleSubThreadUtil.showToast(mApplicationContext, "捕获成功", Toast.LENGTH_SHORT);
            mLastLockViewClickEvent = null;

            if (mCurLoadPage.isImportViewResourceIdNameCaptured()) {
                mCurLoadPage = null;
            }
        }
    }


    public interface OnLoadImportViewResourceIdFinishListener {
        void onLoadImportViewResourceIdFinished();
    }
}
