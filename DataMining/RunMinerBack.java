package DataMining;

import dtype.SystemResource;
import mathy.SimpleMatrix;
import mathy.SimpleMatrixInt;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.RList;
import org.rosuda.JRI.RVector;
import org.rosuda.JRI.Rengine;
import util.MoreArray;
import util.TabFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

/**
 * User: marcin
 * Date: Oct 14, 2007
 * Time: 3:28:32 PM
 */
public class RunMinerBack {

    int debug = MINER_STATIC.DEFAULT_DEBUG;

    public Miner mi;
    public Parameters orig_prm;
    public MinerViewBack mvb;
    //private Rengine re;
    public InitRandVar irv;

    public SimpleMatrix expr_matrix;
    public String[] gene_labels;
    public String[] exp_labels;
    public SimpleMatrixInt feat_matrix;
    public String[] feat_labels;

    ValueBlockList exclude_vbl;
    ArrayList exclude_list;

    boolean testR = false;
    boolean first = true;

    boolean fix_seed = false;
    long fixed_random_seed;

    SystemResource sysRes;

    int walltime = 0;

    boolean run = true;


    /**
     *
     */
    public RunMinerBack() {

    }

    /**
     * @param p
     */
    public RunMinerBack(Parameters p) {
        orig_prm = p;
        if (orig_prm != null) {
            initBack();
            run();
        } else
            System.out.println("RunMinerBack orig_prm is null.");
    }

    /**
     * @param p
     * @param s
     * @param d
     */
    public RunMinerBack(Parameters p, SystemResource s, int d) {
        sysRes = s;
        debug = d;
        orig_prm = p;
        if (orig_prm != null) {
            initBack();
            run();
        } else
            System.out.println("RunMinerBack orig_prm is null.");
    }

    /**
     * @param p
     * @param s
     * @param d
     */
    public RunMinerBack(Parameters p, SystemResource s, int d, Integer wt) {
        sysRes = s;
        debug = d;
        walltime = wt;
        orig_prm = p;
        if (orig_prm != null) {
            initBack();
            run();
        } else
            System.out.println("RunMinerBack orig_prm is null.");
    }


    /**
     * @param parameterpath
     */
    public RunMinerBack(String parameterpath) {
        init(parameterpath);
        run();
    }


    /**
     * @param parameterpath
     */
    public RunMinerBack(String parameterpath, SystemResource s) {
        debug = MINER_STATIC.DEFAULT_DEBUG;
        sysRes = s;
        init(parameterpath);
        run();
    }

    /**
     * @param parameterpath
     */
    public RunMinerBack(String parameterpath, SystemResource s, Integer wt) {
        debug = MINER_STATIC.DEFAULT_DEBUG;
        sysRes = s;
        walltime = wt;
        init(parameterpath);
        run();
    }

    /**
     * @param parameterpath
     */
    public RunMinerBack(String parameterpath, SystemResource s, boolean r) {
        sysRes = s;
        run = r;
        System.out.println("RunMinerBack run? " + r);
        init(parameterpath);
        if (run)
            run();
    }

    /**
     * @param parameterpath
     */
    public RunMinerBack(String parameterpath, boolean r) {
        run = r;
        System.out.println("RunMinerBack run? " + r);
        init(parameterpath);
        if (run)
            run();
    }

    /**
     * @param parameterpath
     * @param s
     * @param d
     */
    public RunMinerBack(String parameterpath, SystemResource s, int d) {
        sysRes = s;
        debug = d;
        init(parameterpath);
        //orig_prm = new Parameters(prm);
        run();
    }

    /**
     * @param parameterpath
     * @param s
     * @param d
     */
    public RunMinerBack(String parameterpath, SystemResource s, int d, Integer wt) {
        sysRes = s;
        debug = d;
        walltime = wt;
        init(parameterpath);
        //orig_prm = new Parameters(prm);
        run();
    }

    /**
     * @param parameterpath
     * @param d
     */
    public RunMinerBack(String parameterpath, SystemResource s, int d, boolean r) {
        sysRes = s;
        debug = d;
        run = r;
        init(parameterpath);
        //orig_prm = new Parameters(prm);
        if (run)
            run();
    }

    /**
     * @param m
     */
    public RunMinerBack(MinerViewBack m) {
        mvb = m;
        orig_prm = mvb.prm;
        //orig_prm = new Parameters(mvb.prm);
        initFast();
    }


    /**
     *
     */
    public void updateParameters(String parameterpath) {
        System.out.println("loading parameters " + parameterpath);
        orig_prm.param_path = parameterpath;
        orig_prm.read(parameterpath);
        //orig_prm = new Parameters(prm);
    }

