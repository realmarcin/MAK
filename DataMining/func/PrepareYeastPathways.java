package DataMining.func;

import DataMining.BlockMethods;
import DataMining.Parameters;
import util.MoreArray;
import util.StringUtil;
import util.TabFile;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

/**
 * User: marcinjoachimiak
 * Date: Feb 22, 2008
 * Time: 1:08:16 PM
 */
public class PrepareYeastPathways {

    String parameter_template;
    String parameter_path = "/home/marcin/parameters/yeast_pathways/";
    String workdir = "/home/marcin/full";
    ArrayList pathway_names;
    ArrayList gene_data, pathways, paths;
    String[] gene_names;
    PrintWriter pw;

    int runs = 20;

    /**
     * @param args
     */
    public PrepareYeastPathways(String[] args) {

        gene_data = TabFile.readtoList(args[0], "\t");
        pathways = TabFile.readtoList(args[1], "\t");
        for (int i = 0; i < pathways.size(); i++) {
            ArrayList pathdata = (ArrayList) pathways.get(i);
            String pathids = MoreArray.arrayListtoString(pathdata);
            if (pathids.indexOf("(Pathway)") != -1) {
                pathways.remove(i);
                i--;
            }
        }
        ArrayList tmp_gene_names = TabFile.readtoList(args[2]);
        gene_names = new String[tmp_gene_names.size() - 1];
        //start at 1, skip header
        for (int i = 1; i < tmp_gene_names.size(); i++) {
            ArrayList cur = (ArrayList) tmp_gene_names.get(i);
            gene_names[i - 1] = (String) cur.get(0);
            gene_names[i - 1] = StringUtil.replace(gene_names[i - 1], "\"", "");
            System.out.println("gene_names " + i + "\t" + gene_names[i - 1]);
        }
        //System.out.println("gene_names");
        //MoreArray.printArray(gene_names);
        parameter_template = args[3];

        String lastpath = null;
        paths = new ArrayList();
        ArrayList thispath = new ArrayList();
        pathway_names = new ArrayList();

        removeSingleIds(gene_data);

        matchIds(gene_data, lastpath, thispath);

        createParamFiles();
    }

