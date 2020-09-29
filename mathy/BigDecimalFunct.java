package mathy;

import java.math.BigDecimal;

/**
 * Created by marcin
 * Date: Feb 10, 2006
 * Time: 12:32:01 PM
 */
public class BigDecimalFunct {

    /*public double divide(double a, double b) {

        BigDecimal d1 = new BigDecimal(a);
        BigDecimal s1 = new BigDecimal(b);
        s1 = s1.divide(new BigDecimal(2.0), 50, BigDecimal.ROUND_HALF_UP

        return s1.doubleValue();
    }*/

    public final static BigDecimal divide(double a, double b) {

        BigDecimal d1 = new BigDecimal(a);
        BigDecimal s1 = new BigDecimal(b);
        s1 = s1.divide(new BigDecimal(2.0), 50, BigDecimal.ROUND_HALF_UP);

        return s1;
    }
}
