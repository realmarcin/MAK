package DataMining.util;

import DataMining.ValueBlock;
import DataMining.ValueBlockList;
import mathy.SimpleMatrix;
import util.*;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created with IntelliJ IDEA.
 * User: marcin
 * Date: 2/26/19
 * Time: 7:46 PM
 */
public class BiclusterAnnotSimpleforAPI {

    boolean debug = false;

    HashMap options;
    String[] valid_args = {
            "-vbl", "-collabs", "-colids", "-rowlabs", "-rowids", "-data", "-corcut"
    };

    String header = "bicluster,hpo,bicluster_score,bicluster_mean,hpo_bicluster_cor,mondo_list";//col_enrich_all

    SimpleMatrix input_data;
    ValueBlockList vbl = null;
    String[][] col_ids, row_ids;
    String[][] col_lab, row_lab;

    double cor_cut = Double.NaN;

    String vblfile;

    /**
     * @param args
     */
    public BiclusterAnnotSimpleforAPI(String[] args) {

        init(args);

        ArrayList out = new ArrayList();
        out.add(header);
        for (int i = 0; i < vbl.size(); i++) {
            System.out.print(i + " ");
            ValueBlock v = (ValueBlock) vbl.get(i);
            //System.out.println(v.toString());
            int[] Xentity = v.genes;

            //String header = "index\tgene\tscore\tmean\tcorrelation\tcolumn_enrich\tcolumn_enrich_MONDO\tcolumn_enrich_UBERON\tcolumn_enrich_DOID\tcolumn_enrich_NCIT\tcolumn_ids\tcolumn_labels\trow_ids";
            for (int j = 0; j < v.genes.length; j++) {
                //System.out.println("gene " + row_ids[Xentity[j] - 1][1]);
                if (!row_ids[Xentity[j] - 1][1].equals("NA")) {
                    if (debug)
                        System.out.println("row " + v.genes[j] + "\t" + row_ids[Xentity[j] - 1][1]);
                    String str = "";

                    double cor = Double.NaN;
                    if (v.exp_data == null)
                        v.exp_data = ValueBlock.getData(v, input_data.data);

                    try {
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
                        str += v.index + "," + "" + StringUtil.replace(row_ids[Xentity[j] - 1][1], "\"", "") + "," +
                                v.full_criterion + "," + mean + "," + cor;
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
     * @return
     */
    private String combineYLabels(int[] indices) {

        String str = "";
        for (int i = 0; i < indices.length; i++) {
            str += col_ids[i][1] + "__";
        }
        str = str.substring(0, str.length() - 2);

        //OOM
        //str = StringUtil.replace("\"","",str);
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

        if (options.get("-colids") != null) {
            String f = (String) options.get("-colids");
            System.out.println("reading col ids");
            col_ids = TabFile.readtoArray(f);
            col_ids = MoreArray.replaceAll(col_ids, "\"", "");
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

        if (options.get("-collabs") != null) {
            String f = (String) options.get("-collabs");
            System.out.println("reading col labs");
            col_lab = TabFile.readtoArray(f);
            System.out.println("done col labs");
            System.out.println("col_labs " + col_lab[0][0] + "\t" + col_lab[0][1]);
        }

        if (options.get("-rowlabs") != null) {
            String f = (String) options.get("-rowlabs");
            System.out.println("reading row labs");
            row_lab = TabFile.readtoArray(f);
            System.out.println("done row labs");
            System.out.println("row_labs " + row_lab[0][0] + "\t" + row_lab[0][1]);
        }

        if (options.get("-data") != null) {
            String f = (String) options.get("-data");
            input_data = new SimpleMatrix(f);
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
        if (args.length >= 12 || args.length <= 14) {
            BiclusterAnnotSimpleforAPI rm = new BiclusterAnnotSimpleforAPI(args);
        } else {
            System.out.println("syntax: java DataMining.util.BiclusterAnnotSimpleforAPI\n" +
                    "<-vbl bicluster file>\n" +
                    "<-collabs column labeling file>\n" +
                    "<-colids column ids file>\n" +
                    "<-rowlabs row labeling file>\n" +
                    "<-rowids row ids file>\n" +
                    "<-data input data file>\n" +
                    "<-corcut correlation cutoff, NaN for none OPTIONAL>"
            );
        }
    }


}

/*

        -vbl
        RNAseqDB_sortedids_20180623_all_cut_scoreperc66.0_exprNaN_0.0__nr_0.25_score_root.txt
        -colids
        RNAseqDB_sortedids_expids.txt
        -rowids
        RNAseqDB_sortedids_NEWgeneids_ENSEMBL_v1.txt
        -corcut
        NaN

*/
