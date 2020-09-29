package mathy;

import java.math.BigInteger;

/**
 * Class to calculate the factorial of an integer.
 */
public class Factorial {


    int limit = 0;
    final static BigInteger minusone = new BigInteger("" + -1);


    /**
     * @param n
     * @return
     */
    public static double calcD(int n) {

        BigInteger result = new BigInteger("" + n);

        result = run(result, n, 0);

        //System.out.println("Factorial double "+n+"\t"+result.doubleValue());

        return result.doubleValue();
    }

    /**
     * @param n
     * @return
     */
    public static double calcD(int n, int m) {

        BigInteger result = new BigInteger("" + n);

        result = run(result, n, m);

        return result.doubleValue();
    }

    /**
     * @param n
     * @return
     */
    public static int calc(int n) {

        BigInteger result = new BigInteger("" + n);

        result = run(result, n, 0);
        //System.out.println("Factorial int "+n+"\t"+result.intValue());
        return result.intValue();
    }

    /**
     * @param n
     * @return
     */
    public static int calc(int n, int m) {

        BigInteger result = new BigInteger("" + n);

        result = run(result, n, m);

        return result.intValue();
    }

    /**
     * @param b
     * @param i
     * @return
     */
    static BigInteger run(BigInteger b, int i, int j) {

        b = timesLoop(b, i, j);
        return b;
    }

    /**
     * @param num
     * @param a
     * @return
     */
    static BigInteger timesLoop(BigInteger num, int a, int b) {
        if ((a - 1) == b)
            return num;
        else {

            BigInteger next = new BigInteger("" + (a - 1));
            //System.out.println("mult "+num+"\t"+next);
            BigInteger pass = num.multiply(next);
            //System.out.println("iteration "+(a-1)+"\t"+pass);
            return timesLoop(pass, a - 1, b);
        }

    }

    public static void main(String[] args) {


        if (args.length == 1) {


            int num = Integer.parseInt(args[0]);

            calc(num);
        } else {


        }

    }

}