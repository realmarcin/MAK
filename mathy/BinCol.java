package mathy;

import util.ParsePath;

import java.io.File;
import java.util.ArrayList;

public class BinCol {

    String outdata = "", outdir, inpath;

    double[] x;
    double min, max;

    int rowsize, bins;
    double binsize;

    /**
     * @param args
     */
    public BinCol(String[] args) {

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

        outdata = "";

        load(f);

        min = stat.findMin(x);
        max = stat.findMax(x);

        System.out.println("min " + min + "\tmax " + max);

        if (min < 0)
            x = stat.addVal(x, Math.abs(min));

        binsize = ((double) max - min) / (double) bins;
        System.out.println("bins " + bins + "\tbinsize " + binsize);

        int[] cx = putIntoBins(x);

        printBins(cx);

        util.TextFile.write(outdata, outdir);
    }


    /**
     * @param d
     * @return
     */
    private int[] putIntoBins(double[] d) {


        int[] ret = new int[bins];

        for (int i = 0; i < d.length; i++) {

            int curbin = (int) (d[i] / binsize);
            //System.out.println(curbin+"\t"+d[i]);

            if (curbin >= ret.length)
                curbin = bins - 1;

            ret[curbin]++;
            //System.out.println(curbin+"\t"+d[i]+"\t"+ret[curbin]);
        }

        return ret;
    }


    /**
     * @param indata
     */
    private void load(String indata) {

        ArrayList read = util.TabFile.readtoList(indata);

        System.out.println("reading " + indata);

        x = new double[read.size()];

        for (int i = 0; i < read.size(); i++) {

            ArrayList curt = (ArrayList) read.get(i);
            rowsize = ((ArrayList) read.get(i)).size();

            x[i] = Double.parseDouble((String) curt.get(0));
        }
    }

    /**
     *
     */
    private void printBins(int[] cy) {

        for (int i = 0; i < bins; i++) {

            double curbin = 0;
            if (min < 0)
                curbin = (((double) i) * binsize) + min;
            else
                curbin = (((double) i) * binsize);
            //System.out.println(curbin);

            outdata += curbin + "\t" + cy[i] + "\n";
        }
    }


    /**
     * @param args
     */
    public static void main(String[] args) {

        if (args.length == 3) {

            BinCol vr = new BinCol(args);
        } else {

            System.out.println("syntax: java mathy.BinCol <data> <outpath> <bins>");
        }

    }
}
