package DataMining.func;

import mathy.Matrix;
import mathy.SimpleMatrix;
import mathy.stat;
import util.MoreArray;
import util.StringUtil;
import util.TextFile;

import java.util.*;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: Oct 29, 2012
 * Time: 5:54:48 PM
 */
public class CollapseMotifNet {

    boolean debug = false;
    String prefix;
    ArrayList edgedata_pval, edgedata_rank;
    SimpleMatrix expdata;
    HashMap edgemap;

    /**
     * @param args
     */
    public CollapseMotifNet(String[] args) {

        init(args);

        int done = -1;
        int round = 0;
        while (done != 0) {
            done = 0;
            System.out.println("round " + round);
            //for all motif pairs
            for (int i = 0; i < edgedata_pval.size(); i++) {
                //System.out.print(".");
                if (debug)
                    System.out.println("edgemap.size top " + edgemap.size() + "\tround " + round);

                String[] s = ((String) edgedata_pval.get(i)).split("\t");
                String a = s[0];
                String b = s[1];
                //double val = Double.parseDouble(s[2]);

                Object test = edgemap.get(a + "=" + b);
                if (test != null) {
                    String[] splita = a.split("_");
                    String[] splitb = b.split("_");
                    String label = a + "=" + b;

                    String indlabel = indexLabel(splita[1], splitb[1]);

                    String label2 = splita[0] + "_" + indlabel;// a + "=" + b;
                    //System.out.println("testing " + splita[0] + "\t" + splitb[0]);
                    //if the TF gene name is the same
                    if (splita[0].equals(splitb[0])) {
                        System.out.println("collapsing " + a + "\t" + b);
                        //remove this pair
                        edgemap.remove(label);
                        System.out.println("edgemap.size " + edgemap.size());
                        //retrieve all neighbors of these two
                        ArrayList[] aneigh = getNeighbors(a, b);
                        System.out.println("aneigh.size " + aneigh[0].size());
                        //ArrayList[] bneigh = getNeighbors(b, aneigh);
                        // System.out.println("bneigh.size " + bneigh[0].size());

                        //remove all edges for these two nodes
                        removeInvolved(a);
                        System.out.println("removeInvolved(a) " + edgemap.size());
                        removeInvolved(b);
                        System.out.println("removeInvolved(b) " + edgemap.size());

                        //putInvolved(label, aneigh);

                        //add back new edges for collapsed pair
                        putInvolved(label2, aneigh);
                        System.out.println("putInvolved " + edgemap.size());


                        edgedata_pval.remove(i);
                        i--;
                        done++;
                    }

                }
            }

            round++;

            //repopulate edgedata_pval with new edges
            edgedata_pval = new ArrayList();
            Set set = edgemap.entrySet();
            Iterator it = set.iterator();
            while (it.hasNext()) {
                Map.Entry cur = (Map.Entry) it.next();
                String reflab = (String) cur.getKey();
                String[] split = reflab.split("=");
                double[][] v = (double[][]) cur.getValue();
                edgedata_pval.add(split[0] + "\t" + split[1] + "\t" + v[0][0]);
            }

            System.out.println("end loop edgedata_pval size " + edgedata_pval.size());
        }


        output();
    }

    /**
     * @param s
     * @param s1
     * @return
     */
    private String indexLabel(String s, String s1) {
        String[] splitinds1 = s.split("\\^");
        String[] splitinds2 = s1.split("\\^");
        ArrayList allinds = new ArrayList();
        for (int x = 0; x < splitinds1.length; x++) {
            allinds.add(splitinds1[x]);
        }
        for (int y = 0; y < splitinds2.length; y++) {
            allinds.add(splitinds2[y]);
        }
        int[] allindsAr = MoreArray.ArrayListtoInt(allinds);
        Arrays.sort(allindsAr);
        String indlabel = MoreArray.toString(allindsAr, "^");// splita[1] + "^" + splitb[1];
        return indlabel;
    }


