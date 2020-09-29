package bioobj;

/**
 * Created by Marcin
 * Date: Mar 11, 2005
 * Time: 5:09:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class SeqOp {


    /**
     * Masks a sequence with 'x' between the specified coordinates.
     *
     * @param str
     * @param from
     * @param to
     * @return
     */
    public final static String maskSeq(String str, int from, int to) {

        String ret = str;

        for (int i = from; i < to; i++) {

            ret = ret.substring(0, i) + "x" + ret.substring(i + 1);
            //System.out.println(i + "  " + ret);
        }

        return ret;
    }


    /**
     * Combines two Strings with specified gaps in betwen.
     *
     * @param a
     * @param gaps
     * @param b
     * @return
     */
    public final static String combineWithGaps(String a, long gaps, String b) {

        int i = 0;
        while (i < gaps) {
            a += "-";
            i++;
        }

        a += b;
        return a;
    }

    /**
     * Reports index of first non-gap character.
     *
     * @param b
     * @return
     */
    public final static int firstNonGap(String b) {

        for (int i = 0; i < b.length(); i++) {
            for (int j = 0; j < SeqConst.gaps.length; j++)
                if (b.charAt(i) != SeqConst.gaps[j])
                    return i;
        }
        return -1;
    }

    /**
     * Reports the non-gap length of a seq
     *
     * @param a
     * @return
     */
    public final static int noGapLen(String a) {
        int gapped = 0;
        for (int i = 0; i < a.length(); i++) {
            char one = a.charAt(i);
            if (one == '~' || one == '-')
                gapped++;
        }
        int ret = a.length() - gapped;
        return ret;
    }


    /**
     * How many non-gap bases do we have?
     */
    public final static int nonGapSeq(String seq) {

        if (seq == null) return -1;

        int rv = 0;
        for (int i = 0; i < seq.length(); i++) {
            for (int j = 0; j < SeqConst.gaps.length; j++) {

                if (SeqConst.gaps[j] == seq.charAt(j))
                    rv++;
            }
        }

        return rv;
    }

    /**
     * Translate a protein to DNA using the human codon usage table.
     *
     * @param seq
     * @return
     */
    public final static String reverse(String seq) {

        String rev = "";
        for (int h = seq.length() - 1; h > -1; h--) {

            char test = seq.charAt(h);
            rev += test;
        }
        return rev;
    }


}
