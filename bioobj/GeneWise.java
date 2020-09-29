package bioobj;

public class GeneWise {
    public String homoseq;
    public String consseq;
    public String predseq;
    public String dnaseq;
    public String homoname;
    public String predname;

    public int homobegin;
    public int homoend;
    public int predbegin;
    public int predend;


    public GeneWise() {
        this.homoseq = null;
        this.consseq = null;
        this.predseq = null;
        this.dnaseq = null;
        this.homoname = null;
        this.predname = null;
        this.homobegin = 0;
        this.homoend = 0;
        this.predbegin = 0;
        this.predend = 0;
    }

    public GeneWise(String h, String c, String p, String d, String e, String f, int n1, int n2, int n3, int n4) {
        h = cleanSeq(h);
        this.homoseq = new String(h);
        this.consseq = new String(c);
        p = cleanSeq(p);
        this.predseq = new String(p);
        this.dnaseq = new String(d);
        this.homoname = new String(e);
        this.predname = new String(f);
        this.homobegin = n1;
        this.homoend = n2;
        this.predbegin = n3;
        this.predend = n4;
    }

    public GeneWise(GeneWise g) {
        //g.homoseq = cleanSeq(g.homoseq);
        this.homoseq = new String(g.homoseq);
        this.consseq = new String(g.consseq);
        //g.predseq = cleanSeq(g.predseq);
        this.predseq = new String(g.predseq);
        this.dnaseq = new String(g.dnaseq);
        this.homoname = new String(g.homoname);
        this.predname = new String(g.predname);
        this.homobegin = g.homobegin;
        this.homoend = g.homoend;
        this.predbegin = g.predbegin;
        this.predend = g.predend;
    }

    private String cleanSeq(String s) {
        int colon = s.indexOf(":");
        int closepar = s.indexOf("]", colon);
        while (colon != -1 && closepar != -1) {
            s = new String(s.substring(0, colon - 1) + s.substring(closepar + 1));
            colon = s.indexOf(":");
            closepar = s.indexOf("]", colon);
        }
        return s;
    }
}
