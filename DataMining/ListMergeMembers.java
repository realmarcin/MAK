package DataMining;

import dtype.IntArrayWrapper;
import mathy.Matrix;
import mathy.stat;
import util.*;

import java.io.File;
import java.util.*;

/**
 * Merges a list of input biclusters.
 * <p/>
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: Feb 2, 2012
 * Time: 9:12:55 PM
 */
public class ListMergeMembers {

    boolean debug = false;

    String[] valid_args = {
            "-dir", "-crit", "-param", "-ocut", "-numgene", "-misscut", "-complete", "-out", "-live", "-debug"//"-sum",
    };
    HashMap options;

    String outdir = "";

    ValueBlockList vbl;
    //ValueBlockList vbl_merged_all;
    //ValueBlockList vbl_merged_trim;
    ValueBlockList vbl_copy;

    HashMap genesHash;
    HashMap expsHash;

    int GENE_SIZE = -1;
    int EXP_SIZE = -1;

    String crit_label = null;
    int critindex = -1;

    public Criterion criterion;

    String header;
    long seed = MINER_STATIC.RANDOM_SEEDS[0];

    String[] gene_labels;

    /*cutoffs*/
    double overlap_cut = 0.25;
    //double singleton_frxn_cutoff = 0.75;
    double missing_frxn_cutoff = 0.5;
    int numgene_cutoff = 1000;

    int numGeneCountMatrixCutoff = 500;

    public AssignCrit assignCrit;

    String inputtraj_path;

    RunMinerBack rmb;

    boolean[] passcrits;

    ArrayList hashge;
    ArrayList storesets;
    ArrayList storemergeStr;
    ArrayList storemergeBicluster;
    ArrayList storemergeHash;
    ValueBlockList storefinal;
    String prefix;

    //boolean addSum = false;
    boolean completeLinkage = true;

    String outpath;

    public int num_merged = 0;

    boolean live = false;

    int mingenes = 5, minexps = 5;
    HashMap nomerge_map = new HashMap();


    /**
     * @param args
     */
    public ListMergeMembers(String[] args) {

        try {

            init(args);
            initVar();

            mergeRounds();

            reconstructMerged();

            output();

            System.out.println("Done!");

        } catch (Exception
                e) {
            e.printStackTrace();
            System.exit(0);
        }
        System.exit(0);
    }

    /**
     *
     */
    private void mergeRounds() {
        boolean done = false;
        int round = 0;
        while (!done) {

            System.out.println("first merge bf " + (String) storemergeStr.get(0));
            testStoreMerge("startbfround " + round);
            //need in same order
            ArrayList sorted_list = ValueBlockListMethods.sort("area", storesets, storemergeStr, storemergeBicluster, storemergeHash, vbl);

            //storemergeStr = vbl.sortStr("area", storemergeStr);
            //vbl.sort("area");//ArrayList get = //, storesets, storemergeStr);
            storesets = (ArrayList) sorted_list.get(0);
            storemergeStr = (ArrayList) sorted_list.get(1);
            if (live) {
                storemergeBicluster = (ArrayList) sorted_list.get(2);
                storemergeHash = (ArrayList) sorted_list.get(3);
            }
            vbl = (ValueBlockList) sorted_list.get(4);

            System.out.println("first merge af " + (String) storemergeStr.get(0));
            testStoreMerge("startafround " + round);

            //int countempty = countEmpty(round);

            String s2 = prefix + "_round_" + round + "_bf.vbl";
            String out2 = vbl.toString(vbl.header != null ? vbl.header : "#" + MINER_STATIC.HEADER_VBL);//ValueBlock.toStringShortColumns());
            System.out.println("writing " + s2);
            if (debug) {
                util.TextFile.write(out2, s2);
            }

            System.out.println("round " + round + "\t" + vbl.size());// + "\t" + countempty);
            int added = 0;

            //for all blocks sorted by decreasing size
            for (int i = 0; i < vbl.size(); i++) {
                int countmerged = 0;
                //System.out.print(":");
                ValueBlock vbl_block = (ValueBlock) vbl.get(i);

                //test(vbl_block, i);

                ValueBlockList vbli = (ValueBlockList) storesets.get(i);

                String curmerge = (String) storemergeStr.get(i);
                if (debug)
                    System.out.println("mergeRounds round " + round + "\ti " + i + "\t vbl entry " + (vbl_block == null ? 0 : 1) +
                            "\tstoresets size " + (vbli != null ? vbli.size() : 0) + "\tstoremergeStr " + curmerge);

                if (vbl_block != null || vbli != null) {

                    //if this block is a result of merging then only compare to the merge list
                    if (!live && vbli.size() > 1)
                        vbl_block = null;
                    //find the maximum overlap block or block list if any
                    int j = findMaxSiminList(vbl_block, vbli, i, round);
                    if (debug)
                        System.out.println("bf while " + i + "\t" + j);
                    boolean merged = false;
                    int subround = 0;

                    //merge to current block until no more blocks overlapping above threshold
                    while (j != -1) {
                        //System.out.print("-" + j);
                        ValueBlock v2 = (ValueBlock) vbl.get(j);
                        ValueBlockList vblj = (ValueBlockList) storesets.get(j);

                        //merge with single block or set of blocks
                        if (v2 != null || vblj != null) {
                            boolean testRecon = false;
                            ValueBlockList testv = new ValueBlockList(vbli);
                            //add to list if not already present, unless live merging -- in that case do not add the temp merged bicluster
                            if (!live && vbl_block != null && !testv.contains(vbl_block)) {
                                testv.add(vbl_block);
                            }

                            if (live) {
                                //only add single block for j if j not merged yet
                                if (!testv.contains(v2) && (vblj.size() == 1)) {
                                    testv.add(v2);
                                }
                                //otherwise add all blocks in merge list for j
                                else if (vblj.size() > 1) {
                                    for (int b = 0; b < vblj.size(); b++) {
                                        ValueBlock v3 = (ValueBlock) vblj.get(b);
                                        if (v3 != null) {
                                            if (!testv.contains(v3)) {
                                                testv.add(v3);
                                            }
                                        }
                                    }
                                }
                                int[] recondata = null;
                                if (missing_frxn_cutoff < 1) {
                                    recondata = testReconstruct(testv);
                                    System.out.println("test reconstruction " +
                                            mingenes + "\t" + minexps + "\t\t" + recondata[0] + "\t" + recondata[1]);
                                    if (recondata[0] < mingenes || recondata[1] < minexps) {
                                        System.out.println("test reconstruction failed size limits " +
                                                mingenes + "\t" + minexps + "\t\t" + recondata[0] + "\t" + recondata[1]);
                                        String si = (String) storemergeStr.get(i);
                                        String sj = (String) storemergeStr.get(j);
                                        String s = si + "--" + sj;
                                        nomerge_map.put(s, 1);
                                        nomerge_map.put(sj + "--" + si, 1);
                                        System.out.println("adding small merge fail " + s);
                                        testRecon = false;
                                    } else
                                        testRecon = true;
                                } else {
                                    //recondata = testReconstructAll(i, testv, j);
                                    testRecon = true;
                                }
                            }

                            if ((live && testRecon) || !live) {

                                added++;
                                countmerged++;
                                if (debug)
                                    System.out.println("vbli bf " + vbli.size());
                                for (int z = 0; z < vbli.size(); z++) {
                                    ValueBlock cur = (ValueBlock) vbli.get(z);
                                    if (debug)
                                        System.out.println("vbli bf cur " + z + "\t" + vbli.size() + "\t" + cur.toString());
                                }

                                int addnow = 0;
                                int identical = 0;
                                //add to list if not already present, unless live merging -- in that case do not add the temp merged bicluster
                                if (!live && vbl_block != null && !vbli.contains(vbl_block)) {
                                    vbli.add(vbl_block);
                                    if (debug)
                                        System.out.println("add1 " + vbli.size() + "\t" + vbl_block.toString());
                                    addnow++;
                                } else if (vbli.contains(vbl_block)) {
                                    if (debug) {
                                        System.out.println("not add1, contains " + vbli.size() + "\t" + vbl_block.toString());
                                        for (int a = 0; a < vbli.size(); a++) {
                                            System.out.println("not add1, contains, vbli " + a + "\t" + vbli.size() + "\t" +
                                                    ((ValueBlock) vbli.get(a)).toString());
                                        }
                                    }
                                }

                                if (vbli.contains(v2)) {
                                    if (debug)
                                        System.out.println("not add2, contains " + vbli.size() + "\t" + v2.toString());
                                    for (int a = 0; a < vbli.size(); a++) {
                                        System.out.println("not add2, contains, vbli " + a + "\t" + vbli.size() + "\t" +
                                                ((ValueBlock) vbli.get(a)).toString());
                                    }
                                }

                                //only add single block for j if j not merged yet
                                if (!vbli.contains(v2) && (vblj.size() == 1)) {
                                    vbli.add(v2);
                                    ValueBlock cur = (ValueBlock) vbli.get(0);
                                    double overlapge = BlockMethods.computeBlockOverlapGeneExpFraction(cur, v2, false);
                                    if (debug)
                                        System.out.println("add2 " + vbli.size() + "\t" + v2.toString() + "\toverlapge " + overlapge);
                                    addnow++;
                                }
                                //otherwise add all blocks in merge list for j
                                else if (vblj.size() > 1) {
                                    for (int b = 0; b < vblj.size(); b++) {
                                        ValueBlock v3 = (ValueBlock) vblj.get(b);
                                        if (v3 != null) {
                                            if (!vbli.contains(v3)) {
                                                vbli.add(v3);
                                                addnow++;
                                                if (debug)
                                                    System.out.println("add3 " + vbli.size() + "\t" + v3.toString());
                                            } else {
                                                identical++;
                                                if (debug)
                                                    System.out.println("not add3, contains " + vbli.size() + "\t" + v3.toString());
                                            }
                                        } else {
                                            if (debug)
                                                System.out.println("not add3, v3 null");
                                        }
                                    }
                                } else {
                                    if (debug)
                                        System.out.println("not add2/3 " + vbli.size());
                                }

                                if (debug)
                                    System.out.println("mergeRounds added " + addnow + " / " + (vblj.size() > 1 ? vblj.size() : 1));
                                ///countempty = countEmpty(vbl);
                                if (debug)
                                    System.out.println("vbli af " + vbli.size() + "\t" + +vblj.size());// + "\t" + countempty);

                                String si = (String) storemergeStr.get(i);
                                String sj = (String) storemergeStr.get(j);

                                if (si == null) {
                                    if (debug)
                                        System.out.println("WARNING mergeRounds si null");
                                }
                                if (sj == null) {
                                    if (debug)
                                        System.out.println("WARNING mergeRounds sj null");
                                }
                                String smerge = si + sj;

                                if (debug)
                                    System.out.println("adding storemergeStr " + round + "\t" + i + "\t" + smerge);
                                storemergeStr.set(i, smerge);
                                if (debug)
                                    System.out.println("removing storemergeStr " + round + "\t" + j + "\t" + sj);
                                storemergeStr.set(j, null);

                                storesets.set(i, vbli);
                                storesets.set(j, null);

                                test(round, i, vbli, j, subround, smerge);


                                if (live) {
                                    if (missing_frxn_cutoff < 1)
                                        liveReconstruct(i, vbli, j);
                                    else {
                                        liveReconstructAll(i, vbli, j);
                                    }
                                }

                                //countEmpty(round);

                                vbl.set(j, null);

                                if (!merged)
                                    merged = true;

                                subround++;
                                //System.out.println("vbl_block size " + vbl_block.genes.length + "\t" + vbl_block.exps.length);
                            }
                        }
                        j = findMaxSiminList(vbl_block, vbli, i, round);

                        if (debug)
                            System.out.println("loop findMaxSiminList " + j + "\t" + (j == -1 ? "ENDING" : ""));
                    }//end while j
                    String s = (String) storemergeStr.get(i);
                    if (merged)
                        storemergeStr.set(i, s + "*");

                    if (vbl_block != null && vbl_block.genes.length > rmb.orig_prm.IMAX) {
                        if (debug)
                            System.out.println("WARNING More genes than allowed " + vbl_block.genes.length + " criterion based on max bounds.");
                    }
                    if (vbl_block != null && vbl_block.exps.length > rmb.orig_prm.JMAX) {
                        if (debug)
                            System.out.println("WARNING More exps than allowed " + vbl_block.exps.length + " criterion based on max bounds.");
                    }
                }

                //if merging occurred then store a naive sum of the merged blocks as a placeholder (for sorting)
                if (countmerged > 0) {
                    if (debug)
                        System.out.println("summerge countmerged " + countmerged);
                    ValueBlock summerge = new ValueBlock();
                    ValueBlockList vblnow = (ValueBlockList) storesets.get(i);
                    for (int k = 0; k < vblnow.size(); k++) {
                        ValueBlock add = (ValueBlock) vblnow.get(k);
                        if (add != null) {
                            summerge.addGenes(add.genes);
                            summerge.addExps(add.exps);
                        } else {
                            if (debug)
                                System.out.println("summerge block in list is null " + k);
                        }
                    }
                    summerge.updateArea();
                    //replace original single block with sum of constituent blocks
                    vbl.set(i, summerge);
                }
            }//end for i

            String s2a = prefix + "_round_" + round + "_af.vbl";
            String out2a = vbl.toString(vbl.header != null ? vbl.header : "#" + MINER_STATIC.HEADER_VBL);//ValueBlock.toStringShortColumns());
            System.out.println("writing " + s2a);
            if (debug) {
                util.TextFile.write(out2a, s2a);
            }
            System.out.println("added " + added);
            System.out.println("loop end round " + round + "\t" + added);
            if (added == 0) {
                System.out.println("completing loop " + added + "\t" + vbl.size());
                done = true;
            } else {
                round++;
                System.out.println("loop advancing to round " + round + "\t" + done);
            }

        }//while !done


        testStoreMerge("final");

        for (int i = 0; i < storemergeStr.size(); i++) {
            String cur = (String) storemergeStr.get(i);
            ValueBlockList v = (ValueBlockList) storesets.get(i);
            int ind = 0;
            if (cur != null) {
                ind = 1;
                System.out.println("test storesets " + i + "\t" + (v != null ? v.size() : Double.NaN) + "\t" + cur);
            }
        }
    }

