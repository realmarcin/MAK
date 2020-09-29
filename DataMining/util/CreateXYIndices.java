package DataMining.util;

import DataMining.ValueBlock;
import DataMining.ValueBlockList;
import DataMining.ValueBlockListMethods;
import mathy.SimpleMatrix;
import util.MoreArray;
import util.StringUtil;
import util.TabFile;
import util.TextFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: marcin
 * Date: 10/18/17
 * Time: 3:24 PM
 */
public class CreateXYIndices {

    /**
     * @param args
     */
    public CreateXYIndices(String[] args) {
        ValueBlockList vbl = null;
        try {
            vbl = ValueBlockListMethods.readAny(args[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //load input matrix

        SimpleMatrix sm = new SimpleMatrix(args[1]);

        if (args.length > 2) {
            withLabels(args, vbl, sm);
        } else {
            noLabels(args, vbl, sm);
        }
    }

    /**
     * @param args
     * @param vbl
     * @param sm
     */
    private void noLabels(String[] args, ValueBlockList vbl, SimpleMatrix sm) {

        if (vbl != null) {

            String[] yids = sm.ylabels;// {};
            String[] xids = sm.xlabels;//{};
            String[] ylabels = sm.ylabels;//{};
            String[] xlabels = sm.xlabels;//{};

            HashMap hm_g = new HashMap();
            HashMap hm_g_label = new HashMap();
            HashMap hm_e = new HashMap();
            HashMap hm_e_label = new HashMap();

            for (int i = 0; i < vbl.size(); i++) {
                ValueBlock vb = (ValueBlock) vbl.get(i);

                for (int g = 0; g < vb.genes.length; g++) {
                    Object test = hm_g.get(vb.genes[g]);
                    if (test == null) {
                        hm_g.put(vb.genes[g], "" + i);
                        String yid = sm.ylabels[vb.genes[g]];
                        System.out.println("g y 1 " + yids[1] + "\t" + yid);
                        String ylabel = null;
                        try {
                            int index = StringUtil.getFirstEqualsIndex(yids, yid);
                            System.out.println("g index " + index + "\t" + yids[index] + "\t" + yid + "\t" + ylabels[index]);
                            ylabel = ylabels[index];
                        } catch (Exception e) {
                            ylabel = yid;
                            e.printStackTrace();
                        }
                        hm_g_label.put(ylabel, "" + i);
                    } else {
                        hm_g.put(vb.genes[g], test.toString() + "," + i);
                        String yid = sm.ylabels[vb.genes[g]];
                        System.out.println("g y 2 " + yids[1] + "\t" + yid);
                        String ylabel = null;
                        try {
                            int index = StringUtil.getFirstEqualsIndex(yids, yid);
                            System.out.println("g index 2 " + index + "\t" + yids[index] + "\t" + yid + "\t" + ylabels[index]);
                            ylabel = ylabels[index];
                        } catch (Exception e) {
                            ylabel = yid;
                            e.printStackTrace();
                        }
                        hm_g_label.put(ylabel, test.toString() + "," + i);
                    }
                }
                for (int e = 0; e < vb.exps.length; e++) {
                    Object test = hm_e.get(vb.exps[e]);
                    if (test == null) {
                        hm_e.put(vb.exps[e], "" + i);
                        String xid = sm.xlabels[vb.exps[e] - 1];
                        System.out.println(xids[1] + "\t" + xid);
                        String xlabel = xlabels[StringUtil.getFirstEqualsIndex(xids, xid)];
                        hm_e_label.put(xlabel, "" + i);
                    } else {
                        hm_e.put(vb.exps[e], test.toString() + "," + i);
                        String xid = sm.xlabels[vb.exps[e] - 1];
                        String xlabel = xlabels[StringUtil.getFirstEqualsIndex(xids, xid)];
                        hm_e_label.put(xlabel, test.toString() + "," + i);
                    }
                }
            }

            ArrayList outg = new ArrayList();
            Iterator it = hm_g_label.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                String value = (String) pair.getValue();
                String x = pair.getKey() + "\t" + value + "\t" + (StringUtil.countOccur(value, ',') + 1);
                System.out.println(x);
                outg.add(x);
            }

            //String outgs = args[0] + "_label_yindex.tsv";
            String outgs = "label_yindex.tsv";
            TextFile.write(outg, outgs);


            ArrayList oute = new ArrayList();
            Iterator it2 = hm_e_label.entrySet().iterator();
            while (it2.hasNext()) {
                Map.Entry pair = (Map.Entry) it2.next();
                String value = (String) pair.getValue();
                String x = pair.getKey() + "\t" + value + "\t" + (StringUtil.countOccur(value, ',') + 1);
                System.out.println(x);
                oute.add(x);
            }

            //String outes = args[0] + "_label_xindex.tsv";
            String outes = "label_xindex.tsv";
            TextFile.write(oute, outes);


            ArrayList outga = new ArrayList();
            Iterator ita = hm_g.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) ita.next();
                String value = (String) pair.getValue();
                String x = pair.getKey() + "\t" + value + "\t" + (StringUtil.countOccur(value, ',') + 1);
                System.out.println(x);
                outga.add(x);
            }

            //String outgs = args[0] + "_label_yindex.tsv";
            String outgsa = "ids_yindex.tsv";
            TextFile.write(outga, outgsa);


            ArrayList outea = new ArrayList();
            Iterator it2a = hm_e.entrySet().iterator();
            while (it2a.hasNext()) {
                Map.Entry pair = (Map.Entry) it2a.next();
                String value = (String) pair.getValue();
                String x = pair.getKey() + "\t" + value + "\t" + (StringUtil.countOccur(value, ',') + 1);
                System.out.println(x);
                outea.add(x);
            }

            //String outes = args[0] + "_label_xindex.tsv";
            String outesa = "ids_xindex.tsv";
            TextFile.write(outea, outesa);

        }
    }

