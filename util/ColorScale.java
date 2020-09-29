package util;

import java.awt.*;
import java.math.BigDecimal;

/**
 * Version 1.0 07/28/04
 * Author Marcin P. Joachimiak
 */

public final class ColorScale {

    public final static Color[] whitered128 = createCol("whitered128");
    public final static Color[] whitered256 = createCol("whitered256");
    public final static Color[] whitered512 = createCol("whitered512");
    public final static Color[] whiteblue128 = createCol("whiteblue128");
    public final static Color[] whiteblue256 = createCol("whiteblue256");
    public final static Color[] whiteblue512 = createCol("whiteblue512");
    public final static Color[] redwhite256 = createCol("redwhite256");
    public final static Color[] redwhite512 = createCol("redwhite512");
    public final static Color[] bluewhitered512 = createCol("bluewhitered512");
    public final static Color[] bluewhitered256 = createCol("bluewhitered256");
    public final static Color[] redwhiteblue512 = createCol("redwhiteblue512");
    public final static Color[] redwhiteblue256 = createCol("redwhiteblue256");


    public ColorScale() {

        super();
    }

    /**
     * Creates the color scales.
     */
    public final static Color[] createCol(String s) {

        Color colorhi, colorlo, colormed = null;

        int num = Integer.parseInt(s.substring(s.length() - 3, s.length()));

        if (s.indexOf("whitered128") == 0) {

            return createCol2(Color.white, Color.red, num);
        } else if (s.indexOf("whitered256") == 0) {

            return createCol2(Color.white, Color.red, num);
        } else if (s.indexOf("whitered512") == 0) {

            return createCol2(Color.white, Color.red, num);
        } else if (s.indexOf("whiteblue128") == 0) {

            return createCol2(Color.white, Color.blue, num);
        } else if (s.indexOf("whiteblue256") == 0) {

            return createCol2(Color.white, Color.blue, num);
        } else if (s.indexOf("whiteblue512") == 0) {

            return createCol2(Color.white, Color.blue, num);
        } else if (s.indexOf("redwhite256") == 0) {

            return createCol2(Color.white, Color.red, num);
        } else if (s.indexOf("bluewhitered256") == 0) {

            return createCol3(Color.blue, Color.white, Color.red, num);
        } else if (s.indexOf("bluewhitered512") == 0) {

            return createCol3(Color.blue, Color.white, Color.red, num);
        } else if (s.indexOf("redwhiteblue256") == 0) {

            return createCol3(Color.red, Color.white, Color.blue, num);
        } else if (s.indexOf("redwhiteblue512") == 0) {

            return createCol3(Color.red, Color.white, Color.blue, num);
        }
        return null;
    }

    /**
     * Creates a color scale with two colors and the specified number.
     */
    public final static Color[] createCol2(Color colorlo, Color colorhi, int n) {

//System.out.println("Creating colorScale2 "+n);

        int r, p, b;

        Color[] ret = new Color[n];

        int rmin = colorlo.getRed();
        int rmax = colorhi.getRed();
        int gmin = colorlo.getGreen();
        int gmax = colorhi.getGreen();
        int bmin = colorlo.getBlue();
        int bmax = colorhi.getBlue();

        for (int i = 0; i < n; i++) {

            if (rmin == rmax)
                r = rmin;
            else
                r = (int) (((double) i / n) * (double) (rmax - rmin)) + rmin;
            if (gmin == gmax)
                p = gmin;
            else
                p = (int) (((double) i / n) * (double) (gmax - gmin)) + gmin;
            if (bmin == bmax)
                b = bmin;
            else
                b = (int) (((double) i / n) * (double) (bmax - bmin)) + bmin;

//System.out.println(i+" !neg "+r+"\t"+p+"\t"+b);
            ret[i] = new Color(r, p, b);
        }
        return ret;
    }

