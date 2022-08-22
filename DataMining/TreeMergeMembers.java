package DataMining;

import dtype.Int2DArStringAr;
import dtype.IntArrayWrapper;
import util.MoreArray;
import util.ParsePath;
import util.StringUtil;
import util.TabFile;

import java.io.File;
import java.util.*;

/**
 * DEPRECATED
 * <p/>
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: Jun 2, 2011
 * Time: 8:09:51 PM
 */
public class TreeMergeMembers {

    boolean debug = true;

    int bicluster_count_min = 1;


    String outdir = "";

    ValueBlockList vbl;
    ValueBlockList vbl_merged;
    ValueBlockList vbl_copy;

    String[][] treecutmatrix;
    String[] nodelabels;
    int maxleaf = -1;

    int GENE_LIMIT = -1;
    int EXP_LIMIT = -1;
    HashMap genes_exps;
    HashMap genes;
    HashMap exps;

    int GENE_SIZE = 6160;
    int EXP_SIZE = 667;

    String crit_label = null;
    int critindex = -1;

    int cluster_count = 1;

    HashMap clustermap = new HashMap();

    boolean intersect = true;

    //String Rcodepath;
    //String inputtxt;
    //String inputR;

    //SimpleMatrix expr_data;

    public Criterion criterion;

    //Parameters prm;
    String header;
    long seed = MINER_STATIC.RANDOM_SEEDS[0];//759820;

    String[] gene_labels;
    //InitRandVar irv;

    double cutoff = 0.99;

    public AssignCrit assignCrit;
    //ObtainNullValues onv;
    boolean useMean = false;

    ArrayList removemember_indices;
    String inputtraj_path;

    RunMinerBack rmb;


    /**
     * @param args
     */
    public TreeMergeMembers(String[] args) {
        init(args);

        genes_exps = new HashMap();
        genes = new HashMap();
        exps = new HashMap();

        removemember_indices = new ArrayList();

        //for each cut level i.e. unique branch length/tree height
        for (int i = 0; i < treecutmatrix.length; i++) {
            cluster_count = 1;
            String[] clusters = treecutmatrix[i];

            System.out.println("level " + i + "\tcluster_count " + cluster_count + "\tclusters " + MoreArray.toString(clusters));
            doOneLevel(clusters);

            System.out.println("doOneLevel cumulative total " + i + "\t" + vbl_merged.size());
        }


        output(args[2]);

        System.out.println("Done!");
    }

