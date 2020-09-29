package util;

import java.awt.*;

/**
 * Created by Marcin
 * Date: Mar 19, 2005
 * Time: 6:41:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class ColorUtil {


    /**
     * @param s
     * @return
     */
    public final static Color parseCol(String s) {

        int comma = s.indexOf(",");
        int r = Integer.parseInt(s.substring(0, comma));
        int comma2 = s.indexOf(",", comma + 1);
        int g = Integer.parseInt(s.substring(comma + 1, comma2));
        int b = Integer.parseInt(s.substring(comma2 + 1, s.length()));

        if (r < 0)
            r = 0;
        else if (r > 255)
            r = 255;
        if (g < 0)
            g = 0;
        else if (g > 255)
            g = 255;
        if (b < 0)
            b = 0;
        else if (b > 255)
            b = 255;

        //System.out.println("parsed "+r+"\t"+g+"\t"+b);
        Color col = new Color(r, g, b);

        return col;
    }

    /**
     * Calculates the integer distance between two RGB colors.
     *
     * @param one
     * @param two
     * @return
     */
    public final static int colRGBDist(Color one, Color two) {

        int r1 = one.getRed();
        int g1 = one.getGreen();
        int b1 = one.getBlue();
        int r2 = two.getRed();
        int g2 = two.getGreen();
        int b2 = two.getBlue();

        return (Math.abs(r1 - r2) + Math.abs(g1 - g2) + Math.abs(b1 - b2));

    }

    /**
     * Method to create a comma delim String from RGB values of a Color.
     *
     * @param c
     * @return
     */
    public final static String getRGBString(Color c) {


        return (c.getRed() + "," + c.getGreen() + "," + c.getBlue());
    }

}
