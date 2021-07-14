package DataMining.util;

import DataMining.ValueBlock;
import DataMining.ValueBlockList;
import DataMining.ValueBlockListMethods;
import mathy.SimpleMatrix;
import util.MapArgOptions;
import util.MoreArray;
import util.ParsePath;
import util.TextFile;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by rauf on 5/7/15.
 *
 *  Performs a comparison between biclusters in set A and their corresponding best overlapping bicluster in set B and visa versa.
 *
 */
public class PairwiseOverlapEnrichmentAnalysis {
    HashMap options;

    String [] bad_nodes = new String[]{"nodearea", "nodecriterion", "nodeGMT", "nodeGOval", "nodemeanexpr", "nodePathval", "nodeTFval", "nodeTIGRroleGO", "nodeTIGRroleGOPath", "nodeTIGRroleval", "nodeTIGRval", "nodeTFexpval", "nodeTFexp", null};
    String [] valid_nodes = {"funclass", "nodePath", "nodeGO", "nodeTF", "nodeTIGRrole", "nodeTIGR", "localize"};

    String[] valid_args = {
            "-setA_dir", "-setB_dir", "-outfile", "-similarity_matrix", "-threshold", "-setA_vbl", "-setB_vbl"
    };

    ValueBlockList setA_vbl;
    ValueBlockList setB_vbl;

    int A_vbl_size;
    int B_vbl_size;

    String [] keys = new String[20];
    String setA_dir;
    String setB_dir;
    String outfile;
    SimpleMatrix similarity_matrix;
    double threshold;

    String outstring = "";

    HashMap noa_dict_a = new HashMap<String, String>();
    HashMap noa_dict_b = new HashMap<String, String>();

    HashMap max_a_to_b = new HashMap<String, String>();
    HashMap max_b_to_a = new HashMap<String, String>();

    HashMap a_gene_len = new HashMap<String, Integer>();
    HashMap a_exp_len = new HashMap<String, Integer>();
    HashMap b_gene_len = new HashMap<String, Integer>();
    HashMap b_exp_len = new HashMap<String, Integer>();

    HashMap<String, ArrayList> a_terms = new HashMap<String, ArrayList>();
    HashMap<String, ArrayList> b_terms = new HashMap<String, ArrayList>();

    public PairwiseOverlapEnrichmentAnalysis(String[] args) {
        try {
            init(args);

            getFileMap(setA_dir, noa_dict_a);
            getFileMap(setB_dir, noa_dict_b);

            for (int i = 0; i < keys.length; i++) {
                String key = keys[i];
                String fileA = (String) noa_dict_a.get(key);
                String fileB = (String) noa_dict_b.get(key);
                if (key != null && fileA != null && fileB != null ) {
                    storeBiclusterToAnnotInfo(fileA, fileB, key);
                }
            }

            getGeneExpLengthMap(a_gene_len, a_exp_len, setA_vbl, A_vbl_size);
            getGeneExpLengthMap(b_gene_len, b_exp_len, setB_vbl, B_vbl_size);

            findMaxOverlapForSetABiclusters();
            findMaxOverlapForSetBBiclusters();

            outstring = "#bicluster_set_a\tbicluster_set_b\toverlap_value\tnum_genes_a\tnum_exps_a\tnum_genes_b\tnum_exps_b";
            for (int i = 0; i < valid_nodes.length; i++) {
                String node = valid_nodes[i];
                outstring += "\t" + node + "_common\t" + node + "_unique_to_a\t" + node + "_unique_to_b";
            }
            outstring += "\n";

            writeToOutstringMaxA();
            writeToOutstringMaxB();

            TextFile.write(outstring, outfile);


        } catch (Exception
                e) {
            e.printStackTrace();
            System.exit(0);
        }
        System.exit(0);
    }

    private void findMaxOverlapForSetABiclusters() {
        int num_a_bics = similarity_matrix.data.length;
        int num_b_bics = similarity_matrix.data[0].length;
        for (int i = 0; i < num_a_bics; i++) {
            String bic_a = similarity_matrix.ylabels[i];
            double max_overlap = 0;
            String overlapping_bic = "Na";
            for (int j = 0; j < num_b_bics; j++) {
                double similarity_value = similarity_matrix.data[i][j];
                if (similarity_value > max_overlap && similarity_value >= threshold) {
                    max_overlap = similarity_value;
                    overlapping_bic = similarity_matrix.xlabels[j];
                }
            }
            max_a_to_b.put(bic_a, overlapping_bic);
        }
    }

