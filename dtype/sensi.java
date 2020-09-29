package dtype;



public class sensi {
    public int x;
    public int y;
    public String s;


    public sensi(int a, int b, String t) {
        this.x = a;
        this.y = b;
        this.s = t;
    }

    public String toString() {
        return (this.x + "\t" + this.y + "\t" + this.s);
    }
}
