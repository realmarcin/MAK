package DataMining;

import DataMining.eval.Evaluate;
import mathy.SimpleMatrix;
import util.MapArgOptions;
import util.MoreArray;
import util.runShell;

import java.io.File;
import java.util.*;

/**
 * User: marcin
 * Date: Jul 20, 2009
 * Time: 1:04:00 PM
 */
public class CreateParamSet {

    //boolean debug = true;

    String[] valid_args = {
            "-param", "-in", "-out", "-max", "-samp", "-rand",
            "-gmin", "-gmax", "-emin", "-emax", "-num",
            "-dataxmax", "-dataymax", "-dataf", "-mode", "-seed", "-top", "-start", "-minsamp", "-precrits", "-crits", "-silent"
    };

    int sampledirs = 1;
    int samples = 5;//10;
    int min_samples = 0;
    //AG, DG, AE, DE
    int startingmoves = 1;
    boolean restrictToMax = false;
    Parameters prm;
    /*
 public final static String[] external_methods = {"CC", "HCL", "ISA", "eans", "OPSM", "xMotif", "SAMBA", "QUBIC"};
    */

    // based on the 4r_incr_nono dataset, NEED DIFFERENT MAX FOR OTHER DATASETS
    // !!!!NEEDS UPDATING
    int[][] maxblocks = {
            {7, 3, 1, 8, -1, -1, 1, 1},
            {4, 2, 4, 5, 7, -1, 6, 6},
            {8, 3, 3, 0, 4, -1, 16, 3},
            {4, 4, 0, 3, 6, -1, 13, 0}
    };
    // !!!!NEEDS UPDATING
    int[][] maxblocks_rand = {
            {8, 0, 1, 4, -1, -1, 4, 22},
            {5, 3, 4, 6, 3, -1, 17, 4},
            {7, 4, 3, 1, 4, -1, 11, 58},
            {9, 4, 0, 7, 6, -1, 2, 23}
    };

    boolean r_incr_nono = false, r_incr_nono_rand = false;

    int[] ignore_crit = null;

    /*omit noninverted correlation criteria and nonull*/
    /*int[] ignore_crit_all = {
            1,2,3,7,9,11,12,13,17,18,19,23,25,27,29,

            //31, 32, 33, 34,
            //35, 36, 37, 38, 39,

            40, 41,
            45, 46, 47,
            48,50,52,53,54,
    };*/

    int[] do_crit = null;//{
    //24,//"MSE"
    //180,//FEM
           /* 183,//Kendall_FEM
            184,//MSE_FEM
            182,//MSE_Kendall_FEMR

            4,//MSER
            10,//FEMR
            6,//KendallR
            93,//MSER_KendallR_FEMR

            51,//FEMC
            26,//MSEC
            67,//MSEC_FEMC
            160,//"MSEC_KendallC_FEMC",
*/

    //178//kendall
    //20,//"MSER_FEMR"

    //161//  MSEC_KendallC_FEMC_inter
    //17,//"MSER_FEMR_inter",

    //168//  MSEC_KendallC_FEMC_feat
    //170,//"MSER_FEMR_feat",

    //171 //"MSEC_KendallC_FEMC_inter_feat",
    //173 //"MSER_FEMR_inter_feat",

    //171 //"MSER_Kendall_FEMR_inter_feat",
    //93,//"MSER_Kendall_FEMR",
    //119,//"MSER_Kendall_FEMR_inter",
    //61//MSER FEMC
    //30//MSEC_FEMR
    //10, //FEMR
    //51  //FEMC
    //9 // FEMR_inter
    //95 //MSE_Kendall_LARSRE
    //77 //MSER_Kendall
    //133 //MSEC_KendallC
    //134,//MSER_Kendall_inter
    //135//MSEC_KendallC_inter
    //};

    /*omit none*/
    /*int[] ignore_crit = {
    1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,
            21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,
            41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,
            61,62,63,64,65,66,67,68,69,70,71,72,73,74,75
    };*/

    int[] pre_crits;

