package DataMining.util;

import DataMining.MINER_STATIC;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: 3/7/15
 * Time: 9:30 AM
 */
public class OutCritList {

    /**
     * @param args
     */
    public OutCritList(String[] args) {

        String out = "";
        for (int i = 0; i < MINER_STATIC.CRIT_LABELS.length; i++) {
            if (MINER_STATIC.CRIT_LABELS[i].indexOf("noninvert") == -1 &&
                    MINER_STATIC.CRIT_LABELS[i].indexOf("LARS") == -1 &&
                    MINER_STATIC.CRIT_LABELS[i].indexOf("MEAN") == -1 &&
                    MINER_STATIC.CRIT_LABELS[i].indexOf("feat") == -1 &&
                    MINER_STATIC.CRIT_LABELS[i].indexOf("inter") == -1)
                out += "'" + MINER_STATIC.CRIT_LABELS[i] + "' ";
        }

        System.out.println(out);
    }


    /**
     * @param args
     */

    public static void main(String[] args) {
        if (args.length == 0) {
            OutCritList o = new OutCritList(args);
        }
    }
}
