package DataMining;

import mathy.SimpleMatrix;
import mathy.SimpleMatrixInt;
import mathy.stat;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.RList;
import org.rosuda.JRI.Rengine;
import util.*;

import java.io.File;
import java.util.*;


/**
 * User: marcinjoachimiak
 * Date: Feb 26, 2010
 * Time: 10:42:28 AM
 */
public class MakeNull extends Program {
    public final static String version = "0.1";
    boolean debug = false;

    String[] valid_args = {
            "-source", "-intxt", "-infeat", "-inTF", "-ininter", "-inR", "-out", "-gmin", "-gmax",
            "-emin", "-emax", "-nsamp", "-genes", "-seed", "-abs", "-frxnsign", "-chunked", "-crits", "-debug"
    };

    String Rcodepath;
    String inputtxt;
    String inputfeat;
    //String inputinter;
    String inputR;

    String outf, output_file_prefix, output_path;

    REXP Rexpr;
    Rengine Rengine;

    Random rand;
    SampleBlocks sb;

    String samplepath = "_samp/", toppath = "_top/", stdoutpath = "_out/";

    public static int num_samples = 10;
    //x - exps
    public static int expmin = 2;
    public static int expmax = 200 + 1;
    //x - genes
    public static int genemin = 2;
    public static int genemax = 200 + 1;

    final static int REGRESSION_MIN_SAMPLE = 3;

    SimpleMatrix input_data;
    SimpleMatrixInt feat_data;
    //SimpleMatrixInt inter_data;
    SimpleMatrix TFtargetmap = null;


    public double percent_allowed_missing_genes = MINER_STATIC.DEFAULT_percent_allowed_missing_genes;
    public double percent_allowed_missing_exp = MINER_STATIC.DEFAULT_percent_allowed_missing_exps;
    public double percent_allowed_missing_in_block = MINER_STATIC.DEFAULT_percent_allowed_missing_in_block;

    Criterion criterion;

    /*TODO :  These two variables may need to be generalized for all valid bins and data dimensions */
    double gene_sampling_bins = 5;
    double exp_sampling_bins = 5;

    /*TODO fix SELECT_CRIT_LABELS using user specified criteria */
    public String[] SELECT_CRIT_LABELS = {

            //"COR",
            //"CORR",
            //"CORC",

            //"MADR"
/*

            "EUC",
            "EUCR",
            "EUCC",

            "MSE",
            "MSER",
            "MSEC",

            "KENDALL",
            "KENDALLR",
            "KENDALLC",

            "GEE",
            "GEERE",
            "GEECE",
*/


            /*"MEAN",
            "MEDRMEAN",
            "MEDCMEAN",*/

            //"LARS",
            //"LARSRE",
            //"LARSCE",


            //"feat",
            //"MAXTF",///non-R criterion
            //"inter",
            //"MADR",



            /*
            //combined criteria
            "MSER_KENDALL_GEERE",
            "MSEC_KENDALLC_GEECE",
            "MSER_KENDALL_GEERE_inter",
            "MSEC_KENDALLC_GEECE_inter",
            "MSER_KENDALL_GEERE_MAXTF",
            "MSEC_KENDALLC_GEECE_MAXTF",
            "MSER_KENDALL_GEERE_inter_MAXTF",
            "MSEC_KENDALLC_GEECE_inter_MAXTF",

            "MSE_KENDALL",
            "MSER_KENDALL",
            "MSEC_KENDALL",

            "MSE_KENDALL_GEERE",
            "MSE_KENDALL_GEECE",
            "MSER_KENDALL_GEERE",
            "MSEC_KENDALL_GEECE",
            */

    };

    int CRIT_MASTER_IND;
    int CRIT_IND;
    String curCRITlabel;

    HashMap critvalmapExpCrit;

    String[] gene_labels;

    int dataexpmax, datagenemax;
    HashMap sampleHash;

    int genestep = 1;
    int expstep = 1;
    int maxgcount = 0;
    int maxecount = 0;
    double[] genesampling;
    double[] expsampling;
    int[] genesteps_ind;
    int[] expsteps_ind;
    String[] genesteps_ind_str;
    String[] expsteps_ind_str;

    //set to 10% of number of samples or 1
    int MAX_TOPLIST_LEN = 1;
    ValueBlockList topNlist = new ValueBlockList();

    long seed = MINER_STATIC.RANDOM_SEEDS[0];//759820;

    HashMap options;
    boolean[] forceinv = {true, true, true, true, true, true, true};
    int[] use_abs = MoreArray.initArray(3, 0);
    private AssignCrit assignCrit;

    boolean chunked = false;

    boolean useFrxnSign = false;

    boolean[] neednull;


    /**
     * @param args
     */
    public MakeNull(String[] args) {

        init(args);

        initRandVar();

        initSampling();
        System.out.println("debug " + debug);
        sb = new SampleBlocks(percent_allowed_missing_in_block, percent_allowed_missing_genes,
                percent_allowed_missing_exp, datagenemax, dataexpmax, rand, input_data.data, false);//debug);

        System.out.println("version " + version);

        System.out.println("datagenemax " + datagenemax + "\tdataexpmax " + dataexpmax);
        MoreArray.printArray(assignCrit.CRITindex);

        if (chunked)
            doChunked();
        else
            doUnchunked();

        System.out.println("finished");
        System.exit(0);
    }

    /**
     *
     */
    private void doChunked() {
        boolean geneexp = false;
        for (CRIT_IND = 0; CRIT_IND < assignCrit.CRITindex.length; CRIT_IND++) {

            boolean genecrit = false;
            if (CRIT_MASTER_IND == 166 || CRIT_MASTER_IND == 167 || CRIT_MASTER_IND == 41 || CRIT_MASTER_IND == 40 || CRIT_MASTER_IND == -10) {
                genecrit = true;
            }

            CRIT_MASTER_IND = assignCrit.CRITindex[CRIT_IND];

            double[][] retmean = new double[expsteps_ind.length][genesteps_ind.length];
            double[][] retsd = new double[expsteps_ind.length][genesteps_ind.length];
            double[][] retmedian = new double[expsteps_ind.length][genesteps_ind.length];
            double[][] retIQR = new double[expsteps_ind.length][genesteps_ind.length];

            curCRITlabel = SELECT_CRIT_LABELS[CRIT_IND];
            System.out.println("CRIT_MASTER_IND " + CRIT_IND + "\t" + CRIT_MASTER_IND + "\t" + curCRITlabel);
            System.out.println("curCRITlabel :" + curCRITlabel + ":\t" +
                    (CRIT_MASTER_IND > 0 ? MINER_STATIC.CRIT_LABELS[CRIT_MASTER_IND - 1] : -1));

            critvalmapExpCrit = new HashMap();
            int gcount = 0;
            int j = genemin;
            while (j < genemax + 1) {
                int ecount = 0;
                int i = expmin;
                //exps
                boolean endexp = false;
                while (i < expmax + 1) {
                    if (debug)
                        System.out.print("" + j + "_" + i + ".");
                    sampleHash = new HashMap();
                    //System.out.println("sampleBlocks");
                    sampleBlocksChunked(i, j);

                    computeCritChunked(i, j, gcount, ecount);
                    if (genecrit) {
                        /*retmeansd[ecount][gcount] = doSummaryStatSpecialChunked();
                        retmedianIQR[ecount][gcount] = doSummaryStatSpecialChunked();*/
                        double[][] vals = doSummaryStatSpecialChunked();
                        retmean[ecount][gcount] = vals[0][0];
                        retsd[ecount][gcount] = vals[0][1];
                        retmedian[ecount][gcount] = vals[1][0];
                        retIQR[ecount][gcount] = vals[1][1];
                    } else {
                        double[][] vals = doSummaryStatChunked();
                        retmean[ecount][gcount] = vals[0][0];
                        retsd[ecount][gcount] = vals[0][1];
                        retmedian[ecount][gcount] = vals[1][0];
                        retIQR[ecount][gcount] = vals[1][1];
                    }
                    if ((i + expsampling[i - expmin] >= expmax && !endexp) || genecrit) {
                        i = expmax;
                        endexp = true;
                    } else
                        i += expsampling[i - expmin];
                    ecount++;
                    if (ecount > maxecount)
                        maxecount = ecount;
                }

                if (j + genesampling[j - genemin] >= genemax && !geneexp) {
                    j = genemax;
                    geneexp = true;
                } else
                    j += genesampling[j - genemin];
                gcount++;

                if (gcount > maxgcount)
                    maxgcount = gcount;

            }

            ArrayList one = new ArrayList();
            one.add(retmean);
            one.add(retsd);

            ArrayList two = new ArrayList();
            two.add(retmedian);
            two.add(retIQR);

            if (two != null)
                output(two, "median", "0.5IQR");
            else
                System.out.println("ERROR no output for " + ("median" + "_0.5IQR"));

            if (one != null)
                output(one, "mean", "sd");
            else
                System.out.println("ERROR no output for " + ("mean" + "_sd"));
            //System.out.println("sampleBlocks j " + j + "\t" + genemax + "\tstep " + gcount + "\t" + genesteps_ind.length);
        }
    }

