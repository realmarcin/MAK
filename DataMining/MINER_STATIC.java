package DataMining;

import mathy.stat;
import util.MoreArray;
import util.StringUtil;


/**
 * User: marcin
 * Date: Nov 25, 2008
 * Time: 10:43:32 AM
 */
public final class MINER_STATIC {

    //-10 = silent
    //0
    // 1
    //  2
    //   3
    // increasing output
    public final static int DEFAULT_DEBUG = -10;

    public final static String version = "MAKv0.3";
    /*The move types.*/
    public final static String[] MOVE_TYPES = {"gene-", "gene+", "experiment-", "experiment+"};
    public final static String[] MOVE_LABELS = {"initial", "g-", "g+", "e-", "e+"};

    /*The search modes -- CASE SENSITIVE !
    s = single, precrit+fullcrit
    b = batch, precrit+fullcrit
    m = mixed, precrit+fullcrit
    S = single, fullcrit
    B = batch, fullcrit
    M = mixed, fullcrit
    p = PLATEAU
    r = random
    c = add combined block based on merged last g+e+ block
    */
    final static String[] MOVE_CLASSES = {"s", "S", "b", "B", "m", "M", "p", "r", "c", "O", "o"};

    public final static String EXPR_LABEL = "expr";
    public final static String inter_LABEL = "inter";
    public final static String TFRANK_LABEL = "tf";

    //final static String[] DATA_FILES = {EXPR_LABEL, inter_LABEL, TFRANK_LABEL};
    //final static String[] FEATURES = {"orthologs", "localization"};//{"GO_func2", "GO_proc2", "GO_comp2", "Fitness2", "ProteinP4", "Local3"};
    //final static String[] FEATURE_FILES = {"GOslim_allfiles2", "GOslim_allfiles2", "GOslim_allfiles2",
    //        "Fitness_allfiles2", "proteinProp_allfiles2", "Localiztion_allfiles2"};
    //public final static String[] PRECRITERIA = {"MSE", "MAD", "KENDALL"};
    //public final static String[] PRECRITERIA_AXIS = {"total", "row", "column"};

    public final static String[] HCL_DMETHODS = {"euclidean", "maximum", "manhattan", "canberra", "binary", "pearson", "abspearson", "correlation", "abscorrelation", "spearman", "kendall"};
    public final static String[] HCL_LINKMETHODS = {"ward", "single", "complete", "average", "mcquitty", "median", "centroid", "centroid2"};

