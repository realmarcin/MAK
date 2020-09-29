package DataMining.util;

import util.TextFile;

import java.io.File;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: 9/24/16
 * Time: 5:35 PM
 */
public class All_vs_All_compare_tasks {

    /**
     * @param args
     */
    public All_vs_All_compare_tasks(String[] args) {

        File dir = new File(args[0]);
        String[] files = dir.list();

        String outf = args[1];
        StringBuffer out = new StringBuffer("");
        for (int i = 0; i < files.length; i++) {
            for (int j = 0; j < files.length; j++) {
                if (i != j)
                    out.append("java -Xmx16G DataMining.CompareBiclusterRuns -one /global/scratch/marcin/method_overlaps/TOPLIST/" + files[i]
                            + " -two /global/scratch/marcin/method_overlaps/TOPLIST/" + files[j] +
                            " &> /global/scratch/marcin/method_overlaps/OUT/compare_" + i + "_" + j + ".out\n");
            }
        }
        TextFile.write(out.toString(), outf);
    }


    /**
     * @param args
     */
    public final static void main(String[] args) {
        if (args.length == 2) {
            All_vs_All_compare_tasks cde = new All_vs_All_compare_tasks(args);
        } else {
            System.out.println("syntax: DataMining.util.All_vs_All_compare_tasks\n" +
                    "<dir of toplists>\n" +
                    "<output task file>");
        }
    }
}
