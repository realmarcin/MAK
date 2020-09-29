package DataMining.eval;

import DataMining.ValueBlock;
import DataMining.ValueBlockList;
import DataMining.ValueBlockListMethods;
import mathy.SimpleMatrix;
import util.MoreArray;
import util.StringUtil;
import util.TextFile;

import java.util.ArrayList;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: Aug 25, 2012
 * Time: 10:50:29 PM
 */
public class COALESCEreformat {

    /**
     * @param args
     */
    public COALESCEreformat(String[] args) {
        ArrayList pass = TextFile.readtoList(args[0]);
        SimpleMatrix sm = new SimpleMatrix(args[1]);

        ValueBlockList out = new ValueBlockList();
        for (int i = 0; i < pass.size(); i += 4) {

            String s = (String) pass.get(i + 1);
            //s = StringUtil.replace(s, "\"", "");
            String[] genes = s.split("\t");

            String s1 = (String) pass.get(i + 2);
            //s1 = StringUtil.replace(s1, "\"", "");
            String[] exps = s1.split("\t");

            ArrayList g = new ArrayList();
            ArrayList e = new ArrayList();
            for (int j = 1; j < genes.length; j++) {
                int index = MoreArray.getArrayInd(sm.ylabels, genes[j]);
                if (index != -1)
                    g.add(index + 1);
            }

            for (int k = 1; k < exps.length; k++) {

                //to match R input file with simplified special characters
                exps[k] = StringUtil.replace(exps[k], " ", ".");
                exps[k] = StringUtil.replace(exps[k], "-", ".");
                exps[k] = StringUtil.replace(exps[k], ",", ".");
                exps[k] = StringUtil.replace(exps[k], "+", ".");
                exps[k] = StringUtil.replace(exps[k], "/", ".");
                exps[k] = StringUtil.replace(exps[k], "(", ".");
                exps[k] = StringUtil.replace(exps[k], ")", ".");
                exps[k] = StringUtil.replace(exps[k], "%", ".");
                exps[k] = StringUtil.replace(exps[k], ":", ".");
                exps[k] = StringUtil.replace(exps[k], "*", ".");
                exps[k] = StringUtil.replace(exps[k], "#", ".");
                exps[k] = StringUtil.replace(exps[k], ",", ".");
                exps[k] = StringUtil.replace(exps[k], "'", ".");
                exps[k] = "\"X" + exps[k].substring(1);

                //System.out.println("test " + exps[k]);
                //System.out.println("ref " + sm.xlabels[1]);

                int index = MoreArray.getArrayInd(sm.xlabels, exps[k]);
                if (index != -1)
                    e.add(index + 1);
                else if (exps[k].charAt(exps[k].length() - 2) == '.') {
                    exps[k] = exps[k].substring(0, exps[k].length() - 2) + "\"";
                    System.out.println(". exps " + exps[k]);
                    index = MoreArray.getArrayInd(sm.xlabels, exps[k]);
                    if (index != -1)
                        e.add(index + 1);
                    else {
                        System.out.println(" if(index == -1)");
                        System.out.println("test " + exps[k]);
                        //System.out.println("ref " + sm.xlabels[1]);
                    }
                } else {
                    System.out.println("last if(index == -1)");
                    System.out.println("last test " + exps[k]);
                    //System.out.println("ref " + sm.xlabels[1]);
                }
            }
            if (g.size() > 0 && e.size() > 0) {
                ValueBlock vb = new ValueBlock(g, e);
                out.add(vb);
            } else {
                System.out.println("Rejecting block g " + g.size() + "\te " + e.size());
            }
        }

        ValueBlockListMethods.writeBIC(args[0] + ".bic", out);
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 2) {
            COALESCEreformat arg = new COALESCEreformat(args);
        } else {
            System.out.println("syntax: java DataMining.eval.COALESCEreformat\n" +
                    "<COALESCE output>\n" +
                    "<data file>"

            );
        }
    }
}
