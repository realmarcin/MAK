package DataMining.func;

import DataMining.*;
import gnu.trove.map.hash.THashMap;
import mathy.stat;
import util.MoreArray;
import util.StringUtil;
import util.TabFile;
import util.TextFile;

import java.io.File;
import java.util.*;


/**
 * Search for cases of putative combinatorial regulation by considering biclusters enriched for regulation as well as
 * bicluster intersections enriched for pairs of binding sites.
 * <p/>
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: 10/26/13
 * Time: 10:29 AM
 */
public class FindCombRegbySiteChunked {

    boolean debug = false;
    boolean debugall = false;

    //HashMap<String, String> funmap = new HashMap<String, String>();
    //HashMap<String, String> TFmap = new HashMap<String, String>();
    //HashMap<String, String> localmap = new HashMap<String, String>();
    HashMap<String, String> overmap = new HashMap<String, String>();

    HashMap<String, String> globalcount;// = new HashMap<String, String>();

    HashMap bindpval, bindTF, bindsites;
    ValueBlockList vbl;

    double minoverlap = 0.0;//0.2;0.01;//

    String[][] tab_data;
    String[] yeastids, yeastnames;


    ArrayList outar = new ArrayList();

    String header = "direction\tkey\tvalues\tvalue\tfrxn\tgene overlap\tintersect genes\ttotal genes\t1i\t1g\t1e\t2i\t2g\t2e\texp overlap\tintesect exp\ttotal exp";

    InitRandVar irv;
    long seed = MINER_STATIC.RANDOM_SEEDS[0];

    ArrayList pvals_raw;// = new ArrayList();
    double[] pvals_adjust;
    HashMap pval_index;// = new HashMap();

    double pvalue_cutoff = 1;// 0.01;

    HashMap global_sitepair_count;// = new HashMap();
    String[] gene_labels;
    String[] exp_labels;
    HashMap done_pairsforgene_hash;// = new HashMap();
    //HashMap<String, String> globalcountpval;// = new HashMap();
    THashMap<String, String> globalcountpval;// = new HashMap();


    //HashMap biclusterpairsitepair_count = new HashMap();
    HashMap globalcountoverlapgenes;// = new HashMap();
    HashMap globalcountoverlapexps;// = new HashMap();

    HashMap globalcountdiffRgenes;// = new HashMap();

    HashMap globalcountdiffLgenes;// = new HashMap();


    ValueBlockList intersect_vbl;// = new ValueBlockList();
    ArrayList intersect_vbl_names;// = new ArrayList();

    boolean minimal = false;


    int chunk = 20000;
    int cur_counter = 0;
    int total_counter = 0;
    int num_chunk = 0;

