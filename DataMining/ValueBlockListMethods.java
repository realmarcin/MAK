package DataMining;

import mathy.Matrix;
import mathy.stat;
import util.MoreArray;
import util.StringUtil;
import util.TextFile;

import java.io.*;
import java.util.*;

public class ValueBlockListMethods implements Serializable, Cloneable {


    /**
     * @param vbl
     * @param vb
     * @return
     */
    public final static int findPosExprCrit(ValueBlockList vbl, ValueBlock vb, boolean[] expr_crits) {
        int ret = -1;
        double masterexprcrit = ValueBlock.computeFullCrit(vb.all_criteria, true, expr_crits, false);
        for (int i = 0; i < vbl.size(); i++) {
            ValueBlock vbtest = (ValueBlock) vbl.get(i);
            double exprcrit = ValueBlock.computeFullCrit(vbtest.all_criteria, true, expr_crits, false);
            if (masterexprcrit >= exprcrit) {
                //System.out.println("findPosFull ret " + (i + 1));
                return i;
            }
        }
        if (ret == -1 && vbl.size() < 100) {
            return vbl.size();
        }
        //System.out.println("findPosFull ret " + (-1));
        return -1;
    }

    /**
     * @param vbl
     * @param vb
     * @return
     */
    public final static int findPosInteractProportion(ValueBlockList vbl, ValueBlock vb) {
        int ret = -1;
        for (int i = 0; i < vbl.size(); i++) {
            ValueBlock vbtest = (ValueBlock) vbl.get(i);
            //if ((vb.all_precriteria.length > 1 && vb.all_precriteria[1] >= vbtest.all_precriteria[1]) ||
            //       (vb.all_criteria.length > 1 && vb.all_criteria[ValueBlock_STATIC.expr_MSE_IND] >= vbtest.all_criteria[ValueBlock_STATIC.expr_MSE_IND])) {
            if (vb.all_criteria[ValueBlock_STATIC.interact_IND] >= vbtest.all_criteria[ValueBlock_STATIC.interact_IND]) {
                //System.out.println("findPosFull ret " + (i + 1));
                return i;
            }
        }
        if (ret == -1 && vbl.size() < 100) {
            return vbl.size();
        }
        //System.out.println("findPosFull ret " + (-1));
        return -1;
    }

    /**
     * @param vbl
     * @param vb
     * @return
     */
    public final static int findPosFeatures(ValueBlockList vbl, ValueBlock vb) {
        int ret = -1;
        for (int i = 0; i < vbl.size(); i++) {
            ValueBlock vbtest = (ValueBlock) vbl.get(i);
            if (vb.all_criteria[ValueBlock_STATIC.feat_IND] >= vbtest.all_criteria[ValueBlock_STATIC.feat_IND]) {
                //System.out.println("findPosFull ret " + (i + 1));
                return i;
            }
        }
        if (ret == -1 && vbl.size() < 100) {
            return vbl.size();
        }
        //System.out.println("findPosFull ret " + (-1));
        return -1;
    }

    /**
     * @param vbl
     * @param vb
     * @return
     */
    public final static int findPosFull(ValueBlockList vbl, ValueBlock vb) {
        int ret = -1;
        for (int i = 0; i < vbl.size(); i++) {
            ValueBlock vbtest = (ValueBlock) vbl.get(i);
            if (vb.full_criterion >= vbtest.full_criterion) {
                //System.out.println("findPosFull ret " + (i + 1));
                return i;
            }
        }
        if (ret == -1 && vbl.size() < 100) {
            return vbl.size();
        }
        //System.out.println("findPosFull ret " + (-1));
        return -1;
    }

    /**
     * @param vbl
     * @param val
     * @return
     */
    public final static int findPosFull(ValueBlockList vbl, double val, int toplen) {
        if (vbl.size() == 0) {
            return 0;
        } else {
            for (int i = 0; i < vbl.size(); i++) {
                ValueBlock vbtest = (ValueBlock) vbl.get(i);
                if (val >= vbtest.full_criterion) {
                    //System.out.println("findPosFull ret " + (i + 1));
                    return i;
                }
            }
            //if not returned in loop
            if (vbl.size() < toplen) {
                return vbl.size();
            }
        }
        //System.out.println("findPosFull ret " + (-1));
        return -1;
    }

    /**
     * @param vbl
     * @param vb
     * @return
     */
    public final static int findPosPre(ValueBlockList vbl, ValueBlockPre vb) {
        int ret = -1;
        for (int i = 0; i < vbl.size(); i++) {
            ValueBlock vbtest = (ValueBlock) vbl.get(i);
            if (vb.pre_criterion >= vbtest.pre_criterion) {
                //System.out.println("findPosFull ret " + (i + 1));
                return i;
            }
        }
        if (ret == -1 && vbl.size() < 100) {
            return vbl.size();
        }
        //System.out.println("findPosFull ret " + (-1));
        return -1;
    }

    /**
     * Finds the last block before random flooding
     *
     * @param vbl
     * @return
     */
    public final static int findLastBFRandom(ValueBlockList vbl) {
        for (int i = 0; i < vbl.size(); i++) {
            ValueBlock vb = (ValueBlock) vbl.get(i);
            //System.out.println(i + "\t" + vb.move_class);
            if (vb.move_class.startsWith("r")) {
                return i - 1;
            }
        }
        return -1;
    }

    /**
     * @param one
     * @param two
     * @return
     */
    public final static double[][] compareFraction(ValueBlockList one, ValueBlockList two) {
        double[][] ret = new double[one.size()][two.size()];
        for (int i = 0; i < one.size(); i++) {
            ValueBlock vb1 = (ValueBlock) one.get(i);
            for (int j = 0; j < two.size(); j++) {
                ValueBlock vb2 = (ValueBlock) two.get(j);
                ret[i][j] = BlockMethods.computeBlockOverlapGeneExpFraction(vb1, vb2, false);
                //ret[i][j] = BlockMethods.computeBlockOverlapGeneExpRootProduct(vb1, vb2);
            }
        }
        return ret;
    }

    /**
     * @param one
     * @param two
     * @return
     */
    public final static double[][] compareRootProduct(ValueBlockList one, ValueBlockList two) {
        double[][] ret = new double[one.size()][two.size()];
        for (int i = 0; i < one.size(); i++) {
            ValueBlock vb1 = (ValueBlock) one.get(i);
            for (int j = 0; j < two.size(); j++) {
                ValueBlock vb2 = (ValueBlock) two.get(j);
                //ret[i][j] = BlockMethods.computeBlockOverlapGeneExpFraction(vb1, vb2, false);
                ret[i][j] = BlockMethods.computeBlockOverlapGeneExpRootProduct(vb1, vb2);
            }
        }
        return ret;
    }

    /**
     * @param one
     * @param two
     * @return
     */
    public final static double[][] compareFractionMap(ValueBlockList one, ValueBlockList two) {
        double[][] ret = new double[one.size()][two.size()];

        ArrayList hashesone = new ArrayList();
        //build gene_exp hash
        for (int i = 0; i < one.size(); i++) {
            if (i % 100 == 0)
                System.out.print(i + "/" + one.size() + " ");
            ValueBlock vi = (ValueBlock) one.get(i);
            HashMap hvi = new HashMap();
            for (int g = 0; g < vi.genes.length; g++) {
                if (vi.exps != null) {
                    for (int e = 0; e < vi.exps.length; e++) {
                        hvi.put("" + vi.genes[g] + "_" + vi.exps[e], 1);
                    }
                } else {
                    hvi.put("" + vi.genes[g], 1);
                }
            }
            if (hvi.size() > 0)
                hashesone.add(hvi);
        }

        ArrayList hashestwo = new ArrayList();
        //build gene_exp hash
        for (int i = 0; i < two.size(); i++) {
            if (i % 100 == 0)
                System.out.print(i + "/" + two.size() + " ");
            ValueBlock vi = (ValueBlock) two.get(i);
            HashMap hvi = new HashMap();
            for (int g = 0; g < vi.genes.length; g++) {
                if (vi.exps != null) {
                    for (int e = 0; e < vi.exps.length; e++) {
                        hvi.put("" + vi.genes[g] + "_" + vi.exps[e], 1);
                    }
                } else {
                    hvi.put("" + vi.genes[g], 1);
                }
            }
            if (hvi.size() > 0)
                hashestwo.add(hvi);
        }

        //pairwise bicluster overlap
        for (int i = 0; i < one.size(); i++) {
            System.out.print(".");
            HashMap hi = (HashMap) hashesone.get(i);
            ValueBlock vi = (ValueBlock) one.get(i);

            for (int j = 0; j < two.size(); j++) {
                ValueBlock vj = (ValueBlock) two.get(j);
                HashMap hj = (HashMap) hashestwo.get(j);
                ret[i][j] = BlockMethods.computeBlockOverlapGeneExpFraction(hi, hj,
                        vi.genes.length, vi.exps.length, vj.genes.length, vj.exps.length);
            }
        }

        return ret;
    }

