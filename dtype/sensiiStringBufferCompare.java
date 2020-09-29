package dtype;

import java.util.Comparator;

/**
 * User: marcin
 * Date: Feb 24, 2007
 * Time: 9:36:20 PM
 */
public class sensiiStringBufferCompare  implements Comparator {
         public int compare(Object one, Object two) {
            return ((sensiiStringBuffer) one).compareTo(two);
        }        
}