    /**
     */
    public void run() {
        System.out.println("RunMinerBack run");
        if (testR)
            testR();
        if (orig_prm.init_block_list == null) {
            //System.out.println("RunMinerBack run if");
            mi = new Miner(this);
            //mi.initR(orig_prm);
            mi.initStoreVarforRun();
            System.out.println("RunMinerBack init_block_list == null run mi.run");
            mi.run();
        } else {
            ArrayList blocks = MoreArray.copyArrayList(orig_prm.init_block_list);
            // System.out.println("RunMinerBack run else "+blocks.size());
            for (int i = 0; i < blocks.size(); i++) {
                //System.out.println("RunMinerBack run else "+i);
                mi = new Miner(this);
                //System.out.println("RunMinerBack run else "+i+" new miner");
                orig_prm.INIT_BLOCKS = (ArrayList[]) blocks.get(i);
                //mi.initR(orig_prm);
                //System.out.println("RunMinerBack run else "+i+" initR");
                mi.initStoreVarforRun();
                //System.out.println("RunMinerBack run else "+i+" initStoreVarforRun");
                System.out.println("RunMinerBack init_block_list != null run mi.run " + i);
                mi.run();
                //System.out.println("RunMinerBack run else "+i+" run");
            }
        }
    }

    /**
     *
     */
    public void runSeries(ArrayList features) {
        //add current FEATURES to global parameters
        /*if (features != null) {
            System.out.println("RunMinerBack runSeries setting FEATURES");
            prm.addFeatures(features);
        }*/

        //if there are no starting blocks
        if (orig_prm.init_block_list == null) {
            if (first) {
                System.out.println("RunMinerBack creating new Miner no initial");
                mi = new Miner(this);
            }
            initAndRun(features);
        }
        //if there are starting blocks
        else {
            ArrayList blocks = MoreArray.copyArrayList(orig_prm.init_block_list);
            for (int i = 0; i < blocks.size(); i++) {
                if (first) {
                    System.out.println("RunMinerBack creating new Miner initial");
                    mi = new Miner(this);
                }
                orig_prm.INIT_BLOCKS = (ArrayList[]) blocks.get(i);
                initAndRun(features);
            }
        }
    }

    /**
     *
     */
    private void initAndRun(ArrayList features) {
        if (first) {
            //mi.initR(orig_prm, features);
            if (fix_seed)
                fixed_random_seed = orig_prm.RANDOM_SEED;
        } else {
            irv.initRSeed();
            //prm.OUTPREFIX = prm.OUTPREFIX;
            //add current FEATURES to local parameters
            //if (features != null)
            //   mi.prm.addFeatures(features);
            if (fix_seed)
                orig_prm.RANDOM_SEED = fixed_random_seed;
        }
        mi.initStoreVarforRun();
        System.out.println("RunMinerBack initAndRun run");
        mi.run();
        if (first)
            first = false;
    }

    /**
     *
     */
    public void stop() {
        mi.stop();
    }

    /**
     *
     */
    private void setLabels() {
        String gread = orig_prm.EXPR_DATA_PATH.substring(0, orig_prm.EXPR_DATA_PATH.indexOf(".txt")) +
                "_geneids.txt";
        String eread = orig_prm.EXPR_DATA_PATH.substring(0, orig_prm.EXPR_DATA_PATH.indexOf(".txt")) +
                "_expids.txt";
        File testg = new File(gread);
        File teste = new File(eread);
        //if (testg.exists()) {
        try {
            String[][] sarray = TabFile.readtoArray(gread);
            System.out.println("setLabels g " + sarray.length + "\t" + sarray[0].length);
            int col = 2;
            String[] n = MoreArray.extractColumnStr(sarray, col);
            gene_labels = MoreArray.replaceAll(n, "\"", "");
            System.out.println("setLabels gene " + gene_labels.length + "\t" + gene_labels[0]);
        } catch (Exception e) {
            System.out.println("expecting 2 cols");
            //e.printStackTrace();
            try {
                String[][] sarray = TabFile.readtoArray(gread);
                System.out.println("setLabels g " + sarray.length + "\t" + sarray[0].length);
                int col = 1;
                String[] n = MoreArray.extractColumnStr(sarray, col);
                gene_labels = MoreArray.replaceAll(n, "\"", "");
                System.out.println("setLabels gene " + gene_labels.length + "\t" + gene_labels[0]);
            } catch (Exception e1) {
                e1.printStackTrace();
                if (orig_prm.TFTARGETMAP_PATH != null) {
                    System.out.println("setLabels no gene id labels but TF targets defined");
                    System.out.println("tried path " + gread);
                    System.exit(0);
                }
            }
        }
        //}
        //if (teste.exists()) {
        try {
            String[][] sarray = TabFile.readtoArray(eread);
            System.out.println("setLabels e " + sarray.length + "\t" + sarray[0].length);
            int col = 2;
            exp_labels = MoreArray.replaceAll(MoreArray.extractColumnStr(sarray, col), "\"", "");
            System.out.println("setLabels e " + exp_labels.length + "\t" + exp_labels[0]);
        } catch (Exception e) {
            System.out.println("expecting 2 cols");
            //e.printStackTrace();
            try {
                String[][] sarray = TabFile.readtoArray(eread);
                System.out.println("setLabels e " + sarray.length + "\t" + sarray[0].length);
                int col = 1;
                exp_labels = MoreArray.replaceAll(MoreArray.extractColumnStr(sarray, col), "\"", "");
                System.out.println("setLabels e " + exp_labels.length + "\t" + exp_labels[0]);
            } catch (Exception e1) {
                System.out.println("tried path " + eread);
                e1.printStackTrace();
            }
        }
        //}
    }


