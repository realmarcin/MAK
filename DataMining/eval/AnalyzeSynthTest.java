package DataMining.eval;

import DataMining.*;
import mathy.stat;
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
 * Date: Jun 28, 2009
 * Time: 1:07:32 PM
 */
public class AnalyzeSynthTest extends Program {

    HashMap options;
    String[] valid_args = {"-dir", "-out", "-tail", "-block"};
    String dirpath, outpath;

    //SimpleMatrix expr_matrix;
    ArrayList results;
    ArrayList results_max;
    AnalyzeBlock abl;

    /*
    String trueblock2 = "10,11,12,13,14,15,16,17,18,19/23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42";
    */
    int dataxdim = 40, dataydim = 100;
    public final static String[] types = {"drift", "geneadd", "genedel", "expadd", "expdel", "0.5random"};

    String[] files, labels;
    String[] taildata;

    String globalType;

    int block = 1;

    ArrayList means, SDs;
    ArrayList means_max, SDs_max;


    /**
     * @param args
     */
    public AnalyzeSynthTest(String[] args) {
        options = MapArgOptions.maptoMap(args, valid_args);

        dirpath = (String) options.get("-dir");
        outpath = (String) options.get("-out");

        /*String tailpath = (String) options.get("-tail");
        if (tailpath != null) {
            taildata = TextFile.readtoArray(tailpath);
        }*/
        block = Integer.parseInt((String) options.get("-block"));
        //String datapath = (String) options.get("-dataset");
        //expr_matrix = new SimpleMatrix(datapath);

        /*class TopFilter implements FilenameFilter {
            public boolean accept(File dir, String name) {
                return (name.endsWith(".toplist.txt"));
            }
        }*/

        start();
    }

    /**
     * @param dir
     * @param out
     * @param b
     */
    public AnalyzeSynthTest(String dir, String out, int b) {
        dirpath = dir;
        outpath = out;
        block = b;
        start();
    }

    /**
     *
     */
    private void start() {
        results = new ArrayList();
        results_max = new ArrayList();

        File dir = new File(dirpath);
        if (dir.exists()) {
            //TopFilter tf = new TopFilter();
            String[] files = dir.list();//tf);

            System.out.println(files.length);
            if (files != null) {
                String s = dirpath.substring(dirpath.lastIndexOf("/") + 1, dirpath.length());
                if (s.length() > 0)
                    s = s + "_";
                run(files, s);
            } else {
                System.out.println("empty dirpath " + dirpath);
                System.exit(0);
            }
        } else {
            System.out.println("broken dirpath " + dirpath);
            System.exit(0);
        }
    }

