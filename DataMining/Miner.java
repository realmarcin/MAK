package DataMining;


import mathy.stat;
import org.rosuda.JRI.RVector;
import util.MoreArray;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.*;

/**
 * Class encapsulating the associative search algorithm.
 * <p/>
 * <p/>
 * <p/>
 * User: marcin
 * Date: Jul 6, 2007
 * Time: 12:51:06 PM
 */
public class Miner {
    boolean debug = false;
    boolean debug_createPreCritTopList = false;
    boolean debug_getCriteriaForBlock = false;
    boolean debug_silent = false;

    boolean doVariance = false;

    //REXP Rexpr;
    //Rengine Rengine;
    public RunMinerBack rmb;
    /*Static variable containing the initial settings of all variables.*/
    //Parameters prm;
    /*Dynamic variable containing the current state of all algorithm parameters related to moving.*/
    MoveParams move_params;

    //ObtainNullValues onv;

    /*Variable storing a set of lists of top blocks by criteria.*/
    ValueListSet vls;
    /*Custom list object for storing the search trajectory.*/
    public ValueBlockList trajectory, finalVarListGenes, finalVarListExp;

    /*TF to target map*/
    //SimpleMatrix TFtargetmap;

    //HashMap tried_blocks;
    HashMap trajectory_blocks;

    int total_moves = 0;
    /*The initial value block object based on pre criteria.*/
    ValueBlockPre VBPInitial, VBPInitialSTART;
    /*The candidate value block object based on full criteria.*/
    ValueBlock VBPCandidate;
    /*The last accepted block*/
    ValueBlock lastBlock;

    /*Keeps track of the gene and experiment sets in the last block.*/
    String last_genes, last_experiments;

    Random rand;

    //java.util.HashMap moves_hash, precrit_moves_map, full_moves_map;

    String mode = MINER_STATIC.MOVE_CLASSES[0];
    //ArrayList miner_log = new ArrayList();
    String workdir;

    //GiveDate gd;
    long start_time;
    double total_time;


    /**
     * @param r
     */
    public Miner(RunMinerBack r) {
        super();
        if (debug)
            System.out.println("Starting Miner from RunMinerBack");
        //gd = new GiveDate();
        start_time = System.currentTimeMillis();//gd.giveMilli();
        rmb = r;
        init();
    }


    /**
     *
     */
    private void init() {
        workdir = System.getProperty("user.dir");
        if (debug)
            System.out.println("setting java seed " + rmb.irv.prm.RANDOM_SEED);
        rand = new Random(rmb.irv.prm.RANDOM_SEED);
        initDebug();
    }

    /**
     *
     */
    public void stop() {
        move_params.stop = true;
        rmb.irv.Rengine.end();
    }


    /**
     *
     */
    public void initDebug() {
        if (rmb.irv.prm.debug == -10 || rmb.irv.prm.debug == -1 || rmb.debug == -10 || rmb.debug == -1) {
            System.out.println("Setting debug mode to: silent");
            debug_silent = true;

            debug = false;
            debug_getCriteriaForBlock = false;
            debug_createPreCritTopList = false;

        } else if (rmb.irv.prm.debug > 0 || rmb.debug > 0) {
            if (rmb.irv.prm.debug >= 1 || rmb.debug >= 1) {
                debug = true;
                if (rmb.irv.prm.debug >= 2 || rmb.debug >= 2) {
                    debug_getCriteriaForBlock = true;
                    if (rmb.irv.prm.debug == 3 || rmb.debug == 3) {
                        debug_createPreCritTopList = true;
                    }
                }
            }
        }
        if (debug)
            System.out.println("Starting R initialization");
    }

