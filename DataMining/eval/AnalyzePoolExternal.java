package DataMining.eval;

import DataMining.*;
import mathy.Matrix;
import mathy.stat;
import util.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * User: mjoachimiak
 * Date: Oct 23, 2009
 * Time: 4:01:38 PM
 */
public class AnalyzePoolExternal extends Program {

    boolean debug = false;
    boolean doGrids = true;
    HashMap options;
    String[] valid_args = {"-dir", "-mode", "-ext", "-cut", "-debug", "-lastdel"};//, "-block"};
    String mode;
    String dirpath;
    String autooutpath;

    ArrayList results;
    ArrayList results_max;

    String[] other_crit = {"external", "none",
            "FEMR,MSER_FEMR,MSER_FEMR_PPI,LARSRE,LARSRE_PPI,MSER_LARSRE_PPI"};
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
    int dataxdim = 80, dataydim = 200;
    double threshold = 0.5;
    /*"/labeled",
   "/cooccur",*/
    String[] reqdirs = {
            "/hamming",
            "/true1",
            "/true2",
            "/true3",
            "/true4",
    };

    Evaluate eval;

    boolean uselastdel = false;
    double[][] ROCdata_spec, ROCdata_sens;
    double[] thresholds;


    /**
     * @param args
     */
    public AnalyzePoolExternal(String[] args) {
        options = MapArgOptions.maptoMap(args, valid_args);

        dirpath = (String) options.get("-dir");
        mode = (String) options.get("-mode");
        String externalpath = (String) options.get("-ext");
        Object o = options.get("-cut");
        if (o != null)
            try {
                threshold = (Double) options.get("-cut");
            } catch (Exception e) {
                //e.printStackTrace();
                String so = (String) o;
                if (so.equals("all") || so.equals("roc"))
                    threshold = Double.NaN;
                else {
                    System.out.println("threshold value not recognized " + so);
                }
            }
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

        if (!Double.isNaN(threshold)) {
            doOne();
        } else {

            double maxthresh = 1.05;
            double step = 0.05;
            thresholds = new double[(int) (maxthresh / step)];
            int count = 0;
            double start = 0.0;
            for (double d = start; d < maxthresh; d += step) {
                thresholds[count] = d;
                count++;
            }
            ROCdata_spec = new double[use_crit_labels.length][thresholds.length];
            ROCdata_sens = new double[use_crit_labels.length][thresholds.length];

            for (double d = start; d < maxthresh; d += step) {
                threshold = d;
                doOne();
            }

            int index = dirpath.lastIndexOf("/");
            String trunclabel = dirpath;
            if (index != -1)
                trunclabel = dirpath.substring(0, index);
            String outspec = "./" + trunclabel + "specificity.txt";
            String[] str = StringUtil.truncate(MoreArray.toStringArray(thresholds), 4);
            TabFile.write(MoreArray.toString(ROCdata_spec), outspec,
                    str, use_crit_labels);
            String outsens = "./" + trunclabel + "sensitivity.txt";
            TabFile.write(MoreArray.toString(ROCdata_spec), outsens, str, use_crit_labels);
        }
    }