    /**
     * reconstruct current merged
     *
     * @param i
     * @param vbli
     * @param j
     */
    private void liveReconstruct(int i, ValueBlockList vbli, int j) {
        HashMap curge = new HashMap();
        for (int z = 0; z < vbli.size(); z++) {
            ValueBlock vbz = (ValueBlock) vbli.get(z);
            curge = LoadHash.addPairAndIncrInt(vbz.genes, vbz.exps, curge);
        }
        boolean test = false;

        ValueBlock scored = reconstructBlock(curge, test);
       /*
       double[][] critsV = getCrit(scored);
        scored.setDataAndMean(rmb.expr_matrix.data);
        scored.updateCrit(critsV, true, passcrits, debug);
        */
        if (debug)
            System.out.println("liveReconstruct added new merged " + scored.genes.length + "\t" + scored.exps.length);
        //vbl.set(i, scored);
        storemergeBicluster.set(i, scored);
        storemergeHash.set(i, curge);
        storemergeBicluster.set(j, null);
        storemergeHash.set(j, null);
    }

    /**
     * reconstruct current merged
     *
     * @param i
     * @param vbli
     * @param j
     */
    private void liveReconstructAll(int i, ValueBlockList vbli, int j) {
        HashMap curge = new HashMap();
        for (int z = 0; z < vbli.size(); z++) {
            ValueBlock vbz = (ValueBlock) vbli.get(z);
            curge = LoadHash.addPairAndIncrInt(vbz.genes, vbz.exps, curge);
        }

        ValueBlock summerge = new ValueBlock();
        ValueBlockList vblnow = (ValueBlockList) storesets.get(i);
        for (int k = 0; k < vblnow.size(); k++) {
            ValueBlock add = (ValueBlock) vblnow.get(k);
            if (add != null) {
                summerge.addGenes(add.genes);
                summerge.addExps(add.exps);
            } else {
                if (debug)
                    System.out.println("summerge block in list is null " + k);
            }
        }
        summerge.updateArea();
        //replace original single block with sum of constituent blocks
        vbl.set(i, summerge);

        if (debug)
            System.out.println("liveReconstruct added new merged " + summerge.genes.length + "\t" + summerge.exps.length);
        //vbl.set(i, scored);
        storemergeBicluster.set(i, summerge);
        storemergeHash.set(i, curge);
        storemergeBicluster.set(j, null);
        storemergeHash.set(j, null);
    }

    /**
     *
     */
    private void testStoreMerge(String label) {
        for (int i = 0; i < storesets.size(); i++) {
            String cur = (String) storemergeStr.get(i);
            if (cur != null) {
                int occur = StringUtil.countOccur(cur, "_");
                ValueBlockList v = (ValueBlockList) storesets.get(i);
                int ind = 0;
                if (v != null) {
                    ind = 1;
                    if (debug && occur > 1)
                        System.out.println(label + " test storemergeStr " + i + "\t" + v.size() + "\t" + cur);
                }
            }
        }
    }

