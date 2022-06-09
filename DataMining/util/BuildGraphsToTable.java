package DataMining.util;

import DataMining.ValueBlock;
import DataMining.ValueBlockList;
import mathy.Matrix;
import mathy.SimpleMatrix;
import mathy.stat;
import util.MoreArray;
import util.StringUtil;
import util.TabFile;
import util.TextFile;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: 12/28/15
 * Time: 12:18 PM
 */
public class BuildGraphsToTable {

    boolean debug = false;

    public String[] header = {
            "Unmerged",
            "Number",
            "Coverage",
            "Coverage fraction",
            "Coverage >1",
            "Coverage fraction >1",
            "Coverage >2",
            "Coverage fraction >2",
            "Enrichment >1",
            "Enrichment >2",
            "GO biclusters",//10
            "TF biclusters",
            "Pathway biclusters",
            "TIGRrole biclusters",
            "GO unique",
            "TF unique",
            "Pathway unique",
            "TIGRrole unique",
            "GO coverage",
            "TF coverage",
            "Pathway coverage",//20
            "TIGRrole coverage",
            "GO p.b.",
            "TF p.b.",
            "Path p.b.",
            "TIGRrole p.b.",
            "Runtime",
            "perbic sum",
            "coverage sum",
            "GOcover/totalcover",
            "TFcover/totalcover",//30
            "Pathcover/totalcover",
            "TIGRrolecover/totalcover",
            "Max Genes",
            "Max exps",
            "Full crit",
            "Coherence",
            "Gene coverage",
            "Condition coverage",
            "RMSD from bg",
            "Mean genes",
            "Mean exps"
    };

    SimpleMatrix input_data;

    double bgmean = Double.NaN;


