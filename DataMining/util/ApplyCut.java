package DataMining.util;

import DataMining.ValueBlock;
import DataMining.ValueBlockList;
import DataMining.ValueBlockListMethods;
import util.MoreArray;
import util.ParsePath;
import util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: Jan 10, 2012
 * Time: 10:05:43 PM
 */
public class ApplyCut {

    boolean debug = false;

    double scorecut = Double.NaN;//0.99;
    double scoreperccut = Double.NaN;//95;

    double exprcut = Double.NaN;
    double exprperccut = Double.NaN;

    double startperccut = 0;//0.2;

    double[] expr_means, sorted_expr_means;

    /**
     * @param args
     */
    public ApplyCut(String[] args) {

        String cutlabel = "";
        if (args[1].indexOf("%") == -1) {
            try {
                scorecut = Double.parseDouble(args[1]);
            } catch (NumberFormatException e) {
                //e.printStackTrace();
            }
            cutlabel = "score" + scorecut;
        } else {
            try {
                scoreperccut = Double.parseDouble(args[1].substring(0, args[1].length() - 1));
            } catch (NumberFormatException e) {
                //e.printStackTrace();
            }
            cutlabel = "scoreperc" + scoreperccut;
        }

        if (args[2].indexOf("%") == -1) {
            try {
                exprcut = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                //e.printStackTrace();
            }
            cutlabel += "_expr" + exprcut;
        } else {
            try {
                exprperccut = Double.parseDouble(args[2].substring(0, args[2].length() - 1));
            } catch (NumberFormatException e) {
                //e.printStackTrace();
            }
            cutlabel += "_exprperc" + exprperccut;
        }

        System.out.println("cutlabel " + cutlabel);

        if (args.length == 4) {
            startperccut = Double.parseDouble(args[3]);
        }
        System.out.println("ApplyCut " + scorecut + "\t" + scoreperccut + "\t" + exprcut + "\t" + exprperccut + "\t" + startperccut);
        ValueBlockList vbl = null;
        ValueBlockList finallist = null;
        try {
            vbl = ValueBlockList.read(args[0], debug);

        } catch (Exception e) {
            e.printStackTrace();
        }


        if (!Double.isNaN(exprperccut)) {
            expr_means = extractExprMeans(vbl);
            sorted_expr_means = MoreArray.copy(expr_means);
            Arrays.sort(sorted_expr_means);
            //sorted_expr_means = MoreArray.reverse(sorted_expr_means);
        }

        double[] scores = new double[vbl.size()];
        for (int i = 0; i < vbl.size(); i++) {
            ValueBlock v = (ValueBlock) vbl.get(i);
            if (!Double.isNaN(v.full_criterion)) {
                scores[i] = v.full_criterion;
            } else {
                System.out.println("full criterion is NaN " + i);
            }
        }
        //sort ascending
        Arrays.sort(scores);
        String[] unique = StringUtil.makeUnique(MoreArray.toStringArray(scores));
        double[] uniqueD = MoreArray.convfromString(unique);


        finallist = processList(vbl, finallist, uniqueD);


        write(args[0], cutlabel, finallist.toString(vbl.header));

    }

