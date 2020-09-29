package util;

import java.util.Vector;

public class CopyVector {

    public CopyVector() {

    }

    public Vector copyVector(Vector v) {

        Vector r = new Vector(v.size());

        for (int i = 0; i < v.size(); i++) {

            Object o = (Object) v.elementAt(i);
            r.addElement((Object) o);
        }
        return r;
    }


}