package DataMining.eval;

import DataMining.ValueBlockList;
import DataMining.ValueBlockListMethods;


/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: 10/25/16
 * Time: 9:41 PM
 */
public class EBICreformat {


    /**
     * @param args
     */
    public EBICreformat(String[] args) {


        String readref = args[0];
        System.out.println("ref " + readref);
        ValueBlockList refvbl = null;
        try {
            refvbl = ValueBlockListMethods.readUniBic(readref, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("refvbl " + refvbl.size());

        ValueBlockListMethods.writeBIC(args[1], refvbl, 1);
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 2) {
            EBICreformat arg = new EBICreformat(args);
        } else {
            System.out.println("syntax: java DataMining.eval.EBICreformat\n" +
                    "<input>\n" +
                    "<out file>"

            );
        }
    }
}
