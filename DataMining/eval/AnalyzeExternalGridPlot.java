package DataMining.eval;

import DataMining.MarkGrid;
import DataMining.ValueBlock;
import DataMining.ValueBlockList;
import mathy.Matrix;
import mathy.SimpleMatrix;
import util.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * User: marcin
 * Date: Sep 27, 2009
 * Time: 1:19:06 PM
 */
public class AnalyzeExternalGridPlot extends Program {

    HashMap options;
    String[] valid_args = {"-dir", "-data", "-mode"};

    ArrayList results;

    String[] files;

    String globalType;

    ArrayList means, SDs;

    int block = 1;

    String[] refblocklabels = {"true1", "true2", "true3", "true4"};

    ArrayList refblocks;
    ValueBlock refblock;
    SimpleMatrix sm;
    String block_label;

    ArrayList sets_of_searches;
    ArrayList sets_of_searches_threshold;
    ArrayList sets_of_starts;

    double threshold = 0.5;

    /**
     * @param args
     */
    public AnalyzeExternalGridPlot(String[] args) {
        options = MapArgOptions.maptoMap(args, valid_args);

        String dirpath = (String) options.get("-dir");
        ParsePath pp = new ParsePath(dirpath);
        String dirext = pp.getLastDirinPath();
        System.out.println("dirext " + dirext);
        String datapath = (String) options.get("-data");
        String mode = (String) options.get("-mode");

        sets_of_searches_threshold = new ArrayList();
        sets_of_searches = new ArrayList();
        sets_of_starts = new ArrayList();
        sm = new SimpleMatrix(datapath);
        refblocks = new ArrayList();
        if (mode.equals("basic")) {
            refblocks.add(EvaluateBasic.true1);
            refblocks.add(EvaluateBasic.true2);
        } else if (mode.equals("const")) {
            refblocks.add(EvaluateConst.true1);
            refblocks.add(EvaluateConst.true2);
            refblocks.add(EvaluateConst.true3);
            refblocks.add(EvaluateConst.true4);
        } else if (mode.equals("incr")) {
            refblocks.add(EvaluateIncr.true1);
            refblocks.add(EvaluateIncr.true2);
            refblocks.add(EvaluateIncr.true3);
            refblocks.add(EvaluateIncr.true4);
        } else if (mode.equals("constnono")) {
            refblocks.add(EvaluateConstNono.true1);
            refblocks.add(EvaluateConstNono.true2);
            refblocks.add(EvaluateConstNono.true3);
            refblocks.add(EvaluateConstNono.true4);
        } else if (mode.equals("incrnono")) {
            refblocks.add(EvaluateIncrNono.true1);
            refblocks.add(EvaluateIncrNono.true2);
            refblocks.add(EvaluateIncrNono.true3);
            refblocks.add(EvaluateIncrNono.true4);
        }
        File dir1 = new File(dirpath);
        if (dir1.exists()) {
            String[] files1 = dir1.list();
            for (int a = 0; a < files1.length; a++) {
                File dir = new File(dirpath + "/" + files1[a]);
                ParsePath ppp = new ParsePath(files1[a]);
                if (dir.exists()) {
                    String[] files = dir.list();
                    if (files != null) {
                        run(dirpath + "/" + files1[a], files, ppp.getName());
                    } else {
                        System.out.println("AnalyzeExternalGridPlot empty dirpath " + dirpath);
                        System.exit(0);
                    }
                }
            }

            String[][] cur = (String[][]) sets_of_searches_threshold.get(0);
            String[][] join_data = new String[3 * cur.length][sets_of_searches.size() * cur[0].length];
            int coloffset = 0;
            for (int i = 0; i < sets_of_searches_threshold.size(); i++) {
                cur = (String[][]) sets_of_searches_threshold.get(i);
                for (int j = 0; j < cur.length; j++) {
                    for (int k = 0; k < cur[0].length; k++) {
                        join_data[j][k + coloffset] = cur[j][k];
                    }
                }
                coloffset += cur[0].length;
                System.out.println("AnalyzeExternalGridPlot coloffset" + coloffset);
            }
            coloffset = 0;
            for (int i = 0; i < sets_of_searches.size(); i++) {
                cur = (String[][]) sets_of_searches.get(i);
                for (int j = 0; j < cur.length; j++) {
                    for (int k = 0; k < cur[0].length; k++) {
                        join_data[j + cur.length][k + coloffset] = cur[j][k];
                    }
                }
                coloffset += cur[0].length;
                System.out.println("AnalyzeExternalGridPlot coloffset" + coloffset);
            }
            coloffset = 0;
            for (int i = 0; i < sets_of_starts.size(); i++) {
                cur = (String[][]) sets_of_starts.get(i);
                for (int j = 0; j < cur.length; j++) {
                    for (int k = 0; k < cur[0].length; k++) {
                        join_data[j + 2 * cur.length][k + coloffset] = cur[j][k];
                    }
                }
                coloffset += cur[0].length;
                System.out.println("AnalyzeExternalGridPlot coloffset" + coloffset);
            }

            coloffset = cur[0].length;
            String[] insertcol = MoreArray.initArray(3 * cur.length, "-");
            //insert column dividers
            for (int i = 0; i < sets_of_searches.size() - 1; i++) {
                join_data = MoreArray.insertColumn(join_data, insertcol, coloffset);
                coloffset += cur[0].length + 1;
            }
            String[] insertrow = MoreArray.initArray(join_data[0].length, "-");
            //insert row dividers
            join_data = MoreArray.insertRow(join_data, insertrow, cur.length);
            join_data = MoreArray.insertRow(join_data, insertrow, 2 * cur.length + 1);
            //String[][] outdata = MoreArray.toString(join_data,"","");
            //int[] colsums = Matrix.sumOverRows(join_data);
            //int[] rowsums = Matrix.sumOverCols(join_data);
            String[] xlabels = MoreArray.initArray(join_data[0].length, ".");// MoreArray.toStringArray(colsums);
            //xlabels = StringUtil.prepend(xlabels, "= ");
            String[] ylabels = MoreArray.initArray(join_data.length, ".");//MoreArray.toStringArray(rowsums);
            //ylabels = StringUtil.prepend(ylabels, "= ");
            String outm = dirext + "_" + threshold + "_gridplot_all.txt";//dirpath + sysRes.file_separator +
            join_data = MoreArray.insertColumn(join_data, ylabels, 1);
            String collabels = MoreArray.toString(xlabels, "\t");
            collabels = "\t" + collabels;
            TabFile.write(join_data, outm, "\t", "\n" + collabels + "\n");
            System.out.println("finished");
        } else {
            System.out.println("AnalyzeExternalGridPlot broken dirpath " + dirpath);
            System.exit(0);
        }


    }

