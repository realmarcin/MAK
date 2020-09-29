package dtype;

import java.util.ArrayList;


/**
 *
 */
public class sensii {
    public int x;
    public int y;
    public String s1;
    public String s2;

    /**
     * @param a
     * @param b
     * @param t
     * @param z
     */
    public sensii(int a, int b, String t, String z) {
        this.x = a;
        this.y = b;
        this.s1 = t;
        this.s2 = z;
    }

    /**
     * @return
     */
    public String toString() {
        return (x + "\t" + y + "\t" + s1 + "\t" + s2);
    }

    /**
     * @param expr
     * @return
     */
    public int compareTo(Object expr) {
        if (((sensii) expr).x > x)
            return -1;
        else if (((sensii) expr).x == x)
            return 0;
        else
            return 1;
    }

    /**
     * @param s
     * @return
     */
    public final static ArrayList toList(Object[] s) {
        ArrayList a = new ArrayList();
        for (int i = 0; i < s.length; i++) {
            a.add((sensii)s[i]);
        }
        return a;
    }
}
