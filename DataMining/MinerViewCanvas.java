package DataMining;

import dtype.DDouble;
import mathy.stat;
import util.ColorMap;
import util.ColorScale;
import util.MoreArray;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.util.ArrayList;

//import org.jibble.epsgraphics.EpsGraphics2D;

/**
 * Class embodying a graphical canvas for the MinerView application.
 * <p/>
 * User: marcin
 * Date: Oct 11, 2007
 * Time: 5:33:47 PM
 */
public class MinerViewCanvas extends JComponent implements javax.swing.Scrollable {

    public boolean debug = false;
    public MinerView mv;

    public Graphics2D ig;
    BufferedImage im;

    Color remExpColor = Color.cyan.darker();
    Color remGeneColor = Color.magenta;
    //public Color[] triplescale = ColorScale.createCol3(Color.blue.brighter(), Color.white, Color.orange.darker(), 15);
    public Color[] triplescale = ColorScale.createCol3(Color.blue.brighter(), Color.white, Color.orange, 15);
    //public Color[] dualscale = ColorScale.createCol2(Color.white, Color.orange.darker(), 15);
    public Color[] dualscale = ColorScale.createCol2(Color.white, Color.orange, 15);
    ColorMap cm;

    /*data cell dimensions*/
    public final static int default_xunit = 1, default_yunit = 1;
    public int xunit = 1, yunit = 1;
    public final static int xOffset = 30;
    public final static int yOffset = 30;
    int sizex = -1, sizey = -1;
    int sizecanvx = -1, sizecanvy = -1;
    //int sizex = -1, sizey = -1;
    //int sizecanvx = 1050, sizecanvy = 1000;
    int max_blocks_per_window = -1;

    ArrayList criteriaYPos;
    ArrayList criteria;
    ArrayList remaining;

    Color[] criteria_colors = {
            Color.black,
            new Color(20, 20, 20),
            new Color(50, 50, 50),
            new Color(80, 80, 80),
            new Color(110, 110, 110),
            new Color(140, 140, 140),
            new Color(170, 170, 170),
            new Color(200, 200, 200),
            new Color(230, 230, 230),
            new Color(255, 255, 255),
    };

    String[] criteria_label = {"expr_mean", "expr_mse", "expr_reg", "expr_kend", "expr_cor", "expr_euc", "interact", "feature", "TF"};

    ValueBlock startBlock;
    int startBlockSlashInd;

    /*For displaying cell values.*/
    public final static Font fontiny = new Font("terminal", Font.PLAIN, 7);//Lucida sans console//MonoSpaced
    public final static Font fontsmallbold = new Font("terminal", Font.BOLD, 8);
    public final static Font fontsmall = new Font("terminal", Font.PLAIN, 5);
    public final static Font fontsupersmall = new Font("terminal", Font.PLAIN, 4);
    public final static Font fontsupersmall2 = new Font("terminal", Font.PLAIN, 7);
    public final static Font fontsmall2 = new Font("terminal", Font.PLAIN, 10);
    public final static Font fontmediumsmall = new Font("terminal", Font.PLAIN, 10);
    public final static Font fontmedium = new Font("terminal", Font.PLAIN, 12);
    FontMetrics fm_small;
    FontMetrics fm_small2;
    int small_advance;
    int small2_advance;
    FontRenderContext frc;

    final static double sqrt2 = Math.sqrt(2);

    double graphRange = 390.0 - 200.0;
    int trackOffset = 380;
    double master_scale_max = 1.0;

    /*Interval for fullCriterion scale.*/
    double intervalsPerCriterion = 10.0;
    double mainScaleIntervals;
    int curTrajectoryPosition = 0;
    int curYPosition = 0;

    final int gene_label_offset = 7;

    ValueBlockList trajectory;

    boolean threshold_color = false;