    /**
     * @param args
     */
    public BuildGraphsToTable(String[] args) {

        String indir = args[0];
        //String summarydir = args[1];
        String topdir = args[1];

        input_data = new SimpleMatrix(args[2]);
        bgmean = Matrix.avg(input_data.data);

        String outfile = args[3];
        File dirout = new File(indir);
        String[] listout = dirout.list();
        System.out.println("listout " + listout.length);

   /*     File dirsummary = new File(summarydir);
        String[] listsummary = dirsummary.list();
        System.out.println("listsummary " + listsummary.length);*/

        File dirtop = new File(topdir);
        String[] listtop = dirtop.list();
        System.out.println("listtop " + listtop.length);


        ArrayList out = new ArrayList();

        int len = 0;
        for (int i = 0; i < listout.length; i++) {
            if (!listout[i].equals(".DS_Store")) {
                len++;
            }
        }
        String[][] out_data = new String[len][35];
        String[] row_labels = new String[len];
        int count = 0;

        /*boolean bad = false;
        for (int i = 0; i < listout.length; i++) {

            if (!listout[i].equals(".DS_Store")) {

                //System.out.println(i + "\t" + listout[i]);

                BlockData blockData = new BlockData(args[2], listout[i], listsummary, listtop).invoke();
                ValueBlockList vbl = blockData.getVbl();
                String[][] top_data = blockData.getTop_data();
                *//*if (vbl != null && vbl.size() > 0 && top_data != null) {
                    String outstr = "Problem: " + listout[i];
                    *//**//*if (vbl == null)
                        outstr += "vbl is null,";
                    else if (vbl.size() == 0)
                        outstr += "vbl is empty,";
                    if (top_data == null)
                        outstr += "top_data is null";*//**//*

                    System.out.println(outstr);
                    bad = true;
                }*//*
            }
        }
        if (bad)
            System.exit(1);*/


        for (int i = 0; i < listout.length; i++) {

            if (!listout[i].equals(".DS_Store")) {

                System.out.println("doing "+i + "\t" + listout[i]);

                BlockData blockData = null;
                try {
                    blockData = new BlockData(args[1], listout[i], listtop).invoke();
                } catch (Exception e) {
                    System.out.print("args[1] " + args[1]);
                    System.out.print("listout[i] " + listout[i]);
                    System.out.print("listtop[1] " + listtop[1]);

                    e.printStackTrace();
                    System.exit(1);
                }
                ValueBlockList vbl = blockData.getVbl();
                String[][] top_data = blockData.getTop_data();


                row_labels[count] = listout[i];
                double[] stats = new double[42];
                String[] data = TextFile.readtoArray(indir + "/" + listout[i]);
                double fold1 = 0;
                double fold2 = 0;
                double total = 0;

                for (int j = 0; j < data.length; j++) {

                    stats[0] = 0;

                    if (data[j].startsWith("Data set > 1 fold changers ")) {
                        fold1 = Double.parseDouble(data[j].substring(data[j].lastIndexOf("changers ") + "changers ".length(), data[j].indexOf("\tout of")));
                        total = Double.parseDouble(data[j].substring(data[j].lastIndexOf("out of ") + "out of ".length(), data[j].indexOf(" =")));
                    }
                    if (data[j].startsWith("Data set > 2 fold changers ")) {
                        fold2 = Double.parseDouble(data[j].substring(data[j].lastIndexOf("changers ") + "changers ".length(), data[j].indexOf("\tout of")));
                    }


                    if (data[j].startsWith("Bicluster set BIC.size()") || data[j].startsWith("Bicluster set bicluster_set_size")) {
                        stats[1] = Double.parseDouble(data[j].substring(data[j].lastIndexOf(" "), data[j].length()));
                    }

                    if (data[j].startsWith("Bicluster set covers")) {
                        stats[2] = Double.parseDouble(data[j].substring(data[j].lastIndexOf("covers ") + "covers ".length(), data[j].indexOf("\tout of")));
                        stats[3] = Double.parseDouble(data[j].substring(data[j].lastIndexOf("= ") + "= ".length(), data[j].length()));
                    }

                    if (data[j].startsWith("Bicluster set > 1 fold changers")) {
                        stats[4] = Double.parseDouble(data[j].substring(data[j].lastIndexOf("1 fold changers ") + "1 fold changers ".length(), data[j].indexOf("\tout of")));
                        stats[5] = Double.parseDouble(data[j].substring(data[j].lastIndexOf("= ") + "= ".length(), data[j].length()));
                    }

                    if (data[j].startsWith("Bicluster set > 2 fold changers")) {
                        stats[6] = Double.parseDouble(data[j].substring(data[j].lastIndexOf("2 fold changers ") + "2 fold changers ".length(), data[j].indexOf("\tout of")));
                        stats[7] = Double.parseDouble(data[j].substring(data[j].lastIndexOf("= ") + "= ".length(), data[j].length()));
                    }


                    if (data[j].startsWith("GO biclusters ")) {

                        stats[10] = Double.parseDouble(data[j].substring(data[j].lastIndexOf("GO biclusters\t") + "GO biclusters\t".length(), data[j].indexOf("\tunique")));
                        stats[14] = Double.parseDouble(data[j].substring(data[j].lastIndexOf("unique GOs ") + "unique GOs ".length(), data[j].indexOf("\tcover")));
                        stats[18] = Double.parseDouble(data[j].substring(data[j].lastIndexOf("cover ") + "cover ".length(), data[j].length()));

                        /*if(debug) {
                            System.out.println("GO biclusters "+stats[10]);
                            System.out.println("GO unique "+stats[14]);
                            System.out.println("GO cover "+stats[18]);
                        }*/
                    }


                    if (data[j].startsWith("TF biclusters ")) {
                        System.out.println(data[j].substring(data[j].lastIndexOf("TF biclusters ") + "TF biclusters ".length(), data[j].indexOf("\tunique")));
                        System.out.println(data[j].substring(data[j].lastIndexOf("unique TFs ") + "unique TFs ".length(), data[j].indexOf("\tcover")));
                        System.out.println(data[j].substring(data[j].lastIndexOf("cover ") + "cover ".length(), data[j].length()));

                        stats[11] = Double.parseDouble(data[j].substring(data[j].lastIndexOf("TF biclusters ") + "TF biclusters ".length(), data[j].indexOf("\tunique")));
                        stats[15] = Double.parseDouble(data[j].substring(data[j].lastIndexOf("unique TFs ") + "unique TFs ".length(), data[j].indexOf("\tcover")));
                        stats[19] = Double.parseDouble(data[j].substring(data[j].lastIndexOf("cover ") + "cover ".length(), data[j].length()));

                        /*if(debug) {
                                System.out.println("TF biclusters "+stats[11]);
                                System.out.println("TF unique "+stats[15]);
                                System.out.println("TF cover "+stats[19]);
                        }*/
                    }


                    if (data[j].startsWith("Path biclusters ")) {
                        stats[12] = Double.parseDouble(data[j].substring(data[j].lastIndexOf("Path biclusters ") + "Path biclusters ".length(), data[j].indexOf("\tunique")));
                        stats[16] = Double.parseDouble(data[j].substring(data[j].lastIndexOf("unique Paths ") + "unique Paths ".length(), data[j].indexOf("\tcover")));
                        stats[20] = Double.parseDouble(data[j].substring(data[j].lastIndexOf("cover ") + "cover ".length(), data[j].length()));
                    }


                    if (data[j].startsWith("TIGRrole biclusters ")) {
                        stats[13] = Double.parseDouble(data[j].substring(data[j].lastIndexOf("TIGRrole biclusters ") + "TIGRrole biclusters ".length(), data[j].indexOf("\tunique")));
                        stats[17] = Double.parseDouble(data[j].substring(data[j].lastIndexOf("unique TIGRroles ") + "unique TIGRroles ".length(), data[j].indexOf("\tcover")));
                        stats[21] = Double.parseDouble(data[j].substring(data[j].lastIndexOf("cover ") + "cover ".length(), data[j].length()));
                    }

                }


                //=LOG((F8/D8)/0.153343377,2)
                stats[8] = Math.log((stats[4] / stats[2]) / (fold1 / total)) / Math.log(2.0);

                double number2 = stats[6];
                double bictotal = stats[2];
                double bicratio2 = number2 / bictotal;
                double totalratio2 = (fold2 / total);

                System.out.println("bicratio2 " + bicratio2 + "\ttotalratio2 " + totalratio2);
                stats[9] = Math.log(bicratio2 / totalratio2) / Math.log(2.0);


                //GO p.b.
                stats[22] = (stats[14] / stats[1]);
                //System.out.println("GO p.b. "+ stats[22] +" = "+ stats[18] +" /  "+stats[1]);
                //	TF p.b.
                //13
                stats[23] = (stats[15] / stats[1]);
                //System.out.println("TF p.b. "+ stats[23] +" = "+ stats[19] +" /  "+stats[1]);
                // 	Path p.b.
                stats[24] = (stats[16] / stats[1]);
                // TIGRrole p.b.
                stats[25] = (stats[17] / stats[1]);

                //runtime
                stats[26] = 0;
                //perbic sum
                stats[27] = stats[22] + stats[23] + stats[24] + stats[25];
                //coverage sum
                stats[28] = stats[18] + stats[19] + stats[20] + stats[21];//stats[5] + stats[7] +
                //GOcover/totalcover
                stats[29] = stats[18] / stats[28];
                //TFcover/totalcover
                stats[30] = stats[19] / stats[28];
                //Pathcover/totalcover
                stats[31] = stats[20] / stats[28];
                //TIGRrolecover/totalcover
                stats[32] = stats[21] / stats[28];


                /*"Coherence",
                "Gene coverage",
                "Condition coverage",
                "RMSD from bg"*/
                if (top_data != null) {
                    System.out.println("top_data " + top_data.length);
                    System.out.println("top_data " + top_data[0].length);
                } else
                    System.out.println("top_data null");

                if (vbl != null && vbl.size() > 0 && top_data != null) {
                    System.out.println("outputting data measures");
                    stats[33] = getMaxGenes(vbl);
                    stats[34] = getMaxExps(vbl);
                    stats[35] = getMeanFullcrit(vbl);
                    //stats[36] = getMeanMinVar(vbl);
                    stats[36] = getCorrelation(vbl, listout[i]);
                    stats[37] = getGeneCoverage(top_data);
                    stats[38] = getExpCoverage(top_data);
                    stats[39] = getRMSDtobg(vbl);
                    stats[40] = getMeanGenes(vbl);
                    stats[41] = getMeanExps(vbl);
                } else {
                    stats[33] = 0;
                    stats[34] = 0;
                    stats[35] = 0;
                    stats[36] = 0;
                    stats[37] = 0;
                    stats[38] = 0;
                    stats[39] = 0;
                    stats[40] = 0;
                    stats[41] = 0;
                }

                MoreArray.printArray(stats);

                out_data[count] = MoreArray.toStringArray(stats);
                //out.add(MoreArray.toStringArray(stats));

                count++;
            }
        }

        out.add(0, header);
        System.out.println("writing " + outfile);
        TabFile.write(out_data, outfile, header, row_labels);
    }


