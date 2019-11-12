package DataMining;

import util.ColorScale;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * User: marcin
 * Date: Oct 14, 2007
 * Time: 3:38:59 PM
 */
public class TrajectoryViewCanvas extends JComponent {

    TrajectoryView mv;

    public Graphics2D ig;
    BufferedImage im;

    private Color[] triplescale;
    private ArrayList colorlabels;
    int maxnumincr = 20;
    int numincr;
    double dataincr;
    double colorscaleunit;
    double scale = Double.NaN;

    /*data cell dimensions*/
    int xunit = 10, yunit = 10;
    /**
     * rows and columns of the data matrix
     */
    int data_rows, data_cols;
    /**
     * predicted image dimensions
     */
    int sizex, sizey;

    ArrayList[] current_block = null;
    int current_block_index;
    int millis = 500;


    final static double sqrt2 = Math.sqrt(2);


    /**
     *
     */
    public TrajectoryViewCanvas(TrajectoryView m) {
        super();
        mv = m;
        im = null;
        /*xunit = (int)((double)mv.dimx/(double)mv.data_cols);
     yunit = (int)((double)mv.dimy/(double)mv.data_rows);
     System.out.println("MinerViewCanvas xunit "+xunit+"\tyunit "+yunit);
     System.out.println("MinerViewCanvas xunit "+xunit+mv.data_cols*+"\tyunit "+yunit);*/
        createCol();
        setCScaleIncr();
        //makeScaleLabels();
        setSize();
    }

    /**
     * @param g
     */
    public void paint(Graphics g) {
        ig = (Graphics2D) g;
        drawStuff();
    }


    /**
     * @param g
     */
    public void paint(Graphics2D g) {
        ig = g;
        drawStuff();
    }

    /**
     * @param g
     */
    public void paintComponent(Graphics g) {
        ig = (Graphics2D) g;
        drawStuff();
    }

    /**
     * @param imsizex
     * @param imsizey
     */
    public Image createImage(int imsizex, int imsizey) {
        //System.out.println("createImage  " + imx + "\t" + imy + "\t" + numcols + "\t" + numrows);
        im = new BufferedImage(sizex, sizey, BufferedImage.TYPE_INT_ARGB);
        ig = im.createGraphics();
        //System.out.println("createImage created graphics");
        return im;
    }

    /**
     * Creates the Graphics objects and paints on image.
     *
     * @param la
     * @param lb
     */
    public void makeImage(final int la, final int lb) {
        //System.out.println("makeImage image_made");
        mv.data_rows = la;
        mv.data_cols = lb;
        drawStuff();
        //System.out.println("a.f drawStuff");
    }

    /**
     *
     */
    private void setCScaleIncr() {
        double datarange = Math.abs(mv.data_max - mv.data_min);
        BigDecimal dr = new BigDecimal(datarange);
        numincr = triplescale.length;
        if (numincr > maxnumincr) {
            numincr = maxnumincr;
            dataincr = dr.divide(new BigDecimal(numincr), 50, BigDecimal.ROUND_HALF_UP).doubleValue();
        }

        scale = datarange / ((double) (triplescale.length));// - 1));
    }


    /**
     *
     */
    public void setIncr() {
        BigDecimal bd = new BigDecimal(((mv.data_max - mv.data_min) / dataincr));
        numincr = bd.setScale(0, BigDecimal.ROUND_UP).intValue();
    }

    /**
     * Generates numeric labels for the color scale.
     */
    private void makeScaleLabels() {
        colorlabels = new ArrayList();
        int count = 0;
        numincr = triplescale.length;
        int end = (int) numincr;
        double print1 = 0, print2 = 0;
        for (int i = 0; i < end; i++) {
            double one = ((double) numincr - 1 - count) * dataincr;
            double two = ((double) numincr - 1 - count + 1) * dataincr;
            print1 = mathy.stat.roundUp(mv.data_min + one, 2);
            print2 = mathy.stat.roundUp(mv.data_min + two, 2);
            String p1 = "" + print1;
            if (print1 == 0)
                p1 = "0.00";
            int dot = p1.indexOf(".");
            if (dot != -1)
                if (p1.substring(dot, p1.length() - 1).length() > 2)
                    p1 = p1.substring(0, dot + 3);
                else if (p1.substring(dot, p1.length() - 1).length() < 2)
                    p1 += "0";
            String p2 = "" + print2;
            if (print2 == 0)
                p2 = "0.00";
            dot = p2.indexOf(".");
            if (dot != -1)
                if (p2.substring(dot, p2.length() - 1).length() > 2)
                    p2 = p2.substring(0, dot + 3);
                else if (p2.substring(dot, p2.length() - 1).length() < 2)
                    p2 += "0";
            String p = p1 + " - " + p2;
            colorlabels.add((String) p);
            count++;
        }
    }

    /**
     * Sets dimensions of the colorgrid.
     */
    public void setSize() {
        sizex = xunit * mv.data_rows;
        sizey = yunit * mv.data_cols;
        createImage(sizex, sizey);
    }

    /**
     * Creates the color scales.
     */
    public void createCol() {
        triplescale = ColorScale.bluewhitered512;
    }