    /**
     * Methods to test the JRI Java-to-R interface.
     */
    public void testR() {
        try {
            REXP x;
            irv.Rengine.eval("data(iris)", false);
            System.out.println("testR " + (x = irv.Rengine.eval("iris")));
            // generic vectors are RVector to accomodate names
            RVector v = x.asVector();
            if (v.getNames() != null) {
                System.out.println("has names:");
                for (Enumeration e = v.getNames().elements(); e.hasMoreElements(); ) {
                    System.out.println("testR " + e.nextElement());
                }
            }
            // for compatibility with Rserve we allow casting of vectors to lists
            RList vl = x.asList();
            String[] k = vl.keys();
            if (k != null) {
                System.out.println("and once again from the list:");
                int i = 0;
                while (i < k.length) System.out.println("testR " + k[i++]);
            }

            // get boolean array
            System.out.println("testR " + (x = irv.Rengine.eval("iris[[1]]>mean(iris[[1]])")));
            // R knows about TRUE/FALSE/NA, so we cannot use boolean[] this way
            // instead, we use int[] which is more convenient (and what R uses internally anyway)
            int[] bi = x.asIntArray();
            {
                int i = 0;
                while (i < bi.length) {
                    System.out.println("testR " + (bi[i] == 0 ? "F " : (bi[i] == 1 ? "T " : "NA ")));
                    i++;
                }
                System.out.println("");
            }

            // push a boolean array
            boolean by[] = {true, false, false};
            boolean retjri = irv.Rengine.assign("bool", by);
            System.out.println("testR " + (x = irv.Rengine.eval("bool")));
            // asBool returns the first element of the array as RBool
            // (mostly useful for boolean arrays of the length 1). is should return true
            System.out.println("isTRUE? " + x.asBool().isTRUE());

            // now for a real dotted-pair list:
            System.out.println("testR " + (x = irv.Rengine.eval("pairlist(a=1,b='foo',c=1:5)")));
            RList l = x.asList();
            if (l != null) {
                int i = 0;
                String[] a = l.keys();
                System.out.println("Keys:");
                while (i < a.length) System.out.println("testR " + a[i++]);
                System.out.println("Contents:");
                i = 0;
                while (i < a.length) System.out.println("testR " + l.at(i++));
            }
            System.out.println("testR " + irv.Rengine.eval("sqrt(36)"));
        } catch (Exception e) {
            System.out.println("EX:" + e);
            e.printStackTrace();
        }
    }


    /**
     * @param start
     * @return
     */
    public ArrayList evaluateVSTrue(double[] start, double[] end) {
        ArrayList ar = new ArrayList();
        ar.add(evaluateVSTrueStart(start[0], start[1], start[2], start[3], start[4], start[5], start[6], start[7], start[8], start[9]));
        ar.add(evaluateVSTrueFinalClosest(end[0], end[1], end[2], end[3], end[4], end[5], end[6], end[7], end[8], end[9]));
        return ar;
    }

    /**
     * @param start
     * @param end
     * @param final_coords
     * @return
     */
    public ArrayList evaluateVSTrue(double[] start, double[] end, ValueBlock final_coords) {
        ArrayList ar = new ArrayList();
        ar.add(evaluateVSTrueStart(start[0], start[1], start[2], start[3], start[4], start[5], start[6], start[7], start[8], start[9]));
        ar.add(evaluateVSTrueFinalClosest(end[0], end[1], end[2], end[3], end[4], end[5], end[6], end[7], end[8], end[9]));
        ar.add(evaluateVSTrueFinalMembership(final_coords));
        ar.add(evaluateVSTrueFinalMembershipIdentity(final_coords));
        return ar;
    }

    /**
     * @param ref
     * @return
     */
    public double[] evaluateVSTrueFinal(double[] ref) {
        return evaluateVSTrueFinal(ref[0], ref[1], ref[2], ref[3], ref[4], ref[5], ref[6], ref[7], ref[8], ref[9]);
    }

    /**
     * @param ref
     * @return
     */
    public double[] evaluateVSTrueStart(double[] ref) {
        return evaluateVSTrueStart(ref[0], ref[1], ref[2], ref[3], ref[4], ref[5], ref[6], ref[7], ref[8], ref[9]);
    }

