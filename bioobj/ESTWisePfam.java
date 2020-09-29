package bioobj;

public class ESTWisePfam {
    public String model;
    public String start_end;
    public String target;
    public String strand;
    public String codontable;
    public double subs_error;
    public double indel_error;
    public String algorithm;
    public double score;

    public ESTWisePfam() {
        this.model = null;
        this.start_end = null;
        this.target = null;
        this.strand = null;
        this.codontable = null;
        this.subs_error = -1;
        this.indel_error = -1;
        this.algorithm = null;
        this.score = -1;
    }

    public ESTWisePfam(ESTWisePfam e) {
        this.model = e.model;
        this.start_end = e.start_end;
        this.target = e.target;
        this.strand = e.strand;
        this.codontable = e.codontable;
        this.subs_error = e.subs_error;
        this.indel_error = e.indel_error;
        this.algorithm = e.algorithm;
        this.score = e.score;
    }
}
