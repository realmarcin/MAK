package DataMining.util;

import DataMining.MINER_STATIC;
import DataMining.ValueBlock;
import DataMining.ValueBlockList;
import mathy.SimpleMatrix;
import util.DelimFile;
import util.MapArgOptions;
import util.MoreArray;
import util.TextFile;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: Jul 5, 2012
 * Time: 4:13:17 PM
 */
public class MarkOrder {

    String[] valid_args = {
            "-orderx", "-ordery", "-data", "-list", "-out", "-set"
    };
    HashMap options;

    int[] orderx;
    int[] ordery;

    String out;

    SimpleMatrix data;

    ValueBlockList list;
    ValueBlockList blockset;

    int[][] gmarginals, emarginals;


    /**
     * @param args
     */
    public MarkOrder(String[] args) {
        init(args);

        if (blockset == null)
            doLinearList();
        else {
            doSet();
        }
    }

    /**
     *
     */
    private void doSet() {
        String[] outdata = new String[list.size() + 1];
        outdata[0] = MINER_STATIC.HEADER_VBL + "\tgdist\tedist\tgedist";//"index\t" +);//ValueBlock.toStringShortColumns()

        for (int i = 0; i < list.size(); i++) {
            ValueBlock vcur = (ValueBlock) list.get(i);
            double gdist = 0;
            double gcount = 0;
            int glen = vcur.genes.length;

            //all unique gene pairs
            for (int a = 0; a < glen; a++) {
                for (int b = a + 1; b < glen; b++) {
                    gdist += (double) pairCooccur(vcur.genes[a], vcur.genes[b], true);
                    gcount++;
                }
            }

            //mean
            double meangdist = gdist / gcount;
            //normalize to baseline intra-cluster distance
            double grefdist = refDist(glen);
            double normgdist = meangdist / grefdist;
            //adding pseudocount and inverting for distance measure
            normgdist = 1.0 / (normgdist + 0.000000001);

            double edist = 0;
            double ecount = 0;
            int elen = vcur.exps.length;
            //all unique experiment pairs
            for (int a = 0; a < elen; a++) {
                for (int b = a + 1; b < elen; b++) {
                    edist += (double) pairCooccur(vcur.exps[a], vcur.exps[b], false);
                    ecount++;
                }
            }
            //mean
            double meanedist = edist / ecount;
//normalize to baseline intra-cluster distance
            double erefdist = refDist(elen);
            double normedist = meanedist / erefdist;
            //adding pseudocount and inverting for distance measure
            normedist = 1.0 / (normedist + 0.000000001);

            double gnumpair = (double) (vcur.genes.length * vcur.genes.length - vcur.genes.length) / 2.0;
            double enumpair = (double) (vcur.exps.length * vcur.exps.length - vcur.exps.length) / 2.0;

            System.out.println(i + "\t" + glen + "\tg count " + gcount + "\ttotal " + gnumpair + "\tdist " + gdist + "\tmean " + meangdist + "\tnorm " + normgdist + "\trefdist " + grefdist);
            System.out.println(i + "\t" + elen + "\te count " + ecount + "\ttotal " + enumpair + "\tdist " + edist + "\tmean " + meanedist + "\tnorm " + normedist + "\trefdist " + erefdist);

            outdata[i + 1] = "" + (i + 1) + "\t" + vcur.toStringShort() + "\t" + normgdist + "\t" + normedist + "\t" + (normgdist + normedist);
        }


        TextFile.write(outdata, out);
    }

