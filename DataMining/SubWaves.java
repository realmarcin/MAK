package DataMining;

import DataMining.util.ListfromDir;
import util.CopyFile;
import util.MapArgOptions;
import util.TextFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Code to submit and process waves of jobs.
 * <p/>
 * User: marcin
 * Date: Feb 3, 2011
 * Time: 10:46:52 PM
 */
public class SubWaves {


    HashMap options;
    String[] valid_args = {
            "-param",
            "-submit",
            "-qsub",
            "-exclude",
            "-result",
            "-skip",
            "-base"
    };

    String parampath;
    String submitpath;
    String qsubpath;
    String excludepath;
    String resultwavepath;
    String resultwaveleaf;
    String resultotalpath;

    int wave_size = 120;

    int skip_to_wave = 0;

    int global_waves = 1;

    String baseexcludefile;
    ValueBlockList base;

    String wcoutfile = "/global/home/users/marcin/running.jobs";

    //5 minutes
    int sampling_time = 300000;

    /**
     * '
     *
     * @param args
     */
    public SubWaves(String[] args) {
        init(args);

        File pdir = new File(parampath);
        String[] flist = pdir.list();

        //runShell rs = new runShell();
        int numwaves = (int) ((double) flist.length / (double) wave_size);
        int wavecount = 0;

        for (int w = 0; w < global_waves; w++) {
            //loop over all param files
            for (int i = 0; i < flist.length + wave_size - 1; ) {
                System.out.println("wave " + i + "\t" + wavecount);
                File tod = new File(submitpath);
                System.out.println("clearing dir " + submitpath);
                recursiveDeleteFilesinDirectory(tod);
                System.out.println("copy files");

                //copy param files for wave
                for (int j = i; j < i + wave_size; j++) {
                    File from = new File(parampath + "/" + flist[i]);
                    File to = new File(submitpath + "/" + flist[i]);
                    try {
                        CopyFile.copy(from, to);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                System.out.println("copied " + wave_size + " files");

                /*String s = "/bin/bash " + qsubpath;
                System.out.println("execute: " + s);
                boolean done1 = rs.execute(s);*/

                File wd = new File("/global/home/users/marcin");
                System.out.println("Working Directory: " + wd);

                Process proc = null;
                try {
                    String s = "/bin/bash " + qsubpath;
                    System.out.println("execute: " + s);
                    proc = Runtime.getRuntime().exec(s, null, wd);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                boolean complete = false;
                int time = 0;
                System.out.println("submitted wave, waiting");
                while (!complete) {
                    try {
                        Thread.sleep(sampling_time);
                    } catch (InterruptedException ie) {
                        System.out.println(ie.getMessage());
                    }
                    time += sampling_time;
                    //code to assess number of jobs done
                    String str = "qstat -f | grep marcin > /global/home/users/marcin/" + wcoutfile;
                    /*System.out.println("execute: " + str);
                    boolean done2 = rs.execute(str);*/

                    try {
                        String s = "/bin/bash " + str;
                        System.out.println("execute: " + s);
                        proc = Runtime.getRuntime().exec(s, null, wd);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    ArrayList ar = TextFile.readtoList(wcoutfile);
                    System.out.println("wave jobs remaining " + ar.size() + ", time passed " + time);
                    //won't finish until they finish
                    if (ar.size() == 0)
                        complete = true;
                }


                //create exclude list
                ListfromDir lfd = new ListfromDir(resultotalpath, excludepath, "g+i");
                int size = lfd.run();
                System.out.println("wrote exclude list " + excludepath);
                System.out.println("completed wave " + wavecount + ". Exclude list size " + size);

                wavecount++;
                i += wave_size;
            }
            //move result dir to collection and make new one
            if (global_waves > 1) {
                File results = new File(resultwavepath);
                String s1 = resultotalpath + "/" + resultwaveleaf + "_" + w;
                File s = new File(s1);
                System.out.println("renaming dir " + resultwavepath + "\n to " + s1);
                results.renameTo(s);
                results = new File(resultwavepath);
                results.mkdir();

            }
        }

    }

    /**
     * @param args
     */
    private void init(String[] args) {

        options = MapArgOptions.maptoMap(args, valid_args);

        parampath = (String) options.get("-param");
        submitpath = (String) options.get("-submit");
        File c = new File(submitpath);
        if (!c.exists() || !c.isDirectory()) {
            c.mkdir();
        }
        qsubpath = (String) options.get("-qsub");
        excludepath = (String) options.get("-exclude");
        resultwavepath = (String) options.get("-result");

        resultwaveleaf = resultwavepath.substring(resultwavepath.lastIndexOf("/") + 1);
        File d = new File(resultwavepath);
        if (!d.exists() || !d.isDirectory()) {
            d.mkdir();
        }
        resultotalpath = resultwavepath + "_wave";
        File e = new File(resultotalpath);
        if (!e.exists() || !e.isDirectory()) {
            e.mkdir();
        }
        if (options.get("-base") != null) {
            baseexcludefile = (String) options.get("-base");
            try {
                base = ValueBlockList.read(baseexcludefile, false);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        if (options.get("-skip") != null) {
            skip_to_wave = Integer.parseInt((String) options.get("-skip"));
        }
    }


    /**
     * @param path
     * @return
     */
    public static void recursiveDeleteFilesinDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    recursiveDeleteFilesinDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        //return( path.delete() );
    }


    /**
     * @param args
     */

    public final static void main(String[] args) {
        if (args.length == 10 || args.length == 14) {
            SubWaves rm = new SubWaves(args);
        } else {
            System.out.println("syntax: java DataMining.SubWaves\n" +
                    "<-param dir of params>\n" +
                    "<-submit dir for wave params>\n" +
                    "<-qsub  path>\n" +
                    "<-exclude file path>\n" +
                    "<-result dir>\n" +
                    "<-base OPTIONAL exclude file>\n" +
                    "<-skip OPTIONAL to wave n>"
            );
        }
    }
}
