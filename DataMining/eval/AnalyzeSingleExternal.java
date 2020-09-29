package DataMining.eval;

import DataMining.*;
import util.MapArgOptions;
import util.MoreArray;
import util.Program;
import util.StringUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: marcinjoachimiak
 * Date: May 25, 2010
 * Time: 7:04:46 PM
 */
public class AnalyzeSingleExternal extends Program {

    boolean debug = false;
    HashMap options;
    String[] valid_args = {"-dir", "-mode", "-ext", "-start", "-debug", "-lastdel"};//, "-block"};
    String mode;
    String dirpath;
    String autooutpath;

    String start;

    ArrayList results;
    ArrayList results_max;

    String[] other_crit = {"external", "none",
            "GEERE,MSER_GEERE,MSER_GEERE_PPI,LARSRE,LARSRE_PPI,MSER_LARSRE_PPI"};
    //zero indexed
    int[] combined_crit = {2};
    String[] use_crit_labels = StringUtil.splice(other_crit, MINER_STATIC.CRIT_LABELS);

    ArrayList labels;
    String[] taildata;

    String globalType;

    ArrayList means, SDs;
    //ArrayList means_max, SDs_max;

    int block = 1;
    ValueBlock refblock;
    int refblock_index = 0;

    HashMap files_to_blocks;
    ArrayList curfiles;
    int dataxdim = 40, dataydim = 100;

    Evaluate eval;

    boolean uselastdel = false;


    /**
     * @param args
     */
    public AnalyzeSingleExternal(String[] args) {
        options = MapArgOptions.maptoMap(args, valid_args);

        dirpath = (String) options.get("-dir");
        mode = (String) options.get("-mode");
        String externalpath = (String) options.get("-ext");

        Object dbg = options.get("-debug");
        if (dbg != null && ((String) dbg).equalsIgnoreCase("y")) {
            debug = true;
        }
        Object ld = options.get("-lastdel");
        if (dbg != null && ((String) ld).equalsIgnoreCase("y")) {
            uselastdel = true;
        }
        if (mode.indexOf("nono") != -1) {
            dataydim = 200;
            dataxdim = 80;
        }

        setupEval(externalpath);

        doOne();
    }

    /**
     *
     */
    private void doOne() {

        File dir = new File(dirpath);

        if (dir.exists()) {
            String[] dirlist = dir.list();
            if (debug)
                System.out.println("doOne dir size " + dirlist.length);
            if (dirlist != null) {
                runNew(dirlist, dirpath.substring(dirpath.lastIndexOf("/") + 1, dirpath.length()) + "_summary.txt");
            } else {
                System.out.println("AnalyzePoolExternal empty dirpath " + dirpath);
                System.exit(0);
            }
        } else {
            System.out.println("AnalyzePoolExternal broken dirpath " + dirpath);
            System.exit(0);
        }
    }

    /**
     * @param i
     */
    private void setRefBlock(int i) {
        if (mode.equals("basic")) {
            if (i == 0)
                refblock = EvaluateBasic.true1;
            else if (i == 1)
                refblock = EvaluateBasic.true2;
        } else if (mode.equals("const")) {
            if (i == 0)
                refblock = EvaluateConst.true1;
            else if (i == 1)
                refblock = EvaluateConst.true2;
            else if (i == 2)
                refblock = EvaluateConst.true3;
            else if (i == 3)
                refblock = EvaluateConst.true4;
        } else if (mode.equals("incr")) {
            if (i == 0)
                refblock = EvaluateIncr.true1;
            else if (i == 1)
                refblock = EvaluateIncr.true2;
            else if (i == 2)
                refblock = EvaluateIncr.true3;
            else if (i == 3)
                refblock = EvaluateIncr.true4;
        } else if (mode.equals("constnono")) {
            if (i == 0)
                refblock = EvaluateConstNono.true1;
            else if (i == 1)
                refblock = EvaluateConstNono.true2;
            else if (i == 2)
                refblock = EvaluateConstNono.true3;
            else if (i == 3)
                refblock = EvaluateConstNono.true4;
        } else if (mode.equals("incrnono")) {
            if (i == 0)
                refblock = EvaluateIncrNono.true1;
            else if (i == 1)
                refblock = EvaluateIncrNono.true2;
            else if (i == 2)
                refblock = EvaluateIncrNono.true3;
            else if (i == 3)
                refblock = EvaluateIncrNono.true4;
        } else if (mode.equals("constnonorand")) {
            if (i == 0)
                refblock = EvaluateConstNonoRand.true1;
            else if (i == 1)
                refblock = EvaluateConstNonoRand.true2;
            else if (i == 2)
                refblock = EvaluateConstNonoRand.true3;
            else if (i == 3)
                refblock = EvaluateConstNonoRand.true4;
        } else if (mode.equals("incrnonorand")) {
            if (i == 0)
                refblock = EvaluateIncrNonoRand.true1;
            else if (i == 1)
                refblock = EvaluateIncrNonoRand.true2;
            else if (i == 2)
                refblock = EvaluateIncrNonoRand.true3;
            else if (i == 3)
                refblock = EvaluateIncrNonoRand.true4;
        }
    }

