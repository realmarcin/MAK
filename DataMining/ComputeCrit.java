package DataMining;

import mathy.stat;
import org.rosuda.JRI.RList;
import util.MoreArray;

import java.util.ArrayList;

/**
 * Class to compute the criterion value for a block.
 * <p/>
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: Mar 1, 2011
 * Time: 10:19:15 PM
 */
public class ComputeCrit {

    /**
     For current set of criteria see MINER_STATIC.CRIT_LABELS + MAXTF

     */


    /**
     * @param irv
     * @param Ic
     * @param Jc
     * @param prm
     * @param criterion
     * @param gene_labels
     * @param feat_data   =  rmb.feat_matrix.data
     * @param debug
     * @return
     * @throws Exception
     */
    public final static double[][] compute(InitRandVar irv, int[] Ic, int[] Jc,
                                           Parameters prm, Criterion criterion,
                                           String[] gene_labels, int[][] feat_data, boolean debug) {//throws Exception

        //System.out.println("compute " + debug);
        return computeOne(irv, prm, criterion, gene_labels, feat_data, debug, Ic, Jc);
    }


    /**
     * @param irv
     * @param IcJc
     * @param prm
     * @param criterion
     * @param gene_labels
     * @param feat_data
     * @param debug
     * @return
     */
    public final static ArrayList computeList(InitRandVar irv, ArrayList IcJc,
                                              Parameters prm, Criterion criterion,
                                              String[] gene_labels, int[][] feat_data, boolean debug) {//throws Exception

        ArrayList ar = new ArrayList();
        for (int z = 0; z < IcJc.size(); z++) {
            int[][] get = (int[][]) IcJc.get(z);

            ar.add(computeOne(irv, prm, criterion, gene_labels, feat_data, debug, get[0], get[1]));
        }

        return ar;
    }

