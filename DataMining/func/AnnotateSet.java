package DataMining.func;

import DataMining.*;
import mathy.Matrix;
import mathy.SimpleMatrix;
import mathy.stat;
import util.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Annotates the input set of biclusters with annotation and other reference data.
 * <p/>
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: Aug 9, 2012
 * Time: 2:21:11 PM
 */
public class AnnotateSet {


    String[] valid_args = {
            "-bic", "-expr", "-tab", "-TFEXP", "-TFREF", "-GOT", "-goyeast", "-PATH", "-out"
    };
    HashMap options;
    ValueBlockList BIC;
    String prefix;
    SimpleMatrix expr_data;
    int datagenemax, dataexpmax;
    String[][] tab_data;
    String[][] TIGR_data;
    String[] yeastnames;
    String[] yeastids;
    String[][] GO_data;
    String[][] GO_yeast_data;
    SimpleMatrix TFtargetmap;
    SimpleMatrix TFREF_data;
    HashMap TIGRmap;
    HashMap TIGRrolemap;
    HashMap COGmap;
    HashMap Pathmap;
    HashMap GO_yeast_data_map = new HashMap();
    ArrayList GO_C = new ArrayList(), GO_P = new ArrayList(), GO_F = new ArrayList();

    HashMap yeast_gene_names = new HashMap();

    long seed = MINER_STATIC.RANDOM_SEEDS[0];//759820;

    int bicluster_set_size = -1;

    ArrayList summary = new ArrayList();

    double GOcover, TFcover, Pathcover, TIGRrolecover;
    HashMap totalGO = new HashMap(), totalPath = new HashMap();
    HashMap foundGO = new HashMap(), foundTF = new HashMap(), foundPath = new HashMap(), foundTIGRrole = new HashMap();

    String outpath = "";

