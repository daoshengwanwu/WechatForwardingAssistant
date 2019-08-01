package com.daoshengwanwu.android.model;


import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class ForwardingContentLab {
    private static ForwardingContentLab sInstance = new ForwardingContentLab();

    private Map<UUID, String> mUUIDContentMap = new HashMap<>();


    public static ForwardingContentLab getInstance() {
        return sInstance;
    }

    public void putContent(Context context, UUID uuid, String content) {
        mUUIDContentMap.put(uuid, content);
        saveAllDataToSP(context);
    }

    public String getContent(UUID uuid) {
        String str = mUUIDContentMap.get(uuid);

        return str == null ? "" : str;
    }

    private void saveAllDataToSP(Context context) {
        Gson gson = new Gson();
        String str = gson.toJson(mUUIDContentMap);
        SharedPreferences.Editor editor = context.getSharedPreferences("content_lab", Context.MODE_PRIVATE).edit();
        editor.putString("key_content_lab", str);
        editor.commit();
    }

    public void restoreAllData(Context context) {
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = context.getSharedPreferences("content_lab", Context.MODE_PRIVATE);
        String str = sharedPreferences.getString("key_content_lab", "{}");
        mUUIDContentMap = gson.fromJson(str, new TypeToken<Map<UUID, String>>(){}.getType());
    }
}
