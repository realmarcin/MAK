package DataMining.eval;

import DataMining.ValueBlockList;
import DataMining.ValueBlockListMethods;

/**
 * User: marcin
 * Date: 2/19/20
 * Time: 9:37 PM
 */
public class UniBicReformat {


    /**
     * @param args
     */
    public UniBicReformat(String[] args) {

        String readref = args[0];
        System.out.println("ref " + readref);
        ValueBlockList refvbl = null;
        try {
            refvbl = ValueBlockListMethods.readUniBic(readref, 1, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("refvbl " + refvbl.size());

        ValueBlockListMethods.writeBIC(args[1], refvbl);//, 1);
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 2) {
            UniBicReformat arg = new UniBicReformat(args);
        } else {
            System.out.println("syntax: java DataMining.eval.UniBicReformat\n" +
                    "<input>\n" +
                    "<out file>"

            );
        }
    }
}

