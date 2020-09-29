package DataMining;

import mathy.Matrix;
import mathy.stat;
import util.MoreArray;
import util.TabFile;

import java.io.File;
import java.util.ArrayList;

/**
 * User: marcin
 * Date: Nov 11, 2010
 * Time: 4:47:02 PM
 */
public class MergeBlocks {

    ValueBlockList master = new ValueBlockList();
    ArrayList master_counts = new ArrayList();
    double minoverlap = 0.25;
    double count_incthresh = 0.25;

    /**
     * @param args
     */
    public MergeBlocks(String[] args) {

        File indir = new File(args[0]);

        if (args.length >= 3) {
            minoverlap = Double.parseDouble(args[2]);
        }
        if (args.length == 3) {
            count_incthresh = Double.parseDouble(args[3]);
        }

        int total = 0;
        String[] list = indir.list();
        for (int i = 0; i < list.length; i++) {
            String f = args[0] + "/" + list[i];
            System.out.println("reading " + f);
            ValueBlockList curlist = null;
            try {
                curlist = ValueBlockList.read(f, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("list " + curlist.size());
            for (int j = 0; j < curlist.size(); j++) {
                total++;
                ValueBlock vb = (ValueBlock) curlist.get(j);
                System.out.println("vb " + j + "\t" + vb.genes.length + "\t" + vb.exps.length);
                //System.out.println(vb.toStringShort());
                boolean any = false;
                for (int m = 0; m < master.size(); m++) {
                    ValueBlock mastervb = (ValueBlock) master.get(m);
                    System.out.println("mastervb " + j + "\t" + mastervb.genes.length + "\t" + mastervb.exps.length);
                    double overlap = BlockMethods.computeBlockOverlapGeneAndExpWithRef(mastervb, vb);
                    //System.out.println("overlap " + overlap);
                    if (overlap > minoverlap) {
                        any = true;
                        int[] orig_genes = mastervb.genes;
                        int[] orig_exps = mastervb.exps;
                        ValueBlock merged = BlockMethods.mergeBlocks(mastervb, vb);
                        master.set(m, merged);
                        int[][] counts = (int[][]) master_counts.get(m);
                        counts = BlockMethods.expandCounts(counts, orig_genes, orig_exps, merged.genes, merged.exps);
                        counts = BlockMethods.markGeneExps(vb, counts, merged);
                        master_counts.set(m, counts);
                        break;
                    }
                }
                if (!any) {
                    master.add(vb);
                    int[][] counts = BlockMethods.markGeneExps(vb, null, null);
                    master_counts.add(counts);
                }
            }
        }

        for (int i = 0; i < master.size(); i++) {
            ValueBlock ms = (ValueBlock) master.get(i);
            int[][] msc = (int[][]) master_counts.get(i);
            double[][] mscd = Matrix.convInttoDouble(msc);
            mscd = stat.norm(mscd, Matrix.findMax(mscd));
            for (int a = 0; a < ms.genes.length; a++) {
                for (int b = 0; b < ms.exps.length; b++) {
                    if (mscd[a][b] < count_incthresh)
                        mscd[a][b] = 0;
                }
            }

            System.out.println("lengths " + mscd.length + "\t" + mscd[0].length + "\t" +
                    ms.genes.length + "\t" + ms.exps.length);
            String f = "block" + i + ".txt";
            System.out.println("writing " + f);
            TabFile.write(MoreArray.toString(mscd, ""), f,
                    MoreArray.toStringArray(ms.exps), MoreArray.toStringArray(ms.genes));
        }

        System.out.println("merged from " + total + " to " + master.size());
        System.out.println("writing " + args[1]);
        ValueBlockListMethods.writeBIC(args[1],master);
    }


    /**
     * @param args
     */
    public final static void main(String[] args) {
        if (args.length == 2 || args.length == 3 || args.length == 4) {
            MergeBlocks rm = new MergeBlocks(args);
        } else {
            System.out.println("syntax: java DataMining.MergeBlocks\n" +
                    "<in dir> <out file> <OPTIONAL overlap cutoff, default=0.25> <OPTIONAL include threshold>"
            );
        }
    }

}
