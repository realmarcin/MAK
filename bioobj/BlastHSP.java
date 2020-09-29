package bioobj;


/**
 * Object for a BLAST result HSP.
 */
public class BlastHSP {

    public int blastbits;
    public double blastscore;
    public double blasteval;
    public int identity;
    public int alilen;
    public String[] strands;
    public int rank;

    public long query_min;
    public long query_max;
    public long subject_min;
    public long subject_max;

    public String query_seq;
    public String subject_seq;
    public int query_len;
    public int subject_len;


    /**
     *
     */
    public BlastHSP() {

        blastbits = 10000000;
        blasteval = 10000000;
        identity = 10000000;
        alilen = 10000000;
        strands = null;

        query_min = 1000000000;
        query_max = -1;
        subject_min = 1000000000;
        subject_max = -1;
        subject_seq = "";
        query_seq = "";
        query_len = -1;
        subject_len = -1;
    }

    /**
     * @param bhsp
     */
    public BlastHSP(BlastHSP bhsp) {

        //this();

        blastbits = bhsp.blastbits;
        blasteval = bhsp.blasteval;
        identity = bhsp.identity;
        alilen = bhsp.alilen;
        if (bhsp.strands != null) {
            strands = new String[2];
            strands[0] = bhsp.strands[0];
            strands[1] = bhsp.strands[1];
        }
        query_min = bhsp.query_min;
        query_max = bhsp.query_max;
        subject_min = bhsp.subject_min;
        subject_max = bhsp.subject_max;
        rank = bhsp.rank;
        query_seq = bhsp.query_seq;
        subject_seq = bhsp.subject_seq;
        query_len = bhsp.query_len;
        subject_len = bhsp.subject_len;
    }

    /**
     * @param bits
     * @param ev
     * @param id
     * @param allen
     * @param strnds
     * @param r
     * @param a
     * @param b
     * @param c
     * @param d
     */
    public BlastHSP(int bits, double ev, int id, int allen, String[] strnds, int r, long a, long b, long c, long d) {

        blastbits = bits;
        blasteval = ev;
        identity = id;
        alilen = allen;
        if (strnds != null) {
            strands = new String[2];
            strands[0] = strnds[0];
            strands[1] = strnds[1];
        }


        query_min = a;
        query_max = b;
        subject_min = c;
        subject_max = d;
        rank = r;
    }

    /**
     * @param bits
     * @param ev
     * @param id
     * @param allen
     * @param strnds
     * @param r
     */
    public BlastHSP(int bits, double ev, int id, int allen, String[] strnds, int r) {
        blastbits = bits;
        blasteval = ev;
        identity = id;
        alilen = allen;

        if (strnds != null) {
            strands = new String[2];
            strands[0] = strnds[0];
            strands[1] = strnds[1];
        }

        rank = r;
    }

    /**
     * @return
     */
    public String toString() {

        String ret = blastbits + " : " + blasteval + " : " + identity + " : " + alilen + " : " + strands + " : " + rank;
        //System.out.println(ret);
        return ret;
    }
}
