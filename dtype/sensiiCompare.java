package dtype;

import java.util.Comparator;

/**
 * User: marcin
 * Date: Feb 24, 2007
 * Time: 12:09:40 AM
 */
public class sensiiCompare implements Comparator {

    public int compare(Object one, Object two) {
        return ((sensii) one).compareTo(two);
    }

}
