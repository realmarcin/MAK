package dtype;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * User: marcin
 * Date: Feb 1, 2007
 * Time: 7:30:21 PM
 */
public class TripleChar implements Serializable {
    int x;
    public char[] a;
    public char[] b;
    public char[] c;

    /**
     *
     */
    public TripleChar() {
        init();
    }

    private void init() {
        x = 0;
        a = null;
        b = null;
        c = null;
    }

    /**
     * @param x
     * @param y
     * @param z
     */
    public TripleChar(char[] x, char[] y, char[] z) {
        a = x;
        b = y;
        c = z;
    }

    /**
     * @param i
     * @param one
     * @param two
     * @param three
     */
    public TripleChar(int i, char[] one, char[] two, char[] three) {
        x = i;
        a = one;
        b = two;
        c = three;
    }


    /**
     * Writes an ArrayList of TripleChar objects to a file.
     *
     * @param a
     * @param f
     */
    public final static void write(ArrayList a, String f) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(f));
            for (int i = 0; i < a.size(); i++) {
                TripleChar cur = (TripleChar) a.get(i);
                out.println(new String(cur.a) + "\t" +
                        (cur.b!=null?new String(cur.b):"none") + "\t" + (cur.c!=null?new String(cur.c):"none"));
            }
            out.close();
        } catch (IOException e) {
            System.out.println("Error creating or writing file " + f);
            System.out.println("IOException: " + e.getMessage());
        }
    }
}
