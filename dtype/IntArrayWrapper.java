package dtype;

import java.util.Arrays;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: Feb 9, 2012
 * Time: 10:27:01 AM
 */
public class IntArrayWrapper {

    public final int[] data;

    public IntArrayWrapper(int[] data) {
        if (data == null) {
            throw new NullPointerException();
        }
        this.data = data;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof IntArrayWrapper)) {
            return false;
        }
        return Arrays.equals(data, ((IntArrayWrapper) other).data);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }


    /*
    //test code
    HashMap test = new HashMap();
    int[] it1 = {2, 3};
    IntArrayWrapper i1 = new IntArrayWrapper(it1);
    int[] it2 = {2, 3};
    IntArrayWrapper i2 = new IntArrayWrapper(it2);
    int[] it3 = {1, 3};
    IntArrayWrapper i3 = new IntArrayWrapper(it3);
    test.put(i1, 1);

    if (test.get(i2) != null) {
        System.out.println("test HashMap i2 matches");
    } else
        System.out.println("test HashMap i2 does not match");
    if (test.get(i3) != null) {
        System.out.println("test HashMap i3 matches");
    } else
        System.out.println("test HashMap i3 does not match");*/

}