    /*
     */
    public final static String[] CRIT_LABELS = {
            "MSER_inter",//1
            "MADR_inter",//2
            "KENDALLR_inter",//3
            "MSER",//4 SINGLE
            "MADR",//5 SINGLE
            "KENDALLR",//6
            "LARSRE_inter",//7
            "LARSRE",//8
            "FEMR_inter",//9
            "FEMR",//10
            "MSER_LARSRE_inter",//11
            "MADR_LARSRE_inter",//12
            "KENDALLR_LARSRE_inter",//13
            "MSER_LARSRE",//14
            "MADR_LARSRE",//15
            "KENDALLR_LARSRE",//16
            "MSER_FEMR_inter",//17
            "MADR_FEMR_inter",//18
            "KENDALLR_FEMR_inter",//19
            "MSER_FEMR",//20
            "MADR_FEMR",//21
            "KENDALLR_FEMR",//22
            "MSE_inter",//23
            "MSE",//24  SINGLE

            "MSEC_inter",//25
            "MSEC",//26   SINGLE
            "MSEC_LARSRE_inter",//27
            "MSEC_LARSRE",//28
            "MSEC_FEMR_inter",//29
            "MSEC_FEMR",//30

            "MSERnonull_noninvert",//31
            "MSEnonull_noninvert",//32
            "MSECnonull_noninvert",//33
            "MADRnonull_noninvert",//34

            "KENDALLRnonull",//35
            "MSERnonull",//36
            "MSEnonull",//37
            "MSECnonull",//38
            "MADRnonull",//39
            "inter",//40
            "internonull",//41

            "MEAN",//42
            "MEDRMEAN",//43
            "MEDCMEAN", //44

            "MEANnonull",//45
            "MEDRMEANnonull", //46
            "MEDCMEANnonull", //47

            "LARSCE_inter",//48
            "LARSCE",//49
            "FEMC_inter",//50
            "FEMC",//51
            "MSER_LARSCE_inter",//52
            "MADR_LARSCE_inter",//53
            "KENDALLR_LARSCE_inter",//54
            "MSER_LARSCE",//55
            "MADR_LARSCE",//56
            "KENDALLR_LARSCE",//57
            "MSER_FEMC_inter",//58
            "MADR_FEMC_inter",//59
            "KENDALLR_FEMC_inter",//60
            "MSER_FEMC",//61
            "MADR_FEMC",//62
            "KENDALLR_FEMC",//63

            "MSEC_LARSCE_inter",//64
            "MSEC_LARSCE",//65
            "MSEC_FEMC_inter",//66
            "MSEC_FEMC",//67

            "MSE_LARSRE_inter",//68
            "MSE_LARSRE",//69
            "MSE_FEMR_inter",//70
            "MSE_FEMR",//71

            "MSE_LARSCE_inter",//72
            "MSE_LARSCE",//73
            "MSE_FEMC_inter",//74
            "MSE_FEMC",//75

            "MSE_KENDALLR",//76
            "MSER_KENDALLR",//77
            "MSEC_KENDALLR",//78

            "CORR",//79
            "CORC",//80

            "MSE_CORR",//81
            "MSE_CORC",//82
            "MSER_CORR",//83
            "MSEC_CORC",//84
            "MSER_KENDALLR_CORR",//85
            "MSEC_KENDALLR_CORC",//86
            "KENDALLR_CORR",//87
            "KENDALLR_CORC",//88
            "MSER_KENDALLR_CORR_FEMR",//89
            "MSEC_KENDALLR_CORC_FEMC",//90

            "MSE_KENDALLR_FEMR",//91
            "MSE_KENDALLR_FEMC",//92
            "MSER_KENDALLR_FEMR",//93
            "MSEC_KENDALLR_FEMC",//94

            "MSE_KENDALLR_LARSRE",//95
            "MSE_KENDALLR_LARSCE",//96
            "MSER_KENDALLR_LARSRE",//97
            "MSEC_KENDALLR_LARSCE",//98

            "MSE_KENDALLR_CORR",//99
            "MSE_KENDALLR_CORC",//100

            "MSE_KENDALLR_CORR_FEMR",//101
            "MSE_KENDALLR_CORC_FEMC",//102

            "MSE_KENDALLR_CORR_LARSRE",//103
            "MSE_KENDALLR_CORC_LARSCE",//104

            "CORR_inter",//105
            "CORC_inter",//106

            "MSE_CORR_inter",//107
            "MSE_CORC_inter",//108
            "MSER_CORR_inter",//109
            "MSEC_CORC_inter",//110
            "MSER_KENDALLR_CORR_inter",//111
            "MSEC_KENDALLR_CORC_inter",//112
            "KENDALLR_CORR_inter",//113
            "KENDALLR_CORC_inter",//114
            "MSER_KENDALLR_CORR_FEMR_inter",//115
            "MSEC_KENDALLR_CORC_FEMC_inter",//116

            "MSE_KENDALLR_FEMR_inter",//117
            "MSE_KENDALLR_FEMC_inter",//118
            "MSER_KENDALLR_FEMR_inter",//119
            "MSEC_KENDALLR_FEMC_inter",//120

            "MSE_KENDALLR_LARSRE_inter",//121
            "MSE_KENDALLR_LARSCE_inter",//122
            "MSER_KENDALLR_LARSRE_inter",//123
            "MSEC_KENDALLR_LARSCE_inter",//124

            "MSE_KENDALLR_CORR_inter",//125
            "MSE_KENDALLR_CORC_inter",//126

            "MSE_KENDALLR_CORR_FEMR_inter",//127
            "MSE_KENDALLR_CORC_FEMC_inter",//128

            "MSE_KENDALLR_CORR_LARSRE_inter",//129
            "MSE_KENDALLR_CORC_LARSCE_inter",//130

            "KENDALLCnonull",//131
            "KENDALLC",//132
            "MSEC_KENDALLC",//133

            "MSER_KENDALLR_inter",//134
            "MSEC_KENDALLC_inter",//135

            "EUCR",//136
            "EUCC",//137

            "MSE_EUCR",//138
            "MSE_EUCC",//139
            "MSER_EUCR",//140
            "MSEC_EUCC",//141
            "MSER_KENDALLR_EUCR",//142
            "MSEC_KENDALLR_EUCC",//143
            "KENDALLR_EUCR",//144
            "KENDALLR_EUCC",//145
            "MSER_KENDALLR_EUCR_FEMR",//146
            "MSEC_KENDALLR_EUCC_FEMC",//147

            "EUCR_inter",//148
            "EUCC_inter",//149

            "MSE_EUCR_inter",//150
            "MSE_EUCC_inter",//151
            "MSER_EUCR_inter",//152
            "MSEC_EUCC_inter",//153
            "MSER_KENDALLR_EUCR_inter",//154
            "MSEC_KENDALLR_EUCC_inter",//155
            "KENDALLR_EUCR_inter",//156
            "KENDALLR_EUCC_inter",//157
            "MSER_KENDALLR_EUCR_FEMR_inter",//158
            "MSEC_KENDALLR_EUCC_FEMC_inter",//159

            "MSEC_KENDALLC_FEMC",//160
            "MSEC_KENDALLC_FEMC_inter",//161

            "KENDALLC_FEMC",//162
            "KENDALLC_FEMC_inter",//163

            "KENDALLC_LARSCE",//164
            "KENDALLC_LARSCE_inter",//165
            "feat",//166
            "featnonull",//167
            "MSEC_KENDALLC_FEMC_feat",//168
            "MSER_KENDALLR_FEMR_feat",//169
            "MSER_FEMR_feat",//170

            "MSEC_KENDALLC_FEMC_inter_feat",//171
            "MSER_KENDALLR_FEMR_inter_feat",//172
            "MSER_FEMR_inter_feat",//173

            "MSEC_KENDALLC_FEMR",//174
            "MSEC_KENDALLC_FEMC_CORR",//175

            "MSER_KENDALLR_FEMC",//176
            "MSER_KENDALLR_FEMR_CORC",//177

            "KENDALL",//178
            "KENDALLnonull",//179
            "FEM",//180
            "FEMnonull",//181
            "MSE_KENDALL_FEM",//182
            "KENDALL_FEM",//183
            "MSE_FEM",//184

            "COR",//185
            "EUC",//186
            "LARS",//187

            "CORnonull",//188
            "CORRnonull",//189
            "CORCnonull",//190
            "EUCnonull",//191
            "EUCRnonull",//192
            "EUCCnonull",//193
            "LARSnonull",//194
            "LARSREnonull",//195
            "LARSCEnonull",//196
            "FEMRnonull",//197
            "FEMCnonull",//198

            "MSECnonull_KENDALLC_FEMC",//199
            "MSECnonull_KENDALLCnonull_FEMC",//200
            "MSECnonull_KENDALLC_FEMCnonull",//201
            "MSECnonull_KENDALLCnonull_FEMCnonull",//202
            "MSEC_KENDALLCnonull_FEMC",//203
            "MSEC_KENDALLC_FEMCnonull",//204
            "MSEC_KENDALLCnonull_FEMCnonull",//205

            "KENDALL_FEM_feat",//206
            "FEMC_feat",//207
            "FEM_feat",//208

            "MSEC_KENDALLC_FEMC_MAXTF",//209
            "MSEC_KENDALLC_FEMC_inter_MAXTF",//210
            "MSEC_KENDALLC_FEMC_feat_MAXTF",//211
            "MSEC_KENDALLC_FEMC_inter_feat_MAXTF",//212
            "MAXTF", //213
            "KENDALL_FEM_MAXTF", //214
            "Binarynonull", //215
            "Binary", //216
            "BinaryRnonull", //217
            "BinaryR", //218
            "BinaryCnonull", //219
            "BinaryC", //220
            "MSEC_inter", //221

            "SPEARMANC", //222
            "SPEARMANR", //223
            "SPEARMAN",   //224

            "KENDALL_SPEARMAN_FEM",//225
            "KENDALLC_SPEARMANC_FEMC",//226
            "MSEC_KENDALLC_SPEARMANC_FEMC",//227
            "KENDALLR_SPEARMANR_FEMR",//228
            "MSE_KENDALL_SPEARMAN_FEM",//229

            "SPEARMANC_nonull", //230
            "SPEARMANR_nonull", //231
            "SPEARMAN_nonull",   //232

    };
    public final static String[] notnonull = {"nonull"};
    public final static String[] notnonullFEMRFEMC = {"nonull", "FEMR", "FEMC"};
    //public final static String[] notnonullFEMRFEMC = {"FEMnonull","FEMRnonull","FEMCnonull", "FEMR", "FEMC"};
    public final static String[] notnonullLARSRELARSCE = {"nonull", "LARSRE", "LARSCE"};
    //public final static String[] notnonullLARSRELARSCE = {"LARSnonull","LASRCEnonull","LASRREnonull", "LARSRE", "LARSCE"};
    public final static String[] notnonullCORRCORC = {"nonull", "CORR", "CORC"};
    //public final static String[] notnonullCORRCORC = {"CORnonull","CORRnonull","CORCnonull", "CORR", "CORC"};
    public final static String[] notnonullEUCREUCC = {"nonull", "EUCR", "EUCC"};
    //public final static String[] notnonullEUCREUCC = {"EUCnonull","EUCRnonull","EUCCnonull", "EUCR", "EUCC"};
    public final static String[] notnonullSPEARMANRSPEARMANC = {"nonull", "SPEARMANR", "SPEARMANC"};
    public final static String[] notnonullnotKENDALLC = {"nonull", "KENDALLC"};
    //public final static String[] notnonullnotKENDALLC = {"KENDALLCnonull", "KENDALLC"};
    public final static String[] notnonullMSERMSEC = {"nonull", "MSER", "MSEC"};
    //public final static String[] notnonullMSERMSEC = {"MSER", "MSEC", "MSEnonull", "MSERnonull", "MSECnonull"};
    public final static String[] notMSERnotMSEC = {"MSER", "MSEC"};
    public final static String[] notKendallRnotKendallC = {"KENDALLR", "KENDALLC"};
    public final static String[] notnonullnotinter = {"nonull", "inter"};
    public final static String[] notnonullnotinternotKENDALLRKENDALLC = {"nonull", "inter", "KENDALLR", "KENDALLC"};
    public final static String[] notnonullnotKENDALLCnotinter = {"nonull", "KENDALLC", "inter"};
    public final static String[] notMSERnotMSECnotnonullnotinter = {"MSER", "MSEC", "nonull", "inter"};
    public final static String[] notnonullnotKENDALLRKENDALLC = {"nonull", "KENDALLC", "KENDALLR"};
    //public final static String[] notnonullnotKENDALLRKENDALLC = {"KENDALLnonull", "KENDALLCnonull", "KENDALLRnonull", "KENDALLC", "KENDALLR"};