    /**
     *
     */
    private void reconstructMerged() {
        //reconstruct merged block from merge lists
        if (storesets != null) {
            storefinal = new ValueBlockList();
            int count = 0;
            int remove = 0;

            /*
            int empty1 = countEmpty(storesets);
            int empty2 = countEmpty(storemergeStr);
            int empty3 = 0;
            int empty4 = 0;
            if (live) {
                empty3 = countEmpty(storemergeBicluster);
                empty4 = countEmpty(storemergeHash);
                if (debug)
                    System.out.println("reconstructMerged storesets.size() " + storesets.size() + "\t" + empty1 + "\t:" +
                            storemergeStr.size() + "\t" + empty2 + "\t:" + storemergeBicluster.size() + "\t" + empty3
                            + "\t:" + storemergeHash.size() + "\t" + empty4);
            } else {
                if (debug)
                    System.out.println("reconstructMerged storesets.size() " + storesets.size() + "\t" + empty1 + "\t:" +
                            storemergeStr.size() + "\t" + empty2);
            }
            */

            ArrayList newstoremerge = new ArrayList();
            int counttotal = 0;
            for (int i = 0; i < storesets.size(); i++) {
                if (storesets.get(i) != null) {
                    counttotal++;
                }
            }
            if (debug)
                System.out.println("reconstructMerged not null " + counttotal);
            for (int i = 0; i < storesets.size(); i++) {
                if (storesets.get(i) != null) {

                    if (storemergeStr.get(i) != null) {
                        newstoremerge.add(storemergeStr.get(i));
                        if (debug)
                            System.out.println("reconstructMreged " + (String) storemergeStr.get(i));

                        /*TODD test if storesets properly populated */
                        ValueBlockList vbli = (ValueBlockList) storesets.get(i);
                        HashMap curge = new HashMap();
                        for (int j = 0; j < vbli.size(); j++) {
                            ValueBlock vbj = (ValueBlock) vbli.get(j);
                            curge = util.LoadHash.addPairAndIncrInt(vbj.genes, vbj.exps, curge);
                        }
                        count += vbli.size();
                        if (debug)
                            System.out.println("reconstructMerged block " + i + "/" + counttotal +
                                    "\tset has " + vbli.size() + "\tprocessed blocks " + count);

                        if (vbli.size() > 1) {
                            ValueBlock scored = reconstructBlock(curge, false);
                            double[][] critsV = getCrit(scored);
                            scored.setDataAndMean(rmb.expr_matrix.data);
                            scored.updateCrit(critsV, true, passcrits, debug);

                            storefinal.add(scored);
                        } else if (vbli.size() == 1)
                            storefinal.add((ValueBlock) vbli.get(0));
                        if (debug)
                            System.out.println("final mapping " + i + "\tto " + (storefinal.size() - 1));

                    } else {
                        if (debug)
                            System.out.println("reconstructMerged ERROR " + i + "\tstoremergeStr entry is null, while storesets is not");
                    }
                } else {
                    remove++;
                }
            }
            if (debug)
                System.out.println("reconstructMerged storefinal remove " + remove);
            storemergeStr = MoreArray.copyArrayList(newstoremerge);
        }
    }

    /**
     * @param ge
     * @param debug
     * @return
     */
    public ValueBlock reconstructBlock(HashMap ge, boolean debug) {
        Set keys = ge.keySet();
        Iterator itfinal = keys.iterator();

        HashMap<Integer, Integer> genes = new HashMap<Integer, Integer>();
        HashMap<Integer, Integer> exps = new HashMap<Integer, Integer>();
        //sum totals for each gene and exp
        //equal to the number of times that gene occurs in a gene_exp pair for this set of blocks (same for exp)
        while (itfinal.hasNext()) {
            IntArrayWrapper gene_exp_pair = (IntArrayWrapper) itfinal.next();

            int val = (Integer) ge.get(gene_exp_pair);

            Object tg = (Object) genes.get(gene_exp_pair.data[0]);
            Object te = (Object) exps.get(gene_exp_pair.data[1]);

            if (tg == null) {
                genes.put(gene_exp_pair.data[0], val);
            } else {
                int cur = genes.get(gene_exp_pair.data[0]);

                int sum = val + cur;
                genes.put(gene_exp_pair.data[0], sum);
                if (debug) {
                    System.out.println("reconstructBlock g * " + gene_exp_pair.data[0] + "\t" + gene_exp_pair.data[1] + "\t" + sum);
                }
            }

            if (te == null) {
                exps.put(gene_exp_pair.data[1], val);
            } else {
                int cur = exps.get(gene_exp_pair.data[1]);
                int sum = val + cur;
                exps.put(gene_exp_pair.data[1], sum);
                if (debug) {
                    System.out.println("reconstructBlock e " + gene_exp_pair.data[0] + "\t* " + gene_exp_pair.data[1] + "\t" + sum);
                }
            }
        }

        Set keysG = genes.keySet();
        int sizeG = keysG.size();
        Set keysE = exps.keySet();
        int sizeE = keysE.size();

        ArrayList all_genes = new ArrayList();
        ArrayList all_exps = new ArrayList();

        Iterator itG = keysG.iterator();
        while (itG.hasNext()) {
            int g = (Integer) itG.next();
            all_genes.add(g);

        }
        Iterator itE = keysE.iterator();
        while (itE.hasNext()) {
            int e = (Integer) itE.next();
            all_exps.add(e);
        }

        //populate global sum matrix using global indices of total genes and total exps
        int[][] count = new int[sizeG][sizeE];
        itfinal = keys.iterator();
        while (itfinal.hasNext()) {
            IntArrayWrapper s = (IntArrayWrapper) itfinal.next();
            int gind = MoreArray.getArrayInd(all_genes, s.data[0]);
            int eind = MoreArray.getArrayInd(all_exps, s.data[1]);
            int val = (Integer) ge.get(s);
            count[gind][eind] = val;

            if (debug)
                System.out.println("global count " + gind + "\t" + eind + "\t" + val);
        }

        //start with experiments to remain gene-centric (and remove fewer genes)
        //missing count E
        double[] remEcount = new double[sizeE];
        for (int i = 0; i < sizeE; i++) {
            for (int j = 0; j < sizeG; j++) {
                if (count[j][i] == 0)
                    remEcount[i]++;
            }
        }
        //find and remove E if less than half populated
        ArrayList remE = new ArrayList();
        ArrayList E = new ArrayList();
        double Ecut = (double) sizeG * missing_frxn_cutoff;
        for (int i = 0; i < sizeE; i++) {
            if (remEcount[i] > Ecut) {
                remE.add(i + 1);
                if (debug)
                    System.out.println("remove e " + "\t" + remEcount[i] + "\t" + Ecut + "\t" + missing_frxn_cutoff);
            } else {
                E.add(all_exps.get(i));
            }
        }
        //count = Matrix.removeColumns(count, remE);
        //sizeE = count[0].length;

        if (debug)
            System.out.println("remove exps " + remE.size() + "\tkeep " + E.size());

        //TODO account for missing experiments, only count gene missing over REMAINING exps
        //missing count G
        double[] remGcount = new double[sizeG];
        for (int i = 0; i < sizeG; i++) {
            for (int j = 0; j < E.size(); j++) {
                int curE = (Integer) E.get(j);
                int eind = MoreArray.getArrayInd(all_exps, curE);

                //System.out.println("reconstructBlock " + i + "\t" + j + "\tcurE " + curE + "\teind " + eind + "\tcount[0].length " + count[0].length);
                if (count[i][eind] == 0)
                    remGcount[i]++;
            }
        }
        //find and remove G if less than half populated
        ArrayList remG = new ArrayList();
        ArrayList G = new ArrayList();
        double Gcut = (double) E.size() * missing_frxn_cutoff;
        for (int i = 0; i < sizeG; i++) {
            if (remGcount[i] > Gcut) {
                remG.add(i + 1);
                if (debug)
                    System.out.println("remove g " + "\t" + remGcount[i] + "\t" + Gcut + "\t" + missing_frxn_cutoff);
            } else {
                G.add(all_genes.get(i));
            }
        }
        //count = Matrix.removeRows(count, remG);

        if (debug)
            System.out.println("remove genes " + remG.size() + "\tkeep " + G.size());

        int[] genesFinal = MoreArray.ArrayListtoInt(G);
        int[] expsFinal = MoreArray.ArrayListtoInt(E);

        Arrays.sort(genesFinal);
        Arrays.sort(expsFinal);

        if (debug)
            System.out.println("reconstructBlock final: " + genesFinal.length + "\t" + expsFinal.length + "\torig: " + all_genes.size() + "\t" + all_exps.size());
        ValueBlock ret = null;
        if (genesFinal.length != 0 && expsFinal.length != 0)
            ret = new ValueBlock(genesFinal, expsFinal);//all_genes, all_exps
        else {
            ret = new ValueBlock(all_genes, all_exps);
            System.out.println("WARNING reconstructed block is empty, using all genes and experiments");
        }
        if (debug)
            System.out.println("reconstructBlock final final: " + ret.genes.length + "\t" + ret.exps.length);

        //String s1 = prefix + "__" + index + "_reconstruct_countmatrix.txt";
        //outputCountMatrix(ge, index, keys, genesFinal, expsFinal, s1);

        return ret;
    }

