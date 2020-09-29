package DataMining.util;

import DataMining.ValueBlock;
import DataMining.ValueBlockList;
import DataMining.ValueBlockListMethods;
import util.MoreArray;
import util.TabFile;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: marcin
 * Date: 2/27/17
 * Time: 11:59 PM
 */
public class MatchManytoOne {


    /**
     * @param args
     */
    public MatchManytoOne(String[] args) {


        File dir = new File(args[0]);
        String[] list = dir.list();

        ValueBlockList[] vbl_list = new ValueBlockList[list.length];
        ValueBlockList query = null;
        for (int i = 0; i < list.length; i++) {
            try {
                ValueBlockList source = ValueBlockListMethods.readAny(args[0] + "/" + list[i], false);
                vbl_list[i] = source;
            } catch (Exception e1) {
                e1.printStackTrace();
                vbl_list = null;
                System.exit(1);
            }
        }

        try {
            query = ValueBlockListMethods.readAny(args[1], false);
        } catch (Exception e) {
            e.printStackTrace();
        }


        int[][] ret = new int[query.size()][list.length];

        if (query != null && vbl_list != null) {

            for (int a = 0; a < query.size(); a++) {
                System.out.print(".");
                ValueBlock vb = (ValueBlock) query.get(a);
                String vbid = vb.blockId();
                for (int i = 0; i < list.length; i++) {

                    for (int z = 0; z < vbl_list[i].size(); z++) {
                        ValueBlock vbz = (ValueBlock) vbl_list[i].get(z);

                        if (vbz.blockId().equals(vbid)) {
                            System.out.print("*");
                            ret[a][i] = 1;
                            //break;
                        }
                    }
                }
            }
        }

        int[] xlabels = mathy.stat.createNaturalNumbers(1, query.size() + 1);

        String s = args[1] + "_match.txt";
        //System.out.println("\nwriting ... " + s);
        String[] xlabels1 = MoreArray.toStringArray(xlabels);

        System.out.println("size " + query.size() + "\t" + xlabels.length + "\t" + list.length);

        //System.out.println("xlabels");
        //MoreArray.printArray(xlabels1);
        System.out.println("ylabels");
        MoreArray.printArray(list);

        System.out.println("writing " + s);
        TabFile.write(ret, s, list, xlabels1);

    }


    /**
     * @param args
     */

    public final static void main(String[] args) {
        if (args.length == 2) {
            MatchManytoOne rm = new MatchManytoOne(args);
        } else {
            System.out.println("syntax: java DataMining.util.MatchManytoOne\n" +
                    "<dir of block lists>\n" +
                    "<single query block list>"
            );
        }
    }

}
