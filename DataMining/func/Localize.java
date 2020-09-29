package DataMining.func;

import util.MoreArray;
import util.TabFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: Aug 8, 2012
 * Time: 10:32:15 PM
 */
public class Localize {

    public final static String[] localization_terms = {
            //"cell budding",
            "cell cortex",
            //"cellular bud",
            "cellular membrane organization",
            "cell wall",
            "chromosome",
            "chromosome segregation",
            "cytoplasm",
            "cytoplasmic membrane-bounded vesicle",
            "cytoskeleton",
            "cytoskeleton organization",
            "endoplasmic reticulum",
            "endomembrane system",
            "extracellular",
            "extracellular region",
            "fungal-type cell wall organization",
            "golgi",
            "golgi apparatus",
            "Golgi vesicle transport",
            "lysosome",
            "membrane",
            "membrane fraction",
            "transmembrane transport",
            "microtubule",
            "microtubule organizing center",
            "motor activity",
            "mitochondrial envelope",
            "mitochondrion",
            "mitochondrion organization",
            "cellular respiration",
            "nucleolus",
            "ribosomal small subunit biogenesis",
            "ribosomal large subunit biogenesis",
            "rRNA processing",
            "ribosome assembly",
            "nuclear membrane",
            "nucleus",
            "nucleus organization",
            "nuclear transport",
            "nucleic acid binding transcription factor activity",
            "transcription from RNA polymerase III promoter",
            "transcription from RNA polymerase I promoter",
            "transcription from RNA polymerase II promoter",
            "DNA repair",
            "DNA replication",
            "peroxisome",
            "peroxisome organization",
            "plasma membrane",
            //"spore",
            //"sporulation",
            "vacuole",
            "vacuole organization",
            "vesicle-mediated transport",
            "vesicle organization",
            "none"
    };

    public final static int[] localization_group_index = {
            ////"cell budding",
            2,
            //"cell cortex",
            ////"cellular bud",
            7,
            //"cellular membrane organization",
            0,
            //"cell wall",
            9,
            //"chromosome",
            9,
            //"chromosome segregation",
            1,
            //"cytoplasm",
            1,
            //"cytoplasmic membrane-bounded vesicle",
            2,
            //"cytoskeleton",
            2,
            //"cytoskeleton organization",
            3,
            //"endoplasmic reticulum",
            3,
            //"endomembrane system",
            4,
            //"extracellular",
            4,
            //"extracellular region",
            0,
            //"fungal-type cell wall organization",
            5,
            //"golgi",
            5,
            //"Golgi apparatus",
            5,
            //"Golgi vesicle transport",
            6,
            //"lysosome",
            7,
            //"membrane",
            7,
            //membrane fraction
            7,
            //transmembrane transport
            2,
            //"microtubule",
            2,
            //"microtubule organizing center",
            2,
            //motor activity
            8,
            //"mitochondrial envelope",
            8,
            //"mitochondrion",
            8,
            //"mitochondrion organization",
            8,
            //"cellular respiration",
            12,
            //"nucleolus",
            12,
            //ribosomal small subunit biogenesis
            12,
            //ribosomal large subunit biogenesis
            12,
            //rRNA processing
            12,
            //ribosome assembly
            9,
            //"nuclear membrane",
            9,
            //"nucleus",
            9,
            //"nucleus organization",
            9,
            //nuclear transport
            9,
            //nucleic acid binding transcription factor activity
            9,
            //transcription from RNA polymerase III promoter
            9,
            //transcription from RNA polymerase I promoter
            9,
            //transcription from RNA polymerase II promoter
            9,
            //DNA repair
            9,
            //DNA replication
            10,
            //"peroxisome",
            10,
            //"peroxisome organization",
            7,
            //"plasma membrane",
            ////"spore",
            ////"sporulation",
            11,
            //"vacuole",
            11,
            //"vacuole organization",
            5,
            //"vesicle-mediated transport",
            5,
            //"vesicle organization",
            -1
            //"none"
    };

