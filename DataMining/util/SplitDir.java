package DataMining.util;

import DataMining.ValueBlock;
import DataMining.ValueBlockList;
import DataMining.ValueBlockListMethods;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: Mar 6, 2012
 * Time: 10:05:47 PM
 */
public class SplitDir {

    ValueBlockList rstart, cstart;
    String outpath;
    int iscol = -1;
    String iscolStr;

    /**
     * @param args
     */
    public SplitDir(String[] args) {

        try {
            rstart = ValueBlockList.read(args[1], false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            rstart = ValueBlockList.read(args[2], false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        outpath = args[2];
        File test2 = new File(outpath);
        boolean b = test2.mkdir();

        if (args[3].equals("col"))
            iscol = 1;
        else if (args[3].equals("row"))
            iscol = 0;
        else {
            System.out.println("iscol not defined" + args[3]);
            System.exit(0);
        }

        iscolStr = iscol == 1 ? "col" : "row";

        runOne(args[0]);
    }

    /**
     * @param indir
     * @return
     */
    private void runOne(String indir) {
        File f = new File(indir);
        String[] list = f.list();

        //for all trajectories in the input dir
        for (int i = 0; i < list.length; i++) {
            ValueBlockList vbl = null;
            try {
                String inStr = indir + "/" + list[i];
                vbl = ValueBlockList.read(inStr, false);
                ValueBlock v = (ValueBlock) vbl.get(0);

                int rind = rstart.blockIndex(v);
                int cind = cstart.blockIndex(v);
                int ind = -1;

                if (iscol == 0)
                    ind = rind;
                else if (iscol == 1)
                    ind = cind;

                if (ind != -1) {
                    System.out.println("copy to row " + inStr);
                    copy(inStr, outpath);
                }

                ValueBlock row = (ValueBlock) rstart.get(rind);
                ValueBlock col = (ValueBlock) cstart.get(cind);

                System.out.println(i + "\t" + iscolStr + "\t" + "R: " + row.full_criterion +
                        "\tC: " + col.full_criterion);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param inStr
     * @throws IOException
     */
    private final static void copy(String inStr, String rpath) throws IOException {
        File inputFile = new File(inStr);
        File outputFile = new File(rpath + "/" + inStr);

        FileReader in = new FileReader(inputFile);
        FileWriter out = new FileWriter(outputFile);
        int c;

        while ((c = in.read()) != -1)
            out.write(c);

        in.close();
        out.close();
    }


    /**
     * @param args
     */
    public final static void main(String[] args) {

        if (args.length == 5) {
            SplitDir rm = new SplitDir(args);
        } else {
            System.out.println("syntax: java DataMining.util.SplitDir\n" +
                    "<dir of ValueBlockLists>\n" +
                    "<rstart file>\n" +
                    "<cstart file>\n" +
                    "<outdir>\n" +
                    "<col/row>"
            );
        }
    }
}
