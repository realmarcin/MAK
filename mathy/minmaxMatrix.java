package mathy;

import util.ParsePath;

import java.io.File;

/**
 * Class to count the min/max values in a dir or dir-o-dirs of Matrix (.mat) files.
 */

public class minmaxMatrix {

    String indir;
    double min = Double.POSITIVE_INFINITY;
    double max = Double.NEGATIVE_INFINITY;
    ;
    int level = 1;
    String[] list1;
    String[][] list2;

    public minmaxMatrix(String[] args) {

        indir = args[0];
        level = Integer.parseInt(args[1]);

        File test = new File(indir);
        list1 = test.list();

        if (level == 2) {

            list2 = new String[list1.length][];

            for (int i = 0; i < list1.length; i++) {

                File test2 = new File(indir + list1[i]);
                list2[i] = test2.list();
            }

            runLevel2();
        } else
            runLevel1();


    }

    private void runLevel2() {

        int count = 0;

        for (int i = 0; i < list1.length; i++) {

            SuperMatrix now = new SuperMatrix();
            now.load(indir + "/" + list1[i], 14, 14);

            double[] get = now.findMinMaxNonDiag();

            System.out.println("DIR:\t\t" + list1[i]);
            System.out.println("Min: " + get[0]);
            System.out.println("Max: " + get[1]);
        }

    }

    private void runLevel1() {

        for (int i = 0; i < list1.length; i++) {

            Matrix now = new Matrix();
            String r = indir + "/" + list1[i];
            System.out.println(r);

            ParsePath pp = new ParsePath(r);
            if (pp.getExt().equals(".mat")) {

                now.load(r);
                double tn = now.findMin();
                double tx = now.findMax();


                if (Double.isNaN(min))
                    min = tn;
                else if (min > tn)
                    min = tn;
                if (Double.isNaN(max))
                    max = tx;
                else if (max > tx)
                    max = tx;
            }
        }

        System.out.println("Min: " + min);
        System.out.println("Max: " + max);

    }


    public static void main(String[] args) {


        if (args.length != 2) {
            System.out.println("syntax: java mathy.minmaxMatrix <indir> <level>");
        } else {

            minmaxMatrix mmm = new minmaxMatrix(args);
        }

    }

}