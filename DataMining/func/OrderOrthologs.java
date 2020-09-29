package DataMining.func;

import mathy.Matrix;
import mathy.SimpleMatrix;
import mathy.stat;
import util.MoreArray;
import util.TabFile;
import util.TextFile;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: Feb 16, 2012
 * Time: 9:33:52 PM
 */
public class OrderOrthologs {

    /**
     * @param args
     */
    public OrderOrthologs(String[] args) {

        SimpleMatrix sm = new SimpleMatrix(args[0]);

        System.out.println("first row " + MoreArray.toString(sm.data[0], ","));

        String[] target_genes = TextFile.readtoArray(args[1]);
        String[][] coords_str = TabFile.readtoArray(args[2]);
        String[][] species_dist_data = TabFile.readtoArray(args[3]);

        String[] species_order = MoreArray.extractColumnStr(species_dist_data, 3);
        String[] startstop_order = MoreArray.extractColumnStr(coords_str, 1);

        int[] start = MoreArray.extractColumnInt(coords_str, 2);
        int[] stop = MoreArray.extractColumnInt(coords_str, 3);

        //find indices of target genes in matrix labels
        int[] og_index = new int[target_genes.length];
        int[] og_ss_index = new int[target_genes.length];
        for (int i = 0; i < target_genes.length; i++) {
            og_index[i] = MoreArray.getArrayInd(sm.ylabels, target_genes[i]);
            og_ss_index[i] = MoreArray.getArrayInd(startstop_order, target_genes[i]);
        }

        //sort by index in start/stop
        ArrayList newtarget = MoreArray.initArrayList(target_genes.length);//new ArrayList();
        int[] sort_og_ss_index = MoreArray.copy(og_ss_index);
        Arrays.sort(sort_og_ss_index);
        for (int i = 0; i < target_genes.length; i++) {
            og_ss_index[i] = MoreArray.getArrayInd(startstop_order, target_genes[i]);
            int index = MoreArray.getArrayIndexOf(sort_og_ss_index, og_ss_index[i]);
            newtarget.set(index, target_genes[i]);
        }

        for (int i = 0; i < newtarget.size(); i++) {
            if (newtarget.get(i) == null) {
                newtarget.remove(i);
                i--;
            }
        }

        System.out.println("new vs old target " + newtarget.size() + "\t" + target_genes.length);
        target_genes = MoreArray.ArrayListtoString(newtarget);

        //refresh indices for sorted targets
        og_index = new int[target_genes.length];
        og_ss_index = new int[target_genes.length];
        for (int i = 0; i < target_genes.length; i++) {
            og_index[i] = MoreArray.getArrayInd(sm.ylabels, target_genes[i]);
            og_ss_index[i] = MoreArray.getArrayInd(startstop_order, target_genes[i]);
        }


        ArrayList usedspecies = new ArrayList();
        for (int i = 0; i < sm.xlabels.length; i++) {
            int test = MoreArray.getArrayInd(sm.xlabels, species_order[i]);
            if (test != -1)
                usedspecies.add(species_order[i]);
        }
        species_order = MoreArray.ArrayListtoString(usedspecies);

        //find indices of ordered genes for matrix labels
        int[] order_index = new int[sm.xlabels.length];
        for (int i = 0; i < sm.xlabels.length; i++) {
            order_index[i] = MoreArray.getArrayInd(species_order, sm.xlabels[i]);
        }

        int[] ss_index = new int[sm.ylabels.length];
        for (int i = 0; i < sm.ylabels.length; i++) {
            ss_index[i] = MoreArray.getArrayInd(startstop_order, sm.ylabels[i]);
        }

        ArrayList newdataAr = new ArrayList();
        ArrayList newlabelsAr = new ArrayList();
        double[] zeroarray = MoreArray.initArray(sm.data[0].length, 0.0);

        ArrayList TFsums = new ArrayList();
        ArrayList TFmeanhamming = new ArrayList();

        for (int i = 0; i < target_genes.length; i++) {

            ArrayList curset = new ArrayList();

            int curyind = og_index[i];

            if (curyind == -1) {
                System.out.println("curyind == -1 " + i + "\t" + target_genes[i]);
            } else {
                int curstartstop = og_ss_index[i];

                int maxback = Math.min(10, curstartstop);
                //10 before
                for (int j = 0; j < maxback; j++) {
                    String curg = startstop_order[curstartstop - maxback + j];
                    int index = MoreArray.getArrayInd(sm.ylabels, curg);
                    if (index == -1) {
                        System.out.println("index == -1 " + curg);
                    } else {
                        curset.add(sm.data[index]);
                        newdataAr.add(sm.data[index]);
                        newlabelsAr.add(curg);
                    }
                }
                //current
                newdataAr.add(sm.data[curyind]);
                curset.add(sm.data[curyind]);
                newlabelsAr.add("*****" + target_genes[i]);

                TFsums.add("" + stat.sumEntries(sm.data[curyind]));

                int maxfor = Math.min(10, startstop_order.length - curstartstop);
                //10 after
                for (int j = 0; j < maxfor; j++) {
                    String curg = startstop_order[curstartstop + j + 1];
                    int index = MoreArray.getArrayInd(sm.ylabels, curg);
                    if (index == -1) {
                        System.out.println("gene not found " + curg);
                    } else {
                        newdataAr.add(sm.data[index]);
                        curset.add(sm.data[index]);
                        newlabelsAr.add(curg);
                    }
                }

                int[][] passdata = MoreArray.doubletoIntMatrix(MoreArray.ArrayListto2DDouble(curset));
                double mh = stat.meanPairHamming(passdata);
                TFmeanhamming.add("" + mh);

                newlabelsAr.add("empty");
                newdataAr.add(zeroarray);
                newlabelsAr.add("empty");
                newdataAr.add(zeroarray);
                newlabelsAr.add("empty");
                newdataAr.add(zeroarray);
            }
        }

        TextFile.write(TFsums, "TFsums.txt");
        TextFile.write(TFmeanhamming, "TFmeanhamming.txt");

        String[] newylabels = MoreArray.ArrayListtoString(newlabelsAr);
        double[][] new_data = MoreArray.ArrayListto2DDouble(newdataAr);

        System.out.println("new_data " + new_data.length + "\t" + new_data[0].length);

        double[][] ordered_data = new double[new_data.length][sm.data[0].length];
        System.out.println("ordered_data " + ordered_data.length + "\t" + ordered_data[0].length);
        //ArrayList neworderAr = new ArrayList();
        String[] neworderxlabels = new String[sm.data[0].length];

        //for each taxa do order
        for (int i = 0; i < sm.xlabels.length; i++) {
            double[] extract = Matrix.extractColumn(new_data, i + 1);

            if (order_index[i] > sm.data[0].length) {
                System.out.println("WARNING skipping i " + i + "\t" + sm.xlabels[i] + "\tordered_data " + ordered_data.length +
                        "\tordered_data " + ordered_data[0].length +
                        "\textract " + extract.length + "\tneworder " + order_index[i]);
                //neworderxlabels[neworder] = "none";
            } else {
                ordered_data = Matrix.replaceColumn(ordered_data, extract, order_index[i] + 1);
                //ordered_data[i] = extract;
                //neworderAr.add(species_order[neworder]);
                //System.out.println("neworderxlabels " + neworder + "\t" + neworderxlabels.length + "\tspecies_order " + species_order.length);
                //neworderxlabels[neworder] = species_order[neworder];
            }
        }

        //String[] newxlabels = MoreArray.ArrayListtoString(neworderAr);
        System.out.println("ordered_data " + ordered_data.length + "\t" + ordered_data[0].length + "\tnewylabels " +
                newylabels.length + "\tnewxlabels " + neworderxlabels.length);

        TabFile.write(ordered_data, args[0] + "_speciesorder.txt", species_order, newylabels);
    }


    /**
     * @param args
     */
    public final static void main(String[] args) {
        if (args.length == 4) {
            OrderOrthologs rm = new OrderOrthologs(args);
        } else {
            System.out.println("syntax: java DataMining.func.OrderOrthologs\n" +
                    "<gene x taxa ortholog matrix VIMSS ids>\n" +
                    "<order gene subset VIMSS ids>\n" +
                    "<gene coordinates:locusId begin end strand>\n" +
                    "<species distance:locusId begin end strand>"
            );
        }
    }
}
