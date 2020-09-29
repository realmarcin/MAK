package bioobj;


/**
 * Class of variables and methods for display of sequences.
 */
public class SSE {


//size = number of pdb structured residues
    public int res;

//0 helix, 1, sheet, 2 ssbond
    public int type;

    public SSE(int s, int p) {
        res = s;
        type = p;
    }

    public String toString() {
        return (res + "\t" + type);
    }
}
