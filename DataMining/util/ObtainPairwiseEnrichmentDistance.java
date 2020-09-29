package DataMining.util;

import mathy.SimpleMatrix;
import util.MapArgOptions;
import util.MoreArray;
import util.TextFile;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Created by rauf on 5/7/15.
 *
 * Performs a comparison for all bicluster pairs from set A and B which have an overlap measure past the specified threshold.
 */
public class ObtainPairwiseEnrichmentDistance {
    HashMap options;

    String [] bad_nodes = new String[]{"nodearea", "nodecriterion", "nodeGMT", "nodeGOval", "nodemeanexpr", "nodePathval", "nodeTFval", "nodeTIGRroleGO", "nodeTIGRroleGOPath", "nodeTIGRroleval", "nodeTIGRval", "nodeTFexpval", "nodeTFexp", null};

    String[] valid_args = {
            "-setA_dir", "-setB_dir", "-outfile", "-similarity_matrix", "-threshold"
    };

    String [] keys = new String[20];
    String setA_dir;
    String setB_dir;
    String outfile;
    SimpleMatrix similarity_matrix;
    double threshold;


    String outstring = "";

    HashMap noa_dict_a = new HashMap<String, String>();
    HashMap noa_dict_b = new HashMap<String, String>();

    HashMap<String, ArrayList> a_terms = new HashMap<String, ArrayList>();
    HashMap<String, ArrayList> b_terms = new HashMap<String, ArrayList>();

    public ObtainPairwiseEnrichmentDistance(String[] args) {
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
            outstring = "#bicluster_set_a\tbicluster_set_b\toverlap_value\tcommon_annotations\tunique_annotation_set_a\tunique_annotation_set_b\n";
            writeToOutstring();
            TextFile.write(outstring, outfile);


        } catch (Exception
                e) {
            e.printStackTrace();
            System.exit(0);
        }
        System.exit(0);
    }

    private void writeToOutstring() {
        int num_a_bics = similarity_matrix.data.length;
        int num_b_bics = similarity_matrix.data[0].length;
        System.out.println("GOT HERE");
        for (int i = 0; i < num_a_bics; i++) {
            String bic_a = similarity_matrix.ylabels[i];
            Collection<String> bic_a_annots = a_terms.get(bic_a);
            for (int j = 0; j < num_b_bics; j++) {
                double similarity_value = similarity_matrix.data[i][j];
                if (similarity_value >= threshold) {
                    String bic_b = similarity_matrix.xlabels[j];
                    System.out.println("A: " + bic_a + "\t" + "B: " + bic_b);
                    Collection<String> bic_b_annots = b_terms.get(bic_b);
                    //System.out.println(bic_b_annots);
                    //System.out.println(bic_a_annots);
                    Collection<String> tmp = new ArrayList();
                    for (String item : bic_b_annots) {
                        tmp.add(item);
                    }
                    tmp.retainAll(bic_a_annots);
                    //System.out.println(bic_b_annots);
                    //System.out.println(tmp);
                    //System.out.println("-------------");
                    String intersection = "";
                    String unique_to_a = "";
                    int count = 0;
                    for (String temp : bic_a_annots) {
                        if (!tmp.contains(temp) && temp.replaceAll("\\s+", "") != "" && temp != null) {
                            if (count == 0) {
                                unique_to_a += temp;
                            } else {
                                unique_to_a += "; " + temp;
                            }
                            count++;
                        }
                    }
                    count = 0;
                    String unique_to_b = "";
                    for (String temp : bic_b_annots) {
                        if (!tmp.contains(temp) && temp.replaceAll("\\s+","") != "" && temp != null) {
                            if (count == 0) {
                                unique_to_b += temp;
                            } else {
                                unique_to_b += "; " + temp;
                            }
                        }
                        count++;
                    }
                    count = 0;
                    for (String temp : tmp) {
                        if (temp.replaceAll("\\s+","") != "" && temp != null) {
                            if (count == 0) {
                                intersection += temp;
                            } else {
                                intersection += "; " + temp;
                            }
                        }
                        count++;
                    }
                    outstring += bic_a + "\t" + bic_b + "\t" + similarity_value + "\t" + intersection + "\t" + unique_to_a + "\t" + unique_to_b + "\n";
                }
            }
        }
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
                String bicluster = aLine.split("=")[0].replaceAll("\\s+","");
                ArrayList currentlist = a_terms.get(bicluster);
                if (currentlist == null) {
                    currentlist = new ArrayList();
                }
                String [] annotations = aLine.split("=")[1].split("_");
                for (int i = 0; i < annotations.length; i++) {
                    String annot = annotations[i].replaceAll("\\s+","").replace("GO:", "").replace("Path:", "").replace("TIGR:", "").replace("TIGRrole:", "");
                    currentlist.add(key + "_|_" + annot);
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
                String bicluster = bLine.split("=")[0].replaceAll("\\s+", "");
                ArrayList currentlist = b_terms.get(bicluster);
                if (currentlist == null) {
                    currentlist = new ArrayList();
                }
                String [] annotations = bLine.split("=")[1].split("_");
                for (int i = 0; i < annotations.length; i++) {
                    String annot = annotations[i].replaceAll("\\s+","").replace("GO:", "").replace("Path:", "").replace("TIGR:", "").replace("TIGRrole:", "");
                    currentlist.add(key + "_|_" + annot);
                }
                b_terms.put(bicluster, currentlist);
            }
            count ++;
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
            ObtainPairwiseEnrichmentDistance rm = new ObtainPairwiseEnrichmentDistance(args);
        } else {
            System.out.println("syntax: java DataMining.func.ObtainPairwiseEnrichmentDistance\n" +
                            "<-similarity_matrix>\n" +
                            "<-setA_dir>\n" +
                            "<-setB_dir>\n" +
                            "<-threshold>\n" +
                            "<-outfile>\n"
            );
        }
    }
}