    /**
     * @param block
     * @param i
     * @return
     */
    private int findMaxSiminList(ValueBlock block, ValueBlockList vbl, int i, int round) {
        int maxInd = -1;
        double max = Double.NaN;
        int count = 0;
        double maxnow = Double.NaN;

        /* for (int j = 0; j < storesets.size(); j++) {
            ValueBlockList curvbl = (ValueBlockList) storesets.get(j);
            if (curvbl != null) {
                for (int k = 0; k < curvbl.size(); k++) {
                    //System.out.print(".");
                    ValueBlock vtest = (ValueBlock) curvbl.get(k);
                    System.out.println("vtest_" + j + "_" + k);
                    System.out.println(vtest.toStringShort());
                }
            }
        }*/
        if (debug)
            System.out.println("findMaxSiminListtop");
        for (int j = 0; j < storesets.size(); j++) {
            //System.out.print(",");
            if (j != i) {
                //printTestCase(vbl, i, j, -1, Double.NaN);

                ValueBlockList curstoredset = (ValueBlockList) storesets.get(j);
                if (curstoredset != null) {
                    /* if (i == 0 && j == 146) {
                        System.out.println("findMaxSiminListtop 0_146 " + curstoredset.size() + "\t" + vbl.size() + "\t" + round);
                    }*/

                    count += curstoredset.size();
                    //int size = curstoredset.size();
                    //if (vbl.size() > 1) {
                    int size = curstoredset.size() * vbl.size();
                    //}
                    //System.out.println("\nfindMaxSiminList loop case " + i + "\t" + j + "\t" + size + "\t" + max + "\t" + maxInd);
                    int countover = 0;
                    boolean overlapMerged = false;
                    ArrayList vals = new ArrayList();
                    for (int k = 0; k < curstoredset.size(); k++) {
                        //System.out.print(".");
                        ValueBlock vtest = (ValueBlock) curstoredset.get(k);

                        //test for overlap of vref with vtest, using vref as reference
                        //if ref list contains merged biclusters
                        if (vbl.size() > 1) {
                            for (int a = 0; a < vbl.size(); a++) {
                                ValueBlock vref = (ValueBlock) vbl.get(a);

                                boolean test = false;//isTestCase(i, j);
                                double overlapge = BlockMethods.computeBlockOverlapGeneExpFraction(vref, vtest, test);

                                //System.out.println("findMaxSiminList " + i + "\t" + j + "\t" + k + "\t" + overlapge + "\t" + overlap_cut);
                                vals.add(overlapge);
                                //count cases of overlap above threshold
                                if (overlapge > overlap_cut) {
                                    countover++;
                                    String ma = (String) storemergeStr.get(j);
                                    String mb = (String) storemergeStr.get(i);
                                    //functions if satisfy overlap requirements
                                    flagLOverlap(j, k, vtest, a, vref, overlapge, "cluster", ma, mb);
                                }
                                //printTestCase(vbl, i, j, countover, overlapge);
                            }
                        } else if (vbl.size() == 1) {
                            boolean test = false;// isTestCase(i, j);
                            //test for overlap of block with vtest, using block as reference
                            double overlapge = BlockMethods.computeBlockOverlapGeneExpFraction(block, vtest, test);

                            //System.out.println("findMaxSiminList " + i + "\t" + j + "\t" + k + "\t" + overlapge + "\t" + overlap_cut);
                            vals.add(overlapge);
                            boolean go = false;
                            //count cases of overlap above threshold
                            if (overlapge > overlap_cut) {
                                countover++;
                                String ma = (String) storemergeStr.get(j);
                                String mb = (String) storemergeStr.get(i);
                                //functions if satisfy overlap requirements
                                flagLOverlap(j, k, vtest, -1, block, overlapge, "single", ma, mb);
                                go = true;
                            }
                            //printTestCase(vbl, i, j, countover, overlapge);
                        }

                        double[] d = null;
                        double mean = Double.NaN;
                        if (vals.size() > 1) {
                            d = MoreArray.ArrayListtoDouble(vals);
                            mean = stat.avg(d);
                            //System.out.println("\nfindMaxSiminList loop case " + i + "\t" + j + "\t" + size + "\t" +
                            //        max + "\t" + maxInd + "\t" + countover + "\t" + mean + "\t" + MoreArray.toString(d, ","));
                        } else if (vals.size() == 1) {
                            mean = (Double) vals.get(0);
                        }

                        //if live merging and not all pairs above overlap cutoff, test vs merged
                        if (live && countover < size) {
                            ArrayList al = overlapMerged(block, vbl, i, j, curstoredset, overlapMerged);
                            overlapMerged = ((String) al.get(0)).equals("y") ? true : false;
                            maxnow = ((Double) al.get(1));
                        }

                        if (vtest != null) {
                            if (vtest.genes.length == 0 || vtest.exps.length == 0) {
                                if (debug)
                                    System.out.println("findMaxSiminList " + i + "\t" + j + "\t" + k +
                                            "\tgenes or exps == 0 " + vtest.genes.length + "\t" + vtest.exps.length);
                            }
                            //identical
                            if (block != null && block.blockId().equals(vtest.blockId())) {
                                if (debug)
                                    System.out.println("findMaxSiminList blocks are identical " + i + "\t" + j + "\t" + k);
                            }
                        }
                    }

                    //find mean and max values
                    double[] d = null;
                    double mean = Double.NaN;

                    if (!live) {
                        if (vals.size() > 1) {
                            d = MoreArray.ArrayListtoDouble(vals);
                            mean = stat.avg(d);
                            //System.out.println("\nfindMaxSiminList loop case " + i + "\t" + j + "\t" + size + "\t" +
                            //        max + "\t" + maxInd + "\t" + countover + "\t" + mean + "\t" + MoreArray.toString(d, ","));
                        } else if (vals.size() == 1) {
                            mean = (Double) vals.get(0);
                        }


                        if (vals.size() > 1) {
                            maxnow = stat.findMax(d);
                            //System.out.println("\nfindMaxSiminList loop case " + i + "\t" + j + "\t" + size + "\t" +
                            //        max + "\t" + maxInd + "\t" + countover + "\t" + mean + "\t" + MoreArray.toString(d, ","));
                        } else if (vals.size() == 1) {
                            maxnow = (Double) vals.get(0);
                        }
                    }

                    if (Double.isNaN(maxnow)) {
                        d = MoreArray.ArrayListtoDouble(vals);
                        if (debug)
                            System.out.println("findMaxSiminList Double.isNaN(maxnow) " + MoreArray.toString(d, ","));
                    }

                    //if all overlaps above threshold and mean is greater than max or NA i.e. complete linkage
                    if ((countover == size || (countover > 0 && !completeLinkage)) &&
                            (Double.isNaN(max) || (completeLinkage && mean > max) || (!completeLinkage && maxnow > max))) {
//                        System.out.println("\nfindMaxSiminList loop newmax " + i + "\t" + j + "\t" + size + "\t" +
//                                max + "\t" + maxInd + "\t" + countover + "\t" + mean);

                        boolean nomerge_pass = true;
                        if (j != -1) {
                            String si1 = (String) storemergeStr.get(i);
                            String sj1 = (String) storemergeStr.get(j);
                            Object oforw = nomerge_map.get(si1 + "--" + sj1);
                            Object orev = nomerge_map.get(sj1 + "--" + si1);

                            if (oforw != null || orev != null)
                                nomerge_pass = false;
                        }

                        if (nomerge_pass) {
                            maxInd = j;
                            max = mean;
                        }
                    } else if (live && overlapMerged && (maxnow > max || Double.isNaN(max))) {
//                        System.out.println("\nfindMaxSiminList loop newmax " + i + "\t" + j + "\t" + size + "\t" +
//                                max + "\t" + maxInd + "\t" + countover + "\t" + mean);
                        boolean nomerge_pass = true;
                        if (j != -1) {
                            String si1 = (String) storemergeStr.get(i);
                            String sj1 = (String) storemergeStr.get(j);
                            Object oforw = nomerge_map.get(si1 + "--" + sj1);
                            Object orev = nomerge_map.get(sj1 + "--" + si1);

                            if (oforw != null || orev != null)
                                nomerge_pass = false;
                        }

                        if (nomerge_pass) {
                            maxInd = j;
                            max = maxnow;
                        }
                    }
                }
            }
        }
        System.out.println("findMaxSiminList done " + count + "\t" + maxInd + "\t" + max + "\t" + i + "\t" + round);
        return maxInd;
    }

