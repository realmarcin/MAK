package DataMining.eval;

import DataMining.BlockMethods;
import DataMining.ValueBlock;
import DataMining.ValueBlockList;
import DataMining.ValueBlockListMethods;
import mathy.stat;
import util.DirFilter;
import util.MoreArray;
import util.TabFile;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: Mar 4, 2011
 * Time: 9:55:54 PM
 */
public class CompareTrajSet {

    boolean debug = true;

    Evaluate eval;

    int dataydim = 200;
    int dataxdim = 80;

    int totaldim = dataydim * dataxdim;

    String externalpath = null;

    String[] xlabels = {
            "start", "crit", "isInter", "isReg", "notPPInotReg", "isNull", "isBS",
            "true1F1", "true2F1", "true3F1", "true4F1",
            "true1sens", "true2sens", "true3sens", "true4sens",
            "true1spec", "true2spec", "true3spec", "true4spec",
            "firsttrue1F1", "firsttrue2F1", "firsttrue3F1", "firsttrue4F1",
            "firsttrue1sens", "firsttrue2sens", "firsttrue3sens", "firsttrue4sens",
            "firsttrue1spec", "firsttrue2spec", "firsttrue3spec", "firsttrue4spec", "startcrit",
            "true1prec", "true2prec", "true3prec", "true4prec",
            "true1recall", "true2recall", "true3recall", "true4recall",
            "firsttrue1prec", "firsttrue2prec", "firsttrue3prec", "firsttrue4prec",
            "firsttrue1recall", "firsttrue2recall", "firsttrue3recall", "firsttrue4recall"
    };
    String[] starts, crits;

    ArrayList avg_data, sd_data, labels;

    boolean lastadd = false;

    boolean isOverlapping = false;
    boolean haveMerged = false;


    ValueBlockList merged;


