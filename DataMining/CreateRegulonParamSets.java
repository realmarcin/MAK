package DataMining;

import bioobj.YeastRegulons;
import mathy.SimpleMatrix;
import util.MoreArray;
import util.StringUtil;
import util.TextFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;

/**
 * User: marcinjoachimiak
 * Date: May 24, 2008
 * Time: 9:49:07 PM
 */
public class CreateRegulonParamSets {

    String[] toplist;
    String toplist_dir;


    boolean sample = false;

    /**
     * @param args
     */
    public CreateRegulonParamSets(String[] args) {
        File testtemp = new File(args[0]);
        if (!testtemp.exists()) {
            System.out.println("parameter file does not exist");
            System.exit(0);
        }

        Parameters template = null;
        try {
            template = new Parameters(args[0]);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("CreateParamSetfromParamSet\n" + template);
        SimpleMatrix expr_matrix = new SimpleMatrix(args[3]);
        for (int i = 0; i < expr_matrix.ylabels.length; i++) {
            expr_matrix.ylabels[i] = StringUtil.replace(expr_matrix.ylabels[i], "\"", "");
        }
        int total_genes = expr_matrix.data.length;

        YeastRegulons yr = new YeastRegulons(args[1], expr_matrix.ylabels, "MacIsaac");
        String[] list = TextFile.readtoArray(args[2]);
        System.out.println("CreateParamSetfromParamSet list");
        MoreArray.printArray(list);
        String outdir = args[4];

        File test = new File(outdir);
        if (!test.exists() || !test.isDirectory()) {
            boolean create = test.mkdirs();
            System.out.println("Created new file with outcome " + create);
        }

        if (args.length == 6) {
            toplist_dir = args[5];
            File file = new File(toplist_dir);
            toplist = file.list();

        }

        Random random = new Random();

        //ValueBlock first = null;
        ArrayList cur_genes = new ArrayList();
        for (int i = 0; i < list.length; i++) {
            if (list[i].length() > 0) {
                int index = MoreArray.getArrayInd(yr.factors, list[i]);
                System.out.println(index + "\t:" + list[i] + ":");
                /*if (toplist != null) {
                    int topindex = MoreArray.getArrayIndexOfAtZero(toplist, list[i]);
                    if (topindex != -1) {
                        ValueBlockList vbl = ValueBlockList.read(toplist_dir + "/" + toplist[topindex]);
                        first = (ValueBlock) vbl.get(0);
                    }
                }*/
                if (sample) {
                    System.out.println("RunMinerSeries doing " + i + " of " + list.length);
                    cur_genes.removeAll(cur_genes);
                    //cur_genes = addRandomRegulonMembers(cur_genes, (int) (yr.significant_int[index].size() / 4.0),
                    //       random, yr.significant_int[index]);
                    //cur_genes = addRandom(cur_genes, random, gene_num, total_genes);
                    System.out.println("RunMinerSeries addRandomRegulonMembers sampled " + cur_genes.size());
                    MoreArray.printArray(MoreArray.ArrayListtoInt(cur_genes));
                    //if(rmb.prm.init_block_list != null)
                    cur_genes = addRandomNotRegulon(cur_genes, random,
                            yr.significant_int[index].size() - cur_genes.size(), total_genes, yr.significant_int[index]);
                }


                Parameters out = new Parameters(template);
                out.OUTPREFIX = list[i] + "_" + Math.abs(random.nextInt());
                out.init_block_list = new ArrayList();
                out.INIT_BLOCKS = new ArrayList[2];
                int[] genesInt = MoreArray.tointArray(yr.significant_int[index]);
                if (sample)
                    genesInt = MoreArray.ArrayListtoInt(cur_genes);
                /*int[] expsInt = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
                if (first != null)
                    expsInt = first.exps;*/
                out.INIT_BLOCKS[0] = MoreArray.convtoArrayList(genesInt);
                out.INIT_BLOCKS[1] = template.INIT_BLOCKS[1];
                out.init_block_list.add(out.INIT_BLOCKS);
                String outfile = outdir + "/" + list[i] + "_parameters.txt";
                System.out.println("outfile " + outfile);
                out.write(outfile);
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
                System.out.println("RunMinerSeries addRandomRegulonMembers skipping " + add);
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
        if (args.length == 5 || args.length == 6) {
            CreateRegulonParamSets rm = new CreateRegulonParamSets(args);
        } else {
            System.out.println("syntax: java DataMining.CreateRegulonParamSets\n" +
                    "<template parameter file>\n" +
                    "<regulon file>\n" +
                    "<regulon list>\n" +
                    "<data set>\n" +
                    "<OUTDIR>\n" +
                    "<dir of toplists (optional)>"
            );
        }
    }

}
