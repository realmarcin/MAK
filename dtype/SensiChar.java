package dtype;

import java.util.ArrayList;

/**
 * User: marcin
 * Date: Feb 1, 2007
 * Time: 7:11:39 PM
 */
public class SensiChar {

    public int x;
    public int y;
    public char[] c;


    public SensiChar(int a, int b, char[] t) {
        this.x = a;
        this.y = b;
        this.c = t;
    }

    public String toString() {
        return (this.x + "\t" + this.y + "\t" + String.copyValueOf(this.c));
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
            a.add((SensiChar) s[i]);
        }
        return a;
    }
}

