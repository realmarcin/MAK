package util;

import java.util.ArrayList;

/**
 * User: marcin
 * Date: Apr 15, 2009
 * Time: 6:14:54 PM
 */
public class MatrixtoList {


    /**
     * @param args
     */
    public MatrixtoList(String[] args) {
        String[][] matrix = TabFile.readtoArray(args[0]);
        System.out.println(matrix[0][0] + "\t" +
                matrix[1][0] + "\t" +
                matrix[0][1] + "\t" +
                matrix[1][1]);
        ArrayList out = new ArrayList();

        for (int i = 1; i < matrix.length; i++) {
            for (int j = 1; j < matrix[0].length; j++) {
                String s = matrix[i][0] + "_" + matrix[j][0] + "\t" + matrix[i][j];
                //System.out.println("s "+i+"\t"+j+"\t"+s);
                out.add(s);
            }
        }
        TextFile.write(out, args[1]);
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 2) {
            MatrixtoList ce = new MatrixtoList(args);
        } else {
            System.out.println("syntax: java util.MatrixtoList\n" +
                    "<matrix file>\n" +
                    "<out file>\n");
        }
    }
}