    /**
     *
     */
    private void doOne() {
        String s1 = "" + threshold;
        autooutpath = dirpath + "_sum_poolmax_" + s1.substring(0, Math.min(s1.length(), 4));
        File outpath = new File(autooutpath);
        boolean created = outpath.mkdir();
        System.out.println("created dir " + created + "\t" + outpath);

        File dir = new File(dirpath);
        File t1 = null;
        int len = 4;
        if (mode.equals("const") || mode.equals("incr") || mode.indexOf("nono") != -1) {
            len = reqdirs.length;
        }
        for (int i = 0; i < len; i++) {
            String s = outpath + reqdirs[i];
            t1 = new File(s);
            boolean created2 = t1.mkdir();
            System.out.println("created dir " + created2 + "\t" + s);
        }

        if (dir.exists()) {
            String[] dirlist = dir.list();
            if (debug)
                System.out.println("doOne dir size " + dirlist.length);
            if (dirlist != null) {
                if (mode.equals("basic")) {
                    refblock = EvaluateBasic.true1;
                    refblock_index = 0;
                    run(dirlist);
                    refblock_index = 1;
                    refblock = EvaluateBasic.true2;
                    run(dirlist);
                } else if (mode.equals("const")) {
                    refblock = EvaluateConst.true1;
                    refblock_index = 0;
                    run(dirlist);
                    refblock = EvaluateConst.true2;
                    refblock_index = 1;
                    run(dirlist);
                    refblock = EvaluateConst.true3;
                    refblock_index = 2;
                    run(dirlist);
                    refblock = EvaluateConst.true4;
                    refblock_index = 3;
                    run(dirlist);
                } else if (mode.equals("incr")) {
                    refblock = EvaluateIncr.true1;
                    refblock_index = 0;
                    run(dirlist);
                    refblock = EvaluateIncr.true2;
                    refblock_index = 1;
                    run(dirlist);
                    refblock = EvaluateIncr.true3;
                    refblock_index = 2;
                    run(dirlist);
                    refblock = EvaluateIncr.true4;
                    refblock_index = 3;
                    run(dirlist);
                } else if (mode.equals("constnono")) {
                    refblock = EvaluateConstNono.true1;
                    refblock_index = 0;
                    run(dirlist);
                    refblock = EvaluateConstNono.true2;
                    refblock_index = 1;
                    run(dirlist);
                    refblock = EvaluateConstNono.true3;
                    refblock_index = 2;
                    run(dirlist);
                    refblock = EvaluateConstNono.true4;
                    refblock_index = 3;
                    run(dirlist);
                } else if (mode.equals("incrnono")) {
                    refblock = EvaluateIncrNono.true1;
                    refblock_index = 0;
                    run(dirlist);
                    refblock = EvaluateIncrNono.true2;
                    refblock_index = 1;
                    run(dirlist);
                    refblock = EvaluateIncrNono.true3;
                    refblock_index = 2;
                    run(dirlist);
                    refblock = EvaluateIncrNono.true4;
                    refblock_index = 3;
                    run(dirlist);
                } else if (mode.equals("constnonorand")) {
                    refblock = EvaluateConstNonoRand.true1;
                    refblock_index = 0;
                    run(dirlist);
                    refblock = EvaluateConstNonoRand.true2;
                    refblock_index = 1;
                    run(dirlist);
                    refblock = EvaluateConstNonoRand.true3;
                    refblock_index = 2;
                    run(dirlist);
                    refblock = EvaluateConstNonoRand.true4;
                    refblock_index = 3;
                    run(dirlist);
                } else if (mode.equals("incrnonorand")) {
                    refblock = EvaluateIncrNonoRand.true1;
                    refblock_index = 0;
                    run(dirlist);
                    refblock = EvaluateIncrNonoRand.true2;
                    refblock_index = 1;
                    run(dirlist);
                    refblock = EvaluateIncrNonoRand.true3;
                    refblock_index = 2;
                    run(dirlist);
                    refblock = EvaluateIncrNonoRand.true4;
                    refblock_index = 3;
                    run(dirlist);
                }
            } else {
                System.out.println("AnalyzePoolExternal empty dirpath " + dirpath);
                System.exit(0);
            }
        } else {
            System.out.println("AnalyzePoolExternal broken dirpath " + dirpath);
            System.exit(0);
        }
        /* runShell rs = new runShell();
        String e = "tar -zcf " + autooutpath +
                ".tar.gz " + autooutpath;
        System.out.println(e);
        rs.execute(e);*/
    }


