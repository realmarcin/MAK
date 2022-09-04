package DataMining;

import mathy.Matrix;
import mathy.stat;
import util.MoreArray;
import util.StringUtil;
import util.TabFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * User: marcinjoachimiak
 * Date: Aug 1, 2007
 * Time: 2:38:47 PM
 */
public class ValueBlockPre {// implements Comparable<ValueBlockPre>

    /* Pre criterion value for this block, can be any of criteria */
    public double pre_criterion = Double.NaN;
    public double[] all_precriteria;

    /*double expr_criterion = Double.NaN;
    double expr_criterion2 = Double.NaN;
    double inter_criterion = 0;*/

    public int[] genes, exps;
    public int trajectory_position = 0;
    public int index = -1;

    /* way to access the previous block */
    public int prev_block = -1;

    /*
    History variables specifies type of move: experiment, gene, add, subtract
    move_type, move_item
    0=-g
    1=+g
    2=-e
    3=+e*/
    public int move_type = -1;

    /* Index of exp or gene involved in the move */
    public int changed = -1;

    public double percentOrigGenes = Double.NaN;
    public double percentOrigExp = Double.NaN;

    public double exp_mean = Double.NaN;
    public double signed_exp_mean = Double.NaN;
    public int signed_exp_mean_row_pos;
    public int signed_exp_mean_row_neg;
    public int signed_exp_mean_col_pos;
    public int signed_exp_mean_col_neg;
    public double[][] exp_data;

    public boolean computedOverlap = false;
    //boolean data_already_set = false;

    public int block_area = -1;

    public String move_class = null;

    public boolean majorityRowPlus = true;
    public boolean majorityColPlus = true;

    public int iteration = -1;


    /**
     *
     */
    public ValueBlockPre() {
        init();
    }

    /**
     *
     */
    public ValueBlockPre(int g, int e) {
        init();

        genes = MoreArray.initArray(g, 1);
        exps = MoreArray.initArray(e, 1);
    }

    /**
     *
     */
    private void init() {
        pre_criterion = Double.NaN;
        all_precriteria = new double[ValueBlock_STATIC.NUM_CRIT];
        /* expr_criterion = Double.NaN;
      expr_criterion2 = Double.NaN;
      inter_criterion = 0;*/
        genes = null;
        exps = null;
        trajectory_position = 0;
        index = -1;
        prev_block = -1;
        move_type = -1;
        changed = -1;
        percentOrigGenes = Double.NaN;
        percentOrigExp = Double.NaN;
        exp_mean = Double.NaN;
        signed_exp_mean = Double.NaN;
        exp_data = null;
        computedOverlap = false;
        block_area = -1;
        move_class = MINER_STATIC.MOVE_CLASSES[0];
        iteration = -1;
    }

    /**
     * @param crit
     * @param coord
     * @param move
     * @param mc
     */
    public ValueBlockPre(double crit, int[][] coord, int move, String mc) {
        pre_criterion = crit;
        genes = MoreArray.removeWhereEqual(coord[0], MoreArray.initArray(coord[0].length, 0));
        exps = MoreArray.removeWhereEqual(coord[1], MoreArray.initArray(coord[1].length, 0));
        updateArea();
        //genesStr = MoreArray.toString(genes, ",");
        //expsStr = MoreArray.toString(exps, ",");
        //block_id = genesStr + "/" + expsStr;
        move_type = move;
        move_class = mc;
        iteration = -1;
    }

    /**
     * @param a
     * @param mc
     */
    public ValueBlockPre(ArrayList[] a, String mc) {
        genes = MoreArray.ArrayListtoInt(a[0]);
        exps = MoreArray.ArrayListtoInt(a[1]);
        updateArea();
        move_class = mc;
        iteration = -1;
    }

    /**
     * @param vb
     */
    public ValueBlockPre(ValueBlockPre vb) {
        pre_criterion = vb.pre_criterion;
        all_precriteria = vb.all_precriteria;
        block_area = vb.block_area;
        genes = vb.genes;
        exps = vb.exps;
        prev_block = vb.prev_block;
        move_type = vb.move_type;
        changed = vb.changed;
        trajectory_position = vb.trajectory_position;
        move_class = vb.move_class;
        exp_data = vb.exp_data;
        iteration = vb.iteration;
    }