    /**
     */
    private void createParamFiles() {
        int count_smallpath = 0;
        int count_largepath = 0;
        int count_smallpath_mapped = 0;
        int count_not_mapped = 0;
        int count_largepath_mapped = 0;
        try {
            pw = new PrintWriter(new FileWriter("yeast_pathways.condor"), true);
            Parameters prm = new Parameters();
            prm.read(parameter_template);
            System.out.println("paths.bicluster_set_size() " + paths.size());
            String[] param_path = new String[paths.size()];
            for (int i = 0; i < paths.size(); i++) {
                String path_name = (String) pathway_names.get(i);
                System.out.println("path_name " + path_name);
                ArrayList cur = (ArrayList) paths.get(i);
                String genes = "";

                //if (cur.bicluster_set_size() > 4 && cur.bicluster_set_size() < 10) {
                for (int j = 0; j < cur.size(); j++) {
                    String gene = (String) cur.get(j);
                    System.out.println("searching for " + gene);
                    int index = MoreArray.getArrayInd(gene_names, gene);
                    if (index == -1) {
                        System.out.println("did not find id " + gene);
                    }

                    //System.out.println("index " + index + "\t" + gene);
                    String str = "" + index;
                    //int curind = genesStr.indexOf(str);
                    //System.out.println("curind " + curind + "\t" + gene + "\t" + genesStr);
                    if (index != -1) {// && curind == -1 && !genesStr.equals(str)
                        //&& genesStr.indexOf(str) != genesStr.length() - str.length()) {
                        genes += index + ",";
                        System.out.println("adding " + genes);
                    }
                }
                int count_mapped_genes = StringUtil.countOccur(genes, ",");
                //if (count_mapped_genes > 4 && count_mapped_genes < 10) {
                if (count_mapped_genes > 0) {
                    genes = genes.substring(0, genes.length() - 1);
                    System.out.println("genes " + genes);
                    String id = BlockMethods.IcJctoijID(prm.INIT_BLOCKS);
                    String exps = id.substring(id.indexOf("/"), id.length());
                    ArrayList[] coords = BlockMethods.ijIDtoIcJctoList(genes + exps);
                    prm.INIT_BLOCKS[0] = MoreArray.copyArrayList(coords[0]);
                    prm.INIT_BLOCKS[1] = MoreArray.copyArrayList(coords[1]);
                    System.out.println("create param genes " + genes + "\texps " + exps);
                    double frxn = (double) count_mapped_genes / (double) cur.size();
                    String frxnStr = "" + frxn;
                    frxnStr = frxnStr.substring(0, frxnStr.indexOf(".") + 2);
                    param_path[i] = "" + count_mapped_genes + "_" + frxnStr + "_" + path_name + "_parameters.txt";
                    prm.write(param_path[i]);

                    int gene_num = coords[0].size();
                    int perc10 = (int) ((double) gene_num / 10.0);

                    if (gene_num >= 5) {//perc10 > 1 &&
                        //leave each out                        
                        for (int l = 0; l < gene_num; l++) {
                            System.out.println(gene_num + "\t" + l);
                            prm.INIT_BLOCKS[0] = MoreArray.copyArrayList(coords[0]);
                            //System.out.println(gene_num + "\t" + prm.INIT_BLOCKS[0].bicluster_set_size() + "\t" + l);
                            String param_path_leaveone = "" + count_mapped_genes + "_" + frxnStr + "_" + path_name +
                                    "_leave1_" + l + "_parameters.txt";
                            prm.INIT_BLOCKS[0].remove(l);
                            prm.write(param_path_leaveone);
                        }

                        Random r = new Random();
                        //10 cases of dropping 10 percent of genesStr
                        for (int l = 0; l < gene_num; l++) {
                            prm.INIT_BLOCKS[0] = MoreArray.copyArrayList(coords[0]);
                            String param_path_leaveperc10 = "" + count_mapped_genes + "_" + frxnStr + "_" + path_name
                                    + "_leaveperc10_" + l + "_parameters.txt";
                            for (int m = 0; m < perc10; m++) {
                                int drop = r.nextInt(prm.INIT_BLOCKS[0].size());
                                //System.out.println(drop + "\t" + (drop - 1) + "\t" + (prm.INIT_BLOCKS[0].bicluster_set_size() + 1));
                                prm.INIT_BLOCKS[0].remove(drop);
                            }
                            prm.write(param_path_leaveperc10);
                        }
                        for (int l = 0; l < gene_num; l++) {
                            prm.INIT_BLOCKS[0] = MoreArray.copyArrayList(coords[0]);
                            String param_path_addperc10 = "" + count_mapped_genes + "_" + frxnStr + "_" + path_name
                                    + "_randomperc10_" + l + "_parameters.txt";
                            for (int m = 0; m < perc10; m++) {
                                int add = r.nextInt(gene_names.length - 1) + 1;
                                prm.INIT_BLOCKS[0].add(new Integer(add));
                            }
                            prm.write(param_path_addperc10);
                        }

                    }

                }
                if (cur.size() <= 4)
                    count_smallpath++;
                if (cur.size() >= 10)
                    count_largepath++;
                if (count_mapped_genes <= 4)
                    count_smallpath_mapped++;
                if (count_mapped_genes == 0)
                    count_not_mapped++;
                if (count_mapped_genes >= 10)
                    count_largepath_mapped++;
                //paths.remove(i);
                //i--;
            }

            for (int k = 0; k < runs; k++) {
                for (int i = 0; i < paths.size(); i++) {
                    if (param_path[i] != null) {
                        String path_name = (String) pathway_names.get(i);
                        makeScript(path_name, parameter_path + "/" + param_path[i], k);
                    }
                }
            }
            pw.close();
        }

        catch (IOException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("Small and large pathways excluded " + count_smallpath + "\t" + count_largepath);
        System.out.println("Small and large pathways excluded by mapped genes " +
                count_smallpath_mapped + "\t" + count_largepath_mapped);
        System.out.println("Pathways excluded by total mapping failure " + count_not_mapped);
    }

    /**
     * @param gene_data
     * @param lastpath
     * @param thispath
     */
    private void matchIds(ArrayList gene_data, String lastpath, ArrayList thispath) {
        int count = 0;
        for (int i = 0; i < pathways.size(); i++) {
            ArrayList pathdata = (ArrayList) pathways.get(i);
            String pathids = MoreArray.arrayListtoString(pathdata);
            System.out.println("matchIds pathids " + pathids);
            String curpath = (String) pathdata.get(0);
            System.out.println("matchIds curpath 0 " + curpath);
            if (lastpath == null)
                lastpath = curpath;
            if (!curpath.equals(lastpath)) {
                paths.add(thispath);
                lastpath = curpath;
                curpath = StringUtil.replace(curpath, " ", "_");
                curpath = StringUtil.replace(curpath, ",", "-");
                curpath = StringUtil.replace(curpath, "<i>", "");
                curpath = StringUtil.replace(curpath, "</i>", "");
                System.out.println("matchIds adding " + curpath);
                pathway_names.add(curpath);
                thispath = new ArrayList();
            }
            int end = pathdata.size() - 1;
            String one = (String) pathdata.get(end - 1);
            String two = (String) pathdata.get(end);

            if (one.length() != 4 && one.length() != 5) {
                one = two;
            }
            int ind = one.indexOf(",");
//truncate i(f comma delimited gene number list)
            if (ind != -1)
                one = one.substring(0, ind);
            System.out.println("matchIds Searching " + one);
            if ((one.length() == 4 || one.length() == 5) || one.indexOf("Y") == 0 || one.indexOf("SGD_REF:") != -1) {
                for (int j = 0; j < gene_data.size(); j++) {
                    ArrayList genedata = (ArrayList) gene_data.get(j);
                    int enddata = genedata.size() - 1;
                    String compare_gene_name_one = (String) genedata.get(0);
//String compare_gene_name_two = (String) genedata.get(enddata);
                    String yname = (String) genedata.get(enddata - 1);
                    String sname = (String) genedata.get(enddata);
/*if (one.equals(compare_gene_name_one)) {
System.out.println("Searching found match " + one + "\t" + compare_gene_name_one + "\t" + yname);
}*/
//System.out.println("Searching against " + one + "\t" + compare_gene_name_one + "\t" + yname);
//String sname = (String) genedata.get(enddata);
                    if (compare_gene_name_one != null && compare_gene_name_one.length() > 0 &&
                            (compare_gene_name_one.length() == 4 || compare_gene_name_one.length() == 5)) {
                        if (one.equals(compare_gene_name_one)) {
                            thispath.add(yname);
                            //System.out.println("matchIds compare_gene_name_one Adding " + one + "\t" + compare_gene_name_one + "\t" + yname + "\t" + +thispath.bicluster_set_size() + "\t" + pathdata.bicluster_set_size());
                            count++;
                        }
                    } else if (yname != null && yname.length() > 0 && yname.length() == 7) {
                        if (one.equals(compare_gene_name_one)) {
                            thispath.add(yname);
                            //System.out.println("matchIds yname Adding " + one + "\t" + compare_gene_name_one + "\t" + yname + "\t" + thispath.bicluster_set_size() + "\t" + pathdata.bicluster_set_size());
                            count++;
                        } /*else {
                            System.out.println("matchIds No match id: one.equals(compare_gene_name_one)" + one + "\t" + yname);
                        }*//*else if (one.equals(compare_gene_name_two)) {}
                            thispath.add(yname);
                            System.out.println("Adding " + yname);
                        }*/
                    } else if (sname != null && sname.length() > 0 && sname.length() == 10) {
                        int startthis = one.indexOf("SGD_REF:");
                        if (startthis != -1) { //(sname)) {
                            int endthis = one.indexOf("|", startthis + 1);
                            if (endthis == -1)
                                endthis = one.length();
                            one = one.substring(startthis + 8, endthis);
                            if (one.equals(sname)) {
                                thispath.add(yname);
                                //System.out.println("matchIds sname Adding " + one + "\t" + compare_gene_name_one + "\t" + yname + "\t" + pathdata.bicluster_set_size());
                                count++;
                            }
                        }
                    }
                }
            } else {
                System.out.println("matchIds No match id: one.length() == 4 || one.length() == 5, one: " + one);
            }

        }
        System.out.println("matchIds Identified " + count + " yeast genes in pathways from " +
                pathways.size() + ". Total pathways " + paths.size());
    }

    /**
     * @param gene_data
     */
    private void removeSingleIds(ArrayList gene_data) {
        System.out.println("gene_data " + gene_data.size());
        for (int j = 0; j < gene_data.size(); j++) {
            ArrayList genedata = (ArrayList) gene_data.get(j);
            if (genedata.size() == 1) {
                gene_data.remove(j);
                j--;
            }
        }
        System.out.println("gene_data " + gene_data.size());
    }

    /**
     * @param pathway
     * @param paramfile
     */
    private void makeScript(String pathway, String paramfile, int index) {
        pw.println("Universe = vanilla");
        pw.println("Environment = LD_LIBRARY_PATH=/usr/local/lib64/R/lib/:/usr/local/gtl/bin/JRI/;" +
                "CLASSPATH=/usr/local/gtl/bin/JRI/src/:/home/kkeller/downloads/jri-ex/:/home/marcin/classes;" +
                "R_HOME=/usr/local/lib64/R/");
        pw.println("Executable = /usr/local/gtl/bin/java/bin/java");
        pw.println("Arguments = DataMining.RunMiner -param " + paramfile + "");
        pw.println("output = RunMiner_" + pathway + "_" + index + ".out");
        pw.println("error = RunMiner_" + pathway + "_" + index + ".error");
        pw.println("Log = RunMiner_" + pathway + "_" + index + ".log");
        pw.println("should_transfer_files = NO");
        pw.println("Requirements = (Arch == \"x86_64\") || (Arch == \"INTEL\")");
        pw.println("Initialdir = " + workdir);
        pw.println("Queue");
        pw.println("");
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 4) {
            PrepareYeastPathways rm = new PrepareYeastPathways(args);
        } else {
            System.out.println("syntax: java DataMining.func.PrepareYeastPathways\n" +
                    "<gene data>\n" +
                    "<biochemical_pathways>\n" +
                    "<gene names>\n" +
                    "<parameter template>");
        }
    }

}
