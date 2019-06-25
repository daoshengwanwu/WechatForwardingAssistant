package com.daoshengwanwu.android.util;


import android.text.TextUtils;


public class CustomTextUtils {
    private CustomTextUtils() {
        /* cannot be instantiated */
    }

    public static String getSurname(String fullName) {
        if (TextUtils.isEmpty(fullName)) {
            return "#none";
        }

        return fullName.charAt(0) + "";
    }

    public static String getName(String fullName) {
        if (TextUtils.isEmpty(fullName)) {
            return "#none";
        }

        return fullName.split("-")[0];
    }
}
