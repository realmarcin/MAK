package DataMining.util;

import DataMining.*;
import mathy.SimpleMatrix;
import util.MoreArray;
import util.Program;
import util.StringUtil;
import util.TextFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * For each pair of biclusters, score the bicluster formed by merging the pair.
 * <p/>
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: 3/11/14
 * Time: 12:07 PM
 */
public class ScoreMergedPairs extends Program {

    boolean debug = true;

    ValueBlockList vbl;
    java.lang.String[] gene_labels, exp_labels;
    //InitRandVar irv;

    HashMap overmap = new HashMap();
    SimpleMatrix expr;

    public Criterion criterion;
    int curCRIT;
    String curCRITlabel;

    public RunMinerBack rmb;

    AssignCrit assignCrit;

    public static String[] CRITlabels = {
            "MSEC_KendallC_GEECE"};

    public String dist = "min";

    /**
     * @param args
     */
    public ScoreMergedPairs(String[] args) {

        init(args);

        Set overmapSet = overmap.keySet();
        Iterator it = overmapSet.iterator();
        ValueBlockList out = new ValueBlockList();
        ValueBlockList out_intersect = new ValueBlockList();
        ArrayList outar = new ArrayList();

        while (it.hasNext()) {
            String key = (String) it.next();
            int index = key.indexOf("_");
            int i = Integer.parseInt(key.substring(0, index));
            int j = Integer.parseInt(key.substring(index + 1));
            //System.out.println("key " + key);
            double curval = Double.parseDouble((String) overmap.get(key));
            //for (int i = 0; i < vbl.size(); i++) {
            ValueBlock vi = (ValueBlock) vbl.get(i - 1);
            //for (int j = i + 1; j < vbl.size(); j++) {
            ValueBlock vj = (ValueBlock) vbl.get(j - 1);

            int[] commongenes_all = MoreArray.crossFirstIndex(vi.genes, vj.genes);
            int[] commonexps_all = MoreArray.crossFirstIndex(vi.exps, vj.exps);

            int[] commongenes = new int[0];
            try {
                commongenes = MoreArray.removeByVal(commongenes_all, new int[]{-1});
            } catch (Exception e) {
                e.printStackTrace();
            }
            int[] commonexps = new int[0];
            try {
                commonexps = MoreArray.removeByVal(commonexps_all, new int[]{-1});
            } catch (Exception e) {
                e.printStackTrace();
            }

            ValueBlock intersect = new ValueBlock();
            intersect.addGenes(commongenes);
            intersect.addExps(commonexps);

            ValueBlock merged = new ValueBlock();
            merged.addGenes(vi.genes);
            merged.addGenes(vj.genes);
            merged.addExps(vi.exps);
            merged.addExps(vj.exps);

            if (merged.exp_data == null) {
                merged.setDataAndMean(rmb.expr_matrix.data);
                merged.setSignedMean(rmb.expr_matrix.data);
            }
            merged.trimNAN(rmb.expr_matrix.data, MINER_STATIC.DEFAULT_percent_allowed_missing_genes, MINER_STATIC.DEFAULT_percent_allowed_missing_exps, false);

            if (merged.exps.length > 0) {
                double[] ret = computeCrit(merged.genes, merged.exps, i);
                System.out.println("values " + MoreArray.toString(ret));
                if (ret != null && ret.length > 0)
                    merged.updateAllCriteria(ret);
                else {
                    System.out.println("crit failed for " + MoreArray.toString(merged.genes, ",") +
                            "\t" + MoreArray.toString(merged.exps, ","));
                }
            } else {
                System.out.println("too few exps or genes " + merged.exps.length + "\t" + merged.genes.length);
            }

            double viexpcrit = (vi.all_criteria[1] + vi.all_criteria[2] + vi.all_criteria[3]) / 3.0;
            double vjexpcrit = (vj.all_criteria[1] + vj.all_criteria[2] + vj.all_criteria[3]) / 3.0;
            double scoremerged = (merged.all_criteria[1] + merged.all_criteria[2] + merged.all_criteria[3]) / 3.0;

            double intersectval = (double) commongenes.length * commonexps.length;
            double mergedarea = (double) merged.genes.length * (double) merged.exps.length;
            double meanscore = Math.min(viexpcrit, vjexpcrit);// ((viexpcrit + vjexpcrit) / 2.0);

            double scoreratio = scoremerged / meanscore;
            String s = key + "\t" + meanscore + "\t" + intersect + "\t" + viexpcrit + "\t" + vjexpcrit + "\t" +
                    mergedarea + "\t" + scoremerged + "\t" + (intersectval / mergedarea) + "\t" + scoreratio;

            System.out.println("score " + (scoreratio < 1 ? "yes" : "no") + "\t" + s);
            outar.add(s);

            if (scoreratio < 1) {
                out.add(merged);
                out_intersect.add(intersect);
            }

        }

        if (out.size() > 0) {
            String stoplist = out.toString(MINER_STATIC.HEADER_VBL);// ValueBlock.toStringShortColumns());
            String outpath = args[0] + "_toplist_mergedeffect_" + dist + ".txt";
            util.TextFile.write(stoplist, outpath);
            System.out.println("wrote toplist_mergedeffect " + outpath);

            String stoplist2 = out_intersect.toString(MINER_STATIC.HEADER_VBL);// ValueBlock.toStringShortColumns());
            String outpath3 = args[0] + "_toplist_intersect_" + dist + ".txt";
            util.TextFile.write(stoplist2, outpath3);
            System.out.println("wrote toplist_mergedeffect " + outpath3);

            String outpath2 = args[0] + "_data_mergedeffect_" + dist + ".txt";
            TextFile.write(outar, outpath2);
            System.out.println("wrote data_mergedeffect " + outpath2);
        }

    }

