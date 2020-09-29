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
public class MatrixtoTxt {


    boolean camelCase = true;

    /**
     * @param args
     */
    public MatrixtoTxt(String[] args) {

        SimpleMatrix sm = new SimpleMatrix(args[0]);
        ArrayList out = new ArrayList();
        StringBuffer s = new StringBuffer("and ");
        sm.xlabels = StringUtil.replace(sm.xlabels, "\"", "");
        sm.ylabels = StringUtil.replace(sm.ylabels, "\"", "");

        //sm.xlabels = StringUtil.camelCase(sm.xlabels);
        //sm.ylabels = StringUtil.camelCase(sm.ylabels);


        //sm.xlabels = StringUtil.stringify(sm.xlabels);
        //sm.ylabels = StringUtil.stringify(sm.ylabels);

        sm.xlabels = StringUtil.lowerCase(sm.xlabels);
        sm.ylabels = StringUtil.lowerCase(sm.ylabels);

        for (int i = 0; i < sm.ylabels.length; i++) {
            String ylab = sm.ylabels[i];
            for (int j = 0; j < sm.xlabels.length; j++) {
                if (sm.data[i][j] > 0) {
                    String xlab = sm.xlabels[j];
                    //out.add(ylab + " has a " + xlab + " and ");
                    s.append(ylab + " " + xlab + " stop.\n");
                }
            }
        }

        TextFile.write(s.toString(), args[0] + "_for2vec.txt");
        System.out.println("done");
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 1) {
            MatrixtoTxt rm = new MatrixtoTxt(args);
        } else {
            System.out.println("syntax: java DataMining.util.MatrixtoTxt\n" +
                    "<matrix file>\n"
            );
        }
    }
}
