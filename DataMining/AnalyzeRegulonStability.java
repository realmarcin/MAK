package DataMining;

import mathy.stat;
import util.MoreArray;
import util.TabFile;
import util.TextFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * User: marcin
 * Date: May 26, 2008
 * Time: 3:40:16 PM
 */
public class AnalyzeRegulonStability {

    String[] MacIsaac_regulons;

    String[] outlabels = {
            "target_regulon",
            "num_genes",
            "mean_block_area",
            "sd_block_area",
            "mean_full_criterion",
            "sd_full_criterion",
            "mean_pre_criterion",
            "sd_pre_criterion",
            "mean_percentOrigExp",
            "sd_percentOrigExp",
            "mean_percentOrigGenes",
            "sd_percentOrigGenes",
            "mean_moves",
            "sd_moves",
            "mean_areadiff",
            "sd_areadiff",
            "mean_fulldiff",
            "sd_fulldiff",
            "mean_prediff",
            "sd_prediff",
            "mean_overlap_rootproduct",
            "sd_block_overlap_rootproduct",
            "mean_block_overlap_min",
            "sd_block_overlap_min"
    };


    /**
     * @param args
     */
    public AnalyzeRegulonStability(String[] args) {

        String indir = args[0];
        File in = new File(indir);
        String[] factors = TextFile.readtoArray(args[1]);
        String outdir = args[2];
        String[] list = in.list();

        String[][] out_data = new String[factors.length][outlabels.length];

        runStats(indir, factors, outdir, list, out_data);

    }

