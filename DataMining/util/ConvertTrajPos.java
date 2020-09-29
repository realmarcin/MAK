package DataMining.util;

import DataMining.MINER_STATIC;
import DataMining.ValueBlock;
import DataMining.ValueBlockList;

import java.io.File;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: Sep 19, 2011
 * Time: 6:04:45 PM
 */
public class ConvertTrajPos {


    /**
     * @param args
     */
    public ConvertTrajPos(String[] args) {

        File dir = new File(args[0]);
        String[] list = dir.list();
        String out = args[1];

        for (int i = 0; i < list.length; i++) {
            ValueBlockList in = null;
            try {
                in = ValueBlockList.read(args[0] + "/" + list[i], false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            int count = -1;
            int last = -1;
            for (int j = 0; j < in.size(); j++) {
                ValueBlock v = (ValueBlock) in.get(j);
                String cla = v.move_class;
                if (cla.startsWith("B")) {
                    last = v.trajectory_position;
                } else if (cla.startsWith("S")) {
                    if (count == -1)
                        count = last;
                    v.trajectory_position = count + v.trajectory_position;
                }
                in.set(j, v);
            }
            String stoplist = in.toString(in.header + "\n" + MINER_STATIC.HEADER_VBL);// ValueBlock.toStringShortColumns());
            String outpath = out + "/" + list[i];
            System.out.println("writing " + outpath);
            util.TextFile.write(stoplist, outpath);
        }
    }

    /**
     * @param args
     */

    public final static void main(String[] args) {

        if (args.length == 2) {
            ConvertTrajPos rm = new ConvertTrajPos(args);
        } else {
            System.out.println("syntax: java DataMining.util.ConvertTrajPos\n" +
                    "<dir of ValueBlockLists>\n" +
                    "<outfile>"
            );
        }
    }
}