    //MSE
    public final static int[] MSEtotalCrit = stat.add(
            StringUtil.occurIndexButNot(CRIT_LABELS, "MSE", notnonullMSERMSEC), 1);  //MoreArray.add(//24 - 1),

    //MSER
    public final static int[] MSERCrit = stat.add(StringUtil.occurIndexButNot(CRIT_LABELS, "MSER", notnonull), 1);
    //MSEC
    public final static int[] MSECCrit = stat.add(StringUtil.occurIndexButNot(CRIT_LABELS, "MSEC", notnonull), 1);
    //KENDALL
    public final static int[] KENDALLRCritAll = stat.add(StringUtil.occurIndex(CRIT_LABELS, "KENDALLR"), 1);
    public final static int[] KENDALLCCritAll = stat.add(StringUtil.occurIndex(CRIT_LABELS, "KENDALLC"), 1);
    public final static int[] KENDALLtotalCrit = stat.add(StringUtil.occurIndexButNot(CRIT_LABELS, "KENDALL", notnonullnotKENDALLRKENDALLC), 1);
    public final static int[] KENDALLtotalCritAll = stat.add(StringUtil.occurIndex(CRIT_LABELS, "KENDALL"), 1);

    public final static String[] KENDALLRnonullCrit = {"KENDALLRnonull"};
    public final static int[] KENDALLRCrit = stat.add(StringUtil.occurIndexButNot(CRIT_LABELS, "KENDALLR", KENDALLRnonullCrit), 1);
    public final static String[] KENDALLCnonullCrit = {"KENDALLCnonull"};
    public final static int[] KENDALLCCrit = stat.add(StringUtil.occurIndexButNot(CRIT_LABELS, "KENDALLC", KENDALLCnonullCrit), 1);

