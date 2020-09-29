package mathy;

import junit.framework.TestCase;

/**
 * User: marcin
 * Date: Feb 23, 2008
 * Time: 5:05:20 PM
 */

public class CorrelationTest extends TestCase {

    /**
     * 
     * @param test
     */
    public CorrelationTest(String test) {
        super(test);
    }


    /**
     * The test.
     */
    public void test() throws Exception {
        double[] a = {1.0, 1.0, 1.0};
        double cor1 = Double.NaN;
        try {
            cor1 = stat.correlationCoeffSample(a, a);
            System.out.println("Identity correlation " + cor1);
        } catch (Exception ex) {
            //e.printStackTrace();
            System.out.println("AssertionFailed");
        }
        try {
            assertTrue("Identity correlation var=0 should be Double.NaN ", Double.NaN == cor1);
        } catch (junit.framework.AssertionFailedError ex) {
            // e.printStackTrace();
            System.out.println("AssertionFailed");
        }

        double[] b = {2.0, 2.0, 2.0};
        double cor2 = Double.NaN;
        try {
            cor2 = stat.correlationCoeffSample(a, b);
            System.out.println("Perfect linear correlation " + cor2);
        } catch (Exception ex) {
            //ex.printStackTrace();
        }
        try {
            assertTrue("Identity correlation var=0 should be Double.NaN ", Double.isNaN(cor2));
        } catch (junit.framework.AssertionFailedError ex) {
            //e.printStackTrace();
            System.out.println("AssertionFailed");
        }

        double[] c = {1.0, 2.0, 3.0};
        double cor3 = Double.NaN;
        try {
            cor3 = stat.correlationCoeffSample(c, c);
            System.out.println("Identity correlation var>0 " + cor3);
        } catch (Exception ex) {
            //e.printStackTrace();
            System.out.println("AssertionFailed");
        }
        try {
            assertTrue("Identity correlation var>0 should be 1 ",1.0f == cor3);
        } catch (junit.framework.AssertionFailedError ex) {
            //e.printStackTrace();
            System.out.println("AssertionFailed");
        }

        double[] d = {2.0, 3.0, 4.0};
        double cor4 = Double.NaN;
        try {
            cor4 = stat.correlationCoeffSample(c, d);
            System.out.println("Perfect linear correlation var>0 " + cor4);
        } catch (Exception ex) {
            //e.printStackTrace();
        }
        try {
            assertTrue("Perfect linear correlation var>0 should be 1 ", 1.0f == cor4);
        } catch (junit.framework.AssertionFailedError ex) {
            //e.printStackTrace();
            System.out.println("AssertionFailed");
        }

        double[] e = {
                0.6,
                1,
                4,
                6,
                10
        };
        double[] f = {
                123,
                1,
                3342,
                32,
                121
        };
        double cor5 = Double.NaN;
        try {
            cor5 = stat.correlationCoeffSample(e,f);
            System.out.println("Perfect linear correlation var>0 " + cor5);
        } catch (Exception ex) {
            //e.printStackTrace();
        }
        try {
            assertTrue("Perfect linear correlation var>0 should be 1 ",-0.03477201913837414 == cor5);
        } catch (junit.framework.AssertionFailedError ex) {
            //e.printStackTrace();
            System.out.println("AssertionFailed");
        }
    }

    /**
     * Called before every test.
     */
    protected void setUp() throws Exception {
    }

    /**
     * Called after every test.
     */
    protected void tearDown() throws Exception {
    }

}
