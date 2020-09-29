package dtype;

public class dat {
    float x;
    String s;

    public dat(float a, String b) {
        this.x = a;
        this.s = b;
    }

    public String toString() {
        return (x + "\t" + s);
    }
}