    /**
     * @param clusters
     */
    private void doOneLevel(String[] clusters) {

        //over all leaves
        for (int i = 1; i < maxleaf + 1; i++) {
            String test = "" + i;
            //System.out.println("leaf " + i);
            //over all clusters
            for (int j = 0; j < clusters.length; j++) {
                //if (debug)
                //    System.out.println("doOneLevel clusters[j] :" + clusters[j] + ":\t:" + test + ":");
                if (clusters[j].equals(test)) {
                    System.out.println("match " + test);
                    //over all remaining clusters
                    for (int k = j + 1; k < clusters.length; k++) {
                        if (clusters[k].equals(test)) {
                            System.out.println("detected cluster " + test);
                            merge(clusters, test);
                            //to finish outer loop
                            j = clusters.length;
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Creates a key string based on the populated leaves.
     *
     * @param cluster_indices
     * @return
     */
    private String makeKey(int[] cluster_indices) {
        String ret = "";
        for (int i = 0; i < cluster_indices.length; i++) {
            if (cluster_indices[i] != -1) {
                if (ret.length() == 0)
                    ret += cluster_indices[i];
                else
                    ret += "_" + cluster_indices[i];
            }
        }
        return ret;
    }

    /**
     * Creates the union of bicluster leaves belonging to a parent node.
     *
     * @param clusters
     * @param cluster
     */
    private void merge(String[] clusters, String cluster) {
        //find all matching
        int[] cluster_indices = StringUtil.locateEquals(clusters, "" + cluster);
        String key = makeKey(cluster_indices);

        Object o = clustermap.get(key);
        if (o == null) {
            clustermap.put(key, 1);
            System.out.println("doOneLevel " + clusters[0] + "\t" + cluster_indices[0]);
            System.out.println("cluster_indices 0 " + cluster_indices.length + "\tcluster " +
                    cluster + "\t" + MoreArray.toString(cluster_indices));
            int[] rem = {-1};
            //remove all none matching
            try {
                cluster_indices = MoreArray.removeByVal(cluster_indices, rem);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("cluster_indices 1 " + cluster_indices.length + "\tcluster " +
                    cluster + "\t" + MoreArray.toString(cluster_indices));

            System.out.println("cluster " + cluster + "\t" +
                    cluster_indices.length + "\t" + MoreArray.toString(cluster_indices));
            ValueBlock vb = new ValueBlock();

            System.out.println("sizes " + vbl.size() + "\t" + cluster_indices.length);

            //for all cluster levels
            for (int i = 0; i < cluster_indices.length; i++) {
                int index = cluster_indices[i] - 1;
                if (removemember_indices.indexOf(index) == -1)
                    removemember_indices.add(index);
                System.out.println("cluster index " + i + "\t" + index + "\t" + vbl.size());

                ValueBlock cur = (ValueBlock) vbl.get(index);
                System.out.println("cluster index " + i + "\t" + cluster_indices.length);
                if (vb.genes == null) {
                    vb.genes = MoreArray.copy(cur.genes);
                    genes = util.LoadHash.addAndIncr(cur.genes, genes);
                    System.out.println("added genes 0 " + genes.keySet().size());
                } else {
                    if (debug) {
                        System.out.println(cluster + " merging g master " +
                                MoreArray.toString(vb.genes, ","));
                        System.out.println(cluster + " merging g add    " + MoreArray.toString(cur.genes, ","));
                    }

                    vb.addGenes(cur.genes);
                    genes = util.LoadHash.addAndIncr(cur.genes, genes);
                    System.out.println("added genes 1 " + genes.keySet().size());
                }
                if (vb.exps == null) {
                    vb.exps = MoreArray.copy(cur.exps);
                    exps = util.LoadHash.addAndIncr(cur.exps, exps);
                } else {
                    if (debug) {
                        System.out.println(cluster + " merging e master " +
                                MoreArray.toString(vb.exps, ","));
                        System.out.println(cluster + " merging e add    " + MoreArray.toString(cur.exps, ","));
                    }
                    vb.addExps(cur.exps);
                    exps = util.LoadHash.addAndIncr(cur.exps, exps);
                }
                genes_exps = util.LoadHash.addPairAndIncrInt(cur.genes, cur.exps, genes_exps);
                System.out.println("genes_exps size " + genes_exps.size());
            }

            if (intersect) {
                vb = createIntersect(vb);
            }

            if (useMean)
                rmb.orig_prm.USE_MEAN = true;
            else
                rmb.orig_prm.USE_MEAN = false;

            critindex = assignCrit.CRITindex[0];
            System.out.println("doing " + critindex + "\t" + assignCrit.labels[0] +
                    "\tisMeanCrit " + assignCrit.CRITmean[0] + "\tisTFCrit " + assignCrit.CRITTF[0]);

            criterion = new Criterion(critindex, useMean, true,
                    rmb.orig_prm.USE_ABS_AR, assignCrit.CRITTF[0] == 1 ? true : false, rmb.orig_prm.needinv, true, rmb.irv.prm.FRXN_SIGN, debug);
            rmb.orig_prm.crit = criterion;
            rmb.irv.onv = new ObtainNullValues(rmb.irv.Rengine, rmb.orig_prm, debug);

            if (vb.genes.length > rmb.orig_prm.IMAX) {
                System.out.println("WARNING More genes than allowed " + vb.genes.length + " criterion based on max bounds.");
            }
            if (vb.exps.length > rmb.orig_prm.JMAX) {
                System.out.println("WARNING More exps than allowed " + vb.exps.length + " criterion based on max bounds.");
            }
            boolean[] passcrits = Criterion.getExprCritTypes(criterion, true, rmb.orig_prm.USE_MEAN, debug);
            double[][] crits = null;
            if (vb.genes.length != 0 && vb.exps.length != 0) {
                try {
                    if (criterion == null)
                        System.out.println("criterion is null");
                    if (rmb.irv == null)
                        System.out.println("rmb.irv is null");
                    if (rmb.irv.onv == null)
                        System.out.println("rmb.irv.onv is null");

                    System.out.println("genes " + MoreArray.toString(vb.genes, ","));
                    System.out.println("exps " + MoreArray.toString(vb.exps, ","));

                    crits = ComputeCrit.compute(rmb.irv, vb.genes, vb.exps,
                            rmb.orig_prm, criterion,
                            gene_labels, null, debug);
                } catch (Exception e) {
                    System.out.println("criterion compute failed");
                    e.printStackTrace();
                }
                if (crits == null) {
                    System.out.println("error");
                    System.exit(1);
                }
                //double origfull = curBlock.fullcrit;
                double[] critvals = crits[0];
                double fullcrit = ValueBlock.computeFullCrit(critvals, true, passcrits, debug);

                if (debug) {
                    System.out.println("fullcrit " + fullcrit);
                    MoreArray.printArray(critvals);
                }

                ValueBlock curblock = new ValueBlock(vb.genes, vb.exps);
                curblock.setDataAndMean(rmb.expr_matrix.data);

                curblock.updateCrit(crits, true, passcrits, debug);

                System.out.println("curblock.full_criterion > cutoff " + curblock.full_criterion + " > " + cutoff + " ?");
                if (curblock.full_criterion > cutoff) {
                    vbl_merged.add(curblock);
                    System.out.println("merged " + vbl_merged.size() + "\t" + curblock.genes.length + "\t" + curblock.exps.length +
                            "\tvbl_merged_all " + vbl_merged.size());
                    cluster_indices = StringUtil.locateEquals(clusters, "" + cluster);
                    try {
                        cluster_indices = MoreArray.removeByVal(cluster_indices, rem);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    System.out.println("merged block below cutoff " + curblock.full_criterion);
                }
            }
        } else {
            System.out.println("skipping " + key);
        }
    }

    /**
     * @param vb
     */
    private ValueBlock createIntersect(ValueBlock vb) {
        boolean changed = false;

        Set keys = genes_exps.keySet();
        Iterator it = keys.iterator();

        Int2DArStringAr iasa = new Int2DArStringAr(keys.size());

        int count = 0;

        while (it.hasNext()) {

            IntArrayWrapper cur = (IntArrayWrapper) it.next();
            int test1 = cur.data[0];
            int test2 = cur.data[1];

            int v = (Integer) genes_exps.get(cur);

            iasa.data1[count] = test1;
            iasa.data2[count] = test2;
            iasa.data3[count] = v;
            count++;
        }

        //for all genes
        ArrayList gar = MoreArray.convtoArrayList(vb.genes);
        for (int i = 0; i < gar.size(); i++) {
            System.out.print(".");
            int curg = (Integer) gar.get(i);
            int gcount = 0;
            for (int j = 0; j < iasa.data1.length; j++) {
                int test = (Integer) iasa.data1[j];
                if (test == curg) {
                    int val = (Integer) iasa.data3[j];
                    //is the gene found in more than 1 bicluster?
                    if (val > bicluster_count_min) {
                        gcount++;
                    }
                }
            }
            //if gene not found in more than 1 bicluster remove it
            if (gcount == 0) {
                System.out.println("removing g " + curg);
                genes.remove(curg);
                System.out.print(1);
                genes_exps = util.LoadHash.removeHalfKey1(genes_exps, curg);
                System.out.print(2);
                iasa.removebyPairElement(curg, true);
                System.out.print(3);
                gar.remove(i);
                System.out.print(4);
                i--;
                changed = true;
            }
        }
        ArrayList ear = MoreArray.convtoArrayList(vb.exps);
        //for all genes in the bicluster
        for (int i = 0; i < ear.size(); i++) {
            System.out.print(",");
            int cure = (Integer) ear.get(i);
            int ecount = 0;
            for (int j = 0; j < iasa.data1.length; j++) {
                int test = (Integer) iasa.data2[j];//-1;
                if (test == cure) {
                    int val = (Integer) iasa.data3[j];
                    if (val > bicluster_count_min) {
                        ecount++;
                    }
                }
            }
            //if exp not found in more than 1 bicluster remove it
            if (ecount == 0) {
                System.out.println("removing e " + cure);
                exps.remove(cure);
                System.out.print(1);
                genes_exps = util.LoadHash.removeHalfKey2(genes_exps, cure);
                System.out.print(2);
                iasa.removebyPairElement(cure, false);
                System.out.print(3);
                ear.remove(i);
                System.out.print(4);
                i--;
                changed = true;
            }
        }
        System.out.print("\n");
        if (changed) {
            vb.genes = MoreArray.ArrayListtoInt(gar);
            vb.exps = MoreArray.ArrayListtoInt(ear);
            vb.update(rmb.expr_matrix.data);
        }

        return vb;
    }


    /**
     * @param arg
     */
    private void output(String arg) {

        Set geneset = genes.keySet();
        Set expset = exps.keySet();
        System.out.println("total unfiltered " + vbl_merged.size());
        System.out.println("total: covered " + geneset.size() + " genes, out of " + GENE_SIZE);
        System.out.println("total: covered " + expset.size() + " exps, out of " + EXP_SIZE);

        ValueBlockList tooLarge = new ValueBlockList();
        for (int i = 0; i < vbl_merged.size(); i++) {
            ValueBlock v = (ValueBlock) vbl_merged.get(i);
            //System.out.println("v.genes.length " + v.genes.length + "\tv.exps.length " + v.exps.length);
            if ((GENE_LIMIT != -1 && v.genes.length > GENE_LIMIT) || (EXP_LIMIT != -1 && v.exps.length > EXP_LIMIT)) {
                tooLarge.add(new ValueBlock(v));
                System.out.println("removing HUGE " + v.genes.length + "\t" + v.exps.length + "\t" + tooLarge.size());
                vbl_merged.remove(i);
                i--;
            }
        }

        System.out.println("WARNING: output of block gene-exp scores is DISABLED");
        /*for (int i = 0; i < vbl_merged_all.size(); i++) {
            ValueBlock v = (ValueBlock) vbl_merged_all.get(i);
            v.scoreGeneExpAndWrite(genes_exps, "" + i + "_geneexp_count.txt", "#" + v.blockId());//outdir +
            //v.scoreGeneExpPairsAndWrite(genes_exps_pairs, "" + i + "_geneexp_pair_count.txt", "#" + v.blockId());// outdir +
            //vbl.set(i, v);
        }*/

        genes = new HashMap();
        exps = new HashMap();
        for (int i = 0; i < tooLarge.size(); i++) {
            ValueBlock v = (ValueBlock) tooLarge.get(i);
            genes = util.LoadHash.addAndIncr(v.genes, genes);
            exps = util.LoadHash.addAndIncr(v.exps, exps);
        }
        Set lgeneset = genes.keySet();
        Set lexpset = exps.keySet();
        System.out.println("large: covered " + geneset.size() + " genes, out of " + GENE_SIZE);
        System.out.println("large: covered " + expset.size() + " exps, out of " + EXP_SIZE);

        genes = new HashMap();
        exps = new HashMap();
        for (int i = 0; i < vbl_merged.size(); i++) {
            ValueBlock v = (ValueBlock) vbl_merged.get(i);
            genes = util.LoadHash.addAndIncr(v.genes, genes);
            exps = util.LoadHash.addAndIncr(v.exps, exps);
        }
        geneset = genes.keySet();
        expset = exps.keySet();
        System.out.println("final: covered " + geneset.size() + " genes, out of " + GENE_SIZE);
        System.out.println("final: covered " + expset.size() + " exps, out of " + EXP_SIZE);

        Iterator itfinal = geneset.iterator();
        Iterator itlarge = lgeneset.iterator();
        Iterator itfinalexp = expset.iterator();
        Iterator itlargeexp = lexpset.iterator();

        int gcount = 0;
        while (itfinal.hasNext()) {
            int cura = (Integer) itfinal.next();
            while (itlarge.hasNext()) {
                int curb = (Integer) itlarge.next();
                if (cura == curb) {
                    gcount++;
                    break;
                }
            }
        }

        int ecount = 0;
        while (itfinalexp.hasNext()) {
            int cura = (Integer) itfinalexp.next();
            while (itlargeexp.hasNext()) {
                int curb = (Integer) itlargeexp.next();
                if (cura == curb) {
                    gcount++;
                    break;
                }
            }
        }

        System.out.println("final vs large: common gene " + gcount + ", common exp " + ecount);

        ParsePath pp = new ParsePath(inputtraj_path);

        String s = pp.getName() + "_merged.txt";
        System.out.println("writing " + s);
        String out = vbl_merged.toString(vbl_merged.header != null ? vbl_merged.header : "#" + MINER_STATIC.HEADER_VBL);// ValueBlock.toStringShortColumns());
        util.TextFile.write(out, s);
        //vbl_merged_all.writeBIC(arg);

        int[] remove = MoreArray.ArrayListtoInt(removemember_indices);
        Arrays.sort(remove);
        int rem = 0;
        for (int i = 0; i < remove.length; i++) {
            //System.out.println("removing " + remove[i]);
            vbl_copy.remove(remove[i] - rem);
            rem++;
        }

        String s2 = pp.getName() + "_trim.txt";
        System.out.println("writing " + s2);
        String out2 = vbl_copy.toString(vbl_copy.header != null ? vbl_copy.header : "#" + MINER_STATIC.HEADER_VBL);//ValueBlock.toStringShortColumns());
        util.TextFile.write(out2, s2);

        String outf = arg + "_toolarge.bic";
        System.out.println("writing " + outf);
        ValueBlockListMethods.writeBIC(outf, tooLarge);
    }

    /**
     * @param args
     */
    private void init(String[] args) {

        System.out.println("treecut " + args[0]);
        treecutmatrix = TabFile.readtoArray(args[0], true);

        System.out.println("init loaded treecutmatrix file");
        nodelabels = treecutmatrix[0];
        nodelabels = StringUtil.replace(nodelabels, "X", "");
        nodelabels = StringUtil.replace(nodelabels, "V", "");
        nodelabels = StringUtil.replace(nodelabels, "\"", "");
        nodelabels = MoreArray.removeEntry(nodelabels, 1);
        String s = nodelabels[nodelabels.length - 1];
        System.out.println("nodelabels " + MoreArray.toString(nodelabels, "_"));
        maxleaf = Integer.parseInt(s);
        System.out.println("maxleaf " + maxleaf);
        treecutmatrix = MoreArray.removeRow(treecutmatrix, 1);

        crit_label = args[3];
        assignCrit = new AssignCrit(crit_label);
        critindex = assignCrit.CRITindex[0];


        inputtraj_path = args[1];
        ValueBlockList[] vblget = ValueBlockListMethods.getFirstandLast(inputtraj_path, null, crit_label, null);
        vbl = vblget[1];
        vbl_copy = new ValueBlockList(vbl);
        vbl_merged = new ValueBlockList();

        //expr_data = new SimpleMatrix(args[4]);

        //prm = new Parameters();
        //prm.read(args[4]);

        rmb = new RunMinerBack(args[4], false);

        //prm.USE_MEAN = true;
        header = "#" + s;

        //Rcodepath = prm.R_METHODS_PATH;
        //inputtxt = prm.EXPR_DATA_PATH;
        //inputR = prm.R_DATA_PATH;

        //outdir = prm.OUTDIR;
        //if (outdir.equals(""))
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

        //expr_data = new SimpleMatrix(prm.EXPR_DATA_PATH);

        GENE_SIZE = rmb.expr_matrix.data.length;
        EXP_SIZE = rmb.expr_matrix.data[0].length;

        //irv = new InitRandVar(prm, seed, Rcodepath, inputR, debug);

        if (args.length == 6) {
            cutoff = Double.parseDouble(args[5]);
        }
    }

    /**
     * @param args
     */
    public final static void main(String[] args) {

        if (args.length == 5 || args.length == 6) {
            TreeMergeMembers rm = new TreeMergeMembers(args);
        } else {
            System.out.println("syntax: java DataMining.TreeMergeMembers\n" +
                    "<tree level matrix>\n" +
                    "<dir of block lists or file of trajectory endpoints>\n" +
                    "<outfile>\n" +
                    "<criterion label e.g. __FEMR__ >\n" +
                    "<parameter file>\n" +
                    "<OPTIONAL score cutoff>"
            );
        }
    }

}