package DataMining.util;

import DataMining.*;
import util.TabFile;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: 8/15/13
 * Time: 7:38 PM
 */
public class IntersectBic {

    ValueBlockList BIC;
    String outprefix;

    /**
     * @param args
     */
    public IntersectBic(String[] args) {

        try {
            BIC = ValueBlockListMethods.readAny(args[0], false);
            //BIC = ValueBlockListMethods.incrementIndices(BIC, offsetincr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        outprefix = args[0].substring(0, args[0].lastIndexOf(".")) + "_";


        String[][] data = TabFile.readtoArray(args[1]);

        ValueBlockList outvbl = new ValueBlockList();
        for (int i = 0; i < data.length; i++) {
            String cur = data[i][0];
            String[] split = cur.split("_");

            ValueBlock cur1 = (ValueBlock) BIC.get(Integer.parseInt(split[0]) - 1);
            ValueBlock cur2 = (ValueBlock) BIC.get(Integer.parseInt(split[1]) - 1);

            ValueBlock inter = BlockMethods.intersectBlocks(cur1, cur2);
            outvbl.add(inter);

        }

        String header = "#";
        String s = outvbl.toString(header + MINER_STATIC.HEADER_VBL);//ValueBlock.toStringShortColumns());
        String outpath = args[0] + "_intersect.vbl";
        util.TextFile.write(s, outpath);
        System.out.println("wrote " + outpath);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 2) {
            IntersectBic rm = new IntersectBic(args);
        } else {
            System.out.println("syntax: java DataMining.util.IntersectBic\n" +
                    "<bicluster list>\n" +
                    "<intersect data>"
            );
        }
    }
}