    /**
     * Method to evaluate the Miner results based on known true values.
     *
     * @param expr_mean
     * @param expr_mse
     * @param expr_reg
     * @param expr_kend
     * @param expr_cor
     * @param Ivalp
     * @param feature
     * @param minTF
     * @return
     */
    public double[] evaluateVSTrueFinal(double expr_mean, double expr_mse,
                                        double expr_reg, double expr_kend, double expr_cor,
                                        double expr_euc, double expr_spearman,
                                        double Ivalp, double feature, double minTF) {
        double[] ret = new double[10];
        ValueBlock cur = (ValueBlock) mi.trajectory.get(mi.trajectory.size() - 1);
        ret[0] = expr_mean - cur.all_criteria[ValueBlock_STATIC.expr_MEAN_IND];
        ret[1] = expr_mse - cur.all_criteria[ValueBlock_STATIC.expr_MSE_IND];
        ret[2] = expr_reg - cur.all_criteria[ValueBlock_STATIC.expr_FEM_IND];
        ret[3] = expr_kend - cur.all_criteria[ValueBlock_STATIC.expr_KEND_IND];
        ret[4] = expr_cor - cur.all_criteria[ValueBlock_STATIC.expr_COR_IND];
        ret[5] = expr_euc - cur.all_criteria[ValueBlock_STATIC.expr_EUC_IND];
        ret[6] = expr_spearman - cur.all_criteria[ValueBlock_STATIC.expr_SPEARMAN_IND];
        ret[7] = Ivalp - cur.all_criteria[ValueBlock_STATIC.interact_IND];
        ret[8] = feature - cur.all_criteria[ValueBlock_STATIC.feat_IND];
        ret[9] = minTF - cur.all_criteria[ValueBlock_STATIC.TF_IND];
        double[] obs = {cur.pre_criterion, cur.all_criteria[ValueBlock_STATIC.expr_MEAN_IND], cur.all_criteria[ValueBlock_STATIC.expr_MSE_IND],
                cur.all_criteria[ValueBlock_STATIC.expr_FEM_IND], cur.all_criteria[ValueBlock_STATIC.expr_KEND_IND],
                cur.all_criteria[ValueBlock_STATIC.expr_COR_IND], cur.all_criteria[ValueBlock_STATIC.expr_EUC_IND],
                cur.all_criteria[ValueBlock_STATIC.expr_SPEARMAN_IND], cur.all_criteria[ValueBlock_STATIC.interact_IND]};
        System.out.println("evaluateVSTrueFinal final");
        MoreArray.printArray(obs);

        return ret;
    }

    /**
     * Method to evaluate the Miner results based on known true values.
     *
     * @param expr_mean
     * @param expr_mse
     * @param expr_reg
     * @param expr_kend
     * @param expr_cor
     * @param Ivalp
     * @param feature
     * @param minTF
     * @return
     */
    public double[] evaluateVSTrueFinalMin(double expr_mean,
                                           double expr_mse,
                                           double expr_reg, double expr_kend,
                                           double expr_cor, double expr_euc, double expr_spearman,
                                           double Ivalp, double feature, double minTF) {
        double[] ret = new double[10];
        double min = Double.POSITIVE_INFINITY;
        int min_index = -1;
        int size = mi.trajectory.size();
        for (int i = 1; i < size; i++) {
            ValueBlock cur = (ValueBlock) mi.trajectory.get(i);
            if (cur.pre_criterion < min) {
                min_index = i;
                min = cur.pre_criterion;
                ret[0] = expr_mean - cur.all_criteria[ValueBlock_STATIC.expr_MEAN_IND];
                ret[1] = expr_mse - cur.all_criteria[ValueBlock_STATIC.expr_MSE_IND];
                ret[2] = expr_reg - cur.all_criteria[ValueBlock_STATIC.expr_FEM_IND];
                ret[3] = expr_kend - cur.all_criteria[ValueBlock_STATIC.expr_KEND_IND];
                ret[4] = expr_cor - cur.all_criteria[ValueBlock_STATIC.expr_COR_IND];
                ret[5] = expr_cor - cur.all_criteria[ValueBlock_STATIC.expr_EUC_IND];
                ret[6] = expr_cor - cur.all_criteria[ValueBlock_STATIC.expr_SPEARMAN_IND];
                ret[7] = Ivalp - cur.all_criteria[ValueBlock_STATIC.interact_IND];
                ret[8] = feature - cur.all_criteria[ValueBlock_STATIC.feat_IND];
                ret[9] = minTF - cur.all_criteria[ValueBlock_STATIC.TF_IND];
            }
        }
        System.out.println("evaluateVSTrueFinalMin " + min_index);
        MoreArray.printArray(ret);
        return ret;
    }

