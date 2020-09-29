package bioobj;

import util.MoreArray;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * User: marcin
 * Date: Feb 18, 2007
 * Time: 1:24:10 PM
 */
public class ClusterMatch {

    public int[] starts;
    public int[] stops;
    public int[] pos;
    public int total_hits;
    public double[] scores;
    public String id;
    public String name;
    public String[] seqs;

    /**
     *
     */
    public ClusterMatch() {
        total_hits = -1;
        id = null;
        name = null;
    }

    /**
     *
     * @param i
     * @param n
     * @param s
     * @param t
     * @param p
     */
    public ClusterMatch(String i, String n, String[] s, int t, int[] p) {
        pos=p;
        total_hits = t;
        id = i;
        name = n;
        seqs = s;
    }

    public String toStringTrunc() {
        return "" + id + "\t" + name + "\t" +total_hits +"\t"+ MoreArray.toString(pos);
    }
}
