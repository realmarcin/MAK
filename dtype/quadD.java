package dtype;

import java.util.ArrayList;

/**
 * Quad of ints
 */
public class quadD {

    private ArrayList dubs;


    public quadD() {


        dubs = new ArrayList();
    }

    public quadD(int k) {

        dubs = new ArrayList(k);
    }

    public quadD(quadD c) {

        this.dubs = c.dubs;
    }

    public void add(double q) {

        if (this.dubs == null) {
            this.dubs = new ArrayList();
        }
        this.dubs.add(new Double(q));

    }

    public void add(Double q) {

        if (this.dubs == null) {
            this.dubs = new ArrayList();
        }
        this.dubs.add((Double) q);

    }

    public ArrayList retDubs() {

        return this.dubs;
    }
}

