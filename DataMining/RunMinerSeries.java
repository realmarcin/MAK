package DataMining;

import bioobj.YeastRegulons;
import mathy.SimpleMatrix;
import util.MapArgOptions;
import util.MoreArray;
import util.StringUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * User: marcin
 * Date: Mar 29, 2008
 * Time: 4:20:00 PM
 */
public class RunMinerSeries {
    RunMinerBack rmb;

    MapArgOptions map;
    HashMap options;
    String[] valid_args = {"-param", "-regulons", "-dataset", "-mode"};// "-list",
    String[] modes = {"regulons", "feature_by_precrit"};
    String mode = "regulons";

    String target_regulon = null;//"GAL4";//null;//"MCM1";//"GCN4";
    int regulon_subsamples = 1;
    int runs = 2;
    int gene_add = 0;
    int start = 0;

    int[] precrit_indices = {1, 2, 3};//MSE: total, col, row
    String[] precrit = {"MSE", "MSER", "MSEC", "MADR", "Kendall", "KendallC"};

    /*
    This gives a matrix, merging the data columns together
    FeatureData=cbind(objname1,objname2)
    this gives the indices of the featuredata matrix that are target_regulon variables.
    Ifactor=which(cbind(objname1_Ifac,objname2_Ifac)==1)
   */


