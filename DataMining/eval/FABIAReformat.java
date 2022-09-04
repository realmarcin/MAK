package DataMining.eval;

import DataMining.ValueBlockList;
import DataMining.ValueBlockListMethods;
import mathy.SimpleMatrix;

public class FABIAReformat {


    /**
     * @param args
     */
    public FABIAReformat(String[] args) {

        String readref = args[0];
        System.out.println("ref " + readref);

        SimpleMatrix sm = new SimpleMatrix(args[1]);

        ValueBlockList refvbl = null;

        System.out.println("sm.ylabels " + sm.ylabels.length + "\tsm.xlabels " + sm.xlabels.length);

        try {
            refvbl = ValueBlockListMethods.readFABIA(readref, sm.ylabels, sm.xlabels, true);
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
            DataMining.eval.FABIAReformat arg = new DataMining.eval.FABIAReformat(args);
        } else {
            System.out.println("syntax: java DataMining.eval.FABIAReformat\n" +
                    "<input>\n" +
                    "<reference input data file>\n" +
                    "<out file>"

            );
        }
    }
}
