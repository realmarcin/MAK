package DataMining.util;

import DataMining.*;
import mathy.SimpleMatrix;
import util.MoreArray;
import util.StringUtil;
import util.TextFile;

import java.io.File;
import java.util.ArrayList;

public class EvalSaelensRelevRecover {

    boolean debug = false;

    /**
     * @param args
     */
    public EvalSaelensRelevRecover(String[] args) {

        System.out.println("args[0] " + args[0]);
        SimpleMatrix sm = new SimpleMatrix(args[0]);

        System.out.println("sm.ylabels " + MoreArray.toString(sm.ylabels));
        System.out.println("args[2] " + args[2]);

        ArrayList recovery_vals = new ArrayList();
        ArrayList relevance_vals = new ArrayList();

        String readtest = args[2];
        System.out.println("test " + readtest);
        ValueBlockList testvbl = null;
        try {
            testvbl = ValueBlockList.read(readtest, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("testvbl " + testvbl.size());

        File testref = new File(args[1]);
        if (!testref.isDirectory()) {
            doOne(args[1], sm, recovery_vals, relevance_vals, testvbl);
        } else {
            String[] list = testref.list();
            for (int i = 0; i < list.length; i++) {
                System.out.println(list[i]);
                doOne(args[1] + "/" + list[i], sm, recovery_vals, relevance_vals, testvbl);
            }
        }
    }

    /**
     * @param arg
     * @param sm
     * @param recovery_vals
     * @param relevance_vals
     * @param testvbl
     */
    private void doOne(String arg, SimpleMatrix sm, ArrayList recovery_vals, ArrayList relevance_vals, ValueBlockList testvbl) {
        String readref = arg;
        //System.out.println("ref " + readref);
        ValueBlockList refvbl = null;
        try {
            refvbl = ValueBlockListMethods.readJSONGenes(readref, sm.ylabels);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println("refvbl " + refvbl.size());

        String outref = "ref.vbl";
        //System.out.println("writing " + outref);
        String outrefstr = refvbl.toString(MINER_STATIC.HEADER_VBL);//+ ValueBlock.toStringShortColumns()
        TextFile.write(outrefstr, outref);

        double jaccard_max_recovery = 0;
        int max_recovery_pos = -1;
        double[] recovery_vals_d = new double[refvbl.size()];
        for (int a = 0; a < refvbl.size(); a++) {

            for (int b = 0; b < testvbl.size(); b++) {
                double jaccard = BlockMethods.JaccardIndexGenes((ValueBlock) refvbl.get(a), (ValueBlock) testvbl.get(b));
                if (jaccard > jaccard_max_recovery) {
                    jaccard_max_recovery = jaccard;
                    max_recovery_pos = b;
                }
            }
            if (debug)
                System.out.println(max_recovery_pos + "\t" + jaccard_max_recovery);
            recovery_vals_d[a] = jaccard_max_recovery;
        }


        double jaccard_max_relevance = 0;
        int max_relevance_pos = -1;

        double[] relevance_vals_d = new double[testvbl.size()];
        for (int b = 0; b < testvbl.size(); b++) {

            for (int a = 0; a < refvbl.size(); a++) {
                double jaccard = BlockMethods.JaccardIndexGenes((ValueBlock) testvbl.get(b), (ValueBlock) refvbl.get(a));
                if (jaccard > jaccard_max_relevance) {
                    jaccard_max_relevance = jaccard;
                    max_relevance_pos = a;
                }
            }
            if (debug)
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


        String outpath1 = "recovery.txt";
        System.out.println("outpath1");
        System.out.println(outpath1);
        TextFile.write(recovery_vals, outpath1);
        String outpath2 = "relevance.txt";
        TextFile.write(relevance_vals, outpath2);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 3) {
            DataMining.util.EvalSaelensRelevRecover arg = new DataMining.util.EvalSaelensRelevRecover(args);
        } else {
            System.out.println("syntax: java DataMining.util.EvalSaelensRelevRecover\n" +
                    "<ref data set or dir>\n" +
                    "<ref biclusters dir>\n" +
                    "<test biclusters dir>"
            );
        }
    }
}
