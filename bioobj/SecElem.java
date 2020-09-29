package bioobj;


/**
 * Double of float (x,y,z) for points in space.
 */
public class SecElem {
    public int cardinal;
    public int type;
    public int length;
    public int beg;
    public int end;

    public SecElem(int a, int b, int c, int d, int e) {
        cardinal = a;
        type = b;
        length = c;
        beg = d;
        end = e;
    }

    public String toString() {
        String[] types = {"COIL", "HELIX", "SHEET"};
        return ("cardinality " + cardinal + "\t" + types[(int) type] + "\t" + "length  " + length + "\t" + "beg  " + beg + "\t" + "end  " + end);
    }
}
