package DataMining.util;

import DataMining.MINER_STATIC;
import DataMining.ValueBlock;
import DataMining.ValueBlockList;
import DataMining.ValueBlockListMethods;
import util.ParsePath;

import java.util.ArrayList;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: Nov 10, 2011
 * Time: 12:45:48 AM
 */
public class MakeNRList {

    boolean debug = true;
    ValueBlockList vbl;

    /**
     * @param args
     */
    public MakeNRList(String[] args) {

        try {
            vbl = ValueBlockListMethods.readAny(args[0], false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ParsePath pp = new ParsePath(args[0]);
        String prefix = pp.getName() + "_";

        int size = vbl.size();

        //ArrayList hashes = new ArrayList();
        //build gene_exp hash
        /*for (int i = 0; i < size; i++) {
            if (i % 100 == 0)
                System.out.print(i + "/" + vbl.size() + " ");
            ValueBlock vi = (ValueBlock) vbl.get(i);
            HashMap hvi = new HashMap();
            for (int g = 0; g < vi.genes.length; g++) {
                for (int e = 0; e < vi.exps.length; e++) {
                    hvi.put("" + vi.genes[g] + "_" + vi.exps[e], 1);
                }
            }
            hashes.add(hvi);
        }*/

        ArrayList rem = new ArrayList();
        //pairwise bicluster overlap
        for (int i = 0; i < size; i++) {
            System.out.print(".");
            //HashMap hi = (HashMap) hashes.get(i);
            ValueBlock vi = (ValueBlock) vbl.get(i);
            for (int j = i + 1; j < size; j++) {
                ValueBlock vj = (ValueBlock) vbl.get(j);
                //HashMap hj = (HashMap) hashes.get(j);
                //double overlap = BlockMethods.computeBlockOverlapGeneExpRootProduct(hi, hj,
                //        vi.genes.length, vi.exps.length, vj.genes.length, vj.exps.length);
                // if (overlap == 1)
                if (vi.equals(vj))
                    if (vi.genes.length * vi.exps.length > vj.genes.length * vj.exps.length) {
                        rem.add(j);
                    } else {
                        rem.add(i);
                    }
            }
        }
        int counter = 0;
        if (rem.size() > 0) {
            for (int i = 0; i < rem.size(); i++) {
                int cur = (Integer) rem.get(i);
                System.out.println("removing " + cur);
                vbl.remove(cur - counter);
                counter++;
            }
        }
        System.out.println("\nremove " + counter);
        String outpath = args.length == 2 ? args[1] : prefix + "_nr.txt";

        //RAUF'S CHANGES; ORIGINAL CODE WAS:
        /*
        String s = vbl.toString("#" + MINER_STATIC.HEADER_VBL);//ValueBlock.toStringShortColumns());
        System.out.println("outpath " + outpath);
        util.TextFile.write(s, outpath);
        */

        String s = vbl.toString("#" + MINER_STATIC.HEADER_VBL);//ValueBlock.toStringShortColumns());
        String[] ss = s.split("\n");
        String news = "";
        for (int co = 0; co < ss.length; co++) {
            if (co != 0) {
                news += "\"" + ss[co].split("\t")[2] + "\"\n";
            }
        }

        System.out.println("outpath " + outpath);
        util.TextFile.write(news, outpath);
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 1 || args.length == 2) {
            MakeNRList rm = new MakeNRList(args);
        } else {
            System.out.println("syntax: java DataMining.util.MakeNRList\n" +
                    "<bicluster list>\n" +
                    "<OPTIONAL out expr data path>"
            );
        }
    }

}
