package DataMining.eval;


import DataMining.ValueBlock;
import DataMining.ValueBlockList;
import DataMining.ValueBlockListMethods;
import util.MoreArray;
import util.StringUtil;
import util.TabFile;

import java.util.ArrayList;

/**
 * User: marcin
 * Date: Oct 6, 2010
 * Time: 12:27:58 PM
 */
public class ExcMonkeyBiclusters {

    /**
     *
     */
    public ExcMonkeyBiclusters(String[] args) {

        String[] genedata = TabFile.readtoArrayOne(args[0]);
        String[] expdata = TabFile.readtoArrayOne(args[1]);
        String[][] refgenes = TabFile.readtoArray(args[2]);
        String[][] refexps = TabFile.readtoArray(args[3]);
        refgenes = MoreArray.replaceAll(refgenes, "\"", "");

        System.out.println("refgenes");
        MoreArray.printArray(refgenes);

        refexps = MoreArray.replaceAll(refexps, "\"", "");

        String[] genes_ref = MoreArray.extractColumnStr(refgenes, 2);
        String[] exps_ref = MoreArray.extractColumnStr(refexps, 2);
        exps_ref = StringUtil.replace(exps_ref, "..", ".");
        exps_ref = StringUtil.replace(exps_ref, "..", ".");
        exps_ref = StringUtil.replace(exps_ref, ".", "_");
        exps_ref = StringUtil.replace(exps_ref, "__", "_");

        //strip id from exp labels
        for (int i = 0; i < exps_ref.length; i++) {
            exps_ref[i] = exps_ref[i].substring(exps_ref[i].indexOf("_") + 1);
            while (exps_ref[i].charAt(exps_ref[i].length() - 1) == '_')
                exps_ref[i] = exps_ref[i].substring(0, exps_ref[i].length() - 1);
        }

        System.out.println("start exps_ref");
        MoreArray.printArray(exps_ref);
        System.out.println("end exps_ref");

        ArrayList genenotfound = new ArrayList();
        ArrayList expnotfound = new ArrayList();
        ValueBlockList vbl = new ValueBlockList();

        for (int i = 1; i < genedata.length; i++) {
            if (genedata[i].length() > 0) {
                String genesr = genedata[i].substring(genedata[i].indexOf("(") + 1, genedata[i].length() - 1);
                System.out.println("genedata[i] " + i + "\t" + genedata[i]);
                System.out.println("genesr " + i + "\t" + genesr);
                //System.out.println("expdata[i] " + i + "\t" + expdata[i]);
                String expsr = expdata[i].substring(expdata[i].indexOf("(") + 1, expdata[i].length() - 1);
                //System.out.println("expsr " + expsr);

                String[] genes = genesr.split("::");
                String[] exps = expsr.split("::");

                ArrayList g = new ArrayList();
                ArrayList e = new ArrayList();

                for (int j = 0; j < genes.length; j++) {
                    int gindex = MoreArray.getArrayInd(genes_ref, genes[j]) + 1;
                    int last = genes[j].indexOf("-");

                    String substring = genes[j];//
                    if (last != -1)
                        substring = genes[j].substring(0, last);
                    if (gindex == 0 && last != -1) {
                        //gindex = MoreArray.getArrayInd(genes_ref, substring) + 1;
                        //if (gindex == -1) {
                        //gindex = MoreArray.getArrayInd(genes_ref, substring) + 1;
                        // if (gindex != -1) {
                        System.out.println("inexact match " + gindex + "\t" + substring + "\t" + genes[j]);
                        gindex = -1;
                        //}
                        //}
                    }
                    if (gindex > 0) {
                        g.add(gindex);
                        System.out.println("found genes " + genes[j] + "\t" + genes_ref[gindex - 1] + "\t" + gindex + "\t" + substring);
                    } else {
                        System.out.println("not found, omitting gene " + gindex + "\t" + genes[j] + "\t" + substring);
                        if (genenotfound.indexOf(genes[j]) == -1)
                            genenotfound.add(genes[j]);
                    }

                }
                for (int j = 0; j < exps.length; j++) {
                    //System.out.println(exps_ref[0]);
                    //System.out.println("exps "+j + "\t" + exps[j]);
                    if (exps[j].indexOf("Yeast_Gasch00_") != -1 || exps[j].indexOf("Yeast_Gasch01_") != -1) {
                        String search = "" + exps[j].substring("Yeast_Gasch00_".length()) + "";
                        while (search.charAt(search.length() - 1) == '_')
                            search = search.substring(0, search.length() - 1);
                        search = StringUtil.replace(search, "minredo", "min_redo");

                        if (search.equals("DES459_mec1_log_phase_IR_time_0_sample")) {
                            search = "DES459_mec1_log_phase_IR_time";
                        } else if (search.equals("DES459_mec1_log_phase_MMS_time_0_sample")) {
                            search = "DES459_mec1_log_phase_MMS_time";
                        }
                        //int eindex = MoreArray.getArrayIndexOf(exps_ref, search) + 1;
                        int ind = MoreArray.getArrayInd(exps_ref, search);
                        int eindex = ind + 1;

                        if (eindex > 0) {
                            if (search.length() != exps[j].length())
                                System.out.println("found exp " + search + "\t::\t" + exps[j]);
                            e.add(eindex);
                        } else {
                            System.out.println("not found, omitting exp " + search);
                            if (expnotfound.indexOf(search) == -1)
                                expnotfound.add(search);
                            //MoreArray.printArray(exps_ref);
                        }
                    }
                }


                int[] g1 = MoreArray.ArrayListtoInt(g);
                int[] e1 = MoreArray.ArrayListtoInt(e);
                System.out.println("genes " + i + "\t" + MoreArray.toString(g1, ","));
                ValueBlock vb = new ValueBlock(g1, e1);
                vbl.add(vb);
            }
        }
        System.out.println("genes not found");
        MoreArray.printArray(MoreArray.ArrayListtoString(genenotfound));

        System.out.println("exps not found");
        MoreArray.printArray(MoreArray.ArrayListtoString(expnotfound));

        ValueBlockListMethods.writeBIC("cMonkey_yeast.bic", vbl);
    }

    /**
     * @param args
     */
    public final static void main(String[] args) {
        if (args.length == 4) {
            ExcMonkeyBiclusters rm = new ExcMonkeyBiclusters(args);
        } else {
            System.out.println("syntax: java DataMining.eval.ExcMonkeyBiclusters\n" +
                    "<cMonkey genes>\n" +
                    "<cMonkey exps>\n" +
                    "<ref gene list>\n" +
                    "<ref exp list>"
            );
        }
    }
}
