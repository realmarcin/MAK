package dtype;

/**
 * Data type containing three ints and a String.
 */
public class TripIntStr {
    public int x;
    public int y;
    public int z;
    public String a;

    /**
     * Constructor
     *
     * @param a int
     * @param b int
     * @param c int
     * @param a String
     */
    public TripIntStr(int a, int b, int c, String g) {
        this.x = a;
        this.y = b;
        this.z = c;
        this.a = g;
    }

    /**
     * Returns String value
     *
     * @return String
     */
    public String toString() {
        return (x + "\t" + y + "\t" + z + "\t" + a);
    }
}
