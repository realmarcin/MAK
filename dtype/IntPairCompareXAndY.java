package dtype;

import java.util.Comparator;

/**
 * User: marcin
 * Date: Dec 6, 2008
 * Time: 11:20:45 PM
 */

public class IntPairCompareXAndY implements Comparator {

    public int compare(Object one, Object two) {
        return ((IntPair) one).compareToXAndY(two);
    }
}

