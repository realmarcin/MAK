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
public class BiclusterAnnotforAPI {

    boolean debug = false;
    //String header = "index\tgene\tscore\tmean\tcorrelation\tcolumn_enrich\tcolumn_enrich_MONDO\tcolumn_enrich_UBERON\t" +
    //        "column_enrich_DOID\tcolumn_enrich_NCIT\tcolumn_extids\tcolumn_ids\trow_ids";

    HashMap options;
    String[] valid_args = {
            "-vbl", "-colenrich", "-colids", "-rowids", "-data", "-rowenrich", "-corcut"
    };


    String header = "# Fields:\n" +
            "# bicluster,gene,bicluster_score,bicluster_mean,gene_bicluster_cor,row_enrich,all_col_labels\n" +//,all_col_ids\n" +
            "# ";//,column_extids,column_ids,row_ids";

    SimpleMatrix input_data;
    ValueBlockList vbl = null;
    String[][] col_ids, row_ids, col_map;
    String[] col_lab, row_lab;

    double cor_cut = Double.NaN;

    String vblfile;

    /**
     * @param args
     */
    public BiclusterAnnotforAPI(String[] args) {

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
            //double[] colprofile = new double[0];
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
                    String row_enrich = row_lab[i + 1];//.substring(Math.min(6, col_lab[i + 1].length()));

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
                    //System.out.println("row_lab " + col_enrich);

                    if (cor > cor_cut || Double.isNaN(cor_cut)) {
                        str += v.index + "," + "" + StringUtil.replace(row_ids[genes[j] - 1][1], "\"", "") + "," + v.full_criterion + "," + mean + "," + cor + "," +
                                row_enrich;

                        String nowylabels = combineYLabels(v.exps);
                        str += "," + nowylabels;
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
     * //"-vbl,", "-colenrich", "-colids", "-rowids", "-data", "-rowlab", "-corcut"
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


        if (options.get("-corcut") != null) {
            String f = (String) options.get("-corcut");
            try {
                cor_cut = Double.valueOf(f);
            } catch (NumberFormatException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

    }


    /**
     * @return
     */
    private String combineYLabels(int[] indices) {

        String str = "";
        for (int i = 0; i < indices.length; i++) {
            str += col_ids[i][1].replace("\"", "") + "__";
        }
        str = str.substring(0, str.length() - 2);

        //OOM
        //str = StringUtil.replace("\"","",str);
        return str;
    }


    /**
     * @param args
     */
    public final static void main(String[] args) {
        if (args.length >= 10 || args.length <= 18) {
            BiclusterAnnotforAPI rm = new BiclusterAnnotforAPI(args);
        } else {
            System.out.println("syntax: java DataMining.util.BiclusterAnnotforAPI\n" +
                    "<-vbl bicluster summary file>\n" +
                    "<-colenrich column enrichment file>\n" +
                    "<-collabs column labeling file>\n" +
                    "<-colids column ids file>\n" +
                    "<-rowids row ids file>\n" +
                    "<-data input data file>\n" +
                    "<-rowenrich row labels>\n" +
                    "<-corcut correlation cutoff, NaN for none OPTIONAL>"
            );
        }
        //"-sum,", "-colids", "-rowids", "-data", "-yesdata", "-rowlab",  "-collabmap", "-corcut"
    }

}