    /**
     * @param block
     * @param vbl
     * @param i
     * @param j
     * @param curstoredset
     * @param overlapMerged
     * @return
     */
    private ArrayList overlapMerged(ValueBlock block, ValueBlockList vbl, int i, int j, ValueBlockList curstoredset,
                                    boolean overlapMerged) {

        double max = 0;
        //if not a complete linkage match, then check if overlap with reconstructed block
        if (live && vbl.size() == 1 && curstoredset.size() > 1) {

            ValueBlock merged = null;
            try {
                merged = (ValueBlock) storemergeBicluster.get(j);
                //System.out.println("test with merged bicluster " + merged.toString());
                if (merged != null) {
                    double overlapgeMerged = BlockMethods.computeBlockOverlapGeneExpFraction(block, merged, false);
                    //System.out.println("test with merged bicluster overlapgeMerged " + overlapgeMerged);
                    if (overlapgeMerged > overlap_cut) {
                        max = overlapgeMerged;
                        if (debug)
                            System.out.println("single with merged " + merged.toString());
                        overlapMerged = true;
                    }
                }
            } catch (Exception e) {
                //e.printStackTrace();
                //System.out.println("Exception (ValueBlock) storemergeBicluster.get(j); :" + storemergeBicluster.get(j) + ":");
            }
        } else if (live && vbl.size() > 1 && curstoredset.size() == 1) {
            ValueBlock merged = null;
            try {
                merged = (ValueBlock) storemergeBicluster.get(i);
                ValueBlock single = (ValueBlock) curstoredset.get(0);
                //System.out.println("test with merged bicluster " + merged.toString());
                if (merged != null) {
                    double overlapgeMerged = BlockMethods.computeBlockOverlapGeneExpFraction(merged, single, false);
                    //System.out.println("test with merged bicluster overlapgeMerged " + overlapgeMerged);
                    if (overlapgeMerged > overlap_cut) {
                        max = overlapgeMerged;
                        if (debug)
                            System.out.println("merged with single " + i + "\t" + j);
                        overlapMerged = true;
                    }
                }
            } catch (Exception e) {
                //e.printStackTrace();
                //System.out.println("Exception (ValueBlock) storemergeBicluster.get(j); :" + storemergeBicluster.get(j) + ":");
            }
        } else if (live && vbl.size() > 1 && curstoredset.size() > 1) {
            ValueBlock merged1 = null;
            ValueBlock merged2 = null;
            try {
                merged1 = (ValueBlock) storemergeBicluster.get(i);
                merged2 = (ValueBlock) storemergeBicluster.get(j);
                //System.out.println("test with merged bicluster " + merged.toString());
                if (merged1 != null && merged2 != null) {
                    double overlapgeMerged = BlockMethods.computeBlockOverlapGeneExpFraction(merged1, merged2, false);
                    //System.out.println("test with merged bicluster overlapgeMerged " + overlapgeMerged);
                    if (overlapgeMerged > overlap_cut) {
                        max = overlapgeMerged;
                        if (debug)
                            System.out.println("merged with merged " + i + "\t" + j);
                        overlapMerged = true;
                    }
                }
            } catch (Exception e) {
                //e.printStackTrace();
                //System.out.println("Exception (ValueBlock) storemergeBicluster.get(j); :" + storemergeBicluster.get(j) + ":");
            }
        }

        ArrayList ret = new ArrayList();
        ret.add(overlapMerged == true ? "y" : "n");
        ret.add(max);
        return ret;
    }

    /**
     * @param k
     * @param vtest
     * @param a
     * @param vref
     */
    private void flagLOverlap(double j, double k, ValueBlock vtest, double a, ValueBlock vref, double o, String label, String ma, String mb) {
        double tg = vtest.genes.length;
        double fg = vref.genes.length;
        double te = vtest.exps.length;
        double fe = vref.exps.length;
        double d1 = tg / fg;
        double d2 = fe / te;
        double d3 = fg / tg;
        double d4 = te / fe;
        if (d1 > 1.25 && d2 > 1.25) {
            if (debug) {
                System.out.println("L_overlap_case\t" + label + "\ttest\tstoreind\t" + j + "\tind1\t" + k + "\tind2\t" +
                        (a == -1 ? a : a) + "\tgratio\t" + d1 + "\teratio\t" + d2 + "\toverlap\t" + o +
                        "\tid " + d1 + "_" + d2 + "_" + o +
                        "\ttg " + vtest.genes.length + "\tfg " + vref.genes.length + "\tte " + vtest.exps.length +
                        "\tfe " + vref.exps.length + "\tsourceids " + ma + "___" + mb);
                System.out.println("vtest");
                System.out.println(vtest.toStringShort());
                System.out.println("vref");
                System.out.println(vref.toStringShort());
            }
        } else if (
                (d3 > 1.25 && d4 > 1.25)) {
            if (debug) {
                System.out.println("L_overlap_case\t" + label + "\tref\tstoreind\t" + j + "\tind1\t" + k + "\tind2\t" +
                        (a == -1 ? a : a) + "\tgratio\t" + d3 + "\teratio\t" + d4 + "\toverlap\t" + o +
                        "\tid " + d1 + "_" + d2 + "_" + o +
                        "\ttg " + vtest.genes.length + "\tfg " + vref.genes.length + "\tte " + vtest.exps.length +
                        "\tfe " + vref.exps.length + "\tsourceids " + ma + "___" + mb);
                System.out.println("vtest");
                System.out.println(vtest.toStringShort());
                System.out.println("vref");
                System.out.println(vref.toStringShort());
            }
        }
    }

    /**
     * @param merged
     * @return
     */
    private double[][] getCrit(ValueBlock merged) {
        double[][] crits = null;
        try {
            if (criterion == null)
                System.out.println("criterion is null");
            if (rmb.irv == null)
                System.out.println("rmb.irv is null");
            if (rmb.irv.onv == null)
                System.out.println("rmb.irv.onv is null");

            if (debug) {
                System.out.println("genes " + MoreArray.toString(merged.genes, ","));
                System.out.println("exps " + MoreArray.toString(merged.exps, ","));
            }

            crits = ComputeCrit.compute(rmb.irv, merged.genes, merged.exps,
                    rmb.orig_prm, criterion,
                    gene_labels, rmb.feat_matrix != null ? rmb.feat_matrix.data : null, debug);
        } catch (Exception e) {
            System.out.println("criterion compute failed");
            e.printStackTrace();
        }
        if (crits == null) {
            System.out.println("error");
            System.exit(1);
        }

        double[] critvals = crits[0];
        double fullcrit = ValueBlock.computeFullCrit(critvals, true, passcrits, debug);

        if (debug) {
            System.out.println("fullcrit " + fullcrit);
            MoreArray.printArray(critvals);
        }
        return crits;
    }

    /**
     * @param round
     * @return
     */
    private int countEmpty(int round) {
        int countempty = countEmpty(vbl);
        int countemptyst = countEmpty(storesets);
        int countemptysm = countEmpty(storemergeStr);
        int countemptysb = 0;
        int countemptysh = 0;
        if (live) {
            countemptysb = countEmpty(storemergeBicluster);
            countemptysh = countEmpty(storemergeHash);

            System.out.println("countEmpty round top " + round + "\tvbl " + vbl.size() + "\tnull " + countempty +
                    "\tstoresets " + storesets.size() + "\tnull " + countemptyst +
                    "\tstoremergeStr " + storemergeStr.size() + "\tnull " + countemptysm +
                    "\tstoremergeBicluster " + storemergeBicluster.size() + "\tnull " + countemptysb +
                    "\tstoremergeHash " + storemergeHash.size() + "\tnull " + countemptysh);
        } else
            System.out.println("countEmpty round top " + round + "\tvbl " + vbl.size() + "\tnull " + countempty +
                    "\tstoresets " + storesets.size() + "\tnull " + countemptyst +
                    "\tstoremergeStr " + storemergeStr.size() + "\tnull " + countemptysm);
        return countempty;
    }

    /**
     * @param vbl
     * @return
     */
    public final static int countEmpty(ArrayList vbl) {
        return countEmpty(vbl, true);
    }

    /**
     * @param vbl
     * @return
     */
    public final static int countEmpty(ArrayList vbl, boolean print) {
        int r = 0;
        for (int i = 0; i < vbl.size(); i++) {
            if (vbl.get(i) == null) {
                r++;
                if (print)
                    System.out.println("countEmpty " + i + "\t" + r);
            }
        }
        return r;
    }

    /**
     * test reconstruct
     *
     * @param vbli
     * @return
     */
    private int[] testReconstruct(ValueBlockList vbli) {
        HashMap curge = new HashMap();
        for (int z = 0; z < vbli.size(); z++) {
            ValueBlock vbz = (ValueBlock) vbli.get(z);
            curge = LoadHash.addPairAndIncrInt(vbz.genes, vbz.exps, curge);
        }
        boolean test = false;

        ValueBlock scored = reconstructBlock(curge, test);
        int[] ret = {scored.genes.length, scored.exps.length};
        //if (scored.genes.length > 5 && scored.exps.length > 5) {
        return ret;
    }

    /**
     * @param round
     * @param i
     * @param vbli
     * @param j
     * @param subround
     * @param smerge
     */
    private void test(int round, int i, ValueBlockList vbli, int j, int subround, String smerge) {
        if (debug)
            testStoreMerge("round" + round + "_i" + i + "_j" + j);

        String tmpsmerge = smerge;
        tmpsmerge = StringUtil.replace(tmpsmerge, "*", "");
        String[] tmpsmergear = tmpsmerge.split("_");
        if (vbli.size() != tmpsmergear.length) {
            System.out.println("WARNING store merge labels " + tmpsmergear.length
                    + " do not match merged blocks size " + vbli.size() + "\tround " + round + "\tsubround " + subround);
            MoreArray.printArray(tmpsmergear);
        }
    }