    /**
     * @param args
     * @param vbl
     * @param sm
     */
    private void withLabels(String[] args, ValueBlockList vbl, SimpleMatrix sm) {
        String[][] xlabels_data = TabFile.readtoArray(args[2]);
        String[] xids = MoreArray.extractColumnStr(xlabels_data, 2);
        sm.xlabels = StringUtil.replace(sm.xlabels, "\"", "");
        sm.xlabels = StringUtil.replace(sm.xlabels, ".", ":");
        String[] xlabels = MoreArray.extractColumnStr(xlabels_data, 3);
        String[][] ylabels_data = TabFile.readtoArray(args[3]);
        String[] yids = MoreArray.extractColumnStr(ylabels_data, 2);
        sm.ylabels = StringUtil.replace(sm.ylabels, "\"", "");
        String[] ylabels = MoreArray.extractColumnStr(ylabels_data, 3);
        System.out.println("yids " + yids[0] + "\t" + yids[1]);
        System.out.println("ylabels " + ylabels[0] + "\t" + ylabels[1]);


        if (vbl != null) {

            HashMap hm_g = new HashMap();
            HashMap hm_g_label = new HashMap();
            HashMap hm_e = new HashMap();
            HashMap hm_e_label = new HashMap();

            for (int i = 0; i < vbl.size(); i++) {
                ValueBlock vb = (ValueBlock) vbl.get(i);

                for (int g = 0; g < vb.genes.length; g++) {
                    Object test = hm_g.get(vb.genes[g]);
                    if (test == null) {
                        hm_g.put(vb.genes[g], "" + i);
                        String yid = sm.ylabels[vb.genes[g]];
                        System.out.println("g y 1 " + yids[1] + "\t" + yid);
                        String ylabel = null;
                        try {
                            int index = StringUtil.getFirstEqualsIndex(yids, yid);
                            System.out.println("g index " + index + "\t" + yids[index] + "\t" + yid + "\t" + ylabels[index]);
                            ylabel = ylabels[index];
                        } catch (Exception e) {
                            ylabel = yid;
                            e.printStackTrace();
                        }
                        hm_g_label.put(ylabel, "" + i);
                    } else {
                        hm_g.put(vb.genes[g], test.toString() + "," + i);
                        String yid = sm.ylabels[vb.genes[g]];
                        System.out.println("g y 2 " + yids[1] + "\t" + yid);
                        String ylabel = null;
                        try {
                            int index = StringUtil.getFirstEqualsIndex(yids, yid);
                            System.out.println("g index 2 " + index + "\t" + yids[index] + "\t" + yid + "\t" + ylabels[index]);
                            ylabel = ylabels[index];
                        } catch (Exception e) {
                            ylabel = yid;
                            e.printStackTrace();
                        }
                        hm_g_label.put(ylabel, test.toString() + "," + i);
                    }
                }
                for (int e = 0; e < vb.exps.length; e++) {
                    Object test = hm_e.get(vb.exps[e]);
                    if (test == null) {
                        hm_e.put(vb.exps[e], "" + i);
                        String xid = sm.xlabels[vb.exps[e] - 1];
                        System.out.println(xids[1] + "\t" + xid);
                        String xlabel = xlabels[StringUtil.getFirstEqualsIndex(xids, xid)];
                        hm_e_label.put(xlabel, "" + i);
                    } else {
                        hm_e.put(vb.exps[e], test.toString() + "," + i);
                        String xid = sm.xlabels[vb.exps[e] - 1];
                        String xlabel = xlabels[StringUtil.getFirstEqualsIndex(xids, xid)];
                        hm_e_label.put(xlabel, test.toString() + "," + i);
                    }
                }
            }

            ArrayList outg = new ArrayList();
            Iterator it = hm_g_label.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                String value = (String) pair.getValue();
                String x = pair.getKey() + "\t" + value + "\t" + (StringUtil.countOccur(value, ',') + 1);
                System.out.println(x);
                outg.add(x);
            }

            //String outgs = args[0] + "_label_yindex.tsv";
            String outgs = "label_yindex.tsv";
            TextFile.write(outg, outgs);


            ArrayList oute = new ArrayList();
            Iterator it2 = hm_e_label.entrySet().iterator();
            while (it2.hasNext()) {
                Map.Entry pair = (Map.Entry) it2.next();
                String value = (String) pair.getValue();
                String x = pair.getKey() + "\t" + value + "\t" + (StringUtil.countOccur(value, ',') + 1);
                System.out.println(x);
                oute.add(x);
            }

            //String outes = args[0] + "_label_xindex.tsv";
            String outes = "label_xindex.tsv";
            TextFile.write(oute, outes);


            ArrayList outga = new ArrayList();
            Iterator ita = hm_g.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) ita.next();
                String value = (String) pair.getValue();
                String x = pair.getKey() + "\t" + value + "\t" + (StringUtil.countOccur(value, ',') + 1);
                System.out.println(x);
                outga.add(x);
            }

            //String outgs = args[0] + "_label_yindex.tsv";
            String outgsa = "ids_yindex.tsv";
            TextFile.write(outga, outgsa);


            ArrayList outea = new ArrayList();
            Iterator it2a = hm_e.entrySet().iterator();
            while (it2a.hasNext()) {
                Map.Entry pair = (Map.Entry) it2a.next();
                String value = (String) pair.getValue();
                String x = pair.getKey() + "\t" + value + "\t" + (StringUtil.countOccur(value, ',') + 1);
                System.out.println(x);
                outea.add(x);
            }

            //String outes = args[0] + "_label_xindex.tsv";
            String outesa = "ids_xindex.tsv";
            TextFile.write(outea, outesa);

        }
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 1 || args.length == 4) {
            CreateXYIndices rm = new CreateXYIndices(args);
        } else {
            System.out.println("syntax: java DataMining.util.CollectPartial\n" +
                    "<toplist file> <input matrix> <xlabels OPTIONAL> <ylabels OPTIONAL>"
            );
        }
    }
}