    /**
     * @param args
     */
    public FindCombRegbySiteChunked(String[] args) {

        init(args);

        refreshVars();

        boolean done = false;

        while (!done) {

            if (total_counter + chunk >= overmap.size()) {
                done = true;
                System.out.println("Setting done to true");
            }

            Set overmapSet = loadCounts();

            String outpath = args[4] + "_combreg_" + minoverlap + "_" + pvalue_cutoff + "_" + num_chunk + ".txt";

            TextFile.write(outar, outpath);
            System.out.println("outpath " + outpath);

            pvals_adjust = MoreArray.ArrayListtoDouble(pvals_raw);

            ArrayList outglobalpval = new ArrayList();
            Set sglobal = null;
            Iterator itglobal = null;
            HashMap finalglobalcountpval = null;

            sglobal = globalcountpval.entrySet();
            itglobal = sglobal.iterator();
            finalglobalcountpval = new HashMap();
            while (itglobal.hasNext()) {
                Map.Entry cur = (Map.Entry) itglobal.next();
                String key = (String) cur.getKey();

                try {
                    int x = (Integer) pval_index.get(key);
                    double adjustpval = pvals_adjust[x];
                    if (adjustpval < pvalue_cutoff) {
                        finalglobalcountpval.put(key, cur.getValue() + ":" + adjustpval);
                    }
                } catch (Exception e) {
                    System.out.println("did not find key in pval_index " + key);
                    e.printStackTrace();
                }
            }


            System.out.println("finalglobalcountpval " + finalglobalcountpval.size());
            sglobal = finalglobalcountpval.entrySet();
            itglobal = sglobal.iterator();
            while (itglobal.hasNext()) {
                Map.Entry cur = (Map.Entry) itglobal.next();
                String value = (String) cur.getValue();
                if (debugall)
                    System.out.println("value " + value);
                String key = (String) cur.getKey();
                int index = key.lastIndexOf("__") + 2;
                int index2 = key.indexOf("_");
                String bics = key.substring(0, index - 2);

                String TFs = key.substring(index);

                String[] TFsar = TFs.split("_");

                String vbone = key.substring(0, index2);
                String vbtwo = key.substring(index2 + 1, index - 2);
                outglobalpval.add(key + "\t" + vbone + "\t" + vbtwo + "\t" + bics + "\t" + TFs + "\t" +
                        TFsar[0] + "\t" + TFsar[1] + "\t" + MoreArray.toString(value.split(":"), "\t"));
            }

            if (!minimal) {
                String outpath2 = args[4] + "_signifcombreg_" + minoverlap + "_" + this.pvalue_cutoff + "_" + num_chunk + ".txt";
                TextFile.write(outglobalpval, outpath2);
                System.out.println("outpath2 " + outpath2);


                ArrayList ids = new ArrayList();
                Set s3 = globalcountoverlapgenes.entrySet();
                Iterator it3 = s3.iterator();
                while (it3.hasNext()) {
                    Map.Entry cur = (Map.Entry) it3.next();
                    ids.add(cur.getKey() + "\t" + cur.getValue());
                }
                String outpath5 = args[4] + "_intersectgenes_" + minoverlap + "_" + this.pvalue_cutoff + "_" + num_chunk + ".txt";
                TextFile.write(ids, outpath5);


                ArrayList idsR = new ArrayList();
                Set s3a = globalcountdiffRgenes.entrySet();
                Iterator it3a = s3a.iterator();
                while (it3a.hasNext()) {
                    Map.Entry cur = (Map.Entry) it3a.next();
                    idsR.add(cur.getKey() + "\t" + cur.getValue());
                }
                String outpath5a = args[4] + "_Rgenes_" + minoverlap + "_" + pvalue_cutoff + "_" + num_chunk + ".txt";
                TextFile.write(idsR, outpath5a);

                ArrayList idsL = new ArrayList();
                Set s3b = globalcountdiffLgenes.entrySet();
                Iterator it3b = s3b.iterator();
                while (it3b.hasNext()) {
                    Map.Entry cur = (Map.Entry) it3b.next();
                    idsL.add(cur.getKey() + "\t" + cur.getValue());
                }
                String outpath5b = args[4] + "_Lgenes_" + minoverlap + "_" + pvalue_cutoff + "_" + num_chunk + ".txt";
                TextFile.write(idsL, outpath5b);


                ArrayList expids = new ArrayList();
                Set s4 = globalcountoverlapexps.entrySet();
                Iterator it4 = s4.iterator();
                while (it4.hasNext()) {
                    Map.Entry cur = (Map.Entry) it4.next();
                    expids.add(cur.getKey() + "\t" + cur.getValue());
                }
                String outpath6 = args[4] + "_intersectexp_" + minoverlap + "_" + pvalue_cutoff + "_" + num_chunk + ".txt";
                TextFile.write(expids, outpath6);


                String statheader = "site pair\tcount\tfrxn of bicluster pairs\tmean frxn\tmin frxn\tmax frxn\tintersect for max\tmean intersect\tmin intersect\tmax intersect";
                ArrayList outglobal = new ArrayList();
                ArrayList outglobalall = new ArrayList();
                if (num_chunk == 0)
                    outglobal.add(statheader);
                Set s2 = globalcount.entrySet();
                Iterator it2 = s2.iterator();

                while (it2.hasNext()) {
                    Map.Entry cur = (Map.Entry) it2.next();
                    outglobalall.add(cur.getKey() + "\t" + cur.getValue());
                    String[] ar = ((String) cur.getValue()).split("--");
                    ArrayList data_frxn = new ArrayList();
                    ArrayList data_intersect = new ArrayList();
                    for (int i = 0; i < ar.length; i++) {
                        String[] split = ar[i].split(":");
                        data_frxn.add(Double.parseDouble(split[3]));
                        data_intersect.add(Double.parseDouble(split[2]));
                        //String s1 = cur.getKey() + "\t" + split[0] + "\t" + split[1];
                        //System.out.println(s1);
                        //outglobal.add(s1);
                    }

                    double[] data_frxnd = MoreArray.ArrayListtoDouble(data_frxn);
                    double[] data_intersectd = MoreArray.ArrayListtoDouble(data_intersect);


                    double max = stat.findMax(data_frxnd);
                    int maxind = MoreArray.getArrayInd(data_frxnd, max);
                    outglobal.add(cur.getKey() + "\t" + ar.length + "\t" + (double) ar.length / (2.0 * (double) overmapSet.size()) + "\t" + stat.avg(data_frxnd) + "\t" + stat.findMin(data_frxnd) + "\t" + max + "\t" + data_intersectd[maxind] + "\t" +
                            stat.avg(data_intersectd) + "\t" + stat.findMin(data_intersectd) + "\t" + stat.findMax(data_intersectd));//+"\t"+Math.log());
                }

                TextFile.write(outglobal, args[4] + "_combregstats_" + minoverlap + "_" + pvalue_cutoff + "_" + num_chunk + ".txt");
                String outpath3 = args[4] + "_combregstatsall_" + minoverlap + "_" + pvalue_cutoff + "_" + num_chunk + ".txt";
                TextFile.write(outglobalall, outpath3);

                System.out.println("outpath3 " + outpath3);


                if (intersect_vbl.size() > 0) {
                    String stoplist = intersect_vbl.toString("#" + "\n" + MINER_STATIC.HEADER_VBL);// ValueBlock.toStringShortColumns());
                    String outpath7 = args[4] + "_intersect_" + minoverlap + "_" + pvalue_cutoff + "_" + num_chunk + ".vbl";
                    System.out.println("writing " + outpath7);
                    TextFile.write(stoplist, outpath7);
                }

                String outpath9 = args[4] + "_intersect_names_" + minoverlap + "_" + pvalue_cutoff + "_" + num_chunk + ".txt";
                TextFile.write(intersect_vbl_names, outpath9);
            }

            cur_counter = 0;
            num_chunk++;

            refreshVars();
        }

        System.out.println("stats : total_counter " + total_counter + "\tnum_chunks " + overmap + "\tovermap " + overmap.size());

        try {
            irv.Rengine.end();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        System.exit(0);
    }

    /**
     *
     */
    private void refreshVars() {

        outar = new ArrayList();
        if (num_chunk == 0)
            outar.add(header);

        globalcount = new HashMap<String, String>();

        pvals_raw = new ArrayList();
        pval_index = new HashMap();

        //double expected_maximal_number_of_data = 100000000d;
        //int capacity = (int) ((expected_maximal_number_of_data) / 0.75 + 1);
        globalcountpval = new THashMap<String, String>();//(capacity);

        globalcountoverlapgenes = new HashMap();
        globalcountoverlapexps = new HashMap();
        globalcountdiffRgenes = new HashMap();
        globalcountdiffLgenes = new HashMap();

        intersect_vbl = new ValueBlockList();

        intersect_vbl_names = new ArrayList();
    }

    /**
     * @return
     */
    private Set loadCounts() {
        Set overmapSet = overmap.keySet();
        Iterator it = overmapSet.iterator();
        int totalcombpair = 0;
        int totalcombpairsites = 0;

        int start = 0;
        while (it.hasNext() && start < total_counter) {
            start++;
            String key = (String) it.next();
        }

        while (it.hasNext() && cur_counter < chunk) {
            String key = (String) it.next();
            //if (key.equals("40_89") || key.equals("30_89")) {
            //System.out.println("key " + key);
            double curval = Double.parseDouble((String) overmap.get(key));
            if (curval > minoverlap) {
                String[] split = key.split("_");
                System.out.println("key " + key + "\t" + split[0] + "\t" + split[1]);
                String[] TFone = new String[0];
                int vbone = -1;

                vbone = Integer.parseInt(split[0]);
                System.out.println("searching for vbone " + vbone);
                try {
                    //retrieve enriched binding sites in ONE
                    TFone = MoreArray.ArrayListtoString((ArrayList) bindpval.get(vbone - 1));
                } catch (Exception e) {
                    TFone = new String[1];
                    TFone[0] = "noneA";
                    //e.printStackTrace();
                }
//                    System.out.println("one");
//                    MoreArray.printArray(TFone);

                int vbtwo = Integer.parseInt(split[1]);

                System.out.println("searching for vbtwo " + vbtwo);
                String[] TFtwo = new String[0];
                try {
                    //retrieve enriched binding sites in TWO
                    TFtwo = MoreArray.ArrayListtoString((ArrayList) bindpval.get(vbtwo - 1));
                } catch (Exception e) {
                    TFtwo = new String[1];
                    TFtwo[0] = "noneB";
                    //e.printStackTrace();
                }
//                    System.out.println("two");
//                    MoreArray.printArray(TFtwo);
                boolean combpair = doTFOne(key, TFone, vbone, vbtwo, TFtwo, "0");
                combpair = doTFOne(key, TFtwo, vbtwo, vbone, TFone, "1");//= doTFTwo(key, TFone, vbone, vbtwo, TFtwo, combpair);

                if (combpair)
                    totalcombpair++;

                       /* if (combpairsites)
                            totalcombpairsites++;*/

                System.out.println("totalcombpair " + totalcombpair + "\ttotalcombpairsites " + totalcombpairsites);
            }

            cur_counter++;
            total_counter++;
            if (debug) {
                System.out.println("start " + start + "\tcur_counter " + cur_counter + "\tchunk " + chunk +
                        "\ttotal_counter " + total_counter + "\tnum_chunk " + num_chunk);
            }
        }
        return overmapSet;
    }

    /**
     * @param vbone
     * @param vbtwo
     * @param vb1
     * @param vb2
     * @param expover
     * @param geneover
     * @param commongenes
     * @param commonexps
     * @param intersectgenes
     * @param intersectexp
     * @return
     */
    private String makeString(int vbone, int vbtwo, ValueBlock vb1, ValueBlock vb2, double expover, double geneover, int[] commongenes, int[] commonexps, int intersectgenes, int intersectexp) {
        return geneover + "\t" + intersectgenes + "\t" + commongenes.length + "\t" + (vbone - 1) + "\t" + vb1.genes.length + "\t" + vb1.exps.length + "\t" +
                (vbtwo - 1) + "\t" + vb2.genes.length + "\t" + vb2.exps.length + "\t" + expover + "\t" + intersectexp + "\t" + commonexps.length;
    }

    /**
     * @param key
     * @param TFone
     * @param vbone
     * @param vbtwo
     * @param TFtwo
     * @param dir
     * @return
     */
    private boolean doTFOneNew(String key, String[] TFone, int vbone, int vbtwo, String[] TFtwo, String dir) {

        String one = MoreArray.toString(TFone, "_");
        String two = MoreArray.toString(TFtwo, "_");
        ValueBlock vb1 = (ValueBlock) vbl.get(vbone - 1);
        ValueBlock vb2 = (ValueBlock) vbl.get(vbtwo - 1);

        double expover = BlockMethods.computeBlockOverlapExpSum(vb1, vb2);
        double geneover = BlockMethods.computeBlockOverlapGeneSum(vb1, vb2);

        boolean combpair = false;

        //find intersection and if intersection genes have dual binding sites
        int[] commongenes = MoreArray.crossFirstIndex(vb1.genes, vb2.genes);
        int[] commonexps = MoreArray.crossFirstIndex(vb1.exps, vb2.exps);

        ArrayList arg = new ArrayList();
        for (int i = 0; i < commongenes.length; i++) {
            if (commongenes[i] != -1) {
                int cur = vb2.genes[commongenes[i]];
                arg.add(cur);
            }
        }
        ArrayList are = new ArrayList();
        for (int i = 0; i < commonexps.length; i++) {
            if (commonexps[i] != -1) {
                int cur = vb2.exps[commonexps[i]];
                are.add(cur);
            }
        }

        int[] intersectg = MoreArray.ArrayListtoInt(arg);
        int[] intersecte = MoreArray.ArrayListtoInt(are);
        ValueBlock common = new ValueBlock(intersectg, intersecte);

        if (!intersect_vbl.contains(common)) {
            intersect_vbl.add(common);
            intersect_vbl_names.add(key);
            System.out.println("intersect_vbl " + intersect_vbl.size());
        }
        int intersectgenes = stat.countGreaterThan(commongenes, -1);
        int intersectexp = stat.countGreaterThan(commonexps, -1);

        String label = "" + dir + "\t" + makeString(vbone, vbtwo, vb1, vb2, expover, geneover, commongenes, commonexps, intersectgenes, intersectexp);

        //System.out.println(label);
        //MoreArray.printArray(commongenes);

        HashMap pairs = new HashMap();
        ArrayList sites = (ArrayList) bindTF.get(vbone);
        //for genes in intersection find all TF pairs with one matching an enriched TF in bicluster one and the other enriched TF in bicluster two
        for (int z = 0; z < commongenes.length; z++) {
            if (commongenes[z] != -1) {
                String cur = (String) sites.get(z);
                String[] splitsites = cur.split("_");
                Arrays.sort(splitsites);
                //for each gene consider all pairs of sites
                for (int m = 0; m < splitsites.length; m++) {
                    //System.out.println(splitsites[m]);
                    for (int n = m + 1; n < splitsites.length; n++) {
                        int indexTFenrichMtwo = MoreArray.getArrayInd(TFtwo, splitsites[m]);
                        int indexTFenrichMone = MoreArray.getArrayInd(TFone, splitsites[m]);
                        int indexTFenrichNtwo = MoreArray.getArrayInd(TFtwo, splitsites[n]);
                        int indexTFenrichNone = MoreArray.getArrayInd(TFone, splitsites[n]);
                        /*
                        //either m is vb one and n is vb two or vice versa
                        if ((indexTFenrichNtwo != -1 && indexTFenrichNone == -1 && indexTFenrichMone != -1 && indexTFenrichMtwo == -1) ||
                                (indexTFenrichMtwo != 1 && indexTFenrichMone == -1 && indexTFenrichNone != -1 && indexTFenrichNtwo == -1)) {
                        */
                        //if ((indexTFenrichNtwo == -1 && indexTFenrichMone == -1) ||
                        //        (indexTFenrichMtwo == -1 && indexTFenrichNone == -1)) {
                        //System.out.println("found match");
                        String[] sort = {splitsites[m], splitsites[n]};
                        Arrays.sort(sort);
                        String sitepair = sort[0] + "_" + sort[1];
                        //System.out.println("found match " + key + "\t" + sitepair);
                        Object o = pairs.get(sitepair);
                        if (o == null) {
                            pairs.put(sitepair, 1);
                        } else {
                            pairs.put(sitepair, ((Integer) o) + 1);
                        }
                        //}
                    }
                }
            }
        }
        ArrayList ids = new ArrayList();
        ArrayList names = new ArrayList();
        for (int z = 0; z < commongenes.length; z++) {
            if (commongenes[z] != -1) {
                ids.add(yeastids[commongenes[z] - 1]);
                names.add(yeastnames[commongenes[z] - 1]);
            }
        }

        ArrayList expnames = new ArrayList();
        for (int z = 0; z < commonexps.length; z++) {
            if (commonexps[z] != -1) {
                expnames.add(exp_labels[commonexps[z] - 1]);
            }
        }

        ArrayList diffRnames = new ArrayList();
        ArrayList diffLnames = new ArrayList();
        for (int z = 0; z < vb1.genes.length; z++) {
            try {
                int arrayInd = MoreArray.getArrayInd(commongenes, vb1.genes[z]);
                if (arrayInd == -1) {
                    diffRnames.add(yeastnames[vb1.genes[z] - 1]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (int z = 0; z < vb2.genes.length; z++) {
            try {
                int arrayInd = MoreArray.getArrayInd(commongenes, vb2.genes[z]);
                if (arrayInd == -1) {
                    diffLnames.add(yeastnames[vb2.genes[z] - 1]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        combpair = doGlobalCount("" + (vbone - 1) + "_" + (vbtwo - 1), key, dir, combpair, intersectgenes,
                label, pairs, one, two, MoreArray.arrayListtoString(ids, ","),
                MoreArray.arrayListtoString(names, ","), MoreArray.arrayListtoString(expnames, ","), geneover, expover,
                MoreArray.ArrayListtoString(diffRnames), MoreArray.ArrayListtoString(diffLnames));


        return combpair;
    }

    /**
     * @param key
     * @param TFone
     * @param vbone
     * @param vbtwo
     * @param TFtwo
     * @param dir
     * @return
     */

    private boolean doTFOne(String key, String[] TFone, int vbone, int vbtwo, String[] TFtwo, String dir) {

        String one = MoreArray.toString(TFone, "_");
        String two = MoreArray.toString(TFtwo, "_");
        ValueBlock vb1 = (ValueBlock) vbl.get(vbone - 1);
        ValueBlock vb2 = (ValueBlock) vbl.get(vbtwo - 1);

        double expover = BlockMethods.computeBlockOverlapExpSum(vb1, vb2);
        double geneover = BlockMethods.computeBlockOverlapGeneSum(vb1, vb2);

        boolean combpair = false;

        //for each enriched TF in bicluster one
        for (int a = 0; a < TFone.length; a++) {
            //for (int b = 0; b < TFtwo.length; b++) {
            //  System.out.println(TFone[a] + "\t" + TFtwo[b]);
            //int indintwo = MoreArray.getArrayInd(TFtwo, TFone[a]);
            //int indintwo = two.indexOf(TFone[a]);

            //int[] commonTFs = StringUtil.crossIndex(TFone, TFtwo);
            //int TFsum = stat.countGreaterThan(commonTFs, -1);
            //System.out.println("TFsum " + TFsum);
            //if (indintwo == -1) {

            //if enriched TF in bicluster one not enriched in bicluster two
            if (!two.equals(TFone[a]) && !two.startsWith(TFone[a] + "_") && !two.endsWith("_" + TFone[a])) {

                System.out.println("doTFOne " + a + "\t" + key + "\tTFone[a] " + TFone[a] + "\tone " + one + "\ttwo " + two);
                //double overlap = BlockMethods.JaccardIndexGenesExps(vb1, vb2);

                //find intersection and if intersection genes have dual binding sites, reports indices relative to vb2
                int[] commongenes = MoreArray.crossFirstIndex(vb1.genes, vb2.genes);
                int[] commonexps = MoreArray.crossFirstIndex(vb1.exps, vb2.exps);

                if (debugall) {
                    System.out.println("commongenes");
                    MoreArray.printArray(commongenes);
                    System.out.println("commonexps");
                    MoreArray.printArray(commonexps);
                }

                //int[] commongenes_nominone = MoreArray.removeByVal(commongenes, new int[]{-1});
                //int[] commonexps__nominone = MoreArray.removeByVal(commonexps, new int[]{-1});


                ArrayList arg = new ArrayList();
                for (int i = 0; i < commongenes.length; i++) {
                    if (commongenes[i] != -1) {
                        int cur = vb2.genes[commongenes[i]];
                        arg.add(cur);
                    }
                }
                ArrayList are = new ArrayList();
                for (int i = 0; i < commonexps.length; i++) {
                    if (commonexps[i] != -1) {
                        int cur = vb2.exps[commonexps[i]];
                        are.add(cur);
                    }
                }

                int[] intersectg = MoreArray.ArrayListtoInt(arg);
                int[] intersecte = MoreArray.ArrayListtoInt(are);
                ValueBlock common = new ValueBlock(intersectg, intersecte);


                if (debugall) {
                    System.out.println("common.toString()");
                    System.out.println(common.toString());
                }

                if (!minimal && !intersect_vbl.contains(common)) {
                    intersect_vbl.add(common);
                    intersect_vbl_names.add(key);
                    if (debugall)
                        System.out.println("intersect_vbl " + intersect_vbl.size());
                }
                int intersectgenes = stat.countGreaterThan(commongenes, -1);
                int intersectexp = stat.countGreaterThan(commonexps, -1);

                String label = "" + dir + "\t" + makeString(vbone, vbtwo, vb1, vb2, expover, geneover, intersectg, intersecte, intersectgenes, intersectexp);

                //System.out.println(label);
                //MoreArray.printArray(commongenes);

                HashMap pairs = new HashMap();
                ArrayList sites = (ArrayList) bindTF.get(vbone);
                //for genes in intersection find all TF pairs with one matching an enriched TF in bicluster one and the other enriched TF in bicluster two
                for (int z = 0; z < commongenes.length; z++) {
                    //only for common genes
                    if (commongenes[z] != -1) {
                        String cur = (String) sites.get(z);
                        if (!cur.equals("null")) {
                            String[] splitsites = cur.split("_");
                            Arrays.sort(splitsites);
                            //for each gene consider all pairs of sites
                            for (int m = 0; m < splitsites.length; m++) {
                                //System.out.println(splitsites[m]);
                                for (int n = m + 1; n < splitsites.length; n++) {
                                    int indexTFenrichMtwo = MoreArray.getArrayInd(TFtwo, splitsites[m]);
                                    int indexTFenrichMone = MoreArray.getArrayInd(TFone, splitsites[m]);
                                    int indexTFenrichNtwo = MoreArray.getArrayInd(TFtwo, splitsites[n]);
                                    int indexTFenrichNone = MoreArray.getArrayInd(TFone, splitsites[n]);

                                    //either m is vb one and n is vb two or vice versa
                                    //if ((indexTFenrichNtwo != -1 && indexTFenrichNone == -1 && splitsites[m].equals(TFone[a]) && indexTFenrichMtwo == -1) ||
                                    //       (indexTFenrichMtwo != 1 && indexTFenrichMone == -1 && splitsites[n].equals(TFone[a]) && indexTFenrichNtwo == -1)) {

                                    if ((indexTFenrichNtwo != -1 && indexTFenrichNone == -1 && splitsites[m].equals(TFone[a]) && indexTFenrichMtwo == -1) ||
                                            (indexTFenrichMtwo != 1 && indexTFenrichMone == -1 && splitsites[n].equals(TFone[a]) && indexTFenrichNtwo == -1)) {
                                        //System.out.println("found match");
                                        String[] sort = {splitsites[m], splitsites[n]};
                                        Arrays.sort(sort);
                                        String sitepair = sort[0] + "_" + sort[1];
                                        if (debugall)
                                            System.out.println("doTFOne found match " + key + "\t" + sitepair);
                                        Object o = pairs.get(sitepair);
                                        if (o == null) {
                                            pairs.put(sitepair, 1);
                                        } else {
                                            pairs.put(sitepair, ((Integer) o) + 1);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (debugall)
                    System.out.println("doTFOne pairs " + pairs.size());

                ArrayList ids = new ArrayList();
                ArrayList names = new ArrayList();
                for (int z = 0; z < commongenes.length; z++) {
                    if (commongenes[z] != -1) {
                        ids.add(yeastids[vb2.genes[commongenes[z]] - 1]);
                        names.add(yeastnames[vb2.genes[commongenes[z]] - 1]);
                    }
                }
                if (debugall)
                    System.out.println("doTFOne ids " + ids.size());

                ArrayList expnames = new ArrayList();
                for (int z = 0; z < commonexps.length; z++) {
                    if (commonexps[z] != -1) {
                        expnames.add(exp_labels[vb2.exps[commonexps[z]] - 1]);
                    }
                }
                if (debugall)
                    System.out.println("doTFOne expnames " + expnames.size());


                ArrayList diffRnames = new ArrayList();
                ArrayList diffLnames = new ArrayList();
                for (int z = 0; z < vb1.genes.length; z++) {
                    try {
                        int arrayInd = MoreArray.getArrayInd(commongenes, vb1.genes[z]);
                        if (arrayInd == -1) {
                            diffRnames.add(yeastids[vb1.genes[z] - 1]);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (debugall)
                    System.out.println("doTFOne diffRnames " + diffRnames.size());


                for (int z = 0; z < vb2.genes.length; z++) {
                    try {
                        int arrayInd = MoreArray.getArrayInd(commongenes, vb2.genes[z]);
                        if (arrayInd == -1) {
                            diffLnames.add(yeastids[vb2.genes[z] - 1]);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (debugall)
                    System.out.println("doTFOne diffLnames " + diffLnames.size());


                if (debugall)
                    System.out.println("bf doGlobalCount");

                combpair = doGlobalCount("" + (vbone - 1) + "_" + (vbtwo - 1), key, dir, combpair, intersectgenes,
                        label, pairs, one, two, MoreArray.arrayListtoString(ids, ","),
                        MoreArray.arrayListtoString(names, ","), MoreArray.arrayListtoString(expnames, ","), geneover, expover,
                        MoreArray.ArrayListtoString(diffRnames), MoreArray.ArrayListtoString(diffLnames));

                if (debugall)
                    System.out.println("af doGlobalCount");
            }
        }
        return combpair;
    }

    /**
     * @param key
     * @param dir
     * @param combpair
     * @param intersectgenes
     * @param label
     * @param pairs
     * @return
     */
    private boolean doGlobalCount(String vbpair, String key, String dir, boolean combpair, int intersectgenes,
                                  String label, HashMap pairs, String one, String two, String ids, String names, String expnames, double geneover, double expover,
                                  String[] Rnames, String[] Lnames) {
        Set s2 = pairs.entrySet();
        Iterator it2 = s2.iterator();
        if (debugall)
            System.out.println("doGlobalCount " + vbpair + "\t" + key + "\t" + s2.size());
        while (it2.hasNext()) {
            Map.Entry cur = (Map.Entry) it2.next();

            String fullkey = key + "__" + cur.getKey();

            if (debugall) {
                System.out.println("fullkey " + fullkey);
                System.out.println("doGlobalCount " + cur);
                System.out.println("doGlobalCount " + cur.getKey());
                System.out.println("doGlobalCount " + global_sitepair_count.size());
                System.out.println("doGlobalCount " + global_sitepair_count.get(cur.getKey()));
            }
            int c = (Integer) global_sitepair_count.get(cur.getKey());
            //System.out.println("c " + c);
            //total genes in bicluster - genes with site pair
            int d = done_pairsforgene_hash.size() - c;

            //System.out.println("c d " + c + "\t" + d);
            if (debugall)
                System.out.println("pvals_raw.size() bf " + pvals_raw.size());

            double pval = 1;//vbpair + ":" + cur.getKey()
            try {
                pval = computePval(fullkey, (Integer) cur.getValue(), intersectgenes - (Integer) cur.getValue(), c, d);
            } catch (Exception e) {
                e.printStackTrace();
            }


            if (debugall)
                System.out.println("pvals_raw.size() af " + pvals_raw.size());

            double frxn = ((double) (Integer) cur.getValue()) / (double) intersectgenes;//commongenes.length;
            //if (frxn > frxnintersect) {
            if (debugall)
                System.out.println(cur.getKey() + "\t" + frxn + "\t" + pval);
            combpair = true;
            String s = dir + "\t" + key + "\t" + cur.getKey() + "\t" + (Integer) cur.getValue() + "\t" + frxn + "\t" + label;
            //System.out.println("positive case TF " + s);
            //System.out.println(label);

            outar.add(s);


            if (!minimal) {
                Object o = globalcount.get(cur.getKey());
                if (o == null) {
                    StringBuilder sb = new StringBuilder(dir);
                    sb.append(":").append(key).append(":").append(cur.getValue()).append(":").append(frxn).append(":").
                            append(intersectgenes);

                    globalcount.put((String) cur.getKey(), sb.toString());// + ":" + pval);
                    //globalcount.put((String) cur.getKey(), dir + ":" + key + ":" + cur.getValue() + ":" + frxn + ":" + intersectgenes);// + ":" + pval);
                } else {
                    StringBuilder sb = new StringBuilder((String) o);
                    sb.append("--").append(dir).append(":").append(key).append(":").append(cur.getValue()).append(":").
                            append(frxn).append(":").append(intersectgenes);

                    globalcount.put((String) cur.getKey(), sb.toString());// + ":" + pval);
                    //globalcount.put((String) cur.getKey(), (String) o + "--" + dir + ":" + key + ":" + cur.getValue() + ":" + frxn + ":" + intersectgenes);// + ":" + pval);
                }
                if (debugall)
                    System.out.println("globalcount " + globalcount.size());
            }


            //String e = dir + ":" + one + ":" + two + ":" + key + ":" + cur.getValue() + ":" + frxn + ":" + intersectgenes + ":" + geneover + ":" + expover;
            StringBuilder sb = new StringBuilder(dir);
            sb.append(":").append(one).append(":").append(two).append(":").append(key).append(":").
                    append(cur.getValue()).append(":").append(frxn).append(":").append(intersectgenes).
                    append(":").append(geneover).append(":").append(expover);

            //globalcountpval_key.add(fullkey);
            //globalcountpval_list.add(sb.toString());

            globalcountpval.put(fullkey, sb.toString());// + ":" + pval);

            if (!minimal) {
                globalcountoverlapgenes.put(fullkey, ids + "_____" + names);// + ":" + pval);

                globalcountdiffRgenes.put(fullkey, MoreArray.toString(Rnames, ","));// + ":" + pval);
                globalcountdiffLgenes.put(fullkey, MoreArray.toString(Lnames, ","));// + ":" + pval);

                globalcountoverlapexps.put(fullkey, ids + "_____" + expnames);// + ":" + pval);
                //biclusterpairsitepair_count.put(fullkey, cur.getValue());// + ":" + pval);
            }
            //}
        }

        if (debug) {
            //System.out.println("globalcount " + globalcount.size());
            //System.out.println("globalcountpval_key " + globalcountpval_key.size());
            System.out.println("globalcountpval " + globalcountpval.size());
            //System.out.println("biclusterpairsitepair_count " + biclusterpairsitepair_count.size());
            //System.out.println("doGlobalCount combpair " + combpair);
        }

        return combpair;
    }


    /**
     * @param label
     * @param a
     * @param b
     * @param c
     * @param d
     * @return
     */
    private double computePval(String label, int a, int b, int c, int d) {

        if (debugall)
            System.out.println("computePval " + a + "\t" + b + "\t" + c + "\t" + d);

        //in block w/ TF
        //int a = bicluster_targets;
        //in block not TF
        //int b = samps.size() - bicluster_targets;
        //out block w/ TF
        //int countgreat = stat.countGreaterThan(Matrix.extractColumn(motif_data.data, labelindex + 1), RANK_SCORE_THRESHOLD);
        //int c = (int) Math.max(0, countgreat - a);
        //out block not TF
        //int d = motif_data.data.length - (a + b) - c;

        String rcall1 = "data <- matrix(c(" + a + "," + b + "," + c + "," + d + "), " +
                "nr=2, dimnames=list(c(\"in\",\"out\"), c(\"TF\",\"noTF\")))";
        if (debugall)
            System.out.println("computePval " + rcall1);

        irv.Rexpr = irv.Rengine.eval(rcall1);

        double pval = Double.NaN;
        try {
            String rcall2 = "fisher.test(data, alternative=\"greater\")$p.value";
            if (debugall)
                System.out.println("computePval " + rcall2);

            irv.Rexpr = irv.Rengine.eval(rcall2);
            pval = irv.Rexpr.asDouble();
            if (debugall)
                System.out.println("computePval " + pval);

        } catch (Exception e) {
            System.out.println("TF test data " + a + "\t" + b + "\t" + c + "\t" + d);
            e.printStackTrace();
        }

        //System.out.println("computePval " + a + "\t" + b + "\t" + c + "\t" + d + "\t" +pval);

        pvals_raw.add(pval);
        pval_index.put(label, pvals_raw.size() - 1);

        //System.out.println("computePval pvals_raw.size() " + pvals_raw.size());

        return pval;
    }

    /**
     *
     */
    private void adjustPvals() {
        if (pvals_raw == null)
            System.out.println("pvals_raw == null");
        if (irv == null)
            System.out.println("irv == null");
        if (irv.Rengine == null)
            System.out.println("irv.Rengine == null");
        irv.Rengine.assign("data", MoreArray.ArrayListtoDouble(pvals_raw));
        //irv.Rengine.eval("data <- c(" + MoreArray.toString(TFlabelpval, ",") + ")");
        String rcall2 = "p.adjust(data,\"fdr\")";
        irv.Rexpr = irv.Rengine.eval(rcall2);
        pvals_adjust = irv.Rexpr.asDoubleArray();
    }

    /**
     * @param args
     */
    private void init(String[] args) {

        String[] overdata = TextFile.readtoArray(args[0]);
        //String[] TFdata = TextFile.readtoArray(args[1]);
        //String[] fundata = TextFile.readtoArray(args[2]);
        String[] localdata = TextFile.readtoArray(args[3]);

        /*for (int i = 1; i < fundata.length; i++) {
            //System.out.println(i + "\t" + fundata[i]);
            String[] now = fundata[i].split(" = ");
            if (now.length > 1 && !now[1].equals("none")) {
                //System.out.println("adding fun " + now[0] + "\t" + now[1]);
                funmap.put(now[0], now[1]);
            }
        }*/
        //System.exit(0);

        File tff = new File(args[1]);
        String[] files = tff.list();
        bindpval = new HashMap();
        bindTF = new HashMap();
        bindsites = new HashMap();

        for (int i = 0; i < files.length; i++) {
            if (files[i].endsWith("TFbind_pval0.001.txt")) {
                int start = files[i].lastIndexOf("__") + 2;
                int stop = files[i].indexOf("_", start + 1);
                int label = Integer.parseInt(files[i].substring(start, stop));
                try {
                    String[][] data = TabFile.readtoArray(args[1] + "/" + files[i]);
                    ArrayList ar = new ArrayList();
                    for (int j = 0; j < data.length; j++) {
                        ar.add(data[j][1]);
                    }
                    //System.out.println("adding for 1 " + label);
                    bindpval.put(label, ar);
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            } else if (files[i].endsWith("TFbind.txt")) {
                int start = files[i].lastIndexOf("__") + 2;
                int stop = files[i].indexOf("_", start + 1);
                int label = Integer.parseInt(files[i].substring(start, stop)) + 1;
                String[] data = TabFile.readtoArrayOne(args[1] + "/" + files[i]);
                ArrayList ar = new ArrayList();
                for (int j = 0; j < data.length; j++) {
                    //if (!data[j].equals("null"))
                    ar.add(data[j]);
                }
                //System.out.println("adding for 2 " + label);
                bindTF.put(label, ar);
            } else if (files[i].endsWith("TFbind_motif.txt")) {
                int start = files[i].lastIndexOf("__") + 2;
                int stop = files[i].indexOf("_", start + 1);
                int label = Integer.parseInt(files[i].substring(start, stop)) + 1;
                String[] data = TabFile.readtoArrayOne(args[1] + "/" + files[i]);
                ArrayList ar = new ArrayList();
                for (int j = 0; j < data.length; j++) {
                    ar.add(data[j]);
                }
                //System.out.println("adding for 2 " + label);
                bindsites.put(label, ar);
            }
        }


        //System.exit(0);

        /*for (int i = 1; i < localdata.length; i++) {
            //System.out.println(i + "\t" + localdata[i]);
            String[] now = localdata[i].split(" = ");
            if (!now[1].equals("none")) {
                localmap.put(now[0], now[1]);
            }
        }*/

        overmap = new HashMap<String, String>();

        for (int i = 1; i < overdata.length; i++) {
            String[] now = overdata[i].split(" = ");
            //overmap.put(StringUtil.replace(now[0], " ((pp)) ", "_"), now[1]);
            //System.out.println("over " + StringUtil.replace(now[0], " ((pp)) ", "_") + "\t" + now[1]);
            overmap.put(StringUtil.replace(now[0], " ((pp)) ", "_"), now[1]);
        }

        try {
            vbl = ValueBlockListMethods.readAny(args[4], false);
        } catch (Exception e) {
            e.printStackTrace();
        }


        tab_data = TabFile.readtoArray(args[5]);
        System.out.println("read tab_data " + tab_data.length + "\t" + tab_data[0].length);
        yeastids = new String[tab_data.length - 1];
        yeastnames = new String[tab_data.length - 1];
        for (int i = 1; i < tab_data.length; i++) {
            yeastids[i - 1] = tab_data[i][7];
            yeastnames[i - 1] = StringUtil.replace(tab_data[i][8], ",", "_");
            yeastnames[i - 1] = StringUtil.replace(tab_data[i][8], "'", "_");
        }
        if (debugall) {
            System.out.println("yeastnames");
            System.out.println(MoreArray.toString(yeastnames, ","));
        }


        File testg = new File(args[6]);
        if (testg.exists()) {
            try {
                String[][] sarray = TabFile.readtoArray(args[6]);
                System.out.println("setLabels g " + sarray.length + "\t" + sarray[0].length);
                int col = 2;
                String[] n = MoreArray.extractColumnStr(sarray, col);
                gene_labels = MoreArray.replaceAll(n, "\"", "");
                System.out.println("setLabels gene " + gene_labels.length + "\t" + gene_labels[0]);
            } catch (Exception e) {
                System.out.println("expecting 2 cols");
                //e.printStackTrace();
                try {
                    String[][] sarray = TabFile.readtoArray(args[6]);
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

        File teste = new File(args[7]);
        if (teste.exists()) {
            try {
                String[][] sarray = TabFile.readtoArray(args[7]);
                System.out.println("setLabels e " + sarray.length + "\t" + sarray[0].length);
                int col = 2;
                String[] n = MoreArray.extractColumnStr(sarray, col);
                exp_labels = MoreArray.replaceAll(n, "\"", "");
                System.out.println("setLabels exp " + exp_labels.length + "\t" + exp_labels[0]);
            } catch (Exception e) {
                System.out.println("expecting 2 cols");
                //e.printStackTrace();
                try {
                    String[][] sarray = TabFile.readtoArray(args[7]);
                    System.out.println("setLabels e " + sarray.length + "\t" + sarray[0].length);
                    int col = 1;
                    String[] n = MoreArray.extractColumnStr(sarray, col);
                    exp_labels = MoreArray.replaceAll(n, "\"", "");
                    System.out.println("setLabels gene " + exp_labels.length + "\t" + exp_labels[0]);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }

        global_sitepair_count = new HashMap();
        done_pairsforgene_hash = new HashMap();
        //count pairs of sites for all genes in biclusters
        for (int i = 0; i < files.length; i++) {

            if (files[i].endsWith("TFbind.txt")) {
                try {
                    int start = files[i].lastIndexOf("__") + 2;
                    int stop = files[i].indexOf("_", start + 1);
                    int label = Integer.parseInt(files[i].substring(start, stop));
                    //System.out.println("loading " + label + "\t" + vbl.size());
                    ValueBlock vb = (ValueBlock) vbl.get(label);
                    String[] data = TabFile.readtoArrayOne(args[1] + "/" + files[i]);
                    ArrayList ar = new ArrayList();
                    for (int j = 0; j < data.length; j++) {
                        //System.out.println("TFbind " + data.length + "\t" + vb.genes.length + "\t" +
                        //        gene_labels.length + "\t" + gene_labels[vb.genes[j]] + "\t" + done_pairsforgene_hash.get(gene_labels[vb.genes[j]]));
                        Object o1 = done_pairsforgene_hash.get(gene_labels[vb.genes[j] - 1]);
                        //only count for each gene once, only count genes
                        if (o1 == null) {
                            //System.out.println("TFbind data[j] " + data[j]);
                            String[] split = data[j].split("_");
                            for (int x = 0; x < split.length; x++) {
                                for (int y = x + 1; y < split.length; y++) {
                                    String[] sort = {split[x], split[y]};
                                    Arrays.sort(sort);
                                    String sitepair = sort[0] + "_" + sort[1];
                                    Object o = global_sitepair_count.get(sitepair);
                                    //System.out.println(i + "\t" + j + "\t" + sitepair + "\t" + o);
                                    if (o == null)
                                        global_sitepair_count.put(sitepair, 1);
                                    else {
                                        Integer oi = (Integer) o;
                                        global_sitepair_count.put(sitepair, (Integer) oi + 1);
                                    }
                                }
                            }

                            done_pairsforgene_hash.put(gene_labels[vb.genes[j] - 1], 1);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("failed to load " + files[i]);
                    e.printStackTrace();
                    System.exit(0);
                }
            }
        }

        //System.exit(0);

        try {
            irv = new InitRandVar(seed, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (args.length == 9) {
            if (args[8].equalsIgnoreCase("y"))
                debug = true;
            else if (args[8].equalsIgnoreCase("n"))
                debug = false;
        }

    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 8 || args.length == 9) {
            FindCombRegbySiteChunked rm = new FindCombRegbySiteChunked(args);
        } else {
            System.out.println("syntax: java DataMining.func.FindCombRegbySiteChunked\n" +
                    "<overlap data>\n" +
                    "<TF path>\n" +
                    "<func path>\n" +
                    "<localization path>\n" +
                    "<vbl>\n" +
                    "<tab file path>\n" +
                    "<gene labels>\n" +
                    "<exp labels>\n" +
                    "<OPTIONAL DEBUG Y/>"
            );
        }
    }
}