    /**
     * add back new edges for collapsed pair
     *
     * @param a
     */
    public void putInvolved(String a, ArrayList[] neigh) {
        for (int i = 0; i < neigh[0].size(); i++) {
            String label = a + "=" + (String) neigh[0].get(i);
            double[][] val = (double[][]) neigh[1].get(i);
            if (debug)
                System.out.println("putInvolved " + label + "\t" + MoreArray.toString(val[0], ","));
            edgemap.put(label, val);
        }
        if (debug)
            System.out.println("putInvolved put " + neigh[0].size() + "\t" + a);
    }

    /**
     * @param a
     */
    public void removeInvolved(String a) {
        Set set = edgemap.entrySet();
        Iterator it = set.iterator();

        ArrayList rem = new ArrayList();
        while (it.hasNext()) {
            Map.Entry cur = (Map.Entry) it.next();
            String reflab = (String) cur.getKey();
            String[] split = reflab.split("=");
            if (split[0].equals(a)) {
                if (!rem.contains(reflab))
                    rem.add(reflab);
                //edgemap.remove(reflab);
            } else if (split[1].equals(a)) {
                if (!rem.contains(reflab))
                    rem.add(reflab);
                //edgemap.remove(reflab);
            }
        }
        int irem = rem.size();
        for (int i = 0; i < rem.size(); i++) {
            String s = (String) rem.get(i);
            edgemap.remove(s);
            if (debug)
                System.out.println("removeInvolved " + s);
        }
        if (debug)
            System.out.println("removeInvolved removed " + irem + "\t" + a);
    }


    /**
     * @return
     */
    public ArrayList[] getNeighbors(String str1, String str2) {
        ArrayList[] ret = new ArrayList[2];
        ret[0] = new ArrayList();
        ret[1] = new ArrayList();

        //for (int i = 0; i < edgedata_pval.size(); i++) {
        Set set = edgemap.entrySet();
        Iterator it = set.iterator();

        while (it.hasNext()) {
            Map.Entry cur = (Map.Entry) it.next();
            String reflab = (String) cur.getKey();

            //String edgeStr = (String) edgedata_pval.get(i);
            //String[] s = edgeStr.split("\t");
            String[] s = reflab.split("=");
            String a = s[0];
            String b = s[1];
            double[][] val = (double[][]) cur.getValue();//Double.parseDouble(s[2]);
            if (a.equals(str1) && !b.equals(str2)) {
                if (!ret[0].contains(b)) {
                    ret[0].add(b);
                    ret[1].add(val);
                } else {
                    int ind = ret[0].indexOf(b);
                    double[][] d = (double[][]) ret[1].get(ind);
                    double[][] join = Matrix.mergeArrayHorizontal(val, d);
                    ret[1].set(ind, join);
                }
                if (debug)
                    System.out.println("getNeighbors 1 " + a + "\t" + b + "\t" + str1 + "\t" + str2);//+ "\t:" + edgeStr + ":\t"
            } else if (b.equals(str1) && !a.equals(str2)) {
                if (!ret[0].contains(a)) {
                    ret[0].add(a);
                    ret[1].add(val);
                } else {
                    int ind = ret[0].indexOf(a);
                    double[][] d = (double[][]) ret[1].get(ind);
                    //double[] nd = {(d[0] + val[0]) / 2.0, (d[1] + val[1]) / 2.0,};
                    double[][] join = Matrix.mergeArrayHorizontal(val, d);
                    ret[1].set(ind, join);
                }
                if (debug)
                    System.out.println("getNeighbors 2 " + b + "\t" + a + "\t" + str1 + "\t" + str2);//+ "\t:" + edgeStr + ":\t"
            } else if (a.equals(str2) && !b.equals(str1)) {
                if (!ret[0].contains(b)) {
                    ret[0].add(b);
                    ret[1].add(val);
                } else {
                    int ind = ret[0].indexOf(b);
                    double[][] d = (double[][]) ret[1].get(ind);
                    //double[] nd = {(d[0] + val[0]) / 2.0, (d[1] + val[1]) / 2.0,};
                    double[][] join = Matrix.mergeArrayHorizontal(val, d);
                    ret[1].set(ind, join);
                }
                if (debug)
                    System.out.println("getNeighbors 3 " + a + "\t" + b + "\t" + str1 + "\t" + str2);//+ "\t:" + edgeStr + ":\t"
            } else if (b.equals(str1) && !a.equals(str2)) {
                if (!ret[0].contains(a)) {
                    ret[0].add(a);
                    ret[1].add(val);
                } else {
                    int ind = ret[0].indexOf(a);
                    double[][] d = (double[][]) ret[1].get(ind);
                    //double[] nd = {(d[0] + val[0]) / 2.0, (d[1] + val[1]) / 2.0,};
                    double[][] join = Matrix.mergeArrayHorizontal(val, d);
                    ret[1].set(ind, join);
                }
                if (debug)
                    System.out.println("getNeighbors 4 " + b + "\t" + a + "\t" + str1 + "\t" + str2);//+ "\t:" + edgeStr + ":\t"
            }
        }
        return ret;
    }


