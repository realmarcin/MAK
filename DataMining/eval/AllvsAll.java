package DataMining.eval;

import DataMining.*;
import util.MapArgOptions;
import util.MoreArray;
import util.TabFile;
import util.TextFile;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: Dec 7, 2012
 * Time: 3:49:55 PM
 */
public class AllvsAll {

    String oneresultspath;
    String tworesultspath;
    String[] valid_args = {"-one", "-two", "-dist", "-out", "-genes", "-exps"};

    String[] dist_types = {"total", "gene", "exp", "geneenrich"};
    HashMap options;

    ArrayList output = new ArrayList();
    ArrayList output2 = new ArrayList();
    ArrayList output2pvals = new ArrayList();

    double thresholdcut = 0.0;//0.25
    String seldist = null;//"gene";
    String outpath;

    InitRandVar irv;
    long seed = MINER_STATIC.RANDOM_SEEDS[0];

    ArrayList pvals_final = new ArrayList();
    ArrayList counts_final = new ArrayList();
    ArrayList map_size_final = new ArrayList();
    ArrayList map_size_total = new ArrayList();
    HashMap bindtotal_pvals_index = new HashMap();

    ArrayList<String> pvals_final_labels = new ArrayList<String>();
    ArrayList<Double> adjust_pvals = new ArrayList<Double>();

    int num_genes;
    int num_exps;

    ArrayList<String> labels = new ArrayList<String>();

