package DataMining.func;

import DataMining.BlockMethods;
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
 * Date: Nov 8, 2012
 * Time: 10:31:14 PM
 */
public class ContentVsRegulation {


    String[] valid_args = {
            "-bic", "-expr", "-tab", "-motif", "-edges"
    };

    HashMap options;

    ValueBlockList BIC;
    SimpleMatrix expr_data;
    SimpleMatrix motif_data;
    String[][] tab_data;
    String[] yeastids, yeastnames;
    ArrayList edgedata;
    String inBIC;
    String inEdges;


    HashMap pairs = new HashMap();
    HashMap pairsmax = new HashMap();
    HashMap counts = new HashMap();
    HashMap exps2TF = new HashMap();
    HashMap expr_data_labels_hash = new HashMap();
    HashMap motif_data_labels_hash = new HashMap();
    HashMap biclusterTFMap = new HashMap();
    HashMap biclustermotifMap = new HashMap();

    ArrayList biclusterMotifs = new ArrayList();
    ArrayList expr_cors = new ArrayList();


    double TF_COEXPR_CUTOFF = 0.7;
    //double motifProportion = 0.25;
    double RANK_SCORE_THRESHOLD = 0.8;//0.99;
    double MOTIF_PAIR_SCORE_THRESHOLD = 0.1;//0.99;
    double BICLUSTER_FRACTION = 0.3;

    String suffix;

    /**
     * @param args
     */
    public ContentVsRegulation(String[] args) {
        init(args);

        System.out.println("done init");


        for (int i = 0; i < BIC.size(); i++) {
            ValueBlock vb = (ValueBlock) BIC.get(i);
            //System.out.println("i " + i + "\t" + vb.genes.length);
            System.out.print(".");
            ArrayList samps = new ArrayList();
            for (int j = 0; j < vb.genes.length; j++) {
                String g = expr_data.ylabels[vb.genes[j] - 1];

                Object o = motif_data_labels_hash.get(g);
                if (o != null) {
                    double[] cursamp = motif_data.data[(Integer) o];
                    //System.out.println("\ncursamp ");
                    //System.out.println(MoreArray.toString(cursamp, ","));

                    samps.add(cursamp);
                    //System.out.print("-");
                } else {
                    //System.out.print("notfound " + g);
                }
            }
            //System.out.println("\ngenes " + vb.genes.length + "\t" + samps.size());
            double[][] sampsmat = MoreArray.convtodouble2DArray(samps, motif_data.data[0].length);
            //System.out.println("\n2D");

            if (sampsmat.length > 0) {
                double[] allsums = Matrix.columnSum(sampsmat);
                double[] allsumsnorm = Matrix.norm(sampsmat.length, allsums);
                double[] TFcors = new double[allsums.length];
                //for all TFs, determine TF correlations
                for (int k = 0; k < allsums.length; k++) {
                    if (allsums[k] != 0) {
                        vb.setDataAndMean(expr_data.data);
                        double[] mean = stat.norm(Matrix.columnSum(vb.exp_data), vb.genes.length);

                        String TFname = motif_data.xlabels[k].substring(0, motif_data.xlabels[k].indexOf("_"));


                        Object o = expr_data_labels_hash.get(TFname);
                        if (o != null) {

                            ArrayList TFdataar = new ArrayList();
                            for (int z = 0; z < vb.exps.length; z++) {
                                TFdataar.add(expr_data.data[(Integer) o][vb.exps[z] - 1]);
                            }
                            double[] TFdata = MoreArray.ArrayListtoDouble(TFdataar);
                            TFcors[k] = stat.correlationCoeff(mean, TFdata);

                            if (TFcors[k] >= TF_COEXPR_CUTOFF && allsumsnorm[k] > BICLUSTER_FRACTION) {
                                //System.out.println(i + "\t" + TFname);
                                String ind = "" + i;
                                Object tof = biclusterTFMap.get(ind);

                                if (tof == null)
                                    biclusterTFMap.put(ind, TFname);
                                else {
                                    String stof = (String) tof;
                                    if (!stof.equals(TFname) && stof.indexOf(TFname + "^") == -1)
                                        biclusterTFMap.put(ind, TFname + "^" + (stof));
                                }

                                Object mof = biclustermotifMap.get(ind);
                                if (mof == null)
                                    biclustermotifMap.put(ind, motif_data.xlabels[k]);
                                else {
                                    String smof = (String) mof;
                                    if (!smof.equals(TFname) && smof.indexOf(motif_data.xlabels[k] + "^") == -1)
                                        biclustermotifMap.put(ind, motif_data.xlabels[k] + "^" + (smof));
                                }
                            }
                        }
                    }

                }
            }

            // System.out.print("\n");
        }

        ArrayList out = makeOutTF();
        String outpath = args[1] + "_TFover.txt";
        System.out.println("writing " + outpath);
        TextFile.write(out, outpath);

        ArrayList out2 = makeOutMotif();
        String outpath2 = args[1] + "_motifover.txt";
        System.out.println("writing " + outpath2);
        TextFile.write(out2, outpath2);
    }


