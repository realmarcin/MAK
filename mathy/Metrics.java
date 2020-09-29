package mathy;


public class Metrics {

    /**
     * Finds 3D Euclidean distance of double.
     */
    public static double euc3dist(double x1, double y1, double z1, double x2, double y2, double z2) {
        return (Math.sqrt(((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2) + (z1 - z2) * (z1 - z2))));
    }

    /**
     * Finds 2D Euclidean distance of double.
     */
    public static double euc2dist(double x1, double y1, double x2, double y2) {
        return (Math.sqrt(((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2))));
    }

}