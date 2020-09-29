package bioobj;

import java.awt.*;

public class AminoColor {

    public final static Color[] phobicphilic = {Color.orange, Color.blue};
    public final static char[] phobic = ("ACFGILMVWY").toCharArray();
    public final static char[] philic = ("DEHKNPQRST").toCharArray();
    /**
     * yellow = cys
     * pink = glycine
     * magenta = proline
     * <p/>
     * orange = phobic
     * red = negative
     * blue = positive
     * cyan = hbond donor and/or acceptor
     * <p/>
     * black = gaps
     */
    public final static Color[] twenty = {Color.orange, Color.yellow, Color.red, Color.red, Color.orange, Color.pink, Color.blue, Color.orange, Color.blue, Color.orange, Color.orange, Color.cyan, Color.magenta, Color.cyan, Color.blue, Color.cyan, Color.cyan, Color.orange, Color.orange, Color.orange};
    public final static char[] aa = ("ACDEFGHIKLMNPQRSTVWY").toCharArray();

    public final static Color[] twentyone = {Color.orange, Color.yellow, Color.red, Color.red, Color.orange, Color.pink, Color.blue, Color.orange, Color.blue, Color.orange, Color.orange, Color.cyan, Color.magenta, Color.cyan, Color.blue, Color.cyan, Color.cyan, Color.orange, Color.orange, Color.orange, Color.black, Color.black, Color.black};
    public final static char[] aaplusgaps = ("ACDEFGHIKLMNPQRSTVWY~-.").toCharArray();

    public final static Color phospho = Color.green;
    public final static char[] phosphoable = ("STY").toCharArray();


    /**
     * Returns the index of the char in the reference array.
     */
    public final static int getTwenty(char a) {

        int ret = -1;
        for (int s = 0; s < twenty.length; s++) {

            if (a == aa[s]) {

                ret = s;
                break;
            }
        }

        return ret;
    }

    /**
     * Returns the Color of the char in the reference array.
     */
    public final static Color getTwentyCol(char a) {

        Color ret = null;

        for (int s = 0; s < aa.length; s++) {

            if (a == aa[s]) {

                ret = twenty[s];
                break;
            }
        }

        return ret;
    }

    /**
     * Returns the Color of the char in the reference array.
     */
    public final static Color getTwentyOneCol(char a) {

        Color ret = null;

        for (int s = 0; s < aaplusgaps.length; s++) {

            if (a == aaplusgaps[s]) {

                ret = twentyone[s];
                break;
            }
        }

        return ret;
    }

    /**
     * Returns the Color of the char in the reference array.
     */
    public final static Color getPhosphoCol(char a) {

        Color ret = null;

        for (int s = 0; s < phosphoable.length; s++) {

            if (a == phosphoable[s]) {

                ret = phospho;
                break;
            }
        }

        return ret;
    }

}