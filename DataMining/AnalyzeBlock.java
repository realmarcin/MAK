package DataMining;

import bioobj.YeastRegulons;
import util.MoreArray;

/**
 * User: marcin
 * Date: May 18, 2009
 * Time: 5:37:38 PM
 */
public class AnalyzeBlock {

    YeastRegulons yr;

    /**
     * @param r
     * @param y
     * @param m
     */
    public AnalyzeBlock(String r, String[] y, String m) {
        yr = new YeastRegulons(r, y, m);
        for (int i = 0; i < yr.factors.length; i++) {
            System.out.println("AnalyzeRegulonShaving " + i + "\t" + yr.factors[i] + "\t" + yr.significant[i].size());
        }

    }

    /**
     * @param vbl
     */
    public final static double[] analyzeBlocktoFirst(ValueBlockList vbl) {
        double[] add = null;
        if (vbl != null) {
            ValueBlock first = (ValueBlock) vbl.get(0);
            ValueBlock last = (ValueBlock) vbl.get(vbl.size() - 1);
            int firstcount = first.genes.length;
            int lastcount = 0;

            for (int m = 0; m < last.genes.length; m++) {
                try {
                    int index_in_regulon = MoreArray.getArrayInd(first.genes, last.genes[m]);
                    if (index_in_regulon != -1)
                        lastcount++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            double ratio1 = (double) firstcount / (double) firstcount;
            double ratio2 = (double) lastcount / (double) firstcount;

            double[] d = {
                    first.genes.length,
                    lastcount,
                    -1,
                    ratio1,
                    ratio2,
                    first.genes.length,
                    last.genes.length,
                    (double) last.genes.length / (double) firstcount,
                    first.exps.length,
                    last.exps.length,
                    (double) last.exps.length / (double) first.exps.length,
                    last.percentOrigGenes,
                    last.percentOrigExp,
                    first.pre_criterion,
                    last.pre_criterion,
                    vbl.size()
            };

            add = d;
        }

        return add;
    }

    /**
     * @param ref
     * @param vbl
     */
    public final static double[] analyzeBlockBasicTests(ValueBlock ref, ValueBlockList vbl, String type, int index) {
        double[] add = null;
        if (vbl != null) {
            ValueBlock first = (ValueBlock) vbl.get(0);
            ValueBlock last = (ValueBlock) vbl.get(vbl.size() - 1);
            int firstcount = 0;
            int lastcount = 0;
            for (int m = 0; m < first.genes.length; m++) {
                int s = first.genes[m];
                //System.out.println("analyzeBlocktoFirst " + s);
                try {
                    int index_in = MoreArray.getArrayInd(ref.genes, s);
                    if (index_in != -1) {
                        firstcount++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            for (int m = 0; m < last.genes.length; m++) {
                int s = last.genes[m];
                try {
                    int index_in = MoreArray.getArrayInd(ref.genes, s);
                    if (index_in != -1)
                        lastcount++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            int size = ref.genes.length;
            double ratio1 = (double) firstcount / (double) size;
            double ratio2 = (double) lastcount / (double) size;

            boolean passed = false;
            boolean passed_final = false;
            if (index != -1 && type != null) {
                for (int i = 0; i < vbl.size(); i++) {
                    ValueBlock cur = (ValueBlock) vbl.get(i);
                    passed = testBlock(cur, type, index);
                    if (passed)
                        break;
                }
                passed_final = testBlock(last, type, index);
            }
            /*
            "criterion\t

            first ref. count\t
            last ref. count\t
            regulon_size\t" +
            "first ref. ratio\t
            last ref. ratio\t
            num genes first\t
            num genes last\t
            percent num genes last\t" +
            "num exps first\t
            num exps last\t
            percent exp last\t
            last.percentOrigGenes\t" +
            "last.percentOrigExp\t
            first.precrit\t
            last.precrit\t
            number moves\t
            passed\t
            passed_final\t" +
            18
            "F1g\t
            precisiong\t
            recallg\t
            F1e\t
            precisione\t
            recalle\t
            F1recallge\t
            runtime";
            8
            total 27
            */
            double[] d = {
                    firstcount,
                    lastcount,
                    size,
                    ratio1,
                    ratio2,
                    first.genes.length,
                    last.genes.length,
                    (double) last.genes.length / (double) ref.genes.length,
                    first.exps.length,
                    last.exps.length,
                    (double) last.exps.length / (double) ref.exps.length,
                    last.percentOrigGenes,
                    last.percentOrigExp,
                    first.pre_criterion,
                    last.pre_criterion,
                    vbl.size(),
                    (passed ? 1 : 0),
                    (passed_final ? 1 : 0),
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0
            };

            add = d;
        } else {
            double[] d = {
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0
            };
            add = d;
        }
        return add;
    }

    /**
     * @param cur
     * @param type
     * @param index
     */
    private final static boolean testBlock(ValueBlock cur, String type, int index) {
        boolean passed = false;
        try {
            if (type.equals("drift")) {
                passed = false;
            } else if (type.equals("geneadd")) {
                int index_in_block = MoreArray.getArrayInd(cur.genes, index);
                if (index_in_block == -1) {
                    passed = true;
                }
            } else if (type.equals("genedel")) {
                int index_in_block = MoreArray.getArrayInd(cur.genes, index);
                if (index_in_block != -1) {
                    passed = true;
                }
            } else if (type.equals("expadd")) {
                int index_in_block = MoreArray.getArrayInd(cur.exps, index);
                if (index_in_block == -1) {
                    passed = true;
                }
            } else if (type.equals("expdel")) {
                int index_in_block = MoreArray.getArrayInd(cur.exps, index);
                if (index_in_block != -1) {
                    passed = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return passed;
    }

    /**
     * @param cur_factor
     * @param vbl
     */
    public double[] analyzeBlock(int cur_factor, ValueBlockList vbl) {
        double[] add = null;
        if (vbl != null) {
            ValueBlock first = (ValueBlock) vbl.get(0);
            ValueBlock last = (ValueBlock) vbl.get(vbl.size() - 1);
            int firstcount = 0;
            int lastcount = 0;
            for (int m = 0; m < first.genes.length; m++) {
                int s = first.genes[m];
                //System.out.println("analyzeBlocktoFirst " + s);
                int index_in_regulon = MoreArray.getArrayInd(yr.significant_int[cur_factor], s);
                if (index_in_regulon != -1) {
                    firstcount++;
                }
            }
            for (int m = 0; m < last.genes.length; m++) {
                int s = last.genes[m];
                int index_in_regulon = MoreArray.getArrayInd(yr.significant_int[cur_factor], s);
                if (index_in_regulon != -1)
                    lastcount++;
            }
            int regulon_size = yr.significant[cur_factor].size();
            double ratio1 = (double) firstcount / (double) regulon_size;
            double ratio2 = (double) lastcount / (double) regulon_size;

            double[] d = {
                    firstcount,
                    lastcount,
                    regulon_size,
                    ratio1,
                    ratio2,
                    first.genes.length,
                    last.genes.length,
                    (double) last.genes.length / (double) regulon_size,
                    first.exps.length,
                    last.exps.length,
                    (double) last.exps.length / (double) first.exps.length,
                    last.percentOrigGenes,
                    last.percentOrigExp,
                    first.pre_criterion,
                    last.pre_criterion,
                    vbl.size()
            };

            add = d;
        }

        return add;
    }


    /**
     * @param cur_factor
     * @param vbl
     */
    public double[] analyzeBlockUnique(int cur_factor, ValueBlockList vbl) {
        double[] add = null;
        if (vbl != null) {
            ValueBlock first = (ValueBlock) vbl.get(0);
            ValueBlock last = (ValueBlock) vbl.get(vbl.size() - 1);
            int firstcount = 0;
            int lastcount = 0;
            for (int m = 0; m < first.genes.length; m++) {
                int s = first.genes[m];
                //System.out.println("analyzeBlocktoFirst " + s);
                int index_in_regulon = MoreArray.getArrayInd(yr.significant_unique_int[cur_factor], s);
                if (index_in_regulon != -1) {
                    firstcount++;
                }
            }
            for (int m = 0; m < last.genes.length; m++) {
                int s = last.genes[m];
                int index_in_regulon = MoreArray.getArrayInd(yr.significant_unique_int[cur_factor], s);
                if (index_in_regulon != -1)
                    lastcount++;
            }
            int regulon_size = yr.significant_unique[cur_factor].size();
            double ratio1 = (double) firstcount / (double) regulon_size;
            double ratio2 = (double) lastcount / (double) regulon_size;

            double[] d = {
                    firstcount,
                    lastcount,
                    regulon_size,
                    ratio1,
                    ratio2,
                    first.genes.length,
                    last.genes.length,
                    (double) last.genes.length / (double) regulon_size,
                    first.exps.length,
                    last.exps.length,
                    (double) last.exps.length / (double) first.exps.length,
                    last.percentOrigGenes,
                    last.percentOrigExp,
                    first.pre_criterion,
                    last.pre_criterion,
                    vbl.size()
            };
            add = d;
        }
        return add;
    }


    /**
     * @param ref
     * @param traj
     * @return
     */
    public final static int findMaxGeneExperimentF1(ValueBlock ref, ValueBlockList traj) {
        int ind = -1;
        double max = 0;
        for (int m = 0; m < traj.size(); m++) {
            ValueBlock cur = (ValueBlock) traj.get(m);
            double pnum = BlockMethods.computeBlockOverlapGeneAndExpWithRef(ref, cur);
            double p = (pnum) / (double) (ref.genes.length + ref.exps.length);
            double r = (pnum) / (double) (cur.genes.length + cur.exps.length);
            double F1 = 2 * (p * r) / (p + r);
            if (F1 > max) {
                max = F1;
                ind = m;
            }
        }
        return ind;
    }
}
