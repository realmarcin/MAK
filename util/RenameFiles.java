package util;

import java.io.File;
import java.util.ArrayList;

/**
 * User: marcin
 * Date: Mar 28, 2007
 */
public class RenameFiles {

    int total_rep = -1, total_time = -1;
    ArrayList reps = new ArrayList();
    int[] times, sets, sets_count;

    int label_expId = 114;

    /**
     * @param args
     */
    public RenameFiles(String[] args) {
        String in = args[0];
        String out = args[1];
        File f = new File(in);
        String[] list = f.list();
        for (int i = 0; i < list.length; i++) {
            int q = list[i].indexOf("query_d");

            String str = list[i].substring(0, q) + "_query_d.txt";
            String newout = out + "/" + str;
            String orig = in + "/" + list[i];
            File nf = new File(orig);
            nf.renameTo(new File(newout));
            /*String[] split = list[i].split("E102");
            if (split.length > 1) {
                String newout = out + "\\" + split[0] + "E114" + split[1];
                System.out.println(split[0] + "\t" + split[1] + "\t" + newout);

                String orig = in + "\\" + list[i];
                File nf = new File(orig);
                nf.renameTo(new File(newout));
                System.out.println("renaming " + orig + "\t" + newout);
            }*/
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 2) {
            RenameFiles ce = new RenameFiles(args);
        } else {
            System.out.println("usage: java util.RenameFiles <in dir> <out dir>");
        }
    }
}