    /**
     * Creates a color scale with two colors and the specified number.
     */
    public final static Color[] createCol2(Color colorlo, Color colorhi, int n, double r1, double firsthalf, double secondhalf) {

//System.out.println("Creating colorScale2 "+n);

        double firstd = Double.NaN;
        double secondd = Double.NaN;

        n = (int) (firsthalf + secondhalf);
        Color[] ret = new Color[n];

        //System.out.println("createCol2: using ratio " + r1 + " = firsthalf " + firsthalf + "\tsecondhalf " + secondhalf + "\tn " + n);

        int r, g, b;

        int rmin = colorlo.getRed();
        int rmax = colorhi.getRed();
        int gmin = colorlo.getGreen();
        int gmax = colorhi.getGreen();
        int bmin = colorlo.getBlue();
        int bmax = colorhi.getBlue();

        double firstratio = firsthalf / (firsthalf + secondhalf);
        boolean reset_min = false;

        if (n == 2) {
            ret[0] = new Color(rmin, gmin, bmin);
            ret[1] = new Color(rmax, gmax, bmax);

        } else {
            for (int i = 0; i < n; i++) {

                double secondratio = ((i - firsthalf)) / (firsthalf + secondhalf);
                double sum = firstratio + secondratio;

                if (i < firsthalf) {
                    if (rmin == rmax)
                        r = rmin;
                    else
                        r = (int) ((((double) i) / firsthalf) * (double) (rmax - rmin)) + rmin;
                    if (gmin == gmax)
                        g = gmin;
                    else
                        g = (int) ((((double) i) / firsthalf) * (double) (gmax - gmin)) + gmin;
                    if (bmin == bmax)
                        b = bmin;
                    else
                        b = (int) ((((double) i) / firsthalf) * (double) (bmax - bmin)) + bmin;
                    reset_min = true;

                    //System.out.println(i+" firstratio " + firstratio + "\tsecondratio " + secondratio + "\tsum " + sum + "\tn " + n + "\tsum "+sum +"\t"+r+"\t"+g+"\t"+b);

                } else {
                    if (reset_min) {
                        rmin = ret[i - 1].getRed();
                        gmin = ret[i - 1].getGreen();
                        bmin = ret[i - 1].getBlue();
                        reset_min = false;
                    }

                    if (rmin == rmax)
                        r = rmin;
                    else
                        r = (int) ((firstratio + secondratio) * (double) (rmax - rmin)) + rmin;
                    if (gmin == gmax)
                        g = gmin;
                    else
                        g = (int) ((firstratio + secondratio) * (double) (gmax - gmin)) + gmin;
                    if (bmin == bmax)
                        b = bmin;
                    else
                        b = (int) ((firstratio + secondratio) * (double) (bmax - bmin)) + bmin;
                    //System.out.println(i+ " secondratio " + secondratio + "\tfirstratio " + firstratio + "\tsum " + sum + "\tn " + n + "\tsum "+sum +"\t"+r+"\t"+g+"\t"+b);
                }
                ret[i] = new Color(r, g, b);
            }
        }

        return ret;
    }

    /**
     * Creates a color scale with three colors and the specified number.
     * @param colorlo
     * @param colormed
     * @param colorhi
     * @param n
     * @param r1
     * @param firsthalf
     * @param secondhalf
     * @return
     */
    public final static Color[] createCol3(Color colorlo, Color colormed, Color colorhi, int n, double r1, double firsthalf, double secondhalf) {
//System.out.println("Creating colorScale3 "+n);
        int r, g, b;
        int rmin = colorlo.getRed();
        int rmax = colormed.getRed();
        int gmin = colorlo.getGreen();
        int gmax = colormed.getGreen();
        int bmin = colorlo.getBlue();
        int bmax = colormed.getBlue();

        int rmin2 = colormed.getRed();
        int rmax2 = colorhi.getRed();
        int gmin2 = colormed.getGreen();
        int gmax2 = colorhi.getGreen();
        int bmin2 = colormed.getBlue();
        int bmax2 = colorhi.getBlue();

        firsthalf = new BigDecimal(firsthalf).setScale(0, BigDecimal.ROUND_UP).intValue();
        secondhalf = new BigDecimal(secondhalf).setScale(0, BigDecimal.ROUND_UP).intValue();
        n = (int) (firsthalf + secondhalf);
        //System.out.println("createCol3: using ratio " + r1 + " = firsthalf " + firsthalf + "\tsecondhalf " + secondhalf + "\tn " + n);                
        Color[] ret = new Color[n];

        //System.out.println("createCol3: using ratio " + r1 + " = firsthalf " + firsthalf + "\tsecondhalf " + secondhalf + "\tn " + n);
        if (n == 2) {
            ret[0] = new Color(rmin, gmin, bmin);
            ret[1] = new Color(rmax2, gmax2, bmax2);
        } else {
            for (double i = 0; i < firsthalf - 1; i++) {
                if (rmin == rmax)
                    r = rmin;
                else
                    r = (int) ((i / firsthalf) * (double) (rmax - rmin)) + rmin;
                if (gmin == gmax)
                    g = gmin;
                else
                    g = (int) ((i / firsthalf) * (double) (gmax - gmin)) + gmin;
                if (bmin == bmax)
                    b = bmin;
                else
                    b = (int) ((i / firsthalf) * (double) (bmax - bmin)) + bmin;
                ret[(int) i] = new Color(r, g, b);
            }

            for (double i = firsthalf - 1; i < n; i++) {
                double curi = i - firsthalf + 1;
                if (rmin2 == rmax2)
                    r = rmin2;
                else
                    r = (int) ((curi / secondhalf) * (double) (rmax2 - rmin2)) + rmin2;
                if (gmin2 == gmax2)
                    g = gmin2;
                else
                    g = (int) ((curi / secondhalf) * (double) (gmax2 - gmin2)) + gmin2;
                if (bmin2 == bmax2)
                    b = bmin2;
                else
                    b = (int) ((curi / secondhalf) * (double) (bmax2 - bmin2)) + bmin2;
                ret[(int) i] = new Color(r, g, b);
            }
        }
        return ret;
    }

