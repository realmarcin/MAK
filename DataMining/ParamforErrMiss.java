package DataMining;

import util.*;

import java.io.File;
import java.util.ArrayList;

/**
 * User: marcin
 * Date: Oct 26, 2009
 * Time: 1:52:27 PM
 */
public class ParamforErrMiss {

    boolean debug = true;

    runShell rs;
    String outdir, paramdir, datadir;

    String prefix, suffix, suffix2, prefixref, suffixref;

    public int[] missingArInt;

    public int missingdatastarts;
    public int smallstarts;
    public int bigstarts;
    public int[] missingdatastarts_ar;
    public int[] smallstarts_ar;
    public int[] bigstarts_ar;


    /**
     *
     */
    public ParamforErrMiss() {

    }

    /**
     * @param args
     */
    public ParamforErrMiss(String[] args) {

        datadir = args[0];
        paramdir = args[1];
        outdir = args[2];
        if (args.length >= 4) {
            prefixref = args[3];
            suffixref = args[4];
            prefix = args[5];
            if (args.length >= 7)
                suffix = args[6];
            if (args.length >= 8)
                suffix2 = args[7];
        }

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

        if (args.length < 4 || suffix != null)
            run();
        else if (args.length > 4) {
            runNoSuffix();
        }
    }


    /**
     */
    public void run() {
        //corresponding parameter dir
        System.out.println("run");
        System.out.println("parampath " + paramdir);
        File paramf = new File(paramdir);
        ArrayList missingAr = new ArrayList();

        if (paramf.exists()) {
            String[] paramlist = paramf.list();


            ArrayList miss = new ArrayList();
            ArrayList big = new ArrayList();
            ArrayList small = new ArrayList();
            for (int x = 0; x < paramlist.length; x++) {

                int doub = paramlist[0].indexOf("__");
                int predoub = StringUtil.lastIndexBefore(paramlist[0], "_", doub);

                int startindex = 0;
                try {
                    startindex = Integer.parseInt(paramlist[0].substring(predoub + 1, doub));
                } catch (NumberFormatException e) {
                    e.printStackTrace();

                    doub = paramlist[0].lastIndexOf("__");
                    doub = paramlist[0].substring(0, doub).lastIndexOf("__");
                    predoub = StringUtil.lastIndexBefore(paramlist[0], "_", doub);
                }

                Parameters example = new Parameters();
                example.read(paramdir + "/" + paramlist[0]);
                ValueBlock vb = new ValueBlock(example.INIT_BLOCKS[0], example.INIT_BLOCKS[1]);
                boolean aboveNaNThreshold = vb.isAboveNaN(example.PERCENT_ALLOWED_MISSING_IN_BLOCK);
                if (!aboveNaNThreshold) {
                    double frxn = vb.frxnNaN();
                    System.out.println("WARNING starting point exceeds the missing data limit: " + frxn + "\tlimit " +
                            example.PERCENT_ALLOWED_MISSING_IN_BLOCK);
                    //vb.trimNAN(rmb.expr_matrix.data, example.PERCENT_ALLOWED_MISSING_GENES, example.PERCENT_ALLOWED_MISSING_EXP, false);
                    //System.exit(0);
                    miss.add(startindex);
                    missingdatastarts++;
                }

                if (vb.genes.length < example.IMIN || vb.exps.length < example.JMIN) {
                    smallstarts++;
                    small.add(startindex);
                }
                if (vb.genes.length > example.IMAX || vb.exps.length > example.JMAX) {
                    bigstarts++;
                    big.add(startindex);
                }
            }

            missingdatastarts_ar = MoreArray.ArrayListtoInt(miss);
            smallstarts_ar = MoreArray.ArrayListtoInt(small);
            bigstarts_ar = MoreArray.ArrayListtoInt(big);

            File datadirf = new File(datadir);
            String[] datadirlist = datadirf.list();
            //corresponding list of parameter files
            int missing = 0;
            int length = paramlist.length;
            //System.out.println("length " + length);

            for (int a = 0; a < length; a++) {
                int startrandomlabel = paramlist[a].indexOf("AG_") + 3;
                int endrandomlabel = paramlist[a].indexOf("_", startrandomlabel);
                String randomlabel = paramlist[a].substring(startrandomlabel, endrandomlabel);

                System.out.println("paramlist[a] " + paramlist[a]);
                String testtop = paramlist[a].substring(0, paramlist[a].indexOf("_parameters.txt"));
                System.out.println("testtop " + testtop);

                boolean found = false;
                String test = null;

                int doub = paramlist[a].indexOf("__");
                int predoub = StringUtil.lastIndexBefore(paramlist[a], "_", doub);
                String value = paramlist[a].substring(predoub + 1, doub);
                if (prefixref == null) {
                    test = datadir + "/" + testtop + randomlabel + "_-1_S_1_toplist.txt";
                } else {
                    value = paramlist[a].substring(paramlist[a].indexOf(prefixref) + prefixref.length(), paramlist[a].indexOf(suffixref));
                    System.out.println("suf/prefix " + a + "\t" + value);
                    test = datadir + "/" + prefix + value + suffix;
                }

                if (a == 0) {
                    System.out.println(a + " test " + test);
                }

                File testf = new File(test);
                if (testf.exists()) {
                    found = true;
                } else {
                    if (prefixref == null)
                        test = datadir + "/" + testtop + randomlabel;
                    else {
                        value = paramlist[a].substring(paramlist[a].indexOf(prefixref) + prefixref.length(), paramlist[a].indexOf(suffixref));
                        test = prefix + value + suffix;
                        System.out.println("testing partial match " + test);
                        System.out.println("testing partial match " + datadir + "/" + test);
                        int index = StringUtil.getFirstStartsWithRetInt(datadirlist, test);//, suffix2//getFirstIndexOf
                        System.out.println("testing partial match index " + index);
                        System.out.println("testing partial match index " + datadirlist[0]);
                        if (index != -1) {
                            test = datadir + "/" + datadirlist[index];
                            System.out.println("found partial match " + test);
                        }

                    }
                    testf = new File(test);
                    if (testf.exists())
                        found = true;
                }


                if (!found) {

                    missingAr.add(value);
                    System.out.println("DOES NOT EXIST as e.g. " + test + "\tadded to array " + missingAr.size() + "\t" + value);
                    String cmd = "cp " + paramdir + "/" + paramlist[a] + " " + outdir;
                    System.out.println(cmd);
                    rs.execute(cmd);
                    missing++;
                    /*if(missing == 1) {
                       System.out.println("missing "+toppath);
                       System.out.println("missing "+toppath2);
                    }*/
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
     */
    public void runNoSuffix() {
        System.out.println("runNoSuffix");
        System.out.println("datadir " + datadir);
        File datadirf = new File(datadir);
        ArrayList missingAr = new ArrayList();

        if (datadirf.exists()) {
            String[] datalist = datadirf.list();
            //corresponding list of parameter files
            int missing = 0;
            int length = datalist.length;
            String end = "__";
            ArrayList done = new ArrayList();
            //find all existing indices
            for (int a = 0; a < length; a++) {
                int ind1 = datalist[a].indexOf(prefix) + prefixref.length();
                String value = datalist[a].substring(ind1, datalist[a].indexOf(end, ind1 + 1));
                done.add(value);
            }

            File paramf = new File(paramdir);
            if (paramf.exists()) {
                String[] paramlist = paramf.list();
                int length2 = paramlist.length;
                for (int a = 0; a < length2; a++) {
                    boolean found = false;
                    //extract index from parameter file and check if found in results
                    String value = paramlist[a].substring(paramlist[a].indexOf(prefixref) + prefixref.length(), paramlist[a].indexOf(suffixref));
                    for (int i = 0; i < done.size(); i++) {
                        String cur = (String) done.get(i);
                        if (value.equals(cur)) {
                            found = true;
                        }
                    }
                    //if not found cp parameter file
                    if (!found) {
                        missingAr.add(value);
                        System.out.println("DOES NOT EXIST as e.g. " + a);
                        String cmd = "cp " + paramdir + "/" + paramlist[a] + " "
                                + outdir;
                        System.out.println(cmd);
                        rs.execute(cmd);
                        missing++;
                    }
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
        if (args.length == 3 || args.length == 6 || args.length == 7 || args.length == 8) {
            ParamforErrMiss arg = new ParamforErrMiss(args);
        } else {
            System.out.println("syntax: java DataMining.ParamforErrMiss\n" +
                    "<data dir>\n" +
                    "<param dir>\n" +
                    "<param out dir>\n" +
                    "<prefixref y/n OPTIONAL>\n" +
                    "<suffixref y/n OPTIONAL\n" +
                    "<prefix y/n OPTIONAL>\n" +
                    "<suffix y/n OPTIONAL>\n" +
                    "<suffix2 y/n OPTIONAL>"

            );
        }
    }
}
