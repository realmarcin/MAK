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
 * Date: Jul 25, 2009
 * Time: 10:22:00 PM
 */
public class AnalyzeExternal extends Program {

    boolean debug = false;
    HashMap options;
    String[] valid_args = {"-dir", "-mode"};//, "-block"};
    String mode;

    int dataxdim = 40, dataydim = 100;
    ArrayList results;
    ArrayList results_max;

    public String[][] use_crit = {{"external"}, {"none"},
           /* {"MSER_PPI", "MSER", "MSER_GEERE_PPI", "MSER_GEERE", "MSER_LARSRE_PPI"},
            {"MSER_PPI"},
            {"MADR_PPI"},
            {"Kendall_PPI"},
            {"MSER"},
            {"MADR"},
            {"Kendall"},
            {"LARSRE_PPI"},
            {"LARSRE"},
            {"GEERE_PPI"},
            {"GEERE"},
            {"MSER_LARSRE_PPI"},
            {"MADR_LARSRE_PPI"},
            {"Kendall_LARSRE_PPI"},
            {"MSER_LARSRE"},
            {"MADR_LARSRE"},
            {"Kendall_LARSRE"},
            {"MSER_GEERE_PPI"},
            {"MADR_GEERE_PPI"},
            {"Kendall_GEERE_PPI"},
            {"MSER_GEERE"},
            {"MADR_GEERE"},
            {"Kendall_GEERE"},
            {"MSE_INT"},
            {"MSE"},
            {"MSE_LARSRE_PPI"},
            {"MSE_LARSRE"},
            {"MSE_GEERE_PPI"},
            {"MSE_GEERE"},
            {"MSEC_INT"},
            {"MSEC"},
            {"MSEC_LARSRE_PPI"},
            {"MSEC_LARSRE"},
            {"MSEC_GEERE_PPI"},
            {"MSEC_GEERE"},
            {"MSERnonull"},
            {"MSEnonull"},
            {"MSECnonull"}*/
    };

    ArrayList labels;
    String[] taildata;

    String globalType;

    ArrayList means, SDs;
    ArrayList means_max, SDs_max;

    int block = 1;
    ValueBlock refblock;


    /**
     * @param args
     */
    public AnalyzeExternal(String[] args) {
        options = MapArgOptions.maptoMap(args, valid_args);

        String dirpath = (String) options.get("-dir");
        mode = (String) options.get("-mode");

        File dir = new File(dirpath);

        File t1 = new File(dirpath + "/true1");
        t1.mkdir();
        t1 = new File(dirpath + "/true1max");
        t1.mkdir();
        t1 = new File(dirpath + "/true2");
        t1.mkdir();
        t1 = new File(dirpath + "/true2max");
        t1.mkdir();
        if (mode.equals("const") || mode.equals("incr") || mode.equals("constnono") || mode.equals("incrnono")) {
            t1 = new File(dirpath + "/true3");
            t1.mkdir();
            t1 = new File(dirpath + "/true3max");
            t1.mkdir();
            t1 = new File(dirpath + "/true4");
            t1.mkdir();
            t1 = new File(dirpath + "/true4max");
            t1.mkdir();
        }


        String[][] use_crit_dynamic = new String[use_crit.length][];
        use_crit_dynamic[0] = use_crit[0];
        use_crit_dynamic[1] = use_crit[1];


        if (dir.exists()) {
            String[] dirlist = dir.list();
            //labels = new String[dirlist.length];
            if (debug)
                System.out.println(dirlist.length);
            if (dirlist != null) {
                if (mode.equals("basic")) {
                    refblock = EvaluateBasic.true1;
                    run(dirpath, dirlist);
                    refblock = EvaluateBasic.true2;
                    run(dirpath, dirlist);
                } else if (mode.equals("const")) {
                    refblock = EvaluateConst.true1;
                    run(dirpath, dirlist);
                    refblock = EvaluateConst.true2;
                    run(dirpath, dirlist);
                    refblock = EvaluateConst.true3;
                    run(dirpath, dirlist);
                    refblock = EvaluateConst.true4;
                    run(dirpath, dirlist);
                } else if (mode.equals("incr")) {
                    refblock = EvaluateIncr.true1;
                    run(dirpath, dirlist);
                    refblock = EvaluateIncr.true2;
                    run(dirpath, dirlist);
                    refblock = EvaluateIncr.true3;
                    run(dirpath, dirlist);
                    refblock = EvaluateIncr.true4;
                    run(dirpath, dirlist);
                } else if (mode.equals("constnono")) {
                    refblock = EvaluateConstNono.true1;
                    run(dirpath, dirlist);
                    refblock = EvaluateConstNono.true2;
                    run(dirpath, dirlist);
                    refblock = EvaluateConstNono.true3;
                    run(dirpath, dirlist);
                    refblock = EvaluateConstNono.true4;
                    run(dirpath, dirlist);
                } else if (mode.equals("incrnono")) {
                    refblock = EvaluateIncrNono.true1;
                    run(dirpath, dirlist);
                    refblock = EvaluateIncrNono.true2;
                    run(dirpath, dirlist);
                    refblock = EvaluateIncrNono.true3;
                    run(dirpath, dirlist);
                    refblock = EvaluateIncrNono.true4;
                    run(dirpath, dirlist);
                }
            } else {
                System.out.println("AnalyzeExternal empty dirpath " + dirpath);
                System.exit(0);
            }
        } else {
            System.out.println("AnalyzeExternal broken dirpath " + dirpath);
            System.exit(0);
        }


    }

