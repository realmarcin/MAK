package DataMining.util;

import DataMining.*;
import mathy.SimpleMatrix;
import util.TextFile;

import java.io.File;
import java.util.Random;

/**
 * Make N sets of random biclusters with sizes matching the input bicluster set.
 * <p/>
 * <p/>
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: 3/21/14
 * Time: 5:17 PM
 */
public class MakeRandomSet {

    ValueBlockList vbl;
    SimpleMatrix expr;
    SampleBlocks sb;

    Random rand;
    long seed = MINER_STATIC.RANDOM_SEEDS[0];//759820;

    int samples = 1000;

    /**
     * @param args
     */
    public MakeRandomSet(String[] args) {

        try {
            vbl = ValueBlockListMethods.readAny(args[0], false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        expr = new SimpleMatrix(args[1]);

        File make = new File(args[2]);
        make.mkdir();

        if(args.length ==4) {

        }
        else {
        rand = new Random(seed);
        }

        sb = new SampleBlocks(MINER_STATIC.DEFAULT_percent_allowed_missing_in_block, MINER_STATIC.DEFAULT_percent_allowed_missing_genes,
                               MINER_STATIC.DEFAULT_percent_allowed_missing_exps, expr.data.length, expr.data[0].length, rand, expr.data, false);

        for (int j = 0; j < samples; j++) {

            ValueBlockList samps = new ValueBlockList();
            for (int i = 0; i < vbl.size(); i++) {
                ValueBlock vb = (ValueBlock) vbl.get(i);

                samps.add(sampleOneBlock(vb.genes.length, vb.exps.length, j));
            }

            String stoplist = samps.toString("#" + "\n" + MINER_STATIC.HEADER_VBL);// ValueBlock.toStringShortColumns());
            String outpath = args[2] + "/" + args[0] + "_rand_" + j + ".vbl";
            System.out.println("writing samps.size() " + samps.size());
            System.out.println("writing " + outpath);
            TextFile.write(stoplist, outpath);
        }

    }

    /**
     * @param g
     * @param e
     */
    private ValueBlock sampleOneBlock(int g, int e, int count) {
        boolean stop = false;
        while (!stop) {
            //System.out.println("sampleOneBlock " + aboveNaNThreshold + "\t" + aboveNaNThresholdGene + "\t" + aboveNaNThresholdExp);
            //System.out.print(".");
            int[][] sample = sb.sampleRetInt(g, e, false);
            String label = "" + e + "_" + g + "__" + count;

           /* System.out.println("sampleOneBlock " + label);
            System.out.println("sampleOneBlock g " + sample[0]);
            System.out.println("sampleOneBlock e " + sample[1]);
            */
            if (sample != null && sample[0] != null && sample[1] != null) {

                stop = true;

                //System.out.println("sampleHash added " + label);
                //System.out.println("sampleHash added 0 "+MoreArray.toString(sample[0], ","));
                //System.out.println("sampleHash added 1 "+MoreArray.toString(sample[1], ","));

                ValueBlock v = new ValueBlock(sample[0], sample[1]);
                return v;
            }
            if (sample == null) {
                System.out.println("sampleHash is NULL " + label);
            }
            if (sample[0] == null) {
                System.out.println("sampleHash genes ind 0 is NULL " + label);
            }
            if (sample[1] == null) {
                System.out.println("sampleHash exp ind 1 is NULL " + label);
            }
        }
        return null;
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 3) {
            MakeRandomSet rm = new MakeRandomSet(args);
        } else {
            System.out.println("syntax: java DataMining.util.MakeRandomSet\n" +
                    "<vbl>\n" +
                    "<expression data>\n" +
                    "<out dir>"
            );
        }
    }
}

