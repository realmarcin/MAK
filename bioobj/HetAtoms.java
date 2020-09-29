package bioobj;

import dtype.Coord;

import java.util.ArrayList;

/**
 * Class to represent a collection of heteratom molecules as a ArrayList object of Molecules.
 * <p/>
 * <pre>
 * Version 1.0 10/26/03
 * </pre>
 *
 * @author Marcin Joachimak
 */
public class HetAtoms {

    public ArrayList molecs;
    public String name;
    public int size;

    public HetAtoms() {

        this.molecs = new ArrayList();
        this.name = new String();
    }

    public HetAtoms(ArrayList c, String n) {
        this.molecs = util.ArrayListCopy.copy(c);
        this.size = retSize();
        this.name = new String(n);

    }

    public int retSize() {

        return this.molecs.size();
    }

    public void addMolec(Molecule c) {

        molecs.add((Molecule) c);
    }

    /**
     * Returns coordinates of all stored heteratoms as a single ArrayList of Coord objects.
     */
    public ArrayList retAsArrayList() {

        ArrayList ret = new ArrayList();

        for (int i = 0; i < retSize(); i++) {

            Molecule molec = (Molecule) this.molecs.get(i);
            int msize = molec.retSize();
            //System.out.println("Molecule atom num: "+size+"\t"+molec.name);

            for (int j = 0; j < msize; j++) {

                Coord now = (Coord) molec.coords.get(j);
                ret.add((Coord) now);
            }

        }

//System.out.println("retAsArrayList "+ret.size());

        return ret;
    }


    /**
     * Method to print the contents of this HetAtoms object to System.out.
     */
    public void systemPrint() {

        System.out.println("Molecule.systemPrint()");
        for (int i = 0; i < retSize(); i++) {

            Molecule molec = (Molecule) this.molecs.get(i);
            int size = molec.retSize();
            System.out.println("Molecule atom num: " + size + "\t" + molec.name);

            for (int j = 0; j < size; j++) {

                Coord now = (Coord) molec.coords.get(j);
                System.out.print(molec.name + "\t" + now.fulltoString() + "\n");
            }

        }

    }
}
