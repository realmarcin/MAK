package DataMining.util;

import util.MoreArray;
import util.TabFile;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: 11/29/16
 * Time: 6:34 PM
 */
public class ReadVect {

    /**
     * @param args
     */
    public ReadVect(String[] args) {

        String[] g = MoreArray.extractColumnStr(TabFile.readtoArray(args[0], false), 1);

        double[] allpvals = MoreArray.convfromString(g);

        System.out.println("loaded adjusted " + allpvals.length);

    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 1) {
            ReadVect rm = new ReadVect(args);
        } else {
            System.out.println("syntax: java DataMining.util.ReadVect <in>");
        }
    }

}