    /**
     * @param m
     * @param x
     */
    public MinerViewCanvas(MinerView m, int x) {
        super();
        mv = m;
        init();
        sizecanvx = mv.mvb.canvdimy;
        sizecanvy = mv.mvb.canvdimy;
        sizex = x;
        max_blocks_per_window = sizecanvx / xunit;
        System.out.println("MinerViewCanvas " + sizex + "\t" + sizey);
        im = null;


        //triplescale = ColorScale.createCol3(Color.blue.brighter(), Color.white, Color.orange, 15);
        System.out.println("mv.dataexpr_max, mv.dataexpr_min " + mv.mvb.dataexpr_max + "\t" +
                mv.mvb.dataexpr_min + "\t" + triplescale.length);

        Color[] curscale = dualscale;
        if (mv.mvb.dataexpr_min < 0)
            curscale = triplescale;

        if (mv.mvb.dataexpr_min_cut != Double.NaN && mv.mvb.dataexpr_max_cut != Double.NaN) {
            threshold_color = true;
            cm = new ColorMap(curscale, mv.mvb.dataexpr_max_cut, mv.mvb.dataexpr_min_cut);
        } else
            cm = new ColorMap(curscale, mv.mvb.dataexpr_max, mv.mvb.dataexpr_min);

        //cm.makeCenteredTruncatedIncrement();
        setTrajectory();
        //updateSize();
        double[] min_max = getMinMaxandScaled();
        createImage();
        drawStuff();
    }

    /**
     * @param m
     * @param x
     */
/*    public MinerViewCanvas(MinerViewBack m, int x) {
        super();
        mv.mvb = m;
        init();
        sizecanvx = mv.mvb.canvdimy;
        sizecanvy = mv.mvb.canvdimy;
        sizex = x;
        max_blocks_per_window = sizecanvx / xunit;
        System.out.println("MinerViewCanvas " + sizex + "\t" + sizey);
        im = null;
        triplescale = ColorScale.createCol3(Color.blue.brighter(), Color.white, Color.orange, 15);
        System.out.println("mv.dataexpr_max, mv.dataexpr_min " + mv.mvb.dataexpr_max + "\t" +
                mv.mvb.dataexpr_min + "\t" + triplescale.length);
        cm = new ColorMap(triplescale, mv.mvb.dataexpr_max, mv.mvb.dataexpr_min);
        setTrajectory();
        double[] min_max = getMinMaxandScaled();
        createImage();
        drawStuff();
    }*/

    /**
     *
     */
 /*   public MinerViewCanvas(MinerView m) {
        super();
        debug = m.debug;

        init();
        mv = m;

        sizecanvx = mv.mvb.canvdimx;
        sizecanvy = mv.mvb.canvdimy;
        max_blocks_per_window = sizecanvx / xunit;

        im = null;
        System.out.println("mv.dataexpr_max, mv.dataexpr_min " + mv.mvb.dataexpr_max + "\t" +
                mv.mvb.dataexpr_min + "\t" + triplescale.length);

        if (mv.mvb.dataexpr_min_cut != Double.NaN && mv.mvb.dataexpr_max_cut != Double.NaN) {
            threshold_color = true;
            cm = new ColorMap(triplescale, mv.mvb.dataexpr_max_cut, mv.mvb.dataexpr_min_cut);
        } else
            cm = new ColorMap(triplescale, mv.mvb.dataexpr_max, mv.mvb.dataexpr_min);
        //cm.makeCenteredTruncatedIncrement();
        //updateSize();
        setTrajectory();

        double[] min_max = getMinMaxandScaled();

        createImage();
        drawStuff();
    }*/

    /**
     *
     */
    private void setTrajectory() {
        if (mv.mvb.rmb != null && mv.mvb.rmb.mi != null && mv.mvb.rmb.mi.trajectory != null) {
            trajectory = mv.mvb.rmb.mi.trajectory;
        } else if (mv.mvb.trajectory != null) {
            trajectory = mv.mvb.trajectory;
        }

        for (int i = 0; i < trajectory.size(); i++) {
            ValueBlock vb = (ValueBlock) trajectory.get(i);
            DDouble dd = new DDouble(vb.percentOrigGenes, vb.percentOrigExp);
            remaining.add(dd);
        }
    }

