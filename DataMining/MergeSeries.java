package DataMining;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: Nov 13, 2012
 * Time: 9:44:11 PM
 */
public class MergeSeries {

    /**
     * @param args
     */
    public MergeSeries(String[] args) {

        boolean done = false;
        int round = 0;
        while (!done) {
            System.out.println("round " + round);
            String[] passargs = {
                    "-dir",
                    args[0],
                    "-crit",
                    "MSEC_KendallC_FEMC_inter_feat_MAXTF_MEDCMEAN",
                    "-param",
                    "../yeast_cmonkey_weighexpr_parameters_TF_inter_feat_mean.txt",
                    "-ocut",
                    "0.5",
                    "-misscut",
                    "0.25",
                    "-numgene",
                    "1000",
                    "-complete",
                    "y",
                    "-sum",
                    "n",
                    "-out",
                    args[0]

            };
            ListMergeMembers lmm = new ListMergeMembers(args);
            if (lmm.num_merged == 0)
                done = true;
            else
                round++;
        }

    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 1) {
            MergeSeries rm = new MergeSeries(args);
        } else {
            System.out.println("syntax: java DataMining.MergeSeries\n" +
                    "<input value block list>"
            );
        }
    }
}
