package DataMining;

//import com.mysql.jdbc.RandomBalanceStrategy;

import DataMining.util.MakeNRList;
import mathy.SimpleMatrix;
import util.MapArgOptions;
import util.MoreArray;
import util.StringUtil;
import util.TextFile;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

//import org.apache.commons.lang3.StringUtils;

/**
 * MAK workflow
 * <p/>
 * Assumes:
 * - java
 * - R
 * - R packages and JRI (through rJava)
 * - Python (* for now)
 * <p/>
 * Created by Rauf Salamzade
 * Developers: Marcin Joachimiak, Rauf Salamzade, Kevin Meng
 * User: raufs
 * Date: 10/15/15
 */

public class MAKflow_Cori_SLURM_v3 {
    String[] valid_args = {
            "-data", "-parameters"
    };

    HashMap options;
    static String[] arg_desc = {
            "<-data real valued dataset>" +
                    "<-parameter template parameter file>"
    };

    static String arg_desc_str = StringUtil.replace(Arrays.toString(arg_desc), "><", ">\n<");


    static double[] levels = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22};

    static String[] labels = {
            "Compute general statistics about data matrix", //1
            "Make MakeNull task file", //2
            "Execute task file", //3
            "Summarizes samples (InterpolateNulls)", //4
            "Smooths null distributions. (smooth function in Miner.R)", //5
            "Create Starting Points (allPossibleInitial function in Miner.R)", //6
            "Create Parameter Files (CreateParamSet)", //7
            "Create RunMiner task file", //8
            "Execute task file", //9
            "Check that all tasks complete successfully. (ParamforErrMiss)", //10
            "Rerun tasks which did not produce results successfully. (RunMiner)", //11
            "Collects trajectory endpoints. (ListfromDir)", //12
            "Filter endpoints by mean and percent of starting point. (ApplyCut)", //13
            "Bicluster list merge and reconstruction. (ListMergeMembers)", //14
            "Creates parameter files for refinement (CreateParamSet)", //15
            "Create RunMiner task file", //16
            "Execute task file", //17
            "Collects trajectory endpoints. (ListfromDir)", //18
            "Filter endpoints by mean and percent of starting point. (ApplyCut)", //19
            "Bicluster list merge and reconstruction. (ListMergeMembers)" //20
    };

    String uniqueID;

    SimpleMatrix sm;
    Parameters prm;
    Runtime runtime;

    boolean firstLoop = true;
    int startiter;
    int iter = 1;
    boolean refine = false;

    String R_path;
    String prm_path;

    double startlevel = 1;
    double stoplevel = 1;

    String geneids_file;
    String expids_file;
    String Rdata_file;

    String basepath;
    String basename;
    String filename;

    String localuser = null;
    String clusteruser = null;
    String clusteruser_prefix = null;
    String clusterpath = null;
    String localpath = null;

    String clusterWorkspace = null;

    String criterion = null;
    int criterion_index = 160;
    int num_criterion = 1;

    String nullprefix;
    String tab_file;

    //for MakeNull and Parameter
    int Imin = 2;
    int Imax = 200;
    int Jmin = 2;
    int Jmax = 100;
    int nsamp = 50;
    int maxnulljobs = 100;
    String absvect = "0,0,1";

    //for starting points
    int Imax_start = 100;
    int Imin_start = 10;
    int Jmax_start = 50;
    int Jmin_start = 10;
    int useAbs = 0;
    String startoutfile = "";
    String startfileprefix = "";
    int num_start_points = 0;

    String default_walltime = "120:00:00";

    String refinement_starting_points = "";
    String refinement_prefix = "";
    int num_str_pt_refine = 0;

    String[] str_pt_index = null;

    boolean test_mode = false;
    //String mode = "normal";//or "test"

    boolean exclude = false;

    int percent = 66;

    boolean usePseudo = true;
    boolean useLog = false;

    String nullJobID = null;

    long period = 300000; // 300000 = 5 minutes

    String iterationSuffix = "";

    String[] levelTimes = new String[50];
    String[] levelNames = new String[50];
    int levelIndex = 0;

    String[] jobIdsArray = new String[10000];
    int jobIdsArrayCounter = 0;

    String TF_file = null;
    String feat_file = null;
    String inter_file = null;

    String R_TF_data = null;
    String R_feat_data = null;
    String R_inter_data = null;

    int num_null_nodes = 4; // assuming max null jobs  is set to 100
    int num_miner_nodes = 150; // assuming there are 4771 starting points
    int num_rerun_miner_nodes = -1;
    String null_walltime = "15:00:00";
    String miner_walltime = "03:00:00";
    String rerun_miner_walltime = "24:00:00";


    /**
     * @param args
     */
    public MAKflow_Cori_SLURM_v3(String[] args) {

        init(args);
        runFlow(startlevel, stoplevel);
    }

    /**
     * @param startLevel
     * @param stopLevel
     */
    private void runFlow(double startLevel, double stopLevel) {

        double setLevel = startLevel;

        /*level1 create Rdata file */
        if (setLevel <= 1 && (stopLevel >= 1 || stopLevel == 0)) {
            long start = System.currentTimeMillis();
            System.out.println("LEVEL 1");

            String scriptbox = "level1.0/";
            String output = "level1.1/";
            String testoutput = "level1.outref/";
            createFile(scriptbox);
            File dir = createFile(output);

            System.out.println("TEST_CRIT_PARSING");
            System.out.println(criterion);

            // Check to ensure that the first row of the dataset is interpretted the same in Java and in R.

            FileInputStream fstream = null;
            try {
                fstream = new FileInputStream(basename + ".txt");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            try {
                PrintWriter writer = new PrintWriter(output + "firstRow_Java.txt", "UTF-8");
                String line;
                int line_count = 0;
                while ((line = br.readLine()) != null) {
                    if (line_count < 2) {
                        writer.println(line);
                    }
                    line_count++;
                }
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String Rprep = "expr_data <- read.table(\"" + basename + ".txt\",sep=\"\\t\",header=T,row.names=1)\n";

            if (TF_file != null) {
                Rprep += "tf_data <- read.table(\"" + TF_file + "\", sep=\"\\t\",header=T,row.names=1)\n";
                Rprep += "save(tf_data, file=\"" + R_TF_data + "\")\n";
            }
            if (feat_file != null) {
                Rprep += "feat_data <- read.table(\"" + feat_file + "\", sep=\"\\t\",header=T,row.names=1)\n";
                Rprep += "save(feat_data, file=\"" + R_feat_data + "\")\n";
            }

            // Check to ensure that the first row of the dataset is interpretted the same in Java and in R.

            Rprep += "write.table(expr_data[1,], sep=\"\\t\", file =\"" + output + "firstRow_R.txt\")\n";
            Rprep += "source(\"" + R_path + "\")\n";
            Rprep += "mode <- Mode(as.matrix(expr_data))\n";
            Rprep += "write.table(mode, sep=\"\\t\", file=\"" + output + "mode.txt\")\n";
            Rprep += "write.table(rowMeans(expr_data), sep=\"\\t\", file=\"" + output + "rowMeans.txt\")\n";
            Rprep += "write.table(colMeans(expr_data), sep=\"\\t\", file=\"" + output + "colMeans.txt\")\n";
            Rprep += "write.table(apply(expr_data,1,var), sep=\"\\t\", file=\"" + output + "rowVars.txt\")\n";
            Rprep += "write.table(apply(expr_data,2,var), sep=\"\\t\", file=\"" + output + "colVars.txt\")\n";
            Rprep += "cmean <- colMeans(expr_data)\n";
            Rprep += "cvar <- apply(expr_data,2,var)\n";
            Rprep += "rmean <- rowMeans(expr_data)\n";
            Rprep += "rvar <- apply(expr_data,1,var)\n";
            /*
            Rprep += "library(ggplot2)\n";
            Rprep += "p1 <- ggplot(data.frame(rvar), aes(x=rvar)) + geom_histogram(fill=\"blue\") + labs(list(title=\"Variance of Rows\", x=\"Variance\", y=\"Count\"))\n";
            Rprep += "p2 <- ggplot(data.frame(cvar), aes(x=cvar)) + geom_histogram(fill=\"red\") + labs(list(title=\"Variance of Columns\", x=\"Variance\", y=\"Count\"))\n";
            Rprep += "p3 <- ggplot(data.frame(rmean), aes(x=rmean)) + geom_histogram(fill=\"yellow\") + labs(list(title=\"Mean of Rows\", x=\"Mean\", y=\"Count\"))\n";
            Rprep += "p4 <- ggplot(data.frame(cmean), aes(x=cmean)) + geom_histogram(fill=\"green\") + labs(list(title=\"Mean of Columns\", x=\"Mean\", y=\"Count\"))\n";
            Rprep += "pdf(\"" + output + "data_statistics.pdf\", height=10, width=20)\n";
            Rprep += "multiplot(p1, p2, p3, p4, cols=2)\n";
            Rprep += "dev.off()\n";
            */
            Rprep += "write.table(rownames(expr_data), col.names = F, \"" + output + geneids_file + "\",sep=\"\\t\")\n";
            Rprep += "write.table(colnames(expr_data),col.names = F,\"" + output + expids_file + "\",sep=\"\\t\")\n";
            Rprep += "expr_data <- data.matrix(expr_data)\n";

            if (feat_file != null && TF_file != null && inter_file != null) {
                Rprep += "interact_data <- read.table(\"" + inter_file + "\", sep=\"\\t\",header=T,row.names=1)\n";
                Rprep += "tf_data <- read.table(\"" + TF_file + "\", sep=\"\\t\",header=T,row.names=1)\n";
                Rprep += "feat_data <- read.table(\"" + feat_file + "\", sep=\"\\t\",header=T,row.names=1)\n";
                Rprep += "save(expr_data, interact_data, feat_data, tf_data, file=\"" + output + Rdata_file + "\")\n";
            } else if (feat_file != null && inter_file != null) {
                Rprep += "feat_data <- read.table(\"" + feat_file + "\", sep=\"\\t\",header=T,row.names=1)\n";
                Rprep += "interact_data <- read.table(\"" + inter_file + "\", sep=\"\\t\",header=T,row.names=1)\n";
                Rprep += "save(expr_data, feat_data, interact_data, file=\"" + output + Rdata_file + "\")\n";
            } else if (feat_file != null && TF_file != null) {
                Rprep += "feat_data <- read.table(\"" + feat_file + "\", sep=\"\\t\",header=T,row.names=1)\n";
                Rprep += "tf_data <- read.table(\"" + TF_file + "\", sep=\"\\t\",header=T,row.names=1)\n";
                Rprep += "save(expr_data, feat_data, tf_data, file=\"" + output + Rdata_file + "\")\n";
            } else if (TF_file != null && inter_file != null) {
                Rprep += "interact_data <- read.table(\"" + inter_file + "\", sep=\"\\t\",header=T,row.names=1)\n";
                Rprep += "tf_data <- read.table(\"" + TF_file + "\", sep=\"\\t\",header=T,row.names=1)\n";
                Rprep += "save(expr_data, interact_data, tf_data, file=\"" + output + Rdata_file + "\")\n";
            } else if (inter_file != null) {
                Rprep += "interact_data <- read.table(\"" + inter_file + "\", sep=\"\\t\",header=T,row.names=1)\n";
                Rprep += "save(expr_data, interact_data, file=\"" + output + Rdata_file + "\")\n";
            } else if (TF_file != null) {
                Rprep += "tf_data <- read.table(\"" + TF_file + "\", sep=\"\\t\",header=T,row.names=1)\n";
                Rprep += "save(expr_data, tf_data, file=\"" + output + Rdata_file + "\")\n";
            } else if (feat_file != null) {
                Rprep += "feat_data <- read.table(\"" + feat_file + "\", sep=\"\\t\",header=T,row.names=1)\n";
                Rprep += "save(expr_data, feat_data, file=\"" + output + Rdata_file + "\")\n";
            } else {
                Rprep += "save(expr_data, file=\"" + output + Rdata_file + "\")\n";
            }

            String Rprep_script_file = scriptbox + basename + "_prep.R";
            System.out.println("WRITING " + Rprep_script_file);
            TextFile.write(Rprep, Rprep_script_file);

            String shell = "R --vanilla < " + Rprep_script_file + " &> " + output + "out.txt";

            runCmd(shell, scriptbox, "shell.sh");
            System.out.println("RUNNING R --vanilla < " + Rprep_script_file);

            String copydata = "cp " + basename + ".txt level1.1/";
            String copyexprdata = "copy_data.sh";
            runCmd(copydata, scriptbox, copyexprdata);

            String copydata_back = "cp level1.1/" + basename + "_geneids.txt level1.1/" + basename + "_expids.txt " + localpath;
            String copydata_back_shell = "copy_data_genes_exps.sh";
            runCmd(copydata_back, scriptbox, copydata_back_shell);

            String test_file = localpath + "test_file.txt";
            TextFile.write("test", test_file);

            String getJavaVersion = "java -version &> level1.1/local_java_version.txt";
            String getJavaVersionShell = "getLocalJavaVersion.sh";
            runCmd(getJavaVersion, scriptbox, getJavaVersionShell);

            File testdirf = new File(testoutput);
            System.out.println("\"" + output + "\": " + dir.length() + ", " +
                    "\"" + testoutput + "\": " + testdirf.length());

            setLevel += 1;

            long end = System.currentTimeMillis();
            System.out.println("LEVEL 1 COMPLETED: " + (end - start));
            System.out.println("" + useAbs);
            String timestamp = getTimeStamp();
            levelTimes[levelIndex] = timestamp;
            levelNames[levelIndex] = "level1";
            levelIndex++;
        }

        /*level2 MakeNull - make task file */
        if (setLevel == 2 && (stopLevel >= 2 || stopLevel == 0)) {
            System.out.println("LEVEL 2");
            long start = System.currentTimeMillis();

            String input = localpath + "level1.1/";
            String scriptbox = "level2.0/";
            String output = "level2.1/";
            String output_subdir1 = output + "files_of_interest/";
            String output_subdir3 = output + "out_files/";
            String testoutput = "level2.outref";
            createFile(scriptbox);
            File dir = createFile(output);
            createFile(output_subdir1);
            createFile(output_subdir3);

            String frxnsign_param = "T";
            if (useAbs == 1) {
                frxnsign_param = "F";
            }

            if (!doesFileExist(input + basename + ".Rdata") || !doesFileExist(input + basename + "_geneids.txt")) {
                System.out.println("ERROR: The path provided for the R data file or gene header file is invalid. Please check that level 1 finished succesfully and files with the suffix *_geneids.txt and *.Rdata\nExiting now ...");
                System.exit(1);
            }

            String null_wrapper = "#!/bin/bash\nTMPDIR=$SCRATCH\n";
            null_wrapper += "java -Xmx2G DataMining.MakeNull  " +//-Xms2G
                    "-source " + R_path + " " +
                    "-intxt " + localpath + filename + " " +
                    "-inR " + input + basename + ".Rdata " +
                    "-out " + localpath + output_subdir1 + nullprefix + "_$1 " +
                    "-gmin " + Imin + " " +
                    "-gmax " + Imax + " " +
                    "-emin " + Jmin + " " +
                    "-emax " + Jmax + " " +
                    "-nsamp " + nsamp + " " +
                    "-genes " + input + basename + "_geneids.txt " +
                    "-seed $1 " +
                    "-debug n " +
                    "-crits " + criterion + " ";
            if (TF_file != null) {
                null_wrapper += "-inTF " + TF_file + " ";
            }
            if (feat_file != null) {
                null_wrapper += "-infeat " + feat_file + " ";
            }
            null_wrapper += "-frxnsign " + frxnsign_param + " " + "-abs " + absvect +
                    " &> " + localpath + output_subdir3 + "$1.out\n";

            String null_wrapper_file = "run_nulls.sh";
            TextFile.write(null_wrapper, scriptbox + null_wrapper_file);

            String makenull_pbs = "";
            String makenull_pbs_file = basename + "_make_nulls.tasks";
            for (int it = 1; it <= maxnulljobs; it++) {
                makenull_pbs += "bash " + localpath + scriptbox + null_wrapper_file + " " + it + "\n";
            }

            System.out.println("WRITING " + makenull_pbs_file);
            TextFile.write(makenull_pbs, scriptbox + makenull_pbs_file);

            File testdirf = new File(testoutput);
            System.out.println("\"" + output + "\": " + dir.length() + ", " +
                    "\"" + testoutput + "\": " + testdirf.length());

            setLevel += 1;
            long end = System.currentTimeMillis();
            System.out.println("LEVEL 2 COMPLETED: " + (end - start));
            String timestamp = getTimeStamp();
            levelTimes[levelIndex] = timestamp;
            levelNames[levelIndex] = "level2";
            levelIndex++;
        }

        /* level3 MakeNull - run task file */
        if (setLevel == 3 && (stopLevel >= 3 || stopLevel == 0)) {
            long start = System.currentTimeMillis();
            System.out.println("LEVEL 3");
            String input = "level2.0/";
            String scriptbox = "level3.0/";
            createFile(scriptbox);

            if (!doesFileExist(localpath + input + basename + "_make_nulls.tasks")) {
                System.out.println("ERROR: The path provided to the null task file " + localpath + input + basename + "_make_nulls.tasks does not exist\nExiting now...");
                System.exit(1);
            }

            try {
                run_tf(localpath + input + basename + "_make_nulls.tasks", scriptbox, null_walltime, num_null_nodes);
            } catch (IOException e) {
                e.printStackTrace();
            }

            long end = System.currentTimeMillis();
            System.out.println("LEVEL 3 COMPLETED: " + (end - start));
            setLevel += 1;
            String timestamp = getTimeStamp();
            levelTimes[levelIndex] = timestamp;
            levelNames[levelIndex] = "level3";
            levelIndex++;
        }

        /* level4 InterpolateNulls*/
        if (setLevel == 4 && (stopLevel >= 4 || stopLevel == 0)) {
            System.out.println("LEVEL 4");
            long start = System.currentTimeMillis();

            String input = "level2.1/files_of_interest/";
            String scriptbox = "level4.0/";
            String output = "level4.1/";
            createFile(scriptbox);
            File dir = createFile(output);

            if (isDirectoryEmpty(localpath + input)) {
                System.out.println("ERROR: The directory where null output files should be located: " + localpath + input + " is empty. Please check whether the MakeNull task file was successfully run.\nExiting now...");
                System.exit(1);
            }

            String interpolate = "java DataMining.InterPolateNulls " + input + " " + output + nullprefix + " &> " + output + "InterPolateNulls.out";
            String interpolate_shell = "runInterpolate.sh";

            runCmd(interpolate, scriptbox, interpolate_shell);

            setLevel += 1;
            long end = System.currentTimeMillis();
            System.out.println("LEVEL 4 COMPLETED: " + (end - start));
            String timestamp = getTimeStamp();
            levelTimes[levelIndex] = timestamp;
            levelNames[levelIndex] = "level4";
            levelIndex++;
        }

        /* level5 smoooooth nulls */
        if (setLevel == 5 && (stopLevel >= 5 || stopLevel == 0)) {
            System.out.println("LEVEL 5");
            long start = System.currentTimeMillis();

            String input = "level4.1/";
            String scriptbox = "level5.0/";
            String output = "level5.1/";
            String testoutput = "level5.outref";
            createFile(scriptbox);
            File dir = createFile(output);

            if (isDirectoryEmpty(localpath + input)) {
                System.out.println("ERROR: The directory where the raw null files should be located: " + localpath + input + " is empty. Please check whether the MakeNull task file was successfully run and whether InterpolateNulls was successful.\nExiting now...");
                System.exit(1);
            }

            String Rsmooth_script = "library(\"fields\")\n";
            System.out.println(R_path);
            Rsmooth_script += "set.seed(" + MINER_STATIC.RANDOM_SEEDS[0] + ")\n";
            Rsmooth_script += "source(\"" + R_path + "\")\n";
            Rsmooth_script += "setwd(\"" + localpath + input + "\")\n";
            Rsmooth_script += "sessionInfo()\n";
            if (TF_file != null || feat_file != null || inter_file != null) {
                Rsmooth_script += "tpsSmooth1D(\"" + nullprefix + "\"," + Imin + "," + Imax + ", usePseudo=" + (usePseudo ? "T" : "F") + ")\n";
            }
            Rsmooth_script += "tpsSmooth(\"" + nullprefix + "\"," + Jmin + "," + Jmax + "," + Imin + "," + Imax +
                    ",usePseudo=" + (usePseudo ? "T" : "F") + ", useLog=" + (useLog ? "T" : "F") + ")";
            String Rsmooth_script_file = basename + "_smooth.R";

            TextFile.write(Rsmooth_script, scriptbox + Rsmooth_script_file);

            String Rsmooth_cmd = "#!/bin/bash\n" +
                    "R --vanilla <" + localpath + "level5.0/" + Rsmooth_script_file + " &> " + localpath + "level5.1/" + Rsmooth_script_file + ".out\n";
            String Rsmooth_shell = scriptbox + "Rsmooth_shell.sh";

            TextFile.write(Rsmooth_cmd, Rsmooth_shell);

            System.out.println("RUNNING " + Rsmooth_script_file);
            System.out.println("bash " + localpath + Rsmooth_shell);

            runCmd("bash " + localpath + "/" + Rsmooth_shell);

            String mv_script = "mv " + localpath + "level4.1/*full.txt " + localpath + "level5.1/";
            String mv_shell = "mv.sh";

            runCmd(mv_script, scriptbox, mv_shell);

            File testdirf = new File(testoutput);
            System.out.println("\"" + output + "\": " + dir.length() + ", " +
                    "\"" + testoutput + "\": " + testdirf.length());

            setLevel += 1;
            long end = System.currentTimeMillis();
            System.out.println("LEVEL 5 COMPLETED: " + (end - start));
            String timestamp = getTimeStamp();
            levelTimes[levelIndex] = timestamp;
            levelNames[levelIndex] = "level5";
            levelIndex++;
        }

        /* level6 create starting points (R) */
        if (setLevel == 6 && (stopLevel >= 6 || stopLevel == 0)) {
            System.out.println("LEVEL 6");
            long start = System.currentTimeMillis();

            String input = "level1.1/";
            String scriptbox = "level6.0/";
            String output = "level6.1/";
            String testoutput = "level6.outref/";

            createFile(scriptbox);
            createFile(output);

            String Rstarts_script = "library(\"amap\")\n";
            Rstarts_script += "set.seed(" + MINER_STATIC.RANDOM_SEEDS[0] + ")\n";
            Rstarts_script += "setwd(\"" + localpath + "/" + input + "\")\n";
            Rstarts_script += "source(\"" + R_path + "\")\n";
            Rstarts_script += "load(\"" + Rdata_file + "\")\n";
            Rstarts_script += "set.seed(" + MINER_STATIC.RANDOM_SEEDS[0] + ")\n";
            Rstarts_script += "expr_data_row=t(apply(expr_data,1,missfxn))\n";
            Rstarts_script += "expr_data_col=apply(expr_data,2,missfxn)\n";

            Rstarts_script += "nbs1=allpossibleInitial(expr_data_row," + Imin_start + "," + Imax_start + "," + Jmin_start + "," + Jmax_start + ",\"correlation\",useAbs=" + useAbs + ", isCol=1)\n";
            Rstarts_script += "nbs2=allpossibleInitial(expr_data_col," + Imin_start + "," + Imax_start + "," + Jmin_start + "," + Jmax_start + ",\"correlation\",useAbs=" + useAbs + ", isCol=0)\n";
            Rstarts_script += "nbsall <- c(nbs1, nbs2)\n";

            startoutfile = output + basename + "_STARTS_abs" + useAbs + "_I" + Imin_start + "_" + Imax_start + "_J" + Jmin_start + "_" + Jmax_start + "_RC.txt";
            String startoutfile_redundant = startoutfile.substring(0, startoutfile.length() - 4) + "_redundant.txt";
            startfileprefix = basename + "_STARTS_abs" + useAbs + "_I" + Imin_start + "_" + Imax_start + "_J" + Jmin_start + "_" + Jmax_start + "_RC";
            Rstarts_script += "write.table(nbsall, file=\"" + localpath + "/" + startoutfile_redundant + "\", sep=\"\\t\", col.names=F,row.names=F)\n";

            String Rstarts_script_file = basename + "_starts.R";
            String starts_shell = "#!/bin/bash\n" +
                    "R --vanilla < " + localpath + "level6.0/" + Rstarts_script_file + " &>  " + localpath + "level6.1/" + Rstarts_script_file + ".out";
            String starts_shell_file = "Rstarts_shell.sh";
            TextFile.write(starts_shell, scriptbox + starts_shell_file);
            String Rstarts_cmd = "bash " + scriptbox + starts_shell_file;

            TextFile.write(Rstarts_script, scriptbox + Rstarts_script_file);
            System.out.println("RUNNING " + Rstarts_cmd);

            runCmd(Rstarts_cmd);

            String argument_list[] = new String[]{startoutfile_redundant, startoutfile};

            MakeNRList.main(argument_list);

            startoutfile = localpath + output + basename + "_STARTS_abs" + useAbs + "_I" + Imin_start + "_" + Imax_start + "_J" + Jmin_start + "_" + Jmax_start + "_RC.txt";
            startfileprefix = basename + "_STARTS_abs" + useAbs + "_I" + Imin_start + "_" + Imax_start + "_J" + Jmin_start + "_" + Jmax_start + "_RC";

            String wcl = "";
            wcl += "wc -l " + startoutfile + " &> " + localpath + scriptbox + "wcl.txt";
            System.out.println(wcl);
            String wcl_file = "wcl.sh";
            runCmd(wcl, scriptbox, wcl_file);

            // check whether startfile has correct name and path
            try {
                String[] fileList = returnNulls(localpath + scriptbox + "wcl.txt", 1);
                this.num_start_points = Integer.parseInt((fileList[0].split("\\s+"))[0]);
                System.out.println("NUMBER OF START POINTS: " + num_start_points);
            } catch (Exception e) {
                e.printStackTrace();
            }

            setLevel += 1;

            File testdirf = new File(testoutput);
            System.out.println("\"" + output + "\": " + testdirf.length() + ", " +
                    "\"" + testoutput + "\": " + testdirf.length());

            long end = System.currentTimeMillis();
            System.out.println("LEVEL 6 COMPLETED: " + (end - start));
            String timestamp = getTimeStamp();
            levelTimes[levelIndex] = timestamp;
            levelNames[levelIndex] = "level6";
            levelIndex++;
        }

        for (int counter = 0; counter <= iter; counter++) {
            /* level7 create parameter files */
            System.out.println(firstLoop + " " + startiter);
            if (startiter != 0 && firstLoop) {
                counter = startiter - 1;
                System.out.println("NEW COUNTER: " + counter);
                firstLoop = false;
            }
            if (setLevel == 7 && (stopLevel >= 7 || stopLevel == 0)) {
                System.out.println("LEVEL 7");
                long start = System.currentTimeMillis();

                String input = "level6.1/";

                try {
                    criterion_index = StringUtil.getFirstEqualsIndex(MINER_STATIC.CRIT_LABELS, criterion);
                } catch (Exception e) {
                    System.out.println("The criterion combination specified is not valid. Try shuffling the order of the criteria.\n" +
                            "Remember, the feat and MAXTF should always be the last criteria in the combination.");
                    e.printStackTrace();
                }

                int[] row_col = new int[0];

                try {
                    row_col = setParameters(filename);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int num_row = row_col[0];
                int num_col = row_col[1];

                String[] absvectArray = absvect.split(",");
                int[] absvectIntArray = new int[absvectArray.length];
                for (int a = 0; a < absvectArray.length; a++) {
                    absvectIntArray[a] = Integer.parseInt(absvectArray[a]);
                }

                int iteration = counter + 1;
                String scriptbox = "level7_iter" + iteration + ".0/";
                String output = "level7_iter" + iteration + ".1/";
                String testoutput = "level7_iter" + iteration + ".outref/";
                String subdir_1 = output + "out_files/";
                String subdir_2 = output + "results/";
                String subdir_3 = output + "toplist_files/";

                prm.OUTPREFIX = "iter_" + counter;
                if (exclude) {
                    if (counter != 0 && !doesFileExist(localpath + "level12.1/results_" + basename + "_concat" + counter + ".vbl")) {
                        System.out.println("ERROR: Exclusion is specified but the exclusion file path is broken: " + localpath + "level12.1/results_" + basename + "_concat" + counter + ".vbl" + ".\nExiting now...");
                        System.exit(1);
                    }
                    prm.EXCLUDE_LIST_PATH = localpath + "level12.1/results_" + basename + "_concat" + counter + ".vbl";
                } else {
                    prm.EXCLUDE_LIST_PATH = "";
                }
                prm.CRIT_TYPE_INDEX = criterion_index;
                prm.PRECRIT_TYPE_INDEX = criterion_index;
                prm.RANDOM_SEED = MINER_STATIC.RANDOM_SEEDS[counter];
                prm.OUTDIR = localpath + subdir_2;
                prm.EXPR_DATA_PATH = localpath + "/level1.1/" + basename + ".txt";
                prm.FEAT_DATA_PATH = "";
                prm.INTERACT_DATA_PATH = "";
                prm.MEANINTERACT_PATH = "";
                prm.SDINTERACT_PATH = "";
                prm.R_DATA_PATH = localpath + "/level1.1/" + basename + ".Rdata";
                prm.R_METHODS_PATH = R_path;
                prm.TFTARGETMAP_PATH = "";
                prm.MAXMOVES = new int[]{500, 30};
                prm.IMAX = Imax;
                prm.IMIN = Imin;
                prm.JMAX = Jmax;
                prm.JMIN = Jmin;
                prm.BATCH_PERC = 0.4;
                prm.PLATEAU_PERC = 0.2;
                prm.OVERRIDE_SHAVING = false;
                prm.SIZE_PRECRIT_LIST = 50;
                prm.PBATCH = 1.0;
                prm.DATA_LEN_EXPS = num_col;
                prm.DATA_LEN_GENES = num_row;
                prm.RANDOMFLOOD = true;
                prm.EXCLUDE_OVERLAP_THRESHOLD = 0.5;
                if (useAbs == 1) {
                    prm.USE_ABS = true;
                    prm.USE_ABS_AR = absvectIntArray;
                    prm.FRXN_SIGN = false;
                } else {
                    prm.USE_ABS = false;
                    prm.USE_ABS_AR = absvectIntArray;
                    prm.FRXN_SIGN = true;
                }

                prm.MEANTF_PATH = "";
                prm.SDTF_PATH = "";
                prm.TFTARGETMAP_PATH = "";

                prm.MEANFEAT_PATH = "";
                prm.SDFEAT_PATH = "";
                prm.FEAT_DATA_PATH = "";

                prm.MEANINTERACT_PATH = "";
                prm.SDINTERACT_PATH = "";
                prm.INTERACT_DATA_PATH = "";

                prm.MEANFEM_PATH = "";
                prm.SDFEM_PATH = "";
                prm.MEANFEMC_PATH = "";
                prm.SDFEMC_PATH = "";
                prm.MEANFEMR_PATH = "";
                prm.SDFEMR_PATH = "";

                prm.MEANKEND_PATH = "";
                prm.MEANKENDC_PATH = "";
                prm.MEANKENDR_PATH = "";
                prm.SDKEND_PATH = "";
                prm.SDKENDC_PATH = "";
                prm.SDKENDR_PATH = "";

                prm.MEANMSE_PATH = "";
                prm.MEANMSEC_PATH = "";
                prm.MEANMSER_PATH = "";
                prm.SDMSER_PATH = "";
                prm.SDMSE_PATH = "";
                prm.SDMSEC_PATH = "";

                prm.MEANMEAN_PATH = "";
                prm.SDMEAN_PATH = "";

                criterion_index = StringUtil.getFirstEqualsIndexIgnoreCase(MINER_STATIC.CRIT_LABELS, criterion);

                // Specify Transcription Factor Criterion Data/Null Locations
                if (TF_file != null) {
                    prm.MEANTF_PATH = localpath + "level5.1/" + nullprefix + "MAXTF_median_full.txt";
                    prm.SDTF_PATH = localpath + "level5.1/" + nullprefix + "MAXTF_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANTF_PATH) || !doesFileExist(prm.SDTF_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the MAXTF criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                    prm.TFTARGETMAP_PATH = TF_file;
                }

                // Specify Features Criterion Data/Null Locations
                if (feat_file != null) {
                    prm.MEANFEAT_PATH = localpath + "level5.1/" + nullprefix + "FEAT_median_full.txt";
                    prm.SDFEAT_PATH = localpath + "level5.1/" + nullprefix + "FEAT_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANFEAT_PATH) || !doesFileExist(prm.SDFEAT_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the feat criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                    prm.FEAT_DATA_PATH = feat_file;
                }

                // Specify Protein-Protein Interaction Criterion Data/Null Locations
                if (inter_file != null) {
                    prm.MEANINTERACT_PATH = localpath + "level5.1/" + nullprefix + "INTER_median_full.txt";
                    prm.SDINTERACT_PATH = localpath + "level5.1/" + nullprefix + "INTER_0.5IQR_full.txt";
                    prm.INTERACT_DATA_PATH = inter_file;
                    if (!doesFileExist(prm.MEANINTERACT_PATH) || !doesFileExist(prm.SDINTERACT_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the inter criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }

                // Specify Expression Criterion
                if (criterion.contains("FEM") && !criterion.contains("FEMC") && !criterion.contains("FEMR")) {
                    prm.MEANFEM_PATH = localpath + "level5.1/" + nullprefix + "FEM_median_full.txt";
                    prm.SDFEM_PATH = localpath + "level5.1/" + nullprefix + "FEM_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANFEM_PATH) || !doesFileExist(prm.SDFEM_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the FEM criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }
                if (criterion.contains("FEMC")) {
                    prm.MEANFEMC_PATH = localpath + "level5.1/" + nullprefix + "FEMC_median_full.txt";
                    prm.SDFEMC_PATH = localpath + "level5.1/" + nullprefix + "FEMC_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANFEMC_PATH) || !doesFileExist(prm.SDFEMC_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the FEMC criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }
                if (criterion.contains("FEMR")) {
                    prm.MEANFEMR_PATH = localpath + "level5.1/" + nullprefix + "FEMR_median_full.txt";
                    prm.SDFEMR_PATH = localpath + "level5.1/" + nullprefix + "FEMR_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANFEMR_PATH) || !doesFileExist(prm.SDFEMR_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the FEMR criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }
                if (criterion.contains("MSE") && !criterion.contains("MSEC") && !criterion.contains("MSER")) {
                    prm.MEANMSE_PATH = localpath + "level5.1/" + nullprefix + "MSE_median_full.txt";
                    prm.SDMSE_PATH = localpath + "level5.1/" + nullprefix + "MSE_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANMSE_PATH) || !doesFileExist(prm.SDMSE_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the MSE criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }
                if (criterion.contains("MSEC")) {
                    prm.MEANMSEC_PATH = localpath + "level5.1/" + nullprefix + "MSEC_median_full.txt";
                    prm.SDMSEC_PATH = localpath + "level5.1/" + nullprefix + "MSEC_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANMSEC_PATH) || !doesFileExist(prm.SDMSEC_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the MSEC criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }
                if (criterion.contains("MSER")) {
                    prm.MEANMSER_PATH = localpath + "level5.1/" + nullprefix + "MSER_median_full.txt";
                    prm.SDMSER_PATH = localpath + "level5.1/" + nullprefix + "MSER_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANMSER_PATH) || !doesFileExist(prm.SDMSER_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the MSER criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }
                if (criterion.contains("KENDALL") && !criterion.contains("KENDALLC") && !criterion.contains("KENDALLR")) {
                    prm.MEANKEND_PATH = localpath + "level5.1/" + nullprefix + "KENDALL_median_full.txt";
                    prm.SDKEND_PATH = localpath + "level5.1/" + nullprefix + "KENDALL_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANKEND_PATH) || !doesFileExist(prm.SDKEND_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the KENDALL criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }
                if (criterion.contains("KENDALLR")) {
                    prm.MEANKENDR_PATH = localpath + "level5.1/" + nullprefix + "KENDALLR_median_full.txt";
                    prm.SDKENDR_PATH = localpath + "level5.1/" + nullprefix + "KENDALLR_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANKENDR_PATH) || !doesFileExist(prm.SDKENDR_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the KENDALLR criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }
                if (criterion.contains("KENDALLC")) {
                    prm.MEANKENDC_PATH = localpath + "level5.1/" + nullprefix + "KENDALLC_median_full.txt";
                    prm.SDKENDC_PATH = localpath + "level5.1/" + nullprefix + "KENDALLC_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANKENDC_PATH) || !doesFileExist(prm.SDKENDC_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the KENDALLC criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }
                if (criterion.contains("BINARY") && !criterion.contains("BINARYC") && !criterion.contains("BINARYR")) {
                    prm.MEANBINARY_PATH = localpath + "level5.1/" + nullprefix + "BINARY_median_full.txt";
                    prm.SDBINARY_PATH = localpath + "level5.1/" + nullprefix + "BINARY_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANBINARY_PATH) || !doesFileExist(prm.SDBINARY_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the BINARY criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }
                if (criterion.contains("BINARYR")) {
                    prm.MEANBINARYR_PATH = localpath + "level5.1/" + nullprefix + "BINARYR_median_full.txt";
                    prm.SDBINARYR_PATH = localpath + "level5.1/" + nullprefix + "BINARYR_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANBINARYR_PATH) || !doesFileExist(prm.SDBINARYR_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the BINARYR criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }
                if (criterion.contains("BINARYC")) {
                    prm.MEANBINARYC_PATH = localpath + "level5.1/" + nullprefix + "BINARYC_median_full.txt";
                    prm.SDBINARYC_PATH = localpath + "level5.1/" + nullprefix + "BINARYC_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANBINARYC_PATH) || !doesFileExist(prm.SDBINARYC_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the BINARYC criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }
                if (criterion.equalsIgnoreCase("MEAN")) {
                    prm.MEANMEAN_PATH = localpath + "level5.1/" + nullprefix + "MEAN_median_full.txt";
                    prm.SDMEAN_PATH = localpath + "level5.1/" + nullprefix + "MEAN_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANMEAN_PATH) || !doesFileExist(prm.SDMEAN_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the MEAN criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }

                prm.clean_paths();
                prm.write(prm_path);

                createFile(scriptbox);
                File dir = createFile(output);
                createFile(subdir_1);
                createFile(subdir_2);
                createFile(subdir_3);

                String argument[];

                if (!doesFileExist(startoutfile)) {
                    System.out.println("ERROR: The starting point biclusters file does not exist: " + startoutfile + "\nExiting now...");
                    System.exit(1);
                }

                argument = new String[]{"-param", prm_path, "-in", startoutfile, "-out", output + basename,
                        "-samp", "" + (counter + 1), "-minsamp", "" + "" + counter, "-top", "y", "-silent",
                        "t", "-crits", criterion.toUpperCase(), "-precrits", criterion.toUpperCase()};

                System.out.println("RUNNING: " + MoreArray.toString(argument, ","));

                CreateParamSet.main(argument);

                System.out.println("PARAMETERS CREATED!");

                setLevel += 1;

                File testdirf = new File(testoutput);
                System.out.println("\"" + output + "\": " + dir.length() + ", " +
                        "\"" + testoutput + "\": " + testdirf.length());
                long end = System.currentTimeMillis();
                System.out.println("LEVEL 7 COMPLETED: " + (end - start));
                String timestamp = getTimeStamp();
                levelTimes[levelIndex] = timestamp;
                levelNames[levelIndex] = "level7_iter" + (counter + 1);
                levelIndex++;
            }

            /* level8 - RunMiner - make task file */
            if (setLevel == 8 && (stopLevel >= 8 || stopLevel == 0)) {
                long start = System.currentTimeMillis();
                int iteration = counter + 1;
                String input = "level7_iter" + iteration + ".1/" + basename + "_1/";
                String scriptbox = "level8_iter" + iteration + ".0/";
                String output = "level8_iter" + iteration + ".1/";
                String subdir_1 = output + "out_files/";
                createFile(scriptbox);
                createFile(output);
                createFile(subdir_1);

                String run_miner_wrapper = "#!/bin/bash\nTMPDIR=$SCRATCH\njava  -Xmx1G DataMining.RunMiner -param " + localpath + input + prm.OUTPREFIX + "_" + startfileprefix + "_$1__" + criterion.toUpperCase() + "__AG_" + MINER_STATIC.RANDOM_SEEDS[counter] + "__parameters.txt " +
                        "&> " + localpath + subdir_1 + prm.OUTPREFIX + "_" + startfileprefix + "_$1__" + criterion.toUpperCase() + "__AG_" + MINER_STATIC.RANDOM_SEEDS[counter] + "_out.txt\n";
                String run_miner_wrapper_file = "run_miner.sh";
                TextFile.write(run_miner_wrapper, scriptbox + run_miner_wrapper_file);

                String task_file = basename + "_run_miner.tasks";
                String task_script = "";
                for (int it = 0; it < num_start_points; it++) {
                    task_script += "bash " + localpath + scriptbox + run_miner_wrapper_file + " " + it + "\n";
                }
                System.out.println("RunMiner task file written!");
                TextFile.write(task_script, scriptbox + task_file);

                long end = System.currentTimeMillis();
                System.out.println("LEVEL 8 COMPLETED: " + (end - start));
                setLevel++;
                String timestamp = getTimeStamp();
                levelTimes[levelIndex] = timestamp;
                levelNames[levelIndex] = "level8_iter" + (counter + 1);
                levelIndex++;
            }

            /* level9 - RunMiner - run task file */
            if (setLevel == 9 && (stopLevel >= 9 || stopLevel == 0)) {
                long start = System.currentTimeMillis();
                System.out.println("LEVEL 9");
                int iteration = counter + 1;
                String input = "level8_iter" + iteration + ".0/"; // where the task file is
                String scriptbox = "level9_iter" + iteration + ".0/";
                createFile(scriptbox);

                if (!doesFileExist(localpath + input + basename + "_run_miner.tasks")) {
                    System.out.println("ERROR: The RunMiner task file does not exist: " + localpath + input + basename + "_run_miner.tasks" + "\nExiting now...");
                    System.exit(1);
                }

                try {
                    run_tf(localpath + input + basename + "_run_miner.tasks", scriptbox, miner_walltime, num_miner_nodes);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                long end = System.currentTimeMillis();
                System.out.println("LEVEL 9 COMPLETED: " + (end - start));
                setLevel += 1;
                String timestamp = getTimeStamp();
                levelTimes[levelIndex] = timestamp;
                levelNames[levelIndex] = "level9";
                levelIndex++;
            }

            /* level10 - ParamforErrMiss - rerun missing */
            if (setLevel == 10 && (stopLevel >= 10 || stopLevel == 0)) {
                getTimeStamp();
                System.out.println("LEVEL 10");
                long start = System.currentTimeMillis();

                int iteration = counter + 1;
                String scriptbox = "level10_iter" + iteration + ".0/";
                String output = "level10_iter" + iteration + ".1/";
                String testoutput = "level10_iter" + iteration + ".outref/";

                String toplist_dir = output + "toplist_files/";
                String pfem_dir = output + "ParamforErrMiss/";

                createFile(scriptbox);
                createFile(output);
                File dir = createFile(toplist_dir);
                createFile(pfem_dir);

                // check that at least 90% of the starting points produced results, if otherwise, exit...

                int num_results = new File(localpath + "level7_iter" + iteration + ".1/results/").listFiles().length / 4;
                double percent_complete = Double.parseDouble(String.valueOf(num_results)) / Double.parseDouble(String.valueOf(num_start_points));

                if (percent_complete < 0.9) {
                    System.out.println("ERROR: Less than 90% of the starting points produced successful results. This indicates that there was an overall issue with running RunMiner biclustering " +
                            "and not just some technical difficulties with some of the starting points. Please check levels 8 and 9.\nExiting now...");
                    System.exit(1);
                }

                String pfem_script_file = basename + "_run_ParamforErrMiss.sh";

                String pfem_script = "java -Xmx1G DataMining.ParamforErrMiss " +
                        localpath + "level7_iter" + iteration + ".1/results/ " +
                        localpath + "level7_iter" + iteration + ".1/" + basename + "_1/ " +
                        localpath + "level10_iter" + iteration + ".1/ParamforErrMiss/ " +
                        "&> " + localpath + "level10_iter" + iteration + ".1/param_for_err_miss.out";

                runCmd(pfem_script, scriptbox, pfem_script_file);

                String task_file = basename + "_rerun_miner.tasks";
                String task_script = "";

                File folder = new File(localpath + "level10_iter" + iteration + ".1/ParamforErrMiss/");
                File[] listOfFiles = folder.listFiles();

                String rerun_miner_wrapper = "#!/bin/bash\nTMPDIR=$SCRATCH\njava  -Xmx1G DataMining.RunMiner -param $1 &> $2";
                String rerun_miner_wrapper_file = "rerun_miner.sh";
                TextFile.write(rerun_miner_wrapper, scriptbox + rerun_miner_wrapper_file);

                for (int i = 0; i < listOfFiles.length; i++) {
                    if (listOfFiles[i].getName().endsWith("parameters.txt")) {
                        String infile = localpath + "level10_iter" + iteration + ".1/ParamforErrMiss/" + listOfFiles[i].getName();
                        String outfile = localpath + "level8_iter" + iteration + ".1/out_files/" + listOfFiles[i].getName() + "_out.txt";

                        //TODO: This might not work but wrapper
                        task_script += "bash " + localpath + scriptbox + rerun_miner_wrapper_file + " " + infile + " " + outfile + "\n";
                    }
                }

                TextFile.write(task_script, scriptbox + task_file);
                System.out.println("Re-RunMiner task file written!");

                File testdir = new File(testoutput);
                System.out.println("\"" + toplist_dir + "\": " + dir.length() + ", " +
                        "\"" + testoutput + "\": " + testdir.length());
                long end = System.currentTimeMillis();
                System.out.println("LEVEL 10 COMPLETED: " + (end - start));
                setLevel++;
                String timestamp = getTimeStamp();
                levelTimes[levelIndex] = timestamp;
                levelNames[levelIndex] = "level10_iter" + (counter + 1);
                levelIndex++;
            }

            /* level11 Rerun Miner */ // And move results to level 11.1
            if (setLevel == 11 && (stopLevel >= 11 || stopLevel == 0)) {
                System.out.println("LEVEL11");
                long start = System.currentTimeMillis();
                int iteration = counter + 1;

                String input = "level10_iter" + iteration + ".0/";
                String scriptbox = "level11_iter" + iteration + ".0/";
                String output = "level11_iter" + iteration + ".1/";
                String testoutput = "level12_iter" + iteration + ".outref/";

                createFile(scriptbox);
                createFile(output);

                if (!doesFileExist(localpath + input + basename + "_rerun_miner.tasks")) {
                    System.out.println("ERROR: The Re-RunMiner task file does not exist: " + localpath + input + basename + "_rerun_miner.tasks" + "\nExiting now...");
                    System.exit(1);
                }

                String wcl = "";
                wcl += "wc -l " + localpath + input + basename + "_rerun_miner.tasks" + " &> " + localpath + scriptbox + "wcl.txt";
                System.out.println(wcl);
                String wcl_file = "wcl.sh";
                runCmd(wcl, scriptbox, wcl_file);

                int num_rerun = 0;
                try {
                    String[] fileList = returnNulls(localpath + scriptbox + "wcl.txt", 1);
                    num_rerun = Integer.parseInt((fileList[0].split("\\s+"))[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (num_rerun_miner_nodes == -1) {
                    num_rerun_miner_nodes = num_rerun / 32 + 1;
                }

                try {
                    run_tf(localpath + input + basename + "_rerun_miner.tasks", scriptbox, rerun_miner_walltime, num_rerun_miner_nodes);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                long end = System.currentTimeMillis();
                System.out.println("LEVEL 11 COMPLETED: " + (end - start));
                setLevel++;
                String timestamp = getTimeStamp();
                levelTimes[levelIndex] = timestamp;
                levelNames[levelIndex] = "level11_iter" + (counter + 1);
                levelIndex++;
            }

            /* level12 ListfromDir */
            if (setLevel == 12 && (stopLevel >= 12 || stopLevel == 0)) {
                System.out.println("LEVEL12");
                long start = System.currentTimeMillis();
                int iteration = counter + 1;
                String input = "level7_iter" + iteration + ".1/toplist_files/";
                String scriptbox = "level12.0/";
                String output = "level12.1/";
                String testoutput = "level12.outref/";

                createFile(output);
                createFile(scriptbox);

                String mv = "";
                mv += "mv " + localpath + "level7_iter" + iteration + ".1/results/*toplist.txt " + localpath + "level7_iter" + iteration + ".1/toplist_files/\n";
                String mv_shell = "ssh.sh";
                runCmd(mv, scriptbox, mv_shell);

                String argument[] = new String[]{input, output + "results_" + basename + "_iter" + (counter + 1) + "_orig" + ".vbl"};
                DataMining.util.ListfromDir.main(argument);

                String concatenate_file = "concatenate.sh";

                DataMining.util.ListfromDir ld = new DataMining.util.ListfromDir();
                ld.callDirect(input, output + "results_" + basename + "_iter" + (counter + 1) + ".vbl", counter + 1);

                String concatenate_script = "";

                iterationSuffix += "" + (counter + 1);

                File folder = new File(localpath + "level12.1/");
                File[] listOfFiles = folder.listFiles();

                int count = 0;
                int count_orig = 0;
                for (int i = 0; i < listOfFiles.length; i++) {
                    if (listOfFiles[i].isFile() && listOfFiles[i].getName().contains("_iter") && !(listOfFiles[i].getName().contains("_orig"))) {
                        if (count == 0) {
                            concatenate_script += "cat " + listOfFiles[i] + " > " + output + "results_" + basename + "_concat" + (counter + 1) + ".vbl\n";
                        } else {
                            concatenate_script += "sed '1d' " + listOfFiles[i] + " >> " + output + "results_" + basename + "_concat" + (counter + 1) + ".vbl\n";
                        }
                        count += 1;
                    } else if (listOfFiles[i].isFile() && listOfFiles[i].getName().contains("_iter") && listOfFiles[i].getName().contains("_orig")) {
                        if (count_orig == 0) {
                            concatenate_script += "cat " + listOfFiles[i] + " > " + output + "results_" + basename + ".vbl\n";
                        } else {
                            concatenate_script += "sed '1d' " + listOfFiles[i] + " >> " + output + "results_" + basename + ".vbl\n";
                        }
                        count_orig += 1;
                    }
                }

                runCmd(concatenate_script, scriptbox, concatenate_file);

                File testdirf = new File(testoutput);
                System.out.println("\"" + output + "\": " + testdirf.length() + ", " + "\"" + testoutput + "\": " + testdirf.length());

                long end = System.currentTimeMillis();
                System.out.println("LEVEL 12 COMPLETED: " + (end - start));
                String timestamp = getTimeStamp();
                levelTimes[levelIndex] = timestamp;
                levelNames[levelIndex] = "level12";
                levelIndex++;
                setLevel++;
            }

            if (iter - counter > 1 && startLevel < 9) {
                setLevel = 7;
            }

        }

        /* level13 ApplyCut */
        if (setLevel == 13 && (stopLevel >= 13 || stopLevel == 0)) {
            System.out.println("LEVEL 13");
            long start = System.currentTimeMillis();

            String input = "level12.1/";
            String scriptbox = "level13.0/";
            String output = "level13.1/";
            String testoutput = "level13.outref/";

            createFile(scriptbox);
            File dir = createFile(output);

            String input_file = "results_" + basename + ".vbl";

            if (!doesFileExist(localpath + input + input_file)) {
                System.out.println("ERROR: The ListfromDir result file does not exist: " + localpath + input + input_file + "\nExiting now...");
                System.exit(1);
            }

            percent = 66;
            String number = "NA";
            System.out.println(localpath + input + input_file);
            String argument[] = new String[]{localpath + input + input_file, percent + "%", number};
            DataMining.util.ApplyCut.main(argument);

            String move = "mv results_" + basename + "_cut_scoreperc* " + output;
            String move_file = "move.sh";
            runCmd(move, scriptbox, move_file);

            if (!new File(localpath + output + "results_" + basename + "_cut_scoreperc" + percent + ".0_exprNaN_0.0.txt").exists()) {
                System.out.println("Expected file not seen. Exiting.");
                System.exit(0);
            }

            File testdirf = new File(testoutput);
            System.out.println("\"" + output + "\": " + dir.length() + ", " +
                    "\"" + testoutput + "\": " + testdirf.length());
            long end = System.currentTimeMillis();
            System.out.println("LEVEL 13 COMPLETED: " + (end - start));
            setLevel++;
            String timestamp = getTimeStamp();
            levelTimes[levelIndex] = timestamp;
            levelNames[levelIndex] = "level13";
            levelIndex++;
        }

        /* level14 ListMergeMembers final */
        if (setLevel == 14 && (stopLevel >= 14 || stopLevel == 0)) {
            System.out.println("LEVEL 14");
            long start = System.currentTimeMillis();

            String input = "level13.1/";
            String scriptbox = "level14.0/";
            String output = "level14.1/";
            String testoutput = "level14.outref/";

            createFile(scriptbox);
            File dir = createFile(output);

            String ssh = "cp " + localpath + "level7_iter1.1/" + basename + "_1/*_1_* " + localpath + scriptbox + "param_example.txt";
            String cp_bash_shell = "cp.sh";
            runCmd(ssh, scriptbox, cp_bash_shell);

            if (!doesFileExist(localpath + scriptbox + "param_example.txt")) {
                System.out.println("ERROR: The parameter file required for merging filtered result file does not exist: " + localpath + scriptbox + "param_example.txt" + " This file" +
                        " is copied from the first starting point from the first iteration so make sure that file exists.\nExiting now...");
                System.exit(1);
            }

            if (!doesFileExist(localpath + input + "results_" + basename + "_cut_scoreperc" + percent + ".0_exprNaN_0.0.txt")) {
                System.out.println("ERROR: The ApplyCut filtered result file does not exist: " + localpath + input + "results_" + basename + "_cut_scoreperc" + percent + ".0_exprNaN_0.0.txt" + "\nExiting now...");
                System.exit(1);
            }

            /*
             NOTE: bash script is currently hardcoded to start ListMergeMembers job on JBEI cluster on a single node with 24 memory available.
            */
            String sl_lmm = "listmergemembers.sl";
            String listmergeMembers_script = "#!/bin/bash\n#SBATCH --partition=regular\n#SBATCH --account=kbase\n#SBATCH --time=30:00:00\n#SBATCH -N 1 -c 1\nexport WORKDIR=$(pwd)\nexport PATH=$PATH:/usr/common/tig/taskfarmer/1.5/bin\nexport THREADS=1\n";

            listmergeMembers_script += "java -Xmx30G DataMining.ListMergeMembersPreComputed -dir " + localpath + input + "results_" + basename + "_cut_scoreperc" + percent + ".0_exprNaN_0.0.txt" +
                    " -crit " + criterion.toUpperCase() + " -param " + localpath + scriptbox + "param_example.txt" + " -ocut 0.25 -misscut 1.0 -numgene 1000 " +
                    "-complete y -live n -out " + localpath + output + "results_" + basename + "_cut_scoreperc" + percent + ".0_exprNaN_0.0_ocut_0.25_misscut_1.0_reconstructed.txt &>" + localpath + output + "ListMergeMembers_0.25_0.out";

            TextFile.write(listmergeMembers_script, sl_lmm);

            // Run SLURM job and wait for it to finish

            String task_shell_out = localpath + scriptbox + "out.txt";
            String listmergeMembers_file = "run_lmm.sh";
            String run_lmm = "sbatch " + sl_lmm + " &> " + task_shell_out;
            runCmd(run_lmm, localpath, listmergeMembers_file);

            String slurm_id = "";
            FileReader file_to_read = null;
            try {
                file_to_read = new FileReader(task_shell_out);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            BufferedReader bf = new BufferedReader(file_to_read);

            String aLine = null;
            try {
                while ((aLine = bf.readLine()) != null) {
                    if (aLine.toLowerCase().startsWith("submitted")) {
                        slurm_id = aLine.split(" ")[3];
                    } else {
                        System.out.println("ERROR: SBATCH SUBMISSION FOR LISTMERGEMEMBERS JOB FAILED");
                        System.exit(1);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                bf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                checkSLURMJobIsCompleted(slurm_id, scriptbox);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Create starting points for refinement,

            FileReader file_to_read_2 = null;
            try {
                file_to_read_2 = new FileReader(localpath + output + "results_" + basename + "_cut_scoreperc" + percent + ".0_exprNaN_0.0_ocut_0.25_misscut_1.0_reconstructed.txt");
            } catch (FileNotFoundException e) {
                System.out.println("The ListMergeMembers result file does not exits: " + localpath + output + "results_" + basename + "_cut_scoreperc" + percent + ".0_exprNaN_0.0_ocut_0.25_misscut_1.0_reconstructed.txt \n Exiting now ...");
                System.exit(1);
                e.printStackTrace();
            }
            BufferedReader bf_2 = new BufferedReader(file_to_read_2);

            String blockInput = "";
            String aLine2 = null;
            int count = 0;
            try {
                while ((aLine2 = bf_2.readLine()) != null) {
                    if (count != 0) {
                        String[] as = aLine2.split("\t");
                        if (count != 1) {
                            blockInput += '\n' + as[2];
                        }
                        blockInput = as[2];
                    }
                    count++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                bf_2.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            num_str_pt_refine = count - 1;
            String refine_file = output + "refinement_input.txt";
            TextFile.write(blockInput, refine_file);
            refinement_starting_points = localpath + refine_file;
            refinement_prefix = localpath + output + "refinement_input";

            File testdirf = new File(testoutput);
            System.out.println("\"" + output + "\": " + dir.length() + ", " +
                    "\"" + testoutput + "\": " + testdirf.length());
            long end = System.currentTimeMillis();
            System.out.println("LEVEL 14 COMPLETED: " + (end - start));
            String timestamp = getTimeStamp();
            levelTimes[levelIndex] = timestamp;
            levelNames[levelIndex] = "level14";
            levelIndex++;
            printLevelTimeStamps();
            setLevel++;
        }

        // REFINEMENT ROUND
        if (!refine) {
            setLevel = 21; //check if this is correct later
        } else {
            /*  */
            /* level15 create parameter files */
            if (setLevel == 15 && (stopLevel >= 15 || stopLevel == 0)) {
                System.out.println("LEVEL 15");
                long start = System.currentTimeMillis();

                try {
                    criterion_index = StringUtil.getFirstEqualsIndex(MINER_STATIC.CRIT_LABELS, criterion);
                } catch (Exception e) {
                    System.out.println("The criterion combination specified is not valid. Try shuffling the order of the criteria.\n" +
                            "Remember, the feat and MAXTF should always be the last criteria in the combination.");
                    e.printStackTrace();
                }

                String scriptbox = "level15.0/";
                String output = "level15.1/";
                String testoutput = "level15.outref/";
                String subdir_1 = output + "out_files/";
                String subdir_2 = output + "results/";
                String subdir_3 = output + "toplist_files/";

                int[] row_col = new int[0];

                try {
                    row_col = setParameters(filename);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int num_row = row_col[0];
                int num_col = row_col[1];

                /*
                Important parameters for refinement.
                 */
                prm.RUN_SEQUENCE = "N";
                prm.RANDOMFLOOD = true;
                prm.EXCLUDE_LIST_PATH = "";
                prm.OVERRIDE_SHAVING = true;

                /* - */

                prm.CRIT_TYPE_INDEX = criterion_index;
                prm.PRECRIT_TYPE_INDEX = criterion_index;
                prm.OUTPREFIX = basename + "_refine";
                prm.RANDOM_SEED = MINER_STATIC.RANDOM_SEEDS[0];
                prm.OUTDIR = localpath + subdir_2;
                prm.EXPR_DATA_PATH = localpath + basename + ".txt";
                prm.FEAT_DATA_PATH = "";
                prm.INTERACT_DATA_PATH = "";
                prm.MEANINTERACT_PATH = "";
                prm.SDINTERACT_PATH = "";
                prm.R_DATA_PATH = localpath + "/level1.1/" + basename + ".Rdata";
                prm.R_METHODS_PATH = R_path;
                prm.TFTARGETMAP_PATH = "";
                prm.MAXMOVES = new int[]{500, 30};
                prm.IMAX = Imax;
                prm.IMIN = Imin;
                prm.JMAX = Jmax;
                prm.JMIN = Jmin;
                prm.BATCH_PERC = 0.4;
                prm.PLATEAU_PERC = 0.2;
                prm.SIZE_PRECRIT_LIST = 50;
                prm.PBATCH = 1.0;
                prm.DATA_LEN_EXPS = num_col;
                prm.DATA_LEN_GENES = num_row;
                String[] absvectArray = absvect.split(",");
                int[] absvectIntArray = new int[absvectArray.length];
                for (int a = 0; a < absvectArray.length; a++) {
                    absvectIntArray[a] = Integer.parseInt(absvectArray[a]);
                }
                if (useAbs == 1) {
                    prm.USE_ABS = true;
                    prm.USE_ABS_AR = absvectIntArray;
                    prm.FRXN_SIGN = false;
                } else {
                    prm.USE_ABS = false;
                    prm.USE_ABS_AR = absvectIntArray;
                    prm.FRXN_SIGN = true;
                }

                prm.MEANTF_PATH = "";
                prm.SDTF_PATH = "";
                prm.TFTARGETMAP_PATH = "";

                prm.MEANFEAT_PATH = "";
                prm.SDFEAT_PATH = "";
                prm.FEAT_DATA_PATH = "";

                prm.MEANINTERACT_PATH = "";
                prm.SDINTERACT_PATH = "";
                prm.INTERACT_DATA_PATH = "";

                prm.MEANFEM_PATH = "";
                prm.SDFEM_PATH = "";
                prm.MEANFEMC_PATH = "";
                prm.SDFEMC_PATH = "";
                prm.MEANFEMR_PATH = "";
                prm.SDFEMR_PATH = "";

                prm.MEANKEND_PATH = "";
                prm.MEANKENDC_PATH = "";
                prm.MEANKENDR_PATH = "";
                prm.SDKEND_PATH = "";
                prm.SDKENDC_PATH = "";
                prm.SDKENDR_PATH = "";

                prm.MEANBINARY_PATH = "";
                prm.MEANBINARYC_PATH = "";
                prm.MEANBINARYR_PATH = "";
                prm.SDBINARY_PATH = "";
                prm.SDBINARYC_PATH = "";
                prm.SDBINARYR_PATH = "";

                prm.MEANMSE_PATH = "";
                prm.MEANMSEC_PATH = "";
                prm.MEANMSER_PATH = "";
                prm.SDMSER_PATH = "";
                prm.SDMSE_PATH = "";
                prm.SDMSEC_PATH = "";

                prm.MEANMEAN_PATH = "";
                prm.SDMEAN_PATH = "";

                criterion_index = StringUtil.getFirstEqualsIndexIgnoreCase(MINER_STATIC.CRIT_LABELS, criterion);


                // Specify Transcription Factor Criterion Data/Null Locations
                if (TF_file != null) {
                    prm.MEANTF_PATH = localpath + "level5.1/" + nullprefix + "MAXTF_median_full.txt";
                    prm.SDTF_PATH = localpath + "level5.1/" + nullprefix + "MAXTF_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANTF_PATH) || !doesFileExist(prm.SDTF_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the MAXTF criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                    prm.TFTARGETMAP_PATH = TF_file;
                }

                // Specify Features Criterion Data/Null Locations
                if (feat_file != null) {
                    prm.MEANFEAT_PATH = localpath + "level5.1/" + nullprefix + "FEAT_median_full.txt";
                    prm.SDFEAT_PATH = localpath + "level5.1/" + nullprefix + "FEAT_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANFEAT_PATH) || !doesFileExist(prm.SDFEAT_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the feat criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                    prm.FEAT_DATA_PATH = feat_file;
                }

                // Specify Protein-Protein Interaction Criterion Data/Null Locations
                if (inter_file != null) {
                    prm.MEANINTERACT_PATH = localpath + "level5.1/" + nullprefix + "INTER_median_full.txt";
                    prm.SDINTERACT_PATH = localpath + "level5.1/" + nullprefix + "INTER_0.5IQR_full.txt";
                    prm.INTERACT_DATA_PATH = inter_file;
                    if (!doesFileExist(prm.MEANINTERACT_PATH) || !doesFileExist(prm.SDINTERACT_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the inter criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }

                // Specify Expression Criterion
                if (criterion.contains("FEM") && !criterion.contains("FEMC") && !criterion.contains("FEMR")) {
                    prm.MEANFEM_PATH = localpath + "level5.1/" + nullprefix + "FEM_median_full.txt";
                    prm.SDFEM_PATH = localpath + "level5.1/" + nullprefix + "FEM_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANFEM_PATH) || !doesFileExist(prm.SDFEM_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the FEM criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }
                if (criterion.contains("FEMC")) {
                    prm.MEANFEMC_PATH = localpath + "level5.1/" + nullprefix + "FEMC_median_full.txt";
                    prm.SDFEMC_PATH = localpath + "level5.1/" + nullprefix + "FEMC_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANFEMC_PATH) || !doesFileExist(prm.SDFEMC_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the FEMC criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }
                if (criterion.contains("FEMR")) {
                    prm.MEANFEMR_PATH = localpath + "level5.1/" + nullprefix + "FEMR_median_full.txt";
                    prm.SDFEMR_PATH = localpath + "level5.1/" + nullprefix + "FEMR_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANFEMR_PATH) || !doesFileExist(prm.SDFEMR_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the FEMR criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }
                if (criterion.contains("MSE") && !criterion.contains("MSEC") && !criterion.contains("MSER")) {
                    prm.MEANMSE_PATH = localpath + "level5.1/" + nullprefix + "MSE_median_full.txt";
                    prm.SDMSE_PATH = localpath + "level5.1/" + nullprefix + "MSE_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANMSE_PATH) || !doesFileExist(prm.SDMSE_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the MSE criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }
                if (criterion.contains("MSEC")) {
                    prm.MEANMSEC_PATH = localpath + "level5.1/" + nullprefix + "MSEC_median_full.txt";
                    prm.SDMSEC_PATH = localpath + "level5.1/" + nullprefix + "MSEC_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANMSEC_PATH) || !doesFileExist(prm.SDMSEC_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the MSEC criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }
                if (criterion.contains("MSER")) {
                    prm.MEANMSER_PATH = localpath + "level5.1/" + nullprefix + "MSER_median_full.txt";
                    prm.SDMSER_PATH = localpath + "level5.1/" + nullprefix + "MSER_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANMSER_PATH) || !doesFileExist(prm.SDMSER_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the MSER criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }
                if (criterion.contains("KENDALL") && !criterion.contains("KENDALLC") && !criterion.contains("KENDALLR")) {
                    prm.MEANKEND_PATH = localpath + "level5.1/" + nullprefix + "KENDALL_median_full.txt";
                    prm.SDKEND_PATH = localpath + "level5.1/" + nullprefix + "KENDALL_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANKEND_PATH) || !doesFileExist(prm.SDKEND_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the KENDALL criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }
                if (criterion.contains("KENDALLR")) {
                    prm.MEANKENDR_PATH = localpath + "level5.1/" + nullprefix + "KENDALLR_median_full.txt";
                    prm.SDKENDR_PATH = localpath + "level5.1/" + nullprefix + "KENDALLR_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANKENDR_PATH) || !doesFileExist(prm.SDKENDR_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the KENDALLR criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }
                if (criterion.contains("KENDALLC")) {
                    prm.MEANKENDC_PATH = localpath + "level5.1/" + nullprefix + "KENDALLC_median_full.txt";
                    prm.SDKENDC_PATH = localpath + "level5.1/" + nullprefix + "KENDALLC_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANKENDC_PATH) || !doesFileExist(prm.SDKENDC_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the KENDALLC criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }
                if (criterion.contains("BINARY") && !criterion.contains("BINARYC") && !criterion.contains("BINARYR")) {
                    prm.MEANBINARY_PATH = localpath + "level5.1/" + nullprefix + "BINARY_median_full.txt";
                    prm.SDBINARY_PATH = localpath + "level5.1/" + nullprefix + "BINARY_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANBINARY_PATH) || !doesFileExist(prm.SDBINARY_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the BINARY criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }
                if (criterion.contains("BINARYR")) {
                    prm.MEANBINARYR_PATH = localpath + "level5.1/" + nullprefix + "BINARYR_median_full.txt";
                    prm.SDBINARYR_PATH = localpath + "level5.1/" + nullprefix + "BINARYR_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANBINARYR_PATH) || !doesFileExist(prm.SDBINARYR_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the BINARYR criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }
                if (criterion.contains("BINARYC")) {
                    prm.MEANBINARYC_PATH = localpath + "level5.1/" + nullprefix + "BINARYC_median_full.txt";
                    prm.SDBINARYC_PATH = localpath + "level5.1/" + nullprefix + "BINARYC_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANBINARYC_PATH) || !doesFileExist(prm.SDBINARYC_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the BINARYC criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }
                if (criterion.equalsIgnoreCase("MEAN")) {
                    prm.MEANMEAN_PATH = localpath + "level5.1/" + nullprefix + "MEAN_median_full.txt";
                    prm.SDMEAN_PATH = localpath + "level5.1/" + nullprefix + "MEAN_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANMEAN_PATH) || !doesFileExist(prm.SDMEAN_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the MEAN criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }

                prm.write(prm_path);

                createFile(scriptbox);
                File dir = createFile(output);
                createFile(subdir_1);
                createFile(subdir_2);
                createFile(subdir_3);


                if (!doesFileExist(refinement_starting_points)) {
                    System.out.println("ERROR: The refinement starting point biclusters file does not exist: " + refinement_starting_points + "\nExiting now...");
                    System.exit(1);
                }

                String argument[];
                argument = new String[]{"-param", prm_path, "-in", refinement_starting_points, "-out", output + basename,
                        "-samp", "" + 1, "-minsamp", "" + 0, "-top", "y", "-silent",
                        "t", "-crits", criterion, "-precrits", criterion};

                System.out.println("RUNNING: " + MoreArray.toString(argument, ","));

                CreateParamSet.main(argument);

                System.out.println("PARAMETERS CREATED!");

                setLevel += 1;

                File testdirf = new File(testoutput);
                System.out.println("\"" + output + "\": " + dir.length() + ", " +
                        "\"" + testoutput + "\": " + testdirf.length());
                long end = System.currentTimeMillis();
                System.out.println("LEVEL 15 COMPLETED: " + (end - start));
                String timestamp = getTimeStamp();
                levelTimes[levelIndex] = timestamp;
                levelNames[levelIndex] = "level15";
                levelIndex++;
            }

            /* level16 - RunMiner - make task file */
            if (setLevel == 16 && (stopLevel >= 16 || stopLevel == 0)) {
                long start = System.currentTimeMillis();
                String input = "level15.1/" + basename + "_1/";
                String scriptbox = "level16.0/";
                String output = "level16.1/";
                String subdir_1 = output + "out_files/";
                createFile(scriptbox);
                createFile(output);
                createFile(subdir_1);

                String refine_wrapper = "#!/bin/bash\nTMPDIR=$SCRATCH\njava  -Xmx1G DataMining.RunMiner -param " + localpath + input + prm.OUTPREFIX + "_" + refinement_prefix + "_$1__" + criterion.toUpperCase() + "__AG_" + MINER_STATIC.RANDOM_SEEDS[0] + "__parameters.txt " +
                        "&> " + localpath + subdir_1 + prm.OUTPREFIX + "_" + refinement_prefix + "_$1__" + criterion.toUpperCase() + "__AG_" + MINER_STATIC.RANDOM_SEEDS[0] + "_out.txt\n";
                String refine_wrapper_file = "refine_miner.sh";
                TextFile.write(refine_wrapper, scriptbox + refine_wrapper_file);

                String task_file = basename + "_refine.tasks";
                String task_script = "";
                for (int it = 0; it < num_str_pt_refine; it++) {
                    task_script += "bash " + localpath + scriptbox + refine_wrapper_file + " " + it + "\n";
                }
                System.out.println("RunMiner task file written!");

                TextFile.write(task_script, scriptbox + task_file);

                long end = System.currentTimeMillis();
                System.out.println("LEVEL 16 COMPLETED: " + (end - start));
                setLevel++;
                String timestamp = getTimeStamp();
                levelTimes[levelIndex] = timestamp;
                levelNames[levelIndex] = "level16";
                levelIndex++;
            }

            /* level17 - RunMiner - run task file */
            if (setLevel == 17 && (stopLevel >= 17 || stopLevel == 0)) {
                long start = System.currentTimeMillis();
                System.out.println("LEVEL 17");
                String input = "level16.0/"; // where the task file is
                String scriptbox = "level17.0/";
                createFile(scriptbox);


                if (!doesFileExist(localpath + input + basename + "_refine.tasks")) {
                    System.out.println("ERROR: The refinement RunMiner file does not exist: " + localpath + input + basename + "_refine.tasks" + "\nExiting now...");
                    System.exit(1);
                }

                // NOTE: Walltime for refinement is set to the max walltime allowed on Cori and number of nodes is calculated so as the jobs are fully parallelized across nodes.
                String refine_walltime = "36:00:00";
                int num_refine_nodes = (num_str_pt_refine / 32) + 1;
                try {
                    run_tf(localpath + input + basename + "_refine.tasks", scriptbox, refine_walltime, num_refine_nodes);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                long end = System.currentTimeMillis();
                System.out.println("LEVEL 17 COMPLETED: " + (end - start));
                setLevel += 1;
                String timestamp = getTimeStamp();
                levelTimes[levelIndex] = timestamp;
                levelNames[levelIndex] = "level17";
                levelIndex++;
            }

            /* level18 - Extract Results from Outfiles */
            if (setLevel == 18 && (stopLevel >= 18 || stopLevel == 0)) {
                getTimeStamp();
                System.out.println("LEVEL 18");
                long start = System.currentTimeMillis();

                String scriptbox = "level18.0/";
                String output = "level18.1/";
                String ex_out = scriptbox + "example_param/";
                String testoutput = "level18.outref/";

                String param_dir = localpath + "level15.1/" + basename + "_1/";
                String outfile_dir = localpath + "level16.1/out_files/";

                createFile(scriptbox);
                createFile(output);
                createFile(ex_out);

                String mv = "";
                mv += "mv " + localpath + "level15.1/results/*toplist.txt " + localpath + "level15.1/toplist_files/\n";
                String mv_shell = "ssh.sh";
                runCmd(mv, scriptbox, mv_shell);

                String cp_script = "cp " + param_dir + "*_0_* " + ex_out;
                String cp_shell = "cp.sh";
                runCmd(cp_script, scriptbox, cp_shell);

                String result_vbl = "results_" + basename + ".vbl";
                String tmp_vbl = localpath + output + "tmp.vbl";
                // E.g. yeast_cmonkey_refine_refinement_input_315__MSEC_KENDALLC_FEMC__AG_759820_out.txt
                String name_prefix = basename + "_refine_refinement_input_";
                String name_suffix = "__" + criterion + "__AG_" + MINER_STATIC.RANDOM_SEEDS[0] + "_out.txt";

                String cmd = "java -Xmx5G DataMining.CollectRefinementResults -indir " + outfile_dir + " -name_prefix " + name_prefix + " -name_suffix " + name_suffix + " -outfile " + tmp_vbl + " &> " + localpath + scriptbox + "collect_partial.out";
                String run_collect_partial = "collect_partial.sh";
                runCmd(cmd, scriptbox, run_collect_partial);

                String result = "";
                String[] header = new String[]{"#number", "block_area", "block_id", "move_type", "pre_criterion", "full_crit", "expr_mean_crit", "expr_mean_crit", "expr_reg_crit", "expr_kend_crit", "expr_cor_crit", "expr_euc_crit", "PPI_crit", "feat_crit", "TF_crit", "percent_orig_genes", "percent_orig_exp", "exp_mean", "trajectory_position", "FEATURE_INDICES", "move_class", "num_genes", "num_exps"};
                result += MoreArray.toString(header, "\t") + "\n";

                FileReader file_to_read = null;
                try {
                    file_to_read = new FileReader(tmp_vbl);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                BufferedReader bf = new BufferedReader(file_to_read);

                String aLine = null;
                int result_counter = 1;
                try {
                    while ((aLine = bf.readLine()) != null) {
                        String[] as = aLine.split("\t");
                        System.out.println(aLine);
                        System.out.println(MoreArray.toString(Arrays.copyOfRange(as, 1, as.length)));
                        result += "" + result_counter + "\t" + MoreArray.toString(Arrays.copyOfRange(as, 1, as.length), "\t") + "\n"; // Check that this is working as should
                        result_counter++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    bf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                TextFile.write(result, output + result_vbl);

                File testdir = new File(testoutput);
                System.out.println("\"" + testdir + "\": " + testdir.length() + ", " +
                        "\"" + testoutput + "\": " + testdir.length());
                long end = System.currentTimeMillis();
                System.out.println("LEVEL 18 COMPLETED: " + (end - start));
                setLevel++;
                String timestamp = getTimeStamp();
                levelTimes[levelIndex] = timestamp;
                levelNames[levelIndex] = "level18";
                levelIndex++;
            }

            /* level19 ApplyCut */
            if (setLevel == 19 && (stopLevel >= 19 || stopLevel == 0)) {
                System.out.println("LEVEL 19");
                long start = System.currentTimeMillis();

                String input = "level18.1/";
                String scriptbox = "level19.0/";
                String output = "level19.1/";
                String testoutput = "level19.outref/";

                createFile(scriptbox);
                File dir = createFile(output);

                String input_file = "results_" + basename + ".vbl";

                if (!doesFileExist(localpath + input + input_file)) {
                    System.out.println("ERROR: The ListfromDir refinement result file does not exist: " + localpath + input + input_file + "\nExiting now...");
                    System.exit(1);
                }

                percent = 66;
                String number = "NA";
                System.out.println(localpath + input + input_file);
                String argument[] = new String[]{localpath + input + input_file, percent + "%", number};
                DataMining.util.ApplyCut.main(argument);

                String move = "mv results_" + basename + "_cut_scoreperc* " + output;
                String move_file = "Move";
                runCmd(move, scriptbox, move_file);

                if (!new File(localpath + output + "results_" + basename + "_cut_scoreperc" + percent + ".0_exprNaN_0.0.txt").exists()) {
                    System.out.println("Expected file not seen. Exiting.");
                    System.exit(0);
                }

                File testdirf = new File(testoutput);
                System.out.println("\"" + output + "\": " + dir.length() + ", " +
                        "\"" + testoutput + "\": " + testdirf.length());
                long end = System.currentTimeMillis();
                System.out.println("LEVEL 19 COMPLETED: " + (end - start));
                setLevel++;
                String timestamp = getTimeStamp();
                levelTimes[levelIndex] = timestamp;
                levelNames[levelIndex] = "level19";
                levelIndex++;
            }

            /* level20 ListMergeMembers final */
            if (setLevel == 20 && (stopLevel >= 20 || stopLevel == 0)) {
                System.out.println("LEVEL 20");
                long start = System.currentTimeMillis();

                String input = "level19.1/";
                String scriptbox = "level20.0/";
                String output = "level20.1/";
                String testoutput = "level20.outref/";

                createFile(scriptbox);
                File dir = createFile(output);

                String ssh = "";
                ssh += "cp " + localpath + "level15.1/" + basename + "_1/*_0_* " + localpath + scriptbox + "param_example.txt";
                String cp_bash_shell = "cp.sh";
                runCmd(ssh, scriptbox, cp_bash_shell);

                if (!doesFileExist(localpath + scriptbox + "param_example.txt")) {
                    System.out.println("ERROR: The example parameter filerequired for merging does not exist: " + localpath + scriptbox + "param_example.txt" + "\nExiting now...");
                    System.exit(1);
                }

                if (!doesFileExist(localpath + input + "results_" + basename + "_cut_scoreperc" + percent + ".0_exprNaN_0.0.txt")) {
                    System.out.println("ERROR: The ApplyCut result file does not exist: " + localpath + input + "results_" + basename + "_cut_scoreperc" + percent + ".0_exprNaN_0.0.txt" + "\nExiting now...");
                    System.exit(1);
                }

                String sl_lmm = "listmergemembers.sl";
                /*
                NOTE: Running LMM is hardcoded as a SLURM script.
                 */
                String listmergeMembers_script = "#!/bin/bash\n#SBATCH --partition=regular\n#SBATCH --account=kbase\n#SBATCH --time=30:00:00\n#SBATCH -N 1 -c 1\nexport WORKDIR=$(pwd)\nexport PATH=$PATH:/usr/common/tig/taskfarmer/1.5/bin\nexport THREADS=1\n";

                listmergeMembers_script += "java -Xmx22G DataMining.ListMergeMembersPreComputed -dir " + localpath + input + "results_" + basename + "_cut_scoreperc" + percent + ".0_exprNaN_0.0.txt" +
                        " -crit " + criterion + " -param " + localpath + scriptbox + "param_example.txt" + " -ocut 0.25 -misscut 1.0 -numgene 1000 " +
                        "-complete y -live n -out " + localpath + output + "results_" + basename + "_cut_scoreperc" + percent + ".0_exprNaN_0.0_ocut_0.25_misscut_1.0_reconstructed.txt &>" + localpath + output + "ListMergeMembers_0.25_0.out";
                TextFile.write(listmergeMembers_script, sl_lmm);

                String task_shell_out = localpath + scriptbox + "out.txt";
                String listmergeMembers_file = "run_lmm.sh";
                String run_lmm = "sbatch " + sl_lmm + " &> " + task_shell_out;
                runCmd(run_lmm, localpath, listmergeMembers_file);

                // RUN SLURM Job and Wait for it to complete.

                String slurm_id = "";
                FileReader file_to_read = null;
                try {
                    file_to_read = new FileReader(task_shell_out);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                BufferedReader bf = new BufferedReader(file_to_read);

                String aLine = null;
                try {
                    while ((aLine = bf.readLine()) != null) {
                        if (aLine.toLowerCase().startsWith("submitted")) {
                            slurm_id = aLine.split(" ")[3];
                        } else {
                            System.out.println("ERROR: SBATCH LISTMERGEMEMBERS SUBMISSION FAILED");
                            System.exit(1);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    bf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    checkSLURMJobIsCompleted(slurm_id, scriptbox);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                File testdirf = new File(testoutput);
                System.out.println("\"" + output + "\": " + dir.length() + ", " +
                        "\"" + testoutput + "\": " + testdirf.length());
                long end = System.currentTimeMillis();
                System.out.println("LEVEL 20 COMPLETED: " + (end - start));
                String timestamp = getTimeStamp();
                levelTimes[levelIndex] = timestamp;
                levelNames[levelIndex] = "level20";
                levelIndex++;
                printLevelTimeStamps();
                setLevel++;
            }
        }
        System.out.println("MAKflow finished, exiting.");
        System.exit(0);
    }

    /**
     * @param args
     */
    private void init(String[] args) {
        uniqueID = getTimeStamp().replace('/', '_').replace(':', '_').replace(' ', '_');
        MoreArray.printArray(args);
        options = MapArgOptions.maptoMap(args, valid_args);
        String error_msg = "syntax: java DataMining.Old_Versions_of_MAKflow.MAKflow\n" + arg_desc_str;

        if (options.get("-parameters") != null) {
            try {
                String parameters_file = (String) options.get("-parameters");
                if (!doesFileExist(parameters_file)) {
                    System.out.println("ERROR: The path provided to the parameter file is invalid and no such file exists.\nExiting now ...");
                    System.exit(1);
                }
                FileReader file_to_read = new FileReader(parameters_file);
                BufferedReader bf = new BufferedReader(file_to_read);

                String aLine = null;
                int count = 0;
                while ((aLine = bf.readLine()) != null) {
                    if (!aLine.contains(" = ")) {
                        System.out.println("ERROR: In the parameter file, parameter names should be separated by the \"=\" character with their corresponding values.\nExiting now...");
                        System.exit(1);
                    }
                    if (!aLine.startsWith("#")) {
                        String[] asplit = aLine.split("=");
                        String param_key = asplit[0].trim();
                        String param_val = asplit[1].trim();
                        System.out.println(param_key);
                        System.out.println(param_val);
                        if (param_key.equalsIgnoreCase("R")) {
                            if (!doesFileExist(param_val)) {
                                System.out.println("ERROR: The path provided to the Miner.R file is invalid and no such file exists.\nExiting now ...");
                                System.exit(1);
                            }
                            R_path = param_val;
                        } else if (param_key.equalsIgnoreCase("start")) {
                            try {
                                startlevel = Double.parseDouble(param_val);
                            } catch (Exception e) {
                                System.out.println("Start level is not a number. Exiting now ...");
                                System.exit(1);
                                e.printStackTrace();
                            }
                        } else if (param_key.equalsIgnoreCase("stop")) {
                            try {
                                stoplevel = Double.parseDouble(param_val);
                            } catch (Exception e) {
                                System.out.println("Start level is not a number. Exiting now ...");
                                System.exit(1);
                                e.printStackTrace();
                            }
                        } else {
                            if (param_key.equalsIgnoreCase("data")) {
                                try {
                                    String f = param_val;

                                    if (!doesFileExist(param_val)) {
                                        System.out.println("ERROR: The path provided to the expression data matrix file is invalid and no such file exists.\nExiting now ...");
                                        System.exit(1);
                                    }

                                    basepath = "./";
                                    final int inde = f.lastIndexOf("/");
                                    if (inde != -1) {
                                        basepath = f.substring(0, inde);
                                    }
                                    filename = f;
                                    basename = f.substring(inde + 1, f.lastIndexOf("."));

                                    //
                                    geneids_file = basename + "_geneids.txt";
                                    expids_file = basename + "_expids.txt";
                                    Rdata_file = basename + ".Rdata";

                                    try {
                                        // Load data matrix as SimpleMatrix in Java

                                        sm = new SimpleMatrix(f);

                                        // Check for Duplicates in Row Data and Labels
                                        // O(n) where n is number of rows and m is number of columns

                                        ArrayList<double[]> uniqueRows = new ArrayList();
                                        ArrayList<String> uniqueRowsLabels = new ArrayList();
                                        int duplicateRows = 0;
                                        int duplicateRowsLabels = 0;
                                        for (int itI = 0; itI < sm.ylabels.length; itI++) {
                                            double[] row = sm.data[itI];
                                            String rowLabel = sm.ylabels[itI];
                                            if (uniqueRows.contains(row)) {
                                                duplicateRows += 1;
                                            }
                                            if (uniqueRowsLabels.contains(rowLabel)) {
                                                duplicateRowsLabels += 1;
                                            }
                                            uniqueRows.add(row);
                                            uniqueRowsLabels.add(rowLabel);
                                        }

                                        // Check for Duplicates in Row Data and Labels
                                        // O(m*n) where n is number of rows and m is number of columns

                                        System.out.println(sm.xlabels.length + "\t" + sm.ylabels.length);
                                        System.out.println(sm.ylabels);
                                        ArrayList<double[]> uniqueCols = new ArrayList();
                                        ArrayList<String> uniqueColsLabels = new ArrayList();
                                        int duplicateCols = 0;
                                        int duplicateColsLabels = 0;
                                        for (int itJ = 0; itJ < sm.xlabels.length; itJ++) {
                                            String colLabel = sm.xlabels[itJ];
                                            double[] col = new double[sm.ylabels.length];
                                            for (int itI = 0; itI < sm.ylabels.length; itI++) {
                                                col[itI] = sm.data[itI][itJ];
                                            }
                                            if (uniqueCols.contains(col)) {
                                                duplicateCols += 1;
                                            }
                                            if (uniqueColsLabels.contains(colLabel)) {
                                                duplicateColsLabels += 1;
                                            }
                                            uniqueRows.add(col);
                                            uniqueRowsLabels.add(colLabel);
                                        }

                                        if (duplicateRowsLabels > 0) {
                                            System.out.println("ERROR: Duplicate row labels in provided data matrix.\nNow exiting...");
                                            System.exit(1);
                                        }
                                        if (duplicateColsLabels > 0) {
                                            System.out.println("ERROR: Duplicate column labels in provided data matrix.\nNow exiting...");
                                            System.exit(1);
                                        }
                                        if (duplicateRows > 0) {
                                            System.out.println("WARNING: Duplicate row data in provided data matrix.");
                                        }
                                        if (duplicateCols > 0) {
                                            System.out.println("WARNING: Duplicate column data in provided data matrix.");
                                        }


                                    } catch (Exception e) {
                                        System.out.println("No file found" + f + " or the file is not in the accepted format.\nPlease refer to the example input file we have provided.\nExiting now ...");
                                        e.printStackTrace();
                                        System.exit(1);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (param_key.equalsIgnoreCase("crit")) {
                                try {
                                    String tmp_criterion = param_val;
                                    criterion_index = StringUtil.getFirstEqualsIndex(MINER_STATIC.CRIT_LABELS, tmp_criterion);
                                    num_criterion = tmp_criterion.split("_").length;
                                    criterion = "";
                                    ArrayList<String> possible_crits = new ArrayList<String>();
                                    ArrayList<String> possible_crit_combos = new ArrayList<String>();
                                    for (int i = 0; i < MINER_STATIC.CRIT_LABELS.length; i++) {
                                        String[] cs = MINER_STATIC.CRIT_LABELS[i].split("_");
                                        possible_crit_combos.add(MINER_STATIC.CRIT_LABELS[i].toUpperCase());
                                        for (int j = 0; j < cs.length; j++) {
                                            possible_crits.add(cs[j].toUpperCase());
                                        }
                                    }

                                    String[] criterion_split = tmp_criterion.split("_");
                                    String crit = "";
                                    ArrayList<String> accepted_crits = new ArrayList<String>();
                                    for (int it = 0; it < criterion_split.length; it++) {
                                        crit = criterion_split[it];
                                        if (!possible_crits.contains(crit.toUpperCase())) {
                                            System.out.println(crit);
                                            System.out.println("ERROR: Unsupported criterion specified!\nExiting now ...");
                                            System.exit(1);
                                        }
                                        if (!crit.equalsIgnoreCase("")) {
                                            accepted_crits.add(crit.toUpperCase());
                                        }
                                    }
                                    List<List<String>> crit_permutations = generatePerm(accepted_crits);
                                    for (List<String> crit_permutation : crit_permutations) {
                                        ArrayList<String> crit_permutation_ls = (ArrayList<String>) crit_permutation;
                                        String crit_permutation_string = StringUtil.join("_", crit_permutation_ls);
                                        crit_permutation_string = crit_permutation_string.substring(0, crit_permutation_string.length() - 1);
                                        System.out.println(crit_permutation_string);
                                        if (possible_crit_combos.contains(crit_permutation_string)) {
                                            criterion = crit_permutation_string;
                                        }
                                    }
                                    if (criterion.equalsIgnoreCase("")) {
                                        System.out.println("ERROR: Criterion is invalid. Please check the input.\nExiting now ...");
                                        System.exit(1);
                                    }
                                    System.out.println(criterion);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (param_key.equalsIgnoreCase("iter")) {
                                try {
                                    iter = Integer.parseInt(param_val);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (param_key.equalsIgnoreCase("startiter")) {
                                try {
                                    if (Integer.parseInt(param_val) > 1) {
                                        startiter = Integer.parseInt(param_val);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (param_key.equalsIgnoreCase("refine")) {
                                try {
                                    refine = StringUtil.isTrueorYes(param_val);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (param_key.equalsIgnoreCase("localpath")) {
                                try {
                                    localpath = param_val; // WILL APPEND CLUSTERWORKSPACE TO THE END LATER ON
                                    String[] path_split = localpath.split("/");
                                    if (!(localpath.endsWith("/"))) {
                                        localpath += "/";
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (param_key.equalsIgnoreCase("abs")) {
                                try {
                                    absvect = param_val;
                                    if (absvect.contains("1")) {
                                        useAbs = 1;
                                    } else {
                                        useAbs = 0;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (param_key.equalsIgnoreCase("imin")) {
                                try {
                                    Imin = Integer.parseInt(param_val);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (param_key.equalsIgnoreCase("jmin")) {
                                try {
                                    Jmin = Integer.parseInt(param_val);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (param_key.equalsIgnoreCase("imax")) {
                                try {
                                    Imax = Integer.parseInt(param_val);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (param_key.equalsIgnoreCase("jmax")) {
                                try {
                                    Jmax = Integer.parseInt(param_val);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (param_key.equalsIgnoreCase("iminstarts")) {
                                try {
                                    Imin_start = Integer.parseInt(param_val);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (param_key.equalsIgnoreCase("jminstarts")) {
                                try {
                                    Jmin_start = Integer.parseInt(param_val);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (param_key.equalsIgnoreCase("imaxstarts")) {
                                try {
                                    Imax_start = Integer.parseInt(param_val);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (param_key.equalsIgnoreCase("jmaxstarts")) {
                                try {
                                    Jmax_start = Integer.parseInt(param_val);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (param_key.equalsIgnoreCase("nsamp")) {
                                try {
                                    nsamp = Integer.parseInt(param_val);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (param_key.equalsIgnoreCase("maxnulljobs")) {
                                try {
                                    maxnulljobs = Integer.parseInt(param_val);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (param_key.equalsIgnoreCase("pseudo")) {
                                try {
                                    usePseudo = StringUtil.isTrueorYes(param_val);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (param_key.equalsIgnoreCase("log")) {
                                try {
                                    useLog = StringUtil.isTrueorYes(param_val);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (param_key.equalsIgnoreCase("exclude")) {
                                try {
                                    exclude = StringUtil.isTrueorYes(param_val);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (param_key.equalsIgnoreCase("num_null_nodes")) {
                                try {
                                    num_null_nodes = Integer.parseInt(param_val);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (param_key.equalsIgnoreCase("num_miner_nodes")) {
                                try {
                                    num_miner_nodes = Integer.parseInt(param_val);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (param_key.equalsIgnoreCase("num_rerun_miner_nodes")) {
                                try {
                                    num_rerun_miner_nodes = Integer.parseInt(param_val);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (param_key.equalsIgnoreCase("null_walltime")) {
                                try {
                                    null_walltime = param_val;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (param_key.equalsIgnoreCase("miner_walltime")) {
                                try {
                                    miner_walltime = param_val;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (param_key.equalsIgnoreCase("rerun_miner_walltime")) {
                                try {
                                    rerun_miner_walltime = param_val;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (param_key.equalsIgnoreCase("runminer_param")) {
                                try {
                                    final String f = param_val;
                                    prm = new Parameters(f, true);
                                    prm_path = f;
                                } catch (Exception e) {
                                    System.out.println("ERROR: Path to runminer template parameter file: " + param_val + " is broken. Exiting now ...");
                                    System.exit(1);
                                    e.printStackTrace();
                                }
                            } else if (param_key.equalsIgnoreCase("feat")) {
                                try {
                                    if (!doesFileExist(param_val)) {
                                        System.out.println("ERROR: The path provided to the feat data matrix file is invalid and no such file exists.\nExiting now ...");
                                        System.exit(1);
                                    }
                                    feat_file = param_val;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (param_key.equalsIgnoreCase("TF")) {
                                try {
                                    if (!doesFileExist(param_val)) {
                                        System.out.println("ERROR: The path provided to the TF data matrix file is invalid and no such file exists.\nExiting now ...");
                                        System.exit(1);
                                    }
                                    TF_file = param_val;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (param_key.equalsIgnoreCase("inter")) {
                                try {
                                    if (!doesFileExist(param_val)) {
                                        System.out.println("ERROR: The path provided to the inter data matrix file is invalid and no such file exists.\nExiting now ...");
                                        System.exit(1);
                                    }
                                    inter_file = param_val;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (param_key.equalsIgnoreCase("workspace")) {
                                try {
                                    clusterWorkspace = param_val;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        count++;
                    }
                }
                bf.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // ENSURE THE NULL BOUNDS SPECIFIED ARE VALID AND ARE NOT LARGER THAN THE BOUNDS OF THE DATA MATRIX.

        int[] row_col = new int[0];

        try {
            if (filename != null) {
                row_col = setParameters(filename);
            } else {
                System.out.println("ERROR: No valid parameter file provided.");
                System.exit(1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        int num_row = row_col[0];
        int num_col = row_col[1];
        if (num_row < Imax || num_col < Jmax) {
            System.out.println("WARNING: IMAX and JMAX were changed from the specified values.");
            Imax = num_row;
            Jmax = num_col;
        }

        // TAKE CARE OF NAMING OF NULL FILES, STARTING POINTS FILE.

        nullprefix = basename + "_nulls_abs" + StringUtil.replace(absvect, ",", "") + "_g" + Imin + "_" + Imax + "_e" + Jmin + "_" + Jmax + "_n" + nsamp + "_";
        startfileprefix = basename + "_STARTS_abs" + useAbs + "_I" + Imin_start + "_" + Imax_start + "_J" + Jmin_start + "_" + Jmax_start + "_RC";

        // Set local directory path and specify path for RunMiner parameter file example.

        localpath += clusterWorkspace + '/';
        if (!doesFileExist(localpath)) {
            System.out.println("ERROR: The localpath/workspace you specified does not exist. MAK will NOT automatically make these directories.\nExiting now ...");
            System.exit(1);
        }


        // Ensure that if TF, feat, or interaction files are provided in the parameter file, that they are also specified in the criteria and visa versa.

        String lev1_outdir = "level1.1/";

        if (criterion.indexOf("MAXTF") != -1) {
            if (TF_file == null) {
                System.out.println("TF based criterion specified, but no TF rank file provided! Now exiting");
                System.exit(1);
            } else {
                R_TF_data = localpath + lev1_outdir + "TF.R";
            }
        }

        if (criterion.indexOf("FEAT") != -1) {
            if (feat_file == null) {
                System.out.println("feat based criterion specified, but no feat input file provided! Now exiting");
                System.exit(1);
            } else {
                R_feat_data = localpath + lev1_outdir + "feat.R";
            }
        }

        if (criterion.indexOf("INTER") != -1) {
            if (inter_file == null) {
                System.out.println("inter based criterion specified, but no inter input file provided! Now exiting");
                System.exit(1);
            } else {
                R_inter_data = localpath + lev1_outdir + "inter.R";
            }
        }

        if (TF_file != null) {
            if (criterion.indexOf("MAXTF") == -1) {
                System.out.println("TF rank file provided, but TF criterion not specified! Now exiting");
                System.exit(1);
            }
        }

        if (feat_file != null) {
            if (criterion.indexOf("FEAT") == -1) {
                System.out.println("feat input file provided, but feat criterion not specified! Now exiting");
                System.exit(1);
            }
        }

        if (inter_file != null) {
            if (criterion.indexOf("FEAT") == -1) {
                System.out.println("inter input file provided, but inter criterion not specified! Now exiting");
                System.exit(1);
            }
        }

        // Ensure that starting point file and the number of starting points is correctly set.

        if (startlevel > 6) {
            startoutfile = localpath + "level6.1/" + basename + "_STARTS_abs" + useAbs + "_I" + Imin_start + "_" + Imax_start + "_J" + Jmin_start + "_" + Jmax_start + "_RC.txt";
            startfileprefix = basename + "_STARTS_abs" + useAbs + "_I" + Imin_start + "_" + Imax_start + "_J" + Jmin_start + "_" + Jmax_start + "_RC";

            String wcl = "";
            wcl += "wc -l " + startoutfile + " &> " + localpath + "level6.0/" + "wcl.txt";
            System.out.println(wcl);
            String wcl_file = "wcl.sh";

            if (!doesFileExist("level6.0/")) {
                System.out.println("ERROR: The directory Level6.0 was never build but the pipeline is being run from a level past level 6. Please manually build this level 6 in the workspace directory and call it \"level6.0/\".\nExiting now ...");
                System.exit(1);
            }

            runCmd(wcl, "level6.0/", wcl_file);

            // check whether startfile has correct name and path
            try {
                String[] fileList = returnNulls(localpath + "level6.0/" + "wcl.txt", 1);
                this.num_start_points = Integer.parseInt((fileList[0].split("\\s+"))[0]);
                System.out.println("NUMBER OF START POINTS: " + num_start_points);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Ensure that refinement starting point file and the number of starting points is correctly set.

        if (startlevel >= 15 && stoplevel < 21 && refine) {
            num_str_pt_refine = 0;
            String lmm_results = "level14.1/";

            if (!doesFileExist(localpath + lmm_results + "refinement_input.txt")) {
                System.out.println("ERROR: The path provided to the refinement starting points file is invalid and no such file exists.\nExiting now ...");
                System.exit(1);
            }

            FileReader file_to_read = null;
            try {
                file_to_read = new FileReader(localpath + lmm_results + "refinement_input.txt");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            BufferedReader bf = new BufferedReader(file_to_read);

            String aLine = null;
            int count = 0;
            try {
                while ((aLine = bf.readLine()) != null) {
                    count++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            num_str_pt_refine = count;
            refinement_starting_points = localpath + lmm_results + "refinement_input.txt";
            refinement_prefix = "refinement_input";
        }
    }

    /**
     * @param levelnumber
     */
    private static void printLevelLabels(int levelnumber) {
        System.out.println(labels[levelnumber]);
    }

    /**
     * @param cmd
     * @param scriptbox
     * @param scriptname
     */
    private void runCmd(String cmd, String scriptbox, String scriptname) {

        TextFile.write(cmd, scriptbox + scriptname);
        Process p = null;

        try {
            p = Runtime.getRuntime().exec("bash " + scriptbox + scriptname + " &> " + scriptbox + scriptname + ".out");
            p.waitFor();
        } catch (Exception ex) {
            if (p != null) p.destroy();
            System.out.println(ex);
        }
    }

    /**
     * @param original
     * @return
     */
    public List<List<String>> generatePerm(ArrayList<String> original) {
        if (original.size() == 0) {
            List<List<String>> result = new ArrayList<List<String>>();
            result.add(new ArrayList<String>());
            return result;
        }
        String firstElement = original.remove(0);
        List<List<String>> returnValue = new ArrayList<List<String>>();
        List<List<String>> permutations = generatePerm(original);
        for (List<String> smallerPermutated : permutations) {
            for (int index = 0; index <= smallerPermutated.size(); index++) {
                List<String> temp = new ArrayList<String>(smallerPermutated);
                temp.add(index, firstElement);
                returnValue.add(temp);
            }
        }
        return returnValue;
    }

    /**
     * @param cmd
     */
    private void runCmd(String cmd) {

        Process p = null;

        try {
            p = Runtime.getRuntime().exec(cmd);
            p.waitFor();

        } catch (Exception ex) {
            if (p != null) p.destroy();
            System.out.println(ex);
        }
    }

    /**
     * @param filesPath
     * @return
     * @throws IOException
     */
    int readLines(String filesPath) throws IOException {
        FileReader file_to_read = new FileReader(filesPath);
        BufferedReader bf = new BufferedReader(file_to_read);

        String aLine;
        int numberOfLines = 0;

        while ((aLine = bf.readLine()) != null) {
            numberOfLines++;
        }
        bf.close();
        return numberOfLines;
    }

    /**
     * @param filesPath
     * @return
     * @throws IOException
     */
    int[] setParameters(String filesPath) throws IOException {
        System.out.println(filesPath);
        if (!doesFileExist(filesPath)) {
            System.out.println("ERROR: No such file: " + filesPath);
            System.exit(1);
        }

        FileReader file_to_read = new FileReader(filesPath);
        BufferedReader bf = new BufferedReader(file_to_read);

        String aLine;
        int numberOfLines = 0;

        int num_col = 0;
        while ((aLine = bf.readLine()) != null) {
            numberOfLines++;
            num_col = aLine.split("\t").length;
        }
        bf.close();
        int[] row_col = new int[2];
        row_col[0] = numberOfLines - 1;
        row_col[1] = num_col - 1;
        return row_col;
    }

    /**
     * @param filePath
     * @param fileLength
     * @return
     * @throws IOException
     */
    String[] returnNulls(String filePath, int fileLength) throws IOException {
        FileReader fr = new FileReader(filePath);
        BufferedReader textReader = new BufferedReader(fr);

        int numberOfLines = fileLength;
        String[] textData = new String[numberOfLines];

        int i;

        for (i = 0; i < numberOfLines; i++) {
            textData[i] = textReader.readLine();
        }
        textReader.close();
        return textData;
    }

    /**
     * @param taskfile
     * @param scriptbox
     * @param walltime
     * @param ntasks
     * @throws IOException
     */
    private void run_tf(String taskfile, String scriptbox, String walltime, int ntasks) throws IOException {
        // 1 is appended to nodes requested with TF because 1 node is required for TF itself to run on.
        ntasks += 1;

        String sl_header = "";
        sl_header += "#!/bin/bash\n" +
                "#SBATCH --partition=regular\n" +
                "#SBATCH --account=kbase\n" +
                "#SBATCH --time=" + walltime + "\n" +
                "#SBATCH  -N " + ntasks + " -c 32\n" +
                "export WORKDIR=$(pwd)\n" +
                "export PATH=$PATH:/usr/common/tig/taskfarmer/1.5/bin\n" +
                "export THREADS=32\n";

        String loc_dir_1 = localpath + scriptbox + "/sl_script/";
        createFile(loc_dir_1);

        String node_task = sl_header + "runcommands.sh " + taskfile;

        String node_task_shell = localpath + "slurm_script.sl";
        String task_shell_out = localpath + "slurm_script.out";
        TextFile.write(node_task, node_task_shell);

        String run_sbatch = "";
        run_sbatch += "sbatch " + node_task_shell + " > " + task_shell_out;
        String sbatch_shell = "execute_sbatch.sh";
        runCmd(run_sbatch, localpath, sbatch_shell); // Automatic execution of SLURM batch script

        String slurm_id = "";
        FileReader file_to_read = new FileReader(task_shell_out);
        BufferedReader bf = new BufferedReader(file_to_read);

        String aLine = null;
        while ((aLine = bf.readLine()) != null) {
            if (aLine.toLowerCase().startsWith("submitted")) {
                slurm_id = aLine.split(" ")[3];
            } else {
                System.out.println("ERROR: SBATCH SUBMISSION FAILED");
                System.exit(1);
            }
        }
        bf.close();

        checkSLURMJobIsCompleted(slurm_id, scriptbox);
    }

    /**
     * @param slurm_id
     * @param scriptbox
     * @throws IOException
     */
    private void checkSLURMJobIsCompleted(String slurm_id, String scriptbox) throws IOException {
        String squeue_file = scriptbox + "squeue.txt";
        long lastTime = System.currentTimeMillis();
        boolean jobIsRunning = true;
        boolean priorCheckReport = true;
        while (jobIsRunning || priorCheckReport) {
            if (System.currentTimeMillis() - lastTime >= period) {
                lastTime = System.currentTimeMillis();
                String run_squeue = "";
                run_squeue += "squeue | grep 'regular' | awk '{print $1}' > " + squeue_file;
                String squeue_shell = "execute_sbatch.sh";
                runCmd(run_squeue, scriptbox, squeue_shell);

                boolean tmp_flag = false;

                FileReader file_to_read = new FileReader(squeue_file);
                BufferedReader bf = new BufferedReader(file_to_read);
                String aLine = null;

                while ((aLine = bf.readLine()) != null) {
                    if (aLine.trim().equalsIgnoreCase(slurm_id)) {
                        tmp_flag = true;
                    }
                }
                bf.close();
                System.out.println(priorCheckReport + "\t" + jobIsRunning);
                System.out.println(lastTime);
                priorCheckReport = jobIsRunning;
                jobIsRunning = tmp_flag;
            }
        }
    }

    boolean doesFileExist(String file) {
        File varTmpDir = new File(file);
        boolean exists = varTmpDir.exists();
        return exists;
    }

    boolean isDirectoryEmpty(String dir) {
        int num_contents = new File(dir).listFiles().length;
        boolean directory_empty = true;
        if (num_contents > 0) {
            directory_empty = false;
        }
        return directory_empty;
    }

    /**
     * @return
     */
    String getTimeStamp() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date)); //2014/08/06 15:59:48
        return dateFormat.format(date);
    }

    /**
     *
     */
    private void printLevelTimeStamps() {
        String printlist = "";
        for (int i = 0; i < levelNames.length; i++) {
            if (levelNames[i] != null && levelTimes[i] != null) {
                printlist += levelNames[i] + '\t' + levelTimes[i] + '\n';
            }
        }
        String timestampfile = localpath + "timestamp_info.txt";
        TextFile.write(printlist, timestampfile);
    }

    /**
     * @param path
     * @return
     */
    private File createFile(String path) {
        File dir = new File(path);
        boolean testdir = dir.mkdir();
        //if (!testdir) {
        //   System.out.println("mkdir failed " + path);
        //   System.exit(0);
        //}
        return dir;
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length > 1) {
            for (int levelnumber = 0; levelnumber < 20; levelnumber++) {
                printLevelLabels(levelnumber);
            }
            MAKflow_Cori_SLURM_v3 arg = new MAKflow_Cori_SLURM_v3(args);
        } else {
            System.out.println("syntax: java DataMining.MAKflow_Cori_SLURM_v3\n" + arg_desc_str
            );
        }
    }
}