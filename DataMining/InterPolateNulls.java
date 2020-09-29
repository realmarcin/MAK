package DataMining;

import mathy.Matrix;
import mathy.stat;
import util.MoreArray;
import util.StringUtil;
import util.TabFile;

import java.io.*;
import java.util.HashMap;

/**
 * Interpolate the null distribution real samples.
 * <p/>
 * User: marcin
 * Date: Mar 1, 2010
 * Time: 5:18:46 PM
 */
public class InterPolateNulls {

    boolean debug = false;

    String outf;

    double[][] rawmean, rawsd;
    double[][] rawmeanspecial, rawsdspecial;


    int[] special_crit = {0, 1, 32, 33, 34, 35};

    String[] labels = {
            "EXPR_INTER_MEDIAN",   //1
            "EXPR_INTER_0.5IQR",
            "EXPR_MSER_MEDIAN",       //3
            "EXPR_MSER_0.5IQR",
            "EXPR_MSE_MEDIAN",           //5
            "EXPR_MSE_0.5IQR",
            "EXPR_MSEC_MEDIAN",          //7
            "EXPR_MSEC_0.5IQR",
            "EXPR_MADR_MEDIAN",             //9
            "EXPR_MADR_0.5IQR",
            "EXPR_KENDALLR_MEDIAN",            //11
            "EXPR_KENDALLR_0.5IQR",
            "EXPR_KENDALLC_MEDIAN",                //13
            "EXPR_KENDALLC_0.5IQR",
            "EXPR_BINARYR_MEDIAN",                     //15
            "EXPR_BINARYR_0.5IQR",
            "EXPR_BINARYC_MEDIAN",                         //17
            "EXPR_BINARYC_0.5IQR",
            "EXPR_GEERE_MEDIAN",                           //19
            "EXPR_GEERE_0.5IQR",
            "EXPR_GEECE_MEDIAN",                           //21
            "EXPR_GEECE_0.5IQR",
            "EXPR_LARSRE_MEDIAN",                          //23
            "EXPR_LARSRE_0.5IQR",
            "EXPR_LARSCE_MEDIAN",                          //25
            "EXPR_LARSCE_0.5IQR",
            "EXPR_MEAN_MEDIAN",                            //27
            "EXPR_MEAN_0.5IQR",
            "EXPR_MEDCMEAN_MEDIAN",                        //29
            "EXPR_MEDCMEAN_0.5IQR",
            "EXPR_MEDRMEAN_MEDIAN",                        //31
            "EXPR_MEDRMEAN_0.5IQR",
            "EXPR_MAXTF_MEDIAN",                           //33
            "EXPR_MAXTF_0.5IQR",

            "EXPR_FEAT_MEDIAN",                           //35
            "EXPR_FEAT_0.5IQR",


     /*       "EXPR_MSE_MEAN_MEDIAN",
            "EXPR_MSE_MEAN_0.5IQR",
            "EXPR_MSER_MEDRMEAN_MEDIAN",
            "EXPR_MSER_MEDRMEAN_0.5IQR",
            "EXPR_MSEC_MEDCMEAN_MEDIAN",
            "EXPR_MSEC_MEDCMEAN_0.5IQR",
            "EXPR_MSER_GEERE_MEDRMEAN_MEDIAN",
            "EXPR_MSER_GEERE_MEDRMEAN_0.5IQR",
            "EXPR_MSEC_GEECE_MEDCMEAN_MEDIAN",
            "EXPR_MSEC_GEECE_MEDCMEAN_0.5IQR",

            "EXPR_MSE_KENDALLR_MEDIAN",
            "EXPR_MSE_KENDALLR_0.5IQR",
            "EXPR_MSER_KENDALLR_MEDIAN",
            "EXPR_MSER_KENDALLR_0.5IQR",
            "EXPR_MSEC_KENDALLC_MEDIAN",
            "EXPR_MSEC_KENDALLC_0.5IQR",

            "EXPR_MSE_KENDALLR_MEAN_MEDIAN",
            "EXPR_MSE_KENDALLR_MEAN_0.5IQR",
            "EXPR_MSER_KENDALLR_MEDRMEAN_MEDIAN",
            "EXPR_MSER_KENDALLR_MEDRMEAN_0.5IQR",
            "EXPR_MSEC_KENDALLC_MEDCMEAN_MEDIAN",
            "EXPR_MSEC_KENDALLC_MEDCMEAN_0.5IQR",

            "EXPR_MSE_KENDALLR_GEERE_MEDIAN",
            "EXPR_MSE_KENDALLR_GEERE_0.5IQR",
            "EXPR_MSE_KENDALLR_GEECE_MEDIAN",
            "EXPR_MSE_KENDALLR_GEECE_0.5IQR",
            "EXPR_MSER_KENDALLR_GEERE_MEDIAN",
            "EXPR_MSER_KENDALLR_GEERE_0.5IQR",
            "EXPR_MSEC_KENDALLC_GEECE_MEDIAN",
            "EXPR_MSEC_KENDALLC_GEECE_0.5IQR",

            "EXPR_MSE_KENDALLR_GEERE_MEDRMEAN_MEDIAN",
            "EXPR_MSE_KENDALLR_GEERE_MEDRMEAN_0.5IQR",
            "EXPR_MSE_KENDALLC_GEECE_MEDCMEAN_MEDIAN",
            "EXPR_MSE_KENDALLC_GEECE_MEDCMEAN_0.5IQR",
            "EXPR_MSER_KENDALLR_GEERE_MEDRMEAN_MEDIAN",
            "EXPR_MSER_KENDALLR_GEERE_MEDRMEAN_0.5IQR",
            "EXPR_MSEC_KENDALLC_GEECE_MEDCMEAN_MEDIAN",
            "EXPR_MSEC_KENDALLC_GEECE_MEDCMEAN_0.5IQR",
*/
            "EXPR_CORR_MEDIAN",//79
            "EXPR_CORR_0.5IQR",//79
            "EXPR_CORC_MEDIAN",//80"
            "EXPR_CORC_0.5IQR",//80"
            /*"EXPR_CORR_MEDRMEAN_MEDIAN",//79
            "EXPR_CORR_MEDRMEAN_0.5IQR",//79
            "EXPR_CORC_MEDCMEAN_MEDIAN",//80
            "EXPR_CORC_MEDCMEAN_0.5IQR",//80*/

            /*"EXPR_MSE_CORR_MEDIAN",//81
            "EXPR_MSE_CORR_0.5IQR",//81
            "EXPR_MSE_CORC_MEDIAN",//82
            "EXPR_MSE_CORC_0.5IQR",//82
            "EXPR_MSE_CORR_MEDRMEAN_MEDIAN",//81
            "EXPR_MSE_CORR_MEDRMEAN_0.5IQR",//81
            "EXPR_MSE_CORC_MEDCMEAN_MEDIAN",//82
            "EXPR_MSE_CORC_MEDCMEAN_0.5IQR",//82
            "EXPR_MSER_CORR_MEDIAN",//83
            "EXPR_MSER_CORR_0.5IQR",//83
            "EXPR_MSEC_CORC_MEDIAN",//84
            "EXPR_MSEC_CORC_0.5IQR",//84
            "EXPR_MSER_CORR_MEDRMEAN_MEDIAN",//83
            "EXPR_MSER_CORR_MEDRMEAN_0.5IQR",//83
            "EXPR_MSEC_CORC_MEDCMEAN_MEDIAN",//84
            "EXPR_MSEC_CORC_MEDCMEAN_0.5IQR",//84
            "EXPR_MSER_CORR_KENDALLR_MEDIAN",//85
            "EXPR_MSER_CORR_KENDALLR_0.5IQR",//85
            "EXPR_MSEC_CORC_KENDALLC_MEDIAN",//86
            "EXPR_MSEC_CORC_KENDALLC_0.5IQR",//86
            "EXPR_MSER_CORR_KENDALLR_MEDRMEAN_MEDIAN",//85
            "EXPR_MSER_CORR_KENDALLR_MEDRMEAN_0.5IQR",//85
            "EXPR_MSEC_CORC_KENDALLC_MEDCMEAN_MEDIAN",//86
            "EXPR_MSEC_CORC_KENDALLC_MEDCMEAN_0.5IQR",//86
            "EXPR_CORR_KENDALLR_MEDIAN",//87
            "EXPR_CORR_KENDALLR_0.5IQR",//87
            "EXPR_CORC_KENDALLC_MEDIAN",//88
            "EXPR_CORC_KENDALLC_0.5IQR",//88
            "EXPR_CORR_KENDALLR_MEDRMEAN_MEDIAN",//87
            "EXPR_CORR_KENDALLR_MEDRMEAN_0.5IQR",//87
            "EXPR_CORC_KENDALLC_MEDCMEAN_MEDIAN",//88
            "EXPR_CORC_KENDALLC_MEDCMEAN_0.5IQR",//88
            "EXPR_MSER_CORR_KENDALLR_GEERE_MEDIAN",//89
            "EXPR_MSER_CORR_KENDALLR_GEERE_0.5IQR",//89
            "EXPR_MSEC_CORC_KENDALLC_GEECE_MEDIAN",//90
            "EXPR_MSEC_CORC_KENDALLC_GEECE_0.5IQR",//90
            "EXPR_MSER_CORR_KENDALLR_GEERE_MEDRMEAN_MEDIAN",//89
            "EXPR_MSER_CORR_KENDALLR_GEERE_MEDRMEAN_0.5IQR",//89
            "EXPR_MSEC_CORC_KENDALLC_GEECE_MEDCMEAN_MEDIAN",//90
            "EXPR_MSEC_CORC_KENDALLC_GEECE_MEDCMEAN_0.5IQR",//90*/

            "EXPR_EUCR_MEDIAN",//79
            "EXPR_EUCR_0.5IQR",//79
            "EXPR_EUCC_MEDIAN",//80
            "EXPR_EUCC_0.5IQR",//80

            "EXPR_KENDALL_MEDIAN",
            "EXPR_KENDALL_0.5IQR",
            "EXPR_BINARY_MEDIAN",
            "EXPR_BINARY_0.5IQR",
            "EXPR_GEE_MEDIAN",
            "EXPR_GEE_0.5IQR",

            "EXPR_LARS_MEDIAN",
            "EXPR_LARS_0.5IQR",

            "EXPR_COR_MEDIAN",
            "EXPR_COR_0.5IQR",

            "EXPR_EUC_MEDIAN",
            "EXPR_EUC_0.5IQR",


            "EXPR_SPEARMANR_MEDIAN",
            "EXPR_SPEARMANR_0.5IQR",
            "EXPR_SPEARMANC_MEDIAN",
            "EXPR_SPEARMANC_0.5IQR",
            "EXPR_SPEARMAN_MEDIAN",
            "EXPR_SPEARMAN_0.5IQR"

            /*"EXPR_MSE_MAXTF_MEDIAN",
            "EXPR_MSE_MAXTF_0.5IQR",
            "EXPR_MSE_MEAN_MAXTF_MEDIAN",
            "EXPR_MSE_MEAN_MAXTF_0.5IQR",
            "EXPR_MSER_MAXTF_MEDIAN",
            "EXPR_MSER_MAXTF_0.5IQR",
            "EXPR_MSER_MEDRMEAN_MAXTF_MEDIAN",
            "EXPR_MSER_MEDRMEAN_MAXTF_0.5IQR",
            "EXPR_MSEC_MAXTF_MEDIAN",
            "EXPR_MSEC_MAXTF_0.5IQR",
            "EXPR_MSEC_MEDCMEAN_MAXTF_MEDIAN",
            "EXPR_MSEC_MEDCMEAN_MAXTF_0.5IQR",
            "EXPR_MEAN_MAXTF_MEDIAN",
            "EXPR_MEAN_MAXTF_0.5IQR",
            "EXPR_MEDRMEAN_MAXTF_MEDIAN",
            "EXPR_MEDRMEAN_MAXTF_0.5IQR",
            "EXPR_MEDCMEAN_MAXTF_MEDIAN",
            "EXPR_MEDCMEAN_MAXTF_0.5IQR",*/
    };

