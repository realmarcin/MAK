package DataMining.util;

import java.io.IOException;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: Jan 10, 2012
 * Time: 11:00:43 PM
 */
public class RunPipe {


    /**
     * @param args
     */
    public RunPipe(String[] args) {

        Runtime R = Runtime.getRuntime();
        Process P = null;

        try {
            String totallistname = args[0];
            if (totallistname.indexOf("/") == args[0].length() - 1)
                totallistname = totallistname.substring(0, args[0].length() - 2);
            String totallist = totallistname + ".vbl";
            P = R.exec("java DataMining.ListfromDir " + args[0] + " " + totallist);
            P.waitFor();

            String cutlistname = totallistname + "_cut";
            String cutlistfile = totallistname + "_cut.txt";
            String s = "java DataMining.util.ApplyCut " + totallist + " 0.99 0.05";
            System.out.println(s);
            P = R.exec(s);
            P.waitFor();

            String s1 = "java -mx64000M DataMining.BlockMembers " + cutlistname + " members_" + totallistname + " __MSEC_KendallC_GEECE__ &> BlockMembers_top.out";
            System.out.println(s1);
            P = R.exec(s1);
            P.waitFor();

            String s2 = "cp HammingMembers_template.R HammingMembers_1.R";
            System.out.println(s2);
            P = R.exec(s2);
            P.waitFor();

            String s3 = "perl -pi -e \"s/memberdata/" + cutlistname + "/g\" HammingMembers_1.R";
            System.out.println(s3);
            P = R.exec(s3);
            P.waitFor();

            String s4 = "R --vanilla < HammingMembers_1.R &> HammingMembers_1.out";
            System.out.println(s4);
            P = R.exec(s4);
            P.waitFor();

            String s5 = "java -mx2000M DataMining.TreeMergeMembers hclust_block_cut_" + cutlistname + ".txt " + cutlistfile + " hclust_block_cut_" + cutlistname + "_merged.txt __MSEC_Kendall_GEECE__ 6160 667 &> TreeMergeMembers.out";
            System.out.println(s5);
            P = R.exec(s5);
            P.waitFor();

            String s6 = "java DataMining.util.ScoreBlocks -bic hclust_block_cut_" + cutlistname + "_merged.txt -tab 4932.tab -param yeast_cmonkey_weighexpr_parameters.txt -goyeast go_slim_mapping.tab -GOT localization_terms.tab -TF mccord_ranks.txt -TFREF RegulationMatrix_Documented_20101213.txt -crit MSEC_KendallC_GEECE  &> score_C_merged.out";
            System.out.println(s6);
            P = R.exec(s6);
            P.waitFor();

            String s7 = "java DataMining.SelectMerged results_yeast_cmonkey_1_expr_round1_top0.01_5percstart.txt hclust_block_cut_" + cutlistname + "_merged_MSEC_KendallC_GEECE.txt " + cutlistname + "_merged_select.vbl &> SelectMerged.out";
            System.out.println(s7);
            P = R.exec(s7);
            P.waitFor();

            String s8 = "java -mx10000M DataMining.func.BuildGraph -bic " + cutlistname + "_merged_select.vbl -expr yeast_cmonkey.txt -mode member -tab 4932.tab -TFREF RegulationMatrix_Documented_20101213.txt -TFEXP mccord_ranks.txt -tab 4932.tab -goyeast go_slim_mapping.tab -GOT localization_terms.tab &> BuildGraph_merged_member.out";
            System.out.println(s8);
            P = R.exec(s8);
            P.waitFor();

            String s9 = "java -mx10000M DataMining.func.BuildGraph -bic " + cutlistname + "_merged_select.vbl -expr yeast_cmonkey.txt -mode tf -tab 4932.tab -TFREF RegulationMatrix_Documented_20101213.txt -TFEXP mccord_ranks.txt -tab 4932.tab -goyeast go_slim_mapping.tab -GOT localization_terms.tab &> BuildGraph_merged_tf.out";
            System.out.println(s9);
            P = R.exec(s9);
            P.waitFor();

            String s10 = "java -mx10000M DataMining.func.BuildGraph -bic " + cutlistname + "_merged_select.vbl -expr yeast_cmonkey.txt -mode COG -tab 4932.tab -TFREF RegulationMatrix_Documented_20101213.txt -TFEXP mccord_ranks.txt -tab 4932.tab -goyeast go_slim_mapping.tab -GOT localization_terms.tab &> BuildGraph_merged_COG.out";
            System.out.println(s10);
            P = R.exec(s10);
            P.waitFor();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
/*
java DataMining.ListfromDir results_yeast_cmonkey_1_top/ results_yeast_cmonkey_1.vbl

java DataMining.util.ApplyCut results_yeast_cmonkey_1_expr_round1_top.vbl 0.99 0.05

java -mx64000M DataMining.BlockMembers results_yeast_cmonkey_1_expr_round1_topcutfromr2_5percstart.txt members_MSEC_KendallC_GEECE_yeast_topcutfrom2 __MSEC_KendallC_GEECE__ &> BlockMembers_top.out

cp HammingMembers_template.R HammingMembers_1.R

perl -pi -e "s/memberdata/759820/g" HammingMembers_1.R

R --vanilla < HammingMembers_1.R &> HammingMembers_1.out

java -mx2000M DataMining.TreeMergeMembers hclust_block_cut_results_yeast_cmonkey_1_expr_mergedround1_top0.01_5percstart__MSEC_KendallC_GEECE.txt results_yeast_cmonkey_1_expr_round1_top0.01_5percstart.txt hclust_block_cut_MSEC_Kendall_GEECE_top0.01_5percstart_round1_merged.txt __MSEC_Kendall_GEECE__ 6160 667 &> TreeMergeMembers.out

java DataMining.util.ScoreBlocks -bic hclust_block_cut_MSEC_Kendall_GEECE_top0.01_5percstart_round1_merged.txt -tab 4932.tab -param yeast_cmonkey_weighexpr_parameters.txt -goyeast go_slim_mapping.tab -GOT localization_terms.tab -TF mccord_ranks.txt -TFREF RegulationMatrix_Documented_20101213.txt -crit MSEC_KendallC_GEECE  &> score_C_merged.out

java DataMining.SelectMerged results_yeast_cmonkey_1_expr_round1_top0.01_5percstart.txt hclust_block_cut_MSEC_Kendall_GEECE_top0.01_5percstart_round1_merged_MSEC_KendallC_GEECE.txt results_yeast_cmonkey_1_expr_round1_top_merged_select.vbl &> SelectMerged.out

java -mx10000M DataMining.func.BuildGraph -bic expr_round1_merged.txt -expr yeast_cmonkey.txt -mode member -tab 4932.tab -TFREF RegulationMatrix_Documented_20101213.txt -TFEXP mccord_ranks.txt -tab 4932.tab -goyeast go_slim_mapping.tab -GOT localization_terms.tab &> BuildGraph_merged_member.out
java -mx10000M DataMining.func.BuildGraph -bic expr_round1_merged.txt -expr yeast_cmonkey.txt -mode tf -tab 4932.tab -TFREF RegulationMatrix_Documented_20101213.txt -TFEXP mccord_ranks.txt -tab 4932.tab -goyeast go_slim_mapping.tab -GOT localization_terms.tab &> BuildGraph_merged_tf.out
java -mx10000M DataMining.func.BuildGraph -bic expr_round1_merged.txt -expr yeast_cmonkey.txt -mode COG -tab 4932.tab -TFREF RegulationMatrix_Documented_20101213.txt -TFEXP mccord_ranks.txt -tab 4932.tab -goyeast go_slim_mapping.tab -GOT localization_terms.tab &> BuildGraph_merged_COG.out
*/

    }

    /**
     * @param args
     */
    public final static void main(String[] args) {

        if (args.length == 1) {
            RunPipe rm = new RunPipe(args);
        } else {
            System.out.println("syntax: java DataMining.util.RunPipe\n" +
                    "<trajectory directory>"
            );
        }
    }
}