    /**
     * Method to evaluate the Miner results based on known true values.
     *
     * @param expr_mean
     * @param expr_mse
     * @param expr_reg
     * @param expr_kend
     * @param expr_cor
     * @param Ivalp
     * @param feature
     * @param minTF
     * @return
     */
    public double[] evaluateVSTrueFinalClosest(double expr_mean,
                                               double expr_mse, double expr_reg, double expr_kend,
                                               double expr_cor, double expr_euc, double expr_spearman, double Ivalp, double feature, double minTF) {
        double[] ret = new double[10];
        double min = Double.POSITIVE_INFINITY;
        int min_index = -1;
        int size = mi.trajectory.size();
        for (int i = 1; i < size; i++) {
            ValueBlock cur = (ValueBlock) mi.trajectory.get(i);
            double diff = Math.abs(cur.pre_criterion - expr_mse);
            if (diff < min) {
                min_index = i;
                min = diff;
                ret[0] = expr_mean - cur.all_criteria[ValueBlock_STATIC.expr_MEAN_IND];
                ret[1] = expr_mse - cur.all_criteria[ValueBlock_STATIC.expr_MSE_IND];
                ret[2] = expr_reg - cur.all_criteria[ValueBlock_STATIC.expr_FEM_IND];
                ret[3] = expr_kend - cur.all_criteria[ValueBlock_STATIC.expr_KEND_IND];
                ret[4] = expr_cor - cur.all_criteria[ValueBlock_STATIC.expr_COR_IND];
                ret[5] = expr_euc - cur.all_criteria[ValueBlock_STATIC.expr_EUC_IND];
                ret[6] = expr_spearman - cur.all_criteria[ValueBlock_STATIC.expr_SPEARMAN_IND];
                ret[7] = Ivalp - cur.all_criteria[ValueBlock_STATIC.interact_IND];
                ret[8] = feature - cur.all_criteria[ValueBlock_STATIC.feat_IND];
                ret[9] = minTF - cur.all_criteria[ValueBlock_STATIC.TF_IND];
            }
        }
        System.out.println("evaluateVSTrueFinalMin " + min_index);
        MoreArray.printArray(ret);
        return ret;
    }

    /**
     * @param final_block
     * @return
     */
    public double[] evaluateVSTrueFinalMembership(ValueBlock final_block) {
        double[] ret = new double[2];
        double max = Double.NEGATIVE_INFINITY;
        int maxindex = -1;
        int size = mi.trajectory.size();
        for (int i = 1; i < size; i++) {
            ValueBlock cur = (ValueBlock) mi.trajectory.get(i);
            cur.updateOverlap(final_block, cur);
            double mean_percsim = (cur.percentOrigExp + cur.percentOrigGenes) / 2.0;
            //System.out.println("evaluateVSTrueFinalMembership " + i + "\t" + cur.percentOrigExp + "\t" + cur.percentOrigGenes + "\t" + mean_percsim);
            if (mean_percsim > max) {
                max = mean_percsim;
                maxindex = i;
            }
        }
        ret[0] = max;
        ret[1] = maxindex;
        //System.out.println("evaluateVSTrueFinalMembership " + maxindex);
        //MoreArray.printArray(ret);
        return ret;
    }

    /**
     * @param final_block
     * @return
     */
    public double[] evaluateVSTrueFinalMembershipIdentity(ValueBlock final_block) {
        double[] ret = new double[2];
        int maxindex = -1;
        int size = mi.trajectory.size();
        for (int i = 1; i < size; i++) {
            ValueBlock cur = (ValueBlock) mi.trajectory.get(i);
            cur.updateOverlap(final_block, cur);
            if (cur.genes == final_block.genes && cur.exps == final_block.exps) {//.equals(final_block.block_id)) {
                maxindex = i;
                break;
            }
        }
        ret[0] = maxindex;
        //System.out.println("evaluateVSTrueFinalMembership " + maxindex);
        //MoreArray.printArray(ret);
        return ret;
    }

    /**
     * Method to evaluate the Miner results based on known true values.
     *
     * @param expr_mean
     * @param expr_mse
     * @param expr_reg
     * @param expr_kend
     * @param expr_cor
     * @param Ivalp
     * @param feature
     * @param minTF
     * @return
     */
    public double[] evaluateVSTrueStart(double expr_mean,
                                        double expr_mse, double expr_reg, double expr_kend,
                                        double expr_cor, double expr_euc, double expr_spearman,
                                        double Ivalp, double feature, double minTF) {
        double[] ret = new double[10];
        ValueBlock cur = (ValueBlock) mi.trajectory.get(0);
        ret[0] = expr_mean - cur.all_criteria[ValueBlock_STATIC.expr_MEAN_IND];
        ret[1] = expr_mse - cur.all_criteria[ValueBlock_STATIC.expr_MSE_IND];
        ret[2] = expr_reg - cur.all_criteria[ValueBlock_STATIC.expr_FEM_IND];
        ret[3] = expr_kend - cur.all_criteria[ValueBlock_STATIC.expr_KEND_IND];
        ret[4] = expr_cor - cur.all_criteria[ValueBlock_STATIC.expr_COR_IND];
        ret[5] = expr_euc - cur.all_criteria[ValueBlock_STATIC.expr_EUC_IND];
        ret[6] = expr_spearman - cur.all_criteria[ValueBlock_STATIC.expr_SPEARMAN_IND];
        ret[7] = Ivalp - cur.all_criteria[ValueBlock_STATIC.interact_IND];
        ret[8] = feature - cur.all_criteria[ValueBlock_STATIC.feat_IND];
        ret[9] = minTF - cur.all_criteria[ValueBlock_STATIC.TF_IND];
        //System.out.println("evaluateVSTrueStart start");
        //MoreArray.printArray(ret);
        return ret;
    }

