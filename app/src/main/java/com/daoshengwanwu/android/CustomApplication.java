package com.daoshengwanwu.android;


import android.app.Application;

import com.daoshengwanwu.android.model.ForwardingContentLab;
import com.daoshengwanwu.android.model.UIForwardingTaskLab;
import com.daoshengwanwu.android.model.UserGroupLab;
import com.daoshengwanwu.android.util.SharedPreferencesUtils;


public class CustomApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferencesUtils.init(this);
        UserGroupLab.getInstance().restoreAllGroupToLab(this);
        ForwardingContentLab.getInstance().restoreAllData(this);
        ForwardingContentLab.getInstance().restoreAllForwardingContent(this);
        UIForwardingTaskLab.getInstance().restoreAllData(this);
        FloatWindowManager.init(this);
    }
}
