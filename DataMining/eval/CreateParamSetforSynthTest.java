package DataMining.eval;

import DataMining.MINER_STATIC;
import DataMining.Parameters;
import DataMining.ValueBlock;
import util.MoreArray;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

/**
 * User: mjoachimiak
 * Date: Jun 27, 2009
 * Time: 11:11:36 PM
 */
public class CreateParamSetforSynthTest {

    String outdir;
    int total_genes = 100;
    int total_exps = 40;
    int[] genes_index, exps_index;

    int start_crit = 1;
    int num_crit = MINER_STATIC.CRIT_LABELS.length + 1;//1+ actual number so can serve as literal limit
    String mode;

    int trials = 10;
    double factor = 0.75;

    public boolean true1 = true;

    /**
     *
     */
    public CreateParamSetforSynthTest(String[] args) {

        mode = args[0];
        outdir = args[2];

        File test = new File(outdir);
        if (!test.exists() || !test.isDirectory()) {
            boolean create = test.mkdirs();
            System.out.println("Created new file with outcome " + create);
        }
        Parameters prm = new Parameters();
        if (!mode.equals("reftranspose"))
            prm.read(args[1]);

        ArrayList[] indices = null;
        if (genes_index == null) {
            indices = (ArrayList[]) prm.init_block_list.get(0);
            genes_index = MoreArray.ArrayListtoInt(indices[0]);
            exps_index = MoreArray.ArrayListtoInt(indices[1]);
        }

        if (mode.equals("basic")) {
            doBasic(outdir, prm, indices);
        } else if (mode.equals("random")) {
            doRandom(outdir, prm, indices);
        } else if (mode.equals("reftranspose")) {
            doRefTranspose(outdir, args[1]);
        }
    }

    /**
     * @param outdir
     * @param refdir
     */
    private void doRefTranspose(String outdir, String refdir) {
        File dir = new File(refdir);
        String[] list = dir.list();
        for (int i = 0; i < list.length; i++) {
            Parameters prm = new Parameters();
            prm.read(refdir + "/" + list[i]);
            ValueBlock cur = new ValueBlock(prm.INIT_BLOCKS);
            prm.INIT_BLOCKS = MoreArray.initArrayListArray(2);
            prm.INIT_BLOCKS[0] = MoreArray.addElements(prm.INIT_BLOCKS[0], cur.exps);
            prm.INIT_BLOCKS[1] = MoreArray.addElements(prm.INIT_BLOCKS[1], cur.genes);
            prm.write(outdir + "/" + list[i]);
        }
    }