    /**
     *
     */
    private void checkRLibVersion() {
        if (!Rengine.versionCheck()) {
            System.out.println("Rengine.getVersion " + Rengine.getVersion());
            System.out.println("Rengine.rniGetVersion " + Rengine.rniGetVersion());
            System.out.println("** Version mismatch - Java files don't match library version.\nProceeding anyway ...");
            //System.exit(1);
        }
    }

    /**
     * Tests for existence of the named input files.az-0`
     * <p/>
     * prm.nulldist_filepath = _path;
     * prm.R_DATA_PATH = rdatapath;
     * prm.MOVES_PATH = prevblock;
     * prm.MEANMSE_PATH = MSEnull;
     * prm.SDMSE_PATH = sdMSEnull;
     * prm.MEANINTERACT_PATH = meanInteractNull;
     * prm.param_path = parameterpath;
     */
    private void testInputFiles() {
        boolean exit = false;
        File test = null;
        try {
            test = new File(orig_prm.R_METHODS_PATH);
        } catch (Exception e) {
            System.out.println("init R_methods file does not exist :" + orig_prm.R_METHODS_PATH + ":");
            exit = true;
            e.printStackTrace();
        }

        try {
            test = new File(orig_prm.R_DATA_PATH);
        } catch (Exception e) {
            System.out.println("initR rdata file does not exist :" + orig_prm.R_DATA_PATH + ":");
            exit = true;
            e.printStackTrace();
        }

        int meanMSE = 0;
        if (orig_prm.MEANMSE_PATH != null) {
            try {
                test = new File(orig_prm.MEANMSE_PATH);
                meanMSE++;
            } catch (Exception e) {
                System.out.println("init MEANMSE_PATH does not exist :" + orig_prm.MEANMSE_PATH + ":");
                e.printStackTrace();
            }
        }
        if (orig_prm.SDMSE_PATH != null) {
            try {
                test = new File(orig_prm.SDMSE_PATH);
                meanMSE++;
            } catch (Exception e) {
                System.out.println("initR SDMSE_PATH does not exist :" + orig_prm.SDMSE_PATH + ":");
                e.printStackTrace();
            }

        }
        int meanMSER = 0;
        if (orig_prm.MEANMSER_PATH != null) {
            try {
                test = new File(orig_prm.MEANMSER_PATH);
                meanMSER++;
            } catch (Exception e) {
                System.out.println("init MEANMSER_PATH does not exist :" + orig_prm.MEANMSER_PATH + ":");
                e.printStackTrace();
            }
        }
        if (orig_prm.SDMSER_PATH != null) {
            try {
                test = new File(orig_prm.SDMSER_PATH);
                meanMSER++;
            } catch (Exception e) {
                System.out.println("initR SDMSER_PATH  does not exist :" + orig_prm.SDMSER_PATH + ":");
                e.printStackTrace();
            }
        }

        if (meanMSE > 0 && meanMSER > 0) {
            System.out.println("initR meanMSE and meanMSER null files are BOTH missing");
            //exit = true;
        }

        if (orig_prm.MEANINTERACT_PATH != null) {
            try {
                test = new File(orig_prm.MEANINTERACT_PATH);
            } catch (Exception e) {
                System.out.println("initR mean Int null file does not exist :" + orig_prm.MEANINTERACT_PATH + ":");
                e.printStackTrace();
            }
        }

        try {
            test = new File(orig_prm.MOVES_PATH);
        } catch (Exception e) {
            System.out.println("init moves file does not exist, continuing :" + orig_prm.MOVES_PATH + ":");
            e.printStackTrace();
        }

        System.out.println("testInputFiles not OK? " + exit);
        if (exit) {
            System.out.println("missing key components, exiting!");
            System.exit(0);
        }
    }

