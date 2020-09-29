package bioobj;


public class TwoLongString {
    public long x;
    public long y;
    public String a;

    public TwoLongString(long one, long two, String s) {
        this.x = one;
        this.y = two;
        this.a = new String(s);
    }

    public TwoLongString() {
    }

    public String toString() {
        return ("x " + x + "\ty " + y + "\ta " + a);
    }
}
