package util;

/**
 * Created by Marcin
 * Date: Apr 1, 2005
 * Time: 4:50:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class CharTool {

    /**
     * Subtracts to char arrays using identity .
     *
     * @param a
     * @param b
     * @return
     */
    public final static char[] subtract(char[] a, char[] b) {

        if (a.length != b.length)
            return null;
        else {

            char[] ret = a;

            for (int i = 0; i < a.length; i++) {

                if (a[i] != '~')
                    if (b[i] == a[i])
                        ret[i] = '~';
            }
            return ret;
        }
    }

}
