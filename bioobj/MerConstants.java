package bioobj;

/**
 * Created by IntelliJ IDEA.
 * User: marcin
 * Date: May 10, 2006
 * Time: 4:47:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class MerConstants {

    /*For region 200 positions long.*/

    public int dblen = 200;
    public int mer_max = 11;

    public int[] mer_lengths = createMerDBLengths(dblen, mer_max);

    public final static double len200_5mer = 196.0;
    public final static double len200_7mer = 194.0;
    public final static double len200_11mer = 190.0;


    public final static double total5mers = 1024;//Math.pow(4, 5);//
    public final static double total7mers = 16384;//Math.pow(4, 7);//16384;
    public final static double total11mers = 4194304;//Math.pow(4, 11);//;//3.0948500982e26

    public final static double[] total_mers = {-1,-1,-1,total5mers,-1,total7mers,-1,-1,-1,total11mers};

    public final static double percentile1_len200_5mer = 1.0/len200_5mer;
    public final static double percentile1_len200_7mer = 1.0/len200_7mer;
    public final static double percentile1_len200_11mer = 1.0/len200_11mer;
    public final static double number1_len200_5mer = percentile1_len200_5mer * total5mers;
    public final static double number1_len200_7mer = (percentile1_len200_7mer) * total7mers;
    public final static double number1_len200_11mer = (percentile1_len200_11mer) * total11mers;

    public final static double percentile10_len200_5mer = 10.0/len200_5mer;
    public final static double percentile10_len200_7mer = 10.0/len200_7mer;
    public final static double percentile10_len200_11mer = 10.0/len200_11mer;
    public final static double number10_len200_5mer = (10.0/len200_5mer) * total5mers;
	public final static double number10_len200_7mer = (10.0/len200_7mer) * total7mers;
    public final static double number10_len200_11mer = (10.0/len200_11mer) * total11mers;

    /*Assuming .01 and .1 percentiles, for e-value = 1 and =10 calculations.*/
    public final static double top5seqs1 = percentile1_len200_5mer* total5mers;//10;//retPercentileforEval(1,len200_5mer,5);//10;
    public final static double top7seqs1 = percentile1_len200_7mer* total7mers;//180;//retPercentileforEval(1,len200_7mer,7);//180;
    public final static double top11seqs1 = percentile1_len200_11mer* total11mers;//41943;//retPercentileforEval(1,len200_11mer,11);//41943;
    public final static double top5seqs10 = percentile1_len200_5mer* total5mers;//102;//retPercentileforEval(10,len200_5mer,5);//102;
    public final static double top7seqs10 = percentile1_len200_7mer* total7mers;//1638;//retPercentileforEval(10,len200_7mer,7);//1638;
    public final static double top11seqs10 = percentile1_len200_11mer* total11mers;//419430;//retPercentileforEval(10,len200_11mer,11);//419430;


    /**
     * Returns the empirical distribution percentile for a given evalue, sequence length, and length of match.
     * @param evalue
     * @param length
     * @param mer
     * @return percentile
     */
    public final static double retPercentileforEval(double evalue, double length, int mer) {

           return (evalue/length) * total_mers[mer-1];
    }


    public final static int[] createMerDBLengths(int mer_max, int db_len) {

        int[] ret = new int[mer_max];

        for (int i=0; i < mer_max;i++)
        ret[i] = db_len - i;


        return ret;
    }

}
