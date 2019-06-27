package com.daoshengwanwu.android.model.item;


import androidx.annotation.Nullable;
import com.daoshengwanwu.android.util.CustomTextUtils;


public final class UserItem {
    public final String fullNickName;

    public final String surname;
    public final String name;


    public UserItem(String nickName) {
        fullNickName = nickName;

        surname = CustomTextUtils.getSurname(nickName);
        name = CustomTextUtils.getName(nickName);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof UserItem)) {
            return false;
        }

        return fullNickName.equals(((UserItem)obj).fullNickName);
    }
}
