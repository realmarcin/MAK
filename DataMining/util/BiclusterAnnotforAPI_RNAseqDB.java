package DataMining.util;

import DataMining.ValueBlock;
import DataMining.ValueBlockList;
import mathy.SimpleMatrix;
import util.*;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * User: marcin
 * Date: 8/17/18
 * Time: 4:21 PM
 */
public class BiclusterAnnotforAPI_RNAseqDB {

    boolean debug = false;
    //String header = "index\tgene\tscore\tmean\tcorrelation\tcolumn_enrich\tcolumn_enrich_MONDO\tcolumn_enrich_UBERON\t" +
    //        "column_enrich_DOID\tcolumn_enrich_NCIT\tcolumn_extids\tcolumn_ids\trow_ids";

    HashMap options;
    String[] valid_args = {
            "-vbl", "-colenrich", "-colids", "-rowids", "-data", "-rowenrich", "-colidmap", "-collabmap", "-corcut"
    };


    String header = "# Fields:\n" +
            "# bicluster,gene,bicluster_score,bicluster_mean,gene_bicluster_cor,col_enrich_all,col_enrich_MONDO,col_enrich_MONDO_label" +
            ",col_enrich_UBERON,col_enrich_UBERON_label,col_enrich_DOID,col_enrich_DOID_label,col_enrich_NCIT,col_enrich_NCIT_label,all_col_labels\n" +//,all_col_ids\n" +
            "# ";//,column_extids,column_ids,row_ids";

    SimpleMatrix input_data;
    ValueBlockList vbl = null;
    String[][] col_ids, row_ids, col_map, col_extid_to_label_map;
    String[] col_lab, row_lab, col_map_ids, col_extid_to_label_ids;

    double cor_cut = Double.NaN;

    String vblfile;

