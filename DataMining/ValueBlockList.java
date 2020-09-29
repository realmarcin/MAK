package DataMining;


import util.MoreArray;
import util.StringUtil;
import util.TextFile;

import java.util.ArrayList;

/**
 * Class for an ArrayList type object containing ValueBlocks from data mining.
 * User: marcinjoachimiak
 * Date: Jun 8, 2007
 * Time: 9:35:32 AM
 */
public class ValueBlockList extends ArrayList {

    private final ValueBlockListMethods valueBlockListMethods = new ValueBlockListMethods();
    /**
     * criteria = 0
     * precit = 1
     * cache = 2
     * trajectory = 3
     */
    int list_type;

    double runtime = -1;
    public String header;

    /**
     *
     */
    public ValueBlockList() {
        super();

    }

    /**
     *
     */
    public ValueBlockList(ValueBlockList v) {
        super();
        header = v.header;
        runtime = v.runtime;

        for (int i = 0; i < v.size(); i++) {
            add(v.get(i));
        }
    }

    /**
     * @param i
     */
    public ValueBlockList(int i) {
        super(i);
        for (int h = 0; h < i; h++) {
            this.add(new ValueBlock());
        }
    }

    /**
     * @param init
     */
    public ValueBlockList(ArrayList init, int mode) {
        super();
        if (mode == 0) {
            for (int i = 0; i < init.size(); i++) {
                String s = (String) init.get(i);
                ValueBlock cur = new ValueBlock(s);
                this.add(cur);
            }
        } else if (mode == 1) {
            for (int i = 0; i < init.size(); i++) {
                ValueBlock cur = (ValueBlock) init.get(i);
                this.add(cur);
            }
        }
    }

    /**
     * Returns a string representation of the block id.
     *
     * @return
     */
    public final String toString() {
        String ret = "";
        for (int i = 0; i < this.size(); i++) {
            ret += i + "\t" + ((ValueBlock) this.get(i)).toStringShort() + "\n";
        }
        return ret;
    }

    /**
     * @param labels
     * @return
     */
    public final String toString(String[] labels) {
        return toString(labels, null);
    }

    /**
     * @param moveLabels
     * @param header
     * @return
     */
    public final String toString(String[] moveLabels, String header) {
        String ret = "";
        if (header != null)
            ret += header + "\n";
        for (int i = 0; i < this.size(); i++) {
            ret += "" + (i + 1) + "\t" + ((ValueBlock) this.get(i)).toStringShort(moveLabels) + "\n";
        }
        return ret;
    }

    /**
     * @param header
     * @return
     */
    public final String toString(String header) {
        String ret = "";
        header = StringUtil.replace(header, "\n", " ");
        if (header != null)
            ret += header + "\n";
        for (int i = 0; i < this.size(); i++) {
            Object o = this.get(i);
            if (o != null)
                ret += "" + (i + 1) + "\t" + ((ValueBlock) o).toStringShort() + "\n";
        }
        return ret;
    }


    /**
     * @param header
     * @return
     */
    public final String toString(String header, int iter) {
        String ret = "";
        header = StringUtil.replace(header, "\n", " ");
        if (header != null)
            ret += header + "\n";
        for (int i = 0; i < this.size(); i++) {
            Object o = this.get(i);
            if (o != null)
                ret += "" + (i + 1) + "\t" + ((ValueBlock) o).toStringShortWithoutIter() + "\t" + iter + "\n";
        }
        return ret;
    }


