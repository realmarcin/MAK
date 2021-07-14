package DataMining.util;

import DataMining.ValueBlock;
import DataMining.ValueBlockList;
import DataMining.ValueBlockListMethods;
import mathy.SimpleMatrix;
import util.MapArgOptions;
import util.MoreArray;
import util.ParsePath;
import util.StringUtil;

import java.util.HashMap;

/**
 * Created by rauf on 5/7/15.
 */
public class ExtremeEnrichFilter {
    HashMap options;

    String[] valid_args = {
            "-bic", "-outfile", "-expr", "-extreme_threshold", "-ratio_threshold"
    };

    SimpleMatrix expr_data;
    int datagenemax, dataexpmax;
    String[] datageneids;
    String inBIC = null;
    ValueBlockList BIC;
    ValueBlockList sortedBIC;
    int bicluster_set_size;
    ParsePath pp = null;

    String outfile;
    double ratio_threshold = 1;
    double extreme_threshold = 2;

    /**
     * @param args
     */
    public ExtremeEnrichFilter(String[] args) {
        try {
            init(args);

            double denominator = computeDenom();

            int bcount = 0; // count for number of cells in bicluster sub matrix
            int bgreat = 0; // count for number of extreme value cells in bicluster sub matrix
            ValueBlockList final_list = new ValueBlockList();
            for (int i = 0; i < bicluster_set_size; i++) {

                ValueBlock v = (ValueBlock) sortedBIC.get(i);

                ValueBlock vb = (ValueBlock) sortedBIC.get(i);
                int[] vgenes = vb.genes;
                int[] vexps = vb.exps;

                for (int k = 0; k < vgenes.length; k++) {
                    int gene_index = vgenes[k] - 1;
                    for (int l = 0; l < vexps.length; l++) {
                        bcount++;
                        int exp_index = vexps[l] - 1;
                        if (Math.abs(expr_data.data[gene_index][exp_index]) > extreme_threshold) {
                            bgreat++;
                        }
                    }
                }

                // Calculate Log-odds ratio
                double numerator = (((double) bgreat) / ((double) bcount)); // frequency of extreme values in bicluster sub matrix
                double odds_ratio = Math.log(numerator / denominator) / Math.log(2);

                System.out.println(i + "\tLog-odds ratio (> " + extreme_threshold + ") :\t" +
                        odds_ratio + "\tnum " + numerator + "\tdenom " + denominator);

                if (odds_ratio >= ratio_threshold) {
                    final_list.add(v);
                } else {
                    break;
                }
            }

            // Write filtered VBL to file
            String out = final_list.toString(BIC.header);
            util.TextFile.write(out, outfile);

        } catch (Exception
                e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.exit(0);
    }

    /**
     * @return
     */
    private double computeDenom() {
        int great = 0; // count for number of extreme values in expression data matrix
        int count = 0; // count for number of cells in expression data matrix

        for (int i = 0; i < expr_data.data.length; i++) {
            for (int j = 0; j < expr_data.data[0].length; j++) {
                if (!Double.isNaN(expr_data.data[i][j])) {
                    count++;
                    if (Math.abs(expr_data.data[i][j]) > extreme_threshold) {
                        great++;
                    }
                }
            }
        }
        System.out.println("Denominator count, not freq:\t" + great);

        return (((double) great / (double) count));
    }

    /**
     * @param args
     */
    private void init(String[] args) {

        MoreArray.printArray(args);
        options = MapArgOptions.maptoMap(args, valid_args);

        if (options.get("-outfile") != null) {
            outfile = (String) options.get("-outfile");
        }

        if (options.get("-expr") != null) {
            String f = (String) options.get("-expr");
            expr_data = new SimpleMatrix(f);

            expr_data.xlabels = StringUtil.replace(expr_data.xlabels, "\"", "");
            expr_data.ylabels = StringUtil.replace(expr_data.ylabels, "\"", "");

            //genes
            datagenemax = expr_data.data.length;
            //exps
            dataexpmax = expr_data.data[0].length;

            datageneids = expr_data.ylabels;
        }

        if (options.get("-ratio_threshold") != null) {
            ratio_threshold = Double.parseDouble((String) options.get("-ratio_threshold"));
        }
        if (options.get("-extreme_threshold") != null) {
            extreme_threshold = Double.parseDouble((String) options.get("-extreme_threshold"));
        }

        if (options.get("-bic") != null) {
            inBIC = (String) options.get("-bic");
            try {
                BIC = ValueBlockListMethods.readAny(inBIC, false);
                sortedBIC = ValueBlockListMethods.sort("exp_mean", BIC); // descending sort on exp_mean
                //sortedBIC = ValueBlockListMethods.reverse(ValueBlockListMethods.sort("exp_mean", BIC)); // ascending sort on exp_mean
                bicluster_set_size = BIC.size();
            } catch (Exception e) {
                e.printStackTrace();
            }
            pp = new ParsePath(inBIC);
            if (BIC != null) {
                System.out.println("BIC.bicluster_set_size() " + BIC.size());
            }
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        System.out.println(args);
        if (args.length == 10) {
            ExtremeEnrichFilter rm = new ExtremeEnrichFilter(args);
        } else {
            System.out.println("syntax: java DataMining.util.ExtremeEnrichFilter\n" +
                    "<-expr>\n" +
                    "<-bic>\n" +
                    "<-ratio_threshold>\n" +
                    "<-extreme_threshold>\n" +
                    "<-outfile>\n"
            );
        }
    }
}