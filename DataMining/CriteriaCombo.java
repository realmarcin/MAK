package DataMining;

import mathy.stat;
import util.MoreArray;
import util.StringUtil;

import java.util.ArrayList;

/**
 * This code is likely BROKEN!!!.
 * <p/>
 * <p/>
 * User: marcin
 * Date: Nov 17, 2010
 * Time: 6:55:42 PM
 */
public class CriteriaCombo {

    public String[] crit_labels;
    public String[] crit_isnulls;


    public final static String[] notnonull = {"nonull"};
    public final static String[] notnonullnotint = {"nonull", "int"};
    public final static String[] notMSERnotMSECnonull = {"MSER", "MSEC", "nonull"};
    public final static String[] notMSERnotMSECnotnonullnotint = {"MSER", "MSEC", "nonull", "int"};
    public final static String[] notKENDALLCnotKENDALLRnonull = {"KENDALLR", "KENDALLC", "nonull", "int"};
    public final static String[] notGEEREGEECE = {"GEERE", "GEECE"};
    public final static String[] notLARSRELARSCE = {"LARSRE", "LASRCE"};

    //MSER  criteria
    public int[] MSERCrit;
    //MSEC criteria
    public int[] MSECCrit;
    //KENDALL criteria
    public int[] KENDALLCrit;
    //KENDALLC criteria
    public int[] KENDALLCCrit;
    //KENDALLR criteria
    public int[] KENDALLRCrit;
    //MADR criteria
    public int[] MADRCrit;
    //MSE criteria
    public int[] MSECrit;
    //MSER  criteria
    public int[] MSERCritnoint;
    //MSEC criteria
    public int[] MSECCritnoint;
    //KENDALLC criteria
    public int[] KENDALLCCritnoint;
    //KENDALLR criteria
    public int[] KENDALLRCritnoint;
    //KENDALL criteria
    public int[] KENDALLCritnoint;
    //MADR criteria
    public int[] MADRCritnoint;
    //MSE criteria
    public int[] MSECritnoint;
    //block mean/median with null
    public int[] MEANCrit;
    //criteria requiring a null but w/o interaction
    public int[] nullCrit;
    //criteria requiring a null with interaction
    public int[] nullCritIntCrit;
    //criteria without a null without interaction
    public int[] nonullCrit;
    //criteria without a null with interaction
    public int[] nonullCritIntCrit;
    //LARSRE criteria
    public int[] LARSCrit;
    public int[] LARSonlyCrit;
    public int[] LARSRECrit;
    public int[] LARSCECrit;
    //GEERE criteria
    public int[] GEECrit;
    public int[] GEEonlyCrit;
    public int[] GEERECrit;
    public int[] GEECECrit;

    public int[] interCrit;
    public int[] TFcrit;
    public int[] featCrit;

    //by axis
    public int[] totalCrit;
    public int[] rowCrit;
    public int[] colCrit;


    /**
     * grouped criteria for criteria combinations
     */
    public String[] expr_mean_crit = {"none", "MEAN", "MEANR", "MEANC",
            "MEANnonull", "MEANRnonull", "MEANCnonull"};
    public String[] expr_cor_crit = {"none", "MSE", "MSER", "MSEC", "KENDALL", "KENDALLR", "KENDALLC",
            "MSEnonull", "MSERnonull", "MSECnonull", "KENDALLnonull", "KENDALLRnonull", "KENDALLCnonull"};
    public String[] expr_cor_crit_special = {"none", "MSEnonull_noninvert", "MSERnonull_noninvert", "MSECnonull_noninvert"};
    public String[] expr_reg_crit = {"none", "LARS", "LARSRE", "LARSCE", "GEE", "GEERE", "GEECE",
            "LARSnonull", "LARSREnonull", "LARSCEnonull", "GEEnonull", "GEEREnonull", "GEECEnonull",};
    public String[] interact_crit = {"none", "int", "intnonull"};
    public String[] tf_crit = {"none", "MaxTF", "MaxTFnonull"};
    public String[] feature_crit = {"none", "LARS"};
    public String[] total_crit = {"MSE", "MEAN", "KENDALL", "LARS", "GEE", "COR", "EUC",
            "MSEnonull", "MEANnonull", "KENDALLnonull", "LARSnonull", "GEEnonull", "CORnonull", "EUCnonull"};
    public String[] row_crit = {"MSER", "MEANR", "LARSRE", "GEERE",
            "KENDALLR", "CORR", "EUCR", "MSERnonull", "MEANRnonull", "LARSREnonull", "GEEREnonull", "KENDALLRnonull",
            "CORRnonull", "EUCRnonull"};
    public String[] col_crit = {"MSEC", "MEANC", "LARSCE", "GEECE", "KENDALLC", "CORC", "EUCC",
            "MSECnonull", "MEANCnonull", "LARSCEnonull", "GEECEnonull", "KENDALLCnonull", "CORCnonull", "EUCCnonull"};


