package DataMining.util;

import mathy.Matrix;
import mathy.SimpleMatrix;
import util.MoreArray;
import util.StringUtil;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: 9/24/16
 * Time: 8:57 PM
 */
public class AnalyzeOverlaps {

    double cutoff = 0.5;

    /**
     * @param args
     */
    public AnalyzeOverlaps(String[] args) {

        File dir = new File(args[0]);
        String[] files = dir.list();


        if (args.length == 3) {
            cutoff = Double.valueOf(args[2]);
            System.out.println("cutoff" + cutoff);
        }

        HashMap methods = new HashMap();
        for (int a = 0; a < files.length; a++) {

            final int vs_index = files[a].indexOf("_vs_");
            String x = files[a].substring(0, vs_index);
            String y = files[a].substring(vs_index + "_vs_".length(), files[a].length() - ".overlap".length());

            System.out.println(a);
            System.out.println(x);
            System.out.println(y);

            if (methods.get(x) == null)
                methods.put(x, 1);
            else {
                int cur = (Integer) methods.get(x);
                methods.put(x, cur + 1);
            }

            if (methods.get(y) == null)
                methods.put(y, 1);
            else {
                int cur = (Integer) methods.get(y);
                methods.put(y, cur + 1);
            }
        }

        //check methods map to collapse truncated or name and remove
        //also need to expand file name to full potential
        HashMap replace = new HashMap();
        Set keysfirst = methods.keySet();
        String[] keysStrfirst = Arrays.copyOf(keysfirst.toArray(), keysfirst.size(), String[].class);
        for (int x = 0; x < keysStrfirst.length; x++) {
            int lastdotx = keysStrfirst[x].lastIndexOf(".");
            for (int y = 0; y < keysStrfirst.length; y++) {
                if (x != y) {
                    int lastdoty = keysStrfirst[y].lastIndexOf(".");
                    System.out.println(keysStrfirst[x]);
                    System.out.println(keysStrfirst[y]);
                    if (keysStrfirst[x].substring(0, lastdotx - 1).indexOf(keysStrfirst[y].substring(0, lastdoty - 1)) != -1) {
                        if (keysStrfirst[x].length() < keysStrfirst[y].length()) {
                            methods.remove(keysStrfirst[x]);
                            System.out.println("removing " + keysStrfirst[x]);
                            replace.put(keysStrfirst[x], keysStrfirst[y]);
                        }
                        if (keysStrfirst[y].length() < keysStrfirst[x].length()) {
                            methods.remove(keysStrfirst[y]);
                            System.out.println("removing " + keysStrfirst[y]);
                            replace.put(keysStrfirst[y], keysStrfirst[x]);
                        }
                    }
                }
            }
        }


        Set keys = methods.keySet();
        String[] keysStr = Arrays.copyOf(keys.toArray(), keys.size(), String[].class);
        //String[] keysStr = (String[]) keys.toArray();

        System.out.println("Methods");
        MoreArray.printArray(keysStr);
        double[][] overlap_fraction = new double[keys.size()][keys.size()];
        for (int a = 0; a < files.length; a++) {
            SimpleMatrix sm = new SimpleMatrix(args[0] + "/" + files[a]);
            //int xdim = sm.xlabels.length;
            //int ydim = sm.ylabels.length;

            //double[] colmax = mathy.Matrix.columnMax(sm.data);
            double[] rowmax = mathy.Matrix.rowMax(sm.data);


            final int vs_index = files[a].indexOf("_vs_");
            String x = files[a].substring(0, vs_index);
            String y = files[a].substring(vs_index + "_vs_".length(), files[a].length() - ".overlap".length());

            System.out.println("x y "+ x+"\t"+y);

            String testkey = (String) replace.get(x);
            if (testkey != null) {
                System.out.println("replace " + x + "\t" + testkey);
                x = testkey;
            }
            String testkeyY = (String) replace.get(y);
            if (testkeyY != null) {
                System.out.println("replace " + x + "\t" + testkey);
                y = testkeyY;
            }

            int xind = StringUtil.getFirstEqualsIgnoreCaseIndex(keysStr, x);
            int yind = StringUtil.getFirstEqualsIgnoreCaseIndex(keysStr, y);

            MoreArray.printArray(rowmax);
            //overlap_fraction[yind][xind] = mathy.stat.countGreaterThan(colmax, cutoff) / (double) colmax.length;
            overlap_fraction[yind][xind] = mathy.stat.countGreaterThan(rowmax, cutoff) / (double) rowmax.length;
            System.out.println("fraction overlap " + files[a] + "\toverlap fraction " + overlap_fraction[yind][xind]
                    + "\t rowmax.length" + rowmax.length + "\tsm.data " + sm.data.length + "\t" + sm.data[0].length);
        }

        for (int z = 0; z < keysStr.length; z++) {
            overlap_fraction[z][z] = 1.0;
        }

        String outf = args[1];
        Matrix.writeTab(overlap_fraction, keysStr, keysStr, outf);
    }


    /**
     * @param args
     */
    public final static void main(String[] args) {
        if (args.length == 2 || args.length == 3) {
            AnalyzeOverlaps cde = new AnalyzeOverlaps(args);
        } else {
            System.out.println("syntax: DataMining.util.AnalyzeOverlaps\n" +
                    "<dir of overlaps>\n" +
                    "<output file>\n" +
                    "<OPTIONAL cutoff default = 0.5>");
        }
    }
}
