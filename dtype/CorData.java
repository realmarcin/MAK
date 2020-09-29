package dtype;

import java.util.Vector;

/**
 * object for storing and sorting tree/contact correlations.
 */
public class CorData {

    public int po1;
    public int po2;
    public Vector nodeC;
    public double dist;
    public double seqsepar;

    public CorData(int a, int b, Vector y, double f) {
        po1 = a;
        po2 = b;
        nodeC = y;
        dist = f;
        seqsepar = Math.abs(po1 - po2);
    }

    public String toString() {
        return ("po1: " + po1 + " po2: " + po2 + " dist: " + dist);
    }
}
