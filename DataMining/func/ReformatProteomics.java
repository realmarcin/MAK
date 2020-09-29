package DataMining.func;

import util.MoreArray;
import util.TabFile;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: 7/15/16
 * Time: 11:14 AM
 */
public class ReformatProteomics {


    /**
     * @param args
     */
    public ReformatProteomics(String[] args) {


        String[][] data = TabFile.readtoArray(args[0]);

        String[] out = new String[data.length - 1];
        for (int i = 1; i < data.length; i++) {
            try {
                out[i - 1] = data[i][1] + "_" + data[i][0] + "_" + data[i][2] + "_" + data[i][6] + "\t" + data[i][3] + "\t" + data[i][4];// StringUtil.replace(" ", "", data[i][6])
            } catch (Exception e) {
                System.out.println(i);
                System.out.println(MoreArray.toString(data[i]));
                e.printStackTrace();
            }
        }
        System.out.println("writing");
        TabFile.write(out, args[0] + "_reformat.txt");
        System.out.println("done");
    }


    /**
     * @param args
     */

    public static void main(String[] args) {
        if (args.length == 1) {
            ReformatProteomics rm = new ReformatProteomics(args);
        } else {
            System.out.println("syntax: java DataMining.func.ReformatProteomics\n" +
                    "<input file>\n"

            );
        }
    }
}
