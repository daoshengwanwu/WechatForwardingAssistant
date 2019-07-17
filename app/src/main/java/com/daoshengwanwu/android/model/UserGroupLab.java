package com.daoshengwanwu.android.model;


import java.util.HashMap;
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

    public UserGroup getUserItemsByUUID(UUID id) {
        return mUUIDSetMap.get(id);
    }

    public void putOrMergeUserItems(UserGroup userGroup) {
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
    }
}