    public final static String[] BinaryRnonullCrit = {"BinaryRnonull"};
    public final static int[] BinaryRCrit = stat.add(StringUtil.occurIndexButNot(CRIT_LABELS, "BinaryR", BinaryRnonullCrit), 1);
    public final static String[] BinaryCnonullCrit = {"BinaryCnonull"};
    public final static int[] BinaryCCrit = stat.add(StringUtil.occurIndexButNot(CRIT_LABELS, "BinaryC", BinaryCnonullCrit), 1);


    //MADR
    public final static int[] MADRCrit = stat.add(StringUtil.occurIndexButNot(CRIT_LABELS, "MADR", notnonull), 1);
    //MSE
    public final static int[] MSECritAll = stat.add(
            StringUtil.occurIndexButNot(CRIT_LABELS, "MSE", notMSERnotMSEC), 1);  //MoreArray.add(//24 - 1),
    //MSER
    public final static int[] MSERCritAll = stat.add(StringUtil.occurIndex(CRIT_LABELS, "MSER"), 1);
    //MSEC
    public final static int[] MSECCritAll = stat.add(StringUtil.occurIndex(CRIT_LABELS, "MSEC"), 1);
    //KENDALL
    public final static int[] KENDALLCritAll = stat.add(StringUtil.occurIndexButNot(CRIT_LABELS, "KENDALL", notKendallRnotKendallC), 1);
    //MADR
    public final static int[] MADRCritAll = stat.add(StringUtil.occurIndex(CRIT_LABELS, "MADR"), 1);

    //Binary
    public final static int[] BinaryCritAll = stat.add(StringUtil.occurIndex(CRIT_LABELS, "Binary"), 1);
    public final static int[] BinaryCrit = stat.add(StringUtil.occurIndexButNot(CRIT_LABELS, "Binary", notnonull), 1);

    //MSER  criteria
    public final static int[] MSERCritnointer = stat.add(StringUtil.occurIndexButNot(CRIT_LABELS, "MSER", notnonullnotinter), 1);
    //MSEC criteria
    public final static int[] MSECCritnointer = stat.add(StringUtil.occurIndexButNot(CRIT_LABELS, "MSEC", notnonullnotinter), 1);
    //KENDALL criteria
    public final static int[] KENDALLCritnointer = stat.add(StringUtil.occurIndexButNot(CRIT_LABELS, "KENDALL", notnonullnotinternotKENDALLRKENDALLC), 1);
    public final static int[] KENDALLRCritnointer = stat.add(StringUtil.occurIndexButNot(CRIT_LABELS, "KENDALLR", notnonullnotinter), 1);
    public final static int[] KENDALLCCritnointer = stat.add(StringUtil.occurIndexButNot(CRIT_LABELS, "KENDALLC", notnonullnotinter), 1);
    //MADR criteria
    public final static int[] MADRCritnointer = stat.add(StringUtil.occurIndexButNot(CRIT_LABELS, "MADR", notnonullnotinter), 1);


