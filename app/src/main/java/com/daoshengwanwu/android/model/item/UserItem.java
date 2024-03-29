package com.daoshengwanwu.android.model.item;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.daoshengwanwu.android.util.CustomTextUtils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;


public final class UserItem implements Comparable<UserItem> {
    public static String converterToPinyin(String chines) {
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

        StringBuilder pinyinName = new StringBuilder();
        char[] nameChar = chines.toCharArray();
        for (int i = 0; i < nameChar.length; i++) {
            if (nameChar[i] > 128) {
                try {
                    String[] strs = PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat);
                    if (strs != null) {
                        for (String str : strs) {
                            pinyinName.append(str);
                        }
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                if (nameChar[i] >= '0' && nameChar[i] <= '9') {
                    pinyinName.append((char)(nameChar[i] + 75));
                } else {
                    pinyinName.append(nameChar[i]);
                }
            }
        }

        return pinyinName.toString();
    }


    public final String labelText;
    public final String fullNickName; // 备注名

    public final String surname; // 备注的第一个字符
    public final String name; // 当前包含姓和名
    public final String pinyin; // 备注的拼音


    public UserItem(String nickName, String labelText) {
        if (nickName != null && nickName.endsWith("…")) {
            try {
                nickName = nickName.substring(0, nickName.length() - 1);
            } catch (Throwable e) {
                // ignore
            }
        }

        this.fullNickName = nickName == null ? "" : nickName;
        this.labelText = labelText == null ? "" : labelText;

        this.surname = CustomTextUtils.getSurname(nickName);
        this.name = CustomTextUtils.getName(nickName);

        pinyin = converterToPinyin(fullNickName + labelText).toLowerCase();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof UserItem)) {
            return false;
        }

        return fullNickName.equals(((UserItem)obj).fullNickName);
    }

    @Override
    public int hashCode() {
        if (fullNickName != null) {
            return fullNickName.hashCode();
        }

        return -1;
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

        return pinyin.compareTo(o.pinyin);
    }
}
