package DataMining;

import mathy.Matrix;
import mathy.stat;
import org.rosuda.JRI.Rengine;
import util.MoreArray;
import util.StringUtil;
import util.TabFile;

import java.io.File;

import static util.MoreArray.extractColumn;

/**
 * Class to load and dynamically assign null distribution values.
 * <p/>
 * User: marcin
 * Date: Aug 21, 2010
 * Time: 3:13:44 PM
 */
public class ObtainNullValues {
    private int[] genes;
    private int[] exps;

    public double[][] precrit_meanMeanNull, precrit_sdMeanNull;
    public double[][] precrit_meanMSENull, precrit_sdMSENull;
    public double[][] precrit_meanRegNull, precrit_sdRegNull;
    public double[][] precrit_meanBinaryNull, precrit_sdBinaryNull;
    public double[][] precrit_meanKendNull, precrit_sdKendNull;
    public double[][] precrit_meanCorNull, precrit_sdCorNull;
    public double[][] precrit_meanEUCNull, precrit_sdEUCNull;
    public double[][] precrit_meanSpearNull, precrit_sdSpearNull;

    public double[] nullMeanData;
    public double[] nullMSEData;
    public double[] nullRegData;
    public double[] nullBinaryData;
    public double[] nullKendData;
    public double[] nullCorData;
    public double[] nullEucData;
    public double[] nullSpearData;
    public double[] nullInteractData;
    public double[] nullFeatData;
    public double[] nullTFData;

    public double[][] meanMeanNull, sdMeanNull;
    public double[][] meanMSENull, sdMSENull;
    public double[][] meanRegNull, sdRegNull;
    public double[][] meanBinaryNull, sdBinaryNull;
    public double[][] meanKendNull, sdKendNull;
    public double[][] meanCorNull, sdCorNull;
    public double[][] meanEucNull, sdEucNull;
    public double[][] meanSpearNull, sdSpearNull;
    public double[] meanInteractNull, sdInteractNull;
    public double[] meanFeatNull, sdFeatNull;
    public double[] meanTFNull, sdTFNull;


    private Rengine re;
    private Parameters prm;

    boolean regNullLoaded = false;

    boolean debug = false;


    /**
     * @param r
     * @param p
     * @param d
     */
    public ObtainNullValues(Rengine r, Parameters p, boolean d) {
        re = r;
        prm = p;
        debug = d;

        loadInterNulls();
        loadTFNulls();
        loadFeatNulls();

        System.out.println("setCriteriaNull " + prm.CRIT_TYPE_INDEX);
        setCriteriaNull();
        System.out.println("setPreCriteriaNull " + prm.PRECRIT_TYPE_INDEX);
        setPreCriteriaNull();
    }


    /**
     * @param vb
     */
    public void setCurNull(ValueBlock vb, Criterion c) {
        genes = vb.genes;
        exps = vb.exps;
        set(c);
    }

    /**
     * @param g
     * @param e
     */
    public void setCurNull(int[] g, int[] e, Criterion c) {
        genes = g;
        exps = e;
        set(c);
    }

