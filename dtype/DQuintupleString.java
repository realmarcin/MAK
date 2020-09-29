package dtype;

/**
 * User: marcinjoachimiak
 * Date: Nov 7, 2007
 * Time: 1:21:33 PM
 */
public class DQuintupleString {


    public double a;
    public double b;
    public double c;
    public double d;
    public double e;
    public String s;

    /**
     * 
     * @param a
     * @param b
     * @param c
     * @param d
     * @param e
     * @param str
     */
    public DQuintupleString(double a, double b, double c, double d, double e, String str) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.e = e;
        this.s = str;
    }

    public String toString() {
        return (a + "\t" + b + "\t" + c + "\t" + d + "\t" + e + "\t" + s);
    }

}
