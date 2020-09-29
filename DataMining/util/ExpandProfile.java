package DataMining.util;

import mathy.stat;
import util.*;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: Jul 16, 2011
 * Time: 12:38:55 AM
 */
public class ExpandProfile {

    /**
     * @param args
     */
    public ExpandProfile(String[] args) {
        File test = new File(args[0]);

        if (test.isDirectory()) {

            String[] list = test.list();
            for (int i = 0; i < list.length; i++) {
                String s = args[0] + "/" + list[i];
                ParsePath pp = new ParsePath(s);
                if (list[i].indexOf("profile") != -1 && list[i].indexOf("expand") == -1) {
                    doOne(s, args[1] + "/" + pp.getName() + "_expand.txt");
                } else {
                    System.out.println("ignoring file " + s);
                }
            }

        } else
            doOne(args[0], args[1]);
    }

    /**
     * @param infile
     * @param outfile
     */
    private void doOne(String infile, String outfile) {
        ArrayList data = TextFile.readtoList(infile, "\t");

        ArrayList labs = new ArrayList();
        for (int i = 0; i < data.size();) {
            //String id = ((String[]) data.get(i))[0];
            String[] lab = (String[]) data.get(i + 1);
            //String[] goval = (String[]) data.get(i + 2);

            for (int j = 0; j < lab.length; j++) {
                if (MoreArray.getArrayInd(labs, lab[j]) == -1)
                    labs.add(lab[j]);
            }
            i += 3;
        }

        int size = (int) ((double) data.size() / 3.0);
        double[][] out = new double[size][labs.size()];
        //System.out.println("labels " + labs.size() + "\t" + size);
        for (int i = 0; i < data.size();) {
            String[] lab = (String[]) data.get(i + 1);
            String[] val = (String[]) data.get(i + 2);
            for (int j = 0; j < lab.length; j++) {
                int index = MoreArray.getArrayInd(labs, lab[j]);
                //System.out.println(i + "\t" + j + "\t" + (i / 3) + "\t" + index);
                //System.out.println(out.length + "\t" + out[0].length + "\t" + val.length);
                //try {
                System.out.println("ExpandProfile index " + index + "\t" + out[0].length +
                        "\tlab " + lab.length + "\tval " + val.length + "\t" + j);
                out[(int) ((double) i / 3.0)][index] = Double.valueOf(val[j]);
                /*} catch (Exception e) {
                    e.printStackTrace();
                }*/
            }
            i += 3;
        }

        String[] ylabs = StringUtil.prependAppend(MoreArray.toStringArray(
                stat.createNaturalNumbers(1, 1 + ((int) ((double) data.size() / 3.0)))), "\"", "\"");
        //System.out.println("len " + out.length + "\t" + out[0].length + "\t" + labs.size() + "\t" + ylabs.length);
        String[] xlabels = MoreArray.ArrayListtoString(labs);
        xlabels = StringUtil.prependAppend(xlabels, "\"", "\"");
        TabFile.write(MoreArray.toString(out, "", ""), outfile,
                xlabels, ylabs);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 2) {
            ExpandProfile ce = new ExpandProfile(args);
        } else {
            System.out.println("usage: java DataMining.util.ExpandProfile\n" +
                    "<compressed profile or dir of profiles>\n" +
                    "<outfile or outdir if first arg is dir>");
        }
    }

}