    /**
     * @param args
     */
    public AnnotateSet(String[] args) {

        init(args);

        for (int i = 0; i < BIC.size(); i++) {

            ValueBlock vb = (ValueBlock) BIC.get(i);
            vb.setDataAndMean(expr_data.data);

            String outpath00 = outpath + "" + (vb.index) + "_data.txt";
            System.out.println(outpath00);
            String[] genes = new String[vb.genes.length];
            for (int k = 0; k < vb.genes.length; k++) {
                genes[k] = expr_data.ylabels[vb.genes[k] - 1];
            }
            String[] exps = new String[vb.exps.length];
            for (int k = 0; k < vb.exps.length; k++) {
                exps[k] = expr_data.xlabels[vb.exps[k] - 1];
            }

            System.out.println("data g " + vb.genes.length + "\te " + vb.exps.length + "\th " + genes.length +
                    "\te " + exps.length + "\tg " + vb.exp_data.length + "\te " + vb.exp_data[0].length);
            TabFile.write(vb.exp_data, outpath00, exps, genes);

            ArrayList TFs = assignTF(i);
            ArrayList GOs = assignGO(i);
            ArrayList PATHs = assignPath(i);
            ArrayList TIGRs = assignTIGRrole(i);


            ArrayList out = new ArrayList();

            String wordcloud_data_TF = "";
            String wordcloud_data_GO = "";
            String wordcloud_data_TIGR = "";
            String wordcloud_data_PATH = "";

            String wordcloud_data_TFEXP = assignTFEXP(i);

            String[] gene_ids = new String[vb.genes.length];

            out.add(vb.index + "\t" + vb.blockId() + "\t" + vb.genes.length + "\t" + vb.exps.length);
            for (int j = 0; j < vb.genes.length; j++) {
                gene_ids[j] = expr_data.ylabels[vb.genes[j] - 1];
                String s = (String) TFs.get(j) + "\t" + (String) GOs.get(j) + "\t" + (String) PATHs.get(j) + "\t" + (String) TIGRs.get(j);

                if ((String) TFs.get(j) != "")
                    wordcloud_data_TF += (String) TFs.get(j) + "*";
                if ((String) GOs.get(j) != "")
                    wordcloud_data_GO += (String) GOs.get(j) + "*";
                if ((String) TIGRs.get(j) != "")
                    wordcloud_data_TIGR += (String) TIGRs.get(j) + "*";
                if ((String) PATHs.get(j) != "")
                    wordcloud_data_PATH += (String) PATHs.get(j) + "*";
                //wordcloud_data_TFEXP += (String) TFEXPs.get(j) + "*";

                out.add(s);
            }

            String outpath0 = outpath + "" + (vb.index) + "_geneids.txt";
            System.out.println(outpath0);
            TextFile.write(gene_ids, outpath0);

            String outpath000 = outpath + "" + (vb.index) + "_textdata.txt";
            System.out.println(outpath000);
            TextFile.write(out, outpath000);

            wordcloud_data_TF = prepareWords(wordcloud_data_TF);
            wordcloud_data_GO = prepareWords(wordcloud_data_GO);
            wordcloud_data_TIGR = prepareWords(wordcloud_data_TIGR);
            wordcloud_data_PATH = prepareWords(wordcloud_data_PATH);
            wordcloud_data_TFEXP = prepareWords(wordcloud_data_TFEXP);

            if (wordcloud_data_TF.length() > 1) {
                String outpath1 = outpath + "" + (vb.index) + "_TF_cloud.txt";
                System.out.println(outpath1);
                TextFile.write(wordcloud_data_TF, outpath1);
            }

            if (wordcloud_data_TFEXP.length() > 1) {
                String outpath1 = outpath + "" + (vb.index) + "_TFEXP_cloud.txt";
                System.out.println(outpath1);
                TextFile.write(wordcloud_data_TFEXP, outpath1);
            }


            if (wordcloud_data_GO.length() > 1) {
                String outpath2 = outpath + "" + (vb.index) + "_GO_cloud.txt";
                System.out.println(outpath2);
                TextFile.write(wordcloud_data_GO, outpath2);
            }

            if (wordcloud_data_TIGR.length() > 1) {
                String outpath3 = outpath + "" + (vb.index) + "_TIGR_cloud.txt";
                System.out.println(outpath3);
                TextFile.write(wordcloud_data_TIGR, outpath3);
            }

            if (wordcloud_data_PATH.length() > 1) {
                String outpath4 = outpath + "" + (vb.index) + "_PATH_cloud.txt";
                System.out.println(wordcloud_data_PATH.length() + "\t" + outpath4);
                TextFile.write(wordcloud_data_PATH, outpath4);
            }


            String[] expers = assignExps(i);
            String wordcloud_data_EXP = "";
            String[] outstr = new String[vb.exps.length];
            for (int j = 0; j < vb.exps.length; j++) {
                String s = expers[j];
                outstr[j] = s;
                s = cleanExpLabel(s);
                wordcloud_data_EXP += s + "*";
            }
            if (wordcloud_data_EXP.length() > 1) {
                wordcloud_data_EXP = prepareWords(wordcloud_data_EXP);
                wordcloud_data_EXP = StringUtil.replace(wordcloud_data_EXP, "_", " ");
                String outpath5 = outpath + "" + (vb.index) + "_experiments_cloud.txt";
                System.out.println(outpath5);
                TextFile.write(wordcloud_data_EXP, outpath5);
            }

            String outpath6 = outpath + "" + (vb.index) + "_experiments.txt";
            System.out.println(outpath6);
            TextFile.write(outstr, outpath6);
        }
    }

