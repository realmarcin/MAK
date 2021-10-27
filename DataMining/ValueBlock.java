package DataMining;

import util.MoreArray;
import util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Object to store basic properties of a data block_id.
 * <p/>
 * User: marcinjoachimiak
 * Date: Jun 8, 2007
 * Time: 9:32:55 AM
 */
public class ValueBlock extends ValueBlockPre implements Comparable<ValueBlock> {

    /* Full list of criteria*/
    public double[] all_criteria;

    /*Summary criterion*/
    public double full_criterion = Double.NaN;

    public int[] feature_indices;

    //boolean removedChromosomeNo = false;

    /**
     *
     */
    public ValueBlock() {
        super();
        all_criteria = new double[ValueBlock_STATIC.NUM_CRIT];
    }

    /**
     *
     */
    public ValueBlock(int g, int e) {
        super(g, e);
    }

    /**
     * Calculates full criteria
     *
     * @param vb
     */
    public ValueBlock(ValueBlockPre vb) {
        super();
        block_area = vb.block_area;
        //block_id = vb.block_id;
        genes = vb.genes;
        exps = vb.exps;
        //genesStr = vb.genesStr;
        //expsStr = vb.expsStr;
        prev_block = vb.prev_block;
        move_type = vb.move_type;
        changed = vb.changed;
        pre_criterion = vb.pre_criterion;
        percentOrigGenes = vb.percentOrigGenes;
        percentOrigExp = vb.percentOrigExp;
        exp_data = vb.exp_data;
        exp_mean = vb.exp_mean;
        trajectory_position = vb.trajectory_position;
        move_class = vb.move_class;
        if (vb.all_precriteria != null) {
            all_precriteria = vb.all_precriteria;
            /*expr_criterion = vb.expr_criterion;
          inter_criterion = vb.inter_criterion;*/
            updateAllCriteria(vb.all_precriteria);
        }
        exp_data = vb.exp_data;


    }


    /**
     * @param d
     */
    public void updateAllCriteria(double[] d) {
        all_criteria = new double[d.length];
        for (int i = 0; i < d.length; i++) {
            all_criteria[i] = d[i];
        }
    }

    /**
     * @param vb
     */
    public ValueBlock(ValueBlock vb) {
        block_area = vb.block_area;
        //block_id = vb.block_id;
        genes = vb.genes;
        exps = vb.exps;
        //genesStr = vb.genesStr;
        //expsStr = vb.expsStr;
        prev_block = vb.prev_block;
        move_type = vb.move_type;
        changed = vb.changed;
        pre_criterion = vb.pre_criterion;
        full_criterion = vb.full_criterion;
        all_criteria = vb.all_criteria;
        all_precriteria = vb.all_precriteria;
        percentOrigGenes = vb.percentOrigGenes;
        percentOrigExp = vb.percentOrigExp;
        exp_data = vb.exp_data;
        exp_mean = vb.exp_mean;
        trajectory_position = vb.trajectory_position;
        feature_indices = vb.feature_indices;
        move_class = vb.move_class;
        iteration = vb.iteration;
    }

    /**
     * @param vb
     */
    public ValueBlock(ValueBlock vb, int[] g, int[] e) {
        genes = g;
        exps = e;
        updateArea();

        prev_block = vb.prev_block;
        move_type = vb.move_type;
        changed = vb.changed;
        pre_criterion = vb.pre_criterion;
        full_criterion = vb.full_criterion;
        all_criteria = vb.all_criteria;
        all_precriteria = vb.all_precriteria;
        percentOrigGenes = vb.percentOrigGenes;
        percentOrigExp = vb.percentOrigExp;
        exp_data = vb.exp_data;
        exp_mean = vb.exp_mean;
        trajectory_position = vb.trajectory_position;
        feature_indices = vb.feature_indices;
        move_class = vb.move_class;
        iteration = vb.iteration;
    }

    /**
     * @param s
     */
    public ValueBlock(String s) {
        int[][] get = BlockMethods.ijIDtoIcJc(s);
        genes = MoreArray.removeWhereEqual(get[0], MoreArray.initArray(get[0].length, 0));
        exps = MoreArray.removeWhereEqual(get[1], MoreArray.initArray(get[1].length, 0));
        Arrays.sort(genes);
        Arrays.sort(exps);
        //genesStr = MoreArray.toString(genes, ",");
        //expsStr = MoreArray.toString(exps, ",");
        updateArea();
    }

