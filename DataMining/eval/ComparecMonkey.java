package DataMining.eval;

import DataMining.BlockMethods;
import DataMining.ValueBlock;
import DataMining.ValueBlockList;
import DataMining.ValueBlockListMethods;
import util.MapArgOptions;
import util.MoreArray;
import util.TextFile;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * User: marcin
 * Date: Dec 22, 2010
 * Time: 7:20:27 PM
 */
public class ComparecMonkey {

    String oneresultspath;
    String tworesultspath;
    String[] valid_args = {"-one", "-two", "-dist"};

    String[] dist_types = {"total", "gene", "exp"};
    HashMap options;

    ArrayList output = new ArrayList();

    double thresholdcut = 0.25;//0;//                                                           
    String seldist = "gene";

    //HashMap[] cmonkeyHash;
    //HashMap[] selfHash;

    /**
     * @param args
     */
    public ComparecMonkey(String[] args) {

        init(args);

        ValueBlockList cmonkey_list = null;
        try {
            cmonkey_list = ValueBlockListMethods.readAny(tworesultspath, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ValueBlockList vbl_set = new ValueBlockList();
        File test = new File(oneresultspath);
        if (test.isDirectory()) {//oneresultspath.indexOf(".bic") == -1) {
            File dir = new File(oneresultspath);
            String[] list = dir.list();

            System.out.println("r " + list.length);
            System.out.println("c " + cmonkey_list.size());

            for (int i = 0; i < list.length; i++) {
                if (list[i].indexOf("toplist.txt") != -1) {
                    ValueBlockList cur = null;
                    try {
                        cur = ValueBlockListMethods.readAny(oneresultspath + "/" + list[i], false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ValueBlock last = (ValueBlock) cur.get(cur.size() - 1);
                    vbl_set.add(last);
                }
            }
        } else if (oneresultspath.indexOf(".bic") != -1) {
            vbl_set = ValueBlockListMethods.readBIC(oneresultspath, false);
        } else {
            try {
                vbl_set = ValueBlockList.read(oneresultspath, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("vbl_set " + vbl_set.size());

        //cmonkeyHash = new HashMap[cmonkey_list.size()];
        //selfHash = new HashMap[vbl_set.size()];

        for (int i = 0; i < dist_types.length; i++) {
            seldist = dist_types[i];
            doType(cmonkey_list, vbl_set);
        }

    }

    /**
     * @param cmonkey_list
     * @param vbl_set
     */
    private void doType(ValueBlockList cmonkey_list, ValueBlockList vbl_set) {

        int[] vbl_set_single = new int[vbl_set.size()];
        int[] cmonkey_single = new int[cmonkey_list.size()];
        for (int i = 0; i < vbl_set.size(); i++) {
            System.out.print("v");
            ValueBlock last = (ValueBlock) vbl_set.get(i);
            /*if (selfHash[i] == null) {
                selfHash[i] = new HashMap();
                selfHash[i] = util.LoadHash.addPairs(last.genes, last.exps, selfHash[i]);
            }*/
            //System.out.println("v g "+MoreArray.toString(last.genes,","));
            //System.out.println("v e "+MoreArray.toString(last.exps,","));
            for (int j = 0; j < cmonkey_list.size(); j++) {
                //System.out.print(".");
                ValueBlock mnk = (ValueBlock) cmonkey_list.get(j);
                /*if (cmonkeyHash[j] == null) {
                    cmonkeyHash[j] = new HashMap();
                    cmonkeyHash[j] = util.LoadHash.addPairs(mnk.genes, mnk.exps, cmonkeyHash[j]);
                }*/
                double value = compareValue(last, mnk);
                //double value = compareValue(last, mnk, selfHash[i], cmonkeyHash[j]);
                //System.out.println("self vs cmonkey " + i + "\t" + j + "\t" + value);
                if (value > thresholdcut) {
                    String s = "c" + j + "\t" + "r" + i + "\t" + "rc" + "\t" + value;
                    output.add(s);
                    vbl_set_single[i]++;
                    cmonkey_single[j]++;
                    //System.out.println("compare " + s);
                }
            }
        }
        System.out.println("\nself vs cmonkey " + output.size());

        int selfself = 0;
        //self-self
        for (int i = 0; i < vbl_set.size(); i++) {
            System.out.print("r");
            ValueBlock last = (ValueBlock) vbl_set.get(i);
            for (int j = i + 1; j < vbl_set.size(); j++) {
                //System.out.print(".");
                ValueBlock last2 = (ValueBlock) vbl_set.get(j);
                double value = compareValue(last, last2);//, selfHash[i], selfHash[j]);
                //System.out.println("self vs self " + i + "\t" + j + "\t" + value);
                if (value > thresholdcut) {
                    String s = "r" + j + "\t" + "r" + i + "\t" + 1 + "\t" + value;
                    output.add(s);
                    vbl_set_single[i]++;
                    vbl_set_single[j]++;
                    selfself++;
                    //System.out.println("results " + s);
                }
            }
        }
        System.out.println("\nself vs self " + output.size() + "\t" + selfself);

        int selfselfcmonkey = 0;
        //self-self cmonkey
        for (int i = 0; i < cmonkey_list.size(); i++) {
            System.out.print("c");
            ValueBlock mnk1 = (ValueBlock) cmonkey_list.get(i);
            for (int j = i + 1; j < cmonkey_list.size(); j++) {
                //System.out.print(".");
                ValueBlock mnk2 = (ValueBlock) cmonkey_list.get(j);
                double value = compareValue(mnk1, mnk2);//, cmonkeyHash[i], cmonkeyHash[j]);
                //System.out.println("cmonkey vs cmonkey " + i + "\t" + j + "\t" + value);
                if (value > thresholdcut) {
                    String s = "c" + j + "\t" + "c" + i + "\t" + "cc" + "\t" + value;
                    output.add(s);
                    cmonkey_single[i]++;
                    cmonkey_single[j]++;
                    selfselfcmonkey++;
                    //System.out.println("cmonkey " + s);
                }
            }
        }
        System.out.println("\ncmonkey vs cmonkey " + output.size() + "\t" + selfselfcmonkey);

        int vblsinglecount = 0;
        for (int i = 0; i < vbl_set.size(); i++) {
            if (vbl_set_single[i] == 0) {
                String s = "r" + i + "\t" + "r" + i + "\t" + "rr" + "\t" + 1;
                output.add(s);
                vblsinglecount++;
            }
        }
        System.out.println("vbl single " + output.size() + "\t" + vblsinglecount);

        int cmonkeysinglecount = 0;
        for (int i = 0; i < cmonkey_list.size(); i++) {
            if (cmonkey_single[i] == 0) {
                String s = "c" + i + "\t" + "c" + i + "\t" + "cc" + "\t" + 1;
                output.add(s);
                cmonkeysinglecount++;
            }
        }
        System.out.println("cmonkey single " + output.size() + "\t" + cmonkeysinglecount);

        String outpath = oneresultspath + "_" + thresholdcut + "_" + seldist + ".txt";
        System.out.println("writing " + outpath);
        TextFile.write(output, outpath);
    }

    /**
     * @param one
     * @param two
     * @return
     */
    //private double compareValue(ValueBlock one, ValueBlock two, HashMap oh, HashMap th) {
    private double compareValue(ValueBlock one, ValueBlock two) {
        double value = Double.NaN;
        int arrayInd = MoreArray.getArrayInd(dist_types, seldist);
        //System.out.println("compareValue " + arrayInd);
        if (arrayInd == 0) {
            //value = BlockMethods.computeBlockOverlapGeneRootProduct(two, one);
            //value = BlockMethods.computeBlockF1(two, one);
            //value = BlockMethods.computefrxnGeneAndExpWithRefPairMin(two, one, false);
            value = BlockMethods.computeBlockOverlapGeneExpRootProduct(two, one);
            //value = compareValueHash(oh, th, one.genes.length, two.genes.length, one.exps.length, two.exps.length);
        } else if (arrayInd == 1) {
            /*double p = BlockMethods.computeBlockPrecisionGenes(two, one);
            double r = BlockMethods.computeBlockRecallGenes(two, one);
            value = (2 * p * r) / (p + r);*/
            //value = BlockMethods.computeBlockOverlapGeneMin(two, one, false);
            value = BlockMethods.computeBlockOverlapGeneRootProduct(two, one);

        } else if (arrayInd == 2) {
            /*double p = BlockMethods.computeBlockPrecisionExps(two, one);
            double r = BlockMethods.computeBlockRecallExps(two, one);
            value = (2 * p * r) / (p + r);*/
            //value = BlockMethods.computeBlockOverlapExpMin(two, one, false);
            value = BlockMethods.computeBlockOverlapExpRootProduct(two, one);
        }
        return value;
    }

    /**
     * @param one
     * @param two
     * @return
     */
    private double compareValueHash(HashMap one, HashMap two,
                                    int oglen, int tglen, int oelen, int telen) {
        double value = Double.NaN;
        int arrayInd = MoreArray.getArrayInd(dist_types, seldist);
        //System.out.println("compareValue " + arrayInd);
        if (arrayInd == 0) {
            //value = BlockMethods.computeBlockOverlapGeneRootProduct(two, one);
            //value = BlockMethods.computeBlockF1(two, one);
            //value = BlockMethods.computefrxnGeneAndExpWithRefPairMin(two, one, false);
            value = BlockMethods.computeBlockOverlapGeneExpRootProduct(two, one,
                    oglen, tglen, oelen, telen);
        } /*else if (arrayInd == 1) {
         *//*double p = BlockMethods.computeBlockPrecisionGenes(two, one);
            double r = BlockMethods.computeBlockRecallGenes(two, one);
            value = (2 * p * r) / (p + r);*//*
            //value = BlockMethods.computeBlockOverlapGeneMin(two, one, false);
            value = BlockMethods.computeBlockOverlapGeneRootProduct(two, one, oglen, tglen, oelen, telen);

        } else if (arrayInd == 2) {
            *//*double p = BlockMethods.computeBlockPrecisionExps(two, one);
            double r = BlockMethods.computeBlockRecallExps(two, one);
            value = (2 * p * r) / (p + r);*//*
            //value = BlockMethods.computeBlockOverlapExpMin(two, one, false);
            value = BlockMethods.computeBlockOverlapExpRootProduct(two, one, oglen, tglen, oelen, telen);
        }*/
        return value;
    }

    /**
     * @param args
     */
    private void init(String[] args) {
        MoreArray.printArray(args);

        options = MapArgOptions.maptoMap(args, valid_args);

        if (options.get("-one") != null) {
            oneresultspath = (String) options.get("-one");//args[0];
        }
        if (options.get("-two") != null) {
            tworesultspath = (String) options.get("-two");//args[1];
        }
        if (options.get("-dist") != null) {
            seldist = (String) options.get("-dist");//args[1];
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 6) {
            ComparecMonkey rm = new ComparecMonkey(args);
        } else {
            System.out.println("syntax: java DataMining.eval.ComparecMonkey\n" +
                    "<-one dir of results or .bic file>\n" +
                    "<-two .bic format file>\n" +
                    "<-dist total, gene, exp>"
            );
        }
    }
}