    /*public MakeNull(String[] args) {

        assignCrit = new AssignCrit(SELECT_CRIT_LABELS);

        init(args);

        initRandVar();

        initSampling();
        System.out.println("debug " + debug);
        sb = new SampleBlocks(percent_allowed_missing_in_block, percent_allowed_missing_genes,
                percent_allowed_missing_exp, datagenemax, dataexpmax, rand, input_data.data, false);//debug);

        System.out.println("version " + version);
        //blocks = MoreArray.initArrayList(expsteps_ind.length, genesteps_ind.length);
        doUnchunked();
    }*/
    private void doUnchunked() {
        sampleHash = new HashMap();
        System.out.println("sampleBlocks");
        sampleBlocks();

        System.out.println("datagenemax " + datagenemax + "\tdataexpmax " + dataexpmax);
        MoreArray.printArray(assignCrit.CRITindex);
        for (CRIT_IND = 0; CRIT_IND < assignCrit.CRITindex.length; CRIT_IND++) {
            CRIT_MASTER_IND = assignCrit.CRITindex[CRIT_IND];
            System.out.println("CRIT_IND " + CRIT_IND);
            System.out.println("SELECT_CRIT_LABELS " + MoreArray.toString(SELECT_CRIT_LABELS, ","));
            System.out.println("assignCrit.CRITindex " + MoreArray.toString(assignCrit.CRITindex, ","));
            curCRITlabel = SELECT_CRIT_LABELS[CRIT_IND];

            if (curCRITlabel.toLowerCase().indexOf("nonull") == -1) {
                //if (curCRITlabel.indexOf("LARS") == -1) {
                System.out.println("CRIT_MASTER_IND " + CRIT_IND + "\t" + CRIT_MASTER_IND + "\t" + curCRITlabel);
                System.out.println("curCRITlabel :" + curCRITlabel + ":\t" +
                        (CRIT_MASTER_IND > 0 ? MINER_STATIC.CRIT_LABELS[CRIT_MASTER_IND - 1] : -1));

                critvalmapExpCrit = new HashMap();
                computeCrit();
                if (CRIT_MASTER_IND == 166 || CRIT_MASTER_IND == 167 || CRIT_MASTER_IND == 41 || CRIT_MASTER_IND == 40 || CRIT_MASTER_IND == -10)
                    doSummaryStatSpecial();
                else
                    doSummaryStat();
            }
            //} else
            //    System.out.println("omitting LARS criteria");
        }

        //save sample random_sizes numbers to a file -- currently generates a file which is too large for hardware
            /*int ecount = expmin;
            int gcount = genemin;
            for (int expind = 0; expind < blocks.length; expind++) {
                for (int geneind = 0; geneind < blocks[0].length; geneind++) {
                    ValueBlockList vbl = new ValueBlockList();
                    System.out.println("writing samples " + expind + "\t" + geneind + "\t" + expsampling.length + "\t" + genesampling.length);
                    int size = blocks[expind][geneind].size();
                    System.out.println("writing samples " + size);
                    for (int i = 0; i < size; i++) {
                        ValueBlock vb = new ValueBlock((ValueBlockPre) blocks[expind][geneind].get(i));
                        vbl.add(vb);
                    }
                    try {
                        int pid = getPID();
                        String s = outf + samplepath + "_" + expind + "_" + geneind + "__" + ecount + "_" + gcount + "_" + pid + "_samples.txt";
                        System.out.println("writing samples " + s);
                        String samples = vbl.toString(ValueBlock.toStringShortColumns());
                        util.TextFile.write(samples, s);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    gcount += genesampling[geneind];
                }
                ecount += expsampling[expind];
            }*/
    }


    /**
     * "-source", "-intxt", "-infeat","-inR", "-out", "-gmin", "-gmax",
     * "-emin", "-emax", "-nsamp", "-inTF", "-genes", "-seed"
     *
     * @param args
     */
    private void init(String[] args) {
        MoreArray.printArray(args);

        options = MapArgOptions.maptoMap(args, valid_args);

        if (options.get("-source") != null) {
            Rcodepath = (String) options.get("-source");
        }
        if (options.get("-intxt") != null) {
            inputtxt = (String) options.get("-intxt");
        }
        if (options.get("-infeat") != null) {
            inputfeat = (String) options.get("-infeat");
        }
        /*if (options.get("-ininter") != null) {
            inputinter = (String) options.get("-ininter");
        }*/
        if (options.get("-inR") != null) {
            inputR = (String) options.get("-inR");
        }
        if (options.get("-out") != null) {
            outf = (String) options.get("-out");
        }
        if (options.get("-gmin") != null) {
            genemin = Integer.parseInt((String) options.get("-gmin"));
        }
        if (options.get("-gmax") != null) {
            genemax = Integer.parseInt((String) options.get("-gmax"));
        }
        if (options.get("-emin") != null) {
            expmin = Integer.parseInt((String) options.get("-emin"));
        }
        if (options.get("-emax") != null) {
            expmax = Integer.parseInt((String) options.get("-emax"));
        }
        if (options.get("-nsamp") != null) {
            num_samples = Integer.parseInt((String) options.get("-nsamp"));
            int i = (int) ((double) num_samples / 10.0);
            if (i > 0)
                MAX_TOPLIST_LEN = i;
        }
        if (options.get("-inTF") != null) {
            TFtargetmap = InitRandVar.loadTFranks((String) options.get("-inTF"));
            System.out.println("TFtargetmap " + TFtargetmap.xlabels.length + "\t" + TFtargetmap.ylabels.length);
            //System.out.println("TFtargetmap.ylabels " + MoreArray.toString(TFtargetmap.ylabels, ","));
        }
        if (options.get("-genes") != null) {
            String[][] sarray = TabFile.readtoArray((String) options.get("-genes"));
            int col = 2;

            System.out.println("gene_labels " + col + "\t" + MoreArray.toString(sarray[1]));
            gene_labels = MoreArray.replaceAll(MoreArray.extractColumnStr(sarray, col), "\"", "");
            System.out.println("gene_labels " + gene_labels.length + "\t" + MoreArray.toString(gene_labels, ","));
        }
        if (options.get("-seed") != null) {
            seed = Integer.parseInt((String) options.get("-seed"));
        }
        if (options.get("-abs") != null) {
            String s = (String) options.get("-abs");
            System.out.println("use_abs str " + s);
            if (s.equalsIgnoreCase("T") || s.equalsIgnoreCase("true") ||
                    s.equalsIgnoreCase("y") || s.equalsIgnoreCase("yes")) {
                use_abs = MoreArray.initArray(3, 1);
            } else if (s.equalsIgnoreCase("F") || s.equalsIgnoreCase("false") ||
                    s.equalsIgnoreCase("n") || s.equalsIgnoreCase("no")) {
                use_abs = MoreArray.initArray(3, 0);
            } else if (s != null) {
                if (s.indexOf(",") != -1) {
                    use_abs = MoreArray.ArrayListtoInt(MoreArray.convtoArrayList(s.split(",")));
                }
                System.out.println("use_abs ar " + MoreArray.toString(use_abs));
            }
        }
        System.out.println("debug bf " + debug);
        if (options.get("-debug") != null) {
            String s = (String) options.get("-debug");
            debug = StringUtil.isTrueorYes(s);
            System.out.println("debug af " + debug);
        }
        if (options.get("-chunked") != null) {
            String s = (String) options.get("-chunked");
            chunked = StringUtil.isTrueorYes(s);
            System.out.println("chunked " + chunked);
        }
        if (options.get("-frxnsign") != null) {
            String s = (String) options.get("-frxnsign");
            useFrxnSign = StringUtil.isTrueorYes(s);
            System.out.println("frxnsign " + useFrxnSign);
        }
        if (options.get("-crits") != null) {
            String s = (String) options.get("-crits");
            if (s.indexOf("_") == -1 && s.indexOf(",") == -1) {
                int index = StringUtil.getFirstEqualsIgnoreCaseIndex(MINER_STATIC.CRIT_LABELS, s);
                SELECT_CRIT_LABELS = new String[1];
                SELECT_CRIT_LABELS[0] = s;
                System.out.println("crit " + s + "\t" + index);
            } else if (s.indexOf("_") != -1) {
                String[] ar = s.split("_");
                SELECT_CRIT_LABELS = new String[ar.length];
                for (int z = 0; z < ar.length; z++) {
                    int index = StringUtil.getFirstEqualsIgnoreCaseIndex(MINER_STATIC.CRIT_LABELS, ar[z]);
                    SELECT_CRIT_LABELS[z] = ar[z];
                    System.out.println("crit " + s + "\t" + index);
                }
            } else if (s.indexOf(",") != -1) {
                String[] ar = s.split(",");
                SELECT_CRIT_LABELS = new String[ar.length];
                for (int z = 0; z < ar.length; z++) {
                    int index = StringUtil.getFirstEqualsIgnoreCaseIndex(MINER_STATIC.CRIT_LABELS, ar[z]);
                    SELECT_CRIT_LABELS[z] = ar[z];
                    System.out.println("crit " + s + "\t" + index);
                }
            }
        }

        //create dirs if necessary
        String[] axdirs = {"", toppath, stdoutpath};
        int lastind = outf.lastIndexOf("/");
        output_path = outf.substring(0, lastind != -1 ? lastind : outf.length());
        output_file_prefix = outf.substring(lastind + 1);
        for (int i = 0; i < axdirs.length; i++) {
            File test = new File(output_path + axdirs[i]);
            if (!test.exists() && !test.isDirectory()) {
                test.mkdir();
            }
        }

        System.out.println("init g " + genemin + "\t" + genemax);
        System.out.println("init e " + expmin + "\t" + expmax);

        if (inputtxt != null) {
            input_data = new SimpleMatrix(inputtxt);
        }

        if (inputfeat != null) {
            feat_data = new SimpleMatrixInt(inputfeat);

            System.out.println("read feat_data " + feat_data.data.length + "\t" + feat_data.data[0].length);
        }

       /* if (inputinter != null) {
            inter_data = new SimpleMatrixInt(inputinter);
            System.out.println("read inter_data " + inter_data.data.length + "\t" + inter_data.data[0].length);
        }*/


        //genes
        datagenemax = input_data.data.length;
        //exps
        dataexpmax = input_data.data[0].length;


        if (feat_data != null && datagenemax != feat_data.data.length) {
            System.out.println("expression and feature data gene axis do not match.");
            System.exit(0);
        }

        /*if (inter_data != null && datagenemax != inter_data.data.length) {
            System.out.println("expression and interaction data gene axis do not match.");
            System.exit(0);
        }*/

        if (genemax > datagenemax) {
            System.out.println("genemax > datagenemax " + genemax + " > " + datagenemax);
            System.exit(0);
        }
        if (expmax > dataexpmax) {
            System.out.println("expmax > dataexpmax " + expmax + " > " + dataexpmax);
            System.exit(0);
        }

        /*TODO disable this feature */
    /*    if (SELECT_CRIT_LABELS == null || SELECT_CRIT_LABELS.length == 0) {
            System.out.println("WARNING WARNING WARNING");
            System.out.println("USING ALL CRITERIA!!! ... except inter and feat and maxtf");

            ArrayList ar = new ArrayList();
            for (int i = 0; i < MINER_STATIC.CRIT_LABELS.length; i++) {
                //filter out inter and feat
                if (MINER_STATIC.CRIT_LABELS[i].indexOf("inter") == -1
                        && MINER_STATIC.CRIT_LABELS[i].indexOf("feat") == -1
                        && MINER_STATIC.CRIT_LABELS[i].indexOf("noninvert") == -1
                        && MINER_STATIC.CRIT_LABELS[i].indexOf("MAXTF") == -1) {
                    ar.add(MINER_STATIC.CRIT_LABELS[i]);
                }
            }
            SELECT_CRIT_LABELS = MoreArray.ArrayListtoString(ar);
        }*/

        System.out.println("SELECT_CRIT_LABELS");
        MoreArray.printArray(SELECT_CRIT_LABELS);
        assignCrit = new AssignCrit(SELECT_CRIT_LABELS);


        neednull = new boolean[7];
        for (int i = 0; i < neednull.length; i++) {
            neednull[i] = false;
        }
    }