    /**
     * Special constructor:
     * apply move and item to initial data
     * Update pre_criterion and coordinates
     *
     * @param vb
     */
    public ValueBlockPre(ValueBlockPre vb, int move) {
        pre_criterion = vb.pre_criterion;
        all_precriteria = vb.all_precriteria;
        block_area = vb.block_area;
        genes = vb.genes;
        exps = vb.exps;
        prev_block = vb.prev_block;
        move_type = move;
        changed = vb.changed;
        trajectory_position = vb.trajectory_position;
        move_class = vb.move_class;
        iteration = vb.iteration;
    }

    /**
     * @param a
     */
    public void update(ArrayList[] a) {
        genes = MoreArray.ArrayListtoInt(a[0]);
        exps = MoreArray.ArrayListtoInt(a[1]);
        updateArea();
    }

    /**
     *
     */
    public void updateArea() {
        block_area = genes.length * exps.length;
    }

    /**
     *
     */
    public void update(double[][] expr_data) {
        updateArea();
        setDataAndMean(expr_data);

        assignSign(expr_data);
    }

    /**
     *
     */
    public void assignSign(double[][] expr_data) {
        majorityRowPlus = assignSignRow(expr_data);
        majorityColPlus = assignSignCol(expr_data);
    }

    /**
     * @param expr_data
     * @return
     */
    public final static boolean assignSignCol(double[][] expr_data) {
        int colneg = 0;
        for (int j = 0; j < expr_data[0].length; j++) {
            double mean = stat.avg(Matrix.extractColumn(expr_data, j + 1));
            if (mean < 0)
                colneg++;
        }
        if (colneg <= 0.5 * (double) expr_data[0].length)
            return true;
        return false;
    }

    /**
     * @param expr_data
     * @return
     */
    public final static boolean assignSignRow(double[][] expr_data) {
        int rowneg = 0;
        for (int i = 0; i < expr_data.length; i++) {
            double mean = stat.avg(expr_data[i]);
            if (mean < 0)
                rowneg++;
        }

        if (rowneg <= 0.5 * (double) expr_data.length)
            return true;
        return false;
    }


    /**
     * @param int_coords
     */
    public void NRCoords(int[][] int_coords) {

        genes = MoreArray.removeWhereEqual(int_coords[0], MoreArray.initArray(int_coords[0].length, 0));
        exps = MoreArray.removeWhereEqual(int_coords[1], MoreArray.initArray(int_coords[1].length, 0));
        updateArea();
    }

    /**
     * @param int_coords
     */
    public void NRGenes(int[] int_coords) {
        genes = MoreArray.removeWhereEqual(int_coords, MoreArray.initArray(int_coords.length, 0));
        if (exps != null)
            updateArea();
    }

    /**
     * @param int_coords
     */
    public void NRExps(int[] int_coords) {
        exps = MoreArray.removeWhereEqual(int_coords, MoreArray.initArray(int_coords.length, 0));
        if (genes != null)
            updateArea();
    }


    /**
     * Returns a file-friendly label for this block.
     *
     * @return
     */
    public String blockLabel(String block_id) {
        String s = StringUtil.replace(block_id, ",", "_");
        s = StringUtil.replace(s, "/", "-");
        return s;
    }


    /**
     * @param ref
     * @param test
     */
    public ValueBlockPre updateOverlap(ValueBlockPre ref, ValueBlockPre test) {
        test = BlockMethods.computeBlockOverlapWithRef(ref, test);
        if (Double.isNaN(test.percentOrigExp) || Double.isNaN(test.percentOrigGenes))
            System.out.println("updateOverlap NaN percentOrigExp:" + test.percentOrigExp +
                    "\tpercentOrigGenes " + test.percentOrigGenes);
        computedOverlap = true;
        return test;
    }


    /**
     * @param vb
     * @return
     */
    public final static double[][] getData(ValueBlockPre vb, double[][] data) {

        double[][] subset = getDataCore(vb.genes, vb.exps, data);
        return subset;
    }

    /**
     * @param genes
     * @param exps
     * @param data
     * @return
     */
    public static double[][] getDataCore(int[] genes, int[] exps, double[][] data) {
        double[][] subset = new double[genes.length][exps.length];
        for (int a = 0; a < genes.length; a++) {
            int Aint = genes[a];
            //expsStr
            for (int b = 0; b < exps.length; b++) {
                int Bint = exps[b];
                //block id indices are offset +1 (Java vs. R)
                int curgene = Aint - 1;
                int curexp = Bint - 1;
                //System.out.println("getData " + curgene + "\t" + curexp+"\t"+data[curgene][curexp]);
                try {
                    subset[a][b] = data[curgene][curexp];
                } catch (Exception e) {
                    System.out.println("Exception getData " + curgene + "\t" + curexp +
                            "\tAint Bint " + Aint + "\t" + Bint + "\ta b " + a + "\t" + b +
                            "\textents " + data.length + "\t" + data[0].length);
                    //MoreArray.printArray(vb.genes);
                    System.out.println("g " + MoreArray.toString(genes, ","));
                    System.out.println("e " + MoreArray.toString(exps, ","));
                    e.printStackTrace();
                    System.exit(0);
                }
            }
        }
        return subset;
    }

