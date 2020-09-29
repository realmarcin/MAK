package dtype;

import java.util.Comparator;

/**
 * User: marcin
 * Date: Jun 15, 2007
 * Time: 10:08:42 PM
 */
public class IntPairCompareY implements Comparator {

    public int compare(Object one, Object two) {
        return ((IntPair) one).compareToY(two);
    }
}