    /**
     *
     */
    private double[][] doSummaryStatChunked() {

        double[][] ret = new double[2][2];
        ret[0] = meanAndSDExpCritChunked(true);
        ret[1] = meanAndSDExpCritChunked(false);

        /*if (retexp2 != null)
            output(retexp2, "mean", "sd");
        else
            System.out.println("ERROR no output for " + ("mean" + "_sd"));*/

        return ret;
    }

    /**
     *
     */
    private void doSummaryStat() {
        ArrayList retexp1 = meanAndSDExpCrit(true);
        if (retexp1 != null)
            output(retexp1, "median", "0.5IQR");
        else
            System.out.println("ERROR no output for " + ("median" + "_0.5IQR"));

        ArrayList retexp2 = meanAndSDExpCrit(false);
        if (retexp2 != null)
            output(retexp2, "mean", "sd");
        else
            System.out.println("ERROR no output for " + ("mean" + "_sd"));
    }

    /**
     *
     */
    private double[][] doSummaryStatSpecialChunked() {
        double[][] ret = new double[2][2];
        ret[0] = meanAndSDExpCritSpecialChunked(true);
           /*if (retexp1 != null)
               outputSpecial(retexp1, "median", "0.5IQR");
           else
               System.out.println("ERROR no output for " + ("median" + "_0.5IQR"));
            */
        ret[1] = meanAndSDExpCritSpecialChunked(false);
           /*if (retexp2 != null)
               outputSpecial(retexp2, "mean", "sd");
           else
               System.out.println("ERROR no output for " + ("mean" + "_sd"));*/
        return ret;
    }

    /**
     *
     */
    private void doSummaryStatSpecial() {
        ArrayList retexp1 = meanAndSDExpCritSpecial(true);
        if (retexp1 != null)
            outputSpecial(retexp1, "median", "0.5IQR");
        else
            System.out.println("ERROR no output for " + ("median" + "_0.5IQR"));

        ArrayList retexp2 = meanAndSDExpCritSpecial(false);
        if (retexp2 != null)
            outputSpecial(retexp2, "mean", "sd");
        else
            System.out.println("ERROR no output for " + ("mean" + "_sd"));
    }


