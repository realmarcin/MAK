package util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * User: marcin
 * Date: Nov 5, 2010
 * Time: 11:17:32 AM
 */
public class MakePairs {

    String delim = "_";

    /**
     * @param args
     */
    public MakePairs(String[] args) {

        String infile = args[0];
        String outfile = null;
        ParsePath pp = new ParsePath(infile);
        outfile = pp.getName() + "_pair." + pp.getExt();

        if (args.length == 2)
            delim = args[1];

        String[][] sarray = TabFile.readtoArray(infile);
        int col = -1;
        if (sarray[0].length > 1) {
            col = 2;
        } else
            col = 1;
        String[] gene_labels = MoreArray.replaceAll(MoreArray.extractColumnStr(sarray, col), "\"", "");

        try {
            PrintWriter out = new PrintWriter(new FileWriter(outfile), true);

            for (int i = 0; i < gene_labels.length; i++)
                for (int j = i + 1; j < gene_labels.length; j++)
                    out.print(gene_labels[i] + delim + gene_labels[j]+"\n");

            out.close();
        } catch (IOException e) {
            System.out.println("Error creating or writing file " + outfile);
            System.out.println("IOException: " + e.getMessage());
        }

    }

    /**
     * @param args
     */
    public final static void main(String[] args) {
        if (args.length == 1 || args.length == 2) {
            MakePairs ce = new MakePairs(args);
        } else {
            System.out.println("syntax: java util.MakePairs <in file> <OPTIONAL delim>");
        }
    }
}