    //1,2,3,4,5,6,7,8
    public final static String[] localization_groups = {
            "cell wall",//0
            "cytoplasm",//1
            "cytoskeleton",//2
            "ER",//3
            "extracellular",//4
            "golgi",//5
            "lysosome",//6
            "membrane",//7
            "mitochondrion",//8
            "nucleus",//9
            "peroxisome",//10
            "vacoule",//11
            "nucleolus"//12

    };

    double overlap_cut = 0.1;

    String[][] binet;

    HashMap overlap_hash;

    /**
     * @param args
     */
    public Localize(String[] args) {

        String[][] summary_data = TabFile.readtoArray(args[0]);
        binet = TabFile.readtoArray(args[1], 1, true);

        overlap_hash = new HashMap();

        for (int i = 0; i < binet.length; i++) {
            overlap_hash.put(binet[i][0] + "_" + binet[i][1], Double.parseDouble(binet[i][2]));
        }

        int[][] output = new int[localization_terms.length][localization_terms.length];

        String outf = args[2];

        /*index	block_id	genes	exps	area	full_crit 	exp_mean	TIGRpval	TIGR
          TIGRrolepval	TIGRrole	GOpval	GO	Pathpval	Path	TFpval	TF
          exp_mean_crit	exp_mse_crit	exp_kendall_crit	exp_reg_crit	inter_crit	feat_crit	TF_crit*/

        for (int i = 0; i < summary_data.length; i++) {
            System.out.print(".");
            int locindex1 = findLocIndexMax(summary_data[i][12]);
            if (locindex1 == -1)

                for (int j = i + 1; j < summary_data.length; j++) {
                    double overlap = findOverlap(i + 1, j + 1);
                    if (overlap > overlap_cut) {
                        int locindex2 = findLocIndexMax(summary_data[j][12]);
                        if (locindex2 == -1)
                            locindex2 = localization_terms.length - 1;

                        output[locindex1][locindex2]++;
                        output[locindex2][locindex1]++;
                        //System.out.println("Localize "+localization_terms[locindex1] + "\t" + localization_terms[locindex2] + "\t" + output[locindex1][locindex2]);
                    }
                }
        }

        TabFile.write(MoreArray.toString(output, "", ""), outf, localization_terms, localization_terms);
    }


    /**
     * @param a
     * @param b
     * @return
     */
    public double findOverlap(int a, int b) {
        String key = "" + a + "_" + b;
        Object o = (Object) overlap_hash.get(key);
        if (o != null) {
            return (Double) o;
        }
        return 0;
    }

    /**
     * @param s
     * @return
     */
    public int findLocIndexMax(String s) {
        s = s.toLowerCase();

        //System.out.println("findLocIndexMax " + s);

        ArrayList ar = new ArrayList();
        ArrayList pos = new ArrayList();
        for (int i = 0; i < localization_terms.length; i++) {
            int ind = s.indexOf("_" + localization_terms[i] + "_");
            if (ind != -1) {
                ar.add((Integer) i);
                pos.add(ind);
            }
        }
        for (int i = 0; i < localization_terms.length; i++) {
            if (s.startsWith("go: " + localization_terms[i] + "_")) {
                ar.add((Integer) i);
                pos.add(0);
            }
        }
        for (int i = 0; i < localization_terms.length; i++) {
            if (s.endsWith("_" + localization_terms[i])) {
                ar.add((Integer) i);
                int ind = s.lastIndexOf("_" + localization_terms[i]);
                pos.add(ind);
            }
        }
        for (int i = 0; i < localization_terms.length; i++) {
            if (s.equals("go: " + localization_terms[i])) {
                ar.add((Integer) i);
                pos.add(0);
            }
        }

        int min = 1000000;
        for (int i = 0; i < pos.size(); i++) {
            int cur = (Integer) pos.get(i);
            if (cur < min) {
                min = cur;
            }
        }
        if (min == 1000000)
            min = -1;
        return min;
    }

