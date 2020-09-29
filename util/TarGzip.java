package util;

import java.io.File;

public class TarGzip {

    public TarGzip(String[] args) {

        File these = new File(args[0]);
        String prepath = new String(args[0] + "/");

        String[] tarlist = these.list();

        for (int i = 0; i < tarlist.length; i++) {
            String cur = tarlist[i];
            runShell rs = new runShell();
            String exec = new String("tar -cf " + prepath + tarlist[i] + ".tar " + prepath + tarlist[i]);
            System.out.println(exec);
            boolean get = rs.execute(exec);
            if (get) {
                exec = new String("gzip " + prepath + tarlist[i] + ".tar");
                System.out.println(exec);
                get = rs.execute(exec);
            }
        }
    }

    /**
     * Main function
     */
    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("SYNTAX: java util.TarGzip <dir of tar balls>" + "\n");
        } else if (args.length == 1) {
            TarGzip kr = new TarGzip(args);
        }
    }
}
