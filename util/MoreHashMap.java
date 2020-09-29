package util;

import java.util.HashMap;

/**
 * User: marcinjoachimiak
 * Date: Nov 19, 2007
 * Time: 11:30:22 PM
 */
public class MoreHashMap extends HashMap {

    public final static HashMap[] initArray(int k) {
        HashMap[] ret = new HashMap[k];
        for (int i = 0; i < k; i++)
            ret[i] = new HashMap();
        return ret;
    }
}
