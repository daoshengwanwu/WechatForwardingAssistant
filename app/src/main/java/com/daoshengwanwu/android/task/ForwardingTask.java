package com.daoshengwanwu.android.task;


import android.content.Context;
import android.view.accessibility.AccessibilityNodeInfo;
import androidx.annotation.NonNull;
import com.daoshengwanwu.android.model.item.UserItem;

import java.util.Set;


public class ForwardingTask extends Task {
    private Context mContext;
    private Set<UserItem> mToForwardingSet;


    protected ForwardingTask(@NonNull Context context, @NonNull Set<UserItem> toForwardingSet) {
        super(TaskId.TASK_FORWARDING);

        mContext = context;
        mToForwardingSet = toForwardingSet;
    }

    @Override
    public void execute(@NonNull AccessibilityNodeInfo rootInfo) {

    }
}
