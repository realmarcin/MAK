package DataMining;


import mathy.stat;
import util.MoreArray;

import java.io.File;
import java.util.ArrayList;

/**
 * User: marcin
 * Date: Aug 26, 2010
 * Time: 5:00:18 PM
 */
public class CheckTraj {

    double batch_perc = 0.5;
    int good = 0;
    boolean debug = false;

    int violateG = 0;
    int violateE = 0;
    int maxG = 0;
    int maxE = 0;

    ValueBlockList vbl;

    boolean requireOverlap = false;
    boolean print = false;

    /**
     *
     */
    public CheckTraj() {

    }

    /**
     *
     */
    public CheckTraj(boolean d) {
        debug = d;
    }

    /**
     *
     */
    public CheckTraj(double p, boolean d) {
        debug = d;
        batch_perc = p;
    }

    /**
     *
     */
    public void init() {
        violateG = 0;
        violateE = 0;
        maxG = 0;
        maxE = 0;
    }

    /**
     *
     */
    public CheckTraj(String[] args) {
        print = true;
        File dir = new File(args[0]);
        String[] list = dir.list();

        if (args.length == 2)
            if (args[1].equalsIgnoreCase("y")) {
                requireOverlap = true;
            } else if (args[1].equalsIgnoreCase("n")) {
                requireOverlap = false;
            }

        int all = 0;
        for (int i = 0; i < list.length; i++) {
            if (list[i].indexOf("toplist") != -1) {
                all++;
                if (debug) {
                    System.out.println("TEST");
                    System.out.println(list[i]);
                }
                run(args[0] + "/" + list[i]);
            }
        }
        System.out.println("GOOD " + good + "\tBAD " + (all - good));
    }


    /**
     * @param arg
     * @return
     */
    public boolean run(String arg, boolean r) {
        requireOverlap = r;
        return run(arg);
    }

    /**
     * @param arg
     * @return
     */
    public boolean run(String arg) {

        init();

        try {
            vbl = ValueBlockList.read(arg, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ValueBlock first = (ValueBlock) vbl.get(0);
        ValueBlock last = (ValueBlock) vbl.get(vbl.size() - 1);
        double overlap = BlockMethods.computeBlockOverlapGeneAndExpWithRef(first, last);
        if (!requireOverlap || overlap > 0) {
            for (int j = 1; j < vbl.size(); j++) {
                ValueBlock prev = (ValueBlock) vbl.get(j - 1);
                ValueBlock cur = (ValueBlock) vbl.get(j);

                int diffg = Math.abs(cur.genes.length - prev.genes.length);
                int diffe = Math.abs(cur.exps.length - prev.exps.length);
                int maxBatchG = (int) (batch_perc * prev.genes.length);
                int maxBatchE = (int) (batch_perc * prev.exps.length);
                if (diffg > maxG) {
                    maxG = diffg;
                    if (debug) {
                        System.out.println("new max G " + maxG + "\tmax allowed " + maxBatchG + "\tprev " +
                                prev.genes.length + "\tcur" + cur.genes.length + "\t" + cur.move_class);
                    }
                }
                if (diffe > maxE) {
                    maxE = diffe;
                    if (debug) {
                        System.out.println("new max E " + maxE + "\tmax allowed " + maxBatchE + "\tprev " +
                                prev.exps.length + "\tcur " + cur.exps.length + "\t" + cur.move_class);
                    }

                }

                if (diffg > maxBatchG) {
                    violateG++;
                }

                if (diffe > maxBatchE) {
                    violateE++;
                }
            }
            if (violateG == 0 && violateE == 0) {
                good++;
                if (debug) {
                    System.out.println("OK");
                    System.out.println(arg);
                    System.out.println("max " + maxG + "\t" + maxE);
                } else if (print) {
                    System.out.println(arg);
                }
                return true;
            }
        } else {
            System.out.println("violated overlap " + overlap);
        }
        if (debug) {
            if (violateG != 0)
                System.out.println("violateG " + violateG + "\t" + maxG);
            if (violateE != 0)
                System.out.println("violateE " + violateE + "\t" + maxE);
        }
        return false;
    }

    /**
     * @return
     */
    public int[] getPos(int trajIndex, double[][] data) {
        ValueBlock v = (ValueBlock) vbl.get(trajIndex);
        ArrayList pos = new ArrayList();
        for (int i = 0; i < v.genes.length; i++) {
            double[] row = MoreArray.getSub(data[v.genes[i] - 1], v.exps[0] - 1, v.exps[v.exps.length - 1] - 1);
            double m = stat.avg(row);
            if (m > 0) {
                pos.add(v.genes[i]);
            }
        }
        return MoreArray.ArrayListtoInt(pos);
    }

    /**
     * @return
     */
    public int[] getNeg(int trajIndex, double[][] data) {
        ValueBlock v = (ValueBlock) vbl.get(trajIndex);
        ArrayList pos = new ArrayList();
        for (int i = 0; i < v.genes.length; i++) {
            double[] row = MoreArray.getSub(data[v.genes[i] - 1], v.exps[0] - 1, v.exps[v.exps.length - 1] - 1);
            double m = stat.avg(row);
            if (m < 0) {
                pos.add(v.genes[i]);
            }
        }
        return MoreArray.ArrayListtoInt(pos);
    }

    /**
     * @param args
     */

    public static void main(String[] args) {
        if (args.length == 1 || args.length == 2) {
            CheckTraj rm = new CheckTraj(args);
        } else {
            System.out.println("syntax: java DataMining.CheckTraj\n" +
                    "<toplist dir>\n" +
                    "<OPTIONAL y/n require first and last nonzero overlap>\n"
            );
        }
    }
}