    /**
     * @param genes
     * @param data
     * @return
     */
    public static int[][] getDataCore(int[] genes, int[][] data) {
        int[][] subset = new int[genes.length][data[0].length];
        for (int a = 0; a < genes.length; a++) {
            try {
                subset[a] = data[genes[a] - 1];
            } catch (Exception e) {
                System.out.println("Exception getDataCore " +
                        "\ta " + a +
                        "\textents " + data.length + "\t" + data[0].length);
                //MoreArray.printArray(vb.genes);
                System.out.println("g " + MoreArray.toString(genes, ","));
                e.printStackTrace();
            }
        }
        return subset;
    }

    /**
     * @param data
     */
    public void setDataAndMean(double[][] data) {
        if (data != null) {
            exp_data = getData(this, data);
            exp_mean = stat.avg(Matrix.avgABSOverSampRows(exp_data));
            signed_exp_mean = stat.avg(Matrix.avgOverSampRows(exp_data));
        }
        //System.out.println("setDataAndMean " + blockId() + "\t" + exp_mean);
    }

    /**
     * @param data
     */
    public void setSignedMean(double[][] data) {
        if (data != null) {
            signed_exp_mean = stat.avg(Matrix.avgOverSampRows(exp_data));
        }
        //System.out.println("setSignedMean " + blockId() + "\t" + signed_exp_mean);
    }

    /**
     * @param data
     */
    public void setSignedCountRow(double[][] data) {
        if (data != null) {
            signed_exp_mean_row_pos = stat.countGreaterThan(Matrix.avgOverSampRows(exp_data), 0);
            signed_exp_mean_row_neg = stat.countLessThan(Matrix.avgOverSampRows(exp_data), 0);
        }
        //System.out.println("setSignedMean " + blockId() + "\t" + signed_exp_mean);
    }

    /**
     * @param data
     */
    public void setSignedCountCol(double[][] data) {
        if (data != null) {
            signed_exp_mean_col_pos = stat.countGreaterThan(Matrix.avgOverCols(exp_data), 0);
            signed_exp_mean_col_neg = stat.countLessThan(Matrix.avgOverCols(exp_data), 0);
        }
        //System.out.println("setSignedMean " + blockId() + "\t" + signed_exp_mean);
    }

    /**
     * @param geneThresh
     * @param expThresh
     */
    public void basictrimNAN(double[][] data, double geneThresh, double expThresh, boolean debug) {
        ArrayList newexp = new ArrayList();
        ArrayList newgene = new ArrayList();

        if (exp_data[0].length > 0) {
            for (int a = 0; a < exp_data.length; a++) {
                int count = 0;
                //count for each row
                for (int e = 0; e < exp_data[0].length; e++) {
                    if (Double.isNaN(exp_data[a][e]))
                        count++;
                }

                //add gene if row satisfies threshold
                //double ratio1 = count / (double) exp_data[0].length;
                //double ratio2 = count / exp_data[0].length;
                double ratio3 = (double) count / (double) exp_data[0].length;
                // System.out.println("trimNAN ratios, use 1 " + genes[a] + "\t" + count + "\t" + exp_data[0].length + "\t" +
                //        geneThresh + "\t1" + ratio1 + "\t2 " + ratio2 + "\t3 " + ratio3);
                if (ratio3 != 1) {
                    newgene.add(genes[a]);
                } else {
                    //if (debug)
                    System.out.println("removing gene " + genes[a] + "\t" + count + "\t" + exp_data[0].length + "\t" +
                            geneThresh + "\t" + ratio3);
                }
            }
            //if (debug)
            //    System.out.println("trimNAN genes " + genes.length + "\t" + newgene.size());
            genes = MoreArray.ArrayListtoInt(newgene);
        }

        if (exp_data.length > 0) {
            for (int e = 0; e < exp_data[0].length; e++) {
                int count = 0;
                //count for each column
                for (int a = 0; a < exp_data.length; a++) {
                    if (Double.isNaN(exp_data[a][e]))
                        count++;
                }
                //add exp if column satisfies threshold
                //double ratio = count / (double) exp_data.length;
                //double ratio1 = count / (double) exp_data.length;
                //double ratio2 = count / exp_data.length;
                double ratio3 = (double) count / (double) exp_data.length;
                //System.out.println("trimNAN ratios, use 1 " + exps[e] + "\t" + count + "\t" + exp_data.length + "\t" +
                //        expThresh + "\t1 " + ratio1 + "\t2 " + ratio2 + "\t3 " + ratio3);
                if (ratio3 != 1) {
                    newexp.add(exps[e]);
                } else {
                    //if (debug)
                    System.out.println("removing exp " + exps[e] + "\t" + count + "\t" + exp_data.length +
                            "\t" + expThresh + "\t" + ratio3);
                }
            }
        }
        //if (debug)
        //    System.out.println("basictrimNAN genes " + genes.length + "\t" + newgene.size() + "\ttrimNAN exps " + exps.length + "\t" + newexp.size());
        exps = MoreArray.ArrayListtoInt(newexp);
        updateArea();
        setDataAndMean(data);
    }