    /**
     * Gives sorted indices of all localization term matches
     *
     * @param s
     * @return
     */
    public final static int[] findLocIndicesSortAlpha(String s) {
        s = s.toLowerCase();

        ArrayList ar = new ArrayList();
        ArrayList pos = new ArrayList();
        for (int i = 0; i < localization_terms.length - 1; i++) {
            int ind = s.indexOf("_" + localization_terms[i] + "_");
            if (ind != -1) {
                ar.add((Integer) i);
                pos.add(ind);
                //return i;
            }
        }


        for (int i = 0; i < localization_terms.length; i++) {
            if (s.startsWith("go: " + localization_terms[i] + "_")) {
                ar.add((Integer) i);
                pos.add(0);
                //return i;
            }
        }
        for (int i = 0; i < localization_terms.length; i++) {
            if (s.endsWith("_" + localization_terms[i])) {
                ar.add((Integer) i);
                int ind = s.lastIndexOf("_" + localization_terms[i]);
                pos.add(ind);
                //return i;
            }
        }
        for (int i = 0; i < localization_terms.length; i++) {
            //System.out.println("findLocIndicesSortAlpha *" + localization_terms[i] + "*" + s + "*");
            if (s.equals("go: " + localization_terms[i])) {
                ar.add((Integer) i);
                pos.add(0);
                //return i;
            }
        }
        for (int i = 0; i < localization_terms.length; i++) {
            //System.out.println("findLocIndicesSortAlpha *" + localization_terms[i] + "*" + s + "*");
            if (s.equals(localization_terms[i])) {
                ar.add((Integer) i);
                pos.add(0);
                //return i;
            }
        }

        ArrayList groups = new ArrayList();
        for (int i = 0; i < ar.size(); i++) {
            int cur = (Integer) ar.get(i);
            int gindex = localization_group_index[cur];
            int index = groups.indexOf(gindex);
            if (index == -1)
                groups.add(gindex);
        }

        int[] ret = MoreArray.ArrayListtoInt(groups);
        Arrays.sort(ret);
        return ret;
    }

    /**
     * @param golabels
     * @return
     */
    public final static String makeGroupLabel(String golabels) {

        //System.out.println("makeGroupLabel test length " + localization_terms.length + "\t" + localization_group_index.length);
        //System.out.println("makeGroupLabel " + golabels);
        int[] gindex = findLocIndicesSortAlpha(golabels);
        //System.out.println("makeGroupLabel " + gindex != null ? gindex.length : Double.NaN);
        //MoreArray.printArray(gindex);
        String ret = "";
        for (int i = 0; i < gindex.length; i++) {
            ret += localization_groups[gindex[i]] + "_";
        }
        if (ret.endsWith("_"))
            ret = ret.substring(0, ret.length() - 1);

        if (ret.equals(""))
            ret = "none";

        //System.out.println("makeGroupLabel " + ret);
        return ret;
    }


    /**
     * @param s
     * @return
     */
    public ArrayList findLocIndex(String s) {
        ArrayList ar = new ArrayList();
        for (int i = 0; i < localization_terms.length; i++) {
            if (s.indexOf("_" + localization_terms[i] + "_") != -1) {
                ar.add((Integer) i);
                //return i;
            }
        }
        for (int i = 0; i < localization_terms.length; i++) {
            if (s.startsWith("GO: " + localization_terms[i] + "_")) {
                ar.add((Integer) i);
                //return i;
            }
        }
        for (int i = 0; i < localization_terms.length; i++) {
            if (s.endsWith("_" + localization_terms[i])) {
                ar.add((Integer) i);
                //return i;
            }
        }
        for (int i = 0; i < localization_terms.length; i++) {
            if (s.equals("GO: " + localization_terms[i])) {
                ar.add((Integer) i);
                //return i;
            }
        }
        return ar;
    }


    /**
     * @param args
     */

    public final static void main(String[] args) {
        if (args.length == 3) {
            Localize rm = new Localize(args);
        } else {
            System.out.println("syntax: java DataMining.func.Localize\n" +
                    "<MAK summary file> <bicluster network> <oufile prefix>"
            );
        }
    }
}
