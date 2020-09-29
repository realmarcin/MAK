package DataMining.func;

import util.MoreArray;
import util.StringUtil;
import util.TabFile;
import util.TextFile;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: Sep 27, 2012
 * Time: 10:48:09 AM
 */
public class PickOverlapFunc {

    boolean debug = true;

    /**
     * @param args
     */
    public PickOverlapFunc(String[] args) {

        //String[][] summary_data = TabFile.readtoArray(args[0]);
        String[] fun_data = TextFile.readtoArray(args[0]);
        fun_data = MoreArray.remove(fun_data, 0);
        String[][] overlap_data = TabFile.readtoArray(args[1]);
        String outfile = args[2];

        ArrayList out = new ArrayList();

        HashMap done = new HashMap();
        for (int i = 0; i < overlap_data.length; i++) {
            double overlap = Double.parseDouble(overlap_data[i][14]);

            String ids = overlap_data[i][20];
            System.out.println("ids1 " + ids);
            //ids = StringUtil.replace(ids, "*___", "");
            ids = StringUtil.replace(ids, "*", "");
            System.out.println("ids2 " + ids);
            int index1 = Integer.parseInt(ids.substring(ids.indexOf(" ") + 1, ids.indexOf("_")));
            System.out.println(index1);
            ids = ids.substring(ids.indexOf(" ") + 1);

            int subindex = (int) Double.parseDouble(overlap_data[i][8]);
            System.out.println("subindex " + subindex);
            if (subindex == -1) {
                subindex = 0;
                //System.out.println("subindex PROBLEM");
                //MoreArray.printArray(overlap_data[i]);
            }
            System.out.println("subindex " + subindex + "\t" + overlap_data[i][7] + "\t" + overlap_data[i][8] + "\t" + overlap_data[i][9]);

            String tmp = null;
            if (ids.indexOf("____") != -1)
                tmp = ids.substring(ids.indexOf("____") + "____".length());
            else if (ids.indexOf("*___") != -1) {
                tmp = ids.substring(ids.indexOf("*___") + "*___".length());
            }
            System.out.println("tmp " + tmp);
            String[] ar = tmp.split("_");
            MoreArray.printArray(ar);
            int index2 = Integer.parseInt(ar[subindex]);


            String key1 = "" + index1 + "_" + index2;
            String key2 = "" + index2 + "_" + index1;

            Object o = done.get(key1);
            if (o == null) {
                o = done.get(key2);
            }

            if (o == null) {
                String s1 = "" + index1 + " =";
                String s2 = "" + index2 + " =";
                String fun1 = null, fun2 = null;
                for (int j = 0; j < fun_data.length; j++) {
                    if (fun_data[j].indexOf(s1) == 0) {
                        fun1 = fun_data[j].substring(fun_data[j].indexOf("= ") + 2);
                    } else if (fun_data[j].indexOf(s2) == 0) {
                        fun2 = fun_data[j].substring(fun_data[j].indexOf("= ") + 2);
                    }
                }

                String[] arfun1 = new String[0];
                try {
                    arfun1 = fun1.split(" x ");
                } catch (Exception e) {
                    System.out.println("fun1 " + fun1);
                    e.printStackTrace();
                }
                String[] arfun2 = new String[0];
                try {
                    arfun2 = fun2.split(" x ");
                } catch (Exception e) {
                    System.out.println("fun2 " + fun2);
                    e.printStackTrace();
                }

                ArrayList diff1 = new ArrayList();
                ArrayList diff2 = new ArrayList();
                ArrayList common = new ArrayList();


                for (int a = 0; a < arfun1.length; a++) {
                    if (debug)
                        System.out.println("arfun1[a] " + arfun1[a]);
                    if (arfun1[a].length() > 0) {
                        String type1 = arfun1[a].substring(0, arfun1[a].indexOf(": "));
                        for (int b = 0; b < arfun2.length; b++) {
                            if (debug)
                                System.out.println("arfun2[b] " + arfun2[b]);
                            if (arfun2[b].length() > 0) {
                                String type2 = arfun2[b].substring(0, arfun2[b].indexOf(": "));
                                if (type1.equals(type2)) {
                                    String[] ararfun1 = arfun1[a].split("_");
                                    String[] ararfun2 = arfun2[b].split("_");
                                    for (int c = 0; c < ararfun1.length; c++) {
                                        boolean added = false;
                                        for (int d = 0; d < ararfun2.length; d++) {
                                            if (ararfun1[c].equals(ararfun2[d])) {
                                                common.add(type1 + ": " + ararfun1[c]);
                                                added = true;
                                            }
                                        }
                                        if (!added) {
                                            diff1.add(ararfun1[c]);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }


                for (int b = 0; b < arfun2.length; b++) {
                    if (debug)
                        System.out.println("arfun2[b] " + arfun2[b]);
                    if (arfun2[b].length() > 0) {
                        String type2 = arfun2[b].substring(0, arfun2[b].indexOf(": "));
                        for (int a = 0; a < arfun1.length; a++) {
                            if (debug)
                                System.out.println("arfun1[a] " + arfun1[a]);
                            if (arfun1[a].length() > 0) {
                                String type1 = arfun1[a].substring(0, arfun1[a].indexOf(": "));
                                if (type2.equals(type1)) {
                                    String[] ararfun1 = arfun1[a].split("_");
                                    String[] ararfun2 = arfun2[b].split("_");
                                    for (int c = 0; c < ararfun2.length; c++) {
                                        boolean added = false;
                                        for (int d = 0; d < ararfun1.length; d++) {
                                            if (ararfun2[c].equals(ararfun1[d])) {
                                                //common.add(type2 + ": " + ararfun2[c]);
                                                added = true;
                                            }
                                        }
                                        if (!added) {
                                            diff2.add(ararfun2[c]);
                                        }
                                    }

                                }
                            }
                        }
                    }
                }

                creatOut(out, index1, index2, diff1, diff2, common);
            }
        }

        TextFile.write(out, outfile);
    }

    /**
     * @param out
     * @param index1
     * @param index2
     * @param diff1
     * @param diff2
     * @param common
     */
    private void creatOut(ArrayList out, double index1, double index2, ArrayList diff1, ArrayList diff2, ArrayList common) {
        for (int z = 0; z < common.size(); z++) {
            out.add("" + index1 + "_" + index2 + "\tcommon\t" + MoreArray.toString(MoreArray.ArrayListtoString(common), "\t"));
        }
        for (int z = 0; z < diff1.size(); z++) {
            out.add("" + index1 + "_" + index2 + "\t1\t" + MoreArray.toString(MoreArray.ArrayListtoString(diff1), "\t"));
        }
        for (int z = 0; z < diff2.size(); z++) {
            out.add("" + index1 + "_" + index2 + "\t2\t" + MoreArray.toString(MoreArray.ArrayListtoString(diff2), "\t"));
        }
    }

    /**
     * @param args
     */

    public final static void main(String[] args) {
        if (args.length == 3) {
            PickOverlapFunc rm = new PickOverlapFunc(args);
        } else {
            System.out.println("syntax: java DataMining.func.PickOverlapFunc\n" +
                    "<functional annotation file> <bicluster overlap file> <outfile>"
            );
        }
    }
}