    /**
     * @param a
     * @param b
     * @return
     */
    public int pairCooccur(int a, int b, boolean genes) {
        int ret = 0;

        for (int i = 0; i < blockset.size(); i++) {
            ValueBlock vb = (ValueBlock) blockset.get(i);
            try {
                if (genes && MoreArray.getArrayInd(vb.genes, a) != -1 && MoreArray.getArrayInd(vb.genes, b) != -1) {
                    ret++;
                } else if (!genes && MoreArray.getArrayInd(vb.exps, a) != -1 && MoreArray.getArrayInd(vb.exps, b) != -1) {
                    ret++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return ret;
    }


    /**
     *
     */
    public void countMarginals() {
        HashMap gcounts = new HashMap();
        HashMap ecounts = new HashMap();
        for (int i = 0; i < blockset.size(); i++) {
            ValueBlock vb = (ValueBlock) blockset.get(i);
            for (int a = 0; a < vb.genes.length; a++) {
                Object o = gcounts.get(vb.genes[a]);
                if (o == null) {
                    gcounts.put(vb.genes[a], 1);
                } else {
                    gcounts.put(vb.genes[a], (Integer) o + 1);
                }
            }
            for (int a = 0; a < vb.exps.length; a++) {
                Object o = ecounts.get(vb.exps[a]);
                if (o == null) {
                    ecounts.put(vb.exps[a], 1);
                } else {
                    ecounts.put(vb.exps[a], (Integer) o + 1);
                }
            }
        }

        Set hset = gcounts.entrySet();
        Iterator git = hset.iterator();
        gmarginals = new int[hset.size()][2];
        int gcounter = 0;
        while (git.hasNext()) {
            Map.Entry cur = (Map.Entry) git.next();
            int key = (Integer) cur.getKey();
            int count = (Integer) cur.getValue();
            gmarginals[gcounter][0] = key;
            gmarginals[gcounter][1] = count;
            gcounter++;
        }

        Set eset = gcounts.entrySet();
        Iterator eit = eset.iterator();
        emarginals = new int[eset.size()][2];
        int ecounter = 0;
        while (eit.hasNext()) {
            Map.Entry cur = (Map.Entry) eit.next();
            int key = (Integer) cur.getKey();
            int count = (Integer) cur.getValue();
            emarginals[ecounter][0] = key;
            emarginals[ecounter][1] = count;
            ecounter++;
        }
    }

    /**
     *
     */
    private void doLinearList() {
        String[] outdata = new String[list.size() + 1];
        outdata[0] = MINER_STATIC.HEADER_VBL + "\tgdist\tedist\tgedist";//"index\t" +  ValueBlock.toStringShortColumns() +
        for (int i = 0; i < list.size(); i++) {
            ValueBlock vcur = (ValueBlock) list.get(i);
            double gdist = 0;
            double gcount = 0;
            int glen = vcur.genes.length;
            //all unique gene pairs
            for (int a = 0; a < glen; a++) {
                try {
                    int aind = MoreArray.getArrayInd(ordery, vcur.genes[a]);
                    if (aind == -1) {
                        System.out.println("WARNING gindex a not found " + vcur.genes[a]);
                    } else {
                        for (int b = a + 1; b < glen; b++) {
                            int bind = MoreArray.getArrayInd(ordery, vcur.genes[b]);
                            if (bind == -1) {
                                System.out.println("WARNING gindex b not found " + vcur.genes[b]);
                            } else {
                                gdist += (double) Math.abs(aind - bind);
                                System.out.println(i + "\t" + gcount + "\t" + aind + "\t" + bind + "\t" + gdist);
                                gcount++;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //mean
            double meangdist = gdist / gcount;
            //normalize to baseline intra-cluster distance
            double grefdist = refDist(glen);
            double normgdist = meangdist / grefdist;

            double edist = 0;
            double ecount = 0;
            int elen = vcur.exps.length;
            //all unique experiment pairs
            for (int a = 0; a < elen; a++) {
                try {
                    int aind = MoreArray.getArrayInd(orderx, vcur.exps[a]);
                    if (aind == -1) {
                        System.out.println("WARNING eindex a not found " + vcur.exps[a]);
                    } else {
                        for (int b = a + 1; b < elen; b++) {
                            int bind = MoreArray.getArrayInd(orderx, vcur.exps[b]);
                            if (bind == -1) {
                                System.out.println("WARNING eindex b not found " + vcur.exps[b]);
                            } else {
                                edist += (double) Math.abs(aind - bind);
                                ecount++;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //mean
            double meanedist = edist / ecount;
            //normalize to baseline intra-cluster distance
            double erefdist = refDist(elen);
            double normedist = meanedist / erefdist;

            double gnumpair = (double) (vcur.genes.length * vcur.genes.length - vcur.genes.length) / 2.0;
            double enumpair = (double) (vcur.exps.length * vcur.exps.length - vcur.exps.length) / 2.0;

            System.out.println(i + "\t" + glen + "\tg count " + gcount + "\ttotal " + gnumpair + "\tdist " + gdist + "\tmean " + meangdist + "\tnorm " + normgdist + "\trefdist " + grefdist);
            System.out.println(i + "\t" + elen + "\te count " + ecount + "\ttotal " + enumpair + "\tdist " + edist + "\tmean " + meanedist + "\tnorm " + normedist + "\trefdist " + erefdist);

            outdata[i + 1] = "" + (i + 1) + "\t" + vcur.toStringShort() + "\t" + normgdist + "\t" + normedist + "\t" + (normgdist + normedist);
        }


        TextFile.write(outdata, out);
    }


    /**
     * @param n
     * @return
     */
    public double refDist(int n) {
        double ret = 0;
        double count = 0;
        for (int a = 0; a < n; a++) {
            for (int b = a + 1; b < n; b++) {
                ret += (double) Math.abs(a - b);
                count++;
            }
        }
        return ret / count;
    }

    /**
     * @param args
     */

    private void init(String[] args) {

        MoreArray.printArray(args);
        options = MapArgOptions.maptoMap(args, valid_args);

        if (options.get("-orderx") != null) {
            String s = (String) options.get("-orderx");
            String[][] da = DelimFile.readtoArray(s, " ", false);
            orderx = MoreArray.extractColumnInt(da, 2);
            System.out.println("orderx len " + orderx.length + "\t" + MoreArray.toString(orderx, ","));
        }
        if (options.get("-ordery") != null) {
            String s = (String) options.get("-ordery");
            String[][] da = DelimFile.readtoArray(s, " ", false);
            ordery = MoreArray.extractColumnInt(da, 2);
            System.out.println("ordery len " + ordery.length);
        }
        if (options.get("-data") != null) {
            String d = (String) options.get("-data");
            data = new SimpleMatrix(d);

            System.out.println("data len " + data.data.length + "\t" + data.data[0].length);
        }
        if (options.get("-list") != null) {
            String s = (String) options.get("-list");
            try {
                list = ValueBlockList.read(s, false);
                System.out.println("list len " + list.size());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (options.get("-out") != null) {
            out = (String) options.get("-out");
        }
        if (options.get("-set") != null) {
            String s = (String) options.get("-set");
            try {
                blockset = ValueBlockList.read(s, false);

                countMarginals();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * @param args
     */
    public final static void main(String[] args) {
        if (args.length == 10 || args.length == 8) {
            MarkOrder rm = new MarkOrder(args);
        } else {
            System.out.println("syntax: java DataMining.util.MarkOrder\n" +
                    "<-orderx global x-axis order>\n" +
                    "<-ordery global y-axis order>\n" +
                    "<-set bicluster set (exclusive with orderx and y)r>\n" +
                    "<-data source data>\n" +
                    "<-list of biclusters>\n" +
                    "<-out output>"
            );
        }
    }
}
