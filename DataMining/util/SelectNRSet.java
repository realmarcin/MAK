package DataMining.util;

import DataMining.*;
import util.MapArgOptions;
import util.MoreArray;
import util.ParsePath;
import util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: 6/11/14
 * Time: 8:19 PM
 * <p/>
 * <p/>
 * Identifies a representative bicluster set from a larger set, based on MIN_OVERLAP, score and area.
 */
public class SelectNRSet {

    boolean debug = false;

    String[] valid_args = {
            "-bic", "-over", "-out", "-mode", "-type", "-debug"};
    HashMap options;

    ValueBlockList vbl;

    double MIN_OVERLAP = 0.25;
    String outpath = null;
    String mode = "score";
    String prefix;

    String type = "root";//frxn
    boolean do_root = true;

    String inpath;

    /**
     * @param args
     */
    public SelectNRSet(String[] args) {

        init(args);

        int size = vbl.size();

        ArrayList hashes = new ArrayList();
        HashMap score = new HashMap();
        HashMap area = new HashMap();
        HashMap lengths = new HashMap();
        //build gene_exp hash
        for (int i = 0; i < size; i++) {
            if (i % 100 == 0)
                System.out.print(i + "/" + vbl.size() + " ");
            ValueBlock vi = (ValueBlock) vbl.get(i);
            score.put(i, vi.full_criterion);
            area.put(i, vi.genes.length * vi.exps.length);
            int[] add = {vi.genes.length, vi.exps.length};
            lengths.put(i, add);
            HashMap hvi = new HashMap();
            for (int g = 0; g < vi.genes.length; g++) {
                for (int e = 0; e < vi.exps.length; e++) {
                    hvi.put("" + vi.genes[g] + "_" + vi.exps[e], 1);
                }
            }
            hashes.add(hvi);
        }

        ValueBlockList select = new ValueBlockList();
        int[] mask = new int[size];
        //pairwise bicluster MIN_OVERLAP
        for (int i = 0; i < size; i++) {
            if (mask[i] != 1) {
                if (i % 100 != 0)
                    System.out.print(i + "-" + select.size() + " ");
                else
                    System.out.print(i + "-" + select.size() + "\n");
                int greatarea = 0, greatscore = 0, greatareascore = 0, overlap = 0;
                //System.out.print(".");
                HashMap hi = (HashMap) hashes.get(i);
                //ValueBlock vi = (ValueBlock) vbl.get(i);
                for (int j = 0; j < size; j++) {
                    if (i != j && mask[j] != 1) {
                        if (debug)
                            System.out.println("ij " + i + "\t" + j);
                        //ValueBlock vj = (ValueBlock) vbl.get(j);
                        HashMap hj = (HashMap) hashes.get(j);
                        int[] lensi = (int[]) lengths.get(i);
                        int[] lensj = (int[]) lengths.get(j);
                        double overlapthis = Double.NaN;
                        if (do_root) {
                            overlapthis = BlockMethods.computeBlockOverlapGeneExpRootProduct(hi, hj,
                                    lensi[0], lensi[1], lensj[0], lensj[1]);

                        } else {
                            overlapthis = BlockMethods.computeBlockOverlapGeneExpFraction(hi, hj,
                                    lensi[0], lensi[1], lensj[0], lensj[1]);
                        }

                        if (overlapthis > MIN_OVERLAP) {
                            if (debug) {
                                System.out.println("overlap > " + MIN_OVERLAP + "\t" + i + "\t" + j + "\t" + overlapthis);
                            }
                            overlap++;
                            double scorei = (Double) score.get(i);
                            double scorej = (Double) score.get(j);
                            double areai = (double) (Integer) area.get(i);
                            double areaj = (double) (Integer) area.get(j);
                            //if (debug)
                            //   System.out.println("areai " + areai + "\tareaj " + areaj);
                            if (debug)
                                System.out.println("scorei " + scorei + "\tscorej " + scorej);

                            boolean scoreY = false;
                            boolean areaY = false;

                            if (scorei < scorej) {
                                greatscore++;
                                if (debug)
                                    System.out.println("greatscore " + scorei + "\t" + scorej);

                                scoreY = true;
                            } else if (scorei == scorej && (mode.equals("score") || mode.equals("sizescore"))) {
                                mask[j] = 1;

                                if (debug)
                                    System.out.println("mask score " + j);
                            }

                            if (areai < areaj) {
                                greatarea++;
                                if (debug)
                                    System.out.println("greatarea " + areai + "\t" + areaj);

                                areaY = true;
                            } else if (areai == areaj && (mode.equals("size") || mode.equals("sizescore"))) {
                                mask[j] = 1;
                                if (debug)
                                    System.out.println("mask area " + j);
                            }


                            if (scoreY && areaY) {
                                greatareascore++;
                                if (debug)
                                    System.out.println("greatareascore");

                            }
                        }
                    }
                }
                if (debug)
                    System.out.println("summary " + i + "\tgreatscore\t" + greatscore +
                            "\tgreatarea\t" + greatarea +
                            "\tgreatareascore\t" + greatareascore);

                //if none overlapping > MIN_OVERLAP have a higher score
                if (mode.equals("score") && greatscore == 0) {//greatareascore == 0 &&
                    ValueBlock vi = (ValueBlock) vbl.get(i);
                    if (!select.contains(vi))
                        select.add(vi);
                    if (debug)
                        System.out.println("adding " + i + "\tscore greatscore = 0");
                } else if (mode.equals("size") && greatarea == 0) {//greatareascore == 0 &&
                    ValueBlock vi = (ValueBlock) vbl.get(i);
                    if (!select.contains(vi))
                        select.add(vi);
                    if (debug)
                        System.out.println("adding " + i + "\tsize greatarea = 0");
                } else if (mode.equals("sizescore") && greatareascore == 0) {//greatareascore == 0 &&
                    ValueBlock vi = (ValueBlock) vbl.get(i);
                    if (!select.contains(vi))
                        select.add(vi);
                    if (debug)
                        System.out.println("adding " + i + "\tsizescore greatareascore = 0");
                }


                if (debug)
                    System.out.println("greatscore " + greatscore + "\tgreatarea " + greatarea +
                            "\tgreatscorearea " + greatareascore + "\tMIN_OVERLAP " + overlap);
            }
        }


        System.out.println("\nnr set " + select.size());
        System.out.println("outpath 1 :" + outpath + ":");
        if (outpath == null || outpath == "" || outpath.length() == 0) {
            ParsePath pp = new ParsePath(inpath);
            //String s = pp.getName();
            String prefixpath = "";
            if (pp.getPath() != null && pp.getPath() != "") {
                prefixpath = pp.getPath() + "/";
            }
            System.out.println("prefixpath " + prefixpath);
            outpath = prefixpath + prefix + "_nr_" + MIN_OVERLAP + "_" + mode + "_" + type + ".txt";
        }

        System.out.println("sorting by full criterion!");
        ValueBlock first = (ValueBlock) select.get(0);

        Collections.sort(select);
        ValueBlock first2 = (ValueBlock) select.get(0);

        System.out.println("before " + first.full_criterion);
        System.out.println("after " + first2.full_criterion);

        String s = select.toString("#" + MINER_STATIC.HEADER_VBL);//ValueBlock.toStringShortColumns());
        System.out.println("outpath 2 :" + outpath + ":");
        util.TextFile.write(s, outpath);


    }

