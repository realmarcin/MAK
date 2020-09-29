package DataMining.util;

import DataMining.*;
import util.MoreArray;
import util.StringUtil;
import util.TabFile;
import util.TextFile;

import java.io.File;
import java.util.*;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: 3/14/14
 * Time: 11:39 PM
 */
public class BiclusterOverlapRegions {

    boolean debug = true;

    ValueBlockList vbl;

    HashMap globalcountdiffRgenes = new HashMap();
    HashMap globalcountdiffLgenes = new HashMap();

    ValueBlockList intersect_vbl = new ValueBlockList();
    HashMap intersect_genenames = new HashMap();
    ArrayList intersect_vbl_names = new ArrayList();
    ArrayList intersect_vbl_out = new ArrayList();

    String[] gene_labels;
    String[] yeastids;
    String[] yeastnames;

    double frxncut = 0.1;

    /**
     * @param args
     */
    public BiclusterOverlapRegions(String[] args) {

        init(args);

        for (int i = 0; i < vbl.size(); i++) {
            ValueBlock vb1 = (ValueBlock) vbl.get(i);
            for (int j = i + 1; j < vbl.size(); j++) {
                String label = "" + i + "_" + j;
                ValueBlock vb2 = (ValueBlock) vbl.get(j);

                double frxn1 = BlockMethods.computeBlockOverlapGeneExpFraction(vb1, vb2, false);
                double frxn2 = BlockMethods.computeBlockOverlapGeneExpFraction(vb2, vb1, false);

                if (frxn1 > frxncut && frxn2 > frxncut) {

                    int[] commongenes = MoreArray.crossFirstIndex(vb1.genes, vb2.genes);
                    int[] commonexps = MoreArray.crossFirstIndex(vb1.exps, vb2.exps);

                    //int[] commongenes_nominone = MoreArray.removeByVal(commongenes, new int[]{-1});
                    //int[] commonexps__nominone = MoreArray.removeByVal(commonexps, new int[]{-1});


                    ArrayList arg = new ArrayList();
                    for (int a = 0; a < commongenes.length; a++) {
                        if (commongenes[a] != -1) {
                            int cur = vb2.genes[commongenes[a]];
                            arg.add(cur);
                        }
                    }
                    ArrayList are = new ArrayList();
                    for (int b = 0; b < commonexps.length; b++) {
                        if (commonexps[b] != -1) {
                            int cur = vb2.exps[commonexps[b]];
                            are.add(cur);
                        }
                    }

                    int[] intersectg = MoreArray.ArrayListtoInt(arg);
                    int[] intersecte = MoreArray.ArrayListtoInt(are);
                    ValueBlock common = new ValueBlock(intersectg, intersecte);

                    if (intersectg.length > 0 && intersecte.length > 0) {

                        intersect_vbl.add(common);

                        intersect_vbl_names.add(label);
                        intersect_vbl_out.add(label + "\t" + frxn1 + "\t" + frxn2);

                        ArrayList commonnames = new ArrayList();
                        for (int z = 0; z < vb1.genes.length; z++) {
                            try {
                                int arrayInd = MoreArray.getArrayInd(intersectg, vb1.genes[z]);
                                if (arrayInd != -1) {
                                    commonnames.add(yeastids[vb1.genes[z] - 1]);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        System.out.println("number of common genes " + commonnames.size() + "\t" + vb1.genes.length + "\t" + vb2.genes.length);
                        intersect_genenames.put(label, MoreArray.ArrayListtoString(commonnames));

                        ArrayList diffRnames = new ArrayList();
                        ArrayList diffLnames = new ArrayList();
                        for (int z = 0; z < vb1.genes.length; z++) {
                            try {
                                int arrayInd = MoreArray.getArrayInd(intersectg, vb1.genes[z]);
                                if (arrayInd == -1) {
                                    diffRnames.add(yeastnames[vb1.genes[z] - 1]);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        System.out.println("number of R genes " + diffRnames.size() + "\t" + vb1.genes.length + "\t" + vb2.genes.length);

                        for (int z = 0; z < vb2.genes.length; z++) {
                            try {
                                int arrayInd = MoreArray.getArrayInd(intersectg, vb2.genes[z]);
                                if (arrayInd == -1) {
                                    diffLnames.add(yeastnames[vb2.genes[z] - 1]);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        System.out.println("number of L genes " + diffLnames.size() + "\t" + vb1.genes.length + "\t" + vb2.genes.length);

                        globalcountdiffRgenes.put(label, MoreArray.ArrayListtoString(diffRnames));
                        globalcountdiffLgenes.put(label, MoreArray.ArrayListtoString(diffLnames));
                    } else {
                        System.out.println("skipping output  " + i + "\t" + j + "\t" + frxn1 + "\t" + frxn2);
                    }
                }
            }
        }


        Set s3a = globalcountdiffRgenes.entrySet();
        Set s3b = globalcountdiffLgenes.entrySet();
        Set s3c = intersect_genenames.entrySet();

        ArrayList ids = new ArrayList();

        Iterator it3c = s3c.iterator();
        while (it3c.hasNext()) {
            Map.Entry cur = (Map.Entry) it3c.next();
            ids.add(cur.getKey() + "\t" + MoreArray.toString((String[]) cur.getValue(), ","));
        }
        String outpath5c = args[0] + "_intersectgenes_" + ".txt";
        TextFile.write(ids, outpath5c);


        ArrayList idsR = new ArrayList();

        Iterator it3a = s3a.iterator();
        while (it3a.hasNext()) {
            Map.Entry cur = (Map.Entry) it3a.next();
            idsR.add(cur.getKey() + "\t" + MoreArray.toString((String[]) cur.getValue(), ","));
        }
        String outpath5a = args[0] + "_Rgenes_" + ".txt";
        TextFile.write(idsR, outpath5a);

        ArrayList idsL = new ArrayList();
        Iterator it3b = s3b.iterator();
        while (it3b.hasNext()) {
            Map.Entry cur = (Map.Entry) it3b.next();
            idsL.add(cur.getKey() + "\t" + MoreArray.toString((String[]) cur.getValue(), ","));
        }
        String outpath5b = args[0] + "_Lgenes_" + ".txt";
        TextFile.write(idsL, outpath5b);


        String stoplist = intersect_vbl.toString("#" + "\n" + MINER_STATIC.HEADER_VBL);// ValueBlock.toStringShortColumns());
        String outpath7 = args[0] + "_intersect_" + ".vbl";
        System.out.println("writing " + outpath7);
        TextFile.write(stoplist, outpath7);

        String outpath9 = args[0] + "_intersect_names_" + ".txt";
        TextFile.write(intersect_vbl_names, outpath9);

        String outpath10 = args[0] + "_intersect_names_wdist" + ".txt";
        TextFile.write(intersect_vbl_out, outpath10);

        System.exit(0);
    }


    /**
     * @param args
     */
    private void init(String[] args) {


        try {
            vbl = ValueBlockListMethods.readAny(args[0], false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        File testg = new File(args[2]);
        if (testg.exists()) {
            try {
                String[][] sarray = TabFile.readtoArray(args[2]);
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
        }
        String[][] tab_data = TabFile.readtoArray(args[1]);
        System.out.println("read tab_data " + tab_data.length + "\t" + tab_data[0].length);
        yeastids = new String[tab_data.length - 1];
        yeastnames = new String[tab_data.length - 1];
        for (int i = 1; i < tab_data.length; i++) {
            yeastids[i - 1] = tab_data[i][7];
            yeastnames[i - 1] = StringUtil.replace(tab_data[i][8], ",", "_");
            yeastnames[i - 1] = StringUtil.replace(tab_data[i][8], "'", "_");
        }
        if (debug) {
            System.out.println("yeastnames");
            System.out.println(MoreArray.toString(yeastnames, ","));
        }

    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 3) {
            BiclusterOverlapRegions rm = new BiclusterOverlapRegions(args);
        } else {
            System.out.println("syntax: java DataMining.util.BiclusterOverlapRegions\n" +
                    "<vbl>\n" +
                    "<tab file path>\n" +
                    "<gene labels>"

            );
        }
    }
}
