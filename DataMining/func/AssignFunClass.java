package DataMining.func;

import util.MoreArray;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: Aug 16, 2012
 * Time: 9:36:26 AM
 */
public class AssignFunClass {

    public final static String[] functional_terms = {
            //GO and general
            "amino acid",
            "alanine",
            "cysteine",
            "aspartate",
            "aspartatic acid",
            "glutamate",
            "glutamatic acid",
            "phenylalanine",
            "glycine",
            "histidine",
            "isoleucine",
            "lysine",
            "leucine",
            "methionine",
            "asparagine",
            "proline",
            "glutamine",
            "arginine",
            "serine",
            "threonine",
            "valine",
            "tryptophan",
            "tyrosine",
            "amino acid biosynthesis",
            "histidine biosynthetic process",
            "branched chain family amino acid biosynthetic process",
            "leucine biosynthetic process",

            "carbohydrate",
            "glycogen",

            "cellular respiration",
            "generation of precursor metabolites and energy",
            "tca cycle",
            "electron transport",
            "mitochondrial electron transport, nadh to ubiquinone",
            "atp synthesis coupled electron transport",

            "purine",
            "pyrimidine",
            "nucleotide",
            "purines, pyrimidines, nucleosides, and nucleotides",
            "dna metabolism",

            "response to stress",
            "cellular homeostastasis",
            //"response to chemical stimulus",
            "trehalose",
            "protein folding",
            "dna repair",

            "ribosome",
            "ribosome biogenesis",
            "translation",
            "protein fate",

            //KEGG specific
            "aerobic respiration, electron transport chain",
            "tca cycle, aerobic respiration",

            //TIGR role specific
            "energy metabolism",

            "protein synthesis",

            "regulation of transcription, dna-dependent",
            "transcription",

            "ciliary or flagellar motility",

    };

    public final static int[] functional_group_index = {
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,

            1,
            1,

            2,
            2,
            2,
            2,
            2,
            2,

            3,
            3,
            3,
            3,
            3,

            4,
            4,
            4,
            4,
            4,

            5,
            5,
            5,
            5,

            2,
            2,

            2,

            5,

            6,
            6,

            7

    };

    public final static String[] functional_groups = {
            "amino acid metabolism",
            "carbohydrate metabolism",
            "energy metabolism",
            "nucleotide metabolism",
            "stress",
            "translation",
            "transcription",
            "motility",
            "other"
    };

    /**
     * Gives sorted indices of all localization term matches
     *
     * @param go
     * @param path
     * @param tigr
     * @return
     */
    public final static int[] findLocIndicesSortAlpha(String go, String path, String tigr) {

        System.out.println("findLocIndicesSortAlpha test lengths " + go + "\t" + path + "\t" + tigr + "\t" +
                functional_terms.length + "\t" + functional_group_index.length);

        try {
            go = go.toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            path = path.toLowerCase();
        } catch (Exception e) {
            //e.printStackTrace();
        }
        try {
            tigr = tigr.toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList ar = new ArrayList();

        boolean any = false;
        //GO
        if (go != null) {
            for (int i = 0; i < functional_terms.length - 1; i++) {
                boolean match = false;
                if (go.equals(functional_terms[i]))
                    match = true;
                else if (go.startsWith(functional_terms[i] + "_"))
                    match = true;
                else if (go.endsWith("_" + functional_terms[i]))
                    match = true;
                else if (go.indexOf("_" + functional_terms[i] + "_") != -1)
                    match = true;
                if (match) {
                    ar.add(i);
                } else if (!go.equals("none")) {
                    any = true;
                    System.out.println("assignfunclass go " + go);
                }
            }
        }

        //path
        if (path != null) {
            for (int i = 0; i < functional_terms.length - 1; i++) {
                boolean match = false;
                if (path.equals(functional_terms[i]))
                    match = true;
                else if (path.startsWith(functional_terms[i] + "_"))
                    match = true;
                else if (path.endsWith("_" + functional_terms[i]))
                    match = true;
                else if (path.indexOf("_" + functional_terms[i] + "_") != -1)
                    match = true;
                if (match) {
                    ar.add(i);
                } else if (!path.equals("none")) {
                    any = true;
                    System.out.println("assignfunclass path " + path);
                }
            }
        }

        //tigr
        if (tigr != null) {
            for (int i = 0; i < functional_terms.length - 1; i++) {
                boolean match = false;
                if (tigr.equals(functional_terms[i]))
                    match = true;
                else if (tigr.startsWith(functional_terms[i] + "_"))
                    match = true;
                else if (tigr.endsWith("_" + functional_terms[i]))
                    match = true;
                else if (tigr.indexOf("_" + functional_terms[i] + "_") != -1)
                    match = true;
                if (match) {
                    ar.add(i);
                } else if (!tigr.equals("none")) {
                    any = true;
                    System.out.println("assignfunclass tigr " + tigr);
                }
            }
        }

        ArrayList groups = new ArrayList();
        if (ar.size() != 0) {
            for (int i = 0; i < ar.size(); i++) {
                int cur = (Integer) ar.get(i);
                int gindex = functional_group_index[cur];
                int index = groups.indexOf(gindex);
                if (index == -1)
                    groups.add(gindex);
            }
        } else if (any) {
            groups.add(functional_groups.length - 1);
        }


        int[] ret = MoreArray.ArrayListtoInt(groups);
        Arrays.sort(ret);
        if (ret.length > 0)
            MoreArray.printArray(ret);
        return ret;
    }

    /**
     * @param golabels
     * @return
     */
    public final static String makeGroupLabel(String golabels, String pathlabel, String tigrlabel) {
        int[] gindex = findLocIndicesSortAlpha(golabels, pathlabel, tigrlabel);
        String ret = "";
        for (int i = 0; i < gindex.length; i++) {
            ret += functional_groups[gindex[i]] + "_";
        }
        if (ret.endsWith("_"))
            ret = ret.substring(0, ret.length() - 1);

        if (ret.equals(""))
            ret = "none";
        return ret;
    }

}
