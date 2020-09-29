package DataMining;

import DataMining.util.ApplyCut;
import DataMining.util.ListfromDir;
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

public class MAKflow_JBEI_SLURM_v2 {
    String[] valid_args = {
            "-data", "-parameters", "-server", "-account", "-qos"
    };

    HashMap options;
    static String[] arg_desc = {
            "<-data real valued dataset>" +
                    "<-parameter template parameter file>"
    };

    static String arg_desc_str = StringUtil.replace(Arrays.toString(arg_desc), "><", ">\n<");


    //static double[] levels = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22};

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
    String prm_in_path;

    double startlevel = 1;
    double stoplevel = 1;

    String geneids_file;
    String expids_file;
    String Rdata_file;

    String basepath;
    String basename;
    String filename;

    //String localuser = null;
    //String clusteruser = null;
    //String clusteruser_prefix = null;
    //String clusterpath = null;
    String localpath = null;

    String qos = "";

    String clusterWorkspace = null;

    String criterion = null;
    int criterion_index = 160;
    int num_criterion = 3;

    int precriterion_index = -1;//26;//MSEC
    int num_precriterion = -1;//1;
    String precriterion = null;

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
    double mem_per_cpu = 2;
    double null_mem_per_cpu = 4;

    //for starting points
    int Imax_start = 100;
    int Imin_start = 10;
    int Jmax_start = 50;
    int Jmin_start = 10;
    int useAbs = 0;
    String startoutfile = "";
    String startfileprefix = "";
    int num_start_points = 0;
    String hclmetric = "correlation";
    String hcllink = "complete";

    int size_precrit_gene = -1;
    int size_precrit_exp = -1;
    double precrit_gene_perc = Double.NaN;
    double precrit_exp_perc = Double.NaN;

    String default_walltime = "24:00:00";

    String refinement_starting_points = "";
    String refinement_prefix = "";
    int num_str_pt_refine = 0;

    //String[] str_pt_index = null;

    //boolean test_mode = false;
    //String mode = "normal";//or "test"

    boolean exclude = false;

    int percent = 66;

    boolean usePseudo = true;
    boolean useLog = false;

    //String nullJobID = null;

    long period = 60000; // 60000 = 1 min.

    String iterationSuffix = "";

    String[] levelTimes = new String[50];
    String[] levelNames = new String[50];
    int levelIndex = 0;

    //String[] jobIdsArray = new String[10000];
    //int jobIdsArrayCounter = 0;

    String TF_file = null;
    String feat_file = null;
    String inter_file = null;

    String R_TF_data = null;
    String R_feat_data = null;
    String R_inter_data = null;

    String runmode = "BS";


    final double min_complete_percent = 0.3;

    int max_jobs = 500;
    private String exclude_path;

    String server = null;
    String account = null;

    String[] precrit_list;
    String[] crit_list;

    boolean noNull = false;


