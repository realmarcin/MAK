package DataMining.util;

import DataMining.MINER_STATIC;
import DataMining.ValueBlockList;
import DataMining.ValueBlockListMethods;
import mathy.SimpleMatrix;
import util.ParsePath;

import java.io.File;

public class ConvertGeneLabelJsontoVBL {

    ValueBlockList vbl;

    String inpath;
    SimpleMatrix sm;
    String[] exps;

    /**
     * @param args
     */
    public ConvertGeneLabelJsontoVBL(String[] args) {

        System.out.println("args[1] " + args[1]);
        sm = new SimpleMatrix(args[1]);

        inpath = args[0];

        File testf = new File(inpath);


        int explen = sm.xlabels.length;
        exps = new String[explen];
        for (int i = 0; i < explen; i++) {
            exps[i] = "" + (i + 1);
        }


        if (testf.isDirectory()) {
            String[] listf = testf.list();
            for (int i = 0; i < listf.length; i++) {

                String curpath = inpath + "/" + listf[i];
                ParsePath pp = new ParsePath(curpath);
                String prefix = pp.getName();
                doOne(curpath, prefix);
            }
        } else {
            String path = inpath;
            ParsePath pp = new ParsePath(inpath);
            String prefix = pp.getName();
            doOne(path, prefix);
        }
    }


    /**
     * @param path
     */
    private void doOne(String path, String prefix) {

        try {
            vbl = ValueBlockListMethods.readJSONGenes(path, sm.ylabels, exps);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("refvbl " + vbl.size());

        String out_str = vbl.toString(MINER_STATIC.HEADER_VBL);
        String outpath = prefix + "_allexp.vbl";
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
                    "<gene set json file or dir>\n" +
                    "<input data>"
            );
        }
    }
}
