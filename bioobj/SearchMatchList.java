package bioobj;

import java.util.ArrayList;

/**
 * User: marcin
 * Date: Feb 18, 2007
 * Time: 1:39:39 PM
 */
public class SearchMatchList extends ArrayList {

    /**
     * @return
     */
    public String[] getSeq() {

        String[] ret = new String[size()];
        for (int i = 0; i < size(); i++) {
            ret[i] = ((SearchMatch) get(i)).seq;
        }
        return ret;
    }

    /**
     * @return
     */
    public int[] getPos() {

        int[] ret = new int[size()];
        for (int i = 0; i < size(); i++) {
            SearchMatch match = (SearchMatch) get(i);
            //System.out.println(match);
            ret[i] = match.pos;
        }
        return ret;
    }
}
