package DataMining.util;

import DataMining.ValueBlock;
import DataMining.ValueBlockList;
import util.MapArgOptions;
import util.MoreArray;
import util.StringUtil;
import util.TextFile;

import java.io.File;
import java.util.HashMap;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: Jun 27, 2012
 * Time: 10:51:40 PM
 */
public class MergeSource {

    String[] valid_args = {
            "-mergelist", "-source", "-merged", "-out"
    };
    HashMap options;
    String mergelist;
    String source;
    String merged;

    ValueBlockList sourceV;
    ValueBlockList mergedV;


    /**
     * @param args
     */
    public MergeSource(String[] args) {
        init(args);

        String[] mergelistA = TextFile.readtoArray(mergelist);
        try {
            sourceV = ValueBlockList.read(source, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mergedV = ValueBlockList.read(merged, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < mergelistA.length; i++) {
            System.out.println("i " + i);
            ValueBlock mergedBlock = (ValueBlock) mergedV.get(i);
            String code = mergelistA[i];
            String[] index = code.split("_");
            System.out.println("i " + i + "\tindex.length " + index.length + "\t" + code);
            index = StringUtil.replace(index, "_", "");
            index = StringUtil.replace(index, "*", "");
            index = StringUtil.removeEmpty(index);
            ValueBlockList vm = new ValueBlockList();
            for (int j = 0; j < index.length; j++) {
                System.out.println("j " + j + "\t:" + index[j] + ":");
                //ERROR here index[j] is null
                int ind = Integer.parseInt(index[j]);
                vm.add(sourceV.get(ind - 1));
            }

            double[][] counts = new double[mergedBlock.genes.length][mergedBlock.exps.length];
            for (int c = 0; c < vm.size(); c++) {
                System.out.println("c " + c);
                ValueBlock cur = (ValueBlock) vm.get(c);
                for (int a = 0; a < cur.genes.length; a++) {
                    for (int b = 0; b < cur.exps.length; b++) {
                        int arrayInd = 0;
                        try {
                            arrayInd = MoreArray.getArrayInd(mergedBlock.genes, cur.genes[a]);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (arrayInd != -1) {
                            try {
                                int arrayInd2 = MoreArray.getArrayInd(mergedBlock.exps, cur.exps[b]);
                                if (arrayInd2 != -1) {
                                    counts[arrayInd][arrayInd2]++;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
            counts = mathy.Matrix.norm(mathy.Matrix.findMax(counts), counts);
            String outpath = "./" + merged + "/" + "counts_" + i + ".txt";
            System.out.println("writing " + i + "\t" + outpath);
            TextFile.write(MoreArray.toString(counts), outpath);
        }

    }


    /**
     * @param args
     */

    private void init(String[] args) {

        MoreArray.printArray(args);
        options = MapArgOptions.maptoMap(args, valid_args);

        if (options.get("-mergelist") != null) {
            mergelist = (String) options.get("-mergelist");
        }
        if (options.get("-source") != null) {
            source = (String) options.get("-source");
        }
        if (options.get("-merged") != null) {
            merged = (String) options.get("-merged");
        }

        File test = new File(merged);
        if (!test.isDirectory()) {
            test.mkdir();
            System.out.println("make dir " + merged);
        }

    }

    /**
     * @param args
     */
    public final static void main(String[] args) {
        if (args.length == 6) {
            MergeSource rm = new MergeSource(args);
        } else {
            System.out.println("syntax: java DataMining.util.MergeSource\n" +
                    "<-mergelist mergelist file>\n" +
                    "<-source source data>\n" +
                    "<-merged meregd data>"
            );
        }
    }
}
