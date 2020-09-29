package bioobj;

/**
 * Created by Marcin
 * Date: Mar 11, 2005
 * Time: 5:06:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class SeqConst {

    public final static char[] gaps = {'-', '~', '.'};

    public final static char[] bases = {'A', 'C', 'G', 'T'};
    public final static char[] complement = {'T', 'G', 'C', 'A'};

    public final static String[] polyA = {"aataaa", "attaaa", "aattaa", "aaataa", "agtaaa", "aatata", "cataaa", "taataa", "aataat"};
    public final static String[] kozak = {"CCACCATGG", "CCGCCATGG"};//" "ccaccatgg", "ccgccatgg"};
    public final static String start = "atg";
    public final static String[] stop = {"tga", "tag", "taa"};

    public final static String[][] trans =
            {{"TTT", "F"}, {"TTC", "F"}, {"TTA", "L"}, {"TTG", "L"},
             {"TCT", "S"}, {"TCC", "S"}, {"TCA", "S"}, {"TCG", "S"},
             {"TAT", "Y"}, {"TAC", "Y"}, {"TAA", "*"}, {"TAG", "*"},
             {"TGT", "C"}, {"TGC", "C"}, {"TGA", "*"}, {"TGG", "W"},
             {"CTT", "L"}, {"CTC", "L"}, {"CTA", "L"}, {"CTG", "L"},
             {"CCT", "P"}, {"CCC", "P"}, {"CCA", "P"}, {"CCG", "P"},
             {"CAT", "H"}, {"CAC", "H"}, {"CAA", "Q"}, {"CAG", "Q"},
             {"CGT", "R"}, {"CGC", "R"}, {"CGA", "R"}, {"CGG", "R"},
             {"ATT", "I"}, {"ATC", "I"}, {"ATA", "I"}, {"ATG", "M"},
             {"ACT", "T"}, {"ACC", "T"}, {"ACA", "T"}, {"ACG", "T"},
             {"AAT", "N"}, {"AAC", "N"}, {"AAA", "K"}, {"AAG", "K"},
             {"AGT", "S"}, {"AGC", "S"}, {"AGA", "R"}, {"AGG", "R"},
             {"GTT", "V"}, {"GTC", "V"}, {"GTA", "V"}, {"GTG", "V"},
             {"GCT", "A"}, {"GCC", "A"}, {"GCA", "A"}, {"GCG", "A"},
             {"GAT", "D"}, {"GAC", "D"}, {"GAA", "E"}, {"GAG", "E"},
             {"GGT", "G"}, {"GGC", "G"}, {"GGA", "G"}, {"GGG", "G"}};

    public final static String AMINO_ACIDS_STR = "ACDEFGHIKLMNPQRSTVWY";

    //public final static String aminoacidchars = "ACDEFGHIKLMNPQRSTVWY~";
    public final static char[] aminoacidchars = {'A', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'V', 'W', 'Y', '~', '-', '.'};
    public final static int aminoacidcharslen = aminoacidchars.length;
    public final static char[] aminoacids = {'A', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'V', 'W', 'Y'};
    public final static String[] aminoacidsStr = {"A", "C", "D", "E", "F", "G", "H", "I", "K", "L", "M", "N", "P", "Q", "R", "S", "T", "V", "W", "Y"};
    public final static int aminoacidslen = aminoacids.length;
    //public final static String aminoacids = "ACDEFGHIKLMNPQRSTVWY";

    public final static String[] type = {"all", "phobic", "signal", "TM"};
    public final static String[] typedata = {"ACDEFGHIKLMNPQRSTVWY", "ACFGHILMVWY", "ACFGILMVWY", "AFGILMVWY"};
    public final static String aminoacidcharsString = typedata[0] + "~";


    /**
     * @param a
     * @return
     */
    public final static int AAInd(char a) {

        for (int k = 0; k < aminoacidslen; k++) {

            if (a == aminoacids[k]) {
                return k;
            }
        }

        return -1;
    }

    /**
     * @param a
     * @return
     */
    public final static int AACharInd(char a) {

        for (int k = 0; k < aminoacidcharslen; k++) {

            if (a == aminoacidchars[k]) {
                return k;
            }
        }

        return -1;
    }

    /**
     * @param s
     * @return
     */
    public final static int AAInd(String s) {

        char test = s.charAt(0);

        for (int k = 0; k < aminoacidslen; k++) {

            if (test == aminoacids[k]) {
                return k;
            }
        }

        return -1;
    }

    /**
     * @param s
     * @return
     */
    public final static int AACharInd(String s) {

        char test = s.charAt(0);

        for (int k = 0; k < aminoacidcharslen; k++) {

            if (test == aminoacidchars[k]) {
                return k;
            }
        }

        return -1;
    }
}
