package DataMining.util;

import DataMining.ValueBlock;
import DataMining.ValueBlockList;
import mathy.SimpleMatrix;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: 9/10/13
 * Time: 12:08 AM
 */
public class CheckIndex {

    /**
     * @param args
     */
    public CheckIndex(String[] args) {
        ValueBlockList vbl = null;
        try {
            vbl = ValueBlockList.read(args[0], false);

            SimpleMatrix sm = new SimpleMatrix(args[1]);
            int genes = sm.data.length;
            int exps = sm.data[0].length;

            for (int i = 0; i < vbl.size(); i++) {
                System.out.println("i " + i);
                ValueBlock vb = (ValueBlock) vbl.get(i);

                for (int a = 0; a < vb.genes.length; a++) {
                    if (vb.genes[a] > genes) {
                        System.out.println("> genes " + i + "\t" + vb.genes[a]);
                    } else if (vb.genes[a] < 1) {
                        System.out.println("< genes " + i + "\t" + vb.genes[a]);
                    }
                }
                for (int b = 0; b < vb.exps.length; b++) {
                    if (vb.exps[b] > exps) {
                        System.out.println("> exps " + i + "\t" + vb.exps[b]);
                    } else if (vb.exps[b] < 1) {
                        System.out.println("< exps " + i + "\t" + vb.exps[b]);
                    }
                }

            }

            System.out.println("done");

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    /**
     * @paramfile args
     */

    public final static void main(String[] args) {
        if (args.length == 2) {
            CheckIndex rm = new CheckIndex(args);
        } else {
            System.out.println("syntax: java DataMining.util.CheckIndex\n" +
                    "<vbl file>\n" +
                    "<data file>"
            );
        }
    }
}
