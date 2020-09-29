package bioobj;

/**
 * Created by Marcin
 * Date: Mar 11, 2005
 * Time: 6:21:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class NucSeqOp extends SeqOp {


    /**
     * Translate a DNA to protein using the human codon usage table.
     *
     * @param dna
     * @return
     */
    public final static String DNAtoProt(String dna) {

        String transprot = "";
        for (int h = 0; h + 3 < dna.length();) {
            String test = dna.substring(h, h + 3);

            for (int a = 0; a < SeqConst.trans.length; a++) {
                if (SeqConst.trans[a][0].equals(test))
                    transprot += SeqConst.trans[a][1];
            }

            h = h + 3;
        }
        return transprot;
    }

    /**
     * Returns a three frame protein translation.
     *
     * @param d
     * @return
     */
    public final static String[] get3Frames(String d) {

        String[] ret = new String[3];

        ret[0] = DNAtoProt(d);

        ret[1] = DNAtoProt(d.substring(1));

        ret[2] = DNAtoProt(d.substring(2));

        return ret;
    }

    /**
     * Performs analysis of an ORF.
     *
     * @param s
     */
    public final static void cloneAnalyze(RNA s) {

        //init();

        findKozak(s, 0);
        findStop(s);
        findPolyA(s);
        findLastStop(s);
    }

    /**
     * Performs analysis of an ORF.
     *
     * @param s
     * @param offset
     */
    public final static void cloneAnalyze(RNA s, int offset) {

        //init();

        findKozak(s, offset);
        findStop(s);
        findPolyA(s);
        findLastStop(s);
    }

    /**
     * Returns the next ORF found in this sequence.
     *
     * @param s
     * @param offset
     */
    public final static void nextOrf(RNA s, int offset) {

        findKozak(s, offset);
        findStop(s);
        if (s.transstop == 0) {
            int count = 1;
            while (s.transstop == 0) {
                findKozak(s, offset + count);
                findStop(s);
                count++;
            }
        }
    }

    /**
     * @param s
     */
    public final static void findLastStop(RNA s) {

        int max = 0;
        if (s.transstart != -1) {
            String test = s.seq.substring(s.transstart);
            for (int i = 0; i < SeqConst.stop.length; i++) {
                if (max < test.lastIndexOf(SeqConst.stop[i]))
                    max = test.lastIndexOf(SeqConst.stop[i]);
            }
            s.transstop = max;
            s.translaststop = max;
        }
    }


    /**
     * Locates the Kozak sequence based on provided input.
     *
     * @param s
     * @param off
     */
    public final static void findKozak(RNA s, int off) {

        int maxid = 0;
        String maxseq = null;
        int maxstart = 0;
        findStart(s, off);
        if (s.transstartseq.equals("null") && s.transstartseq.length() > 5)
            calcKozak(s);
        //System.out.println("now:"+kozakid+", kozakseq "+s.transstartseq);
    }

    /**
     * @param s
     */
    private final static void calcKozak(RNA s) {

        int identity1 = 0;

        for (int i = 0; i < 9; i++) {
            char a = SeqConst.kozak[0].charAt(i);
            char b = s.transstartseq.charAt(i);
            if (a == b)
                identity1++;
            else if (i == 2) {
                a = SeqConst.kozak[1].charAt(i);
                if (a == b)
                    identity1++;
            }
        }

        s.kozakseq = s.transstartseq;
    }


    /**
     * Finds the polyA tail in this seq, if any.
     *
     * @param s
     */
    public final static void findPolyA(RNA s) {

        int maxpa = -1;
        for (int i = 0; i < SeqConst.polyA.length; i++) {
            String low = SeqConst.polyA[i].toLowerCase();
            int hmm = s.seq.indexOf(low, s.transstop + 3);
            if (hmm > maxpa)
                maxpa = hmm;

            String hi = SeqConst.polyA[i].toUpperCase();
            hmm = s.seq.indexOf(hi, s.transstop + 3);
            if (hmm > maxpa)
                maxpa = hmm;
        }
        s.polyastart = maxpa;
        if (s.polyastart > 5 && s.length() > s.polyastart + 20)
            s.polyaseq = s.seq.substring(s.polyastart - 5, s.polyastart + 20);
        else if (s.polyastart > 5) {
            s.polyaseq = s.seq.substring(s.polyastart - 5, s.length());
        } else
            s.polyaseq = "null";
    }


    /**
     * Finds the start codon in String s, if any.
     *
     * @param s
     * @param off
     */
    public final static void findStart(RNA s, int off) {

        s.transstart = s.seq.indexOf(SeqConst.start, off);
        //if(s.transstart > 5)
        //	s.transstartseq = new String(s.substring(s.transstart-5, s.transstart+4));
        //else
        //{
        s.transstart = s.seq.indexOf(SeqConst.start.toUpperCase(), off);
        if (s.transstart > 5 && s.transstart + 4 < s.length())
            s.transstartseq = s.seq.substring(s.transstart - 5, s.transstart + 4);
        else
            s.transstartseq = "null";
        //}

    }

    /**
     * Finds the stop codon in String s, if any.
     *
     * @param s
     */
    public final static void findStop(RNA s) {

        int min = s.seq.length();
        int k = s.transstart + 3;
        boolean done = false;

        while (k + 3 < s.length() && !done) {
            String test = s.seq.substring(k, k + 3);
            for (int i = 0; i < SeqConst.stop.length; i++) {
                if (test.equals(SeqConst.stop[i].toUpperCase()) || test.equals(SeqConst.stop[i])) {
                    min = k;
                    done = true;
                    break;
                }
            }
            k += 3;
        }

        s.transstop = min;
        if (min < s.length())
            s.transstopseq = s.seq.substring(s.transstop, s.transstop + 3);
        else
            s.transstopseq = "null";

        //System.out.println(s.transstart+"  "+s.transstop);
    }
}
