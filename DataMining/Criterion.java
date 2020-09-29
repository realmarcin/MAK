package DataMining;

import util.MoreArray;

/**
 * User: marcin
 * Date: May 13, 2010
 * Time: 8:17:23 PM
 */
public class Criterion {

    public boolean debug = false;
    public int crit = -1;
    /* Combine criterion with protein interactions? T/F */
    public boolean requires_null = true;
    public boolean isMSER = false;
    public boolean isMSE = false;
    public boolean isMSEC = false;
    public boolean isMAD = false;
    public int KENDALLIndex = -1;
    public boolean isGEE = false;
    public boolean isonlyGEE = false;
    public boolean isonlyLARS = false;
    public boolean isLARS = false;
    public boolean isGEERE = false;
    public boolean isLARSRE = false;
    public boolean isGEECE = false;
    public boolean isLARSCE = false;
    public int CORIndex = -1;
    public int EUCIndex = -1;
    public int SPEARIndex = -1;
    public boolean isTFCrit = false;

    public boolean isTotalCrit = false;
    public boolean isRowCrit = false;
    public boolean isColCrit = false;

    public boolean isInteractCrit = false;
    public boolean isFeatureCrit = false;

    public int expr_mse_axis = -1, expr_reg_axis = -1, expr_kend_axis = -1,
            expr_COR_axis = -1, expr_EUC_axis = -1, expr_spear_axis = -1, expr_mean_axis = -1, binary_axis = -1;
    public int expr_mean_crit = -1, isNoninvert = -1;
    public boolean nonull = false, isMeanCrit = false;
    public int expr_reg_crit = -1;

    public boolean[] which_expr_crits;
    public boolean weigh = false;
    public boolean usemean = false;
    public int[] useAbs = MoreArray.initArray(3, 1);
    public boolean useFrxnSign = false;
    public boolean[] forceinv;

    public boolean[] neednull;


    /**
     * @param c
     */
    public Criterion(Criterion c, boolean d) {
        if (debug)
            System.out.println("Criterion a " + weigh + "\t" + isTFCrit + "\tuse_mean " + c.usemean);
        debug = d;
        setParam(c);
    }

    /**
     * @param c
     */
    public Criterion(Criterion c, int[] a, boolean d) {
        useAbs = a;
        if (debug)
            System.out.println("Criterion b " + weigh + "\t" + isTFCrit + "\t" + MoreArray.toString(a, ",") + "\tuse_mean " + c.usemean);
        debug = d;
        setParam(c);
    }

    /**
     * @param c
     * @param d
     */
    public Criterion(int c, boolean m, boolean w, int[] a, boolean tf, boolean d) {
        usemean = m;

        if (c > 0 && MINER_STATIC.CRIT_LABELS[c - 1].indexOf("MEAN") != -1) {
            usemean = true;
        }

        weigh = w;
        useAbs = a;
        isTFCrit = tf;
        if (debug || d)
            System.out.println("Criterion 1 " + weigh + "\t" + isTFCrit + "\t" + MoreArray.toString(a, ",") + "\tusemean " + usemean);
        debug = d;
        init(c);
    }

    /**
     * @param c
     * @param m   usemean
     * @param w   weigh
     * @param a   useAbs
     * @param tf  tf
     * @param inv invert
     * @param d   debug
     */
    public Criterion(int c, boolean m, boolean w, int[] a, boolean tf, boolean[] inv, boolean n, boolean fs, boolean d) {

        if (debug || d)
            System.out.println("Criterion 2 c " + c + "\tusemean bf " + usemean + "\t" + m);

        usemean = m;

        if (c > 0 && MINER_STATIC.CRIT_LABELS[c - 1].indexOf("MEAN") != -1) {
            usemean = true;
            if (debug || d)
                System.out.println("Criterion 2 usemean af " + usemean + "\t" + m);
        }

        weigh = w;
        useAbs = a;
        isTFCrit = tf;
        if (debug || d)
            System.out.println("Criterion 2 " + weigh + "\tisTFCrit " + isTFCrit + "\t" + MoreArray.toString(a, ",") + "\tusemean " + usemean);
        forceinv = inv;
        //requires_null = n;
        useFrxnSign = fs;
        debug = d;
        init(c);
    }