    /**
     * @param args
     */
    public BiclusterAnnotforAPI_RNAseqDB(String[] args) {

        init(args);

        //System.exit(0);
        //done loading
        ArrayList out = new ArrayList();
        out.add(header);
        for (int i = 0; i < vbl.size(); i++) {
            //for (int i = 0; i < 13; i++) {
            System.out.print(i + " ");
            ValueBlock v = (ValueBlock) vbl.get(i);
            //System.out.println(v.toString());
            int[] genes = v.genes;
            double[] colprofile = new double[0];
            if (input_data != null)
                try {
                    v.exp_data = ValueBlock.getData(v, input_data.data);
                    //colprofile = mathy.Matrix.avgOverCols(v.exp_data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            //String header = "index\tgene\tscore\tmean\tcorrelation\tcolumn_enrich\tcolumn_enrich_MONDO\tcolumn_enrich_UBERON\tcolumn_enrich_DOID\tcolumn_enrich_NCIT\tcolumn_ids\tcolumn_labels\trow_ids";
            for (int j = 0; j < v.genes.length; j++) {
                //System.out.println("gene " + row_ids[genes[j] - 1][1]);
                if (!row_ids[genes[j] - 1][1].equals("NA")) {
                    if (debug)
                        System.out.println("gene " + v.genes[j] + "\t" + row_ids[genes[j] - 1][1]);
                    String str = "";
                    String col_enrich = col_lab[i + 1];//.substring(Math.min(6, col_lab[i + 1].length()));

                    double cor = Double.NaN;
                    if (v.exp_data != null)
                        try {
                            //cor = stat.correlationCoeff(colprofile, v.exp_data[j]);
                            ArrayList rowcors = new ArrayList();

                            double[] dataA = v.exp_data[j];
                            double avgA = mathy.stat.avg(dataA);
                            dataA = mathy.stat.replace(dataA, Double.NaN, avgA);

                            for (int b = 0; b < v.genes.length; b++) {
                                if (j != b) {
                                    double[] dataB = v.exp_data[b];
                                    double avgB = mathy.stat.avg(dataB);
                                    dataB = mathy.stat.replace(dataB, Double.NaN, avgB);

                                    double curcor = mathy.stat.correlationCoeff(dataA, dataB);
                                    //System.out.println(i + "\trowCors " + cor);
                                    if (!Double.isNaN(curcor))
                                        rowcors.add(curcor);
                                }
                            }
                            if (rowcors.size() > 0)
                                cor = mathy.stat.listAvg(rowcors);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    double mean = v.exp_mean;
                    //System.out.println("col_lab " + col_enrich);

                    if (cor > cor_cut || Double.isNaN(cor_cut)) {
                        str += v.index + "," + "" + StringUtil.replace(row_ids[genes[j] - 1][1], "\"", "") + "," + v.full_criterion + "," + mean + "," + cor + "," +
                                col_enrich;

                        str = extractEnrichIdsandLabels(str, col_enrich, "MONDO");
                        str = extractEnrichIdsandLabels(str, col_enrich, "UBERON");
                        str = extractEnrichIdsandLabels(str, col_enrich, "DOID");
                        str = extractEnrichIdsandLabels(str, col_enrich, "NCIT");

                        if (col_map != null) {
                            String col_map_str = ",";
                            HashMap tm = new HashMap();
                            int count = 0;
                            for (int a = 0; a < v.exps.length; a++) {
                                int index = StringUtil.getFirstEqualsIndex(col_map_ids, StringUtil.replace(col_ids[v.exps[a] - 1][1], "\"", ""));
                                //System.out.println("index " + index + "\t" + col_map.length + "\t" + col_map[0].length + "\t" + col_ids[v.exps[a] - 1][1] + "\t" + col_map[index][1]);
                                String cand = col_map[index][1];
                                Object test = tm.put(cand, 1);
                                if (test == null) {
                                    if (count > 0)
                                        col_map_str += "__";
                                    col_map_str += cand;
                                    count++;
                                }
                            }
                            if (debug)
                                System.out.println("col_map_str " + col_map_str);
                            str += col_map_str.toUpperCase();

                            /*String col_id_str = ",";
                            HashMap tm2 = new HashMap();
                            int count2 = 0;
                            for (int a = 0; a < v.exps.length; a++) {
                                String cand = col_ids[v.exps[a] - 1][1];
                                Object test = tm2.put(cand, 1);
                                if (test == null) {
                                    if (count2 > 0)
                                        col_id_str += "__";
                                    col_id_str += cand;
                                    count2++;
                                }
                            }
                            if (debug)
                                System.out.println("col_id_str " + col_id_str);
                            str += col_id_str.toUpperCase();*/
                        } else
                            str += ",,";

                        out.add(str);
                    }
                } else {
                    System.out.println("gene id not found " + j);
                }
            }
        }

        String outpath = vblfile.substring(0, vblfile.length() - 4) + "_cor" + cor_cut + "_smartBag.txt";
        System.out.println("\nwriting " + outpath);
        TextFile.write(out, outpath);
    }

    /**
     * @param str
     * @param all_enrich
     * @param type
     * @return
     */
    private String extractEnrichIdsandLabels(String str, String all_enrich, String type) {

        String[] split_enrich_ids = all_enrich.split("_");

        int curid = StringUtil.occurIndexIgnoreCase(all_enrich, type);
        if (curid == -1)
            str += ",,";
        else {
            String labels = "";
            String ids = "";
            str += ",";
            while (curid != -1) {
                int[] index = StringUtil.occurIndexIgnoreCase(split_enrich_ids, type);
                //System.out.println("TYPE "+type + " " + +index.length + "\t" + all_enrich);
                //MoreArray.printArray(split_enrich_ids);
                //MoreArray.printArray(index);
                if (index.length > 0) {
                    for (int a = 0; a < index.length; a++) {
                        String cand = split_enrich_ids[index[a]];
                        int index2 = StringUtil.getFirstEqualsIndexIgnoreCase(col_extid_to_label_ids, cand);
                        //System.out.println("cand index2 " + cand + "\t" + index2+"\t"+a);

                        String label = "";
                        if (index2 != -1) {
                            label = col_extid_to_label_map[index2][2];

                            if (ids.indexOf(split_enrich_ids[index[a]]) == -1) {
                                ids += split_enrich_ids[index[a]] + "__";
                                labels += label + "__";
                            }
                        } else {
                            //System.out.println("missing label for " + split_enrich_ids[index[a]]);
                        }
                    }
                    //System.out.println("ENRICH " + all_enrich);
                    //System.out.println(ids);
                    //System.out.println(labels);
                }

                int endIndex = all_enrich.indexOf("_", curid);
                if (endIndex != -1) {
                    all_enrich = all_enrich.substring(endIndex + 1);
                    curid = StringUtil.occurIndexIgnoreCase(all_enrich, type);
                } else
                    curid = -1;
            }

            if (ids.length() > 0) {
                str += ids.toUpperCase();
                str = str.substring(0, str.length() - 2) + ",";
                if (labels.length() > 0) {
                    str += labels.toUpperCase();
                    str = str.substring(0, str.length() - 2);
                }
            } else
                str += ",";

        }
        return str;
    }


    /**
     * //"-vbl,", "-colenrich", "-colids", "-rowids", "-data", "-rowlab", "-colidmap", "-collabmap", "-corcut"
     *
     * @param args
     */
    private void init(String[] args) {
        MoreArray.printArray(args);

        options = MapArgOptions.maptoMap(args, valid_args);

        if (options.get("-vbl") != null) {
            vblfile = (String) options.get("-vbl");
            System.out.println("reading vbl");
            try {
                vbl = ValueBlockList.read(vblfile, debug);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("done vbl");
        }

        if (options.get("-colenrich") != null) {
            String f = (String) options.get("-colenrich");
            System.out.println("reading col labels");
            col_lab = TextFile.readtoArray(f);
            System.out.println("col_lab " + col_lab.length);
            System.out.println("col_lab " + col_lab[0]);//[0]+"\t"+col_lab[1][1]);
            System.out.println("col_lab " + col_lab[1]);//[0]+"\t"+col_lab[1][1]);
            for (int i = 0; i < col_lab.length; i++) {
                col_lab[i] = col_lab[i].substring(col_lab[i].indexOf(" = ") + 3);
            }
            System.out.println("done col labels");
        }


        if (options.get("-colids") != null) {
            String f = (String) options.get("-colids");
            System.out.println("reading col ids");
            col_ids = TabFile.readtoArray(f);
            System.out.println("done col ids");
            System.out.println("col_ids " + col_ids[0][0] + "\t" + col_ids[0][1]);
        }

        if (options.get("-rowids") != null) {
            String f = (String) options.get("-rowids");
            System.out.println("reading row ids");
            row_ids = TabFile.readtoArray(f);
            System.out.println("done row ids");
            System.out.println("row_ids " + row_ids[0][0] + "\t" + row_ids[0][1]);
        }

        if (options.get("-data") != null) {
            String f = (String) options.get("-data");
            input_data = new SimpleMatrix(f);
        }

        if (options.get("-rowenrich") != null) {
            String f = (String) options.get("-rowenrich");
            System.out.println("reading row labels");
            row_lab = TextFile.readtoArray(f);
            System.out.println("rowenrich " + row_lab.length);
            System.out.println("rowenrich " + row_lab[0]);//[0]+"\t"+row_lab[1][1]);
            System.out.println("rowenrich " + row_lab[1]);//[0]+"\t"+row_lab[1][1]);

            for (int i = 0; i < row_lab.length; i++) {
                int index = row_lab[i].indexOf(" = ") + 3;
                row_lab[i] = row_lab[i].substring(index);
            }
            System.out.println("done row enrich");
        }

        if (options.get("-colidmap") != null) {
            String f = (String) options.get("-colidmap");
            col_map = null;
            col_map_ids = null;
            System.out.println("reading mapping");
            col_map = TabFile.readtoArray(f);
            col_map_ids = MoreArray.extractColumnStr(col_map, 1);
            col_map_ids = StringUtil.replace(col_map_ids, "\"", "");
            System.out.println("col_map_ids " + col_map_ids[1]);
            System.out.println("done mapping");
        }

        if (options.get("-collabmap") != null) {
            String f = (String) options.get("-collabmap");
            col_extid_to_label_map = null;
            col_extid_to_label_ids = null;
            System.out.println("reading mapping");
            col_extid_to_label_map = TabFile.readtoArray(f);
            col_extid_to_label_ids = MoreArray.extractColumnStr(col_extid_to_label_map, 2);
            col_extid_to_label_ids = StringUtil.replace(col_extid_to_label_ids, "\"", "");
            //MoreArray.printArray(col_extid_to_label_ids);
            System.out.println("col_extid_to_label_ids " + col_extid_to_label_ids[1]);
            System.out.println("done mapping");
        }

        if (options.get("-corcut") != null) {
            String f = (String) options.get("-corcut");
            try {
                cor_cut = Double.valueOf(f);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * @param args
     */
    public final static void main(String[] args) {
        if (args.length >= 12 || args.length <= 20) {
            BiclusterAnnotforAPI_RNAseqDB rm = new BiclusterAnnotforAPI_RNAseqDB(args);
        } else {
            System.out.println("syntax: java DataMining.util.BiclusterAnnotforAPI_RNAseqDB\n" +
                    "<-vbl bicluster summary file>\n" +
                    "<-colenrich column enrichment file>\n" +
                    "<-collabs column labeling file>\n" +
                    "<-colids column ids file>\n" +
                    "<-rowids row ids file>\n" +
                    "<-data input data file>\n" +
                    "<-rowenrich row labels>\n" +
                    "<-colidmap column id to id map>\n" +
                    "<-collabmap column id to label map>\n" +
                    "<-corcut correlation cutoff, NaN for none OPTIONAL>"
            );
        }
        //"-sum,", "-colids", "-rowids", "-data", "-yesdata", "-rowlab", "-colidmap", "-collabmap", "-corcut"
    }

}
