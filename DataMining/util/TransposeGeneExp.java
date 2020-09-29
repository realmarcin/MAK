package DataMining.util;

import DataMining.Parameters;
import DataMining.ValueBlock;
import util.MoreArray;

import java.io.File;

/**
 * User: marcin
 * Date: Aug 17, 2009
 * Time: 2:29:02 PM
 */
public class TransposeGeneExp {

    /**
     * @param args
     */
    public TransposeGeneExp(String[] args) {

        File dir = new File(args[0]);
        String[] list = dir.list();

        for (int i = 0; i < list.length; i++) {
            Parameters prm = new Parameters();
            prm.read(args[0] + "/" + list[i]);
            ValueBlock cur = new ValueBlock(prm.INIT_BLOCKS);
            prm.INIT_BLOCKS = MoreArray.initArrayListArray(2);
            prm.INIT_BLOCKS[0] = MoreArray.addElements(prm.INIT_BLOCKS[0], cur.exps);
            prm.INIT_BLOCKS[1] = MoreArray.addElements(prm.INIT_BLOCKS[1], cur.genes);
            prm.write(args[1] + "/" + list[i]);
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 2) {
            TransposeGeneExp arg = new TransposeGeneExp(args);
        } else {
            System.out.println("syntax: java DataMining.util.TransposeGeneExp\n" +
                    "<dir of parameters>\n" +
                    "<out dir>\n"
            );
        }
    }
}
