package dtype;

/**
 * Triple of double (x,y,z) for points in space.
 */
public class Triple {
    public double x;
    public double y;
    public double z;


    public Triple(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Triple(Triple t) {
        this.x = t.x;
        this.y = t.y;
        this.z = t.z;
    }

    public String toString() {
        return (x + "\t" + y + "\t" + z);
    }
}