    /**
     * @param one
     * @param two
     * @return
     */
    public final static double[][] compareFractionGene(ValueBlockList one, ValueBlockList two) {
        double[][] ret = new double[one.size()][two.size()];

        //pairwise bicluster overlap
        for (int i = 0; i < one.size(); i++) {
            System.out.print(".");
            ValueBlock vi = (ValueBlock) one.get(i);
            for (int j = 0; j < two.size(); j++) {
                ValueBlock vj = (ValueBlock) two.get(j);
                ret[i][j] = BlockMethods.computeBlockOverlapGeneFraction(vi, vj,
                        false);
            }
        }

        return ret;
    }

    /**
     * @param one
     * @param two
     * @return
     */
    public final static double[][] compareFractionExp(ValueBlockList one, ValueBlockList two) {
        double[][] ret = new double[one.size()][two.size()];

        //pairwise bicluster overlap
        for (int i = 0; i < one.size(); i++) {
            System.out.print(".");
            ValueBlock vi = (ValueBlock) one.get(i);
            for (int j = 0; j < two.size(); j++) {
                ValueBlock vj = (ValueBlock) two.get(j);
                ret[i][j] = BlockMethods.computeBlockOverlapExpFraction(vi, vj,
                        false);
            }
        }

        return ret;
    }

    /**
     * @param one
     * @param two
     * @return
     */
    public final static double[][] compareRootProductMap(ValueBlockList one, ValueBlockList two) {
        double[][] ret = new double[one.size()][two.size()];

        ArrayList hashesone = new ArrayList();
        //build gene_exp hash
        for (int i = 0; i < one.size(); i++) {
            if (i % 100 == 0)
                System.out.print(i + "/" + one.size() + " ");
            ValueBlock vi = (ValueBlock) one.get(i);
            HashMap hvi = new HashMap();
            for (int g = 0; g < vi.genes.length; g++) {
                if (vi.exps != null) {
                    for (int e = 0; e < vi.exps.length; e++) {
                        hvi.put("" + vi.genes[g] + "_" + vi.exps[e], 1);
                    }
                } else {
                    hvi.put("" + vi.genes[g], 1);
                }
            }
            if (hvi.size() > 0)
                hashesone.add(hvi);
        }

        ArrayList hashestwo = new ArrayList();
        //build gene_exp hash
        for (int i = 0; i < two.size(); i++) {
            if (i % 100 == 0)
                System.out.print(i + "/" + two.size() + " ");
            ValueBlock vi = (ValueBlock) two.get(i);
            HashMap hvi = new HashMap();
            for (int g = 0; g < vi.genes.length; g++) {
                if (vi.exps != null) {
                    for (int e = 0; e < vi.exps.length; e++) {
                        hvi.put("" + vi.genes[g] + "_" + vi.exps[e], 1);
                    }
                } else {
                    hvi.put("" + vi.genes[g], 1);
                }
            }
            if (hvi.size() > 0)
                hashestwo.add(hvi);
        }

        //pairwise bicluster overlap
        for (int i = 0; i < one.size(); i++) {
            System.out.print(".");
            HashMap hi = (HashMap) hashesone.get(i);
            ValueBlock vi = (ValueBlock) one.get(i);
            for (int j = i + 1; j < two.size(); j++) {
                ValueBlock vj = (ValueBlock) two.get(j);
                HashMap hj = (HashMap) hashestwo.get(j);

                if (vj.exps != null) {
                    ret[i][j] = BlockMethods.computeBlockOverlapGeneExpRootProduct(hi, hj,
                            vi.genes.length, vi.exps.length, vj.genes.length, vj.exps.length);
                    ret[j][i] = ret[i][j];

                }
            }
        }

        return ret;
    }

    /**
     * @param one
     * @param two
     * @return
     */
    public final static double[][] compareRootProductGene(ValueBlockList one, ValueBlockList two) {
        double[][] ret = new double[one.size()][two.size()];

        //pairwise bicluster overlap
        for (int i = 0; i < one.size(); i++) {
            System.out.print(".");
            ValueBlock vi = (ValueBlock) one.get(i);
            for (int j = i + 1; j < two.size(); j++) {
                ValueBlock vj = (ValueBlock) two.get(j);
                if (vj.exps != null) {
                    ret[i][j] = BlockMethods.computeBlockOverlapGeneRootProduct(vi, vj);
                    ret[j][i] = ret[i][j];
                }
            }
        }

        return ret;
    }

    /**
     * @param one
     * @param two
     * @return
     */
    public final static double[][] compareRootProductExp(ValueBlockList one, ValueBlockList two) {
        double[][] ret = new double[one.size()][two.size()];

        //pairwise bicluster overlap
        for (int i = 0; i < one.size(); i++) {
            System.out.print(".");
            ValueBlock vi = (ValueBlock) one.get(i);
            for (int j = i + 1; j < two.size(); j++) {
                ValueBlock vj = (ValueBlock) two.get(j);
                if (vj.exps != null) {
                    ret[i][j] = BlockMethods.computeBlockOverlapGeneRootProduct(vi, vj);
                    ret[j][i] = ret[i][j];
                }
            }
        }

        return ret;
    }

    /**
     * @param f
     * @return
     */
    public final static ValueBlockList readAny(String f) throws Exception {
        return readAny(f, false);
    }

