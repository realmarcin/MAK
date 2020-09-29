package dtype;

/**
 *
 */
public class IntDouble {
    public int x;
    public double y;

    public IntDouble(int a, double b) {
        this.x = a;
        this.y = b;
    }

    public String toString() {
        return (x + "\t" + y);
    }
}