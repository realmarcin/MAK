package DataMining.util;

import DataMining.*;
import DataMining.func.BuildGraph;
import mathy.Matrix;
import mathy.SimpleMatrix;
import mathy.stat;
import util.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Class to score a list of blocks with a single or series of criteria, plus summary stats and enrichments.
 * <p/>
 * User: marcin
 * Date: Jan 15, 2011
 * Time: 7:58:30 PM
 */
public class ScoreBlocks extends Program {
    public final static String version = "0.1";
    boolean debug = false;

    String[] valid_args = {
            "-bic", "-param", "-tab", "-go", "-tigr", "-goyeast", "-PATH", "-PPI", "-TFREF",
            "-GOT", "-abs", "-crits", "-incr", "-geneids", "-debug"
    };
    HashMap options;

    String outdir;
    String outprefix;

    public Criterion criterion;
    int curCRIT;
    String curCRITlabel;

    String[] gene_labels;

    int dataexpmax, datagenemax;

    ValueBlockList topNlist = new ValueBlockList();
    ValueBlockList BIC = new ValueBlockList();

    String header;
    int TOPLIST_LEN = -1;
    double TOP_FRACTION = 1;//0.1;

    public RunMinerBack rmb;

    String[][] tab_data, GO_data, GO_yeast_data, TIGR_data, GO_terms_data, path_data;
    SimpleMatrix interact_data, TFREF_data;

    ArrayList outGOprofile = new ArrayList();
    ArrayList outTFprofile = new ArrayList();
    ArrayList outTIGRprofile = new ArrayList();
    ArrayList outTIGRroleprofile = new ArrayList();
    ArrayList outPathprofile = new ArrayList();
    ArrayList block_ids = new ArrayList();

    public AssignCrit assignCrit;

    ArrayList summary = new ArrayList();
    boolean first = true;
    String[] empty = {""};

    HashMap GO_yeast_data_map = new HashMap(), GO_terms_map = new HashMap(), yeast_gene_names = new HashMap();
    ArrayList GO_C = new ArrayList(), GO_P = new ArrayList(), GO_F = new ArrayList();

    int limit_blocks = -1;

    String abs_axis = null;
    HashMap TIGRmap, TIGRrolemap, pathmap;

    int offsetincr = 0;

    boolean annot = false;


    /*
    
     */
    public static String[] CRITlabels = {
            "MSEC_KendallC_FEMC"
            //"MSEC_KendallC_FEMC_PPI_MAXTF"
            //"MSEC_KendallC_PPI_MAXTF"
            /*"MSER",
            "MSEC",*/
            //"MSER_FEMC_MEDRMEAN"
            /*
            "MSER_LARSRE_PPI",//11
            "MADR_LARSRE_PPI",//12
            "Kendall_LARSRE_PPI",//13
            "MSER_FEMR_PPI",//17
            "MADR_FEMR_PPI",//18
            "Kendall_FEMR_PPI",//19
            "MSEC_LARSRE_PPI",//27                                                                                                      SOMR1_expr_geneids.txt
            "MSEC_FEMR_PPI",//29
            "MSER_LARSCE_PPI",//52
            "MADR_LARSCE_PPI",//53
            "Kendall_LARSCE_PPI",//54
            "MSER_FEMC_PPI",//58
            "MADR_FEMC_PPI",//59
            "Kendall_FEMC_PPI",//60
            "MSEC_LARSCE_PPI",//64
            "MSEC_FEMC_PPI",//66
            "MSE_LARSRE_PPI",//68
            "MSE_FEMR_PPI",//70
            "MSE_LARSCE_PPI",//72
            "MSE_FEMC_PPI",//74*/
    };

    //maxproportion + "\t" + ftest + "\t" + curTIGRlab + "\t" + noTIGRprop + "\t" + ftestnoTIGR;
    //maxproportion + "\t" + ftest + "\t" + curTIGRlab + "\t" + noTIGRprop + "\t" + ftestnoTIGR;
    //maxGO + "\t" + ftest + "\t" + curgolab + "\t" + noGOprop + "\t" + ftestnoGO;
    //tfids[maxindex] + "\t" + max;
    public String[] summary_header = {
            "index",
            "block_id",
            "genes",
            "exps",
            "full_crit ",
            "abs_exp_mean",
            "TIGRprop",
            "TIGRftest",
            "TIGR",
            "noTIGRprop",
            "ftestnoTIGR",
            "TIGRroleprop",
            "TIGRroleftest",
            "TIGRrole",
            "noTIGRroleprop",
            "ftestnoTIGR",
            "GOprop",
            "GOftest",
            "GO",
            "noGOprop",
            "noGOftest",
            "TF",
            "TFfreq",
            "exp_mean_crit",
            "exp_mse_crit",
            "exp_kendall_crit",
            "exp_reg_crit",
            "inter_crit",
            "feat_crit",
            "TF_crit",
            "exp_mean",
            "exp_mean_pos_row",
            "exp_mean_neg_row",
            "exp_mean_pos_col",
            "exp_mean_neg_col",
    };

    public String[] summary_header_noannot = {
            "index",
            "block_id",
            "genes",
            "exps",
            "full_crit ",
            "abs_exp_mean",
            "exp_mean_crit",
            "exp_mse_crit",
            "exp_kendall_crit",
            "exp_reg_crit",
            "inter_crit",
            "feat_crit",
            "TF_crit",
            "exp_mean",
            "exp_mean_pos_row",
            "exp_mean_neg_row",
            "exp_mean_pos_col",
            "exp_mean_neg_col",
    };

