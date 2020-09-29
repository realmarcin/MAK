package DataMining.func;

import DataMining.MINER_STATIC;
import DataMining.ValueBlock;
import DataMining.ValueBlockList;
import DataMining.ValueBlockListMethods;
import mathy.Matrix;
import mathy.SimpleMatrix;
import mathy.stat;
import util.MoreArray;
import util.StringUtil;
import util.TabFile;
import util.TextFile;

import java.util.*;

/**
 * Class to extract unique and common experiments between bicluster network modules
 * <p/>
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: 3/20/13
 * Time: 3:40 PM
 */
public class ExpfromNetModules {

    String prefix;

    String[] exp_labels;
    String[] gene_labels;
    ValueBlockList vbl;
    SimpleMatrix sm;
    ArrayList<String> modData;
    HashMap<Integer, String> modMap;
    ArrayList<HashMap> arHashExp;
    ArrayList<HashMap> arHashGene;

    HashMap GOmap;
    HashMap yeast_gene_names;
    HashMap sigGOmap;
    HashMap countGOmap;
    HashMap countExpmap;
    HashMap totalExpCount;

    ValueBlockList single = new ValueBlockList();
    ValueBlockList common = new ValueBlockList();
    ValueBlockList unique = new ValueBlockList();
    ValueBlockList union = new ValueBlockList();

    int[] totalexps, totalgenes;
    private double denom;

    int[] target_mods = {87};// 5,43,54

    /**
     * @param args
     */
    public ExpfromNetModules(String[] args) {

        try {
            init(args);
        } catch (Exception e) {
            e.printStackTrace();
        }

        modMap = buildBiclustertoModuleMap();

        int[] keyStrE = buildExpWeight();
        int[] keyStrG = buildGeneWeight();

        ArrayList large = new ArrayList();
        for (int i = 0; i < target_mods.length; i++) {
            large.add(target_mods[i]);
        }

        for (int i = 0; i < arHashExp.size(); i++) {
            HashMap oneE = arHashExp.get(i);
            HashMap oneG = arHashGene.get(i);
            Set<Integer> keysE = oneE.keySet();
            Set<Integer> keysG = oneG.keySet();

            int[] expone = MoreArray.intfromIntegerArray(keysE);
            int[] geneone = MoreArray.intfromIntegerArray(keysG);

            boolean doSingle = false;
            for (int j = i + 1; j < arHashExp.size(); j++) {
                HashMap twoE = arHashExp.get(j);
                HashMap twoG = arHashGene.get(j);

                Set keys2E = twoE.keySet();
                Set keys2G = twoG.keySet();

                int ind1 = large.indexOf(keyStrE[i]);
                int ind2 = large.indexOf(keyStrE[j]);
                //System.out.println("keyStrE " + keyStrE[i] + "\t" + keyStrE[j] + "\t" + ind1 + "\t" + ind2);

                //if both are large modules
                if (ind1 != -1 && ind2 != -1) {
                    doSingle = true;

                    if (doSingle) {
                        doSingle(keyStrE, j, twoE, "_exps.txt", true);
                        doSingle(keyStrG, j, twoG, "_genes.txt", false);
                        int[] exptwo = MoreArray.intfromIntegerArray(keys2E);
                        int[] genetwo = MoreArray.intfromIntegerArray(keys2G);

                        ValueBlock blocktwo = new ValueBlock(genetwo, exptwo);
                        assignGOfreq(blocktwo, keyStrG[j] + "_GO.txt");
                        //assignGOsig(blocktwo, modKeyStr[j] + "_GO.txt");

                        if (!single.contains(blocktwo)) {
                            single.add(blocktwo);
                            System.out.println("single 2 " + keyStrE[j] + "\t" + blocktwo.toStringShort());
                        }
                    }

                    HashMap<Integer, Integer> unionE = new HashMap();
                    HashMap<Integer, Integer> unionG = new HashMap();

                    unionE.putAll(oneE);
                    unionE.putAll(twoE);

                    unionG.putAll(oneG);
                    unionG.putAll(twoG);

                    doGenesandExps(keyStrE, keyStrG, i, oneE, oneG, keysE, keysG, j, twoE, twoG, keys2E, keys2G, unionE, unionG);
                }
            }
            if (doSingle) {
                doSingle(keyStrE, i, oneE, "_exps.txt", true);
                doSingle(keyStrG, i, oneG, "_genes.txt", false);

                ValueBlock blockone = new ValueBlock(geneone, expone);

                if (!single.contains(blockone)) {
                    single.add(blockone);
                    System.out.println("single 1 " + keyStrE[i] + "\t" + blockone.toStringShort());
                    assignGOfreq(blockone, keyStrG[i] + "_GO.txt");
                    //assignGOsig(blockone, modKeyStr[i] + "_GOsig.txt");
                }
            }
        }


        String header = "#";
        String s = single.toString(header + MINER_STATIC.HEADER_VBL);
        util.TextFile.write(s, prefix + "single.vbl");

        //common between all members of module
        String s1 = common.toString(header + MINER_STATIC.HEADER_VBL);
        util.TextFile.write(s1, prefix + "common.vbl");

        //differences between pairs
        String s2 = unique.toString(header + MINER_STATIC.HEADER_VBL);
        util.TextFile.write(s2, prefix + "unique.vbl");

        //union of all members in module
        String s3 = union.toString(header + MINER_STATIC.HEADER_VBL);
        util.TextFile.write(s3, prefix + "union.vbl");

    }

