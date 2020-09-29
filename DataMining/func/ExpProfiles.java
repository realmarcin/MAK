package DataMining.func;

import DataMining.ValueBlock;
import DataMining.ValueBlockList;
import DataMining.ValueBlockListMethods;
import mathy.Matrix;
import mathy.SimpleMatrix;
import util.MoreArray;
import util.StringUtil;
import util.TabFile;
import util.TextFile;

import java.util.*;

/**
 * Build experiment vs. GO term profile data.
 * <p/>
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: 4/3/13
 * Time: 2:14 PM
 */
public class ExpProfiles {

    String[] exp_labels;
    String[] gene_labels;
    ValueBlockList vbl;
    SimpleMatrix sm;
    ArrayList<HashMap> arHashExp = new ArrayList<HashMap>();
    ArrayList<HashMap> arHashGene = new ArrayList<HashMap>();

    HashMap GOmap;
    HashMap yeast_gene_names;
    HashMap<Integer, String> sigGOmap;
    ArrayList globalExp = new ArrayList();
    ArrayList globalExpInd = new ArrayList();

    String prefix;

    String[][] tab_data;
    String[] genenames;
    String[] geneids;


    /**
     * @param args
     */
    public ExpProfiles(String[] args) {

        try {
            init(args);
        } catch (Exception e) {
            e.printStackTrace();
        }

        buildExpWeight();
        buildGeneWeight();

        for (int i = 0; i < vbl.size(); i++) {
            ValueBlock vb = (ValueBlock) vbl.get(i);
            HashMap twoE = arHashExp.get(i);
            HashMap twoG = arHashGene.get(i);
            doSingle(i, twoE, "_exps.txt", true);
            doSingle(i, twoG, "_genes.txt", false);
        }


        ArrayList master = new ArrayList();
        for (int i = 0; i < vbl.size(); i++) {
            ArrayList cur = (ArrayList) globalExp.get(i);
            for (int j = 0; j < cur.size(); j++) {
                String s = (((String) cur.get(j)).split("\t"))[0];
                if (master.indexOf(s) == -1)
                    master.add(s);
            }
        }

        Collections.sort(master);
        int[][] out = new int[vbl.size()][master.size()];
        String[] ylabels = new String[vbl.size()];

        for (int i = 0; i < vbl.size(); i++) {
            ylabels[i] = "" + (i + 1);
            ArrayList cur = (ArrayList) globalExp.get(i);
            for (int j = 0; j < cur.size(); j++) {
                String[] split = ((String) cur.get(j)).split("\t");
                int ind = master.indexOf(split[0]);
                out[i][ind] = (int) Double.parseDouble(split[1]);
            }
        }

        String[] xlabels = MoreArray.ArrayListtoString(master);
        TabFile.write(out, prefix + "_expprofiles.txt", xlabels, ylabels);


        ArrayList masterGO = new ArrayList();

        Set keysi = sigGOmap.keySet();
        Iterator it = keysi.iterator();
        while (it.hasNext()) {
            int key = (Integer) it.next();
            String cur = sigGOmap.get(key);
            String[] split = cur.split("_");
            for (int j = 0; j < split.length; j++) {
                if (split[j].length() > 0 && masterGO.indexOf(split[j]) == -1)
                    masterGO.add(split[j]);
            }
        }
        Collections.sort(masterGO);

        int[][] out2 = new int[masterGO.size()][master.size() * 2];
        //for each block
        for (int i = 0; i < vbl.size(); i++) {
            ValueBlock vb = (ValueBlock) vbl.get(i);
            vb.setDataAndMean(sm.data);
            //get experiments and indices
            ArrayList exps = (ArrayList) globalExp.get(i);
            HashMap<String, String> expsInd = (HashMap) globalExpInd.get(i);

            //get significant GO terms
            String GOs = sigGOmap.get(i + 1);
            String[] GOsplit = GOs.split("_");

            for (int j = 0; j < exps.size(); j++) {
                String[] split = ((String) exps.get(j)).split("\t");
                int expind = master.indexOf(split[0]);

                String expsnow = (String) expsInd.get(split[0]);
                System.out.println(expind + "\t" + expsnow);
                String[] splitexpind = new String[0];
                try {
                    splitexpind = expsnow.split("_");
                } catch (Exception e) {
                    splitexpind[0] = expsnow;
                    e.printStackTrace();
                }

                ArrayList data = new ArrayList();
                //create sub-bicluster for these conditions and compute mean
                for (int b = 0; b < splitexpind.length; b++) {
                    int trueind = -1;
                    try {
                        trueind = MoreArray.getArrayInd(vb.exps, Integer.parseInt(splitexpind[b]));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    data.add(Matrix.extractColumn(vb.exp_data, trueind));
                }
                double[][] subdata = MoreArray.ArrayListto2DDouble(data);
                double sign = Math.signum(Matrix.avg(subdata));

                for (int k = 0; k < GOsplit.length; k++) {
                    if (GOsplit[k].length() > 0) {
                        int GOind = masterGO.indexOf(GOsplit[k]);
                        if (sign > 0)
                            out2[GOind][expind]++;
                        else
                            out2[GOind][expind + master.size()]--;
                    }
                }
            }
        }

        String[] xlabels2 = new String[xlabels.length * 2];
        for (int k = 0; k < xlabels.length * 2; k++) {
            if (k < xlabels.length)
                xlabels2[k] = xlabels[k] + "_pos";
            else
                xlabels2[k] = xlabels[k - xlabels.length] + "_neg";
        }
        String[] ylabelsGO = MoreArray.ArrayListtoString(masterGO);
        TabFile.write(out2, prefix + "_exp_vs_GO.txt", xlabels2, ylabelsGO);
    }


    /**
     * @param i
     * @param imap
     * @param suffix
     * @param isExp
     */
    private void doSingle(int i,
                          HashMap<Integer, Integer> imap, String suffix, boolean isExp) {

        ArrayList<String> ilabels = new ArrayList<String>();
        HashMap<String, Integer> now = new HashMap<String, Integer>();
        Set keysi = imap.keySet();

        HashMap<String, String> biclusterExp = new HashMap<String, String>();

        Iterator it = keysi.iterator();
        while (it.hasNext()) {
            int key = (Integer) it.next();
            ArrayList get = ExpfromNetModules.processString(imap, key, exp_labels, gene_labels, isExp);
            String label1 = (String) get.get(0);
            double weight = Double.NaN;
            try {
                 weight = (Double) get.get(1);
            } catch (Exception e) {
                weight = (Integer) get.get(1);
                //e.printStackTrace();
            }

            Object o = now.get(label1);
            Object o2 = biclusterExp.get(label1);
            if (o == null) {
                now.put(label1, 1);
                biclusterExp.put(label1, "" + key);
            } else {
                int val = (Integer) o;
                now.put(label1, val + 1);
                String s = (String) o2;
                biclusterExp.put(label1, s + "_" + key);
            }

            //System.out.println("af " + str);
        }

        Set keys2 = now.keySet();
        Iterator it2 = keys2.iterator();
        while (it2.hasNext()) {
            String key = (String) it2.next();
            int val = now.get(key);
            int index = MoreArray.getArrayInd(geneids, key);
            if (index == -1)
                index = MoreArray.getArrayInd(genenames, key);
            String[] row = {""};
            if (index != -1) {
                //+1 offset for header row
                row = tab_data[index + 1];
            }
            ilabels.add(key + "\t" + val + "\t" + MoreArray.toString(row, "\t"));
        }

        String outpath = prefix + "_" + i + "__" + suffix;
        System.out.println("writing " + outpath);
        TextFile.write(ilabels, outpath);

        if (isExp) {
            globalExp.add(ilabels);
            globalExpInd.add(biclusterExp);
        }

        System.out.println("done " + i);
    }


    /**
     * @param args
     * @throws Exception
     */
    private void init(String[] args) throws Exception {
        vbl = ValueBlockListMethods.readAny(args[0]);
       // System.exit(0);
        prefix = args[0];

        sm = new SimpleMatrix(args[1]);

        try {
            String[][] sarray = TabFile.readtoArray(args[2]);
            System.out.println("setLabels e " + sarray.length + "\t" + sarray[0].length);
            int col = 2;
            exp_labels = MoreArray.replaceAll(MoreArray.extractColumnStr(sarray, col), "\"", "");
            System.out.println("setLabels e " + exp_labels.length + "\t" + exp_labels[0]);
        } catch (Exception e) {
            System.out.println("expecting 2 cols");
            e.printStackTrace();
        }

        try {
            String[][] sarray = TabFile.readtoArray(args[3]);
            System.out.println("setLabels e " + sarray.length + "\t" + sarray[0].length);
            int col = 2;
            gene_labels = MoreArray.replaceAll(MoreArray.extractColumnStr(sarray, col), "\"", "");
            System.out.println("setLabels e " + gene_labels.length + "\t" + gene_labels[0]);
        } catch (Exception e) {
            System.out.println("expecting 2 cols");
            e.printStackTrace();
        }


        String[][] GO_yeast_data = TabFile.readtoArray(args[4]);
        GOmap = new HashMap();
        yeast_gene_names = new HashMap();
        for (int i = 0; i < GO_yeast_data.length; i++) {
            GO_yeast_data[i] = StringUtil.replace(GO_yeast_data[i], "\"", "");
            Object n = yeast_gene_names.get(GO_yeast_data[i][0]);
            if (n == null) {
                //System.out.println("adding GO_yeast_data " + GO_yeast_data[i][0] + "\t" + GO_yeast_data[i][1]);
                yeast_gene_names.put(GO_yeast_data[i][0], GO_yeast_data[i][1]);
            }
            System.out.println(i + "\t" + GO_yeast_data[i][4]);
            String addStr = BuildGraph.removeGOequals(GO_yeast_data[i][4]);

            if (!addStr.equals("")) {
                //System.out.println("goyeast " + i + "\t" + MoreArray.toString(GO_yeast_data[i], ","));
                Object o = GOmap.get(GO_yeast_data[i][0]);
                if (o == null) {
                    ArrayList add = new ArrayList();
                    add.add(addStr);
                    GOmap.put(GO_yeast_data[i][0], add);
                } else {
                    ArrayList a = (ArrayList) o;
                    a.add(addStr);
                    GOmap.put(GO_yeast_data[i][0], a);
                }
            }
        }

        System.out.println("GOmap " + GOmap.size());

        String[][] summary_data = TabFile.readtoArray(args[5]);
        sigGOmap = new HashMap<Integer, String>();
        for (int i = 1; i < summary_data.length; i++) {
            sigGOmap.put(i, summary_data[i][12].substring(4));
        }

        tab_data = TabFile.readtoArray(args[6]);

        geneids = new String[tab_data.length - 1];
        genenames = new String[tab_data.length - 1];
        for (int i = 1; i < tab_data.length; i++) {
            geneids[i - 1] = tab_data[i][7];
            genenames[i - 1] = tab_data[i][8];
        }

        System.out.println("geneids");
        MoreArray.printArray(geneids);

        System.out.println("genenames");
        MoreArray.printArray(genenames);
    }

    /**
     * @return
     */
    private void buildExpWeight() {
        for (int z = 0; z < vbl.size(); z++) {
            ValueBlock v = (ValueBlock) vbl.get(z);
            HashMap curHash = new HashMap();
            for (int j = 0; j < v.exps.length; j++) {
                Object o = curHash.get(v.exps[j]);
                if (o == null) {
                    curHash.put(v.exps[j], 1.0);
                } else {
                    double val = (Double) o;
                    curHash.put(v.exps[j], val + 1.0);
                }
            }
            arHashExp.add(z, curHash);
        }
    }

    /**
     * @return
     */
    private void buildGeneWeight() {
        for (int z = 0; z < vbl.size(); z++) {
            ValueBlock v = (ValueBlock) vbl.get(z);
            HashMap curHash = new HashMap();
            for (int j = 0; j < v.genes.length; j++) {
                Object o = curHash.get(v.genes[j]);
                if (o == null) {
                    curHash.put(v.genes[j], 1.0);
                } else {
                    double val = (Double) o;
                    curHash.put(v.genes[j], val + 1.0);
                }
            }
            arHashGene.add(z, curHash);
        }
    }

    /**
     * @param args
     */
    public final static void main(String[] args) {

        if (args.length == 7) {
            ExpProfiles rm = new ExpProfiles(args);
        } else {
            System.out.println("syntax: java DataMining.func.ExpProfiles\n" +
                    "<value block list>\n" +
                    "<expr data file>\n" +
                    //"<Moduland modules.csv>\n" +
                    "<exp labels>\n" +
                    "<gene labels>\n" +
                    "<GO yeast annotation file>\n" +
                    "<value block list summary file>\n" +
                    "<yeast tab file>"
            );
        }
    }
}