    /**
     * @param dirs
     */
    private void run(String[] dirs) {
        int count = 0;
        int max = debug ? 1 : use_crit_labels.length;
        //for all criteria and sets of criteria
        for (int n = 0; n < max; n++) {
            System.out.println("USE_CRIT " + use_crit_labels[n] +
                    "\tthreshold " + threshold + "\trefblock " + refblock_index);
            //(use_crit[n].length > 1 ? MoreArray.toString(use_crit[n], ",") : use_crit[n])
            labels = new ArrayList();
            results = new ArrayList();
            results_max = new ArrayList();

            files_to_blocks = new HashMap();
            curfiles = new ArrayList();
            //for all dirs of dirs
            for (int a = 0; a < dirs.length; a++) {
                String curdirstr = dirpath + "/" + dirs[a];
                File curdir = new File(curdirstr);
                String[] files = curdir.list();
                if (debug) {
                    System.out.println("run " + n + "\t" + a + "\t" + use_crit_labels[n]);
                    System.out.println("run curdir " + curdirstr);
                    if (files != null)
                        System.out.println("run dir size " + files.length);
                }
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
                            int type = Evaluate.getExtMethod(files[i]);
                            String typelabel = Evaluate.external_methods[type];
                            String externalmax = "";
                            try {
                                externalmax = typelabel + "_" + eval.maxexternal[type][refblock_index] + "_";
                            } catch (Exception e) {
                                System.out.println("run type " + type + "\trefblock_index " + refblock_index);
                                e.printStackTrace();
                            }

                            if (files[i].indexOf(externalmax) != -1) {
                                //System.out.println("run file has to match " + externalmax + "\t*\t" + files[i]);
                                //System.out.println("run including " + files[i]);
                                String path = curdirstr + sysRes.file_separator + files[i];
                                if (type == -1)
                                    System.out.println("run bad path/file " + path);
                                globalType = typelabel;
                                if (debug)
                                    System.out.println("run " + typelabel);
                                int start = files[i].indexOf("__") + 2;
                                int stop = files[i].indexOf("__", start);
                                String str1 = files[i].substring(start, stop);

                                if (debug)
                                    System.out.println("run criterion " + str1);
                                int label_index = MoreArray.getArrayInd(MINER_STATIC.CRIT_LABELS, str1);
                                int ind = -1;
                                try {
                                    //ind = MoreArray.getArrayInd(use_crit_labels, MINER_STATIC.CRIT_LABELS[label_index]);
                                    ind = use_crit_labels[n].equals(MINER_STATIC.CRIT_LABELS[label_index]) ? 1 : -1;
                                } catch (Exception e) {
                                    System.out.println("run use_crit problem " + label_index);
                                    e.printStackTrace();
                                }

                                if (use_crit_labels[n].equals("none") ||
                                        (use_crit_labels[n].equals("external")) ||
                                        ind != -1) {
                                    //System.out.println("use_crit " + MINER_STATIC.CRIT_LABELS[label_index]);
                                    //System.out.println("ValueBlockList " + path);
                                    ValueBlockList vbl = null;
                                    try {
                                        vbl = ValueBlockList.read(path, false);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    //System.out.println("ValueBlockList " + vbl.size());
                                    if (vbl == null || vbl.size() == 0) {
                                        System.out.println("ValueBlockList is empty " + path);
                                    } else if (vbl.size() > 0) {
                                        labels.add(typelabel);
                                        //System.out.println("label " + labels[results.size()]);
                                        int geneindex = -1;
                                        int position = vbl.size() - 1;

                                        if (use_crit_labels[n].equals("external"))
                                            position = 0;
                                        else if (uselastdel) {
                                            int deleteind = ValueBlockListMethods.lastBlockBeforeDeletion(vbl);
                                            position = deleteind != -1 ? deleteind : vbl.size() - 1;
                                        }

                                        ValueBlock curblock = (ValueBlock) vbl.get(position);
                                        //System.out.println("external " + externalmax + "\t" + curblock.blockId());
                                        //System.out.println("external " + files[i]);
                                        files_to_blocks.put(files[i], curblock.blockId());
                                        curfiles.add(files[i]);

                                        double[] add = BlockMethods.summarizeResults(refblock, curblock, typelabel,
                                                geneindex, vbl, files[i], dataydim, dataxdim, use_crit_labels[n], false);
                                        results.add(add);
                                        /* if ((cur_use_crit[0].equals("external"))) {
                                            System.out.println("AnalyzePoolExternal run "+files[i]);
                                            System.out.println("g spec " + add[19] + "\tsens " + add[20]);
                                            System.out.println("e spec " + add[22] + "\tsens " + add[23]);
                                            System.out.println("ge spec " + add[24] + "\tsens " + add[25]);

                                        }*/
                                        /* System.out.println("external add");
                                        MoreArray.printArray(add);*/
                                        /*int positionmax = AnalyzeBlock.findMaxGeneExperimentF1(refblock, vbl);
                                        double[] addmax = MoreArray.initArray(add.length, 0.0);
                                        if (positionmax != -1) {
                                            ValueBlock curblockmax = (ValueBlock) vbl.get(positionmax);
                                            addmax = BlockMethods.summarizeResults(refblock, curblockmax, typelabel, geneindex, vbl);
                                        }
                                        results_max.add(addmax);*/
                                    }
                                }
                            }
                        }
                    }
            }
            System.out.println("run means.size() b/f pool " + results.size());
            String s = createRefLabel();
            pool(use_crit_labels[n], s);
            String label = StringUtil.replace(use_crit_labels[n], ",", "_") + "__" + s;
            if (StringUtil.countOccur(use_crit_labels[n], ",") > 0)
                label = "combined__" + s;
            writeMeanSD(label, s);
        }
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
     * @param crit
     * @param reflabel
     */
    private void pool(String crit, String reflabel) {
        String[] labelStr = MoreArray.convtoString(labels);
        means = new ArrayList();
        SDs = new ArrayList();

        //for all external methods
        for (int i = 0; i < Evaluate.external_methods.length; i++) {
            ArrayList blockIds = new ArrayList();
            String curExternalMethod = Evaluate.external_methods[i];
            if (debug)
                System.out.println("pool " + curExternalMethod);

            for (int j = 0; j < labelStr.length; j++) {
                //System.out.println("pool " + labelStr[j] + "\t" + curExternalMethod + "\t" + curfiles.get(j));
                if (labelStr[j] != null && labelStr[j].equals(curExternalMethod)) {
                    /*if ((criterion.equals("external"))) {
                        System.out.pritln("pool " + labelStr[j] + "\t" + curExternalMethod + "\t" + curfiles.get(j));
                    }*/
                    //tmp.add((double[]) results.get(j));
                    blockIds.add(files_to_blocks.get(curfiles.get(j)));
                    //System.out.println("pool " + curExternalMethod + "\t" + reflabel + "\t" +
                    //        curfiles.get(j) + "\n" + files_to_blocks.get(curfiles.get(j)));
                    if (crit.equals("external")) {
//                        System.out.println("pool " + curExternalMethod + "\t" + reflabel + "\t" +
//                                curfiles.get(j) + "\n" + files_to_blocks.get(curfiles.get(j)));
                        break;
                    }
                    //System.out.println("pool YES");
                }
            }
            int len = (MINER_STATIC.HEADER_EVAL.split(",")).length - 1;
            double[] add = MoreArray.initArray(len, 0.0);
            double[] sds = MoreArray.initArray(len, 0.0);
            if (blockIds.size() > 0) {
                int[][] griddata = new int[dataydim][dataxdim];
                System.out.println("pool " + dataydim + "\t" + dataxdim);
                int length = blockIds.size();
                if (crit.equals("external") && length > 0) {
                    length = 1;
                }
                if (length == 0) {
                    System.out.println("pool 0 results " + curExternalMethod);
                }
                for (int j = 0; j < length; j++) {
                    String s = (String) blockIds.get(j);
                    griddata = MarkGrid.sumBlockonGrid(griddata, s);
                }

                if (doGrids) {
                    //createLabeledGrid(criterion, blockIds, i, griddata, reflabel);
                    generateHamming(crit, blockIds, i, reflabel);
                    //createCooccurGrids(criterion, blockIds, i, reflabel);
                }

                int max = Matrix.findMax(griddata);
                int cutoff = (int) (max * threshold);
                /*if (criterion.equals("external")) {
                    cutoff = 0;
                }*/
                //System.out.println("pool: max " + max + "\tcutoff " + cutoff);
                if (!crit.equals("external"))
                    griddata = Matrix.filterBelowtoZero(griddata, cutoff, 0);

                ArrayList genes = new ArrayList();
                ArrayList exps = new ArrayList();

                int[][] accepted = new int[griddata.length][griddata[0].length];
                for (int j = 0; j < griddata.length; j++) {
                    for (int k = 0; k < griddata[0].length; k++) {
                        if (griddata[j][k] > 0 && accepted[j][k] == 0) {
                            if (genes.indexOf("" + (j + 1)) == -1)
                                genes.add("" + (j + 1));
                            if (exps.indexOf("" + (k + 1)) == -1)
                                exps.add("" + (k + 1));
                            accepted[j][k] = 1;
                        }
                    }
                }
                String pooledIds = null;

                if (genes.size() > 0 && exps.size() > 0) {
                    pooledIds = MoreArray.toString(MoreArray.ArrayListtoString(genes), ",")
                            + "/" + MoreArray.toString(MoreArray.ArrayListtoString(exps), ",");
                    //if ((criterion.equals("external"))) {
                    System.out.println("pool max " + max);
                    System.out.println("pool avg " + Matrix.avg(griddata));
                    System.out.println("pool min non 0 " + Matrix.findMinNonZero(griddata));
                    System.out.println("pool cutoff " + cutoff);
                    System.out.println("pool " + pooledIds);
                    //}
                    ValueBlock pooledBlock = new ValueBlock(pooledIds);
                    add = BlockMethods.summarizeResults(refblock, pooledBlock, null, -1, null, null, dataydim, dataxdim, null, false);

                    //if ((criterion.equals("external"))) {
                    System.out.println("pool " + curExternalMethod + "\treflabel " + reflabel + "\tcriterion " + crit);
                    System.out.println("pool g spec " + add[19] + "\tsens " + add[20]);
                    System.out.println("pool e spec " + add[22] + "\tsens " + add[23]);
                    System.out.println("poolge spec " + add[24] + "\tsens " + add[25]);
                    //}
                    /* System.out.println("add");
                    MoreArray.printArray(add);*/
                    //System.out.println("floorAndCeling "+floorAndCeling+"\tcurExternalMethod "+curExternalMethod+"\t"+add[25] +"\t"+add[26]);
                    //if (criterion.equals("external")) {
                    /*if (curExternalMethod.equals("MSER")) {
                        System.out.println("pool " + curExternalMethod + "\t" + criterion + "\t" + reflabel);
                        //System.out.println(MoreArray.toString(add, "\t"));
                        System.out.println(floorAndCeling+"\t"+"g p " + add[19] + "\r r " + add[20] + "\t\te p " +
                                add[22] + "\tr " + add[23] + "\t\tge p " + add[24] + "\tr " + add[25]);
                    }*/
                    int critindex = StringUtil.getFirstEqualsIndex(use_crit_labels, crit);

                    /*System.out.println("thresholds");
                    MoreArray.printArray(thresholds);*/
                    int threshindex = MoreArray.getArrayInd(thresholds, threshold);
                    System.out.println("pool critindex " + crit + "\t" + critindex + "\tthreshindex " + threshold + "\t" + threshindex);
                    try {
                        ROCdata_spec[critindex][threshindex] = add[24];
                    } catch (Exception e) {
                        //MoreArray.printArray(thresholds);
                        e.printStackTrace();
                    }
                    ROCdata_sens[critindex][threshindex] = add[25];
                }
            }
            means.add(add);
            SDs.add(sds);
        }
        System.out.println("pool means.size() " + means.size());
        //System.out.println("SDs.size() "+SDs.size());
    }