    String[] critlabels = {
            "INTER", //Rauf's change made into captials
            "INTER",//1
            "MSER",
            "MSER",//3
            "MSE",
            "MSE",//5
            "MSEC",
            "MSEC",//7
            "MADR",
            "MADR",//9
            "KENDALLR",
            "KENDALLR",//11
            "KENDALLC",
            "KENDALLC",//13
            "BINARYR",
            "BINARYR",//11
            "BINARYC",
            "BINARYC",//13
            "GEERE",
            "GEERE",//15
            "GEECE",
            "GEECE",//17
            "LARSRE",
            "LARSRE",//19
            "LARSCE",
            "LARSCE",//21
            "MEAN",
            "MEAN",//23
            "MEDCMEAN",
            "MEDCMEAN",//25
            "MEDRMEAN",
            "MEDRMEAN",//27
            "MAXTF",
            "MAXTF",//29
            "FEAT", //Rauf's change made into capitals
            "FEAT",

            /*"MSE_MEAN",
            "MSE_MEAN",
            "MSER_MEDRMEAN",
            "MSER_MEDRMEAN",
            "MSEC_MEDCMEAN",
            "MSEC_MEDCMEAN",
            "MSER_GEERE_MEDRMEAN",
            "MSER_GEERE_MEDRMEAN",
            "MSEC_GEECE_MEDCMEAN",
            "MSEC_GEECE_MEDCMEAN",

            "MSE_Kendall",
            "MSE_Kendall",
            "MSER_Kendall",
            "MSER_Kendall",
            "MSEC_KendallC",
            "MSEC_KendallC",

            "MSE_KendallR_MEAN",
            "MSE_KendallR_MEAN",
            "MSER_KendallR_MEDRMEAN",
            "MSER_KendallR_MEDRMEAN",
            "MSEC_KendallC_MEDCMEAN",
            "MSEC_KendallC_MEDCMEAN",

            "MSE_KendallR_GEERE",
            "MSE_KendallR_GEERE",
            "MSE_KendallR_GEECE",
            "MSE_KendallR_GEECE",
            "MSER_KendallR_GEERE",
            "MSER_KendallR_GEERE",
            "MSEC_KendallC_GEECE",
            "MSEC_KendallC_GEECE",

            "MSE_KendallR_GEERE_MEDRMEAN",
            "MSE_KendallR_GEERE_MEDRMEAN",
            "MSE_KendallR_GEECE_MEDCMEAN",
            "MSE_KendallR_GEECE_MEDCMEAN",
            "MSER_KendallR_GEERE_MEDRMEAN",
            "MSER_KendallR_GEERE_MEDRMEAN",
            "MSEC_KendallC_GEECE_MEDCMEAN",
            "MSEC_KendallC_GEECE_MEDCMEAN",*/

            "CORR",
            "CORR",
            "CORC",
            "CORC",
            /*"CORR_MEDRMEAN",
            "CORR_MEDRMEAN",
            "CORC_MEDCMEAN",
            "CORC_MEDCMEAN",

            "MSE_CORR",
            "MSE_CORR",
            "MSE_CORC",
            "MSE_CORC",
            "MSE_CORR_MEDRMEAN",
            "MSE_CORR_MEDRMEAN",
            "MSE_CORC_MEDCMEAN",
            "MSE_CORC_MEDCMEAN",
            "MSER_CORR",
            "MSER_CORR",
            "MSEC_CORC",
            "MSEC_CORC",
            "MSER_CORR_MEDRMEAN",
            "MSER_CORR_MEDRMEAN",
            "MSEC_CORC_MEDCMEAN",
            "MSEC_CORC_MEDCMEAN",
            "MSER_KendallR_CORR",
            "MSER_KendallR_CORR",
            "MSEC_KendallC_CORC",
            "MSEC_KendallC_CORC",
            "MSER_KendallR_CORR_MEDRMEAN",
            "MSER_KendallR_CORR_MEDRMEAN",
            "MSEC_KendallC_CORC_MEDCMEAN",
            "MSEC_KendallC_CORC_MEDCMEAN",
            "KendallR_CORR",
            "KendallR_CORR",
            "KendallC_CORC",
            "KendallC_CORC",
            "KendallR_CORR_MEDRMEAN",
            "KendallR_CORR_MEDRMEAN",
            "KendallC_CORC_MEDCMEAN",
            "KendallC_CORC_MEDCMEAN",
            "MSER_KendallR_CORR_GEERE",
            "MSER_KendallR_CORR_GEERE",
            "MSEC_KendallC_CORC_GEECE",
            "MSEC_KendallC_CORC_GEECE",
            "MSER_KendallR_CORR_GEERE_MEDRMEAN",
            "MSER_KendallR_CORR_GEERE_MEDRMEAN",
            "MSEC_KendallC_CORC_GEECE_MEDCMEAN",
            "MSEC_KendallC_CORC_GEECE_MEDCMEAN",
*/
            "EUCR",
            "EUCR",
            "EUCC",
            "EUCC",

            "KENDALL",
            "KENDALL",
            "BINARY",
            "BINARY",
            "GEE",
            "GEE",

            "LARS",
            "LARS",
            "COR",
            "COR",
            "EUC",
            "EUC",

            "SPEARMANR",
            "SPEARMANR",
            "SPEARMANC",
            "SPEARMANC",
            "SPEARMAN",
            "SPEARMAN"


            /* "MSE_MAXTF",
            "MSE_MAXTF",
            "MSE_MEAN_MAXTF",
            "MSE_MEAN_MAXTF",
            "MSER_MAXTF",
            "MSER_MAXTF",
            "MSER_MEDRMEAN_MAXTF",
            "MSER_MEDRMEAN_MAXTF",
            "MSEC_MAXTF",
            "MSEC_MAXTF",
            "MSEC_MEDCMEAN_MAXTF",
            "MSEC_MEDCMEAN_MAXTF",

            "MEAN_MAXTF",
            "MEAN_MAXTF",
            "MEDRMEAN_MAXTF",
            "MEDRMEAN_MAXTF",
            "MEDCMEAN_MAXTF",
            "MEDCMEAN_MAXTF",*/
    };

