package bioobj;

import dtype.sensii;
import mathy.Matrix;
import util.ParsePath;
import util.StringUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Class to represent a set of protein sequences, using the Protein object.
 * Version 2.0 1/1/04
 * Version 1.0 1/1/99
 * Author Marcin P. Joachimiak, Ph.D.
 */
public class ProSet extends SeqSet {

    public String[] seqName;
    public int maxlen;
    public int setsize;
    public int[][] resarray;
    public char[][] seqarray;
    public double[][] seqdists;
    public double[][] logodds;
    public int[] removed;
    public boolean load = false;

    /**
     *
     */
    public ProSet() {
        super();
    }

    /**
     * Creates a ProSet object from a single String FASTA object.
     *
     * @param seqs
     * @param name
     */
    public ProSet(String seqs, String name) {
        super();
        int i = 0;
        File test = new File(name);
        String newname = name;
        while (test.exists()) {
            newname = name + "_" + i;
            test = new File(newname);
        }
        util.TextFile.write(seqs, newname);
        readFasta(newname);
        test.delete();
    }


    /**
     * @param ps
     */
    public ProSet(ProSet ps) {
        super();
        ps.retSize();
        seqName = new String[ps.setsize];
        for (int i = 0; i < ps.setsize; i++) {

            seqName[i] = ps.seqName[i];
            Protein add = (Protein) ps.seqsein(i);
            seqs.add((Protein) add);
        }
        findSize();
        findMaxLen();
    }


    /**
     * @param al
     * @param names
     */
    public ProSet(ArrayList al, ArrayList names) {
        super();
        int diff = 0;
        int start = 0;
        if (names.size() < al.size()) {
            diff = -1;
            start = 1;
        }
//skips seq numbering
        if (((String) al.get(0)).indexOf("1") != -1)
            start = 1;
        for (int i = start; i < al.size(); i++) {
//System.out.println("seq size "+seq.size()+"\tnames size "+names.size());
//System.out.println(i+"\t"+(i+diff));
            Protein add = new Protein((String) al.get(i));
            try {
                add.name = (String) names.get(i + diff);
            } catch (Exception e) {
                add.name = "" + (Integer) names.get(i + diff);
            }
            System.out.println("ProSet " + add.seq);
            System.out.println("ProSet " + add.name);
            seqs.add((Protein) add);
        }
        findSize();
        findMaxLen();
    }


    /**
     *
     */
    public void clear() {
        seqs = null;
        seqName = null;
        ArrayList names = new ArrayList();
        maxlen = -1;
        setsize = -1;
        resarray = null;
        seqarray = null;
        seqdists = null;
        logodds = null;
        removed = null;
        load = false;
        super.clear();
    }

    /**
     * @param i
     * @return
     */
    public Protein protein(int i) {
        return (Protein) super.sequence(i);
    }


    /**
     * Returns the seq data from this ProSet in a ArrayList.
     */
    public ArrayList retSeqArrayList() {
        ArrayList r = new ArrayList();
//        Iterator it = seqs.iterator();
//            while (it.hasNext()) {
        for (int i = 0; i < seqs.size(); i++) {

//            String seq = ((Protein) it.next()).seq;
            String seq = ((Protein) seqs.get(i)).seq;
            r.add((String) seq);
        }
        if (r.size() == 0)
            r = null;
        return r;
    }

    /**
     Returns a Jevtrace style seq vector = the first seq is alignment numbering.
     */
/*
public final ArrayList retJSeqArrayList() {

ArrayList ret = retSeqArrayList();
//System.out.println("ret.size() "+ret.size());
String numb = numberAli(ret);
ret.add(0, numb);
return ret;
}
*/

    /**
     * For adding length indices to a ArrayList String seq alignment.
     */

    /*
    public final static String numberAli(ArrayList newcutseqs) {

        int maxlens = findMaxLenS(newcutseqs);

        //System.out.println("max seq len "+maxlens);

        StringBuffer numbers = new StringBuffer();
        numbers.append("1");
        int count = 1;
        int place = 0;
        String lentest = (String) newcutseqs.get(1);
        for (int i = 0; i < maxlens;) {

            if (count == 9) {

                numbers.append(place + 10);
                count = 2;
                i = i + 2;
                if (place > 100) {

                    count = 3;
                    i++;
                }
                place = place + 10;
            } else {

                numbers.append(' ');
                count++;
                i++;
            }
        }

        return (new String(numbers));
    }
    */

    /**
     * Returns the names data from this ProSet in a ArrayList.
     */
    public ArrayList retNameArrayList() {
        ArrayList r = new ArrayList();
//        Iterator it = seqs.iterator();
//            while (it.hasNext()) {
        for (int i = 0; i < seqs.size(); i++) {
//            String name = ((Protein) it.next()).name;
            String name = ((Protein) seqs.get(i)).name;
            r.add((String) name);
        }
        return r;
    }

    /**
     * Returns the seq data from this ProSet in a ArrayList.
     */
    public String[] retNameArray() {
        return seqName;
    }

    /**
     * Calculates the setsize of this ProSet.
     */
    public void findSize() {
        setsize = seqs.size();
    }

    /**
     * Calculates the setsize of this ProSet.
     */
    public int retSize() {
        setsize = seqs.size();
        return setsize;
    }

    /**
     * Adds a Protein.
     */
    public void add(Protein a) {
        if (seqs == null)
            seqs = new ArrayList();
        seqs.add((Protein) a);
        retSize();
    }

    /**
     * Inserts a Protein. If set is empty adds at 0.
     */
    public void insert(Protein a, int i) {
        if (seqs != null) {
            ArrayList copy = new ArrayList();
            copy.addAll(0, seqs);
            seqs = new ArrayList();
            seqs.add(a);
            seqs.addAll(1, copy);
            retSize();
        } else {
            seqs = new ArrayList();
            seqs.add(a);
        }
    }


    /**
     * Removes the ith Protein.
     */
    public void remove(int i) {
        seqs.remove(i);
        retSize();
    }

    /**
     * Replaces the ith Protein.
     */
    public void replace(Protein a, int i) {

        seqs.set(i, a);
    }

    /**
     * Strips gap residues from all sequences in the set.
     */
    public void stripGaps() {
        retSize();
        for (int i = 0; i < setsize; i++) {
            Protein old = (Protein)
                    seqs.get(i);
            String name = old.name;
            String seq = old.sequence();
            seq = util.StringUtil.replace(seq, "~", "");
            seq = util.StringUtil.replace(seq, "-", "");
            seq = util.StringUtil.replace(seq, ".", "");
            old = new Protein(seq);
            old.name = name;
            replace(old, i);
        }
    }

    /**
     * Converts all residues to upper case.
     */
    public void upperCase() {
        retSize();
        for (int i = 0; i < setsize; i++) {
            Protein old = (Protein)
                    seqs.get(i);
            String name = old.name;
            String seq = old.sequence();
            seq = seq.toUpperCase();
            old = new Protein(seq);
            old.name = name;
            replace(old, i);
        }

    }


    /**
     * Searches the seqseins in this set with the given motif.
     */
    public void grepMotif(String mot, int gaps) {

        mot = mot.toUpperCase();

        //int xtotal = countX(mot);

        //System.out.println("Motif: "+mot+"\tx: "+xtotal+"\tGap length < "+gaps);

        for (int i = 0; i < retSize(); i++) {
            Protein now = (Protein) seqsein(i);
            String curseq = now.sequence();

            int score = 0;
            int motind = 0;
            int lasthitmot = -1;
            int lasthit = -1;
            boolean spacer = false;

            for (int j = 0; j < curseq.length(); j++) {

                if (motind < mot.length() && (j == 0 || (spacer && j - lasthit >= (motind - lasthitmot)) || (!spacer && j - lasthit == motind - lasthitmot))) {

                    char testmot = mot.charAt(motind);
                    if (testmot != 'X') {
                        char testseq = now.seq.charAt(j);
                        if (testseq == testmot) {

                            int start = lasthit - gaps;
                            if (lasthit == 0)
                                start = j - gaps;
                            if (start < 0)
                                start = 0;
                            if (motind == mot.length() - 1) {
                                //System.out.println(now.name);
                                //System.out.println("\t" + j + "\t" + motind + "\tSCORE mot: " + mot + "\t" + now.seq.substring(start, j + 1));
                            }
                            lasthitmot = motind;
                            lasthit = j;
                            score++;
                            motind++;
                            spacer = false;
                        }
                    } else {

                        spacer = true;
                        motind++;
                    }
                } else {

                    motind = 0;
                    lasthitmot = 0;
                    lasthit = 0;
                    score = 0;
                }
            }

        }

    }

