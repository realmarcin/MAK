package DataMining.util;

import DataMining.MINER_STATIC;
import DataMining.ValueBlock;
import DataMining.ValueBlockList;
import DataMining.ValueBlockListMethods;

import java.io.File;

/**
 * Class to make a single ValueBlockList output from a directory of trajectories.
 * <p/>
 * User: marcin
 * Date: Jan 26, 2011
 * Time: 10:07:27 PM
 */
public class ListfromDir {

    boolean debug = false;

    String dir, out;
    boolean firstlast = false;
    boolean geneadd = false;
    boolean expadd = false;
    boolean geneexpadd = false;
    boolean inclusive = false;

    ValueBlockList starts;
    ValueBlockList rowstarts;
    ValueBlockList colstarts;

    boolean useCol = true;

    int iteration = -1;


    /**
     *
     */
    public ListfromDir() {

    }

    /**
     * @param args
     */
    public ListfromDir(String[] args) {

        if (args.length == 3) {
            setOptions(args[2]);
        }
        if (args.length == 4) {
            try {
                starts = ValueBlockList.read(args[3], false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (args.length == 6) {
            try {
                colstarts = ValueBlockList.read(args[3], false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                rowstarts = ValueBlockList.read(args[4], false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            useCol = Integer.parseInt(args[5]) == 1 ? true : false;
        }

        dir = args[0];
        if (args.length == 2)
            out = args[1];
        else {
            int lastind = args[0].lastIndexOf("/");
            if (lastind == args[0].length() - 1) {
                out = args[0].substring(0, args[0].length() - 1) + ".vbl";
            } else
                out = args[0] + ".vbl";
        }
        run();
    }


    /**
     *
     */
    public void callDirect(String indir, String outfile, int additer) {

        dir = indir;
        out = outfile;

        iteration = additer;

        inclusive = false;
        firstlast = false;
        geneadd = false;
        expadd = false;
        geneexpadd = false;

        run();
    }

    /**
     * @param arg
     */
    private void setOptions(String arg) {
        if (arg.indexOf("i") != -1)
            inclusive = true;
        if (arg.equals("fl")) {
            firstlast = true;
        }
        if (arg.equals("l")) {
            firstlast = false;
        } else if (arg.equals("g+")) {
            geneadd = true;
        } else if (arg.equals("e+")) {
            expadd = true;
        } else if (arg.equals("g+e+")) {
            geneexpadd = true;
        }
    }


    /**
     * @param di
     * @param ou BuildGraph_results_yeast_cmonkey_ws27_29_34_ROW_round12_cut_scoreperc66.0_exprNaN_0.0__nr_0.25_root_norm__nr_0.25_score_root.txt.out
     * @param op
     */
    public ListfromDir(String di, String ou, String op) {
        dir = di;
        out = ou;
        setOptions(op);
    }

    /**
     *
     */
    public int run() {
        ValueBlockList outvbl = new ValueBlockList();
        File test1 = new File(dir);
        String[] list = test1.list();
        String s = dir + "/" + list[0];
        File test2 = new File(s);
        if (!test2.isDirectory()) {
            outvbl = runOne(dir, out, outvbl);
        } else
            for (int i = 0; i < list.length; i++) {
                outvbl = runOne(s, out, outvbl);
            }

        return outvbl.size();
    }

    /**
     * @param indir
     * @param outfile
     * @param outvbl
     * @return
     */
    private ValueBlockList runOne(String indir, String outfile, ValueBlockList outvbl) {
        File f = new File(indir);
        String[] list = f.list();

        //System.out.println("runOne " + list.length);
        for (int i = 0; i < list.length; i++) {
            System.out.println("runOne " + indir + "/" + list[i] + "\t" + i);
            ValueBlockList vbl = null;
            try {
                //System.out.println("runOne " + indir + "/" + list[i]);
                vbl = ValueBlockList.read(indir + "/" + list[i], debug);//true);
            } catch (Exception e) {
                //File rm = new File(indir + "/" + list[i]);
                //rm.delete();
                e.printStackTrace();
            }

            if (vbl != null && vbl.size() > 0) {

                boolean go = false;
                ValueBlock teststart = (ValueBlock) vbl.get(0);
                if (starts != null) {
                    if (starts.blockIndex(teststart) != -1) {
                        go = true;
                    }
                } else if (colstarts != null) {
                    int cind = colstarts.blockIndex(teststart);
                    int rind = rowstarts.blockIndex(teststart);
                    ValueBlock cv = (ValueBlock) colstarts.get(cind);
                    ValueBlock rv = (ValueBlock) rowstarts.get(rind);
                    if (cv.full_criterion >= rv.full_criterion && useCol) {
                        go = true;
                    } else if (cv.full_criterion < rv.full_criterion && !useCol) {
                        go = true;
                    }
                } else
                    go = true;

                if (go) {
                    if (firstlast) {
                        outvbl.add((ValueBlock) vbl.get(0));
                        outvbl.add((ValueBlock) vbl.get(vbl.size() - 1));
                    } else {

                        int ind = -1;
                        if (geneadd) {
                            ind = ValueBlockListMethods.lastBlockGeneAddition(vbl);
                            if (ind == -1) {
                                ind = vbl.size() - 1;
                                System.out.println("NO last gene addition");
                            }
                        } else if (expadd) {
                            ind = ValueBlockListMethods.lastBlockExpAddition(vbl);
                            if (ind == -1) {
                                ind = vbl.size() - 1;
                                System.out.println("NO last exp addition");
                            }
                        } else if (geneexpadd) {
                            ind = ValueBlockListMethods.lastBlockGeneExpAddition(vbl);
                            if (ind == -1) {
                                ind = vbl.size() - 1;
                                System.out.println("NO last gene+exp addition");
                            }
                        }

                        if (inclusive) {
                            for (int j = ind; j < vbl.size(); j++) {
                                ValueBlock cur = (ValueBlock) vbl.get(j);
                                outvbl.add(cur);
                            }
                        } else {
                            if (ind != -1)
                                for (int j = ind; j < ind + 1; j++) {
                                    ValueBlock cur = (ValueBlock) vbl.get(j);
                                    outvbl.add(cur);
                                }
                                //default adds last block
                            else {
                                //System.out.println("DEFAULT: add last");
                                ValueBlock cur = (ValueBlock) vbl.get(vbl.size() - 1);
                                outvbl.add(cur);
                            }
                        }
                    }
                }
            } else {
                System.out.println("EMPTY block list " + indir + "/" + list[i]);
            }
        }


        String header = "#";
        String s = null;
        if (iteration == -1)
            s = outvbl.toString(header + MINER_STATIC.HEADER_VBL);//ValueBlock.toStringShortColumns());
        else
            s = outvbl.toString(header + MINER_STATIC.HEADER_VBL_WITHITER, iteration);//ValueBlock.toStringShortColumns());
        util.TextFile.write(s, outfile);
        return outvbl;
    }

    /**
     * @param args
     */
    public final static void main(String[] args) {

        if (args.length == 1 || args.length == 2 || args.length == 3 || args.length == 4 || args.length == 6) {
            ListfromDir rm = new ListfromDir(args);
        } else {
            System.out.println("syntax: java DataMining.util.ListfromDir\n" +
                    "<dir of ValueBlockLists>\n" +
                    "<OPTIONAL outfile>\n" +
                    "<OPTIONAL g+, g+e+, e+, fl, add 'i' suffix for inclusive. Default = add last>\n" +
                    "<OPTIONAL column or row start file OR if args ==6 column scoring file>\n" +
                    "<OPTIONAL if args ==6 row scoring file>\n" +
                    "<OPTIONAL if args ==6 useCol=1/0 (default = 1)>"
            );
        }
    }
}
