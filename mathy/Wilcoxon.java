package mathy;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Vector;

public class Wilcoxon {

    Vector sorted;
    public int dsum;
    public int nsum;
    int dsize;
    int nsize;
    Vector nsort;
    Vector dsort;
    boolean presorted = false;

    /**
     * @param d
     * @param n
     */
    public Wilcoxon(Vector d, Vector n) {

        writeRaw(d, n);

        if (!presorted) {
            nsort = Sort.sort(n, 'n', "min");
            nsize = nsort.size();

            dsort = Sort.sort(d, 'd', "max");
            dsize = dsort.size();
        } else {
            nsort = n;
            dsort = d;
            nsize = n.size();
            dsize = d.size();
        }

        System.out.println("NORMAL " + nsize + "\tDISEASE " + dsize);

        Vector a = combine(dsort, nsort);
        System.out.println("COMBINED SIZE " + a.size());
        sorted = Sort.sort(a, 'x', "max");
        System.out.println("SORTED SIZE " + sorted.size());
        nsum = ranksumDAll(sorted, 'n');
        dsum = ranksumDAll(sorted, 'd');
        System.out.println("Normal rank sum: " + nsum + "\tDisease rank sum: " + dsum);
    }

    /**
     * Combines the two distributions.
     *
     * @param d
     * @param n
     * @return
     */
    private Vector combine(Vector d, Vector n) {

        Vector ret = new Vector();

        for (int i = 0; i < d.size(); i++) {

            Distrib cur = (Distrib) d.elementAt(i);
            double now = cur.val;
            //System.out.println("d "+i+"\t"+now);
            char w = 'd';
            Distrib add = new Distrib(now, w);
            ret.addElement(add);
        }
        for (int i = 0; i < n.size(); i++) {
            Distrib cur = (Distrib) n.elementAt(i);
            double now = cur.val;
            //System.out.println("n "+i+"\t"+now);
            char w = 'n';
            Distrib add = new Distrib(now, w);
            ret.addElement(add);
        }
        return ret;
    }


    /**
     * Calculates the rank sum of a given Vector.
     *
     * @param s
     * @return
     */
    private final static int ranksumD(Vector s) {

        int sum = 0;
        for (int i = 0; i < s.size(); i++) {

            Distrib now = (Distrib) s.elementAt(i);
            sum += now.rank;
        }
        return sum;
    }


    /**
     * Calculates the rank sum of a given set within a Vector
     *
     * @param s
     * @param a
     * @return
     */
    private final static int ranksumDAll(Vector s, char a) {

        int sum = 0;
        for (int i = 0; i < s.size(); i++) {

            Distrib now = (Distrib) s.elementAt(i);
            if (now.set == a) {

                sum += now.rank;
                //System.out.println(i + "\tvalue " + now.val + "\trank " + now.rank + "\tsum " + sum);
            }
        }
        //System.out.println("Rank sum "+sum);
        return sum;
    }


    /**
     * Writes sorted vector to file.
     *
     * @param filename
     */
    public void write(String filename) {

        try {
            PrintWriter out = new PrintWriter(new FileWriter(filename), true);

            out.println("SET\tRANK\tVALUE");
            for (int i = 0; i < sorted.size(); i++) {
                Distrib now = (Distrib) sorted.elementAt(i);
                out.println(now.set + "\t" + now.rank + "\t" + now.val);
            }
            out.close();
        } catch (IOException e) {
            System.out.println("error creating or writing file " + filename);
            System.out.println("IOException: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * @return
     */
    public double[] randTest() {

        return randTest(Double.NaN);
    }


    /**
     * Computes a random distribution of ranks.
     *
     * @param max
     * @return
     */
    public double[] randTest(double max) {


        boolean usemax = false;
        if (!Double.isNaN(max))
            usemax = true;

        double[] ret = new double[2];
        int iteratemax = 1000000;
        int iterate = iteratemax;
        int icount = 0;
        Vector rnormal = new Vector();
        Vector rdisease = new Vector();
        int pdcount = 0;
        int pncount = 0;

        while (iterate > 0) {

            /*
            System.out.print(".");
            if(icount==50) {
                System.out.print("\n");
                icount=0;
            }
            */

            Random r = new Random();
            for (int i = 0; i < dsize; i++) {

                //int add = r.nextInt();
                int add = -1;
                if (usemax)
                    add = r.nextInt((int) max);
                else
                    add = r.nextInt();

                Distrib a = new Distrib((double) add, 'd');
                rdisease.addElement(a);
            }
            for (int i = 0; i < nsize; i++) {

                int add = -1;
                if (usemax)
                    add = r.nextInt((int) max);
                else
                    add = r.nextInt();

                Distrib a = new Distrib((double) add, 'n');
                rnormal.addElement(a);
            }

            //Vector rnsort = sort(rnormal, 'n');
            //Vector rdsort = sort(rdisease, 'd');

            Vector ra = combine(rdisease, rnormal);//(rdsort, rnsort);
            Vector rsorted = Sort.sort(ra, 'x', "max");
            int rnsum = ranksumDAll(rsorted, 'n');
            int rdsum = ranksumDAll(rsorted, 'd');

            if (rdsum >= dsum) {

                pdcount++;
//double estpval = (double)pcount/(double)10000;
//System.out.println("TOTAL "+(dsize+nsize)+"\tITERATION: "+iterate+"\t"+estpval+"\tNSUM "+nsum+"\tCURSUM "+rnsum);
            }
            if (rnsum <= nsum) {

                pncount++;
//double estpval = (double)pcount/(double)10000;
//System.out.println("TOTAL "+(dsize+nsize)+"\tITERATION: "+iterate+"\t"+estpval+"\tNSUM "+nsum+"\tCURSUM "+rnsum);
            }
            rnormal = new Vector();
            rdisease = new Vector();
            iterate--;
            icount++;
        }
        ret[0] = (double) pdcount / (double) iteratemax;
        ret[1] = (double) pncount / (double) iteratemax;
        //System.out.print("\n");
        System.out.println("Estimated p-value: " + ret[0] + " i.e. " + pdcount + " out of " + iteratemax + " satisfied the probability in a random test.");

        return ret;
    }

    /**
     * @param n
     * @param d
     */
    private void writeRaw(Vector n, Vector d) {


    }
}
