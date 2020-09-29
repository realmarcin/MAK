package dtype;

/**
 * Class for representing atomic coordaintes based on the PDB format.
 * <pre>
 * Version 1.0 7/18/99
 * Version 2.0 10/26/03
 * </pre>
 *
 * @author Marcin Joachimak
 */
public class Coord {

    public String res;
    public String atom;
    public String chain;
    public int atomnum;
    public int resnum;
    public float x;
    public float y;
    public float z;
    public float b;
    public float r;

    public Coord() {

        res = new String();
        atom = new String();
        chain = new String();
        atomnum = -1;
        resnum = -1;
        x = -1;
        y = -1;
        z = -1;
        b = -1;
        r = -1;
    }

    public Coord(Coord co) {

        res = co.res;
        atom = co.atom;
        chain = co.chain;
        atomnum = co.atomnum;
        resnum = co.resnum;
        x = co.x;
        y = co.y;
        z = co.z;
        b = co.b;
        r = co.r;

    }

    public Coord(String residue, int a, float[] coo) {

        res = residue;
        atomnum = a;
        x = coo[0];
        y = coo[1];
        z = coo[2];
        b = coo[3];
        r = coo[4];
    }

    public Coord(String resdef, String atomdef, int resn, int a, float[] coo) {

        res = resdef;
        atom = atomdef;
        resnum = resn;
        atomnum = a;
        x = coo[0];
        y = coo[1];
        z = coo[2];
        b = coo[3];
        r = coo[4];
    }

    public Coord(String chain, String resdef, String atomdef, int resn, int a, float[] coo) {

        chain = chain;
        res = resdef;
        atom = atomdef;
        resnum = resn;
        //System.out.println("Coord resnum "+resnum);
        atomnum = a;
        x = coo[0];
        y = coo[1];
        z = coo[2];
        b = coo[3];
        r = coo[4];
    }

    public String toString() {
        return (res + ":\t:" + atomnum + ":\t:" + x + ":\t:" + y + ":\t:" + z);
    }

    public String fulltoString() {
        return (res + ":\t:" + atom + ":\t:" + atomnum + ":\t:" + resnum + ":\t:" + x + ":\t:" + y + ":\t:" + z + ":\t:" + b + ":\t:" + r);
    }
}
