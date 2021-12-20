package DataMining;

import util.MoreArray;
import util.StringUtil;
import util.TextFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * User: marcinjoachimiak
 * Date: May 31, 2007                                                                              f
 * Time: 11:23:29 PM
 */
public class Parameters {

    public int debug = MINER_STATIC.DEFAULT_DEBUG;

    public java.lang.String EXPR_DATA_PATH, INTERACT_DATA_PATH, FEAT_DATA_PATH,
            R_DATA_PATH, MOVES_PATH = "", param_path,
            R_METHODS_PATH, OUTDIR = "", OUTPREFIX, ANNOTATION_PATH, TFTARGETMAP_PATH;

    //The current code uses the median instead of mean and 0.5IQR instead of sd, so variable names are not exactly reflective.
    public String MEANKEND_PATH, MEANKENDR_PATH, MEANKENDC_PATH, MEANCORR_PATH, MEANCORC_PATH, MEANMADR_PATH, MEANMSE_PATH, MEANMSER_PATH, MEANMSEC_PATH, MEANINTERACT_PATH,
            MEANGEE_PATH, MEANGEERE_PATH, MEANGEECE_PATH, MEANLARSRE_PATH, MEANLARSCE_PATH, MEANCOR_PATH, MEANLARS_PATH, MEANEUC_PATH, MEANSPEAR_PATH,
            SDMADR_PATH, SDMSE_PATH, SDMSER_PATH, SDMSEC_PATH, SDKEND_PATH, SDKENDR_PATH, SDKENDC_PATH, SDCORR_PATH, SDCORC_PATH, SDINTERACT_PATH,
            SDGEE_PATH, SDGEERE_PATH, SDGEECE_PATH, SDLARSRE_PATH, SDLARSCE_PATH, MEANEUCR_PATH, MEANEUCC_PATH, SDEUCR_PATH, SDEUCC_PATH,
            MEANSPEARR_PATH, MEANSPEARC_PATH, SDSPEARR_PATH, SDSPEARC_PATH, MEANMEAN_PATH, SDMEAN_PATH,
            MEANRMEAN_PATH, SDRMEAN_PATH, MEANCMEAN_PATH, SDCMEAN_PATH, SDCOR_PATH, SDLARS_PATH, SDEUC_PATH, SDSPEAR_PATH,
            MEANFEAT_PATH, SDFEAT_PATH, MEANTF_PATH, SDTF_PATH, MEANBINARY_PATH, SDBINARY_PATH, MEANBINARYR_PATH, SDBINARYR_PATH, MEANBINARYC_PATH, SDBINARYC_PATH,
            TRAJECTORY_PATH, EXCLUDE_LIST_PATH;
    //##Required info
    public int N_BLOCK;// #Number of local minimum blocks user wishes to find
    public double NOISE_LEVEL;// #Permitted noise level for accepting move
    public double INITPA;// #initial probability of addition
    public double INITPG;// #initial probability of selecting a gene
    public double PA;// #probability of addition
    public double PG;// #probability of selecting a gene
    public double PBATCH; // #probability of a batch move

    public int SIZE_PRECRIT_LIST = -1;//number of moves to consider with precriteria exclusive with SIZE_PRECRIT_LIST_GENE&SIZE_PRECRIT_LIST_EXP
    public double SIZE_PRECRIT_LIST_GENE = -1;//number of moves to consider with precriteria or fraction of genes if 0 < x < 1
    public double SIZE_PRECRIT_LIST_EXP = -1;//number of moves to consider with precriteria or fraction of exps if 0 < x < 1
    public double fraction_genes_for_precritmove = 0.05;
    public double fraction_exps_for_precritmove = 0.05;

    public int[] MAXMOVES;//c(15, 30);//max number of moves to accept
    //#This is the defined minimum and maximum block_id size

    public int IMIN;// #minimum number of rows/genesStr in block_id
    public int JMIN;// #minimum number of columns/expsStr in block_id
    public int IMAX;//  #maximum number of rows/genesStr in block_id
    public int JMAX;//  #maximum number of columns/expsStr in block_id
    public int DATA_LEN_GENES;
    public int DATA_LEN_EXPS;

    public double PERCENT_ALLOWED_MISSING_GENES;
    public double PERCENT_ALLOWED_MISSING_EXP;
    public double PERCENT_ALLOWED_MISSING_IN_BLOCK;

    //public boolean all_features;
    public boolean TRUNCATE_DATA100;

    /*The three randomize options are mutually exclusive.*/
    public boolean RANDOMIZE_BLOCKS;
    public boolean RANDOMIZE_EXPS;
    public boolean RANDOMIZE_GENES;
    /* FIX_GENES is exclusive with RANDOMIZE_GENES*/
    public boolean FIX_GENES;
    /* FIX_EXPS is exclusive with RANDOMIZE_GENES*/
    public boolean FIX_EXPS;

    /*Toggle for use of random seed.*/
    public boolean USE_RANDOM_SEED;
    public long RANDOM_SEED;
    public long DEFAULT_RANDOM_SEED = 123456;

    public String BATCH_DMETHOD = "correlation";
    public String BATCH_LINKMETHOD = "complete";

    public ArrayList[] INIT_BLOCKS;//NULL;//  #sets of initialization blocks for algorithm
    /*TODO: implement initial list of blocks*/
    public ArrayList init_block_list;

    /* Output parameters*/
    public boolean use_checksum_prefix = true;

    final static int default_crit_type_index = 4;
    final static int default_precrit_type_index = 4;
    public int CRIT_TYPE_INDEX = default_crit_type_index;
    public int PRECRIT_TYPE_INDEX = default_precrit_type_index;

    public Criterion crit, precrit;


    public ArrayList FEATURE_INDICES;//ArrayList of integers specifying which feature set to use, 0 means no FEATURES.
    public HashMap feature_map;

    public boolean PLATEAU = false;
    public boolean RANDOMFLOOD = false;
    public boolean batch = false;

    public long CHECKSUM_SEED;
    public String move_class = "sesi";

    public boolean GENE_SHAVE;
    public boolean EXP_SHAVE;
    public boolean GENE_GROW;
    public boolean EXP_GROW;

    //public int lars_max_steps;

    public double RANDOMFLOOD_PERC = MINER_STATIC.DEFAULT_randomflood_percentage;
    public double BATCH_PERC = MINER_STATIC.DEFAULT_batch_percentage;
    public double PLATEAU_PERC = BATCH_PERC;

    public boolean WEIGH_EXPR = false;
    public boolean USE_MEAN = false;
    public boolean USE_ABS = false;
    public boolean FRXN_SIGN = false;
    public boolean OVERRIDE_SHAVING = false;
    //Three binary integers indicating abs/noabs for MSE/Kendall/GEE.
    //The middle value for 'Kendall' also is applied to Pearson correlation and Euclidean.
    public int[] USE_ABS_AR = null;
    public int[] USE_NULL_AR = null;

    public String RUN_SEQUENCE = "BS";
    public String LIB_LOC = null;

    public double EXCLUDE_OVERLAP_THRESHOLD = 1;//0.75;

    public static int MIN_NONMISSING_FOR_BATCH = MINER_STATIC.DEFAULT_MIN_NONMISSING_FOR_BATCH;

    public HashMap parameters_hash = new HashMap();
    public boolean[] needinv;
    public boolean[] neednull;

    public String precrit_type = "MSE";

   /* public static String[] PARAMETER_LABELS = {
            "OUTDIR",
            "OUTPREFIX",
            "EXPR_DATA_PATH",
            "INTERACT_DATA_PATH",
            "FEAT_DATA_PATH",
            "R_DATA_PATH",
            "MOVES_PATH",
            *//*TODO change KEND to KENDALL *//*
            "MEANKEND_PATH",
            "MEANKENDR_PATH",
            "MEANKENDC_PATH",

            "MEANCORR_PATH",
            "MEANCORC_PATH",
            "MEANMSE_PATH",
            "MEANMSER_PATH",
            "MEANMSEC_PATH",
            "MEANGEE_PATH",
            "MEANGEERE_PATH",
            "MEANGEECE_PATH",
            "MEANLARSRE_PATH",
            "MEANLARSCE_PATH",
            "MEANEUCR_PATH",
            "MEANEUCC_PATH",
            "MEANSPEARR_PATH",
            "MEANSPEARC_PATH",
            "MEANINTERACT_PATH",
            "MEANMEAN_PATH",
            "MEANRMEAN_PATH",
            "MEANCMEAN_PATH",
            "MEANFEAT_PATH",
            "MEANTF_PATH",
            "MEANCOR_PATH",
            "MEANLARS_PATH",
            "MEANEUC_PATH",
            "MEANSPEAR_PATH",

            "SDKENDALL_PATH",
            "SDKENDALLR_PATH",
            "SDKENDALLC_PATH",
            "SDCORR_PATH",
            "SDCORC_PATH",
            "SDMADR_PATH",
            "SDMSE_PATH",
            "SDMSER_PATH",
            "SDMSEC_PATH",
            "SDGEE_PATH",
            "SDGEERE_PATH",
            "SDGEECE_PATH",
            "SDLARSRE_PATH",
            "SDLARSCE_PATH",
            "SDEUCR_PATH",
            "SDEUCC_PATH",
            "SDSPEARR_PATH",
            "SDSPEARC_PATH",
            "SDINTERACT_PATH",
            "SDFEAT_PATH",
            "SDTF_PATH",
            "SDCOR_PATH",
            "SDLARS_PATH",
            "SDEUC_PATH",
            "SDSPEAR_PATH",

            "TRAJECTORY_PATH",
            "R_METHODS_PATH",
            "ANNOTATION_PATH",
            "TFTARGETMAP_PATH",
            "N_BLOCK",
            "INIT_BLOCKS",
            "NOISE_LEVEL",

            "INITPA",
            "INITPG",
            "PA",
            "PG",

            "MAXMOVES",
            "SIZE_PRECRIT_LIST",
            "SIZE_PRECRIT_LIST_GENE",
            "SIZE_PRECRIT_LIST_EXP",

            "IMIN",
            "IMAX",
            "JMIN",
            "JMAX",

            "DATA_LEN_GENES",
            "DATA_LEN_EXPS",
            "PERCENT_ALLOWED_MISSING_GENES",
            "PERCENT_ALLOWED_MISSING_EXP",
            "PERCENT_ALLOWED_MISSING_IN_BLOCK",
            "TRUNCATE_DATA100",

            "RANDOMIZE_BLOCKS",
            "RANDOMIZE_GENES",
            "RANDOMIZE_EXPS",
            "FIX_GENES",
            "FIX_EXPS",
            "USE_RANDOM_SEED",
            "RANDOM_SEED",

            "BATCH_DMETHOD",
            "BATCH_LINKMETHOD",

            "CRIT_TYPE_INDEX",
            "PRECRIT_TYPE_INDEX",
            "FEATURE_INDICES",
            "GENE_SHAVE",
            "EXP_SHAVE",
            "GENE_GROW",
            "EXP_GROW",
            "WEIGH_EXPR",
            "USE_MEAN",
            "USE_ABS",
            "FRXN_SIGN",
            "USE_ABS_AR",
            "USE_NULL_AR",
            "OVERRIDE_SHAVING",

            "PLATEAU",
            "PLATEAU_PERC",
            "RANDOMFLOOD",
            "RANDOMFLOOD_PERC",
            "PBATCH",
            "BATCH_PERC",
            "RUN_SEQUENCE",

            "LIB_LOC",
            "EXCLUDE_OVERLAP_THRESHOLD",
            "MIN_NONMISSING_FOR_BATCH",
            "PRECRIT_TYPE",
            "DEBUG",
            "CHECKSUM_SEED"
    };
*/

    /**
     *
     */
    public Parameters() {
        defaults();
    }

    /**
     * @param f
     */
    public Parameters(String f, boolean TemplateFile) throws FileNotFoundException {
        File test = new File(f);
        if (test.exists()) {
            defaults();
            read(f, TemplateFile);
        }
    }

    public Parameters(String f) throws FileNotFoundException {
        File test = new File(f);
        if (test.exists()) {
            defaults();
            read(f, false);
        }
    }

    /**
     * @param p
     */
    public Parameters(Parameters p) {

        debug = p.debug;

        EXPR_DATA_PATH = p.EXPR_DATA_PATH;
        INTERACT_DATA_PATH = p.INTERACT_DATA_PATH;
        FEAT_DATA_PATH = p.FEAT_DATA_PATH;
        R_DATA_PATH = p.R_DATA_PATH;
        MOVES_PATH = p.MOVES_PATH;
        param_path = p.param_path;
        R_METHODS_PATH = p.R_METHODS_PATH;
        //System.out.println("Parameters OUTDIR "+OUTDIR);
        OUTDIR = p.OUTDIR;
        //System.out.println("Parameters OUTDIR "+OUTDIR);
        OUTPREFIX = p.OUTPREFIX != null ? p.OUTPREFIX : "";
        ANNOTATION_PATH = p.ANNOTATION_PATH;
        TFTARGETMAP_PATH = p.TFTARGETMAP_PATH;

        MEANKEND_PATH = p.MEANKEND_PATH;
        MEANKENDR_PATH = p.MEANKENDR_PATH;
        MEANKENDC_PATH = p.MEANKENDC_PATH;
        MEANBINARY_PATH = p.MEANBINARY_PATH;
        MEANBINARYR_PATH = p.MEANBINARYR_PATH;
        MEANBINARYC_PATH = p.MEANBINARYC_PATH;
        MEANCORR_PATH = p.MEANCORR_PATH;
        MEANCORC_PATH = p.MEANCORC_PATH;
        MEANMADR_PATH = p.MEANMADR_PATH;
        MEANMSE_PATH = p.MEANMSE_PATH;
        MEANMSER_PATH = p.MEANMSER_PATH;
        MEANMSEC_PATH = p.MEANMSEC_PATH;
        MEANINTERACT_PATH = p.MEANINTERACT_PATH;
        MEANGEE_PATH = p.MEANGEE_PATH;
        MEANGEERE_PATH = p.MEANGEERE_PATH;
        MEANGEECE_PATH = p.MEANGEECE_PATH;
        MEANLARSRE_PATH = p.MEANLARSRE_PATH;
        MEANLARSCE_PATH = p.MEANLARSCE_PATH;
        MEANEUCR_PATH = p.MEANEUCR_PATH;
        MEANEUCC_PATH = p.MEANEUCC_PATH;
        MEANSPEARR_PATH = p.MEANSPEARR_PATH;
        MEANSPEARC_PATH = p.MEANSPEARC_PATH;
        MEANMEAN_PATH = p.MEANMEAN_PATH;
        MEANRMEAN_PATH = p.MEANRMEAN_PATH;
        MEANCMEAN_PATH = p.MEANCMEAN_PATH;
        MEANFEAT_PATH = p.MEANFEAT_PATH;
        MEANTF_PATH = p.MEANTF_PATH;
        MEANCOR_PATH = p.MEANCOR_PATH;
        MEANLARS_PATH = p.MEANLARS_PATH;
        MEANEUC_PATH = p.MEANEUC_PATH;
        MEANSPEAR_PATH = p.MEANSPEAR_PATH;

        SDKEND_PATH = p.SDKEND_PATH;
        SDKENDR_PATH = p.SDKENDR_PATH;
        SDKENDC_PATH = p.SDKENDC_PATH;
        SDBINARY_PATH = p.SDBINARY_PATH;
        SDBINARYR_PATH = p.SDBINARYR_PATH;
        SDBINARYC_PATH = p.SDBINARYC_PATH;
        SDCORR_PATH = p.SDCORR_PATH;
        SDCORC_PATH = p.SDCORC_PATH;
        SDMADR_PATH = p.SDMADR_PATH;
        SDMSE_PATH = p.SDMSE_PATH;
        SDMSER_PATH = p.SDMSER_PATH;
        SDMSEC_PATH = p.SDMSEC_PATH;
        SDINTERACT_PATH = p.SDINTERACT_PATH;
        SDGEE_PATH = p.SDGEE_PATH;
        SDGEERE_PATH = p.SDGEERE_PATH;
        SDGEECE_PATH = p.SDGEECE_PATH;
        SDLARSRE_PATH = p.SDLARSRE_PATH;
        SDLARSCE_PATH = p.SDLARSCE_PATH;
        SDEUCR_PATH = p.SDEUCR_PATH;
        SDEUCC_PATH = p.SDEUCC_PATH;
        SDSPEARR_PATH = p.SDSPEARR_PATH;
        SDSPEARC_PATH = p.SDSPEARC_PATH;
        SDMEAN_PATH = p.SDMEAN_PATH;
        SDRMEAN_PATH = p.SDRMEAN_PATH;
        SDCMEAN_PATH = p.SDCMEAN_PATH;
        SDFEAT_PATH = p.SDFEAT_PATH;
        SDTF_PATH = p.SDTF_PATH;
        SDCOR_PATH = p.SDCOR_PATH;
        SDLARS_PATH = p.SDLARS_PATH;
        SDEUC_PATH = p.SDEUC_PATH;
        SDSPEAR_PATH = p.SDSPEAR_PATH;

        TRAJECTORY_PATH = p.TRAJECTORY_PATH;
        EXCLUDE_LIST_PATH = p.EXCLUDE_LIST_PATH;

        N_BLOCK = p.N_BLOCK;
        NOISE_LEVEL = p.NOISE_LEVEL;
        INITPA = p.INITPA;
        INITPG = p.INITPG;//fixed BUG this was initpG = initpA, irrelevant if they are identical
        PA = p.PA;
        PG = p.PG;
        PBATCH = p.PBATCH;

        SIZE_PRECRIT_LIST = p.SIZE_PRECRIT_LIST;
        SIZE_PRECRIT_LIST_GENE = p.SIZE_PRECRIT_LIST_GENE;
        fraction_genes_for_precritmove = p.fraction_genes_for_precritmove;
        SIZE_PRECRIT_LIST_EXP = p.SIZE_PRECRIT_LIST_EXP;
        fraction_exps_for_precritmove = p.fraction_exps_for_precritmove;

        MAXMOVES = p.MAXMOVES;
        IMIN = p.IMIN;
        JMIN = p.JMIN;
        IMAX = p.IMAX;
        JMAX = p.JMAX;
        DATA_LEN_GENES = p.DATA_LEN_GENES;
        DATA_LEN_EXPS = p.DATA_LEN_EXPS;
        PERCENT_ALLOWED_MISSING_GENES = p.PERCENT_ALLOWED_MISSING_GENES;
        PERCENT_ALLOWED_MISSING_EXP = p.PERCENT_ALLOWED_MISSING_EXP;
        PERCENT_ALLOWED_MISSING_IN_BLOCK = p.PERCENT_ALLOWED_MISSING_IN_BLOCK;

        boolean b = TFTARGETMAP_PATH != null ? true : false;
        crit = new Criterion(p.crit, debug > 0 ? true : false);
        precrit = new Criterion(p.precrit, debug > 0 ? true : false);

        //all_features = p.all_features;
        TRUNCATE_DATA100 = p.TRUNCATE_DATA100;
        RANDOMIZE_BLOCKS = p.RANDOMIZE_BLOCKS;
        RANDOMIZE_EXPS = p.RANDOMIZE_EXPS;
        RANDOMIZE_GENES = p.RANDOMIZE_GENES;
        FIX_GENES = p.FIX_GENES;
        FIX_EXPS = p.FIX_EXPS;
        WEIGH_EXPR = p.WEIGH_EXPR;
        USE_MEAN = p.USE_MEAN;
        USE_ABS = p.USE_ABS;
        FRXN_SIGN = p.FRXN_SIGN;
        USE_ABS_AR = p.USE_ABS_AR;
        USE_NULL_AR = p.USE_NULL_AR;
        OVERRIDE_SHAVING = p.OVERRIDE_SHAVING;

        USE_RANDOM_SEED = p.USE_RANDOM_SEED;
        RANDOM_SEED = p.RANDOM_SEED;

        BATCH_DMETHOD = p.BATCH_DMETHOD;
        BATCH_LINKMETHOD = p.BATCH_LINKMETHOD;

        INIT_BLOCKS = p.INIT_BLOCKS;
        init_block_list = p.init_block_list;

        use_checksum_prefix = p.use_checksum_prefix;

        CRIT_TYPE_INDEX = p.CRIT_TYPE_INDEX;
        PRECRIT_TYPE_INDEX = p.PRECRIT_TYPE_INDEX;

        FEATURE_INDICES = MoreArray.copyArrayList(p.FEATURE_INDICES);
        feature_map = p.feature_map;

        PLATEAU = p.PLATEAU;
        RANDOMFLOOD = p.RANDOMFLOOD;

        CHECKSUM_SEED = p.CHECKSUM_SEED;
        move_class = p.move_class;

        GENE_SHAVE = p.GENE_SHAVE;
        EXP_SHAVE = p.EXP_SHAVE;

        GENE_GROW = p.GENE_GROW;
        GENE_GROW = p.GENE_GROW;

        RANDOMFLOOD_PERC = p.RANDOMFLOOD_PERC;
        BATCH_PERC = p.BATCH_PERC;
        PLATEAU_PERC = p.PLATEAU_PERC;
        RUN_SEQUENCE = p.RUN_SEQUENCE;
        LIB_LOC = p.LIB_LOC;
        EXCLUDE_OVERLAP_THRESHOLD = p.EXCLUDE_OVERLAP_THRESHOLD;
        precrit_type = p.precrit_type;
        MIN_NONMISSING_FOR_BATCH = p.MIN_NONMISSING_FOR_BATCH;

        setMoveClass();
    }

    /**
     *
     */
    private void setMoveClass() {
        if (PBATCH == 1.0) {
            move_class = "batch";
        } else if (PBATCH != 0.0) {
            move_class = "mixed";
        } else if (PBATCH == 0.0) {
            move_class = "sesi";
        }
    }


