package DataMining.func;

import DataMining.ValueBlock;
import DataMining.ValueBlockList;
import mathy.SimpleMatrix;
import mathy.stat;
import util.MoreArray;
import util.ParsePath;
import util.StringUtil;
import util.TabFile;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Date: May 5, 2010
 * Time: 10:50:26 AM
 */
public class BlocktoPajek {
    double[][] cor;
    String[][] genes;
    SimpleMatrix interdata;
    ValueBlock vb;

    ArrayList gene_ids;
    String outf;
    int xfact = 2, yfact = 2;
    String default_node_color = "Black";
    double edge_weight_multiplier = 10.0;

    double cor_threshold = 0.3;

    /**
     * @param args
     */
    public BlocktoPajek(String[] args) {

        ValueBlockList vbl = null;
        try {
            vbl = ValueBlockList.read(args[0], false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ParsePath pp = new ParsePath(args[0]);
        outf = pp.getName() + ".net";
        SimpleMatrix exprdata = new SimpleMatrix(args[1]);
        interdata = new SimpleMatrix(args[2]);
        genes = TabFile.readtoArray(args[3]);

        vb = (ValueBlock) vbl.get(vbl.size() - 1);

        vb.setDataAndMean(exprdata.data);

        cor = new double[vb.genes.length][vb.genes.length];
        for (int i = 0; i < vb.genes.length; i++) {
            for (int j = i; j < vb.genes.length; j++) {

                if (i == j)
                    cor[i][j] = 1;
                else {
                    cor[i][j] = stat.correlationCoeffSample(exprdata.data[i], exprdata.data[j]);
                    cor[j][i] = cor[i][j];
                }
            }
        }
        gene_ids = new ArrayList();
        for (int i = 0; i < vb.genes.length; i++) {
            gene_ids.add(StringUtil.replace(genes[vb.genes[i] - 1][1], "\"", ""));
        }
        System.out.println(MoreArray.toString(MoreArray.ArrayListtoString(gene_ids), " "));
        createNet();
    }


    /**
     *
     */
    public void createNet() {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(outf));

            //add vertices
            out.println("*Vertices " + cor.length);
            for (int i = 0; i < gene_ids.size(); i++) {
                String name = (String) gene_ids.get(i);//gene_label_to_name.get(cur);

                out.println((i + 1) + "  \"" + name + "\" 0.0 0.0 0.0 x_fact " + xfact + " y_fact " + yfact +
                        " ic " + default_node_color + " bc " + default_node_color);
            }

            out.println("*Edges");
            //System.out.println("output " + sm.data.length + "\t" + sm.data[0].length);
            //add edges
            for (int i = 0; i < cor.length; i++) {
                for (int j = i + 1; j < cor.length; j++) {
                    if (Math.abs(cor[i][j]) > cor_threshold) {
                        /*double secondVal = ((int) val * edge_weight_multiplier);
             secondstr = "a A s " + secondVal + " ap 0";*/
                        String color = "";
                        if (interdata.data[vb.genes[i] - 1][vb.genes[j] - 1] > 0 || interdata.data[vb.genes[j] - 1][vb.genes[i] - 1] > 0) {
                            if (cor[i][j] < 0)
                                color = "Thistle";
                            else
                                color = "Dandelion";
                            out.println((i + 1) + " " + (j + 1) + " " + (Math.abs(cor[i][j]) * edge_weight_multiplier)
                                    + " c " + color + " p solid ");// + p + secondstr);
                        } else {
                            color = "Red";
                            if (cor[i][j] < 0)
                                color = "Blue";
                            out.println((i + 1) + " " + (j + 1) + " " + (Math.abs(cor[i][j]) * edge_weight_multiplier)
                                    + " c " + color + " p dashed ");// + p + secondstr);
                        }
                    } else {
                        if (interdata.data[vb.genes[i] - 1][vb.genes[j] - 1] > 0 || interdata.data[vb.genes[j] - 1][vb.genes[i] - 1] > 0) {
                            String color = "Cerulean";
                            out.println((i + 1) + " " + (j + 1) + " " + (Math.abs(cor[i][j]) * edge_weight_multiplier)
                                    + " c " + color + " p solid ");// + p + secondstr);
                        }
                    }
                }
            }
            out.close();
        }

        catch (IOException e) {
            System.out.println("Error creating or writing file " + outf);
            System.out.println("IOException: " + e.getMessage());
        }

    }

    /**
     * @param args
     */
    public final static void main(String[] args) {
        if (args.length == 4) {
            BlocktoPajek cde = new BlocktoPajek(args);
        } else {
            System.out.println("syntax: DataMining.func.BlocktoPajek\n" +
                    "<toplist file>\n" +
                    "<expression data>\n" +
                    "<interaction data>\n" +

                    "<gene labels>");

        }
    }
}
