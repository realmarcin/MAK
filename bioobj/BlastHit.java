package bioobj;

import java.util.Vector;

public class BlastHit {
    public String seq;
    public String def;

    public String seqName;
    public Vector hsps;
    public int rank;
    public int query_len;
    public int subject_len;


    public BlastHit() {
        this.seq = null;
        this.seqName = null;
        this.rank = -1;
        this.query_len = -1;
        this.subject_len = -1;
    }

    public BlastHit(String name) {
        this.seq = null;
        this.seqName = new String(name);
        this.rank = -1;
        this.def = null;
        this.query_len = -1;
        this.subject_len = -1;
    }

    public BlastHit(BlastHit b) {
        this.seq = b.seq;
        this.seqName = new String(b.seqName);
        this.rank = b.rank;
        this.def = b.def;
        this.query_len = b.query_len;
        this.subject_len = b.subject_len;
        copyHsps(b.hsps);
    }

    private void copyHsps(Vector p) {
        if (this.hsps == null)
            hsps = new Vector();
        if (p != null)
            for (int i = 0; i < p.size(); i++) {
                hsps.addElement((BlastHSP) p.elementAt(i));
            }
    }

    public void addHSP(BlastHSP now) {
        if (this.hsps == null)
            hsps = new Vector();
        hsps.addElement(new BlastHSP(now));
    }

    public void addChartoSeq(char a) {
        this.seq += a;
    }

    public long[] minmaxHSP() {
        long[] ret = new long[2];
        ret[0] = 1000000000;
        ret[1] = 0;
        for (int i = 0; i < this.hsps.size(); i++) {
            BlastHSP bh = (BlastHSP) this.hsps.elementAt(i);
            if (bh != null) {
                if (bh.subject_min < ret[0])
                    ret[0] = bh.subject_min;
                if (bh.subject_max > ret[1])
                    ret[1] = bh.subject_max;
            }
        }
        return ret;
    }
}

