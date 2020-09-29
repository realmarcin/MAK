package mathy;

import java.util.ArrayList;

/**
 * Created by Marcin
 * Date: Dec 20, 2005
 * Time: 7:26:06 PM
 */
public class Numbers {

    /**
     * @param a
     * @return
     */

    public final static int getMinDigits(ArrayList a) {

        int digits = 0;

        for (int i = 0; i < a.size(); i++) {

            String d = "" + ((Double) a.get(i)).doubleValue();

            while (d.endsWith("0")) {
                d = d.substring(0, d.length() - 1);
            }

            int dot = d.indexOf(".");
            int curval = d.length() - dot;

            if (curval > digits)
                digits = curval;

        }

        return digits;
    }

    /**
     * @param a
     * @return
     */
    public final static int getMinDigits(double[][] a) {

        int digits = 0;

        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {

                String d = "" + a[i][j];

                while (d.endsWith("0")) {
                    d = d.substring(0, d.length() - 1);
                }

                int dot = d.indexOf(".");
                int curval = d.length() - dot;

                if (curval > digits)
                    digits = curval;

            }
        }

        return digits;
    }

}