    /**
     * @return
     */
    public void set(Criterion crit) {

        if (debug) {
            System.out.println("set crit.requires_null " + MoreArray.toString(crit.neednull));//crit.requires_null);
            System.out.println("set crit " + crit.crit + "\t" + MINER_STATIC.CRIT_LABELS[crit.crit - 1]);
            System.out.println("set is ppi " + crit.isInteractCrit + "\t" + crit.crit + "\t" + MINER_STATIC.CRIT_LABELS[crit.crit - 1]);
            System.out.println("set is feat " + crit.isFeatureCrit + "\t" + crit.crit + "\t" + MINER_STATIC.CRIT_LABELS[crit.crit - 1]);
            System.out.println("set is TF " + crit.isTFCrit + "\t" + crit.crit + "\t" + MINER_STATIC.CRIT_LABELS[crit.crit - 1]);
            System.out.println("set is expr_COR " + crit.isMSE + "\t" + crit.isMSER + "\t" + crit.isMSEC + "\t" + crit.isMAD + "\t" + crit.KENDALLIndex);
            System.out.println("set is expr_reg " + crit.isLARS + "\t" + crit.isFEM);
        }

        if (crit.usemean) {
            try {
                nullMeanData = setMeanNull(genes.length, exps.length);
            } catch (Exception e) {
                System.out.println("nullMeanData is not defined");
            }
            if (nullMeanData != null) {
                if (debug)
                    System.out.println("R: nullMeanData<-c(" + MoreArray.toString(nullMeanData, ",") + ")");
                boolean retjri = re.assign("nullMeanData", nullMeanData);
            } else {
                if (debug)
                    System.out.println("R: nullMeanData <- NULL");
                re.eval("nullMeanData <- NULL");
            }
        }


        if (crit.isMSE || crit.isMSER || crit.isMSEC || crit.isMAD) {
            try {
                nullMSEData = setMSENull(genes.length, exps.length, crit.requires_null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (nullMSEData != null) {
                if (debug)
                    System.out.println("R: nullMSEData<-c(" + MoreArray.toString(nullMSEData, ",") + ")");
                boolean retjri = re.assign("nullMSEData", nullMSEData);
            } else {
                if (debug)
                    System.out.println("R: nullMSEData <- NULL");
                re.eval("nullMSEData <- NULL");
            }
        } /*else if (crit.isMSE || crit.isMSER || crit.isMSEC || crit.isMAD)
            System.out.println("ERROR: nullMSEData is expected but null");*/


        if (crit.isFEM || crit.isLARS) {
            try {
                nullRegData = setRegNull(genes.length, exps.length);
            } catch (Exception e) {
                System.out.println("nullRegData is not defined for FEM/LARS");
            }
            if (nullRegData != null) {
                if (debug)
                    System.out.println("R: nullRegData<-c(" + MoreArray.toString(nullRegData, ",") + ")");
                boolean retjri = re.assign("nullRegData", nullRegData);
            } else {
                if (debug)
                    System.out.println("R: nullRegData <- NULL");
                re.eval("nullRegData <- NULL");
            }
        }

        //System.out.println("set KENDALLIndex "+crit.KENDALLIndex);
        if (crit.binary_axis != -1) {
            try {
                nullBinaryData = setBinaryNull(genes.length, exps.length);
            } catch (Exception e) {
                System.out.println("nullBinaryData is not defined");
            }
            if (nullBinaryData != null) {
                if (debug)
                    System.out.println("R: nullBinaryData<-c(" + MoreArray.toString(nullBinaryData, ",") + ")");
                boolean retjri = re.assign("nullBinaryData", nullBinaryData);
            } else {
                if (debug)
                    System.out.println("R: nullBinaryData <- NULL");
                re.eval("nullBinaryData <- NULL");
            }
        }


        if (crit.KENDALLIndex != -1) {
            try {
                nullKendData = setKendNull(genes.length, exps.length);
            } catch (Exception e) {
                System.out.println("nullKendData is not defined");
            }
            if (nullKendData != null) {
                if (debug)
                    System.out.println("R: nullKendData<-c(" + MoreArray.toString(nullKendData, ",") + ")");
                boolean retjri = re.assign("nullKendData", nullKendData);
            } else {
                if (debug)
                    System.out.println("R: nullKendData <- NULL");
                re.eval("nullKendData <- NULL");
            }
        }

        if (crit.CORIndex != -1) {
            try {
                nullCorData = setCorNull(genes.length, exps.length);
            } catch (Exception e) {
                System.out.println("nullCorData is not defined");
            }
            if (nullCorData != null) {
                if (debug)
                    System.out.println("R: nullCorData<-c(" + MoreArray.toString(nullCorData, ",") + ")");
                boolean retjri = re.assign("nullCorData", nullCorData);
            } else {
                if (debug)
                    System.out.println("R: nullCorData <- NULL");
                re.eval("nullCorData <- NULL");
            }
        }
        if (crit.EUCIndex != -1) {
            try {
                nullEucData = setEucNull(genes.length, exps.length);
            } catch (Exception e) {
                System.out.println("nullEucData is not defined");
            }
            if (nullEucData != null) {
                if (debug)
                    System.out.println("R: nullEucData<-c(" + MoreArray.toString(nullEucData, ",") + ")");
                boolean retjri = re.assign("nullEucData", nullEucData);
            } else {
                if (debug)
                    System.out.println("R: nullEucData <- NULL");
                re.eval("nullEucData <- NULL");
            }
        }
        if (crit.SPEARIndex != -1) {
            try {
                nullSpearData = setSpearNull(genes.length, exps.length);
            } catch (Exception e) {
                System.out.println("nullSpearData is not defined");
            }
            if (nullSpearData != null) {
                if (debug)
                    System.out.println("R: nullSpearData<-c(" + MoreArray.toString(nullSpearData, ",") + ")");
                boolean retjri = re.assign("nullSpearData", nullSpearData);
            } else {
                if (debug)
                    System.out.println("R: nullSpearData <- NULL");
                re.eval("nullSpearData <- NULL");
            }
        }

        if (crit.isInteractCrit) {
            try {
                nullInteractData = setInteractionNull(genes.length);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (nullInteractData != null) {
                if (debug)
                    System.out.println("R: nullInteractData<-c(" + MoreArray.toString(nullInteractData, ",") + ")");
                boolean retjri = re.assign("nullInteractData", nullInteractData);
            } else {
                if (debug)
                    System.out.println("R: nullInteractData <- NULL");
                re.eval("nullInteractData <- NULL");
            }
        }

        if (crit.isFeatureCrit) { //meanFeatNull != null) {
            try {
                nullFeatData = setFeatNull(genes.length);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (nullFeatData != null) {
                if (debug)
                    System.out.println("R: nullFeatData<-c(" + MoreArray.toString(nullFeatData, ",") + ")");
                boolean retjri = re.assign("nullFeatData", nullFeatData);
            } else {
                if (debug)
                    System.out.println("R: nullFeatData <- NULL");
                re.eval("nullFeatData <- NULL");
            }
        }

        if (crit.isTFCrit) {
            try {
                nullTFData = setTFNull(genes.length);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (nullTFData != null) {
                if (debug)
                    System.out.println("R: nullTFData<-c(" + MoreArray.toString(nullTFData, ",") + ")");
                boolean retjri = re.assign("nullTFData", nullTFData);
            } else {
                if (debug)
                    System.out.println("R: nullTFData <- NULL");
                re.eval("nullTFData <- NULL");
            }
        }


    }

    /**
     * @param genes
     * @param exps
     * @return
     */
    private double[] setMSENull(int genes, int exps, boolean requires) {
        double[] nullData = null;
        nullData = new double[2];
        try {
            nullData[0] = meanMSENull[Math.min(exps - prm.JMIN, meanMSENull.length - 1)]
                    [Math.min(genes - prm.IMIN, meanMSENull[0].length - 1)];
        } catch (Exception e) {
            nullData[0] = Double.NaN;
        }

        try {
            nullData[1] = sdMSENull[Math.min(exps - prm.JMIN, sdMSENull.length - 1)]
                    [Math.min(genes - prm.IMIN, sdMSENull[0].length - 1)];
        } catch (Exception e) {
            nullData[1] = Double.NaN;
        }
        if (nullData == null && requires) {
            System.out.println("ERROR: CRIT_TYPE_INDEX requires a null but nullMSEData is not defined");
            System.exit(0);
        }
        if (Double.isNaN(nullData[0]) || Double.isNaN(nullData[1]))
            nullData = null;

        return nullData;
    }

    /**
     * @param genes
     * @param exps
     * @return
     */
    public double[] setRegNull(int genes, int exps) {
        double[] nullData = new double[2];
        nullData[0] = Double.NaN;
        nullData[1] = Double.NaN;

        try {
            nullData[0] = meanRegNull[Math.min(exps - prm.JMIN, meanRegNull.length - 1)]
                    [Math.min(genes - prm.IMIN, meanRegNull[0].length - 1)];
        } catch (Exception e) {
            if (meanRegNull == null) {
            } else
                System.out.println("setMeanNull out of meanRegNull bounds: genes " + genes + "\tIMIN " +
                        prm.IMIN + "\tIMAX " + prm.IMAX + "\texps " + exps + "\tJMIN " +
                        prm.JMIN + "\tJMAX " + prm.JMAX + "\tmeanRegNull " + meanRegNull.length +
                        "\tprecrit_meanMSENull[0] " + meanRegNull[0].length);
        }

        try {
            nullData[1] = sdRegNull[Math.min(exps - prm.JMIN, sdRegNull.length - 1)]
                    [Math.min(genes - prm.IMIN, sdRegNull[0].length - 1)];
        } catch (Exception e) {
            if (sdRegNull == null) {
            } else
                System.out.println("setMeanNull out of sdRegNull bounds: genes " + genes + "\tIMIN " +
                        prm.IMIN + "\tIMAX " + prm.IMAX + "\texps " + exps + "\tJMIN " +
                        prm.JMIN + "\tJMAX " + prm.JMAX + "\tsdRegNull " + sdRegNull.length +
                        "\tprecrit_sdMSENull[0] " + sdRegNull[0].length);
        }

        if (Double.isNaN(nullData[0]) || Double.isNaN(nullData[1]))
            nullData = null;

        //System.out.println("setRegNull " + nullData[0] + "\t" + nullData[1]);
        return nullData;
    }

    /**
     * @param genes
     * @param exps
     * @return
     */
    public double[] setKendNull(int genes, int exps) {
        double[] nullData = new double[2];
        nullData[0] = Double.NaN;
        nullData[1] = Double.NaN;

        try {
            nullData[0] = meanKendNull[Math.min(exps - prm.JMIN, meanKendNull.length - 1)]
                    [Math.min(genes - prm.IMIN, meanKendNull[0].length - 1)];
        } catch (Exception e) {
            if (meanKendNull == null) {
                System.out.println("setKendNull null: genes " + genes + "\tIMIN " +
                        prm.IMIN + "\tIMAX " + prm.IMAX + "\texps " + exps + "\tJMIN " +
                        prm.JMIN + "\tJMAX " + prm.JMAX);
                e.printStackTrace();
            } else
                System.out.println("setKendNull out of meanKendNull bounds: genes " + genes + "\tIMIN " +
                        prm.IMIN + "\tIMAX " + prm.IMAX + "\texps " + exps + "\tJMIN " +
                        prm.JMIN + "\tJMAX " + prm.JMAX + "\tmeanKendNull " + meanKendNull.length +
                        "\tprecrit_meanKendNull[0] " + meanKendNull[0].length);
        }

        try {
            nullData[1] = sdKendNull[Math.min(exps - prm.JMIN, sdKendNull.length - 1)]
                    [Math.min(genes - prm.IMIN, sdKendNull[0].length - 1)];
        } catch (Exception e) {
            if (sdKendNull == null) {
                System.out.println("setKendNull out of sdKendNull bounds: genes " + genes + "\tIMIN " +
                        prm.IMIN + "\tIMAX " + prm.IMAX + "\texps " + exps + "\tJMIN " +
                        prm.JMIN + "\tJMAX " + prm.JMAX);
                e.printStackTrace();
            } else
                System.out.println("setKendNull null " + genes + "\tIMIN " +
                        prm.IMIN + "\tIMAX " + prm.IMAX + "\texps " + exps + "\tJMIN " +
                        prm.JMIN + "\tJMAX " + prm.JMAX + "\tsdKendNull " + sdKendNull.length +
                        "\tprecrit_sdKendNull[0] " + sdKendNull[0].length);
        }

        if (Double.isNaN(nullData[0]) || Double.isNaN(nullData[1]))
            nullData = null;

        //System.out.println("setKendNull " + nullData[0] + "\t" + nullData[1]);
        return nullData;
    }


    /**
     * @param genes
     * @param exps
     * @return
     */
    public double[] setBinaryNull(int genes, int exps) {
        double[] nullData = new double[2];
        nullData[0] = Double.NaN;
        nullData[1] = Double.NaN;

        try {
            nullData[0] = meanBinaryNull[Math.min(exps - prm.JMIN, meanBinaryNull.length - 1)]
                    [Math.min(genes - prm.IMIN, meanBinaryNull[0].length - 1)];
        } catch (Exception e) {
            if (meanBinaryNull == null) {
            } else
                System.out.println("setMeanNull out of meanRegNull bounds: genes " + genes + "\tIMIN " +
                        prm.IMIN + "\tIMAX " + prm.IMAX + "\texps " + exps + "\tJMIN " +
                        prm.JMIN + "\tJMAX " + prm.JMAX + "\tmeanRegNull " + meanBinaryNull.length +
                        "\tmeanBinaryNull[0] " + meanBinaryNull[0].length);
        }

        try {
            nullData[1] = sdBinaryNull[Math.min(exps - prm.JMIN, sdBinaryNull.length - 1)]
                    [Math.min(genes - prm.IMIN, sdBinaryNull[0].length - 1)];
        } catch (Exception e) {
            if (sdBinaryNull == null) {
            } else
                System.out.println("setBinaryNull out of sdBinaryNull bounds: genes " + genes + "\tIMIN " +
                        prm.IMIN + "\tIMAX " + prm.IMAX + "\texps " + exps + "\tJMIN " +
                        prm.JMIN + "\tJMAX " + prm.JMAX + "\tsdRegNull " + sdBinaryNull.length +
                        "\tsdBinaryNull[0] " + sdBinaryNull[0].length);
        }

        if (Double.isNaN(nullData[0]) || Double.isNaN(nullData[1]))
            nullData = null;

        //System.out.println("setRegNull " + nullData[0] + "\t" + nullData[1]);
        return nullData;
    }


    /**
     * @param genes
     * @param exps
     * @return
     */
    public double[] setCorNull(int genes, int exps) {
        double[] nullData = new double[2];
        nullData[0] = Double.NaN;
        nullData[1] = Double.NaN;

        try {
            nullData[0] = meanCorNull[Math.min(exps - prm.JMIN, meanCorNull.length - 1)]
                    [Math.min(genes - prm.IMIN, meanCorNull[0].length - 1)];
        } catch (Exception e) {
            if (meanCorNull == null) {
            } else
                System.out.println("setCorNull out of meanCorNull bounds: genes " + genes + "\tIMIN " +
                        prm.IMIN + "\tIMAX " + prm.IMAX + "\texps " + exps + "\tJMIN " +
                        prm.JMIN + "\tJMAX " + prm.JMAX + "\tmeanCorNull " + meanCorNull.length +
                        "\tmeanCorNull[0] " + meanCorNull[0].length);
        }

        try {
            nullData[1] = sdCorNull[Math.min(exps - prm.JMIN, sdCorNull.length - 1)]
                    [Math.min(genes - prm.IMIN, sdCorNull[0].length - 1)];
        } catch (Exception e) {
            if (sdCorNull == null) {
            } else
                System.out.println("sdCorNull out of sdCorNull bounds: genes " + genes + "\tIMIN " +
                        prm.IMIN + "\tIMAX " + prm.IMAX + "\texps " + exps + "\tJMIN " +
                        prm.JMIN + "\tJMAX " + prm.JMAX + "\tsdCorNull " + sdCorNull.length +
                        "\tsdCorNull[0] " + sdCorNull[0].length);
        }

        if (Double.isNaN(nullData[0]) || Double.isNaN(nullData[1]))
            nullData = null;

        //System.out.println("setCorNull " + nullData[0] + "\t" + nullData[1]);
        return nullData;
    }

    /**
     * @param genes
     * @param exps
     * @return
     */
    public double[] setEucNull(int genes, int exps) {
        double[] nullData = new double[2];
        nullData[0] = Double.NaN;
        nullData[1] = Double.NaN;

        try {
            nullData[0] = meanEucNull[Math.min(exps - prm.JMIN, meanEucNull.length - 1)]
                    [Math.min(genes - prm.IMIN, meanEucNull[0].length - 1)];
        } catch (Exception e) {
            if (meanEucNull == null) {
            } else
                System.out.println("setCorNull out of meanEucNull bounds: genes " + genes + "\tIMIN " +
                        prm.IMIN + "\tIMAX " + prm.IMAX + "\texps " + exps + "\tJMIN " +
                        prm.JMIN + "\tJMAX " + prm.JMAX + "\tmeanEucNull " + meanEucNull.length +
                        "\tmeanEucNull[0] " + meanEucNull[0].length);
        }

        try {
            nullData[1] = sdEucNull[Math.min(exps - prm.JMIN, sdEucNull.length - 1)]
                    [Math.min(genes - prm.IMIN, sdEucNull[0].length - 1)];
        } catch (Exception e) {
            if (sdEucNull == null) {
            } else
                System.out.println("sdEucNull out of sdCorNull bounds: genes " + genes + "\tIMIN " +
                        prm.IMIN + "\tIMAX " + prm.IMAX + "\texps " + exps + "\tJMIN " +
                        prm.JMIN + "\tJMAX " + prm.JMAX + "\tsdEucNull " + sdEucNull.length +
                        "\tsdEucNull[0] " + sdEucNull[0].length);
        }

        if (Double.isNaN(nullData[0]) || Double.isNaN(nullData[1]))
            nullData = null;

        //System.out.println("setEucNull " + nullData[0] + "\t" + nullData[1]);
        return nullData;
    }

    /**
     * @param genes
     * @param exps
     * @return
     */
    public double[] setSpearNull(int genes, int exps) {
        double[] nullData = new double[2];
        nullData[0] = Double.NaN;
        nullData[1] = Double.NaN;

        try {
            nullData[0] = meanSpearNull[Math.min(exps - prm.JMIN, meanSpearNull.length - 1)]
                    [Math.min(genes - prm.IMIN, meanSpearNull[0].length - 1)];
        } catch (Exception e) {
            if (meanSpearNull == null) {
                System.out.println("meanSpearNull is null");
            } else
                System.out.println("setMeanNull out of meanSpearNull bounds: genes " + genes + "\tIMIN " +
                        prm.IMIN + "\tIMAX " + prm.IMAX + "\texps " + exps + "\tJMIN " +
                        prm.JMIN + "\tJMAX " + prm.JMAX + "\tmeanSpearNull " + meanSpearNull.length +
                        "\tmeanSpearNull[0] " + meanSpearNull[0].length);
        }

        try {
            nullData[1] = sdSpearNull[Math.min(exps - prm.JMIN, sdSpearNull.length - 1)]
                    [Math.min(genes - prm.IMIN, sdSpearNull[0].length - 1)];
        } catch (Exception e) {
            if (sdSpearNull == null) {
                System.out.println("sdSpearNull is null");
            } else
                System.out.println("sdSpearNull out of sdSpearNull bounds: genes " + genes + "\tIMIN " +
                        prm.IMIN + "\tIMAX " + prm.IMAX + "\texps " + exps + "\tJMIN " +
                        prm.JMIN + "\tJMAX " + prm.JMAX + "\tsdSpearNull " + sdSpearNull.length +
                        "\tsdSpearNull[0] " + sdSpearNull[0].length);
        }

        if (Double.isNaN(nullData[0]) || Double.isNaN(nullData[1]))
            nullData = null;

        return nullData;
    }

    /**
     * @param genes
     * @param exps
     * @return
     */
    public double[] setMeanNull(int genes, int exps) {
        double[] nullData = new double[2];
        nullData[0] = Double.NaN;
        nullData[1] = Double.NaN;

        try {
            nullData[0] = meanMeanNull[Math.min(exps - prm.JMIN, meanMeanNull.length - 1)]
                    [Math.min(genes - prm.IMIN, meanMeanNull[0].length - 1)];
        } catch (Exception e) {
            if (meanMeanNull == null) {
            } else
                System.out.println("setMeanNull out of meanMeanNull bounds: genes " + genes + "\tIMIN " +
                        prm.IMIN + "\tIMAX " + prm.IMAX + "\texps " + exps + "\tJMIN " +
                        prm.JMIN + "\tJMAX " + prm.JMAX + "\tmeanMeanNull " + meanMeanNull.length +
                        "\tprecrit_meanMSENull[0] " + meanMeanNull[0].length);
        }

        try {
            nullData[1] = sdMeanNull[Math.min(exps - prm.JMIN, sdMeanNull.length - 1)]
                    [Math.min(genes - prm.IMIN, sdMeanNull[0].length - 1)];
        } catch (Exception e) {
            if (sdMeanNull == null) {
            } else
                System.out.println("setMeanNull out of sdRegNull bounds: genes " + genes + "\tIMIN " +
                        prm.IMIN + "\tIMAX " + prm.IMAX + "\texps " + exps + "\tJMIN " +
                        prm.JMIN + "\tJMAX " + prm.JMAX + "\tsdMeanNull " + sdMeanNull.length +
                        "\tprecrit_sdMSENull[0] " + sdMeanNull[0].length);
        }

        if (Double.isNaN(nullData[0]) || Double.isNaN(nullData[1]))
            nullData = null;

        return nullData;
    }

    /**
     * @param genes
     * @return
     */
    public double[] setInteractionNull(int genes) {
        double[] nullData = null;
        nullData = new double[2];
        try {
            nullData[0] = meanInteractNull[Math.min(genes - prm.IMIN, meanInteractNull.length - 1)];
        } catch (Exception e) {
            nullData[0] = Double.NaN;
        }

        try {
            nullData[1] = sdInteractNull[Math.min(genes - prm.IMIN, sdInteractNull.length - 1)];
        } catch (Exception e) {
            nullData[1] = Double.NaN;
        }

        if (Double.isNaN(nullData[0]) || Double.isNaN(nullData[1]))
            nullData = null;
        return nullData;
    }

    /**
     * @param genes
     * @return
     */
    public double[] setTFNull(int genes) {
        double[] nullData = null;
        nullData = new double[2];
        try {
            nullData[0] = meanTFNull[Math.min(genes - prm.IMIN, meanTFNull.length - 1)];
        } catch (Exception e) {
            nullData[0] = Double.NaN;
        }

        try {
            nullData[1] = sdTFNull[Math.min(genes - prm.IMIN, sdTFNull.length - 1)];
        } catch (Exception e) {
            nullData[1] = Double.NaN;
        }

        if (Double.isNaN(nullData[0]) || Double.isNaN(nullData[1]))
            nullData = null;

        return nullData;
    }

    /**
     * @param genes
     * @return
     */
    public double[] setFeatNull(int genes) {
        double[] nullData = null;
        nullData = new double[2];
        try {
            nullData[0] = meanFeatNull[Math.min(genes - prm.IMIN, meanFeatNull.length - 1)];
        } catch (Exception e) {
            nullData[0] = Double.NaN;
        }

        try {
            nullData[1] = sdFeatNull[Math.min(genes - prm.IMIN, sdFeatNull.length - 1)];
        } catch (Exception e) {
            nullData[1] = Double.NaN;
        }

        if (Double.isNaN(nullData[0]) || Double.isNaN(nullData[1]))
            nullData = null;

        return nullData;
    }

    /**
     *
     */
    public void setCriteriaNull() {
        //based on total expression criterion or interaction criterion or FEATURES
        if ((prm.crit.isTotalCrit && prm.USE_MEAN)) { // || (prm.MEANRMEAN_PATH == null && prm.MEANCMEAN_PATH == null)) {
            if (prm.MEANMEAN_PATH != null && prm.SDMEAN_PATH != null)
                loadMeanNulls(prm.MEANMEAN_PATH, prm.SDMEAN_PATH);
            else {
                System.out.println("meanT null required but missing");
                System.exit(0);
            }
        } else if ((prm.crit.isRowCrit && prm.USE_MEAN)) {// || (prm.MEANMEAN_PATH == null && prm.MEANCMEAN_PATH == null)) {
            if (prm.MEANRMEAN_PATH != null && prm.SDRMEAN_PATH != null)
                loadMeanNulls(prm.MEANRMEAN_PATH, prm.SDRMEAN_PATH);
            else {
                System.out.println("meanR null required but missing");
                System.exit(0);
            }
        } else if ((prm.crit.isColCrit && prm.USE_MEAN)) {// || (prm.MEANRMEAN_PATH == null && prm.MEANMEAN_PATH == null)) {
            if (prm.MEANCMEAN_PATH != null && prm.SDCMEAN_PATH != null)
                loadMeanNulls(prm.MEANCMEAN_PATH, prm.SDCMEAN_PATH);
            else {
                System.out.println("meanC null required but missing");
                System.exit(0);
            }
        }

        if (StringUtil.countOccur(MINER_STATIC.CRIT_LABELS[prm.crit.crit - 1], "_") == 0 && MINER_STATIC.CRIT_LABELS[prm.crit.crit - 1] == "MEAN") {
            loadMeanNulls(prm.MEANMEAN_PATH, prm.SDMEAN_PATH);
        } else if (StringUtil.countOccur(MINER_STATIC.CRIT_LABELS[prm.crit.crit - 1], "_") == 0 && MINER_STATIC.CRIT_LABELS[prm.crit.crit - 1] == "MEDRMEAN") {
            loadMeanNulls(prm.MEANRMEAN_PATH, prm.SDRMEAN_PATH);
        } else if (StringUtil.countOccur(MINER_STATIC.CRIT_LABELS[prm.crit.crit - 1], "_") == 0 && MINER_STATIC.CRIT_LABELS[prm.crit.crit - 1] == "MEDCMEAN") {
            loadMeanNulls(prm.MEANCMEAN_PATH, prm.SDCMEAN_PATH);
        }


        if (prm.crit.isMSE) {
            loadExprNulls(prm.MEANMSE_PATH, prm.SDMSE_PATH);
        } else if (prm.crit.isMSER) {
            loadExprNulls(prm.MEANMSER_PATH, prm.SDMSER_PATH);
        } else if (prm.crit.isMSEC) {
            loadExprNulls(prm.MEANMSEC_PATH, prm.SDMSEC_PATH);
        } else if (prm.crit.isMAD) {
            loadExprNulls(prm.MEANMADR_PATH, prm.SDMADR_PATH);
        }

        if (prm.crit.isonlyFEM) {// && prm.MEANFEM_PATH != null) {
            loadRegNulls(prm.MEANFEM_PATH, prm.SDFEM_PATH);
        } else if (prm.crit.isFEMR) {//  && prm.MEANFEMR_PATH != null) {
            loadRegNulls(prm.MEANFEMR_PATH, prm.SDFEMR_PATH);
        } else if (prm.crit.isFEMC) {//  && prm.MEANFEMC_PATH != null) {
            loadRegNulls(prm.MEANFEMC_PATH, prm.SDFEMC_PATH);
        } else if (prm.crit.isonlyLARS) {//  && prm.MEANLARS_PATH != null) {
            loadRegNulls(prm.MEANLARS_PATH, prm.SDLARS_PATH);
        } else if (prm.crit.isLARSRE) {//  && prm.MEANLARSRE_PATH != null) {
            loadRegNulls(prm.MEANLARSRE_PATH, prm.SDLARSRE_PATH);
        } else if (prm.crit.isLARSCE) {//  && prm.MEANLARSCE_PATH != null) {
            loadRegNulls(prm.MEANLARSCE_PATH, prm.SDLARSCE_PATH);
        }


        //System.out.println("loadKendallNulls "+prm.MEANKENDC_PATH +"\t"+prm.SDKENDC_PATH);
        if (prm.crit.KENDALLIndex == 1) {
            loadKendallNulls(prm.MEANKEND_PATH, prm.SDKEND_PATH);
        } else if (prm.crit.KENDALLIndex == 2) {
            loadKendallNulls(prm.MEANKENDR_PATH, prm.SDKENDR_PATH);
        } else if (prm.crit.KENDALLIndex == 3) {
            loadKendallNulls(prm.MEANKENDC_PATH, prm.SDKENDC_PATH);
        }

        if (prm.crit.binary_axis == 1) {
            loadBinaryNulls(prm.MEANBINARY_PATH, prm.SDBINARY_PATH);
        } else if (prm.crit.binary_axis == 2) {
            loadBinaryNulls(prm.MEANBINARYR_PATH, prm.SDBINARYR_PATH);
        } else if (prm.crit.binary_axis == 3) {
            loadBinaryNulls(prm.MEANBINARYC_PATH, prm.SDBINARYC_PATH);
        }

        if (prm.crit.CORIndex == 1 && !prm.crit.nonull) {
            loadCorNulls(prm.MEANCOR_PATH, prm.SDCOR_PATH);
        } else if (prm.crit.CORIndex == 2 && !prm.crit.nonull) {
            loadCorNulls(prm.MEANCORR_PATH, prm.SDCORR_PATH);
        } else if (prm.crit.CORIndex == 3 && !prm.crit.nonull) {
            loadCorNulls(prm.MEANCORC_PATH, prm.SDCORC_PATH);
        }

        if (prm.crit.EUCIndex == 1) {
            loadEucNulls(prm.MEANEUC_PATH, prm.SDEUC_PATH);
        } else if (prm.crit.EUCIndex == 2) {
            loadEucNulls(prm.MEANEUCR_PATH, prm.SDEUCR_PATH);
        } else if (prm.crit.EUCIndex == 3) {
            loadEucNulls(prm.MEANEUCC_PATH, prm.SDEUCC_PATH);
        }

        if (prm.crit.SPEARIndex == 1) {
            loadSpearNulls(prm.MEANSPEAR_PATH, prm.SDSPEAR_PATH);
        } else if (prm.crit.SPEARIndex == 2) {
            loadSpearNulls(prm.MEANSPEARR_PATH, prm.SDSPEARR_PATH);
        } else if (prm.crit.SPEARIndex == 3) {
            loadSpearNulls(prm.MEANSPEARC_PATH, prm.SDSPEARC_PATH);
        }

    }


    /**
     *
     */
    public void setPreCriteriaNull() {

        if (prm.precrit.isTotalCrit && prm.USE_MEAN && prm.precrit.neednull[0]) {
            if (prm.MEANMEAN_PATH != null && prm.SDMEAN_PATH != null) {
                File t1 = new File(prm.MEANMEAN_PATH);
                if (!t1.exists()) {
                    System.out.println("prm.MEANMEAN_PATH broken " + prm.MEANMEAN_PATH);
                    System.exit(0);
                }
                File t2 = new File(prm.SDMEAN_PATH);
                if (!t2.exists()) {
                    System.out.println("prm.SDMEAN_PATH broken " + prm.SDMEAN_PATH);
                    System.exit(0);
                }
                loadPreCritMeanNulls(prm.MEANMEAN_PATH, prm.SDMEAN_PATH);
            } else {
                System.out.println("MEAN PATH broken " + prm.MEANMEAN_PATH + "\t" + prm.SDMEAN_PATH);
                System.exit(0);
            }
        } else if (prm.precrit.isRowCrit && prm.USE_MEAN && prm.precrit.neednull[0]) {
            if (prm.MEANRMEAN_PATH != null && prm.SDRMEAN_PATH != null) {
                File t1 = new File(prm.MEANRMEAN_PATH);
                if (!t1.exists()) {
                    System.out.println("prm.MEANRMEAN_PATH broken " + prm.MEANRMEAN_PATH);
                    System.exit(0);
                }
                File t2 = new File(prm.SDRMEAN_PATH);
                if (!t2.exists()) {
                    System.out.println("prm.SDRMEAN_PATH broken " + prm.MEANCMEAN_PATH + "\t" + prm.SDCMEAN_PATH);
                    System.exit(0);
                }
                loadPreCritMeanNulls(prm.MEANRMEAN_PATH, prm.SDRMEAN_PATH);
            } else {
                System.out.println("MEANR PATH broken " + prm.MEANRMEAN_PATH + "\t" + prm.SDRMEAN_PATH);
                System.exit(0);
            }
        } else if (prm.precrit.isColCrit && prm.USE_MEAN && prm.precrit.neednull[0]) {
            if (prm.MEANCMEAN_PATH != null && prm.SDCMEAN_PATH != null) {
                File t1 = new File(prm.MEANCMEAN_PATH);
                if (!t1.exists()) {
                    System.out.println("prm.MEANCMEAN_PATH broken " + prm.MEANCMEAN_PATH);
                    System.exit(0);
                }
                File t2 = new File(prm.SDCMEAN_PATH);
                if (!t2.exists()) {
                    System.out.println("prm.SDCMEAN_PATH broken " + prm.SDCMEAN_PATH);
                    System.exit(0);
                }
                loadPreCritMeanNulls(prm.MEANCMEAN_PATH, prm.SDCMEAN_PATH);
            } else {
                System.out.println("MEANC PATH broken " + prm.MEANCMEAN_PATH + "\t" + prm.SDCMEAN_PATH);
                System.exit(0);
            }
        }

        if (prm.precrit.isMSE && prm.precrit.neednull[1]) {
            if (prm.MEANMSE_PATH != null && prm.SDMSE_PATH != null) {
                File t1 = new File(prm.MEANMSE_PATH);
                if (!t1.exists()) {
                    System.out.println("prm.MEANMSE_PATH broken " + prm.MEANMSE_PATH);
                    System.exit(0);
                }
                File t2 = new File(prm.SDMSE_PATH);
                if (!t2.exists()) {
                    System.out.println("prm.SDMSE_PATH broken " + prm.SDMSE_PATH);
                    System.exit(0);
                }
                loadPreCritMSENulls(prm.MEANMSE_PATH, prm.SDMSE_PATH);
            } else {
                System.out.println("MSE null PATH broken " + prm.MEANMSE_PATH + "\t" + prm.SDMSE_PATH);
                System.exit(0);
            }
        } else if (prm.precrit.isMSER && prm.precrit.neednull[1]) {
            if (prm.MEANMSER_PATH != null && prm.SDMSER_PATH != null) {
                File t1 = new File(prm.MEANMSER_PATH);
                if (!t1.exists()) {
                    System.out.println("prm.MEANMSER_PATH broken " + prm.MEANMSER_PATH);
                    System.exit(0);
                }
                File t2 = new File(prm.SDMSER_PATH);
                if (!t2.exists()) {
                    System.out.println("prm.SDMSER_PATH broken " + prm.SDMSER_PATH);
                    System.exit(0);
                }
                loadPreCritMSENulls(prm.MEANMSER_PATH, prm.SDMSER_PATH);
            } else {
                System.out.println("MSER null PATH broken " + prm.MEANMSER_PATH + "\t" + prm.SDMSER_PATH);
                System.exit(0);
            }
        } else if (prm.precrit.isMSEC && prm.precrit.neednull[1]) {
            if (prm.MEANMSEC_PATH != null && prm.SDMSEC_PATH != null) {
                File t1 = new File(prm.MEANMSEC_PATH);
                if (!t1.exists()) {
                    System.out.println("prm.MEANMSEC_PATH broken " + prm.MEANMSEC_PATH);
                    System.exit(0);
                }
                File t2 = new File(prm.SDMSEC_PATH);
                if (!t2.exists()) {
                    System.out.println("prm.SDMSEC_PATH broken " + prm.SDMSEC_PATH);
                    System.exit(0);
                }
                loadPreCritMSENulls(prm.MEANMSEC_PATH, prm.SDMSEC_PATH);
            } else {
                System.out.println("MSEC null PATH broken " + prm.MEANMSEC_PATH + "\t" + prm.SDMSEC_PATH);
                System.exit(0);
            }
        } else if (prm.precrit.isMAD) {
            if (prm.MEANMADR_PATH != null && prm.SDMADR_PATH != null) {
                File t1 = new File(prm.MEANMADR_PATH);
                if (!t1.exists()) {
                    System.out.println("prm.MEANMADR_PATH broken " + prm.MEANMADR_PATH);
                    System.exit(0);
                }
                File t2 = new File(prm.SDMADR_PATH);
                if (!t2.exists()) {
                    System.out.println("prm.SDMADR_PATH broken " + prm.SDMADR_PATH);
                    System.exit(0);
                }
                loadPreCritMSENulls(prm.MEANMADR_PATH, prm.SDMADR_PATH);
            } else {
                System.out.println("MADR null PATH broken " + prm.MEANMADR_PATH + "\t" + prm.SDMADR_PATH);
                System.exit(0);
            }
        }

        if (prm.precrit.isonlyFEM && prm.MEANFEM_PATH != null && prm.precrit.neednull[2]) {
            if (prm.MEANFEM_PATH != null && prm.SDFEM_PATH != null) {
                File t1 = new File(prm.MEANFEM_PATH);
                if (!t1.exists()) {
                    System.out.println("prm.MEANFEM_PATH broken " + prm.MEANFEM_PATH);
                    System.exit(0);
                }
                File t2 = new File(prm.SDFEM_PATH);
                if (!t2.exists()) {
                    System.out.println("prm.SDFEM_PATH broken " + prm.SDFEM_PATH);
                    System.exit(0);
                }
                loadPreCritRegNulls(prm.MEANFEM_PATH, prm.SDFEM_PATH);
            } else {
                System.out.println("FEM null PATH broken " + prm.MEANFEM_PATH + "\t" + prm.SDFEM_PATH);
                System.exit(0);
            }
        } else if (prm.precrit.isFEMR && prm.MEANFEMR_PATH != null && prm.precrit.neednull[2]) {
            if (prm.MEANFEMR_PATH != null && prm.SDFEMR_PATH != null) {
                File t1 = new File(prm.MEANFEMR_PATH);
                if (!t1.exists()) {
                    System.out.println("prm.MEANFEMR_PATH broken " + prm.MEANFEMR_PATH);
                    System.exit(0);
                }
                File t2 = new File(prm.SDFEMR_PATH);
                if (!t2.exists()) {
                    System.out.println("prm.SDFEMR_PATH broken " + prm.SDFEMR_PATH);
                    System.exit(0);
                }
                loadPreCritRegNulls(prm.MEANFEMR_PATH, prm.SDFEMR_PATH);
            } else {
                System.out.println("FEMR null PATH broken " + prm.MEANFEMR_PATH + "\t" + prm.SDFEMR_PATH);
                System.exit(0);
            }
        } else if (prm.precrit.isFEMC && prm.MEANFEMC_PATH != null && prm.precrit.neednull[2]) {
            if (prm.MEANFEMC_PATH != null && prm.SDFEMC_PATH != null) {
                File t1 = new File(prm.MEANFEMC_PATH);
                if (!t1.exists()) {
                    System.out.println("prm.MEANFEMC_PATH broken " + prm.MEANFEMC_PATH);
                    System.exit(0);
                }
                File t2 = new File(prm.SDFEMC_PATH);
                if (!t2.exists()) {
                    System.out.println("prm.SDFEMC_PATH broken " + prm.SDFEMC_PATH);
                    System.exit(0);
                }
                loadPreCritRegNulls(prm.MEANFEMC_PATH, prm.SDFEMC_PATH);
            } else {
                System.out.println("FEMC null PATH broken " + prm.MEANFEMC_PATH + "\t" + prm.SDFEMC_PATH);
                System.exit(0);
            }
        } else if (prm.precrit.isonlyLARS && prm.MEANLARS_PATH != null) {
            if (prm.MEANLARS_PATH != null && prm.SDLARS_PATH != null) {
                File t1 = new File(prm.MEANLARS_PATH);
                if (!t1.exists()) {
                    System.out.println("prm.MEANLARS_PATH broken " + prm.MEANLARS_PATH);
                    System.exit(0);
                }
                File t2 = new File(prm.SDLARS_PATH);
                if (!t2.exists()) {
                    System.out.println("prm.SDLARS_PATH broken " + prm.SDLARS_PATH);
                    System.exit(0);
                }
                loadPreCritRegNulls(prm.MEANLARS_PATH, prm.SDLARS_PATH);
            } else {
                System.out.println("LARS null PATH broken " + prm.MEANLARS_PATH + "\t" + prm.SDLARS_PATH);
                System.exit(0);
            }
        } else if (prm.precrit.isLARSRE && prm.MEANLARSRE_PATH != null) {
            if (prm.MEANLARSRE_PATH != null && prm.SDLARSRE_PATH != null) {
                File t1 = new File(prm.MEANLARSRE_PATH);
                if (!t1.exists()) {
                    System.out.println("prm.MEANLARSRE_PATH broken " + prm.MEANLARSRE_PATH);
                    System.exit(0);
                }
                File t2 = new File(prm.SDLARSRE_PATH);
                if (!t2.exists()) {
                    System.out.println("prm.SDLARSRE_PATH broken " + prm.SDLARSRE_PATH);
                    System.exit(0);
                }
                loadPreCritRegNulls(prm.MEANLARSRE_PATH, prm.SDLARSRE_PATH);
            } else {
                System.out.println("LARSRE null PATH broken " + prm.MEANLARSRE_PATH + "\t" + prm.SDLARSRE_PATH);
                System.exit(0);
            }
        } else if (prm.precrit.isLARSCE && prm.MEANLARSCE_PATH != null) {
            if (prm.MEANLARSCE_PATH != null && prm.SDLARSCE_PATH != null) {
                File t1 = new File(prm.MEANLARSCE_PATH);
                if (!t1.exists()) {
                    System.out.println("prm.MEANLARSCE_PATH broken " + prm.MEANLARSCE_PATH);
                    System.exit(0);
                }
                File t2 = new File(prm.SDLARSCE_PATH);
                if (!t2.exists()) {
                    System.out.println("prm.SDLARSCE_PATH broken " + prm.SDLARSCE_PATH);
                    System.exit(0);
                }
                loadPreCritRegNulls(prm.MEANLARSCE_PATH, prm.SDLARSCE_PATH);
            } else {
                System.out.println("LARSCE null PATH broken " + prm.MEANLARSCE_PATH + "\t" + prm.SDLARSCE_PATH);
                System.exit(0);
            }
        }

        if (prm.precrit.KENDALLIndex == 1 && prm.precrit.neednull[3]) {
            if (prm.MEANKEND_PATH != null && prm.SDKEND_PATH != null) {
                File t1 = new File(prm.MEANKEND_PATH);
                if (!t1.exists()) {
                    System.out.println("prm.MEANKEND_PATH broken " + prm.MEANKEND_PATH);
                    System.exit(0);
                }
                File t2 = new File(prm.SDKEND_PATH);
                if (!t2.exists()) {
                    System.out.println("prm.SDKEND_PATH broken " + prm.SDKEND_PATH);
                    System.exit(0);
                }
                loadPreCritKENDALLNulls(prm.MEANKEND_PATH, prm.SDKEND_PATH);
            } else {
                System.out.println("KENDALL null PATH broken " + prm.MEANKEND_PATH + "\t" + prm.SDKEND_PATH);
                System.exit(0);
            }
        } else if (prm.precrit.KENDALLIndex == 2 && prm.precrit.neednull[3]) {
            if (prm.MEANKENDR_PATH != null && prm.SDKENDR_PATH != null) {
                File t1 = new File(prm.MEANKENDR_PATH);
                if (!t1.exists()) {
                    System.out.println("prm.MEANKENDR_PATH broken " + prm.MEANKENDR_PATH);
                    System.exit(0);
                }
                File t2 = new File(prm.SDKENDR_PATH);
                if (!t2.exists()) {
                    System.out.println("prm.SDKENDR_PATH broken " + prm.SDKENDR_PATH);
                    System.exit(0);
                }
                loadPreCritKENDALLNulls(prm.MEANKENDR_PATH, prm.SDKENDR_PATH);
            } else {
                System.out.println("KENDALLR null PATH broken " + prm.MEANKENDR_PATH + "\t" + prm.SDKENDR_PATH);
                System.exit(0);
            }
        } else if (prm.precrit.KENDALLIndex == 3 && prm.precrit.neednull[3]) {
            if (prm.MEANKENDC_PATH != null && prm.SDKENDC_PATH != null) {
                File t1 = new File(prm.MEANKENDC_PATH);
                if (!t1.exists()) {
                    System.out.println("prm.MEANKENDC_PATH broken " + prm.MEANKENDC_PATH);
                    System.exit(0);
                }
                File t2 = new File(prm.SDKENDC_PATH);
                if (!t2.exists()) {
                    System.out.println("prm.SDKENDC_PATH broken " + prm.SDKENDC_PATH);
                    System.exit(0);
                }
                loadPreCritKENDALLNulls(prm.MEANKENDC_PATH, prm.SDKENDC_PATH);
            } else {
                System.out.println("KENDALLC null PATH broken " + prm.MEANKENDC_PATH + "\t" + prm.SDKENDC_PATH);
                System.exit(0);
            }
        }
        if (prm.precrit.binary_axis == 1 && prm.precrit.neednull[7]) {
            if (prm.MEANBINARY_PATH != null && prm.SDBINARY_PATH != null) {
                File t1 = new File(prm.MEANBINARY_PATH);
                if (!t1.exists()) {
                    System.out.println("prm.MEANBINARY_PATH broken " + prm.MEANBINARY_PATH);
                    System.exit(0);
                }
                File t2 = new File(prm.SDBINARY_PATH);
                if (!t2.exists()) {
                    System.out.println("prm.SDBINARY_PATH broken " + prm.SDBINARY_PATH);
                    System.exit(0);
                }
                loadPreCritBinaryNulls(prm.MEANBINARY_PATH, prm.SDBINARY_PATH);
            } else {
                System.out.println("BINARY null PATH broken " + prm.MEANBINARY_PATH + "\t" + prm.SDBINARY_PATH);
                System.exit(0);
            }
        } else if (prm.precrit.binary_axis == 2 && prm.precrit.neednull[7]) {
            if (prm.MEANBINARYR_PATH != null && prm.SDBINARYR_PATH != null) {
                File t1 = new File(prm.MEANBINARYR_PATH);
                if (!t1.exists()) {
                    System.out.println("prm.MEANBINARYR_PATH broken " + prm.MEANBINARYR_PATH);
                    System.exit(0);
                }
                File t2 = new File(prm.SDBINARYR_PATH);
                if (!t2.exists()) {
                    System.out.println("prm.SDBINARYR_PATH broken " + prm.SDBINARYR_PATH);
                    System.exit(0);
                }
                loadPreCritBinaryNulls(prm.MEANBINARYR_PATH, prm.SDBINARYR_PATH);
            } else {
                System.out.println("BINARYR null PATH broken " + prm.MEANBINARYR_PATH + "\t" + prm.SDBINARYR_PATH);
                System.exit(0);
            }
        } else if (prm.precrit.binary_axis == 3 && prm.precrit.neednull[7]) {
            if (prm.MEANBINARYC_PATH != null && prm.SDBINARYC_PATH != null) {
                File t1 = new File(prm.MEANBINARYC_PATH);
                if (!t1.exists()) {
                    System.out.println("prm.MEANBINARYC_PATH broken " + prm.MEANBINARYC_PATH);
                    System.exit(0);
                }
                File t2 = new File(prm.SDBINARYC_PATH);
                if (!t2.exists()) {
                    System.out.println("prm.SDBINARYC_PATH broken " + prm.SDBINARYC_PATH);
                    System.exit(0);
                }
                loadPreCritBinaryNulls(prm.MEANBINARYC_PATH, prm.SDBINARYC_PATH);
            } else {
                System.out.println("BINARYC null PATH broken " + prm.MEANBINARYC_PATH + "\t" + prm.SDBINARYC_PATH);
                System.exit(0);
            }
        }

        if (prm.precrit.CORIndex == 1 && prm.precrit.neednull[4]) {
            if (prm.MEANCOR_PATH != null && prm.SDCOR_PATH != null) {
                File t1 = new File(prm.MEANCOR_PATH);
                if (!t1.exists()) {
                    System.out.println("prm.MEANCOR_PATH broken " + prm.MEANCOR_PATH);
                    System.exit(0);
                }
                File t2 = new File(prm.SDCOR_PATH);
                if (!t2.exists()) {
                    System.out.println("prm.SDCOR_PATH broken " + prm.SDCOR_PATH);
                    System.exit(0);
                }
                loadPreCritCorNulls(prm.MEANCOR_PATH, prm.SDCOR_PATH);
            } else {
                System.out.println("COR null PATH broken " + prm.MEANCOR_PATH + "\t" + prm.SDCOR_PATH);
                System.exit(0);
            }
        } else if (prm.precrit.CORIndex == 2 && prm.precrit.neednull[4]) {
            if (prm.MEANCORR_PATH != null && prm.SDCORR_PATH != null) {
                File t1 = new File(prm.MEANCORR_PATH);
                if (!t1.exists()) {
                    System.out.println("prm.MEANCORR_PATH broken " + prm.MEANCORR_PATH);
                    System.exit(0);
                }
                File t2 = new File(prm.SDCORR_PATH);
                if (!t2.exists()) {
                    System.out.println("prm.SDCORR_PATH broken " + prm.SDCORR_PATH);
                    System.exit(0);
                }
                loadPreCritCorNulls(prm.MEANCORR_PATH, prm.SDCORR_PATH);
            } else {
                System.out.println("CORR null PATH broken " + prm.MEANCORR_PATH + "\t" + prm.SDCORR_PATH);
                System.exit(0);
            }
        } else if (prm.precrit.CORIndex == 3 && prm.precrit.neednull[4]) {
            if (prm.MEANCORC_PATH != null && prm.SDCORC_PATH != null) {
                File t1 = new File(prm.MEANCORC_PATH);
                if (!t1.exists()) {
                    System.out.println("prm.MEANCORC_PATH broken " + prm.MEANCORC_PATH);
                    System.exit(0);
                }
                File t2 = new File(prm.SDCORC_PATH);
                if (!t2.exists()) {
                    System.out.println("prm.SDCORC_PATH broken " + prm.SDCORC_PATH);
                    System.exit(0);
                }
                loadPreCritCorNulls(prm.MEANCORC_PATH, prm.SDCORC_PATH);
            } else {
                System.out.println("CORC null PATH broken " + prm.MEANCORC_PATH + "\t" + prm.SDCORC_PATH);
                System.exit(0);
            }
        }

        if (prm.precrit.EUCIndex == 1 && prm.precrit.neednull[5]) {
            if (prm.MEANEUC_PATH != null && prm.SDEUC_PATH != null) {
                File t1 = new File(prm.MEANEUC_PATH);
                if (!t1.exists()) {
                    System.out.println("prm.MEANEUC_PATH broken " + prm.MEANEUC_PATH);
                    System.exit(0);
                }
                File t2 = new File(prm.SDEUC_PATH);
                if (!t2.exists()) {
                    System.out.println("prm.SDEUC_PATH broken " + prm.SDEUC_PATH);
                    System.exit(0);
                }
                loadPreCritEucNulls(prm.MEANEUC_PATH, prm.SDEUC_PATH);
            } else {
                System.out.println("EUCR null PATH broken " + prm.MEANEUC_PATH + "\t" + prm.SDEUC_PATH);
                System.exit(0);
            }
        } else if (prm.precrit.EUCIndex == 2 && prm.precrit.neednull[5]) {
            if (prm.MEANEUCR_PATH != null && prm.SDEUCR_PATH != null) {
                File t1 = new File(prm.MEANEUCR_PATH);
                if (!t1.exists()) {
                    System.out.println("prm.MEANEUCR_PATH broken " + prm.MEANEUCR_PATH);
                    System.exit(0);
                }
                File t2 = new File(prm.SDEUCR_PATH);
                if (!t2.exists()) {
                    System.out.println("prm.SDEUCR_PATH broken " + prm.SDEUCR_PATH);
                    System.exit(0);
                }
                loadPreCritEucNulls(prm.MEANEUCR_PATH, prm.SDEUCR_PATH);
            } else {
                System.out.println("EUCR null PATH broken " + prm.MEANEUCR_PATH + "\t" + prm.SDEUCR_PATH);
                System.exit(0);
            }
        } else if (prm.precrit.EUCIndex == 3 && prm.precrit.neednull[5]) {
            if (prm.MEANEUCC_PATH != null && prm.SDEUCC_PATH != null) {
                File t1 = new File(prm.MEANEUCC_PATH);
                if (!t1.exists()) {
                    System.out.println("prm.MEANEUCC_PATH broken " + prm.MEANEUCC_PATH);
                    System.exit(0);
                }
                File t2 = new File(prm.SDEUCC_PATH);
                if (!t2.exists()) {
                    System.out.println("prm.SDEUCC_PATH broken " + prm.SDEUCC_PATH);
                    System.exit(0);
                }
                loadPreCritEucNulls(prm.MEANEUCC_PATH, prm.SDEUCC_PATH);
            } else {
                System.out.println("EUCC" +
                        " null PATH broken " + prm.MEANEUCC_PATH + "\t" + prm.SDEUCC_PATH);
                System.exit(0);
            }
        }


        if (prm.precrit.SPEARIndex == 1 && prm.precrit.neednull[6]) {
            if (prm.MEANSPEAR_PATH != null && prm.SDSPEAR_PATH != null) {
                File t1 = new File(prm.MEANSPEAR_PATH);
                if (!t1.exists()) {
                    System.out.println("prm.MEANSPEAR_PATH broken " + prm.MEANSPEAR_PATH);
                    System.exit(0);
                }
                File t2 = new File(prm.SDSPEAR_PATH);
                if (!t2.exists()) {
                    System.out.println("prm.SDSPEAR_PATH broken " + prm.SDSPEAR_PATH);
                    System.exit(0);
                }
                loadPreCritSpearNulls(prm.MEANSPEAR_PATH, prm.SDSPEAR_PATH);
            } else {
                System.out.println("SPEAR null PATH broken " + prm.MEANSPEAR_PATH + "\t" + prm.SDSPEAR_PATH);
                System.exit(0);
            }
        } else if (prm.precrit.SPEARIndex == 2 && prm.precrit.neednull[6]) {
            if (prm.MEANSPEARR_PATH != null && prm.SDSPEARR_PATH != null) {
                File t1 = new File(prm.MEANSPEARR_PATH);
                if (!t1.exists()) {
                    System.out.println("prm.MEANSPEARR_PATH broken " + prm.MEANSPEARR_PATH);
                    System.exit(0);
                }
                File t2 = new File(prm.SDSPEARR_PATH);
                if (!t2.exists()) {
                    System.out.println("prm.SDSPEARR_PATH broken " + prm.SDSPEARR_PATH);
                    System.exit(0);
                }
                loadPreCritSpearNulls(prm.MEANSPEARR_PATH, prm.SDSPEARR_PATH);
            } else {
                System.out.println("SPEARR null PATH broken " + prm.MEANSPEARR_PATH + "\t" + prm.SDSPEARR_PATH);
                System.exit(0);
            }
        } else if (prm.precrit.SPEARIndex == 3 && prm.precrit.neednull[6]) {
            if (prm.MEANSPEARC_PATH != null && prm.SDSPEARC_PATH != null) {
                File t1 = new File(prm.MEANSPEARC_PATH);
                if (!t1.exists()) {
                    System.out.println("prm.MEANSPEARC_PATH broken " + prm.MEANSPEARC_PATH);
                    System.exit(0);
                }
                File t2 = new File(prm.SDSPEARC_PATH);
                if (!t2.exists()) {
                    System.out.println("prm.SDSPEARC_PATH broken " + prm.SDSPEARC_PATH);
                    System.exit(0);
                }
                loadPreCritSpearNulls(prm.MEANSPEARC_PATH, prm.SDSPEARC_PATH);
            } else {
                System.out.println("SPEARC" +
                        " null PATH broken " + prm.MEANSPEARC_PATH + "\t" + prm.SDSPEARC_PATH);
                System.exit(0);
            }
        }
    }

    /**
     *
     */
    private void loadInterNulls() {
        if (prm.MEANINTERACT_PATH != null && prm.SDINTERACT_PATH != null) {
            File t1 = new File(prm.MEANINTERACT_PATH);
            if (!t1.exists()) {
                System.out.println("prm.MEANINTERACT_PATH broken " + prm.MEANINTERACT_PATH);
                System.exit(0);
            }
            File t2 = new File(prm.SDINTERACT_PATH);
            if (!t2.exists()) {
                System.out.println("prm.SDINTERACT_PATH broken " + prm.SDINTERACT_PATH);
                System.exit(0);
            }

            System.out.println("loadInterNulls " + prm.MEANINTERACT_PATH + "\t" + prm.SDINTERACT_PATH);
            try {
                String[][] m = (String[][]) TabFile.readtoArray(prm.MEANINTERACT_PATH);
                meanInteractNull = extractColumn(m, 1);

                meanInteractNull = stat.filterAboveEqual(meanInteractNull, 1.0, 1.0, true);

                if (debug) {
                    System.out.println("meanInteractNull " + MoreArray.toString(meanInteractNull, ","));
                }
            } catch (Exception e) {
                System.out.println("WARNING no meanInteractNull");
            }
            try {
                String[][] s = (String[][]) TabFile.readtoArray(prm.SDINTERACT_PATH);
                sdInteractNull = extractColumn(s, 1);
            } catch (Exception e) {
                System.out.println("WARNING no sdInteractNull");
            }
        }
    }

    /**
     *
     */
    private void loadTFNulls() {
        if (prm.MEANTF_PATH != null && prm.SDTF_PATH != null) {
            File t1 = new File(prm.MEANTF_PATH);
            if (!t1.exists()) {
                System.out.println("prm.MEANTF_PATH broken " + prm.MEANTF_PATH);
                System.exit(0);
            }
            File t2 = new File(prm.SDTF_PATH);
            if (!t2.exists()) {
                System.out.println("prm.SDTF_PATH broken " + prm.SDTF_PATH);
                System.exit(0);
            }

            System.out.println("loadTFNulls " + prm.MEANTF_PATH + "\t" + prm.SDTF_PATH);
            try {
                String[][] m = (String[][]) TabFile.readtoArray(prm.MEANTF_PATH);
                meanTFNull = extractColumn(m, 1);

                meanTFNull = stat.filterAboveEqual(meanTFNull, 1.0, 1.0, true);

                if (debug) {
                    System.out.println("loadTFNulls " + MoreArray.toString(meanTFNull, ","));
                }
            } catch (Exception e) {
                System.out.println("WARNING no meanTFNull");
            }
            try {
                String[][] s = (String[][]) TabFile.readtoArray(prm.SDTF_PATH);
                sdTFNull = extractColumn(s, 1);
            } catch (Exception e) {
                System.out.println("WARNING no sdTFNull");
            }
        }
    }

    /**
     *
     */
    private void loadFeatNulls() {
        if (prm.MEANFEAT_PATH != null && prm.SDFEAT_PATH != null) {
            File t1 = new File(prm.MEANFEAT_PATH);
            if (!t1.exists()) {
                System.out.println("prm.MEANFEAT_PATH broken " + prm.MEANFEAT_PATH);
                System.exit(0);
            }
            File t2 = new File(prm.SDFEAT_PATH);
            if (!t2.exists()) {
                System.out.println("prm.SDFEAT_PATH broken " + prm.SDFEAT_PATH);
                System.exit(0);
            }

            System.out.println("loadFeatNulls " + prm.MEANFEAT_PATH + "\t" + prm.SDFEAT_PATH);
            try {
                String[][] m = (String[][]) util.TabFile.readtoArray(prm.MEANFEAT_PATH);
                //System.out.println("loadFeatNulls " + m.length + "\t" + m[0].length);
                meanFeatNull = extractColumn(m, 1);

                meanFeatNull = stat.filterAboveEqual(meanFeatNull, 1.0, 1.0, true);

                if (debug) {
                    System.out.println("meanFeatnull " + MoreArray.toString(meanFeatNull, ","));
                }
            } catch (Exception e) {
                System.out.println("WARNING no meanFeatNull");
                //e.printStackTrace();
            }
            try {
                String[][] s = (String[][]) TabFile.readtoArray(prm.SDFEAT_PATH);
                sdFeatNull = extractColumn(s, 1);
            } catch (Exception e) {
                System.out.println("WARNING no sdFeatNull");
            }
        }
    }

    /**
     * @param a
     * @param b
     */
    private void loadExprNulls(String a, String b) {

        System.out.println("loadExprNulls " + a + "\t" + b);
        try {
            meanMSENull = MoreArray.convfromString(util.TabFile.readtoArray(a));

            meanMSENull = Matrix.filterAboveEqual(meanMSENull, 1.0, 1.0, true);

            System.out.println("loadExprNulls mean " + meanMSENull.length + "\t" + meanMSENull[0].length);
        } catch (Exception e) {
            System.out.println("loadExprNulls mean _path failed " + a);
            //e.printStackTrace();
        }
        try {
            sdMSENull = MoreArray.convfromString(util.TabFile.readtoArray(b));
            System.out.println("loadExprNulls sd " + sdMSENull.length + "\t" + sdMSENull[0].length);
        } catch (Exception e) {
            System.out.println("loadExprNulls sd _path failed " + b);
            //e.printStackTrace();
        }
    }

    /**
     * @param a
     * @param b
     */
    private void loadRegNulls(String a, String b) {
        System.out.println("loadRegNulls " + a + "\t" + b);
        try {
            meanRegNull = MoreArray.convfromString(util.TabFile.readtoArray(a));

            meanRegNull = Matrix.filterAboveEqual(meanRegNull, 1.0, 1.0, true);

            System.out.println("loadRegNulls mean " + meanRegNull.length + "\t" + meanRegNull[0].length);
            regNullLoaded = true;
        } catch (Exception e) {
            System.out.println("loadRegNulls mean _path failed " + a);
            //e.printStackTrace();
        }
        try {
            sdRegNull = MoreArray.convfromString(util.TabFile.readtoArray(b));
            System.out.println("loadRegNulls sd " + sdRegNull.length + "\t" + sdRegNull[0].length);
        } catch (Exception e) {
            System.out.println("loadRegNulls sd _path failed " + b);
            //e.printStackTrace();
        }
    }

    /**
     * @param a
     * @param b
     */
    private void loadMeanNulls(String a, String b) {
        File fa = new File(a);
        File fb = new File(b);

        if (fa.exists() && fb.exists()) {
            System.out.println("loadMeanNulls " + a + "\t" + b);
            try {
                meanMeanNull = MoreArray.convfromString(util.TabFile.readtoArray(a));
                System.out.println("loadMeanNulls mean " + meanMeanNull.length + "\t" + meanMeanNull[0].length);
            } catch (Exception e) {
                System.out.println("loadMeanNulls mean nullpath failed " + a);
                //e.printStackTrace();
            }
            try {
                sdMeanNull = MoreArray.convfromString(util.TabFile.readtoArray(b));
                System.out.println("loadMeanNulls sd " + sdMeanNull.length + "\t" + sdMeanNull[0].length);
            } catch (Exception e) {
                System.out.println("loadMeanNulls sd nullpath failed " + b);
                //e.printStackTrace();
            }
        } else {
            System.out.println("loadMeanNulls null required but missing " + a + "\t" + b);
            System.exit(0);
        }
    }

    /**
     * @param a
     * @param b
     */
    private void loadKendallNulls(String a, String b) {
        System.out.println("loadKendallNulls " + a + "\t" + b);
        try {
            meanKendNull = MoreArray.convfromString(util.TabFile.readtoArray(a));

            meanKendNull = Matrix.filterAboveEqual(meanKendNull, 1.0, 1.0, true);

            System.out.println("loadKendallNulls mean " + meanKendNull.length + "\t" + meanKendNull[0].length);
        } catch (Exception e) {
            System.out.println("loadKendallNulls mean nullpath failed " + a);
            //e.printStackTrace();
        }
        try {
            sdKendNull = MoreArray.convfromString(util.TabFile.readtoArray(b));
            System.out.println("loadKendallNulls sd " + sdKendNull.length + "\t" + sdKendNull[0].length);
        } catch (Exception e) {
            System.out.println("loadKendallNulls sd nullpath failed " + b);
            //e.printStackTrace();
        }
        if (meanKendNull == null)
            System.out.println("loadKendallNulls NOT LOADED");
    }

    /**
     * @param a
     * @param b
     */
    private void loadBinaryNulls(String a, String b) {
        System.out.println("loadBinaryNulls " + a + "\t" + b);
        try {
            meanBinaryNull = MoreArray.convfromString(util.TabFile.readtoArray(a));

            meanBinaryNull = Matrix.filterAboveEqual(meanBinaryNull, 1.0, 1.0, true);

            System.out.println("loadBinaryNulls mean " + meanBinaryNull.length + "\t" + meanBinaryNull[0].length);
        } catch (Exception e) {
            System.out.println("loadBinaryNulls mean nullpath failed " + a);
            //e.printStackTrace();
        }
        try {
            sdBinaryNull = MoreArray.convfromString(util.TabFile.readtoArray(b));
            System.out.println("loadBinaryNulls sd " + sdBinaryNull.length + "\t" + sdBinaryNull[0].length);
        } catch (Exception e) {
            System.out.println("loadBinaryNulls sd nullpath failed " + b);
            //e.printStackTrace();
        }
        if (meanBinaryNull == null)
            System.out.println("loadBinaryNulls NOT LOADED");
    }

    /**
     * @param a
     * @param b
     */
    private void loadCorNulls(String a, String b) {
        System.out.println("loadCorNulls " + a + "\t" + b);
        try {
            meanCorNull = MoreArray.convfromString(util.TabFile.readtoArray(a));

            meanCorNull = Matrix.filterAboveEqual(meanCorNull, 1.0, 1.0, true);

            System.out.println("loadCorNulls mean " + meanCorNull.length + "\t" + meanCorNull[0].length);
        } catch (Exception e) {
            System.out.println("loadCorNulls mean nullpath failed " + a);
            //e.printStackTrace();
        }
        try {
            sdCorNull = MoreArray.convfromString(util.TabFile.readtoArray(b));
            System.out.println("loadCorNulls sd " + sdCorNull.length + "\t" + sdCorNull[0].length);
        } catch (Exception e) {
            System.out.println("loadCorNulls sd nullpath failed " + b);
            //e.printStackTrace();
        }
    }

    /**
     * @param a
     * @param b
     */
    private void loadEucNulls(String a, String b) {
        System.out.println("loadEucNulls " + a + "\t" + b);
        try {
            meanEucNull = MoreArray.convfromString(util.TabFile.readtoArray(a));

            meanEucNull = Matrix.filterAboveEqual(meanEucNull, 1.0, 1.0, true);

            System.out.println("loadEucNulls mean " + meanEucNull.length + "\t" + meanEucNull[0].length);
        } catch (Exception e) {
            System.out.println("loadEucNulls mean nullpath failed " + a);
            //e.printStackTrace();
        }
        try {
            sdEucNull = MoreArray.convfromString(util.TabFile.readtoArray(b));
            System.out.println("loadEucNulls sd " + sdEucNull.length + "\t" + sdEucNull[0].length);
        } catch (Exception e) {
            System.out.println("loadEucNulls sd nullpath failed " + b);
            //e.printStackTrace();
        }
    }

    /**
     * @param a
     * @param b
     */
    private void loadSpearNulls(String a, String b) {
        System.out.println("loadSpearNulls " + a + "\t" + b);
        try {
            meanSpearNull = MoreArray.convfromString(util.TabFile.readtoArray(a));

            meanSpearNull = Matrix.filterAboveEqual(meanSpearNull, 1.0, 1.0, true);

            System.out.println("loadSpearNulls mean " + meanSpearNull.length + "\t" + meanSpearNull[0].length);
        } catch (Exception e) {
            System.out.println("loadSpearNulls mean nullpath failed " + a);
            //e.printStackTrace();
        }
        try {
            sdSpearNull = MoreArray.convfromString(util.TabFile.readtoArray(b));
            System.out.println("loadSpearNulls sd " + sdSpearNull.length + "\t" + sdSpearNull[0].length);
        } catch (Exception e) {
            System.out.println("loadSpearNulls sd nullpath failed " + b);
            //e.printStackTrace();
        }
    }

    /**
     * @param a
     * @param b
     */
    private void loadPreCritMeanNulls(String a, String b) {
        System.out.println("loadPreCritMeanNulls " + a + "\t" + b);
        try {
            precrit_meanMeanNull = MoreArray.convfromString(util.TabFile.readtoArray(a));
            System.out.println("loadPreCritMeanNulls mean " + precrit_meanMeanNull.length + "\t" + precrit_meanMeanNull[0].length);
        } catch (Exception e) {
            System.out.println("loadPreCritMeanNulls mean nullpath failed " + a);
            //e.printStackTrace();
        }
        if (b != null) {
            try {
                precrit_sdMeanNull = MoreArray.convfromString(util.TabFile.readtoArray(b));
                System.out.println("loadPreCritMeanNulls sd " + precrit_sdMeanNull.length + "\t" + precrit_sdMeanNull[0].length);
            } catch (Exception e) {
                System.out.println("loadPreCritMeanNulls sd nullpath failed " + b);
                //e.printStackTrace();
            }
        }
    }

    /**
     * @param a
     * @param b
     */
    private void loadPreCritMSENulls(String a, String b) {
        System.out.println("loadPreCritMSENulls " + a + "\t" + b);
        try {
            precrit_meanMSENull = MoreArray.convfromString(util.TabFile.readtoArray(a));
            System.out.println("loadPreCritMSENulls mean " + precrit_meanMSENull.length + "\t" + precrit_meanMSENull[0].length);
        } catch (Exception e) {
            System.out.println("loadPreCritMSENulls mean nullpath failed " + a);
            //e.printStackTrace();
        }
        if (b != null) {
            try {
                precrit_sdMSENull = MoreArray.convfromString(util.TabFile.readtoArray(b));
                System.out.println("loadPreCritMSENulls sd " + precrit_sdMSENull.length + "\t" + precrit_sdMSENull[0].length);
            } catch (Exception e) {
                System.out.println("loadPreCritMSENulls sd nullpath failed " + b);
                //e.printStackTrace();
            }
        }
    }

    /**
     * @param a
     * @param b
     */
    private void loadPreCritKENDALLNulls(String a, String b) {
        System.out.println("loadPreCritKENDALLNulls " + a + "\t" + b);
        try {
            precrit_meanKendNull = MoreArray.convfromString(util.TabFile.readtoArray(a));
            System.out.println("loadPreCritKENDALLNulls mean " + precrit_meanKendNull.length + "\t" + precrit_meanKendNull[0].length);
        } catch (Exception e) {
            System.out.println("loadPreCritKENDALLNulls mean nullpath failed " + a);
            //e.printStackTrace();
        }
        if (b != null) {
            try {
                precrit_sdKendNull = MoreArray.convfromString(util.TabFile.readtoArray(b));
                System.out.println("loadPreCritKENDALLNulls sd " + precrit_sdKendNull.length + "\t" + precrit_sdKendNull[0].length);
            } catch (Exception e) {
                System.out.println("loadPreCritKENDALLNulls sd nullpath failed " + b);
                //e.printStackTrace();
            }
        }
    }

    /**
     * @param a
     * @param b
     */
    private void loadPreCritBinaryNulls(String a, String b) {
        System.out.println("loadPreCritBinaryNulls " + a + "\t" + b);
        try {
            precrit_meanBinaryNull = MoreArray.convfromString(util.TabFile.readtoArray(a));
            System.out.println("loadPreCritBinaryNulls mean " + precrit_meanBinaryNull.length + "\t" + precrit_meanBinaryNull[0].length);
        } catch (Exception e) {
            System.out.println("loadPreCritBinaryNulls mean nullpath failed " + a);
            //e.printStackTrace();
        }
        if (b != null) {
            try {
                precrit_sdBinaryNull = MoreArray.convfromString(util.TabFile.readtoArray(b));
                System.out.println("loadPreCritBinaryNulls sd " + precrit_sdBinaryNull.length + "\t" + precrit_sdBinaryNull[0].length);
            } catch (Exception e) {
                System.out.println("loadPreCritBinaryNulls sd nullpath failed " + b);
                //e.printStackTrace();
            }
        }
    }

    /**
     * @param a
     * @param b
     */
    private void loadPreCritRegNulls(String a, String b) {
        if (debug)
            System.out.println("loadPreCritRegNulls " + a + "\t" + b);
        try {
            precrit_meanRegNull = MoreArray.convfromString(util.TabFile.readtoArray(a));
            if (debug)
                System.out.println("loadPreCritRegNulls mean " + precrit_meanRegNull.length + "\t" + precrit_meanRegNull[0].length);
        } catch (Exception e) {
            System.out.println("loadPreCritRegNulls mean nullpath failed " + a);
            //e.printStackTrace();
        }
        if (b != null) {
            try {
                precrit_sdRegNull = MoreArray.convfromString(util.TabFile.readtoArray(b));
                if (debug)
                    System.out.println("loadPreCritRegNulls sd " + precrit_sdRegNull.length + "\t" + precrit_sdRegNull[0].length);
            } catch (Exception e) {
                System.out.println("loadPreCritRegNulls sd nullpath failed " + b);
                //e.printStackTrace();
            }
        }
    }

    /**
     * @param a
     * @param b
     */
    private void loadPreCritCorNulls(String a, String b) {
        if (debug)
            System.out.println("loadPreCritCorNulls " + a + "\t" + b);
        try {
            precrit_meanCorNull = MoreArray.convfromString(util.TabFile.readtoArray(a));
            if (debug)
                System.out.println("loadPreCritCorNulls mean " + precrit_meanCorNull.length + "\t" + precrit_meanCorNull[0].length);
        } catch (Exception e) {
            System.out.println("loadPreCritCorNulls mean nullpath failed " + a);
            //e.printStackTrace();
        }
        if (b != null) {
            try {
                precrit_sdCorNull = MoreArray.convfromString(util.TabFile.readtoArray(b));
                if (debug)
                    System.out.println("loadPreCritCorNulls sd " + precrit_sdCorNull.length + "\t" + precrit_sdCorNull[0].length);
            } catch (Exception e) {
                System.out.println("loadPreCritCorNulls sd nullpath failed " + b);
                //e.printStackTrace();
            }
        }
    }

    /**
     * @param a
     * @param b
     */
    private void loadPreCritEucNulls(String a, String b) {
        System.out.println("loadPreCritEucNulls " + a + "\t" + b);
        try {
            precrit_meanEUCNull = MoreArray.convfromString(util.TabFile.readtoArray(a));
            System.out.println("loadPreCritEucNulls mean " + precrit_meanEUCNull.length + "\t" + precrit_meanEUCNull[0].length);
        } catch (Exception e) {
            System.out.println("loadPreCritEucNulls mean nullpath failed " + a);
            //e.printStackTrace();
        }
        if (b != null) {
            try {
                precrit_sdEUCNull = MoreArray.convfromString(util.TabFile.readtoArray(b));
                System.out.println("loadPreCritEucNulls sd " + precrit_sdEUCNull.length + "\t" + precrit_sdEUCNull[0].length);
            } catch (Exception e) {
                System.out.println("loadPreCritEucNulls sd nullpath failed " + b);
                //e.printStackTrace();
            }
        }
    }

    /**
     * @param a
     * @param b
     */
    private void loadPreCritSpearNulls(String a, String b) {
        System.out.println("loadPreCritSpearNulls " + a + "\t" + b);
        try {
            precrit_meanSpearNull = MoreArray.convfromString(util.TabFile.readtoArray(a));
            System.out.println("loadPreCritSpearNulls mean " + precrit_meanSpearNull.length + "\t" + precrit_meanSpearNull[0].length);
        } catch (Exception e) {
            System.out.println("loadPreCritSpearNulls mean nullpath failed " + a);
            //e.printStackTrace();
        }
        if (b != null) {
            try {
                precrit_sdSpearNull = MoreArray.convfromString(util.TabFile.readtoArray(b));
                System.out.println("loadPreCritSpearNulls sd " + precrit_sdSpearNull.length + "\t" + precrit_sdSpearNull[0].length);
            } catch (Exception e) {
                System.out.println("loadPreCritSpearNulls sd nullpath failed " + b);
                //e.printStackTrace();
            }
        }
    }

    /**
     *
     */
    public void setCrittoPreCritNulls() {
        meanMeanNull = precrit_meanMeanNull;
        sdMeanNull = precrit_sdMeanNull;

        meanMSENull = precrit_meanMSENull;
        sdMSENull = precrit_sdMSENull;

        meanKendNull = precrit_meanKendNull;
        sdKendNull = precrit_sdKendNull;

        meanBinaryNull = precrit_meanBinaryNull;
        sdBinaryNull = precrit_sdBinaryNull;

        meanRegNull = precrit_meanRegNull;
        sdRegNull = precrit_sdRegNull;

        meanCorNull = precrit_meanCorNull;
        sdCorNull = precrit_sdCorNull;

        meanEucNull = precrit_meanEUCNull;
        sdEucNull = precrit_sdEUCNull;
    }

}