    /**
     * @param f
     * @param d
     * @return
     */
    public final static ValueBlockList readAny(String f, boolean d) throws Exception {
        ValueBlockList BIC = null;
        try {
            System.out.println("ValueBlockList.read BIC");
            BIC = ValueBlockListMethods.readBIC(f, d);
            //System.out.println("ValueBlockList.readBIC BIC " + BIC.size());
        } catch (Exception e) {

            System.out.println("WARNING: not BIC format");
            if (d)
                e.printStackTrace();

            try {
                System.out.println("ValueBlockList.read Simple");
                BIC = ValueBlockListMethods.readSimple(f);
                //System.out.println("ValueBlockList.readSimple Simple " + BIC.size());
            } catch (Exception e1) {
                System.out.println("WARNING: not simple bicluster format");
                if (d)
                    e1.printStackTrace();
                try {
                    System.out.println("ValueBlockList.read vbl");
                    BIC = ValueBlockList.read(f, d);//false);
                    //System.out.println("ValueBlockList.read vbl " + BIC.size());
                } catch (Exception e2) {

                    System.out.println("WARNING: not MAK VBL format");
                    System.out.println("WARNING: failed to recognize a valid bicluster format.");
                    if (d)
                        e2.printStackTrace();

                    try {
                        System.out.println("ValueBlockList.read UniBic");
                        BIC = ValueBlockListMethods.readUniBic(f, 1, d);
                        //System.out.println("ValueBlockList.read vbl " + BIC.size());
                    } catch (Exception e3) {
                        System.out.println("WARNING: failed to recognize a valid bicluster format.");
                        if (d)
                            e3.printStackTrace();
                    }
                }
            }
        }

        try {
            int size = BIC.size();
            System.out.println("ValueBlockList.readAny size " + size);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return BIC;
    }

    /**
     * @param f
     * @return
     */
    public final static ValueBlockList readSimple(String f) throws Exception {
        //System.out.println("readBIC " + f);
        ValueBlockList vls = null;
        try {
            vls = new ValueBlockList();
            BufferedReader in = new BufferedReader(new FileReader(f));
            String data = in.readLine();
            //skip R output HEADER_EVAL
            if (data.equalsIgnoreCase("\"x\""))
                data = in.readLine();
            int count = 0;
            while (data != null) {
                data = StringUtil.replace(data, "\"", "");
                //System.out.println("readSimple " + data);
                String[] all = data.split("\t");
                //System.out.println("readSimple " + all.length);
                if (all.length == 2)
                    data = all[1];
                String[] gene_exps = data.split("/");
                //MoreArray.printArray(gene_exps);
                String[] genes = gene_exps[0].split(",");
                String[] exps = null;
                if (gene_exps.length > 1) {
                    try {

                        exps = gene_exps[1].split(",");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                }
                ValueBlock vb = new ValueBlock();

                //try {
                vb.genes = MoreArray.tointArray(genes);
                //} catch (Exception e) {
                /*if (vb.genes == null) {
                    System.out.println("readBIC detected non-numeric genes ids");
                    MoreArray.printArray(genes);
                }*/
                // }
                //try {
                if (exps != null)
                    vb.exps = MoreArray.tointArray(exps);
                //} catch (Exception e) {
                /* if (vb.exps == null) {
                    System.out.println("readBIC detected non-numeric exps ids");
                    MoreArray.printArray(exps);
                }*/
                //}

               /* System.out.println(count + "\t" + data);
                System.out.println(MoreArray.toString(vb.genes, ","));
                System.out.println(MoreArray.toString(vb.exps, ","));*/

                if (vb.genes != null && vb.exps != null) {
                    int[][] update = {vb.genes, vb.exps};
                    vb.NRCoords(update);
                } else {
                    System.out.println("failed to parse vb " + count + "\t" + data);
                }
                if (vb.genes != null) {
                    int[] update = vb.genes;
                    vb.NRGenes(update);
                } else if (vb.exps != null) {
                    int[] update = vb.exps;
                    vb.NRExps(update);
                }

                vls.add(vb);

                data = in.readLine();
                count++;
            }
            if (vls.size() == 0)
                vls = null;
            in.close();
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
            e.printStackTrace();
        }
        return vls;
    }

    /**
     * @param f
     * @param labels
     * @return
     * @throws Exception
     */
    public final static ValueBlockList readJSONGenes(String f, String[] labels) throws Exception {
        ValueBlockList vls = null;
        try {
            vls = readJSONGenes(f, labels, null);
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
            e.printStackTrace();
        }
        return vls;
    }

    /**
     * @param f
     * @return
     */
    public final static ValueBlockList readJSONGenes(String f, String[] labels, String[] exps) throws Exception {
        //System.out.println("readBIC " + f);
        ValueBlockList vls = null;

        String[] curexps = exps;
        try {
            vls = new ValueBlockList();
            BufferedReader in = new BufferedReader(new FileReader(f));
            String data = in.readLine();
            String[] genes = data.split("\\[");
            for (int i = 0; i < genes.length; i++) {
                if (genes[i].length() > 1) {
                    genes[i] = genes[i].replace("],", "");
                    genes[i] = genes[i].replace("]", "");
                    genes[i] = genes[i].replace(" ", "");
                    //System.out.println("readJSONGenes *"+genes[i]+"*");
                    String[] curgenes = genes[i].split(",");
                    ValueBlock vb = new ValueBlock();
                    vb.genes = new int[curgenes.length];
                    for (int j = 0; j < curgenes.length; j++) {
                        int index = MoreArray.getArrayInd(labels, curgenes[j]);
                        //System.out.println("readJSONGenes *"+curgenes[j] + "*\t\t\t*" + labels[0] + "*\t\t\t" + index);
                        vb.genes[j] = index + 1;
                    }

                    if (exps != null)
                        vb.exps = MoreArray.tointArray(curexps);

                    if (vb.genes != null && vb.exps != null) {
                        int[][] update = {vb.genes, vb.exps};
                        vb.NRCoords(update);
                    }
                    if (vb.genes != null) {
                        int[] update = vb.genes;
                        vb.NRGenes(update);
                    } else if (vb.exps != null) {
                        int[] update = vb.exps;
                        vb.NRExps(update);
                    }

                    vls.add(vb);
                }
            }
            if (vls.size() == 0)
                vls = null;
            in.close();
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
            e.printStackTrace();
        }
        return vls;
    }

    /**
     * @param f
     * @return
     */
    public final static ValueBlockList readCOALESCE(String f, String[] glabel, String[] elabel) throws Exception {
        //System.out.println("readCOALESCE " + f);
        ValueBlockList vls = null;
        try {
            vls = new ValueBlockList();
            System.out.println("readCOALESCE f " + f);
            BufferedReader in = new BufferedReader(new FileReader(f));
            String data = in.readLine();
            while (data != null) {
                data = in.readLine();
                System.out.println("readCOALESCE genes " + data);
                String[] genes = data.split("\t");
                data = in.readLine();
                System.out.println("readCOALESCE exps " + data);
                String[] exps = data.split("\t");
                genes = MoreArray.removeEntry(genes, 1);
                exps = MoreArray.removeEntry(exps, 1);

                for (int i = 0; i < genes.length; i++) {
                    int index = MoreArray.getArrayInd(glabel, genes[i]);
                    genes[i] = "" + (index + 1);
                }
                for (int i = 0; i < exps.length; i++) {
                    int index = MoreArray.getArrayInd(elabel, exps[i]);
                    exps[i] = "" + (index + 1);
                }

                ValueBlock vb = new ValueBlock();
                vb.genes = MoreArray.tointArray(genes);
                vb.exps = MoreArray.tointArray(exps);

                if (vb.genes != null && vb.exps != null) {
                    int[][] update = {vb.genes, vb.exps};
                    vb.NRCoords(update);
                }
                vls.add(vb);

                //skip motif and score
                data = in.readLine();
                while (data != null && data.indexOf("Cluster") == -1)
                    data = in.readLine();
            }
            if (vls.size() == 0)
                vls = null;
            in.close();
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
            e.printStackTrace();
        }
        return vls;
    }

    /**
     * GCTCTG
     * 3.42595
     * 162	225	271	363	455	474	475	618	762	785	836	962	995	1075	1092	1103	1219	1260	1342	1359	1611	1764	1856	1923	2061	2175	2338	2577	2670	2816	2819	2829	2947	2961	3084	3121	3291	3908	3966	4045	4081	4128	4201	4227	4274	4288	4345	4526	4709	4813	4836	4895	4901	4944	4974
     * 3	31	34	41	76	85	93
     *
     * @param f
     * @return
     */
    public final static ValueBlockList readCOALESCESim(String f) throws Exception {
        //System.out.println("readCOALESCESim " + f);
        ValueBlockList vls = null;
        try {
            vls = new ValueBlockList();
            BufferedReader in = new BufferedReader(new FileReader(f));
            String data = in.readLine();
            data = in.readLine();
            while (data != null) {
                String[] genes = data.split(" ");
                data = in.readLine();
                System.out.println("readCOALESCESim reading exps " + data);
                String[] exps = data.split(" ");

                ValueBlock vb = new ValueBlock();
                vb.genes = stat.add(MoreArray.tointArray(genes), 1);
                vb.exps = stat.add(MoreArray.tointArray(exps), 1);

                if (vb.genes != null && vb.exps != null) {
                    int[][] update = {vb.genes, vb.exps};
                    vb.NRCoords(update);
                }
                vls.add(vb);

                //skip motif and score
                data = in.readLine();
                if (data != null)
                    data = in.readLine();
            }
            if (vls.size() == 0)
                vls = null;
            in.close();
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
            e.printStackTrace();
        }
        return vls;
    }

    /**
     * @param f
     * @param debug
     * @return
     */
    public final static ValueBlockList readBIC(String f, boolean debug) {
        if (debug)
            System.out.println("readBIC " + f);
        ValueBlockList vls = null;
        try {
            vls = new ValueBlockList();
            BufferedReader in = new BufferedReader(new FileReader(f));
            String data = in.readLine();
            int count = 0;
            while (data != null) {
                String[] size = data.split(" ");
                data = in.readLine();
                if (debug)
                    System.out.println("data: " + data);
                String[] genes = data.split(" ");
                if (debug) {
                    System.out.println("genes: ");
                    MoreArray.printArray(genes);
                }
                data = in.readLine();
                //System.out.println("data: " + data);
                String[] exps = data.split(" ");
                if (debug) {
                    System.out.println("exps: ");
                    MoreArray.printArray(exps);
                }
                ValueBlock vb = new ValueBlock();
                try {
                    vb.genes = MoreArray.tointArray(genes);
                } catch (Exception e) {
                    //System.out.println("readBIC detected non-numeric genes ids");
                    //MoreArray.printArray(genes);
                    genes = StringUtil.replace(genes, "\"", "");
                    vb.genes = MoreArray.tointArray(genes);
                }
                try {
                    vb.exps = MoreArray.tointArray(exps);
                } catch (Exception e) {
                    //System.out.println("readBIC detected non-numeric exps ids");
                    //MoreArray.printArray(exps);
                    exps = StringUtil.replace(exps, "\"", "");
                    exps = StringUtil.replace(exps, "V", "");
                    vb.exps = MoreArray.tointArray(exps);
                }


               /* System.out.println(count + "\t" + data);
                System.out.println(MoreArray.toString(vb.genes, ","));
                System.out.println(MoreArray.toString(vb.exps, ","));*/

                if (vb.genes != null && vb.exps != null) {
                    /*System.out.println("vb.genes: ");
                    MoreArray.printArray(vb.genes);
                    System.out.println("vb.exps: ");
                    MoreArray.printArray(vb.exps);*/
                    int[][] update = {vb.genes, vb.exps};
                    vb.NRCoords(update);
                    //vb.block_id = vb.genesStr + "/" + vb.expsStr;
                } else {
                    System.out.println("failed to parse vb " + count + "\t" + data);
                } /*else {
                    vb.block_id = BlockMethods.IcJctoijID(vb.coords);
                }*/
                //}
                //System.out.println("vb.block_id: " + vb.block_id);
                vls.add(vb);
                if (debug)
                    System.out.println("readBIC vls.size " + vls.size());
                data = in.readLine();
                count++;
            }
            if (vls.size() == 0)
                vls = null;
            in.close();
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
            e.printStackTrace();
        }
        return vls;
    }


    /**
     * @param f
     * @param debug
     * @return
     */
    public final static ValueBlockList readUniBic(String f, int offset, boolean debug) {
        if (debug)
            System.out.println("readBIC " + f);
        ValueBlockList vls = null;
        try {
            vls = new ValueBlockList();
            BufferedReader in = new BufferedReader(new FileReader(f));
            String data = in.readLine();
            if (debug)
                System.out.println("first " + data);
            data = in.readLine();
            int count = 0;
            while (data.indexOf("Bicluster([") != 0)
                data = in.readLine();
            while (data != null && data.length() > 0) {

                if (debug)
                    System.out.println("data: " + data);

                String[] first = data.split("\\[");

                String rawg = null;
                try {
                    rawg = first[1].substring(0, first[1].indexOf("]"));
                } catch (Exception e) {
                    data = in.readLine();
                    first = data.split("\\[");
                    rawg = first[1].substring(0, first[1].indexOf("]"));
                    //e.printStackTrace();
                }
                String rawe = first[2].substring(0, first[2].indexOf("]"));

                String[] genes = rawg.split(",");
                if (debug) {
                    System.out.println("genes: ");
                    MoreArray.printArray(genes);
                }
                //data = in.readLine();
                String[] exps = rawe.split(",");
                if (debug) {
                    System.out.println("exps: ");
                    MoreArray.printArray(exps);
                }
                ValueBlock vb = new ValueBlock();
                try {
                    vb.genes = MoreArray.tointArray(genes);
                } catch (Exception e) {
                    genes = StringUtil.replace(genes, " ", "");
                    genes = StringUtil.replace(genes, "\"", "");
                    vb.genes = MoreArray.tointArray(genes);
                }
                try {
                    vb.exps = MoreArray.tointArray(exps);
                } catch (Exception e) {
                    exps = StringUtil.replace(exps, " ", "");
                    exps = StringUtil.replace(exps, "\"", "");
                    vb.exps = MoreArray.tointArray(exps);
                }

                if (vb.genes != null && vb.exps != null) {

                    int[][] update = {vb.genes, vb.exps};
                    vb.NRCoords(update);
                } else {
                    System.out.println("failed to parse vb " + count + "\t" + data);
                }


                vls.add(vb);
                if (debug)
                    System.out.println("readUniBic vls.size " + vls.size());
                data = in.readLine();
                count++;
            }
            if (vls.size() == 0)
                vls = null;
            in.close();
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
            e.printStackTrace();
        }
        return vls;
    }

    /**
     * @param f
     * @param glabels
     * @param elabels
     * @param debug
     * @return
     */
    public final static ValueBlockList readRecBic(String f, String[] glabels, String[] elabels, boolean debug) {
        if (debug)
            System.out.println("readRecBic " + f);
        ValueBlockList vls = null;
        try {
            vls = new ValueBlockList();
            BufferedReader in = new BufferedReader(new FileReader(f));
            String data = null;
            int count = 0;

            while (data != null && data.length() > 0) {
                System.out.println("1 "+data);
                while (data != null && data.indexOf("BC: ") != 0)
                    data = in.readLine();

                if(data == null)
                    break;
                data = in.readLine();
                System.out.println("2 "+data);

                if (debug)
                    System.out.println("data: " + data);

                String[] first = data.split(": ");

                String[] genes = first[1].split(" ");
                if (debug) {
                    System.out.println("genes: ");
                    MoreArray.printArray(genes);
                }

                data = in.readLine();
                String[] first2 = data.split(": ");

                String[] exps = first2[1].split(" ");
                if (debug) {
                    System.out.println("exps: ");
                    MoreArray.printArray(exps);
                }

                int[] genes_int = StringUtil.crossIndex(genes, glabels);
                int[] exps_int = StringUtil.crossIndex(exps, elabels);
                genes_int = Matrix.add(genes_int, 1);
                exps_int = Matrix.add(exps_int, 1);

                ValueBlock vb = new ValueBlock();
                vb.genes = genes_int;
                vb.exps = exps_int;

                if (vb.genes != null && vb.exps != null) {

                    int[][] update = {vb.genes, vb.exps};
                    vb.NRCoords(update);
                } else {
                    System.out.println("failed to parse vb " + count + "\t" + data);
                }

                vls.add(vb);
                if (debug)
                    System.out.println("readUniBic vls.size " + vls.size());
                //data = in.readLine();
                count++;
            }
            if (vls.size() == 0)
                vls = null;
            in.close();
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
            e.printStackTrace();
        }
        return vls;
    }

    /**
     * @param f
     * @return
     */
    public final static ValueBlockList readBICTranslatetoInt(String f) throws Exception {
        //System.out.println("readBIC " + f);
        ValueBlockList vls = null;
        //try {
        vls = new ValueBlockList();
        BufferedReader in = new BufferedReader(new FileReader(f));
        String data = in.readLine();
        while (data != null) {
            //String[] size = data.split(" ");
            data = in.readLine();
            //System.out.println("data: " + data);
            data = StringUtil.replace(data, "\"", "");
            String[] genes = data.split(" ");
            /*System.out.println("genes: ");
            MoreArray.printArray(genes);*/
            data = in.readLine();
            //System.out.println("data: " + data);
            data = StringUtil.replace(data, "\"", "");
            data = StringUtil.replace(data, "V", "");
            String[] exps = data.split(" ");
            /*System.out.println("exps: ");
            MoreArray.printArray(exps);*/
            ValueBlock vb = new ValueBlock();
            //try {
            vb.genes = MoreArray.tointArray(genes);
            //} catch (Exception e) {
            //System.out.println("readBIC detected non-numeric ids genes");
            //MoreArray.toString(genes);
            //e.printStackTrace();
            /*System.out.println("readBIC detected non-numeric ids " + vb.coords[0].size());
             vb.coords = MoreArray.initArrayListArray(2);
            vb.coords[0] = MoreArray.convtoArrayList(genes);
            vb.genes = new int[vb.coords[0].size()];
            for (int i = 0; i < vb.coords[0].size(); i++) {
                vb.genes[i] = MoreArray.getArrayInd(xlabels, (String) vb.coords[0].get(i));
            }*/
            //}
            //try {
            vb.exps = MoreArray.tointArray(exps);
            //} catch (Exception e) {
            //System.out.println("readBIC detected non-numeric ids exps");
            //MoreArray.toString(exps);
            //e.printStackTrace();
            /*System.out.println("readBIC detected non-numeric ids " + vb.coords[1].size());
            vb.coords[1] = MoreArray.convtoArrayList(exps);
            vb.exps = new int[vb.coords[1].size()];
            for (int i = 0; i < vb.coords[1].size(); i++) {
                vb.exps[i] = MoreArray.getArrayInd(ylabels, (String) vb.coords[1].get(i));
            }*/
            //}

            if (vb.genes != null && vb.exps != null) {
                /* System.out.println("vb.genes: ");
                MoreArray.printArray(vb.genes);
                System.out.println("vb.exps: ");
                MoreArray.printArray(vb.exps);*/
                int[][] update = {vb.genes, vb.exps};
                vb.NRCoords(update);
                //vb.block_id = vb.genesStr + "/" + vb.expsStr;
                //System.out.println("readBICTranslatetoInt vb.block_id: " + BlockMethods.IcJctoijID(vb.genes, vb.exps));
                vls.add(vb);
            } /*else {
                    vb.block_id = BlockMethods.IcJctoijID(vb.coords);
                }*/

            data = in.readLine();
        }
        if (vls.size() == 0)
            vls = null;
        in.close();
        //} catch (IOException e) {
        //   System.out.println("IOException: " + e.getMessage());
        //    e.printStackTrace();
        //}
        return vls;
    }

    /**
     *
     */
    public final static ValueBlockList reorderByMean(double[][] data, ValueBlockList vbl) {

        //System.out.println("reorderByMean "+data[0][0]+"\t"+data[1][10]+"\t"+data[10][2]);
        //System.out.println("reorderByMean "+data[0][0]+"\t"+data[1][10]+"\t"+data[10][2]);
        //System.out.println("reorderByMean "+data[597][0]+"\t"+data[597][10]+"\t"+data[597][2]);
        //System.out.println("reorderByMean "+data[596][0]+"\t"+data[596][10]+"\t"+data[596][2]);
        //MoreArray.printArray(data[596]);

        for (int i = 0; i < vbl.size(); i++) {
            ValueBlock cur = (ValueBlock) vbl.get(i);

            //System.out.println(i+" getData g " + MoreArray.toString(cur.genes, ","));
            //System.out.println(i+" getData e " + MoreArray.toString(cur.exps, ","));

            /* System.out.println("reorderByMean genes b/f");
            MoreArray.printArray(cur.genes);*/
            double[] rowmeans = new double[cur.genes.length];
            for (int a = 0; a < cur.genes.length; a++) {
                double[] d = Matrix.extractRowSegment(data, cur.genes[a], cur.exps);
                //System.out.println("reorderByMean " + i+"\t"+a+"\t"+cur.genes[a]);
                //MoreArray.printArray(d);

                for (int z = 0; z < d.length; z++) {
                    if (Double.isNaN(d[z])) {
                        //System.out.println("reorderByMean replacing NaN " + i +"\t"+z+"\t"+d[z]);
                        d[z] = 0;
                    }
                }

                rowmeans[a] = stat.avg(d);
                //System.out.println("reorderByMean " + i + "\t" + a + "\t" + rowmeans[a]);
            }
            /* System.out.println("reorderByMean rowmeans");
            MoreArray.printArray(rowmeans);*/
            //double[] rowmeansorig = MoreArray.copy(rowmeans);
            //Arrays.sort(rowmeans);
            //int[] rowindex = MoreArray.crossIndex(rowmeans,rowmeansorig);

            int[] rowrank = stat.ranksDescUnique(rowmeans);
            ArrayList roworder = MoreArray.initArrayList(cur.genes.length);
            //place each gene index at its gene expression mean rank
            for (int a = 0; a < cur.genes.length; a++) {
                //System.out.println("roworder " + b + "\tpos " + rowrank[b] + "\tval " + cur.genes[b]);
                roworder.set(rowrank[a], cur.genes[a]);
            }
            int[] neworder = MoreArray.ArrayListtoInt(roworder);
            for (int a = 0; a < cur.genes.length; a++) {
                if (neworder[a] == 0)
                    System.out.println("cur.genes == 0 a/f a " + a + "\tcur.genes[a] " + cur.genes[a] +
                            "\tneworder[a] " + neworder[a] + "\trowrank[a] " +
                            rowrank[a] + "\trowmeans[a] " + rowmeans[a]);
            }
            cur.genes = neworder;

            double[] colmeans = new double[cur.exps.length];
            for (int b = 0; b < cur.exps.length; b++) {
                double[] d = Matrix.extractColumnSegment(data, cur.exps[b], cur.genes);
                colmeans[b] = stat.avg(d);
            }
            //MoreArray.printArray(colmeans);
            int[] colrank = stat.ranksDescUnique(colmeans);
            /*
            System.out.println("colmeans");
            MoreArray.printArray(colmeans);
            System.out.println("colrank");
            MoreArray.printArray(colrank);
            */

            ArrayList colorder = MoreArray.initArrayList(cur.exps.length);
            for (int b = 0; b < cur.exps.length; b++) {
                //System.out.println("colorder old pos " + b + "\tnew pos " + colrank[b] + "\tval " + cur.exps[b]);
                colorder.set(colrank[b], cur.exps[b]);
            }
            int[] newordere = MoreArray.ArrayListtoInt(colorder);
            for (int b = 0; b < cur.exps.length; b++) {
                if (newordere[b] == 0)
                    System.out.println("cur.exps == 0 a/f pos " + b + "\told val " + cur.exps[b] +
                            "\tnew val " + newordere[b] + "\told rank " +
                            colrank[b] + "\told mean " + colmeans[b]);
            }
            cur.exps = newordere;
            cur.setDataAndMean(data);
            vbl.set(i, cur);

            //if (i > 2)
            //    System.exit(0);
        }

        return vbl;
    }

    /**
     * @param gene_ind
     * @param exp_ind
     */
    public final static ValueBlockList translateIndices(int[] gene_ind, int[] exp_ind, ValueBlockList vbl) {
        for (int a = 0; a < vbl.size(); a++) {
            ValueBlock cur = (ValueBlock) vbl.get(a);
            for (int i = 0; i < cur.genes.length; i++) {
                try {
                    int ind = MoreArray.getArrayInd(gene_ind, cur.genes[i]);
                    cur.genes[i] = ind + 1;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            for (int j = 0; j < cur.exps.length; j++) {
                try {
                    int ind = MoreArray.getArrayInd(exp_ind, cur.exps[j]);
                    cur.exps[j] = ind + 1;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            vbl.set(a, cur);
        }
        return vbl;
    }

    /**
     *
     */
    public final static void writeBIC(String outf, ValueBlockList vbl) {
        ArrayList out = new ArrayList();
        for (int a = 0; a < vbl.size(); a++) {
            ValueBlock cur = (ValueBlock) vbl.get(a);
            out.add("" + cur.genes.length + " " + cur.exps.length);
            out.add(MoreArray.toString(cur.genes, " "));
            out.add(MoreArray.toString(cur.exps, " "));
        }

        System.out.println("writeBIC blocks " + out.size() / 3);
        TextFile.write(out, outf);
    }

    /**
     *
     */
    public final static void writeBIC(String outf, ValueBlockList vbl, int offset) {
        ArrayList out = new ArrayList();
        for (int a = 0; a < vbl.size(); a++) {
            ValueBlock cur = (ValueBlock) vbl.get(a);
            out.add("" + cur.genes.length + " " + cur.exps.length);
            cur.genes = stat.add(cur.genes, offset);
            cur.exps = stat.add(cur.exps, offset);

            out.add(MoreArray.toString(cur.genes, " "));
            out.add(MoreArray.toString(cur.exps, " "));
        }

        System.out.println("writeBIC blocks " + out.size() / 3);
        TextFile.write(out, outf);
    }

    /**
     * Returns index of last addition move.
     *
     * @param vbl
     * @return
     */
    public final static int lastBlockBeforeDeletion(ValueBlockList vbl) {
        int pos = -1;
        for (int a = vbl.size() - 1; a >= 0; a--) {
            ValueBlock cur = (ValueBlock) vbl.get(a);
            //System.out.println("lastBlockBeforeDeletion "+a+"\t"+cur.move_type);
            //if an addition move
            if (cur.move_type == 1 || cur.move_type == 3) {
                pos = a;
                break;
            }
        }
        return pos;
    }

    /**
     * n
     * Returns combined last g+e+ block
     *
     * @param vbl
     * @return
     */
    public final static ValueBlock combinelastGeneExpAdd(ValueBlockList vbl) {
        int g = ValueBlockListMethods.lastBlockGeneAddition(vbl);
        int e = ValueBlockListMethods.lastBlockExpAddition(vbl);

        int[] newgenes, newexps;
        ValueBlock vg = new ValueBlock(), ve = new ValueBlock();
        if (g != -1) {
            vg = (ValueBlock) vbl.get(g);
        }
        if (e != -1) {
            ve = (ValueBlock) vbl.get(e);
        }

        if (g != -1 && e != -1) {
            newgenes = vg.genes;
            newexps = ve.exps;
        } else if (g != -1) {
            newgenes = vg.genes;
            newexps = vg.exps;
        } else if (e != -1) {
            newgenes = ve.genes;
            newexps = ve.exps;
        } else {
            ValueBlock last = (ValueBlock) vbl.get(vbl.size() - 1);
            newgenes = last.genes;
            newexps = last.exps;
        }

        return new ValueBlock(newgenes, newexps);
    }

    /**
     * Returns index of last addition move.
     *
     * @param vbl
     * @return
     */
    public final static int lastBlockGeneAddition(ValueBlockList vbl) {
        int pos = -1;
        for (int a = vbl.size() - 1; a >= 0; a--) {
            ValueBlock cur = (ValueBlock) vbl.get(a);
            //System.out.println("lastBlockBeforeDeletion "+a+"\t"+cur.move_type);
            //if an addition move
            if (cur.move_type == 1) {
                pos = a;
                break;
            }
        }
        return pos;
    }


    /**
     * Returns index of last addition move.
     *
     * @param vbl
     * @return
     */
    public final static int lastBlockExpAddition(ValueBlockList vbl) {
        int pos = -1;
        for (int a = vbl.size() - 1; a >= 0; a--) {
            ValueBlock cur = (ValueBlock) vbl.get(a);
            //System.out.println("lastBlockBeforeDeletion "+a+"\t"+cur.move_type);
            //if an addition move
            if (cur.move_type == 3) {
                pos = a;
                break;
            }
        }
        return pos;
    }

    /**
     * Returns index of last addition move.
     *
     * @param vbl
     * @return
     */
    public final static int lastBlockGeneExpAddition(ValueBlockList vbl) {
        int geneadd = -1;
        int expadd = -1;
        for (int a = vbl.size() - 1; a >= 0; a--) {
            ValueBlock cur = (ValueBlock) vbl.get(a);
            //System.out.println("lastBlockBeforeDeletion "+a+"\t"+cur.move_type);
            //if an addition move
            if (cur.move_type == 1) {
                geneadd = a;
                if (expadd != -1)
                    break;
            } else if (cur.move_type == 3) {
                expadd = a;
                if (geneadd != -1)
                    break;
            }
        }
        return Math.min(geneadd, expadd);
    }


    /**
     * Distinguishes between a dir of ValueBlockList or single ValueBlockList file
     *
     * @return
     */
    public final static ValueBlockList[] getFirstandLast(String dirstr, String startstr, String targetcrit, String outstr) {

        ValueBlockList[] ret = new ValueBlockList[2];
        ret[0] = new ValueBlockList();
        ret[1] = new ValueBlockList();

        File test = new File(dirstr);
        ArrayList listar = null;
        if (test.isDirectory()) {
            if (targetcrit != null)
                listar = readDir(dirstr, targetcrit, outstr);
            else
                listar = MoreArray.convtoArrayList(test.list());
            for (int i = 0; i < listar.size(); i++) {
                if (i % 100 == 0)
                    System.out.print("\n");
                System.out.print(".");

                ValueBlockList vbl = null;
                try {
                    vbl = ValueBlockList.read((String) listar.get(i), false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                int ind = -1;
                /*find last gene addition block*/
                //ind = vbl.lastBlockGeneAddition();
                //ind = 0;
                ind = vbl.size() - 1;
                ValueBlock lastadd = (ValueBlock) vbl.get(ind == -1 ? vbl.size() - 1 : ind);
                if (ret[1].indexOf(lastadd) == -1)
                    ret[1].add(lastadd);
                else
                    System.out.println("Duplicate block last");

                ValueBlock start = (ValueBlock) vbl.get(0);
                if (ret[0].indexOf(start) == -1)
                    ret[0].add(start);
                else
                    System.out.println("Duplicate block start");
            }
        } else {
            try {
                ret[1] = ValueBlockList.read(dirstr, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (startstr != null)
                try {
                    ret[0] = ValueBlockList.read(startstr, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        return ret;
    }

    /**
     * @return
     */
    public final static ArrayList readDir(String dirstr, String targetcrit, String outstr) {

        ArrayList files = new ArrayList();
        File dir = new File(dirstr);
        String[] listtop = dir.list();
        ArrayList listar = new ArrayList();
        for (int i = 0; i < listtop.length; i++) {
            File f = new File(dirstr + "/" + listtop[i]);
            if (f.isDirectory()) {
                String[] l = f.list();
                for (int j = 0; j < l.length; j++) {
                    if (l[j].indexOf(targetcrit) != -1)
                        listar.add(dirstr + "/" + listtop[i] + "/" + l[j]);
                    files.add(l[j]);
                }
            }
        }

        if (outstr != null)
            TextFile.write(files, outstr + "_files.txt");
        return listar;
    }

    /**
     * @param vb
     * @param offset
     * @return
     */
    public final static ValueBlockList incrementIndices(ValueBlockList vb, int offset) {
        for (int i = 0; i < vb.size(); i++) {
            ValueBlock v = (ValueBlock) vb.get(i);
            v.genes = stat.add(v.genes, offset);
            v.exps = stat.add(v.exps, offset);
            vb.set(i, v);
        }
        return vb;
    }

    /**
     * @param vbl
     * @param cut
     * @return
     */
    public final static ValueBlockList applyThreshold(ValueBlockList vbl, double cut) {
        for (int i = 0; i < vbl.size(); i++) {
            ValueBlock m1 = (ValueBlock) vbl.get(i);
            if (m1.full_criterion < cut) {
                vbl.remove(i);
                i--;
            }
        }
        return vbl;
    }

    /**
     * @param vbl
     * @param cut
     * @return
     */
    public final static ValueBlockList applyThresholdGenes(ValueBlockList vbl, int cut) {
        for (int i = 0; i < vbl.size(); i++) {
            ValueBlock m1 = (ValueBlock) vbl.get(i);
            if (m1.genes.length > cut) {
                vbl.remove(i);
                i--;
            }
        }
        return vbl;
    }

    /**
     * @param vb
     */
    public final static ValueBlockList reverse(ValueBlockList vb) {
        ValueBlockList ret = new ValueBlockList(vb.size());
        for (int i = 0; i < vb.size(); i++) {
            ValueBlock v = (ValueBlock) vb.get(i);
            ret.set(vb.size() - 1 - i, v);
        }
        return ret;
    }


    /**
     * @param v
     * @return
     */
    public final static int countCooccur(int g1, int g2, ValueBlockList v) {
        int ret = 0;
        //System.out.println("countCooccur " + g1 + "\t" + g2);
        for (int i = 0; i < v.size(); i++) {
            ValueBlock test = (ValueBlock) v.get(i);
            int g1ind = 0;
            try {
                g1ind = MoreArray.getArrayInd(test.genes, g1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            int g2ind = 0;
            try {
                g2ind = MoreArray.getArrayInd(test.genes, g2);
            } catch (Exception e) {
                e.printStackTrace();
            }
           /* if (g1ind != -1)
                System.out.println("countCooccur g1ind " + i + "\t" + g1ind);
            if (g2ind != -1)
                System.out.println("countCooccur g2ind " + i + "\t" + g2ind);*/

            if (g1ind != -1 && g2ind != -1) {
                ret++;
                System.out.println("countCooccur ret " + i + "\t" + g1 + "\t" + g2 + "\t" + ret);
            }
        }

        return ret;
    }

    /**
     * @param v
     * @return
     */
    public final static double meanPartialCorrelation(int g1, int g2, ValueBlockList v) {
        ArrayList<Double> ar = new ArrayList<Double>();
        //System.out.println("countCooccur " + g1 + "\t" + g2);
        for (int i = 0; i < v.size(); i++) {
            ValueBlock test = (ValueBlock) v.get(i);
            int g1ind = 0;
            try {
                g1ind = MoreArray.getArrayInd(test.genes, g1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            int g2ind = 0;
            try {
                g2ind = MoreArray.getArrayInd(test.genes, g2);
            } catch (Exception e) {
                e.printStackTrace();
            }
           /* if (g1ind != -1)
                System.out.println("countCooccur g1ind " + i + "\t" + g1ind);
            if (g2ind != -1)
                System.out.println("countCooccur g2ind " + i + "\t" + g2ind);*/

            if (g1ind != -1 && g2ind != -1) {
                double cor = stat.correlationCoeff(test.exp_data[g1ind], test.exp_data[g2ind]);
                ar.add(cor);
                //System.out.println("countCooccur ret " + i + "\t" + g1 + "\t" + g2 + "\t" + ret);
            }
        }

        return stat.avg(MoreArray.ArrayListtoDouble(ar));
    }

    /**
     * @param v
     * @return
     */
    public final static double meanCritCooccur(int g1, int g2, ValueBlockList v) {
        int ret = 0;
        //System.out.println("countCooccur " + g1 + "\t" + g2);
        ArrayList<Double> al = new ArrayList<Double>();
        for (int i = 0; i < v.size(); i++) {
            ValueBlock test = (ValueBlock) v.get(i);
            int g1ind = 0;
            try {
                g1ind = MoreArray.getArrayInd(test.genes, g1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            int g2ind = 0;
            try {
                g2ind = MoreArray.getArrayInd(test.genes, g2);
            } catch (Exception e) {
                e.printStackTrace();
            }
           /* if (g1ind != -1)
                System.out.println("countCooccur g1ind " + i + "\t" + g1ind);
            if (g2ind != -1)
                System.out.println("countCooccur g2ind " + i + "\t" + g2ind);*/

            if (g1ind != -1 && g2ind != -1) {
                //ret++;
                al.add(test.full_criterion);
                //System.out.println("meanCritCooccur ret " + i + "\t" + g1 + "\t" + g2 + "\t" + test.full_criterion);
            }
        }
        double[] data = MoreArray.ArrayListtoDouble(al);
        return stat.avg(data);
    }

    /**
     * @param vbl
     * @return
     */
    public final static ValueBlockList makeNRGeneExp(ValueBlockList vbl) {
        for (int i = 0; i < vbl.size(); i++) {
            ValueBlock v = (ValueBlock) vbl.get(i);

            Set set1 = new HashSet(Arrays.asList(MoreArray.toStringArray(v.genes)));
            int[] g = MoreArray.tointArray((String[]) set1.toArray(new String[0]));

            Set set2 = new HashSet(Arrays.asList(MoreArray.toStringArray(v.exps)));
            int[] e = MoreArray.tointArray((String[]) set2.toArray(new String[0]));

            if (g.length != v.genes.length || e.length != v.exps.length) {
                v = BlockMethods.makeGeneExps(v, g, e);
                vbl.set(i, v);
            }


        }
        return vbl;
    }

    /**
     * @param label
     */
    public final static ValueBlockList sort(String label, ValueBlockList vbl) {
        ValueBlockList tmp = new ValueBlockList();//ValueBlockList.copyArrayList(this);
        //new ValueBlockList();
        boolean done = false;
        //sort by block_area descending
        if (label.equals("area")) {
            //for all blocks
            for (int i = 0; i < vbl.size(); i++) {
                ValueBlock cur = (ValueBlock) vbl.get(i);
                //if first pass
                if (tmp.size() == 0) {
                    //System.out.println("sort 1 " + tmp.vbl.size() + "\t" + cur.block_area);
                    tmp.add(0, cur);
                } else {
                    boolean added = false;
                    //for all sorted
                    for (int j = 0; j < tmp.size(); j++) {
                        ValueBlock v = (ValueBlock) tmp.get(j);
                        if (v.block_area < cur.block_area) {
                            //System.out.println("sort   " + tmp.vbl.size() + "\t" + cur.block_area);
                            tmp.add(j, cur);
                            added = true;
                            break;
                        }
                    }
                    if (!added) {
                        tmp.add(cur);
                    }
                }
            }
            //System.out.println("sort this " + ((ValueBlock) vbl.get(0)).toStringShort());
            //System.out.println("sort tmp " + ((ValueBlock) tmp.vbl.get(0)).toStringShort());
            done = true;
        }
        //sort by full crit descending
        else if (label.equals("full")) {
            for (int i = 0; i < vbl.size(); i++) {
                ValueBlock cur = (ValueBlock) vbl.get(i);
                if (tmp.size() == 0) {
                    tmp.add(0, cur);
                } else {
                    boolean added = false;
                    for (int j = 0; j < tmp.size(); j++) {
                        ValueBlock v = (ValueBlock) tmp.get(j);
                        if (v.full_criterion < cur.full_criterion) {
                            tmp.add(j, cur);
                            added = true;
                            break;
                        }
                    }
                    if (!added) {
                        tmp.add(cur);
                    }
                }
            }
            done = true;
        }
        //Rauf's addition
        //sort by absolute value of exp_mean descending
        else if (label.equals("exp_mean")) {
            for (int i = 0; i < vbl.size(); i++) {
                ValueBlock cur = (ValueBlock) vbl.get(i);
                if (tmp.size() == 0) {
                    tmp.add(0, cur);
                } else {
                    boolean added = false;
                    for (int j = 0; j < tmp.size(); j++) {
                        ValueBlock v = (ValueBlock) tmp.get(j);
                        if (Math.abs(v.exp_mean) < Math.abs(cur.exp_mean)) {
                            tmp.add(j, cur);
                            added = true;
                            break;
                        }
                    }
                    if (!added) {
                        tmp.add(cur);
                    }
                }
            }
            done = true;
        }
        if (done) {
            for (int i = 0; i < tmp.size(); i++) {
                ValueBlock v = (ValueBlock) tmp.get(i);
                vbl.set(i, v);
            }
        }

        return vbl;
    }

    /**
     * @param label
     */
    public final static ArrayList sort(String label, ArrayList alist, ValueBlockList vbl) {
        ValueBlockList tmp = new ValueBlockList();
        ArrayList newalist = new ArrayList();
        boolean done = false;
        //sort by block_area descending
        if (label.equals("area")) {
            //for all blocks
            for (int i = 0; i < vbl.size(); i++) {
                ValueBlock cur = (ValueBlock) vbl.get(i);
                if (cur != null) {
                    ValueBlockList o = (ValueBlockList) alist.get(i);
                    //if first pass
                    if (tmp.size() == 0) {
                        //System.out.println("sort 1 " + tmp.vbl.size() + "\t" + cur.block_area);
                        tmp.add(0, cur);
                        newalist.add(0, o);
                    } else {
                        boolean added = false;
                        //for all sorted
                        for (int j = 0; j < tmp.size(); j++) {
                            ValueBlock v = (ValueBlock) tmp.get(j);
                            if (v != null) {
                                if (v.block_area < cur.block_area) {
                                    //System.out.println("sort   " + tmp.vbl.size() + "\t" + cur.block_area);
                                    tmp.add(j, cur);
                                    newalist.add(j, o);
                                    added = true;
                                    break;
                                }
                            }
                        }
                        if (!added) {
                            tmp.add(cur);
                            if (o == null) {
                                System.out.println("sort vbl is null " + i);
                            }
                            newalist.add(o);
                        }
                    }
                }
            }
            //System.out.println("sort this " + ((ValueBlock) vbl.get(0)).toStringShort());
            //System.out.println("sort tmp " + ((ValueBlock) tmp.vbl.get(0)).toStringShort());
            done = true;
        }
        //sort by full crit descending
        else if (label.equals("full")) {
            for (int i = 0; i < vbl.size(); i++) {
                ValueBlock cur = (ValueBlock) vbl.get(i);
                ValueBlockList o = (ValueBlockList) alist.get(i);
                if (tmp.size() == 0) {
                    tmp.add(0, cur);
                    newalist.add(0, o);
                } else {
                    boolean added = false;
                    for (int j = 0; j < tmp.size(); j++) {
                        ValueBlock v = (ValueBlock) tmp.get(j);
                        if (v.full_criterion < cur.full_criterion) {
                            tmp.add(j, cur);
                            newalist.add(j, o);
                            added = true;
                            break;
                        }
                    }
                    if (!added) {
                        tmp.add(cur);
                        newalist.add(o);
                    }
                }
            }
            done = true;
        }

        if (done) {
            System.out.println("tmp.vbl.size() " + tmp.size() + "\tvbl.size " + vbl.size());
            for (int i = 0; i < tmp.size(); i++) {
                ValueBlock v = (ValueBlock) tmp.get(i);
                vbl.set(i, v);
            }
            //pad with null entries ignored during sorting
            System.out.println("sort " + vbl.size() + "\t" + alist.size() + "\t" + newalist.size());
            if (tmp.size() < vbl.size()) {
                for (int i = tmp.size(); i < vbl.size(); i++) {
                    vbl.set(i, null);
                    newalist.add(null);
                }
            }
            System.out.println("sort " + vbl.size() + "\t" + alist.size() + "\t" + newalist.size());
        }

        return newalist;
    }

    /**
     * @param label
     */
    public final static ArrayList sortStr(String label, ArrayList alist, ValueBlockList vbl) {
        ValueBlockList tmp = new ValueBlockList();
        ArrayList newalist = new ArrayList();
        boolean done = false;
        //sort by block_area descending
        if (label.equals("area")) {
            //for all blocks
            for (int i = 0; i < vbl.size(); i++) {
                ValueBlock cur = (ValueBlock) vbl.get(i);
                if (cur != null) {
                    String o = (String) alist.get(i);
                    //if first pass
                    if (tmp.size() == 0) {
                        //System.out.println("sort 1 " + tmp.vbl.size() + "\t" + cur.block_area);
                        tmp.add(0, cur);
                        newalist.add(0, o);
                    } else {
                        boolean added = false;
                        //for all sorted
                        for (int j = 0; j < tmp.size(); j++) {
                            ValueBlock v = (ValueBlock) tmp.get(j);
                            if (v != null) {
                                if (v.block_area < cur.block_area) {
                                    //System.out.println("sort   " + tmp.vbl.size() + "\t" + cur.block_area);
                                    tmp.add(j, cur);
                                    newalist.add(j, o);
                                    added = true;
                                    break;
                                }
                            }
                        }
                        if (!added) {
                            tmp.add(cur);
                            if (o == null) {
                                System.out.println("sort vbl is null " + i);
                            }
                            newalist.add(o);
                        }
                    }
                }
            }
            //System.out.println("sort this " + ((ValueBlock) vbl.get(0)).toStringShort());
            //System.out.println("sort tmp " + ((ValueBlock) tmp.vbl.get(0)).toStringShort());
            done = true;
        }
        //sort by full crit descending
        else if (label.equals("full")) {
            for (int i = 0; i < vbl.size(); i++) {
                ValueBlock cur = (ValueBlock) vbl.get(i);
                String o = (String) alist.get(i);
                if (tmp.size() == 0) {
                    tmp.add(0, cur);
                    newalist.add(0, o);
                } else {
                    boolean added = false;
                    for (int j = 0; j < tmp.size(); j++) {
                        ValueBlock v = (ValueBlock) tmp.get(j);
                        if (v.full_criterion < cur.full_criterion) {
                            tmp.add(j, cur);
                            newalist.add(j, o);
                            added = true;
                            break;
                        }
                    }
                    if (!added) {
                        tmp.add(cur);
                        newalist.add(o);
                    }
                }
            }
            done = true;
        }

        if (done) {
            System.out.println("tmp.vbl.size() " + tmp.size() + "\tvbl.size " + vbl.size());
            for (int i = 0; i < tmp.size(); i++) {
                ValueBlock v = (ValueBlock) tmp.get(i);
                vbl.set(i, v);
            }
            //pad with null entries ignored during sorting
            System.out.println("sort " + vbl.size() + "\t" + alist.size() + "\t" + newalist.size());
            if (tmp.size() < vbl.size()) {
                for (int i = tmp.size(); i < vbl.size(); i++) {
                    vbl.set(i, null);
                    newalist.add(null);
                }
            }
            System.out.println("sort " + vbl.size() + "\t" + alist.size() + "\t" + newalist.size());
        }

        return newalist;
    }

    /**
     * @param label
     */
    public final static ArrayList sort(String label, ArrayList setslist, ArrayList labellist, ArrayList mergelist, ArrayList mergehash, ValueBlockList vbl) {
        ValueBlockList tmp = new ValueBlockList();
        ArrayList newalist = new ArrayList();
        ArrayList newblist = new ArrayList();
        ArrayList newclist = new ArrayList();
        ArrayList newdlist = new ArrayList();
        boolean done = false;
        int origsize = vbl.size();
        int origsize2 = setslist.size();
        int origsize3 = labellist.size();
        int origsize4 = 0;
        int origsize5 = 0;
        if (mergelist != null) {
            origsize4 = mergelist.size();
            origsize5 = mergehash.size();
        }

        //sort by block_area descending
        if (label.equals("area")) {
            //for all blocks
            for (int i = 0; i < vbl.size(); i++) {
                ValueBlock cur = (ValueBlock) vbl.get(i);
                if (cur != null) {
                    ValueBlockList vbl2 = (ValueBlockList) setslist.get(i);
                    Object o = (Object) labellist.get(i);
                    Object o2 = null;
                    Object vb = null;
                    if (mergelist != null) {
                        o2 = (Object) mergehash.get(i);
                        vb = (Object) mergelist.get(i);
                    }
                    //if first pass, add first block
                    if (tmp.size() == 0) {
                        //System.out.println("sort 1 " + tmp.vbl.size() + "\t" + cur.block_area);
                        tmp.add(0, cur);
                        newalist.add(0, vbl2);
                        newblist.add(0, o);
                        if (mergelist != null) {
                            newclist.add(0, vb);
                            newdlist.add(0, o2);
                        }

                        System.out.println("sort " + i + "\t" + vbl.size() + "\t" + o);
                    } else {
                        boolean added = false;
                        //for all sorted
                        for (int j = 0; j < tmp.size(); j++) {
                            ValueBlock v = (ValueBlock) tmp.get(j);
                            if (v != null) {
                                //add block at position where first smaller area
                                if (v.block_area < cur.block_area) {
                                    //System.out.println("sort   " + tmp.vbl.size() + "\t" + cur.block_area);
                                    tmp.add(j, cur);
                                    newalist.add(j, vbl2);
                                    newblist.add(j, o);
                                    if (mergelist != null) {
                                        newclist.add(j, vb);
                                        newdlist.add(j, o2);
                                    }

                                    System.out.println("sort " + i + "\t" + vbl.size() + "\t" + o);
                                    added = true;
                                    break;
                                }
                            }
                        }
                        //if no area is larger, then add to end
                        if (!added) {

                            tmp.add(cur);
                            if (vbl2 == null)
                                System.out.println("sort vbl is null " + i);
                            if (o == null)
                                System.out.println("sort o is null " + i);

                            newalist.add(vbl2);
                            newblist.add(o);
                            if (mergelist != null) {
                                newclist.add(vb);
                                newdlist.add(o2);
                            }
                            System.out.println("sort " + i + "\t" + vbl.size() + "\t" + o);
                        }
                    }
                }
            }
            //System.out.println("sort this " + ((ValueBlock) vbl.get(0)).toStringShort());
            //System.out.println("sort tmp " + ((ValueBlock) tmp.vbl.get(0)).toStringShort());
            done = true;
        }

        if (done) {
            System.out.println("tmp.vbl.size() " + tmp.size() + "\tvbl.size " + vbl.size() + "\tnewalist " + newalist.size() + "\tnewblist " + newblist.size());
            for (int i = 0; i < tmp.size(); i++) {
                ValueBlock v = (ValueBlock) tmp.get(i);
                vbl.set(i, v);
            }
            //pad with null entries ignored during sorting
            System.out.println("sort: list " + vbl.size() + "\t" + tmp.size() +
                    "\ta " + setslist.size() + "\t" + newalist.size() + "\tb " + labellist.size() + "\t" + newblist.size());
            if (tmp.size() < vbl.size()) {
                for (int i = tmp.size(); i < vbl.size(); i++) {
                    vbl.set(i, null);
                    newalist.add(null);
                    newblist.add(null);
                    newclist.add(null);
                    newdlist.add(null);
                }
            }
            System.out.println("af sort: vbl.size " + vbl.size() + "\tnewalist " + newalist.size() +
                    "\tnewblist " + newblist.size() + "\tnewclist " + newclist.size()
                    + "\t" + origsize + "\t" + origsize2 + "\t" + origsize3 + "\t" + origsize4 + "\t" + origsize5);
        }

        ArrayList ret = new ArrayList();
        ret.add(newalist);
        ret.add(newblist);
        ret.add(newclist);
        ret.add(newdlist);
        ret.add(vbl);
        return ret;
    }


    /**
     * TODO update overlap to account for area OR exps and genes separately
     * TODO require both gene and exp overlap above threshold, not just sum?
     *
     * @return
     */
    public final static double overlap(int[][] coords, ValueBlockList vbl) {
        double max = 0;
        for (int i = 0; i < vbl.size(); i++) {
            double count = 0;
            ValueBlock v = (ValueBlock) vbl.get(i);
            for (int a = 0; a < coords[0].length; a++) {
                for (int b = 0; b < v.genes.length; b++) {
                    if (a == b)
                        count++;
                }
            }
            for (int a = 0; a < coords[1].length; a++) {
                for (int b = 0; b < v.exps.length; b++) {
                    if (a == b)
                        count++;
                }
            }
            double test = count / (double) (coords[0].length + coords[1].length);
            if (test > max)
                max = test;
        }

        return max;
    }
}