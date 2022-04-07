package DataMining.eval;

import DataMining.ValueBlockList;
import DataMining.ValueBlockListMethods;

public class RecBicReformat {


    /**
     * @param args
     */
    public RecBicReformat(String[] args) {

        String readref = args[0];
        System.out.println("ref " + readref);
        ValueBlockList refvbl = null;
        try {
            refvbl = ValueBlockListMethods.readRecBic(readref, 1, false);
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
            DataMining.eval.RecBicReformat arg = new DataMining.eval.RecBicReformat(args);
        } else {
            System.out.println("syntax: java DataMining.eval.RecBicReformat\n" +
                    "<input>\n" +
                    "<out file>"

            );
        }
    }
}
