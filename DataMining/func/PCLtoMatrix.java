package DataMining.func;

import util.MoreArray;
import util.ParsePath;
import util.TabFile;
import util.TextFile;

import java.util.ArrayList;

/**
 * Class to convert a PCL format file to a matrix and to generate some helper scripts.
 * <p/>
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: Dec 4, 2012
 * Time: 9:20:43 PM
 */
public class PCLtoMatrix {

    /**
     * @param args
     */
    public PCLtoMatrix(String[] args) {

        String[][] data = TabFile.readtoArray(args[0]);

        ArrayList rem = new ArrayList();
        rem.add(2);
        rem.add(3);

        data = MoreArray.removeColumns(data, rem);
        data = MoreArray.removeRow(data, 2);
        data[0][0] = "";
        TabFile.write(data, args[0] + "_" + args[1] + ".txt");


        for (int i = 1; i < data.length; i++) {
            data[i][0] = "" + (i);
        }
        for (int i = 1; i < data[0].length; i++) {
            data[0][i] = "" + i;
        }
        TabFile.write(data, args[0] + "_" + args[1] + "_ind.txt");

        String s = MoreArray.toString(data[0], "\t");
        String head = s.substring(s.indexOf("\t") + 1) + "\n";
        data = MoreArray.removeRow(data, 1);
        String Rname = args[0] + "_" + args[1] + "_R.txt";
        TabFile.write(data, Rname, "\t", head);

        String prefix = args[0] + "_" + args[1] + ".Rdata";
        String Rs = "expr_data <- read.table(\"" + Rname + "\", sep=\"\\t\", header=T)\n";
        Rs += "expr_data <- as.matrix(expr_data)\n";
        Rs += "save(expr_data,file=\"" + prefix + "\")\n";

        TextFile.write(Rs, args[0] + "_" + args[1] + "_Rscript.txt");
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 2) {
            PCLtoMatrix rm = new PCLtoMatrix(args);
        } else {
            System.out.println("syntax: java DataMining.func.PCLtoMatrix\n" +
                    "<PCL file>\n" +
                    "<set id>"
            );
        }
    }
}
