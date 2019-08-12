package com.daoshengwanwu.android;


import android.app.Application;
import com.daoshengwanwu.android.model.ForwardingContentLab;
import com.daoshengwanwu.android.model.UIForwardingTaskLab;
import com.daoshengwanwu.android.model.UserGroupLab;


public class CustomApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        UserGroupLab.getInstance().restoreAllGroupToLab(this);
        ForwardingContentLab.getInstance().restoreAllData(this);
        ForwardingContentLab.getInstance().restoreAllForwardingContent(this);
        UIForwardingTaskLab.getInstance().restoreAllData(this);
    }
}
