package dtype;

import java.util.Comparator;

/**
 * User: marcinjoachimiak
 * Date: Sep 11, 2008
 * Time: 4:24:09 PM
 */
public class sensiiiStringBufferCompare  implements Comparator {

         public int compare(Object one, Object two) {
            return ((sensiiiStringBuffer) one).compareTo(two);
        }

}
