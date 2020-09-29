package DataMining.eval;

import util.MoreArray;
import util.StringUtil;
import util.TabFile;
import util.TextFile;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: 3/31/16
 * Time: 9:36 PM
 */
public class cMonkey2reformat {


    String[] gene_labels, exp_labels;

    /**
     * Reformats QUBIC bicluster output (from Expander) to BiCat format.
     *
     * @param args
     */
    public cMonkey2reformat(String[] args) {


        File testg = new File(args[1]);
        File teste = new File(args[2]);
        //if (testg.exists()) {
        try {
            String[][] sarray = TabFile.readtoArray(args[1]);
            System.out.println("setLabels g " + sarray.length + "\t" + sarray[0].length);
            int col = 2;
            String[] n = MoreArray.extractColumnStr(sarray, col);
            gene_labels = MoreArray.replaceAll(n, "\"", "");
            System.out.println("setLabels gene " + gene_labels.length + "\t" + gene_labels[0]);
        } catch (Exception e) {
            System.out.println("expecting 2 cols");
            //e.printStackTrace();
            try {
                String[][] sarray = TabFile.readtoArray(args[1]);
                System.out.println("setLabels g " + sarray.length + "\t" + sarray[0].length);
                int col = 1;
                String[] n = MoreArray.extractColumnStr(sarray, col);
                gene_labels = MoreArray.replaceAll(n, "\"", "");
                System.out.println("setLabels gene " + gene_labels.length + "\t" + gene_labels[0]);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        try {
            String[][] sarray = TabFile.readtoArray(args[2]);
            System.out.println("setLabels e " + sarray.length + "\t" + sarray[0].length);
            int col = 2;
            exp_labels = MoreArray.replaceAll(MoreArray.extractColumnStr(sarray, col), "\"", "");
            System.out.println("setLabels e " + exp_labels.length + "\t" + exp_labels[0]);
        } catch (Exception e) {
            System.out.println("expecting 2 cols");
            //e.printStackTrace();
            try {
                String[][] sarray = TabFile.readtoArray(args[2]);
                System.out.println("setLabels e " + sarray.length + "\t" + sarray[0].length);
                int col = 1;
                exp_labels = MoreArray.replaceAll(MoreArray.extractColumnStr(sarray, col), "\"", "");
                System.out.println("setLabels e " + exp_labels.length + "\t" + exp_labels[0]);
            } catch (Exception e1) {
                System.out.println("tried path " + args[2]);
                e1.printStackTrace();
            }
        }

        //System.out.println(MoreArray.toString(gene_labels));
        //System.out.println(MoreArray.toString(exp_labels));

        ArrayList pass = TabFile.readtoList(args[0]);
        ArrayList out = new ArrayList();


        for (int i = 1; i < pass.size(); i++) {
            ArrayList cur = (ArrayList) pass.get(i);

            //String genesorig = (String) cur.get(6);//with no MEME
            String genesorig = (String) cur.get(23);
            String expsorig = (String) cur.get(0);
            genesorig = StringUtil.replace(genesorig, "\"", "");
            expsorig = StringUtil.replace(expsorig, "\"", "");
            expsorig = StringUtil.replace(expsorig, ", ", ". ");
            //System.out.println("orig " + expsorig);
            String[] genes_split = genesorig.split(",");
            String[] exps_split = expsorig.split(",");

            out.add("" + expsorig.split(" ").length + " " + expsorig.split(" ").length);

            String genes_reform = "";
            String exps_reform = "";

            for (int a = 0; a < genes_split.length; a++) {
                final int arrayInd = MoreArray.getArrayInd(gene_labels, genes_split[a]);
                if (arrayInd > -1) {
                    genes_reform += arrayInd + 1;
                    if (a < genes_split.length - 1)
                        genes_reform += " ";
                } else {
                    System.out.println("no match 1 " + genes_split[a]);
                    System.out.println("no match ref " + gene_labels[0]);
                    System.out.println("no match ref " + gene_labels[1]);
                }
            }

            int extradot = 0, starts = 0;
            for (int b = 0; b < exps_split.length; b++) {
                //System.out.println(b + "\t" + exps_split[b]);
                String replace1 = exps_split[b];

                replace1 = StringUtil.replace(replace1, ":", ".");
                replace1 = StringUtil.replace(replace1, "-", ".");
                replace1 = StringUtil.replace(replace1, "/", ".");
                replace1 = StringUtil.replace(replace1, ",", ".");
                replace1 = StringUtil.replace(replace1, "(", ".");
                replace1 = StringUtil.replace(replace1, ")", ".");
                replace1 = StringUtil.replace(replace1, "+", ".");
                replace1 = StringUtil.replace(replace1, " ", ".");
                replace1 = StringUtil.replace(replace1, "%", ".");
                replace1 = StringUtil.replace(replace1, "'", ".");
                replace1 = StringUtil.replace(replace1, "*", ".");
                replace1 = StringUtil.replace(replace1, "#", ".");
                replace1 = "X" + replace1;
                //29.alpha factor release sample016

                //replace = StringUtil.replace(exps_split[i], " ", ".");
                int arrayInd = MoreArray.getArrayInd(exp_labels, replace1);
                if (arrayInd != -1)
                    exps_reform += arrayInd + 1;
                else {
                    arrayInd = MoreArray.getArrayInd(exp_labels, replace1.substring(0, replace1.length() - 1));
                    if (arrayInd != -1) {
                        //System.out.println("extra dot 1 " + replace1);
                        //System.out.println("extra dot 2 " + exp_labels[arrayInd]);
                        exps_reform += arrayInd + 1;
                        extradot++;
                    } else {
                        arrayInd = MoreArray.getArrayIndStartsWith(exp_labels, replace1);
                        if (arrayInd != -1) {
                            //System.out.println("starts with 1 " + replace1);
                            //System.out.println("starts with 2 " + exp_labels[arrayInd]);
                            exps_reform += arrayInd + 1;
                            starts++;
                        } else {
                            System.out.println("no match 1 " + exps_split[b]);
                            System.out.println("no match 2 " + replace1);
                            System.out.println("no match ref " + exp_labels[0]);
                            System.out.println("no match ref " + exp_labels[1]);
                        }
                    }
                }
                if (b < exps_split.length - 1)
                    exps_reform += " ";
            }

            System.out.println(genes_reform);
            System.out.println(exps_reform);

            out.add(genes_reform);
            out.add(exps_reform);

            System.out.println("extradot " + extradot + " starts " + starts);
        }


        System.out.println("blocks " + out.size() / 3.0);
        TextFile.write(out, args[0] + "_bicreformat.txt");
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 3) {
            cMonkey2reformat arg = new cMonkey2reformat(args);
        } else {
            System.out.println("syntax: java DataMining.eval.cMonkey2reformat\n" +
                    "<cmonkey2 tsv output>" +
                    "<gene ids>" +
                    "<exp ids>" +
                    "\n");
        }
    }
}