package DataMining.util;

import util.TextFile;

import java.io.File;
import java.util.ArrayList;

/**
 * User: marcin
 * Date: 11/4/17
 * Time: 12:29 PM
 */
public class MakeRunTasks {

    /**
     * @param args
     */
    public MakeRunTasks(String[] args) {

        String inpath = args[0];
        File dir = new File(inpath);
        String[] list = dir.list();

        String outpath = args[1];
        String outfile = args[2];

        ArrayList out = new ArrayList();

        for (int i = 0; i < list.length; i++) {
            out.add("java  -Xmx2G DataMining.RunMiner -param " +
                    inpath + "/" + list[i] +
                    "&> " + outpath + "/" + list[i] + ".out"
            );
        }

        TextFile.write(out, outfile);
    }


    /**
     * @param args
     */
    public final static void main(String[] args) {
        if (args.length == 3) {
            MakeRunTasks mn = new MakeRunTasks(args);
        } else {
            System.out.println("syntax: java DataMining.util.MakeRunTasks\n" +
                    "<in path> " +
                    "<out path>" +
                    "<out task file>\n"
            );
        }
    }
}
