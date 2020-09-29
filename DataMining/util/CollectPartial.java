package DataMining.util;

import DataMining.Parameters;
import util.MoreArray;
import util.TextFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: 2/22/13
 * Time: 10:27 AM
 */
public class CollectPartial {

    Parameters prm;

    String delimiter = "";
    String delimiter2 = "";
    //String delimiter = "reconstructed_";
    //String delimiter2 = "0.0_cut_0";

    /**
     * @param args
     */
    public CollectPartial(String[] args) {

        String outdir = args[0];
        File dirout = new File(outdir);
        String[] listout = dirout.list();
        System.out.println("listout " + listout.length);

        String remaindir = args[1];
        File dirremain = new File(remaindir);
        String[] listremain = dirremain.list();
        System.out.println("listremain " + listremain.length);

        String outfile = args[2];

        if (args.length >= 5) {
            delimiter = args[3];
            delimiter2 = args[4];
            if (args.length == 6) {
                prm = new Parameters();
                prm.read(args[5]);
            }
        }

        ArrayList out = new ArrayList();

        String remainids = "";
        //for all remaining param files
        for (int i = 0; i < listremain.length; i++) {
            System.out.println("listremain " + i + "\t" + listremain[i]);
            Parameters thisparam = new Parameters();
            thisparam.read(args[1] + "/" + listremain[i]);
            String testremain = listremain[i] + ".out";
            String remainParam = listremain[i];
            if (!delimiter.equals("")) {
                int iind = listremain[i].indexOf(delimiter) + delimiter.length();
                int iind2 = listremain[i].indexOf("_", iind + 1);
                testremain = listremain[i] + ".out";
                remainParam = listremain[i].substring(iind, iind2);
            }
            remainids += "" + remainParam + ",";
            //System.out.println("i " + i + "\tremain " + listremain[i].substring(iind, iind2) + "\t" + listremain[i]);
            //search .out files
            boolean done = false;
            for (int j = 0; j < listout.length; j++) {
                if (!done) {
                    //System.out.println("listout " + j + "\t" + listout[j]);
                    String outparam = listout[j];
                    if (!delimiter.equals("")) {
                        int jjnd = listout[j].indexOf(delimiter) + delimiter.length();
                        int jjnd2 = listout[j].indexOf("_", jjnd + 1);
                        outparam = listout[j].substring(jjnd, jjnd2);
                    }
                    //System.out.println("j " + j + "\tout " + outparam + "\t" + listout[j]);

                    //yeast_cmonkey_noabs_refine_results_yeast_cmonkey_1_expr_MSEC_KendallC_GEECE_round12345_top_plusbypass_cut_scoreperc66.0_0.0
                    // _cut_
                    // 0.25_1.0_c_reconstructed_


                    //COALESCE_null_00_results_coalesce_1_toplist_00_1_cut_scoreperc66.0_0.0_0.25_0.25_c_liven_reconstructed_161__MSEC_KendallC_GEECE__AG_759820__parameters.txt
                    //COALESCE_null_00_results_coalesce_1_toplist_00_1_cut_scoreperc66.0_0.0_0.25_0.25_c_liven_reconstructed_100__MSEC_KendallC_GEECE__AG_759820_759820_-1_N_32_toplist.txt
               /* String delimiternow = null;
                if (delimiter2 != "") {
                    delimiternow = delimiter2;
                }*/

                    String listoutj2 = null;
                    try {
                        listoutj2 = listout[j];
                        if (!delimiter2.equals("")) {
                            int cutinf = listout[j].indexOf(delimiter2);
                            if (cutinf != -1) {
                                listoutj2 = listout[j].substring(0, cutinf + 3) + listout[j].substring(cutinf + delimiter2.length() - 2);//s.length()-2 ?
                            }
                        }/*else {
                        s = "_reconstructed";
                        cutinf = listout[j].indexOf(s);
                        if (cutinf != -1) {
                            listoutj2 = listout[j].substring(0, cutinf + 3) + listout[j].substring(cutinf + s.length()-2); //s.length()-2 ?
                        }
                    }*/
                        //System.out.println("j2 " + j + "\t" + listoutj2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    boolean equalParam = remainParam.equals(outparam);
                    if (equalParam) {
                        System.out.println("equals " + i + "\t" + j + "\t" + outparam);
                    }

                    if (listout[j].equals(testremain) || (listoutj2 != null && listoutj2.equals(testremain)) || equalParam) {//
                        System.out.println("matched " + i + "\t" + j + "\t" + listout[j]);
                        String[] data = TextFile.readtoArray(outdir + "/" + listout[j]);

                        double totaltime = 0;
                        String lastvb = null;
                        for (int k = 0; k < data.length; k++) {
                            if (data[k].indexOf("runLoop Total time") == 0) {
                                //System.out.println("time " + data[k - 2]);
                                lastvb = data[k - 2];
                                totaltime += Double.parseDouble(data[k].substring(data[k].indexOf("time  ") + "time  ".length(), data[k].indexOf(" s")));
                            }
                        }
                        if (totaltime > 0) {
                            System.out.println("Total time " + j + "\t" + (totaltime / (3600 * 1000)) + " h");

                            System.out.println("lastvb " + lastvb);
                            if (thisparam != null) {
                                String[] split = lastvb.split("\t");
                                try {
                                    String blockid = split[2];
                                    //System.out.println("output param file " + blockid);
                                    String[] ids = blockid.split("/");
                                    String[] genes = ids[0].split(",");
                                    String[] exps = ids[1].split(",");
                                    Parameters outprm = new Parameters(thisparam);
                                    outprm.INIT_BLOCKS[0] = MoreArray.convtoArrayList(MoreArray.tointArray(genes));
                                    outprm.INIT_BLOCKS[1] = MoreArray.convtoArrayList(MoreArray.tointArray(exps));
                                    outprm.write(listremain[i]);
                                    System.out.println("output param file " + listremain[i]);
                                    out.add(lastvb);
                                    System.out.println("added " + out.size());
                                    done = true;
                                } catch (Exception e) {
                                    copyOrig(remaindir + "/" + listremain[i], listremain[i]);
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            copyOrig(remaindir + "/" + listremain[i], listremain[i]);
                            done = true;
                        }
                    }
                }
            }
        }
        System.out.println("writing " + outfile);
        TextFile.write(out, outfile);

        System.out.println(remainids);
    }

    /**
     * @param from
     * @param to
     */
    private void copyOrig(String from, String to) {
        System.out.println("out file is empty, copying original param");
        File param = new File(from);
        try {
            copyFile(param, new File(to));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param sourceFile
     * @param destFile
     * @throws IOException
     */
    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 3 || args.length == 5 || args.length == 6) {
            CollectPartial rm = new CollectPartial(args);
        } else {
            System.out.println("syntax: java DataMining.util.CollectPartial\n" +
                    "< dir with .out files>\n" +
                    "< dir with remaining parameter files>\n" +
                    "< out file>\n" +
                    "<OPTIONAL string flanking left>\n" +
                    "<OPTIONAL string flanking right>\n" +
                    "<OPTIONAL parameter file>"
            );
        }
    }

}
