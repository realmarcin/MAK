package DataMining.util;

import DataMining.*;
import mathy.SimpleMatrix;
import util.MoreArray;
import util.ParsePath;

public class ConverttoGeneLabelSets {

    ValueBlockList vbl;

    String outpath = null;
    String prefix;

    String inpath;

    /**
     * @param args
     */
    public ConverttoGeneLabelSets(String[] args) {

        inpath = args[0];
        try {
            vbl = ValueBlockListMethods.readAny(inpath, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ParsePath pp = new ParsePath(inpath);
        prefix = pp.getName();
        int size = vbl.size();

        SimpleMatrix sm = new SimpleMatrix(args[1]);

        String out_str = "[";
        for (int i = 0; i < size; i++) {
            if (i % 100 == 0)
                System.out.print(i + "/" + vbl.size() + " ");
            ValueBlock vi = (ValueBlock) vbl.get(i);

            String[] curlabels = new String[vi.genes.length];
            for (int j = 0; j < vi.genes.length; j++) {
                int curind = vi.genes[j];
                curlabels[j] = sm.ylabels[curind - 1];
            }

            out_str += "[" + MoreArray.toString(curlabels, ",") + "]";
            if (i < size - 1)
                out_str += ", ";
        }

        out_str += "]";

        String outpath = prefix + ".json";
        System.out.println("outpath :" + outpath + ":");
        util.TextFile.write(out_str, outpath);


    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 2) {
            ConverttoGeneLabelSets rm = new ConverttoGeneLabelSets(args);
        } else {
            System.out.println("syntax: java DataMining.util.ConverttoGeneLabelSets\n" +
                    "<biclusters file>\n" +
                    "<input data>"
            );
        }
    }
}