    /**
     * @param s
     * @return
     */
    private String cleanExpLabel(String s) {
        int ind = s.indexOf(".");
        //remove R index
        s = s.substring(ind + 1);

        int ind2 = s.indexOf(".exp");
        if (ind2 != -1)
            s = s.substring(0, ind2);

        ind2 = s.indexOf(".batch");
        if (ind2 != -1)
            s = s.substring(0, ind2);
        //strip time point

        ind2 = s.indexOf(".min");
        if (ind2 != -1) {
            int ind3 = StringUtil.lastIndexBefore(s, ".", ind2 - 1);
            if (ind3 != -1)
                s = s.substring(0, ind3);
        }
        ind2 = s.indexOf("_min");
        if (ind2 != -1) {
            int ind3 = StringUtil.lastIndexBefore(s, "_", ind2 - 1);
            s = s.substring(0, ind3);
        }
        ind2 = s.indexOf(".minutes");
        if (ind2 != -1) {
            int ind3 = StringUtil.lastIndexBefore(s, ".", ind2 - 1);
            s = s.substring(0, ind3);
        }
        ind2 = s.indexOf("_minutes");
        if (ind2 != -1) {
            int ind3 = StringUtil.lastIndexBefore(s, "_", ind2 - 1);
            s = s.substring(0, ind3);
        }

        ind2 = s.indexOf("hrs");
        if (ind2 != -1) {
            int ind3 = StringUtil.lastIndexBefore(s, ".", ind2 - 1);
            if (ind3 == -1)
                ind3 = StringUtil.lastIndexBefore(s, "_", ind2 - 1);
            s = s.substring(0, ind3);
        }
        ind2 = s.indexOf("hours");
        if (ind2 != -1) {
            int ind3 = StringUtil.lastIndexBefore(s, ".", ind2 - 1);
            if (ind3 == -1)
                ind3 = StringUtil.lastIndexBefore(s, "_", ind2 - 1);
            s = s.substring(0, ind3);
        }
        ind2 = s.indexOf("day");
        if (ind2 != -1) {
            int ind3 = StringUtil.lastIndexBefore(s, "_", ind2 - 1);
            if (ind3 == -1)
                ind3 = StringUtil.lastIndexBefore(s, ".", ind2 - 1);
            s = s.substring(0, ind3);
        }

        s = StringUtil.replace(s, "Pomona", "");
        s = StringUtil.replace(s, "College", "");
        s = StringUtil.replace(s, "Bio164", "");
        s = StringUtil.replace(s, "growth", "");
        s = StringUtil.replace(s, ".Comparison", "");
        s = StringUtil.replace(s, ".Comparison", "");
        s = StringUtil.replace(s, ".comparison", "");
        s = StringUtil.replace(s, ".phase", "");
        s = StringUtil.replace(s, ".rescan", "");
        s = StringUtil.replace(s, ".constant", "");
        s = StringUtil.replace(s, ".redo", "");
        s = StringUtil.replace(s, ".repeat", "");
        s = StringUtil.replace(s, ".test.hyb", "");
        s = StringUtil.replace(s, ".", " ");
        s = StringUtil.replace(s, "_", " ");
        s = StringUtil.replace(s, "  ", " ");
        s = StringUtil.replace(s, ",", "");
        return s;
    }

    /**
     * @param data
     * @return
     */
    private String prepareWords(String data) {
        data = StringUtil.replace(data, "\t", "*");
        data = StringUtil.replace(data, "_", "*");
        data = StringUtil.replace(data, " ", "_");
        data = StringUtil.replace(data, "*", "\n");
        data = StringUtil.replace(data, "\n_\n", "\n");
        data = StringUtil.replace(data, "none", "");

        int len = data.length();
        int lenlast = 0;
        while (len != lenlast) {
            lenlast = len;
            data = StringUtil.replace(data, "\n\n", "\n");
            len = data.length();
        }

        if (data.charAt(0) == '\n') {
            System.out.println("removing firstApply char \\n");
            data = data.substring(1);
        }

        int count = StringUtil.countOccur(data, " ");
        if (count == data.length())
            data = "";
        int count2 = StringUtil.countOccur(data, "\n");
        if (count2 == data.length())
            data = "";
        return data;
    }

