package DataMining.func;

import DataMining.ValueBlock;
import DataMining.ValueBlockList;
import DataMining.ValueBlockListMethods;
import mathy.Matrix;
import mathy.SimpleMatrix;
import mathy.stat;
import util.*;

import java.util.*;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: Oct 23, 2012
 * Time: 9:47:00 AM
 */
public class EvalMotifMotif {

    String[] valid_args = {
            "-bic", "-expr", "-tab", "-motif", "-edges"
    };
    HashMap options, expr_data_labels_hash, motif_data_labels_hash;

    GiveDate gd;

    ValueBlockList BIC;
    SimpleMatrix expr_data, motif_data;
    String[][] tab_data;
    String[] yeastids, yeastnames;

    ArrayList edgedata;

    int num_sample = 10000;
    String inBIC;

    double RANK_SCORE_THRESHOLD = 0.8;//0.99;
    double MOTIF_PAIR_SCORE_THRESHOLD = 0.1;//0.99;
    double TF_COEXPR_CUTOFF = 0.7;

    String suffix;


    ArrayList expr_cors = new ArrayList();
    ArrayList biclusterMotifs = new ArrayList();
    HashMap counts = new HashMap();
    HashMap pairs = new HashMap();
    HashMap pairsmax = new HashMap();
    long start_time;

    /**
     * @param args
     */
    public EvalMotifMotif(String[] args) {

        init(args);

        Random random = new Random();
        gd = new GiveDate();
        start_time = gd.giveMilli();
        ArrayList[] samples_genes = new ArrayList[num_sample];
        ArrayList[] samples_exps = new ArrayList[num_sample];

        System.out.println("BIC.size() " + BIC.size());
        for (int i = 0; i < BIC.size(); i++) {
            ValueBlock cur = (ValueBlock) BIC.get(i);
            //create num_sample random biclusters for this bicluster
            for (int a = 0; a < num_sample; a++) {
                samples_genes[a] = new ArrayList();
                samples_exps[a] = new ArrayList();

                ArrayList arg = new ArrayList();
                ArrayList are = new ArrayList();
                for (int j = 0; j < cur.genes.length; j++) {
                    arg.add(random.nextInt(expr_data.ylabels.length) + 1);
                }
                for (int j = 0; j < cur.exps.length; j++) {
                    are.add(random.nextInt(expr_data.xlabels.length) + 1);
                }
                int[] ing = MoreArray.ArrayListtoInt(arg);
                int[] ine = MoreArray.ArrayListtoInt(are);
                //new random gene set of bicluster size
                samples_genes[a].add(ing);
                samples_exps[a].add(ine);
            }
        }


        long mid_time = gd.giveMilli();
        System.out.println("done sampling " + (mid_time - start_time) / 1000.0 + " s");
        for (int i = 0; i < num_sample; i++) {
            expr_cors = new ArrayList();
            biclusterMotifs = new ArrayList();
            counts = new HashMap();
            pairs = new HashMap();
            pairsmax = new HashMap();

            makeEdgesCount(samples_genes[i], samples_exps[i], i);
        }

        long end_time = gd.giveMilli();
        System.out.println("done " + (end_time - start_time) / 1000.0 + " s");
    }


