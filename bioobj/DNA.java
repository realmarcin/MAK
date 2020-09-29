package bioobj;


/**
 * Created by Marcin
 * Date: Mar 11, 2005
 * Time: 1:10:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class DNA extends Seq {

    public final static String bases = "ACGT";
    public final static String complement = "TGCA";


    /**
     *
     */
    public DNA() {

        super();
    }

    /**
     * @param s
     */
    public DNA(String s) {

        super(s);
    }

    /**
     * Checks if this seq is a valid DNA seq.
     *
     * @return
     */
    public boolean isType() {

        seq = seq.toUpperCase();

        for (int i = 0; i < seq.length(); i++) {

            char test = Character.toUpperCase(seq.charAt(i));

            if (bases.indexOf(test) == -1 && NucShort.code.indexOf(test) == -1)
                return false;
        }

        return true;
    }


    /**
     * Returns the complement of this seq.
     * If character is not recognized, will add to new seq as is.
     *
     * @return
     */
    public final String complement() {

        String ret = "";
        if (seq != null) {
            for (int i = 0; i < seq.length(); i++) {

                char test = Character.toUpperCase(seq.charAt(i));

                if (bases.indexOf(test) != -1)
                    ret += complement.charAt(bases.indexOf(test));
                else if (NucShort.code.indexOf(test) != -1)
                    ret += NucShort.complement.charAt(bases.indexOf(test));
                else
                    ret += test;
            }
        }
        return ret;
    }

    /**
     * Returns the complement of this seq.
     * If character is not recognized, will add to new seq as is.
     *
     * @return
     */
    public final static StringBuffer complement(StringBuffer sq) {

        StringBuffer ret = new StringBuffer("");
        if (sq != null) {
            for (int i = 0; i < sq.length(); i++) {
                char test = '!';
                test = Character.toUpperCase(sq.charAt(i));
                if (bases.indexOf(test) != -1)
                    ret.append(complement.charAt(bases.indexOf(test)));
                else if (NucShort.code.indexOf(test) != -1)
                    ret.append(NucShort.complement.charAt(NucShort.code.indexOf(test)));
                else {
                    System.out.println("Unrecognized character " + test + ", adding without complementing.");
                    ret.append(test);
                }
            }
        }
        return ret;
    }

    /**
     * @param sq
     * @return
     */
    public final static String complement(String sq) {

        StringBuffer ret = new StringBuffer("");
        if (sq != null) {
            for (int i = 0; i < sq.length(); i++) {
                char test = '!';
                test = Character.toUpperCase(sq.charAt(i));
                if (bases.indexOf(test) != -1)
                    ret.append(complement.charAt(bases.indexOf(test)));
                else if (NucShort.code.indexOf(test) != -1)
                    ret.append(NucShort.complement.charAt(NucShort.code.indexOf(test)));
                else {
                    System.out.println("Unrecognized character " + test + ", adding without complementing.");
                    ret.append(test);
                }
            }
        }
        return ret.toString();
    }

    /**
     * @param sq
     * @return
     */
    public final static char[] complement(char[] sq) {
        char[] ret = null;
        if (sq != null) {
            ret = new char[sq.length];
            for (int i = 0; i < sq.length; i++) {
                char test = '!';
                test = Character.toUpperCase(sq[i]);
                if (bases.indexOf(test) != -1)
                    ret[i] = (complement.charAt(bases.indexOf(test)));
                else if (NucShort.code.indexOf(test) != -1)
                    ret[i] = (NucShort.complement.charAt(NucShort.code.indexOf(test)));
                else {
                    System.out.println("Unrecognized character " + test + ", adding without complementing.");
                    ret[i] = (test);
                }
            }
        }
        return ret;
    }

    /**
     * Preferntially adds adds b1, but if gap than a1.
     *
     * @param a
     * @param b
     * @return
     */
    public final static String mergeSeq(String a, String b) {

        String ret = new String();
        for (int i = 0; i < a.length(); i++) {
            char a1 = a.charAt(i);
            char b1 = b.charAt(i);

            if (b1 == '-') {
                ret += a1;
            } else
                ret += b1;
        }
        return ret;
    }

    /**
     * Computes the GC content of the string.
     *
     * @param a
     * @return
     */
    public final static double GC(String a) {
        a = a.toUpperCase();
        double count = 0;
        for (int i = 0; i < a.length(); i++) {
            char c = a.charAt(i);
            if (c == 'G' || c == 'C') {
                count++;
            }
        }
        return count / (double) a.length();
    }


}