    /**
     * Method which runs the algorithm.
     */
    public void run() {

        try {
            if (debug) {
                System.out.println("run start");
                System.out.println("NOT SETTING n.block variable in R");
            }
            //rmb.irv.Rexpr = Rengine.eval("counts=rep(0,n.block)"));

            /*String s1 = "I=dim(expr_data)[1]";
      System.out.println("R: " + s1);
      System.out.println(rmb.irv.Rexpr = Rengine.eval(s1));//number of rows or genesStr
      String s2 = "J=dim(expr_data)[2]";
      System.out.println("R: " + s2);
      System.out.println(rmb.irv.Rexpr = Rengine.eval(s2));//number of columns or expsStr*/
            if (rmb == null)
                initStoreVarforRun();
//printInitRVariables();
            if (debug) {
                System.out.println("Miner run rmb.irv.prm.INIT_BLOCKS: " + rmb.irv.prm.INIT_BLOCKS[0].size() + "\t" + rmb.irv.prm.INIT_BLOCKS[1].size());
                System.out.println("Miner run rmb.irv.prm.INIT_BLOCKS: genes " + MoreArray.toString(MoreArray.ArrayListtoString(rmb.irv.prm.INIT_BLOCKS[0]), ","));
                System.out.println("Miner run rmb.irv.prm.INIT_BLOCKS: exps " + MoreArray.toString(MoreArray.ArrayListtoString(rmb.irv.prm.INIT_BLOCKS[1]), ","));
            }
            boolean added_initial = false;
            VBPInitial = new ValueBlockPre(rmb.irv.prm.INIT_BLOCKS, move_params.current_move_class);
            VBPInitial.setDataAndMean(rmb.expr_matrix.data);

            //VBPInitial.removeAboveExpNaN(rmb.irv.prm.PERCENT_ALLOWED_MISSING_EXP);
            //VBPInitial.removeAboveGeneNaN(rmb.irv.prm.PERCENT_ALLOWED_MISSING_GENES);

            if (rmb.irv.prm.RANDOMIZE_BLOCKS || (rmb.irv.prm.RANDOMIZE_GENES && rmb.irv.prm.RANDOMIZE_EXPS)) {
                VBPInitial = BlockMethods.createRandomBlock(VBPInitial, rmb.irv.prm.DATA_LEN_GENES, rmb.irv.prm.DATA_LEN_EXPS, rand, false);
                VBPInitial.setDataAndMean(rmb.expr_matrix.data);
            } else if (rmb.irv.prm.RANDOMIZE_GENES) {
                VBPInitial = BlockMethods.createRandomGeneBlock(VBPInitial, rmb.irv.prm.DATA_LEN_GENES, rand);
                VBPInitial.setDataAndMean(rmb.expr_matrix.data);
            } else if (rmb.irv.prm.RANDOMIZE_EXPS) {
                VBPInitial = BlockMethods.createRandomExpBlock(VBPInitial, rmb.irv.prm.DATA_LEN_EXPS, rand);
                VBPInitial.setDataAndMean(rmb.expr_matrix.data);
            }

            /*TODO reinstante trimNaN*/
            VBPInitial.trimNAN(rmb.expr_matrix.data, rmb.irv.prm.PERCENT_ALLOWED_MISSING_GENES, rmb.irv.prm.PERCENT_ALLOWED_MISSING_EXP, debug_createPreCritTopList);

            if (VBPInitial.genes.length < rmb.irv.prm.IMIN || VBPInitial.exps.length < rmb.irv.prm.JMIN) {
                System.out.println("ERROR starting point is below the minimum data size after trimming " + rmb.irv.prm.IMIN + "\t" + rmb.irv.prm.JMIN);
                System.exit(0);
            }

            boolean aboveNaNThreshold = VBPInitial.isAboveNaN(rmb.irv.prm.PERCENT_ALLOWED_MISSING_IN_BLOCK);
            if (!aboveNaNThreshold) {
                double frxn = VBPInitial.frxnNaN();
                if (debug)
                    System.out.println("WARNING starting point exceeds the missing data limit: " + frxn + "\tlimit " +
                            rmb.irv.prm.PERCENT_ALLOWED_MISSING_IN_BLOCK);
                VBPInitial.trimNAN(rmb.expr_matrix.data, rmb.irv.prm.PERCENT_ALLOWED_MISSING_GENES, rmb.irv.prm.PERCENT_ALLOWED_MISSING_EXP, debug_createPreCritTopList);
                //System.exit(0);
            }

            VBPInitialSTART = new ValueBlockPre(VBPInitial);
            VBPInitialSTART.setDataAndMean(rmb.expr_matrix.data);

            if (rmb.irv.prm.RUN_SEQUENCE.length() > 0) {

                boolean previous_step = false;
                if (rmb.irv.prm.RUN_SEQUENCE.toLowerCase().charAt(0) != 'n')
                    previous_step = true;

                for (int i = 0; i < rmb.irv.prm.RUN_SEQUENCE.length(); i++) {
                    char cur = rmb.irv.prm.RUN_SEQUENCE.charAt(i);
                    if (cur != 'N' && cur != 'n' && cur != 'O' && cur != 'o') {
                        setMoveModeAndRun(cur, i, false);
                    } else {
                        boolean converged = false;
                        int round = 0;
                        int cursize = trajectory.size();
                        int lastsize = cursize;
                        while (!converged) {
                            setMoveModeAndRun(rmb.irv.prm.RUN_SEQUENCE.charAt(i), round, true);
                            cursize = trajectory.size();
                            if (debug) {
                                System.out.println("N/n/O/o single move mode " + round + "\t" + lastsize + "\t" + cursize);
                            }
                            if (cursize == lastsize) {
                                converged = true;
                                break;
                            } else {
                                lastsize = cursize;
                                round++;
                            }
                        }
                    }
                }
            }
            /*
            The default move sequence is
            - batch, single or mixed depending on PBATCH
            - single if not single in first step
            OBSOLETE - PLATEAU if PLATEAU, then batch or single depending on PBATCH (default = single)
            OBSOLETE - random if random, then batch or single depending on PBATCH (default = single)
            */
            /*else {
                runLoop(added_initial, new ValueBlock(VBPInitial));
                //output();
                //do single moves after batch
                if (rmb.irv.prm.PBATCH != 0.0) {// && rmb.irv.prm.PLATEAU) {
                    *//*System.out.println("single mode");
                    rmb.irv.prm.PBATCH = 0.0;
                    initMoveParam();
                    ValueBlock last = (ValueBlock) trajectory.get(trajectory.size() - 1);
                    VBPInitial = new ValueBlockPre(last);
                    move_params.current_move_class = "sesi";
                    runLoop(true);
                    rmb.irv.prm.batch = rmb.orig_rmb.irv.prm.batch;*//*

                    System.out.println("single full mode");
                    rmb.irv.prm.PRECRIT_TYPE_INDEX = rmb.irv.prm.CRIT_TYPE_INDEX;
                    rm143 b.irv.prm.precrit = new Criterion(rmb.irv.prm.PRECRIT_TYPE_INDEX, true, true, rmb.irv.prm.USE_ABS_AR, TFtargetmap != null ? true : false, rmb.irv.prm.needinv, debug);
                    onv.setPreCriteriaNull();
                    rmb.irv.prm.PBATCH = 0.0;

                    initMoveParam(move_params.move_count);
                    ValueBlock last = (ValueBlock) trajectory.get(trajectory.size() - 1);
                    //VBPInitial = new ValueBlockPre(last);
                    move_params.current_move_class = "sesifull";
                    runLoop(true, last);

                    rmb.irv.prm.PRECRIT_TYPE_INDEX = rmb.orig_rmb.irv.prm.PRECRIT_TYPE_INDEX;
                    rmb.irv.prm.precrit = new Criterion(rmb.irv.prm.PRECRIT_TYPE_INDEX, true, true, rmb.irv.prm.USE_ABS_AR, TFtargetmap != null ? true : false, rmb.irv.prm.needinv, debug);
                    onv.setPreCriteriaNull();
                    rmb.irv.prm.batch = rmb.orig_rmb.irv.prm.batch;
                }
            //do PLATEAU and
            //then
            //do another round
            //of batch
            //moves
            if (rmb.irv.prm.PLATEAU) {
                System.out.println("PLATEAU mode");
                doPlateau();
                //rmb.irv.prm.PBATCH = 1.0;
                //rmb.irv.prm.BATCH_PERC = 0.2;
                initMoveParam(move_params.move_count);
                if (rmb.irv.prm.PBATCH == 1.0)
                    move_params.current_move_class = "batch_plateau";
                else
                    move_params.current_move_class = "sesi_plateau";

                ValueBlock last = (ValueBlock) trajectory.get(trajectory.size() - 1);
                runLoop(false, last);
            }

            if (rmb.irv.prm.RANDOMFLOOD) {
                System.out.println("RANDOMFLOOD mode");
                randomFloodGenes();
                randomFloodExps();
                rmb.irv.prm.RANDOMFLOOD = false;
                //rmb.irv.prm.PBATCH = 1.0;
                //rmb.irv.prm.BATCH_PERC = 0.2;
                //if (rmb.irv.prm.PBATCH != 0.0) {
                initMoveParam(move_params.move_count);
                if (rmb.irv.prm.PBATCH == 1.0)
                    move_params.current_move_class = "batch_random";
                else
                    move_params.current_move_class = "sesi_random";

                ValueBlock last = (ValueBlock) trajectory.get(trajectory.size() - 1);
                runLoop(false, last);
                //}
            }
        }*/
        } catch (Exception e) {
            System.out.println("EX:" + e);
            e.printStackTrace();
        }

        if (debug) {
            try {
                System.out.println("final possible_move_types");
                util.MoreArray.printArray(move_params.possible_move_types);
                System.out.println("final tried_moves");
                util.MoreArray.printArray(move_params.tried_moves);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        /*Generates text and graphical output.*/
        //if (!rmb.irv.prm.RANDOMFLOOD)

        if (trajectory.size() > 0) {
            if (trajectory.size() == 1)
                System.out.println("No moves, no output");

            output();

            //Functionality to add the current trajectory to the exclude list, dynamically.
            /*if (rmb.irv.prm.EXCLUDE_LIST_PATH != null && trajectory.size() > 1) {
                File test = new File(rmb.irv.prm.EXCLUDE_LIST_PATH);

                //add all blocks except for starting points
                for (int i = 1; i < trajectory.size(); i++) {
                    ValueBlock v = (ValueBlock) rmb.exclude_vbl.get(i);
                    rmb.exclude_list.add(MoreArray.toString(v.genes, ",") + "/" + MoreArray.toString(v.exps, ","));
                }
                ValueBlockList newexclude = new ValueBlockList(rmb.exclude_list);
                if (!test.exists() || test.canWrite())
                    newexclude.writeBIC(rmb.irv.prm.EXCLUDE_LIST_PATH);
            }*/
        } else {
            System.out.println("ERROR: trajectory is empty");
        }

        long end_time = System.currentTimeMillis();
        //gd.giveMilli();

        System.out.println("Total time  " + ((end_time - start_time) / 1000.0) + " s");
    }


    /**
     * @param cur
     * @param count
     * @param toCompletion
     */
    private void setMoveModeAndRun(char cur, int count, boolean toCompletion) {
        boolean added_initial = false;
        if (count > 0 || trajectory.size() > 0)
            added_initial = true;
        if (cur == 'b' || cur == 'o') {
            if (debug)
                System.out.println("run batch mode pre+full");
            rmb.irv.prm.PBATCH = 1.0;
            initMoveParam(move_params.move_count);
            int movecount = 0;
            ValueBlock last = new ValueBlock(VBPInitial);
            if (trajectory != null && trajectory.size() != 0) {
                last = (ValueBlock) trajectory.get(trajectory.size() - 1);
                //VBPInitial = new ValueBlockPre(last);
                movecount = trajectory.size();
            }
            move_params.current_move_class = cur + "_" + count;
            runLoop(added_initial, last);
            if (debug)
                System.out.println("setMoveModeAndRun 'batch mode pre+full' did moves " + (trajectory.size() - movecount));
        } else if (cur == 's' || cur == 'n') {
            if (debug)
                System.out.println("run single mode pre+full");
            rmb.irv.prm.PBATCH = 0.0;

            /*if (toCompletion)
                initMoveParam(move_params.move_count, 0.5, 0.5);
            else*/
            initMoveParam(move_params.move_count);

            int movecount = 0;
            ValueBlock last = new ValueBlock(VBPInitial);
            if (trajectory != null && trajectory.size() != 0) {
                last = (ValueBlock) trajectory.get(trajectory.size() - 1);
                //VBPInitial = new ValueBlockPre(last);
                movecount = trajectory.size();
            }
            move_params.current_move_class = cur + "_" + count;
            runLoop(added_initial, last);
            if (debug)
                System.out.println("setMoveModeAndRun 'single mode pre+full' did moves " + (trajectory.size() - movecount));
        } else if (cur == 'm') {
            if (debug)
                System.out.println("run mixed mode pre+full");
            rmb.irv.prm.PBATCH = 0.5;
            initMoveParam(move_params.move_count);
            int movecount = 0;
            ValueBlock last = new ValueBlock(VBPInitial);
            if (trajectory != null && trajectory.size() != 0) {
                last = (ValueBlock) trajectory.get(trajectory.size() - 1);
                //VBPInitial = new ValueBlockPre(last);
                movecount = trajectory.size();
            }
            move_params.current_move_class = cur + "_" + count;
            runLoop(added_initial, last);
            if (debug)
                System.out.println("setMoveModeAndRun 'mixed mode pre+full' did moves " + (trajectory.size() - movecount));
        } else if (cur == 'B' || cur == 'O') {
            if (debug)
                System.out.println("run batch mode full " + cur);
            //set precrit to orig full
            rmb.irv.prm.PRECRIT_TYPE_INDEX = rmb.irv.prm.CRIT_TYPE_INDEX;
            boolean b = rmb.irv.TFtargetmap != null ? true : false;
            //System.out.println("run batch mode full " + b);
            boolean require_null = true;

            if (debug || debug_createPreCritTopList || debug_getCriteriaForBlock) {
                MoreArray.printArray(MINER_STATIC.CRIT_LABELS);
            }

            if (MINER_STATIC.CRIT_LABELS[rmb.irv.prm.PRECRIT_TYPE_INDEX].indexOf("nonull") == -1)
                require_null = false;
            rmb.irv.prm.precrit = new Criterion(rmb.irv.prm.PRECRIT_TYPE_INDEX, rmb.irv.prm.crit.usemean, true, rmb.irv.prm.USE_ABS_AR,
                    b, rmb.irv.prm.needinv, require_null, rmb.irv.prm.FRXN_SIGN, debug);
            rmb.irv.onv.setPreCriteriaNull();
            //rmb.irv.onv.setCrittoPreCritNulls();
            rmb.irv.prm.PBATCH = 1.0;
            initMoveParam(move_params.move_count);
            int movecount = 0;
            ValueBlock last = new ValueBlock(VBPInitial);
            if (trajectory != null && trajectory.size() != 0) {
                last = (ValueBlock) trajectory.get(trajectory.size() - 1);
                //VBPInitial = new ValueBlockPre(last);
                movecount = trajectory.size();
            }
            move_params.current_move_class = cur + "_" + count;
            runLoop(added_initial, last);
            //reset precrit to orig, batch to orig
            rmb.irv.prm.PRECRIT_TYPE_INDEX = rmb.orig_prm.PRECRIT_TYPE_INDEX;

            rmb.irv.prm.precrit = new Criterion(rmb.irv.prm.PRECRIT_TYPE_INDEX, rmb.irv.prm.crit.usemean, true, rmb.irv.prm.USE_ABS_AR,
                    rmb.irv.TFtargetmap != null ? true : false, rmb.irv.prm.needinv, require_null, rmb.irv.prm.FRXN_SIGN, debug);
            rmb.irv.onv.setPreCriteriaNull();
            rmb.irv.prm.batch = rmb.orig_prm.batch;

            if (debug)
                System.out.println("setMoveModeAndRun 'batch mode full' did moves " + (trajectory.size() - movecount));
        } else if (cur == 'S' || cur == 'N') {
            if (debug)
                System.out.println("run single mode full " + cur);
            //set precrit to orig full
            rmb.irv.prm.PRECRIT_TYPE_INDEX = rmb.irv.prm.CRIT_TYPE_INDEX;

            boolean require_null = true;
            if (MINER_STATIC.CRIT_LABELS[rmb.irv.prm.PRECRIT_TYPE_INDEX].indexOf("nonull") == -1)
                require_null = false;

            rmb.irv.prm.precrit = new Criterion(rmb.irv.prm.PRECRIT_TYPE_INDEX, rmb.irv.prm.crit.usemean, true, rmb.irv.prm.USE_ABS_AR,
                    rmb.irv.TFtargetmap != null ? true : false, rmb.irv.prm.needinv, require_null, rmb.irv.prm.FRXN_SIGN, debug);
            rmb.irv.onv.setPreCriteriaNull();
            rmb.irv.prm.PBATCH = 0.0;

            initMoveParam(move_params.move_count);

            int movecount = 0;
            ValueBlock last = new ValueBlock(VBPInitial);
            if (trajectory != null && trajectory.size() != 0) {
                last = (ValueBlock) trajectory.get(trajectory.size() - 1);
                //VBPInitial = new ValueBlockPre(last);
                movecount = trajectory.size();
            }
            String lab = cur + "_";

            move_params.current_move_class = lab + count;
            runLoop(added_initial, last);
            //reset precrit to orig, batch to orig
            rmb.irv.prm.PRECRIT_TYPE_INDEX = rmb.orig_prm.PRECRIT_TYPE_INDEX;
            rmb.irv.prm.precrit = new Criterion(rmb.irv.prm.PRECRIT_TYPE_INDEX, rmb.irv.prm.crit.usemean, true, rmb.irv.prm.USE_ABS_AR,
                    rmb.irv.TFtargetmap != null ? true : false, rmb.irv.prm.needinv, require_null, rmb.irv.prm.FRXN_SIGN, debug);
            rmb.irv.onv.setPreCriteriaNull();
            rmb.irv.prm.batch = rmb.orig_prm.batch;
            if (debug)
                System.out.println("setMoveModeAndRun 'single mode full' did moves " + (trajectory.size() - movecount));
        } else if (cur == 'M') {
            if (debug)
                System.out.println("run mixed mode full");
            //set precrit to orig full
            rmb.irv.prm.PRECRIT_TYPE_INDEX = rmb.irv.prm.CRIT_TYPE_INDEX;

            boolean require_null = true;
            if (MINER_STATIC.CRIT_LABELS[rmb.irv.prm.PRECRIT_TYPE_INDEX].indexOf("nonull") == -1)
                require_null = false;

            rmb.irv.prm.precrit = new Criterion(rmb.irv.prm.PRECRIT_TYPE_INDEX, rmb.irv.prm.crit.usemean, true, rmb.irv.prm.USE_ABS_AR,
                    rmb.irv.TFtargetmap != null ? true : false, rmb.irv.prm.needinv, require_null, rmb.irv.prm.FRXN_SIGN, debug);
            rmb.irv.onv.setPreCriteriaNull();
            //rmb.irv.onv.setCrittoPreCritNulls();
            rmb.irv.prm.PBATCH = 0.5;
            initMoveParam(move_params.move_count);
            int movecount = 0;
            ValueBlock last = new ValueBlock(VBPInitial);
            if (trajectory != null && trajectory.size() != 0) {
                last = (ValueBlock) trajectory.get(trajectory.size() - 1);
                //VBPInitial = new ValueBlockPre(last);
                movecount = trajectory.size();
            }
            move_params.current_move_class = "M_" + count;
            runLoop(added_initial, last);
            //reset precrit to orig, batch to orig
            rmb.irv.prm.PRECRIT_TYPE_INDEX = rmb.orig_prm.PRECRIT_TYPE_INDEX;
            rmb.irv.prm.precrit = new Criterion(rmb.irv.prm.PRECRIT_TYPE_INDEX, rmb.irv.prm.crit.usemean, true, rmb.irv.prm.USE_ABS_AR,
                    rmb.irv.TFtargetmap != null ? true : false, rmb.irv.prm.needinv, require_null, rmb.irv.prm.FRXN_SIGN, debug);
            rmb.irv.onv.setPreCriteriaNull();
            rmb.irv.prm.batch = rmb.orig_prm.batch;
            if (debug)
                System.out.println("setMoveModeAndRun 'mixed mode full' did moves " + (trajectory.size() - movecount));
        } else if (cur == 'p') {
            if (debug)
                System.out.println("run PLATEAU step");
            int movecount = trajectory.size();
            doPlateau();
            initMoveParam(move_params.move_count);
            move_params.current_move_class = "p_" + count;
            VBPCandidate.move_class = "p_" + count;
            addInitial();
            if (debug)
                System.out.println("setMoveModeAndRun 'PLATEAU mode' did moves " + (trajectory.size() - movecount));
        } else if (cur == 'r') {
            if (debug)
                System.out.println("run random step");
            int movecount = trajectory.size();
            ValueBlock last = (ValueBlock) trajectory.get(trajectory.size() - 1);
            VBPInitial = new ValueBlockPre(last);
            randomFloodGenes();
            randomFloodExps();
            rmb.irv.prm.RANDOMFLOOD = false;
            initMoveParam(move_params.move_count);
            move_params.current_move_class = "r_" + count;

            addInitial();
            if (debug)
                System.out.println("setMoveModeAndRun 'random mode' did moves " + (trajectory.size() - movecount));
        } else if (cur == 'c') {
            if (debug)
                System.out.println("run combine step");
            ValueBlock last = ValueBlockListMethods.combinelastGeneExpAdd(trajectory);
            VBPInitial = new ValueBlockPre(last);
            move_params.current_move_class = "c_" + count;
            addInitial();
            if (debug)
                System.out.println("setMoveModeAndRun 'combine mode'");
        }
    }

    /**
     *
     */
    private void doPlateau() {
        ValueBlock last = (ValueBlock) trajectory.get(trajectory.size() - 1);
        VBPInitial = new ValueBlockPre(last);
        int[] genes = VBPInitial.genes;
        int add_size = (int) (VBPInitial.genes.length * rmb.irv.prm.PLATEAU_PERC);
        if (VBPInitial.genes.length + add_size >= rmb.irv.prm.IMAX) {
            add_size = rmb.irv.prm.IMAX - VBPInitial.genes.length;
        }
        ArrayList add = new ArrayList();
        add = MoreArray.addElements(add, genes);
        if (debug)
            System.out.println("doPlateau - potential: " + add.size() + "\tlimit: " + add_size + "\ttotal top: " + vls.expr_top100.size());
        int count = 0;
        int index = 0;
        while (count < add_size && index < MINER_STATIC.TOPLIST_LEN) {
            ValueBlock cur = (ValueBlock) vls.expr_top100.get(index);
            int[] cur_genes = cur.genes;
            int[] diff = MoreArray.diffArray(genes, cur_genes);
            for (int j = 0; j < diff.length; j++) {
                //Integer integer = new Integer(diff[j]);
                Integer integer = Integer.valueOf(diff[j]);
                if (add.indexOf(integer) == -1) {
                    add.add(integer);
                    count++;
                }
            }
            index++;
        }
        if (debug)
            System.out.println("doPlateau a/f " + add.size());
        VBPInitial.genes = MoreArray.ArrayListtoInt(add);
        VBPInitial.trajectory_position = 1;
        VBPInitial.move_class = "PLATEAU";
        if (debug)
            System.out.println("doPlateau genes");
        MoreArray.printArray(VBPInitial.genes);
        //trajectory.add(new ValueBlock(VBPCandidate));
        rmb.irv.prm.PLATEAU = false;
    }

    /**
     *
     */
    private void randomFloodGenes() {
        if (debug)
            System.out.println("Doing randomFloodGenes step series.");
        /*ValueBlock last = (ValueBlock) trajectory.get(trajectory.size() - 1);
        VBPInitial = new ValueBlockPre(last);*/

        int[] nIc = stat.createNaturalNumbersRemoved(1, rmb.irv.prm.DATA_LEN_GENES, VBPInitial.genes);
        ArrayList cur = MoreArray.intarraytoList(nIc);
        if (cur == null) {
            if (debug) {
                System.out.println("WARNING: no more genes to add");
                MoreArray.printArray(VBPInitial.genes);
            }
        } else {
            //remove last 2 genes if LARS
            //int maxlen = MoreArray.getArrayInd(MINER_STATIC.LARSCrit, rmb.irv.prm.CRIT_TYPE_INDEX) != -1 /*||
            //        MoreArray.getArrayInd(MINER_STATIC.GEECrit, rmb.irv.prm.CRIT_TYPE_INDEX) != -1*/ ?
            //        rmb.irv.prm.DATA_LEN_GENES - 2 : rmb.irv.prm.DATA_LEN_GENES;
            int maxlen = rmb.irv.prm.DATA_LEN_GENES;
            int addnum = (int) Math.min(maxlen - VBPInitial.genes.length, (rmb.irv.prm.RANDOMFLOOD_PERC * VBPInitial.genes.length));
            if (debug)
                System.out.println("Doing RANDOMFLOOD step series adding random genes " + addnum);
            int added = 0;
            Collections.sort(cur);
            int[] newgenes = VBPInitial.genes;
            while (cur.size() > 0 && added < addnum) {
                int addpos = 0;
                try {
                    addpos = rand.nextInt(cur.size());
                    ArrayList ar = MoreArray.convtoArrayList(newgenes);
                    Object o = cur.get(addpos);
                    if (debug)
                        System.out.println("randomFloodGenes adding gene " + o);
                    ar.add(o);
                    Collections.sort(ar);
                    newgenes = MoreArray.ArrayListtoInt(ar);
                    cur.remove(addpos);
                    added++;
                } catch (Exception e) {
                    System.out.println("randomFloodGenes " + cur.size() + "\t" + added + "\t" + addnum);
                    e.printStackTrace();
                }
            }
            VBPInitial.genes = newgenes;
            VBPInitial.move_class = "rg";
            if (debug)
                System.out.println("randomFloodGenes " + cur.size() + "\t" + added + "\t" + addnum);
        }

        //move_params = null;
        //initMoveParam();
        //runLoop(false);
    }

    /**
     *
     */
    private void randomFloodExps() {
        if (debug)
            System.out.println("Doing randomFloodExps step series.");

        int[] nIc = stat.createNaturalNumbersRemoved(1, rmb.irv.prm.DATA_LEN_EXPS, VBPInitial.exps);
        ArrayList cur = MoreArray.intarraytoList(nIc);
        if (cur == null) {
            if (debug) {
                System.out.println("WARNING: no more exps to add");
                MoreArray.printArray(VBPInitial.exps);
            }
        } else {
            int maxlen = rmb.irv.prm.DATA_LEN_EXPS;
            int addnum = (int) Math.min(maxlen - VBPInitial.exps.length, (rmb.irv.prm.RANDOMFLOOD_PERC * VBPInitial.exps.length));
            if (debug)
                System.out.println("Doing randomFloodExps step series adding random exps " + addnum);
            int added = 0;
            Collections.sort(cur);
            int[] newexps = VBPInitial.exps;
            while (cur.size() > 0 && added < addnum) {
                int addpos = 0;
                try {
                    addpos = rand.nextInt(cur.size());
                    ArrayList ar = MoreArray.convtoArrayList(newexps);
                    Object o = cur.get(addpos);
                    if (debug)
                        System.out.println("randomFloodExps adding exp " + o);
                    ar.add(o);
                    Collections.sort(ar);
                    newexps = MoreArray.ArrayListtoInt(ar);
                    cur.remove(addpos);
                    added++;
                } catch (Exception e) {
                    System.out.println("randomFloodExps " + cur.size() + "\t" + added + "\t" + addnum);
                    e.printStackTrace();
                }
            }
            VBPInitial.exps = newexps;
            VBPInitial.move_class = "re";
            if (debug)
                System.out.println("randomFloodExps " + cur.size() + "\t" + added + "\t" + addnum);
        }

        //move_params = null;
        //initMoveParam();
        //runLoop(false);
    }

    /**
     * @param added_initial
     * @param start
     */
    private void runLoop(boolean added_initial, ValueBlock start) {

        VBPCandidate = new ValueBlock(start);
        VBPCandidate.move_type = -1;

        last_genes = BlockMethods.IcJctoijID(start.genes);
        last_experiments = BlockMethods.IcJctoijID(start.exps);

        int[] counts = new int[rmb.irv.prm.N_BLOCK];
        if (debug)
            System.out.println("WARNING: runLoop using only first rmb.irv.prm.MAXMOVES value");
//loops over number of starting blocks (blocks to find)
        boolean first = false;
        for (int st = 0; st < rmb.irv.prm.N_BLOCK; st++) {
            if (debug)
                System.out.println("runLoop Start Looping: rmb.irv.prm.MAXMOVES.length " +
                        rmb.irv.prm.MAXMOVES.length +
                        "\trmb.irv.prm.N_BLOCK " + rmb.irv.prm.N_BLOCK + "\tst " + st
                        + "\tmove_count " + move_params.move_count);
//printInitRVariables();
//initialize loop variables
            move_params.stop = false;
//START LOOP
            int total_number_moves = 0;
            long curtime = System.currentTimeMillis();
            while (!move_params.stop && (rmb.walltime == 0 || (rmb.walltime > 0 && (curtime - start_time) / 1000.0 < rmb.walltime))) {

                move_params.tried_moves = MoreArray.initArray(4, 0);
                move_params.unit_moveSeq = "";

                if (debug) {
                    System.out.println("runLoop VBPCandidate.genes " + MoreArray.toString(VBPCandidate.genes, ", "));
                    System.out.println("runLoop VBPCandidate.exps " + MoreArray.toString(VBPCandidate.exps, ", "));
                }

                move_params.possible_move_types = maskMoves(rmb.irv.prm, VBPCandidate.genes, VBPCandidate.exps,
                        move_params.Single_or_Batch);

                //if (!rmb.irv.prm.OVERRIDE_SHAVING)
                setShaving();

                double[][] cur_precrit = getCriteriaForBlock(VBPCandidate, rmb.irv.prm.precrit);

                if (debug)
                    System.out.println("runLoop VBPCandidate " + VBPCandidate);
                if (cur_precrit == null) {
                    System.out.println("ERROR runLoop the pre-criteria were null");
                    System.exit(0);
                }
                if (cur_precrit != null) {
                    //VBPCandidate.pre_criterion = Matrix.sumEntries(cur_precrit);
                    //VBPCandidate.all_precriteria = cur_precrit;
                    if (debug) {
                        System.out.println("VBPCandidate cur_precrit " + MoreArray.toString(cur_precrit[0], ",") + "\t" +
                                MoreArray.toString(cur_precrit[1], ","));
                    }
                    if (debug_createPreCritTopList) {
                        System.out.println("cur_precrit");
                        MoreArray.printArray(cur_precrit);
                    }
                    boolean use_mean_now = rmb.irv.prm.precrit.isMeanCrit ? true : rmb.irv.prm.USE_MEAN;

                    if (debug) {
                        System.out.println("runLoop use_mean_now " + use_mean_now +
                                "\trmb.irv.prm.USE_MEAN " + rmb.irv.prm.USE_MEAN +
                                "\trmb.irv.prm.crit.usemean " + rmb.irv.prm.crit.usemean);
                    }

                    boolean[] passcrits = Criterion.getExprCritTypes(rmb.irv.prm.precrit, rmb.irv.prm.WEIGH_EXPR,
                            use_mean_now, debug_getCriteriaForBlock);
                    VBPCandidate.updatePreCrit(cur_precrit, rmb.irv.prm.WEIGH_EXPR, passcrits, debug_getCriteriaForBlock);

                    if (debug) {
                        System.out.println("runLoop VBPCandidate.pre_criterion " + VBPCandidate.pre_criterion);
                        System.out.println("runLoop Ic " + MoreArray.toString(VBPCandidate.genes, ","));
                        //MoreArray.printArray(Ic);
                        System.out.println("runLoop Jc " + MoreArray.toString(VBPCandidate.exps, ","));
                        //MoreArray.printArray(Jc);
                        if (this.debug_getCriteriaForBlock) {
                            System.out.println("runLoop runLoop cur_precrit " +
                                    MoreArray.toString(cur_precrit[0], ",") + "\t" + MoreArray.toString(cur_precrit[1], ","));
                            //MoreArray.printArray(cur_precrit);
                        }
                    }
                    getFinalCriteriaForBlock(VBPCandidate);

                    if (first) {
                        updatePreList(VBPCandidate);
                        updateLists(VBPCandidate);
                        first = false;
                    }
                    if (debug)
                        System.out.println("runLoop VBPCandidate.all_criteria " + MoreArray.toString(VBPCandidate.all_criteria, ","));
                    //MoreArray.printArray(VBPCandidate.all_criteria);
                    if (debug)
                        System.out.println("runLoop added_initial " + added_initial);
                    if (!added_initial) {
                        added_initial = addInitial();
                    }

                    //move_params.possible_move_types = maskMoves(prm, Ic, Jc, move_params.Single_or_Batch);
                    if (debug) {
                        System.out.println("runLoop while (!move_params.stop)  a/f maskMoves possible_move_types");
                        MoreArray.printArray(move_params.possible_move_types);
                        System.out.println("runLoop while (!move_params.stop)  tried_moves");
                        MoreArray.printArray(move_params.tried_moves);
                    }

                    while (move_params.moreMoves() || !move_params.stop) {
                        long move_start_time = System.currentTimeMillis();
                        //gd.giveMilli();
                        setRVariables(VBPCandidate.genes, VBPCandidate.exps);
                        if (debug) {
                            System.out.println("runLoop top of while loop move_params.possible_move_types");
                            MoreArray.printArray(move_params.possible_move_types);
                            System.out.println("runLoop top of while loop move_params.tried_moves");
                            MoreArray.printArray(move_params.tried_moves);
                        }

                        //get vector of possible unique MOVE_TYPES given the constraints
                        move_params.stop = currentMove(VBPCandidate.genes, VBPCandidate.exps);
//System.out.println("top of while loop stop " + move_params.stop);

                        //outputs MOVE_TYPES we've looked at in this prev_block loop
                        if (debug) {
                            /*System.out.println("runLoop MOVE_TYPES we've looked at: ");
                            MoreArray.printArray(move_params.tried_moves);*/
                            if (move_params.possible_move_list != null)
                                System.out.println("runLoop num_moves " + move_params.possible_move_list[0].size() + "\t" + move_params.possible_move_list[1].size());
                        }
                        int num_moves = (move_params.possible_move_list != null ? move_params.possible_move_list[0].size() + move_params.possible_move_list[1].size() : 0);
                        //Assuming there are possible MOVE_TYPES calculate the full Criteria for those selected
                        if (num_moves > 0) {
                            if (debug)
                                System.out.println("runLoop VBPCandidate b/f " + VBPCandidate.pre_criterion + "\t" + VBPCandidate.move_type);
                            ArrayList candidate = moveProper(VBPCandidate);
                            if (debug)
                                System.out.println("runLoop VBPCandidate b/f " + (candidate != null ? candidate.size() : 0));

                            if (candidate != null && candidate.size() > 0) {
                                move_params.move_count++;

                                move_params.possible_move_types = maskMoves(rmb.irv.prm, VBPCandidate.genes, VBPCandidate.exps, move_params.Single_or_Batch);
                                if (debug) {
                                    System.out.println("runLoop a/f maskMoves possible_move_types");
                                    MoreArray.printArray(move_params.possible_move_types);
                                }
                                if (debug) {
                                    System.out.println("runLoop a/f maskMoves tried_moves");
                                    MoreArray.printArray(move_params.tried_moves);
                                }
                                boolean moreMovesNow = move_params.moreMoves();

                                //exits due to no more available moves
                                if (!moreMovesNow) {
                                    if (debug) {
                                        System.out.println("runLoop Reached !moreMovesNow");
                                        printTrajectory(counts[st], "runLoop init block index " + st + "\tcounts " + MoreArray.toString(counts, ",") + "\tnum_moves " + num_moves);
                                    }
                                    long move_end_time = System.currentTimeMillis();
                                    if (debug)
                                        System.out.println("runLoop Run time for move " + total_number_moves + " " + ((move_end_time - move_start_time) / 1000.0) + " s");
                                    break;
                                }

                                move_params.initMoveDirections();
                                st = 0;
                                //exits due to reaching the maximum allowed moves
                                if (rmb.irv.prm.MAXMOVES[0] != -1 && move_params.move_count >= rmb.irv.prm.MAXMOVES[0]) {
                                    if (debug) {
                                        System.out.println("runLoop Reached max number of moves " + rmb.irv.prm.MAXMOVES[0]);
                                        printTrajectory(counts[st], "runLoop init block index " + st + "\tcounts " + MoreArray.toString(counts) + "\tnum_moves " + num_moves);
                                    }

                                    move_params.stop = true;
                                    long move_end_time = System.currentTimeMillis();
                                    if (debug)
                                        System.out.println("runLoop Run time for move " + total_number_moves + " " + ((move_end_time - move_start_time) / 1000.0) + " s");
                                    break;
                                }
                            }
                            //exits due to all moves tried
                            else if (stat.countOccurence(move_params.tried_moves, 1) == 4) {
                                move_params.stop = true;
                                if (debug) {
                                    System.out.println("runLoop done tried all moves");
                                    printTrajectory(counts[st], "runLoop init block index " + st + "\tcounts " + MoreArray.toString(counts) + "\tnum_moves " + num_moves);
                                }
                                //long move_end_time = System.currentTimeMillis();
                                //System.out.println("runLoop Run time for move " + total_number_moves + " " + ((move_end_time - move_start_time) / 1000.0) + " s");
                                break;
                            }
                        }//ends if (num_moves == 0)
                        counts[st]++;

                        //if (debug)
                        printTrajectory(counts[st], "runLoop init block index " + st + "\tcounts " + MoreArray.toString(counts, ",") + "\tnum_moves " + num_moves);

                        long move_end_time = System.currentTimeMillis();//gd.giveMilli();
                        total_number_moves++;
                        //if (debug)
                        System.out.println("runLoop run time for move " + total_number_moves + " " + ((move_end_time - move_start_time) / 1000.0) + " s");

                        //reset move probabilities in case starting ones were not default
                        if (total_number_moves > 0) {
                            /*move_params.PA = rmb.irv.prm.PA;//0.5;
                          move_params.PG = rmb.irv.prm.PG;//0.5;*/
                            move_params.nonInitpApG(rmb.irv.prm);
                        }

                    }  //ends movement while loop
                } //ends initial block_id loop (starting block_id)

                curtime = System.currentTimeMillis();
            }
            if (debug)
                System.out.println("runLoop End Looping: rmb.irv.prm.MAXMOVES.length " +
                        rmb.irv.prm.MAXMOVES.length +
                        "\trmb.irv.prm.N_BLOCK " + rmb.irv.prm.N_BLOCK + "\tst " + st
                        + "\tmove_count " + move_params.move_count);
        }

        if (doVariance)
            getVarianceForFinal();

        total_time = System.currentTimeMillis();//gd.giveMilli();
        total_time = (total_time - start_time) / 1000.0;
        if (debug) {
            System.out.println("runLoop Stage done " + trajectory.size() + "\n" + trajectory.toString());
            System.out.println("runLoop Total time  " + total_time + " s");
        }
    }

    /**
     * @param count
     * @param x
     */
    private void printTrajectory(int count, String x) {
        //if (debug) {
        System.out.println("runLoop tried_moves");
        MoreArray.printArray(move_params.tried_moves);
        System.out.println(x);
        System.out.println(trajectory.toString());
        System.out.println("runLoop ************End movement loop iteration");
        //}
    }

    /**
     * @return
     */
    private boolean addInitial() {
        VBPInitial = BlockMethods.computeBlockOverlapWithRef(VBPInitial, VBPInitial);

        if (debug) {
            if (VBPInitial.all_precriteria != null)
                System.out.println("length " + VBPInitial.all_precriteria.length);
        }
        VBPCandidate = new ValueBlock(VBPInitial);

        if (debug) {
            System.out.println("length " + VBPCandidate.all_precriteria.length);
        }

        VBPCandidate.move_class = move_params.current_move_class;
        VBPCandidate.setDataAndMean(rmb.expr_matrix.data);

        double[][] cur_precrit = getCriteriaForBlock(VBPCandidate, rmb.irv.prm.crit);

        boolean use_mean_now = rmb.irv.prm.precrit.isMeanCrit ? true : rmb.irv.prm.USE_MEAN;
        if (debug) {
            System.out.println("addInitial use_mean_now " + use_mean_now);
        }

        boolean[] passcrits = Criterion.getExprCritTypes(rmb.irv.prm.precrit, rmb.irv.prm.WEIGH_EXPR,
                use_mean_now, debug_getCriteriaForBlock);
        VBPCandidate.updatePreCrit(cur_precrit, rmb.irv.prm.WEIGH_EXPR, passcrits, debug_getCriteriaForBlock);

        if (debug) {
            System.out.println("length " + VBPCandidate.all_precriteria.length);
        }

        getFinalCriteriaForBlock(VBPCandidate);
        if (debug) {
            System.out.println("length a/f " + VBPCandidate.all_criteria.length);
        }

        if (debug) {
            System.out.println("addInitial wnull " + MoreArray.toString(cur_precrit[0], ","));
            System.out.println("addInitial wonull " + MoreArray.toString(cur_precrit[1], ","));

            System.out.println("addInitial precrit " + VBPCandidate.pre_criterion);
            System.out.println("addInitial precrit " + MoreArray.toString(VBPCandidate.all_precriteria, ","));

            System.out.println("addInitial crit " + VBPCandidate.full_criterion);
            System.out.println("addInitial crit " + MoreArray.toString(VBPCandidate.all_criteria, ","));
        }
/*
if (rmb.irv.prm.FULL_CRITERIA && !move_params.exp_shaving && !move_params.gene_shaving) {
try {
String s = "ExtractFeatures(Ic,Jc,feature_data,Ifactor,I,J)";
System.out.println("R: " + s);
System.out.println(rmb.irv.Rexpr = Rengine.eval(s));
int[] features = (rmb.irv.Rexpr = Rengine.eval(s)).asIntArray();
VBPCandidate.setFeatures(features);
if (debug) {
System.out.println("ExtractFeatures");
MoreArray.printArray(features);
}
} catch (Exception e) {
e.printStackTrace();
}
}
*/

        ValueBlock valueBlock = new ValueBlock(VBPCandidate);
        lastBlock = new ValueBlock(VBPCandidate);
        if (debug)
            System.out.println("initial lastBlock defined");
        trajectory.add(valueBlock);
        trajectory_blocks.put(valueBlock.blockId(), valueBlock);

        if (debug) {
            System.out.println("added initial to trajectory");
            System.out.println(valueBlock.toString());
        }

        if (rmb.mvb != null) {
            rmb.mvb.updateBlocks();
        }
        return true;
    }


    /**
     * @param genes
     * @param exps
     */
    private void setGenesExps(int[] genes, int[] exps) {
        if (debug_getCriteriaForBlock)
            System.out.println("R: Ic<-c(" + MoreArray.toString(genes, ",") + ")");
        rmb.irv.Rengine.assign("Ic", genes);
        if (debug_getCriteriaForBlock)
            System.out.println("R: Jc<-c(" + MoreArray.toString(exps, ",") + ")");
        rmb.irv.Rengine.assign("Jc", exps);
    }

    /**
     * Determines if the existing block is suitable for gene or exp shaving.
     */
    private void setShaving() {

        int num_genes = VBPCandidate.genes.length;
        if (debug_createPreCritTopList)
            System.out.println("setShaving gene " + num_genes + "\t" + rmb.irv.prm.IMAX);

        int num_exps = VBPCandidate.exps.length;
        if (debug_createPreCritTopList)
            System.out.println("setShaving exp " + num_exps + "\t" + rmb.irv.prm.JMAX);

        //force gene deletion
        if (num_genes >= rmb.irv.prm.IMAX && !rmb.irv.prm.OVERRIDE_SHAVING) {//- rmb.irv.prm.IMIN
            move_params.gene_shaving = true;
            /*if (move_params.tried_moves[2] == 1)
          move_params.tried_moves[2] = 0;*/
            move_params.possible_move_types[1] = -1;
            move_params.possible_move_types[2] = -1;
            move_params.possible_move_types[3] = -1;

            move_params.tried_moves[1] = 1;
            move_params.tried_moves[2] = 1;
            move_params.tried_moves[3] = 1;

            if (debug)
                System.out.println("forcing gene deletion");
        } else
            move_params.gene_shaving = false;


        //force experiment deletion
        if (num_exps >= rmb.irv.prm.JMAX && !rmb.irv.prm.OVERRIDE_SHAVING) {//- rmb.irv.prm.JMIN {
            move_params.exp_shaving = true;
            move_params.possible_move_types[0] = -1;
            move_params.possible_move_types[1] = -1;
            move_params.possible_move_types[3] = -1;

            move_params.tried_moves[0] = 1;
            move_params.tried_moves[1] = 1;
            move_params.tried_moves[3] = 1;

            if (debug)
                System.out.println("forcing gene deletion");
            /*if (move_params.tried_moves[2] == 1)
          move_params.tried_moves[2] = 0;*/
        } else
            move_params.exp_shaving = false;
        if (debug)
            System.out.println("setShaving gene_shaving " + move_params.gene_shaving + "\texp_shaving " + move_params.exp_shaving +
                    "\t\t" + num_genes + "\t" + num_exps);
    }

    /**
     *
     */
    private void getVarianceForFinal() {
        ValueBlock last = (ValueBlock) trajectory.get(trajectory.size() - 1);
        finalVarListGenes.add(last);
        finalVarListExp.add(last);
        //if (rmb.irv.prm.precrit.FULL_CRITERIA || rmb.irv.prm.crit.FULL_CRITERIA ||
        //        rmb.irv.prm.precrit.isInteractCrit || rmb.irv.prm.crit.isInteractCrit) {
        for (int i = 0; i < vls.full_top100.size(); i++) {
            ValueBlock cur = (ValueBlock) vls.full_top100.get(i);
            int countGenes = BlockMethods.countGeneOverlapWithRef(last, cur);
            int countExp = BlockMethods.countExpOverlapWithRef(last, cur);
//System.out.println("getVarianceForFinal " + i+"\t"+ countGenes + "\t" + countExp);
            if (countGenes == last.genes.length - 1 && countExp == last.exps.length
                    && !finalVarListGenes.contains(cur)) {
                //System.out.println("getVarianceForFinal " + countGenes + "\t" + countExp);
                finalVarListGenes.add(cur);
            } else if (countGenes == last.genes.length && countExp == last.exps.length - 1
                    && !finalVarListExp.contains(cur)) {
                //System.out.println("getVarianceForFinal " + i + "\t" + countGenes + "\t" + countExp);
                finalVarListExp.add(cur);
            }
        }
        /*} else {
            for (int i = 0; i < vls.expr_top100.size(); i++) {
                ValueBlock cur = (ValueBlock) vls.expr_top100.get(i);
                int countGenes = BlockMethods.countGeneOverlapWithRef(last, cur);
                int countExp = BlockMethods.countExpOverlapWithRef(last, cur);
//System.out.println("getVarianceForFinal " + i+"\t"+ countGenes + "\t" + countExp);
                if (countGenes == last.genes.length - 1 && countExp == last.exps.length
                        && !finalVarListGenes.contains(cur)) {
                    //System.out.println("getVarianceForFinal " + countGenes + "\t" + countExp);
                    finalVarListGenes.add(cur);
                } else if (countGenes == last.genes.length && countExp == last.exps.length - 1
                        && !finalVarListExp.contains(cur)) {
                    //System.out.println("getVarianceForFinal " + i + "\t" + countGenes + "\t" + countExp);
                    finalVarListExp.add(cur);
                }
            }
        }*/
        /*Additional lists used for lookup when topN truncation implemented.*/
    }

    /**
     * Determines the current move and the associated possible moves.
     *
     * @param Jc
     * @return
     * @paramIc
     */
    private boolean currentMove(int[] Ic, int[] Jc) {
        if (debug) {
            System.out.println("currentMove start");
            System.out.println("R: Ic<-c(" + MoreArray.toString(Ic, ",") + ")");
            System.out.println("R: Jc<-c(" + MoreArray.toString(Jc, ",") + ")");
        }

        if (debug)
            System.out.println("currentMove EXP_SHAVE/rmb.irv.prm.GENE_SHAVE " + move_params.exp_shaving +
                    "\t" + move_params.gene_shaving);

        setAddDelGeneExp();

        double dval = Double.NaN;
        //single move
        if (rmb.irv.prm.PBATCH == 0.0) {
            move_params.Single_or_Batch = 0;
            if (debug)
                System.out.println("currentMove rmb.irv.prm.PBATCH == 0.0");
        }
        //batch move
        else if (rmb.irv.prm.PBATCH == 1.0) {
            move_params.Single_or_Batch = 1;
            if (debug)
                System.out.println("currentMove rmb.irv.prm.PBATCH == 1.0");
        } else {
            String s2 = "rbinom(1, 1, " + move_params.pBatch + ")";
            if (debug) {
                System.out.println("R: " + s2);
            }
            dval = (rmb.irv.Rexpr = rmb.irv.Rengine.eval(s2)).asDouble();
            if (debug)
                System.out.println("move_params.Single_or_Batch " + dval);
            move_params.Single_or_Batch = (int) dval;
            if (debug)
                System.out.println("currentMove rmb.irv.prm.PBATCH != 0 && != 1 " + "\tPBATCH " +
                        move_params.pBatch + "\tSingle_or_Batch " + move_params.Single_or_Batch);
        }
        if (debug) {
            System.out.println("currentMove directions trajectory.size() " + trajectory.size() + "\tIc.length " + Ic.length +
                    "\tJc.length " + Jc.length + "\tDelete_or_Add " + move_params.Delete_or_Add + "\tExperiment_or_Gene " +
                    move_params.Experiment_or_Gene + "\tPBATCH " +
                    move_params.pBatch + "\tSingle_or_Batch " + move_params.Single_or_Batch);
        }

        //System.out.println("currentMove PA PG "+move_params.PA+"\t"+move_params.PG);
        int countwhile = 0;
        while (true) {
            if (debug) {
                System.out.println("currentMove final countwhile " + countwhile + "\tDelete_or_Add " + move_params.Delete_or_Add +
                        "\tExperiment_or_Gene " + move_params.Experiment_or_Gene);
                System.out.println("currentMove loop possible_move_types " + MoreArray.toString(move_params.possible_move_types, ","));
                System.out.println("currentMove loop Moves tried_moves " + trajectory.size() + "\t" + MoreArray.toString(move_params.tried_moves, ","));
            }
            //do + move
            if (move_params.Delete_or_Add == 1) {// || (move_params.tried_moves[0] == 1 && move_params.tried_moves[2] == 1)
                //|| (mathy.stat.countOccurence(move_params.possible_move_types, 1) == 0 && move_params.tried_moves[2] == 1)
                //|| (move_params.tried_moves[0] == 1 && mathy.stat.countOccurence(move_params.possible_move_types, 3) == 0)) {
                if (debug) {
                    System.out.println("currentMove addition L0 " + countwhile);
                }
                //do g+ move
                if (move_params.Experiment_or_Gene == 1 && move_params.canAddGenes()) {
                    if (debug) {
                        System.out.println("currentMove gene addition L1 " + countwhile);
                    }
                    //add if gene move chosen or at expr size when expr move chosen
                    //if (move_params.Experiment_or_Gene == 1) {//|| !move_params.isExpAdd() || move_params.tried_moves[3] == 1) {
                    if (debug) {
                        System.out.println("currentMove gene addition L2 " + countwhile);
                    }
                    //if (!rmb.irv.prm.batch) {
                    if (move_params.Single_or_Batch == 0) {
                        move_params.possible_move_list = GA(Ic, rmb.irv.prm.DATA_LEN_GENES);
                    } else {
                        move_params.possible_move_list = batchGA(Ic, Jc);
                        //if no batch moves default to single
                        /*if (move_params.possible_move_list == null) {
                            move_params.possible_move_list = GA(Ic, rmb.irv.prm.DATA_LEN_GENES);
                        }*/
                    }
                    move_params.tried_moves[1] = 1;
                    move_params.moveIndex = 1;
                    if (debug) {
                        System.out.println("currentMove move_params.tried_moves[1] " + countwhile);
                        util.MoreArray.printArray(move_params.tried_moves);
                    }
                    move_params.move_type = MINER_STATIC.MOVE_TYPES[1];//"gene+";
                    break;
                }
                //}
                //do e+ move
                else if (move_params.Experiment_or_Gene == 0 && move_params.canAddExps()) {
                    if (debug)
                        System.out.println("currentMove experiment addition L1 " + countwhile);
                    //add if expr move chosen or at gene size when gene move chosen
                    //if (move_params.Experiment_or_Gene == 0) {// || mathy.stat.countOccurence(move_params.possible_move_types, 2) == 0 || move_params.tried_moves[1] == 1) {
                    if (debug)
                        System.out.println("currentMove experiment addition L2 " + countwhile);
                    //if (!rmb.irv.prm.batch) {
                    if (move_params.Single_or_Batch == 0) {
                        move_params.possible_move_list = EA(Jc, rmb.irv.prm.DATA_LEN_EXPS);
                    } else {
                        move_params.possible_move_list = batchEA(Ic, Jc);
                        //if no batch moves default to single
                        /*if (move_params.possible_move_list == null) {
                            move_params.possible_move_list = EA(Jc, rmb.irv.prm.DATA_LEN_EXPS);
                        }*/
                    }

                    move_params.tried_moves[3] = 1;
                    move_params.moveIndex = 3;
                    if (debug) {
                        System.out.println("currentMove move_params.tried_moves[3] " + countwhile);
                        util.MoreArray.printArray(move_params.tried_moves);
                    }
                    move_params.move_type = MINER_STATIC.MOVE_TYPES[3];
                    break;
                } else {
                    break;
                }
                //}
            }
            //do deletion move
            else if (move_params.Delete_or_Add == 0) {// || (move_params.tried_moves[1] == 1 && move_params.tried_moves[3] == 1) ||
                //(mathy.stat.countOccurence(move_params.possible_move_types, 2) == 0 && move_params.tried_moves[3] == 1) ||
                //(move_params.tried_moves[1] == 1 && move_params.isExpAdd())) {
                if (debug) {
                    System.out.println("currentMove deletion L0 " + countwhile + "\tmove_params.Experiment_or_Gene  " + move_params.Experiment_or_Gene
                            + "\tmove_params.canDelExps() " + move_params.canDelExps());
                    //System.out.println("currentMove deletion L0 " + move_params.exp_shaving + "\t" + move_params.exp_shaving
                    //       + "\t" + MoreArray.toString(move_params.possible_move_types, ",") + "\t" + MoreArray.toString(move_params.tried_moves, ","));
                }
                //do gene deletion move if not at gene lower limit
                if (
                    //(!move_params.exp_shaving || (move_params.gene_shaving && move_params.exp_shaving)) &&
                        move_params.Experiment_or_Gene == 1 && move_params.canDelGenes()) {
                    if (debug)
                        System.out.println("currentMove gene deletion L1 " + countwhile);
                    //delete if gene move chosen or at expr size when expr move chosen
                    //if (move_params.Experiment_or_Gene == 1){// || mathy.stat.countOccurence(move_params.possible_move_types, 3) == 0 || move_params.tried_moves[2] == 1) {
                    if (debug)
                        System.out.println("currentMove gene deletion L2 " + countwhile);
                    //if (!rmb.irv.prm.batch) {
                    if (move_params.Single_or_Batch == 0) {
                        move_params.possible_move_list = GD(Ic);
                    } else {
                        move_params.possible_move_list = batchGD(Ic, Jc);
                    }

                    move_params.tried_moves[0] = 1;
                    move_params.moveIndex = 0;
                    if (debug) {
                        System.out.println("currentMove move_params.tried_moves[0] " + countwhile);
                        util.MoreArray.printArray(move_params.tried_moves);
                    }
                    move_params.move_type = MINER_STATIC.MOVE_TYPES[0];//"gene-";
                    break;
                    //}
                }
                //do exp deletion move if not at exp lower limit
                else if (
                    //(!move_params.gene_shaving || (move_params.gene_shaving && move_params.exp_shaving)) &&
                        move_params.Experiment_or_Gene == 0 && move_params.canDelExps()) {
                    if (debug)
                        System.out.println("currentMove experiment deletion L1 " + countwhile + "\t" + move_params.Single_or_Batch);
                    //add if expr move chosen or at gene size when gene move chosen
                    //if (move_params.Experiment_or_Gene == 0) {// || mathy.stat.countOccurence(move_params.possible_move_types, 1) == 0 || move_params.tried_moves[0] == 1) {
                    //if (debug)
                    //   System.out.println("currentMove experiment deletion L2 " + countwhile);
                    //if (!rmb.irv.prm.batch) {
                    if (move_params.Single_or_Batch == 0) {
                        move_params.possible_move_list = ED(Jc);
                    } else {
                        move_params.possible_move_list = batchED(Ic, Jc);
                    }

                    //testPossibleMovesForNaN("exp");
                    move_params.tried_moves[2] = 1;
                    move_params.moveIndex = 2;
                    if (debug) {
                        System.out.println("currentMove move_params.tried_moves[2] " + countwhile);
                        util.MoreArray.printArray(move_params.tried_moves);
                    }
                    move_params.move_type = MINER_STATIC.MOVE_TYPES[2];//"experiment-";
                    break;
                    //}
                } else {
                    break;
                }
            }
            countwhile++;
        }
        if (debug) {
            /*System.out.println("currentMove out of loop move_params.tried_moves");
            MoreArray.printArray(move_params.tried_moves);
            System.out.println("currentMove out of loop move_params.possible_move_types");
            MoreArray.printArray(move_params.possible_move_types);*/

            if (move_params.possible_move_list != null) {
                if (debug) {
                    System.out.println("currentMove genes " + move_params.possible_move_list[0].size());
                    System.out.println("currentMove experiments " + move_params.possible_move_list[1].size());
                }
            }
            //System.out.println("currentMove OldMovesStrBlockId " + move_params.OldMovesStrBlockId.size());
            if (debug)
                System.out.println("currentMove move_type " + move_params.move_type);
        }

        if (move_params.move_type != null && move_params.possible_move_list != null) {
            if (move_params.Single_or_Batch == 1) {
                /*if (debug) {
                    System.out.println("VBPCandidate.exps");
                    MoreArray.printArray(VBPCandidate.exps);
                }*/
                move_params.pMoveStrings = makeMoveStringsBatch(move_params.possible_move_list, VBPCandidate,
                        move_params.move_type);
            } else {
                move_params.pMoveStrings = makeMoveStrings(move_params.possible_move_list, VBPCandidate,
                        move_params.move_type);
            }
            return false;
        }

        return true;
    }

    /**
     *
     */
    private void setAddDelGeneExp() {
        if (debug)
            System.out.println("setAddDelGeneExp PA " + move_params.pA + "\tPG " + move_params.pG + "\ttrajectory " + trajectory.size());
        //special case for shaving feature
        if (move_params.exp_shaving || move_params.gene_shaving) {
            move_params.Delete_or_Add = 0;
            if (move_params.gene_shaving && move_params.exp_shaving) {

                move_params.makepG(rmb.irv.Rengine);

            } else if (move_params.exp_shaving || rmb.irv.prm.FIX_GENES) {
                move_params.Experiment_or_Gene = 0;
                if (debug)
                    System.out.println("setAddDelGeneExp forcing experiment deletion");
            } else if (move_params.gene_shaving || rmb.irv.prm.FIX_EXPS) {
                move_params.Experiment_or_Gene = 1;
                if (debug)
                    System.out.println("setAddDelGeneExp forcing gene deletion");
            }
        }
        //Updates according to what move is chosen
        //updates pA if hit boundaries of addition or deletion
        else {

            double sum = stat.sumEntries(move_params.tried_moves);
            if (sum == 0) {
                move_params.makepA(rmb.irv.Rengine);

                move_params.makepG(rmb.irv.Rengine);
                if (debug)
                    System.out.println("setAddDelGeneExp random move " + MoreArray.toString(move_params.tried_moves, ","));
            } else if (sum == 1) {
                int index = -1;
                try {
                    index = MoreArray.getArrayInd(move_params.tried_moves, 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (debug)
                    System.out.println("sum == 1 setAddDelGeneExp bf " + MoreArray.toString(move_params.tried_moves, ",") + "\t" + index + "\t"
                            + move_params.Delete_or_Add + "\t" + move_params.Experiment_or_Gene);
                //these moves could be more randomized, literally randomly choosing 1/3
                if (index == 0) {
                    move_params.Delete_or_Add = 0;
                    move_params.Experiment_or_Gene = 0;
                } else if (index == 1) {
                    move_params.Delete_or_Add = 1;
                    move_params.Experiment_or_Gene = 0;
                } else if (index == 2) {
                    move_params.Delete_or_Add = 0;
                    move_params.Experiment_or_Gene = 1;
                } else if (index == 3) {
                    move_params.Delete_or_Add = 1;
                    move_params.Experiment_or_Gene = 1;
                }
                if (debug)
                    System.out.println("sum == 1 setAddDelGeneExp af " + index + "\t"
                            + move_params.Delete_or_Add + "\t" + move_params.Experiment_or_Gene);
            } else if (sum == 2) {
                int[] pass = {1};
                //populate indices of tried moves
                int[] index = MoreArray.crossFirstIndex(move_params.tried_moves, pass);

                if (debug) {
                    System.out.println("sum == 2 setAddDelGeneExp index bf " + MoreArray.toString(move_params.tried_moves, ",")
                            + "\t" + MoreArray.toString(index, ",")
                            + "\t" + move_params.Delete_or_Add + "\t" + move_params.Experiment_or_Gene);
                    //MoreArray.printArray(index);
                }

                if (index[0] == 0 && index[1] == 0) {
                    move_params.makepA(rmb.irv.Rengine);
                    move_params.Experiment_or_Gene = 1;
                    if (debug)
                        System.out.println("setAddDelGeneExp random +/-");
                } else if (index[2] == 0 && index[3] == 0) {
                    move_params.makepA(rmb.irv.Rengine);
                    move_params.Experiment_or_Gene = 1;
                } else if (index[0] == 0 && index[2] == 0) {
                    move_params.Delete_or_Add = 1;
                    move_params.makepG(rmb.irv.Rengine);
                    if (debug)
                        System.out.println("setAddDelGeneExp random g/e");
                } else if (index[1] == 0 && index[3] == 0) {
                    move_params.Delete_or_Add = 0;
                    move_params.makepG(rmb.irv.Rengine);
                    if (debug)
                        System.out.println("setAddDelGeneExp random g/e");
                } else if (index[0] == 0 && index[3] == 0) {
                    move_params.makepG(rmb.irv.Rengine);
                    if (debug)
                        System.out.println("setAddDelGeneExp random g/e");
                    if (move_params.Experiment_or_Gene == 1) {
                        move_params.Delete_or_Add = 1;
                    } else if (move_params.Experiment_or_Gene == 0) {
                        move_params.Delete_or_Add = 0;
                    }
                } else if (index[1] == 0 && index[2] == 0) {
                    move_params.makepG(rmb.irv.Rengine);
                    if (debug)
                        System.out.println("setAddDelGeneExp random g/e");
                    if (move_params.Experiment_or_Gene == 1) {
                        move_params.Delete_or_Add = 0;
                    } else if (move_params.Experiment_or_Gene == 0) {
                        move_params.Delete_or_Add = 1;
                    }
                }
                if (debug)
                    System.out.println("sum == 2 setAddDelGeneExp index af " + MoreArray.toString(move_params.tried_moves, ",")
                            + "\t" + MoreArray.toString(index, ",")
                            + "\t" + move_params.Delete_or_Add + "\t" + move_params.Experiment_or_Gene);
            } else if (sum == 3) {
                int index = -1;
                try {
                    index = MoreArray.getArrayInd(move_params.tried_moves, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (debug)
                    System.out.println("sum == 3 setAddDelGeneExp index==0 bf " + MoreArray.toString(move_params.tried_moves, ",")
                            + "\t" + index + "\t" +
                            move_params.Delete_or_Add + "\t" + move_params.Experiment_or_Gene);

                if (index == 0) {
                    move_params.Delete_or_Add = 0;
                    move_params.Experiment_or_Gene = 1;
                } else if (index == 1) {
                    move_params.Delete_or_Add = 1;
                    move_params.Experiment_or_Gene = 1;
                } else if (index == 2) {
                    move_params.Delete_or_Add = 0;
                    move_params.Experiment_or_Gene = 0;
                } else if (index == 3) {
                    move_params.Delete_or_Add = 1;
                    move_params.Experiment_or_Gene = 0;
                }

                if (debug)
                    System.out.println("sum == 3 setAddDelGeneExp index af " + MoreArray.toString(move_params.tried_moves, ",")
                            + "\t" + index
                            + "\t" + move_params.Delete_or_Add + "\t" + move_params.Experiment_or_Gene);
            }

            if (debug)
                System.out.println("setAddDelGeneExp Delete_or_Add " + move_params.Delete_or_Add +
                        "\tExperiment_or_Gene " + move_params.Experiment_or_Gene);
            if (debug) {
                System.out.println("setAddDelGeneExp loop possible_move_types " + MoreArray.toString(move_params.possible_move_types, ","));
                System.out.println("setAddDelGeneExp loop Moves tried_moves " + MoreArray.toString(move_params.tried_moves, ","));
            }
            //section to force deletion/addition at null bounds -- potentially redundant with setShaving()
            /*
            // at max size in gene and exp force deletion
            //if (move_params.possible_move_types[1]!= 2 &&
            //        move_params.isExpAdd()) {
            if ((Ic.length == rmb.irv.prm.IMAX && move_params.Experiment_or_Gene == 1) ||
                    (Jc.length == rmb.irv.prm.JMAX && move_params.Experiment_or_Gene == 0)) {
                move_params.Delete_or_Add = 0; //force deletion
                if (move_params.PA > 0.5) move_params.PA = 1 - move_params.PA;
                System.out.println("setAddDelGeneExp forcing deletion and flipping PA " + move_params.PA);
            }
            // at min size in gene and exp force addition
            //if (move_params.possible_move_types[0]!= 1 &&
            //        move_params.possible_move_types[2] != 3) {
            else if ((Ic.length == rmb.irv.prm.IMIN && move_params.Experiment_or_Gene == 1) ||
                    (Jc.length == rmb.irv.prm.JMIN && move_params.Experiment_or_Gene == 0)) {
                move_params.Delete_or_Add = 1;
                if (move_params.PA <= 0.5) move_params.PA = 1 - move_params.PA;
                System.out.println("setAddDelGeneExp forcing addition and flipping PA " + move_params.PA);
            }*/

        }
    }


    /**
     * Creates the list of all possible individual moves depending on the current MOVE_TYPE.
     *
     * @param a
     * @param current
     * @param move_type
     * @return
     */
    public ArrayList makeMoveStrings(ArrayList[] a, ValueBlockPre current, String move_type) {

        int count_exclude = 0;
        current.setDataAndMean(rmb.expr_matrix.data);
        ValueBlockPre cur = new ValueBlockPre(current);
//System.out.println("makeMoveStrings start: move_type " + move_type + "\tparam_path " + param_path);
        ArrayList ret = new ArrayList();
//System.out.println("makeMoveStrings g: " + genesStr + "\te: " + expsStr);

        String current_genesStr = BlockMethods.IcJctoijID(current.genes);
        String current_expsStr = BlockMethods.IcJctoijID(current.exps);

        if (move_type.equals("gene+")) {
            for (int i = 0; i < a[0].size(); i++) {
                int[] mod_genes = current.genes;
                Integer integer = (Integer) a[0].get(i);
                int add_index = integer.intValue();
                //add new gene
                mod_genes = util.MoreArray.add(mod_genes, add_index);
                //sort genesStr ascending

                Arrays.sort(mod_genes);

                int[][] newcoords = {mod_genes, current.exps};
                cur.NRCoords(newcoords);
                cur.setDataAndMean(rmb.expr_matrix.data);
                int index = -1;
                try {
                    index = MoreArray.getArrayInd(cur.genes, add_index);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                boolean aboveNaNThresholdGene = cur.isAboveGeneNaN(index, rmb.irv.prm.PERCENT_ALLOWED_MISSING_GENES);
                boolean aboveNaNThreshold = cur.isAboveNaN(rmb.irv.prm.PERCENT_ALLOWED_MISSING_IN_BLOCK);
                //if (!rmb.irv.prm.CONSTANT) {
                if (aboveNaNThresholdGene && aboveNaNThreshold) {
                    String curStr = util.MoreArray.toString(mod_genes, ",") + "/" + current_expsStr;
                    boolean added = false;
                    if (rmb.exclude_vbl != null) {
                        if (rmb.irv.prm.EXCLUDE_OVERLAP_THRESHOLD == 1.0) {
                            if (!rmb.exclude_list.contains(curStr)) {
                                ret.add(curStr);
                                added = true;
                            }
                        } else if (rmb.irv.prm.EXCLUDE_OVERLAP_THRESHOLD > 0.0) {
                            double overlap = ValueBlockListMethods.overlap(newcoords, rmb.exclude_vbl);//rmb.exclude_vbl.overlap(newcoords);
                            if (overlap < rmb.irv.prm.EXCLUDE_OVERLAP_THRESHOLD) {
                                ret.add(curStr);
                                added = true;
                            }
                        }
                        if (!added)
                            count_exclude++;
                        if (!added && debug)
                            System.out.println("excluding g+ s " + curStr);
                    } else
                        ret.add(curStr);
                }
                //}
                /*else if (rmb.irv.prm.FRXN_SIGN) {
                    boolean currentrowPlus = mathy.stat.avg(cur.exp_data[index]) > 0 ? true : false;
                    if ((current.majorityRowPlus && currentrowPlus) || (!current.majorityRowPlus && !currentrowPlus)) {
                        String curStr = util.MoreArray.toString(mod_genes, ",") + "/" + current_expsStr;
                        ret.add(curStr);
                    }
                }*/
            }
        } else if (move_type.equals("gene-")) {
            // System.out.println("makeMoveStrings gene- " + move_type + "\t" + current.coords[0].size());
            for (int i = 0; i < current.genes.length; i++) {
                int[] mod_genes = current.genes;
                mod_genes = util.MoreArray.remove(mod_genes, i);
                int[][] newcoords = {mod_genes, current.exps};
                cur.NRCoords(newcoords);
                cur.setDataAndMean(rmb.expr_matrix.data);
                boolean aboveNaNThreshold = cur.isAboveNaN(rmb.irv.prm.PERCENT_ALLOWED_MISSING_IN_BLOCK);
                if (aboveNaNThreshold) {
                    String rem_genes = util.MoreArray.toString(mod_genes, ",");
                    String curStr = rem_genes + "/" + current_expsStr;
                    boolean added = false;
                    if (rmb.exclude_vbl != null) {
                        if (rmb.irv.prm.EXCLUDE_OVERLAP_THRESHOLD == 1.0) {
                            if (!rmb.exclude_list.contains(curStr)) {
                                ret.add(curStr);
                                added = true;
                            }
                        } else if (rmb.irv.prm.EXCLUDE_OVERLAP_THRESHOLD > 0.0) {
                            double overlap = ValueBlockListMethods.overlap(newcoords, rmb.exclude_vbl);//rmb.exclude_vbl.overlap(newcoords);
                            if (overlap < rmb.irv.prm.EXCLUDE_OVERLAP_THRESHOLD) {
                                ret.add(curStr);
                                added = true;
                            }
                        }
                        if (!added)
                            count_exclude++;
                        if (!added && debug)
                            System.out.println("excluding g- s " + curStr);
                    } else
                        ret.add(curStr);
                }
            }
        } else if (move_type.equals("experiment+")) {
            //System.out.println("makeMoveStrings " + move_type + "\t" + a[1].size());
            for (int i = 0; i < a[1].size(); i++) {
                int[] mod_exp = current.exps;
                Integer integer = (Integer) a[1].get(i);
                int val = integer.intValue();
                mod_exp = util.MoreArray.add(mod_exp, integer.intValue());
                Arrays.sort(mod_exp);
                /*int index = 0;
                try {
                    index = MoreArray.getArrayInd(mod_exp, val);
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
                int[][] newcoords = {current.genes, mod_exp};
                cur.NRCoords(newcoords);
                cur.setDataAndMean(rmb.expr_matrix.data);

                int index = -1;
                try {
                    index = MoreArray.getArrayInd(cur.exps, val);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                boolean aboveNaNThresholdExp = cur.isAboveExpNaN(index, rmb.irv.prm.PERCENT_ALLOWED_MISSING_EXP);
                boolean aboveNaNThreshold = cur.isAboveNaN(rmb.irv.prm.PERCENT_ALLOWED_MISSING_IN_BLOCK);
                //if (!rmb.irv.prm.FRXN_SIGN) {
                if (aboveNaNThresholdExp && aboveNaNThreshold) {// && arrayInd == -1) {
                    String curStr = current_genesStr + "/" + util.MoreArray.toString(mod_exp, ",");
                    boolean added = false;
                    if (rmb.exclude_vbl != null) {
                        if (rmb.irv.prm.EXCLUDE_OVERLAP_THRESHOLD == 1.0) {
                            if (!rmb.exclude_list.contains(curStr)) {
                                ret.add(curStr);
                                added = true;
                            }
                        } else if (rmb.irv.prm.EXCLUDE_OVERLAP_THRESHOLD > 0.0) {
                            double overlap = ValueBlockListMethods.overlap(newcoords, rmb.exclude_vbl);//rmb.exclude_vbl.overlap(newcoords);
                            if (overlap < rmb.irv.prm.EXCLUDE_OVERLAP_THRESHOLD) {
                                ret.add(curStr);
                                added = true;
                            }
                        }
                        if (!added)
                            count_exclude++;
                        if (!added && debug)
                            System.out.println("excluding e+ s " + curStr);
                    } else
                        ret.add(curStr);
                }
                //}
            /*else if (rmb.irv.prm.FRXN_SIGN) {
                    boolean currentColPlus = mathy.stat.avg(mathy.Matrix.extractColumn(cur.exp_data, index + 1)) > 0 ? true : false;
                    if (((current.majorityColPlus && currentColPlus) || (!current.majorityColPlus && !currentColPlus))) {
                        if (aboveNaNThresholdExp && aboveNaNThreshold) {// && arrayInd == -1) {
                            String curStr = current_genesStr + "/" + util.MoreArray.toString(mod_exp, ",");
                            ret.add(curStr);
                        }
                    }
                }*/
            }
        } else if (move_type.equals("experiment-")) {
            //System.out.println("makeMoveStrings " + move_type + "\t" + current.coords[1].size());
            for (int i = 0; i < current.exps.length; i++) {
                int[] mod_exp = current.exps;
                mod_exp = util.MoreArray.remove(mod_exp, i);
                int[][] newcoords = {current.genes, mod_exp};
                //String curid = BlockMethods.IcJctoijID(newcoords);
                cur.NRCoords(newcoords);
                cur.setDataAndMean(rmb.expr_matrix.data);
                boolean aboveNaNThreshold = cur.isAboveNaN(rmb.irv.prm.PERCENT_ALLOWED_MISSING_IN_BLOCK);
                if (aboveNaNThreshold) {
                    String curStr = current_genesStr + "/" + util.MoreArray.toString(mod_exp, ",");
                    boolean added = false;
                    if (rmb.exclude_vbl != null) {
                        if (rmb.irv.prm.EXCLUDE_OVERLAP_THRESHOLD == 1.0) {
                            if (!rmb.exclude_list.contains(curStr)) {
                                ret.add(curStr);
                                added = true;
                            }
                        } else if (rmb.irv.prm.EXCLUDE_OVERLAP_THRESHOLD > 0.0) {
                            double overlap = ValueBlockListMethods.overlap(newcoords, rmb.exclude_vbl);//rmb.exclude_vbl.overlap(newcoords);
                            if (overlap < rmb.irv.prm.EXCLUDE_OVERLAP_THRESHOLD) {
                                ret.add(curStr);
                                added = true;
                            }
                        }
                        if (!added)
                            count_exclude++;
                        if (!added && debug)
                            System.out.println("excluding e- s " + curStr);
                    } else
                        ret.add(curStr);
                }
            }
        }

        if (debug) {
            System.out.println("makeMoveStrings size " + ret.size());
        }
        if (rmb.exclude_vbl == null && debug) {
            System.out.println("makeMoveStrings no exclude");
        } else if (debug) {
            System.out.println("makeMoveStrings exclude " + count_exclude + "\trmb.exclude_vbl.size() " + rmb.exclude_vbl.size());
        }
        return ret;
    }

    /**
     * Creates the list of all possible individual moves depending on the current MOVE_TYPE.
     *
     * @param moves_genes_exps
     * @param current
     * @param move_type
     * @return
     */
    public ArrayList makeMoveStringsBatch(ArrayList[] moves_genes_exps, ValueBlockPre current, String move_type) {

        int count_exclude = 0;
        current.setDataAndMean(rmb.expr_matrix.data);
        ValueBlockPre cur = new ValueBlockPre(current);
//System.out.println("makeMoveStrings start: move_type " + move_type + "\tparam_path " + param_path);
        ArrayList ret = new ArrayList();
//System.out.println("makeMoveStrings g: " + genesStr + "\te: " + expsStr);
        String current_genesStr = BlockMethods.IcJctoijID(current.genes);
        String current_expsStr = BlockMethods.IcJctoijID(current.exps);
        if (move_type.equals("gene+")) {
            for (int i = 0; i < moves_genes_exps[0].size(); i++) {
                int[] mod_genes = current.genes;
                int[] genearray = (int[]) moves_genes_exps[0].get(i);
                //add new genes
                mod_genes = util.MoreArray.add(mod_genes, genearray);
                //sort genesStr ascending
                Arrays.sort(mod_genes);
                int[][] newcoords = {mod_genes, current.exps};
                cur.NRCoords(newcoords);
                cur.setDataAndMean(rmb.expr_matrix.data);

                //remove any gene or exps rows which violate the missing data threshold
                /*TODO reinstate trimNaN*/
                //cur.trimNAN(rmb.expr_matrix.data, rmb.irv.prm.PERCENT_ALLOWED_MISSING_GENES, rmb.irv.prm.PERCENT_ALLOWED_MISSING_EXP, debug_createPreCritTopList);
                cur.basictrimNAN(rmb.expr_matrix.data, rmb.irv.prm.PERCENT_ALLOWED_MISSING_GENES, rmb.irv.prm.PERCENT_ALLOWED_MISSING_EXP, debug_createPreCritTopList);

                if (cur.genes.length > rmb.irv.prm.IMIN && cur.exps.length > rmb.irv.prm.JMIN) {

                    boolean aboveNaNThreshold = cur.isAboveNaN(rmb.irv.prm.PERCENT_ALLOWED_MISSING_IN_BLOCK);
                    /*if (debug_getCriteriaForBlock) {
                        System.out.println("makeMoveStringsBatch gene+ aboveNaNThresholdGene_count " + aboveNaNThresholdGene_count + "\t" + aboveNaNThreshold);
                    }*/
                    //if at least one gene from batch move satisfied missing data threshold
                    //if (aboveNaNThresholdGene_count > 0 && aboveNaNThreshold) {
                    if (aboveNaNThreshold) {
                        String curStr = util.MoreArray.toString(mod_genes, ",") + "/" + current_expsStr;
                        boolean added = false;
                        if (rmb.exclude_vbl != null) {
                            if (rmb.irv.prm.EXCLUDE_OVERLAP_THRESHOLD == 1.0) {
                                /*if (debug) {
                                    System.out.println("excluding test g+ b ");
                                    System.out.println(rmb.exclude_list.get(0));
                                    System.out.println(curStr);
                                }*/
                                if (!rmb.exclude_list.contains(curStr)) {
                                    ret.add(curStr);
                                    added = true;
                                }
                            } else if (rmb.irv.prm.EXCLUDE_OVERLAP_THRESHOLD > 0.0) {
                                //TODO update overlap to account for area OR exps and genes separately
                                double overlap = ValueBlockListMethods.overlap(newcoords, rmb.exclude_vbl);
                                if (overlap < rmb.irv.prm.EXCLUDE_OVERLAP_THRESHOLD) {
                                    added = true;
                                    ret.add(curStr);
                                } /*else {
                                    System.out.println("excluding " + curStr + "\t" + overlap);
                                }*/
                            }
                            if (!added)
                                count_exclude++;
                            if (!added && debug)
                                System.out.println("excluding g+ b " + curStr);
                        } else
                            ret.add(curStr);
                    }
                    //}
                }
            }
        } else if (move_type.equals("gene-")) {
            // System.out.println("makeMoveStrings gene- " + move_type + "\t" + current.coords[0].size());
            for (int i = 0; i < moves_genes_exps[0].size(); i++) {
                int[] mod_genes = current.genes;
                int[] genearray = (int[]) moves_genes_exps[0].get(i);
                //remove genes
                try {
                    mod_genes = MoreArray.removeByVal(mod_genes, genearray);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //sort genes ascending
                //Arrays.sort(mod_genes);
                if (mod_genes.length >= rmb.irv.prm.IMIN) {
                    int[][] newcoords = {mod_genes, current.exps};
                    cur.NRCoords(newcoords);
                    cur.setDataAndMean(rmb.expr_matrix.data);

                    //remove any gene or exps rows which violate the missing data threshold
                    /*TODO reinstate trimNaN*/
                    //cur.trimNAN(rmb.expr_matrix.data, rmb.irv.prm.PERCENT_ALLOWED_MISSING_GENES, rmb.irv.prm.PERCENT_ALLOWED_MISSING_EXP, debug_createPreCritTopList);
                    cur.basictrimNAN(rmb.expr_matrix.data, rmb.irv.prm.PERCENT_ALLOWED_MISSING_GENES, rmb.irv.prm.PERCENT_ALLOWED_MISSING_EXP, debug_createPreCritTopList);

                    if (cur.genes.length > rmb.irv.prm.IMIN && cur.exps.length > rmb.irv.prm.JMIN) {
                        boolean aboveNaNThreshold = cur.isAboveNaN(rmb.irv.prm.PERCENT_ALLOWED_MISSING_IN_BLOCK);
                        if (debug_getCriteriaForBlock) {
                            System.out.println("makeMoveStringsBatch gene- aboveNaNThreshold " + aboveNaNThreshold);
                        }
                        if (aboveNaNThreshold) {
                            String curStr = util.MoreArray.toString(mod_genes, ",") + "/" + current_expsStr;
                            boolean added = false;
                            if (rmb.exclude_vbl != null) {
                                if (rmb.irv.prm.EXCLUDE_OVERLAP_THRESHOLD == 1.0) {
                                   /* if (debug) {
                                        System.out.println("excluding test g- b ");
                                        System.out.println(rmb.exclude_list.get(0));
                                        System.out.println(curStr);
                                    }*/
                                    if (!rmb.exclude_list.contains(curStr)) {
                                        ret.add(curStr);
                                        added = true;
                                    }
                                } else if (rmb.irv.prm.EXCLUDE_OVERLAP_THRESHOLD > 0.0) {
                                    // TODO update overlap to account for area OR exps and genes separately
                                    double overlap = ValueBlockListMethods.overlap(newcoords, rmb.exclude_vbl);
                                    if (overlap < rmb.irv.prm.EXCLUDE_OVERLAP_THRESHOLD) {
                                        added = true;
                                        ret.add(curStr);
                                    } /*else {
                                        System.out.println("excluding " + curStr + "\t" + overlap);
                                    }*/
                                }
                                if (!added)
                                    count_exclude++;
                                if (!added && debug)
                                    System.out.println("excluding g- b " + curStr);
                            } else
                                ret.add(curStr);
                        }
                    }
                }
            }
        } else if (move_type.equals("experiment+")) {
            //System.out.println("makeMoveStrings " + move_type + "\t" + a[1].size());
            for (int i = 0; i < moves_genes_exps[1].size(); i++) {
                int[] mod_exps = current.exps;

                int[] exparray = (int[]) moves_genes_exps[1].get(i);
                //remove exps
                mod_exps = util.MoreArray.add(mod_exps, exparray);
                //sort exps ascending
                Arrays.sort(mod_exps);
                int[][] newcoords = {current.genes, mod_exps};
                cur.NRCoords(newcoords);
                cur.setDataAndMean(rmb.expr_matrix.data);

                //remove any gene or exps rows which violate the missing data threshold
                /*TODO reinstate trimNaN*/
                //cur.trimNAN(rmb.expr_matrix.data, rmb.irv.prm.PERCENT_ALLOWED_MISSING_GENES, rmb.irv.prm.PERCENT_ALLOWED_MISSING_EXP, debug_createPreCritTopList);
                cur.basictrimNAN(rmb.expr_matrix.data, rmb.irv.prm.PERCENT_ALLOWED_MISSING_GENES, rmb.irv.prm.PERCENT_ALLOWED_MISSING_EXP, debug_createPreCritTopList);

                if (cur.genes.length > rmb.irv.prm.IMIN && cur.exps.length > rmb.irv.prm.JMIN) {

                    //if (!rmb.irv.prm.CONSTANT || (rmb.irv.prm.CONSTANT && isSign)) {
                    boolean aboveNaNThreshold = cur.isAboveNaN(rmb.irv.prm.PERCENT_ALLOWED_MISSING_IN_BLOCK);
                    if (aboveNaNThreshold) {
                        //if (aboveNaNThresholdExp_count > 0 && aboveNaNThreshold) {
                        String curStr = current_genesStr + "/" + util.MoreArray.toString(mod_exps, ",");
                        boolean added = false;
                        if (rmb.exclude_vbl != null) {
                            if (rmb.irv.prm.EXCLUDE_OVERLAP_THRESHOLD == 1.0) {
                                /*if (debug) {
                                    System.out.println("excluding test e+ b ");
                                    System.out.println(rmb.exclude_list.get(0));
                                    System.out.println(curStr);
                                }*/
                                if (!rmb.exclude_list.contains(curStr)) {
                                    ret.add(curStr);
                                    added = true;
                                }
                            } else if (rmb.irv.prm.EXCLUDE_OVERLAP_THRESHOLD > 0.0) {
                                //TODO update overlap to account for area OR exps and genes separately
                                double overlap = ValueBlockListMethods.overlap(newcoords, rmb.exclude_vbl);
                                if (overlap < rmb.irv.prm.EXCLUDE_OVERLAP_THRESHOLD) {
                                    added = true;
                                    ret.add(curStr);
                                } /*else {
                                    System.out.println("excluding " + curStr + "\t" + overlap);
                                }*/
                            }
                            if (!added)
                                count_exclude++;
                            if (!added && debug)
                                System.out.println("excluding e+ b " + curStr);
                        } else
                            ret.add(curStr);
                    }
                    //}
                }
            }
        } else if (move_type.equals("experiment-")) {
            for (int i = 0; i < moves_genes_exps[1].size(); i++) {

                int[] mod_exps = current.exps;
                int[] exparray = (int[]) moves_genes_exps[1].get(i);
                /*if (debug) {
                    System.out.println("makeMoveStringsBatch experiment- current exps " + MoreArray.toString(mod_exps, ","));
                    System.out.println("makeMoveStringsBatch experiment- rem exps " + MoreArray.toString(exparray, ","));
                }*/
                //remove exps
                try {
                    mod_exps = MoreArray.removeByVal(mod_exps, exparray);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("mod_exps " + MoreArray.toString(mod_exps, ","));
                    System.out.println("exparray " + MoreArray.toString(exparray, ","));
                }
                if (mod_exps.length >= rmb.irv.prm.JMIN) {
                    //System.out.println("makeMoveStringsBatch experiment- new exps");
                    //MoreArray.printArray(mod_exps);
                    //sort exps ascending
                    //Arrays.sort(mod_exps);
                    int[][] newcoords = {current.genes, mod_exps};
                    cur.NRCoords(newcoords);
                    cur.setDataAndMean(rmb.expr_matrix.data);
                    //remove any gene or exps rows which violate the missing data threshold
                    /*TODO reinstate trimNaN*/
                    //cur.trimNAN(rmb.expr_matrix.data, rmb.irv.prm.PERCENT_ALLOWED_MISSING_GENES, rmb.irv.prm.PERCENT_ALLOWED_MISSING_EXP, debug_createPreCritTopList);
                    cur.basictrimNAN(rmb.expr_matrix.data, rmb.irv.prm.PERCENT_ALLOWED_MISSING_GENES, rmb.irv.prm.PERCENT_ALLOWED_MISSING_EXP, debug_createPreCritTopList);

                    if (cur.genes.length > rmb.irv.prm.IMIN && cur.exps.length > rmb.irv.prm.JMIN) {
                        boolean aboveNaNThreshold = cur.isAboveNaN(rmb.irv.prm.PERCENT_ALLOWED_MISSING_IN_BLOCK);
                        if (debug_getCriteriaForBlock) {
                            System.out.println("makeMoveStringsBatch exp- aboveNaNThreshold " + aboveNaNThreshold);
                        }
                        if (aboveNaNThreshold) {
                            String curStr = current_genesStr + "/" + util.MoreArray.toString(mod_exps, ",");
                            boolean added = false;
                            if (rmb.exclude_vbl != null) {
                                if (rmb.irv.prm.EXCLUDE_OVERLAP_THRESHOLD == 1.0) {
                                   /* if (debug) {
                                        System.out.println("excluding test e+ b ");
                                        System.out.println(rmb.exclude_list.get(0));
                                        System.out.println(curStr);
                                    }*/
                                    if (!rmb.exclude_list.contains(curStr)) {
                                        ret.add(curStr);
                                        added = true;
                                    }
                                } else if (rmb.irv.prm.EXCLUDE_OVERLAP_THRESHOLD > 0.0) {
                                    //TODO update overlap to account for area OR exps and genes separately
                                    double overlap = ValueBlockListMethods.overlap(newcoords, rmb.exclude_vbl);
                                    if (overlap < rmb.irv.prm.EXCLUDE_OVERLAP_THRESHOLD) {
                                        added = true;
                                        ret.add(curStr);
                                    } /*else {
                                        System.out.println("excluding " + curStr + "\t" + overlap);
                                    }*/
                                }
                                if (!added)
                                    count_exclude++;
                                if (!added && debug)
                                    System.out.println("excluding e- b " + curStr);
                            } else
                                ret.add(curStr);
                        }
                    }
                }
            }
        }

        if (debug) {
            System.out.println("makeMoveStringsBatch size " + ret.size());
        }
        if (rmb.exclude_vbl != null && debug)
            System.out.println("makeMoveStringsBatch exclude " + count_exclude);
        return ret;
    }

    /**
     * @param expind
     * @param genes
     * @return
     */
    public boolean countNaNGenes(int expind, ArrayList genes) {
        //String pass = "";
        StringBuffer buf = new StringBuffer("");
        for (int i = 0; i < genes.size(); i++) {
            Integer c = (Integer) genes.get(i);
            //pass += "expr_data[" + c.intValue() + ", " + expind + "],";
            buf.append("expr_data[");
            buf.append(c.intValue());
            buf.append(", ");
            buf.append(expind);
            buf.append("],");
        }
        //pass = pass.substring(0, pass.length() - 1);
        String pass = buf.substring(0, buf.length() - 1);

        String s = "sum(is.na(c(" + pass + ")))";
        if (debug) {
            System.out.println("R: " + s);
            System.out.println(rmb.irv.Rexpr = rmb.irv.Rengine.eval(s));
        }
        int val = rmb.irv.Rexpr.asInt();
        if (val == 0) {
            s = "sum(is.nan(c(" + pass + ")))";
            if (debug) {
                System.out.println("R: " + s);
                System.out.println(rmb.irv.Rexpr = rmb.irv.Rengine.eval(s));
            }
            val = rmb.irv.Rexpr.asInt();
        }

        //System.out.println("countNaNGenes" + pass + "\t" + val);
        double percent = genes.size() * rmb.irv.prm.PERCENT_ALLOWED_MISSING_GENES;
        if (val > percent) {
            //System.out.println("countNaNGenes " + val + "\tfalse");
            return false;
        }
        if (debug) {
            System.out.println("countNaNGenes " + val + "\ttrue");
        }
        return true;
    }

    /**
     * @param geneind
     * @param exp
     * @return
     */
    public boolean countNaNExp(int geneind, ArrayList exp) {
        //String pass = "";
        StringBuffer buf = new StringBuffer("");
        for (int i = 0; i < exp.size(); i++) {
            Integer c = (Integer) exp.get(i);
            //pass += "expr_data[" + geneind + ", " + c.intValue() + "],";
            buf.append("expr_data[");
            buf.append(geneind);
            buf.append(", ");
            buf.append(c.intValue());
            buf.append("],");
        }
        //pass = pass.substring(0, pass.length() - 1);
        String pass = buf.substring(0, buf.length() - 1);

        String s = "sum(is.na(c(" + pass + ")))";
        if (debug) {
            System.out.println("R: " + s);
            System.out.println(rmb.irv.Rexpr = rmb.irv.Rengine.eval(s));
        }
        int val = rmb.irv.Rexpr.asInt();
        if (val == 0) {
            s = "sum(is.nan(c(" + pass + ")))";
            if (debug) {
                System.out.println("R: " + s);
                System.out.println(rmb.irv.Rexpr = rmb.irv.Rengine.eval(s));
            }
            val = rmb.irv.Rexpr.asInt();
        }
        double percent = exp.size() * rmb.irv.prm.PERCENT_ALLOWED_MISSING_EXP;
        if (val > percent) {
            //System.out.println("countNaNExp" + val + "\tfalse");
            return false;
        }
        //System.out.println("countNaNExp" + val + "\ttrue");
        return true;
    }

    /**
     * Attempt gene addition
     *
     * @param Ic
     * @param data_len_I
     * @return
     */
    public ArrayList[] GA(int[] Ic, int data_len_I) {
        ArrayList[] ret = null;
        //System.out.println("GA start");
        int[] nIc = mathy.stat.createNaturalNumbersRemoved(1, data_len_I, Ic);
        //ArrayList cur = util.MoreArray.intarraytoList(nIc);
        if (nIc == null || nIc.length == 0) {
            System.out.println("WARNING: GA Ic leads to no more possible moves " + MoreArray.toString(Ic, ","));
            //MoreArray.printArray(Ic);
        } else {
            Arrays.sort(nIc);
            //Collections.sort(cur);
            //nIc = util.MoreArray.ArrayListtoInt(cur);
//System.out.println("GA nIc " + MoreArray.toString(nIc));
            ret = new ArrayList[2];
            ret[0] = util.MoreArray.addElements(ret[0], nIc);
            ret[1] = new ArrayList();
//System.out.println("GA end");
        }
        return ret;
    }


    /**
     * Attempt batch gene addition
     *
     * @param Ic
     * @param Jc
     * @return
     */
    public ArrayList[] batchGA(int[] Ic, int[] Jc) {
        int[] missvec = MoreArray.initArray(rmb.irv.prm.DATA_LEN_GENES, 1);
        for (int i = 0; i < rmb.irv.prm.DATA_LEN_GENES; i++) {
            double count = 0;
            for (int j = 0; j < Jc.length; j++) {
                count = Double.isNaN(rmb.expr_matrix.data[i][Jc[j] - 1]) ? (count + 1) : count;
            }
            if ((count / (double) Jc.length) > rmb.irv.prm.PERCENT_ALLOWED_MISSING_GENES) {
                missvec[i] = 0;
            }
        }
        /*int blocknonmissing = 0;
        for (int j = 0; j < Ic.length; j++) {
            if (missvec[Ic[j] - 1] == 1)
                blocknonmissing++;
        }
        int countnonmiss = stat.countGreaterThan(missvec, 0);        
        int diff = countnonmiss - blocknonmissing;
        if (diff >Parameters.MIN_NONMISSING_FOR_BATCH) {*/
        ArrayList[] ret = null;
        ret = new ArrayList[2];
        rmb.irv.Rengine.assign("Iibatch", Ic);
        if (debug_createPreCritTopList)
            System.out.println("R: Iibatch <- c(" + MoreArray.toString(Ic, ",") + ")");
        rmb.irv.Rengine.assign("Jjbatch", Jc);
        if (debug_createPreCritTopList)
            System.out.println("R: Jjbatch <- c(" + MoreArray.toString(Jc, ",") + ")");
        rmb.irv.Rengine.assign("missvec", missvec);
        if (debug_createPreCritTopList)
            System.out.println("R: missvec <- c(" + MoreArray.toString(missvec, ",") + ")");
        /*
        Create vector with 0 is > missingness threshold, 1 satisfies
        */
        String call = "currentbatch <- BatchCreate(expr_data,missvec,Iibatch,Jjbatch,1,1,pSize=" +
                rmb.irv.prm.BATCH_PERC + ", Ulim=" + rmb.irv.prm.IMAX + ", dmethod=\"" + rmb.irv.prm.BATCH_DMETHOD + "\", linkmethod=\"" + rmb.irv.prm.BATCH_LINKMETHOD + "\")";
        if (debug_createPreCritTopList)
            System.out.println("R: " + call);
        rmb.irv.Rexpr = rmb.irv.Rengine.eval(call);
        //System.out.println(Rengine.eval("currentbatch"));
        ret[0] = new ArrayList();
        ret[1] = new ArrayList();
        RVector tmpvect = rmb.irv.Rexpr.asVector();
        for (int i = 0; i < tmpvect.size(); i++) {
            int[] tmp = (tmpvect.at(i)).asIntArray();
            /*System.out.println("batchGA cluster " + i);
          System.out.println(MoreArray.toString(tmp, " "));*/
            ret[0].add(tmp);
        }
        //}
        //if (diff <= Parameters.MIN_NONMISSING_FOR_BATCH)
        //    System.out.println("batchGA only " + diff + " would have rejected move type");
        return ret;
    }


    /**
     * Attempt experiment addition
     *
     * @param Jc
     * @param data_len_J
     * @return
     */
    public ArrayList[] EA(int[] Jc, int data_len_J) {
        //System.out.println("EA start Jc");
        ArrayList[] ret = null;
        //MoreArray.printArray(",", Jc);
        int[] nJc = mathy.stat.createNaturalNumbersRemoved(1, data_len_J, Jc);
        //ArrayList cur = util.MoreArray.intarraytoList(nJc);
        if (nJc == null || nJc.length == 0) {
            System.out.println("WARNING: EA Jc leads to no more possible moves " + MoreArray.toString(Jc, ","));
            //MoreArray.printArray(Jc);
        } else {
            Arrays.sort(nJc);
            //Collections.sort(cur);
            //nJc = util.MoreArray.ArrayListtoInt(cur);
            //System.out.println("EA nJc " + MoreArray.toString(nJc));
            ret = new ArrayList[2];
            ret[0] = new ArrayList();
            ret[1] = util.MoreArray.addElements(ret[1], nJc);
            //System.out.println("EA end");
        }
        return ret;
    }

    /**
     * Attempt batch experiment addition
     *
     * @param Ic
     * @param Jc
     * @return
     */
    public ArrayList[] batchEA(int[] Ic, int[] Jc) {
        int[] missvec = MoreArray.initArray(rmb.irv.prm.DATA_LEN_EXPS, 1);
        for (int i = 0; i < rmb.irv.prm.DATA_LEN_EXPS; i++) {
            double count = 0;
            for (int j = 0; j < Ic.length; j++) {
                try {
                    count = Double.isNaN(rmb.expr_matrix.data[Ic[j] - 1][i]) ? (count + 1) : count;
                } catch (Exception e) {
                    System.out.println("batchEA " + i + "\t" + j + "\t" + rmb.expr_matrix.data.length + "\t" +
                            rmb.expr_matrix.data[0].length + "\t" + rmb.irv.prm.DATA_LEN_EXPS + "\t" + count);
                    e.printStackTrace();
                }
            }
            if ((count / (double) Ic.length) > rmb.irv.prm.PERCENT_ALLOWED_MISSING_EXP) {
                missvec[i] = 0;
            }
        }
        /*int blocknonmissing = 0;
        for (int j = 0; j < Jc.length; j++) {
            if (missvec[Jc[j] - 1] == 1)
                blocknonmissing++;
        }
        int countnonmiss = stat.countGreaterThan(missvec, 0);
        int diff = countnonmiss - blocknonmissing;
        if (diff > MINER_STATIC.MIN_NONMISSING_FOR_BATCH) {*/
        ArrayList[] ret = null;
        ret = new ArrayList[2];
        rmb.irv.Rengine.assign("Iibatch", Ic);
        if (debug_getCriteriaForBlock)
            System.out.println("R: Iibatch <- c(" + MoreArray.toString(Ic, ",") + ")");
        rmb.irv.Rengine.assign("Jjbatch", Jc);
        if (debug_getCriteriaForBlock)
            System.out.println("R: Jjbatch <- c(" + MoreArray.toString(Jc, ",") + ")");
        rmb.irv.Rengine.assign("missvec", missvec);
        if (debug_getCriteriaForBlock)
            System.out.println("R: missvec <- c(" + MoreArray.toString(missvec, ",") + ")");

        String call = "currentbatch <- BatchCreate(expr_data,missvec,Iibatch,Jjbatch,0,1,pSize=" +
                rmb.irv.prm.BATCH_PERC + ", Ulim=" + rmb.irv.prm.JMAX + ", dmethod=\"" + rmb.irv.prm.BATCH_DMETHOD + "\", linkmethod=\"" + rmb.irv.prm.BATCH_LINKMETHOD + "\")";
        if (debug_getCriteriaForBlock)
            System.out.println("R: " + call);
        rmb.irv.Rexpr = rmb.irv.Rengine.eval(call);
        if (debug_getCriteriaForBlock)
            System.out.println(rmb.irv.Rengine.eval("currentbatch"));
        ret[0] = new ArrayList();
        ret[1] = new ArrayList();
        RVector tmpvect = rmb.irv.Rexpr.asVector();
        for (int i = 0; i < tmpvect.size(); i++) {
            int[] tmp = (tmpvect.at(i)).asIntArray();
            //System.out.println("batchEA cluster");
            //MoreArray.printArray(tmp);
            ret[1].add(tmp);
        }
        //} else {
        //if (diff <= Parameters.MIN_NONMISSING_FOR_BATCH)
        //    System.out.println("batchEA only " + diff + " would have rejected move type");
        //}
        return ret;
    }

    /**
     * Attempt gene deletion
     *
     * @param Ic
     * @return
     */
    public ArrayList[] GD(int[] Ic) {
        ArrayList[] ret = null;
        //System.out.println("GD start");
        //ArrayList cur = util.MoreArray.intarraytoList(Ic);
        if (Ic != null && Ic.length > 0) {
            //Collections.sort(cur);
            //Ic = util.MoreArray.ArrayListtoInt(cur);
            Arrays.sort(Ic);
            //System.out.println("GD Ic " + MoreArray.toString(Ic));
            if (rmb.irv.prm.GENE_GROW) {
                for (int i = 0; i < VBPInitial.genes.length; i++) {
                    boolean test = stat.testRemove(Ic, VBPInitial.genes[i]);
                    if (test) {
                        Ic = MoreArray.remove(Ic, VBPInitial.genes[i]);
                    }
                }
            }
            ret = new ArrayList[2];
            ret[0] = util.MoreArray.addElements(ret[0], Ic);
            ret[1] = new ArrayList();
            //System.out.println("GD end");
        }
        return ret;
    }

    /**
     * Attempt batch gene deletion
     *
     * @param Ic
     * @return
     */
    public ArrayList[] batchGD(int[] Ic, int[] Jc) {
        ArrayList[] ret = new ArrayList[2];
        rmb.irv.Rengine.assign("Iibatch", Ic);
        if (debug_getCriteriaForBlock)
            System.out.println("R: Iibatch <- c(" + MoreArray.toString(Ic, ",") + ")");
        rmb.irv.Rengine.assign("Jjbatch", Jc);
        if (debug_getCriteriaForBlock)
            System.out.println("R: Jjbatch <- c(" + MoreArray.toString(Jc, ",") + ")");

        double curbatchperc = rmb.irv.prm.BATCH_PERC;
        if (Ic.length >= rmb.irv.prm.IMAX) {
            curbatchperc = Math.max(rmb.irv.prm.BATCH_PERC, (Ic.length - rmb.irv.prm.IMAX + rmb.irv.prm.IMAX * rmb.irv.prm.BATCH_PERC) / (double) Ic.length);//TODO test change to (double)
            if (debug)
                System.out.println("batchGD setting curbatchperc " + curbatchperc);
        }

        String call = "currentbatch <- BatchCreate(expr_data,null,Iibatch,Jjbatch,1,0,pSize=" +
                curbatchperc + ", Ulim=" + rmb.irv.prm.IMAX + ", dmethod=\"" + rmb.irv.prm.BATCH_DMETHOD + "\", linkmethod=\"" + rmb.irv.prm.BATCH_LINKMETHOD + "\")";
        if (debug_getCriteriaForBlock)
            System.out.println("R: " + call);
        rmb.irv.Rexpr = rmb.irv.Rengine.eval(call);
        if (debug_getCriteriaForBlock)
            System.out.println("rmb.irv.Rengine.eval(\"" + call + "\")");
        ret[0] = new ArrayList();
        ret[1] = new ArrayList();
        RVector tmpvect = null;
        try {
            tmpvect = rmb.irv.Rexpr.asVector();
        } catch (Exception e) {
            System.out.println("batchGD error " + call);
            e.printStackTrace();
        }
        for (int i = 0; i < tmpvect.size(); i++) {
            int[] tmp = (tmpvect.at(i)).asIntArray();
            //System.out.println("batchEA cluster");
            //MoreArray.printArray(tmp);
            ret[0].add(tmp);
        }
        return ret;
    }

    /**
     * Attempt experiment deletion
     *
     * @param Jc
     * @return
     */
    public ArrayList[] ED(int[] Jc) {
        ArrayList[] ret = null;
        //ArrayList cur = util.MoreArray.intarraytoList(Jc);
        if (Jc != null && Jc.length > 0) {
            Arrays.sort(Jc);
            //Collections.sort(cur);
            //Jc = util.MoreArray.ArrayListtoInt(cur);
            //System.out.println("ED Jc " + MoreArray.toString(Jc));
            if (rmb.irv.prm.EXP_GROW) {
                for (int i = 0; i < VBPInitial.exps.length; i++) {
                    boolean test = stat.testRemove(Jc, VBPInitial.exps[i]);
                    if (test) {
                        Jc = MoreArray.remove(Jc, VBPInitial.exps[i]);
                    }
                }
            }
            ret = new ArrayList[2];
            ret[0] = new ArrayList();
            ret[1] = util.MoreArray.addElements(ret[1], Jc);
        }
        return ret;
    }

    /**
     * Attempt batch experiment deletion
     *
     * @param Jc
     * @return
     */
    public ArrayList[] batchED(int[] Ic, int[] Jc) {

        ArrayList[] ret = new ArrayList[2];
        rmb.irv.Rengine.assign("Iibatch", Ic);
        if (debug_getCriteriaForBlock)
            System.out.println("R: Iibatch <- c(" + MoreArray.toString(Ic, ",") + ")");
        rmb.irv.Rengine.assign("Jjbatch", Jc);
        if (debug_getCriteriaForBlock)
            System.out.println("R: Jjbatch <- c(" + MoreArray.toString(Jc, ",") + ")");

        if (debug)
            System.out.println("batchED Jc " + MoreArray.toString(Jc, ","));

        double curbatchperc = rmb.irv.prm.BATCH_PERC;
        if (Jc.length >= rmb.irv.prm.JMAX) {
            curbatchperc = Math.max(rmb.irv.prm.BATCH_PERC,
                    (Jc.length - rmb.irv.prm.JMAX + rmb.irv.prm.JMAX * rmb.irv.prm.BATCH_PERC) / Jc.length);//TODO change to (double)
            if (debug) {
                System.out.println("batchED reset curbatchperc from " + rmb.irv.prm.BATCH_PERC + "\tto " + curbatchperc);
            }
            //if (debug)
            //System.out.println("batchED setting curbatchperc " + curbatchperc);
        }
        String call = "currentbatch <- BatchCreate(expr_data,null,Iibatch,Jjbatch,0,0,pSize=" +
                curbatchperc + ", Ulim=" + rmb.irv.prm.JMAX + ", dmethod=\"" + rmb.irv.prm.BATCH_DMETHOD + "\", linkmethod=\"" + rmb.irv.prm.BATCH_LINKMETHOD + "\")";
        rmb.irv.Rexpr = rmb.irv.Rengine.eval(call);
        if (debug)
            System.out.println("R: " + call);
        //System.out.println(rmb.irv.Rengine.eval("currentbatch"));
        ret[0] = new ArrayList();
        ret[1] = new ArrayList();
        RVector tmpvect = null;
        try {
            tmpvect = rmb.irv.Rexpr.asVector();
        } catch (Exception e) {
            System.out.println("R: Iibatch <- c(" + MoreArray.toString(Ic, ",") + ")");
            System.out.println("R: Jjbatch <- c(" + MoreArray.toString(Jc, ",") + ")");
            System.out.println("R: " + call);
            e.printStackTrace();
        }
        for (int i = 0; i < tmpvect.size(); i++) {
            int[] tmp = (tmpvect.at(i)).asIntArray();
            //if (debug) {
            //    System.out.println("batchED cluster " + MoreArray.toString(tmp, ","));
            ret[1].add(tmp);
        }
        return ret;
    }

    /**
     * (re)Initializes parameters for running a search.
     */
    public void initStoreVarforRun() {
        if (debug)
            System.out.println("initStoreVarforRun rmb.first " + rmb.first);
        /*if (rmb.first) {
            if (debug)
                System.out.println("initStoreVarforRun initializing HashMaps");
            //moves_hash = new HashMap();
            //precrit_moves_map = new HashMap();
            //full_moves_map = new HashMap();
        }*/

        vls = new ValueListSet();
        trajectory = new ValueBlockList();
        if (doVariance) {
            finalVarListGenes = new ValueBlockList();
            finalVarListExp = new ValueBlockList();
        }

        //tried_blocks = new HashMap();
        trajectory_blocks = new HashMap();

        initMoveParam(move_params != null ? move_params.move_count : 0);
    }

    /**
     * Initializes the MoveParams object before the search loop
     */
    private void initMoveParam(int move_count) {
        move_params = new MoveParams(rmb.irv.prm, debug || debug_createPreCritTopList || debug_getCriteriaForBlock ? true : false);
        move_params.move_count = move_count;
        last_genes = null;
        last_experiments = null;

        if (debug) {
            System.out.println("initStoreVarforRun move_params");
//System.out.println(move_params);
            move_params.printOut();
        }
    }

    /**
     * Returns an array with the precriteria for the current block.
     *
     * @param vb
     * @param crit
     * @return
     */
    private double[][] getCriteriaForBlock(ValueBlock vb, Criterion crit) {
        //System.out.println("getCriteriaForBlock block " + crit_index);
        return runCrit(crit, vb.genes, vb.exps);
    }


    /**
     * Returns an array with the precriteria for the current block.
     *
     * @param vb
     * @param crit
     * @return
     */
    private double[] getCriteriaForBlockSingle(ValueBlock vb, Criterion crit) {
        //System.out.println("getCriteriaForBlock block " + crit_index);
        double[] ret = ValueBlockPre.reconcileCritVals(runCrit(crit, vb.genes, vb.exps), this.debug_getCriteriaForBlock);
        return ret;
    }

    /**
     * @param genes
     * @param exps
     * @param crit
     * @return
     */
    private double[][] getCriteriaForBlock(int[] genes, int[] exps, Criterion crit) {
        //System.out.println("getCriteriaForBlock array " + crit_index);
        return runCrit(crit, genes, exps);
    }


    /**
     * @param IcJc
     * @param crit
     * @return
     */
    private ArrayList getCriteriaForBlockList(ArrayList IcJc, Criterion crit) {
        //System.out.println("getCriteriaForBlock array " + crit_index);
        return runCritList(crit, IcJc);
    }

    /**
     *
     */
    /**
     * @param crit
     * @param genes
     * @param exps
     * @return
     */
    private double[][] runCrit(Criterion crit, int[] genes, int[] exps) {
        if (debug_getCriteriaForBlock)
            System.out.println("runCrit " + crit.crit);
        long move_start_time = System.currentTimeMillis();//gd.giveMilli();

        //setGenesExps(genes, exps);

        rmb.irv.onv.setCurNull(genes, exps, crit);

        if (debug_getCriteriaForBlock)
            System.out.println("runCrit " + (rmb.irv.onv.nullMSEData != null ? "use null" : "no null") + "\t" +
                    (rmb.irv.onv.nullInteractData != null ? "use int null" : "no int null") + "\t" + crit.crit);

        /*int onlymean = 0;
        int arrayInd = 0;
        try {
            arrayInd = MoreArray.getArrayInd(MINER_STATIC.MEANCritAll, crit.crit);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (arrayInd != -1)
            onlymean = 1;*/

        double[] critwnull = null;
        double[] critwonull = null;

        int count = 0;
        //5 attempts to obtain criterion value
        while (critwnull == null && count < 5) {
            double[][] data = new double[0][];
            try {
                data = ComputeCrit.compute(rmb.irv, genes, exps,
                        rmb.irv.prm, crit,
                        rmb.gene_labels, rmb.feat_matrix != null ? rmb.feat_matrix.data : null,
                        debug_getCriteriaForBlock || debug_createPreCritTopList ? true : false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            critwnull = data[0];
            critwonull = data[1];
            if (critwnull != null)
                break;
            else
                count++;
        }

        if (critwnull == null) {


        }

        if (debug_getCriteriaForBlock) {
            long move_end_time = System.currentTimeMillis();//gd.giveMilli();
            System.out.println("Run time for criterion " + ((move_end_time - move_start_time) / 1000.0) + " s");

            boolean bad = false;
            for (int i = 0; i < critwnull.length; i++) {
                if (critwnull[i] == Double.NaN) {
                    bad = true;
                    break;
                }
            }
            for (int i = 0; i < critwonull.length; i++) {
                if (critwonull[i] == Double.NaN) {
                    bad = true;
                    break;
                }
            }

            if (bad) {
                System.out.println("WARNING found nan, wo");
                MoreArray.printArray(critwonull);
                System.out.println("WARNING found nan, w");
                MoreArray.printArray(critwnull);
            }
        }


        double[][] ret = {critwnull, critwonull};
        return ret;
    }

    /**
     * @param crit
     * @param IcJc
     * @return
     */
    private ArrayList runCritList(Criterion crit, ArrayList IcJc) {
        if (debug_getCriteriaForBlock)
            System.out.println("runCrit " + crit.crit);
        long move_start_time = System.currentTimeMillis();//gd.giveMilli();

        //setGenesExps(genes, exps);

        ArrayList ar = new ArrayList();
        for (int z = 0; z < IcJc.size(); z++) {
            int[][] get = (int[][]) IcJc.get(z);
            int[] genes = get[0];
            int[] exps = get[1];

            rmb.irv.onv.setCurNull(genes, exps, crit);

            if (debug_getCriteriaForBlock)
                System.out.println("runCrit " + (rmb.irv.onv.nullMSEData != null ? "use null" : "no null") + "\t" +
                        (rmb.irv.onv.nullInteractData != null ? "use int null" : "no int null") + "\t" + crit.crit);

            /*int onlymean = 0;
            int arrayInd = 0;
            try {
                arrayInd = MoreArray.getArrayInd(MINER_STATIC.MEANCritAll, crit.crit);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (arrayInd != -1)
                onlymean = 1;*/

            double[] critwnull = null;
            double[] critwonull = null;

            int count = 0;
            //5 attempts to obtain criterion value
            while (critwnull == null && count < 5) {
                double[][] data = new double[0][];
                try {
                    data = ComputeCrit.compute(rmb.irv, genes, exps,
                            rmb.irv.prm, crit,
                            rmb.gene_labels, rmb.feat_matrix != null ? rmb.feat_matrix.data : null,
                            debug_getCriteriaForBlock || debug_createPreCritTopList ? true : false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                critwnull = data[0];
                critwonull = data[1];
                if (critwnull != null)
                    break;
                else
                    count++;
            }

            if (critwnull == null) {


            }

            if (debug_getCriteriaForBlock) {
                long move_end_time = System.currentTimeMillis();//gd.giveMilli();
                System.out.println("Run time for criterion " + ((move_end_time - move_start_time) / 1000.0) + " s");
            }

            double[][] ret = {critwnull, critwonull};
            ar.add(ret);

        }

        return ar;
    }

    /**
     * @param vb
     * @return
     */
    private void getFinalCriteriaForBlock(ValueBlock vb) {
        //long move_start_time = gd.giveMilli();

        if (debug) {
            System.out.println("getFinalCriteriaForBlock rmb.irv.prm.CRIT_TYPE_INDEX && rmb.irv.prm.PRECRIT_TYPE_INDEX " + rmb.irv.prm.CRIT_TYPE_INDEX
                    + "\t" + rmb.irv.prm.PRECRIT_TYPE_INDEX);
            System.out.println("getFinalCriteriaForBlock WEIGH_EXPR " + rmb.irv.prm.WEIGH_EXPR + "\tisMeanCrit " +
                    rmb.irv.prm.crit.isMeanCrit + "\tdebug_getCriteriaForBlock " + debug_getCriteriaForBlock);
        }
        boolean use_mean_now = rmb.irv.prm.crit.isMeanCrit ? true : rmb.irv.prm.USE_MEAN;
        if (debug) {
            System.out.println("getFinalCriteriaForBlock use_mean_now " + use_mean_now + "\t" + rmb.irv.prm.crit.usemean);
        }

        if (rmb.irv.prm.CRIT_TYPE_INDEX != rmb.irv.prm.PRECRIT_TYPE_INDEX) {
            double[][] crits = getCriteriaForBlock(vb, rmb.irv.prm.crit);
            if (debug)
                System.out.print(".");
            if (debug_getCriteriaForBlock) {
                System.out.println("getFinalCriteriaForBlock");
                MoreArray.printArray(crits);
            }

            boolean[] passcrits = Criterion.getExprCritTypes(rmb.irv.prm.crit, rmb.irv.prm.WEIGH_EXPR,
                    use_mean_now, debug_getCriteriaForBlock);
            if (debug)
                System.out.println("getFinalCriteriaForBlock passcrits != " + (passcrits == null ? "null" : "OK"));
            vb.updateCrit(crits, rmb.irv.prm.WEIGH_EXPR, passcrits, debug_getCriteriaForBlock);
        } else {
            boolean[] passcrits = Criterion.getExprCritTypes(rmb.irv.prm.crit, rmb.irv.prm.WEIGH_EXPR,
                    use_mean_now, debug_getCriteriaForBlock);
            if (debug)
                System.out.println("getFinalCriteriaForBlock passcrits = " + (passcrits == null ? "null" : "OK"));
            if (debug_getCriteriaForBlock) {
                System.out.println("doing vb " + BlockMethods.IcJctoijID(vb.genes, vb.exps));
                MoreArray.printArray(vb.all_precriteria);
                System.out.println("vb.all_criteria");
                MoreArray.printArray(vb.all_criteria);
            }

            vb.updateCrit(vb.all_precriteria, rmb.irv.prm.WEIGH_EXPR, passcrits, debug_getCriteriaForBlock);
        }
    }


    /**
     *
     */
    private void testForFullCriteriaFunctionInput() {
        System.out.println("getTopList nullData");
        System.out.println(rmb.irv.Rengine.eval("nullData"));
        System.out.println("getTopList precrit_nullData");
        System.out.println(rmb.irv.Rengine.eval("precrit_nullData"));
        System.out.println("getTopList nullInteractData");
        System.out.println(rmb.irv.Rengine.eval("nullInteractData"));
        System.out.println("getTopList expr_data");
        System.out.println(rmb.irv.Rengine.eval("expr_data[1,1]"));
        System.out.println("getTopList interact_data");
        System.out.println(rmb.irv.Rengine.eval("interact_data[1,1]"));
        System.out.println("getTopList feature_data");
        System.out.println(rmb.irv.Rengine.eval("feature_data[1,1]"));
        System.out.println("getTopList Ifactor");
        System.out.println(rmb.irv.Rengine.eval("Ifactor[1]"));
        System.out.println("getTopList Ic");
        System.out.println(rmb.irv.Rengine.eval("Ic"));
        System.out.println("getTopList Jc");
        System.out.println(rmb.irv.Rengine.eval("Jc"));
    }

    /**
     * Sets the algorithm parameters in R from Java, called at the top of the main loop.
     *
     * @param ic
     * @param jc
     */
    private void setRVariables(int[] ic, int[] jc) {
        if (debug) {
            System.out.println("setRVariables start " + ic.length + "\t" + jc.length);
            String s = "Ic<-c(" + MoreArray.toString(ic, ",") + ")";
            System.out.println("R: " + s);
            System.out.println(rmb.irv.Rengine.eval(s));
            String s1 = "Jc<-c(" + MoreArray.toString(jc, ",") + ")";
            System.out.println("R: " + s1);
            System.out.println(rmb.irv.Rengine.eval(s1));
            System.out.println("setRVariables end");
        }
    }

    /**
     * Marks where the location of cells of the block within the data matrix.
     * The index of Cells in the matrix is ofset by -1 relative to block designations (R vs Java convention).
     *
     * @param a
     * @return
     */
    public int[][] markBlock(ArrayList[] a, int[][] ids) {
        ids = util.MoreArray.initArray(ids, 0);
        int[] xs = util.MoreArray.ArrayListtoInt(a[0]);
        int[] ys = util.MoreArray.ArrayListtoInt(a[1]);
        for (int i = 0; i < xs.length; i++) {
            for (int j = 0; j < ys.length; j++) {
                ids[xs[i] - 1][ys[j] - 1] = 1;
//System.out.println("markBlock setting " + xs[i] + "\t" + ys[j] + "\t" + ids[xs[i] - 1][ys[j] - 1]);
            }
        }
        return ids;
    }

    /**
     * Prints the R variables initialized from Java.
     *
     * @return
     */
    private void printInitRVariables() {
        System.out.println("Miner log " + "Ic " + (rmb.irv.Rexpr = rmb.irv.Rengine.eval("Ic")));
        System.out.println("Miner log " + "Jc " + (rmb.irv.Rexpr = rmb.irv.Rengine.eval("Jc")));
        System.out.println("Miner log " + "expr_data " + (rmb.irv.Rexpr = rmb.irv.Rengine.eval("length(expr_data)")));
        System.out.println("Miner log " + "feature_data");
        System.out.println("Miner log " + (rmb.irv.Rexpr = rmb.irv.Rengine.eval("length(rAllfeat)")));
        System.out.println("Miner log " + (rmb.irv.Rexpr = rmb.irv.Rengine.eval("length(feature_data)")));
        System.out.println("Miner log " + (rmb.irv.Rexpr = rmb.irv.Rengine.eval("mode(feature_data)")));
        System.out.println("Miner log " + (rmb.irv.Rexpr = rmb.irv.Rengine.eval("class(feature_data)")));
        if (rmb.irv.prm.precrit.isInteractCrit || rmb.irv.prm.crit.isInteractCrit)
            System.out.println("Miner log " + "interact_data " + (rmb.irv.Rexpr = rmb.irv.Rengine.eval("length(interact_data)")));
    }

    /**
     * Check Ic and Jc min/max. . .define which MOVE_TYPES can be done to stay in limits
     * MOVE_TYPES 1-4 (0 to 3 in array indices) are
     * -g
     * +g
     * -e
     * +e
     * <p/>
     * Customizations for batch vs single moves, and LARS vs. all criteria
     *
     * @param prm
     * @param Ic
     * @param Jc
     * @return
     */
    private int[] maskMoves(Parameters prm, int[] Ic, int[] Jc, int Single_or_Batch) {
        int[] pmoves = {1, 2, 3, 4};
//System.out.println("maskMoves Ic " + Ic.length + "\tJc " + Jc.length);
//System.out.println("maskMoves I " + rmb.irv.prm.IMIN + "\t" + rmb.irv.prm.IMAX);
//System.out.println("maskMoves J " + rmb.irv.prm.JMIN + "\t" + rmb.irv.prm.JMAX);

        if (Single_or_Batch == 0) {
            if (Ic.length <= prm.IMIN || prm.FIX_GENES)
                pmoves[0] = -1;
            if (Jc.length <= prm.JMIN || prm.FIX_EXPS)
                pmoves[2] = -1;

            //prevent gene addition
            if ((Ic.length >= prm.IMAX && !prm.OVERRIDE_SHAVING) || prm.FIX_GENES)// ||
                //(Ic.length >= prm.IMAX - 2 &&
                //        MoreArray.getArrayInd(MINER_STATIC.LARSCrit, prm.CRIT_TYPE_INDEX) != -1))
                pmoves[1] = -1;
            //prevent experiment addition
            if ((Jc.length >= prm.JMAX && !prm.OVERRIDE_SHAVING) || prm.FIX_EXPS /*||
                    (Jc.length >= prm.JMAX - 2 &&
                            MoreArray.getArrayInd(MINER_STATIC.GEECrit, prm.CRIT_TYPE_INDEX) != -1)*/)
                pmoves[3] = -1;
        }
        // Don't add in batch mode unless within at most N of the max limit and don't delete unless within N of min *//*
        else {
            if (Ic.length <= prm.IMIN + prm.MIN_NONMISSING_FOR_BATCH || prm.FIX_GENES)
                pmoves[0] = -1;
            if (Jc.length <= prm.JMIN + prm.MIN_NONMISSING_FOR_BATCH || prm.FIX_EXPS)
                pmoves[2] = -1;
            if ((Ic.length >= prm.IMAX /*- 1*/ - prm.MIN_NONMISSING_FOR_BATCH & !prm.OVERRIDE_SHAVING) || prm.FIX_GENES)// ||
                //(Ic.length >= prm.IMAX - 2 - prm.MIN_NONMISSING_FOR_BATCH &&
                //        MoreArray.getArrayInd(MINER_STATIC.LARSCrit, prm.CRIT_TYPE_INDEX) != -1))
                pmoves[1] = -1;
            if ((Jc.length >= prm.JMAX /*- 1*/ - prm.MIN_NONMISSING_FOR_BATCH && !prm.OVERRIDE_SHAVING) || prm.FIX_EXPS /*||
                    (Jc.length >= prm.JMAX - 2 - prm.MIN_NONMISSING_FOR_BATCH &&
                            MoreArray.getArrayInd(MINER_STATIC.GEECrit, rmb.irv.prm.CRIT_TYPE_INDEX) != -1)*/)
                pmoves[3] = -1;
        }

        if (debug) {
            System.out.println("maskMoves " + prm.OVERRIDE_SHAVING + "\t" + Ic.length + "\t" + prm.IMAX + "\t" +
                    Jc.length + "\t" + prm.JMAX + "\t" + MoreArray.toString(pmoves, ","));
        }
        //util.MoreArray.printArray(pmoves);
        return pmoves;
    }

    /**
     * Calculates intitial quick criteria, loops over the list of block ids (PMoveStr)
     * and calculates preCriteria.
     *
     * @return preCrit top list as fullCrit ValueBlocks
     */
    public ValueBlockList getTopList() {
        //System.out.println("creatcomputeBlockOverlapWithRefePreCritTopList start");
        if (debug) {
            System.out.println("getTopList pMoves");
            util.MoreArray.printArray(move_params.possible_move_types);
            System.out.println("getTopList tried_moves");
            util.MoreArray.printArray(move_params.tried_moves);

            System.out.println("getTopList move_params.pMoveStrings " + move_params.pMoveStrings.size());
            System.out.println("getTopList  VBPCandidate.genesStr " + VBPCandidate.genes.length + "\t" +
                    BlockMethods.IcJctoijID(VBPCandidate.genes) +
                    "\tVBPCandidate.expsStr " + VBPCandidate.exps.length + "\t" +
                    BlockMethods.IcJctoijID(VBPCandidate.exps) + "\t" + move_params.current_move_class);
        }
        double[] pCrit = getCriteriaForBlockSingle(VBPCandidate, rmb.irv.prm.precrit);

        if (debug_createPreCritTopList) {
            System.out.println("getTopList VBPCandidate rmb.irv.prm.precrit " + rmb.irv.prm.precrit.crit);
            util.MoreArray.printArray(pCrit);
        }
        if (pCrit == null) {
            System.out.println("ERROR getTopList: pCrit is null " +
                    VBPCandidate.genes.length + "\t" + VBPCandidate.exps.length + "\t" +
                    move_params.possible_move_list[0].size() + "\t" + move_params.possible_move_list[1].size());
            System.exit(1);
        }

        int listsize = 0;
        if (rmb.irv.prm.SIZE_PRECRIT_LIST != -1) {
            listsize = rmb.irv.prm.SIZE_PRECRIT_LIST;
        } else {
            if (debug)
                System.out.println("listsize gene/exp " + listsize + "\t" +
                        VBPCandidate.move_type + "\t" + move_params.move_type);
            if (move_params.move_type.indexOf("gene") != -1) {
                listsize = (int) rmb.irv.prm.SIZE_PRECRIT_LIST_GENE;
                if (debug)
                    System.out.println("listsize gene " + listsize);
            } else if (move_params.move_type.indexOf("experiment") != -1) {
                listsize = (int) rmb.irv.prm.SIZE_PRECRIT_LIST_EXP;
                if (debug)
                    System.out.println("listsize exp " + listsize);
            }
        }
        if (debug)
            System.out.println("listsize " + listsize + "\tSIZE_PRECRIT_LIST " + rmb.irv.prm.SIZE_PRECRIT_LIST + "\t" +
                    rmb.irv.prm.SIZE_PRECRIT_LIST_GENE + "\t" + rmb.irv.prm.SIZE_PRECRIT_LIST_EXP);
        double[] curval_list = MoreArray.initArray(listsize, Double.NaN);// double[rmb.irv.prm.SIZE_PRECRIT_LIST];
        double[] expr_mean_val_list = MoreArray.initArray(listsize, Double.NaN);// double[rmb.irv.prm.SIZE_PRECRIT_LIST];
        double[] expr_mse_val_list = MoreArray.initArray(listsize, Double.NaN);// double[rmb.irv.prm.SIZE_PRECRIT_LIST];
        double[] ppi_val_list = MoreArray.initArray(listsize, Double.NaN);
        double[] feature_val_list = MoreArray.initArray(listsize, Double.NaN);
        if (rmb.irv.prm.precrit.isInteractCrit || rmb.irv.prm.crit.isInteractCrit)
            ppi_val_list = MoreArray.initArray(listsize, Double.NaN);// double[rmb.irv.prm.SIZE_PRECRIT_LIST];
        if (rmb.irv.prm.precrit.isFeatureCrit || rmb.irv.prm.crit.isFeatureCrit)
            feature_val_list = MoreArray.initArray(listsize, Double.NaN);
        double[] expr_reg_val_list = MoreArray.initArray(listsize, Double.NaN);
        double[] expr_ken_val_list = MoreArray.initArray(listsize, Double.NaN);
        double[] expr_COR_val_list = MoreArray.initArray(listsize, Double.NaN);
        double[] expr_euc_val_list = MoreArray.initArray(listsize, Double.NaN);
        double[] expr_spear_val_list = MoreArray.initArray(listsize, Double.NaN);
        String[] curstring_list = new String[listsize];
        double[] curmaxTF_list = MoreArray.initArray(listsize, Double.NaN);
        if (debug)
            System.out.println("move_type  " + VBPCandidate.move_type + "\tlistsize " + listsize);

        if (debug_createPreCritTopList) {
            System.out.println("pCrit " + pCrit);
        }
        double fullcrit = ValueBlockPre.computeFullCrit(pCrit, true, rmb.irv.prm.precrit.which_expr_crits, debug_createPreCritTopList);
        if (debug)
            System.out.println("getTopList VBPCandidate fullcrit " + fullcrit);

        double cur_min = fullcrit;
        double cur_max = fullcrit;

        int size = move_params.pMoveStrings.size();
        int added = 0;
        if (debug)
            System.out.println("getTopList move_params.pMoveStrings " + size);

        if (debug) {
            System.out.println("getting top " + listsize + " of " + size + " precrit moves of class " +
                    move_params.current_move_class + " and type " + move_params.move_type);
        }
        for (int i = 0; i < size; i++) {
//            if (i % 100 == 0)
//                System.out.print(".");
            String curId = (String) move_params.pMoveStrings.get(i);
            boolean b = inTrajectory(curId);
            if (b) {
                if (debug) {
                    System.out.println("getTopList inTrajectory " + b + "\t" + curId);
                }
            } else if (!b) {
                if (debug)
                    System.out.print(".");
                int[][] get = BlockMethods.ijIDtoIcJc(curId);

                int[] curIc = util.MoreArray.removeWhereEqual(get[0], util.MoreArray.initArray(get[0].length, 0));
                int[] curJc = util.MoreArray.removeWhereEqual(get[1], util.MoreArray.initArray(get[1].length, 0));

                String s1 = "Ic<-c(" + MoreArray.toString(curIc, ",") + ")";
                if (debug_createPreCritTopList) {
                    System.out.println("R: " + s1);
                }
                rmb.irv.Rengine.assign("Ic", curIc);
                String s2 = "Jc<-c(" + MoreArray.toString(curJc, ",") + ")";
                if (debug_createPreCritTopList) {
                    System.out.println("R: " + s2);
                }
                rmb.irv.Rengine.assign("Jc", curJc);

                /*TODO : This loops over the rows of preCriteria (rows=no.Ids) and sums them. Should allow a weighted sum */
                double[] cur_pCritDouble = getCriteriaForBlock(curIc, curJc, rmb.irv.prm.precrit)[0];
                if (debug_createPreCritTopList) {
                    System.out.println("getTopList VBPCandidate rmb.irv.prm.precrit " + rmb.irv.prm.precrit.crit);
                    util.MoreArray.printArray(cur_pCritDouble);
                }
                if (cur_pCritDouble == null) {
                    System.out.println("ERROR getTopList: pCrit is null " + curIc.length + "\t" +
                            curJc.length + "\t" + move_params.possible_move_list);
                    System.exit(1);
                }

                double curval = ValueBlockPre.computeFullCrit(cur_pCritDouble, true, rmb.irv.prm.crit.which_expr_crits,
                        debug_createPreCritTopList);
                if (debug_createPreCritTopList) {
                    System.out.println("getTopList cur_pCritDouble crit " + curval + "\t" + cur_max);
                    MoreArray.printArray(cur_pCritDouble);
                }

                /*TODO test for ties */
                //System.out.println("curval_list " + curval_list.length);
                if (added < listsize) {
                    if (curval > cur_max)
                        cur_max = curval;
                    if (curval < cur_min)
                        cur_min = curval;
                    int posPre = mathy.stat.findMinPosGreaterThanEqualReverse(curval_list, curval); //findMinPosGreaterThanEqualReverse //findMinPosGreaterThanReverse
                    if (posPre != -1) {
                        curval_list = MINER_STATIC.shift(curval_list, curval, posPre);
                        expr_mean_val_list = MINER_STATIC.shift(expr_mean_val_list, cur_pCritDouble[0], posPre);
                        if (rmb.irv.prm.precrit.expr_mse_axis != -1)
                            expr_mse_val_list = MINER_STATIC.shift(expr_mse_val_list, cur_pCritDouble[1], posPre);
                        if (rmb.irv.prm.precrit.expr_reg_axis != -1)
                            expr_reg_val_list = MINER_STATIC.shift(expr_reg_val_list, cur_pCritDouble[2], posPre);
                        if (rmb.irv.prm.precrit.KENDALLIndex != -1)
                            expr_ken_val_list = MINER_STATIC.shift(expr_ken_val_list, cur_pCritDouble[3], posPre);
                        if (rmb.irv.prm.precrit.CORIndex != -1)
                            expr_COR_val_list = MINER_STATIC.shift(expr_COR_val_list, cur_pCritDouble[4], posPre);
                        if (rmb.irv.prm.precrit.EUCIndex != -1)
                            expr_euc_val_list = MINER_STATIC.shift(expr_euc_val_list, cur_pCritDouble[5], posPre);
                        if (rmb.irv.prm.precrit.SPEARIndex != -1)
                            expr_spear_val_list = MINER_STATIC.shift(expr_spear_val_list, cur_pCritDouble[6], posPre);
                        if (rmb.irv.prm.precrit.isInteractCrit)
                            ppi_val_list = MINER_STATIC.shift(ppi_val_list, cur_pCritDouble[7], posPre);
                        if (rmb.irv.prm.precrit.isFeatureCrit)
                            feature_val_list = MINER_STATIC.shift(feature_val_list, cur_pCritDouble[8], posPre);
                        if (rmb.irv.TFtargetmap != null)
                            curmaxTF_list = MINER_STATIC.shift(curmaxTF_list, cur_pCritDouble[9], posPre);
                        curstring_list = MINER_STATIC.shift(curstring_list, curId, posPre);

                        added++;
                    } else {
                        curval_list = MINER_STATIC.shift(curval_list, curval, added);
                        expr_mean_val_list = MINER_STATIC.shift(expr_mean_val_list, cur_pCritDouble[0], posPre);
                        if (rmb.irv.prm.precrit.expr_mse_axis != -1)
                            expr_mse_val_list = MINER_STATIC.shift(expr_mse_val_list, cur_pCritDouble[1], posPre);
                        if (rmb.irv.prm.precrit.expr_reg_axis != -1)
                            expr_reg_val_list = MINER_STATIC.shift(expr_reg_val_list, cur_pCritDouble[2], posPre);
                        if (rmb.irv.prm.precrit.KENDALLIndex != -1)
                            expr_ken_val_list = MINER_STATIC.shift(expr_ken_val_list, cur_pCritDouble[3], posPre);
                        if (rmb.irv.prm.precrit.CORIndex != -1)
                            expr_COR_val_list = MINER_STATIC.shift(expr_COR_val_list, cur_pCritDouble[4], posPre);
                        if (rmb.irv.prm.precrit.EUCIndex != -1)
                            expr_euc_val_list = MINER_STATIC.shift(expr_euc_val_list, cur_pCritDouble[5], posPre);
                        if (rmb.irv.prm.precrit.SPEARIndex != -1)
                            expr_spear_val_list = MINER_STATIC.shift(expr_spear_val_list, cur_pCritDouble[6], posPre);
                        if (rmb.irv.prm.precrit.isInteractCrit)
                            ppi_val_list = MINER_STATIC.shift(ppi_val_list, cur_pCritDouble[7], posPre);
                        if (rmb.irv.prm.precrit.isFeatureCrit)
                            feature_val_list = MINER_STATIC.shift(feature_val_list, cur_pCritDouble[8], posPre);
                        if (rmb.irv.TFtargetmap != null)
                            curmaxTF_list = MINER_STATIC.shift(curmaxTF_list, cur_pCritDouble[9], posPre);

                        curstring_list = MINER_STATIC.shift(curstring_list, curId, posPre);
                        added++;
                    }
                }
                //should maximize 1 - e-value
                else if (curval > cur_min) {
                    if (curval > cur_max) {
                        /*if (debug_createPreCritTopList) {
                            System.out.println("accepting new max " + curval + "\t" + cur_max);
                        }*/
                        cur_max = curval;
                    }

                    int posPre = mathy.stat.findMinPosGreaterThanEqualReverse(curval_list, curval);
                    if (posPre != -1) {
                        curval_list = MINER_STATIC.shift(curval_list, curval, posPre);
                        expr_mean_val_list = MINER_STATIC.shift(expr_mean_val_list, cur_pCritDouble[0], posPre);
                        if (rmb.irv.prm.precrit.expr_mse_axis != -1)
                            expr_mse_val_list = MINER_STATIC.shift(expr_mse_val_list, cur_pCritDouble[1], posPre);
                        if (rmb.irv.prm.precrit.expr_reg_axis != -1)
                            expr_reg_val_list = MINER_STATIC.shift(expr_reg_val_list, cur_pCritDouble[2], posPre);
                        if (rmb.irv.prm.precrit.KENDALLIndex != -1)
                            expr_ken_val_list = MINER_STATIC.shift(expr_ken_val_list, cur_pCritDouble[3], posPre);
                        if (rmb.irv.prm.precrit.CORIndex != -1)
                            expr_COR_val_list = MINER_STATIC.shift(expr_COR_val_list, cur_pCritDouble[4], posPre);
                        if (rmb.irv.prm.precrit.EUCIndex != -1)
                            expr_euc_val_list = MINER_STATIC.shift(expr_euc_val_list, cur_pCritDouble[5], posPre);
                        if (rmb.irv.prm.precrit.SPEARIndex != -1)
                            expr_spear_val_list = MINER_STATIC.shift(expr_spear_val_list, cur_pCritDouble[6], posPre);
                        if (rmb.irv.prm.precrit.isInteractCrit)
                            ppi_val_list = MINER_STATIC.shift(ppi_val_list, cur_pCritDouble[7], posPre);
                        if (rmb.irv.prm.precrit.isFeatureCrit)
                            feature_val_list = MINER_STATIC.shift(feature_val_list, cur_pCritDouble[8], posPre);
                        if (rmb.irv.TFtargetmap != null)
                            curmaxTF_list = MINER_STATIC.shift(curmaxTF_list, cur_pCritDouble[9], posPre);
                        curstring_list = MINER_STATIC.shift(curstring_list, curId, posPre);
                        added++;
                    }
                    if (curval < curval_list[curval_list.length - 2]) {
                        cur_min = curval;
                    }

                }
                //}//if does not degrade current minTFfraction
            }
        }
        if (debug) {
            System.out.print("\n");
            System.out.println("\ngot top " + size + " precrit moves of class " +
                    move_params.current_move_class + " and type " + move_params.move_type);
        }

        ValueBlockList vbl = new ValueBlockList();
        ArrayList id = new ArrayList();
        for (int i = 0; i < curval_list.length; i++) {
            //System.out.println("getTopList "+i);
            //System.out.println("getTopList "+i+"\t"+curval_list[i]);
            //System.out.println("getTopList "+i+"\t"+curstring_list[i]);
            if (!Double.isNaN(curval_list[i])) {
                ValueBlockPre vbnow = new ValueBlockPre(curval_list[i], BlockMethods.ijIDtoIcJc(curstring_list[i]),
                        util.MoreArray.getArrayInd(MINER_STATIC.MOVE_TYPES, move_params.move_type),
                        move_params.current_move_class);
                vbnow.all_precriteria = new double[ValueBlock_STATIC.NUM_CRIT];
                vbnow.all_precriteria[ValueBlock_STATIC.expr_MEAN_IND] = expr_mean_val_list[i];
                if (rmb.irv.prm.precrit.expr_mse_axis != -1)
                    vbnow.all_precriteria[ValueBlock_STATIC.expr_MSE_IND] = expr_mse_val_list[i];
                if (rmb.irv.prm.precrit.expr_reg_axis != -1)
                    vbnow.all_precriteria[ValueBlock_STATIC.expr_FEM_IND] = expr_reg_val_list[i];
                if (rmb.irv.prm.precrit.KENDALLIndex != -1)
                    vbnow.all_precriteria[ValueBlock_STATIC.expr_KEND_IND] = expr_ken_val_list[i];
                if (rmb.irv.prm.precrit.CORIndex != -1)
                    vbnow.all_precriteria[ValueBlock_STATIC.expr_COR_IND] = expr_COR_val_list[i];
                if (rmb.irv.prm.precrit.EUCIndex != -1)
                    vbnow.all_precriteria[ValueBlock_STATIC.expr_EUC_IND] = expr_euc_val_list[i];
                if (rmb.irv.prm.precrit.SPEARIndex != -1)
                    vbnow.all_precriteria[ValueBlock_STATIC.expr_SPEARMAN_IND] = expr_spear_val_list[i];
                if (rmb.irv.prm.precrit.isInteractCrit)
                    vbnow.all_precriteria[ValueBlock_STATIC.interact_IND] = ppi_val_list[i];
                if (rmb.irv.prm.precrit.isFeatureCrit)
                    vbnow.all_precriteria[ValueBlock_STATIC.feat_IND] = feature_val_list[i];
                //if (rmb.irv.TFtargetmap != null)
                if (rmb.irv.prm.precrit.isTFCrit)
                    vbnow.all_precriteria[ValueBlock_STATIC.TF_IND] = curmaxTF_list[i];

                if(debug_createPreCritTopList) {
                    System.out.println("getTopList precrit vbnow.all_precriteria");
                    System.out.println(rmb.irv.prm.crit.isInteractCrit);
                    System.out.println(rmb.irv.prm.precrit.isInteractCrit);
                    System.out.println(ppi_val_list[i]);
                    System.out.println(feature_val_list[i]);
                    System.out.println(curmaxTF_list[i]);
                    MoreArray.printArray(vbnow.all_precriteria);
                }

                if (vbnow.genes == null)
                    System.out.println("vbnow.genes is null :" + curstring_list[i] + ":");
                //System.out.println("vbnow " + MoreArray.toString(vbnow.genes, ","));
                vbl.add(vbnow);
                id.add(curstring_list[i]);
            }
        }

        for (int i = 0; i < vbl.size(); i++) {
            ValueBlockPre vb = (ValueBlockPre) vbl.get(i);
            String s = (String) id.get(i);
            if (vb == null)
                System.out.println("getTopList vb is null " + s);
            if (vb.genes == null)
                System.out.println("getTopList vb.genes is null " + s);
        }

        if (debug) {
            System.out.println("getTopList Move list after pre-criteria " + vbl.size() +
                    "\tcur_min " + cur_min + "\tcur_max " + cur_max);
            System.out.println("createPreCritTopListfinal criteria stage");
        }

        //if (rmb.irv.prm.PRECRIT_TYPE_INDEX != rmb.orig_rmb.irv.prm.CRIT_TYPE_INDEX) {
        vbl = runFullCriteria(vbl);
        if (debug)
            System.out.println("getTopList end, number of top moves: " + vbl.size());
        return vbl;
        /*}
        ValueBlockList out = new ValueBlockList();
        for (int i = 0; i < vbl.size(); i++) {
            ValueBlock add = new ValueBlock((ValueBlockPre) vbl.get(i));
            add.updateAllCriteria(add.all_precriteria);
            add.full_criterion = add.pre_criterion;
            out.add(add);
        }

        if (debug)
            System.out.println("getTopList end, number of top moves: " + out.size());
        return out;*/
    }

    /**
     * Runs the full criteria on the specified list.
     *
     * @param vbl
     */
    private ValueBlockList runFullCriteria(ValueBlockList vbl) {

        testContext(vbl);

        for (int i = 0; i < vbl.size(); i++) {
            ValueBlockPre vb = (ValueBlockPre) vbl.get(i);

            ValueBlock vbnow = new ValueBlock(vb);
            /*if (debug_createPreCritTopList) {
                //System.out.println("runFullCriteria vb " + BlockMethods.IcJctoijID(vb.genes, vb.exps));
                //System.out.println("runFullCriteria vbnew " + BlockMethods.IcJctoijID(vb.genes, vb.exps));
                System.out.println("vb.all_precriteria");
                MoreArray.printArray(vb.all_precriteria);
                System.out.println("vbnow.all_precriteria");
                MoreArray.printArray(vbnow.all_precriteria);
                MoreArray.printArray(vbnow.all_criteria);
            }*/
            //System.out.println("runFullCriteria " + rmb.irv.prm.PRECRIT_TYPE_INDEX + "\t" + rmb.orig_rmb.irv.prm.CRIT_TYPE_INDEX);
            //if (rmb.irv.prm.PRECRIT_TYPE_INDEX != rmb.orig_rmb.irv.prm.CRIT_TYPE_INDEX) {
            if (debug && i % 10 == 0)
                System.out.print(".");
            rmb.irv.Rengine.assign("Ic", vbnow.genes);
            rmb.irv.Rengine.assign("Jc", vbnow.exps);
            getFinalCriteriaForBlock(vbnow);
            if (debug_createPreCritTopList) {
                System.out.println("runFullCriteria a/f " + BlockMethods.IcJctoijID(vb.genes, vb.exps));
                MoreArray.printArray(vbnow.all_precriteria);
                System.out.println("all");
                MoreArray.printArray(vbnow.all_criteria);
            }

            /* } else {
                boolean[] passcrits = Criterion.getExprCritTypes(rmb.irv.prm.crit, rmb.irv.prm.WEIGH_EXPR,
                        rmb.irv.prm.crit.isMeanCrit ? true : rmb.irv.prm.USE_MEAN, debug_getCriteriaForBlock);
                System.out.println("runFullCriteria pre " + MoreArray.toString(vbnow.all_precriteria, ","));
                System.out.println("runFullCriteria     " + MoreArray.toString(vbnow.all_criteria, ","));
                vbnow.updateCrit(vbnow.all_precriteria, rmb.irv.prm.WEIGH_EXPR, passcrits, debug_getCriteriaForBlock);
            }*/
            vbnow.trajectory_position = move_params.move_count + 1;

            updatePreList(vbnow);
            updateLists(vbnow);
            vbl.set(i, vbnow);

            //System.out.println("runFullCriteria " + vbl.size());
        }
        //Sort descending by full_criterion
        ValueBlockListMethods.sort("full", vbl);

        //truncate list and choose random move in case of ties
        int areTied = testTies(vbl);
        if (areTied > 0) {
            while (vbl.size() > areTied) {
                vbl.remove(areTied);
            }
            int val = rand.nextInt(areTied);
            if (debug)
                System.out.println("runFullCriteria areTied random " + areTied + "\t" + val);
            ValueBlock get = (ValueBlock) vbl.get(val);
            vbl = new ValueBlockList();
            vbl.add(get);
        } else {
            while (vbl.size() > 1) {
                vbl.remove(1);
            }
        }

        return vbl;
    }

    /**
     * @param vbl
     */
    private void testContext(ValueBlockList vbl) {

        for (int i = 0; i < vbl.size(); i++) {
            ValueBlockPre vb = (ValueBlockPre) vbl.get(i);

            ValueBlock vbnew = new ValueBlock(vb);
            if (debug_createPreCritTopList) {
                //System.out.println("runFullCriteria vb " + BlockMethods.IcJctoijID(vb.genes, vb.exps));
                //System.out.println("runFullCriteria vbnew " + BlockMethods.IcJctoijID(vb.genes, vb.exps));
            }
            if (debug) {
                if (vb == null)
                    System.out.println("runFullCriteria vb is null");

                if (vbnew == null)
                    System.out.println("runFullCriteria vbnew is null");
                else if (vbnew.genes == null)
                    System.out.println("runFullCriteria vbnew.genes is null");
                else if (i == 0) {
                    if (rmb.irv.onv.meanMSENull != null) {
                        if (vbnew.genes.length - rmb.irv.prm.IMIN > rmb.irv.onv.meanMSENull[0].length - 1)
                            System.out.println("meanMSENull Number of genes above limit " + rmb.irv.prm.GENE_SHAVE + "\t" +
                                    (vbnew.genes.length - rmb.irv.prm.IMIN) + "\tlimit " + (rmb.irv.onv.meanMSENull[0].length - 1));
                        if (vbnew.exps.length - rmb.irv.prm.JMIN > rmb.irv.onv.meanMSENull.length - 1)
                            System.out.println("meanMSENull Number of exps above limit " + rmb.irv.prm.EXP_SHAVE + "\t" +
                                    (vbnew.exps.length - rmb.irv.prm.JMIN) + "\tlimit " + (rmb.irv.onv.meanMSENull.length - 1));
                        if (vbnew.genes.length - rmb.irv.prm.IMIN < 0)
                            System.out.println("meanMSENull Number of genes below limit " + rmb.irv.prm.GENE_SHAVE + "\t" +
                                    (vbnew.genes.length - rmb.irv.prm.IMIN) + "\tlimit " + (rmb.irv.onv.meanMSENull[0].length - 1));
                        if (vbnew.exps.length - rmb.irv.prm.JMIN < 0)
                            System.out.println("meanMSENull Number of exps below limit " + rmb.irv.prm.EXP_SHAVE + "\t" +
                                    (vbnew.exps.length - rmb.irv.prm.JMIN) + "\tlimit " + (rmb.irv.onv.meanMSENull.length - 1));
                    }
                    if (rmb.irv.onv.meanRegNull != null) {
                        if (vbnew.genes.length - rmb.irv.prm.IMIN > rmb.irv.onv.meanRegNull[0].length - 1)
                            System.out.println("meanRegNull Number of genes above limit " + rmb.irv.prm.GENE_SHAVE + "\t" +
                                    (vbnew.genes.length - rmb.irv.prm.IMIN) + "\tlimit " + (rmb.irv.onv.meanRegNull[0].length - 1));
                        if (vbnew.exps.length - rmb.irv.prm.JMIN > rmb.irv.onv.meanRegNull.length - 1)
                            System.out.println("meanRegNull Number of exps above limit " + rmb.irv.prm.EXP_SHAVE + "\t" +
                                    (vbnew.exps.length - rmb.irv.prm.JMIN) + "\tlimit " + (rmb.irv.onv.meanRegNull.length - 1));
                        if (vbnew.genes.length - rmb.irv.prm.IMIN < 0)
                            System.out.println("meanRegNull Number of genes below limit " + rmb.irv.prm.GENE_SHAVE + "\t" +
                                    (vbnew.genes.length - rmb.irv.prm.IMIN) + "\tlimit " + (rmb.irv.onv.meanRegNull[0].length - 1));
                        if (vbnew.exps.length - rmb.irv.prm.JMIN < 0)
                            System.out.println("meanRegNull Number of exps below limit " + rmb.irv.prm.EXP_SHAVE + "\t" +
                                    (vbnew.exps.length - rmb.irv.prm.JMIN) + "\tlimit " + (rmb.irv.onv.meanRegNull.length - 1));
                    }
                    if (rmb.irv.onv.meanMeanNull != null) {
                        if (vbnew.genes.length - rmb.irv.prm.IMIN > rmb.irv.onv.meanMeanNull[0].length - 1)
                            System.out.println("meanMeanNull Number of genes above limit " + rmb.irv.prm.GENE_SHAVE + "\t" +
                                    (vbnew.genes.length - rmb.irv.prm.IMIN) + "\tlimit " + (rmb.irv.onv.meanMeanNull[0].length - 1));
                        if (vbnew.exps.length - rmb.irv.prm.JMIN > rmb.irv.onv.meanMeanNull.length - 1)
                            System.out.println("meanMeanNull Number of exps above limit " + rmb.irv.prm.EXP_SHAVE + "\t" +
                                    (vbnew.exps.length - rmb.irv.prm.JMIN) + "\tlimit " + (rmb.irv.onv.meanMeanNull.length - 1));
                        if (vbnew.genes.length - rmb.irv.prm.IMIN < 0)
                            System.out.println("meanMeanNull Number of genes below limit " + rmb.irv.prm.GENE_SHAVE + "\t" +
                                    (vbnew.genes.length - rmb.irv.prm.IMIN) + "\tlimit " + (rmb.irv.onv.meanMeanNull[0].length - 1));
                        if (vbnew.exps.length - rmb.irv.prm.JMIN < 0)
                            System.out.println("meanMeanNull Number of exps below limit " + rmb.irv.prm.EXP_SHAVE + "\t" +
                                    (vbnew.exps.length - rmb.irv.prm.JMIN) + "\tlimit " + (rmb.irv.onv.meanMeanNull.length - 1));
                    }
                    if (rmb.irv.onv.meanSpearNull != null) {
                        if (vbnew.genes.length - rmb.irv.prm.IMIN > rmb.irv.onv.meanSpearNull[0].length - 1)
                            System.out.println("meanSpearNull Number of genes above limit " + rmb.irv.prm.GENE_SHAVE + "\t" +
                                    (vbnew.genes.length - rmb.irv.prm.IMIN) + "\tlimit " + (rmb.irv.onv.meanSpearNull[0].length - 1));
                        if (vbnew.exps.length - rmb.irv.prm.JMIN > rmb.irv.onv.meanSpearNull.length - 1)
                            System.out.println("meanSpearNull Number of exps above limit " + rmb.irv.prm.EXP_SHAVE + "\t" +
                                    (vbnew.exps.length - rmb.irv.prm.JMIN) + "\tlimit " + (rmb.irv.onv.meanSpearNull.length - 1));
                        if (vbnew.genes.length - rmb.irv.prm.IMIN < 0)
                            System.out.println("meanSpearNull Number of genes below limit " + rmb.irv.prm.GENE_SHAVE + "\t" +
                                    (vbnew.genes.length - rmb.irv.prm.IMIN) + "\tlimit " + (rmb.irv.onv.meanSpearNull[0].length - 1));
                        if (vbnew.exps.length - rmb.irv.prm.JMIN < 0)
                            System.out.println("meanSpearNull Number of exps below limit " + rmb.irv.prm.EXP_SHAVE + "\t" +
                                    (vbnew.exps.length - rmb.irv.prm.JMIN) + "\tlimit " + (rmb.irv.onv.meanSpearNull.length - 1));
                    }
                    if (rmb.irv.onv.meanKendNull != null) {
                        if (vbnew.genes.length - rmb.irv.prm.IMIN > rmb.irv.onv.meanKendNull[0].length - 1)
                            System.out.println("meanMeanNull Number of genes above limit " + rmb.irv.prm.GENE_SHAVE + "\t" +
                                    (vbnew.genes.length - rmb.irv.prm.IMIN) + "\tlimit " + (rmb.irv.onv.meanKendNull[0].length - 1));
                        if (vbnew.exps.length - rmb.irv.prm.JMIN > rmb.irv.onv.meanKendNull.length - 1)
                            System.out.println("meanMeanNull Number of exps above limit " + rmb.irv.prm.EXP_SHAVE + "\t" +
                                    (vbnew.exps.length - rmb.irv.prm.JMIN) + "\tlimit " + (rmb.irv.onv.meanKendNull.length - 1));
                        if (vbnew.genes.length - rmb.irv.prm.IMIN < 0)
                            System.out.println("meanMeanNull Number of genes below limit " + rmb.irv.prm.GENE_SHAVE + "\t" +
                                    (vbnew.genes.length - rmb.irv.prm.IMIN) + "\tlimit " + (rmb.irv.onv.meanKendNull[0].length - 1));
                        if (vbnew.exps.length - rmb.irv.prm.JMIN < 0)
                            System.out.println("meanMeanNull Number of exps below limit " + rmb.irv.prm.EXP_SHAVE + "\t" +
                                    (vbnew.exps.length - rmb.irv.prm.JMIN) + "\tlimit " + (rmb.irv.onv.meanKendNull.length - 1));
                    }
                }
                if (vbnew.genes == null)
                    System.out.println("runFullCriteria vbnew.genes is null");
                if (rmb == null)
                    System.out.println("runFullCriteria rmb is null");
                if (rmb.irv.prm == null)
                    System.out.println("runFullCriteria rmb.prm is null");
                if (rmb.irv == null)
                    System.out.println("runFullCriteria rmb.irv is null");
                if (rmb.irv.onv == null)
                    System.out.println("runFullCriteria rmb.irv.onv is null");
            }

        }
    }

    /**
     * @param vbl
     * @return
     */
    private int testTies(ValueBlockList vbl) {
        int deep = 0;
        double value = Double.NaN;
        for (int i = 0; i < vbl.size(); i++) {
            ValueBlock b = (ValueBlock) vbl.get(i);
            if (!Double.isNaN(value)) {
                value = b.full_criterion;
            } else if (b.full_criterion == value) {
                deep++;
            } else if (b.full_criterion != value) {
                break;
            }
        }
        return deep;
    }

    /**
     * Tests the possible moves for maximum criteria and updates the output lists.
     *
     * @param currentBlock
     * @return
     */
    public ArrayList moveProper(ValueBlock currentBlock) {
        ArrayList ret = null;
//System.out.println("moveProper start");
        double currentFullCrit = currentBlock.full_criterion;
        double orig_current_Value = currentBlock.full_criterion;
        int max_consideredIndex = -1;
        double suboptimal_consideredValue = Double.NaN;
        int suboptimal_consideredIndex = -1;
        ValueBlock suboptimal_block = null;
        if (debug)
            System.out.println("moveProper starting value " + currentFullCrit);
        //int max_currentInd = util.MoreArray.getArrayInd(currentBlock.all_criteria, max_currentFullCrit

        ValueBlockList curlist = null;
        curlist = getTopList();

        if (curlist.size() > 0) {
            if (debug) {
                System.out.println("Move list of size " + curlist.size() + "\t" + move_params.move_type);
                //System.out.println("moveProper WARNING: only first criterion and sum used for updating lists");
                System.out.println("moveProper " + curlist.size());
            }

            //only consider max, if not better quit
            //for (int i = 0; i < curlist.size(); i++) {
            int i = 0;
            ValueBlock curconsideredBlock = (ValueBlock) curlist.get(i);
            //shaving mode finds best suboptimal move
            if (Double.isNaN(suboptimal_consideredValue) || suboptimal_consideredValue <= curconsideredBlock.full_criterion) {
                suboptimal_consideredIndex = i;
                suboptimal_consideredValue = curconsideredBlock.full_criterion;
                suboptimal_block = curconsideredBlock;
            }
            /*Test if last available move: if not better and no more MOVE_TYPES
        stop algorithm and output max*/
            if (curconsideredBlock.full_criterion < currentFullCrit
                    &&
                    mathy.stat.countOccurence(move_params.tried_moves, 0) == 0) {//i == curlist.size() - 1 &&
                if (debug)
                    System.out.println("moveProper triggered stop tried_moves " + currentFullCrit + "\t" + curconsideredBlock.full_criterion);
                util.MoreArray.printArray(move_params.tried_moves);
                move_params.stop = true;
            }
            //If better or as good
            else if ((curconsideredBlock.full_criterion > currentFullCrit && move_params.Delete_or_Add == 0)
                    || (curconsideredBlock.full_criterion >= currentFullCrit && move_params.Delete_or_Add == 1)) {
                //curconsideredBlock.full_criterion > currentFullCrit) {
                max_consideredIndex = i;
                currentFullCrit = curconsideredBlock.full_criterion;
                int[][] pass = {curconsideredBlock.genes, curconsideredBlock.exps};
                if (debug)
                    System.out.println("moveProper new max " + BlockMethods.IcJctoijID(
                            pass) + "\t" + curconsideredBlock.full_criterion + "\t" + max_consideredIndex);
            }
            //}

            boolean suboptimal = false;
            String text = "shaving";
            /*if (rmb.irv.prm.PLATEAU) {
                text = "PLATEAU";
            }*/
//force best suboptimal move for shaving
            if (max_consideredIndex == -1 && suboptimal_block != null && ((move_params.gene_shaving && move_params.move_type.equals("gene-"))
                    || (move_params.exp_shaving && move_params.move_type.equals("experiment-"))
            )) {//|| (rmb.irv.prm.PLATEAU && suboptimal_consideredValue >= orig_current_Value * rmb.irv.prm.NOISE_LEVEL)
                //&& !move_params.donePlateau
                SetFromSuboptimal setFromSuboptimal = new SetFromSuboptimal(suboptimal_consideredValue,
                        suboptimal_consideredIndex, suboptimal_block, text).invoke();
                suboptimal = setFromSuboptimal.isSuboptimal();
                max_consideredIndex = setFromSuboptimal.getMax_consideredIndex();
                //move_params.donePlateau = true;
                /*Case for adding a tie if no better move */
            } else if (max_consideredIndex == -1 && move_params.Delete_or_Add == 1) {
                SetFromSuboptimal setFromSuboptimal = new SetFromSuboptimal(suboptimal_consideredValue,
                        suboptimal_consideredIndex, suboptimal_block, text).invoke();
                suboptimal = setFromSuboptimal.isSuboptimal();
                int testindex = setFromSuboptimal.getMax_consideredIndex();

                ValueBlock consideredBlock = (ValueBlock) curlist.get(testindex);

                if (consideredBlock.full_criterion == currentFullCrit) {
                    max_consideredIndex = testindex;
                }
            }
            //move_params.tried_moves = util.MoreArray.initArray(move_params.tried_moves, 1);

            if (max_consideredIndex != -1) {
                ret = new ArrayList();
//Since there is now a new optimal block set all MOVE_TYPES to 'tried' so algorithm doesnt look for others

                ValueBlock consideredBlock = (ValueBlock) curlist.get(max_consideredIndex);
                int[][] coords = {consideredBlock.genes, consideredBlock.exps};
                ret.add(coords);
                ret.add(new Double(consideredBlock.full_criterion));
                ret.add(consideredBlock.all_criteria);
                double curCriterion = ((Double) ret.get(1)).doubleValue();
                if (debug) {
                    System.out.println("moveProper Candidate move " + curCriterion + "\tcurrent value " + VBPCandidate.pre_criterion);
                    if (!suboptimal)
                        System.out.println("moveProper Candidate move is optimal " + curCriterion);
                    else {
                        System.out.println("moveProper Candidate move is suboptimal and forced by " + text + "\t" + curCriterion);
                    }
                }
                if (rmb.irv.TFtargetmap != null)
                    VBPCandidate.all_criteria[ValueBlock_STATIC.TF_IND] = consideredBlock.all_criteria[ValueBlock_STATIC.TF_IND];
                VBPCandidate.updateOverlap(VBPInitialSTART, VBPCandidate);
                VBPCandidate.NRCoords((int[][]) ret.get(0));
                VBPCandidate.move_type = util.MoreArray.getArrayInd(MINER_STATIC.MOVE_TYPES, move_params.move_type);

                boolean use_mean_now = rmb.irv.prm.crit.isMeanCrit ? true : rmb.irv.prm.USE_MEAN;
                if (debug) {
                    System.out.println("moveProper use_mean_now " + use_mean_now);
                }
                boolean[] passcrits = Criterion.getExprCritTypes(rmb.irv.prm.crit, rmb.irv.prm.WEIGH_EXPR,
                        use_mean_now, debug_getCriteriaForBlock);
                VBPCandidate.updateCrit(consideredBlock.all_criteria, rmb.irv.prm.WEIGH_EXPR, passcrits, debug_getCriteriaForBlock);
                if (debug) {
                    System.out.println("moveProper consideredBlock.all_criteria");
                    MoreArray.printArray(consideredBlock.all_criteria);
                }
                VBPCandidate.all_criteria = consideredBlock.all_criteria;
                VBPCandidate.pre_criterion = consideredBlock.pre_criterion;

                VBPCandidate = BlockMethods.computeBlockOverlapWithRef(new ValueBlock(VBPInitial), VBPCandidate, debug);
                VBPCandidate.setDataAndMean(rmb.expr_matrix.data);
                VBPCandidate.trajectory_position = move_params.move_count + 1;
                VBPCandidate.move_class = move_params.current_move_class;
                if (debug) {
                    System.out.println("moveProper VBPCandidate.full_criterion " + VBPCandidate.full_criterion);
                    System.out.println("moveProper VBPCandidate.all_criteria");
                    MoreArray.printArray(VBPCandidate.all_criteria);
                }
                //if (!move_params.exp_shaving && !move_params.gene_shaving) {
                /* try {
                    String s = "ExtractFeatures(Ic,Jc,feature_data,Ifactor,I,J)";
                    System.out.println("R: " + s);
                    System.out.println(rmb.irv.Rexpr = Rengine.eval(s));
                    int[] features = (rmb.irv.Rexpr = Rengine.eval(s)).asIntArray();
                    VBPCandidate.setFeatures(features);
                    if (debug) {
                        System.out.println("ExtractFeatures");
                        MoreArray.printArray(features);
                    }
                } catch (Exception e) {
                    System.out.println("ExtractFeatures NO FEATURE " +
                            BlockMethods.IcJctoijID(VBPCandidate.genes, VBPCandidate.exps));
                    //e.printStackTrace();
                }*/
                //}

                if (debug)
                    System.out.println("moveProper adding to trajectory " + VBPCandidate.toString() + "\t" + trajectory.size());
                ValueBlock valueBlock = new ValueBlock(VBPCandidate);
                lastBlock = new ValueBlock(valueBlock);
                if (debug) {
                    System.out.println("moveProper redefined lastBlock");
                }

                move_params.unit_moveSeq += move_params.move_type;

                trajectory.add(valueBlock);
                trajectory_blocks.put(valueBlock.blockId(), valueBlock);

                if (debug)
                    System.out.println("***good move\ncurrent_move_class " + this.move_params.current_move_class + "\nmove_type " +
                            this.move_params.move_type + "\nmoveIndex " + this.move_params.moveIndex + "\nmove_count " +
                            this.move_params.move_count + "\ntried_moves " + MoreArray.toString(this.move_params.tried_moves, ",") +
                            "\tpossible g- " + move_params.canDelGenes() + "\tg+ " + move_params.canAddGenes() +
                            "\te- " + move_params.canDelExps() + "\te+ " + move_params.canAddExps() +
                            "\npA " + move_params.pA + "\tpG " + move_params.pG
                            + "\nunit_moveSeq " + move_params.unit_moveSeq + "\t\t\t" + trajectory.size() + "\nend good move***");

                if (trajectory.size() >= 2 && debug) {
                    ValueBlock last = (ValueBlock) trajectory.get(trajectory.size() - 2);
                    if (last.genes.length > valueBlock.genes.length)
                        System.out.println("moveProper completed deleted genes " + (last.genes.length - valueBlock.genes.length) +
                                "\tfrom " + last.genes.length + "\t" + last.exps.length);
                    else if (last.genes.length < valueBlock.genes.length)
                        System.out.println("moveProper completed added genes " + (valueBlock.genes.length - last.genes.length) +
                                "\tfrom " + last.genes.length + "\t" + last.exps.length);
                    if (last.exps.length > valueBlock.exps.length)
                        System.out.println("movePropercompleted deleted exps " + (last.exps.length - valueBlock.exps.length) +
                                "\tfrom " + last.genes.length + "\t" + last.exps.length);
                    else if (last.exps.length < valueBlock.exps.length)
                        System.out.println("moveProper completed added exps " + (valueBlock.exps.length - last.exps.length) +
                                "\tfrom " + last.genes.length + "\t" + last.exps.length);
                }

                if (rmb.mvb != null) {
                    //System.out.println("updateBlocks trajectory rmb.mv");
                    rmb.mvb.updateBlocks();
//System.out.println("updateBlocks updateSize rmb.mv.backmvc");
                }
                if (debug) {
                    //System.out.println("moveProper trajectory:\n" + trajectory.toString());
                    System.out.println("moveProper After move Ic and Jc " + BlockMethods.IcJctoijID(VBPCandidate.genes, VBPCandidate.exps));
                    System.out.println("moveProper After move VBPCandidate pre_criterion " + VBPCandidate.pre_criterion);
                    if (VBPCandidate.move_type != -1)
                        System.out.println("moveProper After move VBPCandidate move_type " + MINER_STATIC.MOVE_TYPES[VBPCandidate.move_type]);
                }
                //if (!rmb.irv.prm.OVERRIDE_SHAVING)
                setShaving();
            } else {
                if (debug)
                    System.out.println("moveProper No max move");
            }
        }
        //move_params.tried_moves[moveIndex] = 1;// = util.MoreArray.initArray(move_params.tried_moves, 1);
        return ret;
    }

    /**
     * Updates the output lists.
     *
     * @param vb
     */
    private void updateLists(ValueBlock vb) {
        /*System.out.println("updateLists");
        System.out.println("expr_top100 " + vls.expr_top100.size());
        System.out.println("features_top100 " + vls.features_top100.size());
        System.out.println("inter_top100 " + vls.inter_top100.size());
        System.out.println("full_top100 " + vls.full_top100.size());
        System.out.println(vb.toStringShort());
        MoreArray.printArray(vb.all_criteria);*/

        if (!vb.computedOverlap) {
            vb.updateOverlap(VBPInitialSTART, vb);
            vb.setDataAndMean(rmb.expr_matrix.data);
        }

        if (rmb.irv.prm.precrit.expr_mean_crit != -1 || rmb.irv.prm.crit.expr_mean_crit != -1) {
            int posPre = ValueBlockListMethods.findPosExprCrit(vls.expr_top100, vb, rmb.irv.prm.crit.which_expr_crits);
            if (posPre != -1) {
                vls.expr_top100.add(posPre, vb);
                if (posPre < vls.expr_top100.size()) {
                    vls.expr_top100.add(posPre, vb);
                    //System.out.println("updateLists expr_top100 " + posPre);
                } else if (vls.expr_top100.size() < MINER_STATIC.TOPLIST_LEN) {
                    vls.expr_top100.add(vb);
                    //System.out.println("updateLists expr_top100 " + posPre);
                }
                while (vls.expr_top100.size() > MINER_STATIC.TOPLIST_LEN) {
                    vls.expr_top100.remove(vls.expr_top100.size() - 1);
                }
            }
        }

        if (rmb.irv.prm.precrit.isInteractCrit || rmb.irv.prm.crit.isInteractCrit) {
            int posInteract = ValueBlockListMethods.findPosInteractProportion(vls.inter_top100, vb);
            if (posInteract != -1) {
                vls.inter_top100.add(posInteract, vb);
                if (posInteract < vls.inter_top100.size())
                    vls.inter_top100.add(posInteract, vb);
                else if (vls.inter_top100.size() < MINER_STATIC.TOPLIST_LEN)
                    vls.inter_top100.add(vb);
                while (vls.inter_top100.size() > MINER_STATIC.TOPLIST_LEN) {
                    vls.inter_top100.remove(vls.inter_top100.size() - 1);
                }
            }
        }

        //if (rmb.irv.prm.precrit.FULL_CRITERIA || rmb.irv.prm.crit.FULL_CRITERIA) {
        if (rmb.irv.prm.precrit.isFeatureCrit) {
            int posFeatures = ValueBlockListMethods.findPosFeatures(vls.features_top100, vb);
            if (posFeatures != -1) {
                vls.features_top100.add(posFeatures, vb);
                if (posFeatures < vls.features_top100.size())
                    vls.features_top100.add(posFeatures, vb);
                else if (vls.features_top100.size() < MINER_STATIC.TOPLIST_LEN)
                    vls.features_top100.add(vb);
                while (vls.features_top100.size() > MINER_STATIC.TOPLIST_LEN) {
                    vls.features_top100.remove(vls.features_top100.size() - 1);
                }
            }
        }

        //if (rmb.irv.prm.FULL_CRITERIA || rmb.irv.prm.isInter) {
        int posFull = ValueBlockListMethods.findPosFull(vls.full_top100, vb);
        if (posFull != -1) {
            vls.full_top100.add(posFull, vb);
            if (posFull < vls.full_top100.size() || vls.full_top100.size() == 0)
                vls.full_top100.add(posFull, vb);
            else if (vls.full_top100.size() < MINER_STATIC.TOPLIST_LEN)
                vls.full_top100.add(vb);
            while (vls.full_top100.size() > MINER_STATIC.TOPLIST_LEN) {
                vls.full_top100.remove(vls.full_top100.size() - 1);
            }
        }
        //}
    }

    /**
     * @param vb
     */
    private void updatePreList(ValueBlockPre vb) {
        //System.out.println("updatePreList");
        //System.out.println(vb.toStringShort());
        if (!vb.computedOverlap) {
            vb.updateOverlap(VBPInitialSTART, vb);
            vb.setDataAndMean(rmb.expr_matrix.data);
        }
        //System.out.println("getTopList findMinValPre expr_top100");
        int posPre = ValueBlockListMethods.findPosPre(vls.expr_top100, vb);//findPosPreDesc(vls.expr_top100, vb);
        //System.out.println("updateLists posPre " + posPre + "\t" + vb.pre_criterion);
        if (posPre != -1) {
            if (posPre < vls.expr_top100.size())
                vls.expr_top100.add(posPre, vb);
            else if (vls.full_top100.size() < MINER_STATIC.TOPLIST_LEN)
                vls.expr_top100.add(vb);
            while (vls.expr_top100.size() > MINER_STATIC.TOPLIST_LEN) {
                vls.expr_top100.remove(vls.expr_top100.size() - 1);
            }
//System.out.println("updateLists min_val_data expr_top100 " + posPre);
        }
    }

    /**
     *
     */
    public void output() {
        String header = "#" + rmb.irv.prm.param_path + "\tRuntime: " + total_time + "\n";
        System.out.println("output sizes");
//System.out.println("expr_top100 " + vls.expr_top100.size());
//System.out.println("features_top100 " + vls.features_top100.size());
//System.out.println("inter_top100 " + vls.inter_top100.size());
        System.out.println("full_top100 " + vls.full_top100.size());
        if (doVariance) {
            System.out.println("finalVarListGenes " + finalVarListGenes.size());
            System.out.println("finalVarListExp " + finalVarListExp.size());
        }
        //String blockLabel = mode + "_" + BlockMethods.IcJctoijID(VBPInitial.genes, VBPInitial.exps);
        String outprefix_from_param = "";
        if (rmb.irv.prm.OUTPREFIX != null) {
            outprefix_from_param = rmb.irv.prm.OUTPREFIX;
        }
        /*try {
            String s = rmb.irv.prm.OUTDIR + blockLabel + "_" + rmb.irv.prm.RANDOM_SEED + "_expr_top100.txt";
            System.out.println("writing " + s);
            PrintWriter pw = new PrintWriter(new FileWriter(s));
            pw.println(ValueBlock.toStringShortColumns());
            for (int k = 0; k < vls.expr_top100.size(); k++) {
                ValueBlock cur = (ValueBlock) vls.expr_top100.get(k);
                String s1 = cur.toStringShort();
                //System.out.println("writing " + s1);
                pw.println("" + (k + 1) + "\t" + s1);
            }
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String s = rmb.irv.prm.OUTDIR + blockLabel + "_" + rmb.irv.prm.RANDOM_SEED + "_features_top100.txt";
            System.out.println("writing " + s);
            PrintWriter pw = new PrintWriter(new FileWriter(s));
            pw.println(ValueBlock.toStringShortColumns());
            for (int k = 0; k < vls.features_top100.size(); k++) {
                ValueBlock cur = (ValueBlock) vls.features_top100.get(k);
                String s1 = cur.toStringShort();
                //System.out.println("writing " + s1);
                pw.println("" + (k + 1) + "\t" + s1);
            }
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String s = rmb.irv.prm.OUTDIR + blockLabel + "_" + rmb.irv.prm.RANDOM_SEED + "_interact_top100.txt";
            System.out.println("writing " + s);
            PrintWriter pw = new PrintWriter(new FileWriter(s));
            pw.println(ValueBlock.toStringShortColumns());
            for (int k = 0; k < vls.inter_top100.size(); k++) {
                ValueBlock cur = (ValueBlock) vls.inter_top100.get(k);
                String s1 = cur.toStringShort();
                //System.out.println("writing " + s1);
                pw.println("" + (k + 1) + "\t" + s1);
            }
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        /*
        rmb.irv.prm.CHECKSUM_SEED = rand.nextInt(999999999);
        String outlabel_to_compress = blockLabel + "_" + rmb.irv.prm.RANDOM_SEED + "_" + rmb.irv.prm.CHECKSUM_SEED;
        if (rmb.irv.prm.use_checksum_prefix) {
            //if(out_prefix +  "_full_top100.txt".length() >= 255) {
            byte[] bytes = outlabel_to_compress.getBytes();

            // Compute Adler-32 checksum
            Checksum checksumEngine = new Adler32();
            checksumEngine.update(bytes, 0, bytes.length);
            long checksum = checksumEngine.getValue();

            // Compute CRC-32 checksum
            checksumEngine = new CRC32();
            checksumEngine.update(bytes, 0, bytes.length);
            checksum = checksumEngine.getValue();

            // The checksum engine can be reused again for a different byte array by calling reset()
            checksumEngine.reset();

            outlabel_to_compress = "" + checksum;
        }*/
        String outlabel_combo = rmb.irv.prm.RANDOM_SEED + "_" + rmb.irv.prm.CHECKSUM_SEED;

        //if (rmb.irv.prm.FULL_CRITERIA || rmb.irv.prm.isInter) {
        try {
            String s = rmb.irv.prm.OUTDIR + outprefix_from_param + outlabel_combo + "_" + move_params.current_move_class + "_full_top100.txt";
            System.out.println("writing " + s);
            String sftop100 = vls.full_top100.toString(header + "\n" + MINER_STATIC.HEADER_VBL);//ValueBlock.toStringShortColumns());
            util.TextFile.write(sftop100, s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //}

        try {
            String s = rmb.irv.prm.OUTDIR + outprefix_from_param + outlabel_combo + "_pre_top100.txt";
            System.out.println("writing " + s);
            String sptop100 = vls.expr_top100.toString(header + "\n" + MINER_STATIC.HEADER_VBL);//ValueBlock.toStringShortColumns());
            util.TextFile.write(sptop100, s);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (doVariance) {
            try {
                String s = rmb.irv.prm.OUTDIR + outprefix_from_param + outlabel_combo + "_" + move_params.current_move_class + "_finalVarListGenes.txt";
                System.out.println("writing " + s);
                PrintWriter pw = new PrintWriter(new FileWriter(s));
                pw.println(MINER_STATIC.HEADER_VBL);//ValueBlock.toStringShortColumns());
                for (int k = 0; k < finalVarListGenes.size(); k++) {
                    ValueBlockPre cur = (ValueBlockPre) finalVarListGenes.get(k);
                    String s1 = cur.toStringShort();
//System.out.println("writing " + s1);
                    pw.println("" + (k + 1) + "\t" + s1);
                }
                pw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                String s = rmb.irv.prm.OUTDIR + outprefix_from_param + outlabel_combo + "_" + move_params.current_move_class + "_finalVarListExp.txt";
                System.out.println("writing " + s);
                PrintWriter pw = new PrintWriter(new FileWriter(s));
                pw.println(MINER_STATIC.HEADER_VBL);//ValueBlock.toStringShortColumns());
                for (int k = 0; k < finalVarListExp.size(); k++) {
                    ValueBlockPre cur = (ValueBlockPre) finalVarListExp.get(k);
                    String s1 = cur.toStringShort();
//System.out.println("writing " + s1);
                    pw.println("" + (k + 1) + "\t" + s1);
                }
                pw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /*String parampath = rmb.irv.prm.param_path;
        if (rmb.irv.prm.param_path != null && rmb.irv.prm.param_path.indexOf(workdir) != 0) {
            parampath = workdir + sysRes.file_separator + rmb.irv.prm.param_pathli;
        }*/
        /*output trajectory */
        if (trajectory.size() > 0) {
            String stoplist = trajectory.toString(header + "\n" + MINER_STATIC.HEADER_VBL);// ValueBlock.toStringShortColumns());
            String outpath = rmb.irv.prm.OUTDIR + outprefix_from_param + outlabel_combo + "_" +
                    move_params.current_move_class + "_toplist.txt";
            System.out.println("writing " + outpath);
            util.TextFile.write(stoplist, outpath);
            System.out.println("final trajectory param_path " + stoplist);
        }

        /*output graphic of block trajectory and stats*/
        if (rmb.mvb != null) {
            rmb.mvb.backmvc.writecanJPEG(rmb.irv.prm.OUTDIR + outprefix_from_param + outlabel_combo + "_" +
                    move_params.current_move_class + ".jpeg");
            //rmb.mvb.backmvc.writecanEPS(rmb.irv.prm.OUTDIR + outprefix_from_param + outlabel_combo + "_" +
            //        move_params.current_move_class + ".eps");
        }
        /* ? */
        if (VBPInitial.genes != null && VBPInitial.exps != null) {
            rmb.irv.prm.INIT_BLOCKS = new ArrayList[2];
            rmb.irv.prm.INIT_BLOCKS[0] = util.MoreArray.addElements(rmb.irv.prm.INIT_BLOCKS[0], VBPInitial.genes);
            rmb.irv.prm.INIT_BLOCKS[1] = util.MoreArray.addElements(rmb.irv.prm.INIT_BLOCKS[1], VBPInitial.exps);
        }
        /*output parameter state*/
        String outfile = rmb.irv.prm.OUTDIR + outprefix_from_param + outlabel_combo + "_" + rmb.irv.prm.move_class +
                "_parameters.txt";
        System.out.println("outputing used params " + outfile);
        rmb.irv.prm.write(outfile);
    }

    /**
     * @param s
     * @return
     */
    private boolean inTrajectory(String s) {
        /*for (int i = 0; i < trajectory.size(); i++) {
            ValueBlock cur = (ValueBlock) trajectory.get(i);
            if (cur.blockId().equals(s))
                return true;
        }*/
        ValueBlock v = (ValueBlock) trajectory_blocks.get(s);
        if (v != null)
            return true;
        return false;
    }

    /**
     *
     */
    private class SetFromSuboptimal {
        private double suboptimal_consideredValue;
        private int suboptimal_consideredIndex;
        private ValueBlock suboptimal_block;
        private String text;
        private int max_consideredIndex;
        private boolean suboptimal;

        public SetFromSuboptimal(double suboptimal_consideredValue, int suboptimal_consideredIndex, ValueBlock suboptimal_block, String text) {
            this.suboptimal_consideredValue = suboptimal_consideredValue;
            this.suboptimal_consideredIndex = suboptimal_consideredIndex;
            this.suboptimal_block = suboptimal_block;
            this.text = text;
        }

        public int getMax_consideredIndex() {
            return max_consideredIndex;
        }

        public boolean isSuboptimal() {
            return suboptimal;
        }

        public SetFromSuboptimal invoke() {
            suboptimal = true;
            max_consideredIndex = suboptimal_consideredIndex;
            double max_currentValue = suboptimal_consideredValue;
            int[][] pass = {suboptimal_block.genes, suboptimal_block.exps};
            if (debug) {
                System.out.println("moveProper second best for " + text + "\t" +
                        BlockMethods.IcJctoijID(pass) + "\t" + suboptimal_block.full_criterion);
            }
            return this;
        }
    }

}