    /**
     * @param outdir
     * @param prm
     * @param indices
     */
    private void doRandom(String outdir, Parameters prm, ArrayList[] indices) {

        ArrayList bgenes = new ArrayList();
        bgenes = MoreArray.addElements(bgenes, genes_index);
        /*ValueBlock t1 = new ValueBlock(AnalyzeSynthTest.trueblock1);
        ValueBlock t2 = new ValueBlock(AnalyzeSynthTest.trueblock2);
        if (Arrays.equals(t1.genes, genes_index)) {
            true1 = true;
            bgenes = MoreArray.addElements(bgenes, t2.genes);
        } else if (Arrays.equals(t2.genes, genes_index)) {
            true1 = false;
            bgenes = MoreArray.addElements(bgenes, t1.genes);
        }
        ArrayList bexps = new ArrayList();
        bgenes = MoreArray.addElements(bexps, exps_index);
        if (Arrays.equals(t1.exps, exps_index)) {
            true1 = true;
            bexps = MoreArray.addElements(bexps, t2.exps);
        } else if (Arrays.equals(t2.exps, exps_index)) {
            true1 = false;
            bexps = MoreArray.addElements(bexps, t1.exps);
        }*/
        //int[] omitexparray = MoreArray.ArrayListtoInt(bexps);
        //int[] moreexps = mathy.stat.createNaturalNumbersRemoved(1, total_exps, omitexparray);
        //System.out.println("moreexps " + moreexps.length);
        //MoreArray.printArray(moreexps);


        int replace_genes = (int) (factor * (double) genes_index.length);
        int replace_exps = (int) (factor * (double) exps_index.length);
        System.out.println("replace " + replace_genes + "\t" + replace_exps);
        for (int m = 0; m < trials; m++) {
            Random rand = new Random();
            Random rand2 = new Random();
            Random rand3 = new Random();
            Random rand4 = new Random();
            ArrayList half_random_genes = MoreArray.copyArrayList(indices[0]);
            //String[] cur = MoreArray.ArrayListtoString(half_random_genes);

            int num_block_genes = genes_index.length;
            int[] donegenes = new int[total_genes];
            for (int a = 0; a < genes_index.length; a++) {
                donegenes[genes_index[a] - 1] = 1;
            }
            int countg = 0;
            int[] donegenespos = new int[num_block_genes];
            for (int i = 0; i < replace_genes; i++) {
                //pick random position in array
                int whichnew = rand.nextInt(total_genes);
                //pick random gene
                int pos = rand2.nextInt(num_block_genes);
                while (donegenespos[pos] == 1) {
                    pos = rand2.nextInt(num_block_genes);
                }
                int index = MoreArray.getArrayInd(half_random_genes, whichnew + 1);
                //System.out.println("g " + whichnew + "\t" + pos + "\t" + index);
                while (donegenes[whichnew] != 0 || index != -1) {
                    //System.out.println("gn " + whichnew + "\t" + pos + "\t" + index);
                    whichnew = rand.nextInt(total_genes);
                    index = MoreArray.getArrayInd(half_random_genes, whichnew + 1);
                }
                System.out.println("replacing " + countg + "\t" + pos + "\t" + half_random_genes.get(pos) + "\t" + (whichnew + 1));
                half_random_genes.set(pos, new Integer(whichnew + 1));
                donegenespos[pos] = 1;
                donegenes[whichnew] = 1;
                countg++;
            }
            int[] addgenesint = MoreArray.ArrayListtoInt(half_random_genes);
            System.out.println("genes " + countg);
            MoreArray.printArray(addgenesint);

            ArrayList half_random_exps = MoreArray.copyArrayList(indices[1]);
            //String[] cur2 = MoreArray.ArrayListtoString(half_random_exps);

            int num_block_exps = exps_index.length;
            int[] doneexps = new int[total_exps];
            for (int a = 0; a < exps_index.length; a++) {
                doneexps[exps_index[a] - 1] = 1;
            }
            int counte = 0;
            int[] doneexpspos = new int[num_block_genes];
            for (int i = 0; i < replace_exps; i++) {
                int whichnew = rand3.nextInt(total_exps);
                int pos = rand4.nextInt(num_block_exps);
                while (doneexpspos[pos] == 1) {
                    pos = rand2.nextInt(num_block_exps);
                }
                int index = MoreArray.getArrayInd(half_random_exps, whichnew + 1);
                //System.out.println("e " + whichnew + "\t" + pos + "\t" + index);
                while (doneexps[whichnew] != 0 || index != -1) {
                    //System.out.println("en " + whichnew + "\t" + pos + "\t" + index);
                    whichnew = rand3.nextInt(total_exps);
                    index = MoreArray.getArrayInd(half_random_exps, whichnew + 1);
                }
                System.out.println("replacing " + counte + "\t" + pos + "\t" + half_random_exps.get(pos) + "\t" + (whichnew + 1));
                half_random_exps.set(pos, new Integer(whichnew + 1));
                doneexpspos[pos] = 1;
                doneexps[whichnew] = 1;
                counte++;
            }
            int[] addexpsint = MoreArray.ArrayListtoInt(half_random_exps);
            System.out.println("exps " + counte);
            MoreArray.printArray(addexpsint);

            for (int i = start_crit; i < num_crit; i++) {
                Parameters curp = new Parameters(prm);
                curp.INIT_BLOCKS = MoreArray.initArrayListArray(2);
                curp.INIT_BLOCKS[0] = MoreArray.addElements(curp.INIT_BLOCKS[0], addgenesint);
                curp.INIT_BLOCKS[1] = MoreArray.addElements(curp.INIT_BLOCKS[1], addexpsint);
                curp.RANDOM_SEED = MINER_STATIC.RANDOM_SEEDS[m];
                //for (int j = 0; j < random_seeds.length; j++) {
                curp.OUTPREFIX = curp.OUTPREFIX + "_" + MINER_STATIC.CRIT_LABELS[i - 1] + "__" + m + "_0.5random_";
                curp.CRIT_TYPE_INDEX = i;
                String outfile = outdir + "/" + curp.OUTPREFIX + "_parameters.txt";
                curp.write(outfile);
                System.out.println("wrote " + outfile);
                //}
            }
        }
    }

