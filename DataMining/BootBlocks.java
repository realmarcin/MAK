package DataMining;

import mathy.stat;
import util.MoreArray;
import util.Program;
import util.TextFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: 1/23/13
 * Time: 6:43 PM
 */
class BootBlocks extends Program {

    boolean debug = false;

    int N = 2;
    int M = 10000;

    public RunMinerBack rmb;
    private Criterion criterion;

    HashMap<String, double[]> stored = new HashMap<String, double[]>();

    /**
     * @param args
     */
    public BootBlocks(String[] args) {
        super();
        printTime();

        rmb = new RunMinerBack(args[1], sysRes, 0, false);

        criterion = new Criterion(rmb.orig_prm.crit.crit, rmb.orig_prm.USE_MEAN, true,
                rmb.orig_prm.USE_ABS_AR, rmb.orig_prm.MEANTF_PATH != null ? true : false, rmb.orig_prm.needinv, true, rmb.irv.prm.FRXN_SIGN, debug);

        Random rand = new Random(152666);
        try {
            ValueBlockList vbl = ValueBlockListMethods.readAny(args[0]);
            ArrayList out = new ArrayList();
            for (int i = 0; i < vbl.size(); i++) {
                long nowstartms = System.currentTimeMillis();
                System.out.println("" + i + "/" + vbl.size());
                ValueBlock current = (ValueBlock) vbl.get(i);
                current = scoreBlock(current);
                double testmean = current.full_criterion;
                //System.out.println("ref1 " + current.blockId() + "\t" + current.full_criterion);
                System.out.println("ref1 " + current.full_criterion);

                /*for resampling and recentering*/
                /*
                ValueBlock resamp = new ValueBlock(current);
                ArrayList datatest = new ArrayList();
                if (debug)
                    System.out.println("done N samples of current");
                //N subsamples of current
                for (int n = 0; n < N; n++) {
                    System.out.print("-");
                    resamp = BlockMethods.resampleBlock(resamp, rand);
                    resamp = scoreBlock(resamp);
                    datatest.add(resamp.full_criterion);
                }
                double[] datatestar = MoreArray.ArrayListtoDouble(datatest);
                System.out.println("datatestar");
                MoreArray.printArray(datatestar);
                double testmean = stat.avg(datatestar);
                double testsd = stat.SD(datatestar, testmean);*/

                double[] mean2 = null;
                String label = "" + current.genes.length + "_" + current.exps.length;
                Object o = stored.get(label);
                if (o == null) {
                    ArrayList databgmean = new ArrayList();
                    /*for resampling and recentering */
                    //ArrayList databgsd = new ArrayList();
                    //M samples of current
                    for (int m = 0; m < M; m++) {
                        System.out.print(".");
                        //System.out.println("createRandomBlock");
                        /*TODO  createRandomBlock broken? */
                        ValueBlock randsamp = BlockMethods.createRandomBlock(current.genes.length, current.exps.length,
                                rmb.expr_matrix.ylabels.length, rmb.expr_matrix.xlabels.length, rand);
                        //System.out.println("refforsamp " + current.blockId());

                        randsamp = scoreBlock(randsamp);
                        //System.out.println("sampfromref " + randsamp.blockId() + "\t" + randsamp.full_criterion);
                        //System.out.println("sampfromref " + randsamp.full_criterion);


                        /*for resampling and recentering*/
                    /*
                    ArrayList nowdata = new ArrayList();
                    //N subsamples of cursamp
                    for (int n = 0; n < N; n++) {
                        System.out.print(".");
                        ValueBlock resamp_randsamp = new ValueBlock(randsamp);
                        resamp_randsamp = BlockMethods.resampleBlock(resamp_randsamp, rand);
                        if (debug) {
                            System.out.println("ref " + current.blockId());
                            System.out.println("samp " + randsamp.blockId());
                            System.out.println("cur " + resamp_randsamp.blockId());
                        }
                        resamp_randsamp = scoreBlock(resamp_randsamp);
                        nowdata.add(resamp_randsamp.full_criterion);
                        System.out.println("resamp_samp.full_criterion " + m + "\t" + n + "\t" + resamp_randsamp.full_criterion);
                    }

                    double[] dataar = MoreArray.ArrayListtoDouble(nowdata);
                    double mean1 = stat.avg(dataar);
                    System.out.println("mean1");
                    MoreArray.printArray(dataar);
                    databgmean.add(mean1);
                                        databgsd.add(stat.SD(dataar, mean1));
                    */


                        databgmean.add(randsamp.full_criterion);
                    }
                    mean2 = MoreArray.ArrayListtoDouble(databgmean);
                } else {
                    mean2 = (double[]) o;
                }

                /*for resampling and recentering*/
                // double[] mean2 = MoreArray.ArrayListtoDouble(databgmean);
                //System.out.println("mean2");
                // MoreArray.printArray(mean2);
                //double meanmean = stat.avg(mean2);
                //double sd = stat.SD(mean2, meanmean);
                //double[] sd = MoreArray.ArrayListtoDouble(databgsd);

                double bgmean = stat.avg(mean2);
                double count = (double) stat.countGreaterThan(mean2, testmean);
                double ratio = count / (double) mean2.length;
                //String s = i + "\t" + count + "\tratio " + ratio + "\ttest " + testmean + "\tbg " + meanmean + "\t" + testsd + "\t" + sd;
                String s = i + "\t" + count + "\tratio " + ratio + "\ttest " + testmean + "\t" + bgmean + "\t" + count + "\t" + (count / M);
                System.out.println("done " + s);
                out.add(s);

                long nowendms = System.currentTimeMillis();

                System.out.println("done " + i + "\t" + (nowendms - nowstartms) / (1000) + " s");

            }

            TextFile.write(out, args[0] + "_bootblocks.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }

        endTime();
        printTime();
    }   /**
     * @param args
     */
    /*public BootBlocks(String[] args) {
        super();
        printTime();

        rmb = new RunMinerBack(args[1], sysRes, 0, false);

        criterion = new Criterion(rmb.orig_prm.crit.crit, rmb.orig_prm.USE_MEAN, true,
                rmb.orig_prm.USE_ABS_AR, rmb.orig_prm.MEANTF_PATH != null ? true : false, rmb.orig_prm.needinv, true, debug);

        Random rand = new Random(152666);
        try {
            ValueBlockList vbl = ValueBlockList.readAny(args[0]);
            ArrayList out = new ArrayList();
            for (int i = 0; i < vbl.size(); i++) {
                System.out.print("" + i + "/" + vbl.size());
                ValueBlock current = (ValueBlock) vbl.get(i);
                System.out.println("ref1 " + current.blockId());
                ValueBlock resamp = new ValueBlock(current);
                ArrayList datatest = new ArrayList();
                if (debug)
                    System.out.println("done N samples of current");
                //N subsamples of current
                for (int n = 0; n < N; n++) {
                    System.out.print("-");
                    resamp = BlockMethods.resampleBlock(resamp, rand);
                    resamp = scoreBlock(resamp);
                    datatest.add(resamp.full_criterion);
                }
                double[] datatestar = MoreArray.ArrayListtoDouble(datatest);
                System.out.println("datatestar");
                MoreArray.printArray(datatestar);
                double testmean = stat.avg(datatestar);
                double testsd = stat.SD(datatestar, testmean);

                ArrayList databgmean = new ArrayList();
                ArrayList databgsd = new ArrayList();
                //M samples of current
                for (int m = 0; m < M; m++) {
                    System.out.print(",");
                    //System.out.println("createRandomBlock");
                    *//*TODO  createRandomBlock broken? *//*
                    ValueBlock randsamp = BlockMethods.createRandomBlock(current.genes.length, current.exps.length,
                            rmb.expr_matrix.ylabels.length, rmb.expr_matrix.xlabels.length, rand);
                    System.out.println("refforsamp " + current.blockId());
                    System.out.println("sampfromref " + randsamp.blockId());
                    randsamp.setDataAndMean(rmb.expr_matrix.data);
                    ArrayList nowdata = new ArrayList();
                    //N subsamples of cursamp
                    for (int n = 0; n < N; n++) {
                        System.out.print(".");
                        ValueBlock resamp_randsamp = new ValueBlock(randsamp);
                        resamp_randsamp = BlockMethods.resampleBlock(resamp_randsamp, rand);
                        if (debug) {
                            System.out.println("ref " + current.blockId());
                            System.out.println("samp " + randsamp.blockId());
                            System.out.println("cur " + resamp_randsamp.blockId());
                        }
                        resamp_randsamp = scoreBlock(resamp_randsamp);
                        nowdata.add(resamp_randsamp.full_criterion);
                        System.out.println("resamp_samp.full_criterion " + m + "\t" + n + "\t" + resamp_randsamp.full_criterion);
                    }

                    double[] dataar = MoreArray.ArrayListtoDouble(nowdata);
                    double mean1 = stat.avg(dataar);
                    System.out.println("mean1");
                    MoreArray.printArray(dataar);
                    databgmean.add(mean1);
                    databgsd.add(stat.SD(dataar, mean1));
                }

                double[] mean2 = MoreArray.ArrayListtoDouble(databgmean);
                System.out.println("mean2");
                MoreArray.printArray(mean2);
                double meanmean = stat.avg(mean2);
                double sd = stat.SD(mean2, meanmean);
                //double[] sd = MoreArray.ArrayListtoDouble(databgsd);

                double count = (double) stat.countGreaterThan(mean2, testmean);
                double ratio = count / (double) mean2.length;
                String s = i + "\t" + count + "\tratio " + ratio + "\ttest " + testmean + "\tbg " + meanmean + "\t" + testsd + "\t" + sd;
                System.out.println("done " + s);
                out.add(s);
            }

            TextFile.write(out, args[0] + "_bootblocks.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }

        endTime();
        printTime();
    }*/

    /**
     * @param vblock
     * @return
     */
    private ValueBlock scoreBlock(ValueBlock vblock) {
        vblock.setDataAndMean(rmb.expr_matrix.data);

        double[] ret = computeCrit(vblock.genes, vblock.exps);
        if (debug)
            System.out.println("scoreBlock values " + MoreArray.toString(ret));
        if (ret != null && ret.length > 0) {
            //vblock.updateAllCriteria(ret);
            boolean[] passcrits = Criterion.getExprCritTypes(criterion, true, rmb.orig_prm.USE_MEAN, debug);
            vblock.updateCrit(ret, true, passcrits, debug);
            if (debug)
                System.out.println("scoreBlock " + vblock.full_criterion);
        }

        return vblock;
    }


    /**
     *
     */
    private double[] computeCrit(int[] Ic, int[] Jc) {

        //System.out.print(".");
        if (debug) {
            System.out.println("computeCrit " + criterion.crit + "\t" + criterion.isFEM);
            System.out.println("R: Ic<-c(" + MoreArray.toString(Ic, ",") + ")");
            System.out.println("R: Jc<-c(" + MoreArray.toString(Jc, ",") + ")");
            //System.out.println("R: nullRegData <- NULL");
        }

        if (Ic.length > rmb.orig_prm.IMAX && debug) {
            System.out.println("WARNING More genes than allowed " + Ic.length + " criterion based on max bounds.");
        }
        if (Jc.length > rmb.orig_prm.JMAX && debug) {
            System.out.println("WARNING More exps than allowed " + Jc.length + " criterion based on max bounds.");
        }
        //boolean[] passcrits = Criterion.getExprCritTypes(criterion, true, rmb.orig_prm.USE_MEAN, debug);
        double[][] crits = null;
        try {
            if (criterion == null)
                System.out.println("criterion is null");
            if (rmb.irv == null)
                System.out.println("irv is null");
            if (rmb.irv.onv == null)
                System.out.println("irv.onv is null");
            if (rmb.expr_matrix.ylabels == null)
                System.out.println("gene_labels is null");
            //if (rmb.feat_matrix == null)
            //    System.out.println("rmb.feat_matrix is null");
            if (rmb.orig_prm == null)
                System.out.println("rmb.orig_prm is null");

            crits = ComputeCrit.compute(rmb.irv, Ic, Jc,
                    rmb.orig_prm, criterion,
                    rmb.expr_matrix.ylabels, rmb.feat_matrix != null ? rmb.feat_matrix.data : null, debug);
        } catch (Exception e) {
            System.out.println("criterion compute failed");
            e.printStackTrace();
        }
        if (crits == null || crits.length == 0) {
            System.out.println("ERROR: crits == null");
            //System.exit(1);
        } else {
            //double origfull = curBlock.fullcrit;
            double[] critvals = crits[0];

            /*double fullcrit = ValueBlock.computeFullCrit(critvals, true, passcrits, debug);
            if (debug) {
                System.out.println("fullcrit " + fullcrit);
                MoreArray.printArray(critvals);
            }*/

            return critvals;
        }

        return null;
    }

    /**
     * @param args
     */
    public final static void main(String[] args) {

        if (args.length == 2) {
            BootBlocks rm = new BootBlocks(args);
        } else {
            System.out.println("syntax: java DataMining.BootBlocks\n" +
                    "<value block list>\n" +
                    "<parameter file>"
            );
        }
    }
}