    /**
     * @param files
     */
    private void run(String[] files, String outlabel) {

        ValueBlock refblock = getRefBlock();

        int c = 0;
        for (int i = 0; i < files.length; i++) {
            if (files[i].endsWith("_toplist.txt")) {
                c++;
            }
        }
        labels = new String[c];
        for (int i = 0; i < files.length; i++) {
            if (files[i].endsWith("_toplist.txt")) {
                System.out.println(files[i]);
                int type = getType(files[i]);
                String path = dirpath + sysRes.file_separator + files[i];
                if (type == -1)
                    System.out.println("run bad path/file " + path);
                String typelabel = types[type];
                globalType = typelabel;
                System.out.println("run " + typelabel);
                String s = "_" + typelabel;
                //int ind = files[i].indexOf(s);
                //int label_index = Integer.parseInt(files[i].substring(StringUtil.lastIndexBefore(files[i], "_", ind) + 1, ind));
                int label_index = MoreArray.getArrayInd(MINER_STATIC.CRIT_LABELS,
                        files[i].substring(files[i].indexOf("_") + 1, files[i].indexOf("__")));
                //System.out.println("ValueBlockList " + path);
                ValueBlockList vbl = null;
                try {
                    vbl = ValueBlockList.read(path, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //System.out.println("ValueBlockList " + vbl.size());
                if (!typelabel.equals("0.5random")) {
                    labels[results.size()] = MINER_STATIC.CRIT_LABELS[label_index] + "\t" + typelabel;
                } else {
                    labels[results.size()] = MINER_STATIC.CRIT_LABELS[label_index];
                }

                System.out.println("label " + labels[results.size()]);

                int geneindex = -1;
                if (!typelabel.equals("0.5random")) {
                    if (block == 1) {
                        if (typelabel.equals("drift")) {
                        }
                        if (typelabel.equals("geneadd")) {
                            geneindex = 16;
                        } else if (typelabel.equals("genedel")) {
                            geneindex = 15;
                        } else if (typelabel.equals("expadd")) {
                            geneindex = 31;
                        } else if (typelabel.equals("expdel")) {
                            geneindex = 30;
                        }
                    } else if (block == 2) {
                        if (typelabel.equals("drift")) {
                        }
                        if (typelabel.equals("geneadd")) {
                            geneindex = 1;
                        } else if (typelabel.equals("genedel")) {
                            geneindex = 42;
                        } else if (typelabel.equals("expadd")) {
                            geneindex = 1;
                        } else if (typelabel.equals("expdel")) {
                            geneindex = 19;
                        }
                    }
                }
                ValueBlock curblock = (ValueBlock) vbl.get(vbl.size() - 1);
                double[] add = BlockMethods.summarizeResults(refblock, curblock, typelabel, geneindex, vbl, null,
                        dataydim, dataxdim, null, false);
                results.add(add);

                int positionmax = AnalyzeBlock.findMaxGeneExperimentF1(refblock, vbl);
                double[] addmax = MoreArray.initArray(add.length, 0.0);
                if (positionmax != -1) {
                    ValueBlock curblockmax = (ValueBlock) vbl.get(positionmax);
                    addmax = BlockMethods.summarizeResults(refblock, curblockmax, typelabel, geneindex, vbl, null,
                            dataydim, dataxdim, null, false);
                }
                results_max.add(addmax);
            }
        }

        if (globalType.equals("0.5random")) {
            group();
            writeMeanSD(outlabel);
        } else
            write("analyze_synth_basic.txt");
    }

    /**
     * @return
     */
    private ValueBlock getRefBlock() {
        ValueBlock refblock = null;
        if (block == 1) {
            refblock = EvaluateBasic.true1;
        } else if (block == 2) {
            refblock = EvaluateBasic.true2;
        } else if (block == 3) {
            refblock = EvaluateBasic.true1c;
        } else if (block == 4) {
            refblock = EvaluateBasic.true2c;
        } else if (block == 5) {
            refblock = EvaluateBasic.true1e;
        } else if (block == 6) {
            refblock = EvaluateBasic.true2e;
        } else if (block == 7) {
            refblock = EvaluateBasic.true2ec;
        } else if (block == 8) {
            refblock = EvaluateBasic.true2ec;
        }
        return refblock;
    }

    /**
     *
     */
    private void group() {
        System.out.println("group");
        means = new ArrayList();
        SDs = new ArrayList();
        for (int i = 0; i < MINER_STATIC.CRIT_LABELS.length; i++) {
            String cur = MINER_STATIC.CRIT_LABELS[i];
            ArrayList tmp = new ArrayList();
            System.out.println("group " + cur);
            for (int j = 0; j < labels.length; j++) {
                if (labels[j] != null && labels[j].equals(cur)) {
                    tmp.add((double[]) results.get(j));
                }
            }
            System.out.println("group " + tmp.size());
            double[] mean = stat.arraySampAvg(tmp);
            double[] sd = stat.arraySampSD(tmp, mean);
            means.add(mean);
            SDs.add(sd);
        }

        means_max = new ArrayList();
        SDs_max = new ArrayList();
        for (int i = 0; i < MINER_STATIC.CRIT_LABELS.length; i++) {
            String cur = MINER_STATIC.CRIT_LABELS[i];
            ArrayList tmp = new ArrayList();
            System.out.println("group " + cur);
            for (int j = 0; j < labels.length; j++) {
                if (labels[j] != null && labels[j].equals(cur)) {
                    tmp.add((double[]) results_max.get(j));
                }
            }
            System.out.println("group " + tmp.size());
            double[] mean = stat.arraySampAvg(tmp);
            double[] sd = stat.arraySampSD(tmp, mean);
            means_max.add(mean);
            SDs_max.add(sd);
        }

    }

    /**
     * @param label
     * @return
     */
    public double searchTailData(String label) {
        System.out.println("searchTailData " + label);
        double ret = Double.NaN;
        for (int i = 0; i < taildata.length; i++) {
            if (taildata[i].indexOf("==> ") == 0) {
                if (taildata[i].indexOf(label) != -1) {
                    int start = taildata[i + 10].indexOf("time  ") + "time  ".length();
                    int end = taildata[i + 10].indexOf(" s", start + 1);
                    try {
                        String s = taildata[i + 10].substring(start, end);
                        System.out.println("searchTailData '" + s + "'");
                        ret = Double.parseDouble(s);
                        //System.out.println("searchTailData " + ret);
                    } catch (Exception e) {
                        //e.printStackTrace();
                        System.out.println("ERROR no type " + taildata[i + 10]);
                    }
                }
            }
        }
        return ret;
    }

    /**
     * @param s
     * @return
     */
    public final static int getType(String s) {
        for (int i = 0; i < types.length; i++) {
            if (s.indexOf(types[i]) != -1)
                return i;
        }
        return -1;
    }

    /**
     *
     */
    private void write(String outname) {
        /*String HEADER_EVAL = "criterion\ttest\tfirst ref. count\tlast ref. count\tregulon_size\t" +
                "first ref. ratio\tlast ref. ratio\tnum genes first\tnum genes last\tpercent num genes last\t" +
                "num exps first\tnum exps last\tpercent exp last\tlast.percentOrigGenes\t" +
                "last.percentOrigExp\tfirst.precrit\tlast.precrit\tnumber moves\tpassed\tpassed_final\t" +
                "F1\tprecision\trecall\truntime";*/
        try {
            String outf = outpath + sysRes.file_separator + outname;
            System.out.println("generating " + outf);
            PrintWriter pw = new PrintWriter(new FileWriter(outf), true);

            pw.println(MINER_STATIC.HEADER_EVAL);
            for (int i = 0; i < results.size(); i++) {
                pw.println(labels[i] + "\t" + MoreArray.toString((double[]) results.get(i), "\t"));
            }
            pw.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     *
     */
    private void writeMeanSD(String file_label) {
        try {
            String outm = outpath + sysRes.file_separator + file_label + "analyze_mean.txt";
            System.out.println("generating " + outm);
            PrintWriter pw = new PrintWriter(new FileWriter(outm), true);
            pw.println(MINER_STATIC.HEADER_EVAL + "\t" + MINER_STATIC.HEADER_EVAL);
            System.out.println(means.size() + "\t" + MINER_STATIC.CRIT_LABELS.length + "\t" + means.size() + "\t" + SDs.size());
            for (int i = 0; i < means.size(); i++) {
                pw.println(MINER_STATIC.CRIT_LABELS[i] + "\t" + MoreArray.toString((double[]) means.get(i), "\t")
                        + "\t" + MoreArray.toString((double[]) SDs.get(i), "\t"));
            }
            pw.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        try {
            String outm = outpath + sysRes.file_separator + file_label + "analyze_mean_max.txt";
            System.out.println("generating " + outm);
            PrintWriter pw = new PrintWriter(new FileWriter(outm), true);
            pw.println(MINER_STATIC.HEADER_EVAL + "\t" + MINER_STATIC.HEADER_EVAL);
            for (int i = 0; i < means_max.size(); i++) {
                pw.println(MINER_STATIC.CRIT_LABELS[i] + "\t" + MoreArray.toString((double[]) means_max.get(i), "\t")
                        + "\t" + MoreArray.toString((double[]) SDs_max.get(i), "\t"));
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
        if (args.length == 4 || args.length == 6 || args.length == 8) {
            AnalyzeSynthTest arg = new AnalyzeSynthTest(args);
        } else {
            System.out.println("syntax: java DataMining.eval.AnalyzeSynthTest\n" +
                    "<-dir dir of toplists>\n" +
                    "<-out outpath>\n" +
                    "<-tail tail output to get run time>\n" +
                    "<-block 1 or 2>"
            );
        }
    }
}
