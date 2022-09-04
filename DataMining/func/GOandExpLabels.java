package DataMining.func;

import DataMining.*;
import mathy.Matrix;
import mathy.SimpleMatrix;
import mathy.stat;
import util.MoreArray;
import util.StringUtil;
import util.TabFile;
import util.TextFile;

import java.util.*;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: 9/15/13
 * Time: 8:53 PM
 */
public class GOandExpLabels {

    boolean debug = false;
    String prefix;

    String[] exp_labels;
    String[] gene_labels;
    ValueBlockList vbl;
    SimpleMatrix expr;
    SimpleMatrix motif_data;
    String[][] tabfile;

    double TF_COEXPR_CUTOFF = 0.8;
    double RANK_SCORE_THRESHOLD = 0.99;
    double TF_pvalue_cutoff = 0.001;


    HashMap GOmap;
    HashMap yeast_gene_names;
    HashMap sigGOmap;
    HashMap countGOmap;
    HashMap totalExpCount;
    HashMap motif_data_labels_hash;
    HashMap expr_data_labels_hash = new HashMap();

    HashMap<Integer, String> modMap;
    ArrayList<HashMap> arHashExp;
    ArrayList<HashMap> arHashGene;

    ValueBlockList single = new ValueBlockList();

    int[] totalexps;

    ArrayList<String> modData;

    InitRandVar irv;
    long seed = MINER_STATIC.RANDOM_SEEDS[0];

    String outdir;