    /**
     * @param dirpath
     * @param files
     */
    private void run(String dirpath, String[] files, String dirext) {
        results = new ArrayList();

        int[][] griddata = new int[sm.data.length][sm.data[0].length];
        //griddata = sumBlockonGrid(griddata, refblock);

        String[][] outdata = MoreArray.initArray(sm.data.length, sm.data[0].length, "" + 0);
        String[][] thresholdoutdata = MoreArray.initArray(sm.data.length, sm.data[0].length, "" + 0);
        String[][] startandtruedata = MoreArray.initArray(sm.data.length, sm.data[0].length, "" + 0);
        /*if (refblock != null)
            outdata = markBlockonGrid(outdata, refblock, block_label);
        */

        for (int i = 0; i < refblocks.size(); i++) {
            refblock = (ValueBlock) refblocks.get(i);
            System.out.println("run true " + i);
            MoreArray.toString(refblock.genes, ",");
            outdata = MarkGrid.markBlockonGrid(outdata, refblock, "true");//refblocklabels[i]);
            thresholdoutdata = MarkGrid.markBlockonGrid(thresholdoutdata, refblock, "true");//refblocklabels[i]);
            startandtruedata = MarkGrid.markBlockonGrid(startandtruedata, refblock, "true");//refblocklabels[i]);
        }

        for (int i = 0; i < files.length; i++) {
            if (files[i].endsWith("_toplist.txt")) {
                //System.out.println(files[i]);
                int type = getType(files[i]);
                String path = dirpath + sysRes.file_separator + files[i];
                if (type == -1)
                    System.out.println("run bad path/file " + path);
                String typelabel = Evaluate.external_methods[type];
                globalType = typelabel;
                //System.out.println("run " + typelabel);
                //int index = files[i].indexOf("__") + 2;
                //int end1 = files[i].indexOf("__", index);
                //System.out.println("run files[i] " + files[i]);
                //String str1 = files[i].substring(index, end1);
                //System.out.println("run str1 " + str1);
                //int label_index = MoreArray.getArrayInd(MINER_STATIC.CRIT_LABELS, str1);
                //System.out.println("use_crit " + MINER_STATIC.CRIT_LABELS[label_index]);
                //System.out.println("ValueBlockList " + path);
                ValueBlockList vbl = null;
                try {
                    vbl = ValueBlockList.read(path, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //if (refblock == null) {
                refblock = (ValueBlock) vbl.get(0);
                outdata = MarkGrid.markBlockonGrid(outdata, refblock, "start");
                thresholdoutdata = MarkGrid.markBlockonGrid(thresholdoutdata, refblock, "start");
                startandtruedata = MarkGrid.markBlockonGrid(startandtruedata, refblock, "start");
                //}
                int position = vbl.size() - 1;

                ValueBlock curblock = (ValueBlock) vbl.get(position);
                //System.out.println("ValueBlock " + curblock.toStringShort());

                griddata = MarkGrid.sumBlockonGrid(griddata, curblock);
            }
        }

        int max = Matrix.findMax(griddata);
        int count_threshold = (int) (max * threshold);
        System.out.println("count_threshold " + count_threshold);
        int[][] threshold_griddata = Matrix.filterBelowtoZero(griddata, count_threshold, 0);
        outdata = MarkGrid.markBlockonGridInt(outdata, griddata, "true");
        thresholdoutdata = MarkGrid.markBlockonGridInt(thresholdoutdata, threshold_griddata, "true");
        //outdata = markBlockonGridStr(outdata, griddata);

        int[] colsums = Matrix.sumOverRows(griddata);
        int[] rowsums = Matrix.sumOverCols(griddata);
        sets_of_searches.add(outdata);
        sets_of_searches_threshold.add(thresholdoutdata);
        sets_of_starts.add(startandtruedata);
        String[] xlabels = MoreArray.toStringArray(colsums);
        xlabels = StringUtil.prepend(xlabels, "= ");
        String[] ylabels = MoreArray.toStringArray(rowsums);
        ylabels = StringUtil.prepend(ylabels, "= ");
        String outm = dirext + "_" + threshold + "_gridplot.txt";//dirpath + sysRes.file_separator +
        outdata = MoreArray.insertColumn(outdata, ylabels, 1);
        String collabels = MoreArray.toString(xlabels, "\t");
        collabels = "\t" + collabels;
        TabFile.write(outdata, outm, "\t", "\n" + collabels + "\n");
    }

    /**
     * @param s
     * @return
     */
    public final static int getType(String s) {
        for (int i = 0; i < Evaluate.external_methods.length; i++) {
            if (s.indexOf(Evaluate.external_methods[i]) != -1)
                return i;
        }
        return -1;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 6) {
            AnalyzeExternalGridPlot arg = new AnalyzeExternalGridPlot(args);
        } else {
            System.out.println("syntax: java DataMining.eval.AnalyzeExternalGridPlot\n" +
                    "<-dir dir of toplist summaries>\n" +
                    "<-data expr data file>\n" +
                    "<-mode basic, const, incrdir>"
            );
        }
    }
}
