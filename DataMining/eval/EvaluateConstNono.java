package DataMining.eval;

import DataMining.ValueBlock;

/**
 * User: marcin
 * Date: Nov 3, 2009
 * Time: 5:31:54 PM
 */
public class EvaluateConstNono {

    int num_blocks = 4;
    public final static String trueblock1 = "21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40/6,7,8,9,10,11,12,13,14,15";
    public final static ValueBlock true1 = new ValueBlock(trueblock1);
    public final static String trueblock2 = "61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80/26,27,28,29,30,31,32,33,34,35";
    public final static ValueBlock true2 = new ValueBlock(trueblock2);
    public final static String trueblock3 = "121,122,123,124,125,126,127,128,129,130,131,132,133,134,135,136,137,138,139,140/46,47,48,49,50,51,52,53,54,55";
    public final static ValueBlock true3 = new ValueBlock(trueblock3);
    public final static String trueblock4 = "161,162,163,164,165,166,167,168,169,170,171,172,173,174,175,176,177,178,179,180/66,67,68,69,70,71,72,73,74,75";
    public final static ValueBlock true4 = new ValueBlock(trueblock4);
    double[] expr_cor = {0.3, 0.5, 0.7, 0.9};
    double[] interact_prob = {0.04, 0.04, 0.04, 0.04};

}