    /**
     * @param keyStrE
     * @param keyStrG
     * @param i
     * @param oneE
     * @param oneG
     * @param keysE
     * @param keysG
     * @param j
     * @param twoE
     * @param twoG
     * @param keys2E
     * @param keys2G
     * @param unionE
     * @param unionG
     */
    private void doGenesandExps(int[] keyStrE, int[] keyStrG, int i, HashMap oneE, HashMap oneG, Set<Integer> keysE, Set<Integer> keysG, int j, HashMap twoE, HashMap twoG, Set keys2E, Set keys2G, HashMap<Integer, Integer> unionE, HashMap<Integer, Integer> unionG) {
        ArrayList getE = findUnique(oneE, keysE, twoE);
        HashMap<Integer, Integer> uniqueie = (HashMap<Integer, Integer>) getE.get(0);
        double matche = (Double) getE.get(1);
        ArrayList<Integer> commone = (ArrayList<Integer>) getE.get(2);

        ArrayList getErev = findUniqueReverse(oneE, keys2E, twoE);
        HashMap<Integer, Integer> uniqueje = (HashMap<Integer, Integer>) getErev.get(0);

        ArrayList getG = findUnique(oneG, keysG, twoG);
        HashMap<Integer, Integer> uniqueig = (HashMap<Integer, Integer>) getG.get(0);
        double matchg = (Double) getG.get(1);
        ArrayList<Integer> commong = (ArrayList<Integer>) getG.get(2);

        ArrayList getGrev = findUniqueReverse(oneG, keys2G, twoG);//, common
        HashMap<Integer, Integer> uniquejg = (HashMap<Integer, Integer>) getGrev.get(0);

        Set<Integer> keysIE = uniqueie.keySet();
        Set<Integer> keysIG = uniqueig.keySet();
        //System.out.println("unique I " + i + "\t" + j + "\t" + keysIE.size() + "\t" + keysIG.size());
        int[] eI = MoreArray.intfromIntegerArray(keysIE);
        int[] gI = MoreArray.intfromIntegerArray(keysIG);
        System.out.println("unique I e " + MoreArray.toString(eI));
        System.out.println("unique I g " + MoreArray.toString(gI));
        ValueBlock blockI = new ValueBlock(gI, eI);
        unique.add(blockI);

        Set<Integer> keysJE = uniqueje.keySet();
        Set<Integer> keysJG = uniquejg.keySet();
        int[] eJ = MoreArray.intfromIntegerArray(keysJE);
        int[] gJ = MoreArray.intfromIntegerArray(keysJG);
        System.out.println("unique J e " + MoreArray.toString(eJ));
        System.out.println("unique J g " + MoreArray.toString(gJ));
        ValueBlock blockJ = new ValueBlock(gJ, eJ);
        unique.add(blockJ);

        Set<Integer> unionkeysE = unionE.keySet();
        Set<Integer> unionkeysG = unionG.keySet();
        int[] eunion = MoreArray.intfromIntegerArray(unionkeysE);
        int[] gunion = MoreArray.intfromIntegerArray(unionkeysG);
        System.out.println("union J e " + MoreArray.toString(eunion));
        System.out.println("union J g " + MoreArray.toString(gunion));
        ValueBlock blockunion = new ValueBlock(gunion, eunion);
        union.add(blockunion);

        //if (commong.size() > 0 && commone.size() > 0) {
        int[] ce = MoreArray.ArrayListtoInt(commone);
        int[] cg = MoreArray.ArrayListtoInt(commong);
        System.out.println("common e " + MoreArray.toString(ce));
        System.out.println("common g " + MoreArray.toString(cg));
        ValueBlock blockcom = new ValueBlock(cg, ce);
        common.add(blockcom);
        //}

        System.out.println("doGenesandExps " + i + "\t" + j + "\t" + uniqueie.size() + "\t" + uniqueje.size());

        doUnique(keyStrE, i, j, unionE, uniqueie, uniqueje, keys2E, matche, "_uniqueexps.txt", true, keysIE.size(), keysJE.size());
        assignGOfreq(blockI, keyStrE[i] + "_" + keyStrE[j] + "__" + keyStrE[i] + "_GO.txt");
        doUnique(keyStrG, i, j, unionG, uniqueig, uniquejg, keys2G, matchg, "_uniquegenes.txt", false, -1, -1);
        assignGOfreq(blockJ, keyStrG[i] + "_" + keyStrG[j] + "__" + keyStrG[j] + "_GO.txt");
    }

