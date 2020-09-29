package mathy;

import util.MoreArray;
import util.TabFile;

/**
 * User: marcinjoachimiak
 * Date: May 20, 2008
 * Time: 10:01:58 PM
 */
public class FillMatrix {


    /**
     * @param args
     */
    public FillMatrix(String[] args) {

        String[][] in = TabFile.readtoArray(args[0]);
        double[][] data = MoreArray.convfromString(in);

        double[][] filled = new double[data.length + 9][data[0].length + 9];

        for (int i = 0; i < filled.length; i++) {
            for (int j = 0; j < filled[0].length; j++) {
                if (i < data.length && j < data[0].length && !Double.isNaN(data[i][j]))
                    filled[i][j] = data[i][j];
                else {
                    int imod = (int) ((double) i / 10.0) * 10;
                    int jmod = (int) ((double) j / 10.0) * 10;
                    System.out.println(i + "\t" + j + "\t" + imod + "\t" + jmod);
                    filled[i][j] = data[imod][jmod];
                }
            }
        }
        Matrix.writeTab(filled,  args[0] + "_filled.txt");
        //String[][] out = MoreArray.toString(filled);
        //TabFile.write(out, args[0] + "_filled.txt", "", "");
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 1) {
            FillMatrix rm = new FillMatrix(args);
        } else {
            System.out.println("syntax: java mathy.FillMatrix <input file>");
        }
    }
}