    int[] gene_sample_indices = {};
    int[] exp_sample_indices = {};


    //alphabetical order of mean and sd statistics
    int meanindex = 0;
    int sdindex = 1;


    /**
     * @param args
     */
    public InterPolateNulls(String[] args) {
        //MoreArray.printArray(args);

        if (args.length == 3)
            debug = StringUtil.isTrueorYes(args[2]);
        doDir(args);

        System.exit(0);
    }

    /**
     * @param args
     */
    private void makePseudoCountFile(String[] args) throws IOException {
        File dir = new File(args[0]);
        File[] directoryListing = dir.listFiles();
        HashMap min_vals = null;
        if (directoryListing != null) {
            for (File child : directoryListing) {
                // Do something with child
                String fname = child.getName();
                if (fname.endsWith("_vals.txt")) {
                    String criteria = fname.split("_")[-2];
                    FileInputStream fstream = new FileInputStream(dir + fname);
                    BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
                    String line;
                    String min = (String) min_vals.get(criteria);
                    if (min == null) {
                        min_vals.put(criteria, "250.0");
                    }
                    int count = 0;
                    while ((line = br.readLine()) != null) {
                        String[] ls = line.split("\t");
                        for (int i = 2; i < ls.length; i++) {
                            String str_val = ls[i];
                            double val = Double.parseDouble(str_val);
                            String str_min_val = (String) min_vals.get(criteria);
                            double min_val = Double.parseDouble(str_min_val);
                            if (val < min_val) {
                                // min_val
                            }
                        }
                    }

                }
            }
        }
        outf = args[1];

        PrintWriter writer = new PrintWriter(outf + "", "UTF-8");

    }