    /**
     * @param geneThresh
     * @param expThresh
     */
    public void trimNAN(double[][] data, double geneThresh, double expThresh, boolean debug) {
        ArrayList newexp = new ArrayList();
        ArrayList newgene = new ArrayList();

        /*TODO start by removing genex and exps with the most missing data, sort both together and alternate removal if necessary */

        if (exp_data[0].length > 0) {
            for (int a = 0; a < exp_data.length; a++) {
                int count = 0;
                //count for each row
                for (int e = 0; e < exp_data[0].length; e++) {
                    if (Double.isNaN(exp_data[a][e]))
                        count++;
                }

                //add gene if row satisfies threshold
                //double ratio1 = count / (double) exp_data[0].length;
                //double ratio2 = count / exp_data[0].length;
                double ratio3 = (double) count / (double) exp_data[0].length;
                // System.out.println("trimNAN ratios, use 1 " + genes[a] + "\t" + count + "\t" + exp_data[0].length + "\t" +
                //        geneThresh + "\t1" + ratio1 + "\t2 " + ratio2 + "\t3 " + ratio3);
                if (ratio3 < geneThresh) {
                    newgene.add(genes[a]);
                } else {
                    //if (debug)
                    System.out.println("removing gene " + genes[a] + "\t" + count + "\t" + exp_data[0].length + "\t" +
                            geneThresh + "\t" + ratio3);
                }
            }
            //if (debug)
            //    System.out.println("trimNAN genes " + genes.length + "\t" + newgene.size());
            genes = MoreArray.ArrayListtoInt(newgene);
        }

        if (exp_data.length > 0) {
            for (int e = 0; e < exp_data[0].length; e++) {
                int count = 0;
                //count for each column
                for (int a = 0; a < exp_data.length; a++) {
                    if (Double.isNaN(exp_data[a][e]))
                        count++;
                }
                //add exp if column satisfies threshold
                //double ratio = count / (double) exp_data.length;
                //double ratio1 = count / (double) exp_data.length;
                //double ratio2 = count / exp_data.length;
                double ratio3 = (double) count / (double) exp_data.length;
                //System.out.println("trimNAN ratios, use 1 " + exps[e] + "\t" + count + "\t" + exp_data.length + "\t" +
                //        expThresh + "\t1 " + ratio1 + "\t2 " + ratio2 + "\t3 " + ratio3);
                if (ratio3 < expThresh) {
                    newexp.add(exps[e]);
                } else {
                    //if (debug)
                    System.out.println("removing exp " + exps[e] + "\t" + count + "\t" + exp_data.length +
                            "\t" + expThresh + "\t" + ratio3);
                }
            }
        }
        if (debug)
            System.out.println("trimNAN genes " + genes.length + "\t" + newgene.size() + "\ttrimNAN exps " + exps.length + "\t" + newexp.size());
        exps = MoreArray.ArrayListtoInt(newexp);
        updateArea();
        setDataAndMean(data);
    }

    /**
     * @return
     */
    public int countNaN() {
        int count = 0;
        if (exp_data != null) {
            for (int a = 0; a < exp_data.length; a++) {
                for (int b = 0; b < exp_data[0].length; b++) {
                    if (Double.isNaN(exp_data[a][b]))
                        count++;
                }
            }
        }
        return count;
    }

