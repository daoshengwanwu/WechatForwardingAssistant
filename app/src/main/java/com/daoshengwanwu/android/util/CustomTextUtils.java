package com.daoshengwanwu.android.util;


import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class CustomTextUtils {
    public static final Set<Character> REMARKS_NAME_CHAR_SET = new HashSet<>();


    static {
        REMARKS_NAME_CHAR_SET.addAll(Arrays.asList(
                '-', '&', '@', '(', ')', '.', '。', ',', ' ', '/', '+', '"', '”', '“'));
    }


    public static String getViewFeatureText(@NonNull final String originFeatureText) {
        if (originFeatureText == null) {
            return null;
        }

        for (int i = 0; i < originFeatureText.length(); i++) {
            final char c = originFeatureText.charAt(i);
            if (!isValidViewFeatureCharacter(c)) {
                return originFeatureText.substring(0, i);
            }
        }

        return originFeatureText;
    }

    public static boolean canStringParseToIntger(final String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    public static String getValidRemarkName(@NonNull final String originRemarkName) {
        if (originRemarkName == null) {
            return "";
        }

        for (int i = 0; i < originRemarkName.length(); i++) {
            final char c = originRemarkName.charAt(i);
            if (!isValidRemarkNameCharacter(c)) {
                return originRemarkName.substring(0, i);
            }
        }

        return originRemarkName;
    }

    public static boolean isValidViewFeatureCharacter(final char c) {
        if ('A' <= c && c <= 'Z' || 'a' <= c && c <= 'z') {
            return true;
        }

        return 0x4e00 <= c && c <= 0x9FA5;
    }

    public static boolean isValidRemarkNameCharacter(final char c) {
        if (REMARKS_NAME_CHAR_SET.contains(c)) {
            return true;
        }

        if ('A' <= c && c <= 'Z' ||
            'a' <= c && c <= 'z' ||
            '0' <= c && c <= '9') {

            return true;
        }

        return 0x4e00 <= c && c <= 0x9FA5;
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

    private CustomTextUtils() {
        /* cannot be instantiated */
    }
}
