package util;

import java.io.File;

public class Submitqsub {

    public Submitqsub(String[] args) {


        runShell rs = new runShell();

        File dir = new File(args[0]);
        String[] files = dir.list();

        for (int i = 0; i < files.length; i++) {

            String exec = "qsub " + args[0] + "/" + files[i];
            System.out.println("Submitting " + exec);
            rs.execute(exec, i);
        }
    }

    public static void main(String[] args) {

        if (args.length == 1) {

            Submitqsub sq = new Submitqsub(args);
        } else {
            System.out.println("java util.Submitqsub <dir of qsub scripts>");
        }
    }

}
