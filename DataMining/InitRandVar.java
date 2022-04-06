package DataMining;

import bioobj.GeneAnnotation;
import dtype.SystemResource;
import mathy.SimpleMatrix;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;
import util.MoreArray;
import util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: Mar 1, 2011
 * 7:38 PM
 */
public class InitRandVar {

    boolean debug;
    public REXP Rexpr;
    public Rengine Rengine;
    public ObtainNullValues onv;
    public Parameters prm;
    public String Rcodepath, inputR;

    SimpleMatrix TFtargetmap;
    String[] gene_labels;

    /*Look-up annotations by gene name*/
    HashMap annotations;
    SystemResource sysRes;

    long seed = MINER_STATIC.RANDOM_SEEDS[0];//759820;


    /**
     * @param p
     * @param s
     * @param r
     * @param i
     * @param d
     */
    public InitRandVar(Parameters p, long s, String r, String i, boolean d) {
        debug = d;
        Rcodepath = r;
        inputR = i;
        prm = p;
        seed = prm.RANDOM_SEED;
        init();
    }

    /**
     * @param p
     * @param d
     */
    public InitRandVar(Parameters p, boolean d) {
        debug = d;
        Rcodepath = p.R_METHODS_PATH;
        inputR = p.R_DATA_PATH;
        prm = p;
        seed = prm.RANDOM_SEED;
        init();
    }

    /**
     * @param p
     * @param d
     */
    public InitRandVar(Parameters p, SystemResource s, boolean d) {
        sysRes = s;
        debug = d;
        System.out.println("InitRandVar debug " + d);
        Rcodepath = p.R_METHODS_PATH;
        inputR = p.R_DATA_PATH;
        prm = p;

        if (d)
            System.out.println("InitRandVar usemean " + prm.crit.usemean);

        init();
    }


    /**
     * @param s
     * @param d
     */
    public InitRandVar(long s, boolean d) {
        debug = d;
        seed = s;
        initR();
        initRSeed();
    }

    /**
     *
     */
    private void init() {

        initR();

        String curDir = System.getProperty("user.dir");
        if (debug)
            System.out.println("working dir " + curDir);
        String curdirPC = util.StringUtil.replace(curDir, "\\", "/");
        if (debug) {
            System.out.println("java.library.path " + System.getProperty("java.library.path"));
            System.out.println("java.class.path " + System.getProperty("java.class.path", "."));
        }


        if (sysRes != null) {
            if (debug)
                System.out.println("sysRes.os " + sysRes.os);
            String s01 = null;
            if (sysRes.os.indexOf("Windows") != -1) {
                s01 = "setwd(\"" + curdirPC + "\")";
                System.out.println("R: " + s01);
                System.out.println(Rexpr = Rengine.eval(s01));
            } else {
                s01 = "setwd(\"" + curDir + "\")";
                System.out.println("R: " + s01);
                Rexpr = Rengine.eval(s01);
                System.out.println(Rexpr);
            }
        }


        if (Rcodepath != null) {
            String s02 = "source(\"" + Rcodepath + "\")";
            System.out.println("R: " + s02);
            System.out.println(Rexpr = Rengine.eval(s02));
        }

        if (inputR != null) {
            String s04 = "load(\"" + inputR + "\")";
            System.out.println("R: " + s04);
            System.out.println(Rexpr = Rengine.eval(s04));
            System.out.println(Rexpr = Rengine.eval("dim(expr_data)"));
        }

        if (prm.MOVES_PATH != null && prm.MOVES_PATH.length() > 0) {
            String s1 = "load(\"" + prm.MOVES_PATH + "\")";
            System.out.println("R: " + s1);
            System.out.println(Rexpr = Rengine.eval(s1));
        }

        if (prm.TRUNCATE_DATA100) {
            String s3 = "expr_data<-expr_data[1:100,1:100]";
            System.out.println("R: " + s3);
            Rengine.eval(s3);
            if (prm.crit.isInteractCrit || prm.precrit.isInteractCrit) {
                System.out.println("initR");
                s3 = "interact_data<-interact_data[1:100,1:100]";
                System.out.println("R: " + s3);
                Rengine.eval(s3);
            }
        }

        if (Rcodepath != null && inputR != null) {
            for (int i = 0; i < MINER_STATIC.REQUIRED_R_PACKAGES.length; i++) {
                String s9 = "library(" + MINER_STATIC.REQUIRED_R_PACKAGES[i] + ")";
                System.out.println("R: " + s9);
                System.out.println(Rexpr = Rengine.eval(s9));

                System.out.println(Rexpr = Rengine.eval("sessionInfo()"));
            }
        }

        if (prm != null) {
            onv = new ObtainNullValues(Rengine, prm, debug);

            System.out.println("read prm.TFTARGETMAP_PATH " + prm.TFTARGETMAP_PATH);
            if (prm.TFTARGETMAP_PATH != null && prm.TFTARGETMAP_PATH.length() > 0) {
                TFtargetmap = loadTFranks(prm.TFTARGETMAP_PATH);
                System.out.println("read TFtargetmap " + TFtargetmap.data.length + "\t" + TFtargetmap.data[0].length);
                System.out.println("TFtargetmap " + TFtargetmap.xlabels.length + "\t" + TFtargetmap.ylabels.length);
                //System.out.println("TFtargetmap.ylabels " + MoreArray.toString(TFtargetmap.ylabels, ","));
            }

            if (prm.ANNOTATION_PATH != null && prm.ANNOTATION_PATH.length() > 0) {
                annotations = new HashMap();
                loadAnnotations();
            }
        }

        System.out.println("read prm.TFTARGETMAP_PATH " + prm.TFTARGETMAP_PATH);
        if (prm.TFTARGETMAP_PATH != null && prm.TFTARGETMAP_PATH.length() > 0) {
            TFtargetmap = InitRandVar.loadTFranks(prm.TFTARGETMAP_PATH);
            System.out.println("read TFtargetmap " + TFtargetmap.data.length + "\t" + TFtargetmap.data[0].length);
            System.out.println("TFtargetmap " + TFtargetmap.xlabels.length + "\t" + TFtargetmap.ylabels.length);
            //System.out.println("TFtargetmap.ylabels " + MoreArray.toString(TFtargetmap.ylabels, ","));
        }


        setNumGeneAndExp();

        if (Double.isNaN(prm.PG)) {
            prm.setpG();
        }

        setPrecritList();

        initRSeed();

        setIAndJMinAndMaxBounds();


        if (debug) {
            System.out.println("block size bounds " + prm.IMIN + "\t" + prm.IMAX +
                    "\t:\t" + prm.JMIN + "\t" + prm.JMAX);
            System.out.println("MEANINTERACT_PATH " + prm.MEANINTERACT_PATH);
            System.out.println("SDINTERACT_PATH " + prm.SDINTERACT_PATH);
        }
    }

