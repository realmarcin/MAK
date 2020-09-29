package DataMining.eval;

import DataMining.ValueBlock;

/**
 * User: marcin
 * Date: Nov 3, 2009
 * Time: 5:36:18 PM
 */
public class EvaluateBasic {

    public final static String trueblock1 = "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30/1,2,3,4,5,6,7,8,9,10,11,12,13,14,15";
    public final static ValueBlock true1 = new ValueBlock(trueblock1);
    public final static String block_ga = "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31/1,2,3,4,5,6,7,8,9,10,11,12,13,14,15";
    public final static String block_gd = "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29/1,2,3,4,5,6,7,8,9,10,11,12,13,14,14";
    public final static String block_ea = "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30/1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16";
    public final static String block_ed = "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30/1,2,3,4,5,6,7,8,9,10,11,12,13,14";
    public final static String trueblock2 = "23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42/10,11,12,13,14,15,16,17,18,19";
    public final static ValueBlock true2 = new ValueBlock(trueblock2);
    public final static String block2_ga = "23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,1/10,11,12,13,14,15,16,17,18,19";
    public final static String block2_gd = "23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41/10,11,12,13,14,15,16,17,18,19";
    public final static String block2_ea = "23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42/10,11,12,13,14,15,16,17,18,19,1";
    public final static String block2_ed = "23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42/10,11,12,13,14,15,16,17,18";
    public final static String trueblock1_c = "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15/1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30";
    public final static ValueBlock true1c = new ValueBlock(trueblock1_c);
    public final static String block_ga_c = "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16/1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30";
    public final static String block_gd_c = "1,2,3,4,5,6,7,8,9,10,11,12,13,14/1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30";
    public final static String block_ea_c = "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15/1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31";
    public final static String block_ed_c = "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15/1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29";
    public final static String trueblock2_c = "10,11,12,13,14,15,16,17,18,19/23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42";
    public final static ValueBlock true2c = new ValueBlock(trueblock2_c);
    public final static String block2_ga_c = "10,11,12,13,14,15,16,17,18,19,1/23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42";
    public final static String block2_gd_c = "10,11,12,13,14,15,16,17,18/23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42";
    public final static String block2_ea_c = "10,11,12,13,14,15,16,17,18,19/23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,1";
    public final static String block2_ed_c = "10,11,12,13,14,15,16,17,18,19/23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41";
    public final static String trueblock1_e = "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30/1,2,3,4,5,6,7,8,9,10,11,12,13,14,15";
    public final static ValueBlock true1e = new ValueBlock(trueblock1_e);
    public final static String trueblock2_e = "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15/1,2,3,4,5,6,7,8,9,10,11,12,13,14,15";
    public final static ValueBlock true2e = new ValueBlock(trueblock2_e);
    public final static String trueblock1_ec = "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15/1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30";
    public final static ValueBlock true1ec = new ValueBlock(trueblock1_ec);
    public final static String trueblock2_ec = "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15/1,2,3,4,5,6,7,8,9,10,11,12,13,14,15";
    public final static ValueBlock true2ec = new ValueBlock(trueblock2_ec);
}