    /**
     * @param map
     */
    public void init(HashMap map) {
        checkRLibVersion();

        orig_prm = new Parameters();

        String s1 = (String) map.get("-OUTDIR");
        if (s1 != null)
            orig_prm.OUTDIR = s1;
        String s2 = (String) map.get("-mse");
        if (s2 != null)
            orig_prm.MEANMSE_PATH = s2;
        String s3 = (String) map.get("-sdmse");
        if (s3 != null)
            orig_prm.SDMSE_PATH = s3;
        String s4 = (String) map.get("-int");
        if (s4 != null)
            orig_prm.MEANINTERACT_PATH = s4;
        String s5 = (String) map.get("-rdata");
        if (s5 != null)
            orig_prm.R_DATA_PATH = s5;
        String s6 = (String) map.get("-prev");
        if (s6 != null)
            orig_prm.MOVES_PATH = s6;
        String s7 = (String) map.get("-param");
        if (s7 != null)
            orig_prm.param_path = s7;

        System.out.println("loading parameters " + orig_prm.param_path);
        orig_prm.read(orig_prm.param_path);
        System.out.println("RunMinerBack Parameters init HashMap from " + orig_prm.param_path);
        System.out.println(orig_prm.toString());

        String s8 = (String) map.get("-method");
        if (s8 != null)
            orig_prm.R_METHODS_PATH = s8;

        testInputFiles();

        System.getProperties().list(System.out);
        /*System.out.println("R_HOME " + System.getProperty("R_HOME"));
        String[] R_args = {"--no-save", "--no-restore", "--no-readline"};//, "--min-vsize=1000M", "--max-vsize=2000M"};//, "--max-vsize=2000M", "--max-nsize=2000M"};
        System.out.println("starting Rengine");
        re = new Rengine(R_args, false, new TextConsole());
        System.out.println("Rengine created, waiting for R");*/

        initIRV();
    }

    /**
     */
    public void initFast() {
        checkRLibVersion();

        testInputFiles();
        System.out.println("RunMinerBack initFast: orig_prm.EXPR_DATA_PATH " + orig_prm.EXPR_DATA_PATH);
        if (orig_prm.EXPR_DATA_PATH != null) {
            System.out.println("RunMinerBack initFast: Parameters orig_prm.EXPR_DATA_PATH " + orig_prm.EXPR_DATA_PATH);
            expr_matrix = new SimpleMatrix(orig_prm.EXPR_DATA_PATH);
            setLabels();
            //exp_compendium = TabFile.readtoDoubleArray(orig_prm.EXPR_DATA_PATH);
            System.out.println("RunMinerBack initFast: exp_compendium " + expr_matrix.data.length + "\t" + expr_matrix.data[0].length);
        } else
            System.out.println("RunMinerBack initFast: Parameters prm.EXPR_DATA_PATH IS NULL");

        System.getProperties().list(System.out);
        System.out.println("RRunMinerBack initFast: _HOME " + System.getProperty("R_HOME"));
        String[] R_args = {"--no-save"};//,"--min-vsize=1000M", "--max-vsize=2000M"};//, "--max-vsize=2000M", "--max-nsize=2000M"};//"--max-vsize=2000M"};

        System.out.println("RunMinerBack initFast: starting Rengine");
        /*re = new Rengine(R_args, false, new TextConsole());
        System.out.println("RunMinerBack initFast: Rengine created, waiting for R");*/

        initIRV();
    }

    /**
     * @param parameterpath
     */
    public void init(String parameterpath) {
        checkRLibVersion();

        orig_prm = new Parameters();

        System.out.println("loading parameters " + parameterpath);
        orig_prm.param_path = parameterpath;
        try {
            orig_prm.read(parameterpath);
        } catch (Exception e) {
            System.out.println("Failed to load parameter file, exiting.");
            e.printStackTrace();
        }

        System.out.println("RunMinerBack Parameters init from " + parameterpath);
        if (orig_prm != null)
            initBack();
        else {
            System.out.println("RunMinerBack Parameters are null");
            System.exit(0);
        }
    }

