package DataMining.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * User: marcin
 * Date: May 27, 2008
 * Time: 1:59:23 PM
 */
public class Makeqsub {

    /**
     * @param args
     */
    public Makeqsub(String[] args) {
        String paramdir = args[0];
        String outfile = args[1];

        try {
            PrintWriter pw = new PrintWriter(new FileWriter(outfile), true);
            pw.println("for file in $(find " + paramdir + " -name \\*.*)");
            pw.println("do");
            pw.println("basename=$(basename $file *.txt)");
            pw.println("qsub " +
                    "-o /nfs/jbei-filer2/home/marcin/logs/$basename.$JOB_ID.out " +
                    "-e /nfs/jbei-filer2/home/marcin/logs/$basename.$JOB_ID.err " +
                    "-v LD_LIBRARY_PATH=/nfs/jbei-filer2/home/marcin/R-2.6.1/lib:/nfs/jbei-filer2/home/marcin/JRI," +
                    "CLASSPATH=/nfs/jbei-filer2/home/marcin/classes:/nfs/jbei-filer2/home/marcin/JRI/src," +
                    "R_HOME=/nfs/jbei-filer2/home/marcin/R-2.6.1 " +
                    "-b y " +
                    "java -mx2000M DataMining.RunMiner -param $file");
            pw.println("done");
            pw.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 2) {
            Makeqsub m = new Makeqsub(args);
        } else {
            System.out.println("java util.Makeqsub\n" +
                    "<dir of parameter files (overrides other options)>\n" +
                    "<out file>");
        }
    }
}
