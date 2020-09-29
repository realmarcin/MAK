package dtype;

/**
 * User: marcinjoachimiak
 * Date: Nov 7, 2007
 * Time: 1:12:53 PM
 */
public class DTripleString {

    public double a;
    public double b;
    public double c;
    public String s;

    public DTripleString(double x, double y, double z, String str) {
        this.a = x;
        this.b = y;
        this.c = z;
        this.s = str;
    }

    public DTripleString(DTriple t) {
        this.a = t.a;
        this.b = t.b;
        this.c = t.c;
    }

    public String toString() {
        return (a + "\t" + b + "\t" + c + "\t" + s);
    }

}
