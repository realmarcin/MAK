package DataMining.util;

import DataMining.ValueBlock;
import DataMining.ValueBlockList;
import DataMining.ValueBlockListMethods;
import mathy.SimpleMatrix;
import util.MoreArray;
import util.StringUtil;
import util.TabFile;
import util.TextFile;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: 1/15/13
 * Time: 10:00 PM
 */
public class ToExprData {

    boolean freq = false;

    /**
     * @param args
     */
    public ToExprData(String[] args) {

        SimpleMatrix sm = new SimpleMatrix(args[0]);

        System.out.println("read matrix " + args[0]);

        if (args.length == 3 && args[2].equalsIgnoreCase("YES")) {
            freq = true;
        }
        try {
            ValueBlockList vbl = ValueBlockListMethods.readAny(args[1], false);
            System.out.println("read vbl " + args[1]);
            for (int i = 0; i < vbl.size(); i++) {
                System.out.println("vb " + i);
                ValueBlock vb = (ValueBlock) vbl.get(i);

                SimpleMatrix smout = new SimpleMatrix();
                smout.data = new double[vb.genes.length][vb.exps.length];

                String[] xlab = new String[vb.exps.length];
                String[] ylab = new String[vb.genes.length];
                for (int a = 0; a < vb.genes.length; a++) {
                    ylab[a] = sm.ylabels[vb.genes[a] - 1];
                    System.out.println(a+"\t"+(vb.genes[a] - 1)+"\t"+sm.ylabels[vb.genes[a] - 1]);
                    for (int b = 0; b < vb.exps.length; b++) {
                        xlab[b] = sm.xlabels[vb.exps[b] - 1];
                        smout.data[a][b] = sm.data[vb.genes[a] - 1][vb.exps[b] - 1];
                    }
                }

                String f = args[1] + "__" + i + "_expdata.txt";
                TabFile.write(smout.data, f, xlab, ylab);
                System.out.println("wrote " + f);

                if (freq) {
                    double[] genefreq = new double[vb.genes.length];
                    double[] expfreq = new double[vb.exps.length];

                    for (int a = 0; a < vb.genes.length; a++) {
                        double count = 0;
                        for (int j = 0; j < vbl.size(); j++) {
                            if (j != i) {
                                ValueBlock vbj = (ValueBlock) vbl.get(j);
                                int index = MoreArray.getArrayInd(vbj.genes, vb.genes[a]);
                                if (index != -1)
                                    count++;
                            }
                        }

                        genefreq[a] = count / (double) (vbl.size() - 1);
                    }

                    for (int b = 0; b < vb.exps.length; b++) {
                        double count = 0;
                        for (int j = 0; j < vbl.size(); j++) {
                            if (j != i) {
                                ValueBlock vbj = (ValueBlock) vbl.get(j);
                                int index = MoreArray.getArrayInd(vbj.exps, vb.exps[b]);
                                if (index != -1)
                                    count++;
                            }
                        }
                        expfreq[b] = count / (double) (vbl.size() - 1);
                    }


                    String f1 = args[1] + "__" + i + "_expdata_freqy.txt";
                    TextFile.write(StringUtil.join("\t", MoreArray.toStringArray(genefreq)), f1);
                    System.out.println("wrote " + f1);

                    String f2 = args[1] + "__" + i + "_expdata_freqx.txt";
                    TextFile.write(StringUtil.join("\t", MoreArray.toStringArray(expfreq)), f2);
                    TabFile.write(smout.data, f2, xlab, ylab);
                    System.out.println("wrote " + f2);

                }
                System.out.println("done " + i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @param args
     */
    public final static void main(String[] args) {
        if (args.length == 2 || args.length == 3) {
            ToExprData rm = new ToExprData(args);
        } else {
            System.out.println("syntax: java DataMining.util.ToExprData\n" +
                    "<expr data>\n" +
                    "<value block list ANY PATH PREFIX WILL BE USED AS OUTPUT PREFIX>\n" +
                    "<YES = output row and col bicluster set frequencies per bicluster OPTIONAL>"
            );
        }
    }
}