    /**
     * @return
     */
    private String[] assignExps(int i) {

        System.out.print(".");
        ValueBlock v = (ValueBlock) BIC.get(i);
        String[] explabels = new String[v.exps.length];

        for (int g = 0; g < explabels.length; g++) {
            explabels[g] = expr_data.xlabels[v.exps[g] - 1];
            explabels[g] = StringUtil.replace(explabels[g], "\"", "");
        }

        return explabels;
    }

    /**
     * @return
     */
    private ArrayList assignPath(int i) {

        ArrayList ret = new ArrayList();

        System.out.print(".");
        ValueBlock v = (ValueBlock) BIC.get(i);
        String[] geneids = new String[v.genes.length];

        String cur = "";
        for (int g = 0; g < geneids.length; g++) {
            geneids[g] = expr_data.ylabels[v.genes[g] - 1];//StringUtil.replace(expr_data.ylabels[v.genes[g] - 1], "\"", "");

            ArrayList Pathar = (ArrayList) Pathmap.get(geneids[g]);
            if (Pathar != null) {
                for (int h = 0; h < Pathar.size(); h++) {
                    String Path = (String) Pathar.get(h);
                    if (Path != null && !Path.equals("")) {
                        cur += Path + "_";
                    }
                }
                ret.add(cur);
            } else
                ret.add("none");
        }

        return ret;
    }

    /**
     * @return
     */
    private ArrayList assignTIGRrole(int i) {

        ArrayList ret = new ArrayList();
        System.out.print(".");
        ValueBlock v = (ValueBlock) BIC.get(i);
        String[] geneids = new String[v.genes.length];

        for (int g = 0; g < geneids.length; g++) {
            String cur = "";
            geneids[g] = expr_data.ylabels[v.genes[g] - 1];//StringUtil.replace(expr_data.ylabels[v.genes[g] - 1], "\"", "");
            ArrayList TIGRrolear = (ArrayList) TIGRrolemap.get(geneids[g]);
            if (TIGRrolear != null) {
                for (int h = 0; h < TIGRrolear.size(); h++) {
                    String TIGRrole = (String) TIGRrolear.get(h);
                    if (TIGRrole != null && !TIGRrole.equals("")) {
                        cur += TIGRrole + "_";
                    }
                }
                ret.add(cur);
            } else
                ret.add("none");
        }

        return ret;
    }


    /**
     * @return
     */
    private ArrayList assignGO(int i) {

        ArrayList ret = new ArrayList();
        System.out.print(".");
        ValueBlock v = (ValueBlock) BIC.get(i);
        String[] geneids = new String[v.genes.length];
        for (int g = 0; g < geneids.length; g++) {
            geneids[g] = expr_data.ylabels[v.genes[g] - 1];//StringUtil.replace(expr_data.ylabels[v.genes[g] - 1], "\"", "");
            ArrayList GOar = (ArrayList) GO_yeast_data_map.get(geneids[g]);
            String cur = "";
            if (GOar != null) {
                for (int h = 0; h < GOar.size(); h++) {
                    String[] GO = (String[]) GOar.get(h);
                    if (!GO[4].equals("")) {
                        cur += "" + GO[4] + "_";
                    }
                }
                if (cur.length() > 0)
                    ret.add(cur);
                else
                    ret.add("none");
            } else
                ret.add("none");
        }

        return ret;
    }


    /**
     * @return
     */
    private ArrayList assignTF(int i) {

        ArrayList ret = new ArrayList();

        //for (int i = 0; i < size; i++) {
        System.out.print(".");
        ValueBlock v = (ValueBlock) BIC.get(i);
        String[] geneids = new String[v.genes.length];
        ArrayList samps = new ArrayList();
        for (int g = 0; g < geneids.length; g++) {
            geneids[g] = expr_data.ylabels[v.genes[g] - 1];//StringUtil.replace(expr_data.ylabels[v.genes[g] - 1], "\"", "");

            int index = MoreArray.getArrayIndUniqueIgnoreCase(TFREF_data.xlabels, geneids[g]);
            double[] datacol = Matrix.extractColumn(TFREF_data.data, index);

            String cur = "";
            for (int j = 0; j < datacol.length; j++) {
                if (datacol[j] > 0) {
                    cur += TFREF_data.ylabels[j].substring(0, TFREF_data.ylabels[j].length() - 1) + "_";
                }
            }
            if (cur.length() > 0)
                ret.add(cur);
            else
                ret.add("none");
        }
        //}


        return ret;
    }

