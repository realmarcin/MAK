package DataMining.eval;

import util.MoreArray;
import util.StringUtil;
import util.TabFile;
import util.TextFile;

import java.io.File;
import java.util.ArrayList;

/**
 * User: marcin
 * Date: Nov 3, 2009
 * Time: 2:52:50 PM
 */
public class QUBICReformat {


    String[] gene_labels, exp_labels;

    /**
     * Reformats QUBIC bicluster output (from Expander) to BiCat format.
     *
     * @param args
     */
    public QUBICReformat(String[] args) {


        if (args.length == 3) {
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
        }

        //System.out.println(MoreArray.toString(gene_labels));
        //System.out.println(MoreArray.toString(exp_labels));


        ArrayList pass = TextFile.readtoList(args[0]);
        int count = 0;
        String cur = (String) pass.get(0);
        while (!cur.startsWith("BC")) {
            pass.remove(0);
            cur = (String) pass.get(0);
            count++;
        }
        ArrayList out = new ArrayList();

        cur = (String) pass.get(0);
        System.out.println(cur);


        if (args.length == 1) {
            while (cur.startsWith("BC")) {
                String genes = (String) pass.get(1);
                String exps = (String) pass.get(2);
                genes = genes.substring(genes.indexOf(":") + 2, genes.length() - 1);
                exps = exps.substring(exps.indexOf(":") + 2, exps.length() - 1);
                out.add("" + genes.split(" ").length + " " + exps.split(" ").length);
                out.add(genes);
                out.add(exps);
                int countinner = 0;
                while (countinner < 3) {
                    pass.remove(0);
                    countinner++;
                }
                cur = (String) pass.get(0);
                //System.out.println("1 " + cur);
                while (pass.size() > 0 && !cur.startsWith("BC")) {
                    pass.remove(0);
                    if (pass.size() > 0)
                        cur = (String) pass.get(0);
                }

                if (pass.size() == 0)
                    break;
            }
        } else {
            while (cur.startsWith("BC")) {
                String genesorig = (String) pass.get(1);
                String expsorig = (String) pass.get(2);

                System.out.println("orig " + expsorig);

                String genes = genesorig.substring(genesorig.indexOf(":") + 2, genesorig.length() - 1);
                String exps = expsorig.substring(expsorig.indexOf(":") + 2, expsorig.length() - 1);
                out.add("" + expsorig.split(" ").length + " " + expsorig.split(" ").length);

                String[] genes_split = genes.split(" ");
                exps = StringUtil.replace(exps, "\" \"", "\"__\"");
                exps = StringUtil.replace(exps, " ", ".");
                exps = StringUtil.replace(exps, "\"__\"", "\" \"");

                String[] exps_split = exps.split(" ");

                String genes_reform = "";
                String exps_reform = "";

                for (int i = 0; i < genes_split.length; i++) {
                    genes_reform += MoreArray.getArrayInd(gene_labels, StringUtil.replace(genes_split[i], "\"", "")) + 1;
                    if (i < genes_split.length - 1)
                        genes_reform += " ";
                }
                for (int i = 0; i < exps_split.length; i++) {
                    System.out.println(i + "\t" + exps_split[i]);
                    String replace1 = StringUtil.replace(exps_split[i], "\"", "");
                    replace1 = StringUtil.replace(replace1, ":", ".");
                    replace1 = StringUtil.replace(replace1, "-", ".");
                    replace1 = StringUtil.replace(replace1, "/", ".");
                    replace1 = StringUtil.replace(replace1, ",", ".");
                    replace1 = StringUtil.replace(replace1, "(", ".");
                    replace1 = StringUtil.replace(replace1, ")", ".");
                    replace1 = StringUtil.replace(replace1, "+", ".");
                    replace1 = "X" + replace1;
                    //replace = StringUtil.replace(exps_split[i], " ", ".");
                    final int arrayInd = MoreArray.getArrayInd(exp_labels, replace1);
                    if (arrayInd != -1)
                        exps_reform += arrayInd + 1;
                    else {
                        System.out.println("no match 1 " + exps_split[i]);
                        System.out.println("no match 2 " + replace1);
                    }
                    if (i < exps_split.length - 1)
                        exps_reform += " ";
                }

                System.out.println(genes_reform);
                System.out.println(exps_reform);

                out.add(genes_reform);
                out.add(exps_reform);
                int countinner = 0;
                while (countinner < 3) {
                    pass.remove(0);
                    countinner++;
                }
                cur = (String) pass.get(0);
                //System.out.println("1 " + cur);
                while (pass.size() > 0 && !cur.startsWith("BC")) {
                    pass.remove(0);
                    if (pass.size() > 0)
                        cur = (String) pass.get(0);
                }

                if (pass.size() == 0)
                    break;
            }
        }

        System.out.println("blocks " + out.size() / 3.0);
        TextFile.write(out, args[0] + ".reformat");
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 1 || args.length == 3) {
            QUBICReformat arg = new QUBICReformat(args);
        } else {
            System.out.println("syntax: java DataMining.eval.QUBICReformat\n" +
                    "<QUBIC output>" +
                    "<OPTIONAL gene ids>" +
                    "<OPTIONAL exp ids>" +
                    "\n");
        }
    }
}
