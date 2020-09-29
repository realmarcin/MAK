package dtype;

/**
 * Double of float (x,y,z) for points in space.
 */
public class quint {
    public float a;
    public float b;
    public float c;
    public float d;
    public float e;

    public quint(float a, float b, float c, float d, float e) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.e = e;
    }

    public String toString() {
        return (a + "\t" + b + "\t" + c + "\t" + d + "\t" + e);
    }
}