    /**
     *
     */
    private void makeEdgesCount(ArrayList genesamples, ArrayList expsamples, int index) {
        System.out.println("makeEdgesCount " + genesamples.size());
        long total_time = 0;
        //for all biclusters
        for (int i = 0; i < genesamples.size(); i++) {
            start_time = gd.giveMilli();
            ValueBlock vb = new ValueBlock((int[]) genesamples.get(i), (int[]) expsamples.get(i));
            System.out.println("i " + i + "\t" + vb.genes.length);
            ArrayList samps = new ArrayList();
            for (int j = 0; j < vb.genes.length; j++) {
                String g = expr_data.ylabels[vb.genes[j] - 1];

                Object o = motif_data_labels_hash.get(g);
                //int index = StringUtil.getFirstEqualsIndex(motif_data.ylabels, g);
                //if (index != -1) {
                if (o != null) {
                    double[] cursamp = motif_data.data[(Integer) o];
                    //System.out.println("\ncursamp ");
                    //System.out.println(MoreArray.toString(cursamp, ","));

                    samps.add(cursamp);
                    System.out.print("-");
                } else {
                    //System.out.print("notfound " + g);
                }
            }
            System.out.println("\ngenes " + vb.genes.length + "\t" + samps.size());
            double[][] sampsmat = MoreArray.convtodouble2DArray(samps, motif_data.data[0].length);
            //System.out.println("\n2D");

            if (sampsmat.length > 0) {
                double[] allsums = Matrix.columnSum(sampsmat);

                double[] avgsums = stat.norm(allsums, sampsmat.length);

                double[] TFcors = new double[allsums.length];
                //for all TFs, determine TF correlations
                for (int k = 0; k < allsums.length; k++) {
                    if (allsums[k] != 0) {// && avgsums[k] > motifProportion
                        vb.setDataAndMean(expr_data.data);
                        double[] mean = stat.norm(Matrix.columnSum(vb.exp_data), vb.genes.length);

                        String TFname = motif_data.xlabels[k].substring(0, motif_data.xlabels[k].indexOf("_"));

                        boolean inbic = false;
                        for (int g = 0; g < vb.genes.length; g++) {
                            if (TFname.equals(expr_data.ylabels[vb.genes[g] - 1])) {
                                inbic = true;
                                break;
                            }
                        }

                        Object o = expr_data_labels_hash.get(TFname);
                        //int index = StringUtil.getFirstEqualsIndex(expr_data.ylabels, TFname);
                        if (o != null) {

                            ArrayList TFdataar = new ArrayList();
                            for (int z = 0; z < vb.exps.length; z++) {
                                TFdataar.add(expr_data.data[(Integer) o][vb.exps[z] - 1]);
                            }
                            double[] TFdata = MoreArray.ArrayListtoDouble(TFdataar);

                            TFcors[k] = stat.correlationCoeff(mean, TFdata);

                            ArrayList TFtargets = new ArrayList();
                            //compute correlation of TF motif with its target genes
                            for (int n = 0; n < motif_data.data.length; n++) {
                                if (motif_data.data[n][k] > RANK_SCORE_THRESHOLD) {
                                    TFtargets.add(n + 1);
                                }
                            }
                            ValueBlock vbTFtargs = new ValueBlock(MoreArray.ArrayListtoInt(TFtargets), vb.exps);
                            vbTFtargs.setDataAndMean(expr_data.data);
                            double[] meanTFtargs = stat.norm(Matrix.columnSum(vbTFtargs.exp_data), vbTFtargs.genes.length);

                            double corTFtargs = stat.correlationCoeff(meanTFtargs, TFdata);
                            double corRatio = TFcors[k] / corTFtargs;


                            double corBictargs = stat.correlationCoeff(mean, meanTFtargs);
                            double corBictargRatio = TFcors[k] / corBictargs;

                            String s1 = "cor " + i + "\t" + k + "\t" + TFname + "\t" + TFcors[k] + "\t" +
                                    corTFtargs + "\t" + corRatio + "\t" + corBictargs + "\t" + corBictargRatio;
                            expr_cors.add(s1);

                            /* if (i == 0) {
                                System.out.println(s1);
                                System.out.println("cor " + i + "\t" + TFtargets.size());
                                System.out.println("cor " + i + "\t" + meanTFtargs.length);
                                System.out.println("cor " + i + "\t" + meanTFtargs[0]);
                                System.out.println("cor " + i + "\t" + TFdata.length);
                                System.out.println("cor " + i + "\t" + TFdata[0]);
                            }*/


                            //if (cor > TF_COEXPR_CUTOFF) {
                            if (TFcors[k] >= 0) {
                                String s = "bicluster motif " + 1 + "\t" + i + "\t" + k + "\t" + avgsums[k] + "\t" + (inbic ? 1 : 0) + "\t" +
                                        TFname + "\t" + motif_data.xlabels[k] + "\t" + TFcors[k] + "\t" + corTFtargs
                                        + "\t" + corRatio + "\t" + corBictargs + "\t" + corBictargRatio;
                                //System.out.println(s);
                                biclusterMotifs.add(s);
                            }
                            //} else if (cor < -TF_COEXPR_CUTOFF) {
                            else if (TFcors[k] < 0) {
                                String s = "bicluster motif " + 0 + "\t" + i + "\t" + k + "\t" + avgsums[k] + "\t" + (inbic ? 1 : 0) + "\t" +
                                        TFname + "\t" + motif_data.xlabels[k] + "\t" + TFcors[k] + "\t" + corTFtargs
                                        + "\t" + corRatio + "\t" + corBictargs + "\t" + corBictargRatio;
                                //System.out.println(s);
                                biclusterMotifs.add(s);
                            }
                            //}
                        }
                    }

                }
                binaryCountGenePairs(sampsmat, allsums, TFcors);
            }

            System.out.print("\n");
            total_time = gd.giveMilli();
            System.out.println("biclusterloop " + i + "\t" + ((total_time - start_time) / 1000.0) + " s");
            //}
        }

        String splice = suffix;
        if (index != -1)
            splice = suffix + "_index" + index;

        Set set = pairs.entrySet();
        Iterator it = set.iterator();

        ArrayList outedge = new ArrayList();
        //average each TF pair by the number of biclusters it was found in, given criteria
        while (it.hasNext()) {
            Map.Entry cur = (Map.Entry) it.next();
            String l = (String) cur.getKey();
            String a = l.substring(0, l.indexOf("__"));
            String b = l.substring(l.indexOf("__") + 2);
            double d = (Double) cur.getValue();

            double count = (Integer) counts.get(l);

            outedge.add(a + " ((pp)) " + b + " = " + (d / count));
        }

        String outpath1 = inBIC + splice + "_TFedges.sif";
        System.out.println("size " + outedge.size());
        System.out.println("writing " + outpath1);
        TextFile.write(outedge, outpath1);
        System.out.println("wrote " + outpath1);


        Set setmax = pairsmax.entrySet();
        Iterator itmax = setmax.iterator();

        ArrayList outedgemax = new ArrayList();
        ArrayList outedgemaxsif = new ArrayList();
        //average each TF pair by the number of biclusters it was found in, given criteria
        while (itmax.hasNext()) {
            Map.Entry cur = (Map.Entry) itmax.next();
            String l = (String) cur.getKey();
            String a = l.substring(0, l.indexOf("__"));
            String b = l.substring(l.indexOf("__") + 2);
            double d = (Double) cur.getValue();

            outedgemax.add(a + "\t" + b + "\t" + d);
            outedgemaxsif.add(a + " ((pp)) " + b + " = " + d);
        }


        String outpath4 = inBIC + splice + "_TFedges_max.txt";
        System.out.println("size " + outedgemax.size());
        System.out.println("writing " + outpath4);
        TextFile.write(outedgemax, outpath4);
        System.out.println("wrote " + outpath4);

        String outpath5 = inBIC + splice + "_TFedges_max.sif";
        System.out.println("size " + outedgemaxsif.size());
        System.out.println("writing " + outpath5);
        TextFile.write(outedgemaxsif, outpath5);
        System.out.println("wrote " + outpath5);

        String outpath2 = inBIC + splice + "_biclustermotifs.txt";
        System.out.println("size " + biclusterMotifs.size());
        System.out.println("writing " + outpath2);
        TextFile.write(biclusterMotifs, outpath2);
        System.out.println("wrote " + outpath2);

        String outpath3 = inBIC + splice + "_cors.txt";
        System.out.println("size " + expr_cors.size());
        System.out.println("writing " + outpath3);
        TextFile.write(expr_cors, outpath3);
        System.out.println("wrote " + outpath3);

        long end_time = gd.giveMilli();
        System.out.println("done sample " + index + "\t" + ((end_time - start_time) / 1000.0) + " s");
    }

