package DataMining.util;

import DataMining.ValueBlock;
import DataMining.ValueBlockList;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: Oct 3, 2012
 * Time: 3:02:20 PM
 */
public class DiffSets {


    /**
     * @param args
     */
    public DiffSets(String[] args) {
        ValueBlockList setone = null;
        ValueBlockList settwo = null;

        try {
            setone = ValueBlockList.read(args[0], false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            settwo = ValueBlockList.read(args[1], false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        boolean[] matchedtwo = new boolean[settwo.size()];
        for (int i = 0; i < setone.size(); i++) {
            ValueBlock vone = (ValueBlock) setone.get(i);
            for (int j = 0; j < settwo.size(); j++) {
                if (!matchedtwo[j]) {
                    ValueBlock vtwo = (ValueBlock) settwo.get(j);
                    if (vone.equals(vtwo)) {
                        matchedtwo[j] = true;
                    }
                }
            }
        }

        boolean[] matchedone = new boolean[setone.size()];
        for (int i = 0; i < settwo.size(); i++) {
            ValueBlock vtwo = (ValueBlock) settwo.get(i);
            for (int j = 0; j < setone.size(); j++) {
                if (!matchedone[j]) {
                    ValueBlock vone = (ValueBlock) setone.get(j);
                    if (vtwo.equals(vone)) {
                        matchedone[j] = true;
                    }
                }
            }
        }


        System.out.println("Not matched in two");
        for (int i = 0; i < matchedtwo.length; i++) {
            if (!matchedtwo[i]) {
                ValueBlock cur = (ValueBlock) settwo.get(i);
                System.out.println("two " + i);
                System.out.println("two " + cur.toString());
            }
        }

        System.out.println("Not matched in one");
        for (int i = 0; i < matchedone.length; i++) {
            if (!matchedone[i]) {
                ValueBlock cur = (ValueBlock) setone.get(i);
                System.out.println("one " + i);
                System.out.println("one " + cur.toString());
            }
        }
    }


    /**
     * @param args
     */

    public final static void main(String[] args) {
        if (args.length == 2) {
            DiffSets rm = new DiffSets(args);
        } else {
            System.out.println("syntax: java DataMining.util.DiffSets\n" +
                    "<set 1> <set2>"
            );
        }
    }
}
