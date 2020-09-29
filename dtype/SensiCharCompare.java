package dtype;

import java.util.Comparator;

/**
 * User: marcin
 * Date: Feb 24, 2007
 * Time: 12:02:45 AM
 */
public class SensiCharCompare implements Comparator {

    public int compare(Object one, Object two) {
        return ((SensiChar) one).compareTo(two);
    }
}

 