    /**
     * @param args
     */
    public GOandExpLabels(String[] args) {

        try {
            init(args);
        } catch (Exception e) {
            e.printStackTrace();
        }

        irv = new InitRandVar(seed, false);

        ArrayList pvals_final = new ArrayList();
        ArrayList counts_final = new ArrayList();
        ArrayList map_size_final = new ArrayList();
        ArrayList map_size_total = new ArrayList();
        HashMap bindtotal_pvals_index = new HashMap();

        if (modData != null)
            buildBiclustertoModuleMap();
        //System.out.println("modMap " + modMap.size());
        buildExpWeight();

        for (int i = 0; i < vbl.size(); i++) {

            System.out.println("doing " + i);
            doSingle(i, "_exps.txt", true);
            ValueBlock vb = (ValueBlock) vbl.get(i);

            vb.setDataAndMean(expr.data);
            double[] mean = stat.norm(Matrix.columnSum(vb.exp_data), vb.genes.length);

            assignGOfreq(vb, outdir + "/" + prefix + "_" + i + "_GO.txt");

            String[] outar_bind = new String[vb.genes.length];
            String[] outar_coexpr = new String[vb.genes.length];
            String[] outar_anticoexpr = new String[vb.genes.length];
            String[] outar_bindcoexpr = new String[vb.genes.length];
            String[] outar_bindanticoexpr = new String[vb.genes.length];

            String[] outar_bind_motif = new String[vb.genes.length];
            String[] outar_coexpr_motif = new String[vb.genes.length];
            String[] outar_anticoexpr_motif = new String[vb.genes.length];
            String[] outar_bindcoexpr_motif = new String[vb.genes.length];
            String[] outar_bindanticoexpr_motif = new String[vb.genes.length];

            ArrayList samps = new ArrayList();
            int[] index_map_motifdata = new int[vb.genes.length];
            int counter = 0;
            for (int j = 0; j < vb.genes.length; j++) {
                String g = expr.ylabels[vb.genes[j] - 1];
                //System.out.println("looking for " + g);
                if (motif_data_labels_hash != null) {
                    Object o = motif_data_labels_hash.get(g);
                    if (o != null) {
                        double[] cursamp = motif_data.data[(Integer) o];
                        //System.out.println("\ncursamp ");
                        //System.out.println(MoreArray.toString(cursamp, ","));

                        samps.add(cursamp);
                        index_map_motifdata[j] = counter;
                        counter++;
                        //System.out.print("-");
                    }
                } else {
                    index_map_motifdata[j] = -1;
                    //System.out.print("notfound " + g);
                }
            }

            if (motif_data != null) {
                //System.out.println("\ngenes " + vb.genes.length + "\t" + samps.size());
                double[][] sampsmat = MoreArray.convtodouble2DArray(samps, motif_data.data[0].length);
                //System.out.println("\n2D");

           /* try {
                System.out.println("sampsmat " + sampsmat.length + "\t" + sampsmat[0].length);
            } catch (Exception e) {
                e.printStackTrace();
            }*/


                if (sampsmat.length > 0) {
                    //System.out.println("sampsmat " + sampsmat.length + "\t" + sampsmat[0].length);

                    HashMap bindtotal = new HashMap();
                    HashMap bindtotal_motifs = new HashMap();
                    HashMap bindtotal_motifs_labels = new HashMap();
                    HashMap bindtotal_pvals = new HashMap();
                    ArrayList counthit = new ArrayList();
                    //genes in bicluster mapping to TF binding site data
                    for (int p = 0; p < sampsmat.length; p++) {
                        //System.out.print(".");

                        String cur_bind = "";
                        String cur_coexpr = "";
                        String cur_anticoexpr = "";
                        String cur_bindcoexpr = "";
                        String cur_bindanticoexpr = "";

                        //for all TF binding sites
                        for (int z = 0; z < sampsmat[0].length; z++) {
                            String motifname = motif_data.xlabels[z];
                            String TFname = motif_data.xlabels[z].substring(0, motif_data.xlabels[z].indexOf("_"));
                            String trueTFname = (String) yeast_gene_names.get(TFname);
                            ArrayList TFdataar = new ArrayList();
                            Object o = expr_data_labels_hash.get(TFname);
                            //if TF is present in expression data
                            if (o != null) {
                                for (int t = 0; t < vb.exps.length; t++) {
                                    TFdataar.add(expr.data[(Integer) o][vb.exps[t] - 1]);
                                }
                                double[] TFdata = MoreArray.ArrayListtoDouble(TFdataar);
                                double TFcors = stat.correlationCoeff(mean, TFdata);
                                if (TFcors > TF_COEXPR_CUTOFF) {
                                    if (cur_coexpr.indexOf(trueTFname) == -1)
                                        cur_coexpr += trueTFname + "_";
                                }

                                if (TFcors < -TF_COEXPR_CUTOFF) {
                                    if (cur_anticoexpr.indexOf(trueTFname) == -1)
                                        cur_anticoexpr += trueTFname + "_";
                                }
                                if (sampsmat[p][z] > RANK_SCORE_THRESHOLD) {
                                    if (counthit.indexOf(p) == -1)
                                        counthit.add(p);
                                    if (cur_bind.indexOf(trueTFname) == -1)
                                        cur_bind += trueTFname + "_";

                                    //count per TF
                                    Object testo = bindtotal.get(trueTFname);
                                    if (testo == null) {
                                        bindtotal.put(trueTFname, 1);
                                        if (debug)
                                            System.out.println("bindtotal " + trueTFname + "\t" + bindtotal.get(trueTFname));
                                    } else {
                                        int cur = (Integer) testo;
                                        bindtotal.put(trueTFname, cur + 1);
                                        if (debug)
                                            System.out.println("bindtotal " + trueTFname + "\t" + bindtotal.get(trueTFname));
                                    }

                                    //count per motif
                                    Object test3 = bindtotal_motifs.get(motifname);
                                    if (test3 == null) {
                                        bindtotal_motifs.put(motifname, 1);
                                        if (debug)
                                            System.out.println("bindtotal_motifs " + motifname + "\t" + bindtotal_motifs.get(motifname));
                                    } else {
                                        int cur = (Integer) test3;
                                        bindtotal_motifs.put(motifname, cur + 1);
                                        if (debug)
                                            System.out.println("bindtotal_motifs " + motifname + "\t" + bindtotal_motifs.get(motifname));
                                    }

                                    if (debug)
                                        System.out.println("bindtotal_motifs " + motifname + "\t" + bindtotal_motifs.get(motifname));

                                    //motif labels
                                    Object testo2 = bindtotal_motifs_labels.get(trueTFname);
                                    if (testo2 == null) {
                                        bindtotal_motifs_labels.put(trueTFname, motifname);
                                        if (debug)
                                            System.out.println("bindtotal_motifs_labels " + trueTFname + "\t" + bindtotal_motifs_labels.get(trueTFname));
                                    } else {
                                        String cur = (String) testo2;
                                        bindtotal_motifs_labels.put(trueTFname, cur + "-" + motifname);
                                        if (debug)
                                            System.out.println("bindtotal_motifs_labels " + trueTFname + "\t" + bindtotal_motifs_labels.get(trueTFname));
                                    }
                                }

                                if (sampsmat[p][z] > RANK_SCORE_THRESHOLD && TFcors > TF_COEXPR_CUTOFF) {
                                    if (cur_bindcoexpr.indexOf(trueTFname) == -1)
                                        cur_bindcoexpr += trueTFname + "_";
                                }
                                if (sampsmat[p][z] > RANK_SCORE_THRESHOLD && TFcors < -TF_COEXPR_CUTOFF) {
                                    if (cur_bindanticoexpr.indexOf(trueTFname) == -1)
                                        cur_bindanticoexpr += trueTFname + "_";
                                }
                            }
                        }

                        int arind = -1;
                        try {
                            arind = MoreArray.getArrayInd(index_map_motifdata, p);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        outar_bind[arind] = cur_bind;
                        outar_coexpr[arind] = cur_coexpr;
                        outar_anticoexpr[arind] = cur_coexpr;
                        outar_bindcoexpr[arind] = cur_bindcoexpr;
                    }


                    Set s = bindtotal.entrySet();
                    Iterator it = s.iterator();
                    //for all TFs with a motif in this bicluster
                    while (it.hasNext()) {
                        Map.Entry cur = (Map.Entry) it.next();
                        String TF = (String) cur.getKey();
                        String motifs = (String) bindtotal_motifs_labels.get(TF);
                        //System.out.println("TF " + TF + "\t" + cur.getValue());
                        //System.out.println("motifs " + motifs);
                        String[] motifs_ar = motifs.split("-");

                        ArrayList pvals_raw = new ArrayList();
                        ArrayList counts = new ArrayList();
                        //for all motifs belonging to TF
                        for (int k = 0; k < motifs_ar.length; k++) {
                            int matchindex = StringUtil.getFirstEqualsIndex(motif_data.xlabels, motifs_ar[k]);

                            if (debug)
                                System.out.println("TF " + TF + "\t" + motifs_ar[k] + "\t" + motif_data.xlabels[0]);
                            if (debug)
                                System.out.println("TF " + TF + "\t" + k + "\t" + matchindex + "\t" + motif_data.xlabels[matchindex] + "\t" + motifs_ar[k]);
                            int labelindex = StringUtil.getFirstEqualsIndex(motif_data.xlabels, motif_data.xlabels[matchindex]);
                            int bicluster_targets = (Integer) bindtotal_motifs.get(motifs_ar[k]);//cur.getValue();
                            //in block w/ TF
                            int a = bicluster_targets;
                            //in block not TF
                            int b = samps.size() - bicluster_targets;
                            //out block w/ TF
                            int countgreat = stat.countGreaterThan(Matrix.extractColumn(motif_data.data, labelindex + 1), RANK_SCORE_THRESHOLD);
                            int c = (int) Math.max(0, countgreat - a);
                            //out block not TF
                            int d = motif_data.data.length - (a + b) - c;

                            String rcall1 = "data <- matrix(c(" + a + "," + b + "," + c + "," + d + "), " +
                                    "nr=2, dimnames=list(c(\"in\",\"out\"), c(\"TF\",\"noTF\")))";
                            irv.Rexpr = irv.Rengine.eval(rcall1);

                            double pval = 0;
                            try {
                                String rcall2 = "fisher.test(data, alternative=\"greater\")$p.value";
                                irv.Rexpr = irv.Rengine.eval(rcall2);
                                pval = irv.Rexpr.asDouble();
                            } catch (Exception e) {
                                System.out.println("TF test data " + a + "\t" + b + "\t" + c + "\t" + d);
                                System.out.println(k + "\t" + "TF " + TF + "\t" + motifs_ar[k] + "\tcountgreat " + countgreat);
                                e.printStackTrace();
                            }

                            if (debug) System.out.println("TF test data " + a + "\t" + b + "\t" + c + "\t" + d + "\t" +
                                    pval + "\t" + TF + "\t" + motifs_ar[k]);

                           /* if (pval < TF_pvalue_cutoff) {
                                System.out.println("TF " + TF);
                                System.out.println("genes in block " + (a + b));
                                System.out.println("genes w/ TF " + (a + c));
                                System.out.println("TF test data " + a + "\t" + b + "\t" + c + "\t" + d);
                                System.out.println("TF pval " + pval);
                            }*/
                            pvals_raw.add(pval);
                            counts.add(a);
                        }

                        if (TF.length() > 0) {
                            //stores mean p-value for all motifs belonging to that TF
                            double[] data = MoreArray.ArrayListtoDouble(pvals_raw);
                            //double meanpval = stat.avg(data);
                            double minpval = stat.findMin(data);

                            //int countmin = stat.countGreaterThan(data, this.TF_pvalue_cutoff);
                        /*int counttotal = 0;
                        for (int z = 0; z < data.length; z++) {
                            if (data[z] < this.TF_pvalue_cutoff) {
                                counttotal += (Integer) counts.get(z);
                            }
                        }*/

                            String bicluster_TF = "" + i + "_" + TF;
                            if (debug) System.out.println("bicluster_TF " + bicluster_TF);
                            bindtotal_pvals.put(bicluster_TF, minpval);
                            pvals_final.add(minpval);
                            map_size_final.add(sampsmat.length);
                            map_size_total.add(counthit.size());
                            int minindex = MoreArray.getArrayInd(data, minpval);
                            counts_final.add(counts.get(minindex));
                            Integer integer = new Integer(pvals_final.size() - 1);
                            if (debug)
                                System.out.println("minpval " + integer + "\t" + data.length + "\t" + bicluster_TF + "\t" + minpval);
                            if (debug) MoreArray.printArray(data);
                            bindtotal_pvals_index.put(integer, bicluster_TF);
                        }
                    }
                    //}


                    //for all genes in bicluster
                    for (int p = 0; p < sampsmat.length; p++) {
                        String cur_bind = "";
                        String cur_coexpr = "";
                        String cur_anticoexpr = "";
                        String cur_bindcoexpr = "";
                        String cur_bindanticoexpr = "";

                        //for all motifs
                        for (int z = 0; z < sampsmat[0].length; z++) {
                            String TFname = motif_data.xlabels[z].substring(0, motif_data.xlabels[z].indexOf("_"));
                            //String trueTFname = (String) yeast_gene_names.get(TFname);
                            ArrayList TFdataar = new ArrayList();
                            Object o = expr_data_labels_hash.get(TFname);
                            if (o != null) {
                                for (int t = 0; t < vb.exps.length; t++) {
                                    TFdataar.add(expr.data[(Integer) o][vb.exps[t] - 1]);
                                }
                                double[] TFdata = MoreArray.ArrayListtoDouble(TFdataar);
                                double TFcors = stat.correlationCoeff(mean, TFdata);
                                if (TFcors > TF_COEXPR_CUTOFF) {
                                    if (cur_coexpr.indexOf(motif_data.xlabels[z]) == -1)
                                        cur_coexpr += motif_data.xlabels[z] + "_";
                                }

                                if (TFcors < -TF_COEXPR_CUTOFF) {
                                    if (cur_anticoexpr.indexOf(motif_data.xlabels[z]) == -1)
                                        cur_anticoexpr += motif_data.xlabels[z] + "_";
                                }
                                if (sampsmat[p][z] > RANK_SCORE_THRESHOLD) {
                                    if (cur_bind.indexOf(motif_data.xlabels[z]) == -1) {
                                        cur_bind += motif_data.xlabels[z] + "_";
                                        if (debug)
                                            System.out.println("cur_bind " + cur_bind);
                                    }
                                }

                                if (sampsmat[p][z] > RANK_SCORE_THRESHOLD && TFcors > TF_COEXPR_CUTOFF) {
                                    if (cur_bindcoexpr.indexOf(motif_data.xlabels[z]) == -1)
                                        cur_bindcoexpr += motif_data.xlabels[z] + "_";
                                    System.out.println("TF coexpressed " + i + "\t" + motif_data.xlabels[z] + "\t" + TFcors);

                                }
                                if (sampsmat[p][z] > RANK_SCORE_THRESHOLD && TFcors < -TF_COEXPR_CUTOFF) {
                                    if (cur_bindanticoexpr.indexOf(motif_data.xlabels[z]) == -1)
                                        cur_bindanticoexpr += motif_data.xlabels[z] + "_";
                                }
                            }
                        }

                        int arind = -1;
                        try {
                            arind = MoreArray.getArrayInd(index_map_motifdata, p);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        outar_bind_motif[arind] = cur_bind;
                        outar_coexpr_motif[arind] = cur_coexpr;
                        outar_anticoexpr_motif[arind] = cur_coexpr;
                        outar_bindcoexpr_motif[arind] = cur_bindcoexpr;
                    }
                    output(i, outar_bind, outar_coexpr, outar_anticoexpr, outar_bindcoexpr, outar_bindanticoexpr,
                            outar_bind_motif, outar_coexpr_motif, outar_anticoexpr_motif, outar_bindcoexpr_motif, outar_bindanticoexpr_motif);
                }
            }

        }

        doPvals(pvals_final, counts_final, bindtotal_pvals_index, map_size_final, map_size_total);


        System.exit(0);
    }

    /**
     * @param pvals_final
     * @param counts_final
     * @param bindtotal_pvals_index
     */
    private void doPvals(ArrayList pvals_final, ArrayList counts_final, HashMap bindtotal_pvals_index, ArrayList map_size_final, ArrayList map_size_total) {
        boolean retjri = irv.Rengine.assign("data", MoreArray.ArrayListtoDouble(pvals_final));
        //irv.Rengine.eval("data <- c(" + MoreArray.toString(TFlabelpval, ",") + ")");
        String rcall2 = "p.adjust(data,\"fdr\")";
        irv.Rexpr = irv.Rengine.eval(rcall2);
        double[] allpvals = irv.Rexpr.asDoubleArray();

        ArrayList outar_bindpval = new ArrayList();
        int last = -1;
        HashMap countTFs = new HashMap();

        for (int a = 0; a < vbl.size(); a++) {

            int notfound = 0;
            /*TODO processes data for all biclusters each time ??? */
            for (int m = 0; m < allpvals.length; m++) {
                if (allpvals[m] < TF_pvalue_cutoff) {
                    String get = (String) bindtotal_pvals_index.get(m);
                    if (debug)
                        System.out.println("output pvals " + m + "\t" + get);
                    String[] split = get.split("_");

                    if (debug) System.out.println(get);
                    if (debug) MoreArray.printArray(split);
                    int vbindex = Integer.parseInt(split[0]) + 1;
                    System.out.println("doPvals last " + last + "\tvbindex " + vbindex + "\ta " + a + "\t" + m);
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
                } else {
                    notfound++;
                    //System.out.println("doPvals rejected: a " + a + "\tallpvals[m] " + allpvals[m] + "\tTF_pvalue_cutoff " + TF_pvalue_cutoff);
                }
            }

            System.out.println("doPvals a " + a + "\tnotfound " + notfound);
        }

        System.out.println("countTFs " + countTFs.size());
    }

    /**
     * @param i
     * @param outar_bind
     * @param outar_coexpr
     * @param outar_anticoexpr
     * @param outar_bindcoexpr
     * @param outar_bindanticoexpr
     * @param outar_bind_motif
     * @param outar_coexpr_motif
     * @param outar_anticoexpr_motif
     * @param outar_bindcoexpr_motif
     * @param outar_bindanticoexpr_motif
     */

    private void output(int i, String[] outar_bind, String[] outar_coexpr, String[] outar_anticoexpr, String[] outar_bindcoexpr, String[] outar_bindanticoexpr, String[] outar_bind_motif, String[] outar_coexpr_motif, String[] outar_anticoexpr_motif, String[] outar_bindcoexpr_motif, String[] outar_bindanticoexpr_motif) {

        String outdir1 = outdir + "/" + prefix + "_" + i + "_TFbind.txt";
        System.out.println("writing " + outdir1);
        TextFile.write(outar_bind, outdir1);

        String outdir2 = outdir + "/" + prefix + "_" + i + "_TFcoexpr.txt";
        System.out.println("writing " + outdir2);
        TextFile.write(outar_coexpr, outdir2);

        String outdir3 = outdir + "/" + prefix + "_" + i + "_TFanticoexpr.txt";
        System.out.println("writing " + outdir3);
        TextFile.write(outar_anticoexpr, outdir3);

        String outdir4 = outdir + "/" + prefix + "_" + i + "_TFbindcoexpr.txt";
        System.out.println("writing " + outdir4);
        TextFile.write(outar_bindcoexpr, outdir4);

        String outdir5 = outdir + "/" + prefix + "_" + i + "_TFbindanticoexpr.txt";
        System.out.println("writing " + outdir5);
        TextFile.write(outar_bindanticoexpr, outdir5);


        String outdir1a = outdir + "/" + prefix + "_" + i + "_TFbind_motif.txt";
        System.out.println("writing " + outdir1a);
        TextFile.write(outar_bind_motif, outdir1a);

        String outdir2a = outdir + "/" + prefix + "_" + i + "_TFcoexpr_motif.txt";
        System.out.println("writing " + outdir2a);
        TextFile.write(outar_coexpr_motif, outdir2a);

        String outdir3a = outdir + "/" + prefix + "_" + i + "_TFanticoexpr_motif.txt";
        System.out.println("writing " + outdir3a);
        TextFile.write(outar_anticoexpr_motif, outdir3a);

        String outdir4a = outdir + "/" + prefix + "_" + i + "_TFbindcoexpr_motif.txt";
        System.out.println("writing " + outdir4a);
        TextFile.write(outar_bindcoexpr_motif, outdir4a);

        String outdir5a = outdir + "/" + prefix + "_" + i + "_TFbindanticoexpr_motif.txt";
        System.out.println("writing " + outdir5a);
        TextFile.write(outar_bindanticoexpr_motif, outdir5a);
    }

    /**
     * @return
     */
    private int[] buildExpWeight() {
        Set values = modMap.keySet();
        int size = values.size();
        System.out.println("size " + size);
        arHashExp = new ArrayList<HashMap>(size);
        int[] keysStr = MoreArray.intSettoint(values);

        double[] expr_mean = new double[keysStr.length];
        double[] expr_sd = new double[keysStr.length];

        totalexps = new int[keysStr.length];
        ArrayList sizes = new ArrayList();
        for (int i = 0; i < keysStr.length; i++) {
            String entry = modMap.get(keysStr[i]);
            //System.out.println("buildExpWeight " + keysStr[i] + "\tsize " + (StringUtil.countOccur(entry, '_') + 1) + "\t" + entry);
            HashMap<Integer, Integer> curHash = new HashMap();

            String[] split = entry.split("_");
            ArrayList data = new ArrayList();
            ArrayList datasd = new ArrayList();
            //System.out.println("split.length " + split.length + "\t" + entry);
            sizes.add(new Integer(split.length));
            for (int z = 0; z < split.length; z++) {
                int cur = Integer.parseInt(split[z]);
                ValueBlock v = null;
                try {
                    v = (ValueBlock) vbl.get(cur - 1);
                    v.setDataAndMean(expr.data);
                    //System.out.println("Bicluster data " + v.exp_data[0][0]);
                    double mean = Matrix.avg(v.exp_data);
                    //System.out.println("Bicluster mean " + mean);
                    data.add(mean);
                    double sd = Matrix.SD(v.exp_data, mean);
                    //System.out.println("Bicluster SD " + sd);
                    datasd.add(sd);

                    totalexps[i] += v.exps.length;

                    for (int j = 0; j < v.exps.length; j++) {
                        Object o = curHash.get(v.exps[j]);
                        if (o == null) {
                            curHash.put(v.exps[j], 1);
                        } else {
                            int val = (Integer) o;
                            curHash.put(v.exps[j], val + 1);
                        }
                    }
                } catch (Exception e) {
                    //System.out.println("buildExpWeight no biclusters for module " + cur);
                    e.printStackTrace();
                }

            }
            expr_mean[i] = stat.listAvg(data);
            expr_sd[i] = stat.listAvg(datasd);
            arHashExp.add(i, curHash);
        }
        /*System.out.println("Module keys");
        MoreArray.printArray(keysStr);
        System.out.println("Module sizes");
        MoreArray.printArray(MoreArray.ArrayListtoInt(sizes));
        System.out.println("Bicluster mean");
        MoreArray.printArray(expr_mean);
        System.out.println("Bicluster sd");
        MoreArray.printArray(expr_sd);*/

        totalExpCount = new HashMap<String, Integer>();
        for (int i = 0; i < exp_labels.length; i++) {
            ArrayList get = ExpfromNetModules.processString(null, i + 1, exp_labels, gene_labels, true);
            String label1 = (String) get.get(0);
            Object o = totalExpCount.get(label1);
            if (o == null) {
                totalExpCount.put(label1, 1);
            } else {
                int count = (Integer) o;
                totalExpCount.put(label1, count + 1);
            }
        }

        Set set = totalExpCount.keySet();
        //System.out.println("totalExpCount set " + set.size());
        Iterator it = set.iterator();
        ArrayList outExp = new ArrayList();
        while (it.hasNext()) {
            String key = (String) it.next();
            String val = "" + totalExpCount.get(key);
            String key_val = key + "\t" + val;
            //System.out.println("totalExpCount out " + key_val);
            outExp.add(key_val);
        }

        String outpath = outdir + "/" + "exp_count.txt";
        TextFile.write(outExp, outpath);
        System.out.println("wrote " + outpath);

        return keysStr;
    }

    /**
     * @return
     */
    private HashMap<Integer, String> buildBiclustertoModuleMap() {
        HashMap<Integer, Integer> biclusterMap = new HashMap<Integer, Integer>();
        modMap = new HashMap<Integer, String>();
        for (int i = 12; i < modData.size(); i++) {
            String[] cur = modData.get(i).split(",");
            int module = 0;
            try {
                module = Integer.parseInt(cur[1]);
            } catch (NumberFormatException e) {
                //e.printStackTrace();
            }
            biclusterMap.put(Integer.parseInt(cur[0]), module);

            Object o = modMap.get(module);
            if (o == null) {
                modMap.put(module, cur[0]);
            } else {
                String existing = (String) o;
                String newstr = existing + "_" + cur[0];
                modMap.put(module, newstr);
                //System.out.println("newstr " + newstr);
            }

        }
        return modMap;
    }

    /**
     * @param vblindex
     * @param suffix
     * @param isExp
     */
    private void doSingle(int vblindex, String suffix, boolean isExp) {

        ArrayList<String> ilabels = new ArrayList<String>();
        ArrayList<String> ilabelsraw = new ArrayList<String>();

        ValueBlock cur = (ValueBlock) vbl.get(vblindex);

        HashMap total = new HashMap();

        ArrayList expmap = MoreArray.initArrayListList(cur.exps.length, "");//new ArrayList(cur.exps.length);

        if (!isExp) {
            for (int i = 0; i < cur.genes.length; i++) {
                ArrayList get = processString(cur.genes[i], exp_labels, gene_labels, isExp);
                String label1 = (String) get.get(0);
                Object o = total.get(label1);
                if (o == null) {
                    total.put(label1, 1);
                } else {
                    int count = (Integer) o;
                    total.put(label1, count + 1);
                }
            }
        } else {
            for (int i = 0; i < cur.exps.length; i++) {
                ArrayList get = processString(cur.exps[i], exp_labels, gene_labels, isExp);
                String label1 = (String) get.get(0);
                expmap.set(i, label1);
                Object o = total.get(label1);
                if (o == null) {
                    total.put(label1, 1);
                } else {
                    int count = (Integer) o;
                    total.put(label1, count + 1);
                }
            }
        }

        Set keyt = total.keySet();
        Iterator it2 = keyt.iterator();
        while (it2.hasNext()) {
            String label1 = (String) it2.next();
            int weight = (Integer) total.get(label1);
            double w = weight;
            //System.out.println("label1 " + label1);
            if (isExp)
                w = 100.0 * (double) weight / (double) (Integer) totalExpCount.get(label1);//totalexps[i];
            String str = label1 + "\t" + w;
            String str2 = label1 + "\t" + weight;
            ilabels.add(str);
            ilabelsraw.add(str2);
        }

        String outpath = outdir + "/" + prefix + "_" + vblindex + suffix;
        System.out.println("writing " + outpath);
        TextFile.write(ilabels, outpath);

        String outpath2 = outdir + "/" + prefix + "_" + vblindex + "_" + "raw" + suffix;
        System.out.println("writing " + outpath2);
        TextFile.write(ilabelsraw, outpath2);

        String outpath3 = outdir + "/" + prefix + "_" + vblindex + "_" + "raw" + suffix + "_map.txt";
        System.out.println("writing " + outpath3);
        TextFile.write(expmap, outpath3);
    }


    /**
     * @param index
     * @param exp_labels
     * @param gene_labels
     * @param isExp
     * @return
     */

    public final static ArrayList processString(int index,
                                                String[] exp_labels, String[] gene_labels, boolean isExp) {

        String label = null;

        if (isExp)
            label = exp_labels[index - 1];
        else
            label = gene_labels[index - 1];

        int weight = 1;

        String exp_label1 = label;

        if (isExp) {
            //System.out.println("bf " + label);
            String orig = new String(label);
            int dotind = label.indexOf(".");
            //System.out.println(dotind);
            label = label.substring(dotind + 1);
            label = label.toLowerCase();
            int minind = label.indexOf(".min") != -1 ? label.indexOf(".min") : 1000000;
            int minundind = label.indexOf("_min") != -1 ? label.indexOf("_min") : 1000000;
            int minnodotind = label.indexOf("min") != -1 && label.indexOf("lycoming") == -1 &&
                    label.indexOf("amino") == -1 && label.indexOf("minus") == -1 ? label.indexOf("min") : 1000000;
            int hind = label.endsWith(".h") ? label.lastIndexOf(".h") : 1000000;
            int hrind = label.indexOf(".hr") != -1 ? label.indexOf(".hr") : 1000000;
            int hrsind2 = label.indexOf("hrs") != -1 ? label.indexOf("hrs") : 1000000;
            int hrsind = label.indexOf(".hours") != -1 ? label.indexOf(".hours") : 1000000;
            int Timeind = label.indexOf(".time") != -1 ? label.indexOf(".time") : 1000000;
            int sampleind = label.indexOf(".sample") != -1 ? label.indexOf(".sample") : 1000000;
            int compind = label.indexOf(".comparison") != -1 ? label.indexOf(".comparison") : 1000000;
            int repind = label.indexOf(".repeat") != -1 ? label.indexOf(".repeat") : 1000000;

            String lowerlabel = (label.toLowerCase());

            if (lowerlabel.indexOf(("Chemostat.Comparison").toLowerCase()) != -1) {
                exp_label1 = label.replace("..rescan.", "");
                exp_label1 = exp_label1.replace("..scanner1.", "");
                exp_label1 = exp_label1.replace("comparison2", "");
                exp_label1 = exp_label1.replace("comparison", "");

                int dotdotind = exp_label1.indexOf("..") + 2;
                int mindotind = exp_label1.indexOf("min.") + 4;
                int Aind1 = exp_label1.indexOf("1a.") + 3;
                int Bind1 = exp_label1.indexOf("1b.") + 3;
                int Aind2 = exp_label1.indexOf("2a.") + 3;
                int Bind2 = exp_label1.indexOf("2b.") + 3;
                int Aind3 = exp_label1.indexOf("3a.") + 3;
                int Bind3 = exp_label1.indexOf("3b.") + 3;

                int max = Math.max(Math.max(Math.max(Math.max(Math.max(Math.max(Math.max(dotdotind, mindotind), Aind1), Bind1), Aind2), Bind2), Aind3), Bind3);
                exp_label1 = exp_label1.substring(max);
            } else if (lowerlabel.startsWith(("ypd").toLowerCase())) {
                exp_label1 = "YPD";
            } else if (lowerlabel.indexOf("heat.shock") != -1 || lowerlabel.indexOf("heat_shock") != -1) {
                exp_label1 = "heat.shock";
            } else if (lowerlabel.indexOf("h2o2") != -1) {
                exp_label1 = "H2O2";
            } else if (lowerlabel.indexOf("2mm.md") != -1 || lowerlabel.indexOf("menadione") != -1 || lowerlabel.indexOf("md_ii") != -1) {
                exp_label1 = "menadione";
            } else if (lowerlabel.indexOf("nitrogen.depletion") != -1) {
                exp_label1 = "nitrogen.depletion";
            } else if (lowerlabel.indexOf("starv") != -1) {
                exp_label1 = "starvation";
            } else if (lowerlabel.indexOf("steady.state") != -1) {
                exp_label1 = "steady.state";
            } else if (lowerlabel.indexOf("stationary") != -1) {
                exp_label1 = "stationary";
            } else if (lowerlabel.indexOf("sporulation") != -1) {
                exp_label1 = "sporulation";
            } else if (lowerlabel.indexOf("gamma") != -1) {
                exp_label1 = "gamma";
            } else {
                if (label.indexOf("comp") != -1) {
                    System.out.println("sneaked in comp " + label);
                }
                int min = Math.min(Math.min(Math.min(Math.min(Math.min(Math.min(Math.min(Math.min(Math.min(Math.min(minind, hrind),
                        Timeind), sampleind), minundind), minnodotind), hrsind), hrsind2), compind), repind), hind);

                exp_label1 = label;
                //System.out.println("min " + min);
                if (min != 1000000 && min > 0) {
                    char last = label.charAt(min - 1);
                    if (last == '.' || last == '_') {
                        min--;
                        last = label.charAt(min - 1);
                    }
                    boolean digit = Character.isDigit(last);
                    if (digit) {
                        while (digit) {
                            min--;
                            last = label.charAt(min - 1);
                            digit = Character.isDigit(last);
                        }
                        while (last == '.' || last == '_') {
                            min--;
                            last = label.charAt(min - 1);
                        }
                    }
                    exp_label1 = label.substring(0, min);
                }
            }

            if (exp_label1.indexOf("X") == 0) {
                System.out.println("bf " + orig + "\nbf2 " + label);
                System.out.println("af " + exp_label1);
            }
        }
        ArrayList ret = new ArrayList();
        ret.add(exp_label1);
        ret.add(weight);
        return ret;
    }


    /**
     * @return
     */
    private void assignGOfreq(ValueBlock v, String outpath) {
        HashMap<String, Double> curmap = new HashMap<String, Double>();
        int countb = 0;
        ArrayList outmap = MoreArray.initArrayListList(v.genes.length, "");//new ArrayList(v.genes.length);

        for (int i = 0; i < v.genes.length; i++) {
            String gene = gene_labels[v.genes[i] - 1];
            ArrayList GOar = (ArrayList) GOmap.get(gene);
            if (GOar != null) {
                outmap.set(i, MoreArray.ArrayListtoString(GOar));
                HashMap<String, Integer> unique = new HashMap<String, Integer>();
                for (int h = 0; h < GOar.size(); h++) {
                    String[] GO = new String[1];
                    try {
                        GO = (String[]) GOar.get(h);
                        if (!GO[4].equals("")) {

                            try {
                                Object test = curmap.get(GO[4]);
                                if (test == null) {
                                    Object added = unique.get(GO[4]);
                                    if (added == null) {
                                        curmap.put(GO[4], 1.0);
                                        countb++;
                                        unique.put(GO[4], 1);
                                    }
                                } else {
                                    Object added = unique.get(GO[4]);
                                    if (added == null) {
                                        double val = (Double) test;
                                        curmap.put(GO[4], (val + 1.0));
                                        countb++;
                                        unique.put(GO[4], 1);
                                    }
                                }
                            } catch (Exception e) {
                            }
                        }
                    } catch (Exception e) {
                        try {
                            GO = new String[1];
                            GO[0] = (String) GOar.get(h);
                            //outmap.set(i, GO[0]);
                            Object test = curmap.get(GO[0]);
                            if (test == null) {
                                Object added = unique.get(GO[0]);
                                if (added == null) {
                                    curmap.put(GO[0], 1.0);
                                    countb++;
                                    unique.put(GO[0], 1);
                                }
                            } else {
                                Object added = unique.get(GO[0]);
                                if (added == null) {
                                    double val = (Double) test;
                                    curmap.put(GO[0], (val + 1.0));
                                    countb++;
                                    unique.put(GO[0], 1);
                                }
                            }
                        } catch (Exception e1) {
                            System.out.println("h " + h);
                            System.out.println("GOar " + GOar.size());
                            System.out.println("GO " + GO.length);
                            System.out.println("GO[0] " + GO[0]);
                            e1.printStackTrace();
                        }
                    }
                }
            } else {
                outmap.set(i, "");
            }
        }
        Set keysi = curmap.keySet();
        Iterator it = keysi.iterator();
        ArrayList out = new ArrayList();
        while (it.hasNext()) {
            String key = (String) it.next();
            double val = curmap.get(key);
            out.add(key + "\t" + val);
        }

        System.out.println("writing assignGOfreq " + outpath);
        TextFile.write(out, outpath);

        String outpath2 = outpath + "_map.txt";
        System.out.println("writing assignGOfreq outmap" + outpath);
        TextFile.write(outmap, outpath2, "_");
    }

    /**
     * @param args
     * @throws Exception
     */
    private void init(String[] args) throws Exception {
        vbl = ValueBlockListMethods.readAny(args[0]);
        prefix = args[0] + "_";

        expr = new SimpleMatrix(args[1]);
        expr_data_labels_hash = new HashMap();
        for (int j = 0; j < expr.ylabels.length; j++) {
            expr.ylabels[j] = StringUtil.replace(expr.ylabels[j], "\"", "");
            expr_data_labels_hash.put(expr.ylabels[j], j);
        }

        try {
            modData = TextFile.readtoList(args[2]);
        } catch (Exception e) {
            //e.printStackTrace();
        }

        try {
            String[][] sarray = TabFile.readtoArray(args[3]);
            System.out.println("setLabels e " + sarray.length + "\t" + sarray[0].length);
            int col = 2;
            exp_labels = MoreArray.replaceAll(MoreArray.extractColumnStr(sarray, col), "\"", "");
            System.out.println("setLabels e " + exp_labels.length + "\t" + exp_labels[0]);
        } catch (Exception e) {
            System.out.println("expecting 2 cols");
            e.printStackTrace();
        }

        try {
            String[][] sarray = TabFile.readtoArray(args[4]);
            System.out.println("setLabels e " + sarray.length + "\t" + sarray[0].length);
            int col = 2;
            gene_labels = MoreArray.replaceAll(MoreArray.extractColumnStr(sarray, col), "\"", "");
            System.out.println("setLabels e " + gene_labels.length + "\t" + gene_labels[0]);
        } catch (Exception e) {
            System.out.println("expecting 2 cols");
            e.printStackTrace();
        }


        String[][] GO_yeast_data;
        if (!args[5].equals("null")) {
            GO_yeast_data = TabFile.readtoArray(args[5]);
            GOmap = new HashMap();
            countGOmap = new HashMap();
            yeast_gene_names = new HashMap();
            for (int i = 0; i < GO_yeast_data.length; i++) {
                GO_yeast_data[i] = StringUtil.replace(GO_yeast_data[i], "\"", "");
                Object n = yeast_gene_names.get(GO_yeast_data[i][0]);
                if (n == null) {
                    //System.out.println("adding GO_yeast_data " + GO_yeast_data[i][0] + "\t" + GO_yeast_data[i][1]);
                    yeast_gene_names.put(GO_yeast_data[i][0], GO_yeast_data[i][1]);
                }
                //System.out.println(i + "\t" + GO_yeast_data[i][4]);
                String addStr = BuildGraph.removeGOequals(GO_yeast_data[i][4]);

                if (!addStr.equals("")) {
                    //System.out.println("goyeast " + i + "\t" + MoreArray.toString(GO_yeast_data[i], ","));

                    Object o1 = countGOmap.get(addStr);
                    if (o1 == null) {
                        countGOmap.put(addStr, 1);
                        //System.out.println("countGOmap " + addStr + "\t" + 1);
                    } else {
                        int c = (Integer) o1;
                        countGOmap.put(addStr, c + 1);
                        //System.out.println("countGOmap " + addStr + "\t" + (c + 1));
                    }

                    Object o = GOmap.get(GO_yeast_data[i][0]);
                    if (o == null) {
                        ArrayList add = new ArrayList();
                        add.add(addStr);
                        GOmap.put(GO_yeast_data[i][0], add);
                    } else {
                        ArrayList a = (ArrayList) o;
                        a.add(addStr);
                        GOmap.put(GO_yeast_data[i][0], a);
                    }
                }
            }
        } else {
            for (int i = 0; i < this.tabfile.length; i++) {
                String addStr = BuildGraph.removeGO(tabfile[i][15].toLowerCase());

                if (addStr != null && addStr.length() > 0) {
                    Object o2 = GOmap.get(tabfile[i][0]);
                    Object o3 = GOmap.get(tabfile[i][7]);
                    if (o2 == null && o3 == null) {
                        ArrayList add = new ArrayList();
                        String[] split = addStr.split(",");
                        for (int a = 0; a < split.length; a++) {
                            add.add(split[a]);
                        }
                        /*if (GOrefMAP != null) {
                            for (int a = 0; a < split.length; a++) {
                                String splitadd = (String) GOrefMAP.get(split[0]);

                                Object g = totalGO.get(splitadd);
                                if (g == null) {
                                    totalGO.put(splitadd, 1);
                                } else {
                                    int iv = (Integer) g;
                                    totalGO.put(splitadd, iv + 1);
                                }

                                System.out.println("splitadd " + splitadd);
                                add.add(splitadd);
                                //add.add(addStr);
                            }
                        }*/
                        //System.out.println("+tab_data[i][0] " + tab_data[i][0] + "\t" + tab_data[i][7]);
                        GOmap.put(tabfile[i][0], add);
                        GOmap.put(tabfile[i][7], add);

                        System.out.println("adding 1 GOmap " + tabfile[i][0] + "\t" + addStr);
                        System.out.println("adding 1 GOmap " + tabfile[i][7] + "\t" + addStr);
                    }
                }

            }
        }

        System.out.println("GOmap " + GOmap.size());

        String[][] summary_data = TabFile.readtoArray(args[6]);
        sigGOmap = new HashMap();
        for (int i = 1; i < summary_data.length; i++) {
            if (summary_data[i][12].length() > 4)
                sigGOmap.put(i, summary_data[i][12].substring(4));
        }

        totalExpCount = new HashMap<String, Integer>();
        for (int i = 0; i < exp_labels.length; i++) {
            ArrayList get = processString(i + 1, exp_labels, gene_labels, true);
            String label1 = (String) get.get(0);
            Object o = totalExpCount.get(label1);
            if (o == null) {
                totalExpCount.put(label1, 1);
            } else {
                int count = (Integer) o;
                totalExpCount.put(label1, count + 1);
            }
        }

        Set set = totalExpCount.keySet();
        //System.out.println("totalExpCount set " + set.size());
        Iterator it = set.iterator();
        ArrayList outExp = new ArrayList();
        while (it.hasNext()) {
            String key = (String) it.next();
            String val = "" + totalExpCount.get(key);
            String key_val = key + "\t" + val;
            //System.out.println("totalExpCount out " + key_val);
            outExp.add(key_val);
        }

        try {
            System.out.println("reading motif data");
            motif_data = new SimpleMatrix(args[7]);

            //take abs value of log p-values?
            //motif_data.data = Matrix.abs(motif_data.data);
            //discard -log(p-value) < 3
            //motif_data.data = Matrix.filterBelowtoZero(motif_data.data, 3.0, 0);
            //motif_data.data = Matrix.filterAboveEqual(motif_data.data, 3.0, 1);

            motif_data.xlabels = StringUtil.replace(motif_data.xlabels, "\"", "");
            motif_data.ylabels = StringUtil.replace(motif_data.ylabels, "\"", "");
            System.out.println("motif_data.xlabels");
            MoreArray.printArray(motif_data.xlabels);

            motif_data_labels_hash = new HashMap();
            for (int j = 0; j < motif_data.ylabels.length; j++) {
                //System.out.println("motif data " + motif_data.ylabels[j]);
                motif_data_labels_hash.put(motif_data.ylabels[j], j);
            }

            System.out.println("read motif_data");

        } catch (Exception e) {
            //e.printStackTrace();
        }

        System.out.println("outdir " + args[9]);
        outdir = args[9];
    }

    /**
     * @param args
     */
    public final static void main(String[] args) {

        if (args.length == 10) {
            GOandExpLabels rm = new GOandExpLabels(args);
        } else {
            System.out.println("syntax: java DataMining.func.GOandExpLabels\n" +
                    "<value block list>\n" +
                    "<expr data file>\n" +
                    "<Moduland modules.csv>\n" +
                    "<exp labels>\n" +
                    "<gene labels>\n" +
                    "<GO annotation file>\n" +
                    "<value block list summary file>\n" +
                    "<yetfasco binding site data>\n" +
                    "<tab file>\n" +
                    "<out dir>"

            );
        }
    }
}

