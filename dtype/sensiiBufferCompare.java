package dtype;

import java.util.Comparator;

/**
 * User: marcin
 * Date: Feb 24, 2007
 * Time: 9:22:59 PM
 */
public class sensiiBufferCompare implements Comparator {
     public int compare(Object one, Object two) {
        return ((sensiiBuffer) one).compareTo(two);
    }
}
