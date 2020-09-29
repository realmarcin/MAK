package util;

import java.util.ArrayList;

public class Array {

    /**
     * @param from
     * @param to
     * @return
     */
    public final static double[] copy(double[] from, double[] to) {

        to = new double[from.length];

        for (int i = 0; i < from.length; i++)
            to[i] = from[i];

        return to;
    }

    /**
     * @param from
     * @param to
     * @return
     */
    public final static double[][] copy(double[][] from, double[][] to) {

        to = new double[from.length][from[0].length];

        for (int i = 0; i < from.length; i++)
            for (int j = 0; j < from[0].length; j++)
                to[i][j] = from[i][j];

        return to;
    }

    /**
     * @param g
     * @return
     */
    public final static double[][] conv(ArrayList g) {
        double[] first = (double[]) g.get(0);
        int x = first.length;
        int y = g.size();
        System.out.println("Array.conv x " + x + "\ty " + y);
        double[][] ret = new double[x][y];
        for (int i = 0; i < y; i++) {
            double[] tmp = (double[]) g.get(i);
            for (int j = 0; j < x; j++) {
                ret[j][i] = tmp[j];

            }
        }
        return ret;
    }

    /**
     * @param a
     */
    public final static void printArray(int[] a) {
        //System.out.println("Printing int array of length "+a.length);
        for (int i = 0; i < a.length; i++)
            System.out.println(a[i]);
    }


    /**
     * @param a
     */
    public final static void printArray(int[] a, int cutoff) {
        //System.out.println("Printing int array of length "+a.length);
        for (int i = 0; i < a.length; i++) {
            if (a[i] > cutoff)
                System.out.println(a[i]);
        }
    }

    /**
     * @param a
     * @param s
     * @return
     */
    public final static int getArrayInd(ArrayList a, String s) {
        for (int i = 0; i < a.size(); i++) {
            String n = (String) a.get(i);
            if (n.equals(s)) {
                //System.out.println("getArrayInd "+i+"\t"+n+"\t"+s);
                return i;
            }
        }
        return -1;
    }


    /**
     * Function to copy object contents of a vector.
     */
    public final static ArrayList copyArrayList(ArrayList v) {
        ArrayList r = new ArrayList(v.size());
        for (int i = 0; i < v.size(); i++) {
            Object o = v.get(i);
            r.add(o);
        }
        return r;
    }
}