    /**
     *
     */
    public void initR() {
        //vanilla = –no-save, –no-environ, –no-site-file, –no-init-file and –no-restore
        String[] R_args = {"--vanilla"};
        System.out.println("RunMinerBack standard: starting Rengine");
        Rengine = new Rengine(R_args, false, new TextConsole());
        Rengine.DEBUG = 0;
        System.out.println("RunMinerBack standard: Rengine created, waiting for R");
        if (!Rengine.waitForR()) {
            System.out.println("RunMinerBack standard: Cannot load R");
            System.exit(1);
        } else {
            System.out.println("RunMinerBack standard: R started");
        }

        String s0 = "rm(list=ls())";
        System.out.println("R: " + s0);
        System.out.println(Rexpr = Rengine.eval(s0));
    }

    /**
     *
     */
    private void setPrecritList() {
        if (prm.SIZE_PRECRIT_LIST != -1) {
            prm.SIZE_PRECRIT_LIST_EXP = prm.SIZE_PRECRIT_LIST;
            prm.SIZE_PRECRIT_LIST_GENE = prm.SIZE_PRECRIT_LIST;
        } else {
            if (prm.SIZE_PRECRIT_LIST_EXP == -1) {
                prm.SIZE_PRECRIT_LIST_GENE = prm.DATA_LEN_GENES;
            } else if (prm.SIZE_PRECRIT_LIST_GENE <= 1.0) {
                prm.SIZE_PRECRIT_LIST_GENE = (int) (prm.fraction_genes_for_precritmove * (double) prm.DATA_LEN_GENES);
                if (debug)
                    System.out.println("initR SIZE_PRECRIT_LIST_GENE " + prm.fraction_genes_for_precritmove + "\t" + prm.DATA_LEN_GENES);
            }
            if (prm.SIZE_PRECRIT_LIST_EXP == -1) {
                prm.SIZE_PRECRIT_LIST_EXP = prm.DATA_LEN_EXPS;
            } else if (prm.SIZE_PRECRIT_LIST_EXP <= 1.0) {
                prm.SIZE_PRECRIT_LIST_EXP = (int) (prm.fraction_exps_for_precritmove * (double) prm.DATA_LEN_EXPS);
                if (debug)
                    System.out.println("initR SIZE_PRECRIT_LIST_GENE " + prm.fraction_exps_for_precritmove + "\t" + prm.DATA_LEN_EXPS);
            }
        }
        if (debug)
            System.out.println("initR SIZE_PRECRIT_LIST_GENE/exp ... " + prm.SIZE_PRECRIT_LIST_GENE + "\t" + prm.SIZE_PRECRIT_LIST_EXP);
    }

