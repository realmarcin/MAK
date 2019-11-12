package DataMining;

import util.MoreArray;

import java.util.ArrayList;

/**
 * User: marcinjoachimiak
 * Date: Oct 2, 2007
 * Time: 10:49:09 PM
 */
public class MakeUnique {


    /**
     * Method to create a list from two string list objects:
     * if object is both in list A and list B it is removed, otherwise added from A and B.
     *
     * @param a
     * @param b
     */
    public final static ArrayList makeUnique(ArrayList a, ArrayList b) {
        if (b == null)
            return a;
        else {
            ArrayList ret = new ArrayList();
            for (int i = 0; i < a.size(); i++) {
                ArrayList[] arA = ((ArrayList[]) a.get(i));
                String cura = BlockMethods.IcJctoijID(arA);
                for (int j = 0; i < b.size(); j++) {
                    ArrayList[] arB = ((ArrayList[]) b.get(j));
                    String curb = BlockMethods.IcJctoijID(arB);
                    if (cura.equals(curb)) {
                        ret.add(arA);
                        a.remove(i);
                        b.remove(j);
                        break;
                    }
                }
                MoreArray.addArrayListtoArrayList(ret, a);
                MoreArray.addArrayListtoArrayList(ret, b);
            }
            return ret;
        }
    }

    /**
     * Method to create a unique list based on string lists A and B:
     * if object is found in B it is removed from both, otherwise add from A.
     *
     * @param a
     * @param b
     */
    public final static ArrayList makeUniqueFirstListStr(ArrayList a, ArrayList b) {
        System.out.println("makeUniqueFirstListStr start");
        if (b == null || b.size() == 0) {
            System.out.println("makeUniqueFirstListStr OldMovesStrBlockId is null, return PMoveStr");
            return a;
        } else {
            //ArrayList ret = new ArrayList();
            for (int i = 0; i < a.size(); i++) {
                String cura = (String) a.get(i);
                for (int j = 0; j < b.size(); j++) {
                    String curb = (String) b.get(j);
                    if (cura.equals(curb)) {
                        System.out.println("makeUniqueFirstListStr removing " + cura + " -\t- " + curb);
                        a.remove(i);
                        i--;
                        b.remove(j);
                        j--;
                        break;
                    }
                }
                //ret.ensureCapacity(a.size());
                //ret.addAll(ret.size(), a);
                //MoreArray.addArrayListtoArrayList(ret, a);
            }
            System.out.println("makeUniqueFirstListStr end " + a.size());
            return a;
        }
    }


    /**
     * Method to create a unique list based on the block id list of A and block id lists in list B:
     * all block ids in B not equal to A are added
     *
     * @param a
     * @param b
     * @return
     */
    public final static ArrayList makeUnique(ArrayList[] a, ArrayList b) {
        if (b == null) {
            ArrayList ret = new ArrayList();
            ret.add(a);
            return ret;
        } else {
            ArrayList ret = new ArrayList();
            String cura = BlockMethods.IcJctoijID(a);
            for (int i = 0; i < b.size(); i++) {
                ArrayList[] arB = ((ArrayList[]) b.get(i));
                String curb = BlockMethods.IcJctoijID(arB);
                if (cura.equals(curb)) {
                    ret.add(a);
                    b.remove(i);
                    break;
                }
            }
            MoreArray.addArrayListtoArrayList(ret, b);
            return ret;
        }
    }

    /**
     * Method to create a unique list from block id string A and list of block id strings B:
     * all block ids in B not equal to A are added
     *
     * @param a
     * @param b
     * @return
     */
    public final static ArrayList makeUniqueListVersusStringList(String a, ArrayList b) {
        if (b == null) {
            ArrayList ret = new ArrayList();
            ret.add(a);
            return ret;
        } else {
            ArrayList ret = new ArrayList();
            for (int i = 0; i < b.size(); i++) {
                String curb = ((String) b.get(i));
                if (a.equals(curb)) {
                    ret.add(a);
                    b.remove(i);
                    break;
                }
            }
            MoreArray.addArrayListtoArrayList(ret, b);
            return ret;
        }
    }

    /**
     * Creates a nonredundant block id list from the two individual block id lists A and B.
     * Implements pairwise removal of X and Y block indices found in
     *
     * @param a
     * @param b
     * @return
     */
    public final static ArrayList[] makeUnique(ArrayList[] a, ArrayList[] b) {
        int[] removed0 = new int[a[0].size()];
        int count0 = 0;
        int[] removed1 = new int[a[1].size()];
        int count1 = 0;
        if (b == null || (b[0] == null && b[1] == null)) {
            return a;
        } else {
            ArrayList[] ret = MoreArray.initArrayListArray(2);
            for (int i = 0; i < a[0].size(); i++) {
                Object aZero = a[0].get(i);
                for (int j = 0; j < b[0].size(); j++) {
                    if (aZero == b[0].get(j)) {
                        ret[0].add(aZero);
                        b[0].remove(j);
                        j--;
                        if (removed0[i + count0] == 0) {
                            a[0].remove(i);
                            i--;
                            count0++;
                            removed0[i] = 1;
                        }
                    }
                }
            }
            ret[0] = MoreArray.addArrayList(ret[0], a[0]);
            for (int i = 0; i < a[1].size(); i++) {
                Object aOne = a[1].get(i);
                for (int j = 0; j < b[1].size(); j++) {
                    if (aOne == b[1].get(j)) {
                        ret[1].add(aOne);
                        b[1].remove(j);
                        if (removed1[j + count1] == 0) {
                            a[1].remove(j);
                            j--;
                            count1++;
                            removed1[j] = 1;
                        }
                    }
                }
            }
            ret[1] = MoreArray.addArrayList(ret[1], a[1]);
            return ret;
        }
    }
}
