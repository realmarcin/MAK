package DataMining.util;

import DataMining.MINER_STATIC;
import util.MoreArray;
import util.ParsePath;
import util.TextFile;
import util.runShell;

import java.io.File;
import java.util.ArrayList;

/**
 * User: marcin
 * Date: Oct 26, 2009
 * Time: 1:52:27 PM
 */
public class ParamforErrMissSynth {

    boolean debug = true;

    runShell rs;
    String outdir, paramdir, datadir;

    public int[] missingArInt;


    /**
     * @param args
     */
    public ParamforErrMissSynth(String[] args) {

        datadir = args[0];
        paramdir = args[1];
        outdir = args[2];

        initiateRun(args);

    }

    /**
     * @param args
     */
    public void initiateRun(String[] args) {
        int indexstart = datadir.indexOf("results_") + "results_".length();
        //String paramlabel = datadir.substring(indexstart, datadir.length());
        /* for (int i = 1; i < 6; i++) {
                    File newdir = new File(outdir + "/" + paramlabel + "_" + i);
                    newdir.mkdir();
                }
        */
        rs = new runShell();

        System.out.println("args " + args.length);

        run();
    }

    /**
     *
     */
    public void run() {
        System.out.println("runNoSuffix");
        System.out.println("datadir " + datadir);
        File datadirf = new File(datadir);
        ArrayList missingAr = new ArrayList();

        if (datadirf.exists()) {
            String[] datalist = datadirf.list();
            //corresponding list of parameter files
            int missing = 0;

            File paramf = new File(paramdir);
            if (paramf.exists()) {
                String[] paramlist = paramf.list();
                int length2 = paramlist.length;

                ArrayList param_crit = new ArrayList();
                for (int a = 0; a < length2; a++) {
                    int paramind1 = paramlist[a].indexOf("__") + 2;
                    int paramind2 = paramlist[a].indexOf("__", paramind1);
                    //extract index from parameter file and check if found in results
                    String value = paramlist[a].substring(paramind1 - 2, paramind2 + 2);
                    if (param_crit.indexOf(param_crit) == -1)
                        param_crit.add(value);
                }

                ArrayList result_crit = new ArrayList();
                for (int z = 0; z < MINER_STATIC.RANDOM_SEEDS.length; z++) {

                    String curseed = Integer.toString(MINER_STATIC.RANDOM_SEEDS[z]);

                    for (int a = 0; a < length2; a++) {
                        boolean found = false;
                        int paramind1 = paramlist[a].indexOf("__") + 2;
                        int paramind2 = paramlist[a].indexOf("__", paramind1);
                        //extract index from parameter file and check if found in results
                        String value = paramlist[a].substring(paramind1 - 2, paramind2 + 2);

                        for (int k = 0; k < datalist.length; k++) {
                            int resind1 = datalist[k].indexOf("__") + 2;
                            int resind2 = datalist[k].indexOf("__", resind1);
                            //extract index from result file
                            String resvalue = datalist[k].substring(resind1 - 2, resind2 + 2);
                            if (result_crit.indexOf(resvalue) == -1)
                                result_crit.add(resvalue);
                            String cur = datalist[k];
                            if (resvalue.equals(value) && cur.indexOf(curseed) != -1) {
                                found = true;
                            }
                        }

                        //if not found cp parameter file
                        if (!found) {
                            missingAr.add(value);
                            System.out.println("DOES NOT EXIST as e.g. " + value + "\t" + curseed);
                            String cmd = "cp " + paramdir + "/" + paramlist[a] + " "
                                    + outdir;
                            System.out.println(cmd);
                            rs.execute(cmd);
                            missing++;
                        } else {
                            System.out.println(".");
                        }
                    }
                }
                for (int i = 0; i < param_crit.size(); i++) {
                    String test = (String) param_crit.get(i);
                    if (result_crit.indexOf(test) == -1)
                        System.out.println("not found in result " + test);
                }
                for (int i = 0; i < result_crit.size(); i++) {
                    String test = (String) result_crit.get(i);
                    if (param_crit.indexOf(test) == -1)
                        System.out.println("not found in param " + test);
                }

            }


            System.out.println("finished " + datadir + "\t" + missing + "\t" + missingAr.size());
            String out = MoreArray.arrayListtoString(missingAr, ",");
            missingArInt = MoreArray.ArrayListtoInt(missingAr);
            System.out.println(out);
            ParsePath pp = new ParsePath(datadir);
            TextFile.write(out, "ParamforErrMiss_" + pp.getLastDirinPath() + ".txt");
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 3) {
            ParamforErrMissSynth arg = new ParamforErrMissSynth(args);
        } else {
            System.out.println("syntax: java DataMining.util.ParamforErrMissSynth\n" +
                    "<data dir>\n" +
                    "<param dir>\n" +
                    "<param out dir>\n"

            );
        }
    }
}
