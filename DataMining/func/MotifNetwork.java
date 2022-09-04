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
 * Generate edges between TFs based on cooccurence in biclusters.
 * <p/>
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: Oct 5, 2012
 * Time: 11:22:41 PM
 */
public class MotifNetwork {

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

    double TF_COEXPR_CUTOFF = 0.7;
    //double motifProportion = 0.25;

    HashMap pairs = new HashMap();
    HashMap pairsmax = new HashMap();
    HashMap counts = new HashMap();
    HashMap exps2TF = new HashMap();
    HashMap expr_data_labels_hash = new HashMap();
    HashMap motif_data_labels_hash = new HashMap();

    ArrayList biclusterMotifs = new ArrayList();
    ArrayList expr_cors = new ArrayList();

    double RANK_SCORE_THRESHOLD = 0.8;//0.99;
    double MOTIF_PAIR_SCORE_THRESHOLD = 0.1;//0.99;

    String suffix;

    /**
     * @param args
     */
    public MotifNetwork(String[] args) {

        init(args);

        System.out.println("done init");
        if (BIC != null && expr_data != null && tab_data != null && motif_data != null) {
            System.out.println("start makeEdgesCount ");
            makeEdgesCount();

        } else if (expr_data != null && tab_data != null && edgedata != null) {
            labelYeastNames();
        }
    }


    /**
     *
     */
    private void labelYeastNames() {

        HashMap nodes = new HashMap();
        ArrayList outedge = new ArrayList();

        //ArrayList nodelabels = new ArrayList();
        //nodelabels.add("nodelabels");
        for (int i = 0; i < edgedata.size(); i++) {
            System.out.print(".");
            String cur = (String) edgedata.get(i);
            String[] s = cur.split("\t");

            int i1 = s[0].indexOf("_");
            String one = s[0];
            if (i1 != -1) {
                one = s[0].substring(0, i1);
            }
            int i2 = 0;
            try {
                i2 = s[1].indexOf("_");
            } catch (Exception e) {
                System.out.println("labelYeastNames " + cur + "\t" + MoreArray.toString(s, " - "));
                e.printStackTrace();
            }
            String two = s[1];
            if (i2 != -1) {
                two = s[1].substring(0, i2);
            }

            int index1 = StringUtil.getFirstEqualsIndex(yeastids, one);
            int index2 = StringUtil.getFirstEqualsIndex(yeastids, two);

            String name1 = null, name2 = null;
            try {
                if (index1 != -1 && i1 != -1)
                    name1 = yeastnames[index1] + "_" + s[0].substring(i1 + 1);
                if (index2 != -1 && i2 != -1)
                    name2 = yeastnames[index2] + "_" + s[1].substring(i2 + 1);
            } catch (Exception e) {
                System.out.println("name failed " + index1 + "\t" + i1 + "\t" + index2 + "\t" + i2);
                System.out.println("name failed " + yeastnames[index1]);
                System.out.println("name failed " + yeastnames[index2]);
                System.out.println("name failed " + i1 + "\t" + one.length() + "\t" + one);
                System.out.println("name failed " + i2 + "\t" + two.length() + "\t" + two);
                System.out.println("name failed " + one.substring(i1 + 1));
                System.out.println("name failed " + two.substring(i2 + 1));
                e.printStackTrace();
            }

            if (name1 == null)
                name1 = s[0];
            if (name2 == null)
                name2 = s[1];

            Object o = nodes.get(name1);
            if (o == null) {
                nodes.put(name1, 1);
            } else {
                int count = (Integer) o;
                nodes.put(name1, count + 1);
            }

            Object o2 = nodes.get(name2);
            if (o == null) {
                nodes.put(name2, 1);
            } else {
                int count = (Integer) o;
                nodes.put(name2, count + 1);
            }

            if (index1 != -1 || index2 != -1)
                outedge.add(name1 + " ((pp)) " + name2 + " = " + s[3]);
            else
                outedge.add(cur);
        }

        String outpath1 = inEdges + "_TFedges_rename.sif";//+ suffix
        System.out.println("size " + outedge.size());
        System.out.println("writing " + outpath1);
        TextFile.write(outedge, outpath1);

        /*    String outpath4 = inEdges + "_TFedges_labels.nda";
       System.out.println("size " + outedge.size());
       System.out.println("writing " + outpath4);
       TextFile.write(outedge, outpath4);*/

        Set set = nodes.keySet();
        Iterator it = set.iterator();

        ArrayList outgdf = new ArrayList();
        ArrayList outtxt = new ArrayList();
        outgdf.add("nodedef>name VARCHAR,label VARCHAR");
        int count = 1;
        while (it.hasNext()) {
            String s = (String) it.next();
            outgdf.add(s + ",TF motif " + count);
            count++;
        }
        outgdf.add("edgedef>node1 VARCHAR,node2 VARCHAR, value DOUBLE");
        for (int i = 0; i < edgedata.size(); i++) {

            String cur = (String) edgedata.get(i);
            String[] s = cur.split("\t");

            int i1 = s[0].indexOf("_");
            String one = s[0];
            if (i1 != -1) {
                one = s[0].substring(0, i1);
            }
            int i2 = 0;
            try {
                i2 = s[1].indexOf("_");
            } catch (Exception e) {
                System.out.println("i2 " + cur + " * " + MoreArray.toString(s, " - "));
                e.printStackTrace();
            }
            String two = s[1];
            if (i2 != -1) {
                two = s[1].substring(0, i2);
            }

            int index1 = StringUtil.getFirstEqualsIndex(yeastids, one);
            int index2 = StringUtil.getFirstEqualsIndex(yeastids, two);

            String name1 = null, name2 = null;

            if (index1 != -1 && i1 != -1)
                name1 = yeastnames[index1] + "_" + s[0].substring(i1 + 1);
            if (index2 != -1 && i2 != -1)
                name2 = yeastnames[index2] + "_" + s[1].substring(i2 + 1);

            if (name1 == null)
                name1 = s[0];
            if (name2 == null)
                name2 = s[1];

            if (Double.parseDouble(s[3]) > MOTIF_PAIR_SCORE_THRESHOLD) {
                outgdf.add(name1 + "," + name2 + "," + s[3]);
                outtxt.add(name1 + "\t" + name2 + "\t" + s[3]);
            }
        }
        //GDF format
        /*
        nodedef>name VARCHAR,label VARCHAR
        s1,Site number 1
        s2,Site number 2
        s3,Site number 3
        edgedef>node1 VARCHAR,node2 VARCHAR
        s1,s2,1.2341
        s2,s3,0.453
        s3,s2, 2.34
        s3,s1, 0.871
        */

        String outpath2 = inEdges + "_" + MOTIF_PAIR_SCORE_THRESHOLD + "_TFedges_rename.gdf";//suffix +
        System.out.println("size " + outgdf.size());
        System.out.println("writing " + outpath2);
        TextFile.write(outgdf, outpath2);

        String outpath3 = inEdges + "_" + MOTIF_PAIR_SCORE_THRESHOLD + "_TFedges_rename.txt";//suffix +
        System.out.println("size " + outtxt.size());
        System.out.println("writing " + outpath3);
        TextFile.write(outtxt, outpath3);
    }

