package bioobj;

public class SIM4piece {
    public String queryname;
    public String hitname;
    public int querylen;
    public int query_min;
    public int query_max;
    public long hit_min;
    public long hit_max;
    public int id;
    public int alilen;
    public String relation;
    public String queryseq;
    public String hitseq;
    public String alichars;

    public SIM4piece() {
        this.queryname = null;
        this.hitname = null;
        this.querylen = -1;
        this.query_min = -1;
        this.query_max = -1;
        this.hit_min = -1;
        this.hit_max = -1;
        this.id = -1;
        this.relation = null;
        this.queryseq = new String("");
        this.hitseq = new String("");
        this.alichars = new String("");
    }

    public SIM4piece(SIM4piece s4) {
        this.queryname = s4.queryname;
        this.hitname = s4.hitname;
        this.querylen = s4.querylen;
        this.query_min = s4.query_min;
        this.query_max = s4.query_max;
        this.hit_min = s4.hit_min;
        this.hit_max = s4.hit_max;
        this.id = s4.id;
        if (s4.relation != null)
            this.relation = new String(s4.relation);
        if (s4.queryseq != null)
            this.queryseq = new String(s4.queryseq);
        if (s4.hitseq != null)
            this.hitseq = new String(s4.hitseq);
        if (s4.alichars != null)
            this.alichars = new String(s4.alichars);

    }
}
