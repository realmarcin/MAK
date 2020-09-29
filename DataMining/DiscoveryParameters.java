package DataMining;

import util.MoreArray;
import util.StringUtil;
import util.TextFile;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: 12/18/13
 * Time: 12:17 AM
 */
public class DiscoveryParameters {

    //DvH example parameters
    public String null_data_path = "/global/homes/m/marcinj/common/DvH_select_GEECE_median_full.txt";
    public String taxon = "882";
    public int rounds = 2;
    public boolean refine = true;
    public String[] move_sequences = {"BS", "BS", "N"};
    public double min_raw_bicluster_score = 0.9971357501902345;
    public double max_bicluster_overlap = 0.25;
    public double max_enrich_pvalue = 0.001;
    public String linkage = "complete";
    public String final_bicluster_type = "expression";

    /**
     */
    public DiscoveryParameters() {
    }

    /**
     * @param args
     */
    public DiscoveryParameters(String[] args) {

        writeDefault(args[0]);
    }

    /**
     *
     */
    public void writeDefault(String outfile) {
        try {
            //System.out.println("writing " + outfile);
            PrintWriter pw = new PrintWriter(new FileWriter(outfile));

            pw.println("null_data_path = " + null_data_path + " #Example path to null distribution file");
            pw.println("taxon = " + taxon + " #NCBI taxonomy id");
            pw.println("rounds = " + rounds + " #Number of initial discovery rounds");
            pw.println("refine = " + (refine ? "y" : "n") + " #Do refinement after initial discovery?");
            pw.println("move_sequences = " + MoreArray.toString(move_sequences, ",") + " #Move sequences for initial and refinement stages (if any)");
            pw.println("min_raw_bicluster_score = " + min_raw_bicluster_score + " #Minimum considered bicluster score");
            pw.println("max_bicluster_overlap = " + max_bicluster_overlap + " #Maximum bicluster overlap");
            pw.println("max_enrich_pvalue = " + max_enrich_pvalue + " #Maximum considered enrichment p-value");
            pw.println("linkage = " + linkage + " #Linkage type for bicluster merging");
            pw.println("final_bicluster_type = " + final_bicluster_type + " #Type of bicluster");

            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param file
     */
    public void read(String file) {

        ArrayList data = TextFile.readtoList(file);

        for (int i = 0; i < data.size(); i++) {
            String cur = (String) data.get(i);
            if (cur.indexOf("#") != 0) {
                int startInd = cur.indexOf("=") + 1;
                int endInd = cur.indexOf("#", startInd);
                if (startInd != -1 && endInd != -1) {
                    String extractStr = cur.substring(startInd, endInd);
                    extractStr = StringUtil.replace(extractStr, " ", "");
                    if (extractStr.length() > 0) {
                        if (cur.matches("(?i)null_data_path =.*")) {
                            null_data_path = extractStr;
                            System.out.println("Parameters read null_data_path " + null_data_path);
                        } else if (cur.matches("(?i)taxon =.*")) {
                            taxon = extractStr;
                        }else if (cur.matches("(?i)rounds =.*")) {
                            rounds = Integer.parseInt(extractStr);
                        } else if (cur.matches("(?i)refine =.*")) {
                            refine = extractStr.equalsIgnoreCase("y") ? true : false;
                        } else if (cur.matches("(?i)move_sequences =.*")) {
                            move_sequences = extractStr.split(",");
                        } else if (cur.matches("(?i)min_raw_bicluster_score =.*")) {
                            min_raw_bicluster_score = Double.parseDouble(extractStr);
                        } else if (cur.matches("(?i)max_bicluster_overlap =.*")) {
                            max_bicluster_overlap = Double.parseDouble(extractStr);
                        } else if (cur.matches("(?i)max_enrich_pvalue =.*")) {
                            max_enrich_pvalue = Double.parseDouble(extractStr);
                        } else if (cur.matches("(?i)linkage =.*")) {
                            linkage = extractStr;
                        } else if (cur.matches("(?i)final_bicluster_type =.*")) {
                            final_bicluster_type = extractStr;
                        }
                    }
                }
            }
        }

    }

    /**
     * @paramfile args
     */

    public final static void main(String[] args) {
        if (args.length == 1) {
            DiscoveryParameters rm = new DiscoveryParameters(args);
        } else {
            System.out.println("syntax: java DataMining.DiscoveryParameters <out file>"
            );
        }
    }
}
