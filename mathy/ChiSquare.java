package mathy;

/**
 * Calcs the chi-square statistic for a data set.
 */
public class ChiSquare {

    final static double factor = 1.0 / Math.sqrt(2 * Math.PI);


    /**
     *
     */
    public ChiSquare(double[] d) {


        double sum = 0;
        for (int i = 0; i < d.length; i++) {

            double exp = calcExpect(d[i]);
            sum += Math.pow(exp - d[i], 2) / exp;
        }


    }

    /**
     * @param x
     * @return
     */
    public final static double calcExpect(double x) {


        double y = factor * Math.exp(-.5 * x * x);

        return y;
    }
}