package com.daoshengwanwu.android.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class SharedPreferencesUtils {
    private static final String TABLE_NAME = "com.daoshengwanwu.android.wechatassistant.cache";

    private static volatile SharedPreferences sSharedPreferences = null;


    public static synchronized void init(@NonNull final Context context) {
        final Context applicationContext = context.getApplicationContext();

        if (sSharedPreferences == null) {
            sSharedPreferences = applicationContext.getSharedPreferences(TABLE_NAME, Context.MODE_PRIVATE);
        }
    }

    public static synchronized boolean isInited() {
        return sSharedPreferences != null;
    }


    public enum STRING_CACHE {
        WECHAT_PAGE_FEATURE("wechat_page_feature"),
        CONTACT_PAGE_FEATURE("contact_page_feature"),
        EXPLORE_PAGE_FEATURE("explore_page_feature"),
        CHAT_PAGE_FEATURE("chat_page_feature"),
        FRIEND_PAGE_FEATURE("friend_page_feature"),
        LABEL_MEMBERS_PAGE_FEATURE("label_members_page_feature"),
        PERSONAL_INTRODUCTION_PAGE_FEATURE("personal_introduction_page_feature"),
        SELECT_RECEIVER_PAGE_FEATURE("select_receiver_page"),
        CHAT_PAGE_VIEW_RESOURCE_ID_NAME("chat_page_view_resource_id_name");


        private String mFieldName;


        STRING_CACHE(@NonNull String fieldName) {
            mFieldName = fieldName;

            if (TextUtils.isEmpty(mFieldName)) {
                mFieldName = "feedbacker_string_unknown";
            }
        }

        public void put(String value) {
            if (!isInited()) {
                return;
            }

            sSharedPreferences.edit().putString(mFieldName, value).commit();
        }

        public String get(@Nullable String defValue) {
            if (!isInited()) {
                return defValue;
            }

            return sSharedPreferences.getString(mFieldName, defValue);
        }
    }

    public enum LONG_CACHE {
        ;


        private String mFieldName;


        LONG_CACHE(@NonNull String fieldName) {
            mFieldName = fieldName;

            if (TextUtils.isEmpty(mFieldName)) {
                mFieldName = "feedbacker_long_unknown";
            }
        }

        public void put(long value) {
            if (!isInited()) {
                return;
            }

            sSharedPreferences.edit().putLong(mFieldName, value).commit();
        }

        public long get(long defValue) {
            if (!isInited()) {
                return defValue;
            }

            return sSharedPreferences.getLong(mFieldName, defValue);
        }
    }

    public enum BOOLEAN_CACHE {
        ;


        private String mFieldName;


        BOOLEAN_CACHE(@NonNull String fieldName) {
            mFieldName = fieldName;

            if (TextUtils.isEmpty(mFieldName)) {
                mFieldName = "feedbacker_boolean_unknown";
            }
        }

        public void put(boolean value) {
            if (!isInited()) {
                return;
            }

            sSharedPreferences.edit().putBoolean(mFieldName, value).commit();
        }

        public boolean get(boolean defValue) {
            if (!isInited()) {
                return defValue;
            }

            return sSharedPreferences.getBoolean(mFieldName, defValue);
        }
    }
}
