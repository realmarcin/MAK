package dtype;

import util.MoreArray;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: Jan 18, 2012
 * Time: 10:41:11 PM
 */
public class Int2DArStringAr {

    public int[] data1;
    public int[] data2;
    public int[] data3;


    /**
     * @param s
     */
    public Int2DArStringAr(int s) {
        data1 = new int[s];
        data2 = new int[s];
        data3 = new int[s];
    }

    /**
     * @param j
     */
    public void remove(int j) {

        data1 = MoreArray.remove(data1, j);
        data2 = MoreArray.remove(data2, j);
        data3 = MoreArray.remove(data3, j);

        //System.out.println("remove " + j + "\t" + str.length + "\t" + data.length + "\t" + data[0].length);
    }

    /**
     * @param removeval
     * @param gene
     * @return
     */
    public void removebyPairElement(int removeval, boolean gene) {
        //System.out.println("removebyPairElement " + str.length + "\t" + data.length + "\t" + data[0].length);
        for (int j = 0; j < data1.length; j++) {
            if (gene) {
                if (removeval == data1[j]) {
                    remove(j);
                    j--;
                }
            } else {
                if (removeval == data2[j]) {
                    remove(j);
                    j--;
                }
            }
        }
    }

}

