package mathy;

import util.ArrayCopy;
import util.ParsePath;

import java.io.File;
import java.util.ArrayList;

/**
 * Converts a tab delim file of numerical data using a math operation on the columns.
 */

public class TabtoMath {

    double[][] store;
    String outfile;
    String infile;

    public TabtoMath(String[] args) {

        infile = args[0];
        String origin = args[0];

        File test = new File(infile);

        if (!test.isDirectory()) {

            ParsePath pp = new ParsePath(args[0]);

            if (args.length == 1)
                outfile = pp.getName() + "_tabtomath.out";
            else
                outfile = args[1];

            run();
        } else {

            String[] list = test.list();
            for (int i = 0; i < list.length; i++) {
                infile = origin + "/" + list[i];
                ParsePath pp = new ParsePath(infile);

                outfile = origin + "/" + pp.getName() + "_tabtomath.out";

                run();
            }
        }


    }


    /**
     *
     */
    private void run() {


        System.out.println("outfile " + outfile);

        ArrayList[] a = (ArrayList[]) util.TabFile.read(infile);

        //System.out.println("loaded "+a.length+" columns.");

        store = new double[a.length][a[0].size()];

        for (int i = 0; i < a.length; i++) {

            for (int j = 0; j < a[0].size(); j++) {

                String c = (String) a[i].get(j);
                store[i][j] = Double.parseDouble(c);
                //System.out.println("loaded data "+i+"\t"+j+"\t"+store[i][j]);
            }
        }


        doStuff();

        ArrayList[] w = loadtoArray();

        System.out.println("writing " + outfile);
        util.TabFile.write(w, outfile);
    }


    /**
     * Does the math operation on columns.
     */
    private void doStuff() {

        double[][] data = ArrayCopy.copy(store);

        store = new double[1][store[0].length];

        for (int i = 0; i < data[0].length; i++) {

            store[0][i] = data[0][i] / data[1][i];//mathy.stat.normtoPercent(store[i]);

            //System.out.println(data.length+"\t"+store[0][i]+"\t"+data[1][i]+"\t"+data[0][i]);
        }
    }

    /**
     * Loads and writes the array data to tab file.
     */
    public ArrayList[] loadtoArray() {

        ArrayList[] w = new ArrayList[store.length];

        for (int i = 0; i < store.length; i++) {

            w[i] = new ArrayList();
        }

        for (int i = 0; i < store.length; i++) {

            ArrayList now = new ArrayList();
            for (int j = 0; j < store[0].length; j++) {

                w[i].add((String) "" + store[i][j]);
            }
        }
        return w;
    }

    public static void main(String[] args) {

        if (args.length == 1 || args.length == 2) {

            TabtoMath tm = new TabtoMath(args);
        } else {
            System.out.println("java mathy.TabtoMath <tab delim numerical data or dir> <optional - out file>");
        }
    }

}