    /**
     * @param args
     */
    private void doDir(String[] args) {
        File dir = new File(args[0]);
        String[] list = dir.list();
        outf = args[1];
        int[] countfiles = new int[2];

        int[][] filecount = new int[labels.length][2];
        for (int j = 0; j < labels.length; j++) {

            rawmean = null;
            rawsd = null;

            rawmeanspecial = null;
            rawsdspecial = null;

            countfiles[0] = 0;
            countfiles[1] = 0;

            System.out.println("doDir label bf " + labels[j] + "\t" + j
                    + "\t" + (j % 2) + "\t" + meanindex + "\t" + sdindex);

            countfiles = doOneCrit(dir, list, countfiles, j);
            filecount[j][0] = countfiles[0];
            filecount[j][1] = countfiles[1];
            System.out.println("doDir label af " + labels[j] + "\t" + j +
                    "\t'mean' files " + countfiles[0] + "\t'sd' files " + countfiles[1]
                    + "\t" + (j % 2) + "\t" + meanindex + "\t" + sdindex);

            if (rawmean != null || rawsd != null) {
                if (countfiles[0] > 0 && j % 2 == meanindex) {
                    if (debug) {
                        System.out.println("norm " + countfiles[0]);
                        MoreArray.printArray(rawmean);
                    }
                    rawmean = stat.norm(rawmean, ((double) countfiles[0]));
                    String[][] strings = MoreArray.toString(rawmean, "", "");
                    String f = outf + critlabels[j] + "_median_raw.txt";
                    System.out.println("writing " + f);
                    TabFile.write(strings, f);
                } else if (countfiles[1] > 0 && j % 2 == sdindex) {
                    rawsd = stat.norm(rawsd, ((double) countfiles[1]));
                    String[][] strings = MoreArray.toString(rawsd, "", "");
                    String f = outf + critlabels[j] + "_0.5IQR_raw.txt";
                    System.out.println("writing " + f);
                    TabFile.write(strings, f);
                }
            } else if (rawmeanspecial != null || rawsdspecial != null) {
                if (countfiles[0] > 0 && j % 2 == meanindex) {
                    rawmeanspecial = stat.norm(rawmeanspecial, ((double) countfiles[0]));
                    String[][] strings = MoreArray.toString(rawmeanspecial, "", "");
                    //String[][] out = new String[1][];
                    //out[0] = strings;
                    gene_sample_indices = stat.createNaturalNumbers(1, rawmeanspecial.length);
                    //out = MoreArray.insertRow(out, MoreArray.toStringArray(gene_sample_indices), 1);
                    String f = outf + critlabels[j] + "_median_raw.txt";
                    System.out.println("writing " + f);
                    TabFile.write(strings, f);
                } else if (countfiles[1] > 0 && j % 2 == sdindex) {
                    rawsdspecial = stat.norm(rawsdspecial, ((double) countfiles[1]));
                    String[][] strings = MoreArray.toString(rawsdspecial, "", "");
                    //String[][] out = new String[1][];
                    //out[0] = strings;
                    gene_sample_indices = stat.createNaturalNumbers(1, rawsdspecial.length);
                    //out = MoreArray.insertRow(out, MoreArray.toStringArray(gene_sample_indices), 1);
                    String f = outf + critlabels[j] + "_0.5IQR_raw.txt";
                    System.out.println("writing " + f);
                    TabFile.write(strings, f);
                }
            }
        }

        for (int j = 0; j < labels.length; j++) {
            System.out.println("summary " + labels[j] + "\t" + filecount[j][0] + "\t" + filecount[j][1]);
        }
    }

