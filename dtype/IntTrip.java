package dtype;

/**
 * Data type containing two ints.
 */
public class IntTrip {
    public int x;
    public int y;
    public int z;


    /**
     * Constructor
     *
     * @param x int
     * @param y int
     * @param z int
     */
    public IntTrip(int a, int b, int c) {
        this.x = a;
        this.y = b;
        this.z = c;
    }

    /**
     * Returns String value
     *
     * @return String
     */
    public String toString() {
        return (x + "\t" + y + "\t" + z);
    }
}