    private void findMaxOverlapForSetBBiclusters() {
        int num_a_bics = similarity_matrix.data.length;
        int num_b_bics = similarity_matrix.data[0].length;
        for (int j = 0; j < num_b_bics; j++) {
            String bic_b = similarity_matrix.xlabels[j];
            double max_overlap = 0;
            String overlapping_bic = "Na";
            for (int i = 0; i < num_a_bics; i++) {
                double similarity_value = similarity_matrix.data[i][j];
                if (similarity_value > max_overlap && similarity_value >= threshold) {
                    max_overlap = similarity_value;
                    overlapping_bic = similarity_matrix.ylabels[i];
                }
            }
            max_b_to_a.put(bic_b, overlapping_bic);
        }
    }


    private void writeToOutstringMaxA() {
        int num_a_bics = similarity_matrix.data.length;
        int num_b_bics = similarity_matrix.data[0].length;
        System.out.println("GOT HERE");
        for (int i = 0; i < num_a_bics; i++) {
            String bic_a = similarity_matrix.ylabels[i];
            String overlap_b = (String) max_a_to_b.get(bic_a);
            Collection<String> bic_a_annots = a_terms.get(bic_a);
            for (int j = 0; j < num_b_bics; j++) {
                String bic_b = similarity_matrix.xlabels[j];
                if (overlap_b.equalsIgnoreCase(bic_b)) {
                    double similarity_value = similarity_matrix.data[i][j];
                    System.out.println("A: " + bic_a + "\t" + "B: " + bic_b);
                    Collection<String> bic_b_annots = b_terms.get(bic_b);
                    Collection<String> tmp = new ArrayList();
                    for (String item : bic_b_annots) {
                        tmp.add(item);
                    }
                    tmp.retainAll(bic_a_annots);

                    int a_glen = (Integer) a_gene_len.get(bic_a);
                    int a_elen = (Integer) a_exp_len.get(bic_a);
                    int b_glen = (Integer) b_gene_len.get(bic_b);
                    int b_elen = (Integer) b_exp_len.get(bic_b);

                    outstring += bic_a + "\t" + bic_b + "\t" + similarity_value + "\t" +
                                 a_glen + "\t" + a_elen + "\t" + b_glen + "\t" + b_elen;

                    for (int n = 0; n < valid_nodes.length; n++) {
                        String node = valid_nodes[n];

                        int count = 0;
                        ArrayList<String> intersection_al = new ArrayList<String>();
                        for (String temp : tmp) {
                            if (temp.trim() != "" && temp != null) {
                                if (temp.startsWith(node)) {
                                    intersection_al.add(temp.split("_\\|_")[1]);
                                    count++;
                                }
                            }
                        }

                        String intersection = "NA";
                        if (count > 0) {
                            intersection = joinStrings(intersection_al);
                        }

                        count = 0;
                        ArrayList<String> uniqueA_al = new ArrayList<String>();
                        for (String temp : bic_a_annots) {
                            if (!tmp.contains(temp) && temp.trim() != "" && temp != null) {
                                if (temp.startsWith(node)) {
                                    uniqueA_al.add(temp.split("_\\|_")[1]);
                                    count++;
                                }
                            }
                        }

                        String uniqueA = "NA";
                        if (count > 0) {
                            uniqueA = joinStrings(uniqueA_al);
                        }

                        count = 0;
                        ArrayList<String> uniqueB_al = new ArrayList<String>();
                        for (String temp : bic_b_annots) {
                            if (!tmp.contains(temp) && temp.trim() != "" && temp != null) {
                                if (temp.startsWith(node)) {
                                    uniqueB_al.add(temp.split("_\\|_")[1]);
                                    count++;
                                }
                            }
                        }

                        String uniqueB = "NA";
                        if (count > 0) {
                            uniqueB = joinStrings(uniqueB_al);
                        }

                        outstring += "\t" + intersection + "\t" + uniqueA + "\t" + uniqueB;

                    }
                    outstring += "\n";
                }
            }
        }
    }

