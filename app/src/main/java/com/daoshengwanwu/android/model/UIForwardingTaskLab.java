package com.daoshengwanwu.android.model;


import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.*;

public class UIForwardingTaskLab {
    private static UIForwardingTaskLab sInstance = new UIForwardingTaskLab();

    private Map<UUID, UIForwardingTask> mUUIDUIForwardingTaskMap = new HashMap<>();


    public static UIForwardingTaskLab getInstance() {
        return sInstance;
    }

    private UIForwardingTaskLab() {

    }

    public void putForwrdingTaskLab(Context context, UIForwardingTask task) {
        mUUIDUIForwardingTaskMap.put(task.getId(), task);
        saveAllDataToSp(context);
    }

    public UIForwardingTask getUIForwardingTask(UUID id) {
        return mUUIDUIForwardingTaskMap.get(id);
    }

    private void saveAllDataToSp(Context context) {
        Gson gson = new Gson();
        String str = gson.toJson(mUUIDUIForwardingTaskMap);
        SharedPreferences.Editor editor = context.getSharedPreferences("ui_forwarding_task_lab", Context.MODE_PRIVATE).edit();
        editor.putString("key_str", str);
        editor.commit();
    }

    public void restoreAllData(Context context) {
        SharedPreferences sp = context.getSharedPreferences("ui_forwarding_task_lab", Context.MODE_PRIVATE);
        String str = sp.getString("key_str", "{}");
        Gson gson = new Gson();
        mUUIDUIForwardingTaskMap = gson.fromJson(str, new TypeToken<Map<UUID, UIForwardingTask>>(){}.getType());
    }

    public List<UIForwardingTask> getAllUIForwardingTask() {
        return new ArrayList<>(mUUIDUIForwardingTaskMap.values());
    }
}
