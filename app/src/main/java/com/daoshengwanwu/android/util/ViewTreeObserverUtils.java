package com.daoshengwanwu.android.util;


import android.graphics.Rect;
import android.graphics.Region;
import android.text.TextUtils;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;


// TODO:: 每当Android系统有新版本时，检查该类的兼容性
public class ViewTreeObserverUtils {
    private static final String CLASS_NAME_ON_COMPUTE_INTERNAL_INSETS_LISTENER = "android.view.ViewTreeObserver$OnComputeInternalInsetsListener";
    private static final Class<?> CLASS_ON_COMPUTE_INTERNAL_INSETS_LISTENER;

    private static final Map<OnComputeInternalInsetsListener, Object> LISTENER_OBJECT_MAP = new HashMap<>();


    static {
        try {
            CLASS_ON_COMPUTE_INTERNAL_INSETS_LISTENER = Class.forName(CLASS_NAME_ON_COMPUTE_INTERNAL_INSETS_LISTENER);
        } catch (Throwable e) {
            throw new RuntimeException("cannot load OnComputeInternalInsetsListener");
        }
    }


    public static void addOnComputeInternalInsetsListener(final @NonNull ViewTreeObserver observer,
                                                          final @NonNull OnComputeInternalInsetsListener listener) {

        if (observer == null || listener == null) {
            return;
        }

        final Object listenerWrapper = Proxy.newProxyInstance(
                ViewTreeObserverUtils.class.getClassLoader(),
                new Class<?>[]{ CLASS_ON_COMPUTE_INTERNAL_INSETS_LISTENER },
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        if (method == null || TextUtils.isEmpty(method.getName())) {
                            return null;
                        }

                        final String methodName = method.getName();
                        switch (methodName) {
                            case "onComputeInternalInsets": {
                                if (args == null || args.length != 1) {
                                    break;
                                }

                                final Object info = args[0];
                                if (info == null) {
                                    break;
                                }

                                final InternalInsetsInfo internalInsetsInfo = new InternalInsetsInfo();
                                internalInsetsInfo.readFromInternalInsetsInfo(info);
                                listener.onComputeInternalInsets(internalInsetsInfo);
                                internalInsetsInfo.writeToInternalInsetsInfo(info);

                                return null;
                            }

                            case "equals": {
                                if (args == null || args.length != 1) {
                                    return false;
                                }

                                return proxy == args[0];
                            }

                            default: break;
                        }

                        return method.invoke(listener, args);
                    }
                });

        LISTENER_OBJECT_MAP.put(listener, listenerWrapper);

        try {
            MethodUtils.invokeMethod(observer, "addOnComputeInternalInsetsListener", listenerWrapper);
        } catch (Throwable e) {
            throw new RuntimeException("cannot addOnComputeInternalInsetsListener");
        }
    }

    public static void removeOnComputeInternalInsetsListener(final @NonNull ViewTreeObserver observer,
                                                             final @NonNull OnComputeInternalInsetsListener listener) {

        final Object listenerWrapper = LISTENER_OBJECT_MAP.get(listener);
        if (listenerWrapper == null) {
            return;
        }

        try {
            MethodUtils.invokeMethod(observer, "removeOnComputeInternalInsetsListener", listenerWrapper);
        } catch (Throwable e) {
            throw new RuntimeException("cannot removeOnComputeInternalInsetsListener");
        }
    }


    public static final class InternalInsetsInfo {
        /**
         * Option for {@link #setTouchableInsets(int)}: the entire window frame
         * can be touched.
         */
        public static final int TOUCHABLE_INSETS_FRAME = 0;

        /**
         * Option for {@link #setTouchableInsets(int)}: the area inside of
         * the content insets can be touched.
         */
        public static final int TOUCHABLE_INSETS_CONTENT = 1;

        /**
         * Option for {@link #setTouchableInsets(int)}: the area inside of
         * the visible insets can be touched.
         */
        public static final int TOUCHABLE_INSETS_VISIBLE = 2;

        /**
         * Option for {@link #setTouchableInsets(int)}: the area inside of
         * the provided touchable region in {@link #touchableRegion} can be touched.
         */
        public static final int TOUCHABLE_INSETS_REGION = 3;


        /**
         * Offsets from the frame of the window at which the content of
         * windows behind it should be placed.
         */
        public final Rect contentInsets = new Rect();

        /**
         * Offsets from the frame of the window at which windows behind it
         * are visible.
         */
        public final Rect visibleInsets = new Rect();

        /**
         * Touchable region defined relative to the origin of the frame of the window.
         * Only used when {@link #setTouchableInsets(int)} is called with
         * the option {@link #TOUCHABLE_INSETS_REGION}.
         */
        public final Region touchableRegion = new Region();

        private int mTouchableInsets;


        /**
         * Set which parts of the window can be touched: either
         * {@link #TOUCHABLE_INSETS_FRAME}, {@link #TOUCHABLE_INSETS_CONTENT},
         * {@link #TOUCHABLE_INSETS_VISIBLE}, or {@link #TOUCHABLE_INSETS_REGION}.
         */
        public void setTouchableInsets(int val) {
            mTouchableInsets = val;
        }

        public void set(InternalInsetsInfo other) {
            contentInsets.set(other.contentInsets);
            visibleInsets.set(other.visibleInsets);
            touchableRegion.set(other.touchableRegion);
            mTouchableInsets = other.mTouchableInsets;
        }

        public void updateRegion(Region touchableRegion) {
            if (touchableRegion == null) {
                return;
            }

            this.touchableRegion.set(touchableRegion);
        }

        public void reset() {
            contentInsets.setEmpty();
            visibleInsets.setEmpty();
            touchableRegion.setEmpty();
            mTouchableInsets = TOUCHABLE_INSETS_FRAME;
        }

        public boolean isEmpty() {
            return contentInsets.isEmpty()
                    && visibleInsets.isEmpty()
                    && touchableRegion.isEmpty()
                    && mTouchableInsets == TOUCHABLE_INSETS_FRAME;
        }

        @Override
        public int hashCode() {
            int result = contentInsets.hashCode();
            result = 31 * result + visibleInsets.hashCode();
            result = 31 * result + touchableRegion.hashCode();
            result = 31 * result + mTouchableInsets;
            return result;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            InternalInsetsInfo other = (InternalInsetsInfo)o;
            return mTouchableInsets == other.mTouchableInsets &&
                    contentInsets.equals(other.contentInsets) &&
                    visibleInsets.equals(other.visibleInsets) &&
                    touchableRegion.equals(other.touchableRegion);
        }

        private void writeToInternalInsetsInfo(@NonNull Object info) {
            try {
                final Rect contentInsets = (Rect) FieldUtils.readField(info, "contentInsets");
                final Rect visibleInsets = (Rect) FieldUtils.readField(info, "visibleInsets");
                final Region touchableRegion = (Region) FieldUtils.readField(info, "touchableRegion");
                final int mTouchableInsets = (Integer) FieldUtils.readField(info, "mTouchableInsets");

                contentInsets.set(this.contentInsets);
                visibleInsets.set(this.visibleInsets);
                touchableRegion.set(this.touchableRegion);
                FieldUtils.writeField(info, "mTouchableInsets", this.mTouchableInsets);

            } catch (Throwable e) {
                throw new RuntimeException("cannot write to internal insets info");
            }
        }

        private void readFromInternalInsetsInfo(@NonNull Object info) {
            try {
                final Rect contentInsets = (Rect) FieldUtils.readField(info, "contentInsets");
                final Rect visibleInsets = (Rect) FieldUtils.readField(info, "visibleInsets");
                final Region touchableRegion = (Region) FieldUtils.readField(info, "touchableRegion");
                final int mTouchableInsets = (Integer) FieldUtils.readField(info, "mTouchableInsets");

                this.contentInsets.set(contentInsets);
                this.visibleInsets.set(visibleInsets);
                this.touchableRegion.set(touchableRegion);
                this.mTouchableInsets = mTouchableInsets;

            } catch (Throwable e) {
                throw new RuntimeException("cannot read from internal insets info");
            }
        }
    }


    public interface OnComputeInternalInsetsListener {
        void onComputeInternalInsets(InternalInsetsInfo inoutInfo);
    }
}
