package dtype;



public class sensidoub {
    public double x;
    public double y;
    public String s;


    public sensidoub(double a, double b, String t) {
        this.x = a;
        this.y = b;
        this.s = new String(t);
    }

    public String toString() {
        return (this.x + "\t" + this.y + "\t" + this.s);
    }
}