    /**
     * @param indir
     * @param factors
     * @param outdir
     * @param list
     * @param out_data
     */
    private void runStats(String indir, String[] factors, String outdir, String[] list, String[][] out_data) {

        String[] occup_data = new String[factors.length];
        String[] occup_labels = new String[factors.length];

        for (int i = 0; i < factors.length; i++) {
            System.out.print(".");

            double[] gene_occupancy = null;
            String[] gene_labels = null;

            ArrayList factor_samples = new ArrayList();
            for (int j = 0; j < list.length; j++) {
                //System.out.println(list[j]+"\n"+factors[i]);
                if (list[j].indexOf(factors[i]) == 0) {
                    //System.out.println("adding to list");
                    try {
                        factor_samples.add(ValueBlockList.read(indir + "/" + list[j], false));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            ArrayList areatmp = new ArrayList();
            ArrayList fulltmp = new ArrayList();
            ArrayList pretmp = new ArrayList();
            ArrayList OrigExptmp = new ArrayList();
            ArrayList OrigGenestmp = new ArrayList();
            ArrayList movestmp = new ArrayList();
            ArrayList areadifftmp = new ArrayList();
            ArrayList fulldifftmp = new ArrayList();
            ArrayList predifftmp = new ArrayList();
            ArrayList block_overlap = new ArrayList();
            ArrayList gene_overlap = new ArrayList();

            double numtmp = Double.NaN;
            for (int j = 0; j < factor_samples.size(); j++) {
                ValueBlockList vbl = (ValueBlockList) factor_samples.get(j);
                int k = vbl.size() - 1;
                ValueBlock vb = (ValueBlock) vbl.get(k);
                ValueBlock vbfirst = (ValueBlock) vbl.get(0);
                areatmp.add(new Double(vb.block_area));
                fulltmp.add(new Double(vb.pre_criterion));
                pretmp.add(new Double(vbfirst.pre_criterion));
                OrigExptmp.add(new Double(vb.percentOrigExp));
                OrigGenestmp.add(new Double(vb.percentOrigGenes));
                movestmp.add(new Double(vbl.size()));
                areadifftmp.add(new Double(((double) vb.block_area - (double) vbfirst.block_area) / (double) vbfirst.block_area));
                fulldifftmp.add(new Double(vb.full_criterion - vbfirst.full_criterion));
                predifftmp.add(new Double(vb.pre_criterion - vbfirst.pre_criterion));

                double compare_rootproduct = BlockMethods.computeBlockOverlapGeneRootProduct(vbfirst, vb);
                block_overlap.add(new Double(compare_rootproduct));

                double compare_min = BlockMethods.computeBlockOverlapGeneMin(vbfirst, vb, false);
                gene_overlap.add(new Double(compare_min));

                if (Double.isNaN(numtmp)) {
                    numtmp = vbfirst.genes.length;
                }
                if (gene_occupancy == null) {
                    gene_occupancy = MoreArray.initArray(vbfirst.genes.length, 0.0);
                    gene_labels = MoreArray.toStringArray(vbfirst.genes);//MoreArray.initArray(vbfirst.genes.length, null);
                }
                gene_occupancy = BlockMethods.getGeneOccupancy(vbfirst, vb, gene_occupancy);
            }
            gene_occupancy = stat.norm(gene_occupancy, factor_samples.size());
            Arrays.sort(gene_occupancy);
            occup_data[i] = MoreArray.toString(gene_occupancy);
            ArrayList tmp = MoreArray.convtoArrayList(MoreArray.toStringArray(gene_occupancy));
            tmp.add(0, factors[i]);
            tmp.add("-");
            String[] str = MoreArray.ArrayListtoString(tmp);
            occup_data[i] = MoreArray.toString(str);
            ArrayList tmp2 = MoreArray.convtoArrayList(gene_labels);
            tmp2.add(0, factors[i]);
            occup_labels[i] = MoreArray.toString(MoreArray.ArrayListtoString(tmp2));

            out_data[i][0] = factors[i];
            out_data[i][1] = "" + numtmp;

            int offset = 2;

            double[] areas = MoreArray.ArrayListtoDouble(areatmp);
            double mean_area = stat.avg(areas);
            out_data[i][0 + offset] = "" + mean_area;
            out_data[i][1 + offset] = "" + stat.SD(areas, mean_area);

            double[] fulls = MoreArray.ArrayListtoDouble(fulltmp);
            double mean_fulls = stat.avg(fulls);
            out_data[i][2 + offset] = "" + mean_fulls;
            out_data[i][3 + offset] = "" + stat.SD(fulls, mean_fulls);

            double[] pres = MoreArray.ArrayListtoDouble(pretmp);
            double mean_pres = stat.avg(pres);
            out_data[i][4 + offset] = "" + mean_pres;
            out_data[i][5 + offset] = "" + stat.SD(pres, mean_pres);

            double[] exps = MoreArray.ArrayListtoDouble(OrigExptmp);
            double mean_exps = stat.avg(exps);
            out_data[i][6 + offset] = "" + mean_exps;
            out_data[i][7 + offset] = "" + stat.SD(exps, mean_exps);

            double[] genes = MoreArray.ArrayListtoDouble(OrigGenestmp);
            double mean_genes = stat.avg(genes);
            out_data[i][8 + offset] = "" + mean_genes;
            out_data[i][9 + offset] = "" + stat.SD(genes, mean_genes);

            double[] moves = MoreArray.ArrayListtoDouble(movestmp);
            double mean_move = stat.avg(moves);
            out_data[i][10 + offset] = "" + mean_move;
            out_data[i][11 + offset] = "" + stat.SD(moves, mean_move);

            double[] areadiffs = MoreArray.ArrayListtoDouble(areadifftmp);
            double mean_areadiff = stat.avg(areadiffs);
            out_data[i][12 + offset] = "" + mean_areadiff;
            out_data[i][13 + offset] = "" + stat.SD(areadiffs, mean_areadiff);

            double[] fulldiffs = MoreArray.ArrayListtoDouble(fulldifftmp);
            double mean_fulldiff = stat.avg(fulldiffs);
            out_data[i][14 + offset] = "" + mean_fulldiff;
            out_data[i][15 + offset] = "" + stat.SD(fulldiffs, mean_fulldiff);

            double[] prediffs = MoreArray.ArrayListtoDouble(predifftmp);
            double mean_prediff = stat.avg(prediffs);
            out_data[i][16 + offset] = "" + mean_prediff;
            out_data[i][17 + offset] = "" + stat.SD(prediffs, mean_prediff);

            double[] overlaps = MoreArray.ArrayListtoDouble(block_overlap);
            double mean_overlap = stat.avg(overlaps);
            out_data[i][18 + offset] = "" + mean_overlap;
            out_data[i][19 + offset] = "" + stat.SD(overlaps, mean_overlap);

            double[] gene_overlaps = MoreArray.ArrayListtoDouble(gene_overlap);
            double mean_gene_overlap = stat.avg(gene_overlaps);
            out_data[i][20 + offset] = "" + mean_gene_overlap;
            out_data[i][21 + offset] = "" + stat.SD(gene_overlaps, mean_gene_overlap);
        }

        Random rand = new Random();
        int i = Math.abs(rand.nextInt());
        String outf = "factors_summary_" + i + ".txt";
        TabFile.write(out_data, outlabels, outdir + "/" + outf);
        System.out.println("\n\nwrite " + outf);

        String outf2 = "factors_gene_occupancy_" + i + ".txt";
        TabFile.write(occup_data, outdir + "/" + outf2);
        System.out.println("write " + outf2);
        String outf3 = "factors_gene_occupancy_labels_" + i + ".txt";
        TabFile.write(occup_labels, outdir + "/" + outf3);
        System.out.println("write " + outf3);

    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 3) {
            AnalyzeRegulonStability arg = new AnalyzeRegulonStability(args);
        } else {
            System.out.println("syntax: java DataMining.AnalyzeRegulonStability\n" +
                    "<dir of toplists'>\n" +
                    "<list of TFs>\n" +
                    "<OUTDIR>"
            );
        }
    }
}
