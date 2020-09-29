package DataMining.eval;

import DataMining.ValueBlock;

/**
 * User: marcin
 * Date: Oct 17, 2009
 * Time: 9:38:22 PM
 */
public class EvaluateConst {
    int num_blocks = 4;
    public final static String trueblock1 = "21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40/6,7,8,9,10,11,12,13,14,15";
    public final static ValueBlock true1 = new ValueBlock(trueblock1);
    public final static String trueblock2 = "61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80/26,27,28,29,30,31,32,33,34,35";
    public final static ValueBlock true2 = new ValueBlock(trueblock2);
    public final static String trueblock3 = "61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80/6,7,8,9,10,11,12,13,14,15";
    public final static ValueBlock true3 = new ValueBlock(trueblock3);
    public final static String trueblock4 = "21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40/26,27,28,29,30,31,32,33,34,35";
    public final static ValueBlock true4 = new ValueBlock(trueblock4);
    double[] expr_cor = {0.3, 0.5, 0.7, 0.9};
    double[] interact_prob = {0.04, 0.04, 0.04, 0.04};
}
