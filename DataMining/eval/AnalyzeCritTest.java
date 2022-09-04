package DataMining.eval;

import DataMining.*;
import mathy.Matrix;
import util.MapArgOptions;
import util.MoreArray;
import util.Program;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * User: marcin
 * Date: May 18, 2009
 * Time: 5:49:38 PM
 */
public class AnalyzeCritTest extends Program {

    HashMap options;
    String[] valid_args = {"-dir", "-mode"};//{"-regulons", "-dir", "-dataset"};
    String[] modes = {"mean", "maxdiff", "max", "external"};
    String mode = "mean";

    //String factor = "GAL4";
    //int factor_index = -1;
    //SimpleMatrix expr_matrix;
    //ArrayList[] results_unique;
    //AnalyzeBlock abl;
    //ArrayList[] regulon_genes_int;

    int dataxdim = 40, dataydim = 100;
    int block_size = 15;

    ArrayList[] results;
    ArrayList[] results_max;

    ValueBlock refblock;
    String[] crit_labels;

    /**
     * @param args
     */
    public AnalyzeCritTest(String[] args) {
        options = MapArgOptions.maptoMap(args, valid_args);

        String dirpath = (String) options.get("-dir");
        mode = (String) options.get("-mode");

        results = MoreArray.initArrayListArray(MINER_STATIC.CRIT_LABELS.length);
        results_max = MoreArray.initArrayListArray(MINER_STATIC.CRIT_LABELS.length);

        //String regulons = (String) options.get("-regulons");
        //String datapath = (String) options.get("-dataset");
        //expr_matrix = new SimpleMatrix(datapath);
        //abl = new AnalyzeBlock(regulons, expr_matrix.ylabels, "MacIsaac");
        //factor_index = MoreArray.getArrayInd(abl.yr.factors, factor);
        //results_unique = MoreArray.initArrayListArray(MINER_STATIC.CRIT_LABELS.length);

        crit_labels = new String[MINER_STATIC.CRIT_LABELS.length];
        for (int i = 0; i < MINER_STATIC.CRIT_LABELS.length; i++) {
            crit_labels[i] = "__" + MINER_STATIC.CRIT_LABELS[i] + "__";
        }

        //MoreArray.printArray(crit_labels);

        File dir = new File(dirpath);
        String[] files = dir.list();
        if (files != null) {
            run(dirpath, files, 1);
            run(dirpath, files, 2);
        } else {
            System.out.println("AnalyzeCritTest broken dirpath " + dirpath);
            System.exit(1);
        }
    }

    /**
     * @param dirpath
     * @param files
     */
    private void run(String dirpath, String[] files, int testblock) {

        if (testblock == 1)
            refblock = EvaluateBasic.true1;
        else if (testblock == 2)
            refblock = EvaluateBasic.true2;

        for (int i = 0; i < files.length; i++) {
            String path = dirpath + sysRes.file_separator + files[i];
            if (path.indexOf("toplist") != -1) {
                int label_index = getFirstIndexOf(files[i], crit_labels);

                //System.out.println("run " + files[i] + "\t" + label_index);
                //System.out.println("ValueBlockList " + files[i] + "\t" + label_index + "\t" + crit_labels[label_index]);
                ValueBlockList vbl = null;
                try {
                    vbl = ValueBlockList.read(path, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //System.out.println("ValueBlockList " + vbl.size());
                int geneindex = -1;

                String typelabel = crit_labels[label_index];

                int position = vbl.size() - 1;

                ValueBlock curblock = (ValueBlock) vbl.get(position);
                double[] add = BlockMethods.summarizeResults(refblock, curblock, typelabel, geneindex, vbl, null,
                        dataydim, dataxdim, null, false);
                results[label_index].add(add);

                int positionmax = AnalyzeBlock.findMaxGeneExperimentF1(refblock, vbl);
                double[] addmax = MoreArray.initArray(add.length, 0.0);
                if (positionmax != -1) {
                    ValueBlock curblockmax = (ValueBlock) vbl.get(positionmax);
                    addmax = BlockMethods.summarizeResults(refblock, curblockmax, typelabel, geneindex, vbl, null,
                            dataydim, dataxdim, null, false);
                }
                results_max[label_index].add(addmax);
            }
        }
        write(dirpath, results, "analyze_" + mode + "_" + testblock + ".txt");
        write(dirpath, results_max, "analyze_max_" + mode + "_" + testblock + ".txt");
        //write(dirpath, results_unique, "analyze_regulonshaving_unique_mean.txt", "analyze_regulonshaving_unique_sd.txt");
    }

    /**
     * Returns the first index of the element of the String array which contains the String.
     *
     * @param in
     * @param id
     * @return first index
     */
    public final static int getFirstIndexOf(String in, String[] id) {
        for (int i = 0; i < id.length; i++) {
            int index = in.indexOf(id[i]);
            if (index != -1) {

                /*int nextindex = in.indexOf("_", index + id[i].length());

                 *//*while (in.charAt(nextindex) == '_') {
                    System.out.println("skipping _ " + in.charAt(nextindex));
                    nextindex += 1;
                }*//*

                int test = -1;
                System.out.println("getFirstIndexOf " + in + "\t" + id[i] + "\t" + index + "\t" + nextindex);
                System.out.println("getFirstIndexOf " + in.substring(nextindex));
                try {
                    test = Integer.parseInt(in.substring(index + id[i].length() + 1, nextindex));
                } catch (NumberFormatException e) {
                    //e.printStackTrace();
                }
                String testprefix = in.substring(0, index);
                //System.out.println("testprefix " + testprefix);
                if (test != -1)// && testprefix.equals("synthdata090621")*/

                return i;
            }
        }
        return -1;
    }

    /**
     * @param dirpath
     */
    private void write(String dirpath, ArrayList[] ar, String outname_mean) {
        try {
            String outf = dirpath + sysRes.file_separator + outname_mean;
            System.out.println("generating " + outf);
            PrintWriter pw = new PrintWriter(new FileWriter(outf), true);

            pw.println(MINER_STATIC.HEADER_EVAL + "\t" + MINER_STATIC.HEADER_EVAL);
            for (int i = 0; i < ar.length; i++) {
                if (ar[i] != null) {
                    int size = ar[i].size();
                    double[][] data = new double[size][];
                    for (int j = 0; j < size; j++) {
                        data[j] = (double[]) ar[i].get(j);
                    }
                    if (size > 0) {
                        double[] summary = Matrix.avgOverCols(data);
                        double[] summarySD = Matrix.SDOverCols(data, summary);
                        pw.println(MINER_STATIC.CRIT_LABELS[i] + "\t" + size + "\t" + MoreArray.toString(summary, "\t")
                                + "\t" + size + "\t" + MoreArray.toString(summarySD, "\t"));
                    }
                }
            }
            pw.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 4) {
            AnalyzeCritTest arg = new AnalyzeCritTest(args);
        } else {
            System.out.println("syntax: java DataMining.eval.AnalyzeCritTest\n" +
                    "<-dir dir of toplists>" +
                    "<-mode mean, max, maxdiff, external>");//\n" +
            //"<-regulons 'regulon file'>\n" +
            //"<-dataset labeled expr. data set>"
            //);
        }
    }
}