    /**
     * @return
     */
    private ArrayList makeOutTF() {

        ArrayList out = new ArrayList();
        for (int i = 0; i < BIC.size(); i++) {
            ValueBlock vb1 = (ValueBlock) BIC.get(i);
            Object o = biclusterTFMap.get("" + i);
            if (o != null) {
                String motifStri = (String) o;
                String[] TFs1 = motifStri.split("\\^");
                List list = Arrays.asList(TFs1);
                Set set = new HashSet(list);
                TFs1 = (String[]) set.toArray(new String[0]);
                // System.out.println("makeOut 1 " + (String) o + "\t" + MoreArray.toString(TFs1, ","));

                for (int z = 0; z < TFs1.length; z++) {
                    int index1 = StringUtil.getFirstEqualsIndex(yeastids, TFs1[z]);
                    try {
                        if (index1 != -1)
                            TFs1[z] = yeastnames[index1];
                    } catch (Exception e) {
                    }
                }


                HashMap h1 = new HashMap();
                for (int a = 0; a < TFs1.length; a++) {
                    h1.put(TFs1[a], 1);
                }
                for (int j = i + 1; j < BIC.size(); j++) {
                    ValueBlock vb2 = (ValueBlock) BIC.get(j);

                    Object o2 = biclusterTFMap.get("" + j);
                    if (o2 != null) {
                        String motifStrj = (String) o2;
                        String[] TFs2 = motifStrj.split("\\^");

                        List list2 = Arrays.asList(TFs2);
                        Set set2 = new HashSet(list2);
                        TFs2 = (String[]) set2.toArray(new String[0]);

                        for (int z = 0; z < TFs2.length; z++) {
                            int index1 = StringUtil.getFirstEqualsIndex(yeastids, TFs2[z]);
                            try {
                                if (index1 != -1)
                                    TFs2[z] = yeastnames[index1];
                            } catch (Exception e) {
                            }
                        }

                        //System.out.println("makeOut 2 " + (String) o2 + "\t" + MoreArray.toString(TFs2, ","));

                        double regoverlap = 0;
                        for (int b = 0; b < TFs2.length; b++) {
                            if (h1.get(TFs2[b]) != null)
                                regoverlap++;
                            //System.out.println("overlap TF " + TFs2[b] + "\t" + MoreArray.toString(TFs1, ",") + "\t" + MoreArray.toString(TFs2, ","));
                            //else
                            //    h1.put(TFs2[b], 1);
                        }

                        double sqrt_product = Math.sqrt(TFs1.length * TFs2.length);
                        double frxn = regoverlap / sqrt_product;

                        out.add("" + i + "_" + j + "\t" + BlockMethods.computeBlockOverlapGeneSum(vb1, vb2)
                                + "\t" + BlockMethods.computeBlockOverlapExpSum(vb1, vb2)
                                + "\t" + BlockMethods.computeBlockOverlapGeneExpSum(vb1, vb2)
                                + "\t" + frxn + "\t" + regoverlap);

                        double gover = BlockMethods.computeBlockOverlapGeneSum(vb1, vb2);
                        double eover = BlockMethods.computeBlockOverlapExpSum(vb1, vb2);
                        double regover = BlockMethods.computeBlockOverlapGeneExpSum(vb1, vb2);

                        if (gover > 0.7 && regover < 0.3) {
                            System.out.println("TF gover > 0.7 && regover < 0.3");
                            System.out.println(i + "\t" + j + "\tgover " + gover + "\teover " + eover + "\tregover "
                                    + regover + "\tim " + motifStri + "\tjm " + motifStrj);
                        }
                    }
                }
            }
        }
        return out;
    }

    /**
     * @return
     */