    /**
     * IF any component criterion is 'nonull' then the whole criterion inherits 'nonull'
     */
    private void setNoNull() {
        neednull = new boolean[8];
        //MEAN
        neednull[0] = true;
        if (crit > 0 && (MINER_STATIC.CRIT_LABELS[crit - 1].indexOf("MEANnonull") != -1 ||
                MINER_STATIC.CRIT_LABELS[crit - 1].indexOf("MEDCMEANnonull") != -1 ||
                MINER_STATIC.CRIT_LABELS[crit - 1].indexOf("MEDRMEANnonull") != -1)) {
            neednull[0] = false;
            requires_null = false;
        }
        //MSE
        neednull[1] = true;
        if (crit > 0 && (MINER_STATIC.CRIT_LABELS[crit - 1].indexOf("MSEnonull") != -1 ||
                MINER_STATIC.CRIT_LABELS[crit - 1].indexOf("MSERnonull") != -1 ||
                MINER_STATIC.CRIT_LABELS[crit - 1].indexOf("MSECnonull") != -1)) {
            neednull[1] = false;
            requires_null = false;
        }

        //GEE
        neednull[2] = true;
        if (crit > 0 && (MINER_STATIC.CRIT_LABELS[crit - 1].indexOf("GEEnonull") != -1 ||
                MINER_STATIC.CRIT_LABELS[crit - 1].indexOf("GEECEnonull") != -1 ||
                MINER_STATIC.CRIT_LABELS[crit - 1].indexOf("GEEREnonull") != -1)) {
            neednull[2] = false;
            requires_null = false;
        }

        //Kendall
        neednull[3] = true;
        if (crit > 0 && (MINER_STATIC.CRIT_LABELS[crit - 1].indexOf("KENDALLnonull") != -1 ||
                MINER_STATIC.CRIT_LABELS[crit - 1].indexOf("KENDALLRnonull") != -1 ||
                MINER_STATIC.CRIT_LABELS[crit - 1].indexOf("KENDALLCnonull") != -1)) {
            neednull[3] = false;
            requires_null = false;
        }

        //COR
        neednull[4] = true;
        if (crit > 0 && (MINER_STATIC.CRIT_LABELS[crit - 1].indexOf("CORnonull") != -1 ||
                MINER_STATIC.CRIT_LABELS[crit - 1].indexOf("CORCnonull") != -1 ||
                MINER_STATIC.CRIT_LABELS[crit - 1].indexOf("CORRnonull") != -1)) {
            neednull[4] = false;
            requires_null = false;
        }
        //EUC
        neednull[5] = true;
        if (crit > 0 && (MINER_STATIC.CRIT_LABELS[crit - 1].indexOf("EUCnonull") != -1 ||
                MINER_STATIC.CRIT_LABELS[crit - 1].indexOf("EUCCnonull") != -1 ||
                MINER_STATIC.CRIT_LABELS[crit - 1].indexOf("EUCRnonull") != -1)) {
            neednull[5] = false;
            requires_null = false;
        }

        //SPEAR
        neednull[6] = true;
        if (crit > 0 && (MINER_STATIC.CRIT_LABELS[crit - 1].indexOf("SPEARMANnonull") != -1 ||
                MINER_STATIC.CRIT_LABELS[crit - 1].indexOf("SPEARMANCnonull") != -1 ||
                MINER_STATIC.CRIT_LABELS[crit - 1].indexOf("SPEARMANRnonull") != -1)) {
            neednull[6] = false;
            requires_null = false;
        }

        //Binary
        neednull[7] = true;
        if (crit > 0 && (MINER_STATIC.CRIT_LABELS[crit - 1].indexOf("Binarynonull") != -1 ||
                MINER_STATIC.CRIT_LABELS[crit - 1].indexOf("BinaryCnonull") != -1 ||
                MINER_STATIC.CRIT_LABELS[crit - 1].indexOf("BinaryRnonull") != -1)) {
            neednull[7] = false;
            requires_null = false;
        }
    }

    /**
     * @param c
     */
    private void setParam(Criterion c) {
        crit = c.crit;
        requires_null = c.requires_null;
        isMSER = c.isMSER;
        isMSE = c.isMSE;
        isMSEC = c.isMSEC;
        isMAD = c.isMAD;
        KENDALLIndex = c.KENDALLIndex;
        isGEE = c.isGEE;
        isonlyGEE = c.isonlyGEE;
        isonlyLARS = c.isonlyLARS;
        isLARS = c.isLARS;
        isGEERE = c.isGEERE;
        isLARSRE = c.isLARSRE;
        isGEECE = c.isGEECE;
        isLARSCE = c.isLARSCE;
        CORIndex = c.CORIndex;
        EUCIndex = c.EUCIndex;
        SPEARIndex = c.SPEARIndex;
        isTotalCrit = c.isTotalCrit;
        isRowCrit = c.isRowCrit;
        isColCrit = c.isColCrit;
        isMeanCrit = c.isMeanCrit;

        if (debug) {
            System.out.println("setParam isMeanCrit " + isMeanCrit);
        }

        isTFCrit = c.isTFCrit;
        isInteractCrit = c.isInteractCrit;
        isFeatureCrit = c.isFeatureCrit;

        expr_mean_crit = c.expr_mean_crit;
        expr_mse_axis = c.expr_mse_axis;
        expr_reg_axis = c.expr_reg_axis;
        expr_kend_axis = c.expr_kend_axis;
        expr_COR_axis = c.expr_COR_axis;
        expr_EUC_axis = c.expr_EUC_axis;
        expr_spear_axis = c.expr_spear_axis;
        expr_mean_axis = c.expr_mean_axis;
        binary_axis = c.binary_axis;
        isNoninvert = c.isNoninvert;
        nonull = c.nonull;

        expr_reg_crit = c.expr_reg_crit;
        weigh = c.weigh;
        which_expr_crits = c.which_expr_crits;

        if (debug) {
            System.out.println("setParam usemean " + usemean + "\tc.usemean " + c.usemean);
        }

        usemean = c.usemean;
        useAbs = c.useAbs;
        useFrxnSign = c.useFrxnSign;
        debug = c.debug;
    }

    /**
     *
     */
    private void reset() {
        debug = false;
        crit = -1;
        requires_null = true;
        isMSER = false;
        isMSE = false;
        isMSEC = false;
        isMAD = false;
        KENDALLIndex = -1;
        isGEE = false;
        isonlyGEE = false;
        isonlyLARS = false;
        isLARS = false;
        isGEERE = false;
        isLARSRE = false;
        isGEECE = false;
        isLARSCE = false;
        CORIndex = -1;
        EUCIndex = -1;
        SPEARIndex = -1;
        isTotalCrit = false;
        isRowCrit = false;
        isColCrit = false;
        isMeanCrit = false;
        isTFCrit = false;
        isInteractCrit = false;
        isFeatureCrit = false;

        expr_mean_crit = -1;

        expr_mse_axis = -1;
        expr_reg_axis = -1;
        expr_kend_axis = -1;
        expr_COR_axis = -1;
        expr_EUC_axis = -1;
        expr_mean_axis = -1;
        expr_spear_axis = -1;
        binary_axis = -1;

        isNoninvert = -1;
        nonull = false;

        expr_reg_crit = -1;
        weigh = false;
        which_expr_crits = null;
        useAbs = MoreArray.initArray(3, 1);
        usemean = false;
        useFrxnSign = false;
    }