    /**
     * @param args
     */
    private void init(String[] args) {
        MoreArray.printArray(args);

        options = MapArgOptions.maptoMap(args, valid_args);

        if (options.get("-bic") != null) {
            inpath = null;
            try {
                inpath = (String) options.get("-bic");
                vbl = ValueBlockListMethods.readAny(inpath, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ParsePath pp = new ParsePath(inpath);
            prefix = pp.getName() + "_";
        }
        if (options.get("-over") != null) {
            MIN_OVERLAP = Double.parseDouble((String) options.get("-over"));
        }
        if (options.get("-out") != null) {
            outpath = (String) options.get("-out");
        }
        if (options.get("-mode") != null) {
            mode = (String) options.get("-mode");
        }
        if (options.get("-type") != null) {
            type = (String) options.get("-type");
            if (type.equals("root"))
                do_root = true;
            else
                do_root = false;
        }
        if (options.get("-debug") != null) {
            String cur = (String) options.get("-debug");
            debug = StringUtil.isTrueorYes(cur);
        }
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length >= 2 && args.length <= 12) {
            SelectNRSet rm = new SelectNRSet(args);
        } else {
            System.out.println("syntax: java DataMining.util.SelectNRSet\n" +
                    "<-bic biclusters file>\n" +
                    "<-over min overlap>\n" +
                    "<-out out path>\n" +
                    "<-mode (score, size, scoresize)>\n" +
                    "<-type (root, frxn)>\n" +
                    "<-debug (y, n)>"
            );
        }
    }
}
