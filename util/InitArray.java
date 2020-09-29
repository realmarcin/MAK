package util;

/**
 * Simple class to initialize an array with a value.
 */
public class InitArray {


    public final static int[] initArray(int len, int val) {

        int[] ret = new int[len];
        for (int i = 0; i < len; i++) {

            ret[i] = val;
        }

        return ret;
    }

    public final static boolean[] initBoolean(int len, boolean val) {

        boolean[] ret = new boolean[len];
        for (int i = 0; i < len; i++) {

            ret[i] = val;
        }

        return ret;
    }
}