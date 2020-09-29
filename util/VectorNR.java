package util;

import java.util.Vector;

/**
 * Class to make Vector of Strings nonredundant
 * This class creates a independent copy of the submitted vector.
 *
 * @Author Marcin Joachimiak
 * @Date 01/15/03
 * @Version 1.0
 */
public class VectorNR {

    public VectorNR() {
    }

    public Vector copyVector(Vector ve) {
        Vector ret = new Vector();
        for (int i = 0; i < ve.size(); i++) {
            String is = new String((String) ve.elementAt(i));
            ret.addElement(is);
        }
        return ret;
    }

    public Vector NR(Vector v) {

        Vector ret = copyVector(v);

        for (int i = 0; i < ret.size(); i++) {
            String is = (String) ret.elementAt(i);
            for (int j = i + 1; j < ret.size(); j++) {
                String js = (String) ret.elementAt(j);
                if (is.equals(js)) {
                    ret.removeElementAt(j);
                    j--;
                }
            }
        }

        return ret;
    }

}