package DataMining;

import bioobj.YeastRegulons;
import mathy.SimpleMatrix;
import util.MapArgOptions;
import util.MoreArray;
import util.Program;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * User: marcin
 * Date: Mar 31, 2008
 * Time: 11:12:25 AM
 */
public class AnalyzeRegulonResults extends Program {

    HashMap options;
    String[] valid_args = {"-regulons", "-dir", "-dataset"};

    String factor = "MCM1";//"GAL4";
    SimpleMatrix expr_matrix;

    int block_size = 15;


    /**
     * @param args
     */
    public AnalyzeRegulonResults(String[] args) {

        //args = MapArgOptions.compactArgs(args);
        options = MapArgOptions.maptoMap(args, valid_args);

        String dirpath = (String) options.get("-dir");
        String regulons = (String) options.get("-regulons");
        String datapath = (String) options.get("-dataset");

        expr_matrix = new SimpleMatrix(datapath);

        YeastRegulons yr = new YeastRegulons(regulons, "MacIsaac");

        if (!factor.equals("none")) {
            int index = MoreArray.getArrayInd(yr.factors, factor);
            ArrayList gal4_list = yr.significant[index];
            ArrayList gal4_list_int = new ArrayList();
            for (int i = 0; i < gal4_list.size(); i++) {
                String cur = (String) gal4_list.get(i);
                //System.out.println("GAL4 " + i + "\t" + cur);
                int add = MoreArray.getArrayInd(expr_matrix.ylabels, cur) + 1;
                //System.out.println("AnalyzeRegulonResults "+cur+"\t"+add);
                gal4_list_int.add(new Integer(add));
            }

            File dir = new File(dirpath);
            String[] files = dir.list();
            run(dirpath, gal4_list_int, files);
        }

    }

    /**
     * @param dirpath
     * @param gal4_list_int
     * @param files
     */
    private void run(String dirpath, ArrayList gal4_list_int, String[] files) {
        try {
            String outf = dirpath + sysRes.file_separator + "analyze_regulons.txt";
            System.out.println("generating " + outf);
            PrintWriter pw = new PrintWriter(new FileWriter(outf), true);
            pw.println("dirindex\tdir\tfileindex\tfile\tfirst regulon count\tlast regulon count\tsample" +
                    "\tfirst regulon ratio\tlast regulon ratio\tnum genes last\tlast.percentOrigGenes\t" +
                    "last.percentOrigExp\tfirst.precrit\tlast.precrit");
            //GAL4_10_runs_sample1_random13
            ArrayList[] results = new ArrayList[files.length];
            for (int i = 0; i < files.length; i++) {
                File test = new File(dirpath + sysRes.file_separator + files[i]);
                if (test.isDirectory()) {
                    //System.out.println("AnalyzeRegulonResults " + files[i]);
                    int sample = -1;
                    //int random = -1;
                    int start = files[i].indexOf("_sample") + "_sample".length();
                    int end = files[i].indexOf("_random");
                    if (start != -1 && end != -1)
                        sample = Integer.parseInt(files[i].substring(start, end));
                    else {
                        start = files[i].indexOf("_") + 1;
                        sample = Integer.parseInt(files[i].substring(start, files[i].length()));
                    }
                    System.out.println("sample " + sample);
                    //random = block_size - sample;
                    try {
                        results[sample - 1] = new ArrayList();
                    } catch (Exception e) {
                        System.out.println("test case");
                        results[0] = new ArrayList();
                    }

                    String sampdirpath = dirpath + sysRes.file_separator + files[i] + sysRes.file_separator + "toplist" + sysRes.file_separator;
                    File sampdir = new File(sampdirpath);
                    if (!sampdir.exists()) {
                        sampdirpath = dirpath + sysRes.file_separator + files[i];
                        sampdir = new File(sampdirpath);
                    }

                    System.out.println("sampdirpath " + sampdirpath);
                    String[] sampfiles = sampdir.list();
                    for (int j = 0; j < sampfiles.length; j++) {
                        System.out.println("sampfiles[j] " + sampfiles[j]);
                        ValueBlockList vbl = null;
                        if (sampfiles[j].indexOf("_toplist.txt") == sampfiles[j].length() - "_toplist.txt".length()) {
                            try {
                                vbl = ValueBlockList.read(sampdirpath + sysRes.file_separator + sampfiles[j], false);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (sampfiles[j].indexOf(".bic") == sampfiles[j].length() - ".bic".length()) {
                            vbl = ValueBlockListMethods.readBIC(sampdirpath + sysRes.file_separator + sampfiles[j], false);
                        }
                        if (vbl != null) {
                            ValueBlock first = (ValueBlock) vbl.get(0);
                            ValueBlock last = (ValueBlock) vbl.get(vbl.size() - 1);
                            int firstcount = 0;
                            int lastcount = 0;
                            for (int m = 0; m < first.genes.length; m++) {
                                //int s = ((Integer) first.coords[0].get(m)).intValue();
                                int s = first.genes[m];
                                int index_in_regulon = MoreArray.getArrayInd(gal4_list_int, s);
                                if (index_in_regulon != -1) {
                                    firstcount++;
                                }
                            }
                            for (int m = 0; m < last.genes.length; m++) {
                                //int s = ((Integer) last.coords[0].get(m)).intValue();
                                int s = last.genes[m];
                                int index_in_regulon = MoreArray.getArrayInd(gal4_list_int, s);
                                if (index_in_regulon != -1)
                                    lastcount++;
                            }
                            //if (firstcount != sample)
                            //System.out.println("\noverlap for first " + firstcount + "\tsample " + lastcount+"\t"+sample);
                            double ratio1 = (double) firstcount / (double) sample;
                            double ratio2 = (double) lastcount / (double) sample;
                            pw.println(i + "\t" + files[i] + "\t" + j + "\t" + sampfiles[j] + "\t" + firstcount + "\t" +
                                    lastcount + "\t" + sample + "\t" + ratio1 + "\t" + ratio2 +
                                    "\t" + last.genes.length + "\t" + last.percentOrigGenes + "\t" + last.percentOrigExp
                                    + "\t" + first.pre_criterion + "\t" + last.pre_criterion);
                            /*
                            if (firstcount != lastcount)
                            System.out.println("overlap for last " +  + "\tsample " + sample);
                            */
                        }
                    }
                }
            }
            pw.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 6) {
            AnalyzeRegulonResults arg = new AnalyzeRegulonResults(args);
        } else {
            System.out.println("syntax: java DataMining.AnalyzeRegulonResults\n" +
                    "<-dir dir of dirs of toplists or BicAT output'>\n" +
                    "<-regulons 'regulon file'>\n" +
                    "<-dataset labeled expr. data set>"
            );
        }
    }
}