    /**
     * Object is created based on a list of arguments.
     *
     * @param args
     */
    public RunMinerSeries(String[] args) {
        /*System.out.println("RunMinerSeries args");
        MoreArray.printArray(args);
        args = MapArgOptions.compactArgs(args);
        System.out.println("RunMinerSeries compactArgs");
        MoreArray.printArray(args);*/
        options = MapArgOptions.maptoMap(args, valid_args);

        String param = (String) options.get("-param");
        String regulons = (String) options.get("-regulons");
        String dataset = (String) options.get("-dataset");
        //String list = (String) options.get("-list");
        mode = (String) options.get("-mode");
        if (mode == null)
            mode = "regulons";

        System.out.println("RunMinerSeries " + param + "\t" + regulons + "\t" + dataset);// + "\t" + list);

        SimpleMatrix data = new SimpleMatrix(dataset);
        for (int i = 0; i < data.ylabels.length; i++) {
            data.ylabels[i] = StringUtil.replace(data.ylabels[i], "\"", "");
        }


        YeastRegulons yr = new YeastRegulons(regulons, data.ylabels, "MacIsaac");

        if (mode.equals("regulons")) {
            if (target_regulon != null) {
                int index = MoreArray.getArrayInd(yr.factors, target_regulon);
                //ArrayList regulon_list = yr.significant[index];
                //String[] gal4_ar = MoreArray.ArrayListtoString(factor_list);
                /*System.out.println("RunMinerSeries rmb.expr_matrix.ylabels");
                MoreArray.printArray(rmb.expr_matrix.ylabels);*/
                rmb = new RunMinerBack();
                rmb.init(param);
                try {
                    runOneSeriesWithRandom(yr.significant_int[index], param, rmb.orig_prm.IMAX);
                } catch (Exception e) {
                    System.out.println("yr");
                    try {
                        System.out.println(yr);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    System.out.println("param");
                    try {
                        System.out.println(param);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    System.out.println("rmb");
                    try {
                        System.out.println(rmb);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    e.printStackTrace();
                }
            } else {
                rmb = new RunMinerBack();
                rmb.init(param);
                for (int i = 0; i < yr.factors.length; i++) {
                    if (yr.significant_sizes[i] > 5 && yr.significant_sizes[i] < 95) {
                        runOne(yr.significant_int[i], yr.factors[i]);
                    }
                }
            }
        } /*else if (mode.equals("feature_by_precrit")) {
            int index = MoreArray.getArrayInd(yr.factors, target_regulon);
            //MoreArray.printArray(yr.factors);
            System.out.println(target_regulon + "\t" + index);
            runOneSeriesWithCombo(yr.significant_int[index], param);
        }*/

        System.exit(0);
    }

    /**
     * @param regulon_list_int
     * @param param
     */
    private void runOneSeriesWithRandom(ArrayList regulon_list_int, String param, int max) {
        ArrayList cur_genes = new ArrayList();
        Random random = new Random();
        rmb = new RunMinerBack();
        rmb.init(param);

        Parameters orig = new Parameters(rmb.orig_prm);
        //int gene_num = orig.INIT_BLOCKS[0].size();
        int total_genes = rmb.expr_matrix.data.length;
        //for the number of allowed genesStr in block
        for (int gene_num = max - start; gene_num > -1; gene_num--) {
            gene_add = max - gene_num;
            String outdir = target_regulon + "_" + gene_num + "/";
            File mkdir = new File(outdir);
            mkdir.mkdir();
            rmb.orig_prm.OUTDIR = outdir;
            //for the number of samples per regulon
            for (int j = 0; j < regulon_subsamples; j++) {
                System.out.println("RunMinerSeries doing regulon_subsamples " + j + " of " + regulon_subsamples);
                cur_genes.removeAll(cur_genes);
                cur_genes = addRandomRegulonMembers(cur_genes, gene_num, random, regulon_list_int);
                //cur_genes = addRandom(cur_genes, random, gene_num, total_genes);
                System.out.println("RunMinerSeries addRandomRegulonMembers, current total " + cur_genes.size());
                //if(rmb.orig_prm.init_block_list != null)
                cur_genes = addRandomNotRegulon(cur_genes, random, gene_add - cur_genes.size(), total_genes, regulon_list_int);
                System.out.println("RunMinerSeries addRandom, current total " + cur_genes.size());
                rmb.orig_prm.INIT_BLOCKS[0] = cur_genes;
                rmb.orig_prm.INIT_BLOCKS[1] = orig.INIT_BLOCKS[1];
                rmb.orig_prm.init_block_list.removeAll(rmb.orig_prm.init_block_list);
                rmb.orig_prm.init_block_list.add(rmb.orig_prm.INIT_BLOCKS);
                //for the number of runs per sample
                for (int k = 0; k < runs; k++) {
                    System.out.println("RunMinerSeries doing runOneSeriesWithRandom " + k + " of " + runs);
                    rmb.runSeries(null);
                }
            }
        }
    }

    /**
     * @param regulon_list_int
     * @param param
     */
    /*private void runOneSeriesWithCombo(ArrayList regulon_list_int, String param) {
        ArrayList cur_genes = new ArrayList();
        rmb = new RunMinerBack();
        rmb.fix_seed = true;
        rmb.init(param);
        Parameters orig = new Parameters(rmb.orig_prm);
        cur_genes.add(regulon_list_int);
        System.out.println("RunMinerSeries addRandom, current total " + cur_genes.size());
        rmb.orig_prm.INIT_BLOCKS[0] = cur_genes;
        rmb.orig_prm.INIT_BLOCKS[1] = orig.INIT_BLOCKS[1];
        rmb.orig_prm.init_block_list.removeAll(rmb.orig_prm.init_block_list);
        rmb.orig_prm.init_block_list.add(rmb.orig_prm.INIT_BLOCKS);
        int j = 1;//corresponds to true crit_axis_index =2
        for (int i = 0; i < MINER_STATIC.CRIT_LABELS.length; i++) {
            //for (int j = 0; j < rmb.orig_prm.max_crit_axis_index ; j++) {
            for (int k = 0; k < MINER_STATIC.FEATURES.length; k++) {
                ArrayList send = new ArrayList();
                send.add("" + (k + 1));
                rmb.init(param);
                rmb.orig_prm.CRIT_TYPE_INDEX = i + 1;
                //rmb.orig_prm.crit_axis_index = j + 1;
                String s = MINER_STATIC.FEATURES[k] + "_" + MINER_STATIC.PRECRITERIA[i] + "_" + MINER_STATIC.PRECRITERIA_AXIS[j];
                System.out.println("RunMinerSeries runOneSeriesWithCombo " + s);
                rmb.orig_prm.OUTPREFIX = s;
                rmb.runSeries(send);
            }
            //}
        }
    }
*/

    /**
     * @param regulon_list_int
     * @param curfact
     */
    private void runOne(ArrayList regulon_list_int, String curfact) {
        String outdir = curfact + "/";
        File mkdir = new File(outdir);
        mkdir.mkdir();
        rmb.orig_prm.OUTDIR = outdir;
        //for the number of samples per regulon
        for (int j = 0; j < regulon_subsamples; j++) {
            System.out.println("RunMinerSeries doing regulon_subsamples " + j + " of " + regulon_subsamples);
            rmb.orig_prm.INIT_BLOCKS[0] = regulon_list_int;
            rmb.orig_prm.INIT_BLOCKS[1] = rmb.orig_prm.INIT_BLOCKS[1];
            rmb.orig_prm.init_block_list.removeAll(rmb.orig_prm.init_block_list);
            rmb.orig_prm.init_block_list.add(rmb.orig_prm.INIT_BLOCKS);
            //for the number of runs per sample
            for (int k = 0; k < runs; k++) {
                System.out.println("RunMinerSeries doing runOneSeriesWithRandom " + k + " of " + runs);
                rmb.runSeries(null);
            }
        }
    }


    /**
     * @param cur_exp
     * @param r
     * @param gene_add
     * @param total_genes
     * @param regulon
     * @return
     */
    private ArrayList addRandomNotRegulon(ArrayList cur_exp, Random r, int gene_add, int total_genes, ArrayList regulon) {
        for (int j = 0; j < gene_add; j++) {
            int add = r.nextInt(total_genes - 1) + 1;
            //check if already added
            while (MoreArray.getArrayInd(cur_exp, add) != -1 || MoreArray.getArrayInd(regulon, add) != -1) {
                add = r.nextInt(total_genes - 1) + 1;
            }
            cur_exp.add(new Integer(add));
        }
        return cur_exp;
    }

    /**
     * @param cur_exp
     * @param r
     * @return
     */
    private ArrayList addRandomRegulonMembers(ArrayList cur_exp, int limit, Random r, ArrayList regulon) {
        int size = regulon.size();
        for (int m = 0; m < limit; m++) {
            System.out.println("RunMinerSeries addRandomRegulonMembers current " + m);
            int add = ((Integer) regulon.get(r.nextInt(size))).intValue();
            //int add = MoreArray.getArrayInd(rmb.expr_matrix.ylabels, str) + 1;
            System.out.println("RunMinerSeries addRandomRegulonMembers :" + add);
            //int add = str.intValue();
            //check if already added
            while (MoreArray.getArrayInd(cur_exp, add) != -1) {
                //System.out.println("RunMinerSeries addRandomRegulonMembers skipping " + add);
                add = ((Integer) regulon.get(r.nextInt(size))).intValue();
            }
            cur_exp.add(new Integer(add));
        }
        return cur_exp;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 6 || args.length == 8) {
            RunMinerSeries rm = new RunMinerSeries(args);
        } else {
            System.out.println("syntax: java DataMining.RunMinerSeries\n" +
                    "<-param 'parameter file'>\n" +
                    "<-regulons 'regulon file'>\n" +
                    "<-dataset data set>\n" +
                    //"<-list comma delim regulon list>\n" +
                    "<-mode (OPTIONAL) \"regulons\", \"feature_by_precrit\", default=\"regulons\">\n"
            );
        }
    }

}
