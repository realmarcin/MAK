package dtype;

import java.util.Comparator;

/**
 * User: marcin
 * Date: Feb 24, 2007
 * Time: 12:04:39 AM
 */
public class SensCharCompare implements Comparator {

    public int compare(Object one, Object two) {
        return ((SensChar) one).compareTo(two);
    }
}