    //MSE criteria
    public final static int[] MSECritnointer = stat.add(
            StringUtil.occurIndexButNot(CRIT_LABELS, "MSE", notMSERnotMSECnotnonullnotinter), 1);//24 - 1),

    //block mean/median with null
    public final static int[] MEANCrit = stat.add(StringUtil.occurIndexButNot(CRIT_LABELS, "MEAN", notnonull), 1);//{42, 43, 44};
    public final static int[] MEANCritAll = stat.add(StringUtil.occurIndex(CRIT_LABELS, "MEAN"), 1);

    public final static String[] notMEDCMEANoMEDRMEAN = {"MEDCMEAN", "MEDRMEAN"};
    public final static int[] MEANtotalCrit = stat.add(StringUtil.occurIndexButNot(CRIT_LABELS, "MEAN", notMEDCMEANoMEDRMEAN), 1);//stat.add(StringUtil.locateIndexOf(CRIT_LABELS, "FEM", -2), 1);

    //block mean/median with null
    public final static int[] MEANRCrit = stat.add(StringUtil.occurIndexButNot(CRIT_LABELS, "MEANR", notnonull), 1);//{42, 43, 44};
    public final static int[] MEANRCritAll = stat.add(StringUtil.occurIndex(CRIT_LABELS, "MEANR"), 1);
    //block mean/median with null
    public final static int[] MEANCCrit = stat.add(StringUtil.occurIndexButNot(CRIT_LABELS, "MEANC", notnonull), 1);//{42, 43, 44};
    public final static int[] MEANCCritAll = stat.add(StringUtil.occurIndex(CRIT_LABELS, "MEANC"), 1);

    //criteria requiring a null but w/o interaction
    public final static int[] nullCritnointeract = MoreArray.add(MoreArray.add(MoreArray.add(MoreArray.add(MoreArray.add(MoreArray.add(MoreArray.add(MoreArray.add(
            MSERCritnointer, MSECCritnointer), MADRCritnointer), KENDALLCritnointer), KENDALLRCritnointer), KENDALLCCritnointer), MSECritnointer), MEANCrit), BinaryCrit);
    //criteria requiring a null with interaction
    //public final static int[] nullCritIntCrit =
    //        {1, 3, 2, 11, 12, 13, 17, 18, 19, 23, 25, 27, 29, 52, 53, 54, 58, 59, 60, 64, 66, 68, 70, 72, 74};
    //criteria without a null without interaction
    public final static int[] nonullCrit = stat.add(StringUtil.occurIndex(CRIT_LABELS, "nonull"), 1);
    //{31, 32, 33, 34, 35, 36, 37, 38, 39, 45, 46, 47, 131, 188, 189, 190, 191, 192, 193, 194, 195, 196, 197, 198};
    //criteria without a null with interaction
    //public final static int[] nonullCritIntCrit = {};


    public final static int[] interactCrit = stat.add(StringUtil.occurIndex(CRIT_LABELS, "inter"), 1);
    public final static int[] FEATURECrit = stat.add(StringUtil.occurIndex(CRIT_LABELS, "feat"), 1);
    public final static int[] MAXTFCrit = stat.add(StringUtil.occurIndex(CRIT_LABELS, "MAXTF"), 1);


    //LARSRE criteria
    public final static String[] notLARSREnoLARSCE = {"LARSRE", "LARSCE"};
    public final static int[] LARSTotalCritAll = stat.add(StringUtil.occurIndex(CRIT_LABELS, "LARS"), 1);
    public final static int[] LARSRECritAll = stat.add(StringUtil.occurIndex(CRIT_LABELS, "LARSRE"), 1);
    public final static int[] LARSCECritAll = stat.add(StringUtil.occurIndex(CRIT_LABELS, "LARSCE"), 1);
    public final static int[] LARSCritAll = stat.add(StringUtil.occurIndexButNot(CRIT_LABELS, "LARS", notLARSREnoLARSCE), 1);//stat.add(StringUtil.locateIndexOf(CRIT_LABELS, "FEM", -2), 1);
    public final static int[] LARStotalCrit = stat.add(StringUtil.occurIndexButNot(CRIT_LABELS, "LARS", notnonullLARSRELARSCE), 1);//stat.add(StringUtil.locateIndexOf(CRIT_LABELS, "FEM", -2), 1);
    public final static int[] LARSRECrit = stat.add(StringUtil.locateIndexOf(CRIT_LABELS, "LARSRE", -2), 1);
    public final static int[] LARSCECrit = stat.add(StringUtil.locateIndexOf(CRIT_LABELS, "LARSCE", -2), 1);

