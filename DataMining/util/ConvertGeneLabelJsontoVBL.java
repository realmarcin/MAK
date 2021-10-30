package DataMining.util;

import DataMining.MINER_STATIC;
import DataMining.ValueBlockList;
import DataMining.ValueBlockListMethods;
import mathy.SimpleMatrix;
import util.ParsePath;

public class ConvertGeneLabelJsontoVBL {

    ValueBlockList vbl;
    String prefix;

    String inpath;

    /**
     * @param args
     */
    public ConvertGeneLabelJsontoVBL(String[] args) {

        System.out.println("args[1] " + args[1]);
        SimpleMatrix sm = new SimpleMatrix(args[1]);

        inpath = args[0];

        String[] exps = new String[100];
        for(int i=0;i<100;i++){
            exps[i] = ""+(i + 1);
        }
        try {
            vbl = ValueBlockListMethods.readJSONGenes(inpath, sm.ylabels, exps);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ParsePath pp = new ParsePath(inpath);
        prefix = pp.getName();

        System.out.println("refvbl " + vbl.size());

        String out_str = vbl.toString(MINER_STATIC.HEADER_VBL);
        String outpath = prefix + ".vbl";
        System.out.println("writing " + outpath);
        System.out.println("outpath :" + outpath + ":");
        util.TextFile.write(out_str, outpath);
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 2) {
            DataMining.util.ConvertGeneLabelJsontoVBL rm = new DataMining.util.ConvertGeneLabelJsontoVBL(args);
        } else {
            System.out.println("syntax: java DataMining.util.ConvertGeneLabelJsontoVBL\n" +
                    "<gene set json file>\n" +
                    "<input data>"
            );
        }
    }
}
