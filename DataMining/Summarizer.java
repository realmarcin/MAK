package DataMining;

import util.MoreArray;
import util.StringUtil;
import util.TabFile;
import util.TextFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * DEPRECATED
 * <p/>
 * User: marcin
 * Date: Feb 5, 2008
 * Time: 5:59:42 PM
 */
public class Summarizer {

    String[] modes = {"toplist", "fulllist"};
    int mode = 0;
    int[] random_seeds;

    ArrayList genes, exps;
    int label_offset = 1;

    /**
     * @param args
     */
    public Summarizer(String[] args) {

        if (args.length > 1) {
            String[][] a = TabFile.readtoArray(args[1]);
            String[] arow = MoreArray.extractColumnStr(a, 1);
//            System.out.println("genesStr");
//            MoreArray.printArray(arow);

            String[][] b = TabFile.readtoArray(args[2]);
            String[] brow = MoreArray.extractColumnStr(b, 1);
//            System.out.println("exps");
//            MoreArray.printArray(brow);
            exps = MoreArray.convtoArrayList(brow);
            genes = MoreArray.convtoArrayList(arow);
        }

        File dir = new File(args[0]);
        String[] list = dir.list();
        ArrayList store = new ArrayList();
        int maxlen = 0;
        random_seeds = new int[list.length];
        for (int i = 0; i < list.length; i++) {
            if (list[i].indexOf("_toplist.txt") != -1) {
                System.out.println(list[i]);
                ValueBlockList vbl = null;
                try {
                    vbl = ValueBlockList.read(args[0] + "/" + list[i], false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                int end = list[i].indexOf(modes[mode]);
                int start = StringUtil.lastIndexBefore(list[i], "_", end - 1);
                random_seeds[i] = Integer.parseInt(list[i].substring(start + 1, end - 1));
                if (vbl.size() > maxlen)
                    maxlen = vbl.size();
                //System.out.println("Summarizer " + list[i] + "\t" + vbl.size());
                store.add(vbl);
            }
        }

        writeSummaryTable(store);
        writePre(store, maxlen);
        writeFull(store, maxlen);
        writeInteract(store, maxlen);
        writeR2(store, maxlen);
        writePercGene(store, maxlen);
        writePercExp(store, maxlen);
        writeBIC(store);
        if (args.length > 1) {
            writeBICLabels(store);
            writeBICLabelsRev(store);
        }
    }

    /**
     * @param store
     * @param maxlen
     */
    private void writeFull(ArrayList store, int maxlen) {
        ArrayList full = new ArrayList();
        for (int i = 0; i < store.size(); i++) {
            ValueBlockList vbl = (ValueBlockList) store.get(i);
            double[] dob = MoreArray.initArray(maxlen, Double.NaN);
            /*if (maxlen < vbl.size()) {
                System.out.println("writeFull " + maxlen + "\t" + vbl.size());
            }*/
            for (int j = 0; j < vbl.size(); j++) {
                ValueBlock vb = (ValueBlock) vbl.get(j);
                dob[j] = vb.full_criterion;
            }
            full.add(dob);
        }
        double[][] outD = MoreArray.ArrayListto2DDouble(full);
        String[][] outS = MoreArray.toString(outD, "", "");
        TextFile.write(outS, "toplist_full_criterion.txt");
    }

    /**
     * @param store
     * @param maxlen
     */
    private void writeInteract(ArrayList store, int maxlen) {
        ArrayList full = new ArrayList();
        for (int i = 0; i < store.size(); i++) {
            ValueBlockList vbl = (ValueBlockList) store.get(i);
            double[] dob = MoreArray.initArray(maxlen, Double.NaN);
            /*if (maxlen < vbl.size()) {
                System.out.println("writeFull " + maxlen + "\t" + vbl.size());
            }*/
            for (int j = 0; j < vbl.size(); j++) {
                ValueBlock vb = (ValueBlock) vbl.get(j);
                dob[j] = vb.all_criteria[ValueBlock_STATIC.interact_IND];
            }
            full.add(dob);
        }
        double[][] outD = MoreArray.ArrayListto2DDouble(full);
        String[][] outS = MoreArray.toString(outD, "", "");
        TextFile.write(outS, "toplist_interact_criterion.txt");
    }


    /**
     * @param store
     * @param maxlen
     */
    private void writeR2(ArrayList store, int maxlen) {
        ArrayList full = new ArrayList();
        for (int i = 0; i < store.size(); i++) {
            ValueBlockList vbl = (ValueBlockList) store.get(i);
            double[] dob = MoreArray.initArray(maxlen, Double.NaN);
            /*if (maxlen < vbl.size()) {
                System.out.println("writeFull " + maxlen + "\t" + vbl.size());
            }*/
            for (int j = 0; j < vbl.size(); j++) {
                ValueBlock vb = (ValueBlock) vbl.get(j);
                dob[j] = vb.all_criteria[ValueBlock_STATIC.expr_FEM_IND];
            }
            full.add(dob);
        }
        double[][] outD = MoreArray.ArrayListto2DDouble(full);
        String[][] outS = MoreArray.toString(outD, "", "");
        TextFile.write(outS, "toplist_R2_criterion.txt");
    }

    /**
     * @param store
     */
    private void writeSummaryTable(ArrayList store) {
        String[] out = MoreArray.initArray(store.size() + 1, "");
        out[0] = "RANDOM_SEED\tfirst.block_id\tfirst.gene_num\tfirst.exp_num\tfirst.pre_criterion\tfirst.full_criterion\t" +
                "first.expr_mean\tfirst.expr_cor\tfirst.expr_reg\tfirst.interact_crit\tfirst.feature\tfirst.TF\t" +
                "last.block_id\tlast.gene_num\tlast.exp_num\tlast.pre_criterion\tlast.full_criterion\t" +
                "last.expr_mean\tlast.expr_mse\tlast.expr_reg\tlast.expr_kend\tlast.expr_cor\tlast.expr_euc\tlast.expr_spearman" +
                "\tlast.PPI_crit\tlast.feature\tlast.TF";
        for (int i = 0; i < store.size(); i++) {
            ValueBlockList vbl = (ValueBlockList) store.get(i);
            ValueBlock first = (ValueBlock) vbl.get(0);
            ValueBlock last = (ValueBlock) vbl.get(vbl.size() - 1);
            out[i + 1] += "" + random_seeds[i] + "\t" + BlockMethods.IcJctoijID(first.genes, first.exps) + "\t" +
                    first.genes.length + "\t" + first.exps.length + "\t" +
                    first.pre_criterion + "\t" + first.full_criterion + "\t";
            if (first.all_criteria != null)
                out[i + 1] += first.all_criteria[ValueBlock_STATIC.expr_MEAN_IND] + "\t" + first.all_criteria[ValueBlock_STATIC.expr_MSE_IND] + "\t" +
                        first.all_criteria[ValueBlock_STATIC.expr_FEM_IND] + "\t" +
                        first.all_criteria[ValueBlock_STATIC.expr_KEND_IND] + "\t" +
                        first.all_criteria[ValueBlock_STATIC.expr_COR_IND] + "\t" + first.all_criteria[ValueBlock_STATIC.expr_EUC_IND] + "\t" +
                        first.all_criteria[ValueBlock_STATIC.expr_SPEARMAN_IND] + "\t" +
                        first.all_criteria[ValueBlock_STATIC.interact_IND] +
                        "\t" + first.all_criteria[ValueBlock_STATIC.feat_IND] + "\t" + first.all_criteria[ValueBlock_STATIC.TF_IND] + "\t";
            else
                out[i + 1] += "\t\t\t\t\t\t\t\t";
            out[i + 1] += BlockMethods.IcJctoijID(last.genes, last.exps) + "\t" +
                    last.genes.length + "\t" + last.exps.length + "\t" +
                    last.pre_criterion + "\t" + last.full_criterion
                    + "\t";
            if (last.all_criteria != null)
                out[i + 1] += last.all_criteria[ValueBlock_STATIC.expr_MEAN_IND] + "\t" + last.all_criteria[ValueBlock_STATIC.expr_MSE_IND] + "\t" +
                        last.all_criteria[ValueBlock_STATIC.expr_FEM_IND] + "\t" +
                        last.all_criteria[ValueBlock_STATIC.expr_KEND_IND] + "\t" +
                        last.all_criteria[ValueBlock_STATIC.expr_COR_IND] + "\t" + last.all_criteria[ValueBlock_STATIC.expr_EUC_IND] + "\t" +
                        last.all_criteria[ValueBlock_STATIC.expr_SPEARMAN_IND] + "\t" +
                        last.all_criteria[ValueBlock_STATIC.interact_IND]
                        + "\t" + last.all_criteria[ValueBlock_STATIC.feat_IND] + "\t" + last.all_criteria[ValueBlock_STATIC.TF_IND];
            else
                out[i + 1] += "\t\t\t\t\t\t\t\t";
        }
        TextFile.write(out, "toplist_summary_table.txt");
    }

    /**
     * @param store
     * @param maxlen
     */
    private void writePre(ArrayList store, int maxlen) {
        ArrayList pre = new ArrayList();
        for (int i = 0; i < store.size(); i++) {
            ValueBlockList vbl = (ValueBlockList) store.get(i);
            double[] dob = MoreArray.initArray(maxlen, Double.NaN);
            for (int j = 0; j < vbl.size(); j++) {
                ValueBlock vb = (ValueBlock) vbl.get(j);
                dob[j] = vb.pre_criterion;
            }
            pre.add(dob);
        }
        double[][] outD = MoreArray.ArrayListto2DDouble(pre);
        String[][] outS = MoreArray.toString(outD, "", "");
        TextFile.write(outS, "toplist_pre_criterion.txt");
    }

    /**
     * @param store
     * @param maxlen
     */
    private void writePercGene(ArrayList store, int maxlen) {
        ArrayList gene = new ArrayList();
        for (int i = 0; i < store.size(); i++) {
            ValueBlockList vbl = (ValueBlockList) store.get(i);
            double[] dob = MoreArray.initArray(maxlen, Double.NaN);
            for (int j = 0; j < vbl.size(); j++) {
                ValueBlock vb = (ValueBlock) vbl.get(j);
                dob[j] = vb.percentOrigGenes;
            }
            gene.add(dob);
        }
        double[][] outD = MoreArray.ArrayListto2DDouble(gene);
        String[][] outS = MoreArray.toString(outD, "", "");
        TextFile.write(outS, "toplist_percgene.txt");
    }

    /**
     * @param store
     * @param maxlen
     */
    private void writePercExp(ArrayList store, int maxlen) {
        ArrayList exp = new ArrayList();
        for (int i = 0; i < store.size(); i++) {
            ValueBlockList vbl = (ValueBlockList) store.get(i);
            double[] dob = MoreArray.initArray(maxlen, Double.NaN);
            for (int j = 0; j < vbl.size(); j++) {
                ValueBlock vb = (ValueBlock) vbl.get(j);
                dob[j] = vb.percentOrigExp;
            }
            exp.add(dob);
        }
        double[][] outD = MoreArray.ArrayListto2DDouble(exp);
        String[][] outS = MoreArray.toString(outD, "", "");
        TextFile.write(outS, "toplist_percexp.txt");
    }

    /**
     * @param store
     */
    private void writeBIC(ArrayList store) {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter("final.bic"), true);
//            pw.println("8000");
//            pw.println("Joachimiak");
            for (int i = 0; i < store.size(); i++) {
                ValueBlockList vbl = (ValueBlockList) store.get(i);
                ValueBlock vb = (ValueBlock) vbl.get(vbl.size() - 1);
                //System.out.println(i + "\t" + vb.toStringShort());
                pw.println(vb.genes.length + " " + vb.exps.length);
                //int halfind = vb.block_id.indexOf("/");
                String genesStr = MoreArray.toString(vb.genes, ",");//StringUtil.replace(vb.block_id.substring(0, halfind), ",", " ");
                String expsStr = MoreArray.toString(vb.exps, ",");//StringUtil.replace(vb.block_id.substring(0, halfind), ",", " ");
                //String expsStr = StringUtil.replace(vb.block_id.substring(halfind + 1), ",", " ");
                pw.println(genesStr);
                pw.println(expsStr);
            }
            pw.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * @param store
     */
    public void writeBICLabels(ArrayList store) {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter("final_labels_gene_exp.bic"), true);
            pw.println("8000");
            pw.println("Joachimiak");
            for (int i = 0; i < store.size(); i++) {
                ValueBlockList vbl = (ValueBlockList) store.get(i);
                ValueBlock vb = (ValueBlock) vbl.get(vbl.size() - 1);
                //System.out.println(i + "\t" + vb.toStringShort());
                pw.println(vb.genes.length + " " + vb.exps.length);

                String genesOut = "";
                String expsOut = "";
                for (int j = 0; j < vb.genes.length; j++) {
                    genesOut += genes.get(vb.genes[j]) + " ";
                }
                for (int j = 0; j < vb.exps.length; j++) {
                    expsOut += exps.get(vb.exps[j]) + " ";
                }
                pw.println(genesOut);
                pw.println(expsOut);
            }
            pw.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * @param store
     */
    private void writeBICLabelsRev(ArrayList store) {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter("final_labels_exp_genes.bic"), true);
            pw.println("8000");
            pw.println("Joachimiak");
            for (int i = 0; i < store.size(); i++) {
                ValueBlockList vbl = (ValueBlockList) store.get(i);
                ValueBlock vb = (ValueBlock) vbl.get(vbl.size() - 1);
                //System.out.println(i + "\t" + vb.toStringShort());
                pw.println(vb.genes.length + " " + vb.exps.length);

                String genesOut = "";
                String expsOut = "";
                for (int j = 0; j < vb.genes.length; j++) {
                    genesOut += genes.get(vb.genes[j]) + " ";
                }
                for (int j = 0; j < vb.exps.length; j++) {
                    expsOut += exps.get(vb.exps[j]) + " ";
                }
                pw.println(expsOut);
                pw.println(genesOut);
            }
            pw.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 1 || args.length == 3) {
            Summarizer rm = new Summarizer(args);
        } else {
            System.out.println("syntax: java DataMining.Summarizer <dir of ouputs> <(optional) gene labels> <(optional) exp labels>");
        }
    }

}
