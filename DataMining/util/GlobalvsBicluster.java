package DataMining.util;

import DataMining.ValueBlock;
import DataMining.ValueBlockList;
import DataMining.ValueBlockListMethods;
import mathy.SimpleMatrix;
import mathy.stat;
import util.TextFile;

import java.util.ArrayList;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: 3/18/13
 * Time: 11:19 PM
 */
public class GlobalvsBicluster {

    /**
     * @param args
     */
    public GlobalvsBicluster(String[] args) {

        try {
            ValueBlockList vbl = ValueBlockListMethods.readAny(args[0]);

            SimpleMatrix sm = new SimpleMatrix(args[1]);
            for (int i = 0; i < vbl.size(); i++) {
                ValueBlock v = (ValueBlock) vbl.get(i);
                v.setDataAndMean(sm.data);
                vbl.set(i, v);
            }


            ArrayList<String> out = new ArrayList();
            for (int i = 0; i < sm.data.length; i++) {
                for (int j = i + 1; j < sm.data.length; j++) {
                    double cor = stat.correlationCoeff(sm.data[i], sm.data[j]);

                    int count = ValueBlockListMethods.countCooccur(i + 1, j + 1, vbl);//BlockMethods.countCooccur()
                    double corPart = ValueBlockListMethods.meanPartialCorrelation(i + 1, j + 1, vbl);

                    double meancrit = ValueBlockListMethods.meanCritCooccur(i + 1, j + 1, vbl);
                    String str = "" + i + "\t" + j + "\t" + cor + "\t" + count + "\t" + meancrit + "\t" + corPart;
                    out.add(str);
                }
            }

            String outpath2 = "global_vs_bicluster.txt";
            System.out.println("writing " + outpath2);
            TextFile.write(out, outpath2);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * @param args
     */
    public final static void main(String[] args) {

        if (args.length == 2) {
            GlobalvsBicluster rm = new GlobalvsBicluster(args);
        } else {
            System.out.println("syntax: java DataMining.util.GlobalvsBicluster\n" +
                    "<value block list>\n" +
                    "<expr data file>"
            );
        }
    }

}
