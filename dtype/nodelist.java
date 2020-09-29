package dtype;

/**
 * Vector object for storing and sorting tree/contact correlations.
 */
public class nodelist {
    public int po1;
    public int po2;

    public float score;
    public String s;

    public nodelist(int a, int b, float p, String ta) {
        this.po1 = a;
        this.po2 = b;
        this.score = p;
        this.s = ta;
    }

    public boolean equals(Object o) {
        nodelist i = (nodelist) o;
        return ((po1 == i.po1) && (po2 == i.po2) & (score == i.score) && (s == i.s));
    }


    public String toString() {
        return ("(po1,po2): (" + this.po1 + "," + this.po2 + ") score: " + this.score + "\n" + " nodes: " + s + "\n");
    }
}
