package DataMining.util;

import DataMining.ValueBlock;
import DataMining.ValueBlockList;
import DataMining.ValueBlockListMethods;
import util.MoreArray;
import util.TabFile;
import util.TextFile;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: marcin
 * Date: 4/5/18
 * Time: 3:15 PM
 */
public class AnnotateBiclusterColumns {


    public ValueBlockList vbl;
    public String[] col_labels;


    /**
     * @param args
     */
    public AnnotateBiclusterColumns(String[] args) {

        try {
            vbl = ValueBlockListMethods.readAny(args[0], false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String[][] sarray = TabFile.readtoArray(args[1]);
            System.out.println("setLabels c " + sarray.length + "\t" + sarray[0].length);
            int col = 2;
            String[] n = MoreArray.extractColumnStr(sarray, col);
            col_labels = MoreArray.replaceAll(n, "\"", "");
            System.out.println("setLabels col " + col_labels.length + "\t" + col_labels[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("col_labels " + col_labels.length);
        ArrayList out = new ArrayList();

        for (int i = 0; i < vbl.size(); i++) {
            ValueBlock vb = (ValueBlock) vbl.get(i);

            out.add("\nbicluster" + i);
            int[] cols = vb.exps;
            for (int j = 0; j < cols.length; j++) {

                out.add(col_labels[cols[j] - 1]);
            }
        }

        TextFile.write(out, args[0] + ".col_labels.txt");
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 2) {
            AnnotateBiclusterColumns rm = new AnnotateBiclusterColumns(args);
        } else {
            System.out.println("syntax: java DataMining.util.AnnotateBiclusterColumns\n" +
                    "<biclusters file>\n" +
                    "<column labels>"
            );
        }
    }
}
