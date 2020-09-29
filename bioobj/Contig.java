package bioobj;

public class Contig {
    public long begin;
    public long end;
    public long qbegin;
    public long qend;
    public String seq;

    public Contig(long b, long e, String s) {
        begin = b;
        end = e;
        seq = new String(s);
    }

    public Contig(long b, long e, long qb, long qe, String s) {
        begin = b;
        end = e;
        qbegin = qb;
        qend = qe;
        seq = new String(s);
    }
}