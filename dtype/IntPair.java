package dtype;

/**
 * Data type containing two ints.
 */
public class IntPair {

    public int x;
    public int y;

    /**
     * Constructor
     */
    public IntPair() {
    }

    /**
     * Constructor
     *
     * @param a int
     * @param b int
     */
    public IntPair(int a, int b) {
        x = a;
        y = b;
    }

    /**
     * Returns String value
     *
     * @return String
     */
    public String toString() {
        return (x + "\t" + y);
    }

    /**
     * @return
     */
    public String toStringCompact() {
        return (x + "." + y);
    }

    /**
     * @param ip
     * @return
     */
    public boolean equals(IntPair ip) {

        if (ip.x == x && ip.y == y)
            return true;
        else
            return false;
    }

    /**
     * For double-sorting an IntPair array.
     * dangerous default of 'false'
     *
     * @param ip
     * @return
     */
    public boolean islessThan(IntPair ip) {

        if (ip.x < x)
            return true;
        else if (ip.x == x) {
            if (ip.y < y)
                return true;
            else
                return false;
        } else
            return false;
    }

    /**
     * @param o
     * @return
     */
    public int compareToX(Object o) {
        if (((IntPair) o).x > x)
            return -1;
        else if (((IntPair) o).x == x)
            return 0;
        else
            return 1;
    }

    /**
     * @param o
     * @return
     */
    public int compareToY(Object o) {
        if (((IntPair) o).y > y)
            return -1;
        else if (((IntPair) o).y == y)
            return 0;
        else
            return 1;
    }


    /**
     * @param o
     * @return
     */
    public int compareToXAndY(Object o) {
        IntPair pair = (IntPair) o;
        if (pair.x > x)
            return -1;
        else if (pair.x == x && pair.y > y)
            return -1;
        else
            return 1;
    }

    public final static IntPair[] removeElement(IntPair[] a, int del) {
        //static void 	arraycopy(Object src, int srcPos, Object dest, int destPos, int length)
        //System.arraycopy(a, 0, a, 0, del);
        IntPair[] b = new IntPair[a.length - 1];
        if (del != 0)
            System.arraycopy(a, 0, b, 0, del);
        System.arraycopy(a, del + 1, b, del, a.length - del - 1);
        return b;
    }

}