    /**
     * @param sampsmat
     * @param allsums
     * @param TFcors
     */
    private void binaryCountGenePairs(double[][] sampsmat, double[] allsums, double[] TFcors) {

        double[] absTFcors = stat.abs(TFcors);
        HashMap thesepairs = new HashMap();
        HashMap thesecounts = new HashMap();

        HashMap[] gene_pairs = new HashMap[sampsmat.length];
        //System.out.print(sampsmat.length);
        for (int a = 0; a < sampsmat.length; a++) {
            int countpergene = 0;
            // System.out.print(".");
            gene_pairs[a] = new HashMap();
            for (int m = 0; m < allsums.length; m++) {
                if (sampsmat[a][m] > RANK_SCORE_THRESHOLD && absTFcors[m] > TF_COEXPR_CUTOFF) {
                    for (int n = m + 1; n < allsums.length; n++) {
                        if (sampsmat[a][n] > RANK_SCORE_THRESHOLD && absTFcors[n] > TF_COEXPR_CUTOFF) {
                            if (a == 0) {
                                //System.out.print("*" + sampsmat[a][m] + "_" + absTFcors[m] + "__" + sampsmat[a][n] + "_" + absTFcors[n]);
                                //System.out.print("*" + sampsmat[a][m] + "_" + absTFcors[m] + "__" + sampsmat[a][n] + "_" + absTFcors[n]);
                            }

                            double score = sampsmat[a][m] + sampsmat[a][n];

                            String[] mtfar = {motif_data.xlabels[m], motif_data.xlabels[n]};
                            //sort array alphabetically, motif data is now unordered relative to gene upstream position
                            Arrays.sort(mtfar);
                            String pairTFlabel = mtfar[0] + "__" + mtfar[1];
                            gene_pairs[a].put(pairTFlabel, score);
                            countpergene++;
                        }
                    }
                }
            }
            //System.out.print(countpergene);
        }


        for (int a = 0; a < sampsmat.length; a++) {
            //System.out.print("-");
            Set set = gene_pairs[a].entrySet();
            Iterator it = set.iterator();
            int countpergene = 0;
            while (it.hasNext()) {
                Map.Entry cur = (Map.Entry) it.next();
                String label = (String) cur.getKey();
                double d = (Double) cur.getValue();

                for (int b = a + 1; b < sampsmat.length; b++) {
                    Object o = gene_pairs[b].get(label);

                    if (o != null) {
                        //System.out.print("*");
                        double d2 = (Double) o;

                        double[] tmp = {d, d2};
                        double avgs = stat.avg(tmp);
                        Object refo = thesepairs.get(label);
                        Object refo2 = thesecounts.get(label);
                        if (refo == null) {
                            thesepairs.put(label, avgs);
                            thesecounts.put(label, 1);
                        } else {
                            double refd = (Double) refo;
                            thesepairs.put(label, refd + avgs);
                            int refd2 = (Integer) refo2;
                            thesecounts.put(label, refd2 + 1);
                        }

                        countpergene++;
                    }
                }
            }
            //System.out.print(countpergene);
        }

        System.out.println("");
        double total = (sampsmat.length * sampsmat.length - sampsmat.length) / 2.0;

        Set set = thesepairs.entrySet();
        Iterator it = set.iterator();
        System.out.println("set size " + set.size());
        while (it.hasNext()) {
            Map.Entry cur = (Map.Entry) it.next();
            String label = (String) cur.getKey();
            double d = (Double) cur.getValue();
            d /= total;

            Object o = pairs.get(label);

            //count of how many bicluster had at least a pair of genes with that TF pair, given the criteria
            Object o2 = counts.get(label);
            if (o == null) {
                pairs.put(label, d);
                counts.put(label, 1);
            } else {
                double last = (Double) o;
                pairs.put(label, last + d);
                int d2 = (Integer) o2;
                counts.put(label, d2 + 1);
            }

            Object omax = pairsmax.get(label);
            if (omax == null) {
                pairsmax.put(label, d);
            } else {
                pairsmax.put(label, Math.max((Double) omax, d));
            }
        }

    }


