package DataMining.eval;

import DataMining.ValueBlockList;
import DataMining.ValueBlockListMethods;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: 12/16/12
 * Time: 3:10 PM
 */
public class ConvertCOALESCEsim {


    /**
     * @param args
     */
    public ConvertCOALESCEsim(String[] args) {

        try {
            ValueBlockList reflist = ValueBlockListMethods.readCOALESCESim(args[0]);
            ValueBlockListMethods.writeBIC(args[0] + ".bic",reflist);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 1) {
            ConvertCOALESCEsim arg = new ConvertCOALESCEsim(args);
        } else {
            System.out.println("syntax: java DataMining.eval.ConvertCOALESCEsim\n" +
                    "<COALESCE output>"
            );
        }
    }
}
