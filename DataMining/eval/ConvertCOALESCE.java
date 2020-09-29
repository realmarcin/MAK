package DataMining.eval;

import DataMining.ValueBlockList;
import DataMining.ValueBlockListMethods;
import mathy.SimpleMatrix;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: 12/16/12
 * Time: 4:11 PM
 */
public class ConvertCOALESCE {


    /**
     * @param args
     */
    public ConvertCOALESCE(String[] args) {

        SimpleMatrix sm = new SimpleMatrix(args[1]);
        try {
            ValueBlockList reflist = ValueBlockListMethods.readCOALESCE(args[0], sm.ylabels, sm.xlabels);
            ValueBlockListMethods.writeBIC(args[0] + ".bic", reflist);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 2) {
            ConvertCOALESCE arg = new ConvertCOALESCE(args);
        } else {
            System.out.println("syntax: java DataMining.eval.ConvertCOALESCE\n" +
                    "<COALESCE output>\n" +
                    "<data file>"
            );
        }
    }
}
