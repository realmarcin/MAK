package DataMining;

import util.MoreArray;
import util.Program;
import util.TextFile;

import java.io.File;
import java.util.ArrayList;

/**
 * User: mjoachimiak
 * Date: Jul 8, 2009
 * Time: 11:47:12 PM
 */
public class AnalyzePairs extends Program {


    String tag = "synthdata090621_";
    int min_size = 2;

    /**
     *
     */
    public AnalyzePairs(String[] args) {

        String dirpath = args[0];
        String outpath = args[1];

        File dir = new File(dirpath);
        String[] files = dir.list();
        if (files != null) {

            ArrayList out = new ArrayList();
            int max = 0;
            for (int i = 0; i < files.length; i++) {
                if (files[i].indexOf("toplist") != -1) {
                    String readpathfrom = dirpath + sysRes.file_separator + files[i];
                    System.out.println("ValueBlockList f " + readpathfrom);
                    ValueBlockList vblfrom = null;
                    try {
                        vblfrom = ValueBlockList.read(readpathfrom, false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    int frsi = vblfrom.size();
                    if (frsi >= min_size) {
                        System.out.println("ValueBlockList f " + vblfrom.size());
                        for (int j = i + 1; j < files.length; j++) {
                            if (files[j].indexOf("toplist") != -1) {
                                String readpathto = dirpath + sysRes.file_separator + files[j];
                                System.out.println("ValueBlockList t " + readpathto);
                                ValueBlockList vblto = null;
                                try {
                                    vblto = ValueBlockList.read(readpathto, false);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                int tosi = vblto.size();
                                if (tosi >= min_size) {
                                    System.out.println("ValueBlockList t " + tosi);

                                    int i1 = files[i].indexOf(tag) + tag.length();
                                    int i1e = files[i].indexOf("_", i1 + 1);
                                    i1e = files[i].indexOf("_", i1e + 1);
                                    String l1 = files[i].substring(i1, i1e);
                                    int i2 = files[j].indexOf(tag) + tag.length();
                                    int i2e = files[j].indexOf("_", i2 + 1);
                                    i2e = files[j].indexOf("_", i2e + 1);
                                    String l2 = files[j].substring(i2, i2e);

                                    double[] doubles = AnalyzeBlockList.analyzePair(vblfrom, vblto);
                                    if (doubles.length > max)
                                        max = doubles.length;
                                    out.add(l1 + "-" + l2 + "\t" + MoreArray.toString(doubles, "\t"));
                                }
                            }
                        }
                    }
                }
            }

            String[] labels = MoreArray.initArray(max + 1, "col");
            out.add(0, MoreArray.toString(labels, "\t"));
            TextFile.write(out, outpath + "/" + "analyze_pairs.txt");
        } else {
            System.out.println("AnalyzeRegulonShaving broken dirpath " + dirpath);
            System.exit(1);
        }
    }

    /**
     * @param args
     */

    public static void main(String[] args) {
        if (args.length == 2) {
            AnalyzePairs arg = new AnalyzePairs(args);
        } else {
            System.out.println("syntax: java DataMining.AnalyzePairs\n" +
                    "<dir dir of toplists'>\n" +
                    "<autooutpath 'regulon file'>"
            );
        }
    }
}
