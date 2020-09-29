package dtype;

/**
 * object for storing and sorting tree/contact correlations.
 */
public class correl2 {

    public int po1;
    public int po2;

    public float nodediff;
    public float iddiff;
    public float score;

    public correl2(int a, int b, float e, float f, float p) {
        this.po1 = a;
        this.po2 = b;
        this.nodediff = e;
        this.iddiff = f;
        this.score = p;
    }

    public boolean equals(Object o) {
        correl2 i = (correl2) o;
        return (((po1 == i.po1) || (po1 == i.po2))
                && ((po2 == i.po2) || (po2 == i.po1)) && (nodediff == i.nodediff)
                && (iddiff == i.iddiff));
    }

    public String toString() {
        return ("(po1,po2): (" + this.po1 + "," + this.po2 + ") (nodediff,iddiff): (" + this.nodediff + "," + this.iddiff + ") score: " + this.score + "\n");
    }
}