    /**
     * @param dirs
     */
    private void runNew(String[] dirs, String out) {

        try {
            PrintWriter pw = new PrintWriter(new FileWriter(out), true);

            pw.println("label\tcriterion\texternal\tstart\tseed\ttrue\t" +
                    MINER_STATIC.HEADER_ROC.substring(MINER_STATIC.HEADER_ROC.indexOf("\t") + 1));
            /*for (int i = 0; i < use_crit_labels.length; i++) {
        for (int j= 0; i < use_crit_labels.length; i++) {*/
            int count = 0;
            //for all dirs in dir
            for (int a = 0; a < dirs.length; a++) {
                String curdirstr = dirpath + "/" + dirs[a];
                File curdir = new File(curdirstr);
                String[] files = curdir.list();
                if (files != null)
                    //for all files in dir
                    for (int i = 0; i < files.length; i++) {
                        if (files[i].endsWith("_toplist.txt")) {
                            if (count % 1000 == 0) {
                                System.out.print(".");
                            }
                            count++;
                            if (debug)
                                System.out.println("run " + i + "\t" + files[i]);

                            int[] mappings = parseFile(files[i]);

                            String typelabel = Evaluate.external_methods[mappings[1]];
                            int geneindex = -1;

                            ValueBlockList vbl = ValueBlockList.read(curdirstr + "/" + files[i], false);
                            if (vbl != null && vbl.size() > 0) {
                                int position = vbl.size() - 1;

                                if (use_crit_labels[mappings[0]].equals("external"))
                                    position = 0;
                                else if (uselastdel) {
                                    int deleteind = ValueBlockListMethods.lastBlockBeforeDeletion(vbl);
                                    position = deleteind != -1 ? deleteind : vbl.size() - 1;
                                }

                                ValueBlock curblock = (ValueBlock) vbl.get(position);
                                //System.out.println("external " + externalmax + "\t" + curblock.blockId());
                                //System.out.println("external " + files[i]);
                                for (int r = 0; r < 4; r++) {

                                    setRefBlock(r);

                                    refblock_index = r;

                                    double[] add = BlockMethods.summarizeResults(refblock, curblock, typelabel,
                                            geneindex, vbl, files[i], dataydim, dataxdim, use_crit_labels[mappings[0]], false);

                                    String label = use_crit_labels[mappings[0]] + "_" + Evaluate.external_methods[mappings[1]] + "_" +
                                            mappings[2] + "_" + MINER_STATIC.RANDOM_SEEDS[mappings[3]];
                                    pw.println(label + "\t" + use_crit_labels[mappings[0]] + "\t" +
                                            Evaluate.external_methods[mappings[1]] + "\t" +
                                            mappings[2] + "\t" + MINER_STATIC.RANDOM_SEEDS[mappings[3]] + "\t" + (r + 1) + "\t" +
                                            MoreArray.toString(add, "\t")
                                    );
                                }
                            }
                        }
                    }
            }
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 0 = criteria
     * 1 = ext method
     * 2 = start
     * 3 = seed
     * 4 = true 1-4
     *
     * @param s
     * @return
     */
    public int[] parseFile(String s) {
        int[] ret = new int[5];

        int startcrit = s.indexOf("__");
        int stopcrit = s.indexOf("__", startcrit + 1);
        ret[0] = MoreArray.getArrayInd(use_crit_labels, s.substring(startcrit + 2, stopcrit));
        int extindex = Evaluate.getExtMethod(s);
        ret[1] = extindex;
        ret[2] = Integer.parseInt(s.substring(s.indexOf(Evaluate.external_methods[ret[1]]) +
                Evaluate.external_methods[ret[1]].length() + 1, startcrit));
        int next = s.indexOf("_", stopcrit + 2);
        next = s.indexOf("_", next + 1);
        int end = s.indexOf("_", next + 1);

        try {
            ret[3] = MoreArray.getArrayInd(MINER_STATIC.RANDOM_SEEDS, Integer.parseInt(s.substring(next + 1, end)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("parseFile " + s);
        MoreArray.printArray(ret);
        return ret;
    }


    /**
     * @return
     */
    private String createRefLabel() {
        String s = null;
        if (mode.equals("basic")) {
            s = refblock == EvaluateBasic.true1 ? "true1" : "true2";
        } else if (mode.equals("const")) {
            if (refblock == EvaluateConst.true1) {
                s = "true1";
            } else if (refblock == EvaluateConst.true2) {
                s = "true2";
            } else if (refblock == EvaluateConst.true3) {
                s = "true3";
            } else if (refblock == EvaluateConst.true4) {
                s = "true4";
            }
        } else if (mode.equals("incr")) {
            if (refblock == EvaluateIncr.true1) {
                s = "true1";
            } else if (refblock == EvaluateIncr.true2) {
                s = "true2";
            } else if (refblock == EvaluateIncr.true3) {
                s = "true3";
            } else if (refblock == EvaluateIncr.true4) {
                s = "true4";
            }
        } else if (mode.equals("constnono")) {
            if (refblock == EvaluateConstNono.true1) {
                s = "true1";
            } else if (refblock == EvaluateConstNono.true2) {
                s = "true2";
            } else if (refblock == EvaluateConstNono.true3) {
                s = "true3";
            } else if (refblock == EvaluateConstNono.true4) {
                s = "true4";
            }
        } else if (mode.equals("incrnono")) {
            if (refblock == EvaluateIncrNono.true1) {
                s = "true1";
            } else if (refblock == EvaluateIncrNono.true2) {
                s = "true2";
            } else if (refblock == EvaluateIncrNono.true3) {
                s = "true3";
            } else if (refblock == EvaluateIncrNono.true4) {
                s = "true4";
            }
        } else if (mode.equals("constnonorand")) {
            if (refblock == EvaluateConstNonoRand.true1) {
                s = "true1";
            } else if (refblock == EvaluateConstNonoRand.true2) {
                s = "true2";
            } else if (refblock == EvaluateConstNonoRand.true3) {
                s = "true3";
            } else if (refblock == EvaluateConstNonoRand.true4) {
                s = "true4";
            }
        } else if (mode.equals("incrnonorand")) {
            if (refblock == EvaluateIncrNonoRand.true1) {
                s = "true1";
            } else if (refblock == EvaluateIncrNonoRand.true2) {
                s = "true2";
            } else if (refblock == EvaluateIncrNonoRand.true3) {
                s = "true3";
            } else if (refblock == EvaluateIncrNonoRand.true4) {
                s = "true4";
            }
        }
        return s;
    }


    /**
     * @param externalpath
     */
    private void setupEval(String externalpath) {
        ValueBlockList refs = new ValueBlockList();
        if (mode.equals("basic")) {
            refs.add(EvaluateBasic.true1);
            refs.add(EvaluateBasic.true2);
        } else if (mode.equals("const")) {
            refs.add(EvaluateConst.true1);
            refs.add(EvaluateConst.true2);
            refs.add(EvaluateConst.true3);
            refs.add(EvaluateConst.true4);
        } else if (mode.equals("incr")) {
            refs.add(EvaluateIncr.true1);
            refs.add(EvaluateIncr.true2);
            refs.add(EvaluateIncr.true3);
            refs.add(EvaluateIncr.true4);
        } else if (mode.equals("constnono")) {
            refs.add(EvaluateConstNono.true1);
            refs.add(EvaluateConstNono.true2);
            refs.add(EvaluateConstNono.true3);
            refs.add(EvaluateConstNono.true4);
        } else if (mode.equals("incrnono")) {
            refs.add(EvaluateIncrNono.true1);
            refs.add(EvaluateIncrNono.true2);
            refs.add(EvaluateIncrNono.true3);
            refs.add(EvaluateIncrNono.true4);
        } else if (mode.equals("constnonorand")) {
            refs.add(EvaluateConstNonoRand.true1);
            refs.add(EvaluateConstNonoRand.true2);
            refs.add(EvaluateConstNonoRand.true3);
            refs.add(EvaluateConstNonoRand.true4);
        } else if (mode.equals("incrnonorand")) {
            refs.add(EvaluateIncrNonoRand.true1);
            refs.add(EvaluateIncrNonoRand.true2);
            refs.add(EvaluateIncrNonoRand.true3);
            refs.add(EvaluateIncrNonoRand.true4);
        }
        eval = new Evaluate(externalpath, refs, true, dataydim, dataxdim);
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 6 || args.length == 8 || args.length == 10) {
            AnalyzeSingleExternal arg = new AnalyzeSingleExternal(args);
        } else {
            System.out.println("syntax: java DataMining.eval.AnalyzePoolExternal\n" +
                    "<-dir relative path to dir of toplists>\n" +
                    "<-mode basic,const,incr>\n" +
                    "<-ext bicluster results>\n" +
                    "<-lastdel y/n OPTIONAL>\n" +
                    "<-debug y/n OPTIONAL>"
            );
        }
    }
}
