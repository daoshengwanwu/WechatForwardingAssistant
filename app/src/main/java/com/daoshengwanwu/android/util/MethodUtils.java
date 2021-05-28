package com.daoshengwanwu.android.util;


import android.text.TextUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


public class MethodUtils {
    private static final Map<String, Method> sMethodCache = new HashMap<>();
    private static final HashMap<Class<?>, Class<?>> sPrimitiveToWrapperMap = new HashMap<>();


    static {
        sPrimitiveToWrapperMap.put(Boolean.TYPE, Boolean.class);
        sPrimitiveToWrapperMap.put(Byte.TYPE, Byte.class);
        sPrimitiveToWrapperMap.put(Character.TYPE, Character.class);
        sPrimitiveToWrapperMap.put(Short.TYPE, Short.class);
        sPrimitiveToWrapperMap.put(Integer.TYPE, Integer.class);
        sPrimitiveToWrapperMap.put(Long.TYPE, Long.class);
        sPrimitiveToWrapperMap.put(Double.TYPE, Double.class);
        sPrimitiveToWrapperMap.put(Float.TYPE, Float.class);
        sPrimitiveToWrapperMap.put(Void.TYPE, Void.class);
    }


    public static Method getAccessibleMethod(Class<?> cls, final String methodName, final Class<?>... parameterTypes) {
        Preconditions.checkNotNull(cls, "The class must not be null");
        Preconditions.checkTrue(!TextUtils.isEmpty(methodName), "The method name must not be blank");

        String key = getKey(cls, methodName, parameterTypes);
        Method targetMethod;
        synchronized (sMethodCache) {
            targetMethod = sMethodCache.get(key);
        }

        if (targetMethod != null) {
            if (!targetMethod.isAccessible()) {
                targetMethod.setAccessible(true);
            }

            return targetMethod;
        }

        while (cls != null) {
            try {
                targetMethod = cls.getDeclaredMethod(methodName, parameterTypes);
            } catch (NoSuchMethodException e) {
                // ignore
            }

            if (targetMethod == null) {
                Method[] declaredMethods = cls.getDeclaredMethods();
                for (Method method : declaredMethods) {
                    if (method == null || !TextUtils.equals(method.getName(), methodName)) {
                        continue;
                    }

                    Class<?>[] paramsTypes = method.getParameterTypes();
                    if (parameterTypes == null || parameterTypes.length != paramsTypes.length) {
                        continue;
                    }

                    boolean match = true;
                    for (int index = 0; index < parameterTypes.length; index++) {
                        if (!isAssignableTo(parameterTypes[index], paramsTypes[index])) {
                            match = false;
                        }
                    }

                    if (match) {
                        targetMethod = method;
                        break;
                    }
                }
            }

            if (targetMethod != null) {
                targetMethod.setAccessible(true);
                synchronized (sMethodCache) {
                    sMethodCache.put(key, targetMethod);
                }

                return targetMethod;
            }

            cls = cls.getSuperclass();
        }

        return null;
    }

    public static boolean isAssignableTo(Class<?> firstCls, Class<?> secondCls) {
        if (secondCls == null) {
            return false;
        }

        if (firstCls == null) {
            return !secondCls.isPrimitive();
        }

        if (firstCls.isPrimitive() && !secondCls.isPrimitive()) {
            firstCls = sPrimitiveToWrapperMap.get(firstCls);
        }

        if (secondCls.isPrimitive() && !firstCls.isPrimitive()) {
            secondCls = sPrimitiveToWrapperMap.get(secondCls);
        }

        return secondCls.isAssignableFrom(firstCls);
    }

    public static Object invokeStaticMethod(
            final Class<?> clazz,
            final String methodName,
            Class<?>[] parameterTypes,
            Object[] args)
            throws
            IllegalAccessException,
            InvocationTargetException {

        parameterTypes = GUtils.nullToEmpty(parameterTypes);
        args = GUtils.nullToEmpty(args);

        final Method method = getAccessibleMethod(clazz, methodName, parameterTypes);
        if (method != null) {
            return method.invoke(null, args);
        }

        return null;
    }

    public static Object invokeStaticMethod(
            final Class<?> clazz,
            final String methodName,
            Object... args)
            throws
            IllegalAccessException,
            InvocationTargetException {

        args = GUtils.nullToEmpty(args);
        final Class<?>[] parameterTypes = GUtils.toClassArray(args);

        return invokeStaticMethod(clazz, methodName, parameterTypes, args);
    }

    public static Object invokeMethod(
            final Object object,
            final String methodName,
            Object... args)
            throws
            IllegalAccessException,
            InvocationTargetException {

        args = GUtils.nullToEmpty(args);
        final Class<?>[] parameterTypes = GUtils.toClassArray(args);

        return invokeMethod(object, methodName, parameterTypes, args);
    }

    public static Object invokeMethod(
            final Object object,
            final String methodName,
            Class<?>[] parameterTypes,
            Object[] args)
            throws
            IllegalAccessException,
            InvocationTargetException {

        parameterTypes = GUtils.nullToEmpty(parameterTypes);
        args = GUtils.nullToEmpty(args);

        final Method method = getAccessibleMethod(object.getClass(), methodName, parameterTypes);

        if (method != null) {
            return method.invoke(object, args);
        }

        return null;
    }

    public static <T> T invokeConstructor(
            final Class<T> cls,
            Object... args)
            throws
            IllegalAccessException,
            InvocationTargetException,
            InstantiationException {

        args = GUtils.nullToEmpty(args);
        final Class<?>[] parameterTypes = GUtils.toClassArray(args);

        return invokeConstructor(cls, parameterTypes, args);
    }

    public static <T> T invokeConstructor(
            final Class<T> cls,
            Class<?>[] parameterTypes,
            Object[] args)
            throws
            IllegalAccessException,
            InvocationTargetException,
            InstantiationException {

        args = GUtils.nullToEmpty(args);
        parameterTypes = GUtils.nullToEmpty(parameterTypes);

        final Constructor<T> constructor = getMatchingAccessibleConstructor(cls, parameterTypes);

        if (constructor != null) {
            return constructor.newInstance(args);
        }

        return null;
    }

    public static <T> Constructor<T> getMatchingAccessibleConstructor(Class<T> cls, Class<?>... parameterTypes) {
        Preconditions.checkNotNull(cls, "class cannot be null");

        try {
            final Constructor<T> constructor = cls.getDeclaredConstructor(parameterTypes);
            if (!constructor.isAccessible()) {
                constructor.setAccessible(true);
            }

            return constructor;

        } catch (final NoSuchMethodException e) {
            // ignore
        }

        return null;
    }

    public static int getParameterTypeIndex(Method method, Class<?> paramType) {
        if (method != null && paramType != null) {
            Class<?>[] parameterTypes = method.getParameterTypes();

            if (parameterTypes.length > 0) {
                for (int i = 0; i < parameterTypes.length; i++) {
                    if (TextUtils.equals(paramType.getName(), parameterTypes[i].getName())) {
                        return i;
                    }
                }
            }
        }

        return -1;
    }

    private static String getKey(final Class<?> cls, final String methodName, final Class<?>... parameterTypes) {
        StringBuilder sb = new StringBuilder();
        sb.append(cls.toString()).append("#").append(methodName);

        if (parameterTypes != null && parameterTypes.length > 0) {
            for (Class<?> parameterType : parameterTypes) {
                sb.append("#").append(parameterType.toString());
            }

        } else {
            sb.append("#").append(Void.class.toString());
        }

        return sb.toString();
    }


    private MethodUtils() { }
}
