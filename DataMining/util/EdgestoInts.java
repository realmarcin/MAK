package DataMining.util;

import util.MoreArray;
import util.StringUtil;
import util.TabFile;

import java.util.ArrayList;

/**
 * User: marcin
 * Date: 7/6/20
 * Time: 5:34 PM
 */
public class EdgestoInts {

    String[][] ENIGMA_terms = {
            {"Top_"},
            {"Sample:", "Subsample:", "Experiment:"},
            {"Ph_"},
            {"Isolate:"},
            {"SMeas_Trait:", "WMeas_Trait", "FPT_Trait", "PC2_Trait"},
            {"EE_Bicluster:", "TS_Bicluster:",
                    "PS_Bicluster:", "TM_Bicluster:", "MS_Bicluster:", "FS_Bicluster:"},
            {"Sample_Taxon_Cluster:", "Taxon_Sample_Cluster:", "Exp1_Exp2_Cluster:", "Exp2_Exp1_Cluster:",
                    "Sample_FPT_Cluster:", "Sample_Meas_Cluster:", "Meas_Sample_Cluster:", "Sample_PC2_Cluster:", "PC2_Sample_Cluster:",
                    "FPT_Sample_Cluster:", "Taxon_Meas_Cluster:", "PC2_Sample_Cluster:", "Meas_Taxon_Cluster:"},
            {"Water_Measurement:"},
            {"Sediment_Measurement:"}};

    String[] ENIGMA_term_classes = {
            "Top",
            "Samples_Subsamples_Experiments",
            "Phylogeny",
            "Isolates",
            "Traits",
            "Biclusters",
            "Clusters",
            "Water_Measurements",
            "Sediment_Measurements"
    };


    boolean kgx = false;

    /**
     * @param args
     */
    public EdgestoInts(String[] args) {

        System.out.println("reading");
        String[][] data = TabFile.readtoArray(args[0]);
        System.out.println("collecting nodes");
        ArrayList<String> nodes = new ArrayList<String>();

        if (data[0][0].equalsIgnoreCase(("subject")) &&
                data[0][1].equalsIgnoreCase(("edge_label")) &&
                data[0][2].equalsIgnoreCase(("object"))) {
            kgx = true;
            System.out.println("Detected KGX format");
        }

        int start = 0;
        if (kgx)
            start = 1;

        System.out.println("read " + (data.length - start) + " edges");
        for (int i = start; i < data.length; i++) {
            if (!nodes.contains(data[i][0]))
                nodes.add(data[i][0]);
            if (!nodes.contains(data[i][1]))
                nodes.add(data[i][1]);
        }
        System.out.println("to array");
        String[] nodestr = MoreArray.ArrayListtoString(nodes);

        System.out.println("map edges");
        String[] out = new String[data.length - start];
        for (int i = start; i < data.length; i++) {
            //increment indices for 1 offset
            int index1 = StringUtil.getFirstEqualsIndex(nodestr, data[i][0]);
            index1++;
            int index2 = StringUtil.getFirstEqualsIndex(nodestr, data[i][1]);
            index2++;
            out[i - start] = "" + index1 + "\t" + index2;
        }

        String f = args[0].substring(0, args[0].lastIndexOf('.')) + "_intindex.txt";
        System.out.println("writing " + f);
        TabFile.write(out, f);


        System.out.println("map nodes");
        String[] out2 = new String[nodestr.length];
        String[] out3 = new String[nodestr.length];
        out3[0] = "index\ttype";
        for (int i = 0; i < nodestr.length; i++) {
            out2[i] = "" + (i + 1) + "\t" + nodestr[i];
            out3[i] = "" + (i + 1);
            boolean done = false;
            int offset = 0;
            for (int j = 0; j < ENIGMA_terms.length; j++) {
                for (int k = 0; k < ENIGMA_terms[j].length; k++) {
                    if (nodestr[i].startsWith(ENIGMA_terms[j][k])) {
                        done = true;
                    }
                    if (done)
                        break;
                }
                if (done) {
                    offset = j;
                    break;
                }
            }
            if (done) {
                for (int z = 0; z < offset; z++) {
                    out3[i] += "\t";
                }
                out3[i] += "\t" + ENIGMA_term_classes[offset];
            } else if (!done) {
                System.out.println("new\n" + nodestr[i]);
            }
        }

        String f2 = args[0].substring(0, args[0].lastIndexOf('.')) + "_nodes_intindex.txt";
        System.out.println("writing " + f2);
        TabFile.write(out2, f2);

        String f3 = args[0].substring(0, args[0].lastIndexOf('.')) + "_nodes_meta.txt";
        System.out.println("writing " + f3);
        TabFile.write(out3, f3);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 1) {
            EdgestoInts arg = new EdgestoInts(args);
        } else {
            System.out.println("syntax: java DataMining.util.EdgestoInts <input edge file with node labels>\n");
        }
    }
}
