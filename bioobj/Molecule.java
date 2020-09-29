package bioobj;

import dtype.Coord;

import java.util.Vector;

/**
 * This is class to represent a Molecule as a Vector object of coordinates Coord.
 * <pre>
 * Version 1.0 10/26/03
 * </pre>
 *
 * @author Marcin Joachimak
 */
public class Molecule {

    public String name;
    public Vector coords;
    public int size;

    public Molecule() {

        this.coords = new Vector();
        this.name = new String();
    }

    public Molecule(Vector c, String n) {

        this.coords = util.VectorCopy.copy(c);
        setSize();
        this.name = new String(n);
    }

    public int retSize() {

        return this.coords.size();
    }

    public void setSize() {

        this.size = retSize();
    }

    public void addCoord(Coord c) {

        this.coords.addElement((Coord) c);
        setSize();
    }

}