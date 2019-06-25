package com.daoshengwanwu.android.model.item;


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
}