    private ArrayList makeOutMotif() {

        ArrayList out = new ArrayList();
        for (int i = 0; i < BIC.size(); i++) {
            ValueBlock vb1 = (ValueBlock) BIC.get(i);
            Object o = biclustermotifMap.get("" + i);
            if (o != null) {
                String motifStri = (String) o;
                String[] motifs1 = motifStri.split("\\^");
                List list = Arrays.asList(motifs1);
                Set set = new HashSet(list);
                motifs1 = (String[]) set.toArray(new String[0]);
                //System.out.println("makeOut 1 " + (String) o + "\t" + MoreArray.toString(motifs1, ","));

                HashMap h1 = new HashMap();
                HashMap all = new HashMap();
                for (int a = 0; a < motifs1.length; a++) {
                    h1.put(motifs1[a], 1);
                    all.put(motifs1[a], 1);
                }


                for (int j = i + 1; j < BIC.size(); j++) {
                    ValueBlock vb2 = (ValueBlock) BIC.get(j);

                    Object o2 = biclustermotifMap.get("" + j);
                    if (o2 != null) {
                        String motifStrj = (String) o2;
                        String[] motifs2 = motifStrj.split("\\^");
                        List list2 = Arrays.asList(motifs2);
                        Set set2 = new HashSet(list2);
                        motifs2 = (String[]) set2.toArray(new String[0]);
                        //System.out.println("makeOut 2 " + (String) o2 + "\t" + MoreArray.toString(motifs2, ","));

                        double regoverlap = 0;
                        for (int b = 0; b < motifs2.length; b++) {
                            if (!all.containsKey(motifs2[b]))
                                all.put(motifs2[b], 1);
                            if (h1.get(motifs2[b]) != null)
                                regoverlap++;
                            //System.out.println("overlap motif " + motifs2[b] + "\t" + MoreArray.toString(motifs1, ",") + "\t" + MoreArray.toString(motifs2, ","));

                            //else
                            //    h1.put(motifs2[b], 1);
                        }

                        //double sqrt_product = Math.sqrt(motifs1.length * motifs2.length);
                        double frxn = regoverlap / (double) all.size();

                        double gover = BlockMethods.computeBlockOverlapGeneSum(vb1, vb2);//computeBlockOverlapGeneRootProduct(vb1, vb2);
                        double eover = BlockMethods.computeBlockOverlapExpSum(vb1, vb2);//computeBlockOverlapExpRootProduct(vb1, vb2);
                        double geover = BlockMethods.computeBlockOverlapGeneExpSum(vb1, vb2);
                        out.add("" + i + "_" + j + "\t" + gover
                                + "\t" + eover
                                + "\t" + geover
                                + "\t" + frxn + "\t" + regoverlap);
                        if (gover > 0.7 && geover < 0.3) {
                            System.out.println("motif gover > 0.7 && regover < 0.3");
                            System.out.println(i + "\t" + j + "\tgover " + gover + "\teover " + eover + "\tregover "
                                    + geover + "\tim " + motifStri + "\tjm " + motifStrj);
                        }
                    }
                }

            }
        }
        return out;
    }


