package DataMining.eval;

import DataMining.ValueBlock;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: 4/11/15
 * Time: 12:44 AM
 */
public class EvaluateConstOverRand {

    int num_blocks = 4;

    public final static String trueblock1 = "5,195,86,186,108,60,19,75,175,177,42,81,70,139,4,2,120,157,183,15/27,1,37,7,8,26,71,5,45,30";
    public final static String trueblock2 = "27,138,84,131,181,150,14,61,121,153,199,10,166,71,67,154,76,82,58,97/46,25,11,69,22,31,73,13,63,62";
    public final static String trueblock3 = "69,127,191,151,88,136,158,83,40,187,53,179,167,176,188,89,85,152,73,106/2,77,57,80,15,17,76,58,55,47";
    public final static String trueblock4 = "132,54,107,68,21,110,94,170,145,174,101,32,189,137,126,173,78,169,13,87/65,20,44,23,41,3,34,32,56,4";


    public final static ValueBlock true1 = new ValueBlock(trueblock1);
    public final static ValueBlock true2 = new ValueBlock(trueblock2);
    public final static ValueBlock true3 = new ValueBlock(trueblock3);
    public final static ValueBlock true4 = new ValueBlock(trueblock4);

    double[] expr_cor = {0.3, 0.5, 0.7, 0.9};
    double[] interact_prob = {0.04, 0.04, 0.04, 0.04};

}