    /**
     *
     */
    private void makeEdgesCount() {
        System.out.println("makeEdgesCount ");

        GiveDate gd = new GiveDate();
        long start_time = 0, total_time = 0;
        int total = BIC.size();//10;//
        for (int i = 0; i < total; i++) {

            start_time = gd.giveMilli();

            ValueBlock vb = (ValueBlock) BIC.get(i);
            System.out.println("i " + i + "\t" + vb.genes.length);
            ArrayList samps = new ArrayList();
            //create sample matrix genes x motifs
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
                    System.out.print("notfound " + g);
                }
            }
            System.out.println("\ngenes " + vb.genes.length + "\t" + samps.size());
            double[][] sampsmat = MoreArray.convtodouble2DArray(samps, motif_data.data[0].length);
            //System.out.println("\n2D");

            if (sampsmat.length > 0) {
                //create motif binding probability sum profile across all genes
                double[] allsums = Matrix.columnSum(sampsmat);

                double[] avgsums = stat.norm(allsums, sampsmat.length);

                double[] TFcors = new double[allsums.length];
                double[] TFcors_allexp = new double[allsums.length];
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

                            int[] allexp = stat.createNaturalNumbers(1, expr_data.data[0].length);
                            ValueBlock vbTFtargs_allexp = new ValueBlock(MoreArray.ArrayListtoInt(TFtargets), allexp);
                            vbTFtargs_allexp.setDataAndMean(expr_data.data);

                            ArrayList TFdataar_allexp = new ArrayList();
                            for (int z = 0; z < allexp.length; z++) {
                                TFdataar_allexp.add(expr_data.data[(Integer) o][allexp[z] - 1]);
                            }
                            double[] TFdata_allexp = MoreArray.ArrayListtoDouble(TFdataar_allexp);
                            TFcors_allexp[k] = stat.correlationCoeff(mean, TFdata);


                            double[] meanTFtargs = stat.norm(Matrix.columnSum(vbTFtargs.exp_data), vbTFtargs.genes.length);
                            double[] meanTFtargs_allexp = stat.norm(Matrix.columnSum(vbTFtargs.exp_data), vbTFtargs.genes.length);

                            double corTFtargs = stat.correlationCoeff(meanTFtargs, TFdata);
                            double corRatio = TFcors[k] / corTFtargs;

                            double corTFtargs_allexp = stat.correlationCoeff(meanTFtargs_allexp, TFdata_allexp);
                            double corRatio_allexp = TFcors_allexp[k] / corTFtargs_allexp;

                            double corBictargs = stat.correlationCoeff(mean, meanTFtargs);
                            double corBictargRatio = TFcors[k] / corBictargs;

                            /*TODO count how many other biclusters correlated with TF (and perhaps no motif) */

                            String s1 = "cor " + i + "\t" + k + "\t" + TFname + "\t" + TFcors[k] + "\t" +
                                    corTFtargs + "\t" + corRatio + "\t" + corBictargs + "\t" + corBictargRatio + "\t" + corTFtargs_allexp + "\t" + corRatio_allexp;
                            expr_cors.add(s1);

                            /*if (i == 0) {
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

                //System.out.println("TFcors " + i);
                //MoreArray.printArray(TFcors);
                //binaryCountperGene(sampsmat, allsums, TFcors);
                try {
                    int[][] exps = new int[vb.exps.length][2];
                    int[] c = new int[vb.exps.length];
                    exps[0] = vb.exps;
                    exps[1] = MoreArray.initArray(c, 1);
                    binaryCountGenePairs(sampsmat, allsums, TFcors, exps);
                } catch (Exception e) {
                    System.out.println("makeEdgesCount exps length " + vb.exps.length);
                    e.printStackTrace();
                }
            }

            System.out.print("\n");
            total_time = gd.giveMilli();
            System.out.println("bicluster " + i + "\t" + ((total_time - start_time) / 1000.0) + " s");
            //}
        }


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

        String outpath1 = inBIC + suffix + "_TFedges.sif";
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

        Set setexp = exps2TF.entrySet();
        Iterator itexp = setexp.iterator();

        ArrayList outexp = new ArrayList();
        ArrayList outexpmat = new ArrayList();
        outexpmat.add(MoreArray.toString(expr_data.xlabels, "\t"));
        //average each TF pair by the number of biclusters it was found in, given criteria
        while (itexp.hasNext()) {
            Map.Entry cur = (Map.Entry) itexp.next();
            String l = (String) cur.getKey();
            String a = l.substring(0, l.indexOf("__"));
            String b = l.substring(l.indexOf("__") + 2);
            int[][] d = (int[][]) cur.getValue();
            String outexps = "";
            for (int k = 0; k < d[0].length; k++) {
                if (d[0][k] != 0)
                    //outexps += expr_data.xlabels[d[0][k] - 1] + "--" + d[1][k] + "**";
                    outexps += "e" + d[0][k] + "--" + d[1][k] + "**";
                else {
                    System.out.println("d[0][k] == 0 " + d[0][k]);
                    MoreArray.printArray(d[0]);
                }
            }

            outexp.add(a + "\t" + b + "\t" + outexps);

            String matout = a + "=" + b + "\t";
            for (int k = 0; k < expr_data.xlabels.length; k++) {
                int index = -1;
                try {
                    index = MoreArray.getArrayInd(d[0], k + 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (index != -1)
                    matout += "\t" + d[1][index];
                else
                    matout += "\t" + 0;
            }
            outexpmat.add(matout);
        }

        String outpath4 = inBIC + suffix + "_TFedges_max.txt";
        System.out.println("size " + outedgemax.size());
        System.out.println("writing " + outpath4);
        TextFile.write(outedgemax, outpath4);
        System.out.println("wrote " + outpath4);

        String outpath6 = inBIC + suffix + "_exps.txt";
        System.out.println("size " + outexp.size());
        System.out.println("writing " + outpath6);
        TextFile.write(outexp, outpath6);
        System.out.println("wrote " + outpath6);

        String outpath7 = inBIC + suffix + "_expsmat.txt";
        System.out.println("size " + outexpmat.size());
        System.out.println("writing " + outpath7);
        TextFile.write(outexpmat, outpath7);
        System.out.println("wrote " + outpath7);

        String outpath5 = inBIC + suffix + "_TFedges_max.sif";
        System.out.println("size " + outedgemaxsif.size());
        System.out.println("writing " + outpath5);
        TextFile.write(outedgemaxsif, outpath5);
        System.out.println("wrote " + outpath5);

        String outpath2 = inBIC + suffix + "_biclustermotifs.txt";
        System.out.println("size " + biclusterMotifs.size());
        System.out.println("writing " + outpath2);
        TextFile.write(biclusterMotifs, outpath2);
        System.out.println("wrote " + outpath2);

        String outpath3 = inBIC + suffix + "_cors.txt";
        System.out.println("size " + expr_cors.size());
        System.out.println("writing " + outpath3);
        TextFile.write(expr_cors, outpath3);
        System.out.println("wrote " + outpath3);
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
            int index1 = 0;
            try {
                index1 = MoreArray.getArrayInd(ar[0], curall);
            } catch (Exception e) {
                e.printStackTrace();
            }
            int index2 = 0;
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
            MotifNetwork rm = new MotifNetwork(args);
        } else {
            System.out.println("syntax: java DataMining.func.MotifNetwork\n" +
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
