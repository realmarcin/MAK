package dtype;

import util.MoreArray;

import java.util.ArrayList;

/**
 * User: marcin
 * Date: Dec 11, 2008
 * Time: 10:55:27 PM
 */
public class AList1Str {

    public ArrayList a;
    public String x;

    /**
     * @param one
     */
    public AList1Str(String one) {
        a = new ArrayList();
        x = one;
    }

    /**
     * @param one
     */
    public AList1Str(ArrayList al, String one) {
        a = MoreArray.copyArrayList(al);
        x = one;
    }


    /**
     *
     */
    public AList1Str() {
        a = new ArrayList();
        x = null;
    }
}
