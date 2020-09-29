package DataMining.func;

import util.TextFile;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: Oct 27, 2012
 * Time: 12:34:25 AM
 */
public class ScoreMotifMotif {

    double PVAL_CUTOFF = 0.001;
    double FLOOR_PVALUE = 0.0001;

    /**
     * @param args
     */
    public ScoreMotifMotif(String[] args) {

        ArrayList edgedata = TextFile.readtoList(args[1]);
        System.out.println("read edges " + edgedata.size());

        File f = new File(args[0]);
        String[] list = f.list();

        HashMap[] edgedata_rand = new HashMap[list.length];
        for (int i = 0; i < list.length; i++) {
            ArrayList r = TextFile.readtoList(args[0] + "/" + list[i]);
            edgedata_rand[i] = pairstoMap(r);
            System.out.println("read edges " + edgedata_rand[i].size());
        }


        int size = edgedata.size();
        double[] pvals = new double[size];
        for (int i = 0; i < size; i++) {
            String[] s = ((String) edgedata.get(i)).split("\t");
            String a = s[0];
            String b = s[1];
            double val = Double.parseDouble(s[2]);
            String s1 = a + "_" + b;
            String s2 = b + "_" + a;
            double count = 0;
            for (int j = 0; j < list.length; j++) {
                Object o = edgedata_rand[j].get(s1);
                if (o != null) {
                    double d = (Double) o;
                    if (val < d)
                        count++;
                } else {
                    Object o2 = edgedata_rand[j].get(s2);
                    if (o2 != null) {
                        double d = (Double) o2;
                        if (val < d)
                            count++;
                        //System.out.println(i + " found reverse motif pairing " + s2 + "\tvs " + s1);
                    }
                }
            }

            double p = count / (double) list.length;
            pvals[i] = p;
            System.out.println(i + " p-value " + p);
        }

        ArrayList outar = new ArrayList();
        ArrayList outartop = new ArrayList();
        for (int i = 0; i < size; i++) {
            System.out.print(".");
            if (pvals[i] == 0) {
                pvals[i] = FLOOR_PVALUE;
            }
            String cur = (String) edgedata.get(i) + "\t" + -Math.log(pvals[i]) / Math.log(10);
            outar.add(cur);
            if (pvals[i] < PVAL_CUTOFF)
                outartop.add(cur);
        }
        String outpath = args[1] + "_pval.txt";
        System.out.println("outpath " + outpath);
        TextFile.write(outar, outpath);

        String outpathtop = args[1] + "_pval" + PVAL_CUTOFF + ".txt";
        System.out.println("outpathtop " + outpathtop);
        TextFile.write(outartop, outpathtop);
    }


    /**
     * @return
     */
    public HashMap pairstoMap(ArrayList r) {
        HashMap h = new HashMap();
        for (int i = 0; i < r.size(); i++) {
            String[] s = ((String) r.get(i)).split("\t");
            String a = s[0];
            String b = s[1];
            double val = Double.parseDouble(s[2]);
            String s1 = a + "_" + b;
            h.put(s1, val);
        }
        return h;
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 2) {
            ScoreMotifMotif rm = new ScoreMotifMotif(args);
        } else {
            System.out.println("syntax: java DataMining.func.ScoreMotifMotif\n" +

                    "<dir of random>\n" +
                    "<edges motif motif network>"
            );
        }
    }
}