    /**
     *
     */
    private double[] computeCrit(int[] Ic, int[] Jc, int curblockindex) {

        //System.out.print(".");
        if (debug) {
            System.out.println("computeCrit " + criterion.crit + "\t" + criterion.isGEE);
            System.out.println("R: Ic<-c(" + MoreArray.toString(Ic, ",") + ")");
            System.out.println("R: Jc<-c(" + MoreArray.toString(Jc, ",") + ")");
            //System.out.println("R: nullRegData <- NULL");
        }

        //remove last 2 exps for GEE and LARS if all exps part of block
        /*if ((criterion.isGEE || criterion.isLARS) && Jc.length == expr_data.data[0].length) {
            Jc = MoreArray.remove(Jc, Jc.length - 1);
            Jc = MoreArray.remove(Jc, Jc.length - 1);
            System.out.println("WARNING GEE/LARS: # of columns = # of experiments. Removing last 2 experiments.");
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
            if (rmb.feat_matrix == null)
                System.out.println("rmb.feat_matrix is null");
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
            double[] critvals = crits[0];
            double fullcrit = ValueBlock.computeFullCrit(critvals, true, passcrits, debug);

            if (debug) {
                System.out.println("fullcrit " + fullcrit);
                MoreArray.printArray(critvals);
            }

            ValueBlock curblock = new ValueBlock(Ic, Jc);
            curblock.setDataAndMean(rmb.expr_matrix.data);
            curblock.setSignedMean(rmb.expr_matrix.data);
            curblock.setSignedCountRow(rmb.expr_matrix.data);
            curblock.setSignedCountCol(rmb.expr_matrix.data);

            curblock.updateCrit(crits, true, passcrits, debug);
            return critvals;
        }

        return null;
    }

    /**
     * @param args
     */
    private void init(String[] args) {

        try {
            vbl = ValueBlockListMethods.readAny(args[1], false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String[] overdata = TextFile.readtoArray(args[0]);
        for (int i = 1; i < overdata.length; i++) {
            String[] now = overdata[i].split(" = ");
            //overmap.put(StringUtil.replace(now[0], " ((pp)) ", "_"), now[1]);
            //System.out.println("over " + StringUtil.replace(now[0], " ((pp)) ", "_") + "\t" + now[1]);
            overmap.put(StringUtil.replace(now[0], " ((pp)) ", "_"), now[1]);
        }


        expr = new SimpleMatrix(args[2]);

        rmb = new RunMinerBack(args[3], sysRes, 3, false);

        assignCrit = new AssignCrit(CRITlabels);

        curCRIT = assignCrit.CRITindex[0];
        curCRITlabel = CRITlabels[0];
        boolean useMean = assignCrit.CRITmean[0] == 1;
        if (useMean)
            rmb.orig_prm.USE_MEAN = true;
        else
            rmb.orig_prm.USE_MEAN = false;
        criterion = new Criterion(curCRIT, useMean, true,
                rmb.orig_prm.USE_ABS_AR, assignCrit.CRITTF[0] == 1 ? true : false, rmb.orig_prm.needinv, true, rmb.irv.prm.FRXN_SIGN, debug);
        System.out.println("doing " + curCRIT + "\t" + assignCrit.labels[0] +
                "\tisMeanCrit " + assignCrit.CRITmean[0] + "\tisTFCrit " + assignCrit.CRITTF[0]);

        rmb.orig_prm.crit = criterion;

    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 4) {
            ScoreMergedPairs rm = new ScoreMergedPairs(args);
        } else {
            System.out.println("syntax: java DataMining.func.FindCombRegbySite\n" +
                    "<overlap data>\n" +
                    "<vbl>\n" +
                    "<expression data>\n" +
                    "<parameter file>"
                    //"<gene labels>\n" +
                    //"<exp labels>"
            );
        }
    }
}