    /**
     *
     */
    public void defaults() {
        OUTDIR = "";
        OUTPREFIX = "";
        EXPR_DATA_PATH = null;
        INTERACT_DATA_PATH = null;
        FEAT_DATA_PATH = null;
        R_DATA_PATH = null;
        MOVES_PATH = "";
        param_path = null;

        MEANKEND_PATH = null;
        MEANKENDR_PATH = null;
        MEANKENDC_PATH = null;
        MEANBINARY_PATH = null;
        MEANBINARYR_PATH = null;
        MEANBINARYC_PATH = null;
        MEANCOR_PATH = null;
        MEANCORR_PATH = null;
        MEANCORC_PATH = null;
        MEANMADR_PATH = null;
        MEANMSE_PATH = null;
        MEANMSER_PATH = null;
        MEANMSEC_PATH = null;
        MEANMEAN_PATH = null;
        MEANRMEAN_PATH = null;
        MEANCMEAN_PATH = null;
        MEANGEE_PATH = null;
        MEANGEERE_PATH = null;
        MEANGEECE_PATH = null;
        MEANLARS_PATH = null;
        MEANLARSRE_PATH = null;
        MEANLARSCE_PATH = null;
        MEANFEAT_PATH = null;
        MEANTF_PATH = null;
        MEANINTERACT_PATH = null;
        MEANEUC_PATH = null;
        MEANEUCR_PATH = null;
        MEANEUCC_PATH = null;
        MEANSPEAR_PATH = null;
        MEANSPEARR_PATH = null;
        MEANSPEARC_PATH = null;

        SDKEND_PATH = null;
        SDKENDR_PATH = null;
        SDKENDC_PATH = null;
        SDBINARY_PATH = null;
        SDBINARYR_PATH = null;
        SDBINARYC_PATH = null;
        SDCOR_PATH = null;
        SDCORR_PATH = null;
        SDCORC_PATH = null;
        SDMADR_PATH = null;
        SDMSE_PATH = null;
        SDMSER_PATH = null;
        SDMSEC_PATH = null;
        SDMEAN_PATH = null;
        SDRMEAN_PATH = null;
        SDCMEAN_PATH = null;
        SDGEE_PATH = null;
        SDGEERE_PATH = null;
        SDGEECE_PATH = null;
        SDLARS_PATH = null;
        SDLARSRE_PATH = null;
        SDLARSCE_PATH = null;
        SDFEAT_PATH = null;
        SDTF_PATH = null;
        SDINTERACT_PATH = null;
        SDEUC_PATH = null;
        SDEUCR_PATH = null;
        SDEUCC_PATH = null;
        SDSPEAR_PATH = null;
        SDSPEARR_PATH = null;
        SDSPEARC_PATH = null;

        TRAJECTORY_PATH = null;
        EXCLUDE_LIST_PATH = null;
        R_METHODS_PATH = null;
        ANNOTATION_PATH = null;
        TFTARGETMAP_PATH = null;

        N_BLOCK = 1;
        init_block_list = new ArrayList();
        INIT_BLOCKS = new ArrayList[2];
        NOISE_LEVEL = MINER_STATIC.DEFAULT_noise_level;
        INITPA = MINER_STATIC.DEFAULT_pA;
        INITPG = MINER_STATIC.DEFAULT_pG;
        PA = Double.NaN;
        PG = Double.NaN;
        PBATCH = Double.NaN;
        SIZE_PRECRIT_LIST = -1;
        SIZE_PRECRIT_LIST_GENE = -1;
        fraction_genes_for_precritmove = MINER_STATIC.DEFAULT_fraction_genes_for_precritmove;
        SIZE_PRECRIT_LIST_EXP = -1;
        fraction_exps_for_precritmove = MINER_STATIC.DEFAULT_fraction_exps_for_precritmove;
        MAXMOVES = new int[2];
        MAXMOVES[0] = 15;
        MAXMOVES[1] = 30;
        IMIN = -1;
        JMIN = -1;
        IMAX = -1;
        JMAX = -1;
        DATA_LEN_GENES = -1;
        DATA_LEN_EXPS = -1;

        PERCENT_ALLOWED_MISSING_GENES = MINER_STATIC.DEFAULT_percent_allowed_missing_genes;
        PERCENT_ALLOWED_MISSING_EXP = MINER_STATIC.DEFAULT_percent_allowed_missing_exps;
        PERCENT_ALLOWED_MISSING_IN_BLOCK = MINER_STATIC.DEFAULT_percent_allowed_missing_in_block;

        //all_features = false;
        TRUNCATE_DATA100 = false;
        RANDOMIZE_BLOCKS = false;
        RANDOMIZE_EXPS = false;
        RANDOMIZE_GENES = false;
        FIX_GENES = false;
        FIX_EXPS = false;

        USE_RANDOM_SEED = true;
        RANDOM_SEED = DEFAULT_RANDOM_SEED;

        BATCH_DMETHOD = "correlation";
        BATCH_LINKMETHOD = "complete";

        CRIT_TYPE_INDEX = default_crit_type_index;
        PRECRIT_TYPE_INDEX = default_precrit_type_index;

        FEATURE_INDICES = new ArrayList();
        feature_map = null;
        PLATEAU = false;
        RANDOMFLOOD = false;
        CHECKSUM_SEED = -1;
        move_class = "sesi";

        GENE_SHAVE = false;
        EXP_SHAVE = false;

        GENE_GROW = false;
        EXP_GROW = false;
        WEIGH_EXPR = true;
        USE_MEAN = false;
        //USE_ABS = true;
        USE_ABS = false;
        OVERRIDE_SHAVING = true;
        setAbsAr();
        //setNullAr();

        //USE_ABS_AR = null;//MoreArray.initArray(3, 1);

        debug = 0;

        RANDOMFLOOD_PERC = MINER_STATIC.DEFAULT_randomflood_percentage;
        BATCH_PERC = MINER_STATIC.DEFAULT_batch_percentage;
        PLATEAU_PERC = RANDOMFLOOD_PERC;
        RUN_SEQUENCE = "";
        LIB_LOC = null;
        EXCLUDE_OVERLAP_THRESHOLD = 1;
        precrit_type = "MSE";
        MIN_NONMISSING_FOR_BATCH = MINER_STATIC.DEFAULT_MIN_NONMISSING_FOR_BATCH;

        if (CRIT_TYPE_INDEX != -1) {

            if ((MINER_STATIC.CRIT_LABELS[CRIT_TYPE_INDEX - 1].contains("MEDRMEAN") ||
                    MINER_STATIC.CRIT_LABELS[CRIT_TYPE_INDEX - 1].contains("MEDCMEAN") ||
                    MINER_STATIC.CRIT_LABELS[CRIT_TYPE_INDEX - 1].contains("MEAN"))) {
                // &&MINER_STATIC.CRIT_LABELS[CRIT_TYPE_INDEX - 1].indexOf("_") == -1
                USE_MEAN = true;
                if (debug > 0)
                    System.out.println("defaults USE_MEAN " + USE_MEAN);
            }

            //crit = new Criterion(CRIT_TYPE_INDEX, false);//debug > 0 ? true : false);
            boolean b = TFTARGETMAP_PATH != null ? true : false;
            //System.out.println("TFTARGETMAP_PATH " + b);
            crit = new Criterion(CRIT_TYPE_INDEX, USE_MEAN, WEIGH_EXPR, USE_ABS_AR, b, debug > 0 ? true : false);
            //crit = new Criterion(CRIT_TYPE_INDEX, USE_MEAN, WEIGH_EXPR, USE_ABS_AR, USE_NULL_AR, b, debug > 0 ? true : false);
        } else
            crit = null;
        if (PRECRIT_TYPE_INDEX != -1) {

            if ((MINER_STATIC.CRIT_LABELS[PRECRIT_TYPE_INDEX - 1].contains("MEDRMEAN") ||
                    MINER_STATIC.CRIT_LABELS[PRECRIT_TYPE_INDEX - 1].contains("MEDCMEAN") ||
                    MINER_STATIC.CRIT_LABELS[PRECRIT_TYPE_INDEX - 1].contains("MEAN"))
                //&& MINER_STATIC.CRIT_LABELS[PRECRIT_TYPE_INDEX - 1].indexOf("_") == -1
                    ) {
                USE_MEAN = true;
                if (debug > 0)
                    System.out.println("defaults USE_MEAN " + USE_MEAN);
            }

            //   precrit = new Criterion(PRECRIT_TYPE_INDEX, false);// debug > 0 ? true : false);
            boolean b = TFTARGETMAP_PATH != null ? true : false;
            //System.out.println("TFTARGETMAP_PATH " + b);
            precrit = new Criterion(PRECRIT_TYPE_INDEX, USE_MEAN, WEIGH_EXPR, USE_ABS_AR, b, debug > 0 ? true : false);
            //precrit = new Criterion(PRECRIT_TYPE_INDEX, USE_MEAN, WEIGH_EXPR, USE_ABS_AR,USE_NULL_AR, b, debug > 0 ? true : false);
        } else
            precrit = null;
    }

