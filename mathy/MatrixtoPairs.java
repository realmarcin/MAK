package mathy;

import util.MoreArray;
import util.StringUtil;
import util.TabFile;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * User: marcin
 * Date: Apr 9, 2010
 * Time: 4:57:17 PM
 */
public class MatrixtoPairs {
    double[][] secondData;
    String[] secondData_xlabels, secondData_ylabels;
    String outfile;
    double cutoff = Double.NaN;
    boolean xaxis = true;

    /**
     * @param args
     */
    public MatrixtoPairs(String[] args) {
        cutoff = Double.parseDouble(args[1]);
        System.out.println("cutoff "+cutoff);
        if (args[2].equals("y"))
            xaxis = false;
        outfile = args[3];
        String[][] tmp = TabFile.readtoArray(args[0]);
        secondData_xlabels = tmp[0];
        secondData_xlabels = MoreArray.removeEntry(secondData_xlabels, 1);
        secondData_xlabels = StringUtil.replace(secondData_xlabels, "\"", "");
        MoreArray.printArray(secondData_xlabels);
        tmp = MoreArray.removeRow(tmp, 1);
        secondData_ylabels = MoreArray.extractColumnStr(tmp, 1);
        tmp = MoreArray.removeColumn(tmp, 1);
        secondData_ylabels = StringUtil.replace(secondData_ylabels, "\"", "");
        secondData = MoreArray.convfromString(tmp);

        int count =0;
        try {
            PrintWriter out = new PrintWriter(new FileWriter(outfile));
            if (xaxis) {
                for (int i = 0; i < secondData_ylabels.length; i++) {
                    for (int j = 0; j < secondData_xlabels.length; j++) {
                        if (secondData[i][j] > cutoff) {
                            out.println(secondData_ylabels[i] + "\t" + secondData_xlabels[j] + "\t" + secondData[i][j]);
                            count++;
                        }
                    }
                }
            } else {
                for (int j = 0; j < secondData_xlabels.length; j++) {
                    for (int i = 0; i < secondData_ylabels.length; i++) {
                        if (secondData[i][j] > cutoff) {
                            System.out.println("passed " + j + "\t" + i + "\t" + secondData_xlabels[j] + "\t" + secondData_ylabels[i] + "\t" + secondData[i][j]);
                            out.println(secondData_xlabels[j] + "\t" + secondData_ylabels[i] + "\t" + secondData[i][j]);
                             count++;
                        }
                    }
                }
            }
            out.close();
        } catch (IOException e) {
            System.out.println("Error creating or writing file " + outfile);
            System.out.println("IOException: " + e.getMessage());
        }
            System.out.println("count " + count);
    }


    /**
     * @param args
     */
    public final static void main(String[] args) {
        if (args.length == 4) {
            MatrixtoPairs cde = new MatrixtoPairs(args);
        } else {
            System.out.println("syntax: fungen.MatrixtoPairs\n" +
                    "<interaction matrix tab text file>\n" +
                    "<cutoff>\n" +
                    "<first axis: x or y>\n" +
                    "<outfile>\n");
        }
    }
}
