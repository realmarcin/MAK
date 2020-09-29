package dtype;

/**
 * Tetruple of float (x,y,z) for points in space.
 */
public class tetruple {
    public float x;
    public float y;
    public float z;
    public float w;

    public tetruple(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public String toString() {
        return (x + "\t" + y + "\t" + z + "\t" + w);
    }
}
