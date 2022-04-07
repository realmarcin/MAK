package DataMining.eval;

import DataMining.ValueBlockList;
import DataMining.ValueBlockListMethods;
import mathy.SimpleMatrix;

public class RecBicReformat {


    /**
     * @param args
     */
    public RecBicReformat(String[] args) {

        String readref = args[0];
        System.out.println("ref " + readref);

        SimpleMatrix sm = new SimpleMatrix(args[1]);

        ValueBlockList refvbl = null;
        try {
            refvbl = ValueBlockListMethods.readRecBic(readref, sm.ylabels, sm.xlabels, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("refvbl " + refvbl.size());

        ValueBlockListMethods.writeBIC(args[2], refvbl);//, 1);
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 3) {
            DataMining.eval.RecBicReformat arg = new DataMining.eval.RecBicReformat(args);
        } else {
            System.out.println("syntax: java DataMining.eval.RecBicReformat\n" +
                    "<input>\n" +
                    "<reference input data file>\n" +
                    "<out file>"

            );
        }
    }
}
