package DataMining.eval;

import DataMining.ValueBlock;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: May 4, 2011
 * Time: 3:32:19 PM
 */
public class EvaluateIncrOverRand {

    int num_blocks = 3;

    /*public final static String trueblock1 = "30,44,75,80,100,119,7,168,191,139,43,195,142,192,94,27,62,112,32,41,48,53,187,55,1,68,124,188,66,159/9,21,45,24,64,34,68,41,57,56,51,78,7,52,72";
    public final static String trueblock2 = "187,55,1,68,124,188,66,159,81,29,137,87,122,85,136,3,47,64,106,176/56,51,78,7,52,72,67,74,36,33";
    public final static String trueblock3 = "187,55,1,68,124,188,66,159/56,51,78,7,52,72";*/

    public final static String trueblock1 = "30,44,75,80,100,119,7,168,191,139,43,195,142,192,94,27,62,112,32,41,48,53,187,55,1,68,124,188,66,159/9,21,45,24,64,34,68,41,57,56,51,78,7,52,72";
    public final static String trueblock2 = "187,55,1,68,124,188,66,159,81,29,137,87,122,85,136,3,47,64,106,176/56,51,78,7,52,72,67,74,36,33";
    public final static String trueblock3 = "39,58,155,52,104,45,107,65,172,164,98,182,88,175,103,190,147,57,198,149,95/70,2,15,5,17,55,35,23,3,14,46,59,29,32,71,4,20,75,22,80,8";
    public final static String trueblock4 = "190,147,57,198,149,95,108,4,197,82,174,181,10,144,170/46,59,29,32,71,4,20,75,22,80,8,39,79,42,25,11,43,44,49,12,37,76,62,6,31";


    public final static ValueBlock true1 = new ValueBlock(trueblock1);
    public final static ValueBlock true2 = new ValueBlock(trueblock2);
    public final static ValueBlock true3 = new ValueBlock(trueblock3);
    public final static ValueBlock true4 = new ValueBlock(trueblock4);

    //double[] expr_cor = {0.5, 0.8, 0.65};//overlap correlation assumed to be mean of two blocks, not exact
    double[] expr_cor = {0.4, 0.9, 0.1, 0.6};//overlap correlation assumed to be mean of two blocks, not exact
    double[] interact_prob = {0.25, 0.25, 0.25, 0.25};

}
