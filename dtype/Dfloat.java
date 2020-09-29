package dtype;

/**
 * float of float (x,y,z) for points in space.
 */
public class Dfloat {
    public float x;
    public float y;

    public Dfloat(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public String toString() {
        return (x + "\t" + y);
    }
}