    /**
     * @param args
     */
    public ScoreBlocks(String[] args) {

        System.out.println("valid_args");
        MoreArray.printArray(valid_args);

        init(args);

        TOPLIST_LEN = (int) (BIC.size() * TOP_FRACTION);

        System.out.println("datagenemax " + datagenemax + "\tdataexpmax " + dataexpmax);
        System.out.println("vbl.size() " + BIC.size() + "\tMAX_TOPLIST_LEN " + TOPLIST_LEN +
                "\tTOP_FRACTION " + TOP_FRACTION);

        assignCrit = new AssignCrit(CRITlabels);
        for (int c = 0; c < CRITlabels.length; c++) {

            try {
                boolean useMean = assignCrit.CRITmean[c] == 1;
                if (useMean)
                    rmb.orig_prm.USE_MEAN = true;
                else
                    rmb.orig_prm.USE_MEAN = false;

                topNlist = new ValueBlockList();
                curCRIT = assignCrit.CRITindex[c];
                curCRITlabel = CRITlabels[c];

                criterion = new Criterion(curCRIT, useMean, true,
                        rmb.orig_prm.USE_ABS_AR, assignCrit.CRITTF[c] == 1 ? true : false, rmb.orig_prm.needinv, true, rmb.irv.prm.FRXN_SIGN, debug);
                System.out.println("doing " + curCRIT + "\t" + assignCrit.labels[c] +
                        "\tisMeanCrit " + assignCrit.CRITmean[c] + "\tisTFCrit " + assignCrit.CRITTF[c]);

                rmb.orig_prm.crit = criterion;
                /*rmb.orig_prm.precrit = criterion;
                rmb.orig_prm.CRIT_TYPE_INDEX = curCRIT;
                rmb.orig_prm.PRECRIT_TYPE_INDEX = curCRIT;*/

                rmb.irv.onv = new ObtainNullValues(rmb.irv.Rengine, rmb.orig_prm, debug);
                //irv.onv = onv;
                if (limit_blocks == -1)
                    limit_blocks = BIC.size();
                for (int i = 0; i < limit_blocks; i++) {
                    ValueBlock cur = (ValueBlock) BIC.get(i);

                    block_ids.add(cur.blockId());
                    if (cur.exp_data == null) {
                        cur.setDataAndMean(rmb.expr_matrix.data);
                        cur.setSignedMean(rmb.expr_matrix.data);
                    }
                    System.out.println("cur " + i + "\t" + cur.blockId());
                    cur.trimNAN(rmb.expr_matrix.data, 0.2, 0.2, false);
                    System.out.println("cur af " + i + "\t" + cur.blockId());
                    /*boolean aboveNaNThreshold = cur.isAboveNaN(rmb.orig_prm.PERCENT_ALLOWED_MISSING_IN_BLOCK);
                    if (!aboveNaNThreshold) {
                        double frxn = cur.frxnNaN();
                        System.out.println("exceeds the missing data limit: " + frxn + "\tlimit " +
                                rmb.orig_prm.PERCENT_ALLOWED_MISSING_IN_BLOCK);
                        //System.exit(0);
                    } else {*/
                    if (cur.genes.length > 0 && cur.exps.length > 0) {
                        double[] ret = computeCrit(cur.genes, cur.exps, i);
                        System.out.println("values " + MoreArray.toString(ret));
                        if (ret != null && ret.length > 0)
                            cur.updateAllCriteria(ret);
                        else {
                            System.out.println("crit failed for " + MoreArray.toString(cur.genes, ",") +
                                    "\t" + MoreArray.toString(cur.exps, ","));
                        }
                    } else {
                        System.out.println("too few exps or genes " + cur.exps.length + "\t" + cur.genes.length);
                    }
                    //}
                }
                String outf = outdir + "/" + outprefix + curCRITlabel;
                if (abs_axis != null)
                    outf += "_split" + abs_axis;
                outf += ".txt";
                //String outbic = outdir + "/" + curCRITlabel + ".bic";
                System.out.println("writing " + outf);
                String s = header + MINER_STATIC.HEADER_VBL;//ValueBlock.toStringShortColumns();
                String data = topNlist.toString(s);
                TextFile.write(data, outf);
                //topNlist.writeBIC(outbic);
                System.out.println("wrote " + outf);
                //System.out.println("wrote " + outbic);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String outsum = outdir + "/" + outprefix + "_" + curCRITlabel + "_summary";
        if (abs_axis != null)
            outsum += "_split" + abs_axis;
        outsum += ".txt";
        if (annot)
            summary.add(0, MoreArray.toString(summary_header, "\t"));
        else
            summary.add(0, MoreArray.toString(summary_header_noannot, "\t"));
        util.TextFile.write(summary, outsum);
        System.out.println("wrote " + outsum);

        if (outTFprofile.size() > 0) {
            String outTF = outdir + "/" + outprefix + "TFprofile";
            if (abs_axis != null)
                outTF += "_split" + abs_axis;
            outTF += ".txt";
            util.TextFile.write(outTFprofile, outTF);
            System.out.println("wrote " + outTF);
        }

        if (outGOprofile.size() > 0) {
            String outGO = outdir + "/" + outprefix + "GOprofile";
            if (abs_axis != null)
                outGO += "_split" + abs_axis;
            outGO += ".txt";
            util.TextFile.write(outGOprofile, outGO);
            System.out.println("wrote " + outGO);
        }
        if (outTIGRprofile.size() > 0) {
            String outTIGR = outdir + "/" + outprefix + "TIGRprofile";
            if (abs_axis != null)
                outTIGR += "_split" + abs_axis;
            outTIGR += ".txt";
            util.TextFile.write(outTIGRprofile, outTIGR);
            System.out.println("wrote " + outTIGR);
        }

        if (outTIGRroleprofile.size() > 0) {
            String outTIGRrole = outdir + "/" + outprefix + "TIGRroleprofile";
            if (abs_axis != null)
                outTIGRrole += "_split" + abs_axis;
            outTIGRrole += ".txt";
            util.TextFile.write(outTIGRroleprofile, outTIGRrole);
            System.out.println("wrote " + outTIGRrole);
        }

        if (outPathprofile.size() > 0) {
            String outPath = outdir + "/" + outprefix + "Pathprofile";
            if (abs_axis != null)
                outPath += "_split" + abs_axis;
            outPath += ".txt";
            util.TextFile.write(outPathprofile, outPath);
            System.out.println("wrote " + outPath);
        }

        System.exit(0);
    }


    /**
     *
     */
    private double[] computeCrit(int[] Ic, int[] Jc, int curblockindex) {

        //System.out.print(".");
        if (debug) {
            System.out.println("computeCrit " + criterion.crit + "\t" + criterion.isFEM);
            System.out.println("Ic size " + curblockindex + "\t" + Ic.length);
            System.out.println("Jc size " + curblockindex + "\t" + Jc.length);
            System.out.println("R: Jc<-c(" + MoreArray.toString(Jc, ",") + ")");
            System.out.println("R: Ic<-c(" + MoreArray.toString(Ic, ",") + ")");

            //System.out.println("R: nullRegData <- NULL");
        }

        //remove last 2 exps for FEM and LARS if all exps part of block
        /*if ((criterion.isFEM || criterion.isLARS) && Jc.length == expr_data.data[0].length) {
            Jc = MoreArray.remove(Jc, Jc.length - 1);
            Jc = MoreArray.remove(Jc, Jc.length - 1);
            System.out.println("WARNING FEM/LARS: # of columns = # of experiments. Removing last 2 experiments.");
        }
*/
        if (Ic.length > rmb.orig_prm.IMAX) {
            System.out.println("WARNING More genes than allowed " + Ic.length + " criterion based on max bounds.");
        }
        if (Jc.length > rmb.orig_prm.JMAX) {
            System.out.println("WARNING More exps than allowed " + Jc.length + " criterion based on max bounds.");
        }
        boolean[] passcrits = Criterion.getExprCritTypes(criterion, true, rmb.orig_prm.USE_MEAN, debug);
        double[][] crits = null;
        try {
            if (criterion == null)
                System.out.println("criterion is null");
            if (rmb.irv == null)
                System.out.println("irv is null");
            if (rmb.irv.onv == null)
                System.out.println("irv.onv is null");
            if (gene_labels == null)
                System.out.println("gene_labels is null");
            //if (rmb.feat_matrix == null)
             //   System.out.println("rmb.feat_matrix is null");
            if (rmb.orig_prm == null)
                System.out.println("rmb.orig_prm is null");

            crits = ComputeCrit.compute(rmb.irv, Ic, Jc,
                    rmb.orig_prm, criterion,
                    gene_labels, rmb.feat_matrix != null ? rmb.feat_matrix.data : null, debug);
        } catch (Exception e) {
            System.out.println("criterion compute failed");
            e.printStackTrace();
        }
        if (crits == null || crits.length == 0) {
            System.out.println("ERROR: crits == null");
            //System.exit(1);
        } else {
            //double origfull = curBlock.fullcrit;
            if (debug)
                System.out.println("crits[0].length " + crits[0].length);

            double[] critvals = crits[0];
            double fullcrit = ValueBlock.computeFullCrit(critvals, true, passcrits, debug);

            if (debug) {
                System.out.println("fullcrit " + fullcrit);
                MoreArray.printArray(critvals);
            }
            int posFull = ValueBlockListMethods.findPosFull(topNlist, fullcrit, TOPLIST_LEN);
            if (posFull != -1) {
                ValueBlock curblock = new ValueBlock(Ic, Jc);
                curblock.setDataAndMean(rmb.expr_matrix.data);
                curblock.setSignedMean(rmb.expr_matrix.data);
                curblock.setSignedCountRow(rmb.expr_matrix.data);
                curblock.setSignedCountCol(rmb.expr_matrix.data);

                curblock.updateCrit(crits, true, passcrits, debug);
                if (posFull < topNlist.size() || topNlist.size() == 0)
                    topNlist.add(posFull, curblock);
                else if (topNlist.size() < TOPLIST_LEN)
                    topNlist.add(curblock);
                while (topNlist.size() > TOPLIST_LEN) {
                    topNlist.remove(topNlist.size() - 1);
                }

                //if (first) {
                String sum = "" + (curblockindex + 1) + "\t" + curblock.blockId() + "\t" + curblock.genes.length + "\t" +
                        curblock.exps.length + "\t" + curblock.full_criterion + "\t" + curblock.exp_mean;

                if (annot) {
                    String[] geneids = new String[curblock.genes.length];
                    for (int g = 0; g < geneids.length; g++) {
                        try {
                            //refids[g] = StringUtil.replace(rmb.expr_matrix.ylabels[curblock.genes[g] - 1], "\"", "");
                            geneids[g] = StringUtil.replace(gene_labels[curblock.genes[g] - 1], "\"", "");
                        } catch (Exception e) {
                            System.out.println("expr_data.ylabels " + rmb.expr_matrix.ylabels.length + "\t" + curblock.genes[g]);
                            e.printStackTrace();
                        }
                    }
                    //System.out.println("refids");
                    //System.out.println(MoreArray.toString(refids, "\n"));

                    ArrayList goids = new ArrayList();
                    ArrayList golabels = new ArrayList();
                    if (tab_data != null) {
                        ArrayList COGlabs = new ArrayList();
                        ArrayList COGfunlabs = new ArrayList();
                        ArrayList TIGRlabs = new ArrayList();
                        ArrayList TIGRRolelabs = new ArrayList();

                        for (int g = 0; g < geneids.length; g++) {
                            boolean found = true;
                            for (int t = 1; t < tab_data.length; t++) {
                                //todo convert to hashmap
                                if (tab_data[t][0].equals(geneids[g])) {
                                    String[] singlecase = new String[1];
                                    try {
                                        singlecase[0] = tab_data[t][13];
                                        //System.out.println(tab_data[t][13]);
                                        goids.add(tab_data[t][13].indexOf(",") != -1 ? tab_data[t][13].split(",") : singlecase);
                                    } catch (Exception e) {
                                        goids.add(empty);
                                        //e.printStackTrace();
                                    }
                                    try {
                                        COGlabs.add(tab_data[t][10]);
                                        COGfunlabs.add(tab_data[t][11]);
                                    } catch (Exception e) {
                                        COGlabs.add(empty);
                                        COGfunlabs.add(empty);
                                        //e.printStackTrace();
                                    }
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                System.out.println("not found " + geneids[g]);
                            }
                        }

                        for (int g = 0; g < geneids.length; g++) {
                            Object o = TIGRmap.get(geneids[g]);
                            boolean found = false;
                            if (o != null) {
                                TIGRlabs.add((String) o);
                                found = true;
                            }

                        /*for (int t = 1; t < TIGR_data.length; t++) {
                            if (TIGR_data[t][0].equals(refids[g])) {
                                TIGRlabs.add(TIGR_data[t][1]);
                                found = true;
                                break;
                            }
                        }*/

                            if (!found)
                                TIGRlabs.add("");
                        }

                        /*
                        System.out.println("TIGRlabs");
                        MoreArray.printArray(MoreArray.ArrayListtoString(TIGRlabs));

                        System.out.println("refids vs TIGRlabs " + refids.length + "\t" + TIGRlabs.size());
                        */
                        sum = doTIGR(sum, geneids, TIGRlabs, curblockindex);

                        for (int g = 0; g < geneids.length; g++) {
                            Object o = TIGRrolemap.get(geneids[g]);
                            boolean found = false;
                            if (o != null) {
                                TIGRRolelabs.add((String) o);
                                found = true;
                            }

                            for (int t = 1; t < TIGR_data.length; t++) {
                                if (TIGR_data[t][0].equals(geneids[g])) {
                                    TIGRRolelabs.add(TIGR_data[t][2]);
                                    found = true;
                                    break;
                                }
                            }

                            if (!found)
                                TIGRRolelabs.add("");
                        }

                        System.out.println("geneids vs TIGRRolelabs " + geneids.length + "\t" + TIGRRolelabs.size());
                        sum = doTIGRroles(sum, geneids, TIGRRolelabs, curblockindex);
                    }

                    if (GO_yeast_data != null) {
                        sum = getGO(curblockindex, sum, geneids, goids, golabels);
                    }

                    if (TFREF_data != null) {
                        sum = doTF(sum, geneids, curblock.genes, curblockindex);
                    }
                }

                //System.out.println("curblock.all_criteria");
                //MoreArray.printArray(curblock.all_criteria);
                sum += "\t" + curblock.all_criteria[0] + "\t" + curblock.all_criteria[1] + "\t" + curblock.all_criteria[2] + "\t" +
                        curblock.all_criteria[3] + "\t" + curblock.all_criteria[6] + "\t" + curblock.all_criteria[7] + "\t" +
                        curblock.all_criteria[7] + "\t" + curblock.signed_exp_mean + "\t" +//curblock.all_criteria[8]
                        curblock.signed_exp_mean_row_pos + "\t" + curblock.signed_exp_mean_row_neg + "\t" +
                        curblock.signed_exp_mean_col_pos + "\t" + curblock.signed_exp_mean_col_neg;

                summary.add(sum);
                first = false;
                //}
            }
            return critvals;
        }

        return null;
    }

    /**
     * @param curblockindex
     * @param sum
     * @param geneids
     * @param goids
     * @param golabels
     * @return
     */
    private String getGO(int curblockindex, String sum, String[] geneids, ArrayList goids, ArrayList golabels) {
        for (int g = 0; g < geneids.length; g++) {
            ArrayList thisgoid = new ArrayList();
            ArrayList thisgolab = new ArrayList();

            Object o = GO_yeast_data_map.get(geneids[g]);

            if (o != null) {
                ArrayList a = (ArrayList) o;
                for (int i = 0; i < a.size(); i++) {
                    //for (int t = 0; t < GO_yeast_data.length; t++) {
                    String[] row = (String[]) a.get(i);
                    try {
                        thisgoid.add(row[5]);
                        thisgolab.add(row[4]);
                        //System.out.println(row[4] + "\t" + row[5]);
                    } catch (Exception e) {
                    }
                }
            }
            //}
            if (thisgoid.size() > 0) {
                goids.add(MoreArray.ArrayListtoString(thisgoid));
                golabels.add(MoreArray.ArrayListtoString(thisgolab));
            } else {
                goids.add(empty);
                golabels.add(empty);
            }
        }
        sum = doGO(sum, geneids, goids, curblockindex);
        return sum;
    }

    /**
     * @param sum
     * @param geneids
     * @return
     */
    private String doTF(String sum, String[] geneids, int[] genes, int curblockindex) {
        ArrayList samps = new ArrayList();
        //ArrayList labs = new ArrayList();
        for (int g = 0; g < geneids.length; g++) {
            if (geneids[g] == null)
                System.out.println("geneids[g] is null " + g + "\t" + genes[g] + "\t" + rmb.expr_matrix.ylabels[genes[g] - 1]);
            else {
                for (int h = 0; h < TFREF_data.xlabels.length; h++) {
                    //System.out.println("refids[g] "+refids[g]);
                    //System.out.println("TFREF_data.xlabels[h] "+TFREF_data.xlabels[h]);

                    String s = (String) yeast_gene_names.get(geneids[g]);
                    if (s != null) {
                        if (TFREF_data.xlabels[h] == null)
                            System.out.println("TFREF_data.xlabels[h] is null");

                        if (geneids[g].equalsIgnoreCase(TFREF_data.xlabels[h]) || (s != null &&
                                s.equalsIgnoreCase(TFREF_data.xlabels[h]))) {

                            double[] data = Matrix.extractColumn(TFREF_data.data, h + 1);
                            //System.out.println("doTF " + h + " data " + MoreArray.toString(data));
                            samps.add(data);
                        }
                    } else {
                        //System.out.println("refids[g] not found in yeast_gene_names " + refids[g]);
                    }
                }
            }
        }

        //System.out.println("doTF " + samps.size());

        outTFprofile.add((String) block_ids.get(curblockindex));

        String[] tfids = TFREF_data.ylabels;//(String[])labs.MoreArray.ArrayListtoString(labs);
        if (tfids.length > 0) {
            outTFprofile.add(tfids);

            //System.out.println("tfids");
            // MoreArray.printArray(tfids);

            double[] meansamps = stat.arraySampMean(samps);

            String[] tfvals = MoreArray.toStringArray(meansamps);
            outTFprofile.add(tfvals);

            double max = stat.findMax(meansamps);
            int maxindex = MoreArray.getArrayInd(meansamps, max);

            sum += "\t" + tfids[maxindex] + "\t" + max;
            //System.out.println("doTF tfids " + MoreArray.toString(tfids));
        }


        return sum;
    }

    /**
     * @param sum
     * @param geneids
     * @param golabs
     * @return
     */
    private String doGO(String sum, String[] geneids, ArrayList golabs, int curblockindex) {
        HashMap gomap = new HashMap();
        for (int g = 0; g < geneids.length; g++) {
            //System.out.println("doGO ");
            //MoreArray.printArray((String[]) golabs.get(g));
            String[] cur = (String[]) golabs.get(g);
            if (!cur[0].equals("")) {
                for (int m = 0; m < cur.length; m++) {
                    Object o = gomap.get(cur[m]);
                    if (o == null) {
                        gomap.put(cur[m], 1);
                    }
                }
            }
        }

        Set set = gomap.keySet();
        Iterator iter = set.iterator();
        ArrayList keys = new ArrayList();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            if (key.length() > 0)
                keys.add(key);
        }
        keys.add("noGO");
        String[] keyStr = MoreArray.ArrayListtoString(keys);
        System.out.println("doGO keyStr " + MoreArray.toString(keyStr, ","));

        ArrayList keyStrC = new ArrayList();
        ArrayList keyStrF = new ArrayList();
        ArrayList keyStrP = new ArrayList();
        ArrayList keyStrlabel = new ArrayList();
        ArrayList keyStrClabel = new ArrayList();
        ArrayList keyStrFlabel = new ArrayList();
        ArrayList keyStrPlabel = new ArrayList();
        for (int i = 0; i < keyStr.length; i++) {
            keyStrlabel.add(GO_terms_map.get(keyStr[i]));
            if (keyStr[i].length() > 0) {
                //keyStrlabel.add(keyStr[i]);
                //System.out.println("keyStr[i] " + keyStr[i]);
                if (MoreArray.getArrayInd(GO_C, keyStr[i]) != -1) {
                    String val = (String) GO_terms_map.get(keyStr[i]);
                    //System.out.println("found GO_C " + keyStr[i] + "\t" + val);
                    keyStrC.add(keyStr[i]);
                    keyStrClabel.add(GO_terms_map.get(keyStr[i]));
                } else if (MoreArray.getArrayInd(GO_F, keyStr[i]) != -1) {
                    //System.out.println("found GO_F " + keyStr[i] + "\t" + GO_terms_map.get(keyStr[i]));
                    keyStrF.add(keyStr[i]);
                    keyStrFlabel.add(GO_terms_map.get(keyStr[i]));
                } else if (MoreArray.getArrayInd(GO_P, keyStr[i]) != -1) {
                    //System.out.println("found GO_P " + keyStr[i] + "\t" + GO_terms_map.get(keyStr[i]));
                    keyStrP.add(keyStr[i]);
                    keyStrPlabel.add(GO_terms_map.get(keyStr[i]));
                } else {
                    System.out.println("no GO class " + keyStr[i]);

                }
            }
        }

        System.out.println("doGO all GO " + MoreArray.toString(keyStr, ","));
        System.out.println("doGO all GO labels " + MoreArray.toString(MoreArray.ArrayListtoString(keyStrlabel), ","));

        double[] gocount = extractGO(geneids, golabs, keyStr);

        String[] strC = MoreArray.ArrayListtoString(keyStrC);
        double[] goCcount = new double[0];
        if (strC.length > 0) {
            goCcount = extractGO(geneids, golabs, strC);
        }
        String[] strF = MoreArray.ArrayListtoString(keyStrF);
        double[] goFcount = new double[0];
        if (strF.length > 0) {
            goFcount = extractGO(geneids, golabs, strF);
        }
        String[] strP = MoreArray.ArrayListtoString(keyStrP);
        double[] goPcount = new double[0];
        if (strP.length > 0) {
            goPcount = extractGO(geneids, golabs, strP);
        }

        /*find max GO term proportion*/
        double[] data = stat.norm(gocount, stat.sumEntries(gocount));
        double[] dataC = new double[0];
        if (strC.length > 0) {
            dataC = stat.norm(goCcount, stat.sumEntries(goCcount));
        }
        double[] dataF = new double[0];
        if (strF.length > 0) {
            dataF = stat.norm(goFcount, stat.sumEntries(goFcount));
        }
        double[] dataP = new double[0];
        if (strP.length > 0) {
            dataP = stat.norm(goPcount, stat.sumEntries(goPcount));
        }

        System.out.println("GO terms and counts");
        ArrayList a = new ArrayList();
        String[] gocstr = MoreArray.toStringArray(gocount);
        a.add(gocstr);
        String[] datastr = MoreArray.toStringArray(data);
        a.add(datastr);
        a.add(keyStr);
        a.add(keyStrlabel);
        //System.out.println("lengths " + gocstr.length + "\t" + datastr.length + "\t" + keyStr.length + "\t" + keyStrlabel.size());
        MoreArray.printArray(a, -1);

        String[] stringsC = MoreArray.toStringArray(dataC);
        if (strC.length > 0) {
            System.out.println("GO_C terms and counts");
            ArrayList ac = new ArrayList();
            ac.add(MoreArray.toStringArray(goCcount));
            ac.add(stringsC);
            ac.add(keyStrC);
            ac.add(keyStrClabel);
            MoreArray.printArray(ac, -1);
        }

        String[] stringsF = MoreArray.toStringArray(dataF);
        if (strF.length > 0) {
            System.out.println("GO_F terms and counts");
            ArrayList af = new ArrayList();
            af.add(MoreArray.toStringArray(goFcount));
            af.add(stringsF);
            af.add(keyStrF);
            af.add(keyStrFlabel);
            MoreArray.printArray(af, -1);
        }

        String[] stringsP = MoreArray.toStringArray(dataP);
        if (strP.length > 0) {
            System.out.println("GO_P terms and counts");
            ArrayList ap = new ArrayList();
            ap.add(MoreArray.toStringArray(goPcount));
            ap.add(stringsP);
            ap.add(keyStrP);
            ap.add(keyStrPlabel);
            MoreArray.printArray(ap, -1);
        }


        outGOprofile.add((String) block_ids.get(curblockindex));

        String[] goids = StringUtil.join(MoreArray.ArrayListtoString(keyStrC), MoreArray.ArrayListtoString(keyStrF));
        goids = StringUtil.join(goids, MoreArray.ArrayListtoString(keyStrP));
        outGOprofile.add(goids);

        //System.out.println("goids");
        //MoreArray.printArray(goids);

        String[] govals = StringUtil.join(MoreArray.toStringArray(stat.norm(goCcount, stat.sumEntries(goCcount))),
                MoreArray.toStringArray(stat.norm(goFcount, stat.sumEntries(goFcount))));
        govals = StringUtil.join(govals, MoreArray.toStringArray(stat.norm(goPcount, stat.sumEntries(goPcount))));
        outGOprofile.add(govals);

        //artifically set noGO fraction to 0
        double noGOprop = data[data.length - 1];
        data[data.length - 1] = 0;
        double maxGO = stat.findMax(data);
        int maxAll = MoreArray.getArrayInd(data, maxGO);

        int maxAllC = -1;
        if (dataC.length > 0) {
            double maxGOC = stat.findMax(dataC);
            maxAllC = MoreArray.getArrayInd(dataC, maxGOC);
        }
        int maxAllF = -1;
        if (dataF.length > 0) {
            double maxGOF = stat.findMax(dataF);
            maxAllF = MoreArray.getArrayInd(dataF, maxGOF);
        }
        int maxAllP = -1;
        if (dataP.length > 0) {
            double maxGOP = stat.findMax(dataP);
            maxAllP = MoreArray.getArrayInd(dataP, maxGOP);
        }


        System.out.println("doGO maxGO " + maxGO + "\t" + maxAll + "\t" + keyStr[maxAll]);
        if (maxAllC != -1)
            System.out.println("doGO C maxGO " + maxGO + "\t" + maxAllC + "\t" + strC[maxAllC]);
        if (maxAllF != -1)
            System.out.println("doGO F maxGO " + maxGO + "\t" + maxAllF + "\t" + strF[maxAllF]);
        if (maxAllP != -1)
            System.out.println("doGO P maxGO " + maxGO + "\t" + maxAllP + "\t" + strP[maxAllP]);


        String curgolab = "";
        double ftest = Double.NaN;
        double ftestnoGO = Double.NaN;
        //System.out.println("doGO max " + max + "\t" + gocount.length);
        if (maxAll != gocount.length - 1 && GO_yeast_data == null) {
            curgolab = assignGOData(keyStr[maxAll], curgolab);
        } else if (maxAll != gocount.length - 1) {
            for (int i = 0; i < keyStr.length; i++) {
                String godata = assignGOYeastData(keyStr[i], "");
                //System.out.println(i + "\t" + (i == max ? "max" : "no") + "\tproportion " + data[i] + "\t" + godata);
            }
            curgolab = assignGOYeastData(keyStr[maxAll], curgolab);
        } else {
            curgolab = "noGO";
        }
        sum += "\t" + maxGO + "\t" + ftest + "\t\"" + curgolab + "\"\t" + noGOprop + "\t" + ftestnoGO;
        System.out.println("doGO curgolab " + curgolab);
        return sum;
    }

    /**
     * @param sum
     * @param geneids
     * @param pathlabs
     * @return
     */
    private String doPath(String sum, String[] geneids, ArrayList pathlabs, int curblockindex) {
        HashMap Pathmap = new HashMap();
        for (int g = 0; g < geneids.length; g++) {
            //System.out.println("doPath ");
            //MoreArray.printArray((String[]) Pathlabs.get(g));
            String[] cur = (String[]) pathlabs.get(g);
            if (!cur[0].equals("")) {
                for (int m = 0; m < cur.length; m++) {
                    Object o = Pathmap.get(cur[m]);
                    if (o == null) {
                        Pathmap.put(cur[m], 1);
                    }
                }
            }
        }

        Set set = Pathmap.keySet();
        Iterator iter = set.iterator();
        ArrayList keys = new ArrayList();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            keys.add(key);
        }
        keys.add("noPath");
        String[] keyStr = MoreArray.ArrayListtoString(keys);

        ArrayList keyStrC = new ArrayList();
        ArrayList keyStrlabel = new ArrayList();
        ArrayList keyStrClabel = new ArrayList();

        /*TODO fix errors in below code*/
        /*for (int i = 0; i < keyStr.length; i++) {
            keyStrlabel.add(pathmap.get(keyStr[i]));
            if (keyStr[i].length() > 0) {
                //keyStrlabel.add(keyStr[i]);
                //System.out.println("keyStr[i] " + keyStr[i]);
                if (MoreArray.getArrayInd(Path_C, keyStr[i]) != -1) {
                    String val = (String) pathmap.get(keyStr[i]);
                    //System.out.println("found Path_C " + keyStr[i] + "\t" + val);
                    keyStrC.add(keyStr[i]);
                    keyStrClabel.add(pathmap.get(keyStr[i]));
                } 
            }
        }

        System.out.println("doPath all Path " + MoreArray.toString(keyStr, ","));
        System.out.println("doPath all Path labels " + MoreArray.toString(MoreArray.ArrayListtoString(keyStrlabel), ","));

        double[] Pathcount = extractPath(refids, pathlabs, keyStr);

        String[] strC = MoreArray.ArrayListtoString(keyStrC);
        double[] PathCcount = new double[0];
        if (strC.length > 0) {
            PathCcount = extractPath(refids, pathlabs, strC);
        }

        *//**//*find max Path term proportion*//**//*
        double[] data = stat.roundUp(stat.mult(stat.norm(Pathcount, stat.sumEntries(Pathcount)), 100.0), 0);
        double[] dataC = new double[0];
        if (strC.length > 0) {
            dataC = stat.roundUp(stat.mult(stat.norm(PathCcount, stat.sumEntries(PathCcount)), 100.0), 0);
        }

        System.out.println("Path terms and counts");
        ArrayList a = new ArrayList();
        String[] Pathcstr = MoreArray.toStringArray(Pathcount);
        a.add(Pathcstr);
        String[] datastr = MoreArray.toStringArray(data);
        a.add(datastr);
        a.add(keyStr);
        a.add(keyStrlabel);
        //System.out.println("lengths " + Pathcstr.length + "\t" + datastr.length + "\t" + keyStr.length + "\t" + keyStrlabel.size());
        MoreArray.printArray(a, -1);

        String[] stringsC = MoreArray.toStringArray(dataC);
        if (strC.length > 0) {
            System.out.println("Path_C terms and counts");
            ArrayList ac = new ArrayList();
            ac.add(MoreArray.toStringArray(PathCcount));
            ac.add(stringsC);
            ac.add(keyStrC);
            ac.add(keyStrClabel);
            MoreArray.printArray(ac, -1);
        }

        outPathprofile.add((String) block_ids.get(curblockindex));

        String[] Pathids = MoreArray.ArrayListtoString(keyStrC);
        outPathprofile.add(Pathids);

        //System.out.println("Pathids");
        //MoreArray.printArray(Pathids);

        String[] Pathvals = MoreArray.toStringArray(stat.norm(PathCcount, stat.sumEntries(PathCcount)));
        outPathprofile.add(Pathvals);

        //artifically set noPath fraction to 0
        double noPathprop = data[data.length - 1];
        data[data.length - 1] = 0;
        double maxPath = stat.findMax(data);
        int maxAll = MoreArray.getArrayInd(data, maxPath);

        int maxAllC = -1;
        if (dataC.length > 0) {
            double maxPathC = stat.findMax(dataC);
            maxAllC = MoreArray.getArrayInd(dataC, maxPathC);
        }
        System.out.println("doPath maxPath " + maxPath + "\t" + maxAll + "\t" + keyStr[maxAll]);
        if (maxAllC != -1)
            System.out.println("doPath C maxPath " + maxPath + "\t" + maxAllC + "\t" + strC[maxAllC]);
  
        String curPathlab = "";
        double ftest = Double.NaN;
        double ftestnoPath = Double.NaN;
        //System.out.println("doPath max " + max + "\t" + Pathcount.length);
        if (maxAll != Pathcount.length - 1 && Path_yeast_data == null) {
            curPathlab = assignPathData(keyStr[maxAll], curPathlab);
        } else if (maxAll != Pathcount.length - 1) {
            for (int i = 0; i < keyStr.length; i++) {
                String Pathdata = assignPathYeastData(keyStr[i], "");
                //System.out.println(i + "\t" + (i == max ? "max" : "no") + "\tproportion " + data[i] + "\t" + Pathdata);
            }
            curPathlab = assignPathYeastData(keyStr[maxAll], curPathlab);
        } else {
            curPathlab = "noPath";
        }
        sum += "\t" + maxPath + "\t" + ftest + "\t" + curPathlab + "\t" + noPathprop + "\t" + ftestnoPath;
        System.out.println("doPath curPathlab " + curPathlab);*/
        return sum;
    }

    /**
     * @param geneids
     * @param golabs
     * @param keyStr
     * @return
     */
    private double[] extractGO(String[] geneids, ArrayList golabs, String[] keyStr) {
        double[] gocount = new double[keyStr.length];

        for (int g = 0; g < keyStr.length; g++) {
            for (int h = 0; h < geneids.length; h++) {
                String[] s = (String[]) golabs.get(h);
                if (!s[0].equals("")) {
                    for (int f = 0; f < s.length; f++) {
                        if (s[f].indexOf(keyStr[g]) != -1)
                            gocount[g]++;
                    }
                }
            }
        }
        for (int h = 0; h < geneids.length; h++) {
            String[] cur = (String[]) golabs.get(h);
            if (cur[0].equals("")) {
                gocount[gocount.length - 1]++;
            }
        }
        return gocount;
    }

    /**
     * @param s
     * @param curgolab
     * @return
     */
    private String assignGOYeastData(String s, String curgolab) {
        for (int t = 0; t < GO_yeast_data.length; t++) {
            //System.out.println("assignGOYeastData " + s + "\t" + GO_yeast_data[t][0]);
            if (GO_yeast_data[t][5].equals(s)) {
                try {
                    if (curgolab.length() == 0)
                        curgolab += " ";
                    String s1 = GO_yeast_data[t][4] + " " + GO_yeast_data[t][5];
                    if (curgolab.indexOf(s1) == -1)
                        curgolab += s1;
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            }
        }
        /*irv.Rengine.assign("data", passdata);
  irv.Rexpr = irv.Rengine.eval("fisher.test(data, alternative="greater")");
  ftest = irv.Rexpr.asDouble();*/
        return curgolab;
    }

    /**
     * @param s
     * @param curgolab
     * @return
     */
    private String assignGOData(String s, String curgolab) {
        for (int t = 0; t < GO_data.length; t++) {
            if (GO_data[t][0].equals(s)) {
                try {
                    curgolab = GO_data[t][2] + " " + GO_data[t][3];
                    try {
                        curgolab += "\t" + GO_data[t][4];
                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
                    break;
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            }
        }
        if (curgolab.equals("")) {
            for (int t = 0; t < GO_data.length; t++) {
                if (GO_data[t][1].indexOf(s) != -1) {
                    try {
                        curgolab = GO_data[t][2] + " " + GO_data[t][3];
                        try {
                            curgolab += "\t" + GO_data[t][4];
                        } catch (Exception e) {
                            //e.printStackTrace();
                        }
                        break;
                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
                }
            }
        }

        /*irv.Rengine.assign("data", passdata);
  irv.Rexpr = irv.Rengine.eval("fisher.test(data, alternative="greater")");
  ftest = irv.Rexpr.asDouble();*/
        return curgolab;
    }

    /**
     * @param sum
     * @param geneids
     * @param TIGRlabs
     * @return
     */
    private String doTIGR(String sum, String[] geneids, ArrayList TIGRlabs, int curblockindex) {
        HashMap tigrmap = new HashMap();
        //make unique list of TIGR ids for these genes
        for (int g = 0; g < geneids.length; g++) {
            String cur = (String) TIGRlabs.get(g);
            if (cur != null && !cur.equals("")) {
                Object o = tigrmap.get(cur);
                if (o == null) {
                    tigrmap.put(cur, 1);
                }
            }
        }

        Set set = tigrmap.keySet();
        Iterator iter = set.iterator();
        ArrayList keys = new ArrayList();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            keys.add(key);
        }
        keys.add("noTIGR");
        String[] keyStr = MoreArray.ArrayListtoString(keys);

        System.out.println(MoreArray.toString(keyStr, ","));
        double[] tigrcount = new double[keyStr.length];
        //for each TIGR id and each gene and a single gene-TIGR assignment, increment the TIGR id count
        for (int g = 0; g < keyStr.length; g++) {
            for (int h = 0; h < geneids.length; h++) {
                String s = (String) TIGRlabs.get(h);
                if (s != null && !s.equals("")) {
                    if (s.indexOf(keyStr[g]) != -1)
                        tigrcount[g]++;
                }
            }
        }

        //count cases of no TIGR assignment
        for (int h = 0; h < geneids.length; h++) {
            String cur = (String) TIGRlabs.get(h);
            if (cur != null && cur.equals("")) {
                tigrcount[tigrcount.length - 1]++;
            }
        }

        //normalize by the total number of genes
        double[] data = stat.norm(tigrcount, (double) geneids.length);
        //artifically set noGO fraction to 0
        double noTIGRprop = data[data.length - 1];
        data[data.length - 1] = 0;
        double maxproportion = stat.findMax(data);
        int max = MoreArray.getArrayInd(data, maxproportion);
        System.out.println(maxproportion + "\t" + max + "\t" + keyStr[max]);
        String curTIGRlab = "";
        double ftest = Double.NaN;
        double ftestnoTIGR = Double.NaN;
        if (max != tigrcount.length - 1) {
            for (int t = 0; t < TIGR_data.length; t++) {
                if (TIGR_data[t][1] != null && TIGR_data[t][1].equals(keyStr[max])) {
                    try {
                        curTIGRlab = TIGR_data[t][1];
                        break;
                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
                }
            }
            //if didn't find, search for indexOf
            if (curTIGRlab.equals("")) {
                for (int t = 0; t < TIGR_data.length; t++) {
                    if (TIGR_data[t][1] != null && TIGR_data[t][1].indexOf(keyStr[max]) != -1) {
                        try {
                            curTIGRlab = TIGR_data[t][1];
                            System.out.println("WARNING matched TIGR indexOf " + keyStr[max] + "\t" + TIGR_data[t][1]);
                            break;
                        } catch (Exception e) {
                            //e.printStackTrace();
                        }
                    }
                }
            }

            /*irv.Rengine.assign("data", passdata);
            irv.Rexpr = irv.Rengine.eval("fisher.test(data, alternative="greater")");
            ftest = irv.Rexpr.asDouble();*/
        } else {
            curTIGRlab = "noTIGR";
        }

        outTIGRprofile.add((String) block_ids.get(curblockindex));
        outTIGRprofile.add(keyStr);
        String[] vals = MoreArray.toStringArray(data);
        outTIGRprofile.add(vals);

        sum += "\t" + maxproportion + "\t" + ftest + "\t\"" + curTIGRlab + "\"\t" + noTIGRprop + "\t" + ftestnoTIGR;
        return sum;
    }

    /**
     * @param sum
     * @param geneids
     * @param TIGRrolelabs
     * @return
     */

    private String doTIGRroles(String sum, String[] geneids, ArrayList TIGRrolelabs, int curblockindex) {
        HashMap tigrmap = new HashMap();
        for (int g = 0; g < geneids.length; g++) {
            String cur = (String) TIGRrolelabs.get(g);
            if (cur != null && !cur.equals("")) {
                Object o = tigrmap.get(cur);
                if (o == null) {
                    tigrmap.put(cur, 1);
                }
            }
        }

        Set set = tigrmap.keySet();
        Iterator iter = set.iterator();
        ArrayList keys = new ArrayList();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            keys.add(key);
        }
        keys.add("noTIGR");
        String[] keyStr = MoreArray.ArrayListtoString(keys);

        System.out.println("doTIGRroles");
        System.out.println(MoreArray.toString(keyStr, ","));

        double[] tigrcount = new double[keyStr.length];
        for (int g = 0; g < keyStr.length; g++) {
            for (int h = 0; h < geneids.length; h++) {
                String s = (String) TIGRrolelabs.get(h);
                if (s != null && !s.equals("")) {
                    if (s.indexOf(keyStr[g]) != -1)
                        tigrcount[g]++;
                }
            }
        }

        for (int h = 0; h < geneids.length; h++) {
            String cur = (String) TIGRrolelabs.get(h);
            if (cur != null && cur.equals("")) {
                tigrcount[tigrcount.length - 1]++;
            }
        }

        double[] data = stat.norm(tigrcount, (double) geneids.length);
        //artifically set noGO fraction to 0
        double noTIGRprop = data[data.length - 1];
        data[data.length - 1] = 0;
        double maxproportion = stat.findMax(data);
        int maxind = MoreArray.getArrayInd(data, maxproportion);
        System.out.println("doTIGRroles " + maxproportion + "\t" + maxind + "\t" + keyStr[maxind]);
        String curTIGRlab = "";
        double ftest = Double.NaN;
        double ftestnoTIGR = Double.NaN;
        if (maxind != tigrcount.length - 1) {
            for (int t = 0; t < TIGR_data.length; t++) {
                if (TIGR_data[t][2] != null && TIGR_data[t][2].equals(keyStr[maxind])) {
                    try {
                        curTIGRlab = TIGR_data[t][2];
                        break;
                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
                }
            }
            //if didn't find, search for indexOf
            if (curTIGRlab.equals("")) {
                for (int t = 0; t < TIGR_data.length; t++) {
                    if (TIGR_data[t][2] != null && TIGR_data[t][2].indexOf(keyStr[maxind]) != -1) {
                        try {
                            curTIGRlab = TIGR_data[t][2];
                            System.out.println("WARNING matched TIGR indexOf " + keyStr[maxind] + "\t" + TIGR_data[t][2]);
                            break;
                        } catch (Exception e) {
                            //e.printStackTrace();
                        }
                    }
                }
            }

            /*irv.Rengine.assign("data", passdata);
            irv.Rexpr = irv.Rengine.eval("fisher.test(data, alternative="greater")");
            ftest = irv.Rexpr.asDouble();*/
        } else {
            curTIGRlab = "noTIGR";
        }

        outTIGRroleprofile.add((String) block_ids.get(curblockindex));
        outTIGRroleprofile.add(keyStr);
        String[] vals = MoreArray.toStringArray(data);
        outTIGRroleprofile.add(vals);

        sum += "\t" + maxproportion + "\t" + ftest + "\t\"" + curTIGRlab + "\"\t" + noTIGRprop + "\t" + ftestnoTIGR;
        System.out.println("doTIGRrole curTIGRlab " + curTIGRlab);
        return sum;
    }

    /**
     * @param args
     */
    private void init(String[] args) {
        MoreArray.printArray(args);

        options = MapArgOptions.maptoMap(args, valid_args);

        String inBIC = null;
        if (options.get("-debug") != null) {
            String f = (String) options.get("-debug");
            if (f.equalsIgnoreCase("y"))
                debug = true;
            else if (f.equalsIgnoreCase("n"))
                debug = false;
        }
        if (options.get("-abs") != null) {
            String f = (String) options.get("-abs");
            if (f.equalsIgnoreCase("C"))
                abs_axis = "C";
            else if (f.equalsIgnoreCase("R"))
                abs_axis = "R";
        }
        if (options.get("-incr") != null) {
            String f = (String) options.get("-incr");
            if (f != null)
                offsetincr = 1;
        }
        if (options.get("-bic") != null) {
            inBIC = (String) options.get("-bic");
            try {
                BIC = ValueBlockListMethods.readAny(inBIC, false);
                //BIC = ValueBlockListMethods.incrementIndices(BIC, offsetincr);
            } catch (Exception e) {
                e.printStackTrace();
            }
            outprefix = inBIC.substring(0, inBIC.lastIndexOf(".")) + "_";
        }
        if (options.get("-tab") != null) {
            annot = true;
            String f = (String) options.get("-tab");
            tab_data = TabFile.readtoArray(f);

            TIGR_data = new String[tab_data.length][3];
            TIGRmap = new HashMap();
            TIGRrolemap = new HashMap();
            int nullcount = 0;
            for (int i = 1; i < tab_data.length; i++) {
                TIGR_data[i][0] = tab_data[i][7];
                TIGR_data[i][1] = tab_data[i][13];

                //System.out.println("TIGRmap " + tab_data[i][7] + "\t" + tab_data[i][13]);
                TIGRmap.put(tab_data[i][7], tab_data[i][13]);
                TIGRmap.put(tab_data[i][0], tab_data[i][13]);

                if (tab_data[i][14] != null) {
                    int ind = tab_data[i][14].indexOf(":");
                    String s = tab_data[i][14];
                    if (ind != -1)
                        s = tab_data[i][14].substring(0, ind);
                    TIGR_data[i][2] = s;
                    int index = tab_data[i][14].indexOf(":");
                    String s2 = tab_data[i][14];
                    if (index != -1)
                        s2 = tab_data[i][14].substring(0, index);
                    //System.out.println("TIGRrolemap " + tab_data[i][7] + "\t" + s2);
                    TIGRrolemap.put(tab_data[i][7], s2);
                    TIGRrolemap.put(tab_data[i][0], s2);
                } else {
                    nullcount++;
                    //System.out.println("TIGRrolemap null field " + MoreArray.toString(tab_data[i], ","));
                }
            }
            System.out.println("init TIGRrolemap null count " + nullcount);
        }
        if (options.get("-GOT") != null) {
            annot = true;
            String f = (String) options.get("-GOT");
            GO_terms_data = TabFile.readtoArray(f);
            for (int i = 0; i < GO_terms_data.length; i++) {
                String pad = StringUtil.padLeft(GO_terms_data[i][0], 7, "0");//- GO_terms_data[i][0].length()
                String key = "GO:" + pad;
                //System.out.println("GO terms adding " + key + "\t" + GO_terms_data[i][1]);
                if (!GO_terms_data[i][1].equals("other") && !GO_terms_data[i][1].equals("not_yet_annotated"))
                    GO_terms_map.put(key, GO_terms_data[i][1]);
            }
        }
        if (options.get("-go") != null) {
            annot = true;
            String f = (String) options.get("-go");
            GO_data = TabFile.readtoArray(f);
        }
        if (options.get("-goyeast") != null) {
            annot = true;
            String f = (String) options.get("-goyeast");
            GO_yeast_data = TabFile.readtoArray(f);

            for (int i = 0; i < GO_yeast_data.length; i++) {
                Object n = yeast_gene_names.get(GO_yeast_data[i][0]);
                if (n == null) {
                    //System.out.println("adding GO_yeast_data " + GO_yeast_data[i][0] + "\t" + GO_yeast_data[i][1]);
                    yeast_gene_names.put(GO_yeast_data[i][0], GO_yeast_data[i][1]);
                }
                Object o = GO_yeast_data_map.get(GO_yeast_data[i][0]);
                if (o == null) {
                    String[] add = BuildGraph.removeGO(GO_yeast_data[i]);
                    GO_yeast_data_map.put(GO_yeast_data[i][0], add);
                    //GO_anot_map.put(GO_yeast_data[i][0],(GO_yeast_data[i][2] + GO_yeast_data[i][3]));
                } else {
                    boolean islist = false;
                    ArrayList a = null;
                    try {
                        a = (ArrayList) o;
                        islist = true;
                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
                    //if not list convert to list and add next
                    if (!islist) {
                        ArrayList ar = new ArrayList();
                        String[] add = BuildGraph.removeGO(GO_yeast_data[i]);
                        //ar.add(o);
                        ar.add(add);
                        GO_yeast_data_map.put(GO_yeast_data[i][0], ar);
                    }
                    //if list add to it
                    else {
                        String[] add = BuildGraph.removeGO(GO_yeast_data[i]);
                        a.add(add);
                        GO_yeast_data_map.put(GO_yeast_data[i][0], a);
                    }
                }

                if (GO_yeast_data[i][5].length() > 0)
                    //System.out.println("GO_yeast_data[i][3] " + GO_yeast_data[i][3]);
                    if (GO_yeast_data[i][3].equals("C")) {
                        int ind = MoreArray.getArrayInd(GO_C, GO_yeast_data[i][5]);
                        if (ind == -1) {
                            GO_C.add(GO_yeast_data[i][5]);
                            //System.out.println("adding GO_C " + GO_yeast_data[i][5]);
                        }
                    } else if (GO_yeast_data[i][3].equals("F")) {
                        int ind = MoreArray.getArrayInd(GO_F, GO_yeast_data[i][5]);
                        if (ind == -1) {
                            GO_F.add(GO_yeast_data[i][5]);
                            //System.out.println("adding GO_F " + GO_yeast_data[i][5]);
                        }
                    } else if (GO_yeast_data[i][3].equals("P")) {
                        int ind = MoreArray.getArrayInd(GO_P, GO_yeast_data[i][5]);
                        if (ind == -1) {
                            GO_P.add(GO_yeast_data[i][5]);
                            //System.out.println("adding GO_P " + GO_yeast_data[i][5]);
                        }
                    }
            }
        }


        if (options.get("-PATH") != null) {
            annot = true;
            String f = (String) options.get("-PATH");
            path_data = TabFile.readtoArray(f);

            pathmap = new HashMap();
            for (int i = 0; i < path_data.length; i++) {
                String cur = path_data[i][3];
                Object o = pathmap.get(cur);
                if (o == null)
                    pathmap.put(cur, path_data[i][0]);
            }
        }
        if (options.get("-geneids") != null) {
            try {
                String[][] sarray = TabFile.readtoArray((String) options.get("-geneids"));
                System.out.println("setLabels g " + sarray.length + "\t" + sarray[0].length);
                int col = 2;
                String[] n = MoreArray.extractColumnStr(sarray, col);
                gene_labels = MoreArray.replaceAll(n, "\"", "");
                System.out.println("setLabels gene " + gene_labels.length + "\t" + gene_labels[0]);
            } catch (Exception e) {
                System.out.println("expecting 2 cols");
                //e.printStackTrace();
                try {
                    String[][] sarray = TabFile.readtoArray((String) options.get("-geneids"));
                    int col = 1;
                    String[] n = MoreArray.extractColumnStr(sarray, col);
                    gene_labels = MoreArray.replaceAll(n, "\"", "");
                    System.out.println("setLabels gene " + gene_labels.length + "\t" + gene_labels[0]);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

            }
        }
        if (options.get("-PPI") != null) {
            String f = (String) options.get("-PPI");
            interact_data = new SimpleMatrix(f);
        }
        if (options.get("-TFREF") != null) {
            annot = true;
            String f = (String) options.get("-TFREF");
            //TFREF_data = TabFile.readtoArray(f);
            TFREF_data = new SimpleMatrix(f);

            /*System.out.println("TFREF_data.xlabels");
            MoreArray.printArray(TFREF_data.xlabels);
            System.out.println("TFREF_data.ylabels");
            MoreArray.printArray(TFREF_data.ylabels);*/
        }

        /*if (options.get("-tigr") != null) {
            String f = (String) options.get("-tigr");
            TIGR_data = TabFile.readtoArray(f);
        }*/

        if (options.get("-crits") != null) {
            CRITlabels = ((String) options.get("-crits")).split(",");

            System.out.println("CRITlabels");
            System.out.println(CRITlabels);
        }

        if (options.get("-param") != null) {

            String s = (String) options.get("-param");
            rmb = new RunMinerBack(s, sysRes, 3, false);
            //RunMinerBack rmb = new RunMinerBack(prm, sysRes, debug);

            //prm = new Parameters();
            //
            //prm.read(s);
            //prm.USE_MEAN = true;
            header = "#" + s + "\n";

            //Rcodepath = prm.R_METHODS_PATH;

            //inputtxt = prm.EXPR_DATA_PATH;

            //inputR = prm.R_DATA_PATH;

            // outdir = prm.OUTDIR;
            //if (rmb.orig_prm.OUTDIR.equals(""))
            outdir = "./";

            //seed = prm.RANDOM_SEED;
            //debug = StringUtil.isTrueorYes(prm.debug);

            String gread = rmb.orig_prm.EXPR_DATA_PATH.substring(0, rmb.orig_prm.EXPR_DATA_PATH.indexOf(".txt")) +
                    "_geneids.txt";
            System.out.println("reading geneids " + gread);
            File testg = new File(gread);
            if (testg.exists()) {
                String[][] sarray = TabFile.readtoArray(gread);
                int col = 2;
                /*if (sarray[0].length == 1 && sarray[1].length == 2) {
                    col = 2;
                } else if (sarray[0].length > 1) {
                    col = 2;
                } else
                    col = 1;*/
                //gene_labels = MoreArray.removeEntry(MoreArray.replaceAll(MoreArray.extractColumnStr(sarray, col), "\"", ""), 1);
                gene_labels = MoreArray.replaceAll(MoreArray.extractColumnStr(sarray, col), "\"", "");
                System.out.println("init gene_labels " + gread + "\t" + col + "\t" +
                        gene_labels[0] + "\t" + sarray[0].length + "\t" + sarray[1].length);
            }
        }

        //create dirs if necessary
        File test = new File(outdir);
        if (!test.exists() && !test.isDirectory()) {
            test.mkdir();
        }

        //expr_data = new SimpleMatrix(rmb.orig_prm.EXPR_DATA_PATH);
        //System.out.println("expr_data " + expr_data.ylabels.length);
        //MoreArray.printArray(expr_data.ylabels);
        //genes
        datagenemax = rmb.expr_matrix.data.length;
        //exps
        dataexpmax = rmb.expr_matrix.data[0].length;

        if (BIC != null && abs_axis != null) {
            splitBlocksByExprMean(inBIC);
        }

        System.out.println("finished init");
    }

    /**
     * @param inBIC
     */
    private void splitBlocksByExprMean(String inBIC) {
        ValueBlockList orig = new ValueBlockList();

        System.out.println("splitBlocksByExprMean b/f split " + BIC.size());
        for (int i = 0; i < BIC.size(); i++) {
            orig.add(BIC.get(i));
        }
        for (int i = 0; i < orig.size(); i++) {
            if (i % 100 == 0)
                System.out.print(".");
            ValueBlock v = (ValueBlock) orig.get(i);
            ValueBlock[] get = BlockMethods.splitBlockByExprMean(v, rmb.expr_matrix.data, abs_axis,
                    rmb.orig_prm.PERCENT_ALLOWED_MISSING_GENES, rmb.orig_prm.PERCENT_ALLOWED_MISSING_EXP);

            get[0].trimNAN(rmb.expr_matrix.data, rmb.orig_prm.PERCENT_ALLOWED_MISSING_GENES, rmb.orig_prm.PERCENT_ALLOWED_MISSING_EXP, debug);
            get[1].trimNAN(rmb.expr_matrix.data, rmb.orig_prm.PERCENT_ALLOWED_MISSING_GENES, rmb.orig_prm.PERCENT_ALLOWED_MISSING_EXP, debug);

            boolean plusgood = false;
            boolean neggood = false;
            if (get[0].genes.length > rmb.orig_prm.IMIN && get[0].exps.length > rmb.orig_prm.JMIN) {
                plusgood = true;
            }
            if (get[1].genes.length > rmb.orig_prm.IMIN && get[1].exps.length > rmb.orig_prm.JMIN) {
                neggood = true;
            }

            if (plusgood)
                BIC.set(i, get[0]);
            else if (neggood)
                BIC.set(i, get[1]);

            if (plusgood && neggood)
                BIC.add(i + 1, get[1]);
        }
        System.out.println("splitBlocksByExprMean a/f split " + BIC.size());

        String header = "#";
        String data = BIC.toString(header + MINER_STATIC.HEADER_VBL);//ValueBlock.toStringShortColumns());
        ParsePath pp = new ParsePath(inBIC);
        String f = pp.getName() + "_split." + pp.getExt();
        TextFile.write(data, f);
        System.out.println("splitBlocksByExprMean wrote " + f);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length >= 4 && args.length % 2 == 0 && args.length <= 22) {
            ScoreBlocks rm = new ScoreBlocks(args);
        } else {
            System.out.println("ScoreBlocks version " + version);
            System.out.println("syntax: java DataMining.util.ScoreBlocks\n" +
                    "<-bic .bic list>\n" +
                    "<-param parameter file>\n" +
                    "<OPTIONAL -tab tab genome annotation file>\n" +
                    "<OPTIONAL -go tab GO file>\n" +
                    "<OPTIONAL (exclusive with -go) -goyeast tab GO file>\n" +
                    "<OPTIONAL -GOT tab GO terms file>\n" +
                    "<OPTIONAL -PATH tab pathway-gene mapping file>\n" +
                    "<OPTIONAL -PPI tab PPI file>\n" +
                    "<OPTIONAL -TFREF tab TF-gene mapping file>\n" +
                    "<OPTIONAL -crits comma delim criterion labels>\n" +
                    //"<OPTIONAL -tigr tab tigr file>\n" +
                    "<OPTIONAL -abs R or C split biclusters according to expression sign and by row or column axis, default null>\n" +
                    "<OPTIONAL -incr increment +1 gene/exp indices for 0 start>\n" +
                    "<OPTIONAL -geneids >\n" +
                    "<OPTIONAL -debug y/n>"
            );
        }
    }
}
