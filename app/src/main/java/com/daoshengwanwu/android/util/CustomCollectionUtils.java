package com.daoshengwanwu.android.util;


import java.util.List;

public class CustomCollectionUtils {
    private CustomCollectionUtils() {
        /* cannot be instantiated */
    }

    public static boolean isListEmpty(List list) {
        return list == null || list.size() <= 0;
    }
}