    /**
     * Returns the integer number of increments for a scale of given size and with the fist half specified by ratio.
     *
     * @param size
     * @param ratio
     * @return
     */
    public final static double[] calcHalves(int size, double ratio) {

        //System.out.println("calcHalves " + size + "\t" + ratio);
        double[] ret = new double[2];
        double tolerance = 0.00001;
        double test = Math.abs(ratio - 1);
        if (ratio > 1 && test > tolerance) {
            ret[1] = ((double) size) * (1.0 / ratio);
            ret[0] = (double) size - ret[1];
        } else if (ratio < 1 && test > tolerance) {
            ret[0] = ((double) size) * ratio;
            ret[1] = (double) size - ret[0];
        } else {
            ret[0] = ((double) size) * 0.5;
            ret[1] = (double) size - ret[0];
        }

        ret[0] = new BigDecimal(ret[0]).setScale(0, BigDecimal.ROUND_UP).doubleValue();
        ret[1] = new BigDecimal(ret[1]).setScale(0, BigDecimal.ROUND_UP).doubleValue();
        //System.out.println("calcHalves " + ret[0] + "\t" + ret[1]);
        return ret;
    }

    /**
     * Creates a color scale with two colors and the specified number.
     */
    public final static Color[] createCol3(Color colorlo, Color colormed, Color colorhi, int n) {

//System.out.println("Creating colorScale3 "+n);

        int r, g, b;

        Color[] ret = new Color[n];

        int rmin = colorlo.getRed();
        int rmax = colormed.getRed();
        int gmin = colorlo.getGreen();
        int gmax = colormed.getGreen();
        int bmin = colorlo.getBlue();
        int bmax = colormed.getBlue();

        int rmin2 = colormed.getRed();
        int rmax2 = colorhi.getRed();
        int gmin2 = colormed.getGreen();
        int gmax2 = colorhi.getGreen();
        int bmin2 = colormed.getBlue();
        int bmax2 = colorhi.getBlue();

        for (int i = 0; i < n; i++) {

            int half = (int) ((double) (n) / (double) 2);//(int) ((double) (n - 1) / (double) 2);
            //System.out.println("createCol3 " + n + "\t" + half);
            if (i <= half) {

                if (rmin == rmax)
                    r = rmin;
                else
                    r = (int) (((double) i / half) * (double) (rmax - rmin)) + rmin;
                if (gmin == gmax)
                    g = gmin;
                else
                    g = (int) (((double) i / half) * (double) (gmax - gmin)) + gmin;
                if (bmin == bmax)
                    b = bmin;
                else
                    b = (int) (((double) i / half) * (double) (bmax - bmin)) + bmin;

                //System.out.println(i+" first half "+r+"\t"+g+"\t"+b);
            } else {

                int curi = i - half;
                if (rmin2 == rmax2)
                    r = rmin2;
                else
                    r = (int) (((double) curi / half) * (double) (rmax2 - rmin2)) + rmin2;
                if (gmin2 == gmax2)
                    g = gmin2;
                else
                    g = (int) (((double) curi / half) * (double) (gmax2 - gmin2)) + gmin2;
                if (bmin2 == bmax2)
                    b = bmin2;
                else
                    b = (int) (((double) curi / half) * (double) (bmax2 - bmin2)) + bmin2;

                //System.out.println(i+" second half "+r+"\t"+g+"\t"+b);
            }

            //System.out.println("adding new color "+r+"\t"+g+"\t"+b);

            ret[i] = new Color(r, g, b);
        }
        return ret;
    }

}
