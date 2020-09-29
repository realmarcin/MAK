package DataMining.util;

import util.MapArgOptions;
import util.MoreArray;
import util.TextFile;

import java.io.*;
import java.util.HashMap;

/**
 * Created by rauf on 5/7/15.
 */
public class SummarizeBuildGraphResults {
    HashMap options;

    String [] bad_nodes = new String[]{"nodearea", "nodecriterion", "nodeGMT", "nodeGOval", "nodemeanexpr", "nodePathval", "nodeTFval", "nodeTIGRroleGO", "nodeTIGRroleGOPath", "nodeTIGRroleval", "nodeTIGRval", "nodeTFexpval", null};

    String[] valid_args = {
            "-setA_dir", "-setB_dir", "-outfile"
    };

    String [] keys = new String[20];
    String setA_dir;
    String setB_dir;
    String outfile;

    String outstring = "";

    HashMap noa_dict_a = new HashMap<String, String>();
    HashMap noa_dict_b = new HashMap<String, String>();

    public SummarizeBuildGraphResults(String[] args) {
        try {
            init(args);

            getFileMap(setA_dir, noa_dict_a);
            getFileMap(setB_dir, noa_dict_b);

            outstring = "#noa\tannotation\tset_a_count\tset_b_count\n";
            for (int i = 0; i < keys.length; i++) {
                String key = keys[i];
                String fileA = (String) noa_dict_a.get(key);
                String fileB = (String) noa_dict_b.get(key);
                if (key != null && fileA != null && fileB != null ) {
                    writeTableForNode(fileA, fileB, key);
                }
            }

            TextFile.write(outstring, outfile);


        } catch (Exception
                e) {
            e.printStackTrace();
            System.exit(0);
        }
        System.exit(0);
    }

    private void writeTableForNode(String fileA, String fileB, String key) throws IOException {
        String [] keywords = new String[10000];
        HashMap map_a = new HashMap<String, Integer>();
        HashMap map_b = new HashMap<String, Integer>();

        int kw_count = 0;
        System.out.println(key);
        System.out.println(fileA);
        System.out.println(fileB);
        System.out.println("--------------------------------");
        FileReader file_a = new FileReader(fileA);
        BufferedReader bf_a = new BufferedReader(file_a);
        String aLine;
        int count = 0;
        while ((aLine = bf_a.readLine()) != null) {
            if (count > 0) {
                String [] annotations = aLine.split("=")[1].split("_");
                for (int i = 0; i < annotations.length; i++) {
                    String annot = annotations[i].replaceAll("\\s+","").replace("GO:", "").replace("Path:", "").replace("TIGR:", "").replace("TIGRrole:", "");
                    int ac = 1;
                    if (map_a.get(annot) != null) {
                        int tmp = (Integer) map_a.get(annot);
                        ac = tmp + 1;
                    } else {
                        keywords[kw_count] = annot;
                        kw_count += 1;
                    }
                    map_a.put(annot, ac);
                }
            }
            count ++;
        }

        FileReader file_b = new FileReader(fileB);
        BufferedReader bf_b = new BufferedReader(file_b);
        String bLine;
        count = 0;
        while ((bLine = bf_b.readLine()) != null) {
            if (count > 0) {
                String [] annotations = bLine.split("=")[1].split("_");
                for (int i = 0; i < annotations.length; i++) {
                    String annot = annotations[i].replaceAll("\\s+","").replace("GO:", "").replace("Path:", "").replace("TIGR:", "").replace("TIGRrole:", "");
                    int ac = 1;
                    if (map_b.get(annot) != null) {
                        int tmp = (Integer) map_b.get(annot);
                        ac = tmp + 1;
                    } else {
                        boolean flag = false;
                        for (int j=0; j < keywords.length; j++) {
                            if (annot.equalsIgnoreCase(keywords[j])) {
                                flag = true;
                            }
                        }
                        if (!flag) {
                            keywords[kw_count] = annot;
                            kw_count += 1;
                        }
                    }
                    map_b.put(annot, ac);
                }
            }
            count ++;
        }

        for (int k = 0; k < keywords.length; k++) {
            String annotation = keywords[k];
            int acount = 0;
            int bcount = 0;
            if (map_a.get(annotation) != null) {
                acount = (Integer) map_a.get(annotation);
            }
            if (map_b.get(annotation) != null) {
                bcount = (Integer) map_b.get(annotation);
            }
            if (annotation != null && !(acount == 0 && bcount == 0)) {
                outstring += key + "\t" + annotation + "\t" + acount + "\t" + bcount + "\n";
            }
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
    }

    public static void main(String[] args) {
        System.out.println(args);
        if (args.length >= 2) {
            SummarizeBuildGraphResults rm = new SummarizeBuildGraphResults(args);
        } else {
            System.out.println("syntax: java DataMining.func.SummarizeBuildGraphResults\n" +
                            "<-setA_dir>\n" +
                            "<-setB_dir>\n" +
                            "<-outfile>\n"
            );
        }
    }
}