package bioobj;

import bioobj.SecElem;


public class secCont {
    public int i = 0;
    public int j = 0;
    public SecElem elemi = null;
    public SecElem elemj = null;
    public float dist = 0;
    public int orient = -1;

    public secCont(SecElem elem1, int first, SecElem elem2, int second, float d, int o) {
        elemi = elem1;
        i = first;
        elemj = elem2;
        j = second;
        dist = d;
        orient = o;
    }

    public String toString() {
        String[] types = {"COIL", "HELIX", "SHEET"};
        return ("cardinality " + elemi.cardinal + " res " + i + "\t" +
                types[elemi.type] + "\t" + "cardinality " + elemj.cardinal +
                " res " + j + "\t" + types[elemj.type] + "\t" + "dist " + dist + "\t" + "orient " + orient);
    }

}
