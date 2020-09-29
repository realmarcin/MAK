package dtype;

import java.util.ArrayList;

/**
 * User: marcinjoachimiak
 * Date: Sep 11, 2008
 * Time: 4:02:46 PM
 */
public class sensiiiStringBuffer {


    public int x;
    public int y;
    public int a;
    public int b;
    public String s1;
    public StringBuffer s2;

    /**
     * @param s
     */
    public sensiiiStringBuffer(sensiiiStringBuffer s) {
        this.x = s.x;
        this.y = s.y;
        this.a = s.a;
        this.b = s.b;
        this.s1 = s.s1;
        this.s2 = s.s2;
    }

    /**
     * @param a
     * @param b
     * @param t
     * @param z
     */
    public sensiiiStringBuffer(int a, int b, int c, int d, String t, StringBuffer z) {
        this.x = a;
        this.y = b;
        this.a = c;
        this.b = d;
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
     * @return
     */
    public String toStringB() {
        return (a + "\t" + b + "\t" + s1);
    }

    /**
     * @return
     */
    public String toStringFull() {
        return ("x "+x + "\ty " + y + "\ta " + a + "\tb " + b + "\ts1 " + s1 + "\ts2 " + s2);
    }

    /**
     * @param expr
     * @return
     */
    public int compareTo(Object expr) {
        if (((sensiiiStringBuffer) expr).a > a)
            return -1;
        else if (((sensiiiStringBuffer) expr).a == a)
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
            a.add((sensiiiStringBuffer) s[i]);
        }
        return a;
    }


}
