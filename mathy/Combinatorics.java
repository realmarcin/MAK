package mathy;

/**
 * User: marcin
 * Date: Mar 2, 2010
 * Time: 3:24:34 PM
 */
public class Combinatorics {

    /**
     * @param n
     * @return
     */
    public final static long factorial(long n) {
        long result = 1;
        while (n != 0) {
            result = result * n;
            n = n - 1;
        }
        return result;
    }

    /**
     * @param n
     * @return
     */
    public final static long factorialUptoK(long n, long k) {
        long result = 1;
        while (n > k && n != 0) {
            result = result * n;
            n = n - 1;
        }
        return result;
    }

    /**
     * @param total
     * @param choose
     * @return
     */
    public final static long choose(long total, long choose) {
        if (total < choose)
            return 0;
        if (choose == 0 || choose == total)
            return 1;
        return choose(total - 1, choose - 1) + choose(total - 1, choose);
    }

     /**
     * @param n
     * @return
     */
    public final static long factorialStirlingApprox(long n) {
        long result = 1;
        while (n != 0) {
            result = result * n;
            n = n - 1;
        }
        return result;
    }

}
