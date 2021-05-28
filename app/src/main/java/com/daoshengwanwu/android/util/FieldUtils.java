package com.daoshengwanwu.android.util;


import android.text.TextUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;


public class FieldUtils {
    private static final Map<String, Field> sFieldCache = new HashMap<>();


    public static Field getField(Class<?> cls, String fieldName) {
        Preconditions.checkNotNull(cls, "The class must not be null");
        Preconditions.checkTrue(!TextUtils.isEmpty(fieldName), "The field name must not be blank");

        String key = getKey(cls, fieldName);
        Field cachedField;
        synchronized (sFieldCache) {
            cachedField = sFieldCache.get(key);
        }

        if (cachedField != null) {
            if (!cachedField.isAccessible()) {
                cachedField.setAccessible(true);
            }

            return cachedField;
        }

        while (cls != null) {
            try {
                Field field = cls.getDeclaredField(fieldName);
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }

                synchronized (sFieldCache) {
                    sFieldCache.put(key, field);
                }

                return field;
            } catch (final NoSuchFieldException e) {
                // ignore
            }

            cls = cls.getSuperclass();
        }

        return null;
    }

    public static Object readField(Field field, Object target) throws IllegalAccessException {
        Preconditions.checkArgument(field != null, "The field must not be null");

        if (!field.isAccessible()) {
            field.setAccessible(true);
        }

        return field.get(target);
    }

    public static Object readField(Object target, String fieldName) throws IllegalAccessException {
        Preconditions.checkNotNull(target, "target object must not be null");

        Class<?> cls = target.getClass();
        Field field = getField(cls, fieldName);

        if (field != null) {
            return readField(field, target);
        }

        return null;
    }

    public static Object readStaticField(Field field) throws IllegalAccessException {
        Preconditions.checkNotNull(field, "The field must not be null");
        return readField(field, (Object) null);
    }

    public static Object readStaticField(Class<?> cls, String fieldName) throws IllegalAccessException {
        Field field = getField(cls, fieldName);

        if (field != null) {
            return readStaticField(field);
        }

        return null;
    }

    public static void writeField(Field field, Object target, Object value) throws IllegalAccessException {
        Preconditions.checkNotNull(field, "The field must not be null");

        if (!field.isAccessible()) {
            field.setAccessible(true);
        }

        field.set(target, value);
    }

    public static void writeField(Object target, String fieldName, Object value) throws IllegalAccessException {
        Preconditions.checkNotNull(target, "target object must not be null");

        Class<?> cls = target.getClass();
        Field field = getField(cls, fieldName);

        if (field != null) {
            writeField(field, target, value);
        }
    }

    public static void writeStaticField(Field field, Object value) throws IllegalAccessException {
        Preconditions.checkNotNull(field, "The field must not be null");
        writeField(field, (Object) null, value);
    }

    public static void writeStaticField(Class<?> cls, String fieldName, Object value) throws IllegalAccessException {
        Field field = getField(cls, fieldName);

        if (field != null) {
            writeStaticField(field, value);
        }
    }

    private static String getKey(Class<?> cls, String fieldName) {
        StringBuilder sb = new StringBuilder();

        sb.append(cls.toString()).append("#").append(fieldName);

        return sb.toString();
    }


    private FieldUtils() { }
}