    /**
     * @param coords
     */
    public ValueBlock(ArrayList[] coords) {
        String s = BlockMethods.IcJctoijID(coords);
        int[][] get = BlockMethods.ijIDtoIcJc(s);
        genes = MoreArray.removeWhereEqual(get[0], MoreArray.initArray(get[0].length, 0));
        exps = MoreArray.removeWhereEqual(get[1], MoreArray.initArray(get[1].length, 0));
        Arrays.sort(genes);
        Arrays.sort(exps);
        //genesStr = MoreArray.toString(genes, ",");
        //expsStr = MoreArray.toString(exps, ",");
        updateArea();
        iteration = -1;
    }

    /**
     * @param gs
     * @param es
     */
    public ValueBlock(String[] gs, String[] es) {
        new ValueBlock(MoreArray.tointArray(gs), MoreArray.tointArray(es));
    }

    /**
     * @param g
     * @param e
     */
    public ValueBlock(int[] g, int[] e) {
        genes = MoreArray.copy(g);
        exps = MoreArray.copy(e);
        Arrays.sort(genes);
        Arrays.sort(exps);
        //genesStr = MoreArray.toString(genes, ",");
        //expsStr = MoreArray.toString(exps, ",");
        updateArea();
        iteration = -1;
    }

    /**
     * @param g
     * @param e
     */
    public ValueBlock(ArrayList g, ArrayList e) {
        genes = MoreArray.ArrayListtoInt(g);
        exps = MoreArray.ArrayListtoInt(e);
        Arrays.sort(genes);
        Arrays.sort(exps);
        //genesStr = MoreArray.toString(genes, ",");
        //expsStr = MoreArray.toString(exps, ",");
        updateArea();

        all_criteria = new double[9];
        iteration = -1;
    }

