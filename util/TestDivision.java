package util;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: Nov 1, 2012
 * Time: 11:47:42 AM
 */
public class TestDivision {

    /**
     *
     */
    public TestDivision() {

        System.out.println("1/2 " + (1 / 2));
        System.out.println("1/(double)2 " + (1 / (double) 2));
        System.out.println("1/2.0 " + (1 / 2.0));
        System.out.println("1.0/2.0 " + (1.0 / 2.0));
        System.out.println("1.0/2 " + (1.0 / 2));
    }

    /**
     * @param args
     */
    public final static void main(String[] args) {
        if (args.length == 0) {
            TestDivision rm = new TestDivision();
        } else {
            System.out.println("syntax: java util.TestDivision"
            );
        }
    }
}