    /**
     *
     */
    public void init() {
        mainScaleIntervals = master_scale_max * intervalsPerCriterion;

        remaining = new ArrayList();
        criteriaYPos = new ArrayList();
        criteria = new ArrayList();

        AffineTransform af = new AffineTransform();
        frc = new FontRenderContext(af, false, false);
        Frame j = new Frame();
        fm_small = j.getFontMetrics(fontsmall);
        fm_small2 = j.getFontMetrics(fontsmall2);
        small_advance = fm_small.stringWidth("A");
        small2_advance = fm_small2.stringWidth("A");
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
     *
     */
    public Image createImage() {
        //System.out.println("createImage  " + imx + "\t" + imy + "\t" + numcols + "\t" + numrows);
        im = new BufferedImage(sizecanvx, sizecanvy, BufferedImage.TYPE_INT_ARGB);
        ig = im.createGraphics();
        //System.out.println("createImage created graphics");
        return im;
    }

    /**
     * Draws the color grid representing the in data.
     *
     * @param ig
     * @return Draws the grid.
     */
    public Graphics2D drawTrajectory(Graphics2D ig) {
        //System.out.println("drawTrajectory " + trajectory.size());
        int curadd = 0;
        try {
            int xcount = 0;

            ig.setColor(Color.gray);
            ig.drawLine(xOffset - 1, trackOffset, 1200, trackOffset);
            ig.drawLine(xOffset - 1, trackOffset + (int) graphRange, 1200, trackOffset + (int) graphRange);
            ig.setFont(fontsmall);

            double yTickVal = (190.0 / mainScaleIntervals);
            //System.out.println("drawTrajectory yTickVal " + yTickVal);
            for (double i = 0; i <= mainScaleIntervals; i++) {
                int xSub = 5;
                if (i % intervalsPerCriterion == 0) {
                    xSub = 10;
                    ig.drawString("" + (master_scale_max - (i / intervalsPerCriterion)), xOffset - 23,
                            trackOffset + (int) (i * yTickVal) + 3);
                }
                ig.drawLine(xOffset - xSub, trackOffset + (int) (i * yTickVal), xOffset - 1, trackOffset + (int) (i * yTickVal));
            }

            //System.out.println("MinerViewCanvas drawTrajectory " + trajectory.size() + "\t" + curTrajectoryPosition);
            double minval = Double.NaN, maxval = Double.NaN;
            int prevxcount = -1;
            final int max = Math.min(curTrajectoryPosition + max_blocks_per_window, trajectory.size());
            for (int i = curTrajectoryPosition; i < max; i++) {
                ig.setColor(Color.black);
                ig.drawLine(xOffset + (xcount * xunit) + 1, 390, xOffset + (xcount * xunit) + 1, 394);
                ig.setFont(fontsmall2);
                String str = "" + (i + 1);
                String str2 = "/ " + trajectory.size();
                try {
                    ig.drawString(str, xOffset + (xcount * xunit) + 4, sizey - 5);
                    ig.setFont(fontsmall);
                    int thisoff = this.small2_advance * str.length();
                    ig.drawString(str2, xOffset + (xcount * xunit) + 4 + thisoff, sizey - 5);
                } catch (Exception e) {
                    System.out.println(str);
                    e.printStackTrace();
                }

                ValueBlock vblock = (ValueBlock) trajectory.get(i);
                //System.out.println("drawTrajectory trajectory ValueBlock " + i + "\t" + MoreArray.toString(vblock.all_criteria));
                //System.out.println("drawTrajectory trajectory ValueBlock " + vblock.block_id+ "\t" + ValueBlock.IcJctoijID(vblock.coords));

                for (int a = curYPosition; a < vblock.genes.length; a++) {
                    int Aint = vblock.genes[a];
                    //expsStr
                    for (int b = 0; b < vblock.exps.length; b++) {
                        int Bint = vblock.exps[b];
                        //System.out.println("MinerViewCanvas drawTrajectory b/f fillCell " + Aint + "\t" + Bint);
                        //block id indices are offset +1 (Java vs. R)
                        int curgene = Aint - 1;
                        int curexp = Bint - 1;
                        int adjB = b + xcount;
                        /*System.out.println("MinerViewCanvas drawTrajectory: curgene "
                                + curgene + "\tcurexp " + curexp + "\ta " + a + "\tb " + adjB);*/
                        double curval = fillCell(curgene, curexp, a - curYPosition, adjB, ig);

                        if (curval > maxval)
                            maxval = curval;
                        else if (curval < minval)
                            minval = curval;
                    }
                }

                /*draw added gene*/
                if (vblock.move_type == 1) {
                    ValueBlock vblock_prev = (ValueBlock) trajectory.get(i - 1);
                    int added_gene_index = BlockMethods.addedGene(vblock_prev.genes, vblock.genes);
                    //System.out.println("Added gene " + added_gene_index);
                    int a = vblock.genes.length + 1;
                    for (int b = 0; b < vblock.exps.length; b++) {
                        int Bint = vblock.exps[b];
                        int curexp = Bint - 1;
                        int adjB = b + xcount;
                        double curval = fillCell(added_gene_index, curexp, a - curYPosition, adjB, ig);
                    }
                }

                int pos = 1;
                if (pos == curTrajectoryPosition)
                    pos = 0;
                ig = drawColorGridLabels(ig, xcount, vblock.genes, vblock.exps, pos, i);
                int newxcount = vblock.exps.length;

                drawTracks(ig, xcount, prevxcount, i);

                prevxcount = xcount;

                xcount += newxcount + gene_label_offset;
                curadd++;
                //System.out.println("MinerViewCanvas drawTrajectory: xcount " + xcount + "\tnewxcount " + newxcount + "\tcuradd " + curadd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ig;
    }

    /**
     * @param ig
     * @param xcount
     * @param prevxcount
     * @param i
     */
    private void drawTracks(Graphics2D ig, int xcount, int prevxcount, int i) {

        ig.setFont(fontsmall);

        int xOv = xcount * xunit + xOffset;
        double[] curScale = ((double[]) criteriaYPos.get(i));
        double[] curCrit = ((double[]) criteria.get(i));
        //System.out.println("MinerViewCanvas drawTracks curCrit " + MoreArray.toString(curCrit, ","));
        for (int j = 0; j < curScale.length; j++) {
            int yForCrit = trackOffset + (int) curScale[j];
            ig.setColor(criteria_colors[j]);
            ig.fillOval(xOv, yForCrit, 5, 5);
            double v = curCrit[j];
            /*if (j == 0 && !mv.mvb.isPreCrit) {
                v = curCrit[j] / criteria_scale_max;
            }*/
            String printCritVal = "" + v;
            //System.out.println("MinerViewCanvas drawTracks curCrit " + curCrit[j] +
            //        "\t" + curScale[j] + "\t" + v + "\t" + yForCrit + "\t" + printCritVal);
            try {
                ig.drawString(printCritVal.substring(0, Math.min(printCritVal.length(), 6)), xOv, yForCrit - 1);
                ig.drawString(criteria_label[j], xOv, yForCrit - 8);
            } catch (Exception e) {
                if (debug)
                    e.printStackTrace();
            }
            if (i > 0)
                connectCriteria(i, prevxcount, xcount, ig);
        }

        DDouble cur = null;
        try {
            cur = (DDouble) remaining.get(i);

            //System.out.println("remaining " + i + "\t" + cur.x + "\t" + cur.y);
            /*Percent remaining original genes.*/
            int yForRemX = trackOffset + (int) ((1.0 - cur.x) * graphRange);
            //System.out.println("MinerViewCanvas drawTrajectory yForRemX " + cur.x + "\t" + yForRemX);
            ig.setColor(remGeneColor);
            ig.fillOval(xOv, yForRemX, 5, 5);
            String printXRem = "" + cur.x;
            ig.drawString(printXRem.substring(0, Math.min(printXRem.length(), 6)), xOv, yForRemX - 1);
            ig.drawString("%genes", xOv, yForRemX - 8);
            if (i > 0)
                connectXRem(i, prevxcount, xcount, ig);

            /*Percent remaining original exps.*/
            int yForRemY = trackOffset + (int) ((1.0 - cur.y) * graphRange);
            //System.out.println("MinerViewCanvas drawTrajectory yForRemY " + cur.y + "\t" + yForRemY);
            //ig.setColor(Color.blue);
            ig.setColor(remExpColor);
            ig.fillOval(xOv, yForRemY, 5, 5);
            String printYRem = "" + cur.y;
            ig.drawString(printYRem.substring(0, Math.min(printYRem.length(), 6)), xOv, yForRemY - 1);
            ig.drawString("%exps", xOv, yForRemY - 8);
        } catch (Exception e) {
            System.out.println("remaining " + i + "\t" + remaining.size());
            e.printStackTrace();
        }
        if (i > 0)
            connectYRem(i, prevxcount, xcount, ig);
    }

    /**
     * @param ig
     * @param xcount
     * @param genes
     * @param exps
     * @param blockNum
     * @param index
     * @return
     */
    private Graphics2D drawColorGridLabels(Graphics2D ig, int xcount, int[] genes, int[] exps, int blockNum, int index) {
        String block_id = null;
        if (startBlock == null) {
            startBlock = (ValueBlock) trajectory.get(0);
            block_id = BlockMethods.IcJctoijID(startBlock.genes, startBlock.exps);
            startBlockSlashInd = block_id.indexOf("/");
        } else
            block_id = BlockMethods.IcJctoijID(startBlock.genes, startBlock.exps);

        for (int a = curYPosition + 1; a < genes.length + 1; a++) {
            ig.setFont(fontsupersmall2);
            ig.setColor(Color.black);
            //Integer cur = (Integer) block[0].get(a - 1);
            int cur = genes[a - 1];
            String test1 = "" + cur + ",";
            String test2 = "," + cur + ",";
            String test3 = "," + cur;

            String str = "-";//"" + cur;
            if (block_id.indexOf(test1) == 0
                    || (block_id.indexOf(test2) != -1 && block_id.indexOf(test2) < startBlockSlashInd)
                    || block_id.indexOf(test3) == startBlockSlashInd - test3.length()) {
                ig.setFont(fontsupersmall2);
                ig.setColor(remGeneColor);
            }
            if (blockNum > 0) {
                try {
                    ig.drawString(str, xOffset + (xcount - 1) * xunit - 1 * gene_label_offset, (a - curYPosition) * yunit + yOffset);
                    //ig.drawString(str, xOffset + (xcount - 1) * xunit - 2 * gene_label_offset, (a - curYPosition) * yunit + yOffset);
                } catch (Exception e) {
                    System.out.println("String " + str);
                    e.printStackTrace();
                }
            } else
                ig.drawString(str, xOffset + xcount * xunit + -1 * gene_label_offset, (a - curYPosition) * yunit + yOffset);
            //ig.drawString(str, xOffset + xcount * xunit + -2 * gene_label_offset, (a - curYPosition) * yunit + yOffset);
        }

        for (int b = 0; b < exps.length; b++) {
            ig.setFont(fontsupersmall);
            ig.setColor(Color.black);
            //Integer cur = (Integer) block[1].get(b);
            int cur = exps[b];
            String test1 = "/" + cur + ",";
            String test2 = "," + cur + ",";
            String test3 = "," + cur;

            String str = "|";//"" + cur;
            if (block_id.indexOf(test1, startBlockSlashInd) == startBlockSlashInd
                    || (block_id.indexOf(test2, startBlockSlashInd) != -1 && block_id.indexOf(test2, startBlockSlashInd) > startBlockSlashInd)
                    || block_id.indexOf(test3, startBlockSlashInd) == block_id.length() - test3.length()) {
                ig.setFont(fontsupersmall);
                ig.setColor(Color.cyan.darker());
            }
            try {
                ig.drawString(str, (b + xcount) * xunit + xOffset, 20 + 3 * (1));
                //ig.drawString(str, (b + xcount) * xunit + xOffset + 2, 6 + 3 * (b + 1));
            } catch (Exception e) {
                System.out.println(str);
                e.printStackTrace();
            }
        }

        return ig;
    }

    /**
     * @param i
     * @param prevxcount
     * @param nextxcount
     * @param ig
     */
    public void connectCriteria(int i, int prevxcount, int nextxcount, Graphics ig) {
        try {
            double[] last = (double[]) criteriaYPos.get(i - 1);
            double[] cur = (double[]) criteriaYPos.get(i);
            for (int j = 0; j < last.length; j++) {
                int last_val = (int) last[j];
                int cur_val = (int) cur[j];
                ig.setColor(criteria_colors[j]);
                ig.drawLine(prevxcount * xunit + xOffset, trackOffset + last_val, nextxcount * xunit + xOffset, trackOffset + cur_val);
            }
        } catch (Exception e) {
            System.out.println("connectCriteria " + i + "\t" + criteriaYPos.size());
            e.printStackTrace();
        }
    }

    /**
     * @param i
     * @param ig
     */
    public void connectXRem(int i, int prevxcount, int nextxcount, Graphics ig) {
        //System.out.println("connectXRem " + i);
        int prev = (int) ((1.0 - ((DDouble) remaining.get(i - 1)).x) * graphRange);
        int next = (int) ((1.0 - ((DDouble) remaining.get(i)).x) * graphRange);
        ig.setColor(remGeneColor);
        ig.drawLine(prevxcount * xunit + xOffset, trackOffset + prev, nextxcount * xunit + xOffset, trackOffset + next);
    }

    /**
     * @param i
     * @param ig
     */
    public void connectYRem(int i, int prevxcount, int nextxcount, Graphics ig) {
        //System.out.println("connectYRem " + i);
        int prev = (int) ((1.0 - ((DDouble) remaining.get(i - 1)).y) * graphRange);
        int next = (int) ((1.0 - ((DDouble) remaining.get(i)).y) * graphRange);

        ig.setColor(remExpColor);
        ig.drawLine(prevxcount * xunit + xOffset, trackOffset + prev, nextxcount * xunit + xOffset, trackOffset + next);
    }

    /**
     * @return
     */
    private double[] getMinMaxandScaled() {
        criteria = new ArrayList();
        criteria.ensureCapacity(100);
        criteriaYPos = new ArrayList();
        criteriaYPos.ensureCapacity(100);
        double[] ret = new double[2];

        ret[0] = Double.POSITIVE_INFINITY;
        ret[1] = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < trajectory.size(); i++) {
            ValueBlock vblock = (ValueBlock) trajectory.get(i);
            //System.out.println("getMinMaxandScaled " + i + "\t" + vblock);
            double curval = Double.NaN;
            /*if (!mv.FULL_CRITERIA)
                curval = 1 - Math.abs(vblock.full_criterion);
            else*/
            curval = vblock.full_criterion;

            if (curval < ret[0])
                ret[0] = curval;
            if (curval > ret[1])
                ret[1] = curval;

            /*currently does not display the mean criterion*/
            double[] add = {vblock.all_criteria[ValueBlock_STATIC.expr_MEAN_IND],
                    vblock.all_criteria[ValueBlock_STATIC.expr_MSE_IND],
                    vblock.all_criteria[ValueBlock_STATIC.expr_FEM_IND],
                    vblock.all_criteria[ValueBlock_STATIC.expr_KEND_IND],
                    vblock.all_criteria[ValueBlock_STATIC.expr_COR_IND],
                    vblock.all_criteria[ValueBlock_STATIC.expr_EUC_IND],
                    vblock.all_criteria[ValueBlock_STATIC.expr_SPEARMAN_IND],
                    vblock.all_criteria[ValueBlock_STATIC.interact_IND],
                    vblock.all_criteria[ValueBlock_STATIC.feat_IND],
                    vblock.all_criteria[ValueBlock_STATIC.TF_IND]};
            try {
                //double[] doubles = stat.log(stat.add(add, 1), 10);

                double[] doubles = MoreArray.copy(add);
                //System.out.println("adding criteria " + MoreArray.toString(doubles, ","));
                criteria.add(doubles);
                //double[] get = (double[]) criteria.get(criteria.size()-1);
                //System.out.println("adding criteria get " + MoreArray.toString(get, ","));
            } catch (Exception e) {
                System.out.println(criteria.size());
                e.printStackTrace();
            }
            try {
                double[] logadd = stat.log(stat.add(add, 1), 10);
                criteriaYPos.add(logadd);
                //System.out.println("adding criteriaYPos " + MoreArray.toString(logadd, ","));

            } catch (Exception e) {
                System.out.println(criteriaYPos.size());
                e.printStackTrace();
            }
        }
        //System.out.println("getMinMaxandScaled min/max " + ret[0] + "\t" + ret[1]);
        for (int i = 0; i < criteriaYPos.size(); i++) {
            double[] cur = (double[]) criteriaYPos.get(i);
            //System.out.println("getMinMaxandScaled " + cur.doubleValue() + "\t" +
            //        graphRange + "\t" + (cur.doubleValue() * graphRange));
            for (int j = 0; j < cur.length; j++) {
                if (j < cur.length - 1)
                    cur[j] = (graphRange - (cur[j] / master_scale_max) * graphRange);
                else
                    cur[j] = (graphRange - (Math.abs(cur[j]) / master_scale_max) * graphRange);
            }
            //System.out.println("adding criteriaYPos transform " + MoreArray.toString(cur, ","));
            criteriaYPos.set(i, cur);
        }

        return ret;
    }

    /**
     * Data expected in genes x experiments orientation
     *
     * @param gene_data_index
     * @param exp_data_index
     * @param ycoord
     * @param xcoord
     * @param ig
     * @return
     */
    private double fillCell(int gene_data_index, int exp_data_index, int ycoord, int xcoord, Graphics2D ig) {
        Color curColor = Color.white;
        double val = Double.NaN;
        try {
            val = mv.mvb.dataexpr[gene_data_index][exp_data_index];
            /*Exception case.*/
            if (Double.isNaN(val)) {
                curColor = Color.LIGHT_GRAY;
            }
            /*Data case.*/
            else if (!Double.isNaN(val)) {
                if (threshold_color) {
                    if (val < mv.mvb.dataexpr_min_cut)
                        val = mv.mvb.dataexpr_min_cut;
                    else if (val > mv.mvb.dataexpr_max_cut)
                        val = mv.mvb.dataexpr_max_cut;
                    else
                        val = val / (mv.mvb.dataexpr_max_cut - mv.mvb.dataexpr_min_cut);
                } else
                    val = val / (mv.mvb.dataexpr_max - mv.mvb.dataexpr_min);
                try {
                    curColor = cm.getColorTruncate(val, 0, 1);
                } catch (Exception e) {
                    //System.out.println("MinerViewCanvas fillCell ISSUE with value " + val);
                    //e.printStackTrace();
                }
            }
            ig.setColor(curColor);
            //reverses exp vs gene to gene vs exp orientation
            final int xr = xcoord * xunit + xOffset;
            final int yr = ycoord * yunit + yOffset;
            if (debug)
                System.out.println("MinerViewCanvas fillCell xcoord " + xcoord + "\tycoord " +
                        ycoord + "\t" + val + "\t" + curColor.getRed() + "\t" + curColor.getGreen() + "\t" + curColor.getBlue());
            ig.fillRect(xr, yr, xunit, yunit);

        } catch (Exception e) {
            System.out.println("MinerViewCanvas fillCell ISSUE " + gene_data_index + "\t" + exp_data_index);
            System.out.println("MinerViewCanvas fillCell ISSUE " + mv.mvb.dataexpr.length + "\t" +
                    mv.mvb.dataexpr[0].length);
            //e.printStackTrace();
        }
        //System.out.println("MinerViewCanvas fillCell " + gene_data_index + "\t" + exp_data_index + "\t" + val);
        return val;
    }

    /**
     * The paint method which controls sequential drawing of color-grid elements.
     */
    public void drawStuff() {
        //System.out.println("MinerViewCanvas drawStuff " + sizex + "\t" + sizey + "\t" + curTrajectoryPosition + "\t" + curYPosition);
        ig.setColor(Color.white);
        ig.fillRect(0, 0, sizex, sizey);
        //System.out.println("MinerViewCanvas drawing...");
        if (trajectory != null)
            ig = drawTrajectory(ig);
        /*ig.setColor(Color.black);
        ig.drawLine(0, trackOffset - 2, sizex, trackOffset - 2);
        ig.drawLine(0, trackOffset, sizex, trackOffset);*/
    }

    /**
     * The paint method which controls sequential drawing of color-grid elements.
     */
    public void drawStuff(boolean changed) {
        //System.out.println("MinerViewCanvas drawStuff " + sizex + "\t" + sizey + "\t" + curTrajectoryPosition + "\t" + curYPosition);
        ig.setColor(Color.white);
        ig.fillRect(0, 0, sizex, sizey);
        //System.out.println("MinerViewCanvas drawing...");
        if (trajectory != null)
            ig = drawTrajectory(ig);
        /*ig.setColor(Color.black);
        ig.drawLine(0, trackOffset - 2, sizex, trackOffset - 2);
        ig.drawLine(0, trackOffset, sizex, trackOffset);*/
        repaint();
    }

    /**
     * @param value
     */
    public void setXOffset(int value) {
        curTrajectoryPosition = (int) ((double) value / (double) mv.mvb.canvdimx);
        //System.out.println("MinerViewCanvas setXOffset " + curTrajectoryPosition + "\t" + value + "\t" + mv.mvb.canvdimx);
        drawStuff(true);
    }

    /**
     * @param value
     */
    public void setYOffset(int value) {
        curYPosition = (int) ((double) (value - yOffset) / (double) yunit);//(double) mv.mvb.canvdimy);
        if (curYPosition < 0)
            curYPosition = 0;
        // System.out.println("MinerViewCanvas setYOffset " + curYPosition + "\t" + value + "\t" + mv.mvb.canvdimx);
        drawStuff(true);
    }

    /**
     * @param file
     */
    public void writecanJPEG(String file) {
        curTrajectoryPosition = 0;
        System.out.println("writecanJPEG " + file);
        /*  sizecanvx = sizex;
      sizecanvy = sizey;*/
        System.out.println("preparing to write jpg file " + file + "\t" + sizex + "\t" + sizey);
        //java.awt.image.BufferedImage bi = cgcan.screenShot();
        java.awt.image.BufferedImage bi = new java.awt.image.BufferedImage(sizex, sizey, java.awt.image.BufferedImage.TYPE_INT_RGB);
        System.out.println("finished preparing");
        try {

            /*BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
            JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bi);
            int quality = 100;
            param.setQuality((float) quality / 100.0f, false);
            encoder.setJPEGEncodeParam(param);
            encoder.encode(bi);
            out.close();*/

            FileOutputStream f = new FileOutputStream(file);
            Graphics2D g = bi.createGraphics();
            paint(g);

            g.dispose();

            try {
                //fos = new FileOutputStream("image.jpg");
                ImageIO.write(bi, "jpg", f);
                f.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            /*JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(f);
            JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bi);
            param.setQuality((float) 100, true);
            param.setXDensity(sizex);
            param.setYDensity(sizey);
            System.out.println("preparing to encode image");
            encoder.encode(bi, param);
            g.dispose();
            f.close();*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param file
     */
   /* public void writecanEPS(String file) {
        int tmp = curTrajectoryPosition;
        curTrajectoryPosition = 0;
        System.out.println("writecanEPS " + file);
        ParsePath pp = new ParsePath(file);
        Graphics2D cp = ig;
        try {
            FileOutputStream f = new FileOutputStream(file);
            EpsGraphics2D g = new EpsGraphics2D(pp.getName(), f, 0, 0, sizex, sizey);
            paint((Graphics2D) g);
            g.dispose();
            f.flush();
            f.close();
            paint(cp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    /**
     * For JScrollable
     *
     * @return
     */
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    /**
     * For JScrollable
     *
     * @param visibleRect
     * @param orientation
     * @param direction
     * @return
     */
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return (orientation == SwingConstants.HORIZONTAL) ? visibleRect.width : visibleRect.height;
    }

    /**
     * For JScrollable
     *
     * @return
     */
    public boolean getScrollableTracksViewportHeight() {
        boolean tracks = false;
        if (getParent() instanceof JViewport) {
            tracks = getPreferredSize().getHeight() <
                    ((JViewport) getParent()).getExtentSize().getHeight();
        }
        return tracks;
    }

    /**
     * For JScrollable
     *
     * @return
     */
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }


    /**
     * For JScrollable
     *
     * @param visibleRect
     * @param orientation
     * @param direction
     * @return
     */
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return getScrollableBlockIncrement(visibleRect, orientation, direction) / 5;
    }

}