    /**
     * @param outdir
     * @param prm
     * @param indices
     */
    private void doBasic(String outdir, Parameters prm, ArrayList[] indices) {
        ArrayList addgenes = MoreArray.copyArrayList(indices[0]);
        int[] moregenes = mathy.stat.createNaturalNumbersRemoved(1, total_genes, genes_index);
        addgenes.add(new Integer(moregenes[0]));
        int[] addgenesint = MoreArray.ArrayListtoInt(addgenes);

        ArrayList addexps = MoreArray.copyArrayList(indices[1]);
        int[] moreexps = mathy.stat.createNaturalNumbersRemoved(1, total_exps, exps_index);
        addexps.add(new Integer(moreexps[0]));
        int[] addexpsint = MoreArray.ArrayListtoInt(addexps);

        ArrayList delgenes = MoreArray.copyArrayList(indices[0]);
        delgenes.remove(delgenes.size() - 1);
        int[] delgenesint = MoreArray.ArrayListtoInt(delgenes);

        ArrayList delexps = MoreArray.copyArrayList(indices[1]);
        delexps.remove(delexps.size() - 1);
        int[] delexpsint = MoreArray.ArrayListtoInt(delexps);

        for (int i = start_crit; i < num_crit; i++) {
            Parameters curp = new Parameters(prm);
            //for (int j = 0; j < random_seeds.length; j++) {
            System.out.println(curp.OUTPREFIX);
            curp.OUTPREFIX = curp.OUTPREFIX + "_" + MINER_STATIC.CRIT_LABELS[i - 1] + "__drift_";
            curp.CRIT_TYPE_INDEX = i;
            String outfile = outdir + "/" + curp.OUTPREFIX + "_parameters.txt";
            curp.write(outfile);
            System.out.println("wrote " + outfile);
            //}
        }
        for (int i = start_crit; i < num_crit; i++) {
            Parameters curp = new Parameters(prm);
            curp.INIT_BLOCKS = MoreArray.initArrayListArray(2);
            curp.INIT_BLOCKS[0] = MoreArray.addElements(curp.INIT_BLOCKS[0], addgenesint);
            curp.INIT_BLOCKS[1] = MoreArray.addElements(curp.INIT_BLOCKS[1], exps_index);

            //for (int j = 0; j < random_seeds.length; j++) {
            curp.OUTPREFIX = curp.OUTPREFIX + "_" + MINER_STATIC.CRIT_LABELS[i - 1] + "__geneadd_";
            curp.CRIT_TYPE_INDEX = i;
            String outfile = outdir + "/" + curp.OUTPREFIX + "_parameters.txt";
            curp.write(outfile);
            System.out.println("wrote " + outfile);
            //}
        }
        for (int i = start_crit; i < num_crit; i++) {
            Parameters curp = new Parameters(prm);
            curp.INIT_BLOCKS = MoreArray.initArrayListArray(2);
            curp.INIT_BLOCKS[0] = MoreArray.addElements(curp.INIT_BLOCKS[0], genes_index);
            curp.INIT_BLOCKS[1] = MoreArray.addElements(curp.INIT_BLOCKS[1], addexpsint);

            //for (int j = 0; j < random_seeds.length; j++) {
            curp.OUTPREFIX = curp.OUTPREFIX + "_" + MINER_STATIC.CRIT_LABELS[i - 1] + "__expadd_";
            curp.CRIT_TYPE_INDEX = i;
            String outfile = outdir + "/" + curp.OUTPREFIX + "_parameters.txt";
            curp.write(outfile);
            System.out.println("wrote " + outfile);
            //}
        }
        for (int i = start_crit; i < num_crit; i++) {
            Parameters curp = new Parameters(prm);
            curp.INIT_BLOCKS = MoreArray.initArrayListArray(2);
            curp.INIT_BLOCKS[0] = MoreArray.addElements(curp.INIT_BLOCKS[0], delgenesint);
            curp.INIT_BLOCKS[1] = MoreArray.addElements(curp.INIT_BLOCKS[1], exps_index);
            //for (int j = 0; j < random_seeds.length; j++) {
            curp.OUTPREFIX = curp.OUTPREFIX + "_" + MINER_STATIC.CRIT_LABELS[i - 1] + "__genedel_";
            curp.CRIT_TYPE_INDEX = i;
            String outfile = outdir + "/" + curp.OUTPREFIX + "_parameters.txt";
            curp.write(outfile);
            System.out.println("wrote " + outfile);
            //}
        }
        for (int i = start_crit; i < num_crit; i++) {
            Parameters curp = new Parameters(prm);
            curp.INIT_BLOCKS = MoreArray.initArrayListArray(2);
            curp.INIT_BLOCKS[0] = MoreArray.addElements(curp.INIT_BLOCKS[0], genes_index);
            curp.INIT_BLOCKS[1] = MoreArray.addElements(curp.INIT_BLOCKS[1], delexpsint);
            //for (int j = 0; j < random_seeds.length; j++) {
            curp.OUTPREFIX = curp.OUTPREFIX + "_" + MINER_STATIC.CRIT_LABELS[i - 1] + "__expdel_";
            curp.CRIT_TYPE_INDEX = i;
            String outfile = outdir + "/" + curp.OUTPREFIX + "_parameters.txt";
            curp.write(outfile);
            System.out.println("wrote " + outfile);
            //}
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 3) {
            CreateParamSetforSynthTest rm = new CreateParamSetforSynthTest(args);
        } else {
            System.out.println("syntax: java DataMining.eval.CreateParamSetforSynthTest\n" +
                    "<mode = \"basic\",\"random_sizes\",\"reftranspose\">\n" +
                    "<parameter file or dir>\n" +
                    "<out dir>"
            );
        }
    }
}
