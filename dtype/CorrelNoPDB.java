package dtype;

/**
 * object for storing and sorting tree/contact correlations.
 */
public class CorrelNoPDB {

    public int po1;
    public int po2;
    public float score;


    public CorrelNoPDB(int a, int b, float p) {
        this.po1 = a;
        this.po2 = b;
        this.score = p;
    }


    public String toString() {
        return ("(po1,po2): (" + this.po1 + "," + this.po2 + ")  score: " + this.score + "\n");
    }
}
