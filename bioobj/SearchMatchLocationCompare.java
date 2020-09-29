package bioobj;

import java.util.Comparator;

/**
 * User: marcin
 * Date: Feb 18, 2007
 * Time: 1:29:23 PM
 */
    public class SearchMatchLocationCompare implements Comparator {

        public int compare(Object one, Object two) {
            return ((SearchMatch) one).compareTo(two);
        }
}
