package DataMining;

import mathy.Matrix;
import util.DirFilter;
import util.MoreArray;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * User: marcin
 * Date: Feb 27, 2011
 * Time: 4:46:28 PM
 */
public class EvaluateAllStarts {

    boolean debug = true;
    String indir, workdir, paramfile;

    InitRandVar irv;
    Parameters prm;
    boolean needinv = false;


    /**
     * @paramfile args
     */
    public EvaluateAllStarts(String[] args) {
        init(args);

        File f = new File(indir);


        for (int i = 0; i < MINER_STATIC.CRIT_LABELS.length; i++) {
            Criterion criterion = new Criterion(i, false, true, prm.USE_ABS_AR, false, prm.needinv, true, prm.FRXN_SIGN, debug);
            DirFilter df = new DirFilter("__" + MINER_STATIC.CRIT_LABELS[i] + "__*toplist.txt");
            String[] list = f.list(df);

            ValueBlockList finals = new ValueBlockList();
            HashMap members = new HashMap();
            for (int j = 0; j < list.length; j++) {

                ValueBlockList vbl = null;
                try {
                    vbl = ValueBlockList.read(indir + "/" + list[i], false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                int ind = ValueBlockListMethods.lastBlockGeneAddition(vbl);

                ValueBlock vb = (ValueBlock) vbl.get(ind);
                finals.add(vb);

                ValueBlockListMethods.writeBIC(MINER_STATIC.CRIT_LABELS[i] + "_finalgeneexpadd.bic", finals);

                for (int a = 0; a < vb.genes.length; a++) {
                    for (int b = 0; b < vb.exps.length; b++) {
                        String key = "" + vb.genes[a] + "_" + vb.exps[b];
                        Object get = members.get(key);
                        if (get == null)
                            members.put(key, "" + i);
                        else {
                            String s = (String) get;
                            s += "_" + i;
                            members.put(key, s);
                        }
                    }
                }

                String hamout = MINER_STATIC.CRIT_LABELS[i] + "_hamming.txt";
                BlockMembers bm = new BlockMembers(members, hamout);

                String s2 = "data <- read.table(" + hamout + ")";
                irv.Rengine.eval(s2);
                System.out.println("R: " + s2);
                String s3 = "row.names(data) <- data[,1]";
                irv.Rengine.eval(s3);
                System.out.println("R: " + s3);
                String s4 = "data <- data[,-1]";
                irv.Rengine.eval(s4);
                System.out.println("R: " + s4);
                String s5 = "hclust_gene_exp <- hcluster(data, method=\"binary\")";
                irv.Rengine.eval(s5);
                System.out.println("R: " + s5);

                /*
                R.hclust:
                The algorithm used in hclust is to order the subtree so that the tighter cluster
                is on the left (the last, i.e., most recent, merge of the left subtree is at a
                lower value than the last merge of the right subtree). Single observations are
                the tightest clusters possible, and merges involving two observations place
                them in order by their observation sequence number.
                */

                String s6 = "save(hclust_gene_exp, file=\"hclust_" + MINER_STATIC.CRIT_LABELS[i] + "_hamming_gene_exp\")";
                irv.Rengine.eval(s6);
                System.out.println("R: " + s6);
                String s7 = "pdf(\"hclust_" + MINER_STATIC.CRIT_LABELS[i] + "_hamming_gene_exp.pdf\",width=8,height=11)";
                irv.Rengine.eval(s7);
                System.out.println("R: " + s7);
                String s8 = "plot(hclust_gene_exp)";
                irv.Rengine.eval(s8);
                System.out.println("R: " + s8);
                String s9 = "dev.off(2)";
                /*irv.Rengine.eval(s9);
                System.out.println("R: "+s9);
                String s10 = "hcut <- 0.1";
                irv.Rengine.eval(s10);
                System.out.println("R: "+s10);
                String s11 = "hclust_gene_exp_cut <- cutree(hclust_gene_exp,h=hcut)";
                irv.Rengine.eval(s11);
                System.out.println("R: "+s11);
                String s12 = "write.table(hclust_gene_exp_cut, file=\"hclust_MSEC_gene_exp_cut\", sep=\"\\t\")";
                irv.Rengine.eval(s12);
                System.out.println("R: "+s12);*/

                /*irv.Rengine.eval(s9);
                System.out.println("R: " + s9);
                String s10 = "uheight <- unique(hclust_block$height)";
                irv.Rengine.eval(s10);*/

                irv.Rexpr = irv.Rengine.eval("hclust_block$merge");
                int[][] merge = MoreArray.doubletoIntMatrix(irv.Rexpr.asMatrix());

                irv.Rexpr = irv.Rengine.eval("hclust_block$order");
                int[] order = irv.Rexpr.asIntArray();

                int pos = 0;
                ArrayList clusters = new ArrayList();
                ArrayList clustersData = new ArrayList();
                ArrayList curcluster = new ArrayList();
                ArrayList curclusterData = new ArrayList();
                double curscore = 0;
                double lastcrit = 0;
                while (pos < merge.length) {
                    //if first value is a leaf
                    if (merge[pos][0] < 0) {
                        curcluster.add(order[-merge[pos][0]]);
                        int[] ints = Matrix.extractColumn(bm.hammingdistblock, -merge[pos][0]);
                        int w = ints.length;
                        curclusterData.add(ints);

                        boolean retjri = irv.Rengine.assign("sel_cols", MoreArray.ArrayListtoInt(curcluster));
                        irv.Rengine.eval("row_sums <- rowSums(data)");
                        irv.Rengine.eval("sel_rows <- which(row_sums > 1)");

                        irv.Rexpr = irv.Rengine.eval("data[sel_rows, sel_cols]");
                        int[][] data = MoreArray.doubletoIntMatrix(irv.Rexpr.asMatrix());

                        irv.Rexpr = irv.Rengine.eval("sel_rows");
                        int[] rows = irv.Rexpr.asIntArray();

                        ArrayList genes = new ArrayList();
                        ArrayList exps = new ArrayList();
                        for (int k = 0; k > rows.length; k++) {
                            String cur = bm.ylabels[rows[k]];
                            String[] ge = cur.split("_");
                            genes.add(Integer.parseInt(ge[0]));
                            exps.add(Integer.parseInt(ge[1]));
                        }


                    }
                    //if second value is a leaf
                    if (merge[pos][1] < 0) {
                        curcluster.add(order[-merge[pos][1]]);
                        curclusterData.add(Matrix.extractColumn(bm.hammingdistblock, -merge[pos][1]));
                    }

                    ValueBlock merged = new ValueBlock();


                    double fullcrit = scoreBlock(criterion, merged);
                    if (fullcrit > lastcrit) {
                        lastcrit = fullcrit;
                    } else {
                        clusters.add(curcluster);
                        clustersData.add(curcluster);
                        curcluster = new ArrayList();
                    }

                    //if both values are internal nodes
                    if (merge[pos][0] > 0 && merge[pos][1] > 0) {

                    }
                }

            }
        }
    }

    /**
     * @param criterion
     * @param merged
     * @return
     */
    private double scoreBlock(Criterion criterion, ValueBlock merged) {
        boolean[] passcrits = Criterion.getExprCritTypes(criterion, true, true, debug);
        double[][] crits = null;
        try {
            /*TODO add functionality for feature data*/
            crits = ComputeCrit.compute(irv, merged.genes, merged.exps,
                    prm, criterion,
                    irv.gene_labels, null, debug);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (crits != null) {
            double[] critvals = Matrix.extractColumn(crits, 1);
            return ValueBlock.computeFullCrit(critvals, true, passcrits, debug);
        } else
            return Double.NaN;
    }

    /**
     * @paramfile args
     */
    private void init(String[] args) {
        indir = args[0];
        workdir = args[1];
        paramfile = args[2];

        prm = new Parameters();
        prm.read(paramfile);
        needinv = prm.MEANMSE_PATH == null && prm.MEANMSER_PATH == null && prm.MEANMSEC_PATH == null ? true : false;
        irv = new InitRandVar(prm, prm.RANDOM_SEED, prm.R_METHODS_PATH, prm.R_DATA_PATH, debug);

        String s1 = "setwd(" + workdir + ")";
        irv.Rengine.eval(s1);
        System.out.println("R: " + s1);
    }

    /**
     * @paramfile args
     */

    public final static void main(String[] args) {
        if (args.length == 3) {
            EvaluateAllStarts rm = new EvaluateAllStarts(args);
        } else {
            System.out.println("syntax: java DataMining.util.ListfromDir\n" +
                    "< dir of ValueBlockLists>\n" +
                    "< working dir>\n" +
                    "< parameter file>"
            );
        }
    }
}
