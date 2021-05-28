package com.daoshengwanwu.android.util;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;


public final class GUtils {
    public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
    public static final Class<?>[] EMPTY_CLASS_ARRAY = new Class[0];


    public static boolean isApplicationInstalled(@NonNull Context context, @NonNull String packageName) {
        if (context == null || context.getApplicationContext() == null) {
            return false;
        }

        if (TextUtils.isEmpty(packageName)) {
            return false;
        }

        final PackageManager packageManager = context.getPackageManager();
        if (packageManager == null) {
            return false;
        }

        try {
            final PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            return packageName.equals(packageInfo.packageName);
        } catch (Throwable e) {
            return false;
        }
    }

    public static boolean isDebugMode(@NonNull Context context) {
        if (context == null || context.getApplicationContext() == null) {
            return false;
        }

        try {
            final ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Throwable e) {
            return false;
        }
    }

    public static boolean arrayContains(@Nullable final Object[] arr, @Nullable final Object item) {
        if (arr == null) {
            return false;
        }

        for (Object o : arr) {
            if (o == item) {
                return true;
            }

            if (o == null) {
                continue;
            }

            if (o.equals(item)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isSameLength(final Object[] arr1, final Object[] arr2) {
        int arr1Len = arr1 == null ? 0 : arr1.length;
        int arr2Len = arr2 == null ? 0 : arr2.length;

        return arr1Len == arr2Len;
    }

    public static Class<?>[] toClassArray(final Object... objs) {
        if (objs == null || objs.length == 0) {
            return EMPTY_CLASS_ARRAY;
        }

        final Class<?>[] classes = new Class[objs.length];
        for (int i = 0; i < objs.length; i++) {
            classes[i] = objs[i] == null ? null : objs[i].getClass();
        }

        return classes;
    }

    public static Class<?>[] nullToEmpty(final Class<?>[] arr) {
        if (arr == null || arr.length == 0) {
            return EMPTY_CLASS_ARRAY;
        }

        return arr;
    }

    public static Object[] nullToEmpty(final Object[] arr) {
        if (arr == null || arr.length == 0) {
            return EMPTY_OBJECT_ARRAY;
        }

        return arr;
    }

    public static List<Class<?>> getAllInterfaces(final Class<?> cls) {
        if (cls == null) {
            return null;
        }

        final LinkedHashSet<Class<?>> interfacesFound = new LinkedHashSet<>();
        getAllInterfaces(cls, interfacesFound);

        return new ArrayList<>(interfacesFound);
    }

    private static void getAllInterfaces(Class<?> cls, final HashSet<Class<?>> interfacesFound) {
        while (cls != null) {
            final Class<?>[] interfaces = cls.getInterfaces();

            for (final Class<?> i : interfaces) {
                if (interfacesFound.add(i)) {
                    getAllInterfaces(i, interfacesFound);
                }
            }

            cls = cls.getSuperclass();
        }
    }


    private GUtils() { }
}
