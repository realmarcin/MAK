package bioobj;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Class to represent a set of DNAs to be acted on in a group.
 * Version 1.0, 6/17/99 - original version
 *
 * @author Marcin
 * @version 1.33, 6/17/99
 * @see DNA
 */
public class DNASet extends SeqSet {


    int setsize;
    int maxlen;

    /**
     * Create an empty DNASet set.
     */
    public DNASet() {

        super();
    }


    /**
     * Total # of bases in the set
     */
    final public long bases() {

        int total = 0;
        Iterator it = seqs.iterator();
        while (it.hasNext()) {
            //for (int i = 0; i < seqs.size(); i++) {
            total += ((DNA) it.next()).length();//get(i)).length();
        }
        return total;
    }


    /**
     * @param i
     * @return
     */
    public DNA dna(int i) {

        return (DNA) super.sequence(i);
    }

    /**
     * matches DNA to protein sequences by comparison and translation
     */
    public final static int[] matchNtoPseqs(String dna, String prot) {

        int[] seqtodna = new int[prot.length()];
        String[][] trans =
                {{"TTT", "F"}, {"TTC", "F"}, {"TTA", "L"}, {"TTG", "L"},
                        {"TCT", "S"}, {"TCC", "S"}, {"TCA", "S"}, {"TCG", "S"},
                        {"TAT", "Y"}, {"TAC", "Y"}, {"TAA", "X"}, {"TAG", "X"},
                        {"TGT", "C"}, {"TGC", "C"}, {"TGA", "X"}, {"TGG", "W"},
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


        for (int a = 0; a < prot.length(); a++)
            seqtodna[a] = -1;

        int place = 0;
        int size = 0;

        String seqP = prot;

        String seqN = dna;

        StringBuffer createprot = new StringBuffer();

        int createprotlen = 0;

        char test = seqP.charAt(place);

        int baseappen = 0;

        boolean go = true;
        while (go == true)
            for (int h = 0; h + 3 < seqN.length();) {
                if (place < seqP.length())
                    test = seqP.charAt(place);

                createprotlen = createprot.length();
                boolean first = false;

//adds gaps to translated seq if gaps in original prot
                while (test == '~') {
                    //createprot.append('~');
                    place++;
                    if (place < seqP.length())
                        test = seqP.charAt(place);
                    else if (place >= seqP.length()) {
                        go = false;
                        break;
                    }
                }

                String codon = seqN.substring(h, h + 3);

//adds gaps and shifts frame if codon has gaps
                while ((codon.indexOf("~") != -1) && (h + 3 < seqN.length())) {
                    h++;
                    codon = seqN.substring(h, h + 3);
                }

//attempts to translate current codon and match to current position in prot
//if none are found, forces break
                int last = baseappen;
                int d = -1;
                while (last == baseappen) {
                    d++;
                    if (d < trans.length) {
                        if (trans[d][1].indexOf(test) != -1) {
                            if (trans[d][0].indexOf(codon) != -1) {
                                createprot.append(trans[d][1]);
                                seqtodna[place] = h;

                                baseappen++;

                                if (baseappen == 1)
                                    first = true;
                                else
                                    first = false;
                            }
                        }
                    } else
                        last = -1;
                }

//if did not translate succesfully, increment translation frame
                if (createprot.length() == createprotlen) {

                    h++;
                    if (seqN.length() - h <= 3 && createprot.length() < seqP.length() / 2) {
                        System.out.println("could not match protein to DNA for seq  " + createprot.length() + "     " + dna);
                        go = false;
                    }

                    if (first == true) {
                        if (createprot.length() > 0) {
                            String temp = new String(createprot);
                            temp = temp.substring(0, temp.length() - 1);
                            createprot = new StringBuffer(temp);
                        }
                        first = false;
                        baseappen = 0;
                    }
                }

//translated succesfully, increment codon and AA counters
                if (createprot.length() > createprotlen) {
                    h = h + 3;
                    place++;
                }

                size = h;

                if (place >= seqP.length()) {
                    go = false;
                    break;
                }
                if (h >= seqN.length()) {
                    go = false;
                    break;
                }
            }
        return seqtodna;
    }

    /**
     * determines if given pair of codons is accesible through
     * a "random" base change (2 or 3rd position difference)
     */
    private final static int isRandom(String as, String bs) {
        String[][] delta1 = {
                {"TTT", "CTT", "ATT", "GTT", "FLIV"}, {"TTC", "CTC", "ATC", "GTC", "LLIV"},
                {"TTA", "CTA", "ATA", "GTA", "LLIV"}, {"TTG", "CTG", "ATG", "GTG", "LLMV"},
                {"TCT", "CCT", "ACT", "GCT", "SPTA"}, {"TCC", "CCC", "ACC", "GCC", "SPTA"},
                {"TCA", "CCA", "ACA", "GCA", "SPTA"}, {"TCG", "CCG", "ACG", "GCG", "SPTA"},
                {"TAT", "CAT", "AAT", "GAT", "YHND"}, {"TAC", "CAC", "AAC", "GAC", "YHND"},
                {"xxx", "CAA", "AAA", "GAA", "XQKE"}, {"xxx", "CAG", "AAG", "GAG", "XQKE"},
                {"TGT", "CGT", "AGT", "GGT", "CRSG"}, {"TGC", "CGC", "AGC", "GGC", "CRSG"},
                {"xxx", "CGA", "AGA", "GGA", "XRRG"}, {"TGG", "CGG", "AGG", "GGG", "WRRG"},
        };

        String[][] delta2 = {
                {"TTT", "TCT", "TAT", "TGT", "FSYC"}, {"TTC", "TCC", "TAC", "TGC", "FSYC"},
                {"TTA", "TCA", "xxx", "xxx", "LSXX"}, {"TTG", "TCG", "xxx", "TGG", "LSWX"},
                {"CTT", "CCT", "CAT", "CGT", "LPHR"}, {"CTC", "CCC", "CAC", "CGC", "LPHR"},
                {"CTA", "CCA", "CAA", "CGA", "LPQR"}, {"CTG", "CCG", "CAG", "CGG", "LPQR"},
                {"ATT", "ACT", "AAT", "AGT", "ITNS"}, {"ATC", "ACC", "AAC", "AGC", "ITNS"},
                {"ATA", "ACA", "AAA", "AGA", "ITKR"}, {"ATG", "ACG", "AAG", "AGG", "MTKR"},
                {"GTT", "GCT", "GAT", "GGT", "VADG"}, {"GTC", "GCC", "GAC", "GGC", "VADG"},
                {"GTA", "GCA", "GAA", "GGA", "VAEG"}, {"GTG", "GCG", "GAG", "GGG", "VAEG"}
        };

        int ret = -1;

        int[] place1 = {-1, -1};
        int[] place2 = {-1, -1};

        for (int a = 0; a < delta1.length; a++)
            for (int b = 0; b < delta1[0].length; b++) {
                if (place1[0] == -1) {

                    place1[0] = delta1[a][b].indexOf(as);
                    if (place1[0] != -1) {
                        place1[0] = a;
                        place1[1] = b;
                    }
                }
                if (place2[0] == -1) {
                    place2[0] = delta1[a][b].indexOf(bs);
                    if (place2[0] != -1) {
                        place2[0] = a;
                        place2[1] = b;
                    }
                }
            }

        if (place1[0] == place2[0]) {
            ret = 10 * place1[0] + 100 * place1[1];
        }
        if (ret == -1) {
            place1[0] = -1;
            place2[0] = -1;
            for (int a = 0; a < delta1.length; a++)
                for (int b = 0; b < delta1[0].length; b++) {
                    if (place1[0] == -1) {
                        place1[0] = delta2[a][b].indexOf(as);
                        if (place1[0] != -1) {
                            place1[0] = a;
                            place1[1] = b;
                        }
                    }
                    if (place2[0] == -1) {
                        place2[0] = delta2[a][b].indexOf(bs);
                        if (place2[0] != -1) {
                            place2[0] = a;
                            place2[1] = b;
                        }
                    }
                }
            if (place1[0] == place2[0])
                ret = 10 * place1[0] + 100 * place1[1];
        }

        if (ret != -1) {
            //System.out.println(ret+"   "+as+"   "+delta2[place1[0]][place1[1]]+"  "+delta2[place1[0]][4].charAt(place1[1])+"  "+bs+"  "+delta2[place2[0]][place2[1]]+"  "+delta2[place2[0]][4].charAt(place2[1]));
        }
        return ret;
    }


    /**
     * Reads a ProSet from a Fasta format file.
     */
    public void readFasta(String s) {

        if (seqs == null)
            seqs = new ArrayList();
        try {
            BufferedReader in = new BufferedReader(new
                    FileReader(s));

            String data = in.readLine();

            int count = 0;
            int countera = 0;
            String getdata = "";
            while (data != null) {
                String name = null;
                if (data.indexOf(">") != -1) {
//System.out.println("FIRST "+data);
                    DNA add = new DNA();
                    add.name = data.substring(1);
                    data = in.readLine();
                    while (data != null &&
                            data.indexOf(">") == -1) {
                        add.seq += data;
//	System.out.println("adding SEQ "+data);
                        data = in.readLine();
                    }
                    //System.out.println(add.toString());
                    seqs.add((DNA) add);
                }
                if (data == null)
                    break;
                else if (data.indexOf(">") == -1)
                    data = in.readLine();
            }
            in.close();
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
            e.printStackTrace();
        }

//System.out.println("Read protein set of setsize"+setsize);

        findSize();
        findMaxLen();
    }


    /**
     * Returns the complement of this set.
     *
     * @return
     */
    public Object complementSet() {

        findSize();
        DNASet ret = new DNASet();
        for (int i = 0; i < setsize; i++) {

            DNA old = (DNA) seqs.get(i);
            DNA now = new DNA(old.complement());
            now.name = old.name;
            int go = now.name.indexOf("; Antisense;");
            if (go == -1)
                go = now.name.lastIndexOf(";");

            now.name = now.name.substring(0, go);
            now.name += "; Sense;";
            ret.seqs.add(now);

        }
        return ret;
    }


}
