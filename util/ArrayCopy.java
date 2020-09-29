package util;


/**
 *
 */
public class ArrayCopy {


    /**
     * @param src
     */
    public final static double[][] copy(double[][] src) {

        double[][] dest = new double[src.length][src[0].length];


        for (int i = 0; i < src.length; i++) {
            for (int j = 0; j < src[0].length; j++) {

                dest[i][j] = src[i][j];
            }

        }
        return dest;

    }

}