    /**
     * @param irv
     * @param prm
     * @param criterion
     * @param gene_labels
     * @param feat_data
     * @param debug
     * @param ic
     * @return
     */
    private static double[][] computeOne(InitRandVar irv, Parameters prm, Criterion criterion, String[] gene_labels, int[][] feat_data, boolean debug, int[] ic, int[] jc) {
        //System.out.println("compute " + debug);
        if (debug) {
            System.out.println("R: Ic<-c(" + MoreArray.toString(ic, ",") + ")");
            System.out.println("R: Jc<-c(" + MoreArray.toString(jc, ",") + ")");
        }
        boolean retjri = irv.Rengine.assign("Ic", ic);
        boolean retjri2 = irv.Rengine.assign("Jc", jc);

        //case for only MEAN criteria
        if ((MINER_STATIC.CRIT_LABELS[criterion.crit - 1].contains("MEDRMEAN") ||
                MINER_STATIC.CRIT_LABELS[criterion.crit - 1].contains("MEDCMEAN") ||
                MINER_STATIC.CRIT_LABELS[criterion.crit - 1].contains("MEAN"))
            //&& MINER_STATIC.CRIT_LABELS[criterion.crit - 1].indexOf("_") == -1
                ) {
            prm.USE_MEAN = true;
            if (debug)
                System.out.println("computeOne usemean bf " + prm.USE_MEAN + "\t" + criterion.usemean);
            criterion.usemean = true;
            if (debug)
                System.out.println("computeOne usemean af " + prm.USE_MEAN + "\t" + criterion.usemean);
        }

        if (debug)
            System.out.println("prm.USE_MEAN " + prm.USE_MEAN + "\t" + criterion.usemean);

        String rcall = buildCritCallTotal(criterion.neednull, criterion.usemean, 0, criterion);  //prm.USE_MEAN

        if (debug) {
            System.out.println("ComputeCrit compute " + prm.crit.crit);
            System.out.println("ComputeCrit rcall " + rcall);
        }
        irv.onv.setCurNull(ic, jc, criterion);

        if (debug) {
            //System.out.println("R: " + rcall);
            System.out.println("runCrit: " + ic.length + "\t" + jc.length + "\t" +
                    BlockMethods.IcJctoijID(ic, jc));
            System.out.println("R: " + rcall);
        }

       /* if (debug) {
            System.out.println("runCrit isFEM " + criterion.isFEM);
            if (irv.onv.nullRegData != null)
                System.out.println("runCrit nullRegData " +
                        MoreArray.toString(irv.onv.nullRegData, ","));
            System.out.println("runCrit isFeatureCrit " + criterion.isFeatureCrit);
            if (irv.onv.nullFeatData != null) {
                System.out.println("runCrit nullFeatData " +
                        MoreArray.toString(irv.onv.nullFeatData, ","));
                //System.out.println("nullFeatData " + irv.Rengine.eval("nullFeatData"));
            }
        }*/

        irv.Rexpr = irv.Rengine.eval(rcall);

        double[] crits = new double[9];
        double[] critsraw = new double[9];
        try {
            if (irv.Rexpr != null) {
                RList rList = irv.Rexpr.asList();
                if (rList != null) {
                    RList rl = rList;
                    crits = (rl.at(0)).asDoubleArray();
                    critsraw = (rl.at(1)).asDoubleArray();

                    /*if(debug) {
                         System.out.println("computeOne");
                         MoreArray.printArray(critsraw);
                         MoreArray.printArray(crits);
                    }*/
                    
                    /*if (debug) {
                        if (irv.TFtargetmap == null) {
                            System.out.println("TFtargetmap null");
                        }
                        if (irv.onv.meanTFNull == null) {
                            System.out.println("irv.onv.meanTFNull null");
                        }
                    }*/
                }
            }
        } catch (Exception e) {
            System.out.println("error");
            System.out.println("R: " + rcall);
            System.out.println("Ic " + MoreArray.toString(ic, ","));
            System.out.println("Jc " + MoreArray.toString(jc, ","));
            System.out.println("expr_data " + irv.Rengine.eval("expr_data[Ic,Jc]"));
            System.out.println("ERROR: runCrit returning null");
            try {
                System.out.println("R: nullMeanData<-c(" + MoreArray.toString(irv.onv.nullMeanData, ",") + ")");
            } catch (Exception e1) {
                //e.printStackTrace();
            }
            try {
                System.out.println("R: nullMSEData<-c(" + MoreArray.toString(irv.onv.nullMSEData, ",") + ")");
            } catch (Exception e1) {
                //e.printStackTrace();
            }
            try {
                System.out.println("R: nullRegData<-c(" + MoreArray.toString(irv.onv.nullRegData, ",") + ")");
            } catch (Exception e1) {
                //e.printStackTrace();
            }

            try {
                System.out.println("R: nullKendData<-c(" + MoreArray.toString(irv.onv.nullKendData, ",") + ")");
            } catch (Exception e1) {
                //e.printStackTrace();
            }
            try {
                System.out.println("R: nullCorData<-c(" + MoreArray.toString(irv.onv.nullCorData, ",") + ")");
            } catch (Exception e1) {
                //e.printStackTrace();
            }
            try {
                System.out.println("R: nullEucData<-c(" + MoreArray.toString(irv.onv.nullEucData, ",") + ")");
            } catch (Exception e1) {
                //e.printStackTrace();
            }
            try {
                System.out.println("R: nullInteractData<-c(" + MoreArray.toString(irv.onv.nullInteractData, ",") + ")");
            } catch (Exception e1) {
                //e.printStackTrace();
            }
            try {
                System.out.println("R: nullFeatData<-c(" + MoreArray.toString(irv.onv.nullFeatData, ",") + ")");
            } catch (Exception e1) {
                //e.printStackTrace();
            }
            try {
                System.out.println("R: nullTFData<-c(" + MoreArray.toString(irv.onv.nullTFData, ",") + ")");
            } catch (Exception e1) {
                //e.printStackTrace();
            }
            try {
                System.out.println("R: nullBinaryData<-c(" + MoreArray.toString(irv.onv.nullBinaryData, ",") + ")");
            } catch (Exception e1) {
                //e.printStackTrace();
            }
            //MoreArray.printArray(VBPInitial.exp_data);
            e.printStackTrace();
        }

        //TF criterion
        if (irv.TFtargetmap != null && criterion.isTFCrit) {//irv.onv.meanTFNull != null &&
            //System.out.println("computing TF");
            if (critsraw == null)
                critsraw = new double[9];
            critsraw[ValueBlock_STATIC.TF_IND] = TFCrit.getTF8merRank(ic, irv.TFtargetmap, gene_labels, debug);
            String minTFwnull = "pcauchy(" + critsraw[ValueBlock_STATIC.TF_IND] + ",nullTFData[1],nullTFData[2])";
            irv.Rexpr = irv.Rengine.eval(minTFwnull);
            if (debug)
                System.out.println("R: " + minTFwnull);
            try {
                if (crits == null)
                    crits = new double[9];
                crits[ValueBlock_STATIC.TF_IND] = irv.Rexpr.asDouble();

                if(debug) {
                    System.out.println("TF_data "+ValueBlock_STATIC.TF_IND);
                    System.out.println(crits[ValueBlock_STATIC.TF_IND]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //System.out.println("R Hamming raw " + critsraw[ValueBlock_STATIC.feat_IND]);
        //System.out.println("R Hamming p   " + crits[ValueBlock_STATIC.feat_IND]);

        //feature criterion
        if (feat_data != null && criterion.isFeatureCrit) {
            if (critsraw == null)
                critsraw = new double[9];

            if (crits == null)
                crits = new double[9];

            ArrayList pass = new ArrayList();
            pass.add(critsraw);
            pass.add(crits);

            double[][] data = computeFeatWithNull(irv, pass, ic, feat_data, debug);
            critsraw = data[0];//(double[]) pass.get(0);
            crits = data[1];//(double[]) pass.get(1);

            //critsraw[ValueBlock_STATIC.feat_IND] = data[0][0];
            //crits[ValueBlock_STATIC.feat_IND] = data[0][0];

            if(debug) {
               System.out.println("feat_data "+ValueBlock_STATIC.feat_IND);
               MoreArray.printArray(data[0]);
               MoreArray.printArray(data[1]);
            }
        } else if (debug) {
            System.out.println("ComputeCrit no feat " + (feat_data == null ? "no feat data" : "feat data") +
                    "\t" + criterion.isFeatureCrit);
        }

        if (debug) {
            System.out.println("criterion w null " + criterion.crit + "\t" + MINER_STATIC.CRIT_LABELS[criterion.crit - 1]);
            MoreArray.printArray(crits);
            System.out.println("criterion wo null " + criterion.crit + "\t" + MINER_STATIC.CRIT_LABELS[criterion.crit - 1]);
            MoreArray.printArray(critsraw);
        }

        double[][] ret = {crits, critsraw};
        return ret;
    }


    /**
     * @param data
     * @return
     */
    public final static double[][] computeFeatWithNull(InitRandVar irv, ArrayList data, int[] Ic, int[][] feat_data, boolean debug) {

        double[] critsraw = (double[]) data.get(0);
        double[] crits = (double[]) data.get(1);

        int[][] passdata = ValueBlockPre.getDataCore(Ic, feat_data);
        //invert Hamming distance
        double h = 1.0 / (stat.meanPairHamming(passdata) + 1.0);
        //System.out.println("Java Hamming raw " + h);
        critsraw[ValueBlock_STATIC.feat_IND] = h;//TFCrit.getTF8merRank(Ic, irv.TFtargetmap, gene_labels, debug);
        String s = "pcauchy(" + critsraw[ValueBlock_STATIC.feat_IND] + ",nullFeatData[1],nullFeatData[2])";
        irv.Rexpr = irv.Rengine.eval(s);
        if (debug)
            System.out.println("R: " + s);
        try {

            double hp = irv.Rexpr.asDouble();
            //System.out.println("Java Hamming p   " + hp);
            crits[ValueBlock_STATIC.feat_IND] = hp;
        } catch (Exception e) {
            e.printStackTrace();
        }


        double[][] ret = {crits, critsraw};
        return ret;
    }


    /**
     * @param critsraw
     * @return
     */
    public final static double[] computeFeat(double[] critsraw, int[] Ic, int[][] feat_data, boolean debug) {

        int[][] passdata = ValueBlockPre.getDataCore(Ic, feat_data);
        //invert Hamming distance
        double h = 1.0 / (stat.meanPairHamming(passdata) + 1.0);
        //System.out.println("Java Hamming raw " + h);
        critsraw[ValueBlock_STATIC.feat_IND] = h;//TFCrit.getTF8merRank(Ic, irv.TFtargetmap, gene_labels, debug);

        return critsraw;
    }

    /**
     * @param usenull
     * @param usemean
     * @param meanonly
     * @param thiscrit
     * @return
     */
    public final static String buildCritCallTotal(boolean[] usenull, boolean usemean,
                                                  int meanonly, Criterion thiscrit) {//int[] addindex, int geneorexp,
        //System.out.println("buildCritCallTotal " + MINER_STATIC.CRIT_LABELS[set - 1] + "\t" + set);
        //String s1 = "";
        //if (addindex != null && addindex.length > 0)
        //    s1 = MoreArray.toString(addindex, ",");

        String neednull_bool = "c(";
        for (int i = 0; i < usenull.length; i++) {
            neednull_bool += usenull[i] ? "TRUE" : "FALSE";
            if (i < usenull.length - 1)
                neednull_bool += ",";
        }
        neednull_bool += ")";

        String s = null;
        try {
            s = "Critp.final(" +
                    "Ic," +
                    "Jc," +
                    //"addindex=c(" + s1 + ")," +
                    //"geneorexp=" + geneorexp + "," +
                    //"expr_data," +
                    //"interact_data," +
                    "nullMeanData," +
                    "nullMSEData," +
                    "nullRegData," +
                    "nullBinaryData," +
                    "nullKendData," +
                    "nullCorData," +
                    "nullEucData," +
                    "nullSpearData," +
                    "nullInteractData," +
                    //"nullFeatData," +
                    //"feat_data," +
                    //"useNull=" + (usenull ? "TRUE" : "FALSE") + "," +
                    "useNull=" + neednull_bool + "," +
                    "ECindex=" + thiscrit.expr_mean_crit + "," +
                    "ERegindex=" + thiscrit.expr_reg_crit + "," +
                    "KendARCind=" + (thiscrit.KENDALLIndex) + "," +
                    "Corindex=" + thiscrit.CORIndex + "," +
                    "Eucindex=" + thiscrit.EUCIndex + "," +
                    "Spearindex=" + thiscrit.SPEARIndex + "," +
                    "UseExprMean=" + (meanonly == 1 || usemean ? "TRUE" : "FALSE") + "," +//|| thiscrit.expr_mean_axis != -1
                    "MeanARCind=" + (meanonly == 1 || usemean ? thiscrit.expr_mean_axis : -1) + "," +//usemean ?
                    "ARCind=" + thiscrit.expr_mse_axis + "," +
                    "RegARCind=" + thiscrit.expr_reg_axis + "," +
                    "BinaryARCind=" + thiscrit.binary_axis + "," +
                    "InterI=" + (thiscrit.isInteractCrit ? "TRUE" : "FALSE") + "," +
                    //"FeatI=" + (thiscrit.isFeatureCrit ? "TRUE" : "FALSE") + "," +
                    "Invert=" + thiscrit.isNoninvert + "," +
                    "meanOnly=" + meanonly + "," +
                    "useAbs=c(" + MoreArray.toString(thiscrit.useAbs, ",") + ")," +
                    "frxnsign=" + (thiscrit.useFrxnSign ? "T" : "F") + ")";
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        return s;
    }
}