    /**
     * @param data
     * @param file_separator
     */
    public void read(ArrayList data, String file_separator) {
        char reverse_separator = '/';
        if (file_separator.equals("/"))
            reverse_separator = '\\';
        else if (file_separator.equals("\\"))
            reverse_separator = '/';
        for (int i = 0; i < data.size(); i++) {
            String cur = (String) data.get(i);
            //System.out.println("Parameters read cur "+i+"\t"+cur);
            if (cur.indexOf("#") != 0) {
                int openBrack = cur.indexOf("{");
                int closeBrack = cur.indexOf("}");
                int startInd = cur.indexOf("=") + 1;
                int endInd = cur.indexOf("#", startInd);
                if (startInd != -1 && (endInd != -1 || openBrack != -1)) {
                    String extractStr = cur.substring(startInd, endInd).trim();
                    extractStr = StringUtil.replace(extractStr, " ", "");
                    if (extractStr.length() > 0) {
                        //System.out.println("Parameters read extractStr "+startInd + "\t" + endInd+"\t"+cur+"\t"+extractStr);
                        if (cur.matches("(?i)OUTDIR =.*")) {
                            if (extractStr.length() > 0) {
                                OUTDIR = extractStr;
                                if (OUTDIR.charAt(OUTDIR.length() - 1) != file_separator.charAt(0)
                                        && OUTDIR.charAt(OUTDIR.length() - 1) != reverse_separator)
                                    if (OUTDIR.indexOf(file_separator) != -1 || OUTDIR.indexOf(reverse_separator) == -1)
                                        OUTDIR += file_separator;
                                    else if (OUTDIR.indexOf(reverse_separator) != -1)
                                        OUTDIR += reverse_separator;

                                File test = new File(OUTDIR);
                                if (!test.exists()) {
                                    if (debug >= 0)
                                        System.out.println("Attempting to create output dir");
                                    try {
                                        test.mkdir();
                                        if (debug >= 0)
                                            System.out.println("succeeded");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            //System.out.println("Parameters read OUTDIR " + OUTDIR);
                        } else if (cur.matches("(?i)OUTPREFIX =.*")) {
                            OUTPREFIX = extractStr;
                            if (debug >= 0)
                                System.out.println("Parameters read OUTPREFIX " + OUTPREFIX);
                        } else if (cur.matches("(?i)EXPR_DATA_PATH =.*")) {
                            EXPR_DATA_PATH = extractStr;
                        } else if (cur.matches("(?i)INTERACT_DATA_PATH =.*")) {
                            INTERACT_DATA_PATH = extractStr;
                        } else if (cur.matches("(?i)FEAT_DATA_PATH =.*")) {
                            FEAT_DATA_PATH = extractStr;
                        } else if (cur.matches("(?i)R_DATA_PATH =.*")) {
                            R_DATA_PATH = extractStr;
                        } else if (cur.matches("(?i)MOVES_PATH =.*")) {
                            MOVES_PATH = extractStr;
                        } else if (cur.matches("(?i)MEANKEND_PATH =.*") || cur.matches("(?i)MEANKENDALL_PATH =.*")) {
                            MEANKEND_PATH = extractStr;
                            //System.out.println("MEANKEND_PATH "+MEANKEND_PATH);
                        } else if (cur.matches("(?i)MEANKENDR_PATH =.*") || cur.matches("(?i)MEANKENDALLR_PATH =.*")) {
                            MEANKENDR_PATH = extractStr;
                            //System.out.println("MEANKENDR_PATH "+MEANKENDR_PATH);
                        } else if (cur.matches("(?i)MEANKENDC_PATH =.*") || cur.matches("(?i)MEANKENDALLC_PATH =.*")) {
                            MEANKENDC_PATH = extractStr;
                            //System.out.println("MEANKENDC_PATH "+MEANKENDC_PATH);
                        } else if (cur.matches("(?i)MEANBINARY_PATH =.*")) {
                            MEANBINARY_PATH = extractStr;
                            //System.out.println("MEANBINARY_PATH "+MEANBINARY_PATH);
                        } else if (cur.matches("(?i)MEANBINARYR_PATH =.*")) {
                            MEANBINARYR_PATH = extractStr;
                            //System.out.println("MEANBINARYR_PATH "+MEANBINARYR_PATH);
                        } else if (cur.matches("(?i)MEANBINARYC_PATH =.*")) {
                            MEANBINARYC_PATH = extractStr;
                            //System.out.println("MEANBINARYC_PATH "+MEANBINARYC_PATH);
                        } else if (cur.matches("(?i)MEANCOR_PATH =.*")) {
                            MEANCOR_PATH = extractStr;
                            //System.out.println("MEANCOR_PATH "+MEANCOR_PATH);
                        } else if (cur.matches("(?i)MEANCORR_PATH =.*")) {
                            MEANCORR_PATH = extractStr;
                            //System.out.println("MEANCORR_PATH "+MEANCORR_PATH);
                        } else if (cur.matches("(?i)MEANCORC_PATH =.*")) {
                            MEANCORC_PATH = extractStr;
                            //System.out.println("MEANCORC_PATH "+MEANCORC_PATH);
                        } else if (cur.matches("(?i)MEANMADR_PATH =.*")) {
                            MEANMADR_PATH = extractStr;
                        } else if (cur.matches("(?i)MEANMSE_PATH =.*")) {
                            MEANMSE_PATH = extractStr;
                        } else if (cur.matches("(?i)MEANMSER_PATH =.*")) {
                            MEANMSER_PATH = extractStr;
                        } else if (cur.matches("(?i)MEANMSEC_PATH =.*")) {
                            MEANMSEC_PATH = extractStr;
                        } else if (cur.matches("(?i)MEANINTERACT_PATH =.*")) {
                            MEANINTERACT_PATH = extractStr;
                        } else if (cur.matches("(?i)MEANGEE_PATH =.*")) {
                            MEANGEE_PATH = extractStr;
                        } else if (cur.matches("(?i)MEANGEERE_PATH =.*")) {
                            MEANGEERE_PATH = extractStr;
                        } else if (cur.matches("(?i)MEANGEECE_PATH =.*")) {
                            MEANGEECE_PATH = extractStr;
                        } else if (cur.matches("(?i)MEANLARS_PATH =.*")) {
                            MEANLARS_PATH = extractStr;
                        } else if (cur.matches("(?i)MEANLARSRE_PATH =.*")) {
                            MEANLARSRE_PATH = extractStr;
                        } else if (cur.matches("(?i)MEANLARSCE_PATH =.*")) {
                            MEANLARSCE_PATH = extractStr;
                        } else if (cur.matches("(?i)MEANEUC_PATH =.*")) {
                            MEANEUC_PATH = extractStr;
                        } else if (cur.matches("(?i)MEANEUCR_PATH =.*")) {
                            MEANEUCR_PATH = extractStr;
                        } else if (cur.matches("(?i)MEANEUCC_PATH =.*")) {
                            MEANEUCC_PATH = extractStr;
                        } else if (cur.matches("(?i)MEANSPEAR_PATH =.*")) {
                            MEANSPEAR_PATH = extractStr;
                        } else if (cur.matches("(?i)MEANSPEARR_PATH =.*")) {
                            MEANSPEARR_PATH = extractStr;
                        } else if (cur.matches("(?i)MEANSPEARC_PATH =.*")) {
                            MEANSPEARC_PATH = extractStr;
                        } else if (cur.matches("(?i)SDKENDALL_PATH =.*") || cur.matches("(?i)SDKEND_PATH =.*")) {
                            SDKEND_PATH = extractStr;
                        } else if (cur.matches("(?i)SDKENDALLR_PATH =.*") || cur.matches("(?i)SDKENDR_PATH =.*")) {
                            SDKENDR_PATH = extractStr;
                        } else if (cur.matches("(?i)SDKENDALLC_PATH =.*") || cur.matches("(?i)SDKENDC_PATH =.*")) {
                            SDKENDC_PATH = extractStr;
                        } else if (cur.matches("(?i)SDBINARY_PATH =.*")) {
                            SDBINARY_PATH = extractStr;
                        } else if (cur.matches("(?i)SDBINARYR_PATH =.*")) {
                            SDBINARYR_PATH = extractStr;
                        } else if (cur.matches("(?i)SDBINARYC_PATH =.*")) {
                            SDBINARYC_PATH = extractStr;
                        } else if (cur.matches("(?i)SDCOR_PATH =.*")) {
                            SDCOR_PATH = extractStr;
                        } else if (cur.matches("(?i)SDCORR_PATH =.*")) {
                            SDCORR_PATH = extractStr;
                        } else if (cur.matches("(?i)SDCORC_PATH =.*")) {
                            SDCORC_PATH = extractStr;
                        } else if (cur.matches("(?i)SDMADR_PATH =.*")) {
                            SDMADR_PATH = extractStr;
                        } else if (cur.matches("(?i)SDMSE_PATH =.*")) {
                            SDMSE_PATH = extractStr;
                        } else if (cur.matches("(?i)SDMSER_PATH =.*")) {
                            SDMSER_PATH = extractStr;
                        } else if (cur.matches("(?i)SDMSEC_PATH =.*")) {
                            SDMSEC_PATH = extractStr;
                        } else if (cur.matches("(?i)SDINTERACT_PATH =.*")) {
                            SDINTERACT_PATH = extractStr;
                        } else if (cur.matches("(?i)SDGEE_PATH =.*")) {
                            SDGEE_PATH = extractStr;
                        } else if (cur.matches("(?i)SDGEERE_PATH =.*")) {
                            SDGEERE_PATH = extractStr;
                        } else if (cur.matches("(?i)SDGEECE_PATH =.*")) {
                            SDGEECE_PATH = extractStr;
                        } else if (cur.matches("(?i)SDLARS_PATH =.*")) {
                            SDLARS_PATH = extractStr;
                        } else if (cur.matches("(?i)SDLARSRE_PATH =.*")) {
                            SDLARSRE_PATH = extractStr;
                        } else if (cur.matches("(?i)SDLARSCE_PATH =.*")) {
                            SDLARSCE_PATH = extractStr;
                        } else if (cur.matches("(?i)SDEUC_PATH =.*")) {
                            SDEUC_PATH = extractStr;
                        } else if (cur.matches("(?i)SDEUCR_PATH =.*")) {
                            SDEUCR_PATH = extractStr;
                        } else if (cur.matches("(?i)SDEUCC_PATH =.*")) {
                            SDEUCC_PATH = extractStr;
                        } else if (cur.matches("(?i)SDSPEAR_PATH =.*")) {
                            SDSPEAR_PATH = extractStr;
                        } else if (cur.matches("(?i)SDSPEARR_PATH =.*")) {
                            SDSPEARR_PATH = extractStr;
                        } else if (cur.matches("(?i)SDSPEARC_PATH =.*")) {
                            SDSPEARC_PATH = extractStr;
                        } else if (cur.matches("(?i)MEANMEAN_PATH =.*")) {
                            MEANMEAN_PATH = extractStr;
                        } else if (cur.matches("(?i)SDMEAN_PATH =.*")) {
                            SDMEAN_PATH = extractStr;
                        } else if (cur.matches("(?i)MEANRMEAN_PATH =.*")) {
                            MEANRMEAN_PATH = extractStr;
                        } else if (cur.matches("(?i)SDRMEAN_PATH =.*")) {
                            SDRMEAN_PATH = extractStr;
                        } else if (cur.matches("(?i)MEANCMEAN_PATH =.*")) {
                            MEANCMEAN_PATH = extractStr;
                        } else if (cur.matches("(?i)SDCMEAN_PATH =.*")) {
                            SDCMEAN_PATH = extractStr;
                        } else if (cur.matches("(?i)MEANFEAT_PATH =.*")) {
                            MEANFEAT_PATH = extractStr;
                        } else if (cur.matches("(?i)SDFEAT_PATH =.*")) {
                            SDFEAT_PATH = extractStr;
                        } else if (cur.matches("(?i)MEANTF_PATH =.*")) {
                            MEANTF_PATH = extractStr;
                            if (debug >= 0)
                                System.out.println("MEANTF_PATH " + MEANTF_PATH);
                        } else if (cur.matches("(?i)SDTF_PATH =.*")) {
                            SDTF_PATH = extractStr;
                            if (debug >= 0)
                                System.out.println("SDTF_PATH " + SDTF_PATH);
                        } else if (cur.matches("(?i)TRAJECTORY_PATH =.*")) {
                            TRAJECTORY_PATH = extractStr;
                        } else if (cur.matches("(?i)EXCLUDE_LIST_PATH =.*")) {
                            EXCLUDE_LIST_PATH = extractStr;
                        } else if (cur.matches("(?i)R_METHODS_PATH =.*")) {
                            R_METHODS_PATH = extractStr;
                        } else if (cur.matches("(?i)ANNOTATION_PATH =.*")) {
                            ANNOTATION_PATH = extractStr;
                        } else if (cur.matches("(?i)TFTARGETMAP_PATH =.*")) {
                            TFTARGETMAP_PATH = extractStr;
                        } else if (cur.matches("(?i)N_BLOCK =.*")) {
                            N_BLOCK = Integer.parseInt(extractStr);
                        } else if (cur.matches("(?i)INIT_BLOCKS =.*")) {
                            N_BLOCK = 1;
                            init_block_list = new ArrayList();
                            INIT_BLOCKS = new ArrayList[2];
                            //System.out.println("Parameter read INIT_BLOCKS " + cur);
                            if (openBrack != -1 && closeBrack != -1) {
                                String str = cur.substring(openBrack + 1, closeBrack);
                                //System.out.println("Parameter read INIT_BLOCKS " + str);
                                extractCoords(str);
                            } else {
                                i++;
                                cur = (String) data.get(i);
                                while (cur.indexOf("}") == -1) {
                                    extractCoords(cur);
                                    //System.out.println("Parameter adding block " + cur);
                                    i++;
                                    cur = (String) data.get(i);
                                }
                                i++;
                            }
                            //System.out.println("Parame
                            // ter INIT_BLOCKS");
                            /* try {
                                MoreArray.printArray(MoreArray.ArrayListtoInt(INIT_BLOCKS[0]));
                         tch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                MoreArray.printArray(MoreArray.ArrayListtoInt(INIT_BLOCKS[1]));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }*/
                        } else if (cur.matches("(?i)NOISE_LEVEL =.*")) {
                            NOISE_LEVEL = Double.parseDouble(extractStr);
                        } else if (cur.matches("(?i)INITPA =.*")) {
                            INITPA = Double.parseDouble(extractStr);
                            if (debug >= 0)
                                System.out.println("Parameters read INITPA " + INITPA);
                            if (Double.isNaN(INITPA)) {
                                INITPA = MINER_STATIC.DEFAULT_pA;
                                if (debug >= 0)
                                    System.out.println("Parameters read INITPA was NA, set to " + INITPA);
                            }
                        } else if (cur.matches("(?i)INITPG =.*")) {
                            INITPG = Double.parseDouble(extractStr);
                            if (debug >= 0)
                                System.out.println("Parameters read INITPG " + INITPG);
                            if (Double.isNaN(INITPG)) {
                                INITPG = MINER_STATIC.DEFAULT_pG;
                                if (debug >= 0)
                                    System.out.println("Parameters read INITPG was NA, set to " + INITPG);
                            }
                        } else if (cur.matches("(?i)PA =.*")) {
                            PA = Double.parseDouble(extractStr);
                            if (debug >= 0)
                                System.out.println("Parameters read PA " + PA);
                            if (Double.isNaN(PA)) {
                                if (!Double.isNaN(INITPA))
                                    PA = INITPA;
                                else
                                    PA = MINER_STATIC.DEFAULT_pA;
                                if (debug >= 0)
                                    System.out.println("Parameters read PA was NA, set to " + PA);
                            }
                        } else if (cur.matches("(?i)PG =.*")) {
                            PG = Double.parseDouble(extractStr);
                            if (debug >= 0)
                                System.out.println("Parameters read PG " + PG);
                            if (Double.isNaN(PG)) {
                                if (!Double.isNaN(INITPG))
                                    PG = INITPG;
                                else
                                    PG = MINER_STATIC.DEFAULT_pG;
                                if (debug >= 0)
                                    System.out.println("Parameters read PG was NA, set to " + PG);
                            }
                        } else if (cur.matches("(?i)PBATCH =.*")) {
                            PBATCH = Double.parseDouble(extractStr);
                            if (debug >= 0)
                                System.out.println("Parameters read PBATCH " + PBATCH);
                            setMoveClass();
                        } else if (cur.matches("(?i)SIZE_PRECRIT_LIST =.*")) {
                            SIZE_PRECRIT_LIST = (int) Double.parseDouble(extractStr);
                            //System.out.println("Parameters read SIZE_PRECRIT_LIST "+SIZE_PRECRIT_LIST);
                        } else if (cur.matches("(?i)SIZE_PRECRIT_LIST_GENE =.*")) {
                            SIZE_PRECRIT_LIST_GENE = Double.parseDouble(extractStr);
                            if (SIZE_PRECRIT_LIST_GENE <= 1.0)
                                fraction_genes_for_precritmove = SIZE_PRECRIT_LIST_GENE;
                            //System.out.println("Parameters read SIZE_PRECRIT_LIST_GENE "+SIZE_PRECRIT_LIST_GENE);
                        } else if (cur.matches("(?i)SIZE_PRECRIT_LIST_EXP =.*")) {
                            SIZE_PRECRIT_LIST_EXP = Double.parseDouble(extractStr);
                            if (SIZE_PRECRIT_LIST_EXP <= 1.0)
                                fraction_genes_for_precritmove = SIZE_PRECRIT_LIST_EXP;
                            //System.out.println("Parameters read SIZE_PRECRIT_LIST_EXP "+SIZE_PRECRIT_LIST_EXP);
                        } else if (cur.matches("(?i)MAXMOVES =.*")) {
                            if (openBrack != -1 && closeBrack != -1) {
                                String str = cur.substring(openBrack + 1, closeBrack);
                                //System.out.println("Moves "+str);
                                MAXMOVES = MoreArray.tointArray(StringUtil.parsetoArray(str, ","));
                            }
                        } else if (cur.matches("(?i)IMIN =.*")) {
                            IMIN = Integer.parseInt(extractStr);
                        } else if (cur.matches("(?i)IMAX =.*")) {
                            IMAX = Integer.parseInt(extractStr);
                        } else if (cur.matches("(?i)JMIN =.*")) {
                            JMIN = Integer.parseInt(extractStr);
                        } else if (cur.matches("(?i)JMAX =.*")) {
                            JMAX = Integer.parseInt(extractStr);
                        } else if (cur.matches("(?i)DATA_LEN_GENES =.*")) {
                            try {
                                DATA_LEN_GENES = Integer.parseInt(extractStr);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        } else if (cur.matches("(?i)DATA_LEN_EXPS =.*")) {
                            try {
                                DATA_LEN_EXPS = Integer.parseInt(extractStr);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        } else if (cur.matches("(?i)PERCENT_ALLOWED_MISSING_GENES =.*")) {
                            try {
                                PERCENT_ALLOWED_MISSING_GENES = Double.parseDouble(extractStr);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        } else if (cur.matches("(?i)PERCENT_ALLOWED_MISSING_EXP =.*")) {
                            try {
                                PERCENT_ALLOWED_MISSING_EXP = Double.parseDouble(extractStr);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        } else if (cur.matches("(?i)PERCENT_ALLOWED_MISSING_IN_BLOCK =.*")) {
                            try {
                                PERCENT_ALLOWED_MISSING_IN_BLOCK = Double.parseDouble(extractStr);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        } else if (cur.matches("(?i)TRUNCATE_DATA100 =.*")) {
                            if (extractStr.equalsIgnoreCase("T") || extractStr.equalsIgnoreCase("true") ||
                                    extractStr.equalsIgnoreCase("y") || extractStr.equalsIgnoreCase("yes"))
                                TRUNCATE_DATA100 = true;
                            else
                                TRUNCATE_DATA100 = false;
                        } else if (cur.matches("(?i)RANDOMIZE_BLOCKS =.*")) {
                            if (extractStr.equalsIgnoreCase("T") || extractStr.equalsIgnoreCase("true") ||
                                    extractStr.equalsIgnoreCase("y") || extractStr.equalsIgnoreCase("yes")) {
                                RANDOMIZE_BLOCKS = true;
                            } else
                                RANDOMIZE_BLOCKS = false;
                        } else if (cur.matches("(?i)RANDOMIZE_GENES =.*")) {
                            if (extractStr.equalsIgnoreCase("T") || extractStr.equalsIgnoreCase("true") ||
                                    extractStr.equalsIgnoreCase("y") || extractStr.equalsIgnoreCase("yes"))
                                RANDOMIZE_GENES = true;
                            else {
                                RANDOMIZE_GENES = false;
                            }
                        } else if (cur.matches("(?i)RANDOMIZE_EXPS =.*")) {
                            if (extractStr.equalsIgnoreCase("T") || extractStr.equalsIgnoreCase("true") ||
                                    extractStr.equalsIgnoreCase("y") || extractStr.equalsIgnoreCase("yes"))
                                RANDOMIZE_EXPS = true;
                            else {
                                RANDOMIZE_EXPS = false;
                            }
                        } else if (cur.matches("(?i)FIX_GENES =.*")) {
                            if (extractStr.equalsIgnoreCase("T") || extractStr.equalsIgnoreCase("true") ||
                                    extractStr.equalsIgnoreCase("y") || extractStr.equalsIgnoreCase("yes"))
                                FIX_GENES = true;
                            else {
                                FIX_GENES = false;
                            }
                        } else if (cur.matches("(?i)FIX_EXPS =.*")) {
                            if (extractStr.equalsIgnoreCase("T") || extractStr.equalsIgnoreCase("true") ||
                                    extractStr.equalsIgnoreCase("y") || extractStr.equalsIgnoreCase("yes"))
                                FIX_EXPS = true;
                            else {
                                FIX_EXPS = false;
                            }
                        } else if (cur.matches("(?i)GENE_SHAVE =.*")) {
                            if (extractStr.equalsIgnoreCase("T") || extractStr.equalsIgnoreCase("true") ||
                                    extractStr.equalsIgnoreCase("y") || extractStr.equalsIgnoreCase("yes")) {
                                GENE_SHAVE = true;
                                GENE_GROW = false;
                            } else if (extractStr.equalsIgnoreCase("F") || extractStr.equalsIgnoreCase("FALSE") ||
                                    extractStr.equalsIgnoreCase("N") || extractStr.equalsIgnoreCase("NO")) {
                                GENE_SHAVE = false;
                            }
                        } else if (cur.matches("(?i)EXP_SHAVE =.*")) {
                            if (extractStr.equalsIgnoreCase("T") || extractStr.equalsIgnoreCase("true") ||
                                    extractStr.equalsIgnoreCase("y") || extractStr.equalsIgnoreCase("yes")) {
                                EXP_SHAVE = true;
                                EXP_GROW = false;
                            } else if (extractStr.equalsIgnoreCase("F") || extractStr.equalsIgnoreCase("FALSE") ||
                                    extractStr.equalsIgnoreCase("N") || extractStr.equalsIgnoreCase("NO")) {
                                EXP_SHAVE = false;
                            }
                        } else if (cur.matches("(?i)GENE_GROW =.*")) {
                            if (extractStr.equalsIgnoreCase("T") || extractStr.equalsIgnoreCase("true") ||
                                    extractStr.equalsIgnoreCase("y") || extractStr.equalsIgnoreCase("yes")) {
                                GENE_GROW = true;
                                GENE_SHAVE = false;
                            } else if (extractStr.equalsIgnoreCase("F") || extractStr.equalsIgnoreCase("FALSE") ||
                                    extractStr.equalsIgnoreCase("N") || extractStr.equalsIgnoreCase("NO")) {
                                GENE_GROW = false;
                            }
                        } else if (cur.matches("(?i)EXP_GROW =.*")) {
                            if (extractStr.equalsIgnoreCase("T") || extractStr.equalsIgnoreCase("true") ||
                                    extractStr.equalsIgnoreCase("y") || extractStr.equalsIgnoreCase("yes")) {
                                EXP_GROW = true;
                                EXP_SHAVE = false;
                            } else if (extractStr.equalsIgnoreCase("F") || extractStr.equalsIgnoreCase("FALSE") ||
                                    extractStr.equalsIgnoreCase("N") || extractStr.equalsIgnoreCase("NO")) {
                                EXP_GROW = false;
                            }
                        } else if (cur.matches("(?i)WEIGH_EXPR =.*")) {
                            if (extractStr.equalsIgnoreCase("T") || extractStr.equalsIgnoreCase("true") ||
                                    extractStr.equalsIgnoreCase("y") || extractStr.equalsIgnoreCase("yes")) {
                                WEIGH_EXPR = true;
                            }
                        } else if (cur.matches("(?i)USE_MEAN =.*")) {
                            if (extractStr.equalsIgnoreCase("T") || extractStr.equalsIgnoreCase("true") ||
                                    extractStr.equalsIgnoreCase("y") || extractStr.equalsIgnoreCase("yes")) {
                                USE_MEAN = true;
                            }
                        } else if (cur.matches("(?i)USE_ABS =.*")) {
                            if (debug >= 0)
                                System.out.println("USE_ABS " + extractStr);
                            if (extractStr.equalsIgnoreCase("T") || extractStr.equalsIgnoreCase("true") ||
                                    extractStr.equalsIgnoreCase("y") || extractStr.equalsIgnoreCase("yes")) {
                                USE_ABS = true;
                                //USE_ABS_AR = MoreArray.initArray(3, 1);
                                setAbsAr();
                            } else if (extractStr.equalsIgnoreCase("F") || extractStr.equalsIgnoreCase("false") ||
                                    extractStr.equalsIgnoreCase("n") || extractStr.equalsIgnoreCase("no")) {
                                USE_ABS = false;
                                //USE_ABS_AR = MoreArray.initArray(3, 0);
                                setAbsAr();
                            } else if (extractStr != null) {
                                if (extractStr.indexOf(",") != -1) {
                                    USE_ABS_AR = MoreArray.ArrayListtoInt(MoreArray.convtoArrayList(extractStr.split(",")));
                                }
                                if (debug >= 0)
                                    System.out.println("USE_ABS " + USE_ABS + "\t" + MoreArray.toString(USE_ABS_AR));
                            }
                        } else if (cur.matches("(?i)USE_NULL =.*")) {
                            if (debug >= 0)
                                System.out.println("USE_NULL_AR " + extractStr);
                            /*if (extractStr.equalsIgnoreCase("T") || extractStr.equalsIgnoreCase("true") ||
                                    extractStr.equalsIgnoreCase("y") || extractStr.equalsIgnoreCase("yes")) {
                                USE_ABS = true;
                                //USE_ABS_AR = MoreArray.initArray(3, 1);
                                setAbsAr();
                            } else if (extractStr.equalsIgnoreCase("F") || extractStr.equalsIgnoreCase("false") ||
                                    extractStr.equalsIgnoreCase("n") || extractStr.equalsIgnoreCase("no")) {
                                USE_ABS = false;
                                //USE_ABS_AR = MoreArray.initArray(3, 0);
                                setAbsAr();
                            } else*/
                            if (extractStr != null) {
                                if (extractStr.indexOf(",") != -1) {
                                    USE_NULL_AR = MoreArray.ArrayListtoInt(MoreArray.convtoArrayList(extractStr.split(",")));
                                }
                                if (debug >= 0)
                                    System.out.println("USE_NULL_AR " + USE_NULL_AR + "\t" + MoreArray.toString(USE_NULL_AR));
                            }
                        } else if (cur.matches("(?i)FRXN_SIGN =.*")) {
                            if (debug >= 0)
                                System.out.println("FRXN_SIGN " + extractStr);
                            if (extractStr.equalsIgnoreCase("T") || extractStr.equalsIgnoreCase("true") ||
                                    extractStr.equalsIgnoreCase("y") || extractStr.equalsIgnoreCase("yes")) {
                                FRXN_SIGN = true;
                                //USE_ABS_AR = MoreArray.initArray(3, 1);
                                setAbsAr();
                            } else if (extractStr.equalsIgnoreCase("F") || extractStr.equalsIgnoreCase("false") ||
                                    extractStr.equalsIgnoreCase("n") || extractStr.equalsIgnoreCase("no")) {
                                FRXN_SIGN = false;
                                //USE_ABS_AR = MoreArray.initArray(3, 0);
                                setAbsAr();
                            }
                        } else if (cur.matches("(?i)OVERRIDE_SHAVING =.*")) {
                            if (debug >= 0)
                                System.out.println("OVERRIDE_SHAVING " + extractStr);
                            if (extractStr.equalsIgnoreCase("T") || extractStr.equalsIgnoreCase("true") ||
                                    extractStr.equalsIgnoreCase("y") || extractStr.equalsIgnoreCase("yes")) {
                                OVERRIDE_SHAVING = true;
                            } else if (extractStr.equalsIgnoreCase("F") || extractStr.equalsIgnoreCase("false") ||
                                    extractStr.equalsIgnoreCase("n") || extractStr.equalsIgnoreCase("no")) {
                                OVERRIDE_SHAVING = false;
                            }
                        } else if (cur.matches("(?i)USE_RANDOM_SEED =.*")) {
                            if (extractStr.equalsIgnoreCase("T") || extractStr.equalsIgnoreCase("true") ||
                                    extractStr.equalsIgnoreCase("y") || extractStr.equalsIgnoreCase("yes"))
                                USE_RANDOM_SEED = true;
                            else
                                USE_RANDOM_SEED = false;
                        } else if (cur.matches("(?i)RANDOM_SEED =.*")) {
                            RANDOM_SEED = Integer.parseInt(extractStr);
                            USE_RANDOM_SEED = false;
                        } else if (cur.matches("(?i)BATCH_DMETHOD =.*")) {
                            BATCH_DMETHOD = extractStr;
                        } else if (cur.matches("(?i)BATCH_LINKMETHOD =.*")) {
                            BATCH_LINKMETHOD = extractStr;
                        } else if (cur.matches("(?i)CRIT_TYPE_INDEX =.*")) {
                            CRIT_TYPE_INDEX = Integer.parseInt(extractStr);
                            //System.out.println("crit " + CRIT_TYPE_INDEX);
                        } else if (cur.matches("(?i)PRECRIT_TYPE_INDEX =.*")) {
                            PRECRIT_TYPE_INDEX = Integer.parseInt(extractStr);
                            //System.out.println("precrit " + PRECRIT_TYPE_INDEX);
                        } else if (cur.matches("FEATURES")) {
                            if (debug >= 0)
                                System.out.println("Parameters FEATURE_INDICES " + extractStr);
                            if (extractStr != null && !extractStr.equals("null") && extractStr.length() > 0) {
                                extractStr = util.StringUtil.replace(extractStr, " ", "");
                                int com = cur.indexOf(",");
                                if (com != -1) {
                                    String[] split = extractStr.split(",");
                                    if (debug >= 0)
                                        System.out.println("FEATURES " + split.length);
                                    if (split != null) {
                                        for (int k = 0; k < split.length; k++) {
                                            if (!split[k].equals("null")) {

                                                String[] splitfinal = split[k].split("/");

                                                feature_map.put(splitfinal[0], splitfinal[1]);
                                                //addtoFeatureMap(split[k]);
                                            }
                                        }
                                    }
                                    //if (feature_map != null && feature_map.size() == MINER_STATIC.FEATURE_FILES.length)
                                    //    all_features = true;
                                } /*else {
                                    System.out.println("FEATURES single " + extractStr);
                                    addtoFeatureMap(extractStr);
                                }*/
                            } else {
                                if (debug >= 0)
                                    System.out.println("FEATURES null");
                                FEATURE_INDICES = null;
                                feature_map = null;
                            }
                        } else if (cur.matches("(?i)PLATEAU =.*")) {
                            if (extractStr.equalsIgnoreCase("T") || extractStr.equalsIgnoreCase("true") ||
                                    extractStr.equalsIgnoreCase("y") || extractStr.equalsIgnoreCase("yes")) {
                                PLATEAU = true;
                            } else if (extractStr.equalsIgnoreCase("F") || extractStr.equalsIgnoreCase("FALSE") ||
                                    extractStr.equalsIgnoreCase("N") || extractStr.equalsIgnoreCase("NO")) {
                                PLATEAU = false;
                            }
                        } else if (cur.matches("(?i)plateau_perc =.*")) {
                            PLATEAU_PERC = Double.parseDouble(extractStr);
                        } else if (cur.matches("(?i)RANDOMFLOOD =.*")) {
                            if (extractStr.equalsIgnoreCase("T") || extractStr.equalsIgnoreCase("true") ||
                                    extractStr.equalsIgnoreCase("y") || extractStr.equalsIgnoreCase("yes"))
                                RANDOMFLOOD = true;
                            else if (extractStr.equalsIgnoreCase("F") || extractStr.equalsIgnoreCase("FALSE") ||
                                    extractStr.equalsIgnoreCase("N") || extractStr.equalsIgnoreCase("NO"))
                                RANDOMFLOOD = false;
                        } else if (cur.matches("(?i)randomflood_perc =.*")) {
                            RANDOMFLOOD_PERC = Double.parseDouble(extractStr);
                        } else if (cur.matches("(?i)batch_perc =.*")) {
                            BATCH_PERC = Double.parseDouble(extractStr);
                        } else if (cur.matches("(?i)RUN_SEQUENCE =.*")) {
                            RUN_SEQUENCE = extractStr;
                        } else if (cur.matches("(?i)lib_loc =.*")) {
                            LIB_LOC = extractStr;
                        } else if (cur.matches("(?i)EXCLUDE_OVERLAP_THRESHOLD =.*")) {
                            EXCLUDE_OVERLAP_THRESHOLD = Double.parseDouble(extractStr);
                        } else if (cur.matches("(?i)MIN_NONMISSING_FOR_BATCH =.*")) {
                            MIN_NONMISSING_FOR_BATCH = Integer.parseInt(extractStr);
                        } else if (cur.matches("(?i)debug =.*")) {
                            try {
                                debug = Integer.parseInt(extractStr);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }

        boolean TFcrit = TFTARGETMAP_PATH != null && MEANTF_PATH != null && SDTF_PATH != null ? true : false;
        if (TFTARGETMAP_PATH != null && (MEANTF_PATH == null || SDTF_PATH == null)) {
            throw new IllegalStateException("ERROR TFcrit nulls missing");
        }
        if (TFTARGETMAP_PATH == null && (MEANTF_PATH != null && SDTF_PATH != null)) {
            throw new IllegalStateException("ERROR TFcrit data missing");
        }
        if (debug >= 0)
            System.out.println("TFTARGETMAP_PATH set " + TFcrit);
        /*TODO separate use of TF crit for precrit and full crit */


        if (FEAT_DATA_PATH != null && (MEANFEAT_PATH == null || SDFEAT_PATH == null)) {
            throw new IllegalStateException("ERROR feat nulls missing");
        }
        if (FEAT_DATA_PATH == null && (MEANFEAT_PATH != null && SDFEAT_PATH != null)) {
            throw new IllegalStateException("ERROR feat data missing");
        }

        if (INTERACT_DATA_PATH != null && (MEANINTERACT_PATH == null || SDINTERACT_PATH == null)) {
            throw new IllegalStateException("ERROR int nulls missing");
        }
        if (INTERACT_DATA_PATH == null && (MEANINTERACT_PATH != null && SDINTERACT_PATH != null)) {
            throw new IllegalStateException("ERROR int data missing");
        }


        setInvert();

        precrit = new Criterion(PRECRIT_TYPE_INDEX, USE_MEAN, WEIGH_EXPR, USE_ABS_AR, TFcrit, needinv, true, FRXN_SIGN, debug > 0 ? true : false);
        //precrit = new Criterion(PRECRIT_TYPE_INDEX, USE_MEAN, WEIGH_EXPR, USE_ABS_AR, USE_NULL_AR, TFcrit, needinv, true, FRXN_SIGN, debug > 0 ? true : false);

        crit = new Criterion(CRIT_TYPE_INDEX, USE_MEAN, WEIGH_EXPR, USE_ABS_AR, TFcrit, needinv, true, FRXN_SIGN, debug > 0 ? true : false);
        //crit = new Criterion(CRIT_TYPE_INDEX, USE_MEAN, WEIGH_EXPR, USE_ABS_AR, USE_NULL_AR, TFcrit, needinv, true, FRXN_SIGN, debug > 0 ? true : false);

        if (debug >= 0)
            System.out.println("Parameters a/f read\n" + toString());
    }

    /**
     * @param data
     * @param file_separator
     * @param TemplateFile
     */
    public void read(ArrayList data, String file_separator, boolean TemplateFile) {
        char reverse_separator = '/';
        if (file_separator.equals("/"))
            reverse_separator = '\\';
        else if (file_separator.equals("\\"))
            reverse_separator = '/';
        for (int i = 0; i < data.size(); i++) {
            String cur = (String) data.get(i);
            //System.out.println("Parameters read cur "+i+"\t"+cur);
            if (cur.indexOf("#") != 0) {
                int openBrack = cur.indexOf("{");
                int closeBrack = cur.indexOf("}");
                int startInd = cur.indexOf("=") + 1;
                int endInd = cur.indexOf("#", startInd);
                if (startInd != -1 && (endInd != -1 || openBrack != -1)) {
                    String extractStr = cur.substring(startInd, endInd);
                    extractStr = StringUtil.replace(extractStr, " ", "");
                    if (extractStr.length() > 0) {
                        //System.out.println("Parameters read extractStr "+startInd + "\t" + endInd+"\t"+cur+"\t"+extractStr);
                        if (cur.matches("(?i)OUTDIR =.*")) {
                            if (extractStr.length() > 0) {
                                OUTDIR = extractStr;
                                if (OUTDIR.charAt(OUTDIR.length() - 1) != file_separator.charAt(0)
                                        && OUTDIR.charAt(OUTDIR.length() - 1) != reverse_separator)
                                    if (OUTDIR.indexOf(file_separator) != -1 || OUTDIR.indexOf(reverse_separator) == -1)
                                        OUTDIR += file_separator;
                                    else if (OUTDIR.indexOf(reverse_separator) != -1)
                                        OUTDIR += reverse_separator;

                                File test = new File(OUTDIR);
                                if (!test.exists()) {
                                    if (debug >= 0)
                                        System.out.println("Attempting to create output dir");
                                    try {
                                        test.mkdir();
                                        if (debug >= 0)
                                            System.out.println("succeeded");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            //System.out.println("Parameters read OUTDIR " + OUTDIR);
                        } else if (cur.matches("(?i)OUTPREFIX =.*")) {
                            OUTPREFIX = extractStr;
                            if (debug >= 0)
                                System.out.println("Parameters read OUTPREFIX " + OUTPREFIX);
                        } else if (cur.matches("(?i)EXPR_DATA_PATH =.*")) {
                            EXPR_DATA_PATH = extractStr;
                        } else if (cur.matches("(?i)INTERACT_DATA_PATH =.*")) {
                            INTERACT_DATA_PATH = extractStr;
                        } else if (cur.matches("(?i)FEAT_DATA_PATH =.*")) {
                            FEAT_DATA_PATH = extractStr;
                        } else if (cur.matches("(?i)R_DATA_PATH =.*")) {
                            R_DATA_PATH = extractStr;
                        } else if (cur.matches("(?i)MOVES_PATH =.*")) {
                            MOVES_PATH = extractStr;
                        } else if (cur.matches("(?i)MEANKEND_PATH =.*") || cur.matches("(?i)MEANKENDALL_PATH =.*")) {
                            MEANKEND_PATH = extractStr;
                            //System.out.println("MEANKEND_PATH "+MEANKEND_PATH);
                        } else if (cur.matches("(?i)MEANKENDR_PATH =.*") || cur.matches("(?i)MEANKENDALLR_PATH =.*")) {
                            MEANKENDR_PATH = extractStr;
                            //System.out.println("MEANKENDR_PATH "+MEANKENDR_PATH);
                        } else if (cur.matches("(?i)MEANKENDC_PATH =.*") || cur.matches("(?i)MEANKENDALLC_PATH =.*")) {
                            MEANKENDC_PATH = extractStr;
                            //System.out.println("MEANKENDC_PATH "+MEANKENDC_PATH);
                        } else if (cur.matches("(?i)MEANBINARY_PATH =.*")) {
                            MEANBINARY_PATH = extractStr;
                            //System.out.println("MEANBINARY_PATH "+MEANBINARY_PATH);
                        } else if (cur.matches("(?i)MEANBINARYR_PATH =.*")) {
                            MEANBINARYR_PATH = extractStr;
                            //System.out.println("MEANBINARYR_PATH "+MEANBINARYR_PATH);
                        } else if (cur.matches("(?i)MEANBINARYC_PATH =.*")) {
                            MEANBINARYC_PATH = extractStr;
                            //System.out.println("MEANBINARYC_PATH "+MEANBINARYC_PATH);
                        } else if (cur.matches("(?i)MEANCOR_PATH =.*")) {
                            MEANCOR_PATH = extractStr;
                            //System.out.println("MEANCOR_PATH "+MEANCOR_PATH);
                        } else if (cur.matches("(?i)MEANCORR_PATH =.*")) {
                            MEANCORR_PATH = extractStr;
                            //System.out.println("MEANCORR_PATH "+MEANCORR_PATH);
                        } else if (cur.matches("(?i)MEANCORC_PATH =.*")) {
                            MEANCORC_PATH = extractStr;
                            //System.out.println("MEANCORC_PATH "+MEANCORC_PATH);
                        } else if (cur.matches("(?i)MEANMADR_PATH =.*")) {
                            MEANMADR_PATH = extractStr;
                        } else if (cur.matches("(?i)MEANMSE_PATH =.*")) {
                            MEANMSE_PATH = extractStr;
                        } else if (cur.matches("(?i)MEANMSER_PATH =.*")) {
                            MEANMSER_PATH = extractStr;
                        } else if (cur.matches("(?i)MEANMSEC_PATH =.*")) {
                            MEANMSEC_PATH = extractStr;
                        } else if (cur.matches("(?i)MEANINTERACT_PATH =.*")) {
                            MEANINTERACT_PATH = extractStr;
                        } else if (cur.matches("(?i)MEANGEE_PATH =.*")) {
                            MEANGEE_PATH = extractStr;
                        } else if (cur.matches("(?i)MEANGEERE_PATH =.*")) {
                            MEANGEERE_PATH = extractStr;
                        } else if (cur.matches("(?i)MEANGEECE_PATH =.*")) {
                            MEANGEECE_PATH = extractStr;
                        } else if (cur.matches("(?i)MEANLARS_PATH =.*")) {
                            MEANLARS_PATH = extractStr;
                        } else if (cur.matches("(?i)MEANLARSRE_PATH =.*")) {
                            MEANLARSRE_PATH = extractStr;
                        } else if (cur.matches("(?i)MEANLARSCE_PATH =.*")) {
                            MEANLARSCE_PATH = extractStr;
                        } else if (cur.matches("(?i)MEANEUC_PATH =.*")) {
                            MEANEUC_PATH = extractStr;
                        } else if (cur.matches("(?i)MEANEUCR_PATH =.*")) {
                            MEANEUCR_PATH = extractStr;
                        } else if (cur.matches("(?i)MEANEUCC_PATH =.*")) {
                            MEANEUCC_PATH = extractStr;
                        } else if (cur.matches("(?i)MEANSPEAR_PATH =.*")) {
                            MEANSPEAR_PATH = extractStr;
                        } else if (cur.matches("(?i)MEANSPEARR_PATH =.*")) {
                            MEANSPEARR_PATH = extractStr;
                        } else if (cur.matches("(?i)MEANSPEARC_PATH =.*")) {
                            MEANSPEARC_PATH = extractStr;
                        } else if (cur.matches("(?i)SDKENDALL_PATH =.*")) {
                            SDKEND_PATH = extractStr;
                        } else if (cur.matches("(?i)SDKENDALLR_PATH =.*")) {
                            SDKENDR_PATH = extractStr;
                        } else if (cur.matches("(?i)SDKENDALLC_PATH =.*")) {
                            SDKENDC_PATH = extractStr;
                        } else if (cur.matches("(?i)SDBINARY_PATH =.*")) {
                            SDBINARY_PATH = extractStr;
                        } else if (cur.matches("(?i)SDBINARYR_PATH =.*")) {
                            SDBINARYR_PATH = extractStr;
                        } else if (cur.matches("(?i)SDBINARYC_PATH =.*")) {
                            SDBINARYC_PATH = extractStr;
                        } else if (cur.matches("(?i)SDCOR_PATH =.*")) {
                            SDCOR_PATH = extractStr;
                        } else if (cur.matches("(?i)SDCORR_PATH =.*")) {
                            SDCORR_PATH = extractStr;
                        } else if (cur.matches("(?i)SDCORC_PATH =.*")) {
                            SDCORC_PATH = extractStr;
                        } else if (cur.matches("(?i)SDMADR_PATH =.*")) {
                            SDMADR_PATH = extractStr;
                        } else if (cur.matches("(?i)SDMSE_PATH =.*")) {
                            SDMSE_PATH = extractStr;
                        } else if (cur.matches("(?i)SDMSER_PATH =.*")) {
                            SDMSER_PATH = extractStr;
                        } else if (cur.matches("(?i)SDMSEC_PATH =.*")) {
                            SDMSEC_PATH = extractStr;
                        } else if (cur.matches("(?i)SDINTERACT_PATH =.*")) {
                            SDINTERACT_PATH = extractStr;
                        } else if (cur.matches("(?i)SDGEE_PATH =.*")) {
                            SDGEE_PATH = extractStr;
                        } else if (cur.matches("(?i)SDGEERE_PATH =.*")) {
                            SDGEERE_PATH = extractStr;
                        } else if (cur.matches("(?i)SDGEECE_PATH =.*")) {
                            SDGEECE_PATH = extractStr;
                        } else if (cur.matches("(?i)SDLARS_PATH =.*")) {
                            SDLARS_PATH = extractStr;
                        } else if (cur.matches("(?i)SDLARSRE_PATH =.*")) {
                            SDLARSRE_PATH = extractStr;
                        } else if (cur.matches("(?i)SDLARSCE_PATH =.*")) {
                            SDLARSCE_PATH = extractStr;
                        } else if (cur.matches("(?i)SDEUC_PATH =.*")) {
                            SDEUC_PATH = extractStr;
                        } else if (cur.matches("(?i)SDEUCR_PATH =.*")) {
                            SDEUCR_PATH = extractStr;
                        } else if (cur.matches("(?i)SDEUCC_PATH =.*")) {
                            SDEUCC_PATH = extractStr;
                        } else if (cur.matches("(?i)SDSPEAR_PATH =.*")) {
                            SDSPEAR_PATH = extractStr;
                        } else if (cur.matches("(?i)SDSPEARR_PATH =.*")) {
                            SDSPEARR_PATH = extractStr;
                        } else if (cur.matches("(?i)SDSPEARC_PATH =.*")) {
                            SDSPEARC_PATH = extractStr;
                        } else if (cur.matches("(?i)MEANMEAN_PATH =.*")) {
                            MEANMEAN_PATH = extractStr;
                        } else if (cur.matches("(?i)SDMEAN_PATH =.*")) {
                            SDMEAN_PATH = extractStr;
                        } else if (cur.matches("(?i)MEANRMEAN_PATH =.*")) {
                            MEANRMEAN_PATH = extractStr;
                        } else if (cur.matches("(?i)SDRMEAN_PATH =.*")) {
                            SDRMEAN_PATH = extractStr;
                        } else if (cur.matches("(?i)MEANCMEAN_PATH =.*")) {
                            MEANCMEAN_PATH = extractStr;
                        } else if (cur.matches("(?i)SDCMEAN_PATH =.*")) {
                            SDCMEAN_PATH = extractStr;
                        } else if (cur.matches("(?i)MEANFEAT_PATH =.*")) {
                            MEANFEAT_PATH = extractStr;
                        } else if (cur.matches("(?i)SDFEAT_PATH =.*")) {
                            SDFEAT_PATH = extractStr;
                        } else if (cur.matches("(?i)MEANTF_PATH =.*")) {
                            MEANTF_PATH = extractStr;
                            if (debug >= 0)
                                System.out.println("MEANTF_PATH " + MEANTF_PATH);
                        } else if (cur.matches("(?i)SDTF_PATH =.*")) {
                            SDTF_PATH = extractStr;
                            if (debug >= 0)
                                System.out.println("SDTF_PATH " + SDTF_PATH);
                        } else if (cur.matches("(?i)TRAJECTORY_PATH =.*")) {
                            TRAJECTORY_PATH = extractStr;
                        } else if (cur.matches("(?i)EXCLUDE_LIST_PATH =.*")) {
                            EXCLUDE_LIST_PATH = extractStr;
                        } else if (cur.matches("(?i)R_METHODS_PATH =.*")) {
                            R_METHODS_PATH = extractStr;
                        } else if (cur.matches("(?i)ANNOTATION_PATH =.*")) {
                            ANNOTATION_PATH = extractStr;
                        } else if (cur.matches("(?i)TFTARGETMAP_PATH =.*")) {
                            TFTARGETMAP_PATH = extractStr;
                        } else if (cur.matches("(?i)N_BLOCK =.*")) {
                            N_BLOCK = Integer.parseInt(extractStr);
                        } else if (cur.matches("(?i)INIT_BLOCKS =.*")) {
                            N_BLOCK = 1;
                            init_block_list = new ArrayList();
                            INIT_BLOCKS = new ArrayList[2];
                            //System.out.println("Parameter read INIT_BLOCKS " + cur);
                            if (openBrack != -1 && closeBrack != -1) {
                                String str = cur.substring(openBrack + 1, closeBrack);
                                //System.out.println("Parameter read INIT_BLOCKS " + str);
                                extractCoords(str);
                            } else {
                                i++;
                                cur = (String) data.get(i);
                                while (cur.indexOf("}") == -1) {
                                    extractCoords(cur);
                                    //System.out.println("Parameter adding block " + cur);
                                    i++;
                                    cur = (String) data.get(i);
                                }
                                i++;
                            }
                            //System.out.println("Parame
                            // ter INIT_BLOCKS");
                            /* try {
                                MoreArray.printArray(MoreArray.ArrayListtoInt(INIT_BLOCKS[0]));
                         tch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                MoreArray.printArray(MoreArray.ArrayListtoInt(INIT_BLOCKS[1]));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }*/
                        } else if (cur.matches("(?i)NOISE_LEVEL =.*")) {
                            NOISE_LEVEL = Double.parseDouble(extractStr);
                        } else if (cur.matches("(?i)INITPA =.*")) {
                            INITPA = Double.parseDouble(extractStr);
                            if (debug >= 0)
                                System.out.println("Parameters read INITPA " + INITPA);
                            if (Double.isNaN(INITPA)) {
                                INITPA = MINER_STATIC.DEFAULT_pA;
                                if (debug >= 0)
                                    System.out.println("Parameters read INITPA was NA, set to " + INITPA);
                            }
                        } else if (cur.matches("(?i)INITPG =.*")) {
                            INITPG = Double.parseDouble(extractStr);
                            if (debug >= 0)
                                System.out.println("Parameters read INITPG " + INITPG);
                            if (Double.isNaN(INITPG)) {
                                INITPG = MINER_STATIC.DEFAULT_pG;
                                if (debug >= 0)
                                    System.out.println("Parameters read INITPG was NA, set to " + INITPG);
                            }
                        } else if (cur.matches("(?i)PA =.*")) {
                            PA = Double.parseDouble(extractStr);
                            if (debug >= 0)
                                System.out.println("Parameters read PA " + PA);
                            if (Double.isNaN(PA)) {
                                if (!Double.isNaN(INITPA))
                                    PA = INITPA;
                                else
                                    PA = MINER_STATIC.DEFAULT_pA;
                                if (debug >= 0)
                                    System.out.println("Parameters read PA was NA, set to " + PA);
                            }
                        } else if (cur.matches("(?i)PG =.*")) {
                            PG = Double.parseDouble(extractStr);
                            if (debug >= 0)
                                System.out.println("Parameters read PG " + PG);
                            if (Double.isNaN(PG)) {
                                if (!Double.isNaN(INITPG))
                                    PG = INITPG;
                                else
                                    PG = MINER_STATIC.DEFAULT_pG;
                                if (debug >= 0)
                                    System.out.println("Parameters read PG was NA, set to " + PG);
                            }
                        } else if (cur.matches("(?i)PBATCH =.*")) {
                            PBATCH = Double.parseDouble(extractStr);
                            if (debug >= 0)
                                System.out.println("Parameters read PBATCH " + PBATCH);
                            setMoveClass();
                        } else if (cur.matches("(?i)SIZE_PRECRIT_LIST =.*")) {
                            SIZE_PRECRIT_LIST = (int) Double.parseDouble(extractStr);
                            //System.out.println("Parameters read SIZE_PRECRIT_LIST "+SIZE_PRECRIT_LIST);
                        } else if (cur.matches("(?i)SIZE_PRECRIT_LIST_GENE =.*")) {
                            SIZE_PRECRIT_LIST_GENE = Double.parseDouble(extractStr);
                            if (SIZE_PRECRIT_LIST_GENE <= 1.0)
                                fraction_genes_for_precritmove = SIZE_PRECRIT_LIST_GENE;
                            //System.out.println("Parameters read SIZE_PRECRIT_LIST_GENE "+SIZE_PRECRIT_LIST_GENE);
                        } else if (cur.matches("(?i)SIZE_PRECRIT_LIST_EXP =.*")) {
                            SIZE_PRECRIT_LIST_EXP = Double.parseDouble(extractStr);
                            if (SIZE_PRECRIT_LIST_EXP <= 1.0)
                                fraction_genes_for_precritmove = SIZE_PRECRIT_LIST_EXP;
                            //System.out.println("Parameters read SIZE_PRECRIT_LIST_EXP "+SIZE_PRECRIT_LIST_EXP);
                        } else if (cur.matches("(?i)MAXMOVES =.*")) {
                            if (openBrack != -1 && closeBrack != -1) {
                                String str = cur.substring(openBrack + 1, closeBrack);
                                //System.out.println("Moves "+str);
                                MAXMOVES = MoreArray.tointArray(StringUtil.parsetoArray(str, ","));
                            }
                        } else if (cur.matches("(?i)IMIN =.*")) {
                            IMIN = Integer.parseInt(extractStr);
                        } else if (cur.matches("(?i)IMAX =.*")) {
                            IMAX = Integer.parseInt(extractStr);
                        } else if (cur.matches("(?i)JMIN =.*")) {
                            JMIN = Integer.parseInt(extractStr);
                        } else if (cur.matches("(?i)JMAX =.*")) {
                            JMAX = Integer.parseInt(extractStr);
                        } else if (cur.matches("(?i)DATA_LEN_GENES =.*")) {
                            try {
                                DATA_LEN_GENES = Integer.parseInt(extractStr);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        } else if (cur.matches("(?i)DATA_LEN_EXPS =.*")) {
                            try {
                                DATA_LEN_EXPS = Integer.parseInt(extractStr);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        } else if (cur.matches("(?i)PERCENT_ALLOWED_MISSING_GENES =.*")) {
                            try {
                                PERCENT_ALLOWED_MISSING_GENES = Double.parseDouble(extractStr);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        } else if (cur.matches("(?i)PERCENT_ALLOWED_MISSING_EXP =.*")) {
                            try {
                                PERCENT_ALLOWED_MISSING_EXP = Double.parseDouble(extractStr);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        } else if (cur.matches("(?i)PERCENT_ALLOWED_MISSING_IN_BLOCK =.*")) {
                            try {
                                PERCENT_ALLOWED_MISSING_IN_BLOCK = Double.parseDouble(extractStr);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        } else if (cur.matches("(?i)TRUNCATE_DATA100 =.*")) {
                            if (extractStr.equalsIgnoreCase("T") || extractStr.equalsIgnoreCase("true") ||
                                    extractStr.equalsIgnoreCase("y") || extractStr.equalsIgnoreCase("yes"))
                                TRUNCATE_DATA100 = true;
                            else
                                TRUNCATE_DATA100 = false;
                        } else if (cur.matches("(?i)RANDOMIZE_BLOCKS =.*")) {
                            if (extractStr.equalsIgnoreCase("T") || extractStr.equalsIgnoreCase("true") ||
                                    extractStr.equalsIgnoreCase("y") || extractStr.equalsIgnoreCase("yes")) {
                                RANDOMIZE_BLOCKS = true;
                            } else
                                RANDOMIZE_BLOCKS = false;
                        } else if (cur.matches("(?i)RANDOMIZE_GENES =.*")) {
                            if (extractStr.equalsIgnoreCase("T") || extractStr.equalsIgnoreCase("true") ||
                                    extractStr.equalsIgnoreCase("y") || extractStr.equalsIgnoreCase("yes"))
                                RANDOMIZE_GENES = true;
                            else {
                                RANDOMIZE_GENES = false;
                            }
                        } else if (cur.matches("(?i)RANDOMIZE_EXPS =.*")) {
                            if (extractStr.equalsIgnoreCase("T") || extractStr.equalsIgnoreCase("true") ||
                                    extractStr.equalsIgnoreCase("y") || extractStr.equalsIgnoreCase("yes"))
                                RANDOMIZE_EXPS = true;
                            else {
                                RANDOMIZE_EXPS = false;
                            }
                        } else if (cur.matches("(?i)FIX_GENES =.*")) {
                            if (extractStr.equalsIgnoreCase("T") || extractStr.equalsIgnoreCase("true") ||
                                    extractStr.equalsIgnoreCase("y") || extractStr.equalsIgnoreCase("yes"))
                                FIX_GENES = true;
                            else {
                                FIX_GENES = false;
                            }
                        } else if (cur.matches("(?i)FIX_EXPS =.*")) {
                            if (extractStr.equalsIgnoreCase("T") || extractStr.equalsIgnoreCase("true") ||
                                    extractStr.equalsIgnoreCase("y") || extractStr.equalsIgnoreCase("yes"))
                                FIX_EXPS = true;
                            else {
                                FIX_EXPS = false;
                            }
                        } else if (cur.matches("(?i)GENE_SHAVE =.*")) {
                            if (extractStr.equalsIgnoreCase("T") || extractStr.equalsIgnoreCase("true") ||
                                    extractStr.equalsIgnoreCase("y") || extractStr.equalsIgnoreCase("yes")) {
                                GENE_SHAVE = true;
                                GENE_GROW = false;
                            } else if (extractStr.equalsIgnoreCase("F") || extractStr.equalsIgnoreCase("FALSE") ||
                                    extractStr.equalsIgnoreCase("N") || extractStr.equalsIgnoreCase("NO")) {
                                GENE_SHAVE = false;
                            }
                        } else if (cur.matches("(?i)EXP_SHAVE =.*")) {
                            if (extractStr.equalsIgnoreCase("T") || extractStr.equalsIgnoreCase("true") ||
                                    extractStr.equalsIgnoreCase("y") || extractStr.equalsIgnoreCase("yes")) {
                                EXP_SHAVE = true;
                                EXP_GROW = false;
                            } else if (extractStr.equalsIgnoreCase("F") || extractStr.equalsIgnoreCase("FALSE") ||
                                    extractStr.equalsIgnoreCase("N") || extractStr.equalsIgnoreCase("NO")) {
                                EXP_SHAVE = false;
                            }
                        } else if (cur.matches("(?i)GENE_GROW =.*")) {
                            if (extractStr.equalsIgnoreCase("T") || extractStr.equalsIgnoreCase("true") ||
                                    extractStr.equalsIgnoreCase("y") || extractStr.equalsIgnoreCase("yes")) {
                                GENE_GROW = true;
                                GENE_SHAVE = false;
                            } else if (extractStr.equalsIgnoreCase("F") || extractStr.equalsIgnoreCase("FALSE") ||
                                    extractStr.equalsIgnoreCase("N") || extractStr.equalsIgnoreCase("NO")) {
                                GENE_GROW = false;
                            }
                        } else if (cur.matches("(?i)EXP_GROW =.*")) {
                            if (extractStr.equalsIgnoreCase("T") || extractStr.equalsIgnoreCase("true") ||
                                    extractStr.equalsIgnoreCase("y") || extractStr.equalsIgnoreCase("yes")) {
                                EXP_GROW = true;
                                EXP_SHAVE = false;
                            } else if (extractStr.equalsIgnoreCase("F") || extractStr.equalsIgnoreCase("FALSE") ||
                                    extractStr.equalsIgnoreCase("N") || extractStr.equalsIgnoreCase("NO")) {
                                EXP_GROW = false;
                            }
                        } else if (cur.matches("(?i)WEIGH_EXPR =.*")) {
                            if (extractStr.equalsIgnoreCase("T") || extractStr.equalsIgnoreCase("true") ||
                                    extractStr.equalsIgnoreCase("y") || extractStr.equalsIgnoreCase("yes")) {
                                WEIGH_EXPR = true;
                            }
                        } else if (cur.matches("(?i)USE_MEAN =.*")) {
                            if (extractStr.equalsIgnoreCase("T") || extractStr.equalsIgnoreCase("true") ||
                                    extractStr.equalsIgnoreCase("y") || extractStr.equalsIgnoreCase("yes")) {
                                USE_MEAN = true;
                            }
                        } else if (cur.matches("(?i)USE_ABS =.*")) {
                            if (debug >= 0)
                                System.out.println("USE_ABS " + extractStr);
                            if (extractStr.equalsIgnoreCase("T") || extractStr.equalsIgnoreCase("true") ||
                                    extractStr.equalsIgnoreCase("y") || extractStr.equalsIgnoreCase("yes")) {
                                USE_ABS = true;
                                //USE_ABS_AR = MoreArray.initArray(3, 1);
                                setAbsAr();
                            } else if (extractStr.equalsIgnoreCase("F") || extractStr.equalsIgnoreCase("false") ||
                                    extractStr.equalsIgnoreCase("n") || extractStr.equalsIgnoreCase("no")) {
                                USE_ABS = false;
                                //USE_ABS_AR = MoreArray.initArray(3, 0);
                                setAbsAr();
                            } else if (extractStr != null) {
                                if (extractStr.indexOf(",") != -1) {
                                    USE_ABS_AR = MoreArray.ArrayListtoInt(MoreArray.convtoArrayList(extractStr.split(",")));
                                }
                                if (debug >= 0)
                                    System.out.println("USE_ABS " + USE_ABS + "\t" + MoreArray.toString(USE_ABS_AR));
                            }
                        } else if (cur.matches("(?i)FRXN_SIGN =.*")) {
                            if (debug >= 0)
                                System.out.println("FRXN_SIGN " + extractStr);
                            if (extractStr.equalsIgnoreCase("T") || extractStr.equalsIgnoreCase("true") ||
                                    extractStr.equalsIgnoreCase("y") || extractStr.equalsIgnoreCase("yes")) {
                                FRXN_SIGN = true;
                                //USE_ABS_AR = MoreArray.initArray(3, 1);
                                setAbsAr();
                            } else if (extractStr.equalsIgnoreCase("F") || extractStr.equalsIgnoreCase("false") ||
                                    extractStr.equalsIgnoreCase("n") || extractStr.equalsIgnoreCase("no")) {
                                FRXN_SIGN = false;
                                //USE_ABS_AR = MoreArray.initArray(3, 0);
                                setAbsAr();
                            }
                        } else if (cur.matches("(?i)OVERRIDE_SHAVING =.*")) {
                            if (debug >= 0)
                                System.out.println("OVERRIDE_SHAVING " + extractStr);
                            if (extractStr.equalsIgnoreCase("T") || extractStr.equalsIgnoreCase("true") ||
                                    extractStr.equalsIgnoreCase("y") || extractStr.equalsIgnoreCase("yes")) {
                                OVERRIDE_SHAVING = true;
                            } else if (extractStr.equalsIgnoreCase("F") || extractStr.equalsIgnoreCase("false") ||
                                    extractStr.equalsIgnoreCase("n") || extractStr.equalsIgnoreCase("no")) {
                                OVERRIDE_SHAVING = false;
                            }
                        } else if (cur.matches("(?i)USE_RANDOM_SEED =.*")) {
                            if (extractStr.equalsIgnoreCase("T") || extractStr.equalsIgnoreCase("true") ||
                                    extractStr.equalsIgnoreCase("y") || extractStr.equalsIgnoreCase("yes"))
                                USE_RANDOM_SEED = true;
                            else
                                USE_RANDOM_SEED = false;
                        } else if (cur.matches("(?i)RANDOM_SEED =.*")) {
                            RANDOM_SEED = Integer.parseInt(extractStr);
                            USE_RANDOM_SEED = false;
                        } else if (cur.matches("(?i)BATCH_DMETHOD =.*")) {
                            BATCH_DMETHOD = extractStr;
                        } else if (cur.matches("(?i)BATCH_LINKMETHOD =.*")) {
                            BATCH_LINKMETHOD = extractStr;
                        } else if (cur.matches("(?i)CRIT_TYPE_INDEX =.*")) {
                            CRIT_TYPE_INDEX = Integer.parseInt(extractStr);
                            //System.out.println("crit " + CRIT_TYPE_INDEX);
                        } else if (cur.matches("(?i)PRECRIT_TYPE_INDEX =.*")) {
                            PRECRIT_TYPE_INDEX = Integer.parseInt(extractStr);
                            //System.out.println("precrit " + PRECRIT_TYPE_INDEX);
                        } else if (cur.matches("FEATURES")) {
                            if (debug >= 0)
                                System.out.println("Parameters FEATURE_INDICES " + extractStr);
                            if (extractStr != null && !extractStr.equals("null") && extractStr.length() > 0) {
                                extractStr = util.StringUtil.replace(extractStr, " ", "");
                                int com = cur.indexOf(",");
                                if (com != -1) {
                                    String[] split = extractStr.split(",");
                                    if (debug >= 0)
                                        System.out.println("FEATURES " + split.length);
                                    if (split != null) {
                                        for (int k = 0; k < split.length; k++) {
                                            if (!split[k].equals("null")) {

                                                String[] splitfinal = split[k].split("/");

                                                feature_map.put(splitfinal[0], splitfinal[1]);
                                                //addtoFeatureMap(split[k]);
                                            }
                                        }
                                    }
                                    //if (feature_map != null && feature_map.size() == MINER_STATIC.FEATURE_FILES.length)
                                    //    all_features = true;
                                } /*else {
                                    System.out.println("FEATURES single " + extractStr);
                                    addtoFeatureMap(extractStr);
                                }*/
                            } else {
                                if (debug >= 0)
                                    System.out.println("FEATURES null");
                                FEATURE_INDICES = null;
                                feature_map = null;
                            }
                        } else if (cur.matches("(?i)PLATEAU =.*")) {
                            if (extractStr.equalsIgnoreCase("T") || extractStr.equalsIgnoreCase("true") ||
                                    extractStr.equalsIgnoreCase("y") || extractStr.equalsIgnoreCase("yes")) {
                                PLATEAU = true;
                            } else if (extractStr.equalsIgnoreCase("F") || extractStr.equalsIgnoreCase("FALSE") ||
                                    extractStr.equalsIgnoreCase("N") || extractStr.equalsIgnoreCase("NO")) {
                                PLATEAU = false;
                            }
                        } else if (cur.matches("(?i)plateau_perc =.*")) {
                            PLATEAU_PERC = Double.parseDouble(extractStr);
                        } else if (cur.matches("(?i)RANDOMFLOOD =.*")) {
                            if (extractStr.equalsIgnoreCase("T") || extractStr.equalsIgnoreCase("true") ||
                                    extractStr.equalsIgnoreCase("y") || extractStr.equalsIgnoreCase("yes"))
                                RANDOMFLOOD = true;
                            else if (extractStr.equalsIgnoreCase("F") || extractStr.equalsIgnoreCase("FALSE") ||
                                    extractStr.equalsIgnoreCase("N") || extractStr.equalsIgnoreCase("NO"))
                                RANDOMFLOOD = false;
                        } else if (cur.matches("(?i)randomflood_perc =.*")) {
                            RANDOMFLOOD_PERC = Double.parseDouble(extractStr);
                        } else if (cur.matches("(?i)batch_perc =.*")) {
                            BATCH_PERC = Double.parseDouble(extractStr);
                        } else if (cur.matches("(?i)RUN_SEQUENCE =.*")) {
                            RUN_SEQUENCE = extractStr;
                        } else if (cur.matches("(?i)lib_loc =.*")) {
                            LIB_LOC = extractStr;
                        } else if (cur.matches("(?i)EXCLUDE_OVERLAP_THRESHOLD =.*")) {
                            EXCLUDE_OVERLAP_THRESHOLD = Double.parseDouble(extractStr);
                        } else if (cur.matches("(?i)MIN_NONMISSING_FOR_BATCH =.*")) {
                            MIN_NONMISSING_FOR_BATCH = Integer.parseInt(extractStr);
                        } else if (cur.matches("(?i)debug =.*")) {
                            try {
                                debug = Integer.parseInt(extractStr);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }

        boolean TFcrit = TFTARGETMAP_PATH != null && MEANTF_PATH != null && SDTF_PATH != null ? true : false;
        if (TFTARGETMAP_PATH != null && (MEANTF_PATH == null || SDTF_PATH == null)) {
            throw new IllegalStateException("ERROR TFcrit nulls missing");
        }
        //ORIGINAL CODE:
        /*
        if (TFTARGETMAP_PATH == null && (MEANTF_PATH != null && SDTF_PATH != null)) {
            throw new IllegalStateException("ERROR TFcrit data missing");
        }*/
        //RAUF'S CHANGE:
        if (TFTARGETMAP_PATH == null && (MEANTF_PATH != null && SDTF_PATH != null)) {
            throw new IllegalStateException("ERROR TFcrit data missing " + TFTARGETMAP_PATH);
        }
        if (debug >= 0)
            System.out.println("TFTARGETMAP_PATH set " + TFcrit);
        /*TODO separate use of TF crit for precrit and full crit */

        // if (!TemplateFile) {
        //     checkParameterFileArguments();
        // }

        setInvert();

        if (PRECRIT_TYPE_INDEX == -1) {
            PRECRIT_TYPE_INDEX = CRIT_TYPE_INDEX;
            if (debug >= 0)
                System.out.println("WARNING: PRECRIT_TYPE_INDEX UNDEFINED, SETTING TO CRIT_TYPE_INDEX = NO PRECRIT SCORING");
        }

        precrit = new Criterion(PRECRIT_TYPE_INDEX, USE_MEAN, WEIGH_EXPR, USE_ABS_AR, TFcrit, needinv, true, FRXN_SIGN, debug > 0 ? true : false);

        crit = new Criterion(CRIT_TYPE_INDEX, USE_MEAN, WEIGH_EXPR, USE_ABS_AR, TFcrit, needinv, true, FRXN_SIGN, debug > 0 ? true : false);

        if (debug >= 0)
            System.out.println("Parameters a/f read\n" + toString());
    }

    private void checkParameterFileArguments() {
        if (FEAT_DATA_PATH != null && (MEANFEAT_PATH == null || SDFEAT_PATH == null)) {
            throw new IllegalStateException("ERROR feat nulls missing");
        }
        if (FEAT_DATA_PATH == null && (MEANFEAT_PATH != null && SDFEAT_PATH != null)) {
            throw new IllegalStateException("ERROR feat data missing");
        }

        if (INTERACT_DATA_PATH != null && (MEANINTERACT_PATH == null || SDINTERACT_PATH == null)) {
            throw new IllegalStateException("ERROR int nulls missing");
        }
        if (INTERACT_DATA_PATH == null && (MEANINTERACT_PATH != null && SDINTERACT_PATH != null)) {
            throw new IllegalStateException("ERROR int data missing");
        }
    }

    /**
     * @return
     */

    private void setAbsAr() {
        int absone = USE_ABS ? 1 : 0;
        if (USE_ABS_AR == null) {
            USE_ABS_AR = MoreArray.initArray(3, 1);
            for (int i = 0; i < 3; i++) {
                USE_ABS_AR[i] = absone;
            }
        }

        if (USE_ABS)
            FRXN_SIGN = false;

        if (debug >= 0)
            System.out.println("setAbsAr " + MoreArray.toString(USE_ABS_AR, ","));
    }

    /**
     *
     */
    private void setInvert() {
        needinv = new boolean[7];
        //if (MEANMSE_PATH == null || SDMSE_PATH == null)
        needinv[0] = true;
        //if (MEANMSER_PATH == null || SDMSER_PATH == null)
        needinv[1] = true;
        //if (MEANMSEC_PATH == null || SDMSEC_PATH == null)
        needinv[2] = true;
        //if (MEANMADR_PATH == null || SDMADR_PATH == null)
        needinv[3] = true;
        //if (MEANEUC_PATH == null || SDEUC_PATH == null)
        needinv[4] = true;
        //if (MEANEUCR_PATH == null || SDEUCR_PATH == null)
        needinv[5] = true;
        //if (MEANEUCC_PATH == null || SDEUCC_PATH == null)
        needinv[6] = true;
    }

    /**
     * @param file
     * @param TemplateFile
     */
    public void read(String file, boolean TemplateFile) {
        ArrayList pass = TextFile.readtoList(file);
        String file_separator = System.getProperty("file.separator");
        if (debug >= 0)
            System.out.println("Parameters read " + file);
        read(pass, file_separator, TemplateFile);
        if (Double.isNaN(PA)) {
            PA = INITPA;
            if (debug >= 0)
                System.out.println("Parameters read set PA " + PA);
        } else {
            if (debug >= 0)
                System.out.println("Parameters read PA " + PA);
        }
        if (Double.isNaN(PG) && DATA_LEN_GENES != -1 && DATA_LEN_EXPS != -1) {
            //PG = INITPG;
            setpG();
        } else {
            if (debug >= 0)
                System.out.println("Parameters read PG " + PG);
        }
    }

    /**
     * @param file
     */
    public void read(String file) {
        ArrayList pass = TextFile.readtoList(file);
        String file_separator = System.getProperty("file.separator");
        if (debug >= 0)
            System.out.println("Parameters read " + file);
        read(pass, file_separator);
        if (Double.isNaN(PA)) {
            PA = INITPA;
            if (debug >= 0)
                System.out.println("Parameters read set PA " + PA);
        } else {
            if (debug >= 0)
                System.out.println("Parameters read PA " + PA);
        }
        if (Double.isNaN(PG) && DATA_LEN_GENES != -1 && DATA_LEN_EXPS != -1) {
            //PG = INITPG;
            setpG();
        } else {
            if (debug >= 0)
                System.out.println("Parameters read PG " + PG);
        }
    }

    /**
     *
     */
    public void setpG() {
        PG = (((double) (DATA_LEN_GENES)) / (double) (DATA_LEN_GENES + DATA_LEN_EXPS));
        if (debug >= 0)
            System.out.println("Parameters setpG " + PG + "\t" + DATA_LEN_GENES + "\t" + DATA_LEN_EXPS);
    }

    /**
     * A zero in the vector prevents any custom features from being assigned.
     *
     * @param a
     */
    /*public void addFeatures(ArrayList a) {
        System.out.println("addFeatures " + a.size());
        for (int k = 0; k < a.size(); k++) {
            String s = (String) a.get(k);
            int val = Integer.parseInt(s);
            System.out.println("addFeatures " + val);
            if (val == 0) {
                feature_map = null;
                break;
            }
            addtoFeatureMap(s);
        }
    }*/

    /**
     * @param sub
     */
    /*public void addtoFeatureMap(String sub) {
        int val = Integer.parseInt(sub);
        System.out.println("addtoFeatureMap " + val);
        if (val == 0) {
            feature_map = null;
        } else {
            if (feature_map == null)
                feature_map = new HashMap();
            FEATURE_INDICES.add(sub);
            int feature = Integer.parseInt(sub) - 1;
            System.out.println("addtoFeatureMap " + sub + "\t" + feature);
            System.out.println("addtoFeatureMap " + MINER_STATIC.FEATURES[feature] + "\t" + MINER_STATIC.FEATURE_FILES[feature] + "\t" + OUTPREFIX);
            feature_map.put(MINER_STATIC.FEATURES[feature], MINER_STATIC.FEATURE_FILES[feature]);
        }
    }*/


    /**
     *
     */
    public void clean_paths() {
        EXPR_DATA_PATH = replace(EXPR_DATA_PATH);
        R_DATA_PATH = replace(R_DATA_PATH);
        ANNOTATION_PATH = replace(ANNOTATION_PATH);
        FEAT_DATA_PATH = replace(FEAT_DATA_PATH);
        EXCLUDE_LIST_PATH = replace(EXCLUDE_LIST_PATH);
        INTERACT_DATA_PATH = replace(INTERACT_DATA_PATH);

        MEANBINARY_PATH = replace(MEANBINARY_PATH);
        MEANBINARYC_PATH = replace(MEANBINARYC_PATH);
        MEANBINARYR_PATH = replace(MEANBINARYR_PATH);
        MEANMEAN_PATH = replace(MEANMEAN_PATH);
        MEANCMEAN_PATH = replace(MEANCMEAN_PATH);
        MEANRMEAN_PATH = replace(MEANRMEAN_PATH);
        MEANCOR_PATH = replace(MEANCOR_PATH);
        MEANCORC_PATH = replace(MEANCORC_PATH);
        MEANCORR_PATH = replace(MEANCORR_PATH);
        MEANEUC_PATH = replace(MEANEUC_PATH);
        MEANEUCC_PATH = replace(MEANEUCC_PATH);
        MEANEUCR_PATH = replace(MEANEUCR_PATH);
        MEANSPEAR_PATH = replace(MEANSPEAR_PATH);
        MEANSPEARR_PATH = replace(MEANSPEARR_PATH);
        MEANSPEARC_PATH = replace(MEANSPEARC_PATH);
        MEANFEAT_PATH = replace(MEANFEAT_PATH);
        MEANGEE_PATH = replace(MEANGEE_PATH);
        MEANGEECE_PATH = replace(MEANGEECE_PATH);
        MEANGEERE_PATH = replace(MEANGEERE_PATH);
        MEANKEND_PATH = replace(MEANKEND_PATH);
        MEANKENDR_PATH = replace(MEANKENDR_PATH);
        MEANKENDC_PATH = replace(MEANKENDC_PATH);
        MEANLARS_PATH = replace(MEANLARS_PATH);
        MEANLARSCE_PATH = replace(MEANLARSCE_PATH);
        MEANLARSRE_PATH = replace(MEANLARSRE_PATH);
        MEANMADR_PATH = replace(MEANMADR_PATH);
        MEANMSE_PATH = replace(MEANMSE_PATH);
        MEANMSEC_PATH = replace(MEANMSEC_PATH);
        MEANMSER_PATH = replace(MEANMSER_PATH);
        MEANTF_PATH = replace(MEANTF_PATH);

        SDBINARY_PATH = replace(SDBINARY_PATH);
        SDBINARYC_PATH = replace(SDBINARYC_PATH);
        SDBINARYR_PATH = replace(SDBINARYR_PATH);
        SDMEAN_PATH = replace(SDMEAN_PATH);
        SDCMEAN_PATH = replace(SDCMEAN_PATH);
        SDRMEAN_PATH = replace(SDRMEAN_PATH);
        SDCOR_PATH = replace(SDCOR_PATH);
        SDCORC_PATH = replace(SDCORC_PATH);
        SDCORR_PATH = replace(SDCORR_PATH);
        SDEUC_PATH = replace(SDEUC_PATH);
        SDEUCC_PATH = replace(SDEUCC_PATH);
        SDEUCR_PATH = replace(SDEUCR_PATH);
        SDSPEAR_PATH = replace(SDSPEAR_PATH);
        SDSPEARC_PATH = replace(SDSPEARC_PATH);
        SDSPEARR_PATH = replace(SDSPEARR_PATH);
        SDFEAT_PATH = replace(SDFEAT_PATH);
        SDGEE_PATH = replace(SDGEE_PATH);
        SDGEECE_PATH = replace(SDGEECE_PATH);
        SDGEERE_PATH = replace(SDGEERE_PATH);
        SDKEND_PATH = replace(SDKEND_PATH);
        SDKENDR_PATH = replace(SDKENDR_PATH);
        SDKENDC_PATH = replace(SDKENDC_PATH);
        SDLARS_PATH = replace(SDLARS_PATH);
        SDLARSCE_PATH = replace(SDLARSCE_PATH);
        SDLARSRE_PATH = replace(SDLARSRE_PATH);
        SDMADR_PATH = replace(SDMADR_PATH);
        SDMSE_PATH = replace(SDMSE_PATH);
        SDMSEC_PATH = replace(SDMSEC_PATH);
        SDMSER_PATH = replace(SDMSER_PATH);
        SDTF_PATH = replace(SDTF_PATH);
    }

    /**
     * @param p
     * @return
     */
    public String replace(String p) {

        int index = p.indexOf("//");
        while (index != -1) {
            p = StringUtil.replace(p, "//", "/");
            index = p.indexOf("//");
        }

        return p;
    }


    /**
     * @param outfile
     */
    public void write(String outfile) {

        try {
            //System.out.println("writing prm " + outfile);
            PrintWriter pw = new PrintWriter(new FileWriter(outfile));

            String s1 = "OUTDIR = ";
            if (OUTDIR != null)
                s1 += OUTDIR;
            pw.println(s1 + " #Output directory");

            String s001 = "OUTPREFIX = ";
            if (OUTPREFIX != null)
                s001 += OUTPREFIX;
            pw.println(s001 + " #Output prefix");

            String s01 = "EXPR_DATA_PATH = ";
            if (EXPR_DATA_PATH != null)
                s01 += EXPR_DATA_PATH;
            pw.println(s01 + " #Expression dataset");

            String s02 = "INTERACT_DATA_PATH = ";
            if (INTERACT_DATA_PATH != null)
                s02 += INTERACT_DATA_PATH;
            pw.println(s02 + " #interaction dataset");

            String s03 = "FEAT_DATA_PATH = ";
            if (FEAT_DATA_PATH != null)
                s03 += FEAT_DATA_PATH;
            pw.println(s03 + " #Gene/protein FEATURES");

            String s2 = "R_DATA_PATH = ";
            if (R_DATA_PATH != null)
                s2 += R_DATA_PATH;
            pw.println(s2 + " #RData object file");

            String s3 = "MOVES_PATH = ";
            if (MOVES_PATH != null)
                s3 += MOVES_PATH;
            pw.println(s3 + " #Previous trajectory");

            String s4x = "MEANKEND_PATH = ";
            if (MEANKEND_PATH != null)
                s4x += MEANKEND_PATH;
            pw.println(s4x + " #Mean Kendall's Tau null distribution");

            String s4xa = "MEANKENDR_PATH = ";
            if (MEANKENDR_PATH != null)
                s4xa += MEANKENDR_PATH;
            pw.println(s4xa + " #Mean Kendall's Tau null distribution");

            String s4xx = "MEANKENDC_PATH = ";
            if (MEANKENDC_PATH != null)
                s4xx += MEANKENDC_PATH;
            pw.println(s4xx + " #Mean Kendall's Tau column null distribution");

            String s4xbb = "MEANBINARY_PATH = ";
            if (MEANBINARY_PATH != null)
                s4xbb += MEANBINARY_PATH;
            pw.println(s4xbb + " #Mean Binary null distribution");

            String s4xabb = "MEANBINARYR_PATH = ";
            if (MEANBINARYR_PATH != null)
                s4xabb += MEANBINARYR_PATH;
            pw.println(s4xabb + " #Mean BinaryR null distribution");

            String s4xxbb = "MEANBINARYC_PATH = ";
            if (MEANBINARYC_PATH != null)
                s4xxbb += MEANBINARYC_PATH;
            pw.println(s4xxbb + " #Mean BinaryC null distribution");

            String s4xaax = "MEANCOR_PATH = ";
            if (MEANCOR_PATH != null)
                s4xaax += MEANCOR_PATH;
            pw.println(s4xaax + " #Mean correlation null distribution");

            String s4xaa = "MEANCORR_PATH = ";
            if (MEANCORR_PATH != null)
                s4xaa += MEANCORR_PATH;
            pw.println(s4xaa + " #Mean row correlation null distribution");

            String s4xb = "MEANCORC_PATH = ";
            if (MEANCORC_PATH != null)
                s4xb += MEANCORC_PATH;
            pw.println(s4xb + " #Mean col correlation null distribution");

            String s4y = "MEANMADR_PATH = ";
            if (MEANMADR_PATH != null)
                s4y += MEANMADR_PATH;
            pw.println(s4y + " #MADR mean null distribution");

            String s4 = "MEANMSE_PATH = ";
            if (MEANMSE_PATH != null)
                s4 += MEANMSE_PATH;
            pw.println(s4 + " #MSE mean null distribution");

            String s4a = "MEANMSER_PATH = ";
            if (MEANMSER_PATH != null)
                s4a += MEANMSER_PATH;
            pw.println(s4a + " #MSER mean null distribution");

            String s4b = "MEANMSEC_PATH = ";
            if (MEANMSEC_PATH != null)
                s4b += MEANMSEC_PATH;
            pw.println(s4b + " #MSEC mean null distribution");

            String s6 = "MEANINTERACT_PATH = ";
            if (MEANINTERACT_PATH != null)
                s6 += MEANINTERACT_PATH;
            pw.println(s6 + " #Interaction proportion mean null distribution");

            String s4c = "MEANGEE_PATH = ";
            if (MEANGEE_PATH != null)
                s4c += MEANGEE_PATH;
            pw.println(s4c + " #GEERE mean null distribution");

            String s4ca = "MEANGEERE_PATH = ";
            if (MEANGEERE_PATH != null)
                s4ca += MEANGEERE_PATH;
            pw.println(s4ca + " #GEERE mean null distribution");

            String s4d = "MEANGEECE_PATH = ";
            if (MEANGEECE_PATH != null)
                s4d += MEANGEECE_PATH;
            pw.println(s4d + " #GEECE mean null distribution");

            String s4ex = "MEANLARS_PATH = ";
            if (MEANLARS_PATH != null)
                s4ex += MEANLARS_PATH;
            pw.println(s4ex + " #LARS mean null distribution");

            String s4e = "MEANLARSRE_PATH = ";
            if (MEANLARSRE_PATH != null)
                s4e += MEANLARSRE_PATH;
            pw.println(s4e + " #LARSRE mean null distribution");

            String s4f = "MEANLARSCE_PATH = ";
            if (MEANLARSCE_PATH != null)
                s4f += MEANLARSCE_PATH;
            pw.println(s4f + " #LARSCE mean null distribution");

            String s4eax = "MEANEUC_PATH = ";
            if (MEANEUC_PATH != null)
                s4eax += MEANEUC_PATH;
            pw.println(s4eax + " #EUC mean null distribution");

            String s4ea = "MEANEUCR_PATH = ";
            if (MEANEUCR_PATH != null)
                s4ea += MEANEUCR_PATH;
            pw.println(s4ea + " #EUCR mean null distribution");

            String s4fa = "MEANEUCC_PATH = ";
            if (MEANEUCC_PATH != null)
                s4fa += MEANEUCC_PATH;
            pw.println(s4fa + " #EUCC mean null distribution");

            String s4eaxx = "MEANSPEAR_PATH = ";
            if (MEANSPEAR_PATH != null)
                s4eaxx += MEANSPEAR_PATH;
            pw.println(s4eaxx + " #Spearman mean null distribution");

            String s4eaxxx = "MEANSPEARR_PATH = ";
            if (MEANSPEARR_PATH != null)
                s4eaxxx += MEANSPEARR_PATH;
            pw.println(s4eaxxx + " #Spearman mean null distribution");

            String s4faxxxx = "MEANSPEARC_PATH = ";
            if (MEANSPEARC_PATH != null)
                s4faxxxx += MEANSPEARC_PATH;
            pw.println(s4faxxxx + " #Spearman mean null distribution");

            String s5ha = "MEANMEAN_PATH = ";
            if (MEANMEAN_PATH != null)
                s5ha += MEANMEAN_PATH;
            pw.println(s5ha + " #Mean null distribution");

            String s5hc = "MEANRMEAN_PATH = ";
            if (MEANRMEAN_PATH != null)
                s5hc += MEANRMEAN_PATH;
            pw.println(s5hc + " #medianrmean Mean null distribution");

            String s5he = "MEANCMEAN_PATH = ";
            if (MEANCMEAN_PATH != null)
                s5he += MEANCMEAN_PATH;
            pw.println(s5he + " #mediancmean Mean null distribution");

            String s5hf = "MEANFEAT_PATH = ";
            if (MEANFEAT_PATH != null)
                s5hf += MEANFEAT_PATH;
            pw.println(s5hf + " #Mean feature null distribution");

            String s5h1 = "MEANTF_PATH = ";
            if (MEANTF_PATH != null)
                s5h1 += MEANTF_PATH;
            pw.println(s5h1 + " #Mean TF null distribution");


            String s5c = "SDKENDALL_PATH = ";
            if (SDKEND_PATH != null)
                s5c += SDKEND_PATH;
            pw.println(s5c + " #Kendall sd null distribution");

            String s5ca = "SDKENDALLR_PATH = ";
            if (SDKENDR_PATH != null)
                s5ca += SDKENDR_PATH;
            pw.println(s5ca + " #Kendall sd null distribution");

            String s5cc = "SDKENDALLC_PATH = ";
            if (SDKENDC_PATH != null)
                s5cc += SDKENDC_PATH;
            pw.println(s5cc + " #Kendall sd null column distribution");


            String s5cz = "SDBINARY_PATH = ";
            if (SDBINARY_PATH != null)
                s5cz += SDBINARY_PATH;
            pw.println(s5cz + " #Binary sd null distribution");

            String s5caz = "SDBINARYR_PATH = ";
            if (SDBINARYR_PATH != null)
                s5caz += SDBINARYR_PATH;
            pw.println(s5caz + " #BinaryR sd null distribution");

            String s5ccz = "SDBINARC_PATH = ";
            if (SDBINARYC_PATH != null)
                s5ccz += SDBINARYC_PATH;
            pw.println(s5ccz + " #BinaryC sd null column distribution");

            String s5caax = "SDCOR_PATH = ";
            if (SDCOR_PATH != null)
                s5caax += SDCOR_PATH;
            pw.println(s5caax + " #Mean correlation null distribution");

            String s5caa = "SDCORR_PATH = ";
            if (SDCORR_PATH != null)
                s5caa += SDCORR_PATH;
            pw.println(s5caa + " #Mean row correlation null distribution");

            String s5cb = "SDCORC_PATH = ";
            if (SDCORC_PATH != null)
                s5cb += SDCORC_PATH;
            pw.println(s5cb + " #Mean col correlation null distribution");

            String s5y = "SDMADR_PATH = ";
            if (SDMADR_PATH != null)
                s5y += SDMADR_PATH;
            pw.println(s5y + " #MADR SD null distribution");

            String s5x = "SDMSE_PATH = ";
            if (SDMSE_PATH != null)
                s5x += SDMSE_PATH;
            pw.println(s5x + " #MSE sd null distribution");

            String s5a = "SDMSER_PATH = ";
            if (SDMSER_PATH != null)
                s5a += SDMSER_PATH;
            pw.println(s5a + " #MSER SD null distribution");

            String s5b = "SDMSEC_PATH = ";
            if (SDMSEC_PATH != null)
                s5b += SDMSEC_PATH;
            pw.println(s5b + " #MSEC SD null distribution");

            String s5d = "SDINTERACT_PATH = ";
            if (SDINTERACT_PATH != null)
                s5d += SDINTERACT_PATH;
            pw.println(s5d + " #Interaction proportion SD null distribution");

            String s5e = "SDGEE_PATH = ";
            if (SDGEE_PATH != null)
                s5e += SDGEE_PATH;
            pw.println(s5e + " #GEE SD null distribution");

            String s5ea = "SDGEERE_PATH = ";
            if (SDGEERE_PATH != null)
                s5ea += SDGEERE_PATH;
            pw.println(s5ea + " #GEERE SD null distribution");

            String s5f = "SDGEECE_PATH = ";
            if (SDGEECE_PATH != null)
                s5f += SDGEECE_PATH;
            pw.println(s5f + " #GEECE SD null distribution");

            String s5gx = "SDLARS_PATH = ";
            if (SDLARS_PATH != null)
                s5gx += SDLARS_PATH;
            pw.println(s5gx + " #LARS SD null distribution");

            String s5g = "SDLARSRE_PATH = ";
            if (SDLARSRE_PATH != null)
                s5g += SDLARSRE_PATH;
            pw.println(s5g + " #LARSRE SD null distribution");

            String s5ga = "SDLARSCE_PATH = ";
            if (SDLARSCE_PATH != null)
                s5ga += SDLARSCE_PATH;
            pw.println(s5ga + " #LARSCE SD null distribution");

            String s5gaax = "SDEUC_PATH = ";
            if (SDEUC_PATH != null)
                s5gaax += SDEUC_PATH;
            pw.println(s5gaax + " #EUC SD null distribution");

            String s5gaa = "SDEUCR_PATH = ";
            if (SDEUCR_PATH != null)
                s5gaa += SDEUCR_PATH;
            pw.println(s5gaa + " #EUCR SD null distribution");

            String s5gaaa = "SDEUCC_PATH = ";
            if (SDEUCC_PATH != null)
                s5gaaa += SDEUCC_PATH;
            pw.println(s5gaaa + " #EUCC SD null distribution");


            String s5gaaxx = "SDSPEAR_PATH = ";
            if (SDSPEAR_PATH != null)
                s5gaaxx += SDSPEAR_PATH;
            pw.println(s5gaaxx + " #Spearman SD null distribution");

            String s5gaaxxx = "SDSPEARR_PATH = ";
            if (SDSPEARR_PATH != null)
                s5gaaxxx += SDSPEARR_PATH;
            pw.println(s5gaaxxx + " #SpearmanR SD null distribution");

            String s5gaaaxxxxx = "SDSPEARC_PATH = ";
            if (SDSPEARC_PATH != null)
                s5gaaaxxxxx += SDSPEARC_PATH;
            pw.println(s5gaaaxxxxx + " #SpearmanC SD null distribution");


            String s5hb = "SDMEAN_PATH = ";
            if (SDMEAN_PATH != null)
                s5hb += SDMEAN_PATH;
            pw.println(s5hb + " #SD null distribution");

            String s5hd = "SDRMEAN_PATH = ";
            if (SDRMEAN_PATH != null)
                s5hd += SDRMEAN_PATH;
            pw.println(s5hd + " #sdrmean null distribution");

            String s5hg = "SDCMEAN_PATH = ";
            if (SDCMEAN_PATH != null)
                s5hg += SDCMEAN_PATH;
            pw.println(s5hg + " #sdcmean null distribution");

            String s5hh = "SDFEAT_PATH = ";
            if (SDFEAT_PATH != null)
                s5hh += SDFEAT_PATH;
            pw.println(s5hh + " #feat SD null distribution");

            String s5h2 = "SDTF_PATH = ";
            if (SDTF_PATH != null)
                s5h2 += SDTF_PATH;
            pw.println(s5h2 + " #TF SD null distribution");


            String s6c = "TRAJECTORY_PATH = ";
            if (TRAJECTORY_PATH != null)
                s6c += TRAJECTORY_PATH;
            pw.println(s6c + " #Trajectory curve relating criterion to block size");

            String s6ca = "EXCLUDE_LIST_PATH = ";
            if (EXCLUDE_LIST_PATH != null)
                s6ca += EXCLUDE_LIST_PATH;
            pw.println(s6ca + " #List of blocks to exclude from possible moves");

            String s6a = "R_METHODS_PATH = ";
            if (R_METHODS_PATH != null)
                s6a += R_METHODS_PATH;
            pw.println(s6a + " #Path to R methods file");

            String s6b = "ANNOTATION_PATH = ";
            if (ANNOTATION_PATH != null)
                s6b += ANNOTATION_PATH;
            pw.println(s6b + " #Annotation file");

            String s6d = "TFTARGETMAP_PATH = ";
            if (TFTARGETMAP_PATH != null)
                s6d += TFTARGETMAP_PATH;
            pw.println(s6d + " #TF target map file");

            String s7 = "N_BLOCK = ";
            s7 += N_BLOCK;
            pw.println(s7 + " #Number of local minimum blocks user wishes to find");

            String s8 = "INIT_BLOCKS = {";
            s8 += BlockMethods.IcJctoijID(INIT_BLOCKS);
            pw.println(s8 + "} #Sets (currently 1) of initialization blocks for algorithm");

            String s9 = "NOISE_LEVEL = ";
            s9 += NOISE_LEVEL;
            pw.println(s9 + " #Permitted noise level for accepting move");

            String s10 = "INITPA = ";
            s10 += INITPA;
            pw.println(s10 + " #Initial probability of addition move");

            String s11 = "INITPG = ";
            s11 += INITPG;
            pw.println(s11 + " #Initial probability of selecting a gene");

            String s10a = "PA = ";
            s10a += PA;
            pw.println(s10a + " #Probability of addition move");

            String s11a = "PG = ";
            s11a += PG;
            pw.println(s11a + " #Probability of selecting a gene");


            String s13 = "MAXMOVES = ";
            s13 += "{" + MAXMOVES[0] + "," + MAXMOVES[1] + "}";
            pw.println(s13 + " #Max number of full Criteria moves in walk");

            String s12 = "SIZE_PRECRIT_LIST = ";
            if (SIZE_PRECRIT_LIST != -1)
                s12 += SIZE_PRECRIT_LIST;
            pw.println(s12 + " #Number of pre-criteria moves to consider for full criteria, exclusive with SIZE_PRECRIT_LIST_GENE & SIZE_PRECRIT_LIST_GENE");

            String s12a = "SIZE_PRECRIT_LIST_GENE = ";
            if (SIZE_PRECRIT_LIST_GENE != -1)
                s12a += SIZE_PRECRIT_LIST_GENE;
            pw.println(s12a + " #Number or fraction of pre-criteria gene moves to consider for full criteria or fraction of 0 < x < 1, exclusive with SIZE_PRECRIT_LIST_GENE");

            String s12b = "SIZE_PRECRIT_LIST_EXP = ";
            if (SIZE_PRECRIT_LIST_EXP != -1)
                s12b += SIZE_PRECRIT_LIST_EXP;
            pw.println(s12b + " #Number or fraction of pre-criteria exp moves to consider for full criteria or fraction of 0 < x < 1, exclusive with SIZE_PRECRIT_LIST_EXP");

            String s14 = "IMIN = ";
            if (IMIN != -1)
                s14 += IMIN;
            pw.println(s14 + " #Minimum number of rows/genes in block");

            String s15 = "IMAX = ";
            if (IMAX != -1)
                s15 += IMAX;
            pw.println(s15 + " #Maximum number of rows/genes in block");

            String s16 = "JMIN = ";
            if (JMIN != -1)
                s16 += JMIN;
            pw.println(s16 + " #Minimum number of columns/experiments in block");

            String s17 = "JMAX = ";
            if (JMAX != -1)
                s17 += JMAX;
            pw.println(s17 + " #Maximum number of columns/experiments in block");

            String s18 = "DATA_LEN_GENES = ";
            if (DATA_LEN_GENES != -1)
                s18 += DATA_LEN_GENES;
            pw.println(s18 + " #x dimensions of gene data");

            String s19 = "DATA_LEN_EXPS = ";
            if (DATA_LEN_EXPS != -1)
                s19 += DATA_LEN_EXPS;
            pw.println(s19 + " #y dimensions of experiment data");

            String s19a = "PERCENT_ALLOWED_MISSING_GENES = ";
            if (PERCENT_ALLOWED_MISSING_GENES != -1)
                s19a += PERCENT_ALLOWED_MISSING_GENES;
            pw.println(s19a + " #Allowed percent of missing gene data");

            String s19b = "PERCENT_ALLOWED_MISSING_EXP = ";
            if (PERCENT_ALLOWED_MISSING_EXP != -1)
                s19b += PERCENT_ALLOWED_MISSING_EXP;
            pw.println(s19b + " #Allowed percent of missing experiment data");

            String s19c = "PERCENT_ALLOWED_MISSING_IN_BLOCK = ";
            if (PERCENT_ALLOWED_MISSING_IN_BLOCK != -1)
                s19c += PERCENT_ALLOWED_MISSING_IN_BLOCK;
            pw.println(s19c + " #Allowed percent of missing data in block");

            String s21 = "TRUNCATE_DATA100 = ";
            s21 += TRUNCATE_DATA100;
            pw.println(s21 + " #Truncates the gene expression dataset to 100x100");

            String s22 = "RANDOMIZE_BLOCKS = ";
            s22 += RANDOMIZE_BLOCKS;
            pw.println(s22 + " #Creates random starting block based on entire datasets and block extents of user provided init_block");

            String s22a = "RANDOMIZE_GENES = ";
            s22a += RANDOMIZE_GENES;
            pw.println(s22a + " #Starting with init_block use the same experiments but sample the same number of " +
                    "random genes, overriden by RANDOMIZE_BLOCKS ");

            String s22b = "RANDOMIZE_EXPS = ";
            s22b += RANDOMIZE_EXPS;
            pw.println(s22b + " #Starting with init_block use the same genes but sample the same number of " +
                    "random experiments, overriden by RANDOMIZE_BLOCKS ");

            String s22c = "FIX_GENES = ";
            s22c += FIX_GENES;
            pw.println(s22c + " #Fix the starting genes (exclusive with RANDOMIZE_GENES and FIX_EXPS)");

            String s22d = "FIX_EXPS = ";
            s22d += FIX_EXPS;
            pw.println(s22d + " #Fix the starting experiments (exclusive with RANDOMIZE_EXPS and FIX_GENES)");

            String s23 = "USE_RANDOM_SEED = ";
            s23 += USE_RANDOM_SEED;
            pw.println(s23 + " #Use a random start seed integer or " + DEFAULT_RANDOM_SEED);

            String s23b = "RANDOM_SEED = ";
            //if (!RANDOM_SEED)
            s23b += RANDOM_SEED;
            pw.println(s23b + " #Use the specified seed. if empty then seed is random or " + DEFAULT_RANDOM_SEED + " depending on USE_RANDOM_SEED");

            String s23c = "BATCH_DMETHOD = ";
            s23c += BATCH_DMETHOD;
            pw.println(s23c + " #Use this distance measure for batch moves");

            String s23d = "BATCH_LINKMETHOD = ";
            s23d += BATCH_LINKMETHOD;
            pw.println(s23d + " #Use this linkage criterion for batch moves");

            String s25 = "CRIT_TYPE_INDEX = ";
            s25 += CRIT_TYPE_INDEX;
            String critstring = MINER_STATIC.makeCritString();
            pw.println(s25 + " # " + critstring);
            String s26 = "PRECRIT_TYPE_INDEX = ";
            s26 += PRECRIT_TYPE_INDEX;
            String precritstring = MINER_STATIC.makeCritString();
            pw.println(s26 + " # " + precritstring);

            String s27 = "FEATURE_INDICES = ";
            s27 += FEATURE_INDICES != null ? MoreArray.toString(MoreArray.ArrayListtoString(FEATURE_INDICES), ",") : "";
            pw.println(s27 + " #comma delimited list of =1 GO function, =2 GO process, =3 GO component, " +
                    "=4 gene knockout fitness, =5 protein properties, =6 Protein localization");

            String s280 = "GENE_SHAVE = " + GENE_SHAVE;
            pw.println(s280 + " #Shave genes, do not grow genes");

            String s281 = "EXP_SHAVE = " + EXP_SHAVE;
            pw.println(s281 + " #Shave experiments, do not grow experiments");

            String s282 = "GENE_GROW = " + GENE_GROW;
            pw.println(s282 + " #Grow genes, do not shave genes");

            String s283 = "EXP_GROW = " + EXP_GROW;
            pw.println(s283 + " #Grow experiments, do not shave experiments");

            String s286 = "WEIGH_EXPR = " + WEIGH_EXPR;
            pw.println(s286 + " #Weight expression criteria y/n");

            String s286a = "USE_MEAN = " + USE_MEAN;
            pw.println(s286a + " #use expression mean criterion (total, row, col - based on expression crit)");

            String s286b = "USE_ABS = " + MoreArray.toString(USE_ABS_AR, ",");
            pw.println(s286b + " #use absolute value of data for criteria");

            String s286cc = "FRXN_SIGN = " + FRXN_SIGN;
            pw.println(s286cc + " #Scale criterion value by fraction of maximum row/col sign of data for current block (also incorporated into null distribution)");

            String s286c = "OVERRIDE_SHAVING = " + OVERRIDE_SHAVING;
            pw.println(s286c + " #override gene and experiment shaving triggered by block out of bounds relative to null distribution");

            String s28 = "PLATEAU = " + PLATEAU;
            pw.println(s28 + " #Run PLATEAU step after search");

            String s284 = "PLATEAU_PERC = " + PLATEAU_PERC;
            pw.println(s284 + " #Percent of gene/exp to add/del in PLATEAU move");

            String s29 = "RANDOMFLOOD = " + RANDOMFLOOD;
            pw.println(s29 + " #Run RANDOMFLOOD step after search/PLATEAU");

            String s29b = "RANDOMFLOOD_PERC = " + RANDOMFLOOD_PERC;
            pw.println(s29b + " #Percent of random genes to add for RANDOMFLOOD");

            String s11b = "PBATCH = " + PBATCH;
            pw.println(s11b + " #Probability of a batch move");

            String s29c = "BATCH_PERC = " + BATCH_PERC;
            pw.println(s29c + " #Percent of gene/exp to add/del in batch moves");

            String s29d = "RUN_SEQUENCE = " + (RUN_SEQUENCE != null ? RUN_SEQUENCE : "");
            pw.println(s29d + " #Sequence of move classes: B=batch, S=single, N=single convergence");//M=mixed, R=random, P=PLATEAU,c=combine. Lower case = precrit+fullcrit, upper case = full crit

            String s29e = "LIB_LOC = " + (LIB_LOC != null ? LIB_LOC : "");
            pw.println(s29e + " #Location of R package libraries");

            String s29f = "EXCLUDE_OVERLAP_THRESHOLD = " + (!Double.isNaN(EXCLUDE_OVERLAP_THRESHOLD) ? EXCLUDE_OVERLAP_THRESHOLD : "");
            pw.println(s29f + " #block overlap threshold for exclusion: 1 == string match identity, overlap comparison otherwise");

            String s29g = "MIN_NONMISSING_FOR_BATCH = " + MIN_NONMISSING_FOR_BATCH;
            pw.println(s29g + " #minimum size of batch move");

            String s30a = "DEBUG = " + debug;
            pw.println(s30a + " #toggle debug mode: 0 - none, 1 - base, 2 - block precrit, 3 - precrit list");

            String s30 = "CHECKSUM_SEED = " + CHECKSUM_SEED;
            pw.println(s30 + " #Random seed added to string for file id checksum");

            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @param str
     */
    public void extractCoords(String str) {
        if (debug >= 0)
            System.out.println("Parameters " + str);
        int[][] get = extractGenesandExpsfromString(str);

        Arrays.sort(get[0]);
        Arrays.sort(get[1]);

        INIT_BLOCKS[0] = MoreArray.addElements(INIT_BLOCKS[0], get[0]);
        INIT_BLOCKS[1] = MoreArray.addElements(INIT_BLOCKS[1], get[1]);
        //Collections.sort(INIT_BLOCKS[0]);
        //Collections.sort(INIT_BLOCKS[1]);

        /*System.out.println("Parameters Gene indices:");
        MoreArray.printArray(get[0]);
        System.out.println("Parameters Experiment indices:");
        MoreArray.printArray(get[1]);*/
        //init_block_list = new ArrayList();
        init_block_list.add(INIT_BLOCKS);
    }

    /**
     * @param str
     * @return
     */
    public final static int[][] extractGenesandExpsfromString(String str) {
        String delim = "/";
        int index = str.indexOf(delim);
        if (index == -1) {
            delim = "/";
            index = str.indexOf(delim);
        }
        String first = str.substring(0, index);
        String second = str.substring(index + delim.length(), str.length());
        int[] x = getGenesfromString(first);
        /*System.out.println("Parameters x");
        MoreArray.printArray(x);*/
        int[] y = getExpsfromString(second);
        /*System.out.println("Parameters y");
        MoreArray.printArray(y);*/
        int[][] ret = new int[2][];
        ret[0] = x;
        ret[1] = y;
        return ret;
    }

    /**
     * @param second
     * @return
     */
    public final static int[] getExpsfromString(String second) {
        int[] y = null;
        if (second.indexOf(",") != -1) {
            String[] in = StringUtil.parsetoArray(second, ",");
            int size1 = in.length;
            in = StringUtil.makeUnique(in);
            int size2 = in.length;
            if (size1 != size2) {
                System.out.println("WARNING: there are duplicate entries in the initial exp list");
            }
            y = MoreArray.tointArray(in);
        } else {
            y = new int[1];
            try {
                y[0] = Integer.parseInt(second);
            } catch (NumberFormatException e) {
                //e.printStackTrace();
            }
        }
        return y;
    }

    /**
     * @param first
     * @return
     */
    public final static int[] getGenesfromString(String first) {
        int[] x = null;
        if (first.indexOf(",") != -1) {
            String[] in = StringUtil.parsetoArray(first, ",");
            int size1 = in.length;
            //System.out.println("getGenesfromString b/f unique " + MoreArray.toString(in, ","));
            in = StringUtil.makeUnique(in);
            //System.out.println("getGenesfromString a/f unique " + MoreArray.toString(in, ","));
            int size2 = in.length;
            if (size1 != size2) {
                System.out.println("WARNING: there are duplicate entries in the initial gene list");
            }
            x = MoreArray.tointArray(in);
        } else {
            x = new int[1];
            try {
                x[0] = Integer.parseInt(first);
            } catch (NumberFormatException e) {
                //e.printStackTrace();
            }
        }
        return x;
    }

    /**
     * Used only by CreateParamSet
     *
     * @param k
     */
    public void setCrit(int k) {
        setCrit(k, false);
    }

    /**
     * Used only by CreateParamSet
     *
     * @param k
     */
    public void setCrit(int k, boolean full) {
        //System.out.println("setCrit " + k + "\t" + this.PRECRIT_TYPE_INDEX);
        CRIT_TYPE_INDEX = k;
        setInvert();

        crit = new Criterion(CRIT_TYPE_INDEX, USE_MEAN, true, USE_ABS_AR, TFTARGETMAP_PATH != null ? true : false,
                needinv, true, FRXN_SIGN, debug > 0 ? true : false);
        //sets the precrit to the same axis as the main crit
        setPreCrit(full);
    }

    /**
     * Used only by CreateParamSet
     */
    public void setPreCrit(boolean full) {

        if (full == false) {
            /*
            if (this.PRECRIT_TYPE_INDEX == -1) {
                if (precrit_type.equals("KEND"))
                    if (crit.isRowCrit) {
                        PRECRIT_TYPE_INDEX = 6;
                        System.out.println("setPreCrit row " + PRECRIT_TYPE_INDEX);
                    } else if (crit.isColCrit) {
                        PRECRIT_TYPE_INDEX = 132;
                        System.out.println("setPreCrit col " + PRECRIT_TYPE_INDEX);
                    } else if (crit.isTotalCrit) {
                        PRECRIT_TYPE_INDEX = 6;
                        System.out.println("setPreCrit total " + PRECRIT_TYPE_INDEX);
                    }
                //MSE
                if (precrit_type.equals("MSE") || precrit_type.equals("MSEC"))
                    if (crit.isRowCrit) {
                        PRECRIT_TYPE_INDEX = 4;
                        System.out.println("setPreCrit row " + PRECRIT_TYPE_INDEX);
                    } else if (crit.isColCrit) {
                        PRECRIT_TYPE_INDEX = 26;
                        System.out.println("setPreCrit col " + PRECRIT_TYPE_INDEX);
                    } else if (crit.isTotalCrit) {
                        PRECRIT_TYPE_INDEX = 24;
                        System.out.println("setPreCrit total " + PRECRIT_TYPE_INDEX);
                    }

                //MSE + Kendall + KendallC
                if (precrit_type.equalsIgnoreCase("MSEKend") || precrit_type.equalsIgnoreCase("MSE_Kendall"))
                    if (crit.isRowCrit) {
                        PRECRIT_TYPE_INDEX = 77;//MSER + Kendall
                        System.out.println("setPreCrit row " + PRECRIT_TYPE_INDEX);
                    } else if (crit.isColCrit) {
                        PRECRIT_TYPE_INDEX = 133;//MSEC + KendallC
                        System.out.println("setPreCrit col " + PRECRIT_TYPE_INDEX);
                    } else if (crit.isTotalCrit) {
                        PRECRIT_TYPE_INDEX = 134;//MSE + Kendall + KendallC
                        System.out.println("setPreCrit total " + PRECRIT_TYPE_INDEX);
                    }
                System.out.println("setPreCrit crit " + CRIT_TYPE_INDEX + "\tprecrit " + PRECRIT_TYPE_INDEX);

            }
            */
            if (PRECRIT_TYPE_INDEX != -1) {
                //System.out.println("Pre-criterion set " + PRECRIT_TYPE_INDEX);
                setInvert();

                precrit = new Criterion(PRECRIT_TYPE_INDEX, true, true, USE_ABS_AR, TFTARGETMAP_PATH != null ? true : false,
                        needinv, true, FRXN_SIGN, debug > 0 ? true : false);
            } else {
                System.out.println("Pre-criterion requested but not specified. Defaulting to auto-detected MSE/MSER/MSEC.");
                if (crit.isRowCrit) {
                    PRECRIT_TYPE_INDEX = MoreArray.getArrayIndIgnoreCase(MINER_STATIC.CRIT_LABELS, "MSER");
                    System.out.println("setPreCrit row " + PRECRIT_TYPE_INDEX);
                } else if (crit.isColCrit) {
                    PRECRIT_TYPE_INDEX = MoreArray.getArrayIndIgnoreCase(MINER_STATIC.CRIT_LABELS, "MSEC");
                    System.out.println("setPreCrit col " + PRECRIT_TYPE_INDEX);
                } else if (crit.isTotalCrit) {
                    PRECRIT_TYPE_INDEX = MoreArray.getArrayIndIgnoreCase(MINER_STATIC.CRIT_LABELS, "MSE");
                    System.out.println("setPreCrit total " + PRECRIT_TYPE_INDEX);
                }
                System.out.println("setPreCrit crit " + CRIT_TYPE_INDEX + "\tprecrit " + PRECRIT_TYPE_INDEX);
            }
        } else {
            precrit = new Criterion(CRIT_TYPE_INDEX, USE_MEAN, true, USE_ABS_AR, TFTARGETMAP_PATH != null ? true : false,
                    needinv, true, FRXN_SIGN, debug > 0 ? true : false);
        }
    }

    /**
     * @return
     */

    public String toString() {
        String ret = "";
        //EXPR_DATA_PATH, INTERACT_DATA_PATH, FEAT_DATA_PATH,
        ret += "OUTDIR = " + OUTDIR + "\n";
        ret += "OUTPREFIX = " + OUTPREFIX + "\n";
        ret += "EXPR_DATA_PATH = " + EXPR_DATA_PATH + "\n";
        ret += "INTERACT_DATA_PATH = " + INTERACT_DATA_PATH + "\n";
        ret += "FEAT_DATA_PATH = " + FEAT_DATA_PATH + "\n";
        ret += "MEANKEND_PATH = " + MEANKEND_PATH + "\n";
        ret += "MEANKENDR_PATH = " + MEANKENDR_PATH + "\n";
        ret += "MEANKENDC_PATH = " + MEANKENDC_PATH + "\n";
        ret += "MEANBINARY_PATH = " + MEANBINARY_PATH + "\n";
        ret += "MEANBINARYR_PATH = " + MEANBINARYR_PATH + "\n";
        ret += "MEANBINARYC_PATH = " + MEANBINARYC_PATH + "\n";
        ret += "MEANCOR_PATH = " + MEANCOR_PATH + "\n";
        ret += "MEANCORR_PATH = " + MEANCORR_PATH + "\n";
        ret += "MEANCORC_PATH = " + MEANCORC_PATH + "\n";
        ret += "MEANMADR_PATH = " + MEANMADR_PATH + "\n";
        ret += "MEANMSE_PATH = " + MEANMSE_PATH + "\n";
        ret += "MEANMSER_PATH = " + MEANMSER_PATH + "\n";
        ret += "MEANMSEC_PATH = " + MEANMSEC_PATH + "\n";
        ret += "MEANINTERACT_PATH = " + MEANINTERACT_PATH + "\n";
        ret += "MEANGEE_PATH = " + MEANGEE_PATH + "\n";
        ret += "MEANGEERE_PATH = " + MEANGEERE_PATH + "\n";
        ret += "MEANGEECE_PATH = " + MEANGEECE_PATH + "\n";
        ret += "MEANLARS_PATH = " + MEANLARS_PATH + "\n";
        ret += "MEANLARSRE_PATH = " + MEANLARSRE_PATH + "\n";
        ret += "MEANLARSCE_PATH = " + MEANLARSCE_PATH + "\n";
        ret += "MEANEUC_PATH = " + MEANEUC_PATH + "\n";
        ret += "MEANEUCR_PATH = " + MEANEUCR_PATH + "\n";
        ret += "MEANEUCC_PATH = " + MEANEUCC_PATH + "\n";
        ret += "MEANSPEAR_PATH = " + MEANSPEAR_PATH + "\n";
        ret += "MEANSPEARR_PATH = " + MEANSPEARR_PATH + "\n";
        ret += "MEANSPEARC_PATH = " + MEANSPEARC_PATH + "\n";
        ret += "MEANMEAN_PATH = " + MEANMEAN_PATH + "\n";
        ret += "MEANRMEAN_PATH = " + MEANRMEAN_PATH + "\n";
        ret += "MEANCMEAN_PATH = " + MEANCMEAN_PATH + "\n";
        ret += "MEANFEAT_PATH = " + MEANFEAT_PATH + "\n";
        ret += "MEANTF_PATH = " + MEANTF_PATH + "\n";

        ret += "SDKEND_PATH = " + SDKEND_PATH + "\n";
        ret += "SDKENDR_PATH = " + SDKENDR_PATH + "\n";
        ret += "SDKENDC_PATH = " + SDKENDC_PATH + "\n";
        ret += "SDBINARY_PATH = " + SDBINARY_PATH + "\n";
        ret += "SDBINARYR_PATH = " + SDBINARYR_PATH + "\n";
        ret += "SDBINARYC_PATH = " + SDBINARYC_PATH + "\n";
        ret += "SDCOR_PATH = " + SDCOR_PATH + "\n";
        ret += "SDCORR_PATH = " + SDCORR_PATH + "\n";
        ret += "SDCORC_PATH = " + SDCORC_PATH + "\n";
        ret += "SDMADR_PATH = " + SDMADR_PATH + "\n";
        ret += "SDMSE_PATH = " + SDMSE_PATH + "\n";
        ret += "SDMSER_PATH = " + SDMSER_PATH + "\n";
        ret += "SDMSEC_PATH = " + SDMSEC_PATH + "\n";
        ret += "SDINTERACT_PATH = " + SDINTERACT_PATH + "\n";
        ret += "SDGEE_PATH = " + SDGEE_PATH + "\n";
        ret += "SDGEERE_PATH = " + SDGEERE_PATH + "\n";
        ret += "SDGEECE_PATH = " + SDGEECE_PATH + "\n";
        ret += "SDLARS_PATH = " + SDLARS_PATH + "\n";
        ret += "SDLARSRE_PATH = " + SDLARSRE_PATH + "\n";
        ret += "SDLARSCE_PATH = " + SDLARSCE_PATH + "\n";
        ret += "SDEUC_PATH = " + SDEUC_PATH + "\n";
        ret += "SDEUCR_PATH = " + SDEUCR_PATH + "\n";
        ret += "SDEUCC_PATH = " + SDEUCC_PATH + "\n";
        ret += "SDSPEAR_PATH = " + SDSPEAR_PATH + "\n";
        ret += "SDSPEARR_PATH = " + SDSPEARR_PATH + "\n";
        ret += "SDSPEARC_PATH = " + SDSPEARC_PATH + "\n";
        ret += "SDMEAN_PATH = " + SDMEAN_PATH + "\n";
        ret += "SDRMEAN_PATH = " + SDRMEAN_PATH + "\n";
        ret += "SDCMEAN_PATH = " + SDCMEAN_PATH + "\n";
        ret += "SDFEAT_PATH = " + SDFEAT_PATH + "\n";
        ret += "SDTF_PATH = " + SDTF_PATH + "\n";

        ret += "R_DATA_PATH = " + R_DATA_PATH + "\n";
        ret += "MOVES_PATH = " + MOVES_PATH + "\n";
        ret += "R_METHODS_PATH = " + R_METHODS_PATH + "\n";
        ret += "ANNOTATION_PATH = " + ANNOTATION_PATH + "\n";
        ret += "TFTARGETMAP_PATH = " + TFTARGETMAP_PATH + "\n";
        ret += "EXCLUDE_LIST_PATH = " + EXCLUDE_LIST_PATH + "\n";

        ret += "N_BLOCK = " + N_BLOCK + "\n";
        if (INIT_BLOCKS == null || INIT_BLOCKS[0] == null || INIT_BLOCKS[1] == null) {
            ret += "INIT_BLOCKS = \n";
        } else if (init_block_list == null || init_block_list.size() == 0) {
            System.out.println("toString init_block_list is null");
            ret += "INIT_BLOCKS = " + MoreArray.toString(MoreArray.ArrayListtoInt(INIT_BLOCKS[0]), ",") + "/" +
                    MoreArray.toString(MoreArray.ArrayListtoInt(INIT_BLOCKS[1]), ",") + "\n";
            //System.exit(0);
        } else {
            //System.out.println("toString init_block_list is not null " + init_block_list.size());
            for (int i = 0; i < init_block_list.size(); i++) {
                ArrayList[] curblock = (ArrayList[]) init_block_list.get(i);
                ret += "INIT_BLOCKS " + i + " = " + MoreArray.toString(MoreArray.ArrayListtoInt(curblock[0]), ",") + "/" +
                        MoreArray.toString(MoreArray.ArrayListtoInt(curblock[1]), ",") + "\n";
            }
        }
        ret += "NOISE_LEVEL = " + NOISE_LEVEL + "\n";
        ret += "INITPA = " + INITPA + "\n";
        ret += "INITPG = " + INITPG + "\n";

        ret += "PA = " + PA + "\n";
        ret += "PG = " + PG + "\n";
        ret += "PBATCH = " + PBATCH + "\n";

        ret += "MAXMOVES[0] = " + MAXMOVES[0] + "\n";
        ret += "MAXMOVES[1] = " + MAXMOVES[1] + "\n";
        ret += "SIZE_PRECRIT_LIST = " + SIZE_PRECRIT_LIST + "\n";
        ret += "SIZE_PRECRIT_LIST_GENE = " + SIZE_PRECRIT_LIST_GENE + "\n";
        ret += "SIZE_PRECRIT_LIST_EXP = " + SIZE_PRECRIT_LIST_EXP + "\n";
        ret += "IMIN = " + IMIN + "\n";
        ret += "JMIN = " + JMIN + "\n";
        ret += "IMAX = " + IMAX + "\n";
        ret += "JMAX = " + JMAX + "\n";
        ret += "DATA_LEN_GENES = " + DATA_LEN_GENES + "\n";
        ret += "DATA_LEN_EXPS = " + DATA_LEN_EXPS + "\n";

        ret += "PERCENT_ALLOWED_MISSING_GENES = " + PERCENT_ALLOWED_MISSING_GENES + "\n";
        ret += "PERCENT_ALLOWED_MISSING_EXP = " + PERCENT_ALLOWED_MISSING_EXP + "\n";
        ret += "TRUNCATE_DATA100 = " + TRUNCATE_DATA100 + "\n";
        ret += "RANDOMIZE_BLOCKS = " + RANDOMIZE_BLOCKS + "\n";
        ret += "RANDOMIZE_GENES = " + RANDOMIZE_GENES + "\n";
        ret += "RANDOMIZE_EXPS = " + RANDOMIZE_EXPS + "\n";
        ret += "FIX_GENES = " + FIX_GENES + "\n";
        ret += "FIX_EXPS = " + FIX_EXPS + "\n";
        ret += "GENE_SHAVE = " + GENE_SHAVE + "\n";
        ret += "EXP_SHAVE = " + EXP_SHAVE + "\n";
        ret += "GENE_GROW = " + GENE_GROW + "\n";
        ret += "EXP_GROW = " + EXP_GROW + "\n";
        ret += "WEIGH_EXPR = " + WEIGH_EXPR + "\n";
        ret += "USE_MEAN = " + USE_MEAN + "\n";
        ret += "USE_ABS = " + MoreArray.toString(USE_ABS_AR, ",") + "\n";
        ret += "FRXN_SIGN = " + FRXN_SIGN + "\n";
        ret += "OVERRIDE_SHAVING = " + OVERRIDE_SHAVING + "\n";
        ret += "USE_RANDOM_SEED = " + USE_RANDOM_SEED + "\n";
        if (USE_RANDOM_SEED)
            ret += "RANDOM_SEED = " + "\n";
        else
            ret += "RANDOM_SEED = " + RANDOM_SEED + "\n";
        ret += "BATCH_DMETHOD = " + BATCH_DMETHOD + "\n";
        ret += "BATCH_LINKMETHOD = " + BATCH_LINKMETHOD + "\n";
        ret += "CRIT_TYPE_INDEX = " + CRIT_TYPE_INDEX + "\n";
        ret += "PRECRIT_TYPE_INDEX = " + PRECRIT_TYPE_INDEX + "\n";
        ret += "FEATURE_INDICES = " + (FEATURE_INDICES != null ? MoreArray.toString(MoreArray.ArrayListtoString(FEATURE_INDICES), ",") + " \n" : " \n");
        ret += "PLATEAU = " + PLATEAU + "\n";
        ret += "PLATEAU_PERC = " + PLATEAU_PERC + "\n";
        ret += "RANDOMFLOOD = " + RANDOMFLOOD + "\n";
        ret += "RANDOMFLOOD_PERC = " + RANDOMFLOOD_PERC + "\n";
        ret += "BATCH_PERC = " + BATCH_PERC + "\n";
        ret += "RUN_SEQUENCE = " + RUN_SEQUENCE + "\n";
        ret += "LIB_LOC = " + LIB_LOC + "\n";
        ret += "EXCLUDE_OVERLAP_THRESHOLD = " + EXCLUDE_OVERLAP_THRESHOLD + "\n";
        ret += "PRECRIT_TYPE = " + precrit_type + "\n";
        ret += "DEBUG = " + debug + "\n";
        ret += "CHECKSUM_SEED = " + CHECKSUM_SEED + "\n";

        return ret;
    }


    /**
     *
     */
    public void checkParams() {

        File o = new File(OUTDIR);
        if (o.exists()) {
            System.out.println("OUTDIR exists " + OUTDIR);
        } else {
            System.out.println("FATAL OUTDIR DNE " + OUTDIR);
        }

        System.out.println("OUTPREFIX " + OUTPREFIX);


        //paths
        File e = new File(EXPR_DATA_PATH);
        if (EXPR_DATA_PATH != null && e.exists()) {
            System.out.println("EXPR_DATA_PATH exists " + EXPR_DATA_PATH);
        } else {
            System.out.println("EXPR_DATA_PATH DNE " + EXPR_DATA_PATH);
        }
        if (INTERACT_DATA_PATH != null) {
            File p = new File(INTERACT_DATA_PATH);
            if (p.exists()) {
                System.out.println("INTERACT_DATA_PATH exists " + INTERACT_DATA_PATH);
            } else {
                System.out.println("INTERACT_DATA_PATH DNE " + INTERACT_DATA_PATH);
            }
        }

        File r = new File(R_DATA_PATH);
        if (R_DATA_PATH != null && r.exists()) {
            System.out.println("R_DATA_PATH exists " + R_DATA_PATH);
        } else {
            System.out.println("FATAL R_DATA_PATH DNE " + R_DATA_PATH);
        }
        if (MOVES_PATH != null) {
            File m = new File(MOVES_PATH);
            if (m.exists()) {
                System.out.println("MOVES_PATH exists " + MOVES_PATH);
            } else {
                System.out.println("MOVES_PATH DNE " + MOVES_PATH);
            }
        }

        //criteria null paths
        if (MEANKEND_PATH != null) {
            File me = new File(MEANKEND_PATH);
            if (me.exists()) {
                System.out.println("MEANKEND_PATH exists " + MEANKEND_PATH);
            } else {
                System.out.println("MEANKEND_PATH DNE " + MEANKEND_PATH);
            }
        }

        if (MEANKENDR_PATH != null) {
            File me = new File(MEANKENDR_PATH);
            if (me.exists()) {
                System.out.println("MEANKENDR_PATH exists " + MEANKENDR_PATH);
            } else {
                System.out.println("MEANKENDR_PATH DNE " + MEANKENDR_PATH);
            }
        }

        if (MEANKENDC_PATH != null) {
            File mec = new File(MEANKENDC_PATH);
            if (mec.exists()) {
                System.out.println("MEANKENDC_PATH exists " + MEANKENDC_PATH);
            } else {
                System.out.println("MEANKENDC_PATH DNE " + MEANKENDC_PATH);
            }
        }

        if (MEANBINARY_PATH != null) {
            File me = new File(MEANBINARY_PATH);
            if (me.exists()) {
                System.out.println("MEANBINARY_PATH exists " + MEANBINARY_PATH);
            } else {
                System.out.println("MEANBINARY_PATH DNE " + MEANBINARY_PATH);
            }
        }

        if (MEANBINARYR_PATH != null) {
            File me = new File(MEANBINARYR_PATH);
            if (me.exists()) {
                System.out.println("MEANBINARYR_PATH exists " + MEANBINARYR_PATH);
            } else {
                System.out.println("MEANBINARYR_PATH DNE " + MEANBINARYR_PATH);
            }
        }

        if (MEANBINARYC_PATH != null) {
            File mec = new File(MEANBINARYC_PATH);
            if (mec.exists()) {
                System.out.println("MEANBINARYC_PATH exists " + MEANBINARYC_PATH);
            } else {
                System.out.println("MEANBINARYC_PATH DNE " + MEANBINARYC_PATH);
            }
        }

        if (MEANGEE_PATH != null) {
            File mecr = new File(MEANGEE_PATH);
            if (mecr.exists()) {
                System.out.println("MEANGEE_PATH exists " + MEANGEE_PATH);
            } else {
                System.out.println("MEANGEE_PATH DNE " + MEANGEE_PATH);
            }
        }

        if (MEANGEERE_PATH != null) {
            File mecr = new File(MEANGEERE_PATH);
            if (mecr.exists()) {
                System.out.println("MEANGEERE_PATH exists " + MEANGEERE_PATH);
            } else {
                System.out.println("MEANGEERE_PATH DNE " + MEANGEERE_PATH);
            }
        }

        if (MEANGEECE_PATH != null) {
            File mecc = new File(MEANGEECE_PATH);
            if (mecc.exists()) {
                System.out.println("MEANGEECE_PATH exists " + MEANGEECE_PATH);
            } else {
                System.out.println("MEANGEECE_PATH DNE " + MEANGEECE_PATH);
            }
        }
        if (MEANLARS_PATH != null) {
            File mecr = new File(MEANLARS_PATH);
            if (mecr.exists()) {
                System.out.println("MEANLARS_PATH exists " + MEANLARS_PATH);
            } else {
                System.out.println("MEANLARS_PATH DNE " + MEANLARS_PATH);
            }
        }

        if (MEANLARSRE_PATH != null) {
            File mecr = new File(MEANLARSRE_PATH);
            if (mecr.exists()) {
                System.out.println("MEANLARSRE_PATH exists " + MEANLARSRE_PATH);
            } else {
                System.out.println("MEANLARSRE_PATH DNE " + MEANLARSRE_PATH);
            }
        }

        if (MEANLARSCE_PATH != null) {
            File mecc = new File(MEANLARSCE_PATH);
            if (mecc.exists()) {
                System.out.println("MEANLARSCE_PATH exists " + MEANLARSCE_PATH);
            } else {
                System.out.println("MEANLARSCE_PATH DNE " + MEANLARSCE_PATH);
            }
        }

        if (MEANCOR_PATH != null) {
            File mecr = new File(MEANCOR_PATH);
            if (mecr.exists()) {
                System.out.println("MEANCOR_PATH exists " + MEANCOR_PATH);
            } else {
                System.out.println("MEANCOR_PATH DNE " + MEANCOR_PATH);
            }
        }

        if (MEANCORR_PATH != null) {
            File mecr = new File(MEANCORR_PATH);
            if (mecr.exists()) {
                System.out.println("MEANCORR_PATH exists " + MEANCORR_PATH);
            } else {
                System.out.println("MEANCORR_PATH DNE " + MEANCORR_PATH);
            }
        }

        if (MEANCORC_PATH != null) {
            File mecc = new File(MEANCORC_PATH);
            if (mecc.exists()) {
                System.out.println("MEANCORC_PATH exists " + MEANCORC_PATH);
            } else {
                System.out.println("MEANCORC_PATH DNE " + MEANCORC_PATH);
            }
        }


        if (MEANMADR_PATH != null) {
            File ma = new File(MEANMADR_PATH);
            if (ma.exists()) {
                System.out.println("MEANMADR_PATH exists " + MEANMADR_PATH);
            } else {
                System.out.println("MEANMADR_PATH DNE " + MEANMADR_PATH);
            }
        }

        if (MEANMSE_PATH != null) {
            File mse = new File(MEANMSE_PATH);
            if (mse.exists()) {
                System.out.println("MEANMSE_PATH exists " + MEANMSE_PATH);
            } else {
                System.out.println("MEANMSE_PATH DNE " + MEANMSE_PATH);
            }
        }

        if (MEANMSER_PATH != null) {
            File mser = new File(MEANMSER_PATH);
            if (mser.exists()) {
                System.out.println("MEANMSER_PATH exists " + MEANMSER_PATH);
            } else {
                System.out.println("MEANMSER_PATH DNE " + MEANMSER_PATH);
            }
        }

        if (MEANMSEC_PATH != null) {
            File msec = new File(MEANMSEC_PATH);
            if (msec.exists()) {
                System.out.println("MEANMSEC_PATH exists " + MEANMSEC_PATH);
            } else {
                System.out.println("MEANMSEC_PATH DNE " + MEANMSEC_PATH);
            }
        }

        if (MEANMEAN_PATH != null) {
            File mea = new File(MEANMEAN_PATH);
            if (mea.exists()) {
                System.out.println("MEANMEAN_PATH exists " + MEANMEAN_PATH);
            } else {
                System.out.println("MEANMEAN_PATH DNE " + MEANMEAN_PATH);
            }
        }

        if (MEANRMEAN_PATH != null) {
            File meac = new File(MEANRMEAN_PATH);
            if (meac.exists()) {
                System.out.println("MEANRMEAN_PATH exists " + MEANRMEAN_PATH);
            } else {
                System.out.println("MEANRMEAN_PATH DNE " + MEANRMEAN_PATH);
            }
        }

        if (MEANCMEAN_PATH != null) {
            File mear = new File(MEANCMEAN_PATH);
            if (mear.exists()) {
                System.out.println("MEANCMEAN_PATH exists " + MEANCMEAN_PATH);
            } else {
                System.out.println("MEANCMEAN_PATH DNE " + MEANCMEAN_PATH);
            }
        }


        if (MEANGEE_PATH != null) {
            File mear = new File(MEANGEE_PATH);
            if (mear.exists()) {
                System.out.println("MEANGEE_PATH exists " + MEANGEE_PATH);
            } else {
                System.out.println("MEANGEE_PATH DNE " + MEANGEE_PATH);
            }
        }
        if (MEANGEERE_PATH != null) {
            File mear = new File(MEANGEERE_PATH);
            if (mear.exists()) {
                System.out.println("MEANGEERE_PATH exists " + MEANGEERE_PATH);
            } else {
                System.out.println("MEANGEERE_PATH DNE " + MEANGEERE_PATH);
            }
        }
        if (MEANGEECE_PATH != null) {
            File mear = new File(MEANGEECE_PATH);
            if (mear.exists()) {
                System.out.println("MEANGEECE_PATH exists " + MEANGEECE_PATH);
            } else {
                System.out.println("MEANGEECE_PATH DNE " + MEANGEECE_PATH);
            }
        }


        if (MEANFEAT_PATH != null) {
            File mf = new File(MEANFEAT_PATH);
            if (mf.exists()) {
                System.out.println("MEANFEAT_PATH exists " + MEANFEAT_PATH);
            } else {
                System.out.println("MEANFEAT_PATH DNE " + MEANFEAT_PATH);
            }
        }

        if (MEANTF_PATH != null) {
            File mtf = new File(MEANTF_PATH);
            if (mtf.exists()) {
                System.out.println("MEANTF_PATH exists " + MEANTF_PATH);
            } else {
                System.out.println("MEANTF_PATH DNE " + MEANTF_PATH);
            }
        }

        if (MEANINTERACT_PATH != null) {
            File pi = new File(MEANINTERACT_PATH);
            if (pi.exists()) {
                System.out.println("MEANINTERACT_PATH exists " + MEANINTERACT_PATH);
            } else {
                System.out.println("MEANINTERACT_PATH DNE " + MEANINTERACT_PATH);
            }
        }

        if (SDKEND_PATH != null) {
            File sme = new File(SDKEND_PATH);
            if (sme.exists()) {
                System.out.println("SDKEND_PATH exists " + SDKEND_PATH);
            } else {
                System.out.println("SDKEND_PATH DNE " + SDKEND_PATH);
            }
        }

        if (SDKENDR_PATH != null) {
            File smec = new File(SDKENDR_PATH);
            if (smec.exists()) {
                System.out.println("SDKENDR_PATH exists " + SDKENDR_PATH);
            } else {
                System.out.println("SDKENDRR_PATH DNE " + SDKENDR_PATH);
            }
        }
        if (SDKENDC_PATH != null) {
            File smec = new File(SDKENDC_PATH);
            if (smec.exists()) {
                System.out.println("SDKENDC_PATH exists " + SDKENDC_PATH);
            } else {
                System.out.println("SDKENDC_PATH DNE " + SDKENDC_PATH);
            }
        }

        if (SDBINARY_PATH != null) {
            File sme = new File(SDBINARY_PATH);
            if (sme.exists()) {
                System.out.println("SDBINARY_PATH exists " + SDBINARY_PATH);
            } else {
                System.out.println("SDBINARY_PATH DNE " + SDBINARY_PATH);
            }
        }

        if (SDBINARYR_PATH != null) {
            File smec = new File(SDBINARYR_PATH);
            if (smec.exists()) {
                System.out.println("SDBINARYR_PATH exists " + SDBINARYR_PATH);
            } else {
                System.out.println("SDBINARYRR_PATH DNE " + SDBINARYR_PATH);
            }
        }
        if (SDBINARYC_PATH != null) {
            File smec = new File(SDBINARYC_PATH);
            if (smec.exists()) {
                System.out.println("SDBINARYC_PATH exists " + SDBINARYC_PATH);
            } else {
                System.out.println("SDBINARYC_PATH DNE " + SDBINARYC_PATH);
            }
        }


        if (SDGEE_PATH != null) {
            File mecr = new File(SDGEE_PATH);
            if (mecr.exists()) {
                System.out.println("SDGEE_PATH exists " + SDGEE_PATH);
            } else {
                System.out.println("SDGEE_PATH DNE " + SDGEE_PATH);
            }
        }

        if (SDGEERE_PATH != null) {
            File mecr = new File(SDGEERE_PATH);
            if (mecr.exists()) {
                System.out.println("SDGEERE_PATH exists " + SDGEERE_PATH);
            } else {
                System.out.println("SDGEERE_PATH DNE " + SDGEERE_PATH);
            }
        }

        if (SDGEECE_PATH != null) {
            File mecc = new File(SDGEECE_PATH);
            if (mecc.exists()) {
                System.out.println("SDGEECE_PATH exists " + SDGEECE_PATH);
            } else {
                System.out.println("SDGEECE_PATH DNE " + SDGEECE_PATH);
            }
        }
        if (SDLARS_PATH != null) {
            File mecr = new File(SDLARS_PATH);
            if (mecr.exists()) {
                System.out.println("SDLARS_PATH exists " + SDLARS_PATH);
            } else {
                System.out.println("SDLARS_PATH DNE " + SDLARS_PATH);
            }
        }

        if (SDLARSRE_PATH != null) {
            File mecr = new File(SDLARSRE_PATH);
            if (mecr.exists()) {
                System.out.println("SDLARSRE_PATH exists " + SDLARSRE_PATH);
            } else {
                System.out.println("SDLARSRE_PATH DNE " + SDLARSRE_PATH);
            }
        }

        if (SDLARSCE_PATH != null) {
            File mecc = new File(SDLARSCE_PATH);
            if (mecc.exists()) {
                System.out.println("SDLARSCE_PATH exists " + SDLARSCE_PATH);
            } else {
                System.out.println("SDLARSCE_PATH DNE " + SDLARSCE_PATH);
            }
        }

        if (SDCOR_PATH != null) {
            File smecr = new File(SDCOR_PATH);
            if (smecr.exists()) {
                System.out.println("SDCOR_PATH exists " + SDCOR_PATH);
            } else {
                System.out.println("SDCOR_PATH DNE " + SDCOR_PATH);
            }
        }

        if (SDCORR_PATH != null) {
            File smecr = new File(SDCORR_PATH);
            if (smecr.exists()) {
                System.out.println("SDCORR_PATH exists " + SDCORR_PATH);
            } else {
                System.out.println("SDCORR_PATH DNE " + SDCORR_PATH);
            }
        }

        if (SDCORC_PATH != null) {
            File smecc = new File(SDCORC_PATH);
            if (smecc.exists()) {
                System.out.println("SDCORC_PATH exists " + SDCORC_PATH);
            } else {
                System.out.println("SDCORC_PATH DNE " + SDCORC_PATH);
            }
        }

        if (SDMADR_PATH != null) {
            File sma = new File(SDMADR_PATH);
            if (sma.exists()) {
                System.out.println("SDMADR_PATH exists " + SDMADR_PATH);
            } else {
                System.out.println("SDMADR_PATH DNE " + SDMADR_PATH);
            }
        }

        if (SDMSE_PATH != null) {
            File smse = new File(SDMSE_PATH);
            if (smse.exists()) {
                System.out.println("SDMSE_PATH exists " + SDMSE_PATH);
            } else {
                System.out.println("SDMSE_PATH DNE " + SDMSE_PATH);
            }
        }


        if (SDMSEC_PATH != null) {
            File smsec = new File(SDMSEC_PATH);
            if (smsec.exists()) {
                System.out.println("SDMSEC_PATH exists " + SDMSEC_PATH);
            } else {
                System.out.println("SDMSEC_PATH DNE " + SDMSEC_PATH);
            }
        }


        if (SDMEAN_PATH != null) {
            File smea = new File(SDMEAN_PATH);
            if (smea.exists()) {
                System.out.println("SDMEAN_PATH exists " + SDMEAN_PATH);
            } else {
                System.out.println("SDMEAN_PATH DNE " + SDMEAN_PATH);
            }
        }

        if (SDRMEAN_PATH != null) {
            File smeac = new File(SDRMEAN_PATH);
            if (smeac.exists()) {
                System.out.println("SDRMEAN_PATH exists " + SDRMEAN_PATH);
            } else {
                System.out.println("SDRMEAN_PATH DNE " + SDRMEAN_PATH);
            }
        }

        if (SDCMEAN_PATH != null) {
            File smear = new File(SDCMEAN_PATH);
            if (smear.exists()) {
                System.out.println("SDCMEAN_PATH exists " + SDCMEAN_PATH);
            } else {
                System.out.println("SDCMEAN_PATH DNE " + SDCMEAN_PATH);
            }
        }

        if (SDFEAT_PATH != null) {
            File smf = new File(SDFEAT_PATH);
            if (smf.exists()) {
                System.out.println("SDFEAT_PATH exists " + SDFEAT_PATH);
            } else {
                System.out.println("SDFEAT_PATH DNE " + SDFEAT_PATH);
            }
        }

        if (SDTF_PATH != null) {
            File smtf = new File(SDTF_PATH);
            if (smtf.exists()) {
                System.out.println("SDTF_PATH exists " + SDTF_PATH);
            } else {
                System.out.println("SDTF_PATH DNE " + SDTF_PATH);
            }
        }

        if (SDINTERACT_PATH != null) {
            File spi = new File(SDINTERACT_PATH);
            if (spi.exists()) {
                System.out.println("SDINTERACT_PATH exists " + SDINTERACT_PATH);
            } else {
                System.out.println("SDINTERACT_PATH DNE " + SDINTERACT_PATH);
            }
        }


        //more paths
        if (TRAJECTORY_PATH != null) {
            File tp = new File(TRAJECTORY_PATH);
            if (tp.exists()) {
                System.out.println("TRAJECTORY_PATH exists " + TRAJECTORY_PATH);
            } else {
                System.out.println("TRAJECTORY_PATH DNE " + TRAJECTORY_PATH);
            }
        }

        if (EXCLUDE_LIST_PATH != null) {
            File ex = new File(EXCLUDE_LIST_PATH);
            if (ex.exists()) {
                System.out.println("EXCLUDE_LIST_PATH exists " + EXCLUDE_LIST_PATH);
            } else {
                System.out.println("EXCLUDE_LIST_PATH DNE " + EXCLUDE_LIST_PATH);
                System.exit(0);
            }
        }

        File rm = new File(R_METHODS_PATH);
        if (rm.exists()) {
            System.out.println("R_METHODS_PATH exists " + R_METHODS_PATH);
        } else {
            System.out.println("FATAL R_METHODS_PATH DNE " + R_METHODS_PATH);
        }

        if (ANNOTATION_PATH != null) {
            File an = new File(ANNOTATION_PATH);
            if (an.exists()) {
                System.out.println("ANNOTATION_PATH exists " + ANNOTATION_PATH);
            } else {
                System.out.println("ANNOTATION_PATH DNE " + ANNOTATION_PATH);
            }
        }

        if (TFTARGETMAP_PATH != null) {
            File tf = new File(TFTARGETMAP_PATH);
            if (tf.exists()) {
                System.out.println("TFTARGETMAP_PATH exists " + TFTARGETMAP_PATH);
            } else {
                System.out.println("TFTARGETMAP_PATH DNE " + TFTARGETMAP_PATH);
            }
        }

        System.out.println("N_BLOCK " + N_BLOCK);

        if (INIT_BLOCKS != null) {
            System.out.println("INIT_BLOCKS " + BlockMethods.IcJctoijID(INIT_BLOCKS));
        }

        System.out.println("NOISE_LEVEL " + NOISE_LEVEL);
        if (NOISE_LEVEL < 0 || NOISE_LEVEL > 1) {
            System.out.println("FATAL NOISE_LEVEL " + NOISE_LEVEL);
        }
        System.out.println("INITPA " + INITPA);
        System.out.println("INITPG " + INITPG);
        System.out.println("PA " + PA);
        System.out.println("PG " + PG);
        System.out.println("PBATCH " + PBATCH);

        System.out.println("SIZE_PRECRIT_LIST " + SIZE_PRECRIT_LIST);
        System.out.println("SIZE_PRECRIT_LIST_GENE " + SIZE_PRECRIT_LIST_GENE);
        System.out.println("SIZE_PRECRIT_LIST_EXP " + SIZE_PRECRIT_LIST_EXP);

        System.out.println("MAXMOVES " + MoreArray.toString(MAXMOVES, ","));

        System.out.println("IMIN " + IMIN);
        if (IMIN == -1) {
            System.out.println("FATAL IMIN " + IMIN);
        }
        System.out.println("JMIN " + JMIN);
        if (JMIN == -1) {
            System.out.println("FATAL JMIN " + JMIN);
        }
        System.out.println("IMAX " + IMAX);
        if (IMAX == -1) {
            System.out.println("FATAL IMAX " + IMAX);
        }
        System.out.println("JMAX " + JMAX);
        if (JMAX == -1) {
            System.out.println("FATAL JMAX " + JMAX);
        }
        System.out.println("DATA_LEN_GENES " + DATA_LEN_GENES);
        System.out.println("DATA_LEN_EXPS " + DATA_LEN_EXPS);
        if (DATA_LEN_GENES > IMAX) {
            System.out.println("DATA_LEN_GENES > IMAX " + DATA_LEN_GENES + " > " + IMAX);
        }
        if (DATA_LEN_EXPS > JMAX) {
            System.out.println("DATA_LEN_EXPS > JMAX " + DATA_LEN_EXPS + " > " + JMAX);
        }

        System.out.println("PERCENT_ALLOWED_MISSING_GENES " + PERCENT_ALLOWED_MISSING_GENES);
        System.out.println("PERCENT_ALLOWED_MISSING_EXP " + PERCENT_ALLOWED_MISSING_EXP);
        System.out.println("PERCENT_ALLOWED_MISSING_IN_BLOCK " + PERCENT_ALLOWED_MISSING_IN_BLOCK);

        System.out.println("TRUNCATE_DATA100 " + TRUNCATE_DATA100);
        System.out.println("RANDOMIZE_BLOCKS " + RANDOMIZE_BLOCKS);
        System.out.println("RANDOMIZE_GENES " + RANDOMIZE_GENES);
        System.out.println("RANDOMIZE_EXPS " + RANDOMIZE_EXPS);
        System.out.println("FIX_GENES " + FIX_GENES);
        System.out.println("FIX_EXPS " + FIX_EXPS);
        if (RANDOMIZE_EXPS && FIX_EXPS) {
            System.out.println("FATAL RANDOMIZE_EXPS && FIX_EXPS");
        }
        if (RANDOMIZE_GENES && FIX_GENES) {
            System.out.println("FATAL RANDOMIZE_GENES && FIX_GENES");
        }

        System.out.println("USE_RANDOM_SEED " + USE_RANDOM_SEED);

        System.out.println("RANDOM_SEED " + RANDOM_SEED);

        System.out.println("BATCH_DMETHOD " + BATCH_DMETHOD);
        System.out.println("BATCH_LINKMETHOD " + BATCH_LINKMETHOD);

        System.out.println("CRIT_TYPE_INDEX " + CRIT_TYPE_INDEX);
        if (CRIT_TYPE_INDEX == -1) {
            System.out.println("FATAL CRIT_TYPE_INDEX " + CRIT_TYPE_INDEX);
            System.exit(0);
        }
        System.out.println("PRECRIT_TYPE_INDEX " + PRECRIT_TYPE_INDEX);

        System.out.println("FEATURE_INDICES " + (FEATURE_INDICES == null ? "yes" : "no"));
        for (int i = 0; i < FEATURE_INDICES.size(); i++) {
            System.out.println("FEATURE_INDICES " + i + "\t" + (Integer) FEATURE_INDICES.get(i));
        }
        System.out.println("feature_map " + (feature_map == null ? "yes" : "no"));

        System.out.println("PLATEAU " + PLATEAU);
        System.out.println("RANDOMFLOOD " + RANDOMFLOOD);
        System.out.println("CHECKSUM_SEED " + CHECKSUM_SEED);

        System.out.println("GENE_SHAVE " + GENE_SHAVE);
        System.out.println("EXP_SHAVE " + EXP_SHAVE);
        System.out.println("GENE_GROW " + GENE_GROW);
        System.out.println("EXP_GROW " + EXP_GROW);
        if (GENE_SHAVE && GENE_GROW) {
            System.out.println("FATAL GENE_SHAVE && GENE_GROW");
        }
        if (EXP_SHAVE && EXP_GROW) {
            System.out.println("FATAL EXP_SHAVE && EXP_GROW");
        }

        System.out.println("WEIGH_EXPR " + WEIGH_EXPR);
        System.out.println("USE_MEAN " + USE_MEAN);
        System.out.println("USE_ABS " + MoreArray.toString(USE_ABS_AR, ","));
        System.out.println("FRXN_SIGN " + FRXN_SIGN);
        System.out.println("OVERRIDE_SHAVING " + OVERRIDE_SHAVING);

        System.out.println("debug " + debug);
        System.out.println("RANDOMFLOOD_PERC " + RANDOMFLOOD_PERC);
        if (RANDOMFLOOD_PERC < 0 || RANDOMFLOOD_PERC > 1) {
            System.out.println("FATAL RANDOMFLOOD_PERC " + RANDOMFLOOD_PERC);
        }
        System.out.println("BATCH_PERC " + BATCH_PERC);
        if (BATCH_PERC < 0 || BATCH_PERC > 1) {
            System.out.println("FATAL BATCH_PERC " + BATCH_PERC);
        }
        System.out.println("RUN_SEQUENCE " + RUN_SEQUENCE);
        if (RUN_SEQUENCE == null) {
            System.out.println("FATAL RUN_SEQUENCE == null");
        }

        System.out.println("PLATEAU_PERC " + PLATEAU_PERC);
        if (PLATEAU_PERC < 0 || PLATEAU_PERC > 1) {
            System.out.println("FATAL PLATEAU_PERC " + PLATEAU_PERC);
        }
        System.out.println("LIB_LOC " + LIB_LOC);
        System.out.println("EXCLUDE_OVERLAP_THRESHOLD " + EXCLUDE_OVERLAP_THRESHOLD);
        if (EXCLUDE_OVERLAP_THRESHOLD < 0 || EXCLUDE_OVERLAP_THRESHOLD > 1) {
            System.out.println("FATAL EXCLUDE_OVERLAP_THRESHOLD " + EXCLUDE_OVERLAP_THRESHOLD);
            System.exit(0);
        }

        if ((Double.isNaN(EXCLUDE_OVERLAP_THRESHOLD) || EXCLUDE_OVERLAP_THRESHOLD == 0) && this.EXCLUDE_LIST_PATH != null) {
            System.out.println("FATAL EXCLUDE_LIST_PATH defined but EXCLUDE_OVERLAP_THRESHOLD " + EXCLUDE_OVERLAP_THRESHOLD);
            System.exit(0);
        }


        System.out.println("MIN_NONMISSING_FOR_BATCH " + MIN_NONMISSING_FOR_BATCH);
        if (PBATCH != -1 && MIN_NONMISSING_FOR_BATCH == -1) {
            System.out.println("FATAL PBATCH != -1 && MIN_NONMISSING_FOR_BATCH == -1");
        }

        if (RUN_SEQUENCE.indexOf("b") != -1 || RUN_SEQUENCE.indexOf("B") != -1) {
            if (PBATCH == -1) {
                System.out.println("FATAL batch move but PBATCH == -1");
            }
            if (MIN_NONMISSING_FOR_BATCH == -1) {
                System.out.println("FATAL batch move but MIN_NONMISSING_FOR_BATCH == -1");
            }
        }
        if (PBATCH != -1 && MIN_NONMISSING_FOR_BATCH == -1) {
            System.out.println("FATAL PBATCH != -1 && MIN_NONMISSING_FOR_BATCH == -1");
        }
        if (MIN_NONMISSING_FOR_BATCH != -1 && PBATCH == -1) {
            System.out.println("FATAL MIN_NONMISSING_FOR_BATCH != -1 && PBATCH == -1");
        }

        System.out.println("crit " + (crit == null ? "no" : "yes"));
        System.out.println("precrit " + (precrit == null ? "no" : "yes"));
    }

}