package util;

import java.util.ArrayList;

/**
 * User: marcin
 * Date: Apr 17, 2009
 * Time: 10:30:29 AM
 */
public class MatrixPairLookup {

    /**
     * @param args
     */
    public MatrixPairLookup(String[] args) {
        String[][] matrix = TabFile.readtoArray(args[0]);
        String[][] pairs = TabFile.readtoArray(args[1]);

        System.out.println(matrix[0][0] + "\t" +
                matrix[1][0] + "\t" +
                matrix[0][1] + "\t" +
                matrix[1][1]);
        ArrayList out = new ArrayList();

        String[] cols = matrix[0];
        String[] rows = MoreArray.extractColumnStr(matrix, 1);

        for (int i = 0; i < pairs.length; i++) {

            int A = MoreArray.getArrayInd(cols, pairs[i][0]);
            int B = MoreArray.getArrayInd(rows, pairs[i][1]);
            String s1 = null;
            if (A != -1 && B != -1) {
                s1 = matrix[A][B];
            }
            String s = pairs[i][0] + "_" + pairs[i][1] + "\t" + s1;
            //System.out.println("s "+i+"\t"+j+"\t"+s);
            out.add(s);
        }
        TextFile.write(out, args[2]);
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 3) {
            MatrixPairLookup ce = new MatrixPairLookup(args);
        } else {
            System.out.println("syntax: java util.MatrixtoList\n" +
                    "<matrix file>\n" +
                    "<pair file>\n" +
                    "<out file>\n");
        }
    }
}