    /*omit all but MSER, MSER_inter, FEMR, MSER_FEMR, FEMR_inter, MSER_FEMR_inter
    * 1
    * 4
    * 10
    * 20
    * 9
    * 17
    *
    * */
    int[] ignore_crit_MSERFEMR = {
            /* 2, 3, 5, 6, 7, 8, 11, 12, 13, 14, 15, 16, 18, 19,
21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60,
61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75*/
    };

    boolean random_sizes = false;
    int gmin = -1, gmax = -1, emin = -1, emax = -1, total_blocks = -1, dataxmax = -1, dataymax = -1;
    SampleBlocks sb;
    public double percent_allowed_missing_genes = MINER_STATIC.DEFAULT_percent_allowed_missing_genes;
    public double percent_allowed_missing_exp = MINER_STATIC.DEFAULT_percent_allowed_missing_exps;
    public double percent_allowed_missing_in_block = MINER_STATIC.DEFAULT_percent_allowed_missing_in_block;

    String inparam, inblocks, outpath;
    HashMap options;

    SimpleMatrix expr_data;
    ValueBlockList exclude_list;

    int mode = 1;

    long seed = -1;//9110656;//759820;

    //boolean usetopcrit = false;

    int[] starts;

    boolean silent = false;


    /**
     * @param args
     */
    public CreateParamSet(String[] args) {

        init(args);


        ArrayList nocrit = new ArrayList();

        nocrit = MoreArray.add(nocrit, MINER_STATIC.interactCrit);
        nocrit = MoreArray.add(nocrit, MINER_STATIC.FEATURECrit);
        nocrit = MoreArray.add(nocrit, MINER_STATIC.isNoninvertCrit);
        nocrit = MoreArray.add(nocrit, MINER_STATIC.MEANCritAll);


        Set<Integer> setItems = new LinkedHashSet<Integer>(nocrit);
        nocrit.clear();
        nocrit.addAll(setItems);

        ignore_crit = MoreArray.ArrayListtoInt(nocrit);


        boolean all = false;
        if (ignore_crit != null && do_crit == null) {
            do_crit = new int[MINER_STATIC.CRIT_LABELS.length - ignore_crit.length];

            int counter = 0;
            for (int i = 0; i < MINER_STATIC.CRIT_LABELS.length; i++) {
                try {
                    if (MoreArray.getArrayInd(ignore_crit, i + 1) == -1) {
                        do_crit[counter] = i + 1;
                        counter++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //if (!usetopcrit)
            //    usetopcrit = true;
            all = true;
        }// else if (do_crit != null) {
        //   usetopcrit = true;
        //}

        if (inblocks != null || restrictToMax) {
            System.out.println("createSet " + do_crit.length);
            createSet(prm);
        } else if (!random_sizes) {
            System.out.println("!random_sizes " + do_crit.length);
            for (int k = 1; k < MINER_STATIC.CRIT_LABELS.length + 1; k++) {
                int doindex = -1;

                try {
                    if (do_crit != null)//usetopcrit &&
                        doindex = MoreArray.getArrayInd(do_crit, (k));
                    else if (all)
                        doindex = 1;

                    System.out.println("doindex " + doindex);
                    if (doindex != -1) {

                        if (pre_crits != null && pre_crits[doindex] != -1) {
                            prm.PRECRIT_TYPE_INDEX = pre_crits[doindex];
                        } else {
                            prm.PRECRIT_TYPE_INDEX = k;
                        }

                        if (seed != -1) {
                            Parameters now = new Parameters(prm);
                            if (prm.PRECRIT_TYPE_INDEX == prm.CRIT_TYPE_INDEX)
                                now.setCrit(k, true);
                            else
                                now.setCrit(k, false);
                            now.RANDOM_SEED = seed;
                            String s1 = !now.OUTPREFIX.equals("") ? now.OUTPREFIX + "_" : "";
                            now.OUTPREFIX = s1 + MINER_STATIC.CRIT_LABELS[k - 1] + "_";
                            String s = now.OUTPREFIX != null ? now.OUTPREFIX + "_" : "";
                            String outfile = outpath + "_1" + "/" + s + now.RANDOM_SEED + "_parameters.txt";
                            now.write(outfile);
                        } else {
                            for (int j = min_samples; j < samples; j++) {
                                Parameters now = new Parameters(prm);
                                if (prm.PRECRIT_TYPE_INDEX == prm.CRIT_TYPE_INDEX)
                                    now.setCrit(k, true);
                                else
                                    now.setCrit(k, false);
                                now.RANDOM_SEED = MINER_STATIC.RANDOM_SEEDS[j];
                                String s1 = !now.OUTPREFIX.equals("") ? now.OUTPREFIX + "_" : "";
                                now.OUTPREFIX = s1 + MINER_STATIC.CRIT_LABELS[k - 1] + "_";
                                String s = now.OUTPREFIX != null ? now.OUTPREFIX + "_" : "";
                                String outfile = outpath + "_1" + "/" + s + now.RANDOM_SEED + "_parameters.txt";
                                now.write(outfile);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } else if (random_sizes) {
            System.out.println("random");
            Random rand = new Random(seed);
            sb = new SampleBlocks(percent_allowed_missing_in_block, percent_allowed_missing_genes, percent_allowed_missing_exp, dataymax, dataxmax, rand, expr_data.data, silent ? false : true);
            for (int i = 0; i < total_blocks; i++) {
                //draw random size
                int gtotal = rand.nextInt(gmax - gmin + 1) + gmin;
                int etotal = rand.nextInt(emax - emin + 1) + emin;
                ValueBlockPre VBPInitial = new ValueBlockPre(gtotal, etotal);
                VBPInitial = BlockMethods.createRandomBlock(VBPInitial, dataymax, dataxmax, rand, false);

                if (exclude_list != null)
                    while (exclude_list.contains(VBPInitial)) {
                        gtotal = rand.nextInt(gmax - gmin + 1) + gmin;
                        etotal = rand.nextInt(emax - emin + 1) + emin;
                        VBPInitial = new ValueBlockPre(gtotal, etotal);
                        VBPInitial = BlockMethods.createRandomBlock(VBPInitial, dataymax, dataxmax, rand, false);
                    }

                for (int j = min_samples; j < samples; j++) {
                    for (int k = 1; k < MINER_STATIC.CRIT_LABELS.length + 1; k++) {
                        if (!silent) System.out.print(".");
                        int doindex = -1;
                        try {
                            if (do_crit != null)//usetopcrit &&
                                doindex = MoreArray.getArrayInd(do_crit, (k));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (doindex != -1) {
                            Parameters now = new Parameters(prm);
                            //boolean full = false;
                            //if (prm.crit == prm.precrit)
                            //    full = true;
                            now.setCrit(k, true);
                            now.precrit = now.crit;
                            now.INIT_BLOCKS = MoreArray.initArrayListArray(2);
                            now.INIT_BLOCKS[0] = MoreArray.addElements(now.INIT_BLOCKS[0], VBPInitial.genes);
                            now.INIT_BLOCKS[1] = MoreArray.addElements(now.INIT_BLOCKS[1], VBPInitial.exps);
                            now.RANDOM_SEED = MINER_STATIC.RANDOM_SEEDS[j];
                            String s1 = !now.OUTPREFIX.equals("") ? now.OUTPREFIX + "_" : "";
                            now.OUTPREFIX = s1 + MINER_STATIC.CRIT_LABELS[k - 1] + "__" + (i + 1) + "_";
                            String outfile = outpath + "_1" + "/" + now.OUTPREFIX + now.RANDOM_SEED + "_parameters.txt";
                            now.write(outfile);
                        }
                    }
                }
            }
        }

        runShell rs = new runShell();
        String e = "";
        String paths = "";
        for (
                int n = 0;
                n < sampledirs; n++)
            paths += outpath + "_" + (n + 1) + "/ ";
        e = "tar -zcf " + outpath +
                ".tar.gz " + paths;
        if (!silent) System.out.println(e);
        rs.execute(e);
        if (sampledirs > 1)
            for (
                    int n = 0;
                    n < sampledirs; n++)

            {
                rs = new runShell();
                e = "tar -zcf " + outpath + "_" + (n + 1) +
                        ".tar.gz " + outpath + "_" + (n + 1) + "/";
                if (!silent) System.out.println(e);
                rs.execute(e);
            }

    }


    /**
     * @param args
     */

    private void init(String[] args) {

        options = MapArgOptions.maptoMap(args, valid_args);

        if (options.get("-silent") != null) {
            if (((String) options.get("-silent")).equalsIgnoreCase("F"))
                silent = false;
            else if (((String) options.get("-silent")).equalsIgnoreCase("T"))
                silent = true;

            System.out.println("silent " + silent);
        }

        if (options.get("-param") != null) {
            inparam = (String) options.get("-param");
            prm = new Parameters();
            prm.read(inparam, false);
            if (!silent) System.out.println(prm.toString());
        } else {
            if (!silent) System.out.println("no parameter file specified, quiting");
            System.exit(0);
        }

        if (options.get("-in") != null)
            inblocks = (String) options.get("-in");
        if (options.get("-out") != null) {
            outpath = (String) options.get("-out");
            if (!silent) System.out.println(outpath);
        }

        String strout = (String) options.get("-out");
        if (strout.indexOf("r_incr_nono_rand") != -1) {
            r_incr_nono_rand = true;
            r_incr_nono = false;
        } else if (strout.indexOf("r_incr_nono") != -1) {
            r_incr_nono = true;
            r_incr_nono_rand = false;
        }

        if (options.get("-max") != null) {
            //if (args.length >= 5) {
            String s = (String) options.get("-max");
            if (s.equalsIgnoreCase("y"))
                restrictToMax = true;
            else
                restrictToMax = false;
        }
        if (options.get("-samp") != null) {
            //if (args.length >= 6) {
            samples = Integer.parseInt((String) options.get("-samp"));
        }
        if (options.get("-minsamp") != null) {
            //if (args.length >= 6) {
            min_samples = Integer.parseInt((String) options.get("-minsamp"));
        }

        if (options.get("-rand") != null) {
            //if (args.length == 7) {
            String s = (String) options.get("-rand");
            if (s.equalsIgnoreCase("y"))
                random_sizes = true;
            else
                random_sizes = false;
        }

        if (options.get("-gmin") != null)
            gmin = Integer.parseInt((String) options.get("-gmin"));
        if (options.get("-gmax") != null)
            gmax = Integer.parseInt((String) options.get("-gmax"));
        if (options.get("-emin") != null)
            emin = Integer.parseInt((String) options.get("-emin"));
        if (options.get("-emax") != null)
            emax = Integer.parseInt((String) options.get("-emax"));
        if (options.get("-num") != null)
            total_blocks = Integer.parseInt((String) options.get("-num"));
        if (options.get("-dataxmax") != null) {
            dataxmax = Integer.parseInt((String) options.get("-dataxmax"));
            if (emax == -1)
                emax = dataxmax;
        }
        if (options.get("-dataymax") != null) {
            dataymax = Integer.parseInt((String) options.get("-dataymax"));
            if (gmax == -1)
                gmax = dataymax;
        }

        if (options.get("-dataf") != null) {
            expr_data = new SimpleMatrix((String) options.get("-dataf"));
        }
        if (options.get("-exclude") != null) {
            String f = (String) options.get("-exclude");
            File test = new File(f);
            if (!test.isDirectory())
                try {
                    exclude_list = ValueBlockList.read(f, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            else {
                exclude_list = new ValueBlockList();
                String[] list = test.list();
                for (int i = 0; i < list.length; i++) {
                    ValueBlockList cur = null;
                    try {
                        cur = ValueBlockList.read(f + "/" + list[i], false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    exclude_list.add(cur);
                }
            }
        }
        if (options.get("-mode") != null) {
            mode = Integer.parseInt((String) options.get("-mode"));
        }
        if (options.get("-seed") != null) {
            seed = Integer.parseInt((String) options.get("-seed"));
        }
        /*if (options.get("-top") != null) {
            usetopcrit = ((String) options.get("-top")).equalsIgnoreCase("y") ? true : false;
        }*/

        //if(!silent) System.out.println("starts " + (String) options.get("-start"));
        if (options.get("-start") != null) {
            //if(!silent) System.out.println("starts");
            String s1 = (String) options.get("-start");
            if (s1.indexOf(",") == -1) {
                starts = new int[1];
                starts[0] = Integer.parseInt(s1);
            } else
                starts = MoreArray.tointArray(s1.split(","));

            //if(!silent) System.out.println("starts");
            //MoreArray.printArray(starts);
        } //else
        //  if(!silent) System.out.println("no starts");

        /*if (mode == 1) {
            ignore_crit = ignore_crit_all;
        } else if (mode == 2) {
            ignore_crit = ignore_crit_MSERFEMR;
        }*/

       /* if (options.get("-precrit") != null) {
            precrit = ((String) options.get("-precrit"));
            prm.precrit_type = precrit;
            prm.PRECRIT_TYPE_INDEX = MoreArray.getArrayIndIgnoreCase(MINER_STATIC.CRIT_LABELS, precrit) + 1;
        }*/

        if (options.get("-precrits") != null) {
            String tmp = ((String) options.get("-precrits"));
            //System.out.println("crits " + tmp);
            if (tmp.indexOf(",") != -1) {
                String[] crits = tmp.split(",");
                //if (crits.length == 0)
                //    crits = tmp.split("_");
                pre_crits = new int[crits.length];
                for (int i = 0; i < crits.length; i++) {
                    pre_crits[i] = MoreArray.getArrayIndIgnoreCase(MINER_STATIC.CRIT_LABELS, crits[i]) + 1;
                }
            } else {
                pre_crits = new int[1];
                pre_crits[0] = MoreArray.getArrayIndIgnoreCase(MINER_STATIC.CRIT_LABELS, tmp) + 1;
                //System.out.println("crits set " + tmp + "\t" + do_crit[0]);
            }
            System.out.println("precrits");
            MoreArray.printArray(pre_crits);
        }

        if (options.get("-crits") != null) {
            String tmp = ((String) options.get("-crits"));
            //System.out.println("crits " + tmp);
            if (tmp.indexOf(",") != -1) {
                String[] crits = tmp.split(",");
                //if (crits.length == 0)
                //    crits = tmp.split("_");
                do_crit = new int[crits.length];
                for (int i = 0; i < crits.length; i++) {
                    do_crit[i] = MoreArray.getArrayIndIgnoreCase(MINER_STATIC.CRIT_LABELS, crits[i]) + 1;
                }
            } else {
                do_crit = new int[1];
                do_crit[0] = MoreArray.getArrayIndIgnoreCase(MINER_STATIC.CRIT_LABELS, tmp) + 1;
                //System.out.println("crits set " + tmp + "\t" + do_crit[0]);
            }
            //System.out.println("crits");
            //MoreArray.printArray(do_crit);
        }

        if (!silent)
            System.out.println("init: samples " + samples + "\trestrictToMax " + restrictToMax + "\trandom_sizes " + random_sizes +
                    "\tgmin " + gmin + "\tgmax " + gmax + "\temin " + emin + "\temax " + emax + "\tdataxmax " +
                    dataxmax + "\tdataymax " + dataymax + "\ttotal_blocks " + total_blocks);
    }

    /**
     * @param prm
     */
    private void createSet(Parameters prm) {
        for (int n = 0; n < sampledirs; n++) {
            String s = outpath + "_" + (n + 1);
            if (!silent) System.out.println("creating " + s);
            File outd = new File(s);
            outd.mkdir();
        }

        File dir = new File(inblocks);

        if (!dir.isDirectory()) {
            doOneFile(prm, inblocks);
        } else {
            String[] list = dir.list();

            for (int i = 0; i < list.length; i++) {
                String f = inblocks + "/" + list[i];

                doOneFile(prm, f);

            }
        }
    }

    /**
     * @param prm
     * @param f
     */
    private void doOneFile(Parameters prm, String f) {
        if (!silent) System.out.println("reading " + f);
        //System.out.println("HEY\n" + f);
        String method = f.substring(f.lastIndexOf("/") + 1, f.lastIndexOf("."));

        int methodindex = MoreArray.getArrayInd(Evaluate.external_methods_full, method);

        ValueBlockList one = null;

        try {
            one = ValueBlockListMethods.readAny(f, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*  if (f.indexOf(".bic") != -1)
        try {
            one = ValueBlockList.readBICTranslatetoInt(f);
        } catch (Exception e) {
            if(!silent) System.out.println("readBICTranslatetoInt error");
            e.printStackTrace();
        }
    else
        try {
            one = ValueBlockList.readSimple(f);
        } catch (Exception e) {
            if(!silent) System.out.println("readSimple error");
            e.printStackTrace();
        }*/

        if (!silent) System.out.println("read " + one.size());
        for (int j = 0; j < one.size(); j++) {
            long startTime = 0;
            //if (!silent)
            startTime = System.currentTimeMillis();

            boolean go1 = true;
            if (starts != null) {
                try {
                    if (MoreArray.getArrayInd(starts, j) != -1)
                        go1 = true;
                    else
                        go1 = false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (go1) {
                boolean go = (restrictToMax ? false : true);
                if (!go && r_incr_nono && (j == maxblocks[0][methodindex] || j == maxblocks[1][methodindex] ||
                        j == maxblocks[2][methodindex] || j == maxblocks[3][methodindex])) {
                    go = true;
                    if (!silent)
                        System.out.println("createSet found max " + Evaluate.external_methods_full[methodindex] + "\t" + j);
                } else if (!go && r_incr_nono_rand && (j == maxblocks_rand[0][methodindex]
                        || j == maxblocks_rand[1][methodindex] ||
                        j == maxblocks_rand[2][methodindex] || j == maxblocks_rand[3][methodindex])) {
                    go = true;
                    if (!silent)
                        System.out.println("createSet found max " + Evaluate.external_methods_full[methodindex] + "\t" + j);
                }

                if (go) {
                    ValueBlock block = (ValueBlock) one.get(j);
                    if (!silent) System.out.println("createSet " + method + "\t" + block.toStringShort());
                    for (int k = 1; k < MINER_STATIC.CRIT_LABELS.length + 1; k++) {
                        int doindex = -1;
                        try {
                            if (do_crit != null)
                                doindex = MoreArray.getArrayInd(do_crit, (k));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //if(!silent)
                        //System.out.println("createSet do_crit " + (do_crit != null ? true : false) + "\tdoindex " + doindex + "\t" + k);
                        //MoreArray.printArray(do_crit);

                        if (doindex != -1) {
                            int critindex = k;
                            //condition for criteria which require nulls and minimum amd maximum block size
                            int arrayInd = -1;
                            try {
                                arrayInd = MoreArray.getArrayInd(MINER_STATIC.nonullCrit, critindex);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if ((block.genes.length >= MINER_STATIC.MIN_BLOCK_SIZE && block.exps.length >= MINER_STATIC.MIN_BLOCK_SIZE) ||
                                    (arrayInd != -1)) {

                                for (int n = 0; n < sampledirs; n++) {
                                    for (int l = min_samples; l < samples; l++) {
                                        for (int m = 0; m < startingmoves; m++) {
                                            Parameters now = new Parameters(prm);

                                            if (pre_crits != null && pre_crits[doindex] != -1) {
                                                //System.out.println("setting pre crit " + doindex + "\t" + pre_crits[doindex] + "\t" + k);
                                                now.PRECRIT_TYPE_INDEX = pre_crits[doindex];
                                            } else {
                                                now.PRECRIT_TYPE_INDEX = k;
                                            }

                                            if (prm.PRECRIT_TYPE_INDEX != prm.CRIT_TYPE_INDEX)
                                                now.setCrit(critindex, false);
                                            else {
                                                //System.out.println("setting full crit");
                                                now.setCrit(critindex, true);
                                            }

                                            //if (!silent) System.out.println("now.OUTDIR " + now.OUTDIR);
                                            // Update code below used to be: now.OUTDIR = now.OUTDIR.substring(0, now.OUTDIR.length() - 1) + "_" + (n + 1) + "/";
                                            now.OUTDIR = now.OUTDIR;
                                            now.RANDOM_SEED = MINER_STATIC.RANDOM_SEEDS[l];
                                            String move_label = null;
                                            if (m == 0) {
                                                now.INITPA = 1;
                                                now.INITPG = 1;
                                                move_label = "AG";
                                            }
                                            /* else if (m == 3) {
                                               now.INITPA = 0;
                                               now.INITPG = 1;
                                               move_label = "DG";
                                           } else if (m == 1) {
                                               now.INITPA = 0;
                                               now.INITPG = 0;
                                               move_label = "DE";
                                           } else if (m == 2) {
                                               now.INITPA = 1;
                                               now.INITPG = 0;
                                               move_label = "AE";
                                           } */
                                            now.INIT_BLOCKS = MoreArray.initArrayListArray(2);
                                            now.INIT_BLOCKS[0] = MoreArray.addElements(now.INIT_BLOCKS[0], block.genes);
                                            now.INIT_BLOCKS[1] = MoreArray.addElements(now.INIT_BLOCKS[1], block.exps);
                                            //now.BATCH_PERC = 5.0/Math.max(now.INIT_BLOCKS[0].size(),now.INIT_BLOCKS[1].size());
                                            /* method, block, criterion, seed*/
                                            String s = !now.OUTPREFIX.equals("") ? now.OUTPREFIX + "_" : "";
                                            now.OUTPREFIX = s + method + "_" + j + "__" +
                                                    MINER_STATIC.CRIT_LABELS[k - 1].toUpperCase() + "__" + move_label + "_" + // Rauf's change made to CRIT_LABELS to uppercase
                                                    now.RANDOM_SEED + "_";
                                            String outfile = outpath + "_" + (n + 1) + "/" + now.OUTPREFIX
                                                    + "_parameters.txt";
                                            //if(debug)
                                            //if(!silent) System.out.println("outfile " + outfile);
                                            now.write(outfile);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            //if (!silent) {
            long estimatedTime = System.currentTimeMillis() - startTime;
            //System.out.println("j " + j + "\ttime " + estimatedTime);
            //}
        }
    }


    /**
     * @param args
     */

    public static void main(String[] args) {
        if (args.length >= 2 && args.length <= 40) {
            CreateParamSet rm = new CreateParamSet(args);
        } else {
            System.out.println("syntax: java DataMining.CreateParamSet\n" +
                    "<-param OPTIONAL parameter file>\n" +
                    "<-in OPTIONAL input dir or file>\n" +
                    "<-out out dir>\n" +
                    "<-max OPTIONAL y/n restrict to max for evaluation>\n" +
                    "<-samp OPTIONAL samples>\n" +
                    "<-rand OPTIONAL random_sizes y/n, exclusive with -max and -in\n" +
                    "<-gmin OPTIONAL min genes>\n" +
                    "<-gmax OPTIONAL max genes>\n" +
                    "<-emin OPTIONAL min exp>\n" +
                    "<-emax OPTIONAL max exp>\n" +
                    "<-num OPTIONAL total number of unique blocks (with -rand>\n" +
                    "<-dataxmax OPTIONAL with -rand>\n" +
                    "<-dataymax OPTIONAL with -rand>\n" +
                    "<-dataf OPTIONAL with -rand>n" +
                    "<-exclude file or dir of files OPTIONAL with -rand>\n" +
                    "<-mode OPTIONAL 1=all non null non invert criteria, 2=only MSER and FEMR combos>\n" +
                    "<-seed OPTIONAL random seed>\n" +
                    "<-top OPTIONAL y = restrict to top crit\n" +
                    "<-start OPTIONAL comma delim list of start blocks>\n" +
                    "<-precrit OPTIONAL MSE, KENDALL, MSE_KENDALL etc>\n" +
                    "<-crits OPTIONAL MSE, KENDALL, MSE_KENDALL etc>\n" +
                    "<-silent T/F default F>"

            );
        }
    }
}