    /**
     *
     */
    private void setIAndJMinAndMaxBounds() {
        //System.out.println("setIAndJMinAndMaxBounds b/f" + rmb.prm.IMIN + "\t" + rmb.prm.JMIN + "\t" + rmb.prm.IMAX + "\t" + rmb.prm.JMAX);
        if (prm.IMIN <= MINER_STATIC.MIN_BLOCK_SIZE)
            prm.IMIN = MINER_STATIC.MIN_BLOCK_SIZE;

        if (prm.JMIN <= MINER_STATIC.MIN_BLOCK_SIZE)
            prm.JMIN = MINER_STATIC.MIN_BLOCK_SIZE;

        System.out.println("setIAndJMinAndMaxBounds a/f " + prm.IMIN + "\t" + prm.JMIN + "\t" + prm.IMAX + "\t" + prm.JMAX);
    }


    /**
     * Sets the number of genes and experiments based on the dimensions of the provided dataset.
     */
    private void setNumGeneAndExp() {
        String s1 = "I=dim(expr_data)[1]";
        System.out.println("R: " + s1);
        System.out.println(Rexpr = Rengine.eval(s1));//number of rows or genesStr)
        int test = Rexpr.asInt();
        if (prm.DATA_LEN_GENES == -1 || prm.DATA_LEN_GENES != test) {
            if (debug)
                System.out.println("Setting number of rows/genes (DATA_LEN_GENES):" + test);
            prm.DATA_LEN_GENES = test;
        }
        String s2 = "J=dim(expr_data)[2]";
        System.out.println("R: " + s2);
        System.out.println(Rexpr = Rengine.eval(s2));//number of columns or expsStr)
        test = Rexpr.asInt();
        if (prm.DATA_LEN_EXPS == -1 || prm.DATA_LEN_EXPS != test) {
            if (debug)
                System.out.println("Setting number of columns/experiments (DATA_LEN_EXPS):" + test);
            prm.DATA_LEN_EXPS = test;
        }
    }


    /**
     * Set the random seed for the algorithm, either based on the default seed or user specified.
     */
    public void initRSeed() {
        long l = -1;
        if ((prm != null && !prm.USE_RANDOM_SEED))
            l = prm.RANDOM_SEED;
        else if (seed != -1)
            l = seed;

        if (l != -1) {
            String s5 = "set.seed(" + l + ")";
            System.out.println("R: " + s5);
            if (debug)
                System.out.println("initRSeed SEED IS SPECIFIED " + s5 + "\t" + l);
            System.out.println(Rexpr = Rengine.eval(s5));
        } else {
            String s5 = "set.seed(" + MINER_STATIC.RANDOM_SEEDS[0] + ")";
            System.out.println("R: " + s5);
            if (debug)
                System.out.println("initRSeed SEED IS RANDOM " + s5 + "\t" + l + "\t" + prm.RANDOM_SEED);
            System.out.println(Rexpr = Rengine.eval(s5));
/*s5 = "USE_RANDOM_SEED";
System.out.println("R: "+s5);
Rexpr = Rengine.eval(s5);*/
        }
        if (prm != null)
            System.out.println("initRSeed " + prm.USE_RANDOM_SEED + "\t" + prm.RANDOM_SEED);
    }

    /**
     * @param file
     * @return
     */
    public final static SimpleMatrix loadTFranks(String file) {
        SimpleMatrix sm = new SimpleMatrix(file);
        sm.ylabels = StringUtil.replace(sm.ylabels, "\"", "");
        return sm;
    }

    /**
     *
     */
    public void loadAnnotations() {
        String[] data = util.TextFile.readtoArray(prm.ANNOTATION_PATH);
        for (int i = 0; i < data.length; i++) {
            GeneAnnotation ga = new GeneAnnotation(data[i]);
//System.out.println("loadAnnotations "+ga);
            annotations.put(ga.yid, ga);
        }
    }


