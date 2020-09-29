package DataMining.util;

import DataMining.BlockMethods;
import DataMining.ValueBlock;
import DataMining.ValueBlockList;
import DataMining.ValueBlockListMethods;
import util.StringUtil;
import util.TextFile;

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

    /**
     * @param args
     */
    public EvalUniBicRelevRecover(String[] args) {

        // /global/scratch/marcin/UniBic/UniBic_data_result/data/overlap_bic/overlap_0_0

        int lastslash = args[1].lastIndexOf("/");
        boolean trim = false;
        if (lastslash == args[1].length() - 1) {
            trim = true;
            lastslash = NthLastIndexOf(2, args[1], "/");
        }
        String prefix = args[1].substring(lastslash + 1, trim ? args[1].length() - 1 : args[1].length());

        System.out.println("prefix " + prefix);

        ArrayList recovery_vals = new ArrayList();
        ArrayList relevance_vals = new ArrayList();

        int limit = 5;
        if (args[1].indexOf("narrow") != -1)
            limit = 3;
        for (int i = 0; i < limit; i++) {

            if (prefix.indexOf("tpye") != -1)
                prefix = StringUtil.replace(prefix, "tpye", "type");
            String readtest = args[1] + "/" + dirs[i] + "/level14.1/" + "results_" + prefix + "_" + files[i] + "_cut_scoreperc66.0_exprNaN_0.0__nr_0.25_score_root.txt";
            System.out.println("test " + readtest);
            ValueBlockList testvbl = null;
            try {
                testvbl = ValueBlockList.read(readtest, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("testvbl " + testvbl.size());

            String readref = args[0] + "/" + "" + prefix + "_" + files[i] + "_hiddenBics.txt";
            System.out.println("ref " + readref);
            ValueBlockList refvbl = null;
            try {
                refvbl = ValueBlockListMethods.readUniBic(readref, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("refvbl " + refvbl.size());

            double jaccard_max_recovery = 0;
            int max_recovery_pos = -1;
            double[] recovery_vals_d = new double[refvbl.size()];
            for (int a = 0; a < refvbl.size(); a++) {

                for (int b = 0; b < testvbl.size(); b++) {
                    double jaccard = BlockMethods.JaccardIndexGenesExps((ValueBlock) refvbl.get(a), (ValueBlock) testvbl.get(b));
                    if (jaccard > jaccard_max_recovery) {
                        jaccard_max_recovery = jaccard;
                        max_recovery_pos = b;
                    }
                }
                System.out.println(max_recovery_pos + "\t" + jaccard_max_recovery);
                recovery_vals_d[a] = jaccard_max_recovery;
            }


            double jaccard_max_relevance = 0;
            int max_relevance_pos = -1;

            double[] relevance_vals_d = new double[testvbl.size()];
            for (int b = 0; b < testvbl.size(); b++) {

                for (int a = 0; a < refvbl.size(); a++) {
                    double jaccard = BlockMethods.JaccardIndexGenesExps((ValueBlock) testvbl.get(b), (ValueBlock) refvbl.get(a));
                    if (jaccard > jaccard_max_relevance) {
                        jaccard_max_relevance = jaccard;
                        max_relevance_pos = a;
                    }
                }
                System.out.println(max_relevance_pos + "\t" + jaccard_max_relevance);
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

            recovery_vals.add("" + mean_recovery);
            relevance_vals.add("" + mean_relevance);
        }


        TextFile.write(recovery_vals, prefix + "_recovery.txt");
        TextFile.write(relevance_vals, prefix + "_relevance.txt");
    }


    /**
     * @param Nth
     * @param input
     * @param targetchar
     * @return
     */
    static int NthLastIndexOf(int Nth, String input, String targetchar) {
        if (Nth <= 0) return input.length();
        return NthLastIndexOf(--Nth, targetchar, input.substring(0, input.lastIndexOf(targetchar)));
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 2) {
            EvalUniBicRelevRecover arg = new EvalUniBicRelevRecover(args);
        } else {
            System.out.println("syntax: java DataMining.EvalUniBicRelevRecover\n" +
                    "<ref biclusters dir>\n" +
                    "<test biclusters dir>"
            );
        }
    }
}