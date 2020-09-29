package dtype;

import java.util.ArrayList;


public class SensChar {

    public double x;
    public double y;
    public char[] s;
    public char[] so;

    public SensChar(double a, double b, char[] t) {
        x = a;
        y = b;
        s = t;
    }

    public SensChar(double a, double b, char[] t, char[] ti) {
        x = a;
        y = b;
        s = t;
        so = ti;
    }

    public String toString() {
        return (x + "\t" + y + "\t" + s + "\t" + so);
    }

    /**
     * @param expr
     * @return
     */
    public int compareTo(Object expr) {
        if (((SensChar) expr).x > x)
            return -1;
        else if (((SensChar) expr).x == x)
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
            a.add((SensChar) s[i]);
        }
        return a;
    }
}