    /**
     *
     */
    private void initBack() {
        if (debug != -1) {
            orig_prm.debug = debug;
        }
        System.out.println(orig_prm.toString());
        System.out.println("RunMinerBack init orig_prm.EXPR_DATA_PATH " + orig_prm.EXPR_DATA_PATH);
        if (orig_prm.EXPR_DATA_PATH != null) {
            System.out.println("RunMinerBack Parameters orig_prm.EXPR_DATA_PATH " + orig_prm.EXPR_DATA_PATH);
            expr_matrix = new SimpleMatrix(orig_prm.EXPR_DATA_PATH);
            //System.out.println("RunMinerBack expr_matrix 1st row, 1st col");
            //System.out.println(MoreArray.toString(expr_matrix.data[0], ","));
            //System.out.println(MoreArray.toString(Matrix.extractColumn(expr_matrix.data, 1), ","));
            setLabels();
            System.out.println("RunMinerBack init exp_compendium " + expr_matrix.data.length + "\t" + expr_matrix.data[0].length);
        } else
            System.out.println("RunMinerBack Parameters orig_prm.EXPR_DATA_PATH IS NULL");

        if (orig_prm.FEAT_DATA_PATH != null) {
            System.out.println("RunMinerBack Parameters orig_prm.FEAT_DATA_PATH " + orig_prm.FEAT_DATA_PATH);
            feat_matrix = new SimpleMatrixInt(orig_prm.FEAT_DATA_PATH);
            //System.out.println("RunMinerBack feat_matrix 1st row, 1st col");
            //System.out.println(MoreArray.toString(feat_matrix.data[0], ","));
            //System.out.println(MoreArray.toString(Matrix.extractColumn(feat_matrix.data, 1), ","));
            feat_labels = feat_matrix.xlabels;
            System.out.println("RunMinerBack init feat_compendium " + feat_matrix.data.length + "\t" + feat_matrix.data[0].length);
        } else
            System.out.println("RunMinerBack Parameters orig_prm.FEAT_DATA_PATH IS NULL");

        if (orig_prm.EXCLUDE_LIST_PATH != null) {
            System.out.println("RunMinerBack EXCLUDE_LIST_PATH " + orig_prm.EXCLUDE_LIST_PATH);
            File test = new File(orig_prm.EXCLUDE_LIST_PATH);
            if (test.exists()) {
                /*while (!test.canWrite()) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }*/
                //else {
                try {
                    exclude_vbl = ValueBlockList.read(orig_prm.EXCLUDE_LIST_PATH, false);
                } catch (Exception e) {
                    System.out.println("failed to read exclusion vbl");
                    e.printStackTrace();
                    try {
                        exclude_vbl = ValueBlockListMethods.readBIC(orig_prm.EXCLUDE_LIST_PATH, false);
                    } catch (Exception e1) {
                        System.out.println("failed to read exclusion bic");
                        e1.printStackTrace();
                    }
                }
                //allows for an empty list, e.g. first wave iteration
                if (exclude_vbl == null || exclude_vbl.size() == 0) {
                    exclude_vbl = null;
                    System.out.println("exclude_vbl n ");
                } else {
                    exclude_list = new ArrayList();
                    for (int i = 0; i < exclude_vbl.size(); i++) {
                        ValueBlock v = (ValueBlock) exclude_vbl.get(i);
                        exclude_list.add(MoreArray.toString(v.genes, ",") + "/" + MoreArray.toString(v.exps, ","));
                    }
                    System.out.println("exclude_vbl y " + exclude_list.size());
                }
                //}
            } else {
                System.out.println("exclude_vbl file DNE! " + orig_prm.EXCLUDE_LIST_PATH);
                System.exit(1);
            }
        }

        testInputFiles();

        //method to set system env for R_HOME
        /*if (rhome != null) {
            prm.R_HOME = rhome;
            Properties sysProps = new Properties();
            sysProps.setProperty("R_HOME", prm.R_HOME);
            System.setProperties(sysProps);
        }*/
        //method to print all system properties
        /*Properties sysProps = new Properties();
        Enumeration enProps = sysProps.propertyNames();
        String key = "";
        while (enProps.hasMoreElements()) {
            key = (String) enProps.nextElement();
            if (key.equals("R_HOME"))
                System.out.println("init R_HOME " + key + "  ->  " + sysProps.getProperty(key));
        }*/

        System.getProperties().list(System.out);

        System.out.println("R_HOME " + System.getProperty("R_HOME"));
        /*String[] R_args = {"--no-save"};//, "--min-vsize=1000M", "--max-vsize=2000M"};//, "--max-vsize=2000M", "--max-nsize=2000M"};
        System.out.println("starting Rengine");
        //MoreArray.printArray(R_args);
        //re = new Rengine();//
        re = new Rengine(R_args, false, new TextConsole());
        System.out.println("Rengine created, waiting for R");
        if (!re.waitForR()) {
            System.out.println("Cannot load R");
            System.exit(1);
        } else {
            System.out.println("R started standard");
        }*/
        /*if (re == null) {
            String[] R_args = {"--no-save", "--no-restore", "--no-readline"};
            System.out.println("RunMinerBack standard: starting Rengine");
            re = new Rengine(R_args, false, new TextConsole());
            System.out.println("RunMinerBack standard: Rengine created, waiting for R");
            if (!re.waitForR()) {
                System.out.println("RunMinerBack standard: Cannot load R");
                System.exit(1);
            } else {
                System.out.println("RunMinerBack standard: R started");
            }
        }*/

        initIRV();
    }

    /**
     *
     */
    private void initIRV() {
        irv = new InitRandVar(orig_prm, sysRes, debug > 0 ? true : false);
        //irv = new InitRandVar(orig_prm, prm.RANDOM_SEED, prm.R_METHODS_PATH, prm.R_DATA_PATH, false);
        if (!irv.Rengine.waitForR()) {
            System.out.println("RunMinerBack initFast: Cannot load R");
            System.exit(1);
        } else {
            System.out.println("RunMinerBack initFast: R started");
        }
    }

}
