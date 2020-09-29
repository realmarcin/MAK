package bioobj;

import dtype.StringPair;
import util.ArrayListCopy;
import util.ParsePath;
import util.StringUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Parents class with variables and methods for sets of character based sequences.
 */
public class SeqSet {


    public ArrayList seqs;
    public String[] seqName;
    public int setsize;
    public int maxlen;
    public ArrayList names;
    public boolean eff = false;
    public boolean nexus;
    public boolean load = false;
    public boolean nuc = false;

    /**
     *
     */
    public SeqSet() {

        seqs = new ArrayList();
    }


    public void clear() {

        seqs = new ArrayList();
        seqName = null;
        setsize = -1;
        maxlen = -1;
        names = new ArrayList();
        eff = false;
        nexus = false;
        load = false;
        nuc = false;

    }

    /**
     * Calculates the setsize of this ProSet.
     */
    public int retSize() {

        setsize = seqs.size();
        return setsize;
    }

    /**
     * Removes the ith seq.
     */
    public void remove(int i) {
        seqs.remove(i);
        retSize();
    }

    /**
     * Finds the maximum seq length in this ProSet.
     */
    public void findMaxLen() {

        retSize();
        maxlen = 0;
        for (int i = 0; i < retSize(); i++) {

            Seq now = (Seq) seqs.get(i);
            int cur = now.seq.length();

            if (cur > maxlen)
                maxlen = cur;
        }

    }

    /**
     * @param i
     * @return
     */
    public Object seq(int i) {

        return seqs.get(i);
    }

    /**
     * @param i
     * @return
     */
    public Object sequence(int i) {

        return seqs.get(i);
    }

    /**
     * Truncates the names of the sequences at value c.
     *
     * @param c cut position
     */
    public void truncateNames(int c) {

        checkNames();

        for (int i = 0; i < retSize(); i++) {
            Object pro = seq(i);
            String name = ((Seq) pro).name;
            if (name.length() > c) {
                name = name.substring(0, c);
                ((Seq) pro).name = name;
                seqs.set(i, pro);
                seqName[i] = name;
            }
        }
    }

    /**
     * @param c
     */
    public void truncateNames(String c) {

        checkNames();

        for (int i = 0; i < retSize(); i++) {
            Object pro = seq(i);
            String name = ((Seq) pro).name;
            int index = name.indexOf(" ");
            if (index != -1) {
                name = name.substring(0, index);
                //System.out.println("truncateNames " + name);
                ((Seq) pro).name = name;
                seqs.set(i, pro);
                seqName[i] = name;
            }
        }
    }

    /**
     * Checks the seq names...
     * - is seqName array null?
     * - are any elements null?
     */
    private void checkNames() {

        if (seqName == null) {
            seqName = new String[retSize()];
            setseqNames();
        }


        changeNullNames();
    }


    /**
     * Checks the seq names for nulls, and changes null to new String("").
     */
    private void changeNullNames() {

        for (int i = 0; i < retSize(); i++) {

            if (seqName[i] == null)
                seqName[i] = "";

        }

    }

    /**
     * Returns the length of the longest seq name.
     */
    private int maxseqNameLen() {
        int max = 0;
        for (int i = 0; i < retSize(); i++) {

            if (seqName[i].length() > max)
                max = seqName[i].length();
        }
        return max;
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

        return ret;
    }

    /**
     * Cleans up the ProSet sequences.
     * Removes white spaces, new lines, tabs, replaces slashes to squiggles.
     */
    public void cleanSeq() {
        retSize();
        for (int i = 0; i < retSize(); i++) {
            Object now = seqs.get(i);
            ((Seq) now).seq = util.StringUtil.replace(((Seq) now).seq, " ", "");
            ((Seq) now).seq = util.StringUtil.replace(((Seq) now).seq, "\t", "");
            ((Seq) now).seq = util.StringUtil.replace(((Seq) now).seq, "\n", "");
            ((Seq) now).seq = util.StringUtil.replace(((Seq) now).seq, "-", "~");
            ((Seq) now).seq = util.StringUtil.replace(((Seq) now).seq, "?", "~");
            ((Seq) now).seq = util.StringUtil.replace(((Seq) now).seq, ".", "~");
            ((Seq) now).seq = util.StringUtil.replace(((Seq) now).seq, "*", "~");
            ((Seq) now).seq = ((Seq) now).seq.toUpperCase();
            seqs.set(i, now);
        }
    }


