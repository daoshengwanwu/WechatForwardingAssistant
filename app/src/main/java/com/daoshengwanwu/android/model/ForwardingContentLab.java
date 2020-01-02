package com.daoshengwanwu.android.model;


import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class ForwardingContentLab {
    private static ForwardingContentLab sInstance = new ForwardingContentLab();

    private Map<UUID, String> mUUIDContentMap = new HashMap<>();
    private Map<UUID, ForwardingContent> mForwardingContentMap = new HashMap<>();


    public static ForwardingContentLab getInstance() {
        return sInstance;
    }

    public void putContent(Context context, UUID uuid, String content) {
        mUUIDContentMap.put(uuid, content);
        saveAllDataToSP(context);
    }

    public void putForwardingContent(Context context, ForwardingContent forwardingContent) {
        mForwardingContentMap.put(forwardingContent.getId(), forwardingContent);
        saveAllForwardingContentToSP(context);
    }

    public String getContent(UUID uuid) {
        String str = mUUIDContentMap.get(uuid);

        return str == null ? "" : str;
    }

    public ForwardingContent getForwardingContent(UUID id) {
        return mForwardingContentMap.get(id);
    }

    public List<ForwardingContent> getAllForwardingContents() {
        return new ArrayList<>(mForwardingContentMap.values());
    }

    private void saveAllDataToSP(Context context) {
        Gson gson = new Gson();
        String str = gson.toJson(mUUIDContentMap);
        SharedPreferences.Editor editor = context.getSharedPreferences("content_lab", Context.MODE_PRIVATE).edit();
        editor.putString("key_content_lab", str);
        editor.commit();
    }

    private void saveAllForwardingContentToSP(Context context) {
        Gson gson = new Gson();
        String str = gson.toJson(mForwardingContentMap);
        SharedPreferences.Editor editor = context.getSharedPreferences("forwarding_content_lab", Context.MODE_PRIVATE).edit();
        editor.putString("key_forwarding_content_lab", str);
        editor.commit();
    }

    public void restoreAllData(Context context) {
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = context.getSharedPreferences("content_lab", Context.MODE_PRIVATE);
        String str = sharedPreferences.getString("key_content_lab", "{}");
        mUUIDContentMap = gson.fromJson(str, new TypeToken<Map<UUID, String>>(){}.getType());
    }

    public void restoreAllForwardingContent(Context context) {
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = context.getSharedPreferences("forwarding_content_lab", Context.MODE_PRIVATE);
        String str = sharedPreferences.getString("key_forwarding_content_lab", "{}");
        mForwardingContentMap = gson.fromJson(str, new TypeToken<Map<UUID, ForwardingContent>>(){}.getType());
    }
}