    /**
     * @param i
     * @return
     */
    private String assignTFEXP(int i) {
        String TFlabel = "";
        System.out.print(".");
        ValueBlock v = (ValueBlock) BIC.get(i);

        ArrayList get = TFCrit.getTF8merRankList(v.genes, TFtargetmap, expr_data.ylabels, false);
        if (((String) get.get(1)).length() > 1)
            TFlabel = (String) get.get(1);
        for (int g = 2; g < get.size(); g++) {
            if (((String) get.get(g)).length() > 1)
                TFlabel += " * " + (String) get.get(g);
        }

        return TFlabel;
    }


    /**
     * @param i
     * @return
     */
    private String assignTFEXPOld(int i) {
        String TFlabel = "";
        ArrayList profiles = new ArrayList();
        System.out.print(".");
        ValueBlock v = (ValueBlock) BIC.get(i);
        String[] geneids = new String[v.genes.length];
        ArrayList data = new ArrayList();
        for (int g = 0; g < geneids.length; g++) {
            geneids[g] = expr_data.ylabels[v.genes[g] - 1];//StringUtil.replace(expr_data.ylabels[v.genes[g] - 1], "\"", "");
            int index = MoreArray.getArrayIndUnique(TFtargetmap.ylabels, geneids[g]);
            if (index != -1) {
                data.add(TFtargetmap.data[index]);
            }
        }
        if (data.size() > 0) {
            double[] mean = stat.arraySampAvg(data);
            profiles.add(mean);
            double min = stat.findMin(mean);
            int maxindex = stat.findMinIndex(mean);
            TFlabel = TFtargetmap.xlabels[maxindex];
            System.out.println("assignTFEXP " + TFlabel);
        }

        return TFlabel;
    }

    /**
     * @param g
     * @return
     */
    public final static String[] removeGO(String[] g) {

        g = StringUtil.replace(g, "structural molecule activity", "");
        g = StringUtil.replace(g, "molecular_function", "");
        g = StringUtil.replace(g, "biological_process", "");
        g = StringUtil.replace(g, "GO:0008150", "");
        g = StringUtil.replace(g, "GO:0003674", "");
        g = StringUtil.replace(g, "GO:0005198", "");
        g = StringUtil.replace(g, "ORF|Verified", "");
        g = StringUtil.replace(g, "ORF|Dubious", "");
        g = StringUtil.replace(g, "other", "");
        g = StringUtil.replace(g, "not_yet_annotated", "");

        return g;
    }