    /**
     * @param dirpath
     * @param dirs
     */
    private void run(String dirpath, String[] dirs) {
        int count = 0;
        //for (int n = 0; n < use_crit.length; n++) {
        for (int n = 0; n < MINER_STATIC.CRIT_LABELS.length; n++) {
            labels = new ArrayList();
            results = new ArrayList();
            results_max = new ArrayList();
            String[] cur_use_crit = use_crit[n];

            for (int a = 0; a < dirs.length; a++) {
                String curdirstr = dirpath + "/" + dirs[a];
                File curdir = new File(curdirstr);
                String[] files = curdir.list();
                if (debug) {
                    System.out.println(n + "\t" + a + "\t" + MoreArray.toString(cur_use_crit, "_"));
                    System.out.println(curdirstr);
                    System.out.println(files.length);
                }
                for (int i = 0; i < files.length; i++) {
                    if (files[i].endsWith("_toplist.txt")) {
                        if (count % 1000 == 0) {
                            System.out.print(".");
                        }
                        count++;
                        if (debug)
                            System.out.println(files[i]);
                        int type = Evaluate.getExtMethod(files[i]);
                        String path = curdirstr + sysRes.file_separator + files[i];//dirpath + sysRes.file_separator +
                        if (type == -1)
                            System.out.println("run bad path/file " + path);
                        String typelabel = Evaluate.external_methods[type];
                        globalType = typelabel;
                        if (debug)
                            System.out.println("run " + typelabel);
                        int start = files[i].indexOf("__") + 2;
                        int stop = files[i].indexOf("__", start);
                        String str1 = files[i].substring(start, stop);

                        if (debug)
                            System.out.println("run  str1 " + str1);
                        int label_index = MoreArray.getArrayInd(MINER_STATIC.CRIT_LABELS, str1);
                        int ind = -1;
                        try {
                            ind = MoreArray.getArrayInd(cur_use_crit, MINER_STATIC.CRIT_LABELS[label_index]);
                        } catch (Exception e) {
                            System.out.println("use_crit problem " + label_index);
                            e.printStackTrace();
                        }
                        if (cur_use_crit[0].equals("none") || cur_use_crit[0].equals("external") ||
                                ind != -1) {
                            //System.out.println("use_crit " + MINER_STATIC.CRIT_LABELS[label_index]);
                            //System.out.println("ValueBlockList " + path);
                            ValueBlockList vbl = null;
                            try {
                                vbl = ValueBlockList.read(path, false);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            //System.out.println("ValueBlockList " + vbl.size());

                            labels.add(typelabel);
                            //labels[results.size()] = typelabel;//MINER_STATIC.CRIT_LABELS[label_index];
                            //System.out.println("label " + labels[results.size()]);
                            int geneindex = -1;
                            int position = vbl.size() - 1;

                            if (cur_use_crit[0].equals("external"))
                                position = 0;

                            ValueBlock curblock = (ValueBlock) vbl.get(position);

                            double[] add = BlockMethods.summarizeResults(refblock, curblock, typelabel, geneindex,
                                    vbl, files[i], dataydim, dataxdim, null, false);
                            results.add(add);

                            int positionmax = AnalyzeBlock.findMaxGeneExperimentF1(refblock, vbl);
                            double[] addmax = MoreArray.initArray(add.length, 0.0);
                            if (positionmax != -1) {
                                ValueBlock curblockmax = (ValueBlock) vbl.get(positionmax);
                                addmax = BlockMethods.summarizeResults(refblock, curblockmax, typelabel, geneindex,
                                        vbl, files[i], dataydim, dataxdim, null, false);
                            }
                            results_max.add(addmax);
                        }
                    }
                }
            }
            group();
            String s = null;
            if (mode.equals("basic")) {
                s = refblock == EvaluateBasic.true1 ? "true1" : "true2";
            } else if (mode.equals("const")) {
                if (refblock == EvaluateConst.true1) {
                    s = "true1";
                } else if (refblock == EvaluateConst.true2) {
                    s = "true2";
                } else if (refblock == EvaluateConst.true3) {
                    s = "true3";
                } else if (refblock == EvaluateConst.true4) {
                    s = "true4";
                }
            } else if (mode.equals("incr")) {
                if (refblock == EvaluateIncr.true1) {
                    s = "true1";
                } else if (refblock == EvaluateIncr.true2) {
                    s = "true2";
                } else if (refblock == EvaluateIncr.true3) {
                    s = "true3";
                } else if (refblock == EvaluateIncr.true4) {
                    s = "true4";
                }
            }
            writeMeanSD(dirpath, MoreArray.toString(cur_use_crit, "_") + "__" + s, s);
        }
    }

    /**
     *
     */
    private void group() {
        String[] labelStr = MoreArray.convtoString(labels);
        means = new ArrayList();
        SDs = new ArrayList();
        for (int i = 0; i < Evaluate.external_methods.length; i++) {
            String cur = Evaluate.external_methods[i];
            ArrayList tmp = new ArrayList();
            if (debug)
                System.out.println(cur);
            for (int j = 0; j < labelStr.length; j++) {
                if (labelStr[j] != null && labelStr[j].equals(cur)) {
                    tmp.add((double[]) results.get(j));
                }
            }
            if (debug)
                System.out.println("group " + tmp.size());

            if (tmp.size() > 0) {
                double[] mean = stat.arraySampAvg(tmp);
                double[] sd = stat.arraySampSD(tmp, mean);
                means.add(mean);
                SDs.add(sd);
            } else {
                double[] add = null;
                means.add(add);
                SDs.add(add);
            }
        }

        means_max = new ArrayList();
        SDs_max = new ArrayList();
        for (int i = 0; i < Evaluate.external_methods.length; i++) {
            String cur = Evaluate.external_methods[i];
            ArrayList tmp = new ArrayList();
            if (debug)
                System.out.println(cur);
            for (int j = 0; j < labelStr.length; j++) {
                if (labelStr[j] != null && labelStr[j].equals(cur)) {
                    tmp.add((double[]) results_max.get(j));
                }
            }
            if (debug)
                System.out.println("group max " + tmp.size());
            if (tmp.size() > 0) {
                double[] mean = stat.arraySampAvg(tmp);
                double[] sd = stat.arraySampSD(tmp, mean);
                means_max.add(mean);
                SDs_max.add(sd);
            } else {
                double[] add = null;
                means_max.add(add);
                SDs_max.add(add);
            }
        }
    }

    /**
     * @param dirpath
     */
    private void writeMeanSD(String dirpath, String label, String pathlabel) {
        ArrayList tmp = MoreArray.convtoArrayList(MINER_STATIC.HEADER_EVAL.split("\t"));
        tmp.remove(0);
        String[] tmpstr = MoreArray.ArrayListtoString(tmp);
        String tmptmpstr = MoreArray.toString(tmpstr, "\t");
        try {
            String outm = dirpath + sysRes.file_separator + pathlabel + sysRes.file_separator + label + "_analyze.txt";
            System.out.println("generating " + outm);
            PrintWriter pw = new PrintWriter(new FileWriter(outm), true);
            pw.println(MINER_STATIC.HEADER_EVAL + "\t" + tmptmpstr);
            for (int i = 0; i < means.size(); i++) {
                double[] doubles = (double[]) means.get(i);
                double[] doubles2 = (double[]) SDs.get(i);
                if (doubles != null)
                    pw.println(Evaluate.external_methods[i] + "\t" + MoreArray.toString(doubles, "\t") + "\t" + MoreArray.toString(doubles2, "\t"));
                else
                    pw.println(Evaluate.external_methods[i]);
            }
            pw.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        try {
            String outm = dirpath + sysRes.file_separator + pathlabel + "max" + sysRes.file_separator + label + "_analyze_max.txt";
            System.out.println("generating " + outm);
            PrintWriter pw = new PrintWriter(new FileWriter(outm), true);
            pw.println(MINER_STATIC.HEADER_EVAL + "\t" + tmptmpstr);
            for (int i = 0; i < means_max.size(); i++) {
                double[] doubles = (double[]) means_max.get(i);
                double[] doubles2 = (double[]) SDs_max.get(i);
                if (doubles != null)
                    pw.println(Evaluate.external_methods[i] + "\t" +
                            MoreArray.toString(doubles, "\t") + "\t" + MoreArray.toString(doubles2, "\t"));
                else
                    pw.println(Evaluate.external_methods[i]);
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
            AnalyzeExternal arg = new AnalyzeExternal(args);
        } else {
            System.out.println("syntax: java DataMining.eval.AnalyzeExternal\n" +
                    "<-dir dir of toplists>\n" +
                    "<-mode basic,const,incr>"//\n" +
                    //"<-block 1 or 2>"
            );
        }
    }
}