    /*total, row, column*/
    public int[] crit_axis = {1, 2, 3};


    /**
     *
     */
    public CriteriaCombo() {

        makeCombos();

        makeSets();

    }

    /**
     *
     */
    private void makeSets() {
        //MSER  criteria
        MSERCrit = stat.add(StringUtil.occurIndexButNot(crit_labels, "MSER", notnonull), 1);
        //MSEC criteria
        MSECCrit = stat.add(StringUtil.occurIndexButNot(crit_labels, "MSEC", notnonull), 1);
        //MSEC criteria
        KENDALLCrit = stat.add(StringUtil.occurIndexButNot(crit_labels, "KENDALL", notnonull), 1);
        //MADR criteria
        MADRCrit = stat.add(StringUtil.occurIndexButNot(crit_labels, "MADR", notnonull), 1);
        //MSE criteria
        MSECrit = stat.add(MoreArray.add(StringUtil.occurIndexButNot(crit_labels, "MSE", notMSERnotMSECnonull), 24 - 1), 1);
        //MSER  criteria
        MSERCritnoint = stat.add(StringUtil.occurIndexButNot(crit_labels, "MSER", notnonullnotint), 1);
        //MSEC criteria
        MSECCritnoint = stat.add(StringUtil.occurIndexButNot(crit_labels, "MSEC", notnonullnotint), 1);

        //KENDALL criteria
        KENDALLCritnoint = stat.add(MoreArray.add(StringUtil.occurIndexButNot(crit_labels, "MSE", notKENDALLCnotKENDALLRnonull), 24 - 1), 1);//stat.add(StringUtil.occurIndexButNot(crit_labels, "KENDALL", notnonullnotint), 1);
        //KENDALLC criteria
        KENDALLCCritnoint = stat.add(StringUtil.occurIndexButNot(crit_labels, "KENDALLC", notnonullnotint), 1);
        //KENDALLR criteria
        KENDALLRCritnoint = stat.add(StringUtil.occurIndexButNot(crit_labels, "KENDALLR", notnonullnotint), 1);
        //MADR criteria
        MADRCritnoint = stat.add(StringUtil.occurIndexButNot(crit_labels, "MADR", notnonullnotint), 1);
        //MSE criteria
        MSECritnoint = stat.add(MoreArray.add(StringUtil.occurIndexButNot(crit_labels, "MSE", notMSERnotMSECnotnonullnotint), 24 - 1), 1);
        //block mean/median with null
        MEANCrit = stat.add(StringUtil.occurIndexButNot(crit_labels, "MEAN", notnonullnotint), 1);

        //criteria requiring a null but w/o interaction
        //nullCrit = MoreArray.add(MoreArray.add(MoreArray.add(MoreArray.add(MoreArray.add(MoreArray.add(MoreArray.add(
        //        MSERCritnoint, MSECCritnoint), MADRCritnoint), KENDALLCritnoint),KENDALLCCritnoint),KENDALLRCritnoint), MSECritnoint), MEANCrit);

        interCrit = stat.add(StringUtil.occurIndex(crit_labels, "inter"), 1);
        TFcrit = stat.add(StringUtil.occurIndex(crit_labels, "TF"), 1);
        featCrit = stat.add(StringUtil.occurIndex(crit_labels, "feat"), 1);
        //criteria requiring a null with interaction
        //int[] matches = MoreArray.crossIndex(nullCrit, interCrit);
        //int[] tmp4 = {1, 3, 2, 11, 12, 13, 17, 18, 19, 23, 25, 27, 29, 31, 33};
        //nullCritIntCrit = tmp4;
        //criteria without a null without interaction
        //int[] tmp5 = {8, 10, 35, 36, 37, 35, 36, 37, 38, 39, 40, 41, 42, 43, 49, 50, 51};
        //nonullCrit = tmp5;
        //criteria without a null with interaction
        //int[] tmp6 = {3, 7, 9, 13, 19, 44};
        //nonullCritIntCrit = tmp6;
        //LARSRE criteria
        LARSCrit = stat.add(StringUtil.locateIndexOf(crit_labels, "LARS", -2), 1);
        LARSonlyCrit = stat.add(StringUtil.occurIndexButNot(crit_labels, "LARS", notLARSRELARSCE), 1);
        LARSRECrit = stat.add(StringUtil.locateIndexOf(crit_labels, "LARSRE", -2), 1);
        LARSCECrit = stat.add(StringUtil.locateIndexOf(crit_labels, "LARSCE", -2), 1);
        //GEERE criteria
        GEECrit = stat.add(StringUtil.locateIndexOf(crit_labels, "GEE", -2), 1);
        GEEonlyCrit = stat.add(StringUtil.occurIndexButNot(crit_labels, "GEE", notGEEREGEECE), 1);
        GEERECrit = stat.add(StringUtil.locateIndexOf(crit_labels, "GEERE", -2), 1);
        GEECECrit = stat.add(StringUtil.locateIndexOf(crit_labels, "GEECE", -2), 1);

        //by axis
        totalCrit = MoreArray.add(MoreArray.add(MoreArray.add(MSECrit, KENDALLCrit), KENDALLCritnoint), 46);
        rowCrit = MoreArray.add(MoreArray.add(MoreArray.add(MoreArray.add(MoreArray.add(MoreArray.add(MoreArray.add(MoreArray.add(
                MSERCrit, MSERCritnoint), MADRCrit), MADRCritnoint), LARSRECrit), GEERECrit), KENDALLRCrit), KENDALLRCritnoint), 47);
        colCrit = MoreArray.add(MoreArray.add(MoreArray.add(MoreArray.add(MoreArray.add(MoreArray.add(MSECCrit, MSECCritnoint), LARSCECrit), GEECECrit), KENDALLCCrit), KENDALLCCritnoint), 48);
    }