    /**
     * @param f
     * @param d
     * @return
     */
    public final static ValueBlockList read(String f, boolean d) throws Exception {
        if (d)
            System.out.println("ValueBlockList read");

        ValueBlockList vls = null;
        //try {
        ArrayList pass = TextFile.readtoList(f);
        vls = new ValueBlockList();
        if (d)
            System.out.println("ValueBlockList read " + f + "\tsize " + pass.size());
        int start = 0;
        String testcomment = (String) pass.get(0);
        String testlabels = (String) pass.get(1);
        if (d) {
            System.out.println("read comment/label? " + testcomment);
            System.out.println("read labels? " + testlabels);
        }
        int index = testcomment.indexOf("#");
        int indexno = testcomment.indexOf("index");
        int indexno3 = testcomment.indexOf("name");
        if (d)
            System.out.println("read index " + index + "\t" + indexno);
        if (index == 0 || indexno != 0 || indexno3 != 0) {
            vls.header = testcomment;
            start++;
            if (d)
                System.out.println("read labels " + testlabels);
            int index2 = testlabels.indexOf("index");
            if (d)
                System.out.println("read index2 " + index2);
            if (index2 == 0)
                start++;
            try {
                vls.runtime = Double.parseDouble(testcomment.substring(index, testcomment.length()));
            } catch (Exception e) {
                //e.printStackTrace();
            }
        } /*else if (testcomment.indexOf("#") == 0 || testcomment.indexOf("index") == 0 || testcomment.indexOf("name") == 0) {
            start++;
            vls.header = testcomment;
        }*/

        //if (start > 0)
        if (d)
            System.out.println("ValueBlockList.read skipped " + start);
        //skip testcomment and header lines
        for (int i = start; i < pass.size(); i++) {
            String cur = (String) pass.get(i);
            if (d)
                System.out.println("ValueBlockList cur " + cur);
            if (cur.indexOf("#") != 0) {
                ValueBlock v = null;
                //try {
                try {
                    v = ValueBlock.read(cur, d);
                } catch (Exception e) {
                    if (d) {
                        e.printStackTrace();
                        System.out.println("ValueBlockList.read FAILED " + cur);
                    }
                }

                if (v != null)
                    vls.add(v);
                //} catch (Exception e) {
                //e.printStackTrace();
                //}
                if (d) {
                    try {
                        System.out.println("ValueBlockList " + MoreArray.toString(v.all_criteria));//v.toStringShort());
                        System.out.println("ValueBlockList " + vls.size());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (vls.size() == 0)
            vls = null;
        //} catch (Exception e) {
        //    e.printStackTrace();
        //}
        return vls;
    }

    /**
     * @param vb
     * @return
     */
    public boolean contains(ValueBlock vb) {
        for (int i = 0; i < this.size(); i++) {
            ValueBlock cur = (ValueBlock) this.get(i);
            //System.out.println("contains " + i);
            if (cur.equals(vb)) {
                // System.out.println("contains equals " + i);
                //if (cur.block_id.equals(vb.block_id))
                return true;
            }
        }
        return false;
    }


    /**
     *
     */
    /*public void reorderByMean(double[][] data) {
        valueBlockListMethods.reorderByMean(data);
    }

    *//**
     * @param gene_ind
     * @param exp_ind
     *//*
    public void translateIndices(int[] gene_ind, int[] exp_ind) {
        valueBlockListMethods.translateIndices(gene_ind, exp_ind);
    }

    *//**
     *
     *//*
    public void writeBIC(String outf) {

        valueBlockListMethods.writeBIC(outf);
    }*/

    /**
     * @return
     */
  /*  public int lastBlockBeforeDeletion() {
        return valueBlockListMethods.lastBlockBeforeDeletion();
    }


    *//**
     * @return
     *//*
    public int lastBlockGeneAddition() {
        return valueBlockListMethods.lastBlockGeneAddition();
    }

    *//**
     * @return
     *//*
    public int lastBlockExpAddition() {
        return valueBlockListMethods.lastBlockExpAddition();
    }

    *//**
     * @return
     *//*
    public int lastBlockGeneExpAddition() {
        return valueBlockListMethods.lastBlockGeneExpAddition();
    }
*/
    /**
     * @param label
     */
    /*public void sort(String label) {
        //new ValueBlockList();
        //sort by block_area descending
        valueBlockListMethods.sort(label);
    }

    *//**
     * @param label
     *//*
    public ArrayList sort(String label, ArrayList alist) {
        //sort by block_area descending

        return valueBlockListMethods.sort(label, alist);
    }

    *//**
     * @param label
     *//*
    public ArrayList sortStr(String label, ArrayList alist) {
        //sort by block_area descending

        return valueBlockListMethods.sortStr(label, alist);
    }

    *//**
     * @param label
     *//*
    public ArrayList sort(String label, ArrayList setslist, ArrayList labellist, ArrayList mergelist, ArrayList mergehash) {

        //sort by block_area descending

        return valueBlockListMethods.sort(label, setslist, labellist, mergelist, mergehash);
    }*/

    /**
     * @param label
     */
    /* public ArrayList sortNonBlock(String label, ArrayList alist) {
            ValueBlockList tmp = new ValueBlockList();
            ArrayList newalist = new ArrayList();
            boolean done = false;
            //sort by block_area descending
            if (label.equals("area")) {
                //for all blocks
                for (int i = 0; i < size(); i++) {
                    ValueBlock cur = (ValueBlock) get(i);
                    if (cur != null) {
                        Object o = (Object) alist.get(i);
                        //if first pass
                        if (tmp.size() == 0) {
                            //System.out.println("sort 1 " + tmp.size() + "\t" + cur.block_area);
                            tmp.add(0, cur);
                            newalist.add(0, o);
                        } else {
                            boolean added = false;
                            //for all sorted
                            for (int j = 0; j < tmp.size(); j++) {
                                ValueBlock v = (ValueBlock) tmp.get(j);
                                if (v != null) {
                                    if (v.block_area < cur.block_area) {
                                        //System.out.println("sort   " + tmp.size() + "\t" + cur.block_area);
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
                                    System.out.println("sort o is null " + i);
                                }
                                newalist.add(o);
                            }
                        }
                    }
                }
                //System.out.println("sort this " + ((ValueBlock) get(0)).toStringShort());
                //System.out.println("sort tmp " + ((ValueBlock) tmp.get(0)).toStringShort());
                done = true;
            }
            //sort by block_area descending
            else if (label.equals("full")) {
                for (int i = 0; i < size(); i++) {
                    ValueBlock cur = (ValueBlock) get(i);
                    Object o = (Object) alist.get(i);
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
                System.out.println("tmp.size() " + tmp.size() + "\tsize " + size());
                for (int i = 0; i < tmp.size(); i++) {
                    ValueBlock v = (ValueBlock) tmp.get(i);
                    set(i, v);
                }
                //pad with null entries ignored during sorting
                System.out.println("sort " + size() + "\t" + alist.size() + "\t" + newalist.size());
                if (tmp.size() < size()) {
                    for (int i = tmp.size(); i < size(); i++) {
                        set(i, null);
                        newalist.add(null);
                    }
                }
                System.out.println("sort " + size() + "\t" + alist.size() + "\t" + newalist.size());
            }

            return newalist;
        }
    */

    /**
     * @param v
     */
    public void addElements(ValueBlockList v) {
        for (int i = 0; i < v.size(); i++) {
            ValueBlock cur = (ValueBlock) v.get(i);
            add(cur);
        }
    }


    /**
     * TODO update overlap to account for area OR exps and genes separately
     *
     * @return
     */
    /*public double overlap(int[][] coords) {

        return valueBlockListMethods.overlap(coords);
    }
*/

    /**
     * Function to copy object contents of a vector.
     *
     * @param v
     * @return
     */
    public static ValueBlockList copyArrayList(ValueBlockList v) {
        ValueBlockList r = null;
        if (v != null) {
            r = initArrayList(v.size());
            for (int i = 0; i < v.size(); i++) {
                r.set(i, v.get(i));
            }
        }
        return r;
    }

    /**
     * Returns an ArrayList of the specified size.
     *
     * @param size
     * @return
     */
    public static ValueBlockList initArrayList(int size) {
        ValueBlockList ret = new ValueBlockList(size);
        for (int i = 0; i < size; i++)
            ret.add(null);
        return ret;
    }


    /**
     * @param v
     * @return
     */
    public int blockIndex(ValueBlock v) {
        for (int i = 0; i < size(); i++) {
            ValueBlock test = (ValueBlock) get(i);
            if (test.blockId() == v.blockId())
                return i;
        }

        return -1;
    }


}
