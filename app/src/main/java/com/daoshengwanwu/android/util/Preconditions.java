package com.daoshengwanwu.android.util;


public final class Preconditions {
    public static void checkArgument(boolean b, String errorMessage) {
        if (!b) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public static void checkArgument(boolean b, String errorMessageTemplate, Object... values) {
        if (!b) {
            throw new IllegalArgumentException(String.format(errorMessageTemplate, values));
        }
    }

    public static <T> T checkNotNull(T reference, String errorMessage) {
        if (reference == null) {
            throw new IllegalArgumentException(errorMessage);
        }

        return reference;
    }

    public static <T> T checkNotNull(T reference, String errorMessageTemplate, Object... values) {
        if (reference == null) {
            throw new IllegalArgumentException(String.format(errorMessageTemplate, values));
        }

        return reference;
    }

    public static int checkElementIndex(int index, int size, String errorMessage) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(errorMessage);
        }

        return index;
    }

    public static int checkElementIndex(int index, int size, String errorMessageTemplate, Object... values) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(String.format(errorMessageTemplate, values));
        }

        return index;
    }

    public static void checkTrue(boolean b, String errorMessage) {
        if (!b) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public static void checkTrue(boolean b, String errorMessageTemplate, Object... values) {
        if (!b) {
            throw new IllegalArgumentException(String.format(errorMessageTemplate, values));
        }
    }


    private Preconditions() { }
}
