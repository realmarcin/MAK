package dtype;

/**
 * Class encoding a Branch object for tree construction. *Uses two arrays x, y to store the Branch coordinates.
 * Coordinates are of type float.
 *
 * @Version 1.0 1/1/98
 * @Author Marcin Joachimiak
 */
public class branchfloat {
    public float[] x = null;
    public float[] y = null;

    public branchfloat() {
        this.x = new float[4];
        this.y = new float[4];
    }

    public branchfloat(branchfloat b) {
        if (b != null) {
            this.x = new float[4];
            this.y = new float[4];
            for (int i = 0; i < 4; i++) {
                this.x[i] = b.x[i];
                this.y[i] = b.y[i];
            }
        }
    }

    public String toString() {
        String s = new String();
        for (int i = 0; i < 4; i++) {
            String c = (i == 3) ? "\n" : "\t";
            s += "(" + x[i] + "," + y[i] + ")" + c;
        }
        return s;
    }
}
