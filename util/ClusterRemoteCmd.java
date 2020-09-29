package util;

import java.util.ArrayList;

/**
 * Created by Marcin
 * Date: Jul 23, 2005
 * Time: 6:28:08 PM
 */
public class ClusterRemoteCmd extends FilterFile {

    final static String nodes = "abcdeijklmnopqrstuvwxy";

    /**
     * @param a
     */
    public ClusterRemoteCmd(String[] a) {

        super(a);
    }

    /**
     *
     */
    private void run() {

        int total = 0;
        int i = 0;
        while (total < list.length) {

            ArrayList read = util.TextFile.readtoList(indir + list[total]);

            System.out.println(list[total] + "\tb/f " + read.size());
            for (int j = 0; j < read.size(); j++) {

                String cur = (String) read.get(j);

                if (cur.charAt(0) == '#') {
                    read.remove(j);
                    j--;
                }
            }
            System.out.println(list[total] + "\ta/f " + read.size());

            String out = "ssh fe" + nodes.charAt(i) + " '";
            int j = 0;
            for (j = 0; j < read.size() - 1; j++) {

                out += (String) read.get(j) + " && ";
            }
            out += (String) read.get(j) + "'\n";

            util.TextFile.write(out, outdir + list[total]);
            total++;
            i++;
            if (total == nodes.length())
                i = 0;
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {

        if (args.length == 2) {
            ClusterRemoteCmd ff = new ClusterRemoteCmd(args);
            ff.run();
        } else {

            System.out.println("syntax: java util.FilterFile <in dir> <out dir>");
        }


    }

}
