package DataMining;

import util.MoreArray;
import util.StringUtil;
import util.TabFile;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;


/**
 *
 * DEPRECATED!
 *
 * <p/>
 * Produces merged blocks based on HCL clustering and tree cut at height h.
 * <p/>
 * User: marcin
 * Date: Jan 24, 2011
 * Time: 10:52:26 AM
 */
public class MergeMembers {

    boolean debug = false;
    String outdir;

    ValueBlockList vbl;
    ValueBlockList vbl_out;

    String[][] treecutdata;

    int GENE_LIMIT = 300;
    int EXP_LIMIT = 300;
    HashMap genes_exps;
    HashMap genes_exps_pairs;
    HashMap genes;
    HashMap exps;

    int GENE_SIZE = 6160;
    int EXP_SIZE = 667;

    /**
     * @param args
     */
    public MergeMembers(String[] args) {
        init(args);

        int cluster_count = 1;

        genes_exps_pairs = new HashMap();
        genes_exps = new HashMap();
        genes = new HashMap();
        exps = new HashMap();

        String[] clusters = MoreArray.extractColumnStr(treecutdata, 2);
        int[] cluster_indices = StringUtil.locateEquals(clusters, "" + cluster_count);
        int[] rem = {-1};
        try {
            cluster_indices = MoreArray.removeByVal(cluster_indices, rem);
        } catch (Exception e) {
            e.printStackTrace();
        }
        while (cluster_indices != null && cluster_indices.length > 0) {
            System.out.println("cluster " + cluster_count + "\t" +
                    cluster_indices.length + "\t" + MoreArray.toString(cluster_indices));
            ValueBlock vb = new ValueBlock();

            for (int i = 0; i < cluster_indices.length; i++) {
                ValueBlock cur = (ValueBlock) vbl.get(cluster_indices[i]);
                //System.out.println("cluster index " + i + "\t" + cluster_indices.length);
                if (vb.genes == null) {
                    vb.genes = MoreArray.copy(cur.genes);
                    genes = util.LoadHash.addAndIncr(cur.genes, genes);
                } else {
                    if (debug) {
                        System.out.println(cluster_count + " merging g master " +
                                MoreArray.toString(vb.genes, ","));
                        System.out.println(cluster_count + " merging g add    " + MoreArray.toString(cur.genes, ","));
                    }
                    vb.addGenes(cur.genes);
                    genes = util.LoadHash.addAndIncr(cur.genes, genes);
                }
                if (vb.exps == null) {
                    vb.exps = MoreArray.copy(cur.exps);
                    exps = util.LoadHash.addAndIncr(cur.exps, exps);
                } else {
                    if (debug) {
                        System.out.println(cluster_count + " merging e master " +
                                MoreArray.toString(vb.exps, ","));
                        System.out.println(cluster_count + " merging e add    " + MoreArray.toString(cur.exps, ","));
                    }
                    vb.addExps(cur.exps);
                    exps = util.LoadHash.addAndIncr(cur.exps, exps);
                }

                genes_exps = util.LoadHash.addPairAndIncrStr(cur.genes, cur.exps, genes_exps);
                genes_exps_pairs = util.LoadHash.addPairofPairAndIncr(cur.genes, cur.exps, genes_exps_pairs);

            }
            System.out.println("merged " + vb.genes.length + "\t" + vb.exps.length);
            vbl_out.add(vb);
            cluster_count++;
            cluster_indices = StringUtil.locateEquals(clusters, "" + cluster_count);
            try {
                cluster_indices = MoreArray.removeByVal(cluster_indices, rem);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        output(args[2], vbl, vbl_out);
    }

    /**
     * @param args
     */
    private void init(String[] args) {
        outdir = args[3];

        File test = new File(outdir);
        if (!test.exists()) {
            test.mkdir();
        }

        System.out.println("treecut " + args[0]);
        treecutdata = TabFile.readtoArray(args[0], 1, false);

        vbl = ValueBlockListMethods.readBIC(args[1], false);

        vbl_out = new ValueBlockList();

        if (args.length > 4) {
            GENE_SIZE = Integer.parseInt(args[4]);
            EXP_SIZE = Integer.parseInt(args[5]);
        }
    }

    /**
     * @param arg
     * @param vbl
     * @param vbl_out
     */
    private void output(String arg, ValueBlockList vbl, ValueBlockList vbl_out) {

        Set geneset = genes.keySet();
        Set expset = exps.keySet();
        System.out.println("total: covered " + geneset.size() + " genes, out of " + GENE_SIZE);
        System.out.println("total: covered " + expset.size() + " exps, out of " + EXP_SIZE);

        ValueBlockList tooLarge = new ValueBlockList();
        for (int i = 0; i < vbl_out.size(); i++) {
            ValueBlock v = (ValueBlock) vbl.get(i);
            //System.out.println("v.genes.length " + v.genes.length + "\tv.exps.length " + v.exps.length);
            if (v.genes.length > GENE_LIMIT || v.exps.length > EXP_LIMIT) {
                tooLarge.add(new ValueBlock(v));
                System.out.println("removing HUGE " + v.genes.length + "\t" + v.exps.length + "\t" + tooLarge.size());
                vbl.remove(i);
                i--;
            }
        }

        for (int i = 0; i < vbl_out.size(); i++) {
            ValueBlock v = (ValueBlock) vbl.get(i);
            v.scoreGeneExpAndWrite(genes_exps, outdir + "/" + "" + i + "_geneexp_count.txt", "#" + v.blockId());
            v.scoreGeneExpPairsAndWrite(genes_exps_pairs, outdir + "/" + "" + i + "_geneexp_pair_count.txt", "#" + v.blockId());
            //vbl.set(i, v);
        }

        genes = new HashMap();
        exps = new HashMap();
        for (int i = 0; i < tooLarge.size(); i++) {
            ValueBlock v = (ValueBlock) vbl.get(i);
            genes = util.LoadHash.addAndIncr(v.genes, genes);
            exps = util.LoadHash.addAndIncr(v.exps, exps);
        }
        Set lgeneset = genes.keySet();
        Set lexpset = exps.keySet();
        System.out.println("large: covered " + geneset.size() + " genes, out of " + GENE_SIZE);
        System.out.println("large: covered " + expset.size() + " exps, out of " + EXP_SIZE);

        genes = new HashMap();
        exps = new HashMap();
        for (int i = 0; i < vbl_out.size(); i++) {
            ValueBlock v = (ValueBlock) vbl.get(i);
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

        ValueBlockListMethods.writeBIC(outdir + "/" + arg, vbl_out);
        ValueBlockListMethods.writeBIC(outdir + "/" + arg + "_toolarge.bic", tooLarge);
    }

    /**
     * @param args
     */
    public final static void main(String[] args) {

        if (args.length == 4 || args.length == 6) {
            MergeMembers rm = new MergeMembers(args);
        } else {
            System.out.println("syntax: java DataMining.MergeMembers\n" +
                    "<tree cut file>\n" +
                    "<list of blocks>\n" +
                    //"<count on grid>\n" +
                    "<outfile>\n" +
                    "<outdir>\n" +
                    "<OPTIONAL gene size>\n" +
                    "<OPTIONAL exp size>"
            );
        }
    }


}
