package mathy;

import java.util.ArrayList;

/**
 *
 */
public class VectRatio {

    String outdata = "";
    String outdir = "";

    /**
     * @param args
     */
    public VectRatio(String[] args) {

        ArrayList read = util.TabFile.readtoList(args[0]);

        outdir = args[1];

        for (int i = 0; i < read.size(); i++) {

            ArrayList cur = (ArrayList) read.get(i);

            double first = Double.parseDouble((String) cur.get(0));
            double second = Double.parseDouble((String) cur.get(1));

            outdata += second / first + "\n";
        }

        util.TextFile.write(outdata, outdir);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {

        if (args.length == 2) {

            VectRatio vr = new VectRatio(args);
        } else {

            System.out.println("syntax: java mathy.VectRatio <data> <outpath>");
        }

    }
}