package mathy;

import util.ParsePath;

import java.util.ArrayList;

/**
 * Correlates the columns in a matrix of data.
 */
public class Correlate {

    String infile, outfile;
    double[][] store;
    double[][] cor;

    String[] labels;

    /**
     * @param args
     */
    public Correlate(String[] args) {
        infile = args[0];
        ParsePath pp = new ParsePath(args[0]);
        if (args.length == 1)
            outfile = pp.getName() + "_cor." + pp.getExt();
        else
            outfile = args[1];

        System.out.println("outfile " + outfile);
        ArrayList[] a = (ArrayList[]) util.TabFile.read(infile);
        int cols = a[0].size() - 1;
        System.out.println("loaded rows: " + a.length + ", columns: " + cols);

        int rows = a.length - 1;
        store = new double[rows][cols];

        System.out.println("out rows " + rows + " columns: " + cols);
        cor = new double[a.length - 1][a.length - 1];
        labels = new String[store.length];
        for (int i = 1; i < a.length; i++) {
            System.out.println("correlate " + i);
            //MoreArray.printArray(MoreArray.ArrayListtoString(a[i]));
            labels[i - 1] = (String) a[i].get(0);
            if (a[i].size() != a[0].size())
                System.out.println("Row count problem " + labels[i - 1] + "\t" + a[i].size() + "\tvs first " + a[0].size());

            /* for (int j = 0; j < a[0].size(); j++) {
             System.out.println("i j "+i+"\t"+j+"\t"+(String) a[i].get(j)+"\tlabel "+labels[i]);
            }*/

            for (int j = 1; j < a[i].size(); j++) {
                //System.out.println("loading "+i+"\t"+j+"\t"+(String) a[i].get(j));
                String c = (String) a[i].get(j);
                try {
                    store[i - 1][j - 1] = Double.parseDouble(c);
                } catch (Exception e) {
                    store[i - 1][j - 1] = Double.NaN;
                }
                //System.out.println("loaded data "+i+"\t"+j+"\t"+store[i][j-1]);
            }
        }
        correlate();
        util.WriteArrayFile.write(cor, outfile, labels);
    }

    /**
     *
     */
    private void correlate() {
        for (int i = 0; i < store.length; i++) {
            double[] a = store[i];
            /*System.out.println("correlate " + i);
            MoreArray.printArray(a);*/
            for (int j = i; j < store.length; j++) {
                double[] b = store[j];
                /*System.out.println("correlate " + i+"\t"+j);
                MoreArray.printArray(b);*/
                double corval = mathy.stat.correlationCoeff(a, b);
                cor[i][j] = corval;
                cor[j][i] = corval;
            }
        }
        System.out.println("correlate " + store[0][0]);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 1 || args.length == 2) {
            Correlate tm = new Correlate(args);
        } else {
            System.out.println("java mathy.Correlate <tab delim numerical data with column labels> <OPTIONAL out file>");
        }
    }
}