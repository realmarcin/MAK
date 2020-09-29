package mathy;

import util.ParsePath;

import java.io.File;
import java.util.ArrayList;

public class BinData {

    String outdata = "", outdir, inpath;

    double[] x;
    double[] y;
    double min, max;

    int rowsize, bins;
    double binsize;

    /**
     * @param args
     */
    public BinData(String[] args) {

        inpath = args[0];
        outdir = args[1];
        bins = Integer.parseInt(args[2]);


        File inf = new File(inpath);
        boolean test = inf.isDirectory();

        if (!test) {

            run(inpath);
        } else {


            String[] l = inf.list();
            String origdir = outdir;
            for (int i = 0; i < l.length; i++) {

                ParsePath p = new ParsePath(l[i]);

                outdir = origdir + "" + p.getName() + "_bin.txt";
                String curf = inpath + "" + l[i];
                run(curf);
            }
        }


    }

    /**
     *
     */
    private void run(String f) {

        ArrayList read = util.TabFile.readtoList(f);

        System.out.println("reading " + f);

        x = new double[read.size()];
        y = new double[read.size()];


        for (int i = 0; i < read.size(); i++) {

            ArrayList curt = (ArrayList) read.get(i);
            rowsize = ((ArrayList) read.get(i)).size();

            //for(int k=0;k<curt.size();k++)
            //    System.out.println((string)curt.get(k));

            x[i] = Double.parseDouble((String) curt.get(0));
            y[i] = Double.parseDouble((String) curt.get(1));
        }


        min = stat.findMin(x);
        max = stat.findMax(x);

        System.out.println(min + "\t" + max);

        if (min < 0)
            x = stat.addVal(x, Math.abs(min));

        binsize = ((double) max - min + 1) / (double) bins;

        int[] cy = putIntoBins(y);
        int[] cz = null;

        printBins(cy);

        util.TextFile.write(outdata, outdir);
    }

    /**
     * @param cy
     */
    private void printBins(int[] cy) {

        for (int i = 0; i < bins; i++) {

            double curbin = (((double) i) * binsize) + min;
            //System.out.println(curbin);

            outdata += curbin + "\t" + cy[i] + "\n";
        }
    }

    /**
     * @param d
     * @return
     */
    private int[] putIntoBins(double[] d) {


        int[] ret = new int[bins];

        for (int i = 0; i < x.length; i++) {

            int curbin = (int) (x[i] / binsize);

            //System.out.println(curbin+"\t"+x[i]+"\t"+(x[i]+min));

            ret[curbin]++;//=y[i];
        }

        return ret;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {

        if (args.length == 3) {

            BinData vr = new BinData(args);
        } else {

            System.out.println("syntax: java mathy.BinData <data> <outpath> <bins>");
        }

    }
}