    private void writeToOutstringMaxB() {
        int num_a_bics = similarity_matrix.data.length;
        int num_b_bics = similarity_matrix.data[0].length;
        System.out.println("GOT HERE");
        for (int j = 0; j < num_b_bics; j++) {
            String bic_b = similarity_matrix.xlabels[j];
            String overlap_a = (String) max_b_to_a.get(bic_b);
            Collection<String> bic_b_annots = b_terms.get(bic_b);
            for (int i = 0; i < num_a_bics; i++) {
                String bic_a = similarity_matrix.ylabels[i];
                if (overlap_a.equalsIgnoreCase(bic_a)) {
                    double similarity_value = similarity_matrix.data[i][j];
                    System.out.println("A: " + bic_a + "\t" + "B: " + bic_b);
                    Collection<String> bic_a_annots = a_terms.get(bic_a);
                    Collection<String> tmp = new ArrayList();
                    for (String item : bic_b_annots) {
                        tmp.add(item);
                    }
                    tmp.retainAll(bic_a_annots);

                    int a_glen = (Integer) a_gene_len.get(bic_a);
                    int a_elen = (Integer) a_exp_len.get(bic_a);
                    int b_glen = (Integer) b_gene_len.get(bic_b);
                    int b_elen = (Integer) b_exp_len.get(bic_b);

                    outstring += bic_a + "\t" + bic_b + "\t" + similarity_value + "\t" +
                            a_glen + "\t" + a_elen + "\t" + b_glen + "\t" + b_elen;

                    for (int n = 0; n < valid_nodes.length; n++) {
                        String node = valid_nodes[n];

                        int count = 0;
                        ArrayList<String> intersection_al = new ArrayList<String>();
                        for (String temp : tmp) {
                            if (!tmp.contains(temp) && temp.trim() != "" && temp != null) {
                                if (temp.startsWith(node)) {
                                    intersection_al.add(temp.split("_\\|_")[1]);
                                    count++;
                                }
                            }
                        }

                        String intersection = "NA";
                        if (count > 0) {
                            intersection = joinStrings(intersection_al);
                        }

                        count = 0;
                        ArrayList<String> uniqueA_al = new ArrayList<String>();
                        for (String temp : bic_a_annots) {
                            if (temp.trim() != "" && temp != null) {
                                if (temp.startsWith(node)) {
                                    uniqueA_al.add(temp.split("_\\|_")[1]);
                                    count++;
                                }
                            }
                        }

                        String uniqueA = "NA";
                        if (count > 0) {
                            uniqueA = joinStrings(uniqueA_al);
                        }

                        count = 0;
                        ArrayList<String> uniqueB_al = new ArrayList<String>();
                        for (String temp : bic_b_annots) {
                            if (!tmp.contains(temp) && temp.trim() != "" && temp != null) {
                                if (temp.startsWith(node)) {
                                    uniqueB_al.add(temp.split("_\\|_")[1]);
                                    count++;
                                }
                            }
                        }

                        String uniqueB = "NA";
                        if (count > 0) {
                            uniqueB = joinStrings(uniqueB_al);
                        }

                        outstring += "\t" + intersection + "\t" + uniqueA + "\t" + uniqueB;

                    }
                    outstring += "\n";
                }
            }
        }
    }

    private String joinStrings(ArrayList<String> arrayList) {
        String result = "";
        int count = 0;
        for (String str : arrayList) {
            if (str.trim() != "" && str != null) {
                if (count == 0) {
                    result += str;
                } else {
                    result += "; " + str;
                }
                count++;
            }
        }
        return result;
    }


    private void storeBiclusterToAnnotInfo(String fileA, String fileB, String key) throws IOException {

        //System.out.println(key);
        //System.out.println(fileA);
        //System.out.println(fileB);
        //System.out.println("--------------------------------");
        FileReader file_a = new FileReader(fileA);
        BufferedReader bf_a = new BufferedReader(file_a);
        String aLine;
        int count = 0;
        while ((aLine = bf_a.readLine()) != null) {
            if (count > 0) {
                String bicluster = aLine.split("=")[0].trim();
                ArrayList currentlist = a_terms.get(bicluster);
                if (currentlist == null) {
                    currentlist = new ArrayList();
                }
                String [] annotations = aLine.split("=")[1].split("_");
                for (int i = 0; i < annotations.length; i++) {
                    String annot = annotations[i].trim().replace("GO:", "").replace("Path:", "").replace("TIGR:", "").replace("TIGRrole:", "");
                    if (!annot.trim().equalsIgnoreCase("null") && !annot.trim().equalsIgnoreCase("none")) {
                        currentlist.add(key + "_|_" + annot.toLowerCase());
                    }
                }
                a_terms.put(bicluster, currentlist);
            }
            count ++;
        }

        FileReader file_b = new FileReader(fileB);
        BufferedReader bf_b = new BufferedReader(file_b);
        String bLine;
        count = 0;
        while ((bLine = bf_b.readLine()) != null) {
            if (count > 0) {
                String bicluster = bLine.split("=")[0].trim();
                ArrayList currentlist = b_terms.get(bicluster);
                if (currentlist == null) {
                    currentlist = new ArrayList();
                }
                String [] annotations = bLine.split("=")[1].split("_");
                for (int i = 0; i < annotations.length; i++) {
                    String annot = annotations[i].trim().replace("GO:", "").replace("Path:", "").replace("TIGR:", "").replace("TIGRrole:", "");
                    if (!annot.trim().equalsIgnoreCase("null")  && !annot.trim().equalsIgnoreCase("none")) {
                        currentlist.add(key + "_|_" + annot.toLowerCase());
                    }
                }
                b_terms.put(bicluster, currentlist);
            }
            count ++;
        }
    }

