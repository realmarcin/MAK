package util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Makeqsub {

    PrintWriter pw;
    String workdir = "/scrg/marcin/dump/";
    String outdir = "/scrg/marcin/new_exons_blastn/";
    String scriptoutdir = "/scrg/marcin/new_exon_qsub/";
    String db = "/scrg/marcin/db/est_human";
    String infile = "/scrg/marcin/new_exon.fasta";

    String cmd;
    int granule = 55;
    int total;
    int nodes = 29;

    public Makeqsub(String[] args) {

        workdir = args[0];
        outdir = args[1];
        total = Integer.parseInt(args[2]);

        granule = Integer.parseInt(args[3]);
//granule=(int)total/(nodes-1);
        scriptoutdir = args[4];

        System.out.println("nodes " + nodes + "; total jobs " + total + "; granule " + granule);

        cmd = "java -mx1000M exec.runBlastN " + infile + " " + outdir + " " + db;

        GiveDate gd = new GiveDate();
        String dat = gd.giveShortDate();

        int count = 0;
        for (int i = 0; i < total;) {

            String outf = dat + "_" + count + ".qsub";
            System.out.println("generating " + outf);
            makeScript(i, i + granule, outf);
            count++;
            i = i + granule;
        }

    }

    private void makeScript(int lo, int hi, String p) {

        try {

            pw = new PrintWriter(new FileWriter(p), true);

            pw.println("#!/bin/sh");
            pw.println("#PBS -m bae");
            pw.println("#PBS -M marcin@compbio.berkeley.edu");
            pw.println("#PBS -l nodes=1");
            pw.println("cd " + workdir+"/boot_"+(lo+1));
            pw.println("protpars infile < boot_param_"+(lo+1)+" > out_"+(lo+1));
            pw.println("## end of batch script");
            pw.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {

        if (args.length == 5) {
            Makeqsub mqs = new Makeqsub(args);
        } else {
            System.out.println("java util.Makeqsub <job workdir> <job outdir> <total jobs> <granule> <script outdir>");
        }

    }

}
