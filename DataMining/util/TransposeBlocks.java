package DataMining.util;

import DataMining.ValueBlock;
import DataMining.ValueBlockList;
import util.ParsePath;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: 11/2/16
 * Time: 4:03 PM
 */
public class TransposeBlocks {

    boolean debug = false;

    /**
     * @param args
     */
    public TransposeBlocks(String[] args) {

        ValueBlockList vbl = null;
        ValueBlockList finalvbl = new ValueBlockList();
        try {
            vbl = ValueBlockList.read(args[0], debug);

        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < vbl.size(); i++) {
            ValueBlock v = (ValueBlock) vbl.get(i);
            ValueBlock add = new ValueBlock(v);

            add.genes = v.exps;
            add.exps = v.genes;

            finalvbl.add(add);
        }

        try {
            String a = args[0];
            ParsePath pp = new ParsePath(a);
            String s = pp.getName() + "_transpose.txt";
            System.out.println("writing " + s);
            String out = finalvbl.toString(vbl.header);//+ ValueBlock.toStringShortColumns()
            util.TextFile.write(out, s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param args
     */

    public final static void main(String[] args) {

        if (args.length == 1) {
            TransposeBlocks rm = new TransposeBlocks(args);
        } else {
            System.out.println("syntax: java DataMining.util.TransposeBlocks\n" +
                    "<trajectory output>"
            );
        }
    }
}

