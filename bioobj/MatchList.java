package bioobj;

import java.util.ArrayList;

/**
 * User: marcinjoachimiak
 * Date: Aug 2, 2007
 * Time: 2:37:16 PM
 */
public class MatchList extends ArrayList {


    /**
     * @param t
     */
    public void updateTotalHits(int t) {

        for (int i = 0; i < this.size(); i++) {
            Match cur = (Match) this.get(i);
            cur.total_hits = t;
            this.set(i, cur);
        }
    }

    /**
     * @param a
     * @param m
     * @return
     */
    public final static ArrayList add(ArrayList a, MatchList m) {

        for (int i = 0; i < m.size(); i++) {
            Match cur = (Match) m.get(i);
            a.add(cur.toString());
        }
        return a;
    }

}


