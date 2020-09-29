package DataMining.util;

import DataMining.BlockMethods;
import DataMining.ValueBlock;
import DataMining.ValueBlockList;
import util.*;

import java.util.*;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: Jul 10, 2012
 * Time: 10:48:16 PM
 */
public class MergeNet {

    String[] valid_args = {
            "-mergelist", "-source", "-merged", "-out", "-sourceen", "-mergeden", "-singleton"
    };
    HashMap options;
    String mergelist;
    String source;
    String merged;
    String sourceenS;
    String mergedenS;
    String[][] sourceen;
    String[][] mergeden;

    ValueBlockList sourceV;
    ValueBlockList mergedV;

    double MIN_DIST = 0.000000000000001;

    double TF_pvalue_cutoff = 0.0001;

    boolean singleton = false;


    /**
     * @param args
     */
    public MergeNet(String[] args) {
        init(args);


        String[] mergelistA = TextFile.readtoArray(mergelist);
        try {
            sourceV = ValueBlockList.read(source, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mergedV = ValueBlockList.read(merged, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        sourceen = TabFile.readtoArray(sourceenS, false);
        mergeden = TabFile.readtoArray(mergedenS, false);


        HashMap TFmap = new HashMap();
        HashMap areamap = new HashMap();
        HashMap exprmap = new HashMap();
        HashMap critmap = new HashMap();
        ArrayList out = new ArrayList();
        //for all mergelist entries
        for (int i = 0; i < mergelistA.length; i++) {
            System.out.println("i " + i + "\t" + mergelistA[i]);
            ValueBlock mergedBlock = (ValueBlock) mergedV.get(i);
            String code = mergelistA[i];
            String[] index = code.split("_");
            System.out.println("i " + i + "\tindex.length " + index.length + "\t" + code);
            index = StringUtil.replace(index, "_", "");
            index = StringUtil.replace(index, "*", "");
            index = StringUtil.removeEmpty(index);
            System.out.println("i " + i + "\t" + MoreArray.toString(index));
            ValueBlockList vm = new ValueBlockList();
            int[] thismasterindex = new int[index.length];
            for (int j = 0; j < index.length; j++) {
                //System.out.println("j " + j + "\t:" + index[j] + ":");
                //ERROR here index[j] is null
                int ind = Integer.parseInt(index[j]);
                thismasterindex[j] = ind;
                vm.add(sourceV.get(ind - 1));
            }

            if (vm.size() > 1) {
                for (int c = 0; c < vm.size(); c++) {
                    ValueBlock vc = (ValueBlock) vm.get(c);
                    for (int d = c + 1; d < vm.size(); d++) {
                        ValueBlock vd = (ValueBlock) vm.get(d);

                        double dist = BlockMethods.computeBlockOverlapGeneExpFraction(vc, vd, false);
                        double distc = BlockMethods.computeBlockOverlapGeneExpFraction(mergedBlock, vc, false);
                        double distd = BlockMethods.computeBlockOverlapGeneExpFraction(mergedBlock, vd, false);

                        System.out.println(i + "\t" + c + "\t" + d + "\t" + index[c] + "\t" + index[d] + "\tcd " + dist + "\tmc " + distc + "\tmd " + distd);

                        if (dist == 0) {
                            dist = MIN_DIST;
                        }
                        if (distc == 0) {
                            distc = MIN_DIST;
                        }
                        if (distd == 0) {
                            distd = MIN_DIST;
                        }
                        out.add("" + (thismasterindex[c]) + "\t" + (thismasterindex[d]) + "\t" + dist + "\tii");
                        out.add("m" + (i + 1) + "\t" + (thismasterindex[c]) + "\t" + distc + "\tmi");
                        out.add("m" + (i + 1) + "\t" + (thismasterindex[d]) + "\t" + distd + "\tmi");

                        //TF
                        if (Double.parseDouble(sourceen[thismasterindex[c]][15]) < TF_pvalue_cutoff)
                            TFmap.put("" + thismasterindex[c], sourceen[thismasterindex[c]][16]);
                        if (Double.parseDouble(sourceen[thismasterindex[c]][15]) < TF_pvalue_cutoff)
                            TFmap.put("" + thismasterindex[d], sourceen[thismasterindex[d]][16]);
                        if (Double.parseDouble(mergeden[i + 1][15]) < TF_pvalue_cutoff)
                            TFmap.put("m" + (i + 1), mergeden[i + 1][16]);

                        //area
                        areamap.put("" + thismasterindex[c], sourceen[thismasterindex[c]][4]);
                        areamap.put("" + thismasterindex[d], sourceen[thismasterindex[d]][4]);
                        areamap.put("m" + (i + 1), mergeden[i + 1][4]);

                        //expr mean
                        exprmap.put("" + thismasterindex[c], sourceen[thismasterindex[c]][6]);
                        exprmap.put("" + thismasterindex[d], sourceen[thismasterindex[d]][6]);
                        exprmap.put("m" + (i + 1), mergeden[i + 1][6]);

                        /*System.out.println(MoreArray.toString(sourceen[thismasterindex[c]]));
                System.out.println(i + "\t" + 18 + "\t" + sourceen[thismasterindex[c]][18]);
                System.out.println(i + "\t" + 19 + "\t" + sourceen[thismasterindex[c]][19]);
                System.out.println(i + "\t" + 20 + "\t" + sourceen[thismasterindex[c]][20]);*/

                        //expr crit
                        critmap.put("" + thismasterindex[c], (Double.parseDouble(sourceen[thismasterindex[c]][18]) + Double.parseDouble(sourceen[thismasterindex[c]][19]) + Double.parseDouble(sourceen[thismasterindex[c]][20])) / 3.0);
                        critmap.put("" + thismasterindex[d], (Double.parseDouble(sourceen[thismasterindex[d]][18]) + Double.parseDouble(sourceen[thismasterindex[d]][19]) + Double.parseDouble(sourceen[thismasterindex[d]][20])) / 3.0);
                        critmap.put("m" + (i + 1), (Double.parseDouble(mergeden[i + 1][18]) + Double.parseDouble(mergeden[i + 1][18]) + Double.parseDouble(mergeden[i + 1][18])) / 3.0);

                    }
                }
            } else if (singleton) {
                out.add("" + (thismasterindex[0]) + "\t" + (thismasterindex[0]) + "\t" + 1 + "\tii");
                if (Double.parseDouble(sourceen[thismasterindex[0]][15]) < TF_pvalue_cutoff)
                    TFmap.put("" + thismasterindex[0], sourceen[thismasterindex[0]][16]);
                areamap.put("" + thismasterindex[0], sourceen[thismasterindex[0]][4]);
                exprmap.put("" + thismasterindex[0], sourceen[thismasterindex[0]][6]);
                critmap.put("" + thismasterindex[0],
                        (Double.parseDouble(sourceen[thismasterindex[0]][18]) + Double.parseDouble(sourceen[thismasterindex[0]][19]) + Double.parseDouble(sourceen[thismasterindex[0]][20])) / 3.0);

            }
        }

        output(TFmap, areamap, exprmap, critmap, out);

    }

    /**
     * @param TFmap
     * @param areamap
     * @param exprmap
     * @param critmap
     * @param out
     */
    private void output(HashMap TFmap, HashMap areamap, HashMap exprmap, HashMap critmap, ArrayList out) {
        ParsePath pp = new ParsePath(mergelist);
        String outpath = "./" + pp.getName() + "_merged_network.txt";
        System.out.println("writing " + outpath);
        TextFile.write(out, outpath);


        Set s = TFmap.entrySet();
        ArrayList outTF = new ArrayList();
        Iterator it = s.iterator();
        outTF.add("TF");
        while (it.hasNext()) {
            Map.Entry cur = (Map.Entry) it.next();
            String index = (String) cur.getKey();
            String val = (String) cur.getValue();
            outTF.add("" + index + " = " + val);
        }
        String outpath2 = "./" + pp.getName() + "_TFs.noa";
        System.out.println("writing " + outpath2);
        TextFile.write(outTF, outpath2);


        Set s2 = areamap.entrySet();
        ArrayList outarea = new ArrayList();
        Iterator it2 = s2.iterator();
        outarea.add("area");
        while (it2.hasNext()) {
            Map.Entry cur = (Map.Entry) it2.next();
            String index = (String) cur.getKey();
            String val = (String) cur.getValue();
            outarea.add(index + " = " + val);
        }
        String outpath3 = "./" + pp.getName() + "_area.noa";
        System.out.println("writing " + outpath3);
        TextFile.write(outarea, outpath3);


        Set s3 = exprmap.entrySet();
        ArrayList outexpr = new ArrayList();
        Iterator it3 = s3.iterator();
        outexpr.add("exprmean");
        while (it3.hasNext()) {
            Map.Entry cur = (Map.Entry) it3.next();
            String index = (String) cur.getKey();
            String val = (String) cur.getValue();
            outexpr.add(index + " = " + val);
        }
        String outpath4 = "./" + pp.getName() + "_exprmean.noa";
        System.out.println("writing " + outpath4);
        TextFile.write(outexpr, outpath4);


        Set s4 = critmap.entrySet();
        ArrayList outcrit = new ArrayList();
        Iterator it4 = s4.iterator();
        outcrit.add("exprcrit");
        while (it4.hasNext()) {
            Map.Entry cur = (Map.Entry) it4.next();
            String index = (String) cur.getKey();
            double val = (Double) cur.getValue();
            outcrit.add(index + " = " + val);
        }
        String outpath5 = "./" + pp.getName() + "_exprcrit.noa";
        System.out.println("writing " + outpath5);
        TextFile.write(outcrit, outpath5);
    }

    /**
     * @param args
     */

    private void init(String[] args) {

        MoreArray.printArray(args);
        options = MapArgOptions.maptoMap(args, valid_args);

        if (options.get("-mergelist") != null) {
            mergelist = (String) options.get("-mergelist");
        }
        if (options.get("-source") != null) {
            source = (String) options.get("-source");
        }
        if (options.get("-merged") != null) {
            merged = (String) options.get("-merged");
        }
        if (options.get("-sourceen") != null) {
            sourceenS = (String) options.get("-sourceen");
        }
        if (options.get("-mergeden") != null) {
            mergedenS = (String) options.get("-mergeden");
        }
        if (options.get("-singleton") != null) {
            String s = (String) options.get("-singleton");
            singleton = s.equalsIgnoreCase("Y") || s.equalsIgnoreCase("Yes") || s.equalsIgnoreCase("T") || s.equalsIgnoreCase("TRUE") ? true : false;
        }

        /*File test = new File(merged);
        if (!test.isDirectory()) {
            test.mkdir();
            System.out.println("make dir " + merged);
        }*/

    }

    /**
     * @param args
     */
    public final static void main(String[] args) {
        if (args.length == 10 || args.length == 12) {
            MergeNet rm = new MergeNet(args);
        } else {
            System.out.println("syntax: java DataMining.util.MergeNet\n" +
                    "<-mergelist mergelist file>\n" +
                    "<-source source data>\n" +
                    "<-merged meregd data>\n" +
                    "<-sourceen source enrichment data>\n" +
                    "<-mergeden merged  enrichment data>\n" +
                    "<-singleton OPTIONAL output for singletons>"
            );
        }
    }
}
