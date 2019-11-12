package DataMining;

import util.MoreArray;
import util.Program;
import util.TabFile;

import java.util.Random;

/**
 * User: marcin
 * Date: Feb 11, 2008
 * Time: 1:01:29 PM
 */
public class CreateBlockSets extends Program {
    String outdir;
    Parameters prm;
    int runs = 10;
    int[] size_precrit_list = {10, 20, 30, 50, 200};
    int maxMoves = 30;

    double[][] data;
    int data_rows, data_cols;

    public final static String[] blocks = {
            "1,2,3,4,5/1,2,3,4,5",
            "1,2,3,4,5,6/1,2,3,4,5,6",
            "1,2,3,4,5,6,7/1,2,3,4,5,6,7",
            "1,2,3,4,5,6,7,8/1,2,3,4,5,6,7,8",
            "1,2,3,4,5,6,7,8,9/1,2,3,4,5,6,7,8,9",
            "1,2,3,4,5,6,7,8,9,10/1,2,3,4,5,6,7,8,9,10"
    };

    int blocks_index = 2;

    /*
    * @param args
    */
    public CreateBlockSets(String[] args) {
        super();
        prm = new Parameters();
        prm.read(args[0], false);
        runs = Integer.parseInt(args[1]);
        prm.MAXMOVES[0] = maxMoves;
        data = TabFile.readtoDoubleArray(args[2]);
        data_rows = data.length;
        data_cols = data[0].length;
        outdir = args[3];

        Random r = new Random();
        for (int i = 0; i < size_precrit_list.length; i++) {
            prm.SIZE_PRECRIT_LIST = size_precrit_list[i];
            prm.RANDOM_SEED = size_precrit_list[i];
            for (int j = 0; j < runs; j++) {
                int seed = r.nextInt(1000000);
                prm.RANDOM_SEED = seed;
                //for (int j = 0; j < blocks.length; j++) {
                ValueBlock vb = new ValueBlock(blocks[blocks_index]);
                vb = BlockMethods.createRandomBlock(vb, data_rows, data_cols);
                prm.INIT_BLOCKS = BlockMethods.IcJcLabelstoijID(MoreArray.toStringArray(vb.genes), MoreArray.toStringArray(vb.exps));
                prm.write(outdir + sysRes.file_separator + "pre" + prm.SIZE_PRECRIT_LIST + "_run" +
                        j + "_seed" + seed + "_rows" + vb.genes.length + "_cols" + vb.exps.length + "_parameters.txt");
                //}
            }
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 4) {
            CreateBlockSets rm = new CreateBlockSets(args);
        } else {
            System.out.println("syntax: java DataMining.CreateBlockSets " +
                    "<parameter file>\n" +
                    "<number of runs>\n" +
                    "<data file>\n" +
                    "<out dir>");
        }
    }

}
