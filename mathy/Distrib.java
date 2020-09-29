package mathy;

/**
 * Element of a ranked distribution object. Used for example in the Wilcoxon test.
 */
public class Distrib {

    public double val;
    public char set = ' ';
    public int rank = -1;

    /**
     * @param b
     * @param whichset
     * @param whichrank
     */
    public Distrib(double b, char whichset, int whichrank) {

        val = b;
        set = whichset;
        rank = whichrank;
    }

    /**
     * @param b
     * @param whichset
     */
    public Distrib(double b, char whichset) {

        val = b;
        set = whichset;
    }

    /**
     * @param b
     */
    public Distrib(double b) {

        val = b;
    }

    /**
     * @return
     */
    public String toString() {

        String ret = null;
        if (set != ' ' && rank != -1)
            ret = "VAL: " + val + "\tSET: " + set + "\tRANK: " + rank;
        else if (rank != -1)
            ret = "VAL: " + val + "\tRANK: " + rank;
        else if (set != -1)
            ret = "VAL: " + val + "\tSET: " + set;
        return ret;
    }

}
