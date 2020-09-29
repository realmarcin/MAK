package bioobj;

import java.util.ArrayList;


public class Feature {

    public double score;
    public int start;
    public int stop;
    public String seq;
    boolean mrna = false;

    /**
     *
     */
    public Feature() {

        score = 0;
        seq = null;
        start = -1;
        stop = -1;
    }

    /**
     * @param m
     */
    public Feature(boolean m) {

        score = 0;
        seq = null;
        start = -1;
        stop = -1;

        mrna = m;
    }

    /**
     * @param a
     */
    public Feature(ArrayList a) {

        start = ((Integer) a.get(0)).intValue() + 6;
        stop = ((Integer) a.get(1)).intValue();
        score = ((Double) a.get(2)).doubleValue();
        seq = (String) a.get(3);
        seq = seq.toUpperCase();

        if (mrna)
            seq = util.StringUtil.replace(seq, "T", "U");
    }


    /**
     * @param a
     */
    public Feature(ArrayList a, boolean m) {

        mrna = m;

        start = ((Integer) a.get(0)).intValue() + 6;
        stop = ((Integer) a.get(1)).intValue();
        score = ((Double) a.get(2)).doubleValue();
        seq = (String) a.get(3);
        seq = seq.toUpperCase();

        if (mrna)
            seq = util.StringUtil.replace(seq, "T", "U");
    }
}