package util;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Marcin
 * Date: Jul 23, 2005
 * Time: 6:04:38 PM
 */
public class FilterFile {

    String indir, outdir;
    String[] list;


    public FilterFile() {

    }

    /**
     * @param a
     */
    public FilterFile(String[] a) {

        indir = a[0];
        outdir = a[1];

        loadList();

    }

    private void loadList() {

        File in = new File(indir);

        list = in.list();
    }

    private void run() {
        for (int i = 0; i < list.length; i++) {

            ArrayList read = util.TextFile.readtoList(indir + list[i]);

            System.out.println(list[i] + "\tb/f " + read.size());
            for (int j = 0; j < read.size(); j++) {

                String cur = (String) read.get(j);

                if (cur.charAt(0) == '#') {
                    read.remove(j);
                    j--;
                }
            }
            System.out.println(list[i] + "\ta/f " + read.size());

            util.TextFile.write(read, outdir + list[i]);
        }


    }


    /**
     * @param args
     */
    public static void main(String[] args) {

        if (args.length == 2) {
            FilterFile ff = new FilterFile(args);
            ff.run();
        } else {

            System.out.println("syntax: java util.FilterFile <in dir> <out dir>");
        }


    }
}