    /**
     *
     */
    private void initSampling() {

        int gene_range = genemax - genemin;
        ArrayList genesamples = new ArrayList();
        int interval = (int) stat.roundUp(gene_range / gene_sampling_bins, 0);
        System.out.println("initSampling gene_range " + genemin + "\t" + genemax + "\t" + gene_range +
                "\tgene_sampling_bins " + gene_sampling_bins + "\tinterval " + interval);
        int curdist = 0;
        double curstep = 1;//gene_sampling_bins;
        //for (int i = gene_range; i > -1;) {
        for (int i = 0; i < gene_range; ) {
            if (curdist >= interval) {
                genesamples.add(curstep);
                //genesampling[i] = curstep;
                System.out.println("g initSampling g >= " + i + "\tcurstep " + curstep);
                System.out.println("g initSampling g >= interval, gene i " +
                        i + "\tcurdist " + curdist + "\tcurstep " + curstep + "\tgenestep " + genestep);
                curdist = 0;
                curstep += genestep;
            } else {
                System.out.println("g initSampling g < interval, gene i " +
                        i + "\tcurdist " + curdist + "\tcurstep " + curstep + "\tgenestep " + genestep);
                curdist += curstep;
                for (int j = i; j > Math.max(i - curstep, -1); j--) {
                    genesamples.add(curstep);
                    //genesampling[j] = curstep;
                    System.out.println("g initSampling g < " + j + "\tcurstep " + curstep + "\tsize " + genesamples.size());
                }
                i += curstep;
            }
        }
        genesampling = MoreArray.ArrayListtoDouble(genesamples);//new double[gene_range + 2];

        int exp_range = expmax - expmin;
        ArrayList expsamples = new ArrayList();
        interval = (int) stat.roundUp(exp_range / exp_sampling_bins, 0);
        System.out.println("e initSampling exp_range " + exp_range +
                "\tsampling_bins " + exp_sampling_bins + "\tinterval " + interval);
        curdist = 0;
        curstep = 1;//exp_sampling_bins;
        //for (int i = exp_range; i > -1;) {
        for (int i = 0; i < exp_range; ) {
            if (curdist >= interval) {
                expsamples.add(curstep);
                //expsampling[i] = curstep;
                System.out.println("e initSampling e >= " + i + "\tcurstep " + curstep);
                System.out.println("e initSampling e >= interval, exp i " +
                        i + "\tcurdist " + curdist + "\tcurstep " + curstep + "\texpstep " + expstep);
                curdist = 0;
                curstep += expstep;
            } else {
                System.out.println("e initSampling e < interval, exp i " +
                        i + "\tcurdist " + curdist + "\tcurstep " + curstep + "\texpstep " + expstep);
                curdist += curstep;
                for (int j = i; j > Math.max(i - curstep, -1); j--) {
                    expsamples.add(curstep);
                    //expsampling[j] = curstep;
                    System.out.println("e initSampling e < " + j + "\tcurstep " + curstep + "\tsize " + expsamples.size());
                }
                i += curstep;
            }
        }

        expsampling = MoreArray.ArrayListtoDouble(expsamples);//new double[exp_range + 2];

        System.out.println("genesampling");
        MoreArray.printArray(genesampling);
        System.out.println("expsampling");
        MoreArray.printArray(expsampling);

        System.out.println("sizes sample g " + genesampling.length + "\te " + expsampling.length);
        System.out.println("sizes range g " + (genemax - genemin) + "\te " + (expmax - expmin));

        ArrayList genesteps = new ArrayList();
        ArrayList expsteps = new ArrayList();
        int j = 0;
        int jcount = 0;
        //for (j = genemax; j > genemin - 1;) {
        for (j = genemin; j < genemax + 1; ) {
            genesteps.add(new Integer(j));
            String sample_val = "null";
            try {
                sample_val = "" + genesampling[j - genemin];
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("gene sampling " + j + "\t" + jcount + "\t" +
                    (j - genemin) + "\t" + genesampling.length + "\tsample_val " + sample_val);
            j += genesampling[j - genemin];
            jcount++;
        }

        int last = (Integer) genesteps.get(genesteps.size() - 1);
        if (last < genemax)
            genesteps.add(new Integer(genemax));

        int i = 0;
        int icount = 0;
        //for (i = expmax; i > expmin - 1;) {
        for (i = expmin; i < expmax + 1; ) {
            expsteps.add(new Integer(i));
            System.out.println("exp sampling " + i + "\t" + icount + "\t" + expsampling[i - expmin]);
            i += expsampling[i - expmin];
            icount++;
        }

        int laste = (Integer) expsteps.get(expsteps.size() - 1);
        if (laste < expmax)
            expsteps.add(new Integer(expmax));

        genesteps_ind = MoreArray.ArrayListtoInt(genesteps);
        expsteps_ind = MoreArray.ArrayListtoInt(expsteps);
        System.out.println("genesteps_ind CRIT_LEN " + genesteps_ind.length +
                "\tsampling " + genesampling.length + "\t" + jcount);
        System.out.println("expsteps_ind CRIT_LEN " + expsteps_ind.length +
                "\tsampling " + expsampling.length + "\t" + icount);

        genesteps_ind_str = MoreArray.toStringArray(genesteps_ind);//MoreArray.reverse(MoreArray.toStringArray(genesteps_ind));
        expsteps_ind_str = MoreArray.toStringArray(expsteps_ind);//MoreArray.reverse(MoreArray.toStringArray(expsteps_ind));

        System.out.println("genesteps_ind_str");
        MoreArray.printArray(genesteps_ind_str);
        System.out.println("expsteps_ind_str");
        MoreArray.printArray(expsteps_ind_str);
    }


    /**
     *
     */
    private void sampleBlocksChunked(int i, int j) {
        //System.out.print(".");
                /*if (debug)
                    System.out.println("sampleBlocks " + i + "\t" + j + "\t" +
                            ecount + "\t" + gcount);*/
        for (int a = 0; a < num_samples; a++) {
            sampleOneBlock(j, i, a);
        }
        //System.out.println("sampleBlocks done");
    }

    /**
     *
     */
    private void sampleBlocks() {
        int gcount = 0;
        //genes
        boolean geneexp = false;
        int j = genemin;
        while (j < genemax + 1) {
            int ecount = 0;
            int i = expmin;
            //exps
            boolean endexp = false;
            while (i < expmax + 1) {
                if (debug)
                    System.out.print(".");
                if (debug)
                    System.out.println("sampleBlocks " + i + "\t" + j + "\t" +
                            ecount + "\t" + gcount);
                for (int a = 0; a < num_samples; a++) {
                    sampleOneBlock(j, i, a);
                }
                if (i + expsampling[i - expmin] >= expmax && !endexp) {
                    i = expmax;
                    endexp = true;
                } else
                    i += expsampling[i - expmin];
                ecount++;
            }
            if (j + genesampling[j - genemin] >= genemax && !geneexp) {
                j = genemax;
                geneexp = true;
            } else
                j += genesampling[j - genemin];
            gcount++;
            //System.out.println("sampleBlocks j " + j + "\t" + genemax + "\tstep " + gcount + "\t" + genesteps_ind.length);
        }
        System.out.println("sampleBlocks done");
    }

    /**
     * @param g
     * @param e
     */
    private void sampleOneBlock(int g, int e, int count) {
        boolean stop = false;
        while (!stop) {
            //System.out.println("sampleOneBlock " + aboveNaNThreshold + "\t" + aboveNaNThresholdGene + "\t" + aboveNaNThresholdExp);
            //System.out.print(".");
            int[][] sample = sb.sampleRetInt(g, e, false);//debug);
            String label = "" + e + "_" + g + "__" + count;
            /*if (debug) {
                System.out.println("sampleOneBlock " + label);
                System.out.println("sampleOneBlock g " + sample[0].length);
                System.out.println("sampleOneBlock e " + sample[1].length);
            }*/
            if (sample != null && sample[0] != null && sample[1] != null) {
                sampleHash.put(label, sample);
                stop = true;
                if (debug) {
                    //System.out.println("sampleHash added " + label);
                    //System.out.println("sampleHash added 0 "+MoreArray.toString(sample[0], ","));
                    //System.out.println("sampleHash added 1 "+MoreArray.toString(sample[1], ","));
                }
            }
            if (sample == null) {
                System.out.println("sampleHash is NULL " + label);
            } else if (sample[0] == null) {
                System.out.println("sampleHash genes ind 0 is NULL " + label);
            } else if (sample[1] == null) {
                System.out.println("sampleHash exp ind 1 is NULL " + label);
            }
        }
    }

    /**
     * @param retexp
     */

    private void output(ArrayList retexp, String mlabel, String vlabel) {
        double[][] meanExpcrit = (double[][]) retexp.get(0);
        String f1 = output_path + "/" + output_file_prefix + "_abs" + MoreArray.toString(this.use_abs, "") + "_" +
                MINER_STATIC.EXPR_LABEL + "_" + curCRITlabel + "_" + mlabel + "_" + seed + ".txt";
        TabFile.write(MoreArray.toString(meanExpcrit, "", ""), f1, genesteps_ind_str, expsteps_ind_str);
        System.out.println("output " + f1);

        double[][] sdExpcrit = (double[][]) retexp.get(1);
        String f3 = output_path + "/" + output_file_prefix + "_abs" + MoreArray.toString(this.use_abs, "") + "_" +
                MINER_STATIC.EXPR_LABEL + "_" + curCRITlabel + "_" + vlabel + "_" + seed + ".txt";
        TabFile.write(MoreArray.toString(sdExpcrit, "", ""), f3, genesteps_ind_str, expsteps_ind_str);
        System.out.println("output " + f3);

        try {
            String f4 = output_path + toppath + output_file_prefix + "_" + "_abs" + MoreArray.toString(this.use_abs, "") + "_" +
                    MINER_STATIC.EXPR_LABEL + "_" + curCRITlabel + "_" + seed + "_top" + MAX_TOPLIST_LEN + ".txt";
            System.out.println("output " + f4);
            String sftop100 = topNlist.toString(MINER_STATIC.HEADER_VBL);//ValueBlock.toStringShortColumns());
            util.TextFile.write(sftop100, f4);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param retexp
     */

    private void outputSpecial(ArrayList retexp, String mlabel, String vlabel) {
        double[] meanExpcrit = (double[]) retexp.get(0);
        String f1 = output_path + "/" + output_file_prefix + "_" +
                MINER_STATIC.EXPR_LABEL + "_" + curCRITlabel + "_" + mlabel + "_" + seed + ".txt";
        TabFile.write(MoreArray.toStringArray(meanExpcrit), f1, genesteps_ind_str, null);
        System.out.println("output special " + f1);

        double[] sdExpcrit = (double[]) retexp.get(1);
        String f3 = output_path + "/" + output_file_prefix + "_" +
                MINER_STATIC.EXPR_LABEL + "_" + curCRITlabel + "_" + vlabel + "_" + seed + ".txt";
        TabFile.write(MoreArray.toStringArray(sdExpcrit), f3, genesteps_ind_str, null);
        System.out.println("output special " + f3);

        try {
            String f4 = output_path + toppath + output_file_prefix + "_" +
                    MINER_STATIC.EXPR_LABEL + "_" + curCRITlabel + "_" + seed + "_top" + MAX_TOPLIST_LEN + ".txt";
            System.out.println("output special " + f4 + "\t" + topNlist.size());
            String sftop100 = topNlist.toString(MINER_STATIC.HEADER_VBL);//ValueBlock.toStringShortColumns());
            util.TextFile.write(sftop100, f4);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    private void computeCritChunked(int i, int j, int gcount, int ecount) {
        if (debug)
            System.out.println("computeCritChunked");
        criterion = new Criterion(CRIT_MASTER_IND,
                assignCrit.CRITmean[CRIT_IND] == 1 ? true : false, true, use_abs,
                assignCrit.CRITTF[CRIT_IND] == 1 ? true : false, forceinv, false, useFrxnSign, debug);
        //int gcount = 0;
        //for (int j = genemax; j > genemin - 1;) {
        //boolean endgene = false;
        //for (int j = genemin; j < genemax + 1; ) {
        //int ecount = 0;
        if (debug)
            System.out.print("-");
        //for gene-only criteria
        boolean isSpecialCrit = CRIT_MASTER_IND == 166 || CRIT_MASTER_IND == 167 ||
                CRIT_MASTER_IND == 41 || CRIT_MASTER_IND == 40 || CRIT_MASTER_IND == -10;
        //int expstart = isSpecialCrit ? expmax : expmin;
        //boolean endexp = false;
        //for (int i = expstart; i > expmin - 1;) {
        //for (int i = expstart; i < expmax + 1; ) {
        //System.out.print(j+"_"+i+" ");
        //System.out.println("gene " + j + "\texp " + i);
        for (int ns = 0; ns < num_samples; ns++) {
            String label = "" + i + "_" + j + "__" + ns;
            int[][] VBPInitial = (int[][]) sampleHash.get(label);
            if (debug) {
                System.out.println("computeCritChunked CRIT_MASTER_IND " + CRIT_MASTER_IND +
                        "\tsize exps " + i + "\tgenes " + j + "\t" + label);
                        /*try {
                            System.out.println("computeCrit VBPInitial 0 " + MoreArray.toString(VBPInitial[0], ","));

                        } catch (Exception e) {
                            System.out.println("computeCrit VBPInitial 0 is NULL " + label);
                            e.printStackTrace();
                        }
                        try {
                            System.out.println("computeCrit VBPInitial 1 " + MoreArray.toString(VBPInitial[1], ","));
                        } catch (Exception e) {
                            System.out.println("computeCrit VBPInitial 1 is NULL " + label);
                            e.printStackTrace();
                        }*/
            }

            double[] critsraw = null;
            //get R criteria values, skip if PPI or TF
            if (CRIT_MASTER_IND != 166 && CRIT_MASTER_IND != 167 &&
                    CRIT_MASTER_IND != 41 && CRIT_MASTER_IND != 40 &&
                    CRIT_MASTER_IND != -10) {
                //System.out.print(".");
                if (debug)
                    System.out.println("R: Ic<-c(" + MoreArray.toString(VBPInitial[0], ",") + ")");
                Rengine.assign("Ic", VBPInitial[0]);
                if (debug)
                    System.out.println("R: Jc<-c(" + MoreArray.toString(VBPInitial[1], ",") + ")");
                Rengine.assign("Jc", VBPInitial[1]);
                if (debug)
                    System.out.println("R: nullRegData <- NULL");
                Rengine.eval("nullRegData <- NULL");

                int onlymean = 0;
                int arrayInd = -1;
                try {
                    arrayInd = MoreArray.getArrayInd(MINER_STATIC.MEANCritAll, CRIT_MASTER_IND);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (arrayInd != -1)
                    onlymean = 1;

                String rcall = ComputeCrit.buildCritCallTotal(neednull, onlymean == 1 ? true : false, onlymean, criterion);
                //assignCrit.CRITmean[CRIT_IND] == 1, onlymean, criterion);
                if (debug)
                    System.out.println("R: " + rcall);

                int tried = 0;
                while (tried < 5) {
                    if (tried > 0)
                        System.out.println("attempt genes " + i + "\texps " + j + "\t" + tried + "\t" + rcall);
                    Rexpr = Rengine.eval(rcall);
                    try {
                        RList rl = Rexpr.asList();
                        critsraw = (rl.at(1)).asDoubleArray();
                        break;
                    } catch (Exception e) {
                                /*System.out.println("R: " + rcall);
                             System.out.println("Ic " + Rengine.eval("Ic"));
                             System.out.println("Jc " + Rengine.eval("Jc"));
                             System.out.println("Ic len " + Rengine.eval("length(Ic)"));
                             System.out.println("Jc len " + Rengine.eval("length(Jc)"));
                             //System.out.println("dim " + Rengine.eval("dim(expr_data[Ic,Jc])"));
                             System.out.println("colSums " + Rengine.eval("colSums(!is.na(expr_data[Ic,Jc]))/length(Ic)"));
                             System.out.println("rowSums " + Rengine.eval("rowSums(!is.na(expr_data[Ic,Jc]))/length(Jc)"));
                             System.out.println("var " + Rengine.eval("var(expr_data[Ic,Jc])"));*/
                        //MoreArray.printArray(VBPInitial.exp_data);
                        if (tried == 4) {
                            debugCritError(rcall, e);
                        }
                    }
                    tried++;
                }

                if (critsraw == null || MoreArray.hasNaN(critsraw)) {
                    moreDebugCrit(rcall);
                }
            }

            //MoreArray.printArray(critsraw);
            if (critsraw == null)
                critsraw = new double[ValueBlock_STATIC.NUM_CRIT];
            if (assignCrit.CRITTF[CRIT_IND] == 1) {
                critsraw = critMaxTF(VBPInitial, critsraw);
            } else if (CRIT_MASTER_IND == 41 || CRIT_MASTER_IND == 40) {
                critsraw = critInter(VBPInitial, critsraw);
            } else if (CRIT_MASTER_IND == 166 || CRIT_MASTER_IND == 167) {
                critsraw = critFeat(VBPInitial, critsraw);
            }
            //System.out.println("CRIT_IND " + CRIT_IND + "\t" + CRITTF[CRIT_IND] + "\tTF crit " + critsraw[5]);
            if (debug) {
                String s = null;
                try {
                    s = MINER_STATIC.CRIT_LABELS[CRIT_MASTER_IND - 1];
                } catch (Exception e) {
                    //e.printStackTrace();
                }
                String label1 = null;
                try {
                    label1 = MINER_STATIC.CRIT_LABELS[CRIT_MASTER_IND - 1];
                } catch (Exception e) {
                    //e.printStackTrace();
                }
                System.out.println("criterion wo null " + CRIT_IND + "\t" + label1);
                MoreArray.printArray(critsraw);
            }

            boolean[] passcrits = Criterion.getExprCritTypes(criterion, true,
                    assignCrit.CRITmean[CRIT_IND] == 1 ? true : false, debug);
            //compute weighted full criterion
            double fullcrit = ValueBlock.computeFullCrit(critsraw, true, passcrits, debug);
            if (debug) {
                System.out.println("fullcrit " + MoreArray.toString(critsraw, ",") + "\t=\t" + fullcrit);
                System.out.println("passcrits " + MoreArray.toString(passcrits, ","));
            }

            int posFull = ValueBlockListMethods.findPosFull(topNlist, fullcrit, MAX_TOPLIST_LEN);
            //System.out.println("posFull " + posFull);
            if (posFull != -1) {
                ValueBlock VBPInitialblock = new ValueBlock(VBPInitial[0], VBPInitial[1]);
                VBPInitialblock.updateCrit(critsraw, true, passcrits, debug);
                if (posFull < topNlist.size() || topNlist.size() == 0) {
                    topNlist.add(posFull, VBPInitialblock);
                    //System.out.println("posFull added " + topNlist.size() + "\t" + fullcrit);
                } else if (topNlist.size() < MAX_TOPLIST_LEN)
                    topNlist.add(VBPInitialblock);
                while (topNlist.size() > MAX_TOPLIST_LEN) {
                    int i1 = topNlist.size() - 1;
                    //ValueBlock cur = (ValueBlock) topNlist.get(i1);
                    topNlist.remove(i1);
                    //System.out.println("posFull removed " + topNlist.size() + "\t" + cur.full_criterion);
                }
            }
            String key = "" + ns + "_" + ecount + "_" + gcount;
                   /* if (ecount > maxecount)
                        maxecount = ecount;
                    if (gcount > maxgcount)
                        maxgcount = gcount;*/

            critvalmapExpCrit.put(key, fullcrit);
            if (debug)
                System.out.println("critvalmap key " + key + "\t" + critvalmapExpCrit.size() + "\t" + fullcrit);
        }
                /*if (i + expsampling[i - expmin] >= expmax && !endexp && !isSpecialCrit) {
                    i = expmax;
                    endexp = true;
                } else
                    i += expsampling[i - expmin];

                ecount++;
            }
            if (j + genesampling[j - genemin] >= genemax && !endgene) {
                j = genemax;
                endgene = true;
            } else
                j += genesampling[j - genemin];
            gcount++;
        }*/

        if (debug) {
            System.out.println("maxgcount " + maxgcount + "\tgenesteps_ind " + genesteps_ind.length);
            System.out.println("maxecount " + maxecount + "\texpsteps_ind " + expsteps_ind.length);
        }
    }

    private void debugCritError(String rcall, Exception e) {
        e.printStackTrace();
        moreDebugCrit(rcall);
    }

    /**
     *
     */
    private void computeCrit() {
        if (debug)
            System.out.println("computeCrit CRIT_MASTER_IND " + CRIT_MASTER_IND + "\tCRIT_IND " + CRIT_IND);
        criterion = new Criterion(CRIT_MASTER_IND,
                assignCrit.CRITmean[CRIT_IND] == 1 ? true : false, true, use_abs,
                assignCrit.CRITTF[CRIT_IND] == 1 ? true : false, forceinv, false, useFrxnSign, debug);
        int gcount = 0;
        //for (int j = genemax; j > genemin - 1;) {
        boolean endgene = false;
        for (int j = genemin; j < genemax + 1; ) {
            int ecount = 0;
            //if (debug)
            final double i2 = (genemax + 1 - j) / (genemax + 1 - genemin);
            //double rounded = mathy.stat.roundUp(i2, 2);
            //final String str = ("" + i2);
            //System.out.print("" + str.substring(0, Math.min(str.length(), str.indexOf(".") + 3)) + "-");
            if (debug)
                System.out.print("" + i2 + "-");
            //for gene-only criteria
            boolean isSpecialCrit = CRIT_MASTER_IND == 166 || CRIT_MASTER_IND == 167 ||
                    CRIT_MASTER_IND == 41 || CRIT_MASTER_IND == 40 || CRIT_MASTER_IND == -10;
            if (debug)
                System.out.println("computeCrit isSpecialCrit " + isSpecialCrit);
            int expstart = isSpecialCrit ? expmax : expmin;
            boolean endexp = false;
            //for (int i = expstart; i > expmin - 1;) {
            for (int i = expstart; i < expmax + 1; ) {
                //System.out.print(j+"_"+i+" ");
                //System.out.println("gene " + j + "\texp " + i);
                for (int ns = 0; ns < num_samples; ns++) {
                    String label = "" + i + "_" + j + "__" + ns;
                    int[][] VBPInitial = (int[][]) sampleHash.get(label);
                    if (debug) {
                        System.out.println("computeCrit CRIT_MASTER_IND " + CRIT_MASTER_IND +
                                "\tsize exps " + i + "\tgenes " + j + "\t" + label);
                        /*try {
                            System.out.println("computeCrit VBPInitial 0 " + MoreArray.toString(VBPInitial[0], ","));

                        } catch (Exception e) {
                            System.out.println("computeCrit VBPInitial 0 is NULL " + label);
                            e.printStackTrace();
                        }
                        try {
                            System.out.println("computeCrit VBPInitial 1 " + MoreArray.toString(VBPInitial[1], ","));
                        } catch (Exception e) {
                            System.out.println("computeCrit VBPInitial 1 is NULL " + label);
                            e.printStackTrace();
                        }*/
                    }

                    double[] critsraw = null;
                    //get R criteria values, skip if PPI or TF
                    if (CRIT_MASTER_IND != 166 && CRIT_MASTER_IND != 167 &&
                            CRIT_MASTER_IND != 41 && CRIT_MASTER_IND != 40 &&
                            CRIT_MASTER_IND != -10) {
                        //System.out.print(".");
                        if (debug)
                            System.out.println("R: Ic<-c(" + MoreArray.toString(VBPInitial[0], ",") + ")");
                        Rengine.assign("Ic", VBPInitial[0]);
                        if (debug)
                            System.out.println("R: Jc<-c(" + MoreArray.toString(VBPInitial[1], ",") + ")");
                        Rengine.assign("Jc", VBPInitial[1]);
                        if (debug)
                            System.out.println("R: nullRegData <- NULL");
                        Rengine.eval("nullRegData <- NULL");

                        int onlymean = 0;
                        int arrayInd = -1;
                        try {
                            arrayInd = MoreArray.getArrayInd(MINER_STATIC.MEANCritAll, CRIT_MASTER_IND);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (arrayInd != -1)
                            onlymean = 1;

                        String rcall = ComputeCrit.buildCritCallTotal(neednull,
                                onlymean == 1 ? true : false, onlymean, criterion);
                        if (debug)
                            System.out.println("R: " + rcall);

                        int tried = 0;
                        while (tried < 5) {
                            if (tried > 0)
                                System.out.println("attempt genes " + i + "\texps " + j + "\t" + tried + "\t" + rcall);
                            Rexpr = Rengine.eval(rcall);
                            try {
                                RList rl = Rexpr.asList();
                                critsraw = (rl.at(1)).asDoubleArray();
                                break;
                            } catch (Exception e) {
                                /*System.out.println("R: " + rcall);
                             System.out.println("Ic " + Rengine.eval("Ic"));
                             System.out.println("Jc " + Rengine.eval("Jc"));
                             System.out.println("Ic len " + Rengine.eval("length(Ic)"));
                             System.out.println("Jc len " + Rengine.eval("length(Jc)"));
                             //System.out.println("dim " + Rengine.eval("dim(expr_data[Ic,Jc])"));
                             System.out.println("colSums " + Rengine.eval("colSums(!is.na(expr_data[Ic,Jc]))/length(Ic)"));
                             System.out.println("rowSums " + Rengine.eval("rowSums(!is.na(expr_data[Ic,Jc]))/length(Jc)"));
                             System.out.println("var " + Rengine.eval("var(expr_data[Ic,Jc])"));*/
                                //MoreArray.printArray(VBPInitial.exp_data);
                                if (tried == 4) {
                                    debugCritError(rcall, e);
                                }
                            }
                            tried++;
                        }

                        if (critsraw == null || MoreArray.hasNaN(critsraw)) {
                            moreDebugCrit(rcall);
                        }
                    }

                    //MoreArray.printArray(critsraw);
                    if (critsraw == null)
                        critsraw = new double[ValueBlock_STATIC.NUM_CRIT];

                    //System.out.println("assignCrit.CRITTF " + CRIT_IND + "\t" + assignCrit.CRITTF[CRIT_IND]);
                    if (assignCrit.CRITTF[CRIT_IND] == 1) {
                        critsraw = critMaxTF(VBPInitial, critsraw);
                    } else if (CRIT_MASTER_IND == 41 || CRIT_MASTER_IND == 40) {
                        critsraw = critInter(VBPInitial, critsraw);
                    } else if (CRIT_MASTER_IND == 166 || CRIT_MASTER_IND == 167) {
                        critsraw = critFeat(VBPInitial, critsraw);
                    }
                    //System.out.println("CRIT_IND " + CRIT_IND + "\t" + CRITTF[CRIT_IND] + "\tTF crit " + critsraw[5]);

                    String label1 = null;
                    try {
                        label1 = MINER_STATIC.CRIT_LABELS[CRIT_MASTER_IND - 1];
                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
                    if (debug) {
                        System.out.println("criterion wo null " + CRIT_IND + "\t" + label1);
                        MoreArray.printArray(critsraw);
                    }

                    boolean[] passcrits = null;

                    if (CRIT_IND > 0) {
                        passcrits = Criterion.getExprCritTypes(criterion, true,
                                assignCrit.CRITmean[CRIT_IND] == 1 ? true : false, debug);
                    } else {
                        passcrits = Criterion.getExprCritTypes(criterion, false, false, debug);
                    }

                    //compute weighted full criterion

                    boolean weigh = true;
                    if (label1 != null && label1.toLowerCase().contains("Binary".toLowerCase()))
                        weigh = false;

                    if (debug)
                        System.out.println("computeCrit " + label1 + "\t" + weigh);

                    double fullcrit = ValueBlock.computeFullCrit(critsraw, weigh, passcrits, debug);
                    if (debug) {
                        System.out.println("fullcrit " + MoreArray.toString(critsraw, ",") + "\t=\t" + fullcrit);
                        System.out.println("passcrits " + MoreArray.toString(passcrits, ","));
                    }

                    int posFull = ValueBlockListMethods.findPosFull(topNlist, fullcrit, MAX_TOPLIST_LEN);
                    //System.out.println("posFull " + posFull);
                    if (posFull != -1) {
                        ValueBlock VBPInitialblock = new ValueBlock(VBPInitial[0], VBPInitial[1]);
                        VBPInitialblock.updateCrit(critsraw, true, passcrits, debug);
                        if (posFull < topNlist.size() || topNlist.size() == 0) {
                            topNlist.add(posFull, VBPInitialblock);
                            //System.out.println("posFull added " + topNlist.size() + "\t" + fullcrit);
                        } else if (topNlist.size() < MAX_TOPLIST_LEN)
                            topNlist.add(VBPInitialblock);
                        while (topNlist.size() > MAX_TOPLIST_LEN) {
                            int i1 = topNlist.size() - 1;
                            //ValueBlock cur = (ValueBlock) topNlist.get(i1);
                            topNlist.remove(i1);
                            //System.out.println("posFull removed " + topNlist.size() + "\t" + cur.full_criterion);
                        }
                    }
                    String key = "" + ns + "_" + ecount + "_" + gcount;
                    if (ecount > maxecount)
                        maxecount = ecount;
                    if (gcount > maxgcount)
                        maxgcount = gcount;

                    critvalmapExpCrit.put(key, fullcrit);
                    if (debug)
                        System.out.println("critvalmap key " + key + "\t" + critvalmapExpCrit.size() + "\t" + fullcrit);
                }
                if (i + expsampling[i - expmin] >= expmax && !endexp && !isSpecialCrit) {
                    i = expmax;
                    endexp = true;
                } else
                    i += expsampling[i - expmin];

                ecount++;
            }
            if (j + genesampling[j - genemin] >= genemax && !endgene) {
                j = genemax;
                endgene = true;
            } else
                j += genesampling[j - genemin];
            gcount++;
        }
        System.out.println();
        System.out.println("maxgcount " + maxgcount + "\tgenesteps_ind " + genesteps_ind.length);
        System.out.println("maxecount " + maxecount + "\texpsteps_ind " + expsteps_ind.length);
    }

    /**
     * @param rcall
     */
    private void moreDebugCrit(String rcall) {
        System.out.println("R: " + rcall);
        try {
            System.out.println("eval " + Rengine.eval(rcall));
        } catch (Exception e) {
            //e1.printStackTrace();
            System.out.println("eval output is null");
        }
        try {
            System.out.println("Rexpr.asList " + Rexpr.asList().toString());
        } catch (Exception e) {
            //e1.printStackTrace();
            System.out.println("Rexpr.asList output is null");
        }
        System.out.println("Ic " + Rengine.eval("Ic"));
        System.out.println("Jc " + Rengine.eval("Jc"));
        System.out.println("Ic len " + Rengine.eval("length(Ic)"));
        System.out.println("Jc len " + Rengine.eval("length(Jc)"));

        //System.out.println("dim " + Rengine.eval("dim(expr_data[Ic,Jc])"));
        System.out.println("colSums " + Rengine.eval("colSums(!is.na(expr_data[Ic,Jc]))/length(Ic)"));
        System.out.println("rowSums " + Rengine.eval("rowSums(!is.na(expr_data[Ic,Jc]))/length(Jc)"));
        System.out.println("missing " + Rengine.eval("sum(is.na(expr_data[Ic,Jc]))"));
        System.out.println("var " + Rengine.eval("var(expr_data[Ic,Jc])"));
        System.out.println("data " + Rengine.eval("expr_data[Ic,Jc]"));

        if (feat_data != null) {
            try {
                //System.out.println("dim " + Rengine.eval("dim(feat_data[Ic,Jc])"));
                System.out.println("colSums " + Rengine.eval("colSums(!is.na(feat_data[Ic,Jc]))/length(Ic)"));
                System.out.println("rowSums " + Rengine.eval("rowSums(!is.na(feat_data[Ic,Jc]))/length(Jc)"));
                System.out.println("missing " + Rengine.eval("sum(is.na(feat_data[Ic,Jc]))"));
                System.out.println("var " + Rengine.eval("var(feat_data[Ic,Jc])"));
                System.out.println("data " + Rengine.eval("feat_data[Ic,Jc]"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /*if (inter_data != null) {
            try {
                //System.out.println("dim " + Rengine.eval("dim(interact_data[Ic,Jc])"));
                System.out.println("colSums " + Rengine.eval("colSums(!is.na(interact_data[Ic,Jc]))/length(Ic)"));
                System.out.println("rowSums " + Rengine.eval("rowSums(!is.na(interact_data[Ic,Jc]))/length(Jc)"));
                System.out.println("missing " + Rengine.eval("sum(is.na(interact_data[Ic,Jc]))"));
                System.out.println("var " + Rengine.eval("var(interact_data[Ic,Jc])"));
                System.out.println("data " + Rengine.eval("interact_data[Ic,Jc]"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/

    }

    /**
     * @param VBPInitial
     * @param critsraw
     * @return
     */
    private double[] critMaxTF(int[][] VBPInitial, double[] critsraw) {
        if (TFtargetmap != null) {
            double pass = TFCrit.getTF8merRank(VBPInitial[0], TFtargetmap, gene_labels, debug);
            //System.out.println("computeCritSpecial MaxTF " + pass);
            critsraw[ValueBlock_STATIC.TF_IND] = pass;
        }
        return critsraw;
    }

    /**
     * @param VBPInitial
     * @param critsraw
     * @return
     */
    private double[] critInter(int[][] VBPInitial, double[] critsraw) {
        if (debug) {
            System.out.println("R: Ic<-c(" + MoreArray.toString(VBPInitial[0], ",") + ")");
            System.out.println("R: Jc<-c(" + MoreArray.toString(VBPInitial[1], ",") + ")");
        }
        Rengine.assign("Ic", VBPInitial[0]);
        Rengine.assign("Jc", VBPInitial[1]);
        /*if (debug)
            System.out.println("R: Jc<-c(" + MoreArray.toString(VBPInitial[1], ",") + ")");
        Rengine.assign("Jc", VBPInitial[1]);*/

        Rengine.eval("nullRegData <- NULL");

        int onlymean = 0;
        int arrayInd = 0;
        try {
            arrayInd = MoreArray.getArrayInd(MINER_STATIC.MEANCrit, CRIT_MASTER_IND);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (arrayInd != -1)
            onlymean = 1;
        String rcall = ComputeCrit.buildCritCallTotal(neednull, false, onlymean, criterion);

        if (debug)
            System.out.println("R: " + rcall);
        Rexpr = Rengine.eval(rcall);

        try {
            RList rl = Rexpr.asList();
            critsraw = (rl.at(1)).asDoubleArray();
            /*if (debug) {
                System.out.println("criterion wo null " + CRIT_MASTER_IND + "\t" +
                        MINER_STATIC.CRIT_LABELS[CRIT_MASTER_IND - 1]);
                MoreArray.printArray(critsraw);
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        return critsraw;
    }

    /**
     * @param VBPInitial
     * @param critsraw
     * @return
     */
    private double[] critFeat(int[][] VBPInitial, double[] critsraw) {
      /*  if (debug)
            System.out.println("R: Ic<-c(" + MoreArray.toString(VBPInitial[0], ",") + ")");
        Rengine.assign("Ic", VBPInitial[0]);
        *//*if (debug)
            System.out.println("R: Jc<-c(" + MoreArray.toString(VBPInitial[1], ",") + ")");
        Rengine.assign("Jc", VBPInitial[1]);*//*

        Rengine.eval("nullRegData <- NULL");

        int onlymean = 0;
        int arrayInd = 0;
        try {
            arrayInd = MoreArray.getArrayInd(MINER_STATIC.MEANCrit, CRIT_MASTER_IND);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (arrayInd != -1)
            onlymean = 1;
        String rcall = ComputeCrit.buildCritCallTotal(false, false, onlymean, criterion);

        if (debug)
            System.out.println("R: " + rcall);
        Rexpr = Rengine.eval(rcall);

        try {
            RList rl = Rexpr.asList();
            critsraw = (rl.at(1)).asDoubleArray();
            *//*if (debug) {
                System.out.println("criterion wo null " + CRIT_MASTER_IND + "\t" +
                        MINER_STATIC.CRIT_LABELS[CRIT_MASTER_IND - 1]);
                MoreArray.printArray(critsraw);
            }*//*
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        if (critsraw == null)
            critsraw = new double[9];

       /* System.out.println("VBPInitial[0]");
        MoreArray.printArray(VBPInitial[0]);

        System.out.println("critsraw");
        MoreArray.printArray(critsraw);*/

        critsraw = ComputeCrit.computeFeat(critsraw, VBPInitial[0], feat_data.data, debug);

        return critsraw;
    }

    /**
     * @param useMedianAndIQR
     * @return
     */
    private double[] meanAndSDExpCritChunked(boolean useMedianAndIQR) {
        Set set = critvalmapExpCrit.keySet();
        Iterator iter = set.iterator();
        //System.out.println("keys " + set.size());
        int count = 0;
        //ArrayList[][] critvals = MoreArray.initArrayList(expsteps_ind.length, genesteps_ind.length);
        ArrayList critvals = new ArrayList();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            //System.out.println("start " + key + "\t" + critvalmapExpCrit.get(key));
            ArrayList ind = StringUtil.locateOccurtoList(key, "_");

            /*if (debug) {
                int[] data = MoreArray.ArrayListtoInt(ind);
                System.out.println("meanAndSDExpCrit " + key + "\t" + data.length);
                MoreArray.printArray(data);
            }*/
            int first = (Integer) ind.get(0);
            int second = (Integer) ind.get(1);
            int y = Integer.parseInt(key.substring(first + 1, second));
            int x = Integer.parseInt(key.substring(second + 1));
            Double aDouble = (Double) critvalmapExpCrit.get(key);
            if (debug) {
                System.out.println("meanAndSDExpCrit " + y + "_" + x + "\t" + count + "\t" + aDouble);
            }
            count++;
            try {
                critvals.add(aDouble);
            } catch (Exception e) {
                System.out.println("meanAndSDExpCrit expsteps " + expsteps_ind.length +
                        "\tgenesteps " + genesteps_ind.length + "\tcritvals " +
                        "\texp " + y + "\tgene " + x);
                e.printStackTrace();
            }
        }

        double[] ret = new double[2];

            /*String f = outf + "_" + curCRITlabel + "_vals.txt";
            System.out.println("meanAndSDExpCrit attempting to write value file " + f);
            TabFile.write(critvals, f, "\t");*/

        double mean = Double.NaN;
        double sd = Double.NaN;

        if (debug)
            System.out.println("meanAndSDExpCrit expsteps" + expsteps_ind.length +
                    "\tgenesteps " + genesteps_ind.length + "\tcritvals ");

        for (int j = 0; j < expsteps_ind.length; j++) {
            for (int i = 0; i < genesteps_ind.length; i++) {
                if (debug) {
                    System.out.println("meanAndSDExpCrit median exp " + i + "\tgene " + j + "\t" + genesteps_ind[i] + "\t" + expsteps_ind[j]);
                }

                if (!useMedianAndIQR) {
                    mean = stat.avgABSOverSamp(MoreArray.ArrayListtoDouble(critvals), num_samples);
                    sd = stat.SDOverSample(MoreArray.ArrayListtoDouble(critvals), mean, num_samples);
                } else {
                    double[] vals = MoreArray.ArrayListtoDouble(critvals);
                    if (debug)
                        try {
                            System.out.println("R: vals<-c(" + MoreArray.toString(vals, ",") + ")");
                        } catch (Exception e) {
                            System.out.println("meanAndSDExpCrit i " + i + "\tj " + j);
                            e.printStackTrace();
                        }
                    Rengine.assign("vals", vals);

                    String rcall1 = "median(vals)";
                    if (debug)
                        System.out.println("R: " + rcall1);
                    Rexpr = Rengine.eval(rcall1);
                    mean = Rexpr.asDouble();

                    String rcall = "IQR(vals)";
                    if (debug)
                        System.out.println("R: " + rcall);
                    Rexpr = Rengine.eval(rcall);
                    try {
                        sd = Rexpr.asDouble();
                    } catch (Exception e) {
                        System.out.println("meanAndSDExpCrit mean[j][i] " + j + "\t" + i + "\t" + mean);
                        System.out.println(Rexpr = Rengine.eval("vals"));
                        e.printStackTrace();
                    }
                }
                if (debug)
                    System.out.println("meanAndSDExpCrit median exp " + i + "\tgene " + j + "\t" + mean + "\tsd " + sd);
            }
        }
        ret[0] = mean;
        ret[1] = sd;

        return ret;
    }

    /**
     * @param useMedianAndIQR
     * @return
     */
    private ArrayList meanAndSDExpCrit(boolean useMedianAndIQR) {
        Set set = critvalmapExpCrit.keySet();
        Iterator iter = set.iterator();
        //System.out.println("keys " + set.size());
        int count = 0;
        ArrayList[][] critvals = MoreArray.initArrayList(expsteps_ind.length, genesteps_ind.length);
        while (iter.hasNext()) {
            String key = (String) iter.next();
            //System.out.println("start " + key + "\t" + critvalmapExpCrit.get(key));
            ArrayList ind = StringUtil.locateOccurtoList(key, "_");

            /*if (debug) {
                int[] data = MoreArray.ArrayListtoInt(ind);
                System.out.println("meanAndSDExpCrit " + key + "\t" + data.length);
                MoreArray.printArray(data);
            }*/
            int first = (Integer) ind.get(0);
            int second = (Integer) ind.get(1);
            int y = Integer.parseInt(key.substring(first + 1, second));
            int x = Integer.parseInt(key.substring(second + 1));
            Double aDouble = (Double) critvalmapExpCrit.get(key);
            if (debug) {
                System.out.println("meanAndSDExpCrit " + y + "_" + x + "\t" + count + "\t" + aDouble);
            }
            count++;
            try {
                critvals[y][x].add(aDouble);
            } catch (Exception e) {
                System.out.println("meanAndSDExpCrit expsteps " + expsteps_ind.length +
                        "\tgenesteps " + genesteps_ind.length + "\tcritvals " +
                        critvals.length + "\t" + critvals[0].length +
                        "\texp " + y + "\tgene " + x);
                e.printStackTrace();
            }
        }

        //test
        int passed = 0;
        for (int i = 0; i < critvals.length; i++) {
            for (int j = 0; j < critvals[0].length; j++) {
                if (critvals[i][j].size() > 0)
                    passed++;
                else {
                    System.out.println("WARNING meanAndSDExpCrit no values " + i + "\t" + j);
                }
            }
        }

        ArrayList ret = new ArrayList();

        if (passed > 0) {
            String f = outf + "_" + curCRITlabel + "_vals.txt";
            System.out.println("meanAndSDExpCrit attempting to write value file " + f);
            TabFile.write(critvals, f, "\t");

            double[][] mean = new double[expsteps_ind.length][genesteps_ind.length];
            double[][] sd = new double[expsteps_ind.length][genesteps_ind.length];

            System.out.println("meanAndSDExpCrit expsteps" + expsteps_ind.length +
                    "\tgenesteps " + genesteps_ind.length + "\tcritvals " +
                    critvals.length + "\t" + critvals[0].length + "\tmean " + mean.length + "\t" + mean[0].length);

            for (int j = 0; j < expsteps_ind.length; j++) {
                for (int i = 0; i < genesteps_ind.length; i++) {
                    if (debug) {
                        System.out.println("meanAndSDExpCrit median exp " + i + "\tgene " + j + "\t" + genesteps_ind[i] + "\t" + expsteps_ind[j]);
                        System.out.println("meanAndSDExpCrit critvals[j][i] " + critvals[j][i].size());
                    }

                    if (!useMedianAndIQR) {
                        mean[j][i] = stat.avgABSOverSamp(MoreArray.ArrayListtoDouble(critvals[j][i]), num_samples);
                        sd[j][i] = stat.SDOverSample(MoreArray.ArrayListtoDouble(critvals[j][i]), mean[j][i], num_samples);
                    } else {
                        double[] vals = MoreArray.ArrayListtoDouble(critvals[j][i]);
                        if (debug)
                            try {
                                System.out.println("R: vals<-c(" + MoreArray.toString(vals, ",") + ")");
                            } catch (Exception e) {
                                System.out.println("meanAndSDExpCrit i " + i + "\tj " + j);
                                e.printStackTrace();
                            }
                        Rengine.assign("vals", vals);

                        String rcall1 = "median(vals)";
                        if (debug)
                            System.out.println("R: " + rcall1);
                        Rexpr = Rengine.eval(rcall1);
                        mean[j][i] = Rexpr.asDouble();

                        String rcall = "IQR(vals)";
                        if (debug)
                            System.out.println("R: " + rcall);
                        Rexpr = Rengine.eval(rcall);
                        try {
                            sd[j][i] = Rexpr.asDouble();
                        } catch (Exception e) {
                            System.out.println("meanAndSDExpCrit mean[j][i] " + j + "\t" + i + "\t" + mean[j][i]);
                            System.out.println(Rexpr = Rengine.eval("vals"));
                            e.printStackTrace();
                        }
                    }
                    if (debug)
                        System.out.println("meanAndSDExpCrit median exp " + i + "\tgene " + j + "\t" + mean[j][i] + "\tsd " + sd[j][i]);

                }
            }

            ret.add(mean);
            ret.add(sd);
        } else {
            ret = null;
        }
        return ret;
    }

    /**
     * @param useMedianAndIQR
     * @return
     */
    private double[] meanAndSDExpCritSpecialChunked(boolean useMedianAndIQR) {
        Set set = critvalmapExpCrit.keySet();
        Iterator iter = set.iterator();
        //System.out.println("keys " + set.size());
        int count = 0;
        ArrayList critvals = new ArrayList();//MoreArray.initArrayListArray(genesteps_ind.length);
        while (iter.hasNext()) {
            String key = (String) iter.next();
            //System.out.println("start " + key + "\t" + critvalmapExpCrit.get(key));
            ArrayList ind = StringUtil.locateOccurtoList(key, "_");
                /*if (debug) {
                    int[] data = MoreArray.ArrayListtoInt(ind);
                    System.out.println("meanAndSDExpCritSpecial " + key + "\t" + data.length);
                    MoreArray.printArray(data);
                }*/
            //int first = (Integer) ind.get(0);
            int second = (Integer) ind.get(1);
            int val = Integer.parseInt(key.substring(second + 1));
            int x = val;//maxgcount - val;
            Double aDouble = (Double) critvalmapExpCrit.get(key);
            if (debug) {
                System.out.println("meanAndSDExpCritSpecial x " + x + "\tval " + val + "\tmaxgcount " + maxgcount +
                        "\tcount " + count + "\taDouble " + aDouble);
            }
            count++;
            try {
                critvals.add(aDouble);
            } catch (Exception e) {
                System.out.println("meanAndSDExpCritSpecial genesteps " + genesteps_ind.length +
                        "\tgene " + x);
                e.printStackTrace();
            }
        }

       /* String f = outf + "_" + curCRITlabel + "_vals.txt";
        System.out.println("meanAndSDExpCritSpecial attempting to write value file " + f);
        TabFile.write(critvals, f, "\t");*/

        double mean = Double.NaN, sd = Double.NaN;
        System.out.println("meanAndSDExpCritSpecial genesteps " + genesteps_ind.length);

        if (!useMedianAndIQR) {
            double[] vals = MoreArray.ArrayListtoDouble(critvals);
            mean = stat.avgABSOverSamp(vals, vals.length);
            sd = stat.SDOverSample(vals, mean, vals.length);
        } else {
            double[] vals = MoreArray.ArrayListtoDouble(critvals);
            if (debug)
                try {
                    System.out.println("R: vals<-c(" + MoreArray.toString(vals, ",") + ")");
                } catch (Exception e) {
                    System.out.println("meanAndSDExpCritSpecial curCRITlabel " + CRIT_IND + "\t" + CRIT_MASTER_IND);
                    e.printStackTrace();
                }
            Rengine.assign("vals", vals);

            String rcall1 = "median(vals)";
            if (debug)
                System.out.println("R: " + rcall1);
            Rexpr = Rengine.eval(rcall1);
            mean = Rexpr.asDouble();


            String rcall = "IQR(vals)";
            if (debug)
                System.out.println("R: " + rcall);
            Rexpr = Rengine.eval(rcall);
            sd = Rexpr.asDouble();
        }


        double[] ret = new double[2];
        ret[0] = mean;
        ret[1] = sd;
        return ret;
    }

    /**
     * @param useMedianAndIQR
     * @return
     */
    private ArrayList meanAndSDExpCritSpecial(boolean useMedianAndIQR) {
        Set set = critvalmapExpCrit.keySet();
        Iterator iter = set.iterator();
        //System.out.println("keys " + set.size());
        int count = 0;
        ArrayList[] critvals = MoreArray.initArrayListArray(genesteps_ind.length);
        while (iter.hasNext()) {
            String key = (String) iter.next();
            //System.out.println("start " + key + "\t" + critvalmapExpCrit.get(key));
            ArrayList ind = StringUtil.locateOccurtoList(key, "_");
            /*if (debug) {
                int[] data = MoreArray.ArrayListtoInt(ind);
                System.out.println("meanAndSDExpCritSpecial " + key + "\t" + data.length);
                MoreArray.printArray(data);
            }*/
            int first = (Integer) ind.get(0);
            int second = (Integer) ind.get(1);
            int val = Integer.parseInt(key.substring(second + 1));
            int x = val;//maxgcount - val;
            Double aDouble = (Double) critvalmapExpCrit.get(key);
            if (debug) {
                System.out.println("meanAndSDExpCritSpecial x " + x + "\tval " + val + "\tmaxgcount " + maxgcount +
                        "\tcount " + count + "\taDouble " + aDouble);
            }
            count++;
            try {
                critvals[x].add(aDouble);
            } catch (Exception e) {
                System.out.println("meanAndSDExpCritSpecial genesteps " + genesteps_ind.length + "\tcritvals " +
                        critvals.length +
                        "\tgene " + x);
                e.printStackTrace();
            }
        }

        //test
        int passed = 0;
        for (int i = 0; i < critvals.length; i++) {
            //System.out.println("passed " + i + "\t" + critvals[i].size());
            if (critvals[i].size() > 0) {
                //System.out.println("critval " + i + "\t" + MoreArray.toString(MoreArray.ArrayListtoString(critvals[i]), ","));
                passed++;
            }
        }

        System.out.println("meanAndSDExpCritSpecial passed " + passed);
        ArrayList ret = new ArrayList();
        if (passed > 0) {
            //if (storeVals) {
            String f = outf + "_" + curCRITlabel + "_vals.txt";
            System.out.println("meanAndSDExpCritSpecial attempting to write value file " + f);
            TabFile.write(critvals, f, "\t");
            //}

            double[] mean = new double[genesteps_ind.length];
            double[] sd = new double[genesteps_ind.length];

            System.out.println("meanAndSDExpCritSpecial genesteps " + genesteps_ind.length + "\tcritvals " +
                    critvals.length + "\tmean " + mean.length);

            for (int i = 0; i < genesteps_ind.length; i++) {
                if (debug) {
                    System.out.println("meanAndSDExpCritSpecial critvals[i] " + i + "\tsize " + critvals[i].size());
                }

                if (!useMedianAndIQR) {
                    double[] vals = MoreArray.ArrayListtoDouble(critvals[i]);
                    mean[i] = stat.avgABSOverSamp(vals, vals.length);
                    sd[i] = stat.SDOverSample(vals, mean[i], vals.length);
                } else {
                    double[] vals = MoreArray.ArrayListtoDouble(critvals[i]);
                    if (debug)
                        System.out.println("meanAndSDExpCritSpecial i " + i + "\tvals " + MoreArray.toString(vals));
                    if (debug)
                        try {
                            System.out.println("R: vals<-c(" + MoreArray.toString(vals, ",") + ")");
                        } catch (Exception e) {
                            System.out.println("meanAndSDExpCritSpecial curCRITlabel " + CRIT_IND + "\t" + CRIT_MASTER_IND);
                            e.printStackTrace();
                        }
                    Rengine.assign("vals", vals);

                    String rcall1 = "median(vals)";
                    if (debug)
                        System.out.println("R: " + rcall1);
                    Rexpr = Rengine.eval(rcall1);
                    mean[i] = Rexpr.asDouble();


                    String rcall = "IQR(vals)";
                    if (debug)
                        System.out.println("R: " + rcall);
                    Rexpr = Rengine.eval(rcall);
                    sd[i] = Rexpr.asDouble();
                }
                if (debug)
                    System.out.println("meanAndSDExpCritSpecial median gene " + i + "\t" + mean[i] + "\tsd " + sd[i]);
            }

            ret.add(mean);
            ret.add(sd);
        } else {
            ret = null;
        }
        return ret;
    }


    /**
     *
     */

    private void initRandVar() {
        String[] R_args = {"--no-save"};
        System.out.println("RunMinerBack standard: starting Rengine");
        Rengine = new Rengine(R_args, false, new TextConsole());
        System.out.println("RunMinerBack standard: Rengine created, waiting for R");
        if (!Rengine.waitForR()) {
            System.out.println("RunMinerBack standard: Cannot load R");
            System.exit(1);
        } else {
            System.out.println("RunMinerBack standard: R started");
        }

        String s01 = "rm(list=ls())";
        System.out.println("R: " + s01);
        System.out.println(Rexpr = Rengine.eval(s01));

        //windows specific
       /* String s01a = "memory.limit(size=60000)";
        System.out.println("R: " + s01a);
        System.out.println(Rexpr = Rengine.eval(s01a));

        String s01aa = "memory.size(max=T)";
        System.out.println("R: " + s01aa);
        System.out.println(Rexpr = Rengine.eval(s01aa));

        String s01aaa = "memory.size(max=F)";
        System.out.println("R: " + s01aaa);
        System.out.println(Rexpr = Rengine.eval(s01aaa));*/

        String s02 = "source(\"" + Rcodepath + "\")";
        System.out.println("R: " + s02);
        System.out.println(Rexpr = Rengine.eval(s02));

        String s04 = "load(\"" + inputR + "\")";
        System.out.println("R: " + s04);
        System.out.println(Rexpr = Rengine.eval(s04));

        String s04test = "class(expr_data)";
        String testresult = (Rexpr = Rengine.eval(s04test)).toString();
        if (testresult.equals("data.frame")) {
            System.out.println("data object is a data.frame, should be matrix.");
            System.exit(0);
        }

        /*String s9 = "library(polspline)";
        System.out.println("R: " + s9);
        System.out.println(Rexpr = Rengine.eval(s9));*/

        //String s9b = "library(lars)";
        //System.out.println("R: " + s9b);
        //System.out.println(Rexpr = Rengine.eval(s9b));

        String s9c = "library(irr)";
        System.out.println("R: " + s9c);
        System.out.println(Rexpr = Rengine.eval(s9c));

        //String s9d = "library(geepack)";
        //System.out.println("R: " + s9d);
        //System.out.println(Rexpr = Rengine.eval(s9d));

        System.out.println(Rexpr = Rengine.eval("dim(expr_data)"));
        if (inputfeat != null)
            System.out.println(Rexpr = Rengine.eval("dim(feat_data)"));
        //if (inputinter != null)
        //    System.out.println(Rexpr = Rengine.eval("dim(inter_data)"));

        rand = new Random(seed);
        String s5 = "set.seed(" + seed + ")";
        System.out.println("R: " + s5);
        System.out.println("initRSeed SEED IS SPECIFIED " + s5 + "\t" + seed);
        System.out.println(Rexpr = Rengine.eval(s5));
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length >= 22 && args.length <= 36) {
            MakeNull rm = new MakeNull(args);
        } else {
            System.out.println("MakeNull version " + version);
            System.out.println("syntax: java DataMining.MakeNull\n" +
                    "<-source Miner.R>\n" +
                    "<-intxt input expr data txt>\n" +
                    "<-infeat OPTIONAL input feat data txt>\n" +
                    //"<-ininter OPTIONAL input interact data txt>\n" +
                    "<-inTF OPTIONAL TF target ranks>\n" +
                    "<-inR input data R>\n" +
                    "<-out outfile>\n" +
                    "<-gmin gene min>\n" +
                    "<-gmax gene max>\n" +
                    "<-emin exp min>\n" +
                    "<-emax exp max>\n" +
                    "<-nsamp num_samples>\n" +
                    "<-genes OPTIONAL gene labels>\n" +
                    "<-seed OPTIONAL random seed>\n" +
                    "<-abs OPTIONAL binary abs/noabs 1/0 default 0=noabs>\n" +
                    "<-chunked T/F>\n" +
                    "<-frxnsign T/F>\n" +
                    "<-crits MAK bicluster coherence criterion>\n" +
                    "<-debug OPTIONAL>"
            );
        }
    }
}