    /**
     * @param dir
     * @param list
     * @param countfiles
     * @param j
     */
    private int[] doOneCrit(File dir, String[] list, int[] countfiles, int j) {
        System.out.println("doOneCrit_start " + j + "\t" + labels[j] + "\t" + list.length);
        for (int i = 0; i < list.length; i++) {
            String s = list[i].toUpperCase();

            int index = s.indexOf(labels[j]);
            if (debug)
                System.out.println("doOneCrit " + index + "\t" + s + "\tlabel-" + labels[j]);
            if (index != -1) {
                //exclude special criteria (vector instead of matrix null)
                int test = -1;
                try {
                    test = MoreArray.getArrayInd(special_crit, j);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (debug) {
                    System.out.println("doOneCrit " + s + "\t" + labels[j] + "\tindex " + index + "\ttest " + test + "\t" + j);
                    System.out.println("doOneCrit file recognized " + list[i] + "\t" + labels[j] + "\t" +
                            (j % 2) + "\t" + meanindex + "\t" + sdindex);
                }
                if (test == -1) {
                    if (debug)
                        System.out.println("detected regular crit " + (j % 2) + "\tfile " + list[i]);
                    if (list[i].indexOf("median") != -1 && j % 2 == meanindex) {//j % 2 == meanindex) {
                        if (rawmean == null) {
                            rawmean = MoreArray.convfromString(TabFile.readtoArray(dir + "/" + list[i]));
                            countfiles[0]++;
                        } else {
                            double[][] rawexprmeantmp = new double[0][];
                            try {
                                rawexprmeantmp = MoreArray.convfromString(TabFile.readtoArray(dir + "/" + list[i]));
                            } catch (Exception e) {
                                System.out.println("tried reading " + dir + "/" + list[i]);
                                e.printStackTrace();
                            }
                            rawmean = Matrix.add(rawmean, rawexprmeantmp);
                            countfiles[0]++;
                        }
                    } else if (list[i].indexOf("0.5IQR") != -1 && j % 2 == sdindex) {//(j % 2 == sdindex) {
                        String readStr = dir + "/" + list[i];
                        if (rawsd == null) {
                            rawsd = MoreArray.convfromString(TabFile.readtoArray(readStr));
                            countfiles[1]++;
                        } else {
                            double[][] rawexprsdtmp = MoreArray.convfromString(TabFile.readtoArray(dir + "/" + list[i]));
                            rawsd = Matrix.add(rawsd, rawexprsdtmp);
                            countfiles[1]++;
                        }
                    }
                } else {
                    if (debug)
                        System.out.println("detected special crit");
                    if (j % 2 == meanindex) {
                        if (rawmeanspecial == null) {
                            String[][] g = TabFile.readtoArray(dir + "/" + list[i]);
                            System.out.println("strings " + g.length + "\t" + g[0].length);
                            System.out.println("strings " + MoreArray.toString(g[0], ","));// + "\t" + strings[0].length);
                            System.out.println("strings " + MoreArray.toString(g[1], ","));// + "\t" + strings[0].length);

                            String[] ind = g[0];
                            ind = MoreArray.removeEntry(ind, 1);
                            String[] data = g[1];
                            data = MoreArray.removeEntry(data, data.length);
                            String[][] newdata = {ind, data};
                            rawmeanspecial = MoreArray.convfromString(newdata);//
                            countfiles[0]++;
                        } else {
                            String[][] g = TabFile.readtoArray(dir + "/" + list[i]);

                            System.out.println("strings " + g.length + "\t" + g[0].length);
                            System.out.println("strings " + MoreArray.toString(g[0], ","));// + "\t" + strings[0].length);
                            System.out.println("strings " + MoreArray.toString(g[1], ","));// + "\t" + strings[0].length);

                            String[] ind = g[0];
                            ind = MoreArray.removeEntry(ind, 1);
                            String[] data = g[1];
                            data = MoreArray.removeEntry(data, data.length);
                            String[][] newdata = {ind, data};
                            double[][] rawexprmeantmp = MoreArray.convfromString(newdata);
                            rawmeanspecial = Matrix.add(rawmeanspecial, rawexprmeantmp);
                            countfiles[0]++;
                        }
                    } else if (j % 2 == sdindex) {
                        String readStr = dir + "/" + list[i];
                        if (rawsdspecial == null) {
                            String[][] g = TabFile.readtoArray(readStr);
                            String[] ind = g[0];
                            ind = MoreArray.removeEntry(ind, 1);
                            String[] data = g[1];
                            data = MoreArray.removeEntry(data, data.length);
                            String[][] newdata = {ind, data};
                            rawsdspecial = MoreArray.convfromString(newdata);
                            countfiles[1]++;
                        } else {
                            String[][] g = TabFile.readtoArray(dir + "/" + list[i]);
                            String[] ind = g[0];
                            ind = MoreArray.removeEntry(ind, 1);
                            String[] data = g[1];
                            data = MoreArray.removeEntry(data, data.length);
                            String[][] newdata = {ind, data};
                            double[][] rawexprsdtmp = MoreArray.convfromString(newdata);
                            rawsdspecial = Matrix.add(rawsdspecial, rawexprsdtmp);
                            countfiles[1]++;
                        }
                    }
                }
            }
        }

        return countfiles;
    }


    /**
     * @param dir
     * @param list
     * @param countfiles
     */
    /* private int[] doPPI(File dir, String[] list, int[] countfiles) {
        double[] ppilabels = null;
        for (int i = 0; i < list.length; i++) {
            int index = list[i].indexOf(labels[0]);
            System.out.println(i + "\t" + index + "\t" + list[i]);
            if (index != -1) {
                double[][] rawppitmp = MoreArray.convfromString(TabFile.readtoArray(dir + "/" + list[i]));
                if (rawppi == null) {
                    rawppi = rawppitmp[1];
                    ppilabels = rawppitmp[0];
                } else
                    rawppi = Matrix.add(rawppi, rawppitmp[1]);
                countfiles[0]++;
            }
        }
        rawppi = stat.norm(rawppi, (double) countfiles[0]);
        TextFile.write(MoreArray.toString(ppilabels, "\t") + "\n" + MoreArray.toString(rawppi, "\t"),
                outf + "_ppi_mean_raw.txt");

        finalppi = new double[rawppi.length * step - 4];
        expandPPI(rawppi);
        outputPPI(finalppi);
        return countfiles;
    }*/


    /**
     * @param args
     */

    public static void main(String[] args) {
        if (args.length == 2 || args.length == 3) {
            InterPolateNulls rm = new InterPolateNulls(args);
        } else {
            System.out.println("syntax: java DataMining.InterPolateNulls\n" +
                    "<dir> <outfile prefix> <OPTIONAL debug y/n>"
            );
        }
    }

}