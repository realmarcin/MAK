package DataMining.eval;

import DataMining.ValueBlock;

/**
 * User: marcin
 * Date: Dec 5, 2009
 * Time: 10:32:33 AM
 */
public class EvaluateConstNonoRand {

    int num_blocks = 4;

    public final static String trueblock1 = "5,195,86,186,108,60,19,75,175,177,42,81,70,139,4,2,120,157,183,15/27,1,37,7,8,26,71,5,45,30";
    public final static String trueblock2 = "27,138,84,131,181,150,14,61,121,153,199,10,166,71,67,154,76,82,58,97/46,25,11,69,22,31,73,13,63,62";
    public final static String trueblock3 = "69,127,191,151,88,136,158,83,40,187,53,179,167,176,188,89,85,152,73,106/2,77,57,80,15,17,76,58,55,47";
    public final static String trueblock4 = "132,54,107,68,21,110,94,170,145,174,101,32,189,137,126,173,78,169,13,87/65,20,44,23,41,3,34,32,56,4";


    public final static ValueBlock true1 = new ValueBlock(trueblock1);
    public final static ValueBlock true2 = new ValueBlock(trueblock2);
    public final static ValueBlock true3 = new ValueBlock(trueblock3);
    public final static ValueBlock true4 = new ValueBlock(trueblock4);
    /* public final static String trueblock1 = "28,18,87,40,14,4,114,36,198,189,195,21,104,68,90,135,22,86,165,127/39,77,78,51,22,54,50,34,67,3";
         public final static ValueBlock true1 = new ValueBlock(trueblock1);
         public final static String trueblock2 = "74,78,166,81,70,144,164,158,1,108,137,110,128,131,93,183,107,32,5,44/71,53,58,68,14,6,72,46,5,74";
         public final static ValueBlock true2 = new ValueBlock(trueblock2);
         public final static String trueblock3 = "175,129,41,13,35,136,67,122,156,171,109,197,124,69,199,157,84,66,177,188/35,28,49,32,76,11,15,36,66,7";
         public final static ValueBlock true3 = new ValueBlock(trueblock3);
         public final static String trueblock4 = "97,113,38,31,187,134,20,60,151,160,174,34,103,167,33,62,132,63,19,184/16,17,47,2,23,48,52,43,10,20";
         public final static ValueBlock true4 = new ValueBlock(trueblock4);
     */
    double[] expr_cor = {0.3, 0.5, 0.7, 0.9};
    double[] interact_prob = {0.04, 0.04, 0.04, 0.04};

}
