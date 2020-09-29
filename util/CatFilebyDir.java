package util;

import java.io.File;

public class CatFilebyDir {
    String[] dirs;
    String out;

    public CatFilebyDir(String[] args) {
        this.out = new String(args[0]);
        this.dirs = new String[args.length];

        for (int i = 1; i < args.length; i++) {
            this.dirs[i - 1] = new String(args[i]);
        }

        File firstdir = new File(this.dirs[0]);
        String[] firstlist = firstdir.list();
        runShell rs = new runShell();

        for (int i = 0; i < firstlist.length; i++) {
            String firstfile = dirs[0] + "/" + firstlist[i];
            String catall = new String("cat " + firstfile + " ");
            for (int j = 1; j < dirs.length; j++) {
                File test = new File(dirs[j] + "/" + firstlist[i]);
                if (test.exists())
                    catall += dirs[j] + "/" + firstlist[i] + " ";
            }
            catall += " > " + out + "/" + firstlist[i];
            System.out.println("EXEC " + catall);
            rs.execute(catall);
        }
    }

    public static void main(String[] argv) {
        if (argv.length >= 3 && argv.length <= 5) {
            CatFilebyDir mad = new CatFilebyDir(argv);
        } else {
            System.out.println("syntax:  java util.CatFilebyDir <outdir> <dir1>  <dir2> <opt dir3> <opt dir4> <opt dir5>\n");
        }
    }

}