    /**
     * @param vbl
     * @return
     */
    public double getMeanFullcrit(ValueBlockList vbl) {

        double[] crits = new double[vbl.size()];
        for (int i = 0; i < vbl.size(); i++) {
            ValueBlock cur = (ValueBlock) vbl.get(i);
            crits[i] = cur.full_criterion;
        }

        return stat.avg(crits);
    }


    /**
     * @param vbl
     * @return
     */
    public double getMaxGenes(ValueBlockList vbl) {

        double max = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < vbl.size(); i++) {
            ValueBlock cur = (ValueBlock) vbl.get(i);
            double len = cur.genes.length;
            if (len > max)
                max = len;
        }

        return max;
    }

    /**
     * @param vbl
     * @return
     */
    public double getMaxExps(ValueBlockList vbl) {

        double max = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < vbl.size(); i++) {
            ValueBlock cur = (ValueBlock) vbl.get(i);
            double len = cur.exps.length;
            if (len > max)
                max = len;
        }

        return max;
    }

    /**
     * @param vbl
     * @return
     */
    public double getMeanGenes(ValueBlockList vbl) {

        double[] genes = new double[vbl.size()];
        for (int i = 0; i < vbl.size(); i++) {
            ValueBlock cur = (ValueBlock) vbl.get(i);
            genes[i] = cur.genes.length;
        }

        return stat.avg(genes);
    }

    /**
     * @param vbl
     * @return
     */
    public double getMeanExps(ValueBlockList vbl) {

        double[] exps = new double[vbl.size()];
        for (int i = 0; i < vbl.size(); i++) {
            ValueBlock cur = (ValueBlock) vbl.get(i);
            exps[i] = cur.exps.length;
        }

        return stat.avg(exps);
    }


    /**
     * @param vbl
     * @return
     */
    public double getMeanMinVar(ValueBlockList vbl) {

        double[] minvars = new double[vbl.size()];
        for (int i = 0; i < vbl.size(); i++) {
            ValueBlock cur = (ValueBlock) vbl.get(i);
            cur.update(input_data.data);
            double meanRowVar = 0;
            for (int a = 0; a < cur.exp_data.length; a++) {
                double[] row = cur.exp_data[a];
                double mean = stat.avg(row);
                meanRowVar += stat.SD(row, mean);
            }
            meanRowVar /= cur.exp_data.length;

            double meanColVar = 0;
            for (int b = 0; b < cur.exp_data[0].length; b++) {
                double[] col = Matrix.extractColumn(cur.exp_data, b + 1);
                double mean = stat.avg(col);
                meanColVar += stat.SD(col, mean);
            }

            meanColVar /= cur.exp_data[0].length;

            double mean = Matrix.avg(cur.exp_data);
            double absmean = Matrix.avg(Matrix.abs(cur.exp_data));

            //ROW or COL
            minvars[i] = Math.min(meanRowVar, meanColVar);
            //constant
            minvars[i] = Math.min(minvars[i], Matrix.SD(cur.exp_data, mean));
            //checker
            minvars[i] = Math.min(minvars[i], Matrix.SD(Matrix.abs(cur.exp_data), absmean));
        }

        return stat.avg(minvars);
    }


    /**
     * @param vbl
     * @return
     */
    public double getCorrelation(ValueBlockList vbl, String prefix) {

        double[] maxcor = new double[vbl.size()];
        int[] whichpattern = new int[4];
        for (int i = 0; i < vbl.size(); i++) {

            ArrayList rowCors = new ArrayList();
            ArrayList colCors = new ArrayList();
            ArrayList rowCorsAbs = new ArrayList();
            ArrayList colCorsAbs = new ArrayList();

            ValueBlock cur = (ValueBlock) vbl.get(i);
            cur.update(input_data.data);

            if (cur.exp_data[0].length > 2) {
                for (int a = 0; a < cur.exp_data.length; a++) {
                    double[] dataA = cur.exp_data[a];
                    double avgA = mathy.stat.avg(dataA);
                    dataA = mathy.stat.replace(dataA, Double.NaN, avgA);
                    for (int b = a + 1; b < cur.exp_data.length; b++) {
                        double[] dataB = cur.exp_data[b];
                        double avgB = mathy.stat.avg(dataB);
                        dataB = mathy.stat.replace(dataB, Double.NaN, avgB);

                        double cor = mathy.stat.correlationCoeff(dataA, dataB);
                        //System.out.println(i + "\trowCors " + cor);
                        if (!Double.isNaN(cor))
                            rowCors.add(cor);
                    }
                }
            } else {
                //System.out.println("small row " + i + "\t" + cur.exp_data[0].length);
                rowCors.add(-1.0);
            }

            if (cur.exp_data.length > 2) {
                for (int a = 0; a < cur.exp_data[0].length; a++) {
                    double[] dataA = Matrix.extractColumn(cur.exp_data, a + 1);
                    double avgA = mathy.stat.avg(dataA);
                    dataA = mathy.stat.replace(dataA, Double.NaN, avgA);
                    for (int b = a + 1; b < cur.exp_data[0].length; b++) {
                        double[] dataB = Matrix.extractColumn(cur.exp_data, b + 1);
                        double avgB = mathy.stat.avg(dataB);
                        dataB = mathy.stat.replace(dataB, Double.NaN, avgB);

                        double cor = mathy.stat.correlationCoeff(dataA, dataB);
                        //System.out.println(i + "\tcolCors " + cor);
                        if (!Double.isNaN(cor))
                            colCors.add(cor);
                    }
                }
            } else {
                //System.out.println("small col " + i + "\t" + cur.exp_data.length);
                colCors.add(Double.NaN);
            }

            //abs row
            if (cur.exp_data[0].length > 2) {
                for (int a = 0; a < cur.exp_data.length; a++) {
                    double[] dataA = mathy.stat.abs(cur.exp_data[a]);
                    double avgA = mathy.stat.avg(dataA);
                    dataA = mathy.stat.replace(dataA, Double.NaN, avgA);
                    for (int b = a + 1; b < cur.exp_data.length; b++) {
                        double[] dataB = mathy.stat.abs(cur.exp_data[b]);
                        double avgB = mathy.stat.avg(dataB);
                        dataB = mathy.stat.replace(dataB, Double.NaN, avgB);

                        double cor = mathy.stat.correlationCoeff(dataA, dataB);
                        //System.out.println(i + "\trowCorsAbs " + cor);
                        if (!Double.isNaN(cor))
                            rowCorsAbs.add(cor);
                    }
                }
            } else {
                //System.out.println("small row " + i + "\t" + cur.exp_data[0].length);
                rowCorsAbs.add(-1.0);
            }

            //abs col
            if (cur.exp_data.length > 2) {
                for (int a = 0; a < cur.exp_data[0].length; a++) {
                    double[] dataA = mathy.stat.abs(Matrix.extractColumn(cur.exp_data, a + 1));
                    double avgA = mathy.stat.avg(dataA);
                    dataA = mathy.stat.replace(dataA, Double.NaN, avgA);
                    for (int b = a + 1; b < cur.exp_data[0].length; b++) {
                        double[] dataB = mathy.stat.abs(Matrix.extractColumn(cur.exp_data, b + 1));
                        double avgB = mathy.stat.avg(dataB);
                        dataB = mathy.stat.replace(dataB, Double.NaN, avgB);

                        double cor = mathy.stat.correlationCoeff(dataA, dataB);
                        //System.out.println(i + "\tcolCorsAbs " + cor);
                        if (!Double.isNaN(cor))
                            colCorsAbs.add(cor);
                    }
                }
            } else {
                //System.out.println("small col " + i + "\t" + cur.exp_data.length);
                colCorsAbs.add(-1.0);
            }

            double[] avg = new double[4];
            /*double avgRow = mathy.stat.listAvg(rowCors);
            double avgCol = mathy.stat.listAvg(colCors);
            double avgRowAbs = mathy.stat.listAvg(rowCorsAbs);
            double avgColAbs = mathy.stat.listAvg(colCorsAbs); */

            avg[0] = mathy.stat.listAvg(rowCors);
            avg[1] = mathy.stat.listAvg(colCors);
            avg[2] = mathy.stat.listAvg(rowCorsAbs);
            avg[3] = mathy.stat.listAvg(colCorsAbs);

            //System.out.println("maxcor[i] bf " + maxcor[i]);
            maxcor[i] = mathy.stat.findMax(avg);
            //System.out.println("maxcor[i] af " + maxcor[i]);
            //MoreArray.printArray(avg);
            int index = mathy.stat.findIndex(avg, maxcor[i]);
            try {
                whichpattern[index]++;
            } catch (Exception e) {
                MoreArray.printArray(cur.exp_data);
                System.out.println("maxcor[i] " + maxcor[i]);
                MoreArray.printArray(avg);
                e.printStackTrace();
            }
            //System.out.println("maxcor[i] af " + maxcor[i]);
        }

        String outf = prefix + "_correlations.txt";
        TabFile.write(MoreArray.toStringArray(maxcor), outf);
        System.out.println("wrote " + outf);

        double avgmaxcor = stat.avg(maxcor);
        System.out.println("avgmaxcor " + avgmaxcor);
        MoreArray.printArray(maxcor);

        System.out.println("whichpattern");
        MoreArray.printArray(whichpattern);

        return avgmaxcor;
    }


    /**
     * @param vbl
     * @return
     */

    public double getRMSDtobg(ValueBlockList vbl) {

        double[] RMSDs = new double[vbl.size()];
        for (int i = 0; i < vbl.size(); i++) {
            ValueBlock cur = (ValueBlock) vbl.get(i);

            double vals = 0;
            for (int a = 0; a < cur.exp_data.length; a++) {
                for (int b = 0; b < cur.exp_data[0].length; b++) {

                    if (!Double.isNaN(cur.exp_data[a][b])) {
                        final double v = cur.exp_data[a][b] - bgmean;
                        //System.out.println(" v " + v);
                        vals += v * v;
                    }
                }
            }
            RMSDs[i] = Math.sqrt(vals / ((double) cur.exp_data.length * (double) cur.exp_data[0].length));
            //System.out.println("  cur.exp_data.length  cur.exp_data[0].length " +
            //        cur.exp_data.length + "\t" + cur.exp_data[0].length);
            System.out.println("  RMSDs[i] " + RMSDs[i] + "\t" + vals);
        }

        return stat.avg(RMSDs);
    }


    /**
     * @param top_data
     * @return
     */
    public double getGeneCoverage(String[][] top_data) {

        String[] data = MoreArray.extractColumnStr(top_data, 3);

        System.out.println("data len " + data.length + "\t" + data[0]);
        HashMap hm = new HashMap();
        for (int i = 0; i < data.length; i++) {
            String g = null;
            try {
                int endIndex = data[i].indexOf("/");
                g = data[i].substring(0, endIndex);
                g = StringUtil.replace(g, "\"", "");
                String[] gids = g.split(",");
                for (int a = 0; a < gids.length; a++) {
                    hm.put(Integer.parseInt(gids[a]), 1);
                }
            } catch (Exception e) {
                System.out.println("Error for " + i);
                System.out.println(data[i]);
                e.printStackTrace();
            }
        }

        double cover = (double) hm.size() / (double) input_data.data.length;
        return cover;
    }

    /**
     * @param top_data
     * @return
     */
    public double getExpCoverage(String[][] top_data) {

        String[] data = MoreArray.extractColumnStr(top_data, 3);

        HashMap hm = new HashMap();
        for (int i = 0; i < data.length; i++) {
            String e = data[i].substring(data[i].indexOf("/") + 1, data[i].length());
            e = StringUtil.replace(e, "\"", "");
            String[] eids = e.split(",");
            for (int a = 0; a < eids.length; a++) {
                hm.put(Integer.parseInt(eids[a]), 1);
            }
        }

        double cover = (double) hm.size() / (double) input_data.data[0].length;
        return cover;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 4) {
            BuildGraphsToTable rm = new BuildGraphsToTable(args);
        } else {
            System.out.println("syntax: java DataMining.util.BuildGraphsToTable\n" +
                    "<dir with BuildGrarph .out files>\n" +
                    //"<dir with BuildGrarph summary files>\n" +
                    "<dir with toplist files>\n" +
                    "<input data>\n" +
                    "<outfiles>"

            );
        }
    }


    /**
     *
     */
    private class BlockData {
        private String arg;
        private String s;
        //private String[] listsummary;
        private String[] listtop;
        private String[][] top_data;
        private ValueBlockList vbl;

        public BlockData(String arg, String s, String... listtop) {//String[] listsummary,
            this.arg = arg;
            this.s = s;
            //this.listsummary = listsummary;
            this.listtop = listtop;
        }

        public String[][] getTop_data() {
            return top_data;
        }

        public ValueBlockList getVbl() {
            return vbl;
        }

        public BlockData invoke() {
            int startt = s.indexOf("BuildGraph_") + "BuildGraph_".length();
            int stopt1 = s.indexOf(".out");
            int stopt2 = s.indexOf("_known.out");
            int stopt = -1;
            if (stopt1 != -1 && stopt2 != -1)
                stopt = Math.min(stopt1, stopt2);
            else if (stopt1 != -1)
                stopt = stopt1;
            else if (stopt2 != -1)
                stopt = stopt2;

            top_data = null;
            vbl = null;
            System.out.println("startt " + startt + "\t" + stopt);
            if (startt != -1 && stopt != -1) {
                String search = s.substring(startt, stopt);//("BuildGraph_");
                System.out.println("search " + search);

               /* String summary_file = null;
                for (int a = 0; a < listsummary.length; a++) {
                    if (listsummary[a].indexOf(search) != -1) {
                        System.out.println("found it " + listsummary[a]);
                        summary_file = listsummary[a];
                        break;
                    }
                }

                String[][] summary_data = null;
                if (summary_file != null) {

                }*/

                String toplist_file = null;
                for (int a = 0; a < listtop.length; a++) {
                    if (listtop[a].equals(search)) {
                        System.out.println("found it " + listtop[a]);
                        toplist_file = listtop[a];
                        break;
                    }
                }

                if (toplist_file == null)
                    System.out.println("toplist_file is null for " + search);
                else if (toplist_file != null) {
                    String path = arg + "/" + toplist_file;
                    System.out.println("invoke " + path);
                    try {
                        vbl = ValueBlockList.read(path, debug);
                        System.out.println("invoke vbl " + vbl.size());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    top_data = TabFile.readtoArray(path, 1, debug);
                    System.out.println("invoke top_data " + top_data.length);
                    if (debug) {
                        System.out.println("invoke top_data " + top_data.length);
                        MoreArray.printArray(top_data[0]);
                    }
                }
            }
            return this;
        }
    }
}