    /**
     * @param keysStr
     * @param i
     * @param imap
     */
    private void doSingle(int[] keysStr, int i,
                          HashMap<Integer, Integer> imap, String suffix, boolean isExp) {

        ArrayList<String> ilabels = new ArrayList<String>();
        ArrayList<String> ilabelsraw = new ArrayList<String>();

        Set keysi = imap.keySet();
        Iterator it = keysi.iterator();
        HashMap total = new HashMap();
        while (it.hasNext()) {
            int key = (Integer) it.next();
            ArrayList get = processString(imap, key, exp_labels, gene_labels, isExp);
            String label1 = (String) get.get(0);
            int weight = (Integer) get.get(1);
            Object o = total.get(label1);
            if (o == null) {
                total.put(label1, 1);//weight);
            } else {
                int count = (Integer) o;
                total.put(label1, count + 1);//weight);
            }
        }

        Set keyt = total.keySet();
        Iterator it2 = keyt.iterator();
        while (it2.hasNext()) {
            String label1 = (String) it2.next();
            int weight = (Integer) total.get(label1);
            double w = weight;
            if (isExp)
                w = 100.0 * (double) weight / (double) (Integer) totalExpCount.get(label1);//totalexps[i];
            String str = label1 + "\t" + w;
            String str2 = label1 + "\t" + weight;
            ilabels.add(str);
            ilabelsraw.add(str2);
        }

        String outpath = "" + keysStr[i] + "__" + suffix;
        System.out.println("writing " + outpath);
        TextFile.write(ilabels, outpath);

        String outpath2 = "" + keysStr[i] + "__" + "_raw" + suffix;
        System.out.println("writing " + outpath2);
        TextFile.write(ilabelsraw, outpath2);


        System.out.println(i + "\t" + keysStr[i]);
    }