    /**
     */
    private void output() {

        System.out.println("WARNING: output of block gene-exp scores is DISABLED");

        genesHash = new HashMap();
        expsHash = new HashMap();
        for (int i = 0; i < storefinal.size(); i++) {
            ValueBlock v = (ValueBlock) storefinal.get(i);
            double[][] critsV = getCrit(v);
            v.setDataAndMean(rmb.expr_matrix.data);
            v.updateCrit(critsV, true, passcrits, debug);

           /* if (v.exp_mean == 0) {
                v.setDataAndMean(rmb.expr_matrix.data);
                double[][] critsV = getCrit(v);
                v.updateCrit(critsV, true, passcrits, debug);
            }*/

            if (numgene_cutoff != -1 && v.genes.length < numgene_cutoff) {
                if (v.all_criteria == null) {
                    System.out.println("output storefinal criteria missing for block " + i);
                }
                genesHash = util.LoadHash.addAndIncr(v.genes, genesHash);
                expsHash = util.LoadHash.addAndIncr(v.exps, expsHash);
            } else {
                System.out.println("output excluding block with " + v.genes.length + " genes");
            }
        }
        Set geneset = genesHash.keySet();
        Set expset = expsHash.keySet();
        System.out.println("total final merged & reconstructed " + storefinal.size());
        System.out.println("final: covered " + geneset.size() + " genes, out of " + GENE_SIZE);
        System.out.println("final: covered " + expset.size() + " exps, out of " + EXP_SIZE);

        if (storefinal != null) {
            String s1 = null;
            if (outpath == null) {
                s1 = prefix + "_reconstructed.txt";
            } else
                s1 = outpath;
            String out1 = storefinal.toString(storefinal.header != null ? storefinal.header : "#" + MINER_STATIC.HEADER_VBL);//ValueBlock.toStringShortColumns());
            System.out.println("writing " + s1);
            util.TextFile.write(out1, s1);
            String nc = "";
            if (numgene_cutoff != -1)
                nc = "_" + numgene_cutoff;
            String s2 = prefix + "_reconstructed.txt";
            System.out.println("storefinal bf gene num cut " + storefinal.size());
            if (numgene_cutoff != -1) {
                storefinal = ValueBlockListMethods.applyThresholdGenes(storefinal, numgene_cutoff);
                System.out.println("storefinal af gene num cut " + storefinal.size());
            }
            String out2 = storefinal.toString(storefinal.header != null ? storefinal.header : "#" + MINER_STATIC.HEADER_VBL);//ValueBlock.toStringShortColumns());
            System.out.println("writing " + s2);
            if (debug) {
                util.TextFile.write(out2, s2);
            }
        }

        for (int i = 0; i < vbl.size(); i++) {
            ArrayList genesar = new ArrayList();
            ArrayList expsar = new ArrayList();

            HashMap curmap = (HashMap) hashge.get(i);
            Set keys = curmap.keySet();

            Iterator itfinal = keys.iterator();
            while (itfinal.hasNext()) {
                IntArrayWrapper in = (IntArrayWrapper) itfinal.next();
                genesar.add(in.data[0]);
                expsar.add(in.data[1]);
            }

            int[] genes = MoreArray.ArrayListtoInt(genesar);
            int[] exps = MoreArray.ArrayListtoInt(expsar);

            Arrays.sort(genes);
            Arrays.sort(exps);
            String s1 = prefix + "__" + i + "_countmatrix.txt";

            if (genes.length < 500)
                try {
                    int[][] count = new int[genes.length][exps.length];
                    itfinal = keys.iterator();
                    while (itfinal.hasNext()) {
                        IntArrayWrapper in = (IntArrayWrapper) itfinal.next();
                        int iind = MoreArray.getArrayInd(genes, in.data[0]);
                        int jind = MoreArray.getArrayInd(exps, in.data[1]);
                        count[iind][jind] = (Integer) curmap.get(in);
                    }

                    int max = Matrix.findMax(count);
                    if (max > 1) {

                        System.out.println("writing " + s1);
                        TabFile.write(count, s1, MoreArray.toStringArray(genes), MoreArray.toStringArray(exps), "\t", "#", "");
                    }
                } catch (Exception e) {

                    System.out.println("failed count matrix output " + i + "\t" + s1);
                    e.printStackTrace();
                }
        }

        String m2 = prefix + "_mergelist.txt";
        String[] outdata = MoreArray.ArrayListtoString(storemergeStr);
        System.out.println("writing " + m2);
        if (debug) {
            util.TextFile.write(outdata, m2);
        }
        num_merged = vbl_copy.size() - storefinal.size();
        System.out.println("num_merged " + num_merged);
    }

    /**
     * output countmatrix if < numGeneCountMatrixCutoff genes, ie excluding large biclusters
     *
     * @param ge
     * @param index
     * @param keys
     * @param genesFinal
     * @param expsFinal
     * @param s1
     */
    private void outputCountMatrix(HashMap ge, int index, Set keys, int[] genesFinal, int[] expsFinal, String s1) {
        Iterator itfinal;
        if (genesFinal.length > 0 && expsFinal.length > 0 && genesFinal.length < numGeneCountMatrixCutoff) {
            try {
                int[][] finalcount = new int[genesFinal.length][expsFinal.length];
                itfinal = keys.iterator();

                System.out.println("index " + index + " keys.size() " + keys.size() + "\t" +
                        finalcount.length + "\t" + finalcount[0].length);
                while (itfinal.hasNext()) {
                    IntArrayWrapper in = (IntArrayWrapper) itfinal.next();
                    int iind = MoreArray.getArrayInd(genesFinal, in.data[0]);
                    int jind = MoreArray.getArrayInd(expsFinal, in.data[1]);
                    //only if gene and exp not removed
                    if (iind != -1 && jind != -1)
                        finalcount[iind][jind] = (Integer) ge.get(in);
                }

                int max = Matrix.findMax(finalcount);
                if (max > 1) {

                    //System.out.println("writing " + s1);
                    String[] gAr = MoreArray.toStringArray(genesFinal);
                    String[] eAr = MoreArray.toStringArray(expsFinal);
                       /*System.out.println("writing " + genesFinal.length + "\t" + expsFinal.length
                               + "\t" + finalcount.length + "\t" + finalcount[0].length + "\t" +
                               gAr.length + "\t" + eAr.length);*/
                    TabFile.write(finalcount, s1, eAr, gAr, "\t", "#", "");
                }
            } catch (Exception e) {

                System.out.println("failed count matrix output " + index + "\t" + s1);
                e.printStackTrace();
            }
        } else {
            System.out.println("index " + index + "\tgenes " + genesFinal.length + "\texps " + expsFinal.length);
        }
    }

