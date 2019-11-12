package util;

import java.awt.*;
import java.math.BigDecimal;

/**
 * User: marcin
 * Date: Oct 14, 2007
 * Time: 6:13:53 PM
 */
public class ColorMap {

    public Color[] colorscale;
    public double dataincr;
    public double firsthalfdataincr;
    public double secondhalfdataincr;
    public int firsthalfincr;
    public int secondhalfincr;
    public double datamax;
    public double datamin;
    public double datarange;
    public int bins;
    public double inflectioncoord = 0;


    /**
     * @param scale
     * @param max
     * @param min
     */
    public ColorMap(Color[] scale, double max, double min) {
        colorscale = scale;
        bins = colorscale.length;
        datamax = max;
        datamin = min;
    }

    /**
     * @param scale
     * @param max
     * @param min
     * @param inflection
     */
    public ColorMap(Color[] scale, double max, double min, double inflection) {
        colorscale = scale;
        bins = colorscale.length;
        datamax = max;
        datamin = min;
        inflectioncoord = inflection;
    }

    /**
     * @return
     */
    public Color getColor(double value) {
        try {
            double v = value;
            //shifts values in case bottom of range is negative
            if (datamin < inflectioncoord) {
                int index = -1;
                if (v < inflectioncoord) {
                    index = (int) ((inflectioncoord - v) / firsthalfdataincr);
                    System.out.println("ColorMap value datamin < inflectioncoord " + value + "\tv " + v + "\tfirsthalfdataincr " +
                            firsthalfdataincr + "\tindex " + index);
                } else {
                    index = (int) (v / secondhalfdataincr);
                    index += firsthalfdataincr;
                    System.out.println("ColorMap value datamin < inflectioncoord" + value + "\tv " + v + "\tsecondhalfdataincr " +
                            secondhalfdataincr + "\tindex " + index);
                }
                return colorscale[index];
            } else {
                int index = (int) (v / dataincr);
                System.out.println("ColorMap value datamin >= inflectioncoord " + value + "\tv " + v + "\tdataincr " +
                        dataincr + "\tindex " + index);
                return colorscale[index];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param value
     * @param min
     * @param max
     * @return
     */
    public Color getColorTruncate(double value, double min, double max) {
        //System.out.println("ColorMap getColorTruncate " + value + "\t" + min + "\t" + max);
        try {
            int index = -1;
            if (value >= min && value <= max) {
                index = (int) ((colorscale.length-1) * ((value + Math.abs(min)) / (max - min)));
                //System.out.println("ColorMap value > min && value < max " + value + "\tvalue " + value + "\tfirsthalfdataincr " +
                //        firsthalfdataincr + "\tindex " + index+"\t"+colorscale.length);
            } else if (value < min) {
                index = 0;
                //System.out.println("ColorMap value < min " + value + "\tvalue " + value + "\tfirsthalfdataincr " +
                //        firsthalfdataincr + "\tindex " + index+"\t"+colorscale.length);
            } else if (value > max) {
                index = colorscale.length - 1;
                //System.out.println("ColorMap value > max " + value + "\tvalue " + value + "\tfirsthalfdataincr " +
                //         firsthalfdataincr + "\tindex " + index+"\t"+colorscale.length);
            }

            Color color = null;
            try {
                color = colorscale[index];
            } catch (Exception e) {
                System.out.println("ERROR ColorMap.getColorTruncate() " + colorscale.length + "\t" + index);
                if (index > colorscale.length - 1)
                    index = colorscale.length - 1;
                    color = colorscale[index];
                //e.printStackTrace();
            }
            return color;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     */
    private void setBasicncrement() {
        datarange = Math.abs(datamax - datamin);
        BigDecimal dr = new BigDecimal(datarange);
        System.out.println("ColorMap setDataIncrement datarange " + datarange + "\tdataincr " +
                dataincr + "\tdatamin " + datamin + "\t" + datamax + "\tbins " + bins);
        dataincr = dr.divide(new BigDecimal(bins), 50, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     *
     */
    public void makeCenteredExtentIncrement() {
        datarange = Math.abs(datamax - datamin);
        BigDecimal dr = new BigDecimal(datarange);
        System.out.println("ColorMap setDataIncrement datarange " + datarange + "\tdataincr " +
                dataincr + "\tdatamin " + datamin + "\t" + datamax + "\tbins " + bins);
        double firstval = inflectioncoord - datamin;
        BigDecimal dba = new BigDecimal(firstval);
        try {
            firsthalfincr = (int) (bins * dba.divide(new BigDecimal(datarange), 50, BigDecimal.ROUND_HALF_UP).doubleValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
        BigDecimal dbb = new BigDecimal(datamax);
        secondhalfincr = (int) (bins * dbb.divide(new BigDecimal(datarange), 50, BigDecimal.ROUND_HALF_UP).doubleValue());
        System.out.println("ColorMap setDataIncrement firsthalfincr " + firsthalfincr + "\tsecondhalfincr " + secondhalfincr);
        firsthalfdataincr = dba.divide(new BigDecimal(firsthalfincr), 50, BigDecimal.ROUND_HALF_UP).doubleValue();
        secondhalfdataincr = dbb.divide(new BigDecimal(secondhalfincr), 50, BigDecimal.ROUND_HALF_UP).doubleValue();
        System.out.println("ColorMap setDataIncrement firsthalfdataincr " + firsthalfdataincr +
                "\tsecondhalfdataincr " + secondhalfdataincr + "\t" + firstval + "\t" + datamax);
    }


    public void makeCenteredTruncatedIncrement() {
        datarange = 4.0;
        System.out.println("ColorMap setDataIncrement datarange " + datarange + "\tdataincr " +
                dataincr + "\tdatamin " + datamin + "\t" + datamax + "\tbins " + bins);
        double firstval = inflectioncoord - datamin;
        firsthalfincr = 10;
        secondhalfincr = 10;
        System.out.println("ColorMap setDataIncrement firsthalfincr " + firsthalfincr + "\tsecondhalfincr " + secondhalfincr);
        firsthalfdataincr = 2.0 / 10.0;
        secondhalfdataincr = 2.0 / 10.0;
        System.out.println("ColorMap setDataIncrement firsthalfdataincr " + firsthalfdataincr +
                "\tsecondhalfdataincr " + secondhalfdataincr + "\t" + firstval + "\t" + datamax);
    }
}
