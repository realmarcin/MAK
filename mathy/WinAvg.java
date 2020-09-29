package mathy;

import java.util.ArrayList;

public class WinAvg {


    public WinAvg(String[] args) {

        String outdata = "";
        String outdir = "";

        ArrayList read = util.TabFile.readtoList(args[0]);

        outdir = args[1];

        for (int i = 0; i < read.size(); i++) {

            ArrayList curt = (ArrayList) read.get(i);
            int rowsize = ((ArrayList) read.get(i)).size();

            double[][] data = new double[rowsize][5];
            for (int j = i - 2; j < i + 3; j++) {

                if (j >= 0 && j < read.size()) {

                    ArrayList cur = (ArrayList) read.get(j);

                    double[] get = retDouble(cur);

                    for (int k = 0; k < get.length; k++) {
                        //System.out.println(k+"\t"+(i-j+2));
                        data[k][i - j + 2] = get[k];
                    }
                }
            }

            for (int l = 0; l < rowsize; l++) {
                if (l < 2)
                    outdata += mathy.stat.avg(data[l]) + "\t";
                else
                    outdata += mathy.stat.avg(data[l]) + "\n";
            }
        }

        util.TextFile.write(outdata, outdir);
    }

    /**
     * @param c
     * @return
     */
    private double[] retDouble(ArrayList c) {

        double[] d = new double[c.size()];

        for (int i = 0; i < c.size(); i++)
            d[i] = Double.parseDouble((String) c.get(i));

        return d;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {

        if (args.length == 2) {

            WinAvg vr = new WinAvg(args);
        } else {

            System.out.println("syntax: java mathy.WinAvg <data> <outpath>");
        }

    }
}