    /**
     * @param args
     */
    private void init(String[] args) {

        options = MapArgOptions.maptoMap(args, valid_args);

        if (options.get("-bic") != null) {
            inBIC = (String) options.get("-bic");
            try {
                BIC = ValueBlockListMethods.readAny(inBIC, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (BIC != null) {
                System.out.println("BIC.bicluster_set_size() " + BIC.size());
            }
        }

        if (options.get("-expr") != null) {
            String f = (String) options.get("-expr");
            expr_data = new SimpleMatrix(f);
            expr_data.xlabels = StringUtil.replace(expr_data.xlabels, "\"", "");
            expr_data.ylabels = StringUtil.replace(expr_data.ylabels, "\"", "");
            System.out.println("read expr_data ");

            expr_data_labels_hash = new HashMap();
            for (int j = 0; j < expr_data.ylabels.length; j++) {
                expr_data_labels_hash.put(expr_data.ylabels[j], j);
            }
        }

        if (options.get("-motif") != null) {
            System.out.println("reading motif data");
            String f = (String) options.get("-motif");
            motif_data = new SimpleMatrix(f);
            //take abs value of log p-values?
            //motif_data.data = Matrix.abs(motif_data.data);
            //discard -log(p-value) < 3
            //motif_data.data = Matrix.filterBelowtoZero(motif_data.data, 3.0, 0);
            //motif_data.data = Matrix.filterAboveEqual(motif_data.data, 3.0, 1);

            motif_data.xlabels = StringUtil.replace(motif_data.xlabels, "\"", "");
            motif_data.ylabels = StringUtil.replace(motif_data.ylabels, "\"", "");

            motif_data_labels_hash = new HashMap();
            for (int j = 0; j < motif_data.ylabels.length; j++) {
                motif_data_labels_hash.put(motif_data.ylabels[j], j);
            }
            System.out.println("read motif_data");
        }


        if (options.get("-tab") != null) {
            String f = (String) options.get("-tab");
            tab_data = TabFile.readtoArray(f);
            System.out.println("read tab_data " + tab_data.length + "\t" + tab_data[0].length);
            yeastids = new String[tab_data.length - 1];
            yeastnames = new String[tab_data.length - 1];
            for (int i = 1; i < tab_data.length; i++) {
                yeastids[i - 1] = tab_data[i][7];
                yeastnames[i - 1] = StringUtil.replace(tab_data[i][8], ",", "_");
                yeastnames[i - 1] = StringUtil.replace(tab_data[i][8], "'", "_");
            }
            System.out.println("yeastnames");
            System.out.println(MoreArray.toString(yeastnames, ","));
        }

        if (options.get("-edges") != null) {
            String inEdges = (String) options.get("-edges");
            edgedata = TextFile.readtoList(inEdges);
            System.out.println("read edges " + edgedata.size());
        }

        suffix = "_r" + RANK_SCORE_THRESHOLD + "_c" + TF_COEXPR_CUTOFF;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 10) {
            EvalMotifMotif rm = new EvalMotifMotif(args);
        } else {
            System.out.println("syntax: java DataMining.func.EvalMotifMotif\n" +
                    "<-bic valueblock list>\n" +
                    "<-tab tab data path>\n" +
                    "<-motif motif data>\n" +
                    "<-expr expr data>\n" +
                    "<-edges motif motif network>"
            );
        }
    }
}