    /**
     * @param keysStr
     * @param i
     * @param j
     * @param union
     * @param uniquei
     * @param uniquej
     * @param keys2
     * @param match
     */
    private void doUnique(int[] keysStr, int i, int j, HashMap<Integer, Integer> union,
                          HashMap<Integer, Integer> uniquei, HashMap<Integer, Integer> uniquej, Set keys2, double match, String suffix, boolean isExp, int len1, int len2) {
        ArrayList<String> uniqueiLabels = new ArrayList<String>();
        ArrayList<String> uniqueiLabelsraw = new ArrayList<String>();
        ArrayList<String> uniquejLabels = new ArrayList<String>();
        ArrayList<String> uniquejLabelsraw = new ArrayList<String>();
        Set keysi = uniquei.keySet();
        Iterator it = keysi.iterator();
        HashMap total = new HashMap();

        while (it.hasNext()) {
            int key = (Integer) it.next();
            ArrayList get = processString(uniquei, key, exp_labels, gene_labels, isExp);
            String label1 = (String) get.get(0);
            //int weight = (Integer) get.get(1);
            Object o = total.get(label1);
            if (o == null) {
                total.put(label1, 1);//weight);
            } else {
                int count = (Integer) o;
                total.put(label1, count + 1);//weight);
            }
            //String str = label1 + "\t" + weight;
            //uniqueiLabels.add(str);
            //System.out.println("af " + str);
        }

        Set keyt = total.keySet();
        Iterator it2 = keyt.iterator();
        while (it2.hasNext()) {
            String label1 = (String) it2.next();
            int weight = (Integer) total.get(label1);
            double weight1 = (double) weight;
            if (isExp) {
                double denom = (double) (Integer) totalExpCount.get(label1);
                System.out.println("exp ratio " + label1 + "\t" + weight1 + "\t" + denom);
                weight1 = weight1 / denom;
            }
            String str = label1 + "\t" + weight1;
            uniqueiLabels.add(str);
            String str2 = label1 + "\t" + weight;
            uniqueiLabelsraw.add(str2);
        }

        String outpath = "" + keysStr[i] + "_" + keysStr[j] + "__" + keysStr[i] + suffix;
        System.out.println("writing " + outpath);
        TextFile.write(uniqueiLabels, outpath);

        String outpath2 = "" + keysStr[i] + "_" + keysStr[j] + "__" + keysStr[i] + "_raw" + suffix;
        System.out.println("writing " + outpath2);
        TextFile.write(uniqueiLabelsraw, outpath2);

        Set keysj = uniquej.keySet();
        it2 = keysj.iterator();
        total = new HashMap();
        while (it2.hasNext()) {
            int keyj = (Integer) it2.next();
            ArrayList get = processString(uniquej, keyj, exp_labels, gene_labels, isExp);
            String exp_label1 = (String) get.get(0);
            //int weight = (Integer) get.get(1);
            Object o = total.get(exp_label1);
            if (o == null) {
                total.put(exp_label1, 1);//weight);
            } else {
                int count = (Integer) o;
                total.put(exp_label1, count + 1);//weight);
            }
            //String str = exp_label1 + "\t" + weight;
            //uniquejLabels.add(str);
            //System.out.println("af " + str);
        }

        keyt = total.keySet();
        it2 = keyt.iterator();

        while (it2.hasNext()) {
            String label1 = (String) it2.next();
            int weight = (Integer) total.get(label1);
            double weight1 = (double) weight;
            if (isExp) {
                double denom = (double) (Integer) totalExpCount.get(label1);
                System.out.println("exp ratio " + label1 + "\t" + weight1 + "\t" + denom);
                weight1 = weight1 / denom;
            }
            String str = label1 + "\t" + weight1;
            uniquejLabels.add(str);
            String str2 = label1 + "\t" + weight;
            uniquejLabelsraw.add(str2);
        }

        String outpath1 = "" + keysStr[i] + "_" + keysStr[j] + "__" + keysStr[j] + suffix;
        System.out.println("writing " + outpath1);
        TextFile.write(uniquejLabels, outpath1);
        String outpath3 = "" + keysStr[i] + "_" + keysStr[j] + "__" + keysStr[j] + "_raw" + suffix;
        System.out.println("writing " + outpath3);
        TextFile.write(uniquejLabelsraw, outpath3);

        System.out.println(i + "\t" + j + "\t" + keysStr[i] + "\t" + keysStr[j] + "\tmatch\t" + match +
                "\tmin norm\t" + (match / (double) keys2.size()) + "\tJaccard\t" + (match / (double) union.size()));
    }

    /**
     * @param one
     * @param keys2
     * @param two
     * @return
     */
    private ArrayList findUniqueReverse(HashMap one, Set keys2, HashMap two) {
        HashMap<Integer, Integer> uniquej = new HashMap<Integer, Integer>();
        ArrayList<Integer> common = new ArrayList<Integer>();
        double match = 0;

        if (two == null)
            System.out.println("two is null");

        Iterator keyIt2 = keys2.iterator();
        while (keyIt2.hasNext()) {
            int key = (Integer) keyIt2.next();
            int val = (Integer) two.get(key);
            Object o = one.get(key);
            if (o != null) {
            } else {
                Object o2 = uniquej.get(key);
                if (o2 == null) {
                    uniquej.put(key, val);
                } else {
                    System.out.println("multiple");
                    //int cur = (Integer) o2;
                    //uniquej.put(key, cur + 1);
                }
            }
        }

        ArrayList ret = new ArrayList();
        ret.add(uniquej);
        ret.add(match);
        ret.add(common);
        return ret;
    }

