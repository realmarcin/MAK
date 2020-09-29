package util;

import dtype.IntArrayWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * User: marcin
 * Date: Jan 26, 2011
 * Time: 3:49:38 PM
 */
public class LoadHash {

    /**
     * @param ar
     * @param h
     * @return
     */
    public final static HashMap add(int[] ar, HashMap h) {
        for (int i = 0; i < ar.length; i++)
            h.put(ar[i], 1);
        return h;
    }

    /**
     * @param ar
     * @param h
     * @return
     */
    public final static HashMap addAndIncr(int[] ar, HashMap h) {
        for (int i = 0; i < ar.length; i++) {
            Object o = h.get(ar[i]);
            if (o == null)
                h.put(ar[i], 1);
            else {
                int c = (Integer) o;
                c++;
                h.put(ar[i], c);
            }
        }
        return h;
    }

    /**
     * Adds key-values in b to matching key-values in a or as new key-values
     *
     * @param ref
     * @param add
     * @return
     */
    public final static HashMap addAndIncr(HashMap ref, HashMap add) {
        Set keysA = add.keySet();
        Iterator itfinal = keysA.iterator();
        while (itfinal.hasNext()) {
            int s = (Integer) itfinal.next();
            int valA = (Integer) add.get(s);
            Object o = ref.get(s);
            if (o != null) {
                ref.put(s, valA + (Integer) o);
            } else {
                ref.put(s, valA);
            }
        }
        return ref;
    }

    /**
     * Adds key-values in b to matching key-values in a or as new key-values
     *
     * @param ref
     * @param add
     * @return
     */
    public final static HashMap addAndIncrStr(HashMap ref, HashMap add) {
        Set keysA = add.keySet();
        Iterator itfinal = keysA.iterator();
        while (itfinal.hasNext()) {
            String s = (String) itfinal.next();
            int valA = (Integer) add.get(s);
            Object o = ref.get(s);
            if (o != null) {
                ref.put(s, valA + (Integer) o);
            } else {
                ref.put(s, add);
            }
        }
        return ref;
    }

    /**
     * Adds key-values in b to matching key-values in a or as new key-values
     *
     * @param ref
     * @param add
     * @return
     */
    public final static HashMap addAndIncrIntArray(HashMap ref, HashMap add) {
        Set keysAdd = add.keySet();
        Iterator itfinal = keysAdd.iterator();
        while (itfinal.hasNext()) {
            IntArrayWrapper s = (IntArrayWrapper) itfinal.next();
            Object o = ref.get(s);
            int valAdd = (Integer) add.get(s);
            if (o != null) {
                ref.put(s, valAdd + (Integer) o);
            } else {
                ref.put(s, valAdd);
            }
        }
        return ref;
    }

    /**
     * @param ar
     * @param h
     * @return
     */
    public final static HashMap addPairAndIncrInt(int[] ar, int[] br, HashMap h) {
        for (int i = 0; i < ar.length; i++) {
            if (br != null) {
                for (int j = 0; j < br.length; j++) {
                    int[] up = {ar[i], br[j]};
                    IntArrayWrapper ia = new IntArrayWrapper(up);
                    Object o = h.get(ia);
                    if (o == null) {
                        //System.out.println("addPairAndIncr adding " + s + "\t" + 1);
                        h.put(ia, 1);
                    } else {
                        //System.out.println("addPairAndIncr adding " + s + "\t" + c);
                        h.put(ia, (Integer) o + 1);
                    }
                }
            } else {
                int[] up = {ar[i]};
                IntArrayWrapper ia = new IntArrayWrapper(up);
                Object o = h.get(ia);
                if (o == null) {
                    //System.out.println("addPairAndIncr adding " + s + "\t" + 1);
                    h.put(ia, 1);
                } else {
                    //System.out.println("addPairAndIncr adding " + s + "\t" + c);
                    h.put(ia, (Integer) o + 1);
                }
            }
        }

        return h;
    }

    /**
     * @param ar
     * @param h
     * @return
     */
    public final static HashMap addPairAndIncrStr(int[] ar, int[] br, HashMap h) {
        for (int i = 0; i < ar.length; i++) {
            for (int j = 0; j < br.length; j++) {
                String up = "" + ar[i] + "_" + br[j];
                //String s = "" + ar[i] + "_" + br[j];
                Object o = h.get(up);
                if (o == null) {
                    //System.out.println("addPairAndIncr adding " + s + "\t" + 1);
                    h.put(up, 1);
                } else {
                    int c = (Integer) o;
                    c++;
                    //System.out.println("addPairAndIncr adding " + s + "\t" + c);
                    h.put(up, c);
                }
            }
        }
        return h;
    }

    /**
     * @param ar
     * @param h
     * @return
     */
    public final static HashMap addPairs(int[] ar, int[] br, HashMap h) {
        for (int i = 0; i < ar.length; i++) {
            for (int j = 0; j < br.length; j++) {
                String s = "" + ar[i] + "_" + br[j];
                Object o = h.get(s);
                h.put(s, 1);
            }
        }
        return h;
    }

    /**
     * @param ar
     * @param h
     * @return
     */

    public final static HashMap addPairofPairAndIncr(int[] ar, int[] br, HashMap h) {
        for (int i = 0; i < ar.length; i++) {
            for (int j = 0; j < br.length; j++) {
                for (int a = i + 1; a < ar.length; a++) {
                    for (int b = j + 1; b < br.length; b++) {
                        String s = "" + ar[i] + "_" + br[j] + "__" + ar[a] + "_" + br[b];
                        Object o = h.get(s);
                        if (o == null)
                            h.put(s, 1);
                        else {
                            int c = (Integer) o;
                            c++;
                            h.put(s, c);
                        }
                    }
                }
            }
        }
        return h;
    }

    /**
     * @param h
     * @param key
     * @return
     */
    public final static HashMap removeHalfKey1(HashMap h, int key) {
        Set keys = h.keySet();
        Iterator itfinal = keys.iterator();
        ArrayList rem = new ArrayList();
        while (itfinal.hasNext()) {
            IntArrayWrapper s = (IntArrayWrapper) itfinal.next();
            if (key == s.data[0]) {
                rem.add(s);
            }
        }

        for (int i = 0; i < rem.size(); i++) {
            //String s = (String) rem.get(i);
            h.remove(rem.get(i));
        }
        return h;
    }

    /**
     * @param h
     * @param key
     * @return
     */
    public final static HashMap removeHalfKey2(HashMap h, int key) {
        Set keys = h.keySet();
        Iterator itfinal = keys.iterator();
        ArrayList rem = new ArrayList();
        while (itfinal.hasNext()) {
            IntArrayWrapper s = (IntArrayWrapper) itfinal.next();
            if (key == s.data[1]) {
                rem.add(s);
            }
        }

        for (int i = 0; i < rem.size(); i++) {
            //String s = (String) rem.get(i);
            h.remove(rem.get(i));
        }
        return h;
    }
}
