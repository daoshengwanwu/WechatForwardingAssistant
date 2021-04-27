package com.daoshengwanwu.android.model;


import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class UserGroupLab {
    private static final UserGroupLab sInstance = new UserGroupLab();

    private Map<UUID, UserGroup> mUUIDSetMap = new HashMap<>();


    public static UserGroupLab getInstance() {
        return sInstance;
    }

    private UserGroupLab() {

    }

    public boolean contains(@NonNull final UUID userGroupUUID) {
        if (userGroupUUID == null) {
            return false;
        }

        return getUserItemsByUUID(userGroupUUID) != null;
    }

    public UserGroup createUserGroup(Context context, String groupName) {
        UserGroup userGroup = new UserGroup(groupName);

        mUUIDSetMap.put(userGroup.getUUID(), userGroup);
        saveAllGroupToSP(context);

        return userGroup;
    }

    public UserGroup getUserItemsByUUID(UUID id) {
        return mUUIDSetMap.get(id);
    }

    public UserGroup getCloneUserItemsByUUID(UUID id) {
        return new UserGroup(getUserItemsByUUID(id));
    }

    public void putOrMergeUserItems(Context context, UserGroup userGroup) {
        if (userGroup == null) {
            return;
        }

        UUID id = userGroup.getUUID();
        UserGroup group = mUUIDSetMap.get(id);

        if (group == null) {
            group = userGroup;
        } else {
            group.mergeUserItems(userGroup);
        }

        mUUIDSetMap.put(id, group);
        saveAllGroupToSP(context);
    }

    public List<UserGroup> getAllUserGroups() {
        return new ArrayList<>(mUUIDSetMap.values());
    }

    public int size() {
        return mUUIDSetMap.size();
    }

    public void removeUserGroups(Context context, Collection<UserGroup> groups) {
        if (groups == null) {
            return;
        }

        for (UserGroup group : groups) {
            mUUIDSetMap.remove(group.getUUID());
        }

        saveAllGroupToSP(context);
    }

    public void updateGroup(Context context, UserGroup group) {
        if (group == null) {
            return;
        }

        mUUIDSetMap.put(group.getUUID(), group);
        saveAllGroupToSP(context);
    }

    public void saveAllGroupToSP(Context context) {
        String str = getMapJsonString();
        SharedPreferences.Editor editor = context.getSharedPreferences("sp_lab_name", Context.MODE_PRIVATE).edit();
        editor.putString("key_lab_string", str);
        editor.commit();
    }

    public void restoreAllGroupToLab(Context context) {
        SharedPreferences sp = context.getSharedPreferences("sp_lab_name", Context.MODE_PRIVATE);
        String str = sp.getString("key_lab_string", "{}");
        mUUIDSetMap = getMapFromJsonString(str);
    }

    private String getMapJsonString() {
        Gson gson = new Gson();

        return gson.toJson(mUUIDSetMap);
    }

    private Map<UUID, UserGroup> getMapFromJsonString(String str) {
        Gson gson = new Gson();

        return gson.fromJson(str, new TypeToken<Map<UUID, UserGroup>>(){}.getType());
    }
}
