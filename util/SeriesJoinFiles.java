package util;

import java.io.File;

/**
 * User: marcin
 * Date: Jan 15, 2009
 * Time: 8:23:26 PM
 */
public class SeriesJoinFiles {

    boolean left = false;

    /**
     *
     */
    public SeriesJoinFiles(String[] args) {
        String in1 = args[0];
        int col1 = Integer.parseInt(args[1]);
        String path = args[2];
        int col2 = Integer.parseInt(args[3]);
        if (args.length == 5)
            left = true;

        File f = new File(path);
        String[] list = f.list();
        JoinFiles jf = null;
        for (int i = 0; i < list.length; i++) {
            if (jf == null)
                jf = new JoinFiles(in1, col1, path, list[i], col2, left);
            else
                jf.update(path, list[i], col2, left);
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {

        if (args.length == 4 || args.length == 5) {
            SeriesJoinFiles ce = new SeriesJoinFiles(args);
        } else {
            System.out.println("usage: java util.SeriesJoinFiles\n" +
                    "<master file>\n" +
                    " <column (index starts at 0)>\n" +
                    " <dir>\n" +
                    " <column (index starts at 0)>\n" +
                    " <optional: leftjoin>\n");
            System.out.println("usage: Assumes that the specified column labels are unique in file 2");
        }
    }
}
