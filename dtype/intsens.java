package dtype;



public class intsens {
    public int x;
    public int y;
    public String s;

    public intsens(int a, int b, String t) {
        this.x = a;
        this.y = b;
        this.s = t;
    }

    public String toString() {
        return (x + "\t" + y + "\t" + s);
    }
}
