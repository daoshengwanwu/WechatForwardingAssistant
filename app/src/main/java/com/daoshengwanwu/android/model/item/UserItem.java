package com.daoshengwanwu.android.model.item;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.daoshengwanwu.android.util.CustomTextUtils;


public final class UserItem implements Comparable<UserItem> {
    public final String labelText;
    public final String fullNickName;

    public final String surname;
    public final String name;


    public UserItem(String nickName, String labelText) {
        this.fullNickName = nickName == null ? "" : nickName;
        this.labelText = labelText == null ? "" : labelText;

        this.surname = CustomTextUtils.getSurname(nickName);
        this.name = CustomTextUtils.getName(nickName);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof UserItem)) {
            return false;
        }

        return fullNickName.equals(((UserItem)obj).fullNickName) &&
                labelText.equals(((UserItem)obj).labelText);
    }

    @Override
    public int hashCode() {
        return fullNickName.hashCode() * 31 + labelText.hashCode();
    }

    @NonNull
    @Override
    public String toString() {
        return fullNickName;
    }

    @Override
    public int compareTo(UserItem o) {
        if (o == null) {
            return 1;
        }

        return (labelText + fullNickName).compareTo(o.labelText + fullNickName);
    }
}
