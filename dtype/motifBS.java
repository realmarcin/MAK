package dtype;

public class motifBS {

    public String def;
    public String seq;

    public double raw_score;
    public double norm_score;
    public int start;
    public int stop;

    public motifBS() {

    }

    public motifBS(String s, String ss, double r, double n, int a, int o) {

        if (s != null)
            this.def = new String(s);
        if (ss != null)
            this.seq = new String(ss);
        this.raw_score = r;
        this.norm_score = n;
        this.start = a;
        this.stop = o;
    }

    public String toString() {

        return (this.def + "\t" + this.seq + "\t" + this.raw_score + "\t" + this.norm_score + "\t" + this.start + "\t" + this.stop);
    }
}
