package dtype;

/**
 * DTriple of double (x,y,z) for points in space.
 */
public class DTriple {
    public double a;
    public double b;
    public double c;


    public DTriple(double x, double y, double z) {
        this.a = x;
        this.b = y;
        this.c = z;
    }

    public DTriple(DTriple t) {
        this.a = t.a;
        this.b = t.b;
        this.c = t.c;
    }

    public String toString() {
        return (a + "\t" + b + "\t" + c);
    }
}
