/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maths;

import java.lang.reflect.Array;
import java.util.Objects;

/**
 * @author zachs
 */
public class ArrayUtils {

    public static int maxPosition(double[] array) {
        Objects.requireNonNull(array);

        if (array.length == 0) {
            return -1;
        }

        int position = 0;
        double max = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
                position = i;
            }
        }

        return position;
    }

    public static <T> T[][] flat(T[][][] array, Class<T> type) {
        return flat(array, type, 0, array.length);
    }

    public static <T> T[][] flat(T[][][] array, Class<T> type, int from) {
        return flat(array, type, from, array.length);
    }

    public static <T> T[][] flat(T[][][] array, Class<T> type, int from, int to) {
        if (from < 0 || to < 0) throw new IllegalArgumentException(
            "Arguments from must be positive numbers"
        );
        if (from > to) throw new IllegalArgumentException(
            "Argument from must be smaller than argument to"
        );

        int flatSize = 0;

        for (int i = from; i < to; i++) {
            flatSize += array[i].length;
        }


        T[][] flatArray = (T[][]) Array.newInstance(type, flatSize, 0);

        int startCopyPos = 0;
        for (int i = from; i < to; i++) {
            System.arraycopy(array[i], 0, flatArray, startCopyPos, array[i].length);
            startCopyPos += array[i].length;
        }

        return flatArray;
    }

    public static Object[][] flat(Object[][][] array, Object[][] flatArray) {
        int flatSize = 0;

        for (Object[][] ar : array) {
            flatSize += ar.length;
        }

        int startCopyPos = 0;
        for (Object[][] ar : array) {
            System.arraycopy(ar, 0, flatArray, startCopyPos, ar.length);
            startCopyPos += ar.length;
        }

        return flatArray;
    }

    private ArrayUtils() {
    }
}
