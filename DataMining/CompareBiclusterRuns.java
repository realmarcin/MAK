package DataMining;

import mathy.Matrix;
import util.MapArgOptions;
import util.MoreArray;
import util.ParsePath;

import java.io.File;
import java.util.HashMap;

/**
 * User: marcinjoachimiak
 * Date: Mar 27, 2008
 * Time: 12:59:05 PM
 */
public class CompareBiclusterRuns {

    boolean debug = false;

    String[] valid_args = {
            "-one", "-two", "-dir", "-mode", "-type"
    };
    HashMap options;

    String[] xlabels, ylabels;
    //String[] gene_labels, exp_labels;

    int label_offset = 1;

    String mode = "fraction";
    String listone;
    String listtwo;
    String listdir;
    String type = "all";


    /**
     * @param args
     */
    public CompareBiclusterRuns(String[] args) {

        init(args);

        //SimpleMatrix data = new SimpleMatrix(args[args.length - 1]);

        if (listone != null)
            label_offset = 2;

        /*for (int i = 0; i < data.ylabels.length; i++) {
            data.ylabels[i] = StringUtil.replace(data.ylabels[i], "\"", "");
            //System.out.println("gene_names " + i + "\t" + gene_labels[i - 1]);
        }

        for (int i = 0; i < data.xlabels.length; i++) {
            data.xlabels[i] = StringUtil.replace(data.xlabels[i], "\"", "");
            //System.out.println("gene_names " + i + "\t" + exp_labels[i - 1]);
        }
*/
        if (listone != null && listtwo != null) {
            ValueBlockList one = null;//, data.xlabels, data.ylabels);
            try {
                one = ValueBlockListMethods.readAny(listone);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ValueBlockList two = null;//, data.xlabels, data.ylabels);
            try {
                two = ValueBlockListMethods.readAny(listtwo);
            } catch (Exception e) {
                e.printStackTrace();
            }

            ParsePath pp0 = new ParsePath(listone);
            ParsePath pp1 = new ParsePath(listtwo);
            final String outf = pp0.getFile() + "_vs_" + pp1.getFile() + ".overlap";
            double[][] overlap = comparePairofSets(one, two);
            outputMatrix(outf, overlap);
        } else if (listdir != null) {
            File dir = new File(listdir);
            String[] files = dir.list();
            for (int i = 0; i < files.length; i++) {
                if (!files[i].equals(".DS_Store")) {
                    String first = listdir + "/" + files[i];
                    for (int j = i; j < files.length; j++) {
                        if (!files[j].equals(".DS_Store")) {
                            String second = listdir + "/" + files[j];
                            ValueBlockList one = null;
                            try {
                                one = ValueBlockListMethods.readAny(first, false);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            ValueBlockList two = null;
                            try {
                                two = ValueBlockListMethods.readAny(second, false);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (one != null && two != null) {
                                String outf = files[i] + "_vs_" + files[j] + ".overlap";
                                double[][] overlap = comparePairofSets(one, two);
                                if (outf.length() > 240) {
                                    System.out.println("truncating\n" + outf);
                                    int length = files[j].length();
                                    outf = files[i] + "_vs_" + files[j].substring(0, length - (length - 240)) + ".overlap";
                                    System.out.println("truncating\n" + outf);
                                }

                                outputMatrix(outf, overlap);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * @param one
     * @param two
     */
    private double[][] comparePairofSets(ValueBlockList one, ValueBlockList two) {
        System.out.println("comparePairofSets sizes " + one.size() + "\t" + two.size());

        xlabels = new String[two.size()];
        for (int i = 0; i < two.size(); i++) {
            xlabels[i] = "" + (i + 1);
        }
        ylabels = new String[one.size()];
        for (int i = 0; i < one.size(); i++) {
            ylabels[i] = "" + (i + 1);
        }

        double[][] ret = null;
        if (type.equalsIgnoreCase("all")) {
            if (mode.equals("fraction"))
                ret = ValueBlockListMethods.compareFractionMap(one, two);
            else if (mode.equals("rootproduct"))
                ret = ValueBlockListMethods.compareRootProductMap(one, two);
        } else if (type.equalsIgnoreCase("exp")) {
            if (mode.equals("fraction"))
                ret = ValueBlockListMethods.compareFractionExp(one, two);
            else if (mode.equals("rootproduct"))
                ret = ValueBlockListMethods.compareRootProductExp(one, two);
        } else if (type.equalsIgnoreCase("gene")) {
            if (mode.equals("fraction"))
                ret = ValueBlockListMethods.compareFractionGene(one, two);
            else if (mode.equals("rootproduct"))
                ret = ValueBlockListMethods.compareRootProductGene(one, two);
        }
        return ret;
    }

    /**
     * @param outf
     * @param overlap
     */
    private void outputMatrix(String outf, double[][] overlap) {
        Matrix data = new Matrix(overlap);
        data.xlabels = xlabels;
        data.ylabels = ylabels;
        data.writeBlock(outf, "#\n");
    }

    /**
     * @param args
     */
    private void init(String[] args) {
        MoreArray.printArray(args);

        options = MapArgOptions.maptoMap(args, valid_args);

        if (options.get("-mode") != null) {
            String f = (String) options.get("-mode");
            if (f.equals("fraction"))
                mode = f;
            else if (f.equals("rootproduct"))
                mode = f;
        }

        if (options.get("-one") != null) {
            String f = (String) options.get("-one");
            listone = f;
            if (options.get("-two") != null) {
                String f2 = (String) options.get("-two");
                listtwo = f2;
            }
        } else if (options.get("-dir") != null) {
            String f = (String) options.get("-dir");
            listdir = f;
        }

        if (options.get("-type") != null) {
            String f = (String) options.get("-type");
            type = f;
        }


        System.out.println("mode " + mode + "\ttype " + type);
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 4 || args.length == 6 || args.length == 8) {
            CompareBiclusterRuns cbr = new CompareBiclusterRuns(args);
        } else {
            System.out.println("syntax: DataMining.CompareBiclusterRuns <dir of bicluster results in .bic format>\n" +
                    "<-one valueblock list>\n" +
                    "<-two valueblock list>\n" +
                    "OR\n" +
                    "<-dir dir of valueblock lists>\n" +
                    "<-mode fraction (default, asymetric) or rootproduct (symmetric) OPTIONAL>\n" +
                    "<-type all (default), gene, exp OPTIONAL>");
        }
    }
}