    //FEMR criteria
    public final static String[] notFEMRnoFEMC = {"FEMR", "FEMC"};
    public final static int[] FEMTotalCritAll = stat.add(StringUtil.occurIndex(CRIT_LABELS, "FEM"), 1);
    public final static int[] FEMRCritAll = stat.add(StringUtil.occurIndex(CRIT_LABELS, "FEMR"), 1);
    public final static int[] FEMCCritAll = stat.add(StringUtil.occurIndex(CRIT_LABELS, "FEMC"), 1);
    public final static int[] FEMCritAll = stat.add(StringUtil.occurIndexButNot(CRIT_LABELS, "FEM", notFEMRnoFEMC), 1);//stat.add(StringUtil.locateIndexOf(CRIT_LABELS, "FEM", -2), 1);
    public final static int[] FEMtotalCrit = stat.add(StringUtil.occurIndexButNot(CRIT_LABELS, "FEM", notnonullFEMRFEMC), 1);//stat.add(StringUtil.locateIndexOf(CRIT_LABELS, "FEM", -2), 1);
    public final static int[] FEMRCrit = stat.add(StringUtil.locateIndexOf(CRIT_LABELS, "FEMR", -2), 1);
    public final static int[] FEMCCrit = stat.add(StringUtil.locateIndexOf(CRIT_LABELS, "FEMC", -2), 1);


    //Pearson
    public final static String[] notCORRnoCORC = {"CORR", "CORC"};
    public final static int[] CORTotalCritAll = stat.add(StringUtil.occurIndex(CRIT_LABELS, "COR"), 1);
    public final static int[] CORRCritAll = stat.add(StringUtil.occurIndex(CRIT_LABELS, "CORR"), 1);
    public final static int[] CORCCritAll = stat.add(StringUtil.occurIndex(CRIT_LABELS, "CORC"), 1);
    public final static int[] CORCritAll = stat.add(StringUtil.occurIndexButNot(CRIT_LABELS, "COR", notCORRnoCORC), 1);//stat.add(StringUtil.locateIndexOf(CRIT_LABELS, "FEM", -2), 1);
    public final static int[] CORtotalCrit = stat.add(StringUtil.occurIndexButNot(CRIT_LABELS, "COR", notnonullCORRCORC), 1);//stat.add(StringUtil.locateIndexOf(CRIT_LABELS, "FEM", -2), 1);
    public final static int[] CORRCrit = stat.add(StringUtil.locateIndexOf(CRIT_LABELS, "CORR", -2), 1);
    public final static int[] CORCCrit = stat.add(StringUtil.locateIndexOf(CRIT_LABELS, "CORC", -2), 1);


    //EUClidean
    public final static String[] notEUCRnoEUCC = {"EUCR", "EUCC"};
    public final static int[] EUCCritTotalAll = stat.add(StringUtil.occurIndex(CRIT_LABELS, "EUC"), 1);
    public final static int[] EUCRCritAll = stat.add(StringUtil.occurIndex(CRIT_LABELS, "EUCR"), 1);
    public final static int[] EUCCCritAll = stat.add(StringUtil.occurIndex(CRIT_LABELS, "EUCC"), 1);
    public final static int[] EUCCritAll = stat.add(StringUtil.occurIndexButNot(CRIT_LABELS, "EUC", notEUCRnoEUCC), 1);//stat.add(StringUtil.locateIndexOf(CRIT_LABELS, "FEM", -2), 1);
    public final static int[] EUCtotalCrit = stat.add(StringUtil.occurIndexButNot(CRIT_LABELS, "EUC", notnonullEUCREUCC), 1);//stat.add(StringUtil.locateIndexOf(CRIT_LABELS, "FEM", -2), 1);
    public final static int[] EUCRCrit = stat.add(StringUtil.locateIndexOf(CRIT_LABELS, "EUCR", -2), 1);
    public final static int[] EUCCCrit = stat.add(StringUtil.locateIndexOf(CRIT_LABELS, "EUCC", -2), 1);

