package DataMining.util;

import util.MoreArray;
import util.TabFile;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: Aug 24, 2012
 * Time: 11:29:45 PM
 */
public class MatrixtoPCL {

    /**
     * @param args
     */
    public MatrixtoPCL(String[] args) {

        String[][] data = TabFile.readtoArray(args[0]);

        System.out.println(data.length + "\t" + data[0].length);

        int[] onesx = new int[data[0].length];
        int[] onesy = new int[data.length + 1];
        onesx = MoreArray.initArray(onesx, 1);
        onesy = MoreArray.initArray(onesy, 1);

        String[] onesxS = MoreArray.toStringArray(onesx);
        String[] onesyS = MoreArray.toStringArray(onesy);

        data = MoreArray.insertRow(data, onesxS, 2);
        data[1][0] = "";
        data = MoreArray.insertColumn(data, onesyS, 2);
        data[0][1] = "";
        data[1][1] = "";

        String[] names = MoreArray.extractColumnStr(data, 1);
        data = MoreArray.insertColumn(data, names, 2);

        data[0][0] = "YORF";
        data[0][1] = "NAME";
        data[0][2] = "GWEIGHT";
        data[1][0] = "EWEIGHT";
        data[1][1] = "";
        data[1][2] = "";

        String out = args[0] + ".pcl";
        TabFile.write(data, out);
    }


    /**
     * @param args
     */
    public final static void main(String[] args) {
        if (args.length == 1) {
            MatrixtoPCL cde = new MatrixtoPCL(args);
        } else {
            System.out.println("syntax: util.MatrixtoPCL\n" +
                    "<input file>"
            );
        }
    }
}
