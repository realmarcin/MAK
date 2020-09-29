package DataMining.eval;

import mathy.stat;
import util.MoreArray;
import util.TabFile;
import util.TextFile;

import java.io.File;
import java.util.ArrayList;

/**
 * User: marcin
 * Date: Aug 25, 2009
 * Time: 7:49:39 PM
 */
public class CriteriaEval {

    String[] evals = {
            "results_synth_rand_r",
            "results_synth_rand2_r",
            "results_synth_rand_c",
            "results_synth_rand2_c",
            "results_synth_rand_e",
            "results_synth_rand2_e",
            "results_synth_rand_e_c",
            "results_synth_rand2_e_c"
    };


    /**
     * @param args
     */
    public CriteriaEval(String[] args) {

        String dir = args[0];
        String outpath = args[1];

        File dirs = new File(dir);

        String[] list = dirs.list();
        for (int i = 0; i < list.length; i++) {
            if (MoreArray.getArrayInd(evals, list[i]) != -1) {
                AnalyzeSynthTest ast = null;
                if (list[i].indexOf("rand2") == -1)
                    ast = new AnalyzeSynthTest(dir + "/" + list[i], outpath, 1);
                else
                    ast = new AnalyzeSynthTest(dir + "/" + list[i], outpath, 2);
            }
        }
        pairAnalysis(outpath, "_analyze_mean.txt", "mean");
        //results_synth_rand_r_analyze_mean.txt
        pairAnalysis(outpath, "_analyze_sd.txt", "sd");

    }

    /**
     * @param outpath
     */
    private void pairAnalysis(String outpath, String label, String shortlabel) {
        File dirs = new File(outpath);
        String[] list = dirs.list();
        String[] xlabels = null, ylabels = null;
        //row vs column
        ArrayList R = new ArrayList();
        ArrayList C = new ArrayList();
        for (int i = 0; i < list.length; i++) {
            if (list[i].endsWith("_r" + label) || list[i].endsWith("_e" + label)) {
                String[][] data = TabFile.readtoArray(outpath + "/" + list[i]);
                if (xlabels == null) {
                    xlabels = data[0];
                    xlabels = MoreArray.remove(xlabels, 0);
                }
                if (ylabels == null) {
                    ylabels = MoreArray.extractColumnStr(data, 1);
                    ylabels = MoreArray.remove(ylabels, 0);
                }
                data = MoreArray.removeRow(data, 1);
                data = MoreArray.removeColumn(data, 1);
                R.add(MoreArray.convfromString(data));
            } else if (list[i].contains(label)) {
                String[][] data = TabFile.readtoArray(outpath + "/" + list[i]);
                data = MoreArray.removeRow(data, 1);
                data = MoreArray.removeColumn(data, 1);
                C.add(MoreArray.convfromString(data));
            }
        }
        System.out.println("R " + shortlabel + " " + R.size());
        System.out.println("C " + shortlabel + " " + C.size());
        double[][] Ravg = stat.array2DSampAvg(R);
        double[][] Cavg = stat.array2DSampAvg(C);
        TextFile.write(MoreArray.toString(Ravg, "", ""), xlabels, ylabels, outpath + shortlabel + "_R.txt");
        TextFile.write(MoreArray.toString(Cavg, "", ""), xlabels, ylabels, outpath + shortlabel + "_C.txt");

        //overlapping was embedded
        ArrayList O = new ArrayList();
        ArrayList E = new ArrayList();
        for (int i = 0; i < list.length; i++) {
            if (list[i].endsWith("_r" + label) || list[i].endsWith("_c" + label)) {
                String[][] data = TabFile.readtoArray(outpath + "/" + list[i]);
                data = MoreArray.removeRow(data, 1);
                data = MoreArray.removeColumn(data, 1);
                O.add(MoreArray.convfromString(data));
            } else if (list[i].contains(label)) {
                String[][] data = TabFile.readtoArray(outpath + "/" + list[i]);
                data = MoreArray.removeRow(data, 1);
                data = MoreArray.removeColumn(data, 1);
                E.add(MoreArray.convfromString(data));
            }
        }
        System.out.println("O " + shortlabel + " " + O.size());
        System.out.println("E " + shortlabel + " " + E.size());
        double[][] Oavg = stat.array2DSampAvg(O);
        double[][] Eavg = stat.array2DSampAvg(E);
        TextFile.write(MoreArray.toString(Oavg, "", ""), xlabels, ylabels, outpath + shortlabel + "_O.txt");
        TextFile.write(MoreArray.toString(Eavg, "", ""), xlabels, ylabels, outpath + shortlabel + "_E.txt");

        //true vs true2
        ArrayList one = new ArrayList();
        ArrayList two = new ArrayList();
        for (int i = 0; i < list.length; i++) {
            if (!list[i].contains("_rand2") && list[i].contains(label)) {
                String[][] data = TabFile.readtoArray(outpath + "/" + list[i]);
                data = MoreArray.removeRow(data, 1);
                data = MoreArray.removeColumn(data, 1);
                one.add(MoreArray.convfromString(data));
            } else if (list[i].contains(label)) {
                String[][] data = TabFile.readtoArray(outpath + "/" + list[i]);
                data = MoreArray.removeRow(data, 1);
                data = MoreArray.removeColumn(data, 1);
                two.add(MoreArray.convfromString(data));
            }
        }
        System.out.println("one " + shortlabel + " " + one.size());
        System.out.println("two " + shortlabel + " " + two.size());
        double[][] oneavg = stat.array2DSampAvg(one);
        double[][] twoavg = stat.array2DSampAvg(two);
        TextFile.write(MoreArray.toString(oneavg, "", ""), xlabels, ylabels, outpath + shortlabel + "_one.txt");
        TextFile.write(MoreArray.toString(twoavg, "", ""), xlabels, ylabels, outpath + shortlabel + "_two.txt");
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 2) {
            CriteriaEval arg = new CriteriaEval(args);
        } else {
            System.out.println("syntax: java DataMining.eval.CriteriaEval\n" +
                    "<dir of results>\n" +
                    "<outpath>"
            );
        }
    }


}