    /**
     * Counts the occurence of 'x' in the String m. For motifs...
     */
    private final static int countX(String m) {

        int ret = 0;
        for (int i = 0; i < m.length(); i++) {

            char test = m.charAt(i);
            if (test == 'X')
                ret++;
        }

        return ret;
    }

    /**
     * Copies seq information stored as String to char[][].
     */
    public final void convertSeqtoCharArray() {
        retSize();
        findMaxLen();
        //System.out.println("convertSeqtoCharArray: initializing distance array.");
        seqarray = new char[setsize][maxlen];
        //System.out.println("convertSeqtoCharArray: initialization complete.");
        for (int i = 0; i < setsize; i++) {
            Protein cur = (Protein) seqsein(i);
            String curseq = cur.sequence();
            int thismax = curseq.length();
            curseq.getChars(0, thismax, seqarray[i], 0);
        }
        //System.out.println("convertSeqtoCharArray: complete.");
    }

    /**
     * @return
     */
    public double avgNongapLen() {
        retSize();
        double total = 0;
        for (int i = 0; i < setsize; i++) {
            Protein now = (Protein) seqsein(i);
            String seq = now.sequence();
            String nongap = StringUtil.replace(seq, "~", "");
            nongap = StringUtil.replace(seq, "-", "");
            total += nongap.length();
        }
        total = total / retSize();
        return total;
    }

    /**
     * Performs the NR operation using the sequences in the file - if the sequences are lexicographically identical, the one further in the list is removed.
     */
    public void makeNRbyAlignSeq() {
        int remcount = 0;
        retSize();
        for (int i = 0; i < setsize; i++) {

            Protein now = (Protein) seqs.get(i);
            for (int j = i + 1; j < setsize; j++) {

                Protein naw = (Protein) seqs.get(j);
                if (now.sequence().equals(naw.sequence())) {
                    //System.out.println("REMOVING "+j+" "+naw.name);
                    seqs.remove(j);
                    j--;
                    remcount++;
                }
            }
        }
//System.out.println("Removed "+remcount+" sequences.");
    }

    /**
     * Performs the NR operation using the sequences in the file - if the sequences are lexicographically identical, the one further in the list is removed.
     */
    public HashMap makeNRbyAlignSeqHash() {
        int remcount = 0;
        HashMap h = new HashMap();
        for (int i = 0; i < retSize(); i++) {
            Protein now = (Protein) seqs.get(i);
            for (int j = i + 1; j < retSize(); j++) {
                Protein naw = (Protein) seqs.get(j);
                String seq_two = naw.sequence();
                if (now.sequence().equals(naw.sequence())) {
                    //System.out.println("REMOVING "+j+" "+naw.name);
                    if (h.containsKey(seq_two)) {
                        sensii curval = (sensii) h.get(seq_two);
                        curval.x++;
                        curval.s2 += "," + naw.name;
                        h.put(seq_two, curval);
                    } else {
                        sensii curval = new sensii(1, 0, seq_two, "" + now.name + "," + naw.name);
                        curval.x++;
                        //curval.s2 += "_" + j;
                        h.put(seq_two, curval);
                    }
                    now.name += "," + naw.name;
                    seqs.set(i, now);
                    now = (Protein) seqs.get(i);
                    seqs.remove(j);
                    j--;
                    remcount++;
                }
            }
        }
//System.out.println("Removed "+remcount+" sequences.");
        return h;
    }


    /**
     * Performs the NR operation using the seq names - sequences further in the list with identical names are removed.
     */
    public void makeNRbyName() {
        for (int i = 0; i < retSize(); i++) {
            Protein now = (Protein) seqs.get(i);
            for (int j = i + 1; j < retSize(); j++) {
                Protein naw = (Protein) seqs.get(j);
                if (now.name.equals(naw.name)) {
//System.out.println("REMOVING "+j+" "+naw.name);
                    seqs.remove(j);
                    j--;
                }
            }
        }
    }

    /**
     * Performs the NR operation using a pairwise distance matrix.
     * The criteria are a user provided identity cutoff. The seq further down in the list is removed.
     *
     * @param seqids
     * @param idcut
     * @param type
     * @return
     */
    public final int makeNRbyID(double[][] seqids, double idcut, String type) {
//	System.out.println("makeNRbyID with identity cutoff "+idcut);
        retSize();
        removed = new int[setsize];
        int remcount = 0;
        for (int i = 0; i < setsize; i++) {
            for (int j = i + 1; j < setsize; j++) {
                if (type.equals("greater"))
                    if (seqids[i][j] * (double) 100 > idcut) {
//System.out.println(i+"\tremoving: "+j+"\t"+seqids[i][j]+"\t"+idcut);
                        removed[j] = 10;
//remcount++;
                    }
                if (type.equals("less"))
                    if (seqids[i][j] * (double) 100 < idcut) {
//System.out.println(i+"\tremoving: "+j+"\t"+seqids[i][j]+"\t"+idcut);
                        removed[j] = 10;
//remcount++;
                    }
            }
        }
        int counter = 0;
        for (int i = 0; i < removed.length; i++) {
            Protein naw = (Protein) seqs.get(i - counter);
            if (removed[i] == 10) {
                //System.out.println("REMOVING "+(i-counter)+" "+naw.name);
                seqs.remove(i - counter);
                counter++;
            }
        }
        retSize();
//System.out.println("Removed "+counter+" sequences.");
        return counter;
    }

    //makeNRbyDiffCount

    /**
     * Performs the NR operation using a pairwise difference count matrix.
     * The criteria are a user provided identity cutoff. The seq further down in the list is removed.
     *
     * @param seqids
     * @param idcut
     * @param type
     * @return
     */
    public final int makeNRbyDiffCount(double[][] seqids, String[] names, double idcut, String type, double[] nongaplen) {
        retSize();
        removed = new int[setsize];
        int remcount = 0;
        for (int i = 0; i < setsize; i++) {
            for (int j = i + 1; j < setsize; j++) {
                if (type.equals("greater"))
                    if (seqids[i][j] > idcut) {
//System.out.println(i+"\tremoving: "+j+"\t"+seqids[i][j]+"\t"+idcut);
                        removed[j] = 10;
//remcount++;
                    }
                if (type.equals("less"))
                    if (seqids[i][j] < idcut) {
//System.out.println(i+"\tremoving: "+j+"\t"+seqids[i][j]+"\t"+idcut);
                        if (nongaplen[i] >= nongaplen[j])
                            removed[j] = 10;
                        else
                            removed[i] = 10;
                        //System.out.println(i + "\t" + names[i] + "\t" + nongaplen[i] + "\t" + j + "\t" + names[j] + "\t" + nongaplen[j] + "\t" + seqids[i][j] + "\t" + idcut);
//remcount++;
                    }
            }
        }
        int counter = 0;
        //removing seqs distabled for now
        for (int i = 0; i < removed.length; i++) {
            Protein naw = (Protein) seqs.get(i - counter);
            if (removed[i] == 10) {
                //System.out.println("REMOVING "+(i-counter)+" "+naw.name);
                seqs.remove(i - counter);
                counter++;
            }
        }
        retSize();
//System.out.println("Removed "+counter+" sequences.");
        return counter;
    }

