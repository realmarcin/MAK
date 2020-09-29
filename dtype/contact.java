package dtype;

import java.util.Vector;

/**
 * Class for handling contact data derived from a PDB.
 */

public class contact {
    int pos;
    int[] cntcts;

    public contact(int pos, int[] cntcts) {
        this.pos = pos;
        this.cntcts = cntcts;
    }

    public String toString() {
        return (pos + "\t" + cntcts);
    }

    public Vector readCntcts(String f) {
        Vector cntct = new Vector();
        //contact ct = new contact(pos, cntcts[]);
        return cntct;
    }

    public void selectCntcts(Vector cntcts) {
        //Vector select;
        //select.addElement((Item)s, p, green);
    }
}
