package dtype;

/**
 * object for storing and sorting tree/contact correlations.
 */
public class Correl {

    public int po1;
    public int po2;
    public int pa1;
    public int pa2;
    public double nodediff;
    public double iddiff;
    public double score;


    public Correl(int a, int b, int e, int f, double p) {
        po1 = a;
        po2 = b;
        pa1 = e;
        pa2 = f;
        score = p;
    }

    public boolean equals(Object o) {
        Correl i = (Correl) o;
        return (((po1 == i.po1) || (po1 == i.po2))
                && ((po2 == i.po2) || (po2 == i.po1)) && ((pa1 == i.pa1) || (pa1 == i.pa2))
                && ((pa2 == i.pa2) || (pa2 == i.pa1)));
    }


    public String toString() {
        return ("(po1,po2): (" + po1 + "," + po2 + ") (pa1,pa2): (" + pa1 + "," + pa2 + ") score: " + score + "\n");
    }
}
