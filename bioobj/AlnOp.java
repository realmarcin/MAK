package bioobj;

/**
 * Created by Marcin
 * Date: Mar 11, 2005
 * Time: 5:38:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class AlnOp {

    /**
     * Calculates the alignment identity and coverage for aligned String aa and bb.
     *
     * @param aa
     * @param bb
     * @return
     */
    public final static int[] calcAliParams(String aa, String bb) {

        int[] ret = new int[2];

        for (int i = 0; i < aa.length(); i++) {
            char one = aa.charAt(i);
            char two = bb.charAt(i);

            boolean onegap = false;
            boolean twogap = false;
            if (one == '-' || one == '~') {
                ret[1]++;
                onegap = true;
            } else if (two == '-' || two == '~') {
                ret[1]++;
                twogap = true;
            }

            if (!onegap && !twogap) {
                if (one == two)
                    ret[0]++;
            }

        }


        double nongaplen = (double) aa.length() - (double) ret[1];
        ret[1] = (int) ((double) 100 * ((nongaplen) / (double) aa.length()));
        ret[0] = (int) ((double) 100 * ((double) ret[0] / (double) nongaplen));

        //System.out.println("identity "+ret[0]+"\tcoverage "+ret[1]);
        return ret;
    }

    /**
     * Calculates the alignment identity for aligned String aa and bb.
     *
     * @param aa
     * @param bb
     * @return
     */
    public final static int calcAliIdentity(String aa, String bb) {
        int ret = 0;

        for (int i = 0; i < aa.length(); i++) {
            char one = aa.charAt(i);
            char two = bb.charAt(i);
            if ((one != '-' && one != '~') && (two != '-' && two != '~') && one == two)
                ret++;
        }

        ret = (int) ((double) 100 * (((double) aa.length() - (double) ret) / (double) aa.length()));
        return ret;
    }

    /**
     * Calculates the alignment coverage for aligned String aa and bb.
     *
     * @param aa
     * @param bb
     * @return
     */
    public final static int calcAliCoverage(String aa, String bb) {
        int ret = 0;

        for (int i = 0; i < aa.length(); i++) {

            char one = aa.charAt(i);
            char two = bb.charAt(i);

            if (one == '-' || one == '~')
                ret++;
            else if (two == '-' || two == '~')
                ret++;
        }

        ret = (int) ((double) 100 * (((double) aa.length() - (double) ret) / (double) aa.length()));
        return ret;
    }

}
