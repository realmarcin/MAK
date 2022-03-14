package DataMining.util;

import DataMining.BlockMethods;
import DataMining.ValueBlock;
import DataMining.ValueBlockList;
import DataMining.ValueBlockListMethods;
import mathy.stat;
import util.MoreArray;
import util.StringUtil;
import util.TextFile;

import java.io.File;
import java.util.ArrayList;

/**
 * User: marcin
 * Date: 12/2/19
 * Time: 4:44 PM
 */
public class EvalUniBicRelevRecover {

    boolean debug = false;

    String[] dirs = {"ONE", "TWO", "THREE", "FOUR", "FIVE"};
    String[] files = {"data1", "data2", "data3", "data4", "data5"};
    int topN = -1;

    /**
     * @param args
     */
    public EvalUniBicRelevRecover(String[] args) {

        // /global/scratch/marcin/UniBic/UniBic_data_result/data/overlap_bic/overlap_0_0
        System.out.println("args[1] " + args[1]);
        int lastslash = args[1].lastIndexOf("/");
        boolean trim = false;
        if (lastslash == args[1].length() - 1) {
            trim = true;
            lastslash = NthLastIndexOf(1, args[1], "/");
        }

        String prefix = args[1].substring(lastslash + 1, trim ? args[1].length() - 1 : args[1].length());
        System.out.println("prefix " + prefix);

        int secondlastslash = NthLastIndexOf(2, args[1], "/");
        String unique_prefix = args[1].substring(secondlastslash + 1, lastslash);
        System.out.println("unique_prefix " + unique_prefix);

        int thirdlastslash = NthLastIndexOf(3, args[1], "/");
        String unique_label = args[1].substring(thirdlastslash + 1, secondlastslash);
        System.out.println("unique_label " + unique_label);

        ArrayList recovery_vals = new ArrayList();
        ArrayList relevance_vals = new ArrayList();

        int limit = 5;
        if (args[1].indexOf("narrow") != -1)
            limit = 3;

        if(args.length == 3)
            topN = Integer.parseInt(args[2]);

        for (int i = 0; i < limit; i++) {

            if (prefix.indexOf("tpye") != -1)
                prefix = StringUtil.replace(prefix, "tpye", "type");
            String readtest = args[1] + "/" + dirs[i] + "/level14.1/" + "results_" + prefix + "_" + files[i] + "_cut_scoreperc66.0_exprNaN_0.0__nr_0.25_score_root.txt";
            System.out.println("test " + readtest);
            File testf = new File(readtest);
            if(!testf.exists()) {
                readtest = args[1] + "/" + dirs[i] + "/level14.1/" + "results_" + prefix + "_" + files[i] + "_cut_scoreperc90.0.0_exprNaN_0.0__nr_0.25_score_root.txt";
                testf = new File(readtest);
                if(!testf.exists()) {
                    readtest = args[1] + "/" + dirs[i] + "/level14.1/" + "results_" + prefix + "_" + files[i] + "_cut_scoreperc80.0.0_exprNaN_0.0__nr_0.25_score_root.txt";
                    /*testf = new File(readtest);
                    if(!testf.exists()) {
                        readtest = args[1] + "/" + dirs[i] + "/level14.1/" + "results_" + prefix + "_" + files[i] + "_cut_scoreperc80.0.0_exprNaN_0.0__nr_0.25_score_root.txt";
                    }*/
                }
            }
            ValueBlockList testvbl = null;
            try {
                testvbl = ValueBlockList.read(readtest, false);
            } catch (Exception e) {
                e.printStackTrace();

                try {
                    readtest = args[1] + "/" + dirs[i] + "/level14.1/" + "results_" + prefix + "_" + files[i] + "__nr_0.25_score_root.txt";
                    testvbl = ValueBlockList.read(readtest, false);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
            System.out.println("testvbl " + testvbl.size());

            String readref = args[0] + "/" + "" + prefix + "_" + files[i] + "_hiddenBics.txt";
            System.out.println("ref " + readref);
            ValueBlockList refvbl = null;
            try {
                refvbl = ValueBlockListMethods.readUniBic(readref, 1, false);
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("refvbl " + refvbl.size());
            System.out.println(refvbl.toString());

            double jaccard_max_recovery = 0;
            int max_recovery_pos = -1;
            double[] recovery_vals_d = new double[refvbl.size()];

            int maxTest = topN != -1? Math.min(topN, testvbl.size()):testvbl.size();

            for (int a = 0; a < refvbl.size(); a++) {
                for (int b = 0; b < maxTest; b++) {
                    double jaccard = BlockMethods.JaccardIndexGenes((ValueBlock) refvbl.get(a), (ValueBlock) testvbl.get(b));//JaccardIndexGenesExps
                    if (jaccard > jaccard_max_recovery) {
                        jaccard_max_recovery = jaccard;
                        max_recovery_pos = b;
                    }
                }
                System.out.println("ref "+max_recovery_pos + "\t" + jaccard_max_recovery);
                recovery_vals_d[a] = jaccard_max_recovery;
            }

            double jaccard_max_relevance = 0;
            int max_relevance_pos = -1;

            double[] relevance_vals_d = new double[testvbl.size()];
            for (int b = 0; b < maxTest; b++) {
                for (int a = 0; a < refvbl.size(); a++) {
                    double jaccard = BlockMethods.JaccardIndexGenes((ValueBlock) testvbl.get(b), (ValueBlock) refvbl.get(a));//JaccardIndexGenesExps
                    if (jaccard > jaccard_max_relevance) {
                        jaccard_max_relevance = jaccard;
                        max_relevance_pos = a;
                    }
                }
                System.out.println("test "+max_relevance_pos + "\t" + jaccard_max_relevance);
                relevance_vals_d[b] = jaccard_max_relevance;
            }
            double mean_recovery = mathy.stat.avg(recovery_vals_d);
            double mean_relevance = mathy.stat.avg(relevance_vals_d);
            double sd_recovery = mathy.stat.SD(recovery_vals_d, mean_recovery);
            double sd_relevance = mathy.stat.SD(relevance_vals_d, mean_relevance);

            System.out.println("Recovery");
            System.out.println(mean_recovery + "\t+/- " + sd_recovery);

            System.out.println("Relevance");
            System.out.println(mean_relevance + "\t+/- " + sd_relevance);

            recovery_vals.add(mean_recovery);
            relevance_vals.add(mean_relevance);
        }


        String outpath1 = unique_label+"_"+unique_prefix + "__" + prefix + "_recovery.txt";
        System.out.println("outpath1");
        System.out.println(outpath1);
        TextFile.write(MoreArray.toStringArray(MoreArray.ArrayListtoDouble(recovery_vals)), outpath1);
        String outpath2 = unique_label+"_"+unique_prefix + "__" + prefix + "_relevance.txt";
        TextFile.write(MoreArray.toStringArray(MoreArray.ArrayListtoDouble(relevance_vals)), outpath2);

        MoreArray.printArray(MoreArray.ArrayListtoDouble(recovery_vals));
        MoreArray.printArray(MoreArray.ArrayListtoDouble(relevance_vals));

        ArrayList recov_stats = new ArrayList();
        double rec_avg = stat.avg(MoreArray.ArrayListtoDouble(recovery_vals));
        double rec_sd = stat.SD(MoreArray.ArrayListtoDouble(recovery_vals), rec_avg);
        recov_stats.add(rec_avg);
        recov_stats.add(rec_sd);
        System.out.println("Recovery "+rec_avg+"\t"+rec_sd);

        ArrayList relev_stats = new ArrayList();
        double relev_avg = stat.avg(MoreArray.ArrayListtoDouble(relevance_vals));
        double relev_sd = stat.SD(MoreArray.ArrayListtoDouble(relevance_vals), relev_avg);
        relev_stats.add(relev_avg);
        relev_stats.add(relev_sd);
        System.out.println("Relevance "+relev_avg+"\t"+relev_sd);

        String outpath1s = unique_label+"_"+unique_prefix + "__" + prefix + "_recovery_stats.txt";
        System.out.println("outpath1s");
        System.out.println(outpath1s);
        TextFile.write(MoreArray.toStringArray(MoreArray.ArrayListtoDouble(recov_stats)), outpath1s);
        String outpath2s = unique_label+"_"+unique_prefix + "__" + prefix + "_relevance_stats.txt";
        TextFile.write(MoreArray.toStringArray(MoreArray.ArrayListtoDouble(relev_stats)), outpath2s);
    }


    /**
     * @param Nth
     * @param input
     * @param targetchar
     * @return
     */
    static int NthLastIndexOf(int Nth, String input, String targetchar) {
        if (Nth <= 0) return input.length();
        System.out.println("input " + input);
        int endIndex = input.lastIndexOf(targetchar);
        System.out.println("endIndex " + endIndex);
        String substring = input.substring(0, endIndex);
        System.out.println("substring " + substring);
        System.out.println("--Nth " + (Nth - 1) + "\t" + targetchar);
        return NthLastIndexOf(--Nth, substring, targetchar);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 2 || args.length == 3) {
            EvalUniBicRelevRecover arg = new EvalUniBicRelevRecover(args);
        } else {
            System.out.println("syntax: java DataMining.util.EvalUniBicRelevRecover\n" +
                    "<ref biclusters dir>\n" +
                    "<test biclusters dir>\n"+
                    "<optional top N>"
            );
        }
    }
}
