package DataMining.util;

import mathy.SimpleMatrix;
import util.StringUtil;
import util.TextFile;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: marcin
 * Date: 4/8/18
 * Time: 11:48 AM
 */
public class MatrixtoPairs {

    /**
     * @param args
     */
    public MatrixtoPairs(String[] args) {

        SimpleMatrix sm = new SimpleMatrix(args[0]);
        ArrayList out = new ArrayList();

        double cutoff = Double.parseDouble(args[1]);

        for (int i = 0; i < sm.ylabels.length; i++) {
            String ylab = sm.ylabels[i];
            for (int j = i+1; j < sm.xlabels.length; j++) {
                if (sm.data[i][j] > cutoff) {
                    String xlab = sm.xlabels[j];
                    //out.add(ylab + " has a " + xlab + " and ");
                    out.add(ylab + "\t" + xlab+"\t"+sm.data[i][j]);
                }
            }
        }

        TextFile.write(out, args[0] + "_pairs.txt");
        System.out.println("done");
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 2) {
            MatrixtoPairs rm = new MatrixtoPairs(args);
        } else {
            System.out.println("syntax: java DataMining.util.MatrixtoPairs\n" +
                    "<matrix file> <cutoff>\n"
            );
        }
    }
}