    /**
     *
     */
    private void output() {
        Set set = edgemap.entrySet();
        Iterator itmax = set.iterator();

        ArrayList outar = new ArrayList();
        while (itmax.hasNext()) {
            Map.Entry cur = (Map.Entry) itmax.next();
            String l = (String) cur.getKey();
            String a = null;
            try {
                a = l.substring(0, l.indexOf("="));
            } catch (Exception e) {
                System.out.println("= not found a " + l);
                e.printStackTrace();
            }
            String b = null;
            try {
                b = l.substring(l.indexOf("=") + 2);
            } catch (Exception e) {
                System.out.println("= not found b " + l);
                e.printStackTrace();
            }
            double[][] d = (double[][]) cur.getValue();
            int counta = StringUtil.countOccur(a, "^") + 1;
            int countb = StringUtil.countOccur(b, "^") + 1;

            String shorta = a.split("_")[0];
            String shortb = b.split("_")[0];
            outar.add(a + "\t" + b + "\t" + shorta + "\t" + shortb + "\t" + stat.avg(d[0]) + "\t" + (counta + countb) + "\t" + stat.avg(d[1]));
        }


        String outpath4 = prefix + "_collapse.txt";
        System.out.println("size " + outar.size());
        System.out.println("writing " + outpath4);
        TextFile.write(outar, outpath4);
        System.out.println("wrote " + outpath4);
    }


    /**
     * @param args
     */
    private void init(String[] args) {

        edgedata_rank = TextFile.readtoList(args[0]);
        System.out.println("read edgedata_rank " + edgedata_rank.size());


        edgedata_pval = TextFile.readtoList(args[1]);
        System.out.println("read edgedata_pval " + edgedata_pval.size());

        //initializes hash of edges with all input pairs
        edgemap = new HashMap();
        for (int i = 0; i < edgedata_pval.size(); i++) {
            String[] s = ((String) edgedata_pval.get(i)).split("\t");
            String a = s[0];
            String b = s[1];
            double val = Double.NaN;
            //use p-value if available
            if (s.length == 3)
                val = Double.parseDouble(s[2]);
            else
                val = Double.parseDouble(s[3]);

            edgemap.put(a + "=" + b, val);
        }

        //initializes hash of edges with all input pairs
        for (int i = 0; i < edgedata_rank.size(); i++) {
            String[] s = ((String) edgedata_rank.get(i)).split("\t");
            String a = s[0];
            String b = s[1];

            String lab = a + "=" + b;
            try {
                double pval = (Double) edgemap.get(lab);

                double val = Double.NaN;
                //use p-value if available
                if (s.length == 3)
                    val = Double.parseDouble(s[2]);
                else
                    val = Double.parseDouble(s[3]);

                double[][] data = {{val}, {pval}};
                edgemap.put(lab, data);
            } catch (Exception e) {
                edgemap.remove(lab);
                //e.printStackTrace();
            }

            //edgemap_rank.put(a + "=" + b, val);
        }


        expdata = new SimpleMatrix(args[1]);
        System.out.println("read expdata " + expdata.data.length);

        prefix = args[0] + "_";
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 3) {
            CollapseMotifNet rm = new CollapseMotifNet(args);
        } else {
            System.out.println("syntax: java DataMining.func.CollapseMotifNet\n" +
                    "<motif motif network rank>\n" +
                    "<motif motif network pval>\n" +
                    "<motif motif exp data>"
            );
        }
    }
}
