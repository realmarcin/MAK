package dtype;

/**
 * Double of float (x,y,z) for points in space.
 */
public class DDouble {
    public double x;
    public double y;

    public DDouble(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public String toString() {
        return (this.x + "\t" + this.y);
    }
}