    /**
     * @param vbl_block
     * @param i
     */
    private void test(ValueBlock vbl_block, int i) {
        if (vbl_block != null && vbl_block.blockId().equals("61,62,63,66,79,123,135,138,147,153,180,199,200,207,217,219,220,221,222,247,260,269,277,293,306,327,339,347,360,390,398,402,412,451,520,522,557,562,563,564,565,591,698,711,738,800,801,816,817,871,916,934,939,981,982,985,1009,1021,1048,1058,1080,1106,1132,1155,1162,1282,1342,1361,1369,1402,1421,1431,1434,1455,1513,1526,1529,1555,1580,1585,1600,1601,1606,1607,1609,1647,1656,1657,1668,1686,1691,1715,1730,1744,1802,1803,1818,1820,1824,1868,1869,1870,1871,1872,1904,1906,1926,1961,1967,2006,2053,2065,2077,2117,2121,2191,2201,2212,2216,2220,2227,2236,2278,2281,2311,2335,2341,2376,2380,2407,2441,2485,2487,2511,2523,2534,2536,2539,2540,2550,2592,2605,2609,2646,2683,2686,2746,2756,2761,2762,2782,2816,2862,2866,2872,2889,2898,2901,2913,2937,2941,2942,2956,2957,2981,2984,3043,3088,3089,3141,3147,3150,3152,3163,3175,3176,3199,3201,3204,3206,3208,3209,3217,3273,3286,3304,3329,3331,3353,3366,3367,3377,3406,3427,3456,3462,3480,3512,3513,3517,3519,3521,3522,3551,3594,3595,3607,3653,3663,3690,3694,3701,3702,3721,3722,3727,3728,3743,3746,3747,3766,3768,3769,3798,3806,3807,3817,3830,3831,3844,3918,3947,3954,3966,4018,4020,4021,4027,4039,4073,4158,4165,4202,4218,4231,4232,4233,4234,4236,4237,4241,4262,4264,4293,4300,4310,4337,4357,4365,4370,4404,4475,4480,4486,4512,4513,4564,4612,4618,4619,4624,4658,4694,4747,4762,4764,4791,4810,4857,4873,4904,4969,4996,5105,5106,5110,5159,5163,5232,5239,5268,5269,5273,5312,5330,5343,5439,5456,5498,5512,5546,5571,5590,5647,5652,5672,5676,5709,5722,5750,5751,5753,5759,5762,5814,5815,5858,5870,5875,5902,5949,5954,5982,5997,5999,6058,6088,6147,6158,6159,6160/35,36,37,38,39,48,51,52,53,54,55,56,59,60,61,62,63,64,65,74,75,77,78,79,81,82,85,88,89,91,96,97,98,103,104,105,106,107,108,109,110,111,112,113,115,118,126,127,129,130,132,133,134,135,136,137,138,139,141,143,149,150,153,154,155,156,157,158,159,160,177,178,179,181,182,183,196,206,212,213,214,216,217,218,231,233,237,238,240,241,242,244,245,246,247,249,250,251,252,253,254,256,257,259,277,279,280,281,282,283,284,286,288,299,300,301,303,305,306,307,310,312,319,320,321,322,325,326,345,346,347,348,349,350,351,353,402")) {
            //"135,147,180,200,269,306,327,339,402,412,816,817,871,1009,1021,1048,1369,1402,1431,1434,1455,1585,1668,1686,1715,1730,1744,1904,1961,2006,2053,2065,2077,2220,2227,2278,2311,2341,2380,2407,2523,2550,2605,2609,2683,2686,2746,2782,2816,2898,2913,3163,3175,3176,3273,3331,3353,3377,3427,3551,3653,3690,3746,3747,3798,3830,3831,3844,3954,3966,4018,4158,4202,4218,4262,4264,4293,4300,4310,4486,4512,4513,4564,4612,4658,4762,4764,4791,4857,4873,4904,4996,5232,5239,5512,5546,5571,5590,5647,5709,5722,5751,5753,5762,5814,5870,5997,5999,6058,6088/35,36,37,38,39,40,41,42,48,49,50,51,52,53,54,55,56,58,59,60,61,62,63,64,65,66,71,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,96,97,98,99,100,101,102,103,104,105,106,107,108,109,110,111,112,113,114,115,117,118,119,126,127,129,130,132,133,134,135,136,137,138,139,140,141,142,143,144,145,146,149,150,151,152,153,154,155,156,157,158,159,160,177,178,179,180,181,182,183,184,185,196,206,207,211,212,213,214,215,216,217,218,230,231,233,234,236,237,238,239,240,241,242,243,244,245,246,247,248,249,250,251,252,253,254,255,256,257,258,259,276,277,278,279,280,281,282,283,284,285,286,288,299,300,301,302,303,304,305,306,307,308,309,310,311,312,319,320,321,322,323,324,325,326,344,345,346,347,348,349,350,351,352,353,354,402,403")) {
            ValueBlock test2 = new ValueBlock("62,63,1797,1798,2756,2984,3199,3201/36,37,38,39,40,41,48,50,51,52,53,54,55,56,57,58,59,60,61,62,76,77,78,79,81,82,85,86,97,98,102,103,104,105,106,107,108,109,110,112,114,118,119,125,126,127,129,130,131,132,134,135,139,140,143,144,147,148,149,151,154,155,156,157,158,159,160,179,182,184,185,196,206,211,212,214,215,218,230,231,232,233,235,237,238,239,240,241,242,243,244,245,248,249,250,251,253,254,255,256,257,258,276,279,280,281,282,283,286,287,288,299,300,301,302,303,304,305,306,307,308,309,311,312,321,322,325,345,346,347,348,349,354");
            //"1686,1904,1960,1961,2407,2782,3176,4147,4300,4310,5150,5151,5590/35,36,37,38,39,40,41,42,48,49,50,51,52,53,54,55,59,60,61,62,63,65,66,71,72,73,74,75,76,77,78,79,81,82,84,85,86,88,89,90,91,96,97,98,99,100,101,102,105,106,107,108,109,110,111,112,113,114,115,117,118,119,129,133,134,135,136,137,138,141,142,143,144,145,146,147,149,150,151,152,154,155,156,157,158,159,160,177,178,180,182,183,184,185,206,207,211,212,213,214,215,218,231,233,236,239,243,244,245,247,248,251,252,253,254,255,256,257,258,259,276,277,278,279,280,281,282,283,285,286,287,299,300,301,302,303,304,305,306,307,309,310,311,312,319,344,345,346,347,348,349,350,351,352,353,354,402");
            for (int z = 0; z < vbl.size(); z++) {
                if (z != i) {
                    try {
                        ValueBlock va = (ValueBlock) vbl.get(z);
                        if (va.equals(test2)) {
                            System.out.println("detected index " + z);
                        }
                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
                }
            }

            double overlapge = BlockMethods.computeBlockOverlapGeneExpFraction(vbl_block, test2, false);
            System.out.println("mergeRounds single detected test case of missing merge " + overlapge);
        } else if (storemergeBicluster.get(i) != null && !storemergeBicluster.get(i).equals("")) {
            try {
                ValueBlock valueBlock = (ValueBlock) storemergeBicluster.get(i);
                if (valueBlock.blockId().equals("61,62,63,66,79,123,135,138,147,153,180,199,200,207,217,219,220,221,222,247,260,269,277,293,306,327,339,347,360,390,398,402,412,451,520,522,557,562,563,564,565,591,698,711,738,800,801,816,817,871,916,934,939,981,982,985,1009,1021,1048,1058,1080,1106,1132,1155,1162,1282,1342,1361,1369,1402,1421,1431,1434,1455,1513,1526,1529,1555,1580,1585,1600,1601,1606,1607,1609,1647,1656,1657,1668,1686,1691,1715,1730,1744,1802,1803,1818,1820,1824,1868,1869,1870,1871,1872,1904,1906,1926,1961,1967,2006,2053,2065,2077,2117,2121,2191,2201,2212,2216,2220,2227,2236,2278,2281,2311,2335,2341,2376,2380,2407,2441,2485,2487,2511,2523,2534,2536,2539,2540,2550,2592,2605,2609,2646,2683,2686,2746,2756,2761,2762,2782,2816,2862,2866,2872,2889,2898,2901,2913,2937,2941,2942,2956,2957,2981,2984,3043,3088,3089,3141,3147,3150,3152,3163,3175,3176,3199,3201,3204,3206,3208,3209,3217,3273,3286,3304,3329,3331,3353,3366,3367,3377,3406,3427,3456,3462,3480,3512,3513,3517,3519,3521,3522,3551,3594,3595,3607,3653,3663,3690,3694,3701,3702,3721,3722,3727,3728,3743,3746,3747,3766,3768,3769,3798,3806,3807,3817,3830,3831,3844,3918,3947,3954,3966,4018,4020,4021,4027,4039,4073,4158,4165,4202,4218,4231,4232,4233,4234,4236,4237,4241,4262,4264,4293,4300,4310,4337,4357,4365,4370,4404,4475,4480,4486,4512,4513,4564,4612,4618,4619,4624,4658,4694,4747,4762,4764,4791,4810,4857,4873,4904,4969,4996,5105,5106,5110,5159,5163,5232,5239,5268,5269,5273,5312,5330,5343,5439,5456,5498,5512,5546,5571,5590,5647,5652,5672,5676,5709,5722,5750,5751,5753,5759,5762,5814,5815,5858,5870,5875,5902,5949,5954,5982,5997,5999,6058,6088,6147,6158,6159,6160/35,36,37,38,39,48,51,52,53,54,55,56,59,60,61,62,63,64,65,74,75,77,78,79,81,82,85,88,89,91,96,97,98,103,104,105,106,107,108,109,110,111,112,113,115,118,126,127,129,130,132,133,134,135,136,137,138,139,141,143,149,150,153,154,155,156,157,158,159,160,177,178,179,181,182,183,196,206,212,213,214,216,217,218,231,233,237,238,240,241,242,244,245,246,247,249,250,251,252,253,254,256,257,259,277,279,280,281,282,283,284,286,288,299,300,301,303,305,306,307,310,312,319,320,321,322,325,326,345,346,347,348,349,350,351,353,402")) {
                    //"135,147,180,200,269,306,327,339,402,412,816,817,871,1009,1021,1048,1369,1402,1431,1434,1455,1585,1668,1686,1715,1730,1744,1904,1961,2006,2053,2065,2077,2220,2227,2278,2311,2341,2380,2407,2523,2550,2605,2609,2683,2686,2746,2782,2816,2898,2913,3163,3175,3176,3273,3331,3353,3377,3427,3551,3653,3690,3746,3747,3798,3830,3831,3844,3954,3966,4018,4158,4202,4218,4262,4264,4293,4300,4310,4486,4512,4513,4564,4612,4658,4762,4764,4791,4857,4873,4904,4996,5232,5239,5512,5546,5571,5590,5647,5709,5722,5751,5753,5762,5814,5870,5997,5999,6058,6088/35,36,37,38,39,40,41,42,48,49,50,51,52,53,54,55,56,58,59,60,61,62,63,64,65,66,71,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,96,97,98,99,100,101,102,103,104,105,106,107,108,109,110,111,112,113,114,115,117,118,119,126,127,129,130,132,133,134,135,136,137,138,139,140,141,142,143,144,145,146,149,150,151,152,153,154,155,156,157,158,159,160,177,178,179,180,181,182,183,184,185,196,206,207,211,212,213,214,215,216,217,218,230,231,233,234,236,237,238,239,240,241,242,243,244,245,246,247,248,249,250,251,252,253,254,255,256,257,258,259,276,277,278,279,280,281,282,283,284,285,286,288,299,300,301,302,303,304,305,306,307,308,309,310,311,312,319,320,321,322,323,324,325,326,344,345,346,347,348,349,350,351,352,353,354,402,403")) {
                    ValueBlock test2 = new ValueBlock("62,63,1797,1798,2756,2984,3199,3201/36,37,38,39,40,41,48,50,51,52,53,54,55,56,57,58,59,60,61,62,76,77,78,79,81,82,85,86,97,98,102,103,104,105,106,107,108,109,110,112,114,118,119,125,126,127,129,130,131,132,134,135,139,140,143,144,147,148,149,151,154,155,156,157,158,159,160,179,182,184,185,196,206,211,212,214,215,218,230,231,232,233,235,237,238,239,240,241,242,243,244,245,248,249,250,251,253,254,255,256,257,258,276,279,280,281,282,283,286,287,288,299,300,301,302,303,304,305,306,307,308,309,311,312,321,322,325,345,346,347,348,349,354");
                    //"1686,1904,1960,1961,2407,2782,3176,4147,4300,4310,5150,5151,5590/35,36,37,38,39,40,41,42,48,49,50,51,52,53,54,55,59,60,61,62,63,65,66,71,72,73,74,75,76,77,78,79,81,82,84,85,86,88,89,90,91,96,97,98,99,100,101,102,105,106,107,108,109,110,111,112,113,114,115,117,118,119,129,133,134,135,136,137,138,141,142,143,144,145,146,147,149,150,151,152,154,155,156,157,158,159,160,177,178,180,182,183,184,185,206,207,211,212,213,214,215,218,231,233,236,239,243,244,245,247,248,251,252,253,254,255,256,257,258,259,276,277,278,279,280,281,282,283,285,286,287,299,300,301,302,303,304,305,306,307,309,310,311,312,319,344,345,346,347,348,349,350,351,352,353,354,402");
                    for (int z = 0; z < vbl.size(); z++) {
                        if (z != i) {
                            try {
                                ValueBlock va = (ValueBlock) vbl.get(z);
                                if (va.equals(test2)) {
                                    System.out.println("detected index " + z);
                                }
                            } catch (Exception e) {
                                //e.printStackTrace();
                            }
                        }
                    }

                    double overlapge = BlockMethods.computeBlockOverlapGeneExpFraction(valueBlock, test2, false);
                    System.out.println("mergeRounds single detected test case of missing merge " + overlapge);
                }
            } catch (Exception e) {
            }
        }
    }

    /**
     * @param args
     */
    private void init(String[] args) {

        MoreArray.printArray(args);
        options = MapArgOptions.maptoMap(args, valid_args);

        if (options.get("-dir") != null) {
            inputtraj_path = (String) options.get("-dir");
        }
        if (options.get("-crit") != null) {
            crit_label = (String) options.get("-crit");
        }
        if (options.get("-param") != null) {
            rmb = new RunMinerBack((String) options.get("-param"), false);
        }
        if (options.get("-numgene") != null) {
            numgene_cutoff = Integer.parseInt((String) options.get("-numgene"));
        }
        if (options.get("-ocut") != null) {
            overlap_cut = Double.parseDouble((String) options.get("-ocut"));
        }
        if (options.get("-misscut") != null) {
            missing_frxn_cutoff = Double.parseDouble((String) options.get("-misscut"));
            System.out.println("missing_frxn_cutoff " + missing_frxn_cutoff);
        }
        if (options.get("-complete") != null) {
            completeLinkage = ((String) options.get("-complete")).equalsIgnoreCase("y") ? true : false;
        }
       /* if (options.get("-sum") != null) {
            addSum = ((String) options.get("-sum")).equalsIgnoreCase("y") ? true : false;
        }*/
        if (options.get("-debug") != null) {
            debug = ((String) options.get("-debug")).equalsIgnoreCase("y") ? true : false;
        }
        if (options.get("-out") != null) {
            outpath = ((String) options.get("-out"));
        }
        if (options.get("-live") != null) {
            live = ((String) options.get("-live")).equalsIgnoreCase("y") ? true : false;
        }

        if (crit_label != null) {
            assignCrit = new AssignCrit(crit_label);
        } else {
            crit_label = MINER_STATIC.CRIT_LABELS[rmb.orig_prm.CRIT_TYPE_INDEX - 1];
            assignCrit = new AssignCrit(crit_label);
        }
        critindex = assignCrit.CRITindex[0];


        ValueBlockList[] vblget = ValueBlockListMethods.getFirstandLast(inputtraj_path, null, crit_label, null);
        vbl = vblget[1];
        System.out.println("read trajectories " + vbl.size());
        vbl_copy = new ValueBlockList(vbl);
        //vbl_merged_all = new ValueBlockList();
        //vbl_merged_trim = new ValueBlockList();

        header = "#";

        outdir = "./";

        seed = rmb.orig_prm.RANDOM_SEED;
        //debug = StringUtil.isTrueorYes(prm.debug);

        String gread = rmb.orig_prm.EXPR_DATA_PATH.substring(0, rmb.orig_prm.EXPR_DATA_PATH.indexOf(".txt")) +
                "_geneids.txt";
        System.out.println("reading geneids " + gread);
        File testg = new File(gread);
        if (testg.exists()) {
            String[][] sarray = TabFile.readtoArray(gread);
            int col = 2;
            gene_labels = MoreArray.replaceAll(MoreArray.extractColumnStr(sarray, col), "\"", "");
            System.out.println("init gene_labels " + gread + "\t" + col + "\t" +
                    gene_labels[0] + "\t" + sarray[0].length + "\t" + sarray[1].length);
        }

        //create dirs if necessary
        File test = new File(outdir);
        if (!test.exists() && !test.isDirectory()) {
            test.mkdir();
        }

        GENE_SIZE = rmb.expr_matrix.data.length;
        EXP_SIZE = rmb.expr_matrix.data[0].length;


        ParsePath pp = new ParsePath(inputtraj_path);
        prefix = pp.getName();

        prefix = prefix + "_" + overlap_cut + "_" + missing_frxn_cutoff + "_" + (completeLinkage ? "c" : "s") + "_" + "live" + (live ? "y" : "n");
    }

    /**
     *
     */
    private void initVar() {
        genesHash = new HashMap();
        expsHash = new HashMap();
        initforCrit();
        hashge = new ArrayList();

        //pre load all blocks to hashes
        for (int i = 0; i < vbl.size(); i++) {
            ValueBlock v = (ValueBlock) vbl.get(i);
            HashMap merged_hash_genes_exps = new HashMap();
            merged_hash_genes_exps = util.LoadHash.addPairAndIncrInt(v.genes, v.exps, merged_hash_genes_exps);
            hashge.add(merged_hash_genes_exps);
        }

        //initialize and populate merge storage
        storesets = new ArrayList(vbl.size());
        storemergeStr = new ArrayList(vbl.size());
        if (live) {
            storemergeBicluster = new ArrayList(vbl.size());
            storemergeHash = new ArrayList(vbl.size());
        }
        for (int i = 0; i < vbl.size(); i++) {
            ValueBlockList curvbl = new ValueBlockList();
            ValueBlock v = (ValueBlock) vbl.get(i);
            curvbl.add(v);
            storesets.add(curvbl);
            storemergeStr.add("" + (i + 1) + "_");
            if (live) {
                storemergeBicluster.add("");
                storemergeHash.add("");
            }
        }
    }

    /**
     *
     */
    private void initforCrit() {

        critindex = assignCrit.CRITindex[0];
        System.out.println("doing " + critindex + "\t" + assignCrit.labels[0] +
                "\tisMeanCrit " + assignCrit.CRITmean[0] + "\tisTFCrit " + assignCrit.CRITTF[0]);

        criterion = new Criterion(critindex, assignCrit.CRITmean[0] == 1 ? true : false, true,
                rmb.orig_prm.USE_ABS_AR, assignCrit.CRITTF[0] == 1 ? true : false, rmb.orig_prm.needinv, true, rmb.irv.prm.FRXN_SIGN, debug);
        rmb.orig_prm.crit = criterion;
        rmb.irv.onv = new ObtainNullValues(rmb.irv.Rengine, rmb.orig_prm, debug);

        passcrits = Criterion.getExprCritTypes(criterion, true, assignCrit.CRITmean[0] == 1 ? true : false, debug);
    }

    /**
     * @param args
     */
    public final static void main(String[] args) {
        if (args.length >= 6 && args.length <= 20) {
            ListMergeMembers rm = new ListMergeMembers(args);
        } else {
            System.out.println("syntax: java DataMining.ListMergeMembers\n" +
                    "<-dir dir of block lists or file of trajectory endpoints>\n" +
                    "<-crit criterion label e.g. __GEERE__ >\n" +
                    "<-param parameter file>\n" +
                    "<-ocut OPTIONAL overlap cutoff>\n" +
                    "<-misscut OPTIONAL missing frxn cutoff>\n" +
                    "<-numgene OPTIONAL msx number of genes in final bicluster >\n" +
                    //"<-sum add summed block while merging y/n>\n" +
                    "<-out merged output file name >\n" +
                    "<-live live reconstruction during merging y/n>\n" +
                    "<-debug debug y/n >\n" +
                    "<-complete y/n complete linkage>"
            );
        }
    }
}