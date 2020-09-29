package DataMining.util;

import util.StringUtil;
import util.TabFile;
import util.TextFile;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: marcin
 * Date: 9/1/20
 * Time: 9:07 AM
 */
public class NMDCtoGraph {

    String[] include = {"depth", "habitat", "community", "ncbi_taxonomy_name",
            "ecosystem_subtype", "ecosystem_type", "location_description",
            "env_local_scale_term", "ecosystem", "env_broad_scale", "env_medium"};

    //id	latitude	longitude	depth	depth_scale	collection_date	sample_name	"ENVO"
    // description	"gold:habitat"	"nmdc:community"	"gold:ncbi_taxonomy_name"
    // "nmdc:ecosystem_subtype"	"nmdc:ecosystem_type"	location_description
    // "gold:biosample_id"	env_local_scale_term	"nmdc:mod_date"	"nmdc:ecosystem"

    ArrayList pairs;
    ArrayList types;
    String[][] tsv;

    /**
     * @param args
     */
    public NMDCtoGraph(String[] args) {

        tsv = TabFile.readtoArray(args[0]);

        pairs = new ArrayList();
        types = new ArrayList();

        //tabs in column headers?
        //stripping nmdc: prefix
        for (int j = 0; j < tsv[0].length; j++) {
            tsv[0][j] = StringUtil.replace(tsv[0][j], "\t", "");
            tsv[0][j] = StringUtil.replace(tsv[0][j], "nmdc:", "");
            tsv[0][j] = StringUtil.replace(tsv[0][j], "gold:", "");
            tsv[0][j] = StringUtil.replace(tsv[0][j], "\"", "");
        }

        for (int i = 1; i < tsv.length; i++) {
            for (int j = 1; j < tsv[0].length; j++) {
                for (int k = 0; k < include.length; k++) {

                    if (tsv[0][j].contains("ncbi_taxonomy_name"))
                        System.out.println("test :" + tsv[0][j] + ":\t:" + include[k] + ":");

                    //System.out.println(":" + tsv[0][j] + ":\t:" + include[k] + ":");
                    if (tsv[0][j].equals(include[k]) && !tsv[i][j].equals("")) {
                        String cur_type = tsv[0][j];
                        if (tsv[i][j].equals("Soil"))
                            cur_type = "habitat";
                        else if (tsv[i][j].equals("Sand"))
                            cur_type = "habitat";
                        else if (tsv[i][j].equals("Deep subsurface"))
                            cur_type = "habitat";

                        //if (include[k].contains("env_"))
                        //    System.out.println("matched " + include[k]);

                        //rely on hardcoded id in [i][0]
                        makeNodesAndEdges(i, j, cur_type);

                        /*if (cur_type.equals("depth")) {
                            makeNodesAndEdgesStrStr(tsv[i][j], "meter", "depth", "measurement");
                        }*/
                        if (cur_type.equals("location_description")) {
                            //System.out.println("parsing location into components " + tsv[i][j]);
                            if (tsv[i][j].indexOf(",") != -1 || tsv[i][j].indexOf(":") != -1) {
                                String[] split = tsv[i][j].split(",|:");
                                //System.out.println("split "+ tsv[i][j]);
                                //MoreArray.printArray(split);

                                for (int z = 0; z < split.length; z++) {
                                    makeNodesAndEdgesStr(i, split[z].trim(), "locality");
                                }
                        /*for (int x = 0; x < split.length; x++) {
                            for (int z = x + 1; z < split.length; z++) {
                                makeNodesAndEdgesStrStr(split[x], split[z], "locality", "locality");
                            }
                        }*/
                            }

                        }
                    }
                }
            }
        }

        pairs.add(0, "subject\tobject");
        types.add(0, "id\ttype");
        System.out.println("edges " + pairs.size());
        System.out.println("nodes " + types.size());

        TextFile.write(pairs, "NMDC_FICUS_edges.txt");
        TextFile.write(types, "NMDC_FICUS_node_types.txt");
    }

    /**
     * @param i
     * @param j
     */
    private void makeNodesAndEdges(int i, int j, String type) {
        String e = tsv[i][0].trim() + "\t" + tsv[i][j].trim();
        if (!pairs.contains(e)) {
            pairs.add(e);
            String node_type = tsv[i][0].trim() + "\t" + tsv[0][0].trim();//+"\t"+tsv[i][0];
            String node_type2 = tsv[i][j].trim() + "\t" + type.trim();//+"\t"+tsv[i][j];
            if (!types.contains(node_type))
                types.add(node_type);
            if (!types.contains(node_type2))
                types.add(node_type2);
        }
    }

    private void makeNodesAndEdgesStr(int i, String j, String type) {
        String e = tsv[i][0].trim() + "\t" + j.trim();
        if (!pairs.contains(e)) {
            pairs.add(e);
            String node_type = tsv[i][0].trim() + "\t" + tsv[0][0].trim();//+"\t"+tsv[i][0];
            String node_type2 = j.trim() + "\t" + type.trim();//+"\t"+tsv[i][j];
            if (!types.contains(node_type))
                types.add(node_type);
            if (!types.contains(node_type2))
                types.add(node_type2);
        }
    }

    private void makeNodesAndEdgesStrStr(String i, String j, String typei, String typej) {
        String e = i.trim() + "\t" + j.trim();
        if (!pairs.contains(e)) {
            pairs.add(e);
            String node_type = i.trim() + "\t" + typei.trim();//+"\t"+tsv[i][0];
            String node_type2 = j.trim() + "\t" + typej.trim();//+"\t"+tsv[i][j];
            if (!types.contains(node_type))
                types.add(node_type);
            if (!types.contains(node_type2))
                types.add(node_type2);
        }
    }

    /**
     * @param args
     */
    public final static void main(String[] args) {
        if (args.length == 1) {
            NMDCtoGraph ng = new NMDCtoGraph(args);
        } else {
            System.out.println("syntax: util.NMDCtoGraph\n" +
                    "<NMDC tsv matrix tab text file>\n");
        }
    }
}