    /**
     * @param args
     */
    public CompareTrajSet(String[] args) {

        String inpath = args[0];
        File masterdir = new File(inpath);
        String[] list = masterdir.list();
        DirFilter df = new DirFilter("toplist.txt");

        //boolean dirmode = args[2].equalsIgnoreCase("y") ? true : false;
        isOverlapping = args[2].equalsIgnoreCase("over") ? true : false;

        avg_data = new ArrayList();
        sd_data = new ArrayList();
        labels = new ArrayList();
        String outlabel = args[1];
        File test = new File(inpath);
        if (!test.isDirectory()) {
            System.out.println("path is not a directory " + inpath);
            System.exit(0);
        }

        //evaluate external criteria
        if (args.length >= 4) {
            externalpath = args[3];
            doExternal(outlabel);
        }

        if (args.length == 5) {
            try {
                merged = ValueBlockListMethods.readAny(args[4], false);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (merged != null) {
                haveMerged = true;
            }
        }

        //dir of dirs
        for (int l = 0; l < list.length; l++) {
            String s = inpath + "/" + list[l];
            System.out.println("do path " + s);
            File curdir = new File(s);
            String[] oneL = curdir.list(df);
            int oneL_len = oneL.length;
            String[] ylabels = new String[oneL_len];

            System.out.println("oneL " + oneL.length);

            populateCritandStarts(oneL);

            doDir(inpath + "/" + list[l], oneL, oneL_len, ylabels);

            if (haveMerged) {
                addMerged();
            }
        }

        out(outlabel, avg_data, sd_data, labels);
        System.out.println("\ndone");
    }

    /**
     *
     */
    private void addMerged() {

        for (int l = 0; l < merged.size(); l++) {

            ValueBlock cur = (ValueBlock) merged.get(l);

            /*double[][] storedouba = MoreArray.ArrayListto2DDouble(curavg);
            avg_data.add(storedouba);
            double[][] storedoubs = MoreArray.ArrayListto2DDouble(cursd);
            sd_data.add(storedoubs);
            labels.add(ylabels);*/

        }

    }

    /**
     * @param outlabel
     */
    private void doExternal(String outlabel) {
        ValueBlockList refs = new ValueBlockList();

        if (!isOverlapping) {
            refs.add(EvaluateIncrNonoRand.true1);
            refs.add(EvaluateIncrNonoRand.true2);
            refs.add(EvaluateIncrNonoRand.true3);
            refs.add(EvaluateIncrNonoRand.true4);
        } else {
            refs.add(EvaluateIncrOverRand.true1);
            refs.add(EvaluateIncrOverRand.true2);
            refs.add(EvaluateIncrOverRand.true3);
            refs.add(EvaluateIncrOverRand.true4);
        }
        eval = new Evaluate(externalpath, refs, true, dataydim, dataxdim);

        File extdir = new File(externalpath);
        String[] extlist = extdir.list();

        ArrayList extsummarydata = new ArrayList();
        String[] extlabels = new String[extlist.length];
        for (int i = 0; i < extlist.length; i++) {
            extlabels[i] = extlist[i].substring(0, extlist[i].indexOf("."));

            String f = externalpath + "/" + extlist[i];
            System.out.println("reading " + f);
            ValueBlockList extvbl = null;

            try {
                extvbl = ValueBlockListMethods.readBICTranslatetoInt(f);
            } catch (Exception e) {
                try {
                    extvbl = ValueBlockListMethods.readSimple(f);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

            double[] F1max = new double[4];
            double[] sensmax = new double[4];
            double[] specmax = new double[4];
            double[] precmax = new double[4];
            double[] recmax = new double[4];
            System.out.println(" extvbl.size() " + extvbl.size());
            for (int j = 0; j < extvbl.size(); j++) {
                ValueBlock cur = (ValueBlock) extvbl.get(j);
                double f1 = Double.NaN;
                double f2 = Double.NaN;
                double f3 = Double.NaN;
                double f4 = Double.NaN;
                if (!isOverlapping) {
                    f1 = BlockMethods.computeBlockF1(EvaluateIncrNonoRand.true1, cur, debug);
                    f2 = BlockMethods.computeBlockF1(EvaluateIncrNonoRand.true2, cur, debug);
                    f3 = BlockMethods.computeBlockF1(EvaluateIncrNonoRand.true3, cur, debug);
                    f4 = BlockMethods.computeBlockF1(EvaluateIncrNonoRand.true4, cur, debug);
                } else {
                    f1 = BlockMethods.computeBlockF1(EvaluateIncrOverRand.true1, cur, debug);
                    f2 = BlockMethods.computeBlockF1(EvaluateIncrOverRand.true2, cur, debug);
                    f3 = BlockMethods.computeBlockF1(EvaluateIncrOverRand.true3, cur, debug);
                    f4 = BlockMethods.computeBlockF1(EvaluateIncrOverRand.true4, cur, debug);
                    //f4 =0;
                }

                if (f1 > F1max[0])
                    F1max[0] = f1;
                if (f2 > F1max[1])
                    F1max[1] = f2;
                if (f3 > F1max[2])
                    F1max[2] = f3;
                if (f4 > F1max[3])
                    F1max[3] = f4;

                double[] d1f = null;
                double[] d2f = null;
                double[] d3f = null;
                double[] d4f = null;
                if (!isOverlapping) {
                    d1f = BlockMethods.computeBlockSensitivitySpecificityGenesAndExps(EvaluateIncrNonoRand.true1, cur, totaldim);
                    d2f = BlockMethods.computeBlockSensitivitySpecificityGenesAndExps(EvaluateIncrNonoRand.true2, cur, totaldim);
                    d3f = BlockMethods.computeBlockSensitivitySpecificityGenesAndExps(EvaluateIncrNonoRand.true3, cur, totaldim);
                    d4f = BlockMethods.computeBlockSensitivitySpecificityGenesAndExps(EvaluateIncrNonoRand.true4, cur, totaldim);
                } else {
                    d1f = BlockMethods.computeBlockSensitivitySpecificityGenesAndExps(EvaluateIncrOverRand.true1, cur, totaldim);
                    d2f = BlockMethods.computeBlockSensitivitySpecificityGenesAndExps(EvaluateIncrOverRand.true2, cur, totaldim);
                    d3f = BlockMethods.computeBlockSensitivitySpecificityGenesAndExps(EvaluateIncrOverRand.true3, cur, totaldim);
                    d4f = BlockMethods.computeBlockSensitivitySpecificityGenesAndExps(EvaluateIncrOverRand.true4, cur, totaldim);
                    //d4f = new double[2];
                    //d4f[0] = 0;
                    //d4f[1] = 0;
                }

                double v1 = 0;
                if (sensmax[0] + specmax[0] != 0)
                    v1 = stat.geometricMeanN2(sensmax[0], specmax[0]);
                double v2 = 0;
                if (sensmax[1] + specmax[1] != 0)
                    v2 = stat.geometricMeanN2(sensmax[1], specmax[1]);
                double v3 = 0;
                if (sensmax[2] + specmax[2] != 0)
                    v3 = stat.geometricMeanN2(sensmax[2], specmax[2]);
                double v4 = 0;
                if (sensmax[3] + specmax[3] != 0)
                    v4 = stat.geometricMeanN2(sensmax[3], specmax[3]);

                if (stat.geometricMeanN2(d1f[0], d1f[1]) > v1) {
                    sensmax[0] = d1f[0];
                    specmax[0] = d1f[1];
                }
                if (stat.geometricMeanN2(d2f[0], d2f[1]) > v2) {
                    sensmax[1] = d2f[0];
                    specmax[1] = d2f[1];
                }
                if (stat.geometricMeanN2(d3f[0], d3f[1]) > v3) {
                    sensmax[2] = d3f[0];
                    specmax[2] = d3f[1];
                }
                if (stat.geometricMeanN2(d4f[0], d4f[1]) > v4) {
                    sensmax[3] = d4f[0];
                    specmax[3] = d4f[1];
                }

                double[] p1r = null;
                double[] p2r = null;
                double[] p3r = null;
                double[] p4r = null;
                if (!isOverlapping) {
                    p1r = BlockMethods.computeBlockPrecisionRecallGenesAndExps(EvaluateIncrNonoRand.true1, cur);
                    p2r = BlockMethods.computeBlockPrecisionRecallGenesAndExps(EvaluateIncrNonoRand.true2, cur);
                    p3r = BlockMethods.computeBlockPrecisionRecallGenesAndExps(EvaluateIncrNonoRand.true3, cur);
                    p4r = BlockMethods.computeBlockPrecisionRecallGenesAndExps(EvaluateIncrNonoRand.true4, cur);
                } else {
                    p1r = BlockMethods.computeBlockPrecisionRecallGenesAndExps(EvaluateIncrOverRand.true1, cur);
                    p2r = BlockMethods.computeBlockPrecisionRecallGenesAndExps(EvaluateIncrOverRand.true2, cur);
                    p3r = BlockMethods.computeBlockPrecisionRecallGenesAndExps(EvaluateIncrOverRand.true3, cur);
                    p4r = BlockMethods.computeBlockPrecisionRecallGenesAndExps(EvaluateIncrOverRand.true4, cur);
                    //d4f = new double[2];
                    //d4f[0] = 0;
                    //d4f[1] = 0;
                }

                double z1 = 0;
                if (precmax[0] + recmax[0] != 0)
                    z1 = stat.geometricMeanN2(precmax[0], recmax[0]);
                double z2 = 0;
                if (precmax[1] + recmax[1] != 0)
                    z2 = stat.geometricMeanN2(precmax[1], recmax[1]);
                double z3 = 0;
                if (precmax[2] + recmax[2] != 0)
                    z3 = stat.geometricMeanN2(precmax[2], recmax[2]);
                double z4 = 0;
                if (precmax[3] + recmax[3] != 0)
                    z4 = stat.geometricMeanN2(precmax[3], recmax[3]);

                if (stat.geometricMeanN2(p1r[0], p1r[1]) > z1) {
                    precmax[0] = p1r[0];
                    recmax[0] = p1r[1];
                }
                if (stat.geometricMeanN2(p2r[0], p2r[1]) > z2) {
                    precmax[1] = p2r[0];
                    recmax[1] = p2r[1];
                }
                if (stat.geometricMeanN2(p3r[0], p3r[1]) > z3) {
                    precmax[2] = p3r[0];
                    recmax[2] = p3r[1];
                }
                if (stat.geometricMeanN2(p4r[0], p4r[1]) > z4) {
                    precmax[3] = p4r[0];
                    recmax[3] = p4r[1];
                }
            }


            double[] merge = MoreArray.join(F1max, sensmax);
            merge = MoreArray.join(merge, specmax);
            merge = MoreArray.join(merge, precmax);
            merge = MoreArray.join(merge, recmax);
            extsummarydata.add(merge);//"" + MoreArray.toString(F1max, "\t") + "\t" + MoreArray.toString(sensspecmax, "\t"));
        }
        String[] extxlabels = {"true1F1Max", "true2F1Max", "true3F1Max", "true4F1Max",
                "true1SensMax", "true2SensMax", "true3SensMax", "true4SensMax",
                "true1SpecMax", "true2SpecMax", "true3SpecMax", "true4SpecMax",
                "true1PrecisionMax", "true2PrecisionMax", "true3PrecisionMax", "true4PrecisionMax",
                "true1RecallMax", "true2RecallMax", "true3RecallMax", "true4RecallMax",};
        double[][] outdataa = MoreArray.ArrayListto2DDouble(extsummarydata);
        String f = outlabel + "_extmax.txt";
        TabFile.write(outdataa, f, extxlabels, extlabels);
    }

    /**
     * @param path
     * @param file_list
     * @param oneL_len
     * @param ylabels
     */
    private void doDir(String path, String[] file_list, int oneL_len, String[] ylabels) {
        int count = 0;
        ArrayList curavg = new ArrayList();
        ArrayList cursd = new ArrayList();
        //for all criteria+start combinations
        for (int a = 0; a < crits.length; a++) {
            //System.out.print(".");
            for (int b = 0; b < starts.length; b++) {
                ArrayList arforavg = new ArrayList();
                String curylabel = null;

                int maxspec = 0;
                int maxsens = 0;
                //current directory of trajectories
                for (int i = 0; i < oneL_len; i++) {
                    if (a == 0 && b == 0)
                        System.out.println(i + "\t" + file_list[i] + "\t" + crits[a] + "\t" + starts[b]);
                    if (file_list[i].indexOf(crits[a]) != -1 && file_list[i].indexOf(starts[b]) != -1) {
                        //System.out.println("success");
                        int ind = file_list[i].indexOf("__");
                        if (curylabel == null)
                            curylabel = file_list[i].substring(ind + ("__").length(),
                                    file_list[i].indexOf("__", ind + "__".length())) + "\t" + starts[b];
                        ValueBlockList vbl = null;
                        String f = path + "/" + file_list[i];
                        try {
                            vbl = ValueBlockList.read(f, true);
                        } catch (Exception e) {
                            System.out.println("failed to read " + f);
                            e.printStackTrace();
                        }
                        if (vbl == null || vbl.size() == 0) {
                            System.out.println("empty? " + f);
                        } else {
                            ValueBlock first = (ValueBlock) vbl.get(0);
                            //ValueBlock last = (ValueBlock) vbl.get(vbl.size() - 1);
                            int vbl_index = vbl.size() - 1;
                            if (lastadd) {
                                vbl_index = ValueBlockListMethods.lastBlockGeneExpAddition(vbl);
                                if (vbl_index == -1) {
                                    vbl_index = ValueBlockListMethods.lastBlockGeneAddition(vbl);
                                }
                                if (vbl_index == -1) {
                                    vbl_index = ValueBlockListMethods.lastBlockExpAddition(vbl);
                                }
                                if (vbl_index == -1) {
                                    vbl_index = vbl.size() - 1;
                                }
                            }
                            ValueBlock last = (ValueBlock) vbl.get(vbl_index);

                            double[] out = new double[xlabels.length - 1];

                            out = computeF1Last(out, file_list[i], last);

                            out = computeSensSpecLast(last, out);

                            out = computeF1First(first, out);

                            out = computeSensSpecFirst(first, out);


                            out[30] = first.full_criterion;


                            out = computePrecisionRecallLast(last, out);
                            out = computePrecisionRecallFirst(last, out);


                            arforavg.add(out);
                        }
                    }
                }
                if (arforavg.size() > 0) {
                    double[] avg = stat.arraySampAvg(arforavg);
                    curavg.add(avg);
                    double[] sd = stat.arraySampSD(arforavg, avg);
                    cursd.add(sd);

                    ylabels[count] = curylabel;
                    count++;
                } else {
                    System.out.println("no values " + crits[a] + "\t" + starts[b]);
                }
            }

            System.out.println(crits[a] + "\t" + curavg.size());
        }


        double[][] storedouba = MoreArray.ArrayListto2DDouble(curavg);
        avg_data.add(storedouba);
        double[][] storedoubs = MoreArray.ArrayListto2DDouble(cursd);
        sd_data.add(storedoubs);
        labels.add(ylabels);
    }

    /**
     * @param first
     * @param out
     * @return
     */
    private double[] computeF1First(ValueBlock first, double[] out) {
        if (!isOverlapping) {
            out[18] = BlockMethods.computeBlockF1(EvaluateIncrNonoRand.true1, first, debug);
            out[19] = BlockMethods.computeBlockF1(EvaluateIncrNonoRand.true2, first, debug);
            out[20] = BlockMethods.computeBlockF1(EvaluateIncrNonoRand.true3, first, debug);
            out[21] = BlockMethods.computeBlockF1(EvaluateIncrNonoRand.true4, first, debug);
        } else {
            out[18] = BlockMethods.computeBlockF1(EvaluateIncrOverRand.true1, first, debug);
            out[19] = BlockMethods.computeBlockF1(EvaluateIncrOverRand.true2, first, debug);
            out[20] = BlockMethods.computeBlockF1(EvaluateIncrOverRand.true3, first, debug);
            out[21] = BlockMethods.computeBlockF1(EvaluateIncrOverRand.true4, first, debug);
            //out[21] = 0;
        }

        return out;
    }

    /**
     * @param first
     * @param out
     */
    private double[] computePrecisionRecallFirst(ValueBlock first, double[] out) {
        double[] d1f = null;
        double[] d2f = null;
        double[] d3f = null;
        double[] d4f = null;
        if (!isOverlapping) {
            d1f = BlockMethods.computeBlockPrecisionRecallGenesAndExps(EvaluateIncrNonoRand.true1, first);
            d2f = BlockMethods.computeBlockPrecisionRecallGenesAndExps(EvaluateIncrNonoRand.true2, first);
            d3f = BlockMethods.computeBlockPrecisionRecallGenesAndExps(EvaluateIncrNonoRand.true3, first);
            d4f = BlockMethods.computeBlockPrecisionRecallGenesAndExps(EvaluateIncrNonoRand.true4, first);
        } else {
            d1f = BlockMethods.computeBlockPrecisionRecallGenesAndExps(EvaluateIncrOverRand.true1, first);
            d2f = BlockMethods.computeBlockPrecisionRecallGenesAndExps(EvaluateIncrOverRand.true2, first);
            d3f = BlockMethods.computeBlockPrecisionRecallGenesAndExps(EvaluateIncrOverRand.true3, first);
            d4f = BlockMethods.computeBlockPrecisionRecallGenesAndExps(EvaluateIncrOverRand.true4, first);
            //d4f = new double[2];//BlockMethods.computeBlockSensitivitySpecificityGenesAndExps(EvaluateIncrOverRand.true4, first, totaldim);
            //d4f[0] = 0;
            //d4f[1] = 0;
        }
        //starts at 39
        out[39] = d1f[0];
        out[40] = d2f[0];
        out[41] = d3f[0];
        out[42] = d4f[0];
        out[43] = d1f[1];
        out[44] = d2f[1];
        out[45] = d3f[1];
        out[46] = d4f[1];

        return out;
    }

    /**
     * @param first
     * @param out
     */
    private double[] computeSensSpecFirst(ValueBlock first, double[] out) {
        double[] d1f = null;
        double[] d2f = null;
        double[] d3f = null;
        double[] d4f = null;
        if (!isOverlapping) {
            d1f = BlockMethods.computeBlockSensitivitySpecificityGenesAndExps(EvaluateIncrNonoRand.true1, first, totaldim);
            d2f = BlockMethods.computeBlockSensitivitySpecificityGenesAndExps(EvaluateIncrNonoRand.true2, first, totaldim);
            d3f = BlockMethods.computeBlockSensitivitySpecificityGenesAndExps(EvaluateIncrNonoRand.true3, first, totaldim);
            d4f = BlockMethods.computeBlockSensitivitySpecificityGenesAndExps(EvaluateIncrNonoRand.true4, first, totaldim);
        } else {
            d1f = BlockMethods.computeBlockSensitivitySpecificityGenesAndExps(EvaluateIncrOverRand.true1, first, totaldim);
            d2f = BlockMethods.computeBlockSensitivitySpecificityGenesAndExps(EvaluateIncrOverRand.true2, first, totaldim);
            d3f = BlockMethods.computeBlockSensitivitySpecificityGenesAndExps(EvaluateIncrOverRand.true3, first, totaldim);
            d4f = BlockMethods.computeBlockSensitivitySpecificityGenesAndExps(EvaluateIncrOverRand.true4, first, totaldim);
            //d4f = new double[2];//BlockMethods.computeBlockSensitivitySpecificityGenesAndExps(EvaluateIncrOverRand.true4, first, totaldim);
            //d4f[0] = 0;
            //d4f[1] = 0;
        }
        out[22] = d1f[0];
        out[23] = d2f[0];
        out[24] = d3f[0];
        out[25] = d4f[0];
        out[26] = d1f[1];
        out[27] = d2f[1];
        out[28] = d3f[1];
        out[29] = d4f[1];

        return out;
    }


    /**
     * @param out
     * @param last
     * @param out
     * @return
     */
    private double[] computeSensSpecLast(ValueBlock last, double[] out) {
        double[] d1 = null;
        double[] d2 = null;
        double[] d3 = null;
        double[] d4 = null;

        if (!isOverlapping) {
            d1 = BlockMethods.computeBlockSensitivitySpecificityGenesAndExps(EvaluateIncrNonoRand.true1, last, totaldim);
            d2 = BlockMethods.computeBlockSensitivitySpecificityGenesAndExps(EvaluateIncrNonoRand.true2, last, totaldim);
            d3 = BlockMethods.computeBlockSensitivitySpecificityGenesAndExps(EvaluateIncrNonoRand.true3, last, totaldim);
            d4 = BlockMethods.computeBlockSensitivitySpecificityGenesAndExps(EvaluateIncrNonoRand.true4, last, totaldim);
        } else {
            d1 = BlockMethods.computeBlockSensitivitySpecificityGenesAndExps(EvaluateIncrOverRand.true1, last, totaldim);
            d2 = BlockMethods.computeBlockSensitivitySpecificityGenesAndExps(EvaluateIncrOverRand.true2, last, totaldim);
            d3 = BlockMethods.computeBlockSensitivitySpecificityGenesAndExps(EvaluateIncrOverRand.true3, last, totaldim);
            d4 = BlockMethods.computeBlockSensitivitySpecificityGenesAndExps(EvaluateIncrOverRand.true4, last, totaldim);
            //d4 = new double[2];
            //d4[0] = 0;
            //d4[1] = 0;
        }

        out[10] = d1[0];
        out[11] = d2[0];
        out[12] = d3[0];
        out[13] = d4[0];
        out[14] = d1[1];
        out[15] = d2[1];
        out[16] = d3[1];
        out[17] = d4[1];


        return out;
    }

    /**
     * @param out
     * @param last
     * @param out
     * @return
     */
    private double[] computePrecisionRecallLast(ValueBlock last, double[] out) {
        double[] d1 = null;
        double[] d2 = null;
        double[] d3 = null;
        double[] d4 = null;

        if (!isOverlapping) {
            d1 = BlockMethods.computeBlockPrecisionRecallGenesAndExps(EvaluateIncrNonoRand.true1, last);
            d2 = BlockMethods.computeBlockPrecisionRecallGenesAndExps(EvaluateIncrNonoRand.true2, last);
            d3 = BlockMethods.computeBlockPrecisionRecallGenesAndExps(EvaluateIncrNonoRand.true3, last);
            d4 = BlockMethods.computeBlockPrecisionRecallGenesAndExps(EvaluateIncrNonoRand.true4, last);
        } else {
            d1 = BlockMethods.computeBlockPrecisionRecallGenesAndExps(EvaluateIncrOverRand.true1, last);
            d2 = BlockMethods.computeBlockPrecisionRecallGenesAndExps(EvaluateIncrOverRand.true2, last);
            d3 = BlockMethods.computeBlockPrecisionRecallGenesAndExps(EvaluateIncrOverRand.true3, last);
            d4 = BlockMethods.computeBlockPrecisionRecallGenesAndExps(EvaluateIncrOverRand.true4, last);
            //d4 = new double[2];
            //d4[0] = 0;
            //d4[1] = 0;
        }

        //starts at 31
        out[31] = d1[0];
        out[32] = d2[0];
        out[33] = d3[0];
        out[34] = d4[0];
        out[35] = d1[1];
        out[36] = d2[1];
        out[37] = d3[1];
        out[38] = d4[1];

        return out;
    }

    /**
     * @param s
     * @param last
     * @return
     */
    private double[] computeF1Last(double[] out, String s, ValueBlock last) {

        out[0] = last.full_criterion;
        out[1] = s.indexOf("PPI") != -1 ? 1 : 0;
        out[2] = s.indexOf("LARS") != -1 || s.indexOf("FEM") != -1 ? 1 : 0;
        out[3] = (s.indexOf("PPI") == -1 && s.indexOf("LARS") == -1 && s.indexOf("FEM") == -1) ? 1 : 0;
        out[4] = s.indexOf("nonull") != -1 ? 0 : 1;
        out[5] = s.indexOf("BS") != -1 ? 1 : 0;

        if (!isOverlapping) {
            out[6] = BlockMethods.computeBlockF1(EvaluateIncrNonoRand.true1, last, debug);
            out[7] = BlockMethods.computeBlockF1(EvaluateIncrNonoRand.true2, last, debug);
            out[8] = BlockMethods.computeBlockF1(EvaluateIncrNonoRand.true3, last, debug);
            out[9] = BlockMethods.computeBlockF1(EvaluateIncrNonoRand.true4, last, debug);
        } else {
            out[6] = BlockMethods.computeBlockF1(EvaluateIncrOverRand.true1, last, debug);
            out[7] = BlockMethods.computeBlockF1(EvaluateIncrOverRand.true2, last, debug);
            out[8] = BlockMethods.computeBlockF1(EvaluateIncrOverRand.true3, last, debug);
            out[9] = BlockMethods.computeBlockF1(EvaluateIncrOverRand.true4, last, debug);
            //out[9] = 0;
        }
        return out;
    }

    /**
     * @param list
     */
    private void populateCritandStarts(String[] list) {
        ArrayList startsA = new ArrayList();
        ArrayList critsA = new ArrayList();
        for (int i = 0; i < list.length; i++) {
            String startstr = null;
            //int start = list[i].indexOf("STARTS_");
            //int start = list[i].indexOf("abs_");
            int end = list[i].indexOf("__");
            int start = list[i].lastIndexOf("_", end - 1);
            start = list[i].lastIndexOf("_", start - 1);
            //System.out.println("populateCritandStarts " + list[i] + "\t" + start);
            if (start != -1) {
                //end = list[i].indexOf("__", start + 7);
                end = list[i].indexOf("__", start + 1);//+4
                /*TODO fix extra "abs_" string*/
                startstr = list[i].substring(start + 1, end);//"_" +
                System.out.println(list[i] + "\t" + start + "\t" + end + "\t" + startstr);
            }
            //System.out.println(list[i] + "\t" + start + "\t" + end + "\t" + startstr);
            if (startstr != null) {
                if (startsA.indexOf(startstr) == -1) {
                    startsA.add(startstr);
                    System.out.println("added start " + startstr);
                }
            }

            String critstr = null;
            int start2 = list[i].indexOf("__");
            if (start2 != -1) {
                int end2 = list[i].indexOf("__", start2 + 2);//list[i].length();//
                critstr = list[i].substring(start2, end2 + 2);
            }
            if (critstr != null) {
                if (critsA.indexOf(critstr) == -1) {
                    //System.out.println("added crit " + critstr);
                    critsA.add(critstr);
                }
            }

        }

        starts = MoreArray.ArrayListtoString(startsA);
        crits = MoreArray.ArrayListtoString(critsA);

        System.out.println("starts");
        MoreArray.printArray(starts);
        System.out.println("crits");
        MoreArray.printArray(crits);
    }

    /**
     * @param outlabel
     * @param dataavg
     * @param datasd
     * @param labels
     */
    private void out(String outlabel, ArrayList dataavg, ArrayList datasd, ArrayList labels) {
        double[][] outdataa = MoreArray.listto2DArray(dataavg, 5);
        String[] allylabels = MoreArray.toOneArray(labels);
        String f = outlabel + "_mean.txt";

        TabFile.write(outdataa, f, xlabels, allylabels);

        String f2 = outlabel + "_sd.txt";
        double[][] outdatas = MoreArray.listto2DArray(datasd, 5);
        TabFile.write(outdatas, f2, xlabels, allylabels);

        System.out.println("wrote " + f);
        System.out.println("wrote " + f2);
    }

    /**
     * @paramfile args
     */

    public final static void main(String[] args) {
        if (args.length == 3 || args.length == 4 || args.length == 5) {
            CompareTrajSet rm = new CompareTrajSet(args);
        } else {
            System.out.println("syntax: java DataMining.eval.CompareTrajSet\n" +
                    "< dir of dirs >\n" +
                    "< out file>\n" +
                    "<type: nono or over>\n" +
                    "<OPTIONAL external method result dir>\n" +
                    "<OPTIONAL: bicluster file>"
            );
        }
    }

}
