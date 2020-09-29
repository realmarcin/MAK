package dtype;


/**
 * Class of variables and methods for display of sequences.
 */
public class sse {


//size = number of pdb structured residues
    public int res;

//0 helix, 1, sheet, 2 ssbond
    public int type;

    public sse(int s, int p) {
        this.res = s;
        this.type = p;
    }

    public String toString() {
        return (res + "\t" + type);
    }
}
