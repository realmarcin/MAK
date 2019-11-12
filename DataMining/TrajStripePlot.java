package DataMining;

import mathy.stat;
import util.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * User: marcinjoachimiak
 * Date: Feb 25, 2010
 * Time: 10:59:38 AM
 */
public class TrajStripePlot extends Program {

    HashMap options;
    String[] valid_args = {"-dir"};

    double[][] griddata;

    int max_moves = 0;
    int abs_max_moves = 500;
    ArrayList transitions;
    HashMap starts;

    /**
     * @param args
     */
    public TrajStripePlot(String[] args) {
        options = MapArgOptions.maptoMap(args, valid_args);

        String dirpath = (String) options.get("-dir");
        int start = dirpath.indexOf("/");
        if (start < 2)
            start = dirpath.length();
        String dirext = dirpath.substring(0, start);
        dirext = StringUtil.replace(dirext, "/", "");
        dirext = StringUtil.replace(dirext, ".", "");
        //System.out.println("dirext " + dirext);

        File dir = new File(dirpath);
        if (dir.exists()) {
            String[] files = dir.list();
            getStartsAndMax(dirpath, files);
            griddata = new double[max_moves + 1][files.length];

            run(dirpath, files);

            int[] zeros = MoreArray.initArray(max_moves, 0);
            String[] zerosStr = MoreArray.toStringArray(zeros);
            String[][] outdata = MoreArray.toString(griddata, "", "");
            int[] intind = MoreArray.ArrayListtoInt(transitions);
            /*System.out.println("transitions ");
            MoreArray.printArray(intind);*/
            for (int i = 0; i < transitions.size(); i++) {
                int pos = (Integer) transitions.get(i);
                outdata = MoreArray.insertColumn(outdata, zerosStr, pos);
            }

            System.out.println("outdata " + outdata.length + "\t" + outdata[0].length);
            String[] xlabels = MoreArray.toStringArray(mathy.stat.createNaturalNumbers(1, files.length + 1 + transitions.size()));
            xlabels = StringUtil.prepend(xlabels, "t");
            String[] ylabels = MoreArray.toStringArray(mathy.stat.createNaturalNumbers(1, max_moves + 1));
            ylabels = StringUtil.prepend(ylabels, "p");

            String outm = dirext + "_stripeplot.txt";
            outdata = MoreArray.insertColumn(outdata, ylabels, 1);
            String collabels = MoreArray.toString(xlabels, "\t");
            collabels = "\t" + collabels;
            TabFile.write(outdata, outm, "\t", "\n" + collabels + "\n");
            System.out.println("wrote " + outm);
        } else {
            System.out.println("TrajStripePlot broken dirpath " + dirpath);
            System.exit(0);
        }
    }

    /**
     * @param dirpath
     * @param files
     */
    private void getStartsAndMax(String dirpath, String[] files) {
        starts = new HashMap();
        transitions = new ArrayList();
        String laststart = null;
        for (int i = 0; i < files.length; i++) {
            ArrayList ind = StringUtil.locateOccurtoList(files[i], "_");
            /*int[] intind = MoreArray.ArrayListtoInt(ind);
            System.out.println("intind ");
            MoreArray.printArray(intind);*/
            int i1 = (Integer) ind.get(1) + 1;
            int i2 = (Integer) ind.get(3);
            String start = files[i].substring(i1, i2);
            //System.out.println("start " + start);
            Object o = starts.get(start);
            if (o != null) {
                int count = (Integer) o;
                //starts.remove(o);
                starts.put(start, count + 1);
            } else {
                //System.out.println("start " + start+"\t"+files[i]);
                starts.put(start, 1);
            }
            if (laststart == null)
                laststart = start;
            else if (!start.equals(laststart)) {
                transitions.add(i);
                // System.out.println("transition " + i + "\t" + start + "\t" + laststart);
                laststart = start;
            }
            String path = dirpath + sysRes.file_separator + files[i];
            ValueBlockList vbl = null;
            try {
                vbl = ValueBlockList.read(path, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (vbl.size() > max_moves)
                max_moves = vbl.size();
        }
        System.out.println("max_moves " + max_moves);
        if (max_moves > abs_max_moves)
            max_moves = abs_max_moves;

        Iterator iter = starts.keySet().iterator();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            System.out.println("start " + key + "\t" + starts.get(key));
        }
    }

    /**
     * @param dirpath
     * @param files
     */
    private void run(String dirpath, String[] files) {
        for (int i = 0; i < files.length; i++) {
            if (files[i].endsWith("_toplist.txt")) {
                String path = dirpath + sysRes.file_separator + files[i];
                ValueBlockList vbl = null;
                try {
                    vbl = ValueBlockList.read(path, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                int min = -1;
                if (vbl.size() > max_moves + 1)
                    min = vbl.size() - max_moves;
                for (int j = vbl.size() - 1; j > min; j--) {
                    ValueBlock cur = (ValueBlock) vbl.get(j);
                    int j1 = vbl.size() - 1 - j;
                    //System.out.println("TrajStripePlot " + max_moves + "\t" + j + "\t" + j1 + "\t" + min);
                    griddata[j1][i] = stat.roundUp(cur.full_criterion, 2);
                }
            }
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 2) {
            TrajStripePlot arg = new TrajStripePlot(args);
        } else {
            System.out.println("syntax: java DataMining.TrajStripePlot\n" +
                    "<-dir dir of trajectories>"
            );
        }
    }

}
