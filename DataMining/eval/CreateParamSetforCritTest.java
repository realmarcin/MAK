package DataMining.eval;

import DataMining.*;
import util.MoreArray;

import java.io.File;
import java.util.ArrayList;

/**
 * Class to create a series of parameter files to test all criteria combinations.
 * <p/>
 * User: marcin
 * Date: May 15, 2009
 * Time: 4:08:46 PM
 */
public class CreateParamSetforCritTest {


    int[] random_seeds = MINER_STATIC.RANDOM_SEEDS;

    /**
     *
     */
    public CreateParamSetforCritTest(String[] args) {

        String outdir = args[2];
        File test = new File(outdir);
        if (!test.exists() || !test.isDirectory()) {
            boolean create = test.mkdirs();
            System.out.println("Created new file with outcome " + create);
        }
        Parameters prm = new Parameters();
        prm.read(args[0], false);

        ValueBlockList vbl = null;
        try {
            vbl = ValueBlockListMethods.readAny(args[1], false);
        } catch (Exception e) {
            e.printStackTrace();
        }


        String preprefix = "";
        //synth_c_const_nono_rand_abs
        int ind = args[0].indexOf("_parameters.txt");
        preprefix = args[0].substring(0, ind);

        for (int i = 0; i < MINER_STATIC.CRIT_LABELS.length; i++) {

            if (MINER_STATIC.CRIT_LABELS[i].indexOf("MAXTF") == -1 &&
                    MINER_STATIC.CRIT_LABELS[i].indexOf("feat") == -1 &&
                    MINER_STATIC.CRIT_LABELS[i].indexOf("inter") == -1 &&
                    MINER_STATIC.CRIT_LABELS[i].indexOf("LARS") == -1 &&
                    MINER_STATIC.CRIT_LABELS[i].indexOf("MEAN") == -1 &&
                    MINER_STATIC.CRIT_LABELS[i].indexOf("invert") == -1 &&
                    MINER_STATIC.CRIT_LABELS[i].indexOf("Binary") == -1) {
                for (int j = 0; j < random_seeds.length; j++) {
                    for (int k = 0; k < vbl.size(); k++) {
                        Parameters curp = new Parameters(prm);
                        ValueBlock vb = (ValueBlock) vbl.get(k);
                        String s = MoreArray.toString(vb.genes, ",") + "/" + MoreArray.toString(vb.exps, ",");
                        System.out.println("start " + s);
                        ArrayList pass = new ArrayList();
                        pass.add(s);

                        curp.N_BLOCK = 1;
                        curp.init_block_list = null;// new ArrayList();
                        curp.INIT_BLOCKS = new ArrayList[2];

                        curp.INIT_BLOCKS[0] = MoreArray.addElements(curp.INIT_BLOCKS[0], vb.genes);//vb.genes;
                        curp.INIT_BLOCKS[1] = MoreArray.addElements(curp.INIT_BLOCKS[1], vb.exps);//vb.exps;
                        //prm.init_block_list.add(prm.INIT_BLOCKS);//pass;

                        curp.USE_RANDOM_SEED = false;
                        curp.RANDOM_SEED = random_seeds[j];
                        curp.OUTPREFIX = preprefix + "_" + k + "__" + MINER_STATIC.CRIT_LABELS[i] + "__" + curp.RANDOM_SEED;
                        curp.CRIT_TYPE_INDEX = i + 1;
                        curp.PRECRIT_TYPE_INDEX = -1;
                        curp.SIZE_PRECRIT_LIST = -1;

                        //System.out.println("start "+k+"\tcrit "+i+"\trand "+j);
                        //System.out.println(curp.toString());

                        String outfile = outdir + "/" + curp.OUTPREFIX + "_parameters.txt";
                        curp.write(outfile);
                        System.out.println("wrote " + outfile);
                    }
                }
            }
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 3) {
            CreateParamSetforCritTest rm = new CreateParamSetforCritTest(args);
        } else {
            System.out.println("syntax: java DataMining.eval.CreateParamSetforCritTest\n" +
                    "<parameter file>\n" +
                    "<starting points>\n" +
                    "<out dir>");
        }
    }
}
