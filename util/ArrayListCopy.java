package util;

import java.util.ArrayList;

public class ArrayListCopy {

    public final static ArrayList copy(ArrayList d) {

        ArrayList ret = new ArrayList();

        for (int i = 0; i < d.size(); i++) {
            Object o = (Object)
                    d.get(i);
            ret.add((Object) o);
        }

        return ret;
    }

    public final static ArrayList copyString(ArrayList
            d) {

        ArrayList ret = new ArrayList();

        for (int i = 0; i < d.size(); i++) {
            String o = new
                    String((String)
                    d.get(i));
            ret.add((String) o);
        }

        return ret;
    }

    public final static ArrayList init(ArrayList al, int s) {

        for (int i = 0; i < s; i++) {

            Object o = new Object();
            al.add(o);
        }

        return al;

    }

}
