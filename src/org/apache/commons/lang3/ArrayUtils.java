/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package org.apache.commons.lang3;

public class ArrayUtils {

    public static char[] clone(char[] array) {
        return array != null ? (char[]) array.clone() : null;
    }

    public static <T> T[] clone(T[] array) {
        return array != null ? array.clone() : null;
    }

    public static <T> T arraycopy(T source, int sourcePos, T dest, int destPos, int length) {
        System.arraycopy(source, sourcePos, dest, destPos, length);
        return dest;
    }

}
