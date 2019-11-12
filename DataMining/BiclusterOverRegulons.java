package DataMining;

import bioobj.YeastRegulons;
import mathy.SimpleMatrix;
import mathy.stat;
import util.MapArgOptions;
import util.MoreArray;
import util.Program;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * User: marcin
 * Date: Apr 1, 2008
 * Time: 10:36:13 PM
 */
public class BiclusterOverRegulons extends Program {

    HashMap options;
    String[] valid_args = {"-regulons", "-input", "-dataset"};

    SimpleMatrix expr_matrix;
    ArrayList[] factor_list, factor_list_int;
    double[] factor_list_sizes;
    PrintWriter pw, pw_frxn, pw_reffrxn;

    /**
     * @param args
     */
    public BiclusterOverRegulons(String[] args) {

        //args = MapArgOptions.compactArgs(args);
        options = MapArgOptions.maptoMap(args, valid_args);

        String input = (String) options.get("-input");
        String regulons = (String) options.get("-regulons");
        String datapath = (String) options.get("-dataset");

        expr_matrix = new SimpleMatrix(datapath);

        YeastRegulons yr = new YeastRegulons(regulons, expr_matrix.ylabels, "MacIsaac");
        expr_matrix = null;

        /* factor_list_int = new ArrayList[yr.factors.length];
        factor_list_sizes = new double[yr.factors.length];
        for (int n = 0; n < yr.factors.length; n++) {
            int index = MoreArray.getArrayInd(yr.factors, yr.factors[n]);
            //factor_list[n] = yr.significant[index];
            factor_list_int[n] = new ArrayList();
            if (factor_list_int != null)
                for (int i = 0; i < factor_list_int[n].size(); i++) {
                    String cur = (String) factor_list_int[n].get(i);
                    //System.out.println("GAL4 " + i + "\t" + cur);
                    int add = MoreArray.getArrayInd(expr_matrix.ylabels, cur) + 1;
                    factor_list_int[n].add(new Integer(add));
                }
        }*/

        File testinput = new File(input);
        if (testinput.isDirectory()) {
            String path = input;
            String[] list = testinput.list();
            for (int i = 0; i < list.length; i++) {
                if (list[i].indexOf(".DS_Store") == -1) {
                    input = path + sysRes.file_separator + list[i];
                    doOne(input, yr);
                }
            }
        } else {
            doOne(input, yr);
        }
    }

    /**
     * @param input
     * @param yr
     */
    private void doOne(String input, YeastRegulons yr) {
        try {
            String outf = input + "_bicoverregulons.txt";
            String outf_frxn = input + "_bicoverregulons_frxn.txt";
            String outf_reffrxn = input + "_bicoverregulons_reffrxn.txt";
            System.out.println("generating " + outf);
            pw = new PrintWriter(new FileWriter(outf), true);
            pw_frxn = new PrintWriter(new FileWriter(outf_frxn), true);
            pw_reffrxn = new PrintWriter(new FileWriter(outf_reffrxn), true);
            pw.println("number\tblock_id\t" + MoreArray.toString(yr.factors, "\t"));
            String sizes = MoreArray.toString(yr.significant_sizes, "\t");
            pw.println("\t\t" + sizes);
            pw_frxn.println("number\tblock_id\t" + MoreArray.toString(yr.factors, "\t"));
            pw_frxn.println("\t\t" + sizes);
            pw_reffrxn.println("number\tblock_id\t" + MoreArray.toString(yr.factors, "\t"));
            pw_reffrxn.println("\t\t" + sizes);
            ValueBlockList vbl = ValueBlockListMethods.readBIC(input, false);
            ValueBlock first = (ValueBlock) vbl.get(0);
            boolean intids = false;
            int test = -1;
            try {
                test = first.genes[0];
                intids = true;
            } catch (Exception e) {
            }
            if (!intids) {
                biclusterCoverId(yr, vbl, "string");
            } else {
                biclusterCoverId(yr, vbl, "int");
                //biclusterCoverIntId(yr, vbl);
            }

            pw.close();
            pw_frxn.close();
            pw_reffrxn.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * @param yr
     * @param vbl
     */
    private void biclusterCoverId(YeastRegulons yr, ValueBlockList vbl, String type) {
        for (int m = 0; m < vbl.size(); m++) {
            ValueBlock cur = (ValueBlock) vbl.get(m);
            String block_id = BlockMethods.IcJctoijID(cur.genes, cur.exps);
            System.out.println("doing " + block_id);
            double[] counts = new double[yr.significant.length];
            double[] divide_frxn = new double[yr.significant.length];
            double[] divide_reffrxn = new double[yr.significant.length];
            for (int n = 0; n < yr.significant.length; n++) {
                if (type.equals("int")) {
                    for (int a = 0; a < cur.genes.length; a++) {
                        int s = cur.genes[a];
                        int index_in_regulon = MoreArray.getArrayIndUnique(yr.significant_int[n], s);
                        if (index_in_regulon != -1) {
                            counts[n]++;
                        }
                    }
                    divide_frxn[n] = Math.sqrt(cur.genes.length * yr.significant_sizes[n]);
                    divide_reffrxn[n] = cur.genes.length;
                } else if (type.equals("string")) {
                    int genes_num = cur.genes.length;
                    for (int a = 0; a < genes_num; a++) {
                        String s = "" + cur.genes[a];
                        //System.out.println(n+"\t"+s);
                        int index_in_regulon = MoreArray.getArrayIndUnique(yr.significant[n], s, false);
                        if (index_in_regulon != -1) {
                            counts[n]++;
                        }
                    }
                    divide_frxn[n] = Math.sqrt(genes_num * yr.significant_sizes[n]);
                    divide_reffrxn[n] = genes_num;
                }
            }
            double[] frxn = stat.divideDefaultValue(counts, divide_frxn, 0);
            double[] frxnref = stat.divideDefaultValue(counts, divide_reffrxn, 0);
            pw.println(m + "\t" + block_id.substring(0, 25) + "\t" + MoreArray.toString(counts, "\t"));
            pw_frxn.println(m + "\t" + block_id.substring(0, 25) + "\t" + MoreArray.toString(frxn, "\t"));
            pw_reffrxn.println(m + "\t" + block_id.substring(0, 25) + "\t" + MoreArray.toString(frxnref, "\t"));
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 6) {
            BiclusterOverRegulons arg = new BiclusterOverRegulons(args);
        } else {
            System.out.println("syntax: java DataMining.BiclusterOverRegulons\n" +
                    "<-input BIC format output (or dir of outputs)'>\n" +
                    "<-regulons 'regulon file'>\n" +
                    "<-dataset labeled expr. data set>"
            );
        }
    }
}