    //SPEARMAN
    public final static String[] notSPEARMANRnotSPEARMANC = {"SPEARMANR", "SPEARMANC"};
    public final static int[] SPEARMANCritTotalAll = stat.add(StringUtil.occurIndex(CRIT_LABELS, "SPEARMAN"), 1);
    public final static int[] SPEARMANRCritAll = stat.add(StringUtil.occurIndex(CRIT_LABELS, "SPEARMANR"), 1);
    public final static int[] SPEARMANCCritAll = stat.add(StringUtil.occurIndex(CRIT_LABELS, "SPEARMANC"), 1);
    public final static int[] SPEARMANCritAll = stat.add(StringUtil.occurIndexButNot(CRIT_LABELS, "SPEARMAN", notSPEARMANRnotSPEARMANC), 1);//stat.add(StringUtil.locateIndexOf(CRIT_LABELS, "FEM", -2), 1);
    public final static int[] SPEARMANtotalCrit = stat.add(StringUtil.occurIndexButNot(CRIT_LABELS, "SPEARMAN", notnonullSPEARMANRSPEARMANC), 1);//stat.add(StringUtil.locateIndexOf(CRIT_LABELS, "FEM", -2), 1);
    //public final static int[] SPEARMANRCrit = stat.add(StringUtil.locateIndexOf(CRIT_LABELS, "SPEARMANR", -2), 1);
    //public final static int[] SPEARMANCCrit = stat.add(StringUtil.locateIndexOf(CRIT_LABELS, "SPEARMANC", -2), 1);

    public final static String[] SPEARMANRnonullCrit = {"SPEARMANRnonull"};
    public final static int[] SPEARMANRCrit = stat.add(StringUtil.occurIndexButNot(CRIT_LABELS, "SPEARMANR", SPEARMANRnonullCrit), 1);
    public final static String[] SPEARMANCnonullCrit = {"SPEARMANCnonull"};
    public final static int[] SPEARMANCCrit = stat.add(StringUtil.occurIndexButNot(CRIT_LABELS, "SPEARMANC", SPEARMANCnonullCrit), 1);


    //public final static int[] totalCrit = MoreArray.add(MSEtotalCrit, MEANCrit);
    //public final static int[] CritAll = MoreArray.add(stat.add(
    //        StringUtil.occurIndexButNot(CRIT_LABELS, "MSE", notMSERnotMSEC), 1), MEANCritAll);

    public final static int[] totalCrit = MoreArray.add(MoreArray.add(MoreArray.add(MoreArray.add(MoreArray.add(MoreArray.add(MoreArray.add(
            MSEtotalCrit, LARStotalCrit), FEMtotalCrit), CORtotalCrit), EUCtotalCrit), SPEARMANtotalCrit), KENDALLtotalCrit), MEANCrit);//MADCrit
    public final static int[] CritAll = MoreArray.add(MoreArray.add(MoreArray.add(MoreArray.add(MoreArray.add(MoreArray.add(MoreArray.add(
            MSECritAll, LARSCritAll), FEMCritAll), CORCritAll), EUCCritAll), SPEARMANCritAll), KENDALLCritAll), MEANCritAll);

    public final static int[] rowCrit = MoreArray.add(MoreArray.add(MoreArray.add(MoreArray.add(MoreArray.add(MoreArray.add(MoreArray.add(MoreArray.add(
            MSERCrit, MADRCrit), LARSRECrit), FEMRCrit), CORRCrit), EUCRCrit), SPEARMANRCrit), KENDALLRCrit), MEANRCrit);
    public final static int[] rowCritAll = MoreArray.add(MoreArray.add(MoreArray.add(MoreArray.add(MoreArray.add(MoreArray.add(MoreArray.add(MoreArray.add(
            MSERCritAll, MADRCritAll), LARSRECritAll), FEMRCritAll), CORRCritAll), EUCRCritAll), SPEARMANRCritAll), KENDALLRCritAll), MEANRCritAll);

    public final static int[] colCrit = MoreArray.add(MoreArray.add(MoreArray.add(MoreArray.add(MoreArray.add(MoreArray.add(MoreArray.add(MoreArray.add(
            MSECCrit, MADRCrit), LARSCECrit), FEMCCrit), CORCCrit), EUCCCrit), SPEARMANCCrit), KENDALLCCrit), MEANCCrit);

    public final static int[] colCritAll = MoreArray.add(MoreArray.add(MoreArray.add(MoreArray.add(MoreArray.add(MoreArray.add(MoreArray.add(MoreArray.add(
            MSECCritAll, MADRCritAll), LARSCECritAll), FEMCCritAll), CORCCritAll), EUCCCritAll), SPEARMANCCritAll), KENDALLCCritAll), MEANCCritAll);

    public final static int[] isNoninvertCrit = stat.add(StringUtil.occurIndex(CRIT_LABELS, "noninvert"), 1);


    /*10 random seeds used.*/
    public final static int[] RANDOM_SEEDS = {
            759820,
            9110656,
            6942092,
            651415,
            7269251,
            2192957,
            8290484,
            3377544,
            1297150,
            8057408
    };

    public final static String HEADER_EVAL = "criterion\tfirst ref. count\tlast ref. count\tregulon_size\t" +
            "first ref. ratio\tlast ref. ratio\tnum genes first\tnum genes last\tpercent num genes last\t" +
            "num exps first\tnum exps last\tpercent exp last\tlast.percentOrigGenes\t" +
            "last.percentOrigExp\tfirst.precrit\tlast.precrit\tnumber moves\tpassed\tpassed_final\t" +
            "F1g\tprecisiong\trecallg\tF1e\tprecisione\trecalle\tprecisionge\trecallge\tF1recallge\truntime";

