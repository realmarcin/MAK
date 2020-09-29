package dtype;

import util.MoreArray;

import java.util.ArrayList;

/**
 * User: marcin
 * Date: Dec 12, 2008
 * Time: 10:32:58 AM
 */
public class ArrayofArraysListInt {

    public ArrayList[] array_of_arrays;
    public ArrayList expLabels;
    public int max_rows;

    /**
     *
     */
    public ArrayofArraysListInt() {
        array_of_arrays = null;
        expLabels = null;
        max_rows = -1;
    }

    /**
     *
     */
    public ArrayofArraysListInt(ArrayList[] a1, ArrayList a2, int m) {
        array_of_arrays = MoreArray.copyArrayListArrayList(a1);
        expLabels = MoreArray.copyArrayList(a2);
        max_rows = m;
    }
}
