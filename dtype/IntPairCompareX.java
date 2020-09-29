package dtype;

import java.util.Comparator;

/**
 * User: marcin
 * Date: Jun 15, 2007
 * Time: 10:05:45 PM
 */
public class IntPairCompareX  implements Comparator {

    public int compare(Object one, Object two) {

        return ((IntPair) one).compareToX(two);
    }
}

