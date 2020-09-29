package DataMining;

import mathy.Matrix;
import mathy.SimpleMatrix;
import util.MapArgOptions;
import util.MoreArray;
import util.Program;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Similar to AnalyzeRegulonResults but uses a series of dirs each with a mix of regulons,
 * vs. a dir of dirs, one for each regulon
 * <p/>
 * User: marcin
 * Date: Nov 3, 2008
 * Time: 10:26:43 AM
 */
public class AnalyzeRegulonShaving extends Program {

    HashMap options;
    String[] valid_args = {"-regulons", "-dir", "-dataset"};


    String factor = "none";
    SimpleMatrix expr_matrix;

    int block_size = 15;

    //ArrayList[] regulon_genes_int;
    ArrayList[] results;
    ArrayList[] results_unique;

    AnalyzeBlock abl;


    /**
     * @param args
     */
    public AnalyzeRegulonShaving(String[] args) {

        options = MapArgOptions.maptoMap(args, valid_args);

        String dirpath = (String) options.get("-dir");
        String regulons = (String) options.get("-regulons");
        String datapath = (String) options.get("-dataset");

        expr_matrix = new SimpleMatrix(datapath);


        abl = new AnalyzeBlock(regulons, expr_matrix.ylabels, "MacIsaac");

        //String[] list = TextFile.readtoArray(regulon_list);

        results = MoreArray.initArrayListArray(abl.yr.factors.length);
        results_unique = MoreArray.initArrayListArray(abl.yr.factors.length);

        File dir = new File(dirpath);
        String[] files = dir.list();
        if (files != null)
            run(dirpath, files);
        else {
            System.out.println("AnalyzeRegulonShaving broken dirpath " + dirpath);
            System.exit(1);
        }

    }

