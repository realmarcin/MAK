package DataMining.eval;

import DataMining.ValueBlock;

/**
 * User: marcin
 * Date: Dec 5, 2009
 * Time: 10:32:10 AM
 */
public class EvaluateIncrNonoRand {

    int num_blocks = 4;

    public final static String trueblock1 = "5,195,86,186,108,60,19,75,175,177,42,81,70,139,4,2,120,157,183,15/27,1,37,7,8,26,71,5,45,30";
    public final static String trueblock2 = "27,138,84,131,181,150,14,61,121,153,199,10,166,71,67,154,76,82,58,97/46,25,11,69,22,31,73,13,63,62";
    public final static String trueblock3 = "69,127,191,151,88,136,158,83,40,187,53,179,167,176,188,89,85,152,73,106/2,77,57,80,15,17,76,58,55,47";
    public final static String trueblock4 = "132,54,107,68,21,110,94,170,145,174,101,32,189,137,126,173,78,169,13,87/65,20,44,23,41,3,34,32,56,4";

    /*
    public final static String trueblock1 = "21,29,166,154,153,131,72,193,47,86,65,115,138,28,3,126,19,1,85,46/44,9,8,67,2,54,60,15,31,76";    
    public final static String trueblock2 = "140,178,108,33,172,6,195,123,27,24,4,163,121,50,169,26,117,16,49,114/24,25,72,29,42,41,17,39,68,33";    
    public final static String trueblock3 = "181,64,10,37,74,175,35,98,152,171,158,194,81,17,199,100,177,38,90,165/21,34,48,80,5,23,40,30,26,38";    
    public final static String trueblock4 = "39,87,167,106,104,157,13,48,68,185,147,32,187,136,70,196,42,60,18,58/73,74,77,66,70,16,71,43,46,47";
    */

    public final static ValueBlock true1 = new ValueBlock(trueblock1);
    public final static ValueBlock true2 = new ValueBlock(trueblock2);
    public final static ValueBlock true3 = new ValueBlock(trueblock3);
    public final static ValueBlock true4 = new ValueBlock(trueblock4);
    
    double[] expr_cor = {0.3, 0.5, 0.7, 0.9};
    double[] interact_prob = {0.04, 0.04, 0.04, 0.04};

}
