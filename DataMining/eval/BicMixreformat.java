package DataMining.eval;

import DataMining.ValueBlock;
import DataMining.ValueBlockList;
import DataMining.ValueBlockListMethods;
import mathy.stat;
import util.MoreArray;
import util.StringUtil;
import util.TabFile;

import java.util.ArrayList;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: 10/25/16
 * Time: 9:41 PM
 */
public class BicMixreformat {


    /**
     * @param args
     */
    public BicMixreformat(String[] args) {


        String[][] exdata = TabFile.readtoArray(args[0]);
        double[][] exdata_d = MoreArray.convfromString(exdata);
        String[][] lamdata = TabFile.readtoArray(args[1]);
        double[][] lamdata_d = MoreArray.convfromString(lamdata);

        System.out.println("exdata " + exdata.length + "\t" + exdata[0].length);
        System.out.println("lamdata " + exdata_d.length + "\t" + lamdata[0].length);
        System.out.println("exdata_d " + exdata.length + "\t" + exdata_d[0].length);
        System.out.println("lamdata_d " + lamdata_d.length + "\t" + lamdata_d[0].length);


        //number of values >0 for each row and column
        int[] excount = stat.countNotEqualByRow(exdata_d, 0.0);

        //MoreArray.printArray(exdata_d[0]);
        System.out.println("excount " + excount[0] + "\t" + excount[1] + "\t" + excount[2] + "\t" +
                excount[3] + "\t" + excount[4]);
        int[] lamcount = stat.countNotEqualByCol(lamdata_d, 0.0);

        System.out.println("excount len " + excount.length + "\tlamcount len " + lamcount.length + "\n");


        ValueBlockList out = new ValueBlockList();
        for (int i = 0; i < exdata.length; i++) {
            System.out.println("excount " + excount[i] + "\tlamcount " + lamcount[i]);
            if (excount[i] != exdata[0].length && lamcount[i] != lamdata.length) {
                ArrayList genes = new ArrayList();
                ArrayList exps = new ArrayList();
                ValueBlock vb = new ValueBlock();

                //genes
                //get 0s
                int[] notexps = StringUtil.locateEquals(exdata[i], "0");

                for (int a = 0; a < exdata[0].length; a++) {
                    if (stat.findIndex(notexps, a) == -1)
                        exps.add(a + 1);
                }

                //exps
                //get 0s
                int[] notgenes = StringUtil.locateEquals(MoreArray.extractColumnStr(lamdata, i + 1), "0");

                for (int b = 0; b < lamdata.length; b++) {
                    if (stat.findIndex(notgenes, b) == -1)
                        genes.add(b + 1);
                }

                if (genes.size() > 1 && exps.size() > 1) {

                    vb.genes = MoreArray.tointArray(genes);
                    vb.exps = MoreArray.tointArray(exps);

                    vb = new ValueBlock(genes, exps);
                    out.add(vb);
                    System.out.println("Adding block " + i + "\tg " + genes.size() + "\te " + exps.size() + "\n");
                } else {
                    System.out.println("Rejecting block " + i + "\tg " + genes.size() + "\te " + exps.size() + "\n");
                }
            } else {
                System.out.println("Rejecting block");
            }
        }

        ValueBlockListMethods.writeBIC(args[2] + ".bic", out);
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 3) {
            BicMixreformat arg = new BicMixreformat(args);
        } else {
            System.out.println("syntax: java DataMining.eval.BicMixreformat\n" +
                    "<BicMix EX>\n" +
                    "<BicMix LAM>\n" +
                    "<out file>"

            );
        }
    }
}
