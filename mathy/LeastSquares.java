package mathy;

/**
 * Created by Marcin
 * Date: Sep 1, 2005
 * Time: 10:30:27 PM
 */
public class LeastSquares {

    double[][] data;
    double[] denom;


    public LeastSquares(double[][] a, double[] b) {

        data = a;
        denom = b;

        /**
         *
         * Solve equations
         *
         * ai * Gobi = y1 * ai^2 + y2 * ai bi + y3 * ai ei + y4 * ai ri + y5 * ai vi
         * bi * Gobi = y1 * bi ai + y2 * bi^2 + y3 * bi ei + y4 * bi ri + y5 * bi vi
         * ei * Gobi = y1 * ei ai + y2 * ei bi + y3 * ei^2 + y4 * ei ri + y5 * ei vi
         * ri * Gobi = y1 * ri bi + y2 * ri bi + y3 * ri ei + y4 * ri^2 + y5 * ri vi
         * vi * Gobi = y1 * vi bi + y2 * vi bi + y3 * vi ei + y4 * vi ri + y5 * vi^2
         *
         * y1 = (ai * Gobi - (y2 * ai bi + y3 * ai ei + y4 * ai ri + y5 * ai vi)) / ai^2
         * y2 = (bi * Gobi - (y1 * bi ai + y3 * bi ei + y4 * bi ri + y5 * bi vi)) / bi^2
         * y5 =  (vi * Gobi - (y1 * vi bi + y2 * vi bi + y3 * vi ei + y4 * vi ri)) / vi^2
         */

        /**
         *
         * matirx
         *
         * ai^2 ai bi    ai ei    ai ri    ai vi        ai Gobi
         * bi ai bi^2    bi ei    bi ri    bi vi        bi Gobi
         * ei ai ei bi    ei^2    ei ri    ei vi        ei Gobi
         * ri ai ri bi    ri ei    ri^2    ri vi        ri Gobi
         * vi ai vi bi    vi ei    vi ri    vi^2        vi Gobi
         *
         */





    }


}
