package dtype;

/**
 * User: marcinjoachimiak
 * Date: Nov 8, 2007
 * Time: 12:27:09 AM
 */
public class StringPairDouble {

    public String a = null;
    public String b = null;
    public double d;

    /**
     * @param one
     * @param two
     * @param val
     */
    public StringPairDouble(String one, String two, double val) {
        this.a = one;
        this.b = two;
        this.d = val;
    }

    /**
     * @return
     */
    public String toString() {
        return (this.a + "\t" + this.b + "\t" + this.d);
    }
}