    private void getGeneExpLengthMap(HashMap gene_length, HashMap exp_length, ValueBlockList vbl, int vbl_size) {
        for (int i = 0; i < vbl_size; i++) {
            ValueBlock v = (ValueBlock) vbl.get(i);
            int id = v.index;
            int glen = v.genes.length;
            int elen = v.exps.length;
            System.out.println(id);
            gene_length.put("" + id, glen);
            exp_length.put("" + id, elen);
        }
    }


    private void getFileMap(String indir, HashMap dictionary) {
        File directory = new File(indir);
        File[] myarray;
        myarray=new File[10];
        myarray = directory.listFiles();
        int count = 0;
        for (int i = 0; i < myarray.length; i++) {
            File curr_file = myarray[i];
            String [] cs1 = ("" + curr_file).split("/");
            String curr_file_name = ("" + curr_file).split("/")[cs1.length-1];
            String [] cs2 = (curr_file_name).split("_");
            if (curr_file_name.endsWith(".noa")) {
                String key = curr_file_name.split("_")[cs2.length-1].split("\\.")[0];
                boolean flag = false;
                for (int j=0; j < bad_nodes.length; j++) {
                    if (key.equalsIgnoreCase(bad_nodes[j])) {
                        flag = true;
                    }
                }
                if (!flag) {
                    String val = "" + curr_file;
                    dictionary.put(key, val);
                    keys[count] = key;
                    count += 1;
                }
            }
        }
    }

    private File createFile(String path) {
        File dir = new File(path);
        boolean testdir = dir.mkdir();
        //if (!testdir) {
        //   System.out.println("mkdir failed " + path);
        //   System.exit(0);
        //}
        return dir;
    }

    private void init(String[] args) {

        MoreArray.printArray(args);
        options = MapArgOptions.maptoMap(args, valid_args);

        if (options.get("-outfile") != null) {
            outfile = (String) options.get("-outfile");
        }

        if (options.get("-setA_dir") != null) {
            setA_dir = (String) options.get("-setA_dir");
            if (!setA_dir.endsWith("/")) {
                setA_dir += "/";
            }
        }

        if (options.get("-setB_dir") != null) {
            setB_dir = (String) options.get("-setB_dir");
            if (!setB_dir.endsWith("/")) {
                setB_dir += "/";
            }
        }

        if (options.get("-setA_vbl") != null) {
            String setA_vbl_path = (String) options.get("-setA_vbl");
            try {
                setA_vbl = ValueBlockListMethods.readAny(setA_vbl_path, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            A_vbl_size = setA_vbl.size();
        }

        if (options.get("-setB_vbl") != null) {
            String setB_vbl_path = (String) options.get("-setB_vbl");
            try {
                setB_vbl = ValueBlockListMethods.readAny(setB_vbl_path, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            B_vbl_size = setB_vbl.size();
        }

        if (options.get("-similarity_matrix") != null) {
            similarity_matrix = new SimpleMatrix((String) options.get("-similarity_matrix"));
        }

        if (options.get("-threshold") != null) {
            threshold = Double.parseDouble((String) options.get("-threshold"));
        }
    }

    public static void main(String[] args) {
        System.out.println(args);
        if (args.length >= 5) {
            PairwiseOverlapEnrichmentAnalysis rm = new PairwiseOverlapEnrichmentAnalysis(args);
        } else {
            System.out.println("syntax: java DataMining.func.PairwiseOVerlapEnrichmentAnalysis\n" +
                            "<-similarity_matrix>\n" +
                            "<-setA_dir>\n" +
                            "<-setB_dir>\n" +
                            "<-setA_vbl>\n" +
                            "<-setB_vbl>\n" +
                            "<-threshold>\n" +
                            "<-outfile>\n"
            );
        }
    }
}