package dtype;

/**
 * Quad of ints
 */
public class Quad {

    public int a;
    public int b;
    public int c;
    public int d;

    public Quad(int x, int y, int z, int q) {
        this.a = x;
        this.b = y;
        this.c = z;
        this.d = q;
    }

    public Quad(Quad c) {
        this.a = c.a;
        this.b = c.b;
        this.c = c.c;
        this.d = c.d;
    }

    public String toString() {
        return (a + "\t" + b + "\t" + c + "\t" + d);
    }
}
