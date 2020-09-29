package mathy;

import util.MoreArray;
import util.StringUtil;
import util.TabFile;

import java.util.ArrayList;

/**
 * User: marcinjoachimiak
 * Date: May 11, 2010
 * Time: 11:25:13 PM
 */
public class PairstoMatrix {


    double[][] secondData;
    String[] refids;
    String[] newdata_xlabels, newdata_ylabels;
    String outfile;
    double cutoff = Double.NaN;
    ArrayList xnot = new ArrayList(), ynot;

    /**
     * @param args
     */
    public PairstoMatrix(String[] args) {
        cutoff = Double.parseDouble(args[1]);

        outfile = args[2];

        if (args.length == 4) {
            refids = MoreArray.extractColumnStr(TabFile.readtoArray(args[3], false), 1);
            for (int i = 0; i < refids.length; i++) {
                if (refids[i] == null || refids[i].length() == 0) {
                    System.out.println("bad ref id " + i);
                }
            }
            //MoreArray.printArray(refids);
        }


        String[][] tmp = TabFile.readtoArray(args[0]);
        ArrayList ylabels = new ArrayList();
        ArrayList xlabels = new ArrayList();
        for (int i = 0; i < tmp.length; i++) {
            if (xlabels.indexOf(tmp[i][0]) == -1) {
                xlabels.add(tmp[i][0]);
            }
            if (ylabels.indexOf(tmp[i][1]) == -1) {
                ylabels.add(tmp[i][1]);
            }
        }

        if (refids != null) {
            newdata_xlabels = StringUtil.replace(refids, "\"", "");
            MoreArray.printArray(newdata_xlabels);
            newdata_ylabels = newdata_xlabels;
        } else {
            newdata_xlabels = MoreArray.ArrayListtoString(xlabels);
            //newdata_xlabels = StringUtil.replace(newdata_xlabels, "\"", "");
            MoreArray.printArray(newdata_xlabels);
            newdata_ylabels = MoreArray.ArrayListtoString(ylabels);
            //newdata_ylabels = StringUtil.replace(newdata_ylabels, "\"", "");
            MoreArray.printArray(newdata_ylabels);
        }

        double[][] outdata = new double[newdata_ylabels.length][newdata_xlabels.length];
        ArrayList all = new ArrayList();
        ArrayList total = new ArrayList();
        int countgood = 0;
        for (int i = 0; i < tmp.length; i++) {

            int xind = -1;
            int yind = -1;

            xind = MoreArray.getArrayInd(newdata_xlabels, tmp[i][0]);
            yind = MoreArray.getArrayInd(newdata_ylabels, tmp[i][1]);

            System.out.println(i + "\t" + xind + "\t" + yind);
            if (total.indexOf(tmp[i][0]) == -1)
                total.add(tmp[i][0]);
            if (total.indexOf(tmp[i][1]) == -1)
                total.add(tmp[i][1]);
            if (xind != -1 && yind != -1) {
                countgood++;
                if (all.indexOf(tmp[i][0]) == -1)
                    all.add(tmp[i][0]);
                if (all.indexOf(tmp[i][1]) == -1)
                    all.add(tmp[i][1]);
                //MoreArray.printArray(tmp[i]);
                if (tmp[i].length == 3) {
                    double d = 0;
                    try {
                        //d = Double.parseDouble(StringUtil.replace("\"\"", "", tmp[i][2]));
                        d = Double.parseDouble(tmp[i][2].trim());
                    } catch (NumberFormatException e) {
                        System.out.println("parseDouble error");
                        try {
                            d = (double) Integer.parseInt(tmp[i][2].trim());
                        } catch (NumberFormatException e1) {
                            System.out.println("parseInt error");
                            System.out.println(i);
                            System.out.println("|" + tmp[i][2] + "|");
                            System.out.println("bad parse");
                            System.out.println(MoreArray.toString(tmp[i]));
                            System.out.println(tmp[i][2]);
                            e1.printStackTrace();
                        }
                    }
                    if (!Double.isNaN(cutoff) && d > cutoff)
                        outdata[yind][xind] = d;
                } else {
                    outdata[yind][xind] = 1;
                }
            } else {
                if (xind == -1) {
                    System.out.println("xind undefined for " + tmp[i][0]);
                    if (xnot.indexOf(tmp[i][0]) == -1)
                        xnot.add(tmp[i][0]);
                }
                if (yind == -1) {
                    System.out.println("yind undefined for " + tmp[i][1]);

                    if (xnot.indexOf(tmp[i][1]) == -1)
                        xnot.add(tmp[i][1]);
                }
            }
        }

        System.out.println(xnot.size() + " undefined ids");
        //secondData = MoreArray.convfromString(tmp);

        TabFile.write(MoreArray.toString(outdata, "", ""), outfile, newdata_xlabels, newdata_ylabels);
        System.out.println("finished writing " + countgood + "\tout of " + tmp.length);
        System.out.println(countgood + " interactions for " + all.size() + " proteins. From a total " + total.size());
    }


    /**
     * @param args
     */
    public final static void main(String[] args) {
        if (args.length == 3 || args.length == 4) {
            PairstoMatrix cde = new PairstoMatrix(args);
        } else {
            System.out.println("syntax: mathy.PairstoMatrix\n" +
                    "<interaction pair tab text file>\n" +
                    "<cutoff>\n" +
                    "<outfile>\n" +
                    "<OPTIONAL list of ids to specify ordering>");
        }
    }
}
