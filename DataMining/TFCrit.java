package DataMining;

import mathy.SimpleMatrix;
import mathy.stat;
import util.MoreArray;
import util.StringUtil;

import java.util.ArrayList;

/**
 * User: marcin
 * Date: Oct 20, 2010
 * Time: 4:49:50 PM
 */
public class TFCrit {

    public final static int MAX_NUM_TF = 2;

    /**
     * Computes the minimum avg. TF rank.
     * Assumes the input data is ranked in descending order, i.e. highest values ranked highest.
     * <p/>
     * Based on McCord et al.
     * 8mer binding ranks for 131 yeast TFs
     * Criterion is the normalized mean minimum mean rank across all TFs
     * Normalized by maximum mean rank
     * Considers the top 2 TFs
     *
     * @param genes
     * @param TFtargetmap
     * @param gene_labels
     * @param debug
     * @return
     */
    public final static double getTF8merRank(int[] genes, SimpleMatrix TFtargetmap, String[] gene_labels, boolean debug) {

        ArrayList data = new ArrayList();
        int notfound = 0;
        for (int i = 0; i < genes.length; i++) {
            int g = genes[i];
            //if (debug)
            //    System.out.println("getTF8merRank " + gene_labels[g] + "\t" + TFtargetmap.ylabels[1]);
            int index = -1;
            try {
                index = StringUtil.getFirstEqualsIndex(TFtargetmap.ylabels, gene_labels[g - 1]);
            } catch (Exception e) {
                if (gene_labels == null)
                    System.out.println("ERROR getTF8merRank gene_labels is null");
                else if (gene_labels[g - 1] == null)
                    System.out.println("ERROR getMinFractiongene_labels[g-1] is null");
                if (TFtargetmap == null)
                    System.out.println("ERROR getTF8merRank TFtargetmap is null");
                else if (TFtargetmap.ylabels == null)
                    System.out.println("ERROR getTF8merRank TFtargetmap.ylabels is null");
                e.printStackTrace();
                System.exit(0);
            }
//            if (debug)
//                System.out.println("getTF8merRank index " + index);

            if (index != -1) {
                data.add(TFtargetmap.data[index]);
//                if (debug)
                   // System.out.println("getTF8merRank added");
                   // System.out.println(TFtargetmap.data[index]);
            } else
                notfound++;
        }

        //data is rank-based so looking for minimum values
        double[] minranks = new double[MAX_NUM_TF];
        double maxrank = 0;
        if (data.size() > 0) {
            //mean values for TFs across that gene set


            double[] avg = stat.arraySampAvg(data);
            maxrank = stat.findMax(avg);
            //find min value

            /*
            System.out.println("AVERAGE");
            for (int r=0; r < avg.length; r++) {
                System.out.println(avg[r]);
            }
            */
            //System.out.println();
            minranks[0] = stat.findMin(avg);
            for (int i = 1; i < MAX_NUM_TF; i++) {
                //remove previous value from consideration (only valid for case = 2)
                int index = MoreArray.getArrayInd(avg, minranks[i - 1]);
                while (index != -1) {
                    avg = MoreArray.remove(avg, index);
                    index = MoreArray.getArrayInd(avg, minranks[i - 1]);
                }
                if (avg.length == 0) {
                    if (debug)
                        System.out.println("all TF ranks removed");
                    break;
                }
                minranks[i] = stat.findMin(avg);
            }
        }

        if (debug)
            if (notfound != 0)
                System.out.println("getTF8merRank notfound " + notfound + " out of " + genes.length);

        double v = 1.0 - stat.avg(minranks);
        v = !Double.isNaN(v) ? v : 0;
        //System.out.println("getTF8merRank " + v + "\tn= " + data.size());
        return v;
    }


    /**
     * Computes the minimum avg. TF rank.
     * Assumes the input data is ranked in descending order, i.e. highest values ranked highest.
     * <p/>
     * Based on McCord et al.
     * 8mer binding ranks for 131 yeast TFs
     * Criterion is the normalized mean minimum mean rank across all TFs
     * Normalized by maximum mean rank
     * Considers the top 2 TFs
     *
     * @param genes
     * @param TFtargetmap
     * @param gene_labels
     * @param debug
     * @return
     */
    public final static ArrayList getTF8merRankList(int[] genes, SimpleMatrix TFtargetmap, String[] gene_labels, boolean debug) {
        ArrayList data = new ArrayList();
        int notfound = 0;
        for (int i = 0; i < genes.length; i++) {
            int g = genes[i];
            /*if (debug)
                System.out.println("getTF8merRank " + gene_labels[g] + "\t" + TFtargetmap.ylabels[1]);*/
            int index = -1;
            try {
                index = StringUtil.getFirstEqualsIndex(TFtargetmap.ylabels, gene_labels[g - 1]);
            } catch (Exception e) {
                if (gene_labels == null)
                    System.out.println("ERROR getTF8merRank gene_labels is null");
                else if (gene_labels[g - 1] == null)
                    System.out.println("ERROR getMinFractiongene_labels[g-1] is null");
                if (TFtargetmap == null)
                    System.out.println("ERROR getTF8merRank TFtargetmap is null");
                else if (TFtargetmap.ylabels == null)
                    System.out.println("ERROR getTF8merRank TFtargetmap.ylabels is null");
                e.printStackTrace();
                System.exit(0);
            }
//            if (debug)
//                System.out.println("getTF8merRank index " + index);
            if (index != -1) {
                data.add(TFtargetmap.data[index]);
//                if (debug)
//                    System.out.println("getTF8merRank added");
            } else
                notfound++;
        }


        //data is rank-based so looking for minimum values
        double[] minranks = new double[MAX_NUM_TF];
        int[] minindex = new int[MAX_NUM_TF];
        double maxrank = 0;
        if (data.size() > 0) {
            //mean values for TFs across that gene set
            double[] avg = stat.arraySampAvg(data);
            maxrank = stat.findMax(avg);
            //find min value
            minranks[0] = stat.findMin(avg);
            minindex[0] = stat.findMinIndex(avg);
            //System.out.println("getTF8merRankList 0 " + minranks[0] + "\t" + minindex[0]);
            for (int i = 1; i < MAX_NUM_TF; i++) {
                //remove previous value from consideration (only valid for case >= 2)
                int index = MoreArray.getArrayInd(avg, minranks[i - 1]);
                while (index != -1) {
                    avg = MoreArray.remove(avg, index);
                    index = MoreArray.getArrayInd(avg, minranks[i - 1]);
                }
                minranks[i] = stat.findMin(avg);
                minindex[i] = stat.findMinIndex(avg);
                //System.out.println("getTF8merRankList " + i + "\t" + minranks[i] + "\t" + minindex[i]);
            }
        }
        //System.out.println("getTF8merRank " + min + "\tn= " + data.size());
        if (debug)
            if (notfound != 0)
                System.out.println("getTF8merRank notfound " + notfound + " out of " + genes.length);

        double v = 1.0 - stat.avg(minranks);// / maxrank;
        v = !Double.isNaN(v) ? v : 0;
        ArrayList ret = new ArrayList();
        ret.add(v);
        for (int i = 0; i < MAX_NUM_TF; i++) {
            System.out.println("getTF8merRank " + i + "\t" + minindex[i]);
            ret.add(TFtargetmap.xlabels[minindex[i]]);
        }
        return ret;
    }
}
