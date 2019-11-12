package DataMining;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: Feb 24, 2011
 * Time: 3:08:21 PM
 */
public final class ValueBlock_STATIC {


    public final static String[] criteria_labels = {"expr_MEAN", "expr_MSE", "expr_FEM", "expr_KEND",
            "expr_COR", "expr_EUC", "PPI", "feat", "minTF"};

    public final static int expr_MEAN_IND = 0;
    public final static int expr_MSE_IND = 1;
    public final static int expr_FEM_IND = 2;
    public final static int expr_KEND_IND = 3;
    public final static int expr_COR_IND = 4;
    public final static int expr_EUC_IND = 5;
    public final static int interact_IND = 6;
    public final static int feat_IND = 7;
    public final static int TF_IND = 8;

    public final static int NUM_CRIT = criteria_labels.length;
}
