package DataMining.func;

import mathy.Matrix;
import mathy.SimpleMatrix;
import mathy.stat;
import util.MoreArray;
import util.StringUtil;
import util.TabFile;
import util.TextFile;

import java.io.File;
import java.util.ArrayList;

/**
 * DEPRECATED
 * <p/>
 * User: marcin
 * Date: Aug 28, 2010
 * Time: 6:06:33 PM
 */
public class DNABindProf {

    /**
     * @param args
     */
    public DNABindProf(String[] args) {

        SimpleMatrix sm = new SimpleMatrix(args[0]);
        sm.xlabels = MoreArray.replaceAll(sm.xlabels, "\"", "");
        sm.ylabels = MoreArray.replaceAll(sm.ylabels, "\"", "");

        double[] colmax = new double[sm.xlabels.length];
        for (int i = 0; i < colmax.length; i++) {
            double[] d = Matrix.getColumn(sm.data, i);
            colmax[i] = stat.findMax(d);
        }


        File dir = new File(args[1]);
        String[] list = dir.list();
        ArrayList normmedianranks = new ArrayList();
        ArrayList rowids = new ArrayList();
        for (int i = 0; i < list.length; i++) {
            String[] gids = TextFile.readtoArray(args[1] + "/" + list[i]);
            String currowid = "" + i + "__" + StringUtil.join("_", gids);
            rowids.add(currowid);
            ArrayList ranks = new ArrayList();
            int notfound = 0;
            for (int j = 0; j < gids.length; j++) {
                int rind = StringUtil.getFirstEqualsIndex(sm.ylabels, gids[j]);
                //System.out.println(sm.ylabels[0] + "\t" + gids[i]);
                if (rind != -1)
                    ranks.add(sm.data[rind]);
                else {
                    notfound++;
                    //   System.out.println("not found " + gids[i]);
                }
            }
            System.out.println(currowid);
            System.out.println("not found " + notfound + " out of " + gids.length);
            if (ranks.size() > 0) {
                double[] meanr = stat.arraySampMedian(ranks);
                meanr = stat.norm(meanr, colmax);
                //meanr = stat.invert(meanr);
                normmedianranks.add(meanr);
            }
        }

        double[][] out = MoreArray.ArrayListto2DDouble(normmedianranks);

        String[][] outs = MoreArray.toString(out, "", "");
        String outf = "DNABindProf_meangroupranks.txt";
        System.out.println("writing " + outf);
        TabFile.write(outs, outf, sm.xlabels, MoreArray.ArrayListtoString(rowids));
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 2) {
            DNABindProf arg = new DNABindProf(args);
        } else {
            System.out.println("syntax: java DataMining.func.DNABindProf\n" +
                    "<DNA binding rank file>\n" +
                    "<dir of id lists>"
            );
        }
    }

}
