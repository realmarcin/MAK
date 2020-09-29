package util;


/**
 * This class is BROKEN -- does not reliably parse very sparse matrices.
 * <p/>
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: Jun 17, 2011
 * Time: 10:18:12 PM
 */
public class TxttoBinary {

    String infile, outfile;

    public TxttoBinary(String[] args) {
        infile = args[0];
        if (args.length == 2)
            outfile = args[1];
        else
            outfile = infile + "_binary.txt";
        String[][] read = TabFile.readtoArray(infile);
        //MoreArray.printArray(read[1]);
        System.out.println("load " + read.length + "\t" + read[0].length);
        int[][] out = new int[read.length - 1][read[0].length - 1];

        String[] xlabels = read[0];
        xlabels = MoreArray.removeEntry(xlabels, 1);

        String[] ylabels = MoreArray.extractColumnStr(read, 1);
        ylabels = MoreArray.removeEntry(ylabels, 1);


        for (int i = 1; i < read.length; i++) {
            for (int j = 1; j < read[0].length; j++) {
                if (read[i][j] == null || read[i][j].length() == 1 || read[i][j].equals("")) {
                    out[i - 1][j - 1] = 0;
                    if (i < 5 && j < 5)
                        System.out.println("not   " + 0 + "\t" + i + "\t" + j + "\t" + ylabels[i - 1] + "\t" + xlabels[j - 1] + "\t:" + read[i][j] + ":");
                } else {
                    if (i < 5 && j < 5)
                        out[i - 1][j - 1] = 1;
                    System.out.println("ortho " + 1 + "\t" + i + "\t" + j + "\t" + ylabels[i - 1] + "\t" + xlabels[j - 1] + "\t:" + read[i][j] + ":");
                }
            }
        }

        /*int[] colsums = Matrix.columnSum(out);
        ArrayList ar = new ArrayList();
        System.out.println("colsums "+colsums.length);
        for (int i = 0; i < colsums.length; i++) {
            if (colsums[i] == 0) {
                ar.add(i);
            }
        }
        read = MoreArray.removeColumns(read, ar);*/
        String[][] outstr = MoreArray.toString(out, "", "");
        /*System.out.println("before "+outstr.length+"\t"+outstr[0].length);
        outstr = MoreArray.removeColumns(outstr, ar);
        System.out.println("after "+outstr.length+"\t"+outstr[0].length);*/

        TabFile.write(outstr, outfile, xlabels, ylabels);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 1 || args.length == 2) {
            TxttoBinary ce = new TxttoBinary(args);
        } else {
            System.out.println("usage: java util.TxttoBinary\n" +
                    "<infile>\n" +
                    "<OPTIONAL outfile>"
            );
        }
    }
}