    /**
     * @param crit
     * @param blockIds
     * @param i
     * @param griddata
     * @param reflabel
     */
    private void createLabeledGrid(String crit, ArrayList blockIds, int i, int[][] griddata, String reflabel) {
        String[][] labeledgrid = MoreArray.initArray(griddata.length, griddata[0].length, "0");
        char[] labels = "ABCDEFGHIJKLMNOPQRSTUWXYZ".toCharArray();
        for (int j = 0; j < blockIds.size(); j++) {
            String curlabel = "";
            if (j < labels.length)
                curlabel = Character.toString(labels[j]);
            else {
                int div = (int) ((double) j / (double) labels.length);
                int mod = j % labels.length;
                curlabel = div + Character.toString(labels[mod]);
            }
            labeledgrid = MarkGrid.labelGrid(labeledgrid, (String) blockIds.get(j), curlabel);
        }

        String outf = autooutpath + sysRes.file_separator + "labeled" + sysRes.file_separator +
                crit + "_" + reflabel + "_" + Evaluate.external_methods_full[i] + "_labeledgrid.txt";

        String[] ylab = MoreArray.toStringArray(stat.createNaturalNumbers(1, dataydim + 1));
        ylab = StringUtil.prepend(ylab, "gene");
        String[] xlab = MoreArray.toStringArray(stat.createNaturalNumbers(1, dataxdim + 1));
        xlab = StringUtil.prepend(xlab, "exp");
        System.out.println("createLabeledGrid writing " + outf);
        TabFile.write(labeledgrid, outf, xlab, ylab, "\t", "\n", "");
    }