    /**
     * @param s
     * @param d
     * @return
     */
    public final static ValueBlock read(String s, boolean d) throws Exception {
        if (d)
            System.out.println("ValueBlock read " + s);
        ValueBlock v = new ValueBlock();
        String[] ar = s.split("\\t");
        if (d)
            MoreArray.printArray(ar);
        if (d)
            System.out.println("read ar " + ar.length);

        int offset = 0;

        //for backwards compatibility with output lacking the feature crit value
        /*if (ar.length == 18) {
            offset = 1;
        }*/
        v.index = Integer.parseInt(ar[0]);
        if (d)
            System.out.println("block index is present " + v.index);
        //}
        v.block_area = Integer.parseInt(ar[1]);
        //v.block_id = ar[2 - offset];
        //v.coords = BlockMethods.ijIDtoIcJctoList(ar[2 - offset]);
        int[][] get = null;
        //if (ar[2].indexOf("\"") != -1)
        ar[2] = StringUtil.replace(ar[2], "\"", "");
        if (d)
            System.out.println("index " + ar[2]);
        try {
            get = BlockMethods.ijIDtoIcJc(ar[2]);
        } catch (Exception e) {
            System.out.println("ValueBlock read detected non-numeric ids " + ar[2]);
            e.printStackTrace();
            //v.coords = BlockMethods.ijIDtoIcJctoList(v.block_id);
        }

        if (get != null) {
            v.genes = MoreArray.removeWhereEqual(get[0], MoreArray.initArray(get[0].length, 0));
            Arrays.sort(v.genes);
            if (d)
                System.out.println("read genes " + MoreArray.toString(v.genes, ","));

            if (get[1] != null) {
                v.exps = MoreArray.removeWhereEqual(get[1], MoreArray.initArray(get[1].length, 0));
                Arrays.sort(v.exps);
                if (d)
                    System.out.println("read exps " + MoreArray.toString(v.exps, ","));
            }
        }

        //compatible with integer or string formats for move types
        try {
            v.move_type = Integer.parseInt(ar[3]);
            if (d)
                System.out.println("read move_type " + v.move_type);
        } catch (NumberFormatException e) {
            //e.printStackTrace();
            //System.out.println("read move_type not integer " + ar[3]);
            v.move_type = MoreArray.getArrayInd(MINER_STATIC.MOVE_TYPES, ar[3]);
            if (v.move_type == -1) {
                //System.out.println("read move_type not long label" + ar[3]);
                v.move_type = MoreArray.getArrayInd(MINER_STATIC.MOVE_LABELS, ar[3]) - 1;
            }
        }
        //System.out.println("rea
        //  d move_type " + v.move_type + "\t" + ar[3]);
        if (d) {
            System.out.println("read offset " + offset);
            MoreArray.printArray(ar);
        }

        try {
            v.pre_criterion = Double.parseDouble(ar[4]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (d)
            System.out.println("read pre_criterion " + v.pre_criterion);

        try {
            v.full_criterion = Double.parseDouble(ar[5]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (d)
            System.out.println("read full_criterion " + v.full_criterion);

        int crits = 6;
        //if(ar[ar.length-3].indexOf("_") == -1)

        /*TODO: modify to add kend and cor expr criteria*/
        double test = Double.parseDouble(ar[13 + offset]);

        if (d)
            System.out.println("read offset " + offset + "\ttest " + test);

        if (d) {
            System.out.println("ar " + ar.length + "\t" + offset);
            MoreArray.printArray(ar);
        }
        if (ar.length == 18 || ar.length == 19) {
            crits = 4;
        } else if (ar.length == 20) {
            offset = 2;
        } else if (ar.length == 22) {
            offset = 4;
            crits = 8;
        } else if (ar.length == 23) {
            offset = 5;
            crits = 9;
        } else if (ar.length == 24) {
            offset = 5;
            crits = 9;
        } else if (ar.length == 25) {
            offset = 6;
            crits = 10;
        } else if (ar.length == 26) {
            offset = 6;
            crits = 10;
        }
        if (d) {
            System.out.println("ar " + ar.length + "\t" + offset);
            System.out.println("read " + test + "\t" + crits + "\t" + offset);
        }
        int offset2 = 0;
        for (int i = 0; i < crits; i++) {
            if (crits == 6 && i == 4)
                offset2 = 2;
            try {
                v.all_criteria[i + offset2] = Double.parseDouble(ar[6 + i]);
                if (d)
                    System.out.println("read all_criteria " + ar[6 + i]);
            } catch (Exception e) {
                v.all_criteria[i + offset2] = 0;
                //e.printStackTrace();
            }
        }

        if (d)
            System.out.println("read all_criteria " + MoreArray.toString(v.all_criteria, ","));

        if (d)
            System.out.println("offset2 " + offset2 + "\toffset " + offset);
        try {
            v.percentOrigGenes = Double.parseDouble(ar[10 + offset]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            v.percentOrigExp = Double.parseDouble(ar[11 + offset]);
        } catch (Exception e) {
            //e.printStackTrace();
        }
        try {
            v.exp_mean = Double.parseDouble(ar[12 + offset]);
        } catch (Exception e) {
            //e.printStackTrace();
        }

        v.trajectory_position = Integer.parseInt(ar[13 + offset]);
        if (d)
            System.out.println("v.trajectory_position " + (13 + offset) + "\t" + v.trajectory_position);
        int ind = -1;
        try {
            v.feature_indices = MoreArray.tointArray(ar[14 + offset].substring(1, ar[14 + offset].length() - 1).split(","));

        } catch (Exception e) {
            //ind = 14 + offset;
            //v.move_class = ar[ind];
            //System.out.println(s);
            //e.printStackTrace();
        }

        ind = 15 + offset;
        v.move_class = ar[ind];


        //skip 16 and 17 for num genes and exps
        if (ar.length == 24) {
            ind = 18 + offset;
            v.iteration = Integer.parseInt(ar[ind]);
        }

        if (d)
            System.out.println("v.move_class " + ind + "\t" + v.move_class);
        //if (ar.length == 19)
        //    v.all_criteria[ValueBlock_STATIC.interact_IND] = Double.parseDouble(ar[16 + offset]);

        return v;
    }

    /**
     * @return
     */
    public String toStringShort() {

        int exps_len = 0;
        if (exps != null)
            exps_len = exps.length;

        if (all_criteria != null) {
            String ret = block_area + "\t" + BlockMethods.IcJctoijID(genes, exps) + "\t" + MINER_STATIC.MOVE_LABELS[move_type + 1]
                    + "\t" + pre_criterion + "\t" + full_criterion;
            for (int i = 0; i < all_criteria.length; i++) {
                ret += "\t" + all_criteria[i];
            }
            ret += "\t" + percentOrigGenes + "\t" + percentOrigExp
                    + "\t" + exp_mean + "\t" + trajectory_position;
            if (feature_indices != null)
                ret += "\t" + MoreArray.toString(feature_indices, ",");
            else
                ret += "\t";
            //System.out.println("toStringShort " + ret);
            ret += "\t" + move_class + "\t" + genes.length + "\t" + exps_len + "\t" + iteration;
            //System.out.println("iteration " + iteration);
            return ret;
        } else {
            String ret = block_area + "\t" + BlockMethods.IcJctoijID(genes, exps) + "\t" + MINER_STATIC.MOVE_LABELS[move_type + 1]
                    + "\t" + pre_criterion + "\t" + full_criterion + "\t";
            for (int i = 0; i < 9; i++) {
                ret += "NaN\t";
            }
            ret += percentOrigGenes + "\t" + percentOrigExp + "\t" + exp_mean + "\t" + trajectory_position;
            //System.out.println(ret);
            if (feature_indices != null)
                ret += "\t" + MoreArray.toString(feature_indices, ",");
            else
                ret += "\t";
            //System.out.println(ret);
            ret += "\t" + move_class + "\t" + genes.length + "\t" + exps_len + "\t" + iteration;
            //System.out.println(ret);
            return ret;
        }
        //return null;
    }

    /**
     * @return
     */
    public String toStringShortWithoutIter() {
        if (all_criteria != null) {
            String ret = block_area + "\t" + BlockMethods.IcJctoijID(genes, exps) + "\t" + MINER_STATIC.MOVE_LABELS[move_type + 1]
                    + "\t" + pre_criterion + "\t" + full_criterion;
            for (int i = 0; i < all_criteria.length; i++) {
                ret += "\t" + all_criteria[i];
            }
            ret += "\t" + percentOrigGenes + "\t" + percentOrigExp
                    + "\t" + exp_mean + "\t" + trajectory_position;
            if (feature_indices != null)
                ret += "\t" + MoreArray.toString(feature_indices, ",");
            else
                ret += "\t";
            ret += "\t" + move_class + "\t" + genes.length + "\t" + exps.length + "\t" + iteration;
            //System.out.println("iteration " + iteration);
            return ret;
        } else {
            String ret = block_area + "\t" + BlockMethods.IcJctoijID(genes, exps) + "\t" + MINER_STATIC.MOVE_LABELS[move_type + 1]
                    + "\t" + pre_criterion + "\t" + full_criterion + "\t";
            for (int i = 0; i < 9; i++) {
                ret += "NaN\t";
            }
            ret += percentOrigGenes + "\t" + percentOrigExp + "\t" + exp_mean + "\t" + trajectory_position;
            //System.out.println(ret);
            if (feature_indices != null)
                ret += "\t" + MoreArray.toString(feature_indices, ",");
            else
                ret += "\t";
            //System.out.println(ret);
            ret += "\t" + move_class + "\t" + genes.length + "\t" + exps.length;
            //System.out.println(ret);
            return ret;
        }
        //return null;
    }

    /**
     * @param move_labels
     * @return
     */
    public final String toStringShort(String[] move_labels) {
        String ret = block_area + "\t" + BlockMethods.IcJctoijID(genes, exps) + "\t" + move_labels[move_type + 1]
                + "\t" + pre_criterion + "\t" + full_criterion;

        for (int i = 0; i < all_criteria.length; i++) {
            ret += "\t" + all_criteria[i];
        }
        ret += "\t" + this.percentOrigGenes + "\t" +
                percentOrigExp + "\t" + exp_mean + "\t" + trajectory_position;
        if (feature_indices != null)
            ret += "\t" + MoreArray.toString(feature_indices, ",");
        else
            ret += "\t";
        ret += "\t" + move_class + "\t" + genes.length + "\t" + exps.length + "\t" + iteration;
        return ret;
    }

    /**
     * @return
     */
    public final String toString() {
        String block_id = BlockMethods.IcJctoijID(genes, exps);
        int sla = block_id.indexOf("/");
        String s = "block genes: " + block_id.substring(0, sla) + "\n";
        s += "block exps : " + block_id.substring(sla + 1, block_id.length()) + "\n";
        s += "move_type: " + MINER_STATIC.MOVE_LABELS[move_type + 1] + "\tfull_criterion: " + full_criterion +
                "\tpre_criterion: " + pre_criterion;
        if (all_criteria != null)
            s += "\texpr_mean_crit: " + all_criteria[ValueBlock_STATIC.expr_MEAN_IND] + "\texpr_mean_crit: " + all_criteria[ValueBlock_STATIC.expr_MSE_IND] +
                    "\texpr_reg_crit: " + all_criteria[ValueBlock_STATIC.expr_FEM_IND] + "\texpr_kend_crit: " + all_criteria[ValueBlock_STATIC.expr_KEND_IND] +
                    "\texpr_cor_crit: " + all_criteria[ValueBlock_STATIC.expr_COR_IND] + "\texpr_euc_crit: " + all_criteria[ValueBlock_STATIC.expr_EUC_IND] +
                    "\texpr_spearman_crit: " + all_criteria[ValueBlock_STATIC.expr_SPEARMAN_IND] +
                    "\tPPI_crit: " + all_criteria[ValueBlock_STATIC.interact_IND] +
                    "\tfeat_crit: " + all_criteria[ValueBlock_STATIC.feat_IND] + "\tTF_crit" + all_criteria[ValueBlock_STATIC.TF_IND];
        s += "\tpercentOrigGenes: " + percentOrigGenes + "\tpercentOrigExp: " + percentOrigExp +
                "\texp_mean: " + exp_mean + "\ttrajectory_position: " + trajectory_position;
        if (feature_indices != null)
            s += "\tFEATURE_INDICES: " + MoreArray.toString(feature_indices, ",");
        else
            s += "\t\t";
        s += "\t" + move_class + "\t" + genes.length + "\t" + exps.length + "\t" + iteration;
        return s;
    }

    /**
     * @param ref
     * @param test
     */
    public ValueBlock updateOverlap(ValueBlock ref, ValueBlock test) {
        test = BlockMethods.computeBlockOverlapWithRef(ref, test, false);
        if (Double.isNaN(test.percentOrigExp) || Double.isNaN(test.percentOrigGenes))
            System.out.println("updateOverlap NaN percentOrigExp:" + test.percentOrigExp +
                    "\tpercentOrigGenes " + test.percentOrigGenes);
        computedOverlap = true;
        return test;
    }

    /**
     * @param f
     */
    public void setFeatures(int[] f) {
        feature_indices = f;
    }

    /**
     * mean crit: total, row, or col
     * +
     * expr_mean_crit: total, row, or col
     * +
     * expr_reg_crit: row, col
     * +
     * PPI
     * +
     * Feature
     * +
     * MinTF
     * <p/>
     * crits:
     * 0 = reg
     * 1 = cor
     * 2 = mean
     *
     * @param critvals
     */
    public void updateCrit(double[][] critvals, boolean weigh, boolean[] crits, boolean debug) {
        if (critvals != null && critvals.length > 0) {
            if (debug) {
                System.out.println("updateCrit " + critvals.length);
                System.out.println("updateCrit " + MoreArray.toString(critvals[0], ","));
                System.out.println("updateCrit fullcrits b/f merge " + MoreArray.toString(critvals[0], ","));
            }
            all_criteria = reconcileCritVals(critvals, debug);
            if (debug) {
                System.out.println("updateCrit fullcrits a/f merge " + MoreArray.toString(all_criteria, ","));
            }
            full_criterion = computeFullCrit(all_criteria, weigh, crits, debug);
            if (debug) {
                System.out.println("updateCrit fullcrit " + full_criterion);
                System.out.println("updateCrit fullcrits " + MoreArray.toString(all_criteria, ","));
            }
        } else {
            full_criterion = 0;
            all_criteria = new double[9];
        }
    }

    /**
     * mean crit: total, row, or col
     * +
     * expr_mean_crit: total, row, or col
     * +
     * expr_reg_crit: row, col
     * <p/>
     * Kendall
     * <p/>
     * Correlation
     * +
     * PPI
     * +
     * Feature
     * +
     * MinTF
     * <p/>
     * crits:
     * 0 = reg
     * 1 = cor
     * 2 = mean
     *
     * @param critvals
     */
    public void updateCrit(double[] critvals, boolean weigh, boolean[] crits, boolean debug) {
        all_criteria = critvals;
        full_criterion = computeFullCrit(all_criteria, weigh, crits, debug);
        if (debug) {
            System.out.println("updateCrit fullcrit " + full_criterion);
            System.out.println("updateCrit fullcrits " + MoreArray.toString(all_criteria, ","));
        }
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

    @Override
    public int compareTo(ValueBlock vbtoo) {
        /* For Ascending order*/
        if (this.full_criterion - vbtoo.full_criterion < 0)
            return 1;
        else if (this.full_criterion - vbtoo.full_criterion > 0)
            return -1;
        return 0;

    }
}