    /**
     * @return
     */
    public int countNaNExp(int e) {
        int count = 0;
        if (exp_data != null) {
            for (int a = 0; a < exp_data.length; a++) {
                //System.out.println("countNaNExp " + exp_data[a][e]+"\t"+a + "\t" + e + "\t" + count + "\t" +
                //      exp_data.length + "\t" + exp_data[0].length);
                if (Double.isNaN(exp_data[a][e]))
                    count++;
            }
        }
        return count;
    }

    /**
     * @return
     */
    public int countNaNGene(int g) {
        int count = 0;
        if (exp_data != null) {
            //System.out.println("countNaNGene " + g + "\t" + exp_data[0].length);
            for (int b = 0; b < exp_data[0].length; b++) {
                //System.out.println("countNaNGene " + g + "\t" + b + "\t" +
                //        exp_data.length + "\t" + exp_data[0].length);
                //System.out.println("countNaNGene");
                //MoreArray.printArray(exp_data[g]);
                if (Double.isNaN(exp_data[g][b]))
                    count++;
            }
        } else {
            System.out.println("countNaNGene exp_data is NULL");
        }
        return count;
    }

    /**
     * @return
     */
    public double frxnNaN() {
        if (exp_data != null) {
            double val = (double) countNaN() / (double) block_area;
            //System.out.println("frxnNaN " + val + "\t" + countNaN() + "\t" + block_area);
            return val;
        } else return -1;
    }

    /**
     * @return
     */
    public double frxnNaNExp(int e) {
        // System.out.println("frxnNaNGene " + e);
        if (exp_data != null) {
            double val = (double) countNaNExp(e) / (double) exp_data.length;
            //System.out.println("frxnNaNExp " + e + "\t" + val);
            return val;
        }
        //System.out.println("frxnNaNExp " + e + "\t-1");
        return -1;
    }

    /**
     * @return
     */
    public double frxnNaNGene(int g) {
        //System.out.println("frxnNaNGene " + g);
        if (exp_data != null) {
            double val = (double) countNaNGene(g) / (double) exp_data[0].length;
            //System.out.println("frxnNaNGene " + g + "\t" + val);
            return val;
        } else {
            System.out.println("frxnNaNGene exp_data is NULL");
        }
        //System.out.println("frxnNaNGene " + g + "\t-1");
        return -1;
    }

    /**
     * @return
     */
    public boolean isAboveNaN(double percent_allowed_missing) {
        double frxn = frxnNaN();
        if (frxn == -1) {
            System.out.println("isAboveNaN failure " + blockId() + "\t" + frxn);
            return false;
        }
        //System.out.println("isAboveNan " + frxn + "\t" + PERCENT_ALLOWED_MISSING_IN_BLOCK);
        return frxn < percent_allowed_missing ? true : false;
    }

    /**
     * @return
     */
    public boolean isAboveExpNaN(int e, double percent_allowed_missing_in_block) {
        double frxn = frxnNaNExp(e);
        if (frxn == -1) {
            System.out.println("isAboveExpNaN failure " + e + "\t" + frxn);
            return false;
        }
        return frxn < percent_allowed_missing_in_block ? true : false;
    }

    /**
     * @param percent_allowed_missing
     * @return
     */
    public void removeAboveExpNaN(double percent_allowed_missing) {
        ArrayList rem = new ArrayList();
        for (int i = 0; i < exps.length; i++) {
            double frxn = frxnNaNExp(i);
            if (frxn >= percent_allowed_missing) {
                rem.add(i);
                System.out.println("removeAboveExpNaN " + i + "\t" + frxn + "\t" + percent_allowed_missing);
            }
        }
        int offset = 0;
        for (int i = 0; i < rem.size(); i++) {
            int r = (Integer) rem.get(i) - offset;
            exps = MoreArray.remove(exps, r);
            offset++;
        }
    }

    /**
     * @return
     */
    public boolean isAboveGeneNaN(int g, double percent_allowed_missing) {
        double frxn = frxnNaNGene(g);
        if (frxn == -1) {
            System.out.println("isAboveGeneNaN failure " + g + "\t" + frxn);
            return false;
        }
        return frxn < percent_allowed_missing ? true : false;
    }

