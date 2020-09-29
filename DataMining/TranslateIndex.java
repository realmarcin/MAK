package DataMining;

import mathy.Matrix;
import util.MoreArray;
import util.TabFile;

import java.io.File;

/**
 * User: marcin
 * Date: Apr 20, 2010
 * Time: 3:08:57 PM
 */
public class TranslateIndex {

    /**
     * @param args
     */
    public TranslateIndex(String[] args) {

        String indir = args[0];
        String outdir = args[3];

        String[][] r_str = TabFile.readtoArray(args[1]);
        int[][] r_ind = MoreArray.convfromStringtoInt(r_str);
        String[][] c_str = TabFile.readtoArray(args[2]);
        int[][] c_ind = MoreArray.convfromStringtoInt(c_str);

        File dir = new File(indir);
        String[] list = dir.list();
        for (int i = 0; i < list.length; i++) {
            String f = indir + "/" + list[i];
            System.out.println("reading " + f);
            ValueBlockList one = null;
            try {
                one = ValueBlockListMethods.readBICTranslatetoInt(f);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ValueBlockListMethods.translateIndices(Matrix.extractColumn(r_ind, 2), Matrix.extractColumn(c_ind, 2), one);
            String s = outdir + "/" + list[i];
            ValueBlockListMethods.writeBIC(s, one);
        }
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 4) {
            TranslateIndex rm = new TranslateIndex(args);
        } else {
            System.out.println("syntax: java DataMining.TranslateIndex\n" +
                    "<in dir>\n" +
                    "<row index>\n" +
                    "<col index>\n" +
                    "<out dir>");
        }
    }
}
