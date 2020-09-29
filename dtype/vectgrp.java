package dtype;

import java.util.Vector;

public class vectgrp {

    public String s;
    public Vector vect;

    public vectgrp() {
        this.s = new String();
        this.vect = new Vector();
    }

    public vectgrp(vectgrp old) {
        this.s = new String(old.s);
        this.vect = new Vector();
        for (int i = 0; i < old.vect.size(); i++) {
            this.vect.addElement(old.vect.elementAt(i));
        }
    }

    public vectgrp(String b, Vector y) {
        this.s = new String(b);
        this.vect = new Vector();
        for (int i = 0; i < y.size(); i++) {
            this.vect.addElement(y.elementAt(i));
        }
    }

    public Vector retVect() {
        Vector v = new Vector();
        for (int i = 0; i < vect.size(); i++)
            v.addElement(vect.elementAt(i));
        return v;
    }

    public String toString() {
        return (s + "\t" + vect.toString());
    }
}


