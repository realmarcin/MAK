package util;

import java.io.File;

public class UntarGunzip {

    public UntarGunzip(String[] args) {

        File these = new File(args[0]);
        String prepath = new String(args[0] + "/");

        String[] tarlist = these.list();

        for (int i = 0; i < tarlist.length; i++) {
            String cur = tarlist[i];
            if (tarlist[i].indexOf(".tar.gz") != -1) {
                runShell rs = new runShell();
                String exec = new String("gunzip " + prepath + tarlist[i]);
                System.out.println(exec);
                boolean get = rs.execute(exec);
                exec = new String("tar xvf " + prepath + tarlist[i].substring(0, tarlist[i].length() - 3));
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
            System.out.println("SYNTAX: java util.UntarGunzip <dir of tar balls>" + "\n");
        } else if (args.length == 1) {
            UntarGunzip kr = new UntarGunzip(args);
        }
    }
}