    /**
     * Performs the NR operation assuming the sequences are aligned. The criteria are 100% identity, the longer seq is kept.
     */
    public final int makeNRbySeq() {
        int remcount = 0;
        retSize();
        for (int i = 0; i < retSize(); i++) {
            Protein now = (Protein)
                    seqs.get(i);
            for (int j = i + 1; j < retSize(); j++) {
                Protein naw = (Protein)
                        seqs.get(j);
                /* Sequence s = new
                Sequence();*/
                int id = AlnOp.calcAliIdentity(now.seq, naw.seq);
                int cover = AlnOp.calcAliCoverage(now.seq, naw.seq);
                int nogaplen1 = SeqOp.noGapLen(now.seq);
                int nogaplen2 = SeqOp.noGapLen(naw.seq);
                //System.out.println("ROUND "+i+" "+j);
                if (id == 100) {
                    if (nogaplen1 >= nogaplen2) {
                        seqs.remove(j);
                        j--;
                        remcount++;
//System.out.println("removing "+j+" "+naw.name+"\tno gap1 "+nogaplen1+"\tnogap2 "+nogaplen2+"\tid "+id+"\tcover "+cover);
                    } else if (nogaplen1 < nogaplen2) {
                        seqs.remove(i);
                        i--;
                        if (i == -1)
                            i = 0;
                        j--;
                        remcount++;
//System.out.println("removing "+i+" "+now.name+"\tnogap1 "+nogaplen1+"\tnogap2 "+nogaplen2+"\tid "+id+"\tcover "+cover);
                    }
                }
            }
        }
        retSize();
//System.out.println("Removed "+remcount+" sequences.");
        return remcount;
    }


    /**
     * Adds a ProSet to the current ProSet.
     */
    public void add(ProSet a) {

        if (seqs == null)
            seqs = new ArrayList();

        a.retSize();
        for (int i = 0; i < a.setsize; i++) {

            seqs.add((Protein) a.seqs.get(i));
        }

        retSize();
    }

    /**
     * Reads a ProSet from a Joy format file.
     */
    public void readJoy(String s) {

        if (seqs == null)
            seqs = new ArrayList();

        File test = new File(s);
        if (test.exists())
            try {
                BufferedReader in = new BufferedReader(new
                        FileReader(s));

                String data = null;
                data = in.readLine();

                //int count =0;
//int countera=0;
// String getdata = "";
                while (data != null) {
                    //String name = null;
                    if (data.indexOf("") != -1) {
                        //System.out.println("FIRST "+data);
                        Protein add = new Protein();
                        add.name = data.substring(1);
                        data = in.readLine();
                        add.name += data;
                        add.name = add.name.trim();
                        data = in.readLine();
                        while (data != null &&
                                data.indexOf("") == -1) {
                            add.seq += data;
                            //      System.out.println("adding SEQ "+data);
                            data = in.readLine();
                        }
                        add.seq = util.StringUtil.replace(add.seq, "*", "");
                        add.seq = util.StringUtil.replace(add.seq, "/", "-");
                        //System.out.println(add.toString());
                        seqs.add((Protein) add);
                    }
                    if (data == null)
                        break;
                    else if (data.indexOf("") == -1)
                        data = in.readLine();
                }
                in.close();
                findSize();
                findMaxLen();
            } catch (IOException e) {
                System.out.println("IOException: " + e.getMessage());
                e.printStackTrace();
            }
// else
// System.out.println("File "+s+" does not exists.");

    }

    /**
     * Reads a ProSet from a CE format file.
     */
    public void readCE(String s) {

        if (seqs == null)
            seqs = new ArrayList();

        File test = new File(s);
        if (test.exists())

            try {

                BufferedReader in = new BufferedReader(new
                        FileReader(s));

                String data = null;
                data = in.readLine();

                //int count =0;
                //int countera=0;
                //String getdata = "";
                while (data != null) {
                    //String name = null;
                    if (data.indexOf("(") != -1) {
                        //System.out.println("FIRST "+data);
                        Protein add = new Protein();
                        add.name = data.substring(0, data.lastIndexOf(" "));
                        add.name = add.name.trim();
                        add.seq += data.substring(data.lastIndexOf(" "));
                        data = in.readLine();
                        while (data != null &&
                                data.indexOf("(") == -1) {
                            add.seq += data;
                            //      System.out.println("adding SEQ "+data);
                            data = in.readLine();
                        }
                        add.seq = util.StringUtil.replace(add.seq, " ", "");
                        seqs.add((Protein) add);
                    }
                    if (data == null)
                        break;
                    else if (data.indexOf("(") == -1)
                        data = in.readLine();
                }
                in.close();
                findSize();
                findMaxLen();
            } catch (IOException e) {
                System.out.println("IOException: " + e.getMessage());
                e.printStackTrace();
            }
// else
// System.out.println("File "+s+" does not exists.");

    }

    /**
     * Reads a ProSet from a Fasta format file.
     */
    public void readFasta(String s) {
        if (seqs == null)
            seqs = new ArrayList();
        File test = new File(s);
        if (test.exists())
            try {
                BufferedReader in = new BufferedReader(new
                        FileReader(s));
                String data = null;
                data = in.readLine();
                while (data != null) {
                    int greatind = data.indexOf(">");
                    if (greatind == 0) {
                        //System.out.println("FIRST "+data);
                        Protein add = new Protein();
                        add.name = data.substring(1);
                        add.name = add.name.trim();
                        data = in.readLine();
                        while (data != null && data.indexOf(">") != 0) {
                            add.seq += data;
                            //	System.out.println("adding SEQ "+data);
                            data = in.readLine();
                        }
                        if (data != null) {
                            greatind = data.indexOf(">");
                        }
                        if (add != null) {
                            if (add.seq.indexOf("@") == -1 && add.name.indexOf("@") == -1)
                                seqs.add(add);
                            else
                                add = new Protein();
                        }
                    }
                    if (data == null)
                        break;
                    else
                        while (data.indexOf(">") != 0) data = in.readLine();
                }
                in.close();
//System.out.println("closed stream");
                cleanProt();
//System.out.println("cleaned");
                findSize();
//System.out.println("findsize");
                findMaxLen();
//System.out.println("maxlen");
                setseqNames();
//System.out.println("setnames");

            } catch (IOException e) {
                System.out.println("IOException: " + e.getMessage());
                e.printStackTrace();
            }
    }


    /**
     * Reads a ProSet from a Fasta format file.
     */
    public void readHMMER(String s) {
        if (seqs == null)
            seqs = new ArrayList();

        File test = new File(s);
        if (test.exists())
            try {
                BufferedReader in = new BufferedReader(new FileReader(s));
                String data = null;
                data = in.readLine();
//int count =0;
//int countera=0;
//String getdata = "";
                if (data.indexOf("hmmsearch - search a seq database with a profile HMM") == 0) {
                    while (data.indexOf("Alignments of top-scoring domains:") != 0) data = in.readLine();
                    data = in.readLine();
                }

//data = in.readLine();
                while (data != null) {
//System.out.println("START "+data);
                    String name = data;
                    data = in.readLine();
                    data = in.readLine();
                    data = in.readLine();
//System.out.println(data);
                    int dataend = Math.min(data.length(), 69);
                    String seq = data.substring(22, dataend);

                    int namend = name.indexOf(";");
                    if (namend > 10)
                        namend = 10;
                    else if (namend == -1)
                        namend = name.length();

                    String shortname = name.substring(0, namend);

                    //System.out.println("reading name " + shortname);

                    data = in.readLine();
                    data = in.readLine();
                    data = in.readLine();
                    data = in.readLine();
//System.out.println(shortname+"\t"+data);
                    while (data != null && data.indexOf(shortname) != -1 && data.indexOf(": score ") == -1) {

                        int end = data.indexOf(" ", 20);
//System.out.println("ALN loop : "+name.substring(0,10)+"\t"+data.substring(19, end));
                        seq += data.substring(19, end);

                        data = in.readLine();
                        if (data != null && data.indexOf(": score ") == -1)
                            data = in.readLine();
                        if (data != null && data.indexOf(": score ") == -1)
                            data = in.readLine();
                        if (data != null && data.indexOf(": score ") == -1)
                            data = in.readLine();

//System.out.println("\nseq "+seq);
//System.out.println("aln loop "+data);
                    }

                    seq = util.StringUtil.replace(seq, " ", "");
//name = name.substring(1);
                    Protein add = new Protein(seq);
                    add.name = name;

//System.out.println(name+"\t"+seq);

                    if (add != null) {

                        seqs.add((Protein) add);
                    }
//data = in.readLine();
                    while (data != null && data.indexOf(": score ") == -1) {

                        data = in.readLine();
                        //System.out.println("end skip data "+data);
                    }

                    if (data == null)
                        break;

//System.out.println("end data "+data);
                }

                in.close();

                cleanProt();
//System.out.println("cleaned");
                findSize();
//System.out.println("findsize");
                findMaxLen();
//System.out.println("maxlen");
                setseqNames();
//System.out.println("setnames");

            } catch (IOException e) {
                System.out.println("IOException: " + e.getMessage());
                e.printStackTrace();
            }
//else
//System.out.println("File "+s+" does not exists.");

    }


