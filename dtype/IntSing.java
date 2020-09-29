package dtype;

/**
 * Data type containing two ints.
 */
public class IntSing {
    public int x;
    public String s;

    /**
     * Constructor
     *
     * @param a int
     */
    public IntSing(int a) {
        this.x = a;
    }

    public IntSing(int a, String t) {
        this.x = a;
        this.s = t;
    }

    /**
     * Returns String value
     *
     * @return String
     */
    public String toString() {
        return (x + "\t"+s);
    }
}