    /**
     * @param features
     */
    public void initRFeaturesAndFactors(HashMap features) {
        //if all features are requested then load from merged R data
        /*  if (rmb.irv.prm.all_features) {
           String setdata = "feature_data <- rAllfeat[,-1]";
           System.out.println("R: " + setdata);
           Rexpr = Rengine.eval(setdata);
       }
       //otherwise the load specified feature data sets, piece by piece
       else {*/
        Iterator iter = features.values().iterator();
        ArrayList unique = new ArrayList();
        while (iter.hasNext()) {
            String s = (String) iter.next();
            int index = MoreArray.getArrayInd(unique, s);
            if (index == -1) {
                if (debug)
                    System.out.println("initRFeaturesAndFactors " + s);
//for (int i = 0; i < FEATURES.size(); i++) {
//String s = (String) FEATURES.get(i);
                String load = "load(\"" + s + "\")";
                System.out.println("R: " + load);
                Rexpr = Rengine.eval(load);
                unique.add(s);
            }
        }
        Iterator iter2 = features.keySet().iterator();
        if (features.size() > 1) {
            int count = 0;
            while (iter2.hasNext()) {
                String s1 = (String) iter2.next();
                String s2 = (String) iter2.next();
/* for (int i = 0; i < FEATURES.size() - 1; i++) {
String s1 = (String) FEATURES.get(i);
String s2 = (String) FEATURES.get(i + 1);*/
                if (count == 0) {
                    //This gives a matrix, merging the data columns together

                    if (s1.equals("Fitness_allfiles2") || s2.equals("Fitness_allfiles2")) {
                        String s6 = s1 + "=rAllfeat[,-1]";
                        if (s2.equals("Fitness_allfiles2"))
                            s6 = s2 + "=rAllfeat[,-1]";
                        System.out.println("R: " + s6);
                        System.out.println(Rexpr = Rengine.eval(s6));
                    }
                    String setdata = "feature_data<-cbind(" + s1 + "," + s2 + ")";
                    System.out.println("R: " + setdata);
                    Rexpr = Rengine.eval(setdata);
//this gives the indices of the featuredata matrix that are factor variables.
                    String setFactors = "Ifactor<-which(cbind(" + s1 + "_Ifac," + s2 + "_Ifac)==1)";
                    System.out.println("R: " + setFactors);
                    Rexpr = Rengine.eval(setFactors);
                } else {
                    if (s2.equals("Fitness_allfiles2")) {
                        String s6 = s2 + "[,-1]";
                        System.out.println("R: " + s6);
                        System.out.println(Rexpr = Rengine.eval(s6));
                    }
                    String setdata = "feature_data<-cbind(feature_data," + s2 + ")";
                    System.out.println("R: " + setdata);
                    Rexpr = Rengine.eval(setdata);
//this gives the indices of the featuredata matrix that are factor variables.
                    String setFactors = "Ifactor<-which(cbind(Ifactor," + s2 + "_Ifac)==1)";
                    System.out.println("R: " + setFactors);
                    Rexpr = Rengine.eval(setFactors);
                }
                count++;
            }
        } else {//case size = 1
            String s1 = (String) iter2.next();
            String setdata = "feature_data<-" + s1;
            System.out.println("R: " + setdata);
            Rexpr = Rengine.eval(setdata);
//this gives the indices of the featuredata matrix that are factor variables.
            String setFactors = "Ifactor<-which(" + s1 + "_Ifac==1)";
            System.out.println("R: " + setFactors);
            Rexpr = Rengine.eval(setFactors);
        }
        //}

        if (debug) {
            String f = "Ifactor";
            System.out.println("R: " + f);
            Rexpr = Rengine.eval(f);
        }

        if (prm.TRUNCATE_DATA100) {
            String s3 = "feature_data<-feature_data[1:100,1:100]";
            System.out.println("R: " + s3);
            Rengine.eval(s3);
        }

        /*Rexpr = Rengine.eval("rAllfeat[,1]");

        String s4 = "chromosome_no_features_index <- which(colnameFs(feature_data)==\"chromosome No\")";
        System.out.println("R: " + s4);
        System.out.println(Rengine.eval(s4));
        String s5 = "chromosome_no_features <- feature_data[,chromosome_no_features_index]";
        System.out.println("R: " + s5);
        Rengine.eval(s5);

        String prefeat = "feature_data <- pre_Feature(feature_data)";
        System.out.println("R: " + prefeat);
        System.out.println(rmb.Rexpr = Rengine.eval(prefeat));*/
    }

}