    public final static String HEADER_ROC = "criterion\tfirst ref. count\tlast ref. count\tregulon_size\t" +
            "first ref. ratio\tlast ref. ratio\tnum genes first\tnum genes last\tpercent num genes last\t" +
            "num exps first\tnum exps last\tpercent exp last\tlast.percentOrigGenes\t" +
            "last.percentOrigExp\tfirst.precrit\tlast.precrit\tnumber moves\tpassed\tpassed_final\t" +
            "F1g\tspecificityg\tsensitivityg\tF1e\tspecificitye\tsensitivitye\tspecificityge\tsensitivityge\tF1recallge\truntime";

    public final static String HEADER_VBL = "number\tblock_area\tblock_id\tmove_type\tpre_criterion\tfull_crit\texpr_mean_crit\texpr_mean_crit\t" +
            "expr_reg_crit\texpr_kend_crit\texpr_cor_crit\texpr_euc_crit\texpr_spear_crit\tPPI_crit\tfeat_crit\tTF_crit\tpercent_orig_genes\t" +
            "percent_orig_exp\texp_mean\t" +
            "trajectory_position\tFEATURE_INDICES\tmove_class\tnum_genes\tnum_exps";

    public final static String HEADER_VBL_WITHITER = "number\tblock_area\tblock_id\tmove_type\tpre_criterion\tfull_crit\texpr_mean_crit\texpr_mean_crit\t" +
            "expr_reg_crit\texpr_kend_crit\texpr_cor_crit\texpr_euc_crit\texpr_spear_crit\tPPI_crit\tfeat_crit\tTF_crit\tpercent_orig_genes\t" +
            "percent_orig_exp\texp_mean\t" +
            "trajectory_position\tFEATURE_INDICES\tmove_class\tnum_genes\tnum_exps\titeration";


    public final static double DEFAULT_pA = 0.5;
    public final static double DEFAULT_pG = 0.5;
    public final static double DEFAULT_percent_allowed_missing_genes = 0.2;
    public final static double DEFAULT_percent_allowed_missing_exps = 0.2;
    public final static double DEFAULT_percent_allowed_missing_in_block = 0.1;
    public final static double DEFAULT_fraction_genes_for_precritmove = 0.05;
    public final static double DEFAULT_fraction_exps_for_precritmove = 0.05;
    public final static double DEFAULT_noise_level = 1.0;
    public final static double DEFAULT_randomflood_percentage = 0.5;
    public final static double DEFAULT_batch_percentage = 0.2;


    /*The following two parameters are null-dependent. For criteria which don't use a null the size is free to change.*/
    /*minimum allowed block size*/
    public final static int MIN_BLOCK_SIZE = 2;
    /*maximum allowed block size*/
    //public final static int MAX_BLOCK_SIZE = 100;


    public static int DEFAULT_MIN_NONMISSING_FOR_BATCH = 3;


    public static int TOPLIST_LEN = 100;


    public static String UNLABELED = "Unlabeled";

    //R packages
    //also rJava but only for JRI so R call to library() not required
    public static String[] REQUIRED_R_PACKAGES = {
            "irr",
            //"Hmisc",
            "fields",
            "amap",
            //"fastcluster"
            //"polspline",
            //"lars",
            //"geepack",
            //"e1071",
    };


    final static int[] passdummy = new int[0];


    /**
     * @param d
     * @param val
     * @param pos
     * @return
     */
    public static double[] shift(double[] d, double val, int pos) {
        //System.out.println("shift " + pos + "\t" + val);
        //System.out.println("shift d");
        //MoreArray.printArray(d);
        double[] tmp = new double[d.length];
        System.arraycopy(d, 0, tmp, 0, pos);
        tmp[pos] = val;
        System.arraycopy(d, pos, tmp, pos + 1, d.length - pos - 1);
        //System.out.println("shift tmp");
        //MoreArray.printArray(tmp);
        return tmp;
    }

    /**
     * @param d
     * @param val
     * @param pos
     * @return
     */
    public static String[] shift(String[] d, String val, int pos) {
        String[] tmp = new String[d.length];
        System.arraycopy(d, 0, tmp, 0, pos);
        tmp[pos] = val;
        System.arraycopy(d, pos, tmp, pos + 1, d.length - pos - 1);
        return tmp;
    }

    /**
     * @return
     */
    public static String makeCritString() {
        String critstring = "";
        int[] index = stat.createNaturalNumbers(1, MINER_STATIC.CRIT_LABELS.length + 1);
        for (int i = 0; i < MINER_STATIC.CRIT_LABELS.length; i++) {
            critstring += " " + MINER_STATIC.CRIT_LABELS[i] + "=" + index[i];
            if (i < MINER_STATIC.CRIT_LABELS.length - 1)
                critstring += ",";
        }
        return critstring;
    }
}
