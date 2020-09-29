package dtype;

public class datd {
    float x;
    String s;
    float p;

    public datd(float a, String t, float b) {
        this.x = a;
        this.s = t;
        this.p = b;
    }

    public String toString() {
        return (x + "\t" + s + "\t" + p);
    }
}
