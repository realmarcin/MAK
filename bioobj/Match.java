package bioobj;

/**
 * User: marcinjoachimiak
 * Date: Aug 2, 2007
 * Time: 2:30:28 PM
 */
public class Match {


    public int start;
    public int stop;
    public int pos;
    public int total_hits;
    public double score;
    public String id;
    public String name;
    public String seq;

    /**
     *
     */
    public Match() {
        total_hits = -1;
        id = null;
        name = null;
    }

    /**
     * @param i
     * @param t
     * @param sc
     * @param sta
     * @param sto
     * @param s
     */
    public Match(String i, int t, double sc, int sta, int sto, String s) {

        id = i;
        start = sta;
        stop = sto;
        total_hits = t;
        score = sc;
        seq = s;
    }

    public String toString() {
        return "" + id + "\t" + total_hits + "\t" + score + "\t" + start + "\t" + stop + "\t" + seq + "\tX";
    }
}

