package bioobj;

/**
 * User: mjoachimiak
 * Date: Oct 14, 2009
 * Time: 11:24:14 AM
 */
public class ReadsStretch {

    public String genelabels=null;
    public String predlabels= null;

    public int start;
    public int stop;
    public double avg;
    public double bg;

    /**
     *
     */
    public ReadsStretch() {
        start = -1;
        stop = -1;
        avg = Double.NaN;
        bg = Double.NaN;
    }

    /**
     * @param beg
     * @param end
     * @param a
     * @param b
     */
    public ReadsStretch(int beg, int end, double a, double b) {
        start = beg;
        stop = end;
        avg = a;
        bg = b;
    }

    /**
     * @param gl
     * @param pl
     * @param beg
     * @param end
     * @param a
     * @param b
     */
    public ReadsStretch(String gl, String pl,
                        int beg, int end, double a, double b) {
        genelabels = gl;
        predlabels = pl;

        start = beg;
        stop = end;
        avg = a;
        bg = b;
    }

}
