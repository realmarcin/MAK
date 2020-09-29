package dtype;

import java.util.ArrayList;

/**
 * User: marcinjoachimiak
 * Date: Nov 6, 2007
 * Time: 8:30:41 PM
 */
public class StringTripleList extends ArrayList {


    /**
     *
     */
    public StringTripleList() {
        super();
    }


    /**
     * @return
     */
    public int findA(String s) {
        for (int i = 0; i < size(); i++) {
            StringTriple ts = (StringTriple) get(i);
            //System.out.println("StringTripleList findA "+ts.a+"\t"+s);
            if (ts.a.equals(s)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * @param s
     * @return
     */
    public int findB(String s) {
        for (int i = 0; i < size(); i++) {
            StringTriple ts = (StringTriple) get(i);
            if (ts.a.equals(s)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * @param s
     * @return
     */
    public int findC(String s) {
        for (int i = 0; i < size(); i++) {
            StringTriple ts = (StringTriple) get(i);
            if (ts.a.equals(s)) {
                return i;
            }
        }
        return -1;
    }
}
