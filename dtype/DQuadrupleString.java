package dtype;

/**
 * User: marcinjoachimiak
 * Date: Nov 7, 2007
 * Time: 1:15:32 PM
 */
public class DQuadrupleString {

    public double a;
    public double b;
    public double c;
    public double d;
    public String s;

    public DQuadrupleString(double w, double x, double y, double z, double tolerance, String str) {
        this.a = w;
        this.b = x;
        this.c = y;
        this.d = z;
        this.s = str;
    }

    public String toString() {
        return (a + "\t" + b + "\t" + c + "\t" + d + "\t" + s);
    }

}