    /**
     * Returns the ith Protein from the ProSet.
     */
    public Protein seqsein(int i) {

        retSize();

        Protein ret = null;
        if (setsize > i)
            ret = new Protein((Protein)
                    seqs.get(i));
        return ret;
    }


    public void setseqName(int i, String n) {
        Protein now = (Protein) seqs.get(i);
        now.name = n;
        seqs.set(i, (Protein) now);
    }

    /**
     * Finds the maximum seq length in this ProSet.
     */
    public void findMaxLen() {
        retSize();
        maxlen = 0;
        for (int i = 0; i < setsize; i++) {
            Protein now = (Protein)
                    seqs.get(i);
            if (now.sequence().length() > maxlen)
                maxlen = now.sequence().length();
        }
    }

    /**
     * Finds the maximum seq length in this ProSet.
     */
    public final static int findMaxLenS(ArrayList ps) {

        int siz = ps.size();
        int retmaxlen = 0;
        for (int i = 0; i < siz; i++) {

            String now = (String) ps.get(i);
            int len = now.length();
            if (len > retmaxlen)
                retmaxlen = len;
        }

        return retmaxlen;
    }

    /**
     * Writes the ProSet in Clustal ALN format.
     */
    public void writeAln(String filename) {
        retSize();
        findMaxLen();

        changeGaps();

        if (filename.indexOf(".aln") != filename.length() - 4)
            filename += ".aln";

        String[] phylname = new String[setsize];

        try {
            PrintWriter out = new
                    PrintWriter(new
                    FileWriter(filename), true);

            out.println("CLUSTAL W (x.xx) multiple seq alignment (from Jevtrace)");
            out.println("");
            out.println("");

            for (int i = 0; i < setsize; i++) {
                Protein now = (Protein) seqs.get(i);
                now.seq = util.StringUtil.replace(now.seq, "~", "-");
                seqs.get(i);
                if (now.name.length() >= 30)
                    phylname[i] = now.name.substring(0, 29);
                else {
                    phylname[i] = now.name;
                    int diff = 30 - now.name.length();
                    while (diff > 1) {
                        phylname[i] += " ";
                        diff--;
                    }
                }
                int maxinit = Math.min(61, now.seq.length());
                out.println(phylname[i] + " " + now.seq.substring(0, maxinit));
//System.out.println(phylname+now.seq.substring(0,51)+"\n");
            }

            out.println("");
//out.println("");

            int j = 61;
            int len = 61;
            while (j < maxlen) {
                int test = maxlen - j;
                if (test < len)
                    len = test;
//System.out.println("j: "+j+"\tmaxlen: "+maxlen+"\tlen: "+len);
                for (int i = 0; i < retSize(); i++) {
                    Protein now = (Protein)
                            seqs.get(i);
                    out.print("\n" + phylname[i] + " " + now.seq.substring(j, j + len));
//System.out.println(now.seq.substring(j,j+len)+"\n");
                }
                if (j + len < maxlen) {

//out.println("");
                    out.println("");
                }
                j += len;
            }
            out.close();
        } catch (IOException e) {
            System.out.println("error creating or writing file " + filename);
            System.out.println("IOException: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Replace gap chars to non-Jevtrace (non-MSF).
     */

    public void changeGaps() {
        retSize();
        for (int i = 0; i < setsize; i++) {
            Protein now = (Protein) seqs.get(i);
            now.seq = util.StringUtil.replace(now.seq, "~", "-");
            seqs.set(i, (Protein) now);
        }
    }

    /**
     *
     */
    private void prepareNexus() {
        retSize();
        for (int i = 0; i < setsize; i++) {
            Protein now = (Protein) seqs.get(i);
            now.seq = util.StringUtil.replace(now.seq, "~", "-");
            now.name = util.StringUtil.replace(now.name, "/", "_");
            now.name = util.StringUtil.replace(now.name, ":", "_");
            now.name = util.StringUtil.replace(now.name, ";", "_");
            now.name = util.StringUtil.replace(now.name, "[", "_");
            now.name = util.StringUtil.replace(now.name, "]", "_");
            now.name = util.StringUtil.replace(now.name, "{", "_");
            now.name = util.StringUtil.replace(now.name, "}", "_");
            now.name = util.StringUtil.replace(now.name, "'", "_");
            now.name = util.StringUtil.replace(now.name, ",", "_");
            now.name = util.StringUtil.replace(now.name, "=", "_");
            seqs.set(i, (Protein) now);
        }
    }


    /**
     * Writes the ProSet in Phylip interleaved format.
     *
     * @param filename
     */
    public void writeNexus(String filename) {
        prepareNexus();
        retSize();
        findMaxLen();
        String[] phylname = new String[setsize];
        try {
            if (filename.indexOf(".nex") == -1)
                filename += ".nex";

            PrintWriter out = new
                    PrintWriter(new
                    FileWriter(filename), true);
            out.println("#NEXUS\n");
            out.println("begin data;");
            out.println("\tdimensions ntax=" + retSize() + " nchar=" + maxlen + ";");
//out.println("symbols=\"ABCDEFGHIKLMNPQRSTVWY\"");
            out.println("\tformat datatype=protein interleave=no gap=-;\n");
            out.println("\tmatrix\n");

            for (int i = 0; i < setsize; i++) {
                Protein now = (Protein)
                        seqs.get(i);

                if (now.name.length() >= 29)
                    phylname[i] = now.name.substring(0, 28);
                else {
                    phylname[i] = now.name;
                    int diff = 29 - now.name.length();
                    while (diff > 1) {
                        phylname[i] += " ";
                        diff--;
                    }
                }
                int maxinit = Math.min(51, now.seq.length());

                phylname[i] = util.StringUtil.replace(phylname[i], "-", "_");
                phylname[i] = util.StringUtil.replace(phylname[i], "*", "_");
                phylname[i] = util.StringUtil.replace(phylname[i], " ", "_");
                phylname[i] = util.StringUtil.replace(phylname[i], "(", "_");
                phylname[i] = util.StringUtil.replace(phylname[i], ")", "_");
                phylname[i] = util.StringUtil.replace(phylname[i], "|", "_");
                phylname[i] = util.StringUtil.replace(phylname[i], "\"", "_");
                phylname[i] = util.StringUtil.replace(phylname[i], "'", "_");

                out.println("\t" + phylname[i] + " " + now.seq);//.substring(0,maxinit));
//System.out.println(phylname+now.seq.substring(0,51)+"\n");
            }
/*
out.println("");

int j = 51;
int len = 50;
while(j < maxlen)
{
int test = maxlen - j;
if(test < len)
        len = test;
//System.out.println("j: "+j+"\tmaxlen: "+maxlen+"\tlen: "+len);
for(int i =0; i < retSize(); i++)
    {
     Protein now = (Protein)
     seqs.get(i);

     out.println(phylname[i]+" "+now.seq.substring(j,j+len));
//System.out.println(now.seq.substring(j,j+len)+"\n");
}
if(j+50 > maxlen)
        break;
else
{
j+=50;
out.println("");
}
}
*/
            out.println("\t;");
            out.println("end;");
            out.close();
        } catch (IOException e) {
            System.out.println("error creating or writing file " + filename);
            System.out.println("IOException: " + e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * Writes the ProSet in Phylip interleaved format.
     *
     * @param filename
     */
    public void writePhylip(String filename, boolean interleaved) {

        retSize();
        findMaxLen();

        changeGaps();

        String rets = "" + setsize;
        /*one added for "_" */
        int numlen = rets.length() + 1;

        int maxnamelen = maxseqNameLen();

        String[] phy = new String[setsize];

        for (int i = 0; i < setsize; i++) {

            Protein now = (Protein)
                    seqs.get(i);
            phy[i] = now.name;

            if (maxnamelen > 9) {
                String num = "_" + i;
                phy[i] = phy[i].substring(0, Math.min(9 - numlen, phy[i].length())) + num;
                phy[i] = StringUtil.padRight(phy[i], 10 - phy[i].length());
            } else {
                //int diff = 10 - now.name.length();
                phy[i] = StringUtil.padLeft(phy[i], 10 - phy[i].length());
            }
        }

        try {
            PrintWriter out = new
                    PrintWriter(new
                    FileWriter(filename), true);
            out.println(setsize + "  " + maxlen);
            if (interleaved)
                writeInterLeaved(out, phy, maxnamelen, numlen);
            else
                writeLong(out, phy, maxnamelen, numlen);
            out.close();
        } catch (IOException e) {
            System.out.println("error creating or writing file " + filename);
            System.out.println("IOException: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Writes sequences in interleaved Phylip format.
     *
     * @param pw
     * @param n
     * @param maxnamelen
     * @param numlen
     */
    private void writeInterLeaved(PrintWriter pw, String[] n, int maxnamelen, int numlen) {
        retSize();
        System.out.println("maxname: " + maxnamelen + "\tnumlen : " + numlen);
        for (int i = 0; i < setsize; i++) {
            Protein now = (Protein)
                    seqs.get(i);
            String phylname = n[i];
            int maxinit = Math.min(51, now.seq.length());
            pw.println(phylname + now.seq.substring(0, maxinit));
//System.out.println(phylname+now.seq.substring(0,51)+"\n");
        }

        pw.println("");
        int j = 51;
        int len = 50;
        while (j < maxlen) {
            int test = maxlen - j;
            if (test < len)
                len = test;
            //System.out.println("ProSet j: " + j + "\tmaxlen: " + maxlen + "\tlen: " + len + "\tsize" + retSize());
            for (int i = 0; i < setsize; i++) {
                Protein now = (Protein) seqs.get(i);
                int cut = j + len;
                if (j + len > now.seq.length())
                    cut = now.seq.length();
                pw.println(now.seq.substring(j, cut));
            }
            if (j + 50 > maxlen)
                break;
            else {
                j += 50;
                pw.print("\n");
            }
        }
    }

    /**
     * Writes sequences in non-interleaved Phylip format.
     *
     * @param pw
     * @param n
     * @param maxnamelen
     * @param numlen
     */
    private void writeLong(PrintWriter pw, String[] n, int maxnamelen, int numlen) {
        retSize();
        System.out.println("maxname: " + maxnamelen + "\tnumlen : " + numlen);
        for (int i = 0; i < setsize; i++) {
            Protein now = (Protein)
                    seqs.get(i);
            String phylname = n[i];
            //int maxinit = Math.min(51, now.seq.length());
            pw.println(phylname + now.seq);
//System.out.println(phylname+now.seq.substring(0,51)+"\n");
        }
    }

    /**
     * Position of the first non-gap residue in seq n
     */
    public int firstNonGap(int n) {

        int i;
        int ret = -1;

        if ((n < 0) || (n >= setsize))
            return -1;
        else {
            Protein pro = seqsein(n);
            String seq = pro.sequence();

            for (i = 0; i < seq.length(); i++) {
                char a = seq.charAt(i);
                if (a == '~' || a == '-') {
                    ret = i;
                    break;
                }
            }
        }

        return ret;
    }


    /**
     * What's the position of the last non-gap residue in seq n
     */
    public int lastNonGap(int n) {

        int i;
        int ret = -1;

        if ((n < 0) || (n >= setsize))
            return -1;
        else {
            Protein pro = seqsein(n);
            String seq = pro.sequence();

            for (i = seq.length() - 1; i >= 0; i--) {

                char a = seq.charAt(i);

                if (a == '~' || a == '-') {

                    ret = i;
                    break;
                }
            }
        }
        return ret;
    }

    /**
     * Output ProSet in MSF format.
     *
     * @param filename
     */
    public void writeMSF(String filename) {
        retSize();
        if (setsize > 0) {
            int i, j, k;
            setseqNames();
            findMaxLen();
            //System.out.println("setsize "+setsize+"\tmaxlen: "+maxlen);
            truncateNames(50);
            //String name = filename.substring(filename.lastIndexOf("/") + 1, filename.length());
            try {
                PrintWriter out = new
                        PrintWriter(new
                        FileWriter(filename), true);

                out.println("!!AA_MULTIPLE_ALIGNMENT\n");
                //out.println("MSF of: "+name+"\n");
                int indentPos = 0;
                // show seq names, calculate max name length
                for (i = 0; i < setsize; i++) {
                    if (seqName[i] != null) {
                        out.println(" Name: " + seqName[i] + "  Len: " + maxlen);
                        if (seqName[i].length() > indentPos)
                            indentPos = seqName[i].length();
                    }
                }
                out.println("\n//\n");
                i = 0;
                while (i < maxlen) {
                    // write segment extents
                    int lastres = Math.min(i + 50, maxlen);
                    String indent2 = "";
                    for (j = 0; j < indentPos; j++)
                        indent2 += " ";

                    String first = String.valueOf(i + 1);
                    String last = String.valueOf(lastres);

                    int pad = lastres - i - first.length() - last.length();
                    pad += ((lastres - i - 1) / 10);

                    int initpad = maxseqNameLen() + 2;
                    String inistr = "";
                    if (pad < 1)
                        pad = 1;

                    String padstr = "";
                    for (j = 0; j < pad; j++)
                        padstr += " ";
                    for (int r = 0; r < initpad; r++)
                        inistr += " ";

                    out.println(inistr + first + padstr + last);

                    // write 50-long segment of residues of each seq
                    for (j = 0; j < setsize; j++) {
                        if (seqName[j] != null) {

                            // right justify up to indentPos - 2.

                            String curseq = ((Protein) seqsein(j)).sequence();
                            String indent = "";
                            for (k = 0; k < indentPos - 2 - seqName[j].length(); k++)
                                indent += " ";

                            String subseq = "";

                            for (k = i; k < i + 50; k++) {

                                if (k < maxlen) {

                                    subseq += curseq.charAt(k);
                                    if (((k - i + 1) % 10) == 0)
                                        subseq += " ";
                                }
                            }
                            out.println(indent + seqName[j] + "  " + subseq);
                        }
                    }
                    i += 50;
                    if (i < maxlen)
                        out.println();
                }
                out.close();
            } catch (IOException e) {
                System.out.println("Error creating or writing file " + filename);
                System.out.println("IOException: " + e.getMessage());
                e.printStackTrace();
            }

        }
    }

    /**
     * Writes the ProSet to a file in Fasta format.
     *
     * @param filename
     */
    public void writeFasta(String filename) {
        writeFasta(filename, false);
    }

    /**
     * Writes the ProSet to a file in Fasta format.
     *
     * @param filename
     * @param append
     */
    public void writeFasta(String filename, boolean append) {

//replaces '~' with '-'
        if (nexus)
            prepareNexus();
        else
            changeGaps();

        retSize();

        String ext = ParsePath.getExt(filename);

        if (append)
            if (!ext.equals("fasta") && !ext.equals("fa"))
                filename += ".fasta";

        try {
            PrintWriter out = new
                    PrintWriter(new
                    FileWriter(filename), true);

            for (int i = 0; i < setsize; i++) {

                Protein now = (Protein)
                        seqs.get(i);

                out.println(">" + now.name + "\n" + now.seq);
            }
            out.close();
        } catch (IOException e) {
            System.out.println("error creating or writing file " + filename);
            System.out.println("IOException: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * @param filename
     * @param rows
     * @param columns
     */
    public void writeArrayScribe(String filename, int rows, int columns) {
//replaces '~' with '-'
/*        if (nexus)
            prepareNexus();
        else
            changeGaps();*/
        retSize();
        try {
            PrintWriter out = new
                    PrintWriter(new
                    FileWriter(filename), true);
            int col = 0, row = 0;
            for (int i = 0; i < setsize; i++) {
                Protein now = (Protein) seqs.get(i);
                String s = now.name;
                if (now.name.indexOf(",") == now.name.length() - 1)
                    s = now.name.substring(0, now.name.length() - 1);
                out.println(now.seq + "\t" + col + "\t" + row + "\t" + s);
                col++;
                if (col >= columns) {
                    col = 0;
                    row++;
                }
            }
            out.close();
        } catch (IOException e) {
            System.out.println("error creating or writing file " + filename);
            System.out.println("IOException: " + e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * Reads a ProSet from a .nex (NEXUS) format file.
     */
    public void readNexus(String in) {

        ArrayList names = new ArrayList();

        boolean leaved = false;

//if(in.indexOf(".aln") != -1)
        try {
            BufferedReader infile = new BufferedReader(new FileReader(in));
//System.out.println("STARTED READING "+in);
            String buffer = null;
            //int i;
            //String thisSeq = null;
            String thisseqName = null;
            int thisSeqNum = -1;
            int buffer_length;

            buffer = infile.readLine();

            if (buffer != null)
                buffer_length = buffer.length();
            else
                buffer_length = 0;

            //int firstSpace = -1;
            int last_space = -1;

            while (buffer != null && buffer.indexOf("begin data;") != 0 || buffer.indexOf("begin trees;") != 0) {

                buffer = infile.readLine();
            }

            buffer = infile.readLine();

            if (buffer != null) {

                while (buffer != null && buffer.indexOf("Dimensions") == -1 && buffer.indexOf("dimensions") == -1)
                    buffer = infile.readLine();

                if (buffer != null) {

                    int nchar = buffer.indexOf("nchar=") + 6;
                    int ntax = buffer.indexOf("ntax=") + 5;

                    maxlen = Integer.parseInt(buffer.substring(nchar, buffer.length() - 1));
                    setsize = Integer.parseInt(buffer.substring(ntax, nchar - 7));

                    buffer = infile.readLine();

                    if (buffer != null) {

                        while (buffer != null && buffer.indexOf("Format") == -1 && buffer.indexOf("format") == -1)
                            buffer = infile.readLine();

                        if (buffer.indexOf("interleaved=yes") != -1)
                            leaved = true;
                        else if (buffer.indexOf("interleaved=no") != -1)
                            leaved = false;

                        while (buffer != null && buffer.indexOf("Matrix") == -1 && buffer.indexOf("matrix") == -1)
                            buffer = infile.readLine();

                        buffer = infile.readLine();
                        while (buffer != null && buffer.indexOf("end;") == -1) {

                            buffer_length = buffer.length();

                            if ((buffer_length > 2) && (buffer.charAt(0) != ' ')) {

                                last_space = buffer.lastIndexOf(' ');
                                //if(last_space==-1)
                                //   last_space = buffer.length();

                                int origlast_space = last_space;

                                thisseqName = buffer.substring(1, last_space);
                                thisseqName = StringUtil.trim(thisseqName);

                                thisSeqNum = findSeqByName(thisseqName);
                                String addseq = buffer.substring(origlast_space, buffer.length());
                                addseq = util.StringUtil.replace(addseq, " ", "");

                                names.add((String) thisseqName);
                                //System.out.println("Add thisSeqNum "+thisSeqNum+"\t:"+seqName+":");

                                Protein cur = new Protein(addseq);
                                cur.name = thisseqName;
                                //System.out.println("Found for first time "+seqName);
                                seqs.add(cur);
                                //System.out.println("old "+cur.name+"\t"+cur.seq);

                            }

                            buffer = infile.readLine();

                            while (buffer != null && buffer.length() == 0) {

                                //System.out.println("SKIPPING "+buffer);
                                buffer = infile.readLine();
                            }

                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (seqs != null) {
            retSize();
            cleanProt();
            findMaxLen();
            setseqNames();
        }

    }

    /**
     Reads MEME output format generated with options -text -print_fasta
     */
/*
public final void readMEME(string f) {

try {

BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(f)));

String line = in.readLine();

while(line != null) {


while(line != null && (line.indexOf("Motif") == -1 || line.indexOf("sites sorted by position p-value") == -1))
line = in.readLine();

if(line == null)
break;

System.out.println("Found list "+line);

int moti = util.StringUtil.nextNonWhite(line,line.indexOf("Motif")+6);
int spam = line.indexOf(" ", moti);

String motif= line.substring(moti, spam);

System.out.println("motif "+motif);

line = in.readLine();
line = in.readLine();
line = in.readLine();
line = in.readLine();
//System.out.println(line);

while(line!= null && line.indexOf("---------")==-1) {
//System.out.println("LOOP "+line);

int spa1 = line.indexOf(" ");
int starti = util.StringUtil.nextNonWhite(line, spa1);
int spa2 = line.indexOf(" ", starti+1);
int starte = util.StringUtil.nextNonWhite(line, spa2);
int spa3 = line.indexOf(" ", starte+1);
int starts =util.StringUtil.nextNonWhite(line, spa3);

//System.out.println(0+"\t"+spa1);
String def = line.substring(0, spa1);
//System.out.println(starti+"\t"+spa2);
String start = line.substring(starti,spa2);
//System.out.println(starte+"\t"+spa3);
String eval = line.substring(starte,spa3);
//System.out.println(starts+"\t"+line.length());
String seq = line.substring(starts, line.length());
seq = util.StringUtil.replace(seq, " ", "");
seq = util.StringUtil.replace(seq, ".", "");

//System.out.println(def+"\n"+start+"\n"+eval+"\n"+seq+"\t"+motif);
//System.out.println(line);

//System.out.println(start+"\n"+eval);
Protein add = new Protein(seq);
String defp = def+"_"+motif+"_"+eval+"_"+start;
def = util.StringUtil.replace(def, ".", "");
add.name = defp;
seqs.add((Protein)add);
line = in.readLine();
}
}

}catch(IOException e)
{e.printStackTrace();}

System.out.println("Loaded "+seqs.size()+" seqs.");
}
*/

    /**
     Reads GCG (MSF) names from alignment.
     Removed automated addName feature in Jevtrace.
     */
    /*
		public final  ArrayList readMSFNames(string f) {

		    ArrayList cnam = new ArrayList();
			String name = null;
			name = " ";
			cnam.add(name);

			try {
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(f)));

			String line = in.readLine();

			while((line != null) && (line.indexOf("//") == -1))
				line = in.readLine();
			if(line.indexOf("1") != -1 || line.length() < 10)
				line = in.readLine();

			while(line != null) {

			StringTokenizer stok = new StringTokenizer(line);

			if(stok.hasMoreTokens()) {

			name = stok.nextToken();

//if(cnam.size() == 1)
//System.out.println("ProSet read name "+cnam.size()+"\t"+name);

				if(cnam.indexOf(name) == -1) {
					cnam.add(name);
				}
				else if(cnam.indexOf(name) != -1)
					break;

			line = in.readLine();
			while(line.length() < 10)
				line = in.readLine();
			}
			}
	    }
	    catch(IOException e)
	{e.printStackTrace();}

setseqNames(cnam);
			return cnam;
			}
            */

    /**
     Reads GCG alignment and assigns to a vector.
     The first element consists of seq numbering.
     Names of sequences are extracted and stored separately.
     */
    /*
		public final ArrayList readMSFSeqs(string f, ArrayList jevnames) {

				load = true;

				int jevnamesize = jevnames.size();
				int namemax = StringUtil.maxStringfromList(jevnames);
//System.out.println("NAMEMAX "+namemax);
// System.out.println("started reading MSF alignment.");
			ArrayList seqs = new ArrayList();
			seqs = ArrayListCopy.init(seqs, jevnamesize);
			//seqs.add(0, new StringBuffer(""));
			String name = null;

			try{

			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
			String line = in.readLine();
			String appen = null;

			while ((line != null) && (line.indexOf("//") == -1))
				line = in.readLine();
			if(line.indexOf("1") != -1 || line.length() < 10)
				line = in.readLine();
	 //System.out.println(f+"\tRecognized PILEUP .msf alignment.");

			int hold =0;
			boolean[] added = new boolean[jevnamesize];

			for(int d =0; d < jevnamesize; d++) {

				added[d] = false;
				}

			String one, two = null;

//System.out.println("first seq "+line);
			while(line != null ) {

//System.out.println(line);

				StringTokenizer stok = new StringTokenizer(line);
				name = stok.nextToken();
				int spa = line.indexOf(" ", namemax+1);

//System.out.println(name+"\t"+namemax+"\tspa "+spa+"\tline "+line.length());
				int max = namemax;
				if(spa != -1 && (spa-namemax) < 20)
					max = spa;

				appen = line.substring(max);

hold = findSeqByName(name);
//System.out.println(hold);
//System.out.println(":"+name+":");
//if(hold != -1)
//	System.out.println("Matched :"+seqName[hold]+":");

//hold--;
//if(hold == 0)
//System.out.println("modifying "+hold);

if(added[hold] == false) {
	seqs.set(hold, new StringBuffer(appen));
	added[hold] = true;
	}
else if(added[hold] == true) {

	StringBuffer add = ((StringBuffer)seqs.get(hold)).append(appen);
	seqs.set(hold, add);
	}

	line = in.readLine();
	while(line != null && line.length() < 10)
		line = in.readLine();

	if(line == null)
		break;
				}

			one = null;
			two = null;
			int si = seqs.size();

			for(int i =0; i < si; i++) {

				appen =  seqs.get(i).toString();
				appen = StringUtil.replace(appen, " ","");

				appen = StringUtil.replace(appen, ".","~");
				seqs.set(i, appen);
				}

				    } catch(IOException e) {e.printStackTrace();}

			return seqs;
		}
        */


    /**
     * Reads a ProSet from a .aln format file found in String path.
     */
    public void readAlnFromFile(String path) throws FileNotFoundException {

        try {
            BufferedReader in = new BufferedReader(new FileReader(path));
            if (in != null)
                readAln(in);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Reads a ProSet from a .aln format InputStream.
     */
    public void readAln(BufferedReader infile) {

        ArrayList names = new ArrayList();

//System.out.println("STARTED READING "+in);
        String buffer = null;
        int i;
        String thisSeq = null;
        String thisseqName = null;
        int thisSeqNum = -1;
        int buffer_length;

        try {

            // skip CLUSTAL line.
            buffer = infile.readLine();
            //System.out.println("FIRST LINE "+buffer);
            buffer = infile.readLine();
            if (buffer != null)
                while (buffer.length() == 0 || buffer.charAt(0) == ' ') {
                    //System.out.println("SKIPPING "+buffer);
                    buffer = infile.readLine();
                }

            if (buffer != null)
                buffer_length = buffer.length();
            else
                buffer_length = 0;

            //int firstSpace = -1;
            int last_space = buffer.lastIndexOf(" ");
            while (last_space == -1) {
                last_space = buffer.lastIndexOf(" ");
                buffer = infile.readLine();
            }


            while (buffer != null) {

                buffer_length = buffer.length();

                if ((buffer_length > 2) && (buffer.charAt(0) != ' ')) {

                    last_space = buffer.lastIndexOf(" ");
                    //if(last_space==-1)
                    //    last_space = buffer.length();
                    int origlast_space = last_space;

                    //if(last_space == -1) {
                    //	System.out.println("Possible problem with alignment.");
                    //break;
                    //}

                    thisseqName = buffer.substring(0, last_space);
                    thisseqName = StringUtil.trim(thisseqName);
                    thisSeqNum = findSeqByName(thisseqName);
                    String addseq = buffer.substring(origlast_space, buffer.length());
                    addseq = util.StringUtil.replace(addseq, " ", "");

                    if (thisSeqNum == -1) {

                        names.add((String) thisseqName);
                        //System.out.println("Add thisSeqNum "+thisSeqNum+"\t:"+seqName+":");

                        Protein cur = new Protein(addseq);
                        cur.name = thisseqName;
                        //System.out.println("Found for first time "+seqName);
                        seqs.add(cur);
                        //System.out.println("old "+cur.name+"\t"+cur.seq);
                    } else if (thisSeqNum != -1) {

                        Protein cur = (Protein) seqs.get(thisSeqNum);
                        cur.seq += addseq;
                        seqs.set(thisSeqNum, cur);
                    }
                }

                buffer = infile.readLine();

                while (buffer != null && (buffer.length() == 0 || buffer.charAt(0) == ' ' || buffer.indexOf(" ") == -1)) {

                    //System.out.println("SKIPPING "+buffer);
                    buffer = infile.readLine();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        if (seqs != null) {

            retSize();
            cleanProt();
            findMaxLen();
            setseqNames();
        }

    }

    /**
     * Converts the dynamically read names ArrayList to String[] seqName.
     */
    public void setseqNames() {
        retSize();
        if (setsize > 0) {
            seqName = new String[setsize];
            for (int i = 0; i < setsize; i++) {
                seqName[i] = (String) ((Protein) seqsein(i)).name;
            }
        }
    }


    public void setseqNames(ArrayList a) {

        setsize = a.size();
        seqName = new String[setsize];

        for (int i = 0; i < setsize; i++) {

            seqName[i] = (String) a.get(i);
//System.out.println("Setting seqName "+i+"\t:"+seqName[i]+":");
        }
//System.out.println("seqName len "+seqName.length);
    }

    /**
     * Cleans up the ProSet sequences.
     * Removes white spaces, new lines, tabs, replaces slashes to squiggles.
     */
    private void cleanProt() {
        retSize();
        for (int i = 0; i < setsize; i++) {
            Protein now = (Protein) seqs.get(i);
            now.seq = util.StringUtil.replace(now.seq, " ", "");
            now.seq = util.StringUtil.replace(now.seq, "\t", "");
            now.seq = util.StringUtil.replace(now.seq, "\n", "");
            now.seq = util.StringUtil.replace(now.seq, "-", "~");
            now.seq = util.StringUtil.replace(now.seq, "?", "~");
            now.seq = util.StringUtil.replace(now.seq, ".", "~");
            now.seq = now.seq.toUpperCase();
            seqs.set(i, now);
        }
    }

    /**
     * Searches the current ProSet with the given name, and
     * returns an integer indicating position of that seq in
     * ProSet.
     */
    public int findSeqByName(String name) {

        if (!load)// && setsize > 0)
            setseqNames();

//System.out.println("findSeqByName setsize "+seqName.length);
        int ret = -1;
        //if(retSize() > 0)
        if (seqName != null)
            for (int i = 0; i < seqName.length; i++) {

//System.out.println(name+":\t:"+seqName[i]+":");
                if (name.equals(seqName[i]))
                    return i;
            }
/*
	if(ret == -1) {

    Protein add = new Protein("");
    add.name = new String(name);
    seqs.add(add);
    ret = retSize()-1;
	}
	*/
        return ret;
    }

    /**
     * Removes all sequences below the NONGAPPED length cutoff k.
     */
    public int removeSeqByLen(double k) {
        int remcount = 0;
        retSize();
        for (int i = 0; i < setsize; i++) {
            Protein now = (Protein) seqsein(i);
            String nowseq = StringUtil.replace(now.sequence(), "-", "");
            nowseq = StringUtil.replace(nowseq, "~", "");
            nowseq = StringUtil.replace(nowseq, " ", "");
            int nowlen = nowseq.length();
            if (nowlen < k) {
//System.out.println("removing "+i+"\t"+now.name);
                remcount++;
                remove(i);
                i--;
            }
            //System.out.println("Removed "+remcount+" sequences.");
        }
//System.out.println("Removed "+remcount+" sequences.");
        return remcount;
    }

    /**
     * Function which counts the type and number of residues at a
     * position.
     */
    public final void countProt() {
        resarray = new int[maxlen][SeqConst.aminoacidslen];
        int norm = 0;
        int j = 0;
        while (j < setsize) {
            for (int pos = 0; pos < maxlen; pos++) {
                char ad = seqarray[j][pos];
                int ra = SeqConst.AACharInd(ad);
                if ((ra != -1) && (ra < SeqConst.aminoacidslen)) {
                    resarray[pos][ra]++;
                }
                //System.out.println("countProt "+pos+"\t"+resarray[pos][0]);
            }
            j++;
        }
        //original Jevtrace implementation
        //countGaps();
    }

    /**
     * Returns the percent seq identity for each alignment column.
     *
     * @return
     */
    public double[] getPosID() {

        retSize();
        findMaxLen();

//if(seqarray == null || resarray == null) {

        convertSeqtoCharArray();
        countProt();
//}

        double[] posids = new double[maxlen];
        for (int i = 0; i < maxlen; i++) {

            double countposid = 0;

            for (int j = 0; j < SeqConst.aminoacidslen; j++) {

                int si = resarray[i][j];

                if (si > posids[i]) {

                    posids[i] = si;
                }
            }

            posids[i] = (double) 100 * (double) (posids[i]) / (double) setsize;
            //System.out.println(i+"\t"+posids[i]);
            //if(cgap[i] < .2)
            //    countposid+=(double)100*(double)cgap[i];
        }
        return posids;
    }

    /**
     * Create the identity seq distance matrix.
     *
     * @return double[] id
     */
    public final static double[][] retIdentityMatrix() {

        double[][] id = new double[20][20];

        for (int i = 0; i < 20; i++)
            for (int j = 0; j < 20; j++) {
                if (i == j)
                    id[i][j] = (double) 1;
                else
                    id[i][j] = (double) 0;
            }
        return id;
    }

    /**
     * Calculates distance between all seq based on supplied matrix.
     *
     * @param dist
     * @return
     */
    public final double[][] calcDist(double[][] dist) {

        if (seqarray == null) {
            //System.out.println("start convertSeqtoCharArray");
            convertSeqtoCharArray();
            System.out.println("finish convertSeqtoCharArray");
        }

        retSize();
        System.out.println("calcDist: size " + setsize);
        System.out.println("calcDist: initializing distances.");
        seqdists = new double[setsize][setsize];
        System.out.println("calcDist: initializing nongaplen.");
        int[][] nongaplen = new int[setsize][setsize];

        for (int i = 0; i < setsize; i++) {
            for (int j = i; j < setsize; j++) {
                double distto = 0;
                double distfrom = 0;
                for (int x = 0; x < maxlen; x++) {
                    char one = seqarray[i][x];
                    char two = seqarray[j][x];
                    int oneind = SeqConst.AAInd(one);
                    int twoind = SeqConst.AAInd(two);
                    if (oneind != -1 && twoind != -1) {
                        distto = distto + dist[oneind][twoind];
                        distfrom = distfrom + dist[twoind][oneind];
                        nongaplen[i][j]++;
                    }
                }
                seqdists[i][j] = distto;
                seqdists[j][i] = distfrom;
            }
        }
        double[][] retdist = new double[setsize][setsize];

        for (int a = 0; a < setsize; a++) {
//System.out.println("Normalizing dists for seq "+a);
            for (int b = 0; b < setsize; b++) {
//System.out.println("Round 1 "+a+"\t"+b);
                if (nongaplen[a][b] != 0) {
                    //System.out.println(seqdists[a][b]+"   "+nongaplen[a][b]+"    "+seqdists[a][b]/nongaplen[a][b]);
                    if (seqdists[a][b] != 0)
                        retdist[a][b] = seqdists[a][b] / (double) nongaplen[a][b];
                    if (seqdists[b][a] != 0)
                        retdist[b][a] = seqdists[b][a] / (double) nongaplen[a][b];
                }
            }
        }

        return retdist;
    }

/*
Calculates a log odds 21*21 matrix, gaps are a 21st char.
@double[][] logodds
*/

    public final double[][] calcLogOdds() {
        retSize();
        if (seqarray == null)
            convertSeqtoCharArray();

//System.out.println("calcDist: initializing distances.");
        logodds = new double[SeqConst.aminoacidcharslen][SeqConst.aminoacidcharslen];
//System.out.println("calcDist: initializing nongaplen.");
        int[][] nongaplen = new int[setsize][setsize];

        int count = 0;
        for (int i = 0; i < setsize; i++) {
            for (int j = i + 1; j < setsize; j++) {
                for (int x = 0; x < maxlen; x++) {
                    char one = seqarray[i][x];
                    char two = seqarray[j][x];
                    int oneind = SeqConst.AACharInd(one);
                    int twoind = SeqConst.AACharInd(two);
                    if (oneind != -1 && twoind != -1) {
                        logodds[oneind][twoind]++;
                        logodds[twoind][oneind]++;
                        count++;
                    }
                }
            }
        }
        logodds = Matrix.norm((double) count, logodds);
        logodds = Matrix.log(logodds);
        logodds = Matrix.mult(logodds, Math.log((double) 2));

        return logodds;
    }

    /**
     * Checks the seq names for nulls, and changes null to "".
     */
    private void changeNullNames() {
        retSize();
        for (int i = 0; i < setsize; i++) {
            if (seqName[i] == null)
                seqName[i] = "";
        }
    }

    /**
     * Checks the seq names...
     * - is seqName array null?
     * - are any elements null?
     */
    private void checkNames() {
        retSize();
        if (seqName == null) {
            seqName = new String[setsize];
            setseqNames();
        }


        changeNullNames();
    }


    /**
     * Returns the length of the longest seq name.
     */
    private int maxseqNameLen() {
        int max = 0;
        retSize();
        for (int i = 0; i < setsize; i++) {
            if (seqName[i].length() > max)
                max = seqName[i].length();
        }
        return max;
    }

    /**
     * @param s
     * @param t
     * @param start
     * @return
     */
    public final static int[] createPairIndex(String s, String t, int start) {


        int size = s.length();
        int[] sTOt = new int[size - start];
        int index = 0;
        int alindex = 0;
        int sum = 0;

        for (int i = start; i < size; i++) {

            String ab = s.substring(i, i + 1);

            boolean added = false;

            if (i != start) {

                if (ab.equals("~")) {

                    sTOt[i] = sTOt[i - 1] + 1;
                    index = 0;
                    added = true;
                }
                if (!(added)) {

                    if (i == start) {

                        //System.out.println("first is a gap");
                        if (ab.equals("~")) {

                            while (ab.equals("~")) {

                                sTOt[i] = -1;
                                i++;
                                ab = s.substring(i, i + 1);
                            }

                            ab = s.substring(i, i + 1);
                            index = t.indexOf(ab, alindex);
                            sTOt[i] = index + (i - 1);
                            sum = index;
                        } else if (!ab.equals("~")) {

                            index = t.indexOf(ab, alindex);
                            sTOt[i] = index;
                            sum = index;
                        }
                    } else if (i != start) {

                        index = t.indexOf(ab, sTOt[i - 1] + 1);

                        if (index == sTOt[i - 1])
                            index = sTOt[i - 1] + 1;

                        sum = sum + index;
                        sTOt[i] = index;
                        index = index - (sTOt[i - 1]) - 1;
                    }
                }
            }
        }
        return sTOt;
    }

    /**
     * Converts the seq names from jevtrace
     * to unique names.
     */
    public final void convertToUniqueNames() {
        retSize();
        if (setsize > 0) {
            setseqNames();
            if (seqName[0].indexOf("0_") != 0)
                for (int i = 0; i < setsize; i++) {
                    seqName[i] = i + "_" + seqName[i];
                    Protein now = (Protein) seqsein(i);
                    now.name = seqName[i];
                    replace(now, i);
                }
        }
    }

    /**
     * @return
     */
    public boolean testAligned() {
        retSize();
        int max = 0;
        for (int i = 0; i < setsize; i++) {
            if (max == 0)
                max = ((Protein) seqs.get(0)).sequence().length();
            else if (max != ((Protein) seqs.get(i)).sequence().length())
                return false;
        }
        return true;
    }


    /**
     * @param A
     * @param B
     */
    public void replace(char A, char B) {

        for (int i = 0; i < seqs.size(); i++) {

            Protein now = (Protein) seqs.get(i);
            StringBuffer cur = new StringBuffer(now.sequence());
            boolean changed = false;
            for (int j = 0; j < cur.length(); j++) {

                char c = cur.charAt(j);
                if (c == 'U') {

                    cur.setCharAt(j, 'T');
                    //System.out.println("replacing U " + j);
                    changed = true;
                }
            }
            if (changed) {
                Protein add = new Protein(cur.toString());
                add.name = now.name;
                seqs.set(i, (Protein) add);
            }

        }
    }

    /**
     *
     */
    public void printFasta() {

        for (int i = 0; i < seqs.size(); i++) {

            Protein now = (Protein) seqs.get(i);
            System.out.println("> " + now.name);
            System.out.println(now.sequence());
        }
    }
}