    /**
     * Writes the ProSet to a file in Fasta format.
     */
    public void writeFasta(String filename, boolean append) {

        //replaces '~' with '-'
        if (nexus)
            prepareNexus();

        retSize();

        String ext = ParsePath.getExt(filename);

        if (append)
            if (!ext.equals("fasta") && !ext.equals("fa"))
                filename += ".fasta";

        try {
            PrintWriter out = new PrintWriter(new FileWriter(filename), true);
            for (int i = 0; i < retSize(); i++) {

                Seq now = (Seq) seqs.get(i);
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
     *
     */
    private void prepareNexus() {
        for (int i = 0; i < retSize(); i++) {
            Object now = seqs.get(i);
            ((Seq) now).seq = util.StringUtil.replace(((Seq) now).seq, "~", "-");
            ((Seq) now).name = util.StringUtil.replace(((Seq) now).name, "/", "_");
            ((Seq) now).name = util.StringUtil.replace(((Seq) now).name, ":", "_");
            ((Seq) now).name = util.StringUtil.replace(((Seq) now).name, ";", "_");
            ((Seq) now).name = util.StringUtil.replace(((Seq) now).name, "[", "_");
            ((Seq) now).name = util.StringUtil.replace(((Seq) now).name, "]", "_");
            ((Seq) now).name = util.StringUtil.replace(((Seq) now).name, "{", "_");
            ((Seq) now).name = util.StringUtil.replace(((Seq) now).name, "}", "_");
            ((Seq) now).name = util.StringUtil.replace(((Seq) now).name, "'", "_");
            ((Seq) now).name = util.StringUtil.replace(((Seq) now).name, ",", "_");
            ((Seq) now).name = util.StringUtil.replace(((Seq) now).name, "=", "_");
            seqs.set(i, now);
        }
    }

    /**
     *
     */
    private void preparePhylip() {
        for (int i = 0; i < retSize(); i++) {
            Seq now = (Seq) seqs.get(i);
            ((Seq) now).seq = util.StringUtil.replace(((Seq) now).seq, "~", "-");
            seqs.set(i, now);
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

        String[] phylname = new String[retSize()];

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

            for (int i = 0; i < retSize(); i++) {

                Seq now = (Seq) seqs.get(i);

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
    public void writePhylip(String filename) {

        retSize();
        findMaxLen();

        preparePhylip();

        try {
            PrintWriter out = new PrintWriter(new FileWriter(filename), true);

            out.println(" " + retSize() + "  " + maxlen);

            for (int i = 0; i < retSize(); i++) {

                Seq now = (Seq) seqs.get(i);

                String phylname = null;
                if (now.name.length() >= 10)
                    phylname = now.name.substring(0, 10);
                else {

                    phylname = now.name;
                    int diff = 10 - now.name.length();
                    while (diff > 0) {

                        phylname += " ";
                        diff--;
                    }
                }
                int maxinit = Math.min(51, now.seq.length());

                out.println(phylname + (now.seq.substring(0, maxinit)));
//System.out.println(phylname+now.seq.substring(0,51)+"\n");
            }

            out.println("");

            int j = 51;
            int len = 50;
            while (j < maxlen) {

                int test = maxlen - j;

                if (test < len)
                    len = test;
//System.out.println("j: "+j+"\tmaxlen: "+maxlen+"\tlen: "+len);
                for (int i = 0; i < retSize(); i++) {

                    Seq now = (Seq) seqs.get(i);

                    int cut = j + len;

                    if (j + len > now.seq.length())
                        cut = now.seq.length();

                    out.println(now.seq.substring(j, cut));

                    out.println(now.seq.substring(j, j + len));
//System.out.println(now.seq.substring(j,j+len)+"\n");
                }
                if (j + 50 > maxlen)
                    break;
                else {
                    j += 50;
                    out.println("");
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
     *
     * @param in
     */
    public void readNexus(String in) {

        names = new ArrayList();

        boolean leaved = false;

//if(in.indexOf(".aln") != -1)
        try {
            BufferedReader infile = new BufferedReader(new FileReader(in));
//System.out.println("STARTED READING "+in);
            String buffer = null;
            int i;
            String thisSeq = null;
            String thisseqName = null;
            int thisSeqNum = -1;
            int bufferlength;

            buffer = infile.readLine();

            if (buffer != null)
                bufferlength = buffer.length();
            else
                bufferlength = 0;

            int firstSpace = -1;
            int lastSpace = -1;

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

                            bufferlength = buffer.length();

                            if ((bufferlength > 2) && (buffer.charAt(0) != ' ')) {

                                lastSpace = buffer.lastIndexOf(' ');
                                int origlastSpace = lastSpace;

                                thisseqName = buffer.substring(1, lastSpace);
                                thisseqName = StringUtil.trim(thisseqName);

                                thisSeqNum = findSeqByName(thisseqName);
                                String addseq = buffer.substring(origlastSpace, buffer.length());
                                addseq = util.StringUtil.replace(addseq, " ", "");

                                names.add((String) thisseqName);
                                //System.out.println("Add thisSeqNum "+thisSeqNum+"\t:"+seqName+":");

                                Seq cur = new Seq(addseq);
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
            cleanSeq();
            findMaxLen();
            setseqNames();
        }

    }

    /**
     * Output ProSet in MSF format.
     *
     * @param filename new file path
     */
    public void writeMSF(String filename) {

        if (retSize() > 0) {

            int i, j, k;
            setseqNames();
            findMaxLen();

            //System.out.println("setsize "+setsize+"\tmaxlen: "+maxlen);
            truncateNames(50);

            String name = filename.substring(filename.lastIndexOf("/") + 1, filename.length());

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
                    for (j = 0; j < retSize(); j++) {
                        if (seqName[j] != null) {

                            // right justify up to indentPos - 2.

                            String curseq = ((Seq) seqs.get(j)).sequence();
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
     * Reads MEME output format generated with options -text -print_fasta
     *
     * @param f
     */
    public final void readMEME(String f) {

        try {

            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(f)));

            String line = in.readLine();

            while (line != null) {


                while (line != null && (line.indexOf("Motif") == -1 || line.indexOf("sites sorted by position p-value") == -1))
                    line = in.readLine();

                if (line == null)
                    break;

                System.out.println("Found list " + line);

                int moti = util.StringUtil.nextNonWhite(line, line.indexOf("Motif") + 6);
                int spam = line.indexOf(" ", moti);

                String motif = line.substring(moti, spam);

                System.out.println("motif " + motif);

                line = in.readLine();
                line = in.readLine();
                line = in.readLine();
                line = in.readLine();
//System.out.println(line);

                while (line != null && line.indexOf("---------") == -1) {
//System.out.println("LOOP "+line);

                    int spa1 = line.indexOf(" ");
                    int starti = util.StringUtil.nextNonWhite(line, spa1);
                    int spa2 = line.indexOf(" ", starti + 1);
                    int starte = util.StringUtil.nextNonWhite(line, spa2);
                    int spa3 = line.indexOf(" ", starte + 1);
                    int starts = util.StringUtil.nextNonWhite(line, spa3);

//System.out.println(0+"\t"+spa1);
                    String def = line.substring(0, spa1);
//System.out.println(starti+"\t"+spa2);
                    String start = line.substring(starti, spa2);
//System.out.println(starte+"\t"+spa3);
                    String eval = line.substring(starte, spa3);
//System.out.println(starts+"\t"+line.length());
                    String seq = line.substring(starts, line.length());
                    seq = util.StringUtil.replace(seq, " ", "");
                    seq = util.StringUtil.replace(seq, ".", "");

//System.out.println(def+"\n"+start+"\n"+eval+"\n"+seq+"\t"+motif);
//System.out.println(line);

//System.out.println(start+"\n"+eval);
                    Seq add = new Seq(seq);
                    String defp = def + "_" + motif + "_" + eval + "_" + start;
                    def = util.StringUtil.replace(def, ".", "");
                    add.name = defp;
                    seqs.add((Seq) add);
                    line = in.readLine();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Loaded " + seqs.size() + " seqs.");
    }

    /**
     * Reads GCG (MSF) names from alignment.
     * Removed automated addName feature in Jevtrace.
     *
     * @param f
     * @return
     */
    public final ArrayList readMSFNames(String f) {
        ArrayList cnam = new ArrayList();
        String name = " ";
        cnam.add(" ");
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
            String line = in.readLine();
            while ((line != null) && (line.indexOf("//") == -1)) {
                //System.out.println("readMSFNames skip 1 " + line);
                line = in.readLine();
            }
            while (line.length() < 10) {
                //System.out.println("readMSFNames skip 2  " + line);
                line = in.readLine();
            }
            while (line.indexOf(" 1 ") != -1 || line.indexOf(" 50 ") != -1) {
                //System.out.println("readMSFNames skip 3 " + line);
                line = in.readLine();
            }
            line = in.readLine();
            //System.out.println("readMSFNames " + line);
            while (line != null) {
                StringTokenizer stok = new StringTokenizer(line);
                if (stok.hasMoreTokens()) {
                    name = stok.nextToken();
                    //if(cnam.size() == 1)
                    //System.out.println("ProSet read name " + cnam.size() + "\t" + name);
                    if (cnam.indexOf(name) == -1) {
                        //System.out.println("ProSet read name " + cnam.size() + "\t" + name);
                        cnam.add(name);
                    } else if (cnam.indexOf(name) != -1)
                        break;
                    line = in.readLine();

                    //int test = -1;
                    String s = StringUtil.replace(line, " ", "");
                    int slen = s.length();
                    /*try {
                        test = Integer.parseInt(s);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }*/
                    while (line.length() < 10 || slen < 10) {
                        line = in.readLine();
                        s = StringUtil.replace(line, " ", "");
                        slen = s.length();
                        /* try {
                            test = Integer.parseInt(StringUtil.replace(line, " ",""));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }*/
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        setseqNames(cnam);
        //System.out.println("a/f setseqNames");
        return cnam;
    }

    /**

     */

    /**
     * Reads GCG alignment and assigns to a vector.
     * The first element consists of seq numbering.
     * Names of sequences are extracted and stored separately.
     *
     * @param f
     * @param jevnames
     * @return
     */
    public final ArrayList readMSFSeqs(String f, ArrayList jevnames) {
        load = true;
        int jevnamesize = jevnames.size();
        int namemax = StringUtil.maxStringfromList(jevnames);
//System.out.println("NAMEMAX "+namemax);
// System.out.println("started reading MSF alignment.");
        ArrayList seqs = new ArrayList();
        seqs = ArrayListCopy.init(seqs, jevnamesize);
        //seqs.add(0, new StringBuffer(""));
        String name = null;
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
            String line = in.readLine();
            String appen = null;
            while ((line != null) && (line.indexOf("//") == -1)) {
                //System.out.println("readMSFSeqs skip 1 " + line);
                line = in.readLine();
            }
            while (line.length() < 10) {
                //System.out.println("readMSFSeqs skip 2  " + line);
                line = in.readLine();
            }
            while (line.indexOf(" 1 ") != -1 || line.indexOf(" 50 ") != -1) {
                //System.out.println("readMSFSeqs skip 3 " + line);
                line = in.readLine();
            }
            line = in.readLine();
            int hold = 0;
            boolean[] added = new boolean[jevnamesize];
            for (int d = 0; d < jevnamesize; d++) {
                added[d] = false;
            }
            String one, two = null;
//System.out.println("first seq "+line);
            while (line != null) {
//System.out.println(line);
                String s = StringUtil.replace(line, " ", "");
                int slen = s.length();
                while (slen < 10 || line.length() < 10) {
                    line = in.readLine();
                }
                StringTokenizer stok = new StringTokenizer(line);
                name = stok.nextToken();
                int spa = line.indexOf(" ", namemax + 1);
//System.out.println(name+"\t"+namemax+"\tspa "+spa+"\tline "+line.length());
                int max = namemax;
                if (spa != -1 && (spa - namemax) < 20)
                    max = spa;
                appen = line.substring(max);

                hold = findSeqByName(name);
                //System.out.println(hold);
                //System.out.println(":" + name + ":");
//if(hold != -1)
//	System.out.println("Matched :"+seqName[hold]+":");
//hold--;
//if(hold == 0)
//System.out.println("modifying "+hold);
                //System.out.println("readMSFSeqs " + name + "\t" + hold + "\t" + appen);
                appen = StringUtil.replace(appen, "-", "~");
                if (added[hold] == false) {
                    seqs.set(hold, new StringBuffer(appen));
                    added[hold] = true;
                } else if (added[hold] == true) {
                    StringBuffer add = ((StringBuffer) seqs.get(hold)).append(appen);
                    seqs.set(hold, add);
                }
                line = in.readLine();
                s = StringUtil.replace(line, " ", "");
                slen = s.length();
                while (line != null && (slen < 10 || line.length() < 10)) {
                    line = in.readLine();
                    if (line != null) {
                        s = StringUtil.replace(line, " ", "");
                        slen = s.length();
                    }
                }
                if (line == null)
                    break;
            }
            one = null;
            two = null;
            int si = seqs.size();
            for (int i = 0; i < si; i++) {
                appen = seqs.get(i).toString();
                appen = StringUtil.replace(appen, " ", "");
                appen = StringUtil.replace(appen, ".", "-");
                appen = StringUtil.replace(appen, "-", "~");
                seqs.set(i, appen);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return seqs;
    }

    /**
     * Reads a ProSet from a .aln format file found in String path.
     *
     * @param path
     * @throws FileNotFoundException
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
     *
     * @param infile
     */
    public void readAln(BufferedReader infile) {

        names = new ArrayList();

//System.out.println("STARTED READING "+in);
        String buffer = null;
        int i;
        String thisSeq = null;
        String thisseqName = null;
        int thisSeqNum = -1;
        int bufferlength;

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
                bufferlength = buffer.length();
            else
                bufferlength = 0;

            int firstSpace = -1;
            int lastSpace = -1;

            while (buffer != null) {

                bufferlength = buffer.length();

                if ((bufferlength > 2) && (buffer.charAt(0) != ' ')) {

                    lastSpace = buffer.lastIndexOf(' ');
                    int origlastSpace = lastSpace;

                    //if(lastSpace == -1) {
                    //	System.out.println("Possible problem with alignment.");
                    //break;
                    //}

                    thisseqName = buffer.substring(0, lastSpace);
                    thisseqName = StringUtil.trim(thisseqName);
                    thisSeqNum = findSeqByName(thisseqName);
                    String addseq = buffer.substring(origlastSpace, buffer.length());
                    addseq = util.StringUtil.replace(addseq, " ", "");

                    if (thisSeqNum == -1) {

                        names.add((String) thisseqName);
                        //System.out.println("Add thisSeqNum "+thisSeqNum+"\t:"+seqName+":");

                        Seq cur = new Seq(addseq);
                        cur.name = thisseqName;
                        //System.out.println("Found for first time "+seqName);
                        seqs.add((Seq) cur);
                        //System.out.println("old "+cur.name+"\t"+cur.seq);
                    } else if (thisSeqNum != -1) {

                        Seq cur = (Seq) seqs.get(thisSeqNum);
                        cur.seq += addseq;
                        seqs.set(thisSeqNum, (Seq) cur);
                    }
                }

                buffer = infile.readLine();

                while (buffer != null && (buffer.length() == 0 || buffer.charAt(0) == ' ')) {

                    //System.out.println("SKIPPING "+buffer);
                    buffer = infile.readLine();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        if (seqs != null) {
            retSize();
            cleanSeq();
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

                seqName[i] = (String) ((Seq) seqs.get(i)).name;
            }
        }
    }

    /**
     * @param a
     */
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
     * Returns the seq data from this ProSet in a ArrayList.
     */
    public ArrayList retSeqArrayList() {

        ArrayList r = new ArrayList();

//        Iterator it = seqs.iterator();
//            while (it.hasNext()) {
        for (int i = 0; i < seqs.size(); i++) {

//            String seq = ((Seq) it.next()).seq;
            String seq = ((Seq) seqs.get(i)).seq;
            r.add((String) seq);
        }
        if (r.size() == 0)
            r = null;
        return r;
    }


    /**
     * Returns a Jevtrace style seq vector = the first seq is alignment numbering.
     */
    public final ArrayList retJSeqArrayList() {

        ArrayList ret = retSeqArrayList();
//System.out.println("ret.size() "+ret.size());
        String numb = numberAli(ret);
        ret.add(0, numb);
        return ret;
    }

    /**
     * For adding length indices to a ArrayList String seq alignment.
     *
     * @param newcutseqs
     * @return
     */
    public final static String numberAli(ArrayList newcutseqs) {

        StringBuffer numbers = new StringBuffer();
        numbers.append("1");
        int count = 1;
        int place = 0;
        String lentest = (String) newcutseqs.get(1);
        int len = lentest.length();
        for (int i = 0; i < len;) {

            if (numbers.length() >= len)
                break;
            //System.out.println(i+"\t"+count+"\t"+place);
            if (count == 9) {

                numbers.append(place + 10);
                count = 1;
                i++;

                if (place >= 90) {
                    count = 2;
                    i++;
                }

                place = place + 10;
            } else {

                numbers.append(".");
                count++;
                i++;
            }
        }

        return (new String(numbers));
    }


    /**
     * Returns the names data from this ProSet in a ArrayList.
     */
    public ArrayList retNameArrayList() {

        ArrayList r = new ArrayList();

        for (int i = 0; i < seqs.size(); i++) {

            String name = ((Seq) seqs.get(i)).name;
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
     * Returns the seq data from this ProSet in a ArrayList.
     */
    public char[][] retNameChar() {

        char[][] ret = new char[seqs.size()][];

        for (int i = 0; i < seqs.size(); i++) {
            String name = ((Seq) seqs.get(i)).name;
            ret[i] = new char[name.length()];
            name.getChars(0, name.length(), ret[i], 0);

        }
        return ret;
    }

    /**
     * Returns the seq data from this ProSet in a ArrayList.
     */
    public char[][] retSeqChar() {

        char[][] ret = new char[seqs.size()][];

        for (int i = 0; i < seqs.size(); i++) {

            Protein cur = (Protein) seqs.get(i);
            String s = cur.sequence();
            ret[i] = new char[s.length()];
            s.getChars(0, s.length(), ret[i], 0);

        }
        return ret;
    }

    /**
     * Calculates the setsize of this ProSet.
     */
    public void findSize() {

        setsize = seqs.size();
    }

    /**
     * Strips gap residues from all sequences in the set.
     */
    public void stripGaps() {
        for (int i = 0; i < retSize(); i++) {
            Object old = seqs.get(i);
            String name = ((Seq) old).name;
            String seq = ((Seq) old).sequence();
            seq = util.StringUtil.replace(seq, "~", "");
            seq = util.StringUtil.replace(seq, "-", "");
            seq = util.StringUtil.replace(seq, ".", "");
            ((Seq) old).seq = seq;
            ((Seq) old).name = name;
            replace(old, i);
        }
    }

    /**
     * Converts all residues to upper case.
     */
    public void upperCase() {
        for (int i = 0; i < retSize(); i++) {
            Object old = seqs.get(i);
            ((Seq) old).seq = ((Seq) old).seq.toUpperCase();
            replace(old, i);
        }
    }

    /**
     * Replaces the ith Seq.
     *
     * @param a
     * @param i
     */
    public void replace(Object a, int i) {
        seqs.set(i, a);
    }

    /**
     * @param a
     */
    public void add(Object a) {
        seqs.add(a);
    }

    /**
     * @param infile
     * @return
     */
    public final static boolean isFasta(String infile) {
        ParsePath pp = new ParsePath(infile);
        String ext = pp.getExt();
        if (ext.equals("fasta") || ext.equals("fa") || ext.equals("faa")) {
            return true;
        }
        return false;
    }


    /**
     * @param a
     * @return
     */
    public final static SeqSet StringPairtoSeqSet(ArrayList a) {
        SeqSet ss = new SeqSet();
        for (int i = 0; i < a.size(); i++) {
            StringPair cur = (StringPair) a.get(i);
            DNA d = new DNA(cur.b);
            d.name = cur.x + " " + cur.y + " " + (cur.z == 0 ? "+" : "_") + "\t" + cur.a;
            ss.add(d);
        }
        return ss;
    }

    /**
     * Reads a ProSet from a Fasta format file.
     *
     * @param s
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
                cleanSeq();
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

}