    /**
     * @param one
     * @param keys
     * @param two
     * @return
     */
    private ArrayList findUnique(HashMap one, Set keys, HashMap two) {
        HashMap<Integer, Integer> unique = new HashMap<Integer, Integer>();
        ArrayList<Integer> common = new ArrayList<Integer>();
        double match = 0;
        Iterator keyIt = keys.iterator();
        while (keyIt.hasNext()) {
            int key = (Integer) keyIt.next();
            int val = (Integer) one.get(key);

            Object o = two.get(key);
            if (o != null) {
                match++;
                common.add(key);
            } else {
                Object o2 = unique.get(key);
                if (o2 == null) {
                    unique.put(key, val);
                } else {
                    System.out.println("multiple");
                    //int cur = (Integer) o2;
                    //unique.put(key, cur + 1);
                }
            }
        }
        ArrayList ret = new ArrayList();
        ret.add(unique);
        ret.add(match);
        ret.add(common);
        if (common.size() == 0)
            System.out.println("intersection is empty");
        return ret;
    }

    /**
     * @return
     */
    private int[] buildExpWeight() {
        Set values = modMap.keySet();
        int size = values.size();
        System.out.println("size " + size);
        arHashExp = new ArrayList<HashMap>(size);
        int[] keysStr = MoreArray.intSettoint(values);

        double[] expr_mean = new double[keysStr.length];
        double[] expr_sd = new double[keysStr.length];

        totalexps = new int[keysStr.length];
        ArrayList sizes = new ArrayList();
        for (int i = 0; i < keysStr.length; i++) {
            String entry = modMap.get(keysStr[i]);
            System.out.println("buildExpWeight " + keysStr[i] + "\tsize " + (StringUtil.countOccur(entry, '_') + 1) + "\t" + entry);
            HashMap<Integer, Integer> curHash = new HashMap();

            String[] split = entry.split("_");
            ArrayList data = new ArrayList();
            ArrayList datasd = new ArrayList();
            System.out.println("split.length " + split.length);
            sizes.add(new Integer(split.length));
            for (int z = 0; z < split.length; z++) {
                int cur = Integer.parseInt(split[z]);
                ValueBlock v = null;
                try {
                    v = (ValueBlock) vbl.get(cur - 1);
                    v.setDataAndMean(sm.data);
                    System.out.println("Bicluster data " + v.exp_data[0][0]);
                    double mean = Matrix.avg(v.exp_data);
                    System.out.println("Bicluster mean " + mean);
                    data.add(mean);
                    double sd = Matrix.SD(v.exp_data, mean);
                    System.out.println("Bicluster SD " + sd);
                    datasd.add(sd);

                    totalexps[i] += v.exps.length;

                    for (int j = 0; j < v.exps.length; j++) {
                        Object o = curHash.get(v.exps[j]);
                        if (o == null) {
                            curHash.put(v.exps[j], 1);
                        } else {
                            int val = (Integer) o;
                            curHash.put(v.exps[j], val + 1);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("buildExpWeight no biclusters for module " + cur);
                    //e.printStackTrace();
                }

            }
            expr_mean[i] = stat.listAvg(data);
            expr_sd[i] = stat.listAvg(datasd);
            arHashExp.add(i, curHash);
        }
        System.out.println("Module keys");
        MoreArray.printArray(keysStr);
        System.out.println("Module sizes");
        MoreArray.printArray(MoreArray.ArrayListtoInt(sizes));
        System.out.println("Bicluster mean");
        MoreArray.printArray(expr_mean);
        System.out.println("Bicluster sd");
        MoreArray.printArray(expr_sd);

        totalExpCount = new HashMap<String, Integer>();
        for (int i = 0; i < exp_labels.length; i++) {
            ArrayList get = processString(null, i + 1, exp_labels, gene_labels, true);
            String label1 = (String) get.get(0);
            Object o = totalExpCount.get(label1);
            if (o == null) {
                totalExpCount.put(label1, 1);
            } else {
                int count = (Integer) o;
                totalExpCount.put(label1, count + 1);
            }
        }

        Set set = totalExpCount.keySet();
        System.out.println("totalExpCount set " + set.size());
        Iterator it = set.iterator();
        ArrayList outExp = new ArrayList();
        while (it.hasNext()) {
            String key = (String) it.next();
            String val = "" + totalExpCount.get(key);
            String key_val = key + "\t" + val;
            System.out.println("totalExpCount out " + key_val);
            outExp.add(key_val);
        }

        String outpath = "exp_count.txt";
        TextFile.write(outExp, outpath);
        System.out.println("wrote " + outpath);

        return keysStr;
    }

    /**
     * @return
     */
    private int[] buildGeneWeight() {
        Set values = modMap.keySet();
        int size = values.size();
        System.out.println("size " + size);
        arHashGene = new ArrayList<HashMap>(size);
        int[] keysStr = MoreArray.intSettoint(values);
        int count = 0;
        totalgenes = new int[keysStr.length];
        for (int i = 0; i < keysStr.length; i++) {
            String entry = modMap.get(keysStr[i]);
            System.out.println(keysStr[i] + "\tsize " + (StringUtil.countOccur(entry, '_') + 1) + "\t" + entry);
            HashMap<Integer, Integer> curHash = new HashMap();

            String[] split = entry.split("_");
            for (int z = 0; z < split.length; z++) {
                int cur = Integer.parseInt(split[z]);
                try {
                    ValueBlock v = (ValueBlock) vbl.get(cur - 1);
                    totalgenes[i] += v.genes.length;
                    for (int j = 0; j < v.genes.length; j++) {
                        Object o = curHash.get(v.genes[j]);
                        if (o == null) {
                            curHash.put(v.genes[j], 1);
                        } else {
                            int val = (Integer) o;
                            curHash.put(v.genes[j], val + 1);
                        }
                    }
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            }

            arHashGene.add(count, curHash);
            count++;
        }
        return keysStr;
    }

    /**
     * @return
     */
    private HashMap<Integer, String> buildBiclustertoModuleMap() {
        HashMap<Integer, Integer> biclusterMap = new HashMap<Integer, Integer>();
        HashMap<Integer, String> modMap = new HashMap<Integer, String>();
        for (int i = 12; i < modData.size(); i++) {
            String[] cur = modData.get(i).split(",");
            int module = Integer.parseInt(cur[1]);
            biclusterMap.put(Integer.parseInt(cur[0]), module);

            Object o = modMap.get(module);
            if (o == null) {
                modMap.put(module, cur[0]);
            } else {
                String existing = (String) o;
                String newstr = existing + "_" + cur[0];
                modMap.put(module, newstr);
                //System.out.println("newstr " + newstr);
            }
        }
        return modMap;
    }


    /**
     * @param uniquei
     * @param key
     * @return
     */
    public final static ArrayList processString(HashMap<Integer, Integer> uniquei, Integer key,
                                                String[] exp_labels, String[] gene_labels, boolean isExp) {

        String label = null;

        if (isExp)
            label = exp_labels[key - 1];
        else
            label = gene_labels[key - 1];

        int weight = -1;
        if (uniquei != null) {
            try {
                weight = (Integer) uniquei.get(key);
            } catch (Exception e) {
                //weight = (Integer)(Double) uniquei.get(key);
            }
        }
        String exp_label1 = label;

        if (isExp) {
            //System.out.println("bf " + label);
            String orig = new String(label);
            int dotind = label.indexOf(".");
            //System.out.println(dotind);
            label = label.substring(dotind + 1);
            label = label.toLowerCase();
            int minind = label.indexOf(".min") != -1 ? label.indexOf(".min") : 1000000;
            int minundind = label.indexOf("_min") != -1 ? label.indexOf("_min") : 1000000;
            int minnodotind = label.indexOf("min") != -1 && label.indexOf("lycoming") == -1 &&
                    label.indexOf("amino") == -1 && label.indexOf("minus") == -1 ? label.indexOf("min") : 1000000;
            int hind = label.endsWith(".h") ? label.lastIndexOf(".h") : 1000000;
            int hrind = label.indexOf(".hr") != -1 ? label.indexOf(".hr") : 1000000;
            int hrsind2 = label.indexOf("hrs") != -1 ? label.indexOf("hrs") : 1000000;
            int hrsind = label.indexOf(".hours") != -1 ? label.indexOf(".hours") : 1000000;
            int Timeind = label.indexOf(".time") != -1 ? label.indexOf(".time") : 1000000;
            int sampleind = label.indexOf(".sample") != -1 ? label.indexOf(".sample") : 1000000;
            int compind = label.indexOf(".comparison") != -1 ? label.indexOf(".comparison") : 1000000;
            int repind = label.indexOf(".repeat") != -1 ? label.indexOf(".repeat") : 1000000;

            String lowerlabel = (label.toLowerCase());

            if (lowerlabel.indexOf(("Chemostat.Comparison").toLowerCase()) != -1) {
                exp_label1 = label.replace("..rescan.", "");
                exp_label1 = exp_label1.replace("..scanner1.", "");
                exp_label1 = exp_label1.replace("comparison2", "");
                exp_label1 = exp_label1.replace("comparison", "");

                int dotdotind = exp_label1.indexOf("..") + 2;
                int mindotind = exp_label1.indexOf("min.") + 4;
                int Aind1 = exp_label1.indexOf("1a.") + 3;
                int Bind1 = exp_label1.indexOf("1b.") + 3;
                int Aind2 = exp_label1.indexOf("2a.") + 3;
                int Bind2 = exp_label1.indexOf("2b.") + 3;
                int Aind3 = exp_label1.indexOf("3a.") + 3;
                int Bind3 = exp_label1.indexOf("3b.") + 3;

                int max = Math.max(Math.max(Math.max(Math.max(Math.max(Math.max(Math.max(dotdotind, mindotind), Aind1), Bind1), Aind2), Bind2), Aind3), Bind3);
                exp_label1 = exp_label1.substring(max);
            } else if (lowerlabel.startsWith(("ypd").toLowerCase())) {
                exp_label1 = "YPD";
            } else if (lowerlabel.indexOf("heat.shock") != -1 || lowerlabel.indexOf("heat_shock") != -1) {
                exp_label1 = "heat.shock";
            } else if (lowerlabel.indexOf("h2o2") != -1) {
                exp_label1 = "H2O2";
            } else if (lowerlabel.indexOf("2mm.md") != -1 || lowerlabel.indexOf("menadione") != -1 || lowerlabel.indexOf("md_ii") != -1) {
                exp_label1 = "menadione";
            } else if (lowerlabel.indexOf("nitrogen.depletion") != -1) {
                exp_label1 = "nitrogen.depletion";
            } else if (lowerlabel.indexOf("starv") != -1) {
                exp_label1 = "starvation";
            } else if (lowerlabel.indexOf("steady.state") != -1) {
                exp_label1 = "steady.state";
            } else if (lowerlabel.indexOf("stationary") != -1) {
                exp_label1 = "stationary";
            } else if (lowerlabel.indexOf("sporulation") != -1) {
                exp_label1 = "sporulation";
            } else if (lowerlabel.indexOf("gamma") != -1) {
                exp_label1 = "gamma";
            } else {
                if (label.indexOf("comp") != -1) {
                    System.out.println("sneaked in comp " + label);
                }
                int min = Math.min(Math.min(Math.min(Math.min(Math.min(Math.min(Math.min(Math.min(Math.min(Math.min(minind, hrind),
                        Timeind), sampleind), minundind), minnodotind), hrsind), hrsind2), compind), repind), hind);

                exp_label1 = label;
                //System.out.println("min " + min);
                if (min != 1000000 && min > 0) {
                    char last = label.charAt(min - 1);
                    if (last == '.' || last == '_') {
                        min--;
                        last = label.charAt(min - 1);
                    }
                    boolean digit = Character.isDigit(last);
                    if (digit) {
                        while (digit) {
                            min--;
                            last = label.charAt(min - 1);
                            digit = Character.isDigit(last);
                        }
                        while (last == '.' || last == '_') {
                            min--;
                            last = label.charAt(min - 1);
                        }
                    }
                    exp_label1 = label.substring(0, min);
                }
            }

            if (exp_label1.indexOf("X") == 0) {
                System.out.println("bf " + orig + "\nbf2 " + label);
                System.out.println("af " + exp_label1);
            }
        }
        ArrayList ret = new ArrayList();
        ret.add(exp_label1);
        ret.add(weight);
        return ret;
    }


    /**
     * @return
     */
    private void assignGOfreq(ValueBlock v, String outpath) {
        HashMap<String, Double> curmap = new HashMap<String, Double>();
        int countb = 0;
        for (int i = 0; i < v.genes.length; i++) {
            String gene = gene_labels[v.genes[i] - 1];
            ArrayList GOar = (ArrayList) GOmap.get(gene);
            if (GOar != null) {
                HashMap<String, Integer> unique = new HashMap<String, Integer>();
                for (int h = 0; h < GOar.size(); h++) {
                    String[] GO = new String[1];
                    try {
                        GO = (String[]) GOar.get(h);
                        if (!GO[4].equals("")) {
                            try {
                                Object test = curmap.get(GO[4]);
                                if (test == null) {
                                    Object added = unique.get(GO[4]);
                                    if (added == null) {
                                        curmap.put(GO[4], 1.0);
                                        countb++;
                                        unique.put(GO[4], 1);
                                    }
                                } else {
                                    Object added = unique.get(GO[4]);
                                    if (added == null) {
                                        double val = (Double) test;
                                        curmap.put(GO[4], (val + 1.0));
                                        countb++;
                                        unique.put(GO[4], 1);
                                    }
                                }
                            } catch (Exception e) {
                            }
                        }
                    } catch (Exception e) {
                        try {
                            GO = new String[1];
                            GO[0] = (String) GOar.get(h);
                            Object test = curmap.get(GO[0]);
                            if (test == null) {
                                Object added = unique.get(GO[0]);
                                if (added == null) {
                                    curmap.put(GO[0], 1.0);
                                    countb++;
                                    unique.put(GO[0], 1);
                                }
                            } else {
                                Object added = unique.get(GO[0]);
                                if (added == null) {
                                    double val = (Double) test;
                                    curmap.put(GO[0], (val + 1.0));
                                    countb++;
                                    unique.put(GO[0], 1);
                                }
                            }
                        } catch (Exception e1) {
                            System.out.println("h " + h);
                            System.out.println("GOar " + GOar.size());
                            System.out.println("GO " + GO.length);
                            System.out.println("GO[0] " + GO[0]);
                            e1.printStackTrace();
                        }
                    }
                }
            }
        }
        Set keysi = curmap.keySet();
        Iterator it = keysi.iterator();
        ArrayList out = new ArrayList();
        while (it.hasNext()) {
            String key = (String) it.next();
            double val = curmap.get(key);
            out.add(key + "\t" + val);
        }

        System.out.println("writing " + outpath);
        TextFile.write(out, outpath);
    }

    /**
     * @param args
     * @throws Exception
     */
    private void init(String[] args) throws Exception {
        vbl = ValueBlockListMethods.readAny(args[0]);
        prefix = args[0] + "_";

        sm = new SimpleMatrix(args[1]);

        modData = TextFile.readtoList(args[2]);

        try {
            String[][] sarray = TabFile.readtoArray(args[3]);
            System.out.println("setLabels e " + sarray.length + "\t" + sarray[0].length);
            int col = 2;
            exp_labels = MoreArray.replaceAll(MoreArray.extractColumnStr(sarray, col), "\"", "");
            System.out.println("setLabels e " + exp_labels.length + "\t" + exp_labels[0]);
        } catch (Exception e) {
            System.out.println("expecting 2 cols");
            e.printStackTrace();
        }

        try {
            String[][] sarray = TabFile.readtoArray(args[4]);
            System.out.println("setLabels e " + sarray.length + "\t" + sarray[0].length);
            int col = 2;
            gene_labels = MoreArray.replaceAll(MoreArray.extractColumnStr(sarray, col), "\"", "");
            System.out.println("setLabels e " + gene_labels.length + "\t" + gene_labels[0]);
        } catch (Exception e) {
            System.out.println("expecting 2 cols");
            e.printStackTrace();
        }


        String[][] GO_yeast_data = TabFile.readtoArray(args[5]);
        GOmap = new HashMap();
        countGOmap = new HashMap();
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

                Object o1 = countGOmap.get(addStr);
                if (o1 == null) {
                    countGOmap.put(addStr, 1);
                    //System.out.println("countGOmap " + addStr + "\t" + 1);
                } else {
                    int c = (Integer) o1;
                    countGOmap.put(addStr, c + 1);
                    //System.out.println("countGOmap " + addStr + "\t" + (c + 1));
                }

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

        String[][] summary_data = TabFile.readtoArray(args[6]);
        sigGOmap = new HashMap();
        for (int i = 1; i < summary_data.length; i++) {
            sigGOmap.put(i, summary_data[i][12].substring(4));
        }

        Set set = countGOmap.keySet();
        System.out.println("countGOmap set " + set.size());
        Iterator it = set.iterator();
        ArrayList outGO = new ArrayList();
        while (it.hasNext()) {
            String key = (String) it.next();
            String val = "" + countGOmap.get(key);
            String key_val = key + "\t" + val;
            System.out.println("countGOmap out " + key_val);
            outGO.add(key_val);
        }

        String outpath = "GO_count.txt";
        TextFile.write(outGO, outpath);
        System.out.println("wrote " + outpath);

    }

    /**
     * @param args
     */
    public final static void main(String[] args) {

        if (args.length == 7) {
            ExpfromNetModules rm = new ExpfromNetModules(args);
        } else {
            System.out.println("syntax: java DataMining.func.ExpfromNetModules\n" +
                    "<value block list>\n" +
                    "<expr data file>\n" +
                    "<Moduland modules.csv>\n" +
                    "<exp labels>\n" +
                    "<gene labels>\n" +
                    "<GO yeast annotation file>\n" +
                    "<value block list summary file>"
            );
        }
    }
}