    /**
     * @param args
     */
    public AllvsAll(String[] args) {

        System.out.println("AllvsAll thresholdcut " + thresholdcut);
        init(args);

        ValueBlockList two_list = null;
        try {
            two_list = ValueBlockListMethods.readAny(tworesultspath, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ValueBlockList vbl_set = new ValueBlockList();
        File test = new File(oneresultspath);
        if (test.isDirectory()) {//oneresultspath.indexOf(".bic") == -1) {
            File dir = new File(oneresultspath);
            String[] list = dir.list();

            System.out.println("r " + list.length);
            System.out.println("c " + two_list.size());

            for (int i = 0; i < list.length; i++) {
                if (list[i].indexOf("toplist.txt") != -1) {
                    ValueBlockList cur = null;
                    try {
                        cur = ValueBlockListMethods.readAny(oneresultspath + "/" + list[i], false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ValueBlock last = (ValueBlock) cur.get(cur.size() - 1);
                    vbl_set.add(last);
                }
            }
        } else if (oneresultspath.indexOf(".bic") != -1) {
            vbl_set = ValueBlockListMethods.readBIC(oneresultspath, false);
        } else {
            try {
                vbl_set = ValueBlockListMethods.readAny(oneresultspath, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("vbl_set " + vbl_set.size());

        if (vbl_set.size() == 0)
            System.exit(0);

        if (seldist == null) {
            for (int i = 0; i < dist_types.length; i++) {
                seldist = dist_types[i];
                doType(two_list, vbl_set);
            }
        } else {
            doType(two_list, vbl_set);
        }

        try {
            irv.Rengine.end();
        } catch (Exception e) {
            //e.printStackTrace();
        }


    }

    /**
     * @param two_list
     * @param vbl_set
     */
    private void doType(ValueBlockList two_list, ValueBlockList vbl_set) {

        double[][] dists = new double[vbl_set.size()][two_list.size()];
        double[][] pvaldists = new double[vbl_set.size()][two_list.size()];

        int[] vbl_set_single = new int[vbl_set.size()];
        int[] two_single = new int[two_list.size()];
        String[] onelabels = new String[vbl_set.size()];
        String[] twolabels = new String[two_list.size()];
        for (int i = 0; i < vbl_set.size(); i++) {
            onelabels[i] = "" + (i + 1);
            //System.out.print("v");
            System.out.print(".");
            ValueBlock vone = (ValueBlock) vbl_set.get(i);
            ValueBlock vone_nr = BlockMethods.makeNRGeneExps(vone);
            if (vone.genes.length != vone_nr.genes.length) {
                System.out.println("gene dups " + i + "\t" + vone.genes.length + "\t" + vone_nr.genes.length);
                for (int w = 0; w < vone.genes.length; w++) {
                    for (int z = w + 1; z < vone.genes.length; z++) {
                        if (vone.genes[w] == vone.genes[z]) {
                            System.out.println("duplicate gene " + vone.genes[w] + "\t" + w + "\t" + z);
                        }
                    }
                }
            }
            if (vone.exps.length != vone_nr.exps.length) {
                System.out.println("exps dups " + i + "\t" + vone.exps.length + "\t" + vone_nr.exps.length);
                for (int w = 0; w < vone.exps.length; w++) {
                    for (int z = w + 1; z < vone.exps.length; z++) {
                        if (vone.exps[w] == vone.exps[z]) {
                            System.out.println("duplicate exp " + vone.exps[w] + "\t" + w + "\t" + z);
                        }
                    }
                }
            }

            /*if (selfHash[i] == null) {
                selfHash[i] = new HashMap();
                selfHash[i] = util.LoadHash.addPairs(last.genes, last.exps, selfHash[i]);
            }*/
            //System.out.println("v g "+MoreArray.toString(last.genes,","));
            //System.out.println("v e "+MoreArray.toString(last.exps,","));
            for (int j = 0; j < two_list.size(); j++) {
                twolabels[j] = "" + (j + 1);
                //System.out.print(".");
                ValueBlock vtwo = (ValueBlock) two_list.get(j);
                ValueBlock vtwo_nr = BlockMethods.makeNRGeneExps(vtwo);

                if (vtwo.genes.length != vtwo_nr.genes.length) {
                    System.out.println("gene dups " + j + "\t" + vtwo.genes.length + "\t" + vtwo_nr.genes.length);
                    for (int w = 0; w < vtwo.genes.length; w++) {
                        for (int z = w + 1; z < vtwo.genes.length; z++) {
                            if (vtwo.genes[w] == vtwo.genes[z]) {
                                System.out.println("duplicate gene " + vtwo.genes[w] + "\t" + w + "\t" + z);
                            }
                        }
                    }
                }
                if (vtwo.exps.length != vtwo_nr.exps.length) {
                    System.out.println("exps dups " + j + "\t" + vtwo.exps.length + "\t" + vtwo_nr.exps.length);
                    for (int w = 0; w < vtwo.exps.length; w++) {
                        for (int z = w + 1; z < vtwo.exps.length; z++) {
                            if (vtwo.exps[w] == vtwo.exps[z]) {
                                System.out.println("duplicate exp " + vtwo.exps[w] + "\t" + w + "\t" + z);
                            }
                        }
                    }
                }
                /*if (twoHash[j] == null) {
                    twoHash[j] = new HashMap();
                    twoHash[j] = util.LoadHash.addPairs(mnk.genes, mnk.exps, twoHash[j]);
                }*/



                /*ValueBlock merged = new ValueBlock(vone);
                merged.addGenes(vtwo.genes);
                merged.addExps(vtwo.exps);
                */
                ArrayList counts = getCounts(vone, vtwo);

                double value = compareValue(vone, vtwo, "12" + "_" + i + "_" + j, counts);
                dists[i][j] = value;

                //double value = compareValue(last, mnk, selfHash[i], twoHash[j]);
                System.out.println("self vs two " + i + "\t" + j + "\t" + value);
                if (value > thresholdcut) {
                    String s = "c" + j + "\t" + "r" + i + "\t" + "rc" + "\t" + value;

                    String s2 = j + "\t" + i + "\t" + vtwo.genes.length + "\t" + vone.genes.length + "\t" + value;

                    output.add(s);
                    output2.add(s2);
                    labels.add("" + j + "_" + i);
                    vbl_set_single[i]++;
                    two_single[j]++;
                    //System.out.println("compare " + s);
                }
            }
        }
        System.out.println("\nself vs two " + output.size());

        if (seldist.equals("geneenrich")) {
            doPvals(pvals_final, counts_final, bindtotal_pvals_index, map_size_final, map_size_total);
        }


        for (int i = 0; i < pvals_final_labels.size(); i++) {
            String s = pvals_final_labels.get(i);
            if (s.startsWith("12")) {
                String[] split = s.split("_");
                //System.out.println(s+"\t"+split[2]+"\t"+split[1]);
                int i1 = Integer.parseInt(split[1]);
                int i2 = Integer.parseInt(split[2]);
                pvaldists[i1][i2] = (Double) adjust_pvals.get(i);// pvals_final.get(i);
                ValueBlock a = (ValueBlock) vbl_set.get(i1);
                ValueBlock b = (ValueBlock) two_list.get(i2);
                output2pvals.add("" + i2 + "\t" + i1 + "\t" + b.genes.length + "\t" + a.genes.length + "\t" + pvaldists[i1][i2]);
            }
        }

        String ff = outpath + "_" + seldist + "_pval.txt";
        System.out.println("writing " + ff);
        TabFile.write(pvaldists, ff, twolabels, onelabels);


        String f = outpath + "_" + seldist + ".txt";
        System.out.println("writing " + f);
        TabFile.write(dists, f, twolabels, onelabels);

        String outpath11 = outpath + "_" + thresholdcut + "_" + seldist + "_pvals_stats.txt";
        System.out.println("writing " + outpath11);
        TextFile.write(output2pvals, outpath11);


        int selfself = 0;
        //self-self
        for (int i = 0; i < vbl_set.size(); i++) {
            System.out.print("r");
            ValueBlock last = (ValueBlock) vbl_set.get(i);
            for (int j = i + 1; j < vbl_set.size(); j++) {
                //System.out.print(".");
                ValueBlock last2 = (ValueBlock) vbl_set.get(j);
                ArrayList counts = getCounts(last, last2);
                double value = compareValue(last, last2, "ss1" + "_" + i + "_" + j, counts);//, selfHash[i], selfHash[j]);
                //System.out.println("self vs self " + i + "\t" + j + "\t" + value);
                if (value > thresholdcut) {
                    String s = "r" + j + "\t" + "r" + i + "\t" + 1 + "\t" + value;
                    output.add(s);
                    vbl_set_single[i]++;
                    vbl_set_single[j]++;
                    selfself++;
                    //System.out.println("results " + s);
                }
            }
        }
        System.out.println("\nself vs self " + output.size() + "\t" + selfself);

        int selfselftwo = 0;
        //self-self two
        for (int i = 0; i < two_list.size(); i++) {
            System.out.print("c");
            ValueBlock mnk1 = (ValueBlock) two_list.get(i);
            for (int j = i + 1; j < two_list.size(); j++) {
                //System.out.print(".");
                ValueBlock mnk2 = (ValueBlock) two_list.get(j);
                ArrayList counts = getCounts(mnk1, mnk2);
                double value = compareValue(mnk1, mnk2, "ss2" + "_" + i + "_" + j, counts);//, twoHash[i], twoHash[j]);
                //System.out.println("two vs two " + i + "\t" + j + "\t" + value);
                if (value > thresholdcut) {
                    String s = "o" + j + "\t" + "c" + i + "\t" + "cc" + "\t" + value;
                    output.add(s);
                    two_single[i]++;
                    two_single[j]++;
                    selfselftwo++;
                    //System.out.println("two " + s);
                }
            }
        }
        System.out.println("\ntwo vs two " + output.size() + "\t" + selfselftwo);

        int vblsinglecount = 0;
        for (int i = 0; i < vbl_set.size(); i++) {
            if (vbl_set_single[i] == 0) {
                String s = "r" + i + "\t" + "r" + i + "\t" + "rr" + "\t" + 1;
                output.add(s);
                vblsinglecount++;
            }
        }
        System.out.println("vbl single " + output.size() + "\t" + vblsinglecount);

        int twosinglecount = 0;
        for (int i = 0; i < two_list.size(); i++) {
            if (two_single[i] == 0) {
                String s = "c" + i + "\t" + "c" + i + "\t" + "cc" + "\t" + 1;
                output.add(s);
                twosinglecount++;
            }
        }
        System.out.println("two single " + output.size() + "\t" + twosinglecount);

        String outpath = oneresultspath + "_" + thresholdcut + "_" + seldist + ".txt";
        System.out.println("writing " + outpath);
        TextFile.write(output, outpath);

        String outpath2 = oneresultspath + "_" + thresholdcut + "_" + seldist + "_stats.txt";
        System.out.println("writing " + outpath2);
        TextFile.write(output2, outpath2);

    }

    /**
     * @param vone
     * @param vtwo
     * @return
     */
    private ArrayList getCounts(ValueBlock vone, ValueBlock vtwo) {

        int common = 0;
        HashMap one = new HashMap();
        for (int a = 0; a < vone.genes.length; a++) {
            one.put(vone.genes[a], 1);
        }
        HashMap two = new HashMap();
        for (int a = 0; a < vtwo.genes.length; a++) {
            two.put(vtwo.genes[a], 1);
            Object o = one.get(vtwo.genes[a]);
            if (o != null)
                common++;
        }

        //in block w/ TF
        int a = common;
        //in block not TF
        int b = vone.genes.length - common;
        //out block w/ TF
        int c = vtwo.genes.length - common;
        //out block not TF
        int d = num_genes - (a + b) - c;
        ArrayList counts = new ArrayList();
        counts.add(a);
        counts.add(b);
        counts.add(c);
        counts.add(d);
        return counts;
    }

    /**
     * @param one
     * @param two
     * @return
     */
    private double compareValue(ValueBlock one, ValueBlock two, String label, ArrayList counts) {
        double value = Double.NaN;
        if (!seldist.equals("geneenrich")) {
            int arrayInd = MoreArray.getArrayInd(dist_types, seldist);
            //System.out.println("compareValue " + seldist + "\t" + arrayInd);
            if (arrayInd == 0) {
                //value = BlockMethods.computeBlockOverlapGeneRootProduct(two, one);
                //value = BlockMethods.computeBlockF1(two, one);
                //value = BlockMethods.computefrxnGeneAndExpWithRefPairMin(two, one, false);
                value = BlockMethods.computeBlockOverlapGeneExpRootProduct(two, one);
                //value = BlockMethods.computeBlockOverlapGeneExpSum(two, one);
                //value = compareValueHash(oh, th, one.genes.length, two.genes.length, one.exps.length, two.exps.length);
            } else if (arrayInd == 1) {
            /*double p = BlockMethods.computeBlockPrecisionGenes(two, one);
            double r = BlockMethods.computeBlockRecallGenes(two, one);
            value = (2 * p * r) / (p + r);*/
                //value = BlockMethods.computeBlockOverlapGeneMin(two, one, false);
                //value = BlockMethods.computeBlockOverlapGeneRootProduct(two, one);
                value = BlockMethods.computeBlockOverlapGeneSum(two, one);

            } else if (arrayInd == 2) {
            /*double p = BlockMethods.computeBlockPrecisionExps(two, one);
            double r = BlockMethods.computeBlockRecallExps(two, one);
            value = (2 * p * r) / (p + r);*/
                //value = BlockMethods.computeBlockOverlapExpMin(two, one, false);
                //value = BlockMethods.computeBlockOverlapExpRootProduct(two, one);
                value = BlockMethods.computeBlockOverlapExpSum(two, one);
            }
            return value;
        } else {
            int a = (Integer) counts.get(0);
            int b = (Integer) counts.get(1);
            int c = (Integer) counts.get(2);
            int d = (Integer) counts.get(3);
            String rcall1 = "data <- matrix(c(" + a + "," + b + "," + c + "," + d + "), " +
                    "nr=2, dimnames=list(c(\"in\",\"out\"), c(\"TF\",\"noTF\")))";
            irv.Rexpr = irv.Rengine.eval(rcall1);

            double pval = Double.NaN;
            try {
                String rcall2 = "fisher.test(data, alternative=\"greater\")$p.value";
                irv.Rexpr = irv.Rengine.eval(rcall2);
                pval = irv.Rexpr.asDouble();

                pvals_final.add(pval);
                pvals_final_labels.add(label);
            } catch (Exception e) {
                System.out.println("TF test data " + a + "\t" + b + "\t" + c + "\t" + d);
                e.printStackTrace();
            }

            return pval;
        }

    }

    /**
     * @param pvals_final
     * @param counts_final
     * @param bindtotal_pvals_index
     */
    private void doPvals(ArrayList pvals_final, ArrayList counts_final, HashMap bindtotal_pvals_index, ArrayList map_size_final, ArrayList map_size_total) {
        irv.Rengine.assign("data", MoreArray.ArrayListtoDouble(pvals_final));
        //irv.Rengine.eval("data <- c(" + MoreArray.toString(TFlabelpval, ",") + ")");
        String rcall2 = "p.adjust(data,\"fdr\")";
        irv.Rexpr = irv.Rengine.eval(rcall2);
        adjust_pvals = MoreArray.convtoArrayList(irv.Rexpr.asDoubleArray());

         /*
        ArrayList outar_bindpval = new ArrayList();
        int last = -1;
        HashMap countTFs = new HashMap();

       for (int a = 0; a < vbl.size(); a++) {
            for (int m = 0; m < allpvals.length; m++) {
                if (allpvals[m] < TF_pvalue_cutoff) {
                    String get = (String) bindtotal_pvals_index.get(m);
                    if (debug) System.out.println("output pvals " + m + "\t" + get);
                    String[] split = get.split("_");

                    if (debug) System.out.println(get);
                    if (debug) MoreArray.printArray(split);
                    int vbindex = Integer.parseInt(split[0]) + 1;
                    if (vbindex == a + 1) {
                        if (countTFs.get(split[1]) == null)
                            countTFs.put(split[1], 1);
                        String s = vbindex + "\t" + split[1] + "\t" + allpvals[m] + "\t" + counts_final.get(m) + "\t" +
                                (Integer) map_size_total.get(m);
                        if (last == -1) {
                            last = vbindex;
                            if (outar_bindpval.indexOf(s) == -1)
                                outar_bindpval.add(s);
                        } else if (last == vbindex) {
                            if (outar_bindpval.indexOf(s) == -1) {
                                outar_bindpval.add(s);
                            }
                        } else if (last != vbindex) {
                            String outpath0 = outdir + "/" + prefix + "_" + (last - 1) + "_TFbind_pval0.001.txt";
                            System.out.println("writing " + outpath0);
                            TextFile.write(outar_bindpval, outpath0);
                            outar_bindpval = new ArrayList();
                            outar_bindpval.add(s);
                            last = vbindex;

                        }
                    }
                }
            }
        }
         System.out.println("countTFs " + countTFs.size());
*/

    }


    /**
     * @param args
     */
    private void init(String[] args) {
        MoreArray.printArray(args);

        options = MapArgOptions.maptoMap(args, valid_args);

        if (options.get("-one") != null) {
            oneresultspath = (String) options.get("-one");//args[0];
        }
        if (options.get("-two") != null) {
            tworesultspath = (String) options.get("-two");//args[1];
        }
        if (options.get("-dist") != null) {
            seldist = (String) options.get("-dist");//args[1];
            if (seldist.equals("geneenrich")) {
                irv = new InitRandVar(seed, false);
            }
        }
        if (options.get("-out") != null) {
            outpath = (String) options.get("-out");//args[1];
        }

        if (options.get("-genes") != null) {
            num_genes = Integer.parseInt((String) options.get("-genes"));
        }

        if (options.get("-exps") != null) {
            num_exps = Integer.parseInt((String) options.get("-exps"));
        }
        System.out.println("genes " + num_genes + "\texps " + num_exps);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 8 || args.length == 12) {
            AllvsAll rm = new AllvsAll(args);
        } else {
            System.out.println("syntax: java DataMining.eval.AllvsAll\n" +
                    "<-one dir of results or .bic file>\n" +
                    "<-two .bic format file>\n" +
                    "<-out out file>\n" +
                    "<-dist type of distance (default 'total')>\n" +
                    "<-genes number of genes>\n" +
                    "<-exps number of exps>"
            );
        }
    }
}
