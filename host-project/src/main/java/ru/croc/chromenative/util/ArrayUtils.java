package ru.croc.chromenative.util;

/**
 * agumenyuk 19.07.2017.
 */
public class ArrayUtils {

    public static byte[] reverse(final byte[] array) {
        int i = 0;
        int j = array.length - 1;
        byte tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
        return array;
    }
}
