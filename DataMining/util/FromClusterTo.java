package DataMining.util;

import util.runShell;

import java.io.File;

/**
 * User: marcin
 * Date: Nov 2, 2009
 * Time: 6:09:37 PM
 */
public class FromClusterTo {

    /**
     * @param args
     */
    public FromClusterTo(String[] args) {
        String inpath = args[0];

        File mkdir = new File(inpath);
        mkdir.mkdir();
        for (int i = 1; i < 6; i++) {
            mkdir = new File(inpath + "/" + inpath + "_" + i);
            mkdir.mkdir();
        }

        /* int counter = 0;
        for (int i = 0; i < 5; i++) {
            String curpath = inpath + "_" + i;
            File curdir = new File(curpath);
            String[] list = curdir.list();
            for (int j = 0; j < list.length; j++) {
                File cur = new File(curpath + "/" + list[i]);
                File rename = new File(inpath + "/" + curpath + "/" + list[i]);
                cur.renameTo(rename);
                counter++;
                if (counter % 1000 == 0) {
                    System.out.print(".");
                }
            }
        }*/
        System.out.println();
        runShell rs = new runShell();
        for (int i = 1; i < 6; i++) {
            String curpath = inpath + "_" + i;
            String e = "find " + curpath + "/ -name '*toplist*' -exec mv {} " + inpath + "/" + curpath + "/ \\;";
            System.out.println(e);
            rs.execute(e);
        }

        String e = "find " + inpath + "/ -name '*toplist*' > ~/" + inpath + ".include";
        System.out.println(e);
        rs.execute(e);
        e = "rsync -e ssh -avzP --files-from=" + inpath + ".include ~/ gobi.qb3.berkeley.edu:/usr2/people/data/marcin/miner_results/results/";
        System.out.println(e);
        rs.execute(e);
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 1) {
            FromClusterTo arg = new FromClusterTo(args);
        } else {
            System.out.println("syntax: java DataMining.util.FromClusterTo\n" +
                    "<dir prefix>");
        }
    }
}
