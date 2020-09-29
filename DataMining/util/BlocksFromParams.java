package DataMining.util;

import DataMining.MINER_STATIC;
import DataMining.Parameters;
import DataMining.ValueBlock;
import DataMining.ValueBlockList;

import java.io.File;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: Oct 21, 2012
 * Time: 12:19:27 AM
 */
public class BlocksFromParams {


    /**
     * @param args
     */
    public BlocksFromParams(String[] args) {

        File dir = new File(args[0]);
        String[] list = dir.list();
        ValueBlockList vbl = new ValueBlockList();
        for (int i = 0; i < list.length; i++) {
            Parameters p = new Parameters();
            String s = args[0] + "/" + list[i];
            System.out.println(i + "\t" + s);
            p.read(s);
            try {
                ValueBlock v = new ValueBlock(p.INIT_BLOCKS[0], p.INIT_BLOCKS[1]);
                vbl.add(v);
            } catch (Exception e) {
                System.out.println(i + "\t" + s);
                e.printStackTrace();
            }
        }

        String sftop100 = vbl.toString("#\n" + MINER_STATIC.HEADER_VBL);//ValueBlock.toStringShortColumns());
        String outpath = args[0] + ".vbl";
        System.out.println("outpath " + outpath);
        util.TextFile.write(sftop100, outpath);
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 1) {
            BlocksFromParams arg = new BlocksFromParams(args);
        } else {
            System.out.println("syntax: java DataMining.util.BlocksFromParams\n" +
                    "<param dir>"
            );
        }
    }
}
