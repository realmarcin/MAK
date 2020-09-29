package DataMining.eval;

import DataMining.BlockMethods;
import DataMining.ValueBlock;
import DataMining.ValueBlockList;
import DataMining.ValueBlockListMethods;
import mathy.stat;
import util.ParsePath;
import util.TextFile;

import java.io.File;
import java.util.ArrayList;

/**
 * Compare two sets of biclusters with gene, exp or total overlap measures.
 * <p/>
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: 4/15/13
 * Time: 4:23 PM
 */
public class CompareSets {

    double overlap_threshold = 0.2;

    /**
     * @param args
     */
    public CompareSets(String[] args) {
        ValueBlockList makvbl = null;
        try {
            makvbl = ValueBlockListMethods.readAny(args[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String[] list = (new File(args[1])).list();

        ArrayList allextset = new ArrayList();

        int count = 0;
        for (int i = 0; i < list.length; i++) {
            if (list[i].endsWith(".txt"))
                count++;
        }
        String[] ext_methods = new String[count];
        int count2 = 0;
        for (int i = 0; i < list.length; i++) {
            if (list[i].endsWith(".txt"))
                try {
                    ValueBlockList cur = ValueBlockListMethods.readAny(args[1] + "/" + list[i]);
                    allextset.add(cur);
                    ext_methods[count2] = list[i];
                    System.out.println("ext methods " + list[i]);
                    System.out.println("ext methods " + allextset.size());
                    count2++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }

        System.out.println("ext methods total " + list.length + "\t" + allextset.size());

        String head = "number\tmean_count\tmean_overlap";
        for (int j = 0; j < ext_methods.length; j++) {
            head += "\t" + ext_methods[j] + "_count\t" + ext_methods[j] + "_over\t" + ext_methods[j] + "_max";
        }

        ArrayList curextcountAr = new ArrayList();
        ArrayList curextoverAr = new ArrayList();
        ArrayList curextmaxAr = new ArrayList();
        for (int j = 0; j < ext_methods.length; j++) {
            //System.out.println("doing " + ext_methods[j]);
            double[] curextcount = new double[makvbl.size()];
            double[] curextsum = new double[makvbl.size()];
            double[] curextsmax = new double[makvbl.size()];
            ValueBlockList curextvbl = (ValueBlockList) allextset.get(j);
            for (int i = 0; i < makvbl.size(); i++) {
                System.out.println("doing " + i);
                ValueBlock curmak = (ValueBlock) makvbl.get(i);
                for (int k = 0; k < curextvbl.size(); k++) {
                    ValueBlock curext = (ValueBlock) curextvbl.get(k);
                    double overlap = Double.NaN;
                    if (args[2].equals("total"))
                        overlap = BlockMethods.JaccardIndexGenesExps(curmak, curext);
                    if (args[2].equals("gene"))
                        overlap = BlockMethods.JaccardIndexGenes(curmak, curext);
                    if (args[2].equals("exp"))
                        overlap = BlockMethods.JaccardIndexExps(curmak, curext);
                    if (overlap > overlap_threshold) {
                        curextcount[i]++;
                    }
                    curextsum[i] += overlap;
                    if (curextsmax[i] < overlap)
                        curextsmax[i] = overlap;
                }

                curextsum[i] = curextsum[i] / (double) curextvbl.size();
                //curextsum[i] = curextsum[i] / (double) curextvbl.size();
            }
            curextcountAr.add(curextcount);
            curextoverAr.add(curextsum);
            curextmaxAr.add(curextsmax);
        }

        ArrayList out = new ArrayList();
        double[] specCount = stat.arraySampAvg(curextcountAr);
        double[] meansSum = stat.arraySampAvg(curextoverAr);
        for (int j = 0; j < specCount.length; j++) {
            String s = (j + 1) + "\t" + specCount[j] + "\t" + meansSum[j];
            if (specCount[j] == 0) {
                System.out.println(j + " is unique at < " + this.overlap_threshold);
            }
            for (int k = 0; k < ext_methods.length; k++) {
                double[] cur = (double[]) curextcountAr.get(k);
                double[] curover = (double[]) curextoverAr.get(k);
                double[] curmax = (double[]) curextmaxAr.get(k);
                s += "\t" + cur[j] + "\t" + curover[j] + "\t" + curmax[j];
                //System.out.println(ext_methods[k] + "\t" + cur[j]);
            }
            out.add(s);
        }

        out.add(0, head);
        ParsePath pp = new ParsePath(args[0]);

        String outpath = pp.getFile() + "_vs_" + args[2] + "_" + overlap_threshold + "_overlaps.txt";
        System.out.println("wrote " + outpath);
        TextFile.write(out, outpath);
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 3) {
            CompareSets rm = new CompareSets(args);
        } else {
            System.out.println("syntax: java DataMining.eval.CompareSets\n" +
                    "< MAK vbl>\n" +
                    "< dir with .vbl files>\n" +
                    "< gene, exp, total>"
            );
        }
    }

}