    /**
     * @param crit
     * @param blockIds
     * @param i
     * @param reflabel
     */
    private void generateHamming(String crit, ArrayList blockIds, int i, String reflabel) {
        String[] ylab = MoreArray.toStringArray(stat.createNaturalNumbers(0, dataydim));
        ylab = StringUtil.prepend(ylab, "gene");
        String outfh = autooutpath + sysRes.file_separator + "hamming" + sysRes.file_separator +
                crit + "_" + reflabel + "_" + Evaluate.external_methods_full[i] + "_Hamming.txt";

        ArrayList blockgenes = new ArrayList();
        if (debug)
            System.out.println("generateHamming blockIds.size() " + blockIds.size() + "\t" + crit + "\t" + reflabel);
        int size = blockIds.size();
        for (int j = 0; j < size; j++) {
            String curblock = (String) blockIds.get(j);
            curblock = curblock.split("/")[0];
            String[] str = curblock.split(",");
            //MoreArray.printArray(str);
            int[] in = MoreArray.tointArray(str);
            if (debug) {
                System.out.println("generateHamming genes " + j + "\t" + in.length);
                MoreArray.printArray(in);
            }
            blockgenes.add(in);
        }

        ArrayList indices = new ArrayList();
        for (int j = 0; j < dataydim; j++) {
            int[] index = new int[blockIds.size()];
            for (int k = 0; k < blockIds.size(); k++) {
                int[] curgs = (int[]) blockgenes.get(k);
                try {
                    int curindex = MoreArray.getArrayInd(curgs, j + 1);
                    if (curindex != -1) {
                        index[k] = 1;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (debug) {
                System.out.println("generateHamming index " + j + "\t" + stat.sumEntries(index) + "\t" + size);
                MoreArray.printArray(index);
            }
            indices.add(index);
        }
        int[][] dist = new int[dataydim][dataydim];
        int count = 0;
        for (int j = 0; j < dataydim; j++) {
            int[] a = (int[]) indices.get(j);
            for (int k = j + 1; k < dataydim; k++) {
                int[] b = (int[]) indices.get(k);
                dist[j][k] = stat.Hamming(a, b);
                dist[k][j] = dist[j][k];
                if (debug) {// && count < 10)
                    if (dist[j][k] != 0)
                        System.out.println("generateHamming dist " + j + "\t" + k + "\t" + dist[j][k]);
                }
                count++;
            }
        }
        System.out.println("generateHamming writing " + outfh);
        TabFile.write(MoreArray.toString(dist, "", ""), outfh, ylab, ylab);
    }

    /**
     * @param crit
     * @param blockIds
     * @param i
     * @param reflabel
     */
    private void createCooccurGrids(String crit, ArrayList blockIds, int i, String reflabel) {

        int[][] cgenes = BlockMethods.cooccurGenes(blockIds, dataydim);
        int[][] cexps = BlockMethods.cooccurExps(blockIds, dataxdim);
        int[][] cgeneexps = BlockMethods.cooccurGenesandExps(blockIds, dataydim, dataxdim);

        String outfg = autooutpath + sysRes.file_separator + "cooccur" + sysRes.file_separator +
                crit + "_" + reflabel + "_" + Evaluate.external_methods_full[i] + "_genecooccur.txt";
        String outfe = autooutpath + sysRes.file_separator + "cooccur" + sysRes.file_separator +
                crit + "_" + reflabel + "_" + Evaluate.external_methods_full[i] + "_expcooccur.txt";
        String outfge = autooutpath + sysRes.file_separator + "cooccur" + sysRes.file_separator +
                crit + "_" + reflabel + "_" + Evaluate.external_methods_full[i] + "_geneexpcooccur.txt";
        String[] ylab = MoreArray.toStringArray(stat.createNaturalNumbers(0, dataydim));
        ylab = StringUtil.prepend(ylab, "gene");
        String[] xlab = MoreArray.toStringArray(stat.createNaturalNumbers(0, dataxdim));
        xlab = StringUtil.prepend(xlab, "exp");
        String[] zlab = MoreArray.toStringArray(stat.createNaturalNumbers(0, dataydim * dataxdim));
        zlab = StringUtil.prepend(zlab, "geneexp");
        System.out.println("createCooccurGrids " + outfg + "\n" + outfe + "\n" + outfge);
        TabFile.write(MoreArray.toString(cgenes, "", ""), outfg, ylab, ylab, "\t", "\n", "");
        TabFile.write(MoreArray.toString(cexps, "", ""), outfe, xlab, xlab, "\t", "\n", "");
        TabFile.write(MoreArray.toString(cgeneexps, "", ""), outfge, zlab, zlab, "\t", "\n", "");
    }

    /**
     * @param label
     * @param pathlabel
     */
    private void writeMeanSD(String label, String pathlabel) {
        ArrayList tmp = MoreArray.convtoArrayList(MINER_STATIC.HEADER_EVAL.split("\t"));
        tmp.remove(0);
        String[] tmpstr = MoreArray.ArrayListtoString(tmp);
        String tmptmpstr = MoreArray.toString(tmpstr, "\t");
        try {
            String outm = autooutpath + sysRes.file_separator + pathlabel + sysRes.file_separator + label + "_analyze.txt";
            System.out.println("writeMeanSD generating " + outm);
            PrintWriter pw = new PrintWriter(new FileWriter(outm), true);
            pw.println(MINER_STATIC.HEADER_ROC + "\t" + tmptmpstr);
            for (int i = 0; i < means.size(); i++) {
                double[] doubles = (double[]) means.get(i);
                double[] doubles2 = (double[]) SDs.get(i);
                if (doubles.length > 0 && doubles2.length > 0)
                    pw.println(Evaluate.external_methods[i] + "\t" + MoreArray.toString(doubles, "\t") +
                            "\t" + MoreArray.toString(doubles2, "\t"));
                else if (doubles.length > 0)
                    pw.println(Evaluate.external_methods[i] + "\t" + MoreArray.toString(doubles, "\t"));
                else
                    pw.println(Evaluate.external_methods[i]);
            }
            pw.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
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
        if (args.length == 6 || args.length == 8 || args.length == 10 || args.length == 12) {
            AnalyzePoolExternal arg = new AnalyzePoolExternal(args);
        } else {
            System.out.println("syntax: java DataMining.eval.AnalyzePoolExternal\n" +
                            "<-dir relative path to dir of toplists>\n" +
                            "<-mode basic,const,incr>\n" +
                            "<-ext bicluster results>\n" +
                            "<-cut floorAndCeling for pooling default = 0.5>\n" +
                            "<-lastdel y/n>\n" +
                            "<-debug y/n>"
                    //"<-block 1 or 2>"
            );
        }
    }
}