    /**
     * Makes 'all' null-based criteria combinations
     * in the following order
     * <p/>
     * expr_mean_crit
     * expr_mean_crit
     * expr_reg_crit
     * interact_crit
     * tf_crit
     * feature_crit
     */
    private void makeCombos() {
        ArrayList combos = new ArrayList();
        ArrayList nulls = new ArrayList();
        for (int i = 0; i < expr_mean_crit.length; i++) {
            int expr_mean_crit_axis = whichCritAxis(expr_mean_crit[i]);
            String nullindex = "";
            boolean isnull = isNull(expr_mean_crit[i]);
            if (isnull)
                nullindex += 1;
            else
                nullindex += 0;
            String critlabel = expr_mean_crit[i] + "_";
            for (int j = 0; j < expr_cor_crit.length; j++) {
                int expr_cor_crit_axis = whichCritAxis(expr_cor_crit[j]);
                if (expr_cor_crit_axis == expr_mean_crit_axis) {
                    isnull = isNull(expr_cor_crit[i]);
                    if (isnull)
                        nullindex += 1;
                    else
                        nullindex += 0;
                    critlabel += expr_cor_crit[i] + "_";
                    for (int k = 0; k < expr_reg_crit.length; k++) {
                        int expr_reg_crit_axis = whichCritAxis(expr_reg_crit[i]);
                        if (expr_reg_crit_axis == expr_mean_crit_axis) {
                            isnull = isNull(expr_cor_crit[i]);
                            if (isnull)
                                nullindex += 1;
                            else
                                nullindex += 0;
                            critlabel += expr_reg_crit[i] + "_";
                            for (int l = 0; l < interact_crit.length; l++) {
                                critlabel += interact_crit[i] + "_";
                                for (int m = 0; m < tf_crit.length; m++) {
                                    critlabel += tf_crit[i] + "_";
                                    for (int n = 0; n < feature_crit.length; n++) {
                                        critlabel += feature_crit[i] + "_";
                                        combos.add(critlabel);
                                        nulls.add(nullindex);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        crit_labels = MoreArray.ArrayListtoString(combos);
        crit_isnulls = MoreArray.ArrayListtoString(nulls);
    }

    /**
     * @param s
     * @return
     */
    public boolean isNull(String s) {
        if (s.indexOf("nonull") != -1)
            return false;
        return true;
    }

    /**
     * @param crit
     * @return
     */
    public int whichCritAxis(String crit) {
        int ret = MoreArray.getArrayInd(total_crit, crit);
        if (ret != -1)
            return 1;

        ret = MoreArray.getArrayInd(row_crit, crit);
        if (ret != -1)
            return 2;

        ret = MoreArray.getArrayInd(col_crit, crit);
        if (ret != -1)
            return 3;

        return -1;
    }


    /**
     * @param args
     */
    public final static void main(String[] args) {
        if (args.length == 0) {
            CriteriaCombo cc = new CriteriaCombo();
        }
    }

}