    /**
     * @param c
     */
    private void init(int c) {
        crit = c;
        if (debug)
            System.out.println("Criterion " + crit);

        isInter();
        isFeature();

        if (debug)
            System.out.println("Criterion " + isFeatureCrit);

        //requiresNull();

        setExprMeanCritType();
        setExprEUCCritType();
        setExprCORCritType();

        setNoNull();
        if (debug)
            System.out.println("Criterion nonull " + requires_null + "\t\t" + MoreArray.toString(neednull));

        //if (nonull) {
        boolean[] ectdata = exprTypeAll();
        if (debug)
            System.out.println("Criterion exprTypeAll " + MoreArray.toString(ectdata, ","));
        isMSE = ectdata[0];
        isMSER = ectdata[1];
        isMSEC = ectdata[2];
        isMAD = ectdata[3];
        KENDALLIndex = getKendallIndex();
        setExprCORCritType();//exprCor();//CORIndex =
        setExprEUCCritType();//exprEuc();//EUCIndex =
        setExprSPEARCritType();//exprEuc();//EUCIndex =
        /*} else {
            boolean[] ectdata = exprCorType();
            isMSE = ectdata[0];
            isMSER = ectdata[1];
            isMSEC = ectdata[2];
            isMAD = ectdata[3];
            KENDALLIndex = ectdata[4];
        }*/

        regCrit();

        boolean[] axisdata = exprAxis();
        isTotalCrit = axisdata[0];
        isRowCrit = axisdata[1];
        isColCrit = axisdata[2];

        if (crit > 0 && (MINER_STATIC.CRIT_LABELS[crit - 1].equals("MEDCMEAN") || MINER_STATIC.CRIT_LABELS[crit - 1].equals("MEDCMEANnonull"))) {
            isTotalCrit = false;
            isRowCrit = false;
            isColCrit = true;
        } else if (crit > 0 && (MINER_STATIC.CRIT_LABELS[crit - 1].equals("MEDRMEAN") || MINER_STATIC.CRIT_LABELS[crit - 1].equals("MEDRMEANnonull"))) {
            isTotalCrit = false;
            isRowCrit = true;
            isColCrit = false;
        } else if (crit > 0 && (MINER_STATIC.CRIT_LABELS[crit - 1].equals("MEAN") || MINER_STATIC.CRIT_LABELS[crit - 1].equals("MEANnonull"))) {
            isTotalCrit = true;
            isRowCrit = false;
            isColCrit = false;
        }


        setMSEAxis();
        setRegAxis();
        setKendAxis();
        setCorAxis();
        setEUCAxis();
        setSpearAxis();
        setBinaryAxis();

        try {
            int meanind = MoreArray.getArrayInd(MINER_STATIC.MEANCritAll, crit);
            //System.out.println("Criterion crit " + crit + "\tmeanind " + meanind);
            if (debug) {
                System.out.println("Criterion isMeanCrit bf " + isMeanCrit + "\t" + usemean);
            }

            if (meanind != -1)
                isMeanCrit = true;

            if (debug) {
                System.out.println("Criterion isMeanCrit af " + isMeanCrit + "\t" + usemean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //if (isMeanCrit)
        setMeanAxis();

        //default to row crit for gene-centric criteria
        if (!isTotalCrit && !isRowCrit && !isColCrit && (isInteractCrit || isTFCrit || isFeatureCrit)) {
            isRowCrit = true;
            expr_mean_axis = 2;
        }

        if (debug && crit > 0)
            System.out.println("Criterion " + MINER_STATIC.CRIT_LABELS[crit - 1]);
        //case for only MEAN criteria
        if (crit > 0 && (MINER_STATIC.CRIT_LABELS[crit - 1].equals("MEDRMEAN") || MINER_STATIC.CRIT_LABELS[crit - 1].equals("MEDRMEANnonull"))) {
            expr_mean_axis = 2;
        } else if (crit > 0 && (MINER_STATIC.CRIT_LABELS[crit - 1].equals("MEDCMEAN") || MINER_STATIC.CRIT_LABELS[crit - 1].equals("MEDCMEANnonull"))) {
            expr_mean_axis = 3;
        } else if (crit > 0 && (MINER_STATIC.CRIT_LABELS[crit - 1].equals("MEAN") || MINER_STATIC.CRIT_LABELS[crit - 1].equals("MEANnonull"))) {
            expr_mean_axis = 1;
        }
        //System.out.println("expr_mean_axis " + expr_mean_axis);

        setisNoninvert();

        which_expr_crits = Criterion.getExprCritTypes(this, weigh, usemean, debug);

    }

    /**
     *
     */
/*
    private void setNoNull() {
        nonull = false;
        try {
            if (MoreArray.getArrayInd(MINER_STATIC.nonullCrit, crit) != -1) {
                nonull = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
*/

    /**
     * Criterion may have components with different axis, the order of assigning the global axis is:
     * <p/>
     * total
     * row
     * col
     * <p/>
     * by majority or first in ties in order of: mse, reg, kend, COR
     *
     * @return
     */
    public boolean[] exprAxis() {
        boolean[] ret = new boolean[3];

        int[] count = new int[3];
        try {
            if (MoreArray.getArrayInd(MINER_STATIC.MSECritAll, crit) != -1) {
                count[0]++;
                //} else if (MoreArray.getArrayInd(MINER_STATIC.MSERCrit, crit) != -1) {
            } else if (MoreArray.getArrayInd(MINER_STATIC.MSERCritAll, crit) != -1) {
                count[1]++;
                //} else if (MoreArray.getArrayInd(MINER_STATIC.MSECCrit, crit) != -1) {
            } else if (MoreArray.getArrayInd(MINER_STATIC.MSECCritAll, crit) != -1) {
                count[2]++;
            }
            //if (MoreArray.getArrayInd(MINER_STATIC.GEEtotalCrit, crit) != -1) {
            if (MoreArray.getArrayInd(MINER_STATIC.GEECritAll, crit) != -1) {
                count[0]++;
                //} else if (MoreArray.getArrayInd(MINER_STATIC.GEERECrit, crit) != -1) {
            } else if (MoreArray.getArrayInd(MINER_STATIC.GEERECritAll, crit) != -1) {
                count[1]++;
                //} else if (MoreArray.getArrayInd(MINER_STATIC.GEECECrit, crit) != -1) {
            } else if (MoreArray.getArrayInd(MINER_STATIC.GEECECritAll, crit) != -1) {
                count[2]++;
            }

            //if (MoreArray.getArrayInd(MINER_STATIC.LARStotalCrit, crit) != -1) {
            if (MoreArray.getArrayInd(MINER_STATIC.LARSCritAll, crit) != -1) {
                count[0]++;
                //} else if (MoreArray.getArrayInd(MINER_STATIC.LARSRECrit, crit) != -1) {
            } else if (MoreArray.getArrayInd(MINER_STATIC.LARSRECritAll, crit) != -1) {
                count[1]++;
                //} else if (MoreArray.getArrayInd(MINER_STATIC.LARSCECrit, crit) != -1) {
            } else if (MoreArray.getArrayInd(MINER_STATIC.LARSCECritAll, crit) != -1) {
                count[2]++;
            }

            //if (MoreArray.getArrayInd(MINER_STATIC.CORtotalCrit, crit) != -1) {
            if (MoreArray.getArrayInd(MINER_STATIC.CORCritAll, crit) != -1) {
                count[0]++;
                //} else if (MoreArray.getArrayInd(MINER_STATIC.CORRCrit, crit) != -1) {
            } else if (MoreArray.getArrayInd(MINER_STATIC.CORRCritAll, crit) != -1) {
                count[1]++;
                //} else if (MoreArray.getArrayInd(MINER_STATIC.CORCCrit, crit) != -1) {
            } else if (MoreArray.getArrayInd(MINER_STATIC.CORCCritAll, crit) != -1) {
                count[2]++;
            }

            //if (MoreArray.getArrayInd(MINER_STATIC.KENDALLtotalCrit, crit) != -1) {
            if (MoreArray.getArrayInd(MINER_STATIC.KENDALLCritAll, crit) != -1) {
                count[0]++;
            }
            //if (MoreArray.getArrayInd(MINER_STATIC.KENDALLRCrit, crit) != -1) {
            if (MoreArray.getArrayInd(MINER_STATIC.KENDALLRCritAll, crit) != -1) {
                count[1]++;
                //} else if (MoreArray.getArrayInd(MINER_STATIC.KENDALLCCrit, crit) != -1) {
            } else if (MoreArray.getArrayInd(MINER_STATIC.KENDALLCCritAll, crit) != -1) {
                count[2]++;
            }

            //if (MoreArray.getArrayInd(MINER_STATIC.EUCtotalCrit, crit) != -1) {
            if (MoreArray.getArrayInd(MINER_STATIC.EUCCritAll, crit) != -1) {
                count[0]++;
                //} else if (MoreArray.getArrayInd(MINER_STATIC.EUCRCrit, crit) != -1) {
            } else if (MoreArray.getArrayInd(MINER_STATIC.EUCRCritAll, crit) != -1) {
                count[1]++;
                //} else if (MoreArray.getArrayInd(MINER_STATIC.EUCCCrit, crit) != -1) {
            } else if (MoreArray.getArrayInd(MINER_STATIC.EUCCCritAll, crit) != -1) {
                count[2]++;
            }

            if (MoreArray.getArrayInd(MINER_STATIC.SPEARMANCritAll, crit) != -1) {
                count[0]++;
                //} else if (MoreArray.getArrayInd(MINER_STATIC.EUCRCrit, crit) != -1) {
            } else if (MoreArray.getArrayInd(MINER_STATIC.SPEARMANRCritAll, crit) != -1) {
                count[1]++;
                //} else if (MoreArray.getArrayInd(MINER_STATIC.EUCCCrit, crit) != -1) {
            } else if (MoreArray.getArrayInd(MINER_STATIC.SPEARMANCCritAll, crit) != -1) {
                count[2]++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //total
        if (count[0] > count[1] && count[0] > count[2]) {
            ret[0] = true;
            ret[1] = false;
            ret[2] = false;
        }
        //row
        else if (count[1] > count[0] && count[1] > count[2]) {
            ret[0] = false;
            ret[1] = true;
            ret[2] = false;
        }
        //col
        else if (count[2] > count[0] && count[2] > count[1]) {
            ret[0] = false;
            ret[1] = false;
            ret[2] = true;
        }
        // total vs row vs col tie or total vs row tie or total vs col tie = total
        else if (count[0] > 0 && (count[0] == count[1] && count[0] == count[2]) ||
                (count[0] == count[1]) ||
                (count[0] == count[2])) {
            ret[0] = true;
            ret[1] = false;
            ret[2] = false;

        }
        //row vs col tie = row
        else if (count[1] > 0 && count[1] == count[2]) {
            ret[0] = false;
            ret[1] = true;
            ret[2] = false;

        }
        if (debug)
            System.out.println("exprAxis " + crit + "\t" + MoreArray.toString(count, ",") + "\t" + MoreArray.toString(ret, ","));

        return ret;
    }


    /**
     * MSE
     * MSER
     * MSEC
     * MADR
     *
     * @return
     */
    public boolean[] exprTypeAll() {
        boolean[] ret = new boolean[4];
        try {
            if (MoreArray.getArrayInd(MINER_STATIC.MSECritAll, crit) != -1) {
                ret[0] = true;
                ret[1] = false;
                ret[2] = false;
                ret[3] = false;
            } else if (MoreArray.getArrayInd(MINER_STATIC.MSERCritAll, crit) != -1) {
                ret[0] = false;
                ret[1] = true;
                ret[2] = false;
                ret[3] = false;
            } else if (MoreArray.getArrayInd(MINER_STATIC.MSECCritAll, crit) != -1) {
                ret[0] = false;
                ret[1] = false;
                ret[2] = true;
                ret[3] = false;
            } else if (MoreArray.getArrayInd(MINER_STATIC.MADRCritAll, crit) != -1) {
                ret[0] = false;
                ret[1] = false;
                ret[2] = false;
                ret[3] = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * @return
     */
    public int getKendallIndex() {
        int ret = -1;
        try {
            if (MoreArray.getArrayInd(MINER_STATIC.KENDALLtotalCritAll, crit) != -1) {
                ret = 1;
            }
            if (MoreArray.getArrayInd(MINER_STATIC.KENDALLCCritAll, crit) != -1) {
                ret = 3;
            } else if (MoreArray.getArrayInd(MINER_STATIC.KENDALLRCritAll, crit) != -1) {
                ret = 2;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println("Criterion getKendallIndex :"+ret+":\t"+crit);
        //MoreArray.printArray(MINER_STATIC.KENDALLCCrit);
        return ret;
    }

    /**
     * @return
     */
    public int exprBinary() {
        int ret = -1;
        try {
            if (MoreArray.getArrayInd(MINER_STATIC.BinaryCritAll, crit) != -1) {
                ret = 1;
            } else if (MoreArray.getArrayInd(MINER_STATIC.BinaryRCrit, crit) != -1) {
                ret = 2;
            } else if (MoreArray.getArrayInd(MINER_STATIC.BinaryCCrit, crit) != -1) {
                ret = 3;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println("Criterion getKendallIndex :"+ret+":\t"+crit);
        //MoreArray.printArray(MINER_STATIC.KENDALLCCrit);
        return ret;
    }

    /**
     * Detects MSE or MAD crit
     */
    public void setExprMeanCritType() {
        expr_mean_crit = -1;
        try {
            if (MoreArray.getArrayInd(MINER_STATIC.MSECritAll, crit) != -1 ||
                    MoreArray.getArrayInd(MINER_STATIC.MSERCritAll, crit) != -1 ||
                    MoreArray.getArrayInd(MINER_STATIC.MSECCritAll, crit) != -1) {
                expr_mean_crit = 1;
                //System.out.println("setExprMeanCritType MSEtotalCrit/MSERCrit/MSECCrit");
            }
            if (MoreArray.getArrayInd(MINER_STATIC.MADRCritAll, crit) != -1) {
                expr_mean_crit = 2;
                //System.out.println("setExprMeanCritType MADRCrit");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (debug)
            System.out.println("expr_mean_crit " + expr_mean_crit);
    }

    /**
     *
     */
    public void setExprEUCCritType() {
        EUCIndex = -1;
        try {
            if (MoreArray.getArrayInd(MINER_STATIC.EUCCritAll, crit) != -1) {
                EUCIndex = 1;
            } else if (MoreArray.getArrayInd(MINER_STATIC.EUCRCrit, crit) != -1) {
                EUCIndex = 2;
            } else if (MoreArray.getArrayInd(MINER_STATIC.EUCCCrit, crit) != -1) {
                EUCIndex = 3;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (debug)
            System.out.println("EUCIndex " + EUCIndex);
    }

    /**
     *
     */
    public void setExprCORCritType() {
        CORIndex = -1;
       /* System.out.println("setExprCORCritType MINER_STATIC.CORCritAll.length " + MINER_STATIC.CORCritAll.length);
        for (int i = 0; i < MINER_STATIC.CORCritAll.length; i++) {
            System.out.println(i + "\t" + (MINER_STATIC.CORCritAll[i] - 1) + "\t" + MINER_STATIC.CRIT_LABELS[MINER_STATIC.CORCritAll[i] - 1]);
        }*/

        try {
            if (MoreArray.getArrayInd(MINER_STATIC.CORCritAll, crit) != -1) {
                CORIndex = 1;
                //System.out.println("setExprCORCritType 1 " + crit + "\t" + MINER_STATIC.CRIT_LABELS[crit - 1]);
                //MoreArray.printArray(MINER_STATIC.CORCritAll);
            } else if (MoreArray.getArrayInd(MINER_STATIC.CORRCritAll, crit) != -1) {
                CORIndex = 2;
                //System.out.println("setExprCORCritType 2 " + crit + "\t" + MINER_STATIC.CRIT_LABELS[crit - 1]);
                //MoreArray.printArray(MINER_STATIC.CORCritAll);
            } else if (MoreArray.getArrayInd(MINER_STATIC.CORCCritAll, crit) != -1) {
                CORIndex = 3;
                //System.out.println("setExprCORCritType 3 " + crit + "\t" + MINER_STATIC.CRIT_LABELS[crit - 1]);
                //MoreArray.printArray(MINER_STATIC.CORCritAll);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (debug)
            System.out.println("CORIndex " + CORIndex);
    }

    /**
     *
     */
    public void setExprSPEARCritType() {
        SPEARIndex = -1;
       /* System.out.println("setExprCORCritType MINER_STATIC.CORCritAll.length " + MINER_STATIC.CORCritAll.length);
        for (int i = 0; i < MINER_STATIC.CORCritAll.length; i++) {
            System.out.println(i + "\t" + (MINER_STATIC.CORCritAll[i] - 1) + "\t" + MINER_STATIC.CRIT_LABELS[MINER_STATIC.CORCritAll[i] - 1]);
        }*/

        try {
            if (MoreArray.getArrayInd(MINER_STATIC.SPEARMANCritAll, crit) != -1) {
                SPEARIndex = 1;
                //System.out.println("setExprCORCritType 1 " + crit + "\t" + MINER_STATIC.CRIT_LABELS[crit - 1]);
                //MoreArray.printArray(MINER_STATIC.CORCritAll);
            } else if (MoreArray.getArrayInd(MINER_STATIC.SPEARMANRCritAll, crit) != -1) {
                SPEARIndex = 2;
                //System.out.println("setExprCORCritType 2 " + crit + "\t" + MINER_STATIC.CRIT_LABELS[crit - 1]);
                //MoreArray.printArray(MINER_STATIC.CORCritAll);
            } else if (MoreArray.getArrayInd(MINER_STATIC.SPEARMANCCritAll, crit) != -1) {
                SPEARIndex = 3;
                //System.out.println("setExprCORCritType 3 " + crit + "\t" + MINER_STATIC.CRIT_LABELS[crit - 1]);
                //MoreArray.printArray(MINER_STATIC.CORCritAll);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (debug)
            System.out.println("SPEARIndex " + SPEARIndex);
    }

    /**
     * total =1
     * row =2
     * col =3
     */
    public void setMSEAxis() {
        expr_mse_axis = -1;
        try {
            if (MoreArray.getArrayInd(MINER_STATIC.MSECritAll, crit) != -1) {
                expr_mse_axis = 1;
                if (debug)
                    System.out.println("setMSEAxis totalCrit " + crit);
            } else if (MoreArray.getArrayInd(MINER_STATIC.MSERCritAll, crit) != -1 ||
                    MoreArray.getArrayInd(MINER_STATIC.MADRCritAll, crit) != -1) {
                expr_mse_axis = 2;
                //System.out.println("setMSEAxis rowCrit");
            } else if (MoreArray.getArrayInd(MINER_STATIC.MSECCritAll, crit) != -1) {
                expr_mse_axis = 3;
                //System.out.println("setMSEAxis colCrit");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (debug)
            System.out.println("expr_mse_axis " + crit + "\t" + expr_mse_axis);
    }

    /**
     * row =2
     * col =3
     */
    public void setRegAxis() {
        expr_reg_axis = -1;
        try {
            if (expr_reg_crit != -1) {

                if (MoreArray.getArrayInd(MINER_STATIC.LARSCritAll, crit) != -1 || MoreArray.getArrayInd(MINER_STATIC.GEECritAll, crit) != -1)
                    expr_reg_axis = 1;
                if (MoreArray.getArrayInd(MINER_STATIC.LARSRECrit, crit) != -1 ||
                        MoreArray.getArrayInd(MINER_STATIC.GEERECrit, crit) != -1)
                    expr_reg_axis = 2;
                else if (MoreArray.getArrayInd(MINER_STATIC.LARSCECrit, crit) != -1 ||
                        MoreArray.getArrayInd(MINER_STATIC.GEECECrit, crit) != -1)
                    expr_reg_axis = 3;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (debug)
            System.out.println("expr_reg_axis " + crit + "\t" + expr_reg_axis);
    }

    /**
     * row =2
     * col =3
     */
    public void setKendAxis() {
        expr_kend_axis = -1;

        try {
            if (MoreArray.getArrayInd(MINER_STATIC.KENDALLCritAll, crit) != -1) {
                expr_kend_axis = 1;
                //System.out.println("setMSEAxis rowCrit");
            } else if (MoreArray.getArrayInd(MINER_STATIC.KENDALLRCrit, crit) != -1) {
                expr_kend_axis = 2;
                //System.out.println("setMSEAxis rowCrit");
            } else if (MoreArray.getArrayInd(MINER_STATIC.KENDALLCCrit, crit) != -1) {
                expr_kend_axis = 3;
                //System.out.println("setMSEAxis colCrit");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (debug)
            System.out.println("expr_kend_axis " + crit + "\t" + expr_kend_axis);
    }

    /**
     * row =2
     * col =3
     */
    public void setBinaryAxis() {
        binary_axis = -1;
        //System.out.println("setBinaryAxis " + crit + "\t" + MINER_STATIC.CRIT_LABELS[crit - 1]);
        try {
            if (MoreArray.getArrayInd(MINER_STATIC.BinaryCritAll, crit) != -1) {
                binary_axis = 1;
                //System.out.println("setBinaryAxis Crit");
            } else if (MoreArray.getArrayInd(MINER_STATIC.BinaryRCrit, crit) != -1) {
                binary_axis = 2;
                //System.out.println("setBinaryAxis rowCrit");
            } else if (MoreArray.getArrayInd(MINER_STATIC.BinaryCCrit, crit) != -1) {
                binary_axis = 3;
                //System.out.println("setBinaryAxis colCrit");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (debug)
            System.out.println("binary_axis " + crit + "\t" + binary_axis);
    }

    /**
     * row =2
     * col =3
     */
    public void setCorAxis() {
        expr_COR_axis = -1;

        try {
            if (MoreArray.getArrayInd(MINER_STATIC.CORtotalCrit, crit) != -1) {
                expr_COR_axis = 1;
                //System.out.println("setMSEAxis rowCrit");
            } else if (MoreArray.getArrayInd(MINER_STATIC.CORRCrit, crit) != -1) {
                expr_COR_axis = 2;
                //System.out.println("setMSEAxis rowCrit");
            } else if (MoreArray.getArrayInd(MINER_STATIC.CORCCrit, crit) != -1) {
                expr_COR_axis = 3;
                //System.out.println("setMSEAxis colCrit");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (debug)
            System.out.println("expr_COR_axis " + crit + "\t" + expr_COR_axis);
    }

    /**
     * row =2
     * col =3
     */
    public void setEUCAxis() {
        expr_EUC_axis = -1;

        try {
            if (MoreArray.getArrayInd(MINER_STATIC.EUCtotalCrit, crit) != -1) {
                expr_EUC_axis = 1;
                //System.out.println("setMSEAxis rowCrit");
            } else if (MoreArray.getArrayInd(MINER_STATIC.EUCRCrit, crit) != -1) {
                expr_EUC_axis = 2;
                //System.out.println("setMSEAxis rowCrit");
            } else if (MoreArray.getArrayInd(MINER_STATIC.EUCCCrit, crit) != -1) {
                expr_EUC_axis = 3;
                //System.out.println("setMSEAxis colCrit");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (debug)
            System.out.println("expr_EUC_axis " + crit + "\t" + expr_EUC_axis);
    }

    /**
     * row =2
     * col =3
     */
    public void setSpearAxis() {
        expr_spear_axis = -1;

        try {
            if (MoreArray.getArrayInd(MINER_STATIC.SPEARMANtotalCrit, crit) != -1) {
                expr_spear_axis = 1;
                //System.out.println("setMSEAxis rowCrit");
            } else if (MoreArray.getArrayInd(MINER_STATIC.SPEARMANRCrit, crit) != -1) {
                expr_spear_axis = 2;
                //System.out.println("setMSEAxis rowCrit");
            } else if (MoreArray.getArrayInd(MINER_STATIC.SPEARMANCCrit, crit) != -1) {
                expr_spear_axis = 3;
                //System.out.println("setMSEAxis colCrit");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (debug)
            System.out.println("expr_spear_axis " + crit + "\t" + expr_spear_axis);
    }

    /**
     * total =1
     * row =2
     * col =3
     *
     * @return
     */
    public void setMeanAxis() {
        expr_mean_axis = -1;
        int[] axis = new int[3];
        //only mean crit
        if (expr_mse_axis == -1 && expr_reg_axis == -1 && expr_kend_axis == -1 && expr_COR_axis == -1 && expr_EUC_axis == -1 && expr_spear_axis == -1) {
            if (isMeanCrit && isTotalCrit)
                expr_mean_axis = 1;
            if (isMeanCrit && isRowCrit)
                expr_mean_axis = 2;
            if (isMeanCrit && isColCrit)
                expr_mean_axis = 3;
        } else {
            if (expr_mse_axis != -1)
                axis[expr_mse_axis - 1]++;
            if (expr_reg_axis != -1)
                axis[expr_reg_axis - 1]++;
            if (expr_kend_axis != -1)
                axis[expr_kend_axis - 1]++;
            if (expr_COR_axis != -1)
                axis[expr_COR_axis - 1]++;
            if (expr_EUC_axis != -1)
                axis[expr_EUC_axis - 1]++;

            if (axis[0] > axis[1] && axis[0] > axis[2])
                expr_mean_axis = 1;
            else if (axis[1] > axis[0] && axis[1] > axis[2])
                expr_mean_axis = 2;
            else if (axis[2] > axis[0] && axis[2] > axis[1])
                expr_mean_axis = 3;
                //all ties = total
            else if (axis[0] > 0 && axis[0] == axis[1] && axis[0] == axis[2])
                expr_mean_axis = 1;
                //if tot = row then tot
            else if (axis[0] > 0 && axis[0] == axis[1])
                expr_mean_axis = 1;
                //if tot = col then tot
            else if (axis[0] > 0 && axis[0] == axis[2])
                expr_mean_axis = 1;
                //if row = col then row
            else if (axis[1] > 0 && axis[1] == axis[2])
                expr_mean_axis = 2;


            if (debug)
                System.out.println("setMeanAxis crit " + crit +
                        "\tMINER_STATIC.CRIT_LABELS[crit - 1] " + MINER_STATIC.CRIT_LABELS[crit - 1] + "\tusemean " + usemean);

            if (crit > 0 && (MINER_STATIC.CRIT_LABELS[crit - 1].contains("MEDRMEAN") ||
                    MINER_STATIC.CRIT_LABELS[crit - 1].contains("MEDCMEAN") ||
                    MINER_STATIC.CRIT_LABELS[crit - 1].contains("MEAN"))
                //&& MINER_STATIC.CRIT_LABELS[crit - 1].indexOf("_") == -1
                    ) {
                usemean = true;
                System.out.println("usemean = true");
                if (MINER_STATIC.CRIT_LABELS[crit - 1].contains("MEDRMEAN") &&
                        MINER_STATIC.CRIT_LABELS[crit - 1].indexOf("_") == -1) {
                    expr_mean_axis = 2;
                } else if (MINER_STATIC.CRIT_LABELS[crit - 1].contains("MEDCMEAN") &&
                        MINER_STATIC.CRIT_LABELS[crit - 1].indexOf("_") == -1) {
                    expr_mean_axis = 3;
                } else if (MINER_STATIC.CRIT_LABELS[crit - 1].contains("MEAN") &&
                        MINER_STATIC.CRIT_LABELS[crit - 1].indexOf("_") == -1) {
                    expr_mean_axis = 1;
                }
            }
        }

        //System.out.println("setMeanAxis " + debug);
        if (debug) {
            System.out.println("setMeanAxis " + crit + "\t" + expr_mean_axis + "\t" + expr_mse_axis + "\t" +
                    expr_reg_axis + "\t" + expr_kend_axis + "\t" + expr_COR_axis + "\t" + expr_EUC_axis + "\t" +
                    expr_spear_axis + "\tusemean " + usemean);
            System.out.println("setMeanAxis " + MoreArray.toString(axis, ","));
        }
    }


    /**
     *
     */
    public void regCrit() {
        //System.out.println("regCrit GEECrit " + MoreArray.getArrayInd(MINER_STATIC.GEECrit, crit));
        //System.out.println("regCrit GEECrit " + MoreArray.toString(MINER_STATIC.GEECrit, ","));

        boolean[] erdata = exprRegType();
        isGEERE = erdata[0];
        isGEECE = erdata[1];
        isonlyGEE = erdata[4];
        if (isGEERE || isGEECE || isonlyGEE)
            isGEE = true;

        isLARSRE = erdata[2];
        isLARSCE = erdata[3];
        isonlyLARS = erdata[5];
        if (isLARSRE || isLARSCE || isonlyLARS)
            isLARS = true;
        //System.out.println("Criterion " + isGEE + "\t" + isLARS + "\t" + MoreArray.toString(erdata, ","));
        expr_reg_crit = -1;//"NULL ";
        if (isLARS) {
            expr_reg_crit = 1;//"1";
            //System.out.println("regCrit LARSCrit");
        }
        if (isGEE) {
            expr_reg_crit = 2;//"2";
            //System.out.println("regCrit GEECrit");
        }

        if (debug) {
            System.out.println("regCrit isGEE " + isGEE + "\tisonlyGEE" + isonlyGEE + "\tisGEERE " + isGEERE + "\tisGEECE " + isGEECE + "\texpr_reg_crit " + expr_reg_crit);
            System.out.println("regCrit isLARS " + isLARS + "\tisonlyLARS" + isonlyLARS + "\tisLARSRE " + isLARSRE + "\tisLARSCE " + isLARSCE + "\texpr_reg_crit " + expr_reg_crit);
        }
    }


    /**
     * @return
     */
    public boolean[] exprRegType() {
        boolean[] ret = new boolean[6];
        //System.out.println("exprRegType ind GEERECrit "+ind);
        try {
            if (MoreArray.getArrayInd(MINER_STATIC.GEERECrit, crit) != -1) {
                ret[0] = true;
                ret[1] = false;
                ret[2] = false;
                ret[3] = false;
                ret[4] = false;
            } else if (MoreArray.getArrayInd(MINER_STATIC.GEECECrit, crit) != -1) {
                ret[0] = false;
                ret[1] = true;
                ret[2] = false;
                ret[3] = false;
                ret[4] = false;
            } else if (MoreArray.getArrayInd(MINER_STATIC.LARSRECrit, crit) != -1) {
                ret[0] = false;
                ret[1] = false;
                ret[2] = true;
                ret[3] = false;
                ret[4] = false;
            } else if (MoreArray.getArrayInd(MINER_STATIC.LARSCECrit, crit) != -1) {
                ret[0] = false;
                ret[1] = false;
                ret[2] = false;
                ret[3] = true;
                ret[4] = false;
            } else if (MoreArray.getArrayInd(MINER_STATIC.GEECritAll, crit) != -1) {
                ret[0] = false;
                ret[1] = false;
                ret[2] = false;
                ret[3] = false;
                ret[4] = true;
            } else if (MoreArray.getArrayInd(MINER_STATIC.LARSCritAll, crit) != -1) {
                ret[0] = false;
                ret[1] = false;
                ret[2] = false;
                ret[3] = false;
                ret[4] = false;
                ret[5] = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }


    /**
     *
     */
    public void isInter() {
        isInteractCrit = false;
        try {
            if (MoreArray.getArrayInd(MINER_STATIC.interactCrit, crit) != -1) {
                isInteractCrit = true;
                //System.out.println("isInter int");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    public void isFeature() {
        isFeatureCrit = false;
        try {
            if (MoreArray.getArrayInd(MINER_STATIC.FEATURECrit, crit) != -1) {
                isFeatureCrit = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    public void setisNoninvert() {
        isNoninvert = 0;
        try {
            if (MoreArray.getArrayInd(MINER_STATIC.isNoninvertCrit, crit) != -1)
                isNoninvert = 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (forceinv != null) {
            if (forceinv[0] && expr_mse_axis == 1)
                isNoninvert = 1;
            else if (forceinv[1] && expr_mse_axis == 2)
                isNoninvert = 1;
            else if (forceinv[2] && expr_mse_axis == 3)
                isNoninvert = 1;
            else if (forceinv[3] && isMAD)
                isNoninvert = 1;
            else if (forceinv[4] && EUCIndex == 1)
                isNoninvert = 1;
            else if (forceinv[5] && EUCIndex == 2)
                isNoninvert = 1;
            else if (forceinv[6] && EUCIndex == 3)
                isNoninvert = 1;
        }
        if (debug)
            System.out.println("invert " + isNoninvert);
    }

    /**
     * Votes by majority or first in ties
     *
     * @return
     */
    public final static boolean[] getExprCritTypes(Criterion c, boolean weigh, boolean use_mean, boolean debug) {
        /*
        mean
        mse
        reg
        kend
        COR
        euc
        SPEARMAN
        tf
        */

        boolean[] passcrits = new boolean[8];
        //if (weigh) {
        //if (c.expr_mean_axis != -1 && use_mean)
        passcrits[0] = use_mean;
        passcrits[1] = c.expr_mse_axis != -1 ? true : false;
        passcrits[2] = c.expr_reg_axis != -1 ? true : false;
        passcrits[3] = c.KENDALLIndex != -1 ? true : false;
        passcrits[4] = c.CORIndex != -1 ? true : false;
        passcrits[5] = c.EUCIndex != -1 ? true : false;
        passcrits[6] = c.SPEARIndex != -1 ? true : false;
        passcrits[7] = c.isTFCrit;
        //}
        if (debug) {
            System.out.println("getExprCritTypes c.expr_mean_axis " + use_mean + ":\t:" + c.expr_mean_axis +
                    "\tc.expr_mse_axis " + c.expr_mse_axis + "\tc.expr_reg_axis " + c.expr_reg_axis +
                    "\tc.KENDALLIndex " + c.KENDALLIndex + "\tc.CORIndex " + c.CORIndex +
                    "\tc.EUCIndex " + c.EUCIndex + "\tc.SPEARIndex " + c.SPEARIndex + "\tc.isTFCrit " + c.isTFCrit);
        }
        return passcrits;
    }
}
