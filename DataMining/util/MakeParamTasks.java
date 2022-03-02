package DataMining.util;

import util.TextFile;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: 11/29/16
 * Time: 12:01 AM
 */
public class MakeParamTasks {

    int iteration = -1;
    String localpath = null;
    int mem_per_cpu = -1;

    /**
     * @param args
     */
    public MakeParamTasks(String[] args) {

        String indir = args[0];
        String outfile = args[1];

        File dir = new File(indir);
        String[] files = dir.list();

        if (args.length >= 3) {
            iteration = Integer.parseInt(args[2]);
        }

        if (args.length >= 4) {
            localpath = args[3];
        }

        if (args.length == 5) {
            mem_per_cpu = Integer.parseInt(args[4]);
        }

        ArrayList out = new ArrayList();

        for (int i = 0; i < files.length; i++) {
            if (iteration != -1 && localpath != null && mem_per_cpu != -1)
                out.add("hostname >  " + localpath + "level8_iter" + iteration + ".1/out_files/" + files[i] + "_host.$HT_TASK_ID.host; " +
                        "java -Xmx" + (int) (mem_per_cpu * 1000.0) + "M DataMining.RunMiner -param " + (indir + "/" + files[i]) + " &> " + localpath + "level8_iter" + iteration + ".1/out_files/" + files[i] + ".out");
            else
                out.add("java -Xmx1G DataMining.RunMiner -param " + (indir + "/" + files[i]) + " &> " + indir + "_out/" + files[i] + ".out");

        }
        //if (files.length > 0)
            TextFile.write(out, outfile);
    }


    /**
     * @param args
     */
    public final static void main(String[] args) {
        if (args.length == 2 || args.length == 3 || args.length == 4 || args.length == 5) {
            MakeParamTasks mn = new MakeParamTasks(args);
        } else {
            System.out.println("syntax: java DataMining.util.MakeParamTasks\n" +
                    "<in param data> " +
                    "<outfile>" +
                    "<OPTIONAL iteration (integer)>" +
                    "<local path>" +
                    "<mem per job in G>\n"
            );
        }
    }
}
