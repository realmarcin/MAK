package dtype;

import java.util.ArrayList;

/**
 * User: marcin
 * Date: Dec 11, 2008
 * Time: 10:53:21 PM
 */
public class AList2Str {
    
    ArrayList a;
    String x;
    String y;

    /**
     * @param one
     * @param two
     */
    public AList2Str(String one, String two) {
        a = new ArrayList();
        x = one;
        y = two;
    }


    /**
     *
     */
    public AList2Str() {
        a = new ArrayList();
        x = null;
        y = null;
    }
}