    /**
     * Draws the color grid representing the in_data.
     *
     * @param ig
     * @return Draws the grid.
     */
    private Graphics2D drawColorGrid(Graphics2D ig) {
        try {
            BigDecimal bscal2 = new BigDecimal(scale);
            for (int i = 0; i < mv.data_rows; i++) {
                for (int j = 0; j < mv.data_cols; j++) {
                    BigDecimal bpcij = null;
                    Color g = Color.white;
                    try {
                        bpcij = new BigDecimal(mv.data[i][j]);
                    } catch (NumberFormatException e) {
                    }
                    /*Exception case.*/
                    if (bpcij == null) {
                        g = Color.LIGHT_GRAY;
                    }
                    /*Data case.*/
                    else if (bpcij != null) {
                        int t = Math.abs(bpcij.divide(bscal2, 50, BigDecimal.ROUND_HALF_UP).intValue());
                        if (t >= triplescale.length) {
                            t = triplescale.length;
                        }
                        /*Problem cases, emits text warning - values out of bounds set to min and max respectively.*/
                        if (Math.abs(t - 0) < .1)
                            t = 0;
                        else if (Math.abs(t - triplescale.length) < .1)
                            t = triplescale.length - 1;
                        if (t >= triplescale.length) {
                            t = triplescale.length - 1;
                        } else if (t < 0) {
                            t = 0;
                        }
                        g = triplescale[t];
                    }
                    ig.setColor(g);
                    final int xr = i * xunit;
                    final int yr = j * yunit;

                    ig.fillRect(xr, yr, (int) (xunit), (int) (yunit));
                }
            }

            if (current_block != null) {
                for (int i = 0; i < current_block[0].size(); i++) {
                    int x = ((Integer) current_block[0].get(i)).intValue();
                    for (int j = 0; j < current_block[1].size(); j++) {
                        int y = ((Integer) current_block[1].get(j)).intValue();
                        final int xr = x * xunit;
                        final int yr = y * yunit;
                        System.out.println("current_block " + xr + "\t" + yr);
                        //ig.drawRect(xr, yr, (int) (xunit), (int) (yunit));

                        ig.setColor(Color.BLACK);
                        int xa = (int) ((double) xunit / 1.05);
                        int ya = (int) (((double) yunit / 1.05));
                        int xoff = (int) ((((double) xunit * sqrt2 - xa) / 2.0) / sqrt2);
                        int yoff = (int) ((((double) xunit * sqrt2 - ya) / 2.0) / sqrt2);
                        ig.drawOval(xr + xoff, yr + yoff, xa, ya);
                        xa = (int) ((double) xunit / 1.1);
                        ya = (int) (((double) yunit / 1.1));
                        xoff = (int) ((((double) xunit * sqrt2 - xa) / 2.0) / sqrt2);
                        yoff = (int) ((((double) xunit * sqrt2 - ya) / 2.0) / sqrt2);
                        ig.drawOval(xr + xoff, yr + yoff, xa, ya);
                        xa = (int) ((double) xunit / 1.15);
                        ya = (int) (((double) yunit / 1.15));
                        xoff = (int) ((((double) xunit * sqrt2 - xa) / 2.0) / sqrt2);
                        yoff = (int) ((((double) xunit * sqrt2 - ya) / 2.0) / sqrt2);
                        ig.drawOval(xr + xoff, yr + yoff, xa, ya);
                        xa = (int) ((double) xunit / 1.2);
                        ya = (int) (((double) yunit / 1.2));
                        xoff = (int) ((((double) xunit * sqrt2 - xa) / 2.0) / sqrt2);
                        yoff = (int) ((((double) xunit * sqrt2 - ya) / 2.0) / sqrt2);
                        ig.drawOval(xr + xoff, yr + yoff, xa, ya);

                        ig.setColor(Color.ORANGE);
                        xa = (int) ((double) xunit / 1.3);
                        ya = (int) (((double) yunit / 1.3));
                        xoff = (int) ((((double) xunit * sqrt2 - xa) / 2.0) / sqrt2);
                        yoff = (int) ((((double) xunit * sqrt2 - ya) / 2.0) / sqrt2);
                        ig.drawOval(xr + xoff, yr + yoff, xa, ya);
                        xa = (int) ((double) xunit / 1.35);
                        ya = (int) (((double) yunit / 1.35));
                        xoff = (int) ((((double) xunit * sqrt2 - xa) / 2.0) / sqrt2);
                        yoff = (int) ((((double) xunit * sqrt2 - ya) / 2.0) / sqrt2);
                        ig.drawOval(xr + xoff, yr + yoff, xa, ya);
                        xa = (int) ((double) xunit / 1.4);
                        ya = (int) (((double) yunit / 1.4));
                        xoff = (int) ((((double) xunit * sqrt2 - xa) / 2.0) / sqrt2);
                        yoff = (int) ((((double) xunit * sqrt2 - ya) / 2.0) / sqrt2);
                        ig.drawOval(xr + xoff, yr + yoff, xa, ya);
                        xa = (int) ((double) xunit / 1.5);
                        ya = (int) (((double) yunit / 1.5));
                        xoff = (int) ((((double) xunit * sqrt2 - xa) / 2.0) / sqrt2);
                        yoff = (int) ((((double) xunit * sqrt2 - ya) / 2.0) / sqrt2);
                        ig.drawOval(xr + xoff, yr + yoff, xa, ya);

                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ig;
    }

    /**
     * Draws the grid.
     *
     * @param ig
     * @return updated Graphics2D object
     */
    private Graphics2D drawGrid(final Graphics2D ig) {
        ig.setColor(Color.lightGray);
        for (int i = 0; i < mv.data_rows + 1; i++) {
            ig.drawLine(i * xunit, 0, i * xunit, mv.data_cols * yunit);
        }
        for (int j = 0; j < mv.data_cols + 1; j++) {
            ig.drawLine(0, j * yunit, mv.data_rows * xunit, j * yunit);
        }
        return ig;
    }


    /**
     * The paint method which controls sequential drawing of color-grid elements.
     */
    public void drawStuff() {
        //System.out.println("drawing graphics: drawStuff " + sizex + "\t" + sizey);
        //im = null;
        ig.setColor(Color.white);
        ig.fillRect(0, 0, sizex, sizey);
        ig = drawColorGrid(ig);
        ig = drawGrid(ig);
        repaint();
    }
}