    /**
     * @param args
     */
    private void init(String[] args) {
        MoreArray.printArray(args);

        ParsePath pp = null;

        options = MapArgOptions.maptoMap(args, valid_args);

        String inBIC = null;

        if (options.get("-bic") != null) {
            inBIC = (String) options.get("-bic");
            try {
                BIC = ValueBlockListMethods.readAny(inBIC, false);
                bicluster_set_size = BIC.size();
            } catch (Exception e) {
                e.printStackTrace();
            }
            pp = new ParsePath(inBIC);
            if (BIC != null) {
                System.out.println("BIC.bicluster_set_size()" + BIC.size());
                /*for (int i = 0; i < BIC.bicluster_set_size(); i++) {
                    ValueBlock v = (ValueBlock) BIC.get(i);
                    BICmap.put(v.index, i);
                }*/
            }
        }
        if (options.get("-expr") != null) {
            String f = (String) options.get("-expr");
            expr_data = new SimpleMatrix(f);

            expr_data.xlabels = StringUtil.replace(expr_data.xlabels, "\"", "");
            expr_data.ylabels = StringUtil.replace(expr_data.ylabels, "\"", "");

            //genes
            datagenemax = expr_data.data.length;
            //exps
            dataexpmax = expr_data.data[0].length;

            expr_data.ylabels = StringUtil.replace(expr_data.ylabels, "\"", "");
        }
        if (options.get("-tab") != null) {
            String f = (String) options.get("-tab");
            tab_data = TabFile.readtoArray(f);

            System.out.println("read tab_data " + tab_data.length + "\t" + tab_data[0].length);
            TIGR_data = new String[tab_data.length][3];
            TIGRmap = new HashMap();
            TIGRrolemap = new HashMap();
            COGmap = new HashMap();
            for (int i = 1; i < tab_data.length; i++) {
                tab_data[i] = StringUtil.replace(tab_data[i], "\"", "");

                TIGR_data[i][0] = tab_data[i][7];
                TIGR_data[i][1] = tab_data[i][13];
                TIGR_data[i][2] = tab_data[i][14];
                if (tab_data[i][13] != null && tab_data[i][13].length() > 0) {
                    Object o = TIGRmap.get(tab_data[i][7]);
                    if (o == null) {
                        ArrayList add = new ArrayList();
                        add.add(tab_data[i][13]);
                        TIGRmap.put(tab_data[i][7], add);
                        //System.out.println("adding TIGR 1 " + tab_data[i][7] + "\t*" + tab_data[i][13] + "*");

                    } else {
                        ArrayList add = (ArrayList) o;
                        add.add(tab_data[i][13]);
                        TIGRmap.put(tab_data[i][7], add);
                        //System.out.println("adding TIGR 2 " + tab_data[i][7] + "\t*" + tab_data[i][13] + "*");
                    }
                }
                if (tab_data[i][14] != null && tab_data[i][14].length() > 0) {
                    Object o2 = TIGRrolemap.get(tab_data[i][7]);
                    int index = tab_data[i][14].indexOf(":");
                    String trimRole = tab_data[i][14].substring(0, index);
                    if (o2 == null) {
                        ArrayList add = new ArrayList();
                        add.add(trimRole);
                        TIGRrolemap.put(tab_data[i][7], add);
                        //System.out.println("adding 1 " + tab_data[i][14]);
                    } else {
                        ArrayList add = (ArrayList) o2;
                        add.add(trimRole);
                        TIGRrolemap.put(tab_data[i][7], add);
                        //System.out.println("adding 2 " + tab_data[i][14]);
                    }
                }
                if (tab_data[i][11] != null && tab_data[i][11].length() > 0) {
                    Object o2 = COGmap.get(tab_data[i][7]);
                    if (o2 == null) {
                        ArrayList add = new ArrayList();
                        add.add(tab_data[i][11]);
                        COGmap.put(tab_data[i][7], add);
                        //System.out.println("adding 1 " + tab_data[i][14]);
                    } else {
                        ArrayList add = (ArrayList) o2;
                        add.add(tab_data[i][11]);
                        COGmap.put(tab_data[i][7], add);
                        //System.out.println("adding 2 " + tab_data[i][14]);
                    }
                }
            }
            yeastids = new String[tab_data.length - 1];
            yeastnames = new String[tab_data.length - 1];
            for (int i = 1; i < tab_data.length; i++) {
                yeastids[i - 1] = tab_data[i][7];
                yeastnames[i - 1] = tab_data[i][8];
            }

            System.out.println("yeastnames");
            System.out.println(MoreArray.toString(yeastnames, ","));
        }

        //Pathmap
        if (options.get("-GOT") != null) {
            String f = (String) options.get("-GOT");
            GO_data = TabFile.readtoArray(f);
        }
        if (options.get("-goyeast") != null) {
            String f = (String) options.get("-goyeast");
            GO_yeast_data = TabFile.readtoArray(f);

            for (int i = 0; i < GO_yeast_data.length; i++) {
                GO_yeast_data[i] = StringUtil.replace(GO_yeast_data[i], "\"", "");
                Object n = yeast_gene_names.get(GO_yeast_data[i][0]);
                if (n == null) {
                    //System.out.println("adding GO_yeast_data " + GO_yeast_data[i][0] + "\t" + GO_yeast_data[i][1]);
                    yeast_gene_names.put(GO_yeast_data[i][0], GO_yeast_data[i][1]);
                }

                //System.out.println("goyeast " + i + "\t" + MoreArray.toString(GO_yeast_data[i], ","));
                Object o = GO_yeast_data_map.get(GO_yeast_data[i][0]);
                if (o == null) {
                    String[] add = removeGO(GO_yeast_data[i]);
                    //for (int j = 0; j < add.length; j++) {
                    Object g = totalGO.get(add[4]);
                    if (g == null) {
                        totalGO.put(add[4], 1);
                    } else {
                        int iv = (Integer) g;
                        totalGO.put(add[4], iv + 1);
                    }
                    //}
                    GO_yeast_data_map.put(GO_yeast_data[i][0], add);
                    //GO_anot_map.put(GO_yeast_data[i][0],(GO_yeast_data[i][2] + GO_yeast_data[i][3]));
                } else {
                    String[] add = removeGO(GO_yeast_data[i]);
                    String s = add[4];
                    Object g = totalGO.get(s);
                    if (g == null) {
                        totalGO.put(s, 1);
                    } else {
                        int iv = (Integer) g;
                        totalGO.put(s, iv + 1);
                    }
                    //}

                    boolean islist = false;
                    ArrayList a = null;
                    try {
                        a = (ArrayList) o;
                        islist = true;
                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
                    //if not list convert to list and add next
                    if (!islist) {
                        ArrayList ar = new ArrayList();
                        //ar.add(o);
                        ar.add(add);
                        GO_yeast_data_map.put(GO_yeast_data[i][0], ar);
                    }
                    //if list add to it
                    else {
                        a.add(add);
                        GO_yeast_data_map.put(GO_yeast_data[i][0], a);
                    }
                }

                if (GO_yeast_data[i][5].length() > 0)
                    //System.out.println("GO_yeast_data[i][3] " + GO_yeast_data[i][3]);
                    if (GO_yeast_data[i][3].equals("C")) {
                        int ind = MoreArray.getArrayInd(GO_C, GO_yeast_data[i][5]);
                        if (ind == -1) {
                            GO_C.add(GO_yeast_data[i][5]);
                            //System.out.println("adding GO_C " + GO_yeast_data[i][5]);
                        }
                    } else if (GO_yeast_data[i][3].equals("F")) {
                        int ind = MoreArray.getArrayInd(GO_F, GO_yeast_data[i][5]);
                        if (ind == -1) {
                            GO_F.add(GO_yeast_data[i][5]);
                            //System.out.println("adding GO_F " + GO_yeast_data[i][5]);
                        }
                    } else if (GO_yeast_data[i][3].equals("P")) {
                        int ind = MoreArray.getArrayInd(GO_P, GO_yeast_data[i][5]);
                        if (ind == -1) {
                            GO_P.add(GO_yeast_data[i][5]);
                            //System.out.println("adding GO_P " + GO_yeast_data[i][5]);
                        }
                    }
            }
        }

        if (options.get("-out") != null) {
            outpath = (String) options.get("-out") + "/";
            File fi = new File(outpath);
            if (!fi.isDirectory()) {
                fi.mkdir();
            }
        }
        if (options.get("-TFEXP") != null) {
            String f = (String) options.get("-TFEXP");
            TFtargetmap = new SimpleMatrix(f);
            TFtargetmap = InitRandVar.loadTFranks(f);
            TFtargetmap.xlabels = StringUtil.replace(TFtargetmap.xlabels, "\"", "");
            TFtargetmap.ylabels = StringUtil.replace(TFtargetmap.ylabels, "\"", "");
        }
        if (options.get("-TFREF") != null) {
            String f = (String) options.get("-TFREF");
            TFREF_data = new SimpleMatrix(f);
            TFREF_data.xlabels = StringUtil.replace(TFREF_data.xlabels, "\"", "");
            TFREF_data.ylabels = StringUtil.replace(TFREF_data.ylabels, "\"", "");
            //System.out.println("motif_data.xlabels");
            //MoreArray.printArray(motif_data.xlabels);
        }


        if (options.get("-PATH") != null) {
            String f = (String) options.get("-PATH");
            String[][] tab_data2 = TabFile.readtoArray(f, true);
            //ArrayList tabdata = TextFile.readtoList(f);

            // System.out.println("read tab_data2 " + f + "\t" + tabdata.size());// + "\t" + tabdata[0].length);
            System.out.println("read tab_data2 " + f + "\t" + tab_data2.length + "\t" + tab_data2[0].length);
            Pathmap = new HashMap();
            //for (int i = 0; i < tabdata.size(); i++) {
            for (int i = 0; i < tab_data2.length; i++) {
                //String[] tab_data2 = ((String) tab_data2.get(i)).split("\t");

                //System.out.println("Pathmap " + i + "\t" + MoreArray.toString(tab_data2[i], ","));
                if (tab_data2[i].length > 3) {
                    tab_data2[i] = StringUtil.replace(tab_data2[i], "\"", "");
                    //String name = tab_data2[i][3];
                    if (tab_data2[i][3] != null) {
                        int index = StringUtil.getFirstEqualsIndex(yeastnames, tab_data2[i][3]);
                        //System.out.println("Pathmap " + name + "\t" + index);
                        if (index != -1) {
                            String yid = yeastids[index];
                            if (tab_data2[i][0] != null && tab_data2[i][0].length() > 0) {
                                //String s = tab_data2[i][0];
                                Object o2 = Pathmap.get(yid);
                                if (o2 == null) {
                                    ArrayList add = new ArrayList();
                                    Object g = totalPath.get(tab_data2[i][0]);
                                    if (g == null) {
                                        totalPath.put(tab_data2[i][0], 1);
                                    } else {
                                        int iv = (Integer) g;
                                        totalPath.put(tab_data2[i][0], iv + 1);
                                    }

                                    add.add(tab_data2[i][0]);
                                    Pathmap.put(yid, add);
                                    //System.out.println("Pathmap adding 1 " + tab_data2[i][0]);
                                } else {
                                    Object g = totalPath.get(tab_data2[i][0]);
                                    if (g == null) {
                                        totalPath.put(tab_data2[i][0], 1);
                                    } else {
                                        int iv = (Integer) g;
                                        totalPath.put(tab_data2[i][0], iv + 1);
                                    }

                                    ArrayList add = (ArrayList) o2;
                                    add.add(tab_data2[i][0]);
                                    Pathmap.put(yid, add);
                                    //System.out.println("Pathmap adding 2 " + tab_data2[i][0]);
                                }
                            }
                        }
                    } else {
                        System.out.println("Pathmap name is null " + i + "\t" + tab_data2[i][3] + "\t" + MoreArray.toString(tab_data2[i], ","));
                    }
                }
            }
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 16 || args.length == 18) {
            AnnotateSet rm = new AnnotateSet(args);
        } else {
            System.out.println("syntax: java DataMining.func.AnnotateSet\n" +
                    "<-bic valueblock list>\n" +
                    "<-expr expr data path>\n" +
                    "<-out OPTIONAL output path>\n" +
                    "<-tab  tab genome annotation file>\n" +
                    "<-GOT  GO annotation file>\n" +
                    "<-goyeast  GO yeast annotation file>\n" +
                    "<-TFEXP  TF-gene experimental data file>\n" +
                    "<-TFREF  TF-gene mapping file>\n" +
                    "<-PATH  pathway-gene mapping file>"
            );
        }
    }
}