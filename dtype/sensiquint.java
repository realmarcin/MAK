package dtype;



public class sensiquint {
    public double a;
    public double b;
    public double c;
    public double d;
    public double e;

    public String s;


    public sensiquint(double a, double b, double c, double d, double e, String t) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.e = e;
        this.s = new String(t);
    }

    public String toString() {
        return (this.s + "\t" + this.a + "\t" + this.b + "\t" + this.c + "\t" + this.d + "\t" + this.e);
    }
}
