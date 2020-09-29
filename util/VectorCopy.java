package util;

import java.util.Vector;

public class VectorCopy {

    public final static Vector copy(Vector d) {

        Vector ret = new Vector();

        for (int i = 0; i < d.size(); i++) {

            Object o = (Object) d.elementAt(i);
            if (o != null)
                ret.addElement((Object) o);
            else
                ret.addElement(null);
        }

        return ret;
    }

    public final static Vector copyString(Vector d) {

        Vector ret = new Vector();

        for (int i = 0; i < d.size(); i++) {
            String o = new String((String) d.elementAt(i));
            if (o != null)
                ret.addElement((String) o);
            else
                ret.addElement(null);
        }

        return ret;
    }


}