    /**
     * @param args
     */
    public MAKflow_JBEI_SLURM_v2(String[] args) {

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

            /*String[] crits = criterion.split("_");
            String[] precrits = precriterion.split("_");
            ArrayList allcrits = new ArrayList();
            for (int i = 0; i < crits.length; i++) {
                allcrits.add(crits[0]);
            }
            for (int i = 0; i < precrits.length; i++) {
                int index = Arrays.asList(crits).indexOf(precrits[0]);
                if (index == -1) {
                    allcrits.add(precrits[0]);
                }
            }

            String[] allcritsAr = (String[]) allcrits.toArray(new String[0]);*/


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

            String Rprep = "expr_data <- as.matrix(read.table(\"" + basename + ".txt\",sep=\"\\t\",header=T,row.names=1))\n";

            if (TF_file != null) {
                Rprep += "tf_data <- as.matrix(read.table(\"" + TF_file + "\", sep=\"\\t\",header=T,row.names=1))\n";
                Rprep += "save(tf_data, file=\"" + R_TF_data + "\")\n";
            }
            if (feat_file != null) {
                Rprep += "feat_data <- as.matrix(read.table(\"" + feat_file + "\", sep=\"\\t\",header=T,row.names=1))\n";
                Rprep += "save(feat_data, file=\"" + R_feat_data + "\")\n";
            }
            if (inter_file != null) {
                Rprep += "interact_data <- as.matrix(read.table(\"" + inter_file + "\", sep=\"\\t\",header=T,row.names=1))\n";
                Rprep += "save(interact_data, file=\"" + R_inter_data + "\")\n";
            }

            Rprep += "colvar <- apply(expr_data,2,var)\n";
            Rprep += "rowvar <- apply(expr_data,1,var)\n";
            Rprep += "rowvar_zero <- which(rowvar == 0)\n";
            Rprep += "colvar_zero <- which(colvar == 0)\n";
            Rprep += "expr_data_orig <- expr_data\n";
            //expr_data <- expr_data_orig[-16597,]
            Rprep += "if(length(rowvar_zero) > 0) expr_data <- expr_data_orig[-rowvar_zero,]\n";
            Rprep += "if(length(colvar_zero) > 0) expr_data <- expr_data_orig[,-colvar_zero]\n";

            Rprep += "write.table(expr_data, sep=\"\\t\", file =\"" + basename + ".txt\", col.names=NA)\n";

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
            Rprep += "colvar <- apply(expr_data,2,var)\n";
            Rprep += "rmean <- rowMeans(expr_data)\n";
            Rprep += "rowvar <- apply(expr_data,1,var)\n";
            /*
            Rprep += "library(ggplot2)\n";
            Rprep += "p1 <- ggplot(data.frame(rowvar), aes(x=rowvar)) + geom_histogram(fill=\"blue\") + labs(list(title=\"Variance of Rows\", x=\"Variance\", y=\"Count\"))\n";
            Rprep += "p2 <- ggplot(data.frame(colvar), aes(x=colvar)) + geom_histogram(fill=\"red\") + labs(list(title=\"Variance of Columns\", x=\"Variance\", y=\"Count\"))\n";
            Rprep += "p3 <- ggplot(data.frame(rmean), aes(x=rmean)) + geom_histogram(fill=\"yellow\") + labs(list(title=\"Mean of Rows\", x=\"Mean\", y=\"Count\"))\n";
            Rprep += "p4 <- ggplot(data.frame(cmean), aes(x=cmean)) + geom_histogram(fill=\"green\") + labs(list(title=\"Mean of Columns\", x=\"Mean\", y=\"Count\"))\n";
            Rprep += "pdf(\"" + output + "data_statistics.pdf\", height=10, width=20)\n";
            Rprep += "multiplot(p1, p2, p3, p4, cols=2)\n";
            Rprep += "dev.off()\n";
            */
            Rprep += "write.table(rownames(expr_data), col.names = F, \"" + output + geneids_file + "\",sep=\"\\t\")\n";
            Rprep += "write.table(colnames(expr_data),col.names = F,\"" + output + expids_file + "\",sep=\"\\t\")\n";
            //Rprep += "expr_data <- data.matrix(expr_data)\n";

            if (feat_file != null && TF_file != null && inter_file != null) {
                //Rprep += "interact_data <- as.matrix(read.table(\"" + inter_file + "\", sep=\"\\t\",header=T,row.names=1))\n";
                //Rprep += "tf_data <- as.matrix(read.table(\"" + TF_file + "\", sep=\"\\t\",header=T,row.names=1))\n";
                //Rprep += "feat_data <- as.matrix(read.table(\"" + feat_file + "\", sep=\"\\t\",header=T,row.names=1))\n";
                Rprep += "save(expr_data, interact_data, feat_data, tf_data, file=\"" + output + Rdata_file + "\")\n";
            } else if (feat_file != null && inter_file != null) {
                //Rprep += "feat_data <- as.matrix(read.table(\"" + feat_file + "\", sep=\"\\t\",header=T,row.names=1))\n";
                //Rprep += "interact_data <- as.matrix(read.table(\"" + inter_file + "\", sep=\"\\t\",header=T,row.names=1))\n";
                Rprep += "save(expr_data, feat_data, interact_data, file=\"" + output + Rdata_file + "\")\n";
            } else if (feat_file != null && TF_file != null) {
                //Rprep += "feat_data <- as.matrix(read.table(\"" + feat_file + "\", sep=\"\\t\",header=T,row.names=1))\n";
                //Rprep += "tf_data <- as.matrix(read.table(\"" + TF_file + "\", sep=\"\\t\",header=T,row.names=1))\n";
                Rprep += "save(expr_data, feat_data, tf_data, file=\"" + output + Rdata_file + "\")\n";
            } else if (TF_file != null && inter_file != null) {
                //Rprep += "interact_data <- as.matrix(read.table(\"" + inter_file + "\", sep=\"\\t\",header=T,row.names=1))\n";
                //Rprep += "tf_data <- as.matrix(read.table(\"" + TF_file + "\", sep=\"\\t\",header=T,row.names=1))\n";
                Rprep += "save(expr_data, interact_data, tf_data, file=\"" + output + Rdata_file + "\")\n";
            } else if (inter_file != null) {
                //Rprep += "interact_data <- as.matrix(read.table(\"" + inter_file + "\", sep=\"\\t\",header=T,row.names=1))\n";
                Rprep += "save(expr_data, interact_data, file=\"" + output + Rdata_file + "\")\n";
            } else if (TF_file != null) {
                //Rprep += "tf_data <- as.matrix(read.table(\"" + TF_file + "\", sep=\"\\t\",header=T,row.names=1))\n";
                Rprep += "save(expr_data, tf_data, file=\"" + output + Rdata_file + "\")\n";
            } else if (feat_file != null) {
                //Rprep += "feat_data <- as.matrix(read.table(\"" + feat_file + "\", sep=\"\\t\",header=T,row.names=1))\n";
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
            System.out.println("LEVEL 1 COMPLETED: " + (end - start) / 1000.0 + " s");
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
            if (useAbs == 1 || criterion.indexOf("Binary") != -1) {
                frxnsign_param = "F";
            }

            if (!doesFileExist(input + basename + ".Rdata") || !doesFileExist(input + basename + "_geneids.txt")) {
                System.out.println("ERROR: The path provided for the R data file or gene header file is invalid. Please check that level 1 finished succesfully and files with the suffix *_geneids.txt and *.Rdata\nExiting now ...");
                System.exit(1);
            }

            String makenull_pbs = "";
            String makenull_pbs_file = basename + "_make_nulls.tasks";
            if (!noNull) {
                for (int it = 1; it <= maxnulljobs; it++) {
                    makenull_pbs += "hostname >  " + localpath + output_subdir3 + it + "_host.$HT_TASK_ID.host; " +
                            "java -Xmx" + (int) (null_mem_per_cpu * 1000.0) + "M DataMining.MakeNull  " +//-Xms2G
                            "-source " + R_path + " " +
                            "-intxt " + localpath + filename + " " +
                            "-inR " + input + basename + ".Rdata " +
                            "-out " + localpath + output_subdir1 + nullprefix + "_" + it + " " +
                            "-gmin " + Imin + " " +
                            "-gmax " + Imax + " " +
                            "-emin " + Jmin + " " +
                            "-emax " + Jmax + " " +
                            "-nsamp " + nsamp + " " +
                            "-genes " + input + basename + "_geneids.txt " +
                            "-seed " + it + " " +
                            "-debug n " +
                            "-crits " + criterion + " ";
                    if (TF_file != null) {
                        makenull_pbs += "-inTF " + TF_file + " ";
                    }
                    if (feat_file != null) {
                        makenull_pbs += "-infeat " + feat_file + " ";
                    }

                    makenull_pbs += "-frxnsign " + frxnsign_param + " " + "-abs " + absvect +
                            " &> " + localpath + output_subdir3 + it + "_host.$HT_TASK_ID.out\n";
                }

                System.out.println("WRITING " + makenull_pbs_file);
                TextFile.write(makenull_pbs, scriptbox + makenull_pbs_file);

                File testdirf = new File(testoutput);
                System.out.println("\"" + output + "\": " + dir.length() + ", " +
                        "\"" + testoutput + "\": " + testdirf.length());
            }

            setLevel += 1;
            long end = System.currentTimeMillis();
            System.out.println("LEVEL 2 COMPLETED: " + (end - start) / 1000.0 + " s");
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

            if (!noNull) {
                if (!doesFileExist(localpath + input + basename + "_make_nulls.tasks")) {
                    System.out.println("ERROR: The path provided to the null task file " + localpath + input + basename + "_make_nulls.tasks does not exist\nExiting now...");
                    System.exit(1);
                }

                try {
                    run_ht_helper(localpath + input + basename + "_make_nulls.tasks", scriptbox, default_walltime, null,
                            Math.min(maxnulljobs, max_jobs), null_mem_per_cpu, false, (int) setLevel);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            long end = System.currentTimeMillis();
            System.out.println("LEVEL 3 COMPLETED: " + (end - start) / 1000.0 + " s");
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

            if (!noNull) {
                if (isDirectoryEmpty(localpath + input)) {
                    System.out.println("ERROR: The directory where null output files should be located: " + localpath + input + " is empty. Please check whether the MakeNull task file was successfully run.\nExiting now...");
                    System.exit(1);
                }

                String interpolate = "java DataMining.InterPolateNulls " + input + " " + output + nullprefix + " &> " + output + "InterPolateNulls.out";
                String interpolate_shell = "runInterpolate.sh";

                runCmd(interpolate, scriptbox, interpolate_shell);

            }
            setLevel += 1;
            long end = System.currentTimeMillis();
            System.out.println("LEVEL 4 COMPLETED: " + (end - start) / 1000.0 + " s");
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

            if (!noNull) {
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
            }

            setLevel += 1;
            long end = System.currentTimeMillis();
            System.out.println("LEVEL 5 COMPLETED: " + (end - start) / 1000.0 + " s");
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
            String outputdir = "level6.1/";
            String testoutput = "level6.outref/";

            createFile(scriptbox);
            createFile(outputdir);

            String Rstarts_script = "library(\"amap\")\n";
            Rstarts_script += "set.seed(" + MINER_STATIC.RANDOM_SEEDS[0] + ")\n";
            Rstarts_script += "setwd(\"" + localpath + "/" + input + "\")\n";
            Rstarts_script += "source(\"" + R_path + "\")\n";
            Rstarts_script += "load(\"" + Rdata_file + "\")\n";
            Rstarts_script += "set.seed(" + MINER_STATIC.RANDOM_SEEDS[0] + ")\n";
            Rstarts_script += "expr_data_row=t(apply(expr_data,1,missfxn))\n";
            Rstarts_script += "expr_data_col=apply(expr_data,2,missfxn)\n";

            Rstarts_script += "nbs1=allpossibleInitial(expr_data_row," + Imin_start + "," + Imax_start + "," + Jmin_start + "," + Jmax_start + ",\"" + hclmetric + "\",useAbs=" + useAbs + ", isCol=1,linkmethod=\"" + hcllink + "\")\n";
            Rstarts_script += "nbs2=allpossibleInitial(expr_data_col," + Imin_start + "," + Imax_start + "," + Jmin_start + "," + Jmax_start + ",\"" + hclmetric + "\",useAbs=" + useAbs + ", isCol=0,linkmethod=\"" + hcllink + "\")\n";
            Rstarts_script += "nbsall <- c(nbs1, nbs2)\n";

            startoutfile = outputdir + basename + "_STARTS_abs" + useAbs + "_I" + Imin_start + "_" + Imax_start + "_J" + Jmin_start + "_" + Jmax_start + "_RC.txt";
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

            startoutfile = localpath + outputdir + basename + "_STARTS_abs" + useAbs + "_I" + Imin_start + "_" + Imax_start + "_J" + Jmin_start + "_" + Jmax_start + "_RC.txt";
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
            System.out.println("\"" + outputdir + "\": " + testdirf.length() + ", " +
                    "\"" + testoutput + "\": " + testdirf.length());

            long end = System.currentTimeMillis();
            System.out.println("LEVEL 6 COMPLETED: " + (end - start) / 1000.0 + " s");
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

                /*try {
                    criterion_index = StringUtil.getFirstEqualsIndex(MINER_STATIC.CRIT_LABELS, criterion);
                } catch (Exception e) {
                    System.out.println("The criterion combination specified is not valid. Try shuffling the order of the criteria.\n" +
                            "Remember, the feat and MAXTF should always be the last criteria in the combination.");
                    e.printStackTrace();
                }*/

                criterion_index = StringUtil.getFirstEqualsIndexIgnoreCase(MINER_STATIC.CRIT_LABELS, criterion);
                precriterion_index = StringUtil.getFirstEqualsIndexIgnoreCase(MINER_STATIC.CRIT_LABELS, precriterion);

                System.out.println("level 7 " + precriterion + "\t" + precriterion_index);
                System.out.println("level 7 " + criterion + "\t" + criterion_index);

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
                String outputdir = "level7_iter" + iteration + ".1/";
                String testoutput = "level7_iter" + iteration + ".outref/";
                String subdir_1 = outputdir + "out_files/";
                String subdir_2 = outputdir + "results/";
                String subdir_3 = outputdir + "toplist_files/";

                prm.OUTPREFIX = "iter_" + counter;
                System.out.println("level 7 prm.OUTPREFIX " + prm.OUTPREFIX);
                if (exclude && counter > 0) {
                    exclude_path = localpath + "level12.1/results_" + basename + "_concat" + counter + ".vbl";
                    if (!doesFileExist(exclude_path)) {
                        System.out.println("WARNING: Exclusion is specified but the exclusion file path is broken: " + exclude_path + ".\nExiting now...");
                        //System.exit(1);
                    } else
                        prm.EXCLUDE_LIST_PATH = exclude_path;
                } else {
                    prm.EXCLUDE_LIST_PATH = "";
                }
                prm.RUN_SEQUENCE = runmode;
                prm.CRIT_TYPE_INDEX = criterion_index;
                prm.PRECRIT_TYPE_INDEX = precriterion_index;
                prm.RANDOM_SEED = MINER_STATIC.RANDOM_SEEDS[counter];
                prm.BATCH_DMETHOD = hclmetric;
                prm.BATCH_LINKMETHOD = hcllink;

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
                prm.SIZE_PRECRIT_LIST = -1;
                prm.PBATCH = 1.0;
                prm.DATA_LEN_EXPS = num_col;
                prm.DATA_LEN_GENES = num_row;

                if (size_precrit_gene != -1) {
                    prm.SIZE_PRECRIT_LIST_GENE = size_precrit_gene;
                }
                if (size_precrit_exp != -1) {
                    prm.SIZE_PRECRIT_LIST_EXP = size_precrit_exp;
                }


                if (precrit_gene_perc != -1) {
                    prm.fraction_genes_for_precritmove = precrit_gene_perc;
                }
                if (precrit_exp_perc != -1) {
                    prm.fraction_exps_for_precritmove = precrit_exp_perc;
                }


                prm.RANDOMFLOOD = false;
                prm.EXCLUDE_OVERLAP_THRESHOLD = 0.5;
                if (useAbs == 1) {
                    prm.USE_ABS = true;
                    prm.USE_ABS_AR = absvectIntArray;
                    prm.FRXN_SIGN = false;
                } else if (useAbs == 0) {
                    prm.USE_ABS = false;
                    prm.USE_ABS_AR = absvectIntArray;
                    prm.FRXN_SIGN = true;
                } else if (criterion.toLowerCase().indexOf("Binary".toLowerCase()) == -1) {
                    prm.USE_ABS = false;
                    prm.USE_ABS_AR = absvectIntArray;
                    prm.FRXN_SIGN = true;
                } else if (criterion.toLowerCase().indexOf("Binary".toLowerCase()) != -1) {
                    prm.USE_ABS = false;
                    prm.USE_ABS_AR = absvectIntArray;
                    prm.FRXN_SIGN = false;
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

                prm.MEANGEE_PATH = "";
                prm.SDGEE_PATH = "";
                prm.MEANGEECE_PATH = "";
                prm.SDGEECE_PATH = "";
                prm.MEANGEERE_PATH = "";
                prm.SDGEERE_PATH = "";

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

                // Specify Transcription Factor Criterion Data/Null Locations
                if (TF_file != null) {
                    prm.MEANTF_PATH = localpath + "level5.1/" + nullprefix + "MAXTF_median_full.txt";
                    prm.SDTF_PATH = localpath + "level5.1/" + nullprefix + "MAXTF_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANTF_PATH) || !doesFileExist(prm.SDTF_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the MAXTF criteria are incorrect. " +
                                "Please check that these null files exist in level5.1/\nExiting now...");
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
                if (criterion.contains("GEE") && !criterion.contains("GEECE") && !criterion.contains("GEERE") && !criterion.toLowerCase().contains("GEEnonull".toLowerCase())) {
                    prm.MEANGEE_PATH = localpath + "level5.1/" + nullprefix + "GEE_median_full.txt";
                    prm.SDGEE_PATH = localpath + "level5.1/" + nullprefix + "GEE_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANGEE_PATH) || !doesFileExist(prm.SDGEE_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the GEE criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }
                if (criterion.contains("GEECE") && !criterion.toLowerCase().contains("GEECEnonull".toLowerCase())) {
                    prm.MEANGEECE_PATH = localpath + "level5.1/" + nullprefix + "GEECE_median_full.txt";
                    prm.SDGEECE_PATH = localpath + "level5.1/" + nullprefix + "GEECE_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANGEECE_PATH) || !doesFileExist(prm.SDGEECE_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the GEECE criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }
                if (criterion.contains("GEERE") && !criterion.toLowerCase().contains("GEEREnonull".toLowerCase())) {
                    prm.MEANGEERE_PATH = localpath + "level5.1/" + nullprefix + "GEERE_median_full.txt";
                    prm.SDGEERE_PATH = localpath + "level5.1/" + nullprefix + "GEERE_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANGEERE_PATH) || !doesFileExist(prm.SDGEERE_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the GEERE criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }
                if (criterion.contains("MSE") && !criterion.contains("MSEC") && !criterion.contains("MSER") && !criterion.toLowerCase().contains("MSEnonull".toLowerCase())) {
                    prm.MEANMSE_PATH = localpath + "level5.1/" + nullprefix + "MSE_median_full.txt";
                    prm.SDMSE_PATH = localpath + "level5.1/" + nullprefix + "MSE_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANMSE_PATH) || !doesFileExist(prm.SDMSE_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the MSE criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }
                if (criterion.contains("MSEC") && !criterion.toLowerCase().contains("MSECnonull".toLowerCase())) {
                    prm.MEANMSEC_PATH = localpath + "level5.1/" + nullprefix + "MSEC_median_full.txt";
                    prm.SDMSEC_PATH = localpath + "level5.1/" + nullprefix + "MSEC_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANMSEC_PATH) || !doesFileExist(prm.SDMSEC_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the MSEC criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }
                if (criterion.contains("MSER") && !criterion.toLowerCase().contains("MSERnonull".toLowerCase())) {
                    prm.MEANMSER_PATH = localpath + "level5.1/" + nullprefix + "MSER_median_full.txt";
                    prm.SDMSER_PATH = localpath + "level5.1/" + nullprefix + "MSER_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANMSER_PATH) || !doesFileExist(prm.SDMSER_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the MSER criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }
                if (criterion.contains("KENDALL") && !criterion.contains("KENDALLC") && !criterion.contains("KENDALLR") && !criterion.toLowerCase().contains("KENDALLnonull".toLowerCase())) {
                    prm.MEANKEND_PATH = localpath + "level5.1/" + nullprefix + "KENDALL_median_full.txt";
                    prm.SDKEND_PATH = localpath + "level5.1/" + nullprefix + "KENDALL_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANKEND_PATH) || !doesFileExist(prm.SDKEND_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the KENDALL criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }
                if (criterion.contains("KENDALLR") && !criterion.toLowerCase().contains("KENDALLRnonull".toLowerCase())) {
                    prm.MEANKENDR_PATH = localpath + "level5.1/" + nullprefix + "KENDALLR_median_full.txt";
                    prm.SDKENDR_PATH = localpath + "level5.1/" + nullprefix + "KENDALLR_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANKENDR_PATH) || !doesFileExist(prm.SDKENDR_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the KENDALLR criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }
                if (criterion.contains("KENDALLC") && !criterion.toLowerCase().contains("KENDALLCnonull".toLowerCase())) {
                    prm.MEANKENDC_PATH = localpath + "level5.1/" + nullprefix + "KENDALLC_median_full.txt";
                    prm.SDKENDC_PATH = localpath + "level5.1/" + nullprefix + "KENDALLC_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANKENDC_PATH) || !doesFileExist(prm.SDKENDC_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the KENDALLC criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }

                if (criterion.contains("COR") && !criterion.contains("CORC") && !criterion.contains("CORR") && !criterion.toLowerCase().contains("CORnonull".toLowerCase())) {
                    prm.MEANCOR_PATH = localpath + "level5.1/" + nullprefix + "COR_median_full.txt";
                    prm.SDCOR_PATH = localpath + "level5.1/" + nullprefix + "COR_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANCOR_PATH) || !doesFileExist(prm.SDCOR_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the COR criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }
                if (criterion.contains("CORR") && !criterion.toLowerCase().contains("CORRnonull".toLowerCase())) {
                    prm.MEANCORR_PATH = localpath + "level5.1/" + nullprefix + "CORR_median_full.txt";
                    prm.SDCORR_PATH = localpath + "level5.1/" + nullprefix + "CORR_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANCORR_PATH) || !doesFileExist(prm.SDCORR_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the CORR criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }
                if (criterion.contains("CORC") && !criterion.toLowerCase().contains("CORCnonull".toLowerCase())) {
                    prm.MEANCORC_PATH = localpath + "level5.1/" + nullprefix + "CORC_median_full.txt";
                    prm.SDCORC_PATH = localpath + "level5.1/" + nullprefix + "CORC_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANCORC_PATH) || !doesFileExist(prm.SDCORC_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the CORC criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }

                if (criterion.contains("EUC") && !criterion.contains("EUCC") && !criterion.contains("EUCR") && !criterion.toLowerCase().contains("EUCnonull".toLowerCase())) {
                    prm.MEANEUC_PATH = localpath + "level5.1/" + nullprefix + "EUC_median_full.txt";
                    prm.SDEUC_PATH = localpath + "level5.1/" + nullprefix + "EUC_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANEUC_PATH) || !doesFileExist(prm.SDEUC_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the EUC criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }
                if (criterion.contains("EUCR") && !criterion.toLowerCase().contains("EUCRnonull".toLowerCase())) {
                    prm.MEANEUCR_PATH = localpath + "level5.1/" + nullprefix + "EUCR_median_full.txt";
                    prm.SDEUCR_PATH = localpath + "level5.1/" + nullprefix + "EUCR_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANEUCR_PATH) || !doesFileExist(prm.SDEUCR_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the EUCR criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }
                if (criterion.contains("EUCC") && !criterion.toLowerCase().contains("EUCCnonull".toLowerCase())) {
                    prm.MEANEUCC_PATH = localpath + "level5.1/" + nullprefix + "EUCC_median_full.txt";
                    prm.SDEUCC_PATH = localpath + "level5.1/" + nullprefix + "EUCC_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANEUCC_PATH) || !doesFileExist(prm.SDEUCC_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the EUCC criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }

                if (criterion.contains("SPEARMAN") && !criterion.contains("SPEARMANC") && !criterion.contains("SPEARMANR") && !criterion.toLowerCase().contains("SPEARMANnonull".toLowerCase())) {
                    prm.MEANSPEAR_PATH = localpath + "level5.1/" + nullprefix + "SPEARMAN_median_full.txt";
                    prm.SDSPEAR_PATH = localpath + "level5.1/" + nullprefix + "SPEARMAN_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANSPEAR_PATH) || !doesFileExist(prm.SDSPEAR_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the SPEAR criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }
                if (criterion.contains("SPEARMANR") && !criterion.toLowerCase().contains("SPEARMANRnonull".toLowerCase())) {
                    prm.MEANSPEARR_PATH = localpath + "level5.1/" + nullprefix + "SPEARMANR_median_full.txt";
                    prm.SDSPEARR_PATH = localpath + "level5.1/" + nullprefix + "SPEARMANR_0.5IQR_full.txt";
                    //System.out.println("MAKflow set SPEARMANR " + prm.SDSPEARR_PATH);
                    if (!doesFileExist(prm.MEANSPEARR_PATH) || !doesFileExist(prm.SDSPEARR_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the SPEARMANR criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }
                if (criterion.contains("SPEARMANC") && !criterion.toLowerCase().contains("SPEARMANCnonull".toLowerCase())) {
                    prm.MEANSPEARC_PATH = localpath + "level5.1/" + nullprefix + "SPEARMANC_median_full.txt";
                    prm.SDSPEARC_PATH = localpath + "level5.1/" + nullprefix + "SPEARMANC_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANSPEARC_PATH) || !doesFileExist(prm.SDSPEARC_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the SPEARMANC criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }

                if (criterion.contains("BINARY") && !criterion.contains("BINARYC") && !criterion.contains("BINARYR") && !criterion.toLowerCase().contains("BINARYnonull".toLowerCase())) {
                    prm.MEANBINARY_PATH = localpath + "level5.1/" + nullprefix + "BINARY_median_full.txt";
                    prm.SDBINARY_PATH = localpath + "level5.1/" + nullprefix + "BINARY_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANBINARY_PATH) || !doesFileExist(prm.SDBINARY_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the BINARY criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }
                if (criterion.contains("BINARYR") && !criterion.toLowerCase().contains("BINARYRnonull".toLowerCase())) {
                    prm.MEANBINARYR_PATH = localpath + "level5.1/" + nullprefix + "BINARYR_median_full.txt";
                    prm.SDBINARYR_PATH = localpath + "level5.1/" + nullprefix + "BINARYR_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANBINARYR_PATH) || !doesFileExist(prm.SDBINARYR_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the BINARYR criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }
                if (criterion.contains("BINARYC") && !criterion.toLowerCase().contains("BINARYCnonull".toLowerCase())) {
                    prm.MEANBINARYC_PATH = localpath + "level5.1/" + nullprefix + "BINARYC_median_full.txt";
                    prm.SDBINARYC_PATH = localpath + "level5.1/" + nullprefix + "BINARYC_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANBINARYC_PATH) || !doesFileExist(prm.SDBINARYC_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the BINARYC criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }
                if (criterion.equalsIgnoreCase("MEAN") && !criterion.toLowerCase().contains("MEANnonull".toLowerCase())) {
                    prm.MEANMEAN_PATH = localpath + "level5.1/" + nullprefix + "MEAN_median_full.txt";
                    prm.SDMEAN_PATH = localpath + "level5.1/" + nullprefix + "MEAN_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANMEAN_PATH) || !doesFileExist(prm.SDMEAN_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the MEAN criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }

                System.out.println("writing parameters " + prm_path);
                prm.write(prm_path);

                createFile(scriptbox);
                File dir = createFile(outputdir);
                createFile(subdir_1);
                createFile(subdir_2);
                createFile(subdir_3);

                String argument[];

                if (!doesFileExist(startoutfile)) {
                    System.out.println("ERROR: The starting point biclusters file does not exist: " + startoutfile + "\nExiting now...");
                    System.exit(1);
                }

                argument = new String[]{"-param", prm_path, "-in", startoutfile, "-out", outputdir + basename,
                        "-samp", "" + (counter + 1), "-minsamp", "" + "" + counter, "-top", "y", "-silent",
                        "t", "-crits", criterion.toUpperCase()};

                if (precriterion != null) {
                    argument = new String[]{"-param", prm_path, "-in", startoutfile, "-out", outputdir + basename,
                            "-samp", "" + (counter + 1), "-minsamp", "" + "" + counter, "-top", "y", "-silent",
                            "t", "-crits", criterion.toUpperCase(), "-precrits", precriterion.toUpperCase()};
                }

                System.out.println("RUNNING: " + MoreArray.toString(argument, ","));

                CreateParamSet.main(argument);

                System.out.println("PARAMETERS CREATED!");

                setLevel += 1;

                File testdirf = new File(testoutput);
                System.out.println("\"" + outputdir + "\": " + dir.length() + ", " +
                        "\"" + testoutput + "\": " + testdirf.length());
                long end = System.currentTimeMillis();
                System.out.println("LEVEL 7 COMPLETED: " + (end - start) / 1000.0 + " s");
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

                String task_file = basename + "_run_miner.tasks";

                String task_script = "";

                System.out.println("level 8 prm.OUTPREFIX " + prm.OUTPREFIX);
                for (int it = 0; it < num_start_points; it++) {
                    task_script += "hostname >  " + localpath + subdir_1 + prm.OUTPREFIX + "_" + startfileprefix + "_" + it + "__" + criterion.toUpperCase() + "__AG_" + MINER_STATIC.RANDOM_SEEDS[counter] + "_host.$HT_TASK_ID.host; " +
                            "java  -Xmx" + (int) (mem_per_cpu * 1000.0) + "M DataMining.RunMiner ";
                    task_script += "-param " + localpath + input + prm.OUTPREFIX + "_" + startfileprefix + "_" + it + "__" + criterion.toUpperCase() + "__AG_" + MINER_STATIC.RANDOM_SEEDS[counter] + "__parameters.txt " +
                            "&> "
                            + localpath + subdir_1 + prm.OUTPREFIX + "_" + startfileprefix + "_" + it + "__" + criterion.toUpperCase() + "__AG_" + MINER_STATIC.RANDOM_SEEDS[counter] + "_host.$HT_TASK_ID.out\n";
                }
                System.out.println("RunMiner task file written!");

                TextFile.write(task_script, scriptbox + task_file);

                long end = System.currentTimeMillis();
                System.out.println("LEVEL 8 COMPLETED: " + (end - start) / 1000.0 + " s");
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
                    run_ht_helper(localpath + input + basename + "_run_miner.tasks", scriptbox, default_walltime,
                            null, max_jobs, mem_per_cpu, false, (int) setLevel);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                long end = System.currentTimeMillis();
                System.out.println("LEVEL 9 COMPLETED: " + (end - start) / 1000.0 + " s");
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

                if (percent_complete < min_complete_percent) {
                    System.out.println("WARNING: Less than 90% of the starting points produced successful results. This indicates that there was an overall issue with running RunMiner biclustering " +
                            "and not just some technical difficulties with some of the starting points. Please check levels 8 and 9.");
                    //\nExiting now...");
                    //System.exit(1);
                }

                String pfem_script_file = basename + "_run_ParamforErrMiss.sh";

                String pfem_script = "";
                if (runmode.compareToIgnoreCase("N") != 0 &&
                        runmode.compareToIgnoreCase("BN") != 0 &&
                        runmode.compareToIgnoreCase("bN") != 0 &&
                        runmode.compareToIgnoreCase("ON") != 0 &&
                        runmode.compareToIgnoreCase("oN") != 0) {
                    pfem_script = "java -Xmx" + (int) (mem_per_cpu * 1000.0) + "M DataMining.ParamforErrMiss ";
                    pfem_script += localpath + "level7_iter" + iteration + ".1/results/ " +
                            localpath + "level7_iter" + iteration + ".1/" + basename + "_1/ " +
                            localpath + "level10_iter" + iteration + ".1/ParamforErrMiss/ " +
                            "&> " + localpath + "level10_iter" + iteration + ".1/param_for_err_miss.out";
                    System.out.println("pfem_script " + pfem_script);
                    runCmd(pfem_script, scriptbox, pfem_script_file);

                } else {// if (runmode.compareToIgnoreCase("N") == 0) {
                    pfem_script = "java -Xmx" + (int) (mem_per_cpu * 1000.0) + "M DataMining.ParamforErrMissForN ";
                    pfem_script += " -results_dir " + localpath + "level7_iter" + iteration + ".1/results/ " +
                            " -param_dir " + localpath + "level7_iter" + iteration + ".1/" + basename + "_1/ " +
                            " -out_dir " + localpath + "level10_iter" + iteration + ".1/ParamforErrMiss/ " +
                            "&> " + localpath + "level10_iter" + iteration + ".1/param_for_err_miss.out";
                    System.out.println("pfem_script");
                    System.out.println(pfem_script);
                    runCmd(pfem_script, scriptbox, pfem_script_file);
                }

                String task_script_file = basename + "_run_MakeParamTasks.sh";
                String task_file = basename + "_rerun_miner.tasks";
                String task_script = "";

                //File folder = new File(localpath + "level10_iter" + iteration + ".1/ParamforErrMiss/");
                //File[] listOfFiles = folder.listFiles();

                task_script = "java -Xmx" + (int) (mem_per_cpu * 1000.0) + "M DataMining.util.MakeParamTasks ";
                task_script += localpath + "level10_iter" + iteration + ".1/ParamforErrMiss/ " +
                        localpath + "level10_iter" + iteration + ".0/" + task_file + " " + iteration + " " + localpath + " " + (int) mem_per_cpu +
                        "&> " + localpath + "level10_iter" + iteration + ".1/MakeParamTasks.out";
                System.out.println("task_script");
                System.out.println(task_script);
                runCmd(task_script, scriptbox, task_script_file);

                /*for (int i = 0; i < listOfFiles.length; i++) {
                    if (listOfFiles[i].getName().endsWith("parameters.txt")) {
                        String infile = localpath + "level10_iter" + iteration + ".1/ParamforErrMiss/" + listOfFiles[i].getName();
                        String outfile = localpath + "level8_iter" + iteration + ".1/out_files/" + listOfFiles[i].getName() + "_host.$HT_TASK_ID.out";

                        task_script += "hostname >  " + localpath + "level8_iter" + iteration + ".1/out_files/" + listOfFiles[i].getName() + "_host.$HT_TASK_ID.host; " +
                                "java  -Xmx" + (int) (mem_per_cpu * 1000.0) + "M DataMining.RunMiner ";
                        task_script += "-param " + infile +
                                "&> " + outfile + '\n';
                    }
                }

                TextFile.write(task_script, scriptbox + task_file);
                System.out.println("Re-RunMiner task file written!");*/

                File testdir = new File(testoutput);
                System.out.println("\"" + toplist_dir + "\": " + dir.length() + ", " +
                        "\"" + testoutput + "\": " + testdir.length());
                long end = System.currentTimeMillis();
                System.out.println("LEVEL 10 COMPLETED: " + (end - start) / 1000.0 + " s");
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

                try {
                    run_ht_helper(localpath + input + basename + "_rerun_miner.tasks", scriptbox, default_walltime,
                            null, max_jobs, mem_per_cpu, false, (int) setLevel);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                long end = System.currentTimeMillis();
                System.out.println("LEVEL 11 COMPLETED: " + (end - start) / 1000.0 + " s");
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
                ListfromDir.main(argument);

                String concatenate_file = "concatenate.sh";

                ListfromDir ld = new ListfromDir();
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
                System.out.println("LEVEL 12 COMPLETED: " + (end - start) / 1000.0 + " s");
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
            //if more than one operation
            if (iter > 1) {
                input_file = "results_" + basename + "_concat" + iter + ".vbl";
            }

            if (!doesFileExist(localpath + input + input_file)) {
                System.out.println("ERROR: The ListfromDir result file does not exist: " + localpath + input + input_file + "\nExiting now...");
                System.exit(1);
            }

            percent = 66;
            String number = "NA";
            System.out.println(localpath + input + input_file);
            String argument[] = new String[]{localpath + input + input_file, percent + "%", number};
            ApplyCut.main(argument);

            String move = "mv " + localpath + input + "/results_" + basename + "_cut_scoreperc" + percent + ".0_exprNaN_0.0.txt " + localpath + output;
            /*TODO harsh renaming here with loss of info to maintain expected file name later*/
            if (iter > 1) {
                move = "mv " + localpath + input + "/results_" + basename + "_concat" + iter + "_cut_scoreperc" + percent + ".0_exprNaN_0.0.txt " + localpath + output + "/" + "results_" + basename + "_cut_scoreperc" + percent + ".0_exprNaN_0.0.txt";
            }
            String move_file = "move.sh";
            runCmd(move, scriptbox, move_file);

            String s = localpath + output + "results_" + basename + "_cut_scoreperc" + percent + ".0_exprNaN_0.0.txt";
            if (!new File(s).exists()) {
                System.out.println("Expected file not seen: " + s + "\nExiting.");
                System.exit(0);
            }

            File testdirf = new File(testoutput);
            System.out.println("\"" + output + "\": " + dir.length() + ", " +
                    "\"" + testoutput + "\": " + testdirf.length());
            long end = System.currentTimeMillis();
            System.out.println("LEVEL 13 COMPLETED: " + (end - start) / 1000.0 + " s");
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

            if (!doesFileExist(localpath + input + "results_" + basename + "_cut_scoreperc" + percent + ".0_exprNaN_0.0.txt")) {
                System.out.println("ERROR: The ApplyCut filtered result file does not exist: " + localpath + input + "results_" + basename + "_cut_scoreperc" + percent + ".0_exprNaN_0.0.txt" + "\nExiting now...");
                System.exit(1);
            }

            /*
             NOTE: bash script is currently hardcoded to start ListMergeMembers job on JBEI cluster on a single node with 24 memory available.
            */
            String sl_sns = scriptbox + "selectnrset.sl";
            String selectnrset_script = "#!/bin/bash\n" +
                    //"#SBATCH --qos=lr_normal\n" +
                    "#SBATCH --partition=" + server + "\n" +//"#SBATCH --partition=lr3\n" +
                    "#SBATCH --account=" + account + "\n" +
                    //"#SBATCH --constraint=lr3_c16\n" +
                    "#SBATCH --ntasks=1\n" +
                    "#SBATCH --cpus-per-task=1\n" +
                    "#SBATCH --mem=22G\n" +
                    "#SBATCH --time=120:00:00\n" +
                    "#SBATCH --output=MAKflow_" + setLevel + "_%j.out\n";
            if (!qos.equals(""))
                selectnrset_script += "#SBATCH --qos=" + qos + "" + "\n";

            //java -mx16000M DataMining.util.SelectNRSet -bic $1 -over 0.25 -mode score -type root
            selectnrset_script += "java -Xmx" + (int) (mem_per_cpu * 3000.0) + "M DataMining.util.SelectNRSet " +
                    "-bic " + localpath + input + "results_" + basename + "_cut_scoreperc" + percent + ".0_exprNaN_0.0.txt" +
                    " -over 0.25 -mode score -type root -out " + localpath + output +
                    "results_" + basename + "_cut_scoreperc" + percent + ".0_exprNaN_0.0__nr_0.25_score_root.txt" +
                    " &>" + localpath + output + "SelectNRSet.out";

            TextFile.write(selectnrset_script, sl_sns);

            // Run SLURM job and wait for it to finish
            String task_shell_out = localpath + scriptbox + "out.txt";
            String selectnrset_file = "run_sns.sh";
            String run_lmm = "sbatch " + sl_sns + " &> " + task_shell_out;
            runCmd(run_lmm, scriptbox, selectnrset_file);

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
                        System.out.println("ERROR: SBATCH SUBMISSION FOR SELECTNRSET JOB FAILED");
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
            System.out.println("LEVEL 14 COMPLETED: " + (end - start) / 1000.0 + " s");
            String timestamp = getTimeStamp();
            levelTimes[levelIndex] = timestamp;
            levelNames[levelIndex] = "level14";
            levelIndex++;
            printLevelTimeStamps();
            setLevel++;
        }

        /*         *//* level14 ListMergeMembers final *//*
        if (setLevel == 14 && (stopLevel >= 14 || stopLevel == 0)) {
            System.out.println("LEVEL 14");
            long start = System.currentTimeMillis();

            String input = "level13.1/";
            String scriptbox = "level14.0/";
            String output = "level14.1/";
            String testoutput = "level14.outref/";

            createFile(scriptbox);
            File dir = createFile(output);

            String ssh = "cp " + localpath + "level7_iter1.1/" + basename + "_1*//*_1_* " + localpath + scriptbox + "param_example.txt";
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

            *//*
             NOTE: bash script is currently hardcoded to start ListMergeMembers job on JBEI cluster on a single node with 24 memory available.
            *//*
            String sl_lmm = scriptbox + "listmergemembers.sl";
            String listmergeMembers_script = "#!/bin/bash\n" +
                    //"#SBATCH --qos=lr_normal\n" +
                    "#SBATCH --partition=" + server + "\n" +//"#SBATCH --partition=lr3\n" +
                    "#SBATCH --account=" + account + "\n" +
                    //"#SBATCH --constraint=lr3_c16\n" +
                    "#SBATCH --ntasks=1\n" +
                    "#SBATCH --mem=22G\n" +
                    "#SBATCH --time=120:00:00\n" +
                    "#SBATCH --output=MAKflow_" + setLevel + "_%j.out\n";
            if (!qos.equals(""))
                listmergeMembers_script += "#SBATCH --qos=" + qos + "" + "\n";

            listmergeMembers_script += "java -Xmx" + (int) (mem_per_cpu * 1000.0) + "M DataMining.ListMergeMembersPreComputed -dir " + localpath + input + "results_" + basename + "_cut_scoreperc" + percent + ".0_exprNaN_0.0.txt" +
                    " -crit " + criterion.toUpperCase() + " -param " + localpath + scriptbox + "param_example.txt" + " -ocut 0.25 -misscut 1.0 -numgene 1000 " +
                    "-complete y -live n -out " + localpath + output + "results_" + basename + "_cut_scoreperc" + percent + ".0_exprNaN_0.0_ocut_0.25_misscut_1.0_reconstructed.txt &>" + localpath + output + "ListMergeMembers_0.25_0.out";

            TextFile.write(listmergeMembers_script, sl_lmm);

            // Run SLURM job and wait for it to finish

            String task_shell_out = localpath + scriptbox + "out.txt";
            String listmergeMembers_file = "run_lmm.sh";
            String run_lmm = "sbatch " + sl_lmm + " &> " + task_shell_out;
            runCmd(run_lmm, scriptbox, listmergeMembers_file);

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
                        } else {
                            blockInput += as[2];
                        }
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
            System.out.println("LEVEL 14 COMPLETED: " + (end - start) / 1000.0 + " s");
            String timestamp = getTimeStamp();
            levelTimes[levelIndex] = timestamp;
            levelNames[levelIndex] = "level14";
            levelIndex++;
            printLevelTimeStamps();
            setLevel++;
        }*/

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
                    System.out.println("The criterion combination specified is not valid. \n" +
                            "Are any feat, ppi, MAXTF, the last criteria in the combination?");
                    e.printStackTrace();
                }
                try {
                    precriterion_index = StringUtil.getFirstEqualsIndex(MINER_STATIC.CRIT_LABELS, precriterion);
                } catch (Exception e) {
                    System.out.println("The precriterion combination specified is not valid. \n" +
                            "Are any feat, ppi, MAXTF, the last criteria in the combination?");
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


                prm.RUN_SEQUENCE = "N";

                prm.RANDOMFLOOD = false;
                prm.EXCLUDE_LIST_PATH = "";
                prm.OVERRIDE_SHAVING = true;

                prm.BATCH_DMETHOD = hclmetric;
                prm.BATCH_LINKMETHOD = hcllink;

                prm.CRIT_TYPE_INDEX = criterion_index;
                prm.PRECRIT_TYPE_INDEX = precriterion_index;
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
                prm.SIZE_PRECRIT_LIST = -1;
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
                } else if (useAbs == 0) {
                    prm.USE_ABS = false;
                    prm.USE_ABS_AR = absvectIntArray;
                    prm.FRXN_SIGN = true;
                } else if (criterion.indexOf("Binary") == -1) {
                    prm.USE_ABS = false;
                    prm.USE_ABS_AR = absvectIntArray;
                    prm.FRXN_SIGN = true;
                } else if (criterion.indexOf("Binary") != -1) {
                    prm.USE_ABS = false;
                    prm.USE_ABS_AR = absvectIntArray;
                    prm.FRXN_SIGN = false;
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

                prm.MEANGEE_PATH = "";
                prm.SDGEE_PATH = "";
                prm.MEANGEECE_PATH = "";
                prm.SDGEECE_PATH = "";
                prm.MEANGEERE_PATH = "";
                prm.SDGEERE_PATH = "";

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
                if (precriterion != null)
                    precriterion_index = StringUtil.getFirstEqualsIndexIgnoreCase(MINER_STATIC.CRIT_LABELS, precriterion);


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
                if (criterion.contains("GEE") && !criterion.contains("GEECE") && !criterion.contains("GEERE") && !criterion.toLowerCase().contains("GEEnonull".toLowerCase())) {
                    prm.MEANGEE_PATH = localpath + "level5.1/" + nullprefix + "GEE_median_full.txt";
                    prm.SDGEE_PATH = localpath + "level5.1/" + nullprefix + "GEE_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANGEE_PATH) || !doesFileExist(prm.SDGEE_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the GEE criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }
                if (criterion.contains("GEECE") && !criterion.toLowerCase().contains("GEECEnonull".toLowerCase())) {
                    prm.MEANGEECE_PATH = localpath + "level5.1/" + nullprefix + "GEECE_median_full.txt";
                    prm.SDGEECE_PATH = localpath + "level5.1/" + nullprefix + "GEECE_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANGEECE_PATH) || !doesFileExist(prm.SDGEECE_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the GEECE criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }
                if (criterion.contains("GEERE") && !criterion.toLowerCase().contains("GEEREnonull".toLowerCase())) {
                    prm.MEANGEERE_PATH = localpath + "level5.1/" + nullprefix + "GEERE_median_full.txt";
                    prm.SDGEERE_PATH = localpath + "level5.1/" + nullprefix + "GEERE_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANGEERE_PATH) || !doesFileExist(prm.SDGEERE_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the GEERE criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }
                if (criterion.contains("MSE") && !criterion.contains("MSEC") && !criterion.contains("MSER") && !criterion.toLowerCase().contains("MSEnonull".toLowerCase())) {
                    prm.MEANMSE_PATH = localpath + "level5.1/" + nullprefix + "MSE_median_full.txt";
                    prm.SDMSE_PATH = localpath + "level5.1/" + nullprefix + "MSE_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANMSE_PATH) || !doesFileExist(prm.SDMSE_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the MSE criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }
                if (criterion.contains("MSEC") && !criterion.toLowerCase().contains("MSECnonull".toLowerCase())) {
                    prm.MEANMSEC_PATH = localpath + "level5.1/" + nullprefix + "MSEC_median_full.txt";
                    prm.SDMSEC_PATH = localpath + "level5.1/" + nullprefix + "MSEC_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANMSEC_PATH) || !doesFileExist(prm.SDMSEC_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the MSEC criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }
                if (criterion.contains("MSER") && !criterion.toLowerCase().contains("MSERnonull".toLowerCase())) {
                    prm.MEANMSER_PATH = localpath + "level5.1/" + nullprefix + "MSER_median_full.txt";
                    prm.SDMSER_PATH = localpath + "level5.1/" + nullprefix + "MSER_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANMSER_PATH) || !doesFileExist(prm.SDMSER_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the MSER criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }
                if (criterion.contains("KENDALL") && !criterion.contains("KENDALLC") && !criterion.contains("KENDALLR") && !criterion.toLowerCase().contains("KENDALLnonull".toLowerCase())) {
                    prm.MEANKEND_PATH = localpath + "level5.1/" + nullprefix + "KENDALL_median_full.txt";
                    prm.SDKEND_PATH = localpath + "level5.1/" + nullprefix + "KENDALL_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANKEND_PATH) || !doesFileExist(prm.SDKEND_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the KENDALL criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }
                if (criterion.contains("KENDALLR") && !criterion.toLowerCase().contains("KENDALLRnonull".toLowerCase())) {
                    prm.MEANKENDR_PATH = localpath + "level5.1/" + nullprefix + "KENDALLR_median_full.txt";
                    prm.SDKENDR_PATH = localpath + "level5.1/" + nullprefix + "KENDALLR_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANKENDR_PATH) || !doesFileExist(prm.SDKENDR_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the KENDALLR criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }
                if (criterion.contains("KENDALLC") && !criterion.toLowerCase().contains("KENDALLCnonull".toLowerCase())) {
                    prm.MEANKENDC_PATH = localpath + "level5.1/" + nullprefix + "KENDALLC_median_full.txt";
                    prm.SDKENDC_PATH = localpath + "level5.1/" + nullprefix + "KENDALLC_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANKENDC_PATH) || !doesFileExist(prm.SDKENDC_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the KENDALLC criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }
                if (criterion.equalsIgnoreCase("MEAN") && !criterion.toLowerCase().contains("MEANnonull".toLowerCase())) {
                    prm.MEANMEAN_PATH = localpath + "level5.1/" + nullprefix + "MEAN_median_full.txt";
                    prm.SDMEAN_PATH = localpath + "level5.1/" + nullprefix + "MEAN_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANMEAN_PATH) || !doesFileExist(prm.SDMEAN_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the MEAN criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }

                if (criterion.contains("COR") && !criterion.contains("CORC") && !criterion.contains("CORR") && !criterion.toLowerCase().contains("CORnonull".toLowerCase())) {
                    prm.MEANCOR_PATH = localpath + "level5.1/" + nullprefix + "COR_median_full.txt";
                    prm.SDCOR_PATH = localpath + "level5.1/" + nullprefix + "COR_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANCOR_PATH) || !doesFileExist(prm.SDCOR_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the COR criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }
                if (criterion.contains("CORR") && !criterion.toLowerCase().contains("CORRnonull".toLowerCase())) {
                    prm.MEANCORR_PATH = localpath + "level5.1/" + nullprefix + "CORR_median_full.txt";
                    prm.SDCORR_PATH = localpath + "level5.1/" + nullprefix + "CORR_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANCORR_PATH) || !doesFileExist(prm.SDCORR_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the CORR criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }
                if (criterion.contains("CORC") && !criterion.toLowerCase().contains("CORCnonull".toLowerCase())) {
                    prm.MEANCORC_PATH = localpath + "level5.1/" + nullprefix + "CORC_median_full.txt";
                    prm.SDCORC_PATH = localpath + "level5.1/" + nullprefix + "CORC_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANCORC_PATH) || !doesFileExist(prm.SDCORC_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the CORC criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }

                if (criterion.contains("EUC") && !criterion.contains("EUCC") && !criterion.contains("EUCR") && !criterion.toLowerCase().contains("EUCnonull".toLowerCase())) {
                    prm.MEANEUC_PATH = localpath + "level5.1/" + nullprefix + "EUC_median_full.txt";
                    prm.SDEUC_PATH = localpath + "level5.1/" + nullprefix + "EUC_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANEUC_PATH) || !doesFileExist(prm.SDEUC_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the EUC criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }
                if (criterion.contains("EUCR") && !criterion.toLowerCase().contains("EUCCnonull".toLowerCase())) {
                    prm.MEANEUCR_PATH = localpath + "level5.1/" + nullprefix + "EUCR_median_full.txt";
                    prm.SDEUCR_PATH = localpath + "level5.1/" + nullprefix + "EUCR_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANEUCR_PATH) || !doesFileExist(prm.SDEUCR_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the EUCR criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }
                if (criterion.contains("EUCC") && !criterion.toLowerCase().contains("EUCRnonull".toLowerCase())) {
                    prm.MEANEUCC_PATH = localpath + "level5.1/" + nullprefix + "EUCC_median_full.txt";
                    prm.SDEUCC_PATH = localpath + "level5.1/" + nullprefix + "EUCC_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANEUCC_PATH) || !doesFileExist(prm.SDEUCC_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the EUCC criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }

                if (criterion.contains("SPEARMAN") && !criterion.contains("SPEARMANC") && !criterion.contains("SPEARMANR") && !criterion.toLowerCase().contains("SPEARnonull".toLowerCase())) {
                    prm.MEANSPEAR_PATH = localpath + "level5.1/" + nullprefix + "SPEARMAN_median_full.txt";
                    prm.SDSPEAR_PATH = localpath + "level5.1/" + nullprefix + "SPEARMANC_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANSPEAR_PATH) || !doesFileExist(prm.SDSPEAR_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the SPEARMAN criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }
                if (criterion.contains("SPEARMANR") && !criterion.toLowerCase().contains("SPEARMANRnonull".toLowerCase())) {
                    prm.MEANSPEARR_PATH = localpath + "level5.1/" + nullprefix + "SPEARMANR_median_full.txt";
                    prm.SDSPEARR_PATH = localpath + "level5.1/" + nullprefix + "SPEARMANR_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANSPEARR_PATH) || !doesFileExist(prm.SDSPEARR_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the SPEARMANR criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
                        System.exit(1);
                    }
                }
                if (criterion.contains("SPEARMANC") && !criterion.toLowerCase().contains("SPEARMANCnonull".toLowerCase())) {
                    prm.MEANSPEARC_PATH = localpath + "level5.1/" + nullprefix + "SPEARMANC_median_full.txt";
                    prm.SDSPEARC_PATH = localpath + "level5.1/" + nullprefix + "SPEARMANC_0.5IQR_full.txt";
                    if (!doesFileExist(prm.MEANSPEARC_PATH) || !doesFileExist(prm.SDSPEARC_PATH)) {
                        System.out.println("ERROR: The paths to the null files for the SPEARMANC criteria are incorrect. Please check that these null files exist in level5.1/\nExiting now...");
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


                if (!doesFileExist(refinement_starting_points)) {
                    System.out.println("ERROR: The refinement starting point biclusters file does not exist: " + refinement_starting_points + "\nExiting now...");
                    System.exit(1);
                }

                String argument[];
                argument = new String[]{"-param", prm_path, "-in", refinement_starting_points, "-out", output + basename,
                        "-samp", "" + 1, "-minsamp", "" + 0, "-top", "y", "-silent",
                        "t", "-crits", criterion};

                if (precriterion != null) {
                    argument = new String[]{"-param", prm_path, "-in", refinement_starting_points, "-out", output + basename,
                            "-samp", "" + 1, "-minsamp", "" + 0, "-top", "y", "-silent",
                            "t", "-crits", criterion, "-precrits", precriterion};
                }

                System.out.println("RUNNING: " + MoreArray.toString(argument, ","));

                CreateParamSet.main(argument);

                System.out.println("PARAMETERS CREATED!");

                setLevel += 1;

                File testdirf = new File(testoutput);
                System.out.println("\"" + output + "\": " + dir.length() + ", " +
                        "\"" + testoutput + "\": " + testdirf.length());
                long end = System.currentTimeMillis();
                System.out.println("LEVEL 15 COMPLETED: " + (end - start) / 1000.0 + " s");
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

                String task_file = basename + "_refine.tasks";
                String task_script = "";
                for (int it = 0; it < num_str_pt_refine; it++) {
                    task_script += "hostname >  " + localpath + subdir_1 + prm.OUTPREFIX + "_" + refinement_prefix + "_" + it + "__" + criterion.toUpperCase() + "__AG_" + MINER_STATIC.RANDOM_SEEDS[0] + "_host.$HT_TASK_ID.host; " +
                            "java  -Xmx" + (int) (mem_per_cpu * 1000.0) + "M DataMining.RunMiner ";
                    task_script += "-param " + localpath + input + prm.OUTPREFIX + "_" + refinement_prefix + "_" + it + "__" + criterion.toUpperCase() + "__AG_" + MINER_STATIC.RANDOM_SEEDS[0] + "__parameters.txt " +
                            "&> " + localpath + subdir_1 + prm.OUTPREFIX + "_" + refinement_prefix + "_" + it + "__" + criterion.toUpperCase() + "__AG_" + MINER_STATIC.RANDOM_SEEDS[0] + "_host.$HT_TASK_ID.out\n";
                }
                System.out.println("RunMiner task file written!");

                TextFile.write(task_script, scriptbox + task_file);

                long end = System.currentTimeMillis();
                System.out.println("LEVEL 16 COMPLETED: " + (end - start) / 1000.0 + " s");
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

                try {
                    run_ht_helper(localpath + input + basename + "_refine.tasks", scriptbox, default_walltime, null,
                            max_jobs, mem_per_cpu, false, (int) setLevel);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                long end = System.currentTimeMillis();
                System.out.println("LEVEL 17 COMPLETED: " + (end - start) / 1000.0 + " s");
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
                String cmd = "#!/bin/bash\n#SBATCH --partition=" + server + "\n" +
                        "#SBATCH --account=" + account + "\n" +
                        "#SBATCH --ntasks=1\n" +
                        "#SBATCH --time=120:00:00\n" +
                        "#SBATCH --output=MAKflow_" + setLevel + "_%j.out\n";
                if (!qos.equals(""))
                    cmd += "#SBATCH --qos=" + qos + "" + "\n";

                // E.g. yeast_cmonkey_refine_refinement_input_315__MSEC_KENDALLC_GEECE__AG_759820_out.txt
                String name_prefix = basename + "_refine_refinement_input_";
                String name_suffix = "__" + criterion + "__AG_" + MINER_STATIC.RANDOM_SEEDS[0] + "_out.txt";

                cmd += "java -Xmx" + (int) (mem_per_cpu * 1000.0) + "M DataMining.CollectRefinementResults -indir " + outfile_dir + " -name_prefix " + name_prefix + " -name_suffix " + name_suffix + " -outfile " + tmp_vbl + " &> " + localpath + scriptbox + "collect_partial.out";
                String run_collect_partial = "collect_partial.sl";
                TextFile.write(cmd, run_collect_partial);

                String task_shell_out = localpath + scriptbox + "out.txt";
                String cp_file = "run_cp.sh";
                String run_cp = "sbatch " + run_collect_partial + " &> " + task_shell_out;
                runCmd(run_cp, scriptbox, cp_file);

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
                            System.out.println("ERROR: SBATCH COLLECTPARTIAL SUBMISSION FAILED");
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

                String result = "";
                String[] header = new String[]{"#number", "block_area", "block_id", "move_type", "pre_criterion", "full_crit", "expr_mean_crit", "expr_mean_crit", "expr_reg_crit", "expr_kend_crit", "expr_cor_crit", "expr_euc_crit", "PPI_crit", "feat_crit", "TF_crit", "percent_orig_genes", "percent_orig_exp", "exp_mean", "trajectory_position", "FEATURE_INDICES", "move_class", "num_genes", "num_exps"};
                result += MoreArray.toString(header, "\t") + "\n";

                FileReader file_to_read_2 = null;
                try {
                    file_to_read_2 = new FileReader(tmp_vbl);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                BufferedReader bf2 = new BufferedReader(file_to_read_2);

                String aLine2 = null;
                int result_counter = 1;
                try {
                    while ((aLine2 = bf2.readLine()) != null) {
                        String[] as = aLine2.split("\t");
                        System.out.println(aLine2);
                        System.out.println(MoreArray.toString(Arrays.copyOfRange(as, 1, as.length)));
                        result += "" + result_counter + "\t" + MoreArray.toString(Arrays.copyOfRange(as, 1, as.length), "\t") + "\n"; // Check that this is working as should
                        result_counter++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    bf2.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                TextFile.write(result, output + result_vbl);

                File testdir = new File(testoutput);
                System.out.println("\"" + testdir + "\": " + testdir.length() + ", " +
                        "\"" + testoutput + "\": " + testdir.length());
                long end = System.currentTimeMillis();
                System.out.println("LEVEL 18 COMPLETED: " + (end - start) / 1000.0 + " s");
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
                ApplyCut.main(argument);

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
                System.out.println("LEVEL 19 COMPLETED: " + (end - start) / 1000.0 + " s");
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
                    System.out.println("ERROR: The example parameter file required for merging does not exist: " + localpath + scriptbox + "param_example.txt" + "\nExiting now...");
                    System.exit(1);
                }

                if (!doesFileExist(localpath + input + "results_" + basename + "_cut_scoreperc" + percent + ".0_exprNaN_0.0.txt")) {
                    System.out.println("ERROR: The ApplyCut result file does not exist: " + localpath + input + "results_" + basename + "_cut_scoreperc" + percent + ".0_exprNaN_0.0.txt" + "\nExiting now...");
                    System.exit(1);
                }

                String sl_lmm = scriptbox + "listmergemembers.sl";
                /*
                NOTE: Running LMM is hardcoded as a SLURM script.
                 */
                String listmergeMembers_script = "#!/bin/bash\n#SBATCH --partition=" + server + "\n" +
                        "#SBATCH --account=" + account + "\n" +
                        //"#SBATCH --constraint=jbei_m24\n" +
                        "#SBATCH --ntasks=1\n" +
                        "#SBATCH --output=MAKflow_" + setLevel + "_%j.out\n";

                if (!qos.equals(""))
                    listmergeMembers_script += "#SBATCH --qos=" + qos + "" + "\n";

                listmergeMembers_script += "java -Xmx" + (int) (mem_per_cpu * 1000.0) + "M DataMining.ListMergeMembersPreComputed -dir " + localpath + input + "results_" + basename + "_cut_scoreperc" + percent + ".0_exprNaN_0.0.txt" +
                        " -crit " + criterion + " -param " + localpath + scriptbox + "param_example.txt" + " -ocut 0.25 -misscut 1.0 -numgene 1000 " +
                        "-complete y -live n -out " + localpath + output + "results_" + basename + "_cut_scoreperc" + percent + ".0_exprNaN_0.0_ocut_0.25_misscut_1.0_reconstructed.txt &>" + localpath + output + "ListMergeMembers_0.25_0.out";
                TextFile.write(listmergeMembers_script, sl_lmm);

                String task_shell_out = localpath + scriptbox + "out.txt";
                String listmergeMembers_file = "run_lmm.sh";
                String run_lmm = "sbatch " + sl_lmm + " &> " + task_shell_out;
                runCmd(run_lmm, scriptbox, listmergeMembers_file);

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
                System.out.println("LEVEL 20 COMPLETED: " + (end - start) / 1000.0 + " s");
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
        String error_msg = "syntax: java DataMining.MAKflow\n" + arg_desc_str;

        if (options.get("-server") != null) {
            try {
                server = (String) options.get("-server");
                System.out.println("init server " + server);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (options.get("-account") != null) {
            try {
                account = (String) options.get("-account");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (options.get("-qos") != null) {
            try {
                qos = (String) options.get("-qos");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

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
                                        System.out.println("No file found " + f + " or the file is not in the accepted format.\nPlease refer to the example input file we have provided.\nExiting now ...");
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
                                    int length = criterion_split.length;
                                    System.out.println("init criterion_split " + length);
                                    for (int it = 0; it < length; it++) {
                                        crit = criterion_split[it];
                                        if (!possible_crits.contains(crit.toUpperCase())) {
                                            System.out.println(crit);
                                            System.out.println("ERROR: Unsupported criterion specified!\nExiting now ...");
                                            System.exit(1);
                                        }
                                        if (!crit.equalsIgnoreCase("")) {
                                            accepted_crits.add(crit.toUpperCase());
                                            if (crit.toLowerCase().indexOf("nonull") != -1)
                                                noNull = true;
                                        }
                                    }

                                    crit_list = accepted_crits.toArray(new String[0]);

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
                                    System.out.println("init criterion " + criterion);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (param_key.equalsIgnoreCase("precrit")) {
                                try {
                                    String tmp_criterion = param_val;
                                    precriterion_index = StringUtil.getFirstEqualsIndex(MINER_STATIC.CRIT_LABELS, tmp_criterion);
                                    num_precriterion = tmp_criterion.split("_").length;
                                    precriterion = null;
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
                                    int length = criterion_split.length;
                                    System.out.println("init precriterion_split " + length);

                                    for (int it = 0; it < length; it++) {
                                        crit = criterion_split[it];
                                        if (!possible_crits.contains(crit.toUpperCase())) {
                                            System.out.println(crit);
                                            System.out.println("ERROR: Unsupported precriterion specified!\nExiting now ...");
                                            System.exit(1);
                                        }
                                        if (!crit.equalsIgnoreCase("")) {
                                            accepted_crits.add(crit.toUpperCase());
                                            if (crit.toLowerCase().indexOf("nonull") != -1)
                                                noNull = true;
                                        }
                                    }

                                    precrit_list = accepted_crits.toArray(new String[0]);
                                    List<List<String>> crit_permutations = generatePerm(accepted_crits);
                                    for (List<String> crit_permutation : crit_permutations) {
                                        ArrayList<String> crit_permutation_ls = (ArrayList<String>) crit_permutation;
                                        String crit_permutation_string = StringUtil.join("_", crit_permutation_ls);
                                        crit_permutation_string = crit_permutation_string.substring(0, crit_permutation_string.length() - 1);
                                        System.out.println(crit_permutation_string);
                                        if (possible_crit_combos.contains(crit_permutation_string)) {
                                            precriterion = crit_permutation_string;
                                        }
                                    }
                                    if (precriterion.equalsIgnoreCase("")) {
                                        System.out.println("ERROR: Precriterion is invalid. Please check the input.\nExiting now ...");
                                        System.exit(1);
                                    }
                                    System.out.println("init precriterion " + precriterion);
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
                            } else if (param_key.equalsIgnoreCase("sizeprecritgene")) {
                                try {
                                    size_precrit_gene = Integer.parseInt(param_val);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (param_key.equalsIgnoreCase("sizeprecritexp")) {
                                try {
                                    size_precrit_exp = Integer.parseInt(param_val);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (param_key.equalsIgnoreCase("precritgeneperc")) {
                                try {
                                    precrit_gene_perc = Integer.parseInt(param_val);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (param_key.equalsIgnoreCase("precritexpperc")) {
                                try {
                                    precrit_exp_perc = Integer.parseInt(param_val);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (param_key.equalsIgnoreCase("maxjobs")) {
                                try {
                                    max_jobs = Integer.parseInt(param_val);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (param_key.equalsIgnoreCase("maxwalltime")) {
                                try {
                                    default_walltime = "" + Integer.parseInt(param_val) + ":00:00";
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
                            } else if (param_key.equalsIgnoreCase("mempercpu")) {
                                try {
                                    mem_per_cpu = Double.parseDouble(param_val);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (param_key.equalsIgnoreCase("nullmempercpu")) {
                                try {
                                    null_mem_per_cpu = Double.parseDouble(param_val);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (param_key.equalsIgnoreCase("hclmetric")) {
                                String test = param_val.toLowerCase();
                                int index = StringUtil.getFirstEqualsIndex(MINER_STATIC.HCL_DMETHODS, test);
                                if (index != -1) {
                                    try {
                                        hclmetric = param_val;
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    System.out.println("invalid HCL distance method given " + param_val);
                                }
                            } else if (param_key.equalsIgnoreCase("hcllink")) {
                                String test = param_val.toLowerCase();
                                int index = StringUtil.getFirstEqualsIndex(MINER_STATIC.HCL_LINKMETHODS, test);
                                if (index != -1) {
                                    try {
                                        hcllink = param_val;
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    System.out.println("invalid HCL linkage method given " + param_val);
                                }
                            } else if (param_key.equalsIgnoreCase("runmode")) {
                                try {
                                    runmode = param_val;
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
                            } else if (param_key.equalsIgnoreCase("runminer_param")) {
                                try {
                                    final String f = param_val;
                                    prm = new Parameters(f, true);
                                    prm_in_path = f;
                                    prm_path = f + "_run.txt";
                                } catch (Exception e) {
                                    System.out.println("ERROR: Path to runminer template parameter file: " + param_val + " is broken. Exiting now ...");
                                    e.printStackTrace();
                                    System.exit(1);
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
        if (num_row < Imax) {
            System.out.println("WARNING: IMAX were changed from the specified values.");
            Imax = num_row;
        }
        if (num_col < Jmax) {
            System.out.println("WARNING: JMAX were changed from the specified values.");
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
            if (criterion.indexOf("INTER") == -1) {
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


        if (account == null || server == null) {
            System.out.println("ERROR: account and/or server parameters are null. Account: " + account + "\taerver:" + server);
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

        //System.out.println("path " + scriptbox + scriptname);
        //System.out.println(cmd);

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
     * @throws java.io.IOException
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
     * @throws java.io.IOException
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
     * @throws java.io.IOException
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
     * @param constraint
     * @param ntasks
     * @param mempercpu
     * @param twoCoresPerTask
     * @throws IOException
     */
    private void run_ht_helper(String taskfile, String scriptbox, String walltime, String constraint, int ntasks,
                               double mempercpu, boolean twoCoresPerTask, int setLevel) throws IOException {
        String sl_header = "";

        sl_header += "#!/bin/bash\n" +
                "#SBATCH --partition=" + server + "\n" +
                "#SBATCH --account=" + account + "\n";

        if (mempercpu > 11) {
            sl_header += "#SBATCH --ntasks-per-node=1\n";
            sl_header += "#SBATCH --mem=" + (int) (mempercpu * 1000.0) + "M\n";
        } else {
            sl_header += "#SBATCH --cpus-per-task=1\n";
            sl_header += "#SBATCH --mem-per-cpu=" + (int) (mempercpu * 1000.0) + "M\n";
        }
        sl_header += "#SBATCH --time=" + walltime + "\n" +
                "#SBATCH --ntasks=" + ntasks + "\n" +
                "#SBATCH --output=MAKflow_" + setLevel + "_%j.out\n";

        if (!qos.equals(""))
            sl_header += "#SBATCH --qos=" + qos + "" + "\n";
        if (constraint != null)
            sl_header += "#SBATCH --constraint=" + constraint + "\n";

        String loc_dir_1 = localpath + scriptbox + "/sl_script/";
        System.out.println(loc_dir_1);
        createFile(loc_dir_1);

        String node_task = "";
        if (twoCoresPerTask) {
            node_task = sl_header + "ht_helper.sh -v  -t " + taskfile + " -n2";
        } else {
            node_task = sl_header + "ht_helper.sh -v  -t " + taskfile + " -n1";
        }

        String node_task_shell = loc_dir_1 + "slurm_script.sl";
        String task_shell_out = loc_dir_1 + "slurm_script.out";
        TextFile.write(node_task, node_task_shell);

        String run_sbatch = "";
        run_sbatch += "sbatch " + node_task_shell + " > " + task_shell_out;
        String sbatch_shell = "execute_sbatch.sh";
        runCmd(run_sbatch, scriptbox, sbatch_shell);

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
     * @throws java.io.IOException
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
                run_squeue += "squeue | grep '" + server + "' | awk '{print $1}' > " + squeue_file;//grep 'slurm_sc' |
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
                System.out.println("priorCheckReport " + priorCheckReport + "\tjobIsRunning " + jobIsRunning);
                System.out.println("time " + lastTime);
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
            MAKflow_JBEI_SLURM_v2 arg = new MAKflow_JBEI_SLURM_v2(args);
        } else {
            System.out.println("syntax: java DataMining.MAKflow_JBEI_SLURM_v2\n" + arg_desc_str
            );
        }
    }
}