    /**
     *
     * @param vbl
     * @param finallist
     * @param uniqueD
     */
    private ValueBlockList processList (ValueBlockList vbl, ValueBlockList finallist, double[] uniqueD) {
        if (!Double.isNaN(scorecut) && Double.isNaN(exprcut) && Double.isNaN(exprperccut)) {
            System.out.println("score cut");
            for (int i = 0; i < vbl.size(); i++) {
                ValueBlock v = (ValueBlock) vbl.get(i);
                if (v.full_criterion < scorecut) {
                    System.out.println("1 " + v.full_criterion + " < " + scorecut);
                    vbl.remove(i);
                    i--;
                } else if (v.percentOrigGenes < startperccut) {
                    System.out.println("v.percentOrigGenes " + v.percentOrigGenes);
                    vbl.remove(i);
                    i--;
                } else if (v.percentOrigExp < startperccut) {
                    System.out.println("v.percentOrigExp " + v.percentOrigExp);
                    vbl.remove(i);
                    i--;
                }
                System.out.println("1 " + vbl.size());
            }
            finallist = makeFinal(uniqueD, vbl);
        } else if (!Double.isNaN(scoreperccut) && Double.isNaN(exprcut) && Double.isNaN(exprperccut)) {
            System.out.println("score % cut");
            for (int i = 0; i < vbl.size(); i++) {
                ValueBlock v = (ValueBlock) vbl.get(i);
                int index = MoreArray.getArrayInd(uniqueD, v.full_criterion);
                double v1 = 100.0 * (double) index / (double) uniqueD.length;
                System.out.println(v1);
                if (v1 < scoreperccut) {
                    //if (v.full_criterion < scorecut) {
                    System.out.println("2 " + v1 + " < " + scoreperccut + "\t" + v.full_criterion);
                    vbl.remove(i);
                    i--;
                } else if (v.percentOrigGenes < startperccut) {
                    System.out.println("v.percentOrigGenes " + v.percentOrigGenes);
                    vbl.remove(i);
                    i--;
                } else if (v.percentOrigExp < startperccut) {
                    System.out.println("v.percentOrigExp " + v.percentOrigExp);
                    vbl.remove(i);
                    i--;
                }
                System.out.println("2 " + vbl.size());
            }
            finallist = makeFinal(uniqueD, vbl);
        } else if (!Double.isNaN(exprcut) && Double.isNaN(scorecut) && Double.isNaN(scoreperccut)) {
            System.out.println("expr cut");
            for (int i = 0; i < vbl.size(); i++) {
                ValueBlock v = (ValueBlock) vbl.get(i);
                if (v.exp_mean < exprcut) {
                    System.out.println("3 " + v.exp_mean + " < " + exprcut);
                    vbl.remove(i);
                    i--;
                } else if (v.percentOrigGenes < startperccut) {
                    System.out.println("v.percentOrigGenes " + v.percentOrigGenes);
                    vbl.remove(i);
                    i--;
                } else if (v.percentOrigExp < startperccut) {
                    System.out.println("v.percentOrigExp " + v.percentOrigExp);
                    vbl.remove(i);
                    i--;
                }
                System.out.println("3 " + vbl.size());
            }
            finallist = makeFinal(uniqueD, vbl);
        } else if (!Double.isNaN(exprperccut) && Double.isNaN(scorecut) && Double.isNaN(scoreperccut)) {
            System.out.println("expr % cut");
            for (int i = 0; i < vbl.size(); i++) {
                ValueBlock v = (ValueBlock) vbl.get(i);
                int index2 = MoreArray.getArrayInd(sorted_expr_means, v.exp_mean);//sorted_expr_means.length -
                double v2 = 100.0 * (double) index2 / (double) uniqueD.length;
                if (v2 < exprperccut) {
                    System.out.println("4 " + v2 + " < " + exprperccut);
                    vbl.remove(i);
                    i--;
                } else if (v.percentOrigGenes < startperccut) {
                    System.out.println("v.percentOrigGenes " + v.percentOrigGenes);
                    vbl.remove(i);
                    i--;
                } else if (v.percentOrigExp < startperccut) {
                    System.out.println("v.percentOrigExp " + v.percentOrigExp);
                    vbl.remove(i);
                    i--;
                }
                System.out.println("4 " + vbl.size());
            }
            finallist = makeFinal(uniqueD, vbl);
        } else if (!Double.isNaN(scorecut) && !Double.isNaN(exprcut)) {
            System.out.println("score cut & expr cut");
            for (int i = 0; i < vbl.size(); i++) {
                ValueBlock v = (ValueBlock) vbl.get(i);
                if (v.full_criterion < scorecut && v.exp_mean < exprcut) {
                    System.out.println("5 " + v.full_criterion + " < " + scorecut + "\t" + v.exp_mean + " < " + exprcut);
                    vbl.remove(i);
                    i--;
                } else if (v.percentOrigGenes < startperccut) {
                    System.out.println("v.percentOrigGenes " + v.percentOrigGenes);
                    vbl.remove(i);
                    i--;
                } else if (v.percentOrigExp < startperccut) {
                    System.out.println("v.percentOrigExp " + v.percentOrigExp);
                    vbl.remove(i);
                    i--;
                }
                System.out.println("5 " + vbl.size());
            }
            finallist = makeFinal(uniqueD, vbl);
        }
        //expression percentile cutoff
        //else if (!Double.isNaN(exprcut)) {
        else if (!Double.isNaN(scorecut) && !Double.isNaN(exprperccut)) {
            System.out.println("score cut & expr % cut");
            for (int i = 0; i < vbl.size(); i++) {
                ValueBlock v = (ValueBlock) vbl.get(i);
                int index2 = MoreArray.getArrayInd(sorted_expr_means, v.exp_mean);//sorted_expr_means.length -
                double v2 = 100.0 * (double) index2 / (double) uniqueD.length;
                if (v.full_criterion < scorecut && v2 < exprperccut) {
                    System.out.println("6 " + v.full_criterion + " < " + scorecut + "\t" + v2 + " < " + exprcut);
                    vbl.remove(i);
                    i--;
                } else if (v.percentOrigGenes < startperccut) {
                    System.out.println("v.percentOrigGenes " + v.percentOrigGenes);
                    vbl.remove(i);
                    i--;
                } else if (v.percentOrigExp < startperccut) {
                    System.out.println("v.percentOrigExp " + v.percentOrigExp);
                    vbl.remove(i);
                    i--;
                }
                System.out.println("6 " + vbl.size());
            }
            finallist = makeFinal(uniqueD, vbl);
        } else if (!Double.isNaN(scoreperccut) && !Double.isNaN(exprcut)) {
            System.out.println("score % cut & expr cut");
            for (int i = 0; i < vbl.size(); i++) {
                ValueBlock v = (ValueBlock) vbl.get(i);
                int index = MoreArray.getArrayInd(uniqueD, v.full_criterion);
                double v1 = 100.0 * (double) index / (double) uniqueD.length;
                if (v1 < scoreperccut && v.exp_mean < exprcut) {
                    System.out.println("7 " + v1 + " < " + scoreperccut + "\t" + v.exp_mean + " < " + exprcut);
                    vbl.remove(i);
                    i--;
                } else if (v.percentOrigGenes < startperccut) {
                    System.out.println("v.percentOrigGenes " + v.percentOrigGenes);
                    vbl.remove(i);
                    i--;
                } else if (v.percentOrigExp < startperccut) {
                    System.out.println("v.percentOrigExp " + v.percentOrigExp);
                    vbl.remove(i);
                    i--;
                }
                System.out.println("7 " + vbl.size());
            }
            finallist = makeFinal(uniqueD, vbl);
        }
        //score percentile cutoff
        else if (!Double.isNaN(scoreperccut) && !Double.isNaN(exprperccut)) {
            System.out.println("score % cut & expr & cut");
            for (int i = 0; i < vbl.size(); i++) {
                ValueBlock v = (ValueBlock) vbl.get(i);
                int index = MoreArray.getArrayInd(uniqueD, v.full_criterion);
                double v1 = 100.0 * (double) index / (double) uniqueD.length;
                int index2 = MoreArray.getArrayInd(sorted_expr_means, v.exp_mean);// sorted_expr_means.length -
                double v2 = 100.0 * (double) index2 / (double) uniqueD.length;
                if (v1 < scoreperccut && v2 < exprperccut) {
                    System.out.println("8 " + v1 + " < " + scoreperccut + "\t" + v2 + " < " + exprperccut);
                    vbl.remove(i);
                    i--;
                } else if (v.percentOrigGenes < startperccut) {
                    System.out.println("v.percentOrigGenes " + v.percentOrigGenes);
                    vbl.remove(i);
                    i--;
                } else if (v.percentOrigExp < startperccut) {
                    System.out.println("v.percentOrigExp " + v.percentOrigExp);
                    vbl.remove(i);
                    i--;
                }
                System.out.println("8 " + vbl.size());
            }
            finallist = makeFinal(uniqueD, vbl);
        }
        return finallist;
    }

    /**
     *
     * @param arg
     * @param cutlabel
     * @param out1
     */
    private void write(String arg, String cutlabel, String out1) {
        try {
            String a = arg;
            ParsePath pp = new ParsePath(a);

            String s = pp.getName() + "_cut_" + cutlabel + "_" + startperccut + ".txt";
            if (pp.getPath() != null && pp.getPath() != "") {
                String prefix = pp.getPath() + "/";
                s = prefix + s;
            }

            System.out.println("writing " + s);
            String out = out1;//+ ValueBlock.toStringShortColumns()
            util.TextFile.write(out, s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param v
     * @return
     */
    public double[] extractExprMeans(ValueBlockList v) {

        ArrayList a = new ArrayList();
        for (int i = 0; i < v.size(); i++) {
            ValueBlock cur = (ValueBlock) v.get(i);
            a.add(cur.exp_mean);
        }

        return MoreArray.ArrayListtoDouble(a);
    }

    /**
     * @param uniqueD
     * @param vbl
     * @return
     */
    private ValueBlockList makeFinal(double[] uniqueD, ValueBlockList vbl) {
        System.out.println("makeFinal");
        ArrayList tmp = new ArrayList(uniqueD.length);
        for (int i = 0; i < uniqueD.length; i++) {
            tmp.add(new ValueBlockList());
        }

        for (int i = 0; i < vbl.size(); i++) {
            ValueBlock v = (ValueBlock) vbl.get(i);
            int index = MoreArray.getArrayInd(uniqueD, v.full_criterion);
            if (index == -1)
                System.out.println("crit value not found in unique list " + v.index + "\t" + v.full_criterion + "\t" + v.blockId());
            ValueBlockList vcur = (ValueBlockList) tmp.get(index);
            vcur.add(v);
            tmp.set(index, vcur);
        }
        ValueBlockList finallist = new ValueBlockList();
        for (int i = 0; i < tmp.size(); i++) {
            ValueBlockList vcur = (ValueBlockList) tmp.get(i);
            for (int j = 0; j < vcur.size(); j++) {
                ValueBlock v = (ValueBlock) vcur.get(j);
                finallist.add(v);
            }
        }

        finallist = ValueBlockListMethods.reverse(finallist);
        return finallist;
    }


    /**
     * @param args
     */

    public final static void main(String[] args) {

        if (args.length == 3 || args.length == 4) {
            ApplyCut rm = new ApplyCut(args);
        } else {
            System.out.println("syntax: java DataMining.util.ApplyCut\n" +
                    "<trajectory output>\n" +
                    "< min. score cutoff ie 0.5 OR min. percentile cutoff ie '95%'>\n" +
                    "<mean expression cutoff ie '0.5' OR mean expression cutoff ie '95%'>\n" +
                    "<OPTIONAL fraction of start cutoff ie 0.05'>"
            );
        }
    }
}
