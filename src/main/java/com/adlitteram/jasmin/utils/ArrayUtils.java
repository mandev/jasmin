package com.adlitteram.jasmin.utils;

public class ArrayUtils {

    public static int indexOf(String[] array, String str) {
        for (int i = 0; i < array.length; i++) {
            if (str.equals(array[i])) {
                return i;
            }
        }
        return -1;
    }

}