    /**
     * Data for a single bicluster
     *
     * @param sampsmat - genes x motifs
     * @param allsums
     * @param TFcors
     */
    private void binaryCountGenePairs(double[][] sampsmat, double[] allsums, double[] TFcors, int[][] exps) {

        double[] absTFcors = stat.abs(TFcors);
        HashMap thesepairs = new HashMap();
        HashMap thesecounts = new HashMap();

        HashMap[] motif_pairs = new HashMap[sampsmat.length];
        System.out.print("binaryCountGenePairs " + sampsmat.length);

        //for all genes
        for (int a = 0; a < sampsmat.length; a++) {
            int countpergene = 0;
            //System.out.print(".");
            motif_pairs[a] = new HashMap();
            //for all pairs of motifs, define reference hash for all genes in bicluster storing scores of motif pairs satisfying criteria
            for (int m = 0; m < allsums.length; m++) {
                if (sampsmat[a][m] > RANK_SCORE_THRESHOLD && absTFcors[m] > TF_COEXPR_CUTOFF) {
                    for (int n = m + 1; n < allsums.length; n++) {
                        if (sampsmat[a][n] > RANK_SCORE_THRESHOLD && absTFcors[n] > TF_COEXPR_CUTOFF) {
                            //if (a == 0)
                            //    System.out.print("*" + sampsmat[a][m] + "_" + absTFcors[m] + "__" + sampsmat[a][n] + "_" + absTFcors[n]);
                            double score = sampsmat[a][m] + sampsmat[a][n];

                            String[] mtfar = {motif_data.xlabels[m], motif_data.xlabels[n]};
                            //sort array alphabetically, motif data is now unordered relative to gene upstream position
                            Arrays.sort(mtfar);
                            String pairTFlabel = mtfar[0] + "__" + mtfar[1];
                            motif_pairs[a].put(pairTFlabel, score);
                            countpergene++;
                        }
                    }
                }
            }
            //System.out.print(countpergene);
        }

        //genes
        for (int a = 0; a < sampsmat.length; a++) {
            //System.out.print("-");
            Set set = motif_pairs[a].entrySet();
            Iterator it = set.iterator();
            int countpergene = 0;
            while (it.hasNext()) {
                Map.Entry cur = (Map.Entry) it.next();
                String label = (String) cur.getKey();
                double d = (Double) cur.getValue();
                //all other genes
                for (int b = a + 1; b < sampsmat.length; b++) {
                    Object o = motif_pairs[b].get(label);
                    //if genes share a motif pair
                    if (o != null) {
                        //System.out.print("*");
                        double d2 = (Double) o;

                        double[] tmp = {d, d2};
                        //avg of sum of motifAB in gene x ranks and motifAB in gene y ranks
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
            Object o3 = exps2TF.get(label);
            Object omax = pairsmax.get(label);

            if (o == null) {
                pairs.put(label, d);
                counts.put(label, 1);
                exps2TF.put(label, exps);
                pairsmax.put(label, d);
            } else {
                double last = (Double) o;
                pairs.put(label, last + d);
                int d2 = (Integer) o2;
                counts.put(label, d2 + 1);

                int[][] finalar = sumExps(exps, o3);

                exps2TF.put(label, finalar);
                pairsmax.put(label, Math.max((Double) omax, d));
            }
        }

    }

    /**
     * @param exps
     * @param o3
     * @return
     */
    private int[][] sumExps(int[][] exps, Object o3) {
        int[][] ar = (int[][]) o3;
        ArrayList all = new ArrayList();
        //
        //System.out.println("ar[0]");
        //MoreArray.printArray(ar[0]);

        HashSet h = new HashSet();
        for (int k = 0; k < ar[0].length; k++) {
            //all.add(ar[0][k]);
            h.add(ar[0][k]);
            if (ar[0][k] == 0) {
                System.out.println("ar[0][k] == 0 !!");
                MoreArray.printArray(ar[0]);
            }
        }

        //System.out.println("exps[0]");
        //MoreArray.printArray(exps[0]);
        for (int k = 0; k < exps[0].length; k++) {
            //if (!all.contains(exps[0][k]))
            //    all.add(exps[0][k]);
            if (!h.contains(exps[0][k]))
                h.add(exps[0][k]);
            if (exps[0][k] == 0) {
                System.out.println("exps[0][k] == 0 !!");
                MoreArray.printArray(exps[0]);
            }
        }

        Iterator it = h.iterator();
        while (it.hasNext()) {
            all.add(it.next());
        }

        int[] allar = MoreArray.ArrayListtoInt(all);
        Arrays.sort(allar);

        //System.out.println("allar");
        //MoreArray.printArray(allar);

        int[][] finalar = new int[2][allar.length];
        //System.out.println("sumExps " + allar.length);
        for (int k = 0; k < allar.length; k++) {
            //int curall = (Integer) all.get(k);
            int curall = allar[k];
            int index1 = -1;
            try {
                index1 = MoreArray.getArrayInd(ar[0], curall);
            } catch (Exception e) {
                e.printStackTrace();
            }
            int index2 = -1;
            try {
                index2 = MoreArray.getArrayInd(exps[0], curall);
            } catch (Exception e) {
                e.printStackTrace();
            }

            finalar[0][k] = allar[k];
            //System.out.println("sumExps " + k + "\t" + curall + "\t" + index1 + "\t" + index2 + "\t" +
            //        ar[0].length + "\t" + exps[0].length + "\t" + finalar[0].length);
            if (index1 != -1)
                finalar[1][k] = ar[1][index1];
            if (index2 != -1)
                finalar[1][k] += exps[1][index2];
        }
        return finalar;
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
            inEdges = (String) options.get("-edges");
            edgedata = TextFile.readtoList(inEdges);
            System.out.println("read edges " + edgedata.size());
        }

        suffix = "_r" + RANK_SCORE_THRESHOLD + "_c" + TF_COEXPR_CUTOFF;

    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 8 || args.length == 6) {
            ContentVsRegulation rm = new ContentVsRegulation(args);
        } else {
            System.out.println("syntax: java DataMining.func.ContentVsRegulation\n" +
                    "<-bic valueblock list>\n" +
                    "<-expr expr data path>\n" +
                    "<-tab tab data path>\n" +
                    "<-motif motif data>\n" +
                    "<-edges OPTIONAL>"
            );
            System.out.println("syntax: bic, expr, tab, motif ");
            System.out.println("OR");
            System.out.println("syntax: tab, expr, edges ");
        }
    }
}