    /**
     * @param percent_allowed_missing
     * @return
     */
    public void removeAboveGeneNaN(double percent_allowed_missing) {
        ArrayList rem = new ArrayList();
        for (int i = 0; i < genes.length; i++) {
            double frxn = frxnNaNGene(i);
            if (frxn >= percent_allowed_missing) {
                rem.add(i);
                System.out.println("removeAboveGeneNaN " + i + "\t" + frxn + "\t" + percent_allowed_missing);
            }
        }
        int offset = 0;
        for (int i = 0; i < rem.size(); i++) {
            int r = (Integer) rem.get(i) - offset;
            genes = MoreArray.remove(genes, r);
            offset++;
        }
    }


    /**
     * @param g
     */
    public void addGenes(int[] g) {
        if (genes != null) {
            ArrayList curg = MoreArray.convtoArrayList(genes);
            ArrayList addg = MoreArray.convtoArrayList(g);
            curg = MoreArray.addArrayListUnique(curg, addg);
            genes = MoreArray.ArrayListtoInt(curg);
        } else {
            genes = g;
        }
        Arrays.sort(genes);
        //updateArea();
    }

    /**
     * @param e
     */
    public void addExps(int[] e) {
        if (exps != null) {
            ArrayList cure = MoreArray.convtoArrayList(exps);
            ArrayList adde = MoreArray.convtoArrayList(e);
            cure = MoreArray.addArrayListUnique(cure, adde);
            exps = MoreArray.ArrayListtoInt(cure);
        } else {
            exps = e;
        }
        Arrays.sort(exps);
        //updateArea();
    }

    /**
     * @return
     */
    public String blockId() {
        return BlockMethods.IcJctoijID(genes, exps);
    }

    /**
     * @return
     */
    public String toStringShort() {
        return block_area + "\t" + BlockMethods.IcJctoijID(genes, exps) + "\t" + MINER_STATIC.MOVE_LABELS[move_type + 1]
                + "\t" + pre_criterion + "\t" + all_precriteria[0] + "\t" + all_precriteria[1] + "\t" +
                all_precriteria[2] + "\t" + all_precriteria[3] + "\t" + all_precriteria[4] + "\t" +
                all_precriteria[5] + "\t" + all_precriteria[6] + "\t" + all_precriteria[7] + "\t" +
                percentOrigGenes + "\t" + percentOrigExp + "\t" + exp_mean
                + "\t" + trajectory_position + "\t" + move_class + "\t" + genes.length + "\t" + exps.length + "\t" + iteration;
    }

    /**
     * @param critvals
     */
    public void updatePreCrit(double[][] critvals, boolean weigh, boolean[] crits, boolean debug) {
        if (debug) {
            System.out.println("updatePreCrit precrit wonull b/f merge " + MoreArray.toString(critvals[1], ","));
        }
        all_precriteria = reconcileCritVals(critvals, debug);
        if (debug) {
            System.out.println("updatePreCrit precrit a/f merge " + MoreArray.toString(all_precriteria, ","));
        }
        pre_criterion = computeFullCrit(all_precriteria, weigh, crits, debug);
        if (debug) {
            System.out.println("updatePreCrit precrit " + pre_criterion);
            System.out.println("updatePreCrit precrits " + MoreArray.toString(all_precriteria, ","));
        }
    }


    /**
     * @param d
     * @return
     */
    public final static double[] reconcileCritVals(double[][] d, boolean debug) {
        if (debug) {
            System.out.println("reconcileCritVals wnull b/f " + MoreArray.toString(d[0], ","));
            System.out.println("reconcileCritVals wonull b/f " + MoreArray.toString(d[1], ","));
        }
        double[] ret = d[0];
        for (int i = 0; i < d.length; i++) {
            if (d[0][i] == 0 && d[1][i] != 0)
                ret[i] = d[1][i];
        }
        if (debug) {
            System.out.println("reconcileCritVals wnull a/f " + MoreArray.toString(ret, ","));
        }
        return ret;
    }

