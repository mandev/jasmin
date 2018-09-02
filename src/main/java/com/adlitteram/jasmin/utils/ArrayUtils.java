/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adlitteram.jasmin.utils;

/**
 *
 * @author Administrateur
 */
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
