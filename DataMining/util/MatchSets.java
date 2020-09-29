package DataMining.util;

import DataMining.*;
import util.MoreArray;

import java.util.ArrayList;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: 2/14/13
 * Time: 10:52 PM
 */
public class MatchSets {

    /**
     * @param args
     */
    public MatchSets(String[] args) {
        ValueBlockList vbl1 = null, vbl2 = null, outexpr = new ValueBlockList(), outfit = new ValueBlockList();
        try {
            vbl1 = ValueBlockListMethods.readAny(args[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            vbl2 = ValueBlockListMethods.readAny(args[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }

        double cutoff = Double.parseDouble(args[2]);

        double max = 0;
        if (vbl1 != null && vbl2 != null) {

            for (int i = 0; i < vbl1.size(); i++) {
                if (vbl1.get(i) != null) {
                    ValueBlock vb1 = (ValueBlock) vbl1.get(i);
                    for (int j = 0; j < vbl2.size(); j++) {
                        if (vbl2.get(j) != null) {
                            ValueBlock vb2 = (ValueBlock) vbl2.get(j);

                            double val = BlockMethods.computeBlockOverlapGeneSum(vb1, vb2);

                            System.out.println(val);
                            if (val > max)
                                max = val;
                            if (val > cutoff) {
                                vbl1.set(i, null);
                                vbl2.set(j, null);

                                int[] common = MoreArray.crossFirstIndex(vb1.genes, vb2.genes);

                                ArrayList genes = new ArrayList();
                                for (int a = 0; a < common.length; a++) {
                                    if (common[a] != -1)
                                        genes.add(vb1.genes[a]);
                                }

                                ValueBlock expr_com = new ValueBlock(vb1);
                                expr_com.genes = MoreArray.ArrayListtoInt(genes);
                                expr_com.updateArea();

                                ValueBlock fit_com = new ValueBlock(vb2);
                                fit_com.genes = MoreArray.ArrayListtoInt(genes);
                                fit_com.updateArea();

                                outexpr.add(expr_com);
                                outfit.add(fit_com);

                                j = vbl2.size();
                                break;
                            }
                        }
                    }
                }
            }
        }

        System.out.println("max " + max);
        String outfexpr = args[0] + "_withfit.vbl";
        String outffit = args[1] + "_withexpr.vbl";

        String se = outexpr.toString("#" + MINER_STATIC.HEADER_VBL);
        System.out.println("outpath " + outfexpr);
        util.TextFile.write(se, outfexpr);

        String sf = outexpr.toString("#" + MINER_STATIC.HEADER_VBL);
        System.out.println("outpath " + outffit);
        util.TextFile.write(sf, outffit);
    }

    /**
     * @param args
     */

    public static void main(String[] args) {
        if (args.length == 3) {
            MatchSets rm = new MatchSets(args);
        } else {
            System.out.println("syntax: java DataMining.util.MatchSets\n" +
                    "<input vbl file> <input vbl file> <cutoff>"
            );
        }
    }
}