    /**
     * critvals:
     * <p/>
     * 1 mean crit: total, row, or col
     * +
     * 2 expr_mean_crit: total, row, or col
     * +
     * 3 expr_reg_crit: row, col
     * +
     * 4 Kendall
     * +
     * 5 Correlation
     * +
     * 6 Euclidean
     * +
     * 7 Spearman
     * +
     * 8 PPI
     * +
     * 9 Feature
     * +
     * 10 MinTF
     *
     * @param critvals
     * @param weigh
     * @param expr_crits
     * @param debug
     */
    public final static double computeFullCrit(double[] critvals, boolean weigh, boolean[] expr_crits, boolean debug) {

        boolean some_expr_true = false;
        for (boolean b : expr_crits)
            if (b)
                some_expr_true = true;
        double ret = 0;
        if (critvals != null && critvals.length > 0) {
            if (debug) {
                try {
                    System.out.println("computeFullCrit critvals: " + critvals.length + "\t" + MoreArray.toString(critvals, ",")
                            + "\t" + weigh + "\t" + MoreArray.toString(expr_crits, ","));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (!weigh || !some_expr_true) {
                ret = stat.sumEntries(critvals);
                if (debug)
                    System.out.println("weighExprCriteria no weigh sum " + ret);
            } else {

                //only TF crit
                if (expr_crits[7] && !expr_crits[0] && !expr_crits[1] && !expr_crits[2] && !expr_crits[3] && !expr_crits[4] && !expr_crits[5] && !expr_crits[6]) {
                    ret = critvals[ValueBlock_STATIC.TF_IND];
                } else {
                    ret = computeCombiExprCrit(critvals, expr_crits, debug);
                    //weight 0.5 TF and 0.5 all other expr crit
                    if (expr_crits[7]) {
                        if (debug)
                            System.out.println("weighExprCriteria 1/2 expr TF " + ret + "\t" + critvals[ValueBlock_STATIC.TF_IND]);
                        ret /= 2.0;//TODO check effect if 2 -> 2.0
                        ret += (critvals[ValueBlock_STATIC.TF_IND] / 2.0);//TODO check effect if 2 -> 2.0
                    }
                }
                //simply sum interaction and features
                if (debug)
                    System.out.println("computeFullCrit " + critvals.length + "\t" + ValueBlock_STATIC.interact_IND + "\t" + ValueBlock_STATIC.feat_IND);
                ret += critvals[ValueBlock_STATIC.interact_IND] + critvals[ValueBlock_STATIC.feat_IND];
            }
        }
        return ret;
    }

    /**
     * critvals
     * MEAN 1
     * MSE   2
     * REG    3
     * KEND    4
     * COR      5
     * Euc       6
     * SPEARMAN       7
     * PPI        8
     * FEAT        9
     * TF           10
     * <p/>
     * expr_crits
     * 1 mean crit: total, row, or col
     * +
     * 2 expr_mean_crit: total, row, or col
     * +
     * 3 expr_reg_crit: row, col
     * +
     * 4 Kendall
     * +
     * 5 Correlation
     * +
     * 6 Euclidean
     * +
     * 7 Spearman
     * +
     * 8 minTF
     *
     * @param critvals
     * @param expr_crits
     * @param debug
     * @return
     */
    private static double computeCombiExprCrit(double[] critvals, boolean[] expr_crits, boolean debug) {
        double ret = 0;

        double count = 0;
        /*count all but last expr crit (TF), that one is weighed separately*/
        for (int i = 0; i < expr_crits.length - 1; i++) {
            if (expr_crits[i])
                count++;
        }

        //average expression related criteria
        if (count > 1)
            ret = (critvals[0] + critvals[1] + critvals[2] + critvals[3] + critvals[4] + critvals[5] + critvals[6]) / count;
        else if (count == 1) {
            for (int a = 0; a < 7; a++)
                ret += critvals[a];
        }

        return ret;
    }

    /**
     * NOT FUNCTIONAL!!
     *
     * @param genes_exps
     */
    public void scoreAndTrim(HashMap genes_exps, boolean takemax) {
        //if (takemax) {
        int sum = 0;
        int max = -1;
        for (int i = 0; i < genes.length; i++) {
            for (int j = 0; j < exps.length; j++) {
                for (int a = i + 1; a < genes.length; a++) {
                    for (int b = j + 1; b < exps.length; b++) {
                        String s = "" + i + "_" + j + "__" + a + "_" + b;
                        int o = (Integer) genes_exps.get(s);
                        sum += o;
                        if (o > max)
                            max = o;
                    }
                }
            }
            //}
        }

        ArrayList remgenes = new ArrayList();
        ArrayList remexps = new ArrayList();
        for (int i = 0; i < genes.length; i++) {
            for (int j = 0; j < exps.length; j++) {
                for (int a = i + 1; a < genes.length; a++) {
                    for (int b = j + 1; b < exps.length; b++) {
                        String s = "" + i + "_" + j + "__" + a + "_" + b;
                        int o = (Integer) genes_exps.get(s);
                        if (o < max)
                            remgenes.add(i);
                    }
                }
            }
        }
    }

    /**
     * @param genes_exps
     */
    public void scoreGeneExpAndWrite(HashMap genes_exps, String f, String id) {
        double[][] out = new double[genes.length][exps.length];
        int count = 0;
        for (int i = 0; i < genes.length; i++) {
            for (int j = 0; j < exps.length; j++) {
                String s = "" + genes[i] + "_" + exps[j];
                //try {
                Object o = genes_exps.get(s);
                if (o != null) {
                    out[i][j] = (Integer) o;
                    count++;
                }
                /*else {
                    System.out.println("scoreGeneExpAndWrite did not find in HashMap " + s);
                }*/
                //} catch (Exception e) {
                //    e.printStackTrace();
                //}
            }
        }

        out = Matrix.norm(count, out);
        TabFile.write(MoreArray.toString(out, "", ""), f,
                MoreArray.toStringArray(exps), MoreArray.toStringArray(genes), id);
    }

    /**
     * @param genes_exps_pairs
     * @param f
     * @param id
     */
    public void scoreGeneExpPairsAndWrite(HashMap genes_exps_pairs, String f, String id) {
        int[][] out = new int[genes.length * exps.length][genes.length * exps.length];

        String[] labels = new String[genes.length * exps.length];
        for (int i = 0; i < genes.length; i++) {
            for (int j = 0; j < exps.length; j++) {
                labels[i * exps.length + j] = "" + genes[i] + "_" + exps[j];
            }
        }

        for (int i = 0; i < genes.length; i++) {
            for (int j = 0; j < exps.length; j++) {
                for (int a = i + 1; a < genes.length; a++) {
                    for (int b = j + 1; b < exps.length; b++) {
                        String s = "" + genes[i] + "_" + exps[j] + "__" + genes[a] + "_" + exps[b];
                        Object o = genes_exps_pairs.get(s);
                        if (o != null) {
                            out[i * exps.length + j][a * exps.length + b] = (Integer) o;
                            out[a * exps.length + b][i * exps.length + j] = (Integer) o;
                        } else {
                            System.out.println("scoreGeneExpPairsAndWrite did not find in HashMap " + s);
                        }
                    }
                }
            }
        }

        TabFile.write(MoreArray.toString(out, "", ""), f, labels, labels, id);
    }


    /**
     * @param test
     * @return
     */
    public boolean contains(ValueBlock test) {
        int[] gar = MoreArray.crossFirstIndex(test.genes, genes);
        int[] ear = MoreArray.crossFirstIndex(test.exps, exps);
        boolean go = true;
        try {
            int arrayInd = MoreArray.getArrayInd(gar, -1);
            if (arrayInd != -1) {
                go = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            int arrayInd = MoreArray.getArrayInd(ear, -1);
            if (arrayInd != -1) {
                go = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return go;
    }

    /**
     * @param test
     * @return
     */
    public boolean containsPercent(ValueBlock test, double percent) {
        int[] gar = MoreArray.crossFirstIndex(test.genes, genes);
        int[] ear = MoreArray.crossFirstIndex(test.exps, exps);
        boolean go = true;
        try {
            int arrayInd = MoreArray.getArrayInd(gar, -1);
            if (arrayInd != -1) {
                go = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            int arrayInd1 = MoreArray.getArrayInd(ear, -1);
            if (arrayInd1 != -1) {
                go = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return go;
    }

    /**
     * @param v
     * @return
     */
    public boolean equals(ValueBlockPre v) {

        Arrays.sort(this.genes);
        Arrays.sort(v.genes);
        Arrays.sort(this.exps);
        Arrays.sort(v.exps);

        if (Arrays.equals(this.genes, v.genes) && Arrays.equals(this.exps, v.exps)) {
            //if (this.blockId().equals(v.blockId())) {
            return true;
        }
        return false;
    }

    /**
     * @param v
     * @return
     */
    public boolean equals(ValueBlock v) {

        Arrays.sort(this.genes);
        Arrays.sort(v.genes);
        Arrays.sort(this.exps);
        Arrays.sort(v.exps);

        if (Arrays.equals(this.genes, v.genes) && Arrays.equals(this.exps, v.exps)) {
            //if (this.blockId().equals(v.blockId())) {
            return true;
        }
        return false;
    }

    /*@Override
    public int compareTo(ValueBlockPre vbtoo) {
        *//* For Ascending order*//*
        if (this.pre_criterion - vbtoo.pre_criterion < 0)
            return -1;
        else if (this.pre_criterion - vbtoo.pre_criterion > 0)
            return 1;
        return 0;

    }*/
}