    /**
     * @param dirpath
     * @param files
     */
    private void run(String dirpath, String[] files) {

        for (int i = 0; i < files.length; i++) {
            String nextdirpath = dirpath + sysRes.file_separator + files[i];// + sysRes.file_separator + files[i];
            File test = new File(nextdirpath);
            if (test.isDirectory()) {
                System.out.println("dir " + files[i]);
                String[] cur_list = test.list();
                for (int j = 0; j < cur_list.length; j++) {
                    String finaldirpath = nextdirpath + sysRes.file_separator;// + cur_list[j];
                    //File test2 = new File(finaldirpath);
                    //System.out.println("dir2 " + finaldirpath);
                    //String[] cur_list2 = test2.list();
                    //for (int k = 0; k < cur_list2.length; k++) {
                    if (cur_list[j].indexOf("toplist") != -1) {
                        String readpath = finaldirpath + sysRes.file_separator + cur_list[j];
                        System.out.println("ValueBlockList " + readpath);
                        ValueBlockList vbl = null;
                        try {
                            vbl = ValueBlockList.read(readpath, false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        System.out.println("ValueBlockList " + vbl.size());

                        //Parameters prm = getParameters(finaldirpath, cur_list[j]);

                        //if (prm != null) {
                        //System.out.println("ValueBlockList prm.OUTPREFIX " + prm.OUTPREFIX);
                        String fact = cur_list[j].substring(0, cur_list[j].indexOf("_"));
                        int cur_fact = MoreArray.getArrayInd(abl.yr.factors, fact);
                        double[] add = abl.analyzeBlock(cur_fact, vbl);
                        results[cur_fact].add(add);
                        double[] addu = abl.analyzeBlockUnique(cur_fact, vbl);
                        results_unique[cur_fact].add(addu);
                        /*} else {
                            System.out.println("prm NOT FOUND " + finaldirpath + sysRes.file_separator
                                    + "\t" + "_parameters.txt");
                        }*/
                    }
                    //}
                }

            }
        }


        write(dirpath, results, "analyze_regulonshaving_mean.txt", "analyze_regulonshaving_sd.txt");
        write(dirpath, results_unique, "analyze_regulonshaving_unique_mean.txt", "analyze_regulonshaving_unique_sd.txt");
    }

    /**
     * @param dirpath
     */
    private void write(String dirpath, ArrayList[] ar, String outname_mean, String outname_sd) {
        String header = "regulon\tsamples\tfirst regulon count\tlast regulon count\tregulon_size\t" +
                "first regulon ratio\tlast regulon ratio\tnum genes first\tnum genes last\tpercent num genes last\t" +
                "num exps first\tnum exps last\tpercent exp last\tlast.percentOrigGenes\t" +
                "last.percentOrigExp\tfirst.precrit\tlast.precrit\tnumber moves";
        try {
            String outf = dirpath + sysRes.file_separator + outname_mean;
            System.out.println("generating " + outf);
            PrintWriter pw = new PrintWriter(new FileWriter(outf), true);
            String outf2 = dirpath + sysRes.file_separator + outname_sd;
            System.out.println("generating " + outf2);
            PrintWriter pw2 = new PrintWriter(new FileWriter(outf2), true);

            pw.println(header);
            pw2.println(header);
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
                        pw.println(abl.yr.factors[i] + "\t" + size + "\t" + MoreArray.toString(summary, "\t"));
                        pw2.println(abl.yr.factors[i] + "\t" + size + "\t" + MoreArray.toString(summarySD, "\t"));
                    }
                }
            }
            pw.close();
            pw2.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * @param finaldirpath
     * @param parse
     * @return
     */
    private Parameters getParameters(String finaldirpath, String parse) {
        Parameters prm = null;
        int firstind = parse.indexOf("_");
        int secondind = parse.indexOf("_", firstind + 1);
        String prefix1 = parse.substring(0, Math.max(secondind, firstind));
        String prefix2 = parse.substring(0, firstind);
        String prefix3 = null;
        if (secondind != -1)
            prefix3 = parse.substring(firstind + 1, secondind);

        String path1 = finaldirpath + sysRes.file_separator + prefix1 + "_parameters.txt";
        String path2 = finaldirpath + sysRes.file_separator + prefix2 + "_parameters.txt";
        String path3 = finaldirpath + sysRes.file_separator + prefix3 + "_parameters.txt";
        String path4 = finaldirpath + sysRes.file_separator + prefix1 + "_sesi_parameters.txt";
        System.out.println("ValueBlockList path1 " + path1);
        System.out.println("ValueBlockList path2 " + path2);
        System.out.println("ValueBlockList path3 " + path3);
        System.out.println("ValueBlockList path4 " + path4);
        File testf1 = new File(path1);
        File testf2 = new File(path2);
        File testf3 = new File(path3);
        File testf4 = new File(path4);
        if (testf1.exists()) {
            try {
                prm = new Parameters(path1);
                System.out.println("ValueBlockList prm.OUTPREFIX path1 " + prm.OUTPREFIX);
            } catch (Exception e) {

            }
        } else if (testf4.exists()) {
            if (testf4.exists()) {
                System.out.println("ValueBlockList trying prm path4 " + path4);
                try {
                    prm = new Parameters(path4);
                    System.out.println("ValueBlockList prm.OUTPREFIX path4 " + prm.OUTPREFIX);
                } catch (FileNotFoundException e1) {
                }
            }
        } else if (testf2.exists() && prefix1 != prefix2) {
            if (testf2.exists()) {
                System.out.println("ValueBlockList trying prm path2 " + path2);
                try {
                    prm = new Parameters(path2);
                    System.out.println("ValueBlockList prm.OUTPREFIX path2 " + prm.OUTPREFIX);
                } catch (FileNotFoundException e1) {
                }
            }
        } else if (testf3.exists()) {
            if (testf3.exists()) {
                System.out.println("ValueBlockList trying prm path3 " + path3);
                try {
                    prm = new Parameters(path3);
                    System.out.println("ValueBlockList prm.OUTPREFIX path3 " + prm.OUTPREFIX);
                } catch (FileNotFoundException e1) {
                }
            }
        }
        return prm;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 6) {
            AnalyzeRegulonShaving arg = new AnalyzeRegulonShaving(args);
        } else {
            System.out.println("syntax: java DataMining.AnalyzeRegulonShaving\n" +
                    "<-dir dir of dirs of toplists or BicAT output'>\n" +
                    "<-regulons 'regulon file'>\n" +
                    "<-dataset labeled expr. data set>"
            );
        }
    }
}

