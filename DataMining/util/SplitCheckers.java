package DataMining.util;

import DataMining.MINER_STATIC;
import DataMining.ValueBlock;
import DataMining.ValueBlockList;
import DataMining.ValueBlockListMethods;
import mathy.Matrix;
import mathy.SimpleMatrix;
import mathy.stat;
import util.MoreArray;

import java.util.ArrayList;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: 1/25/16
 * Time: 5:17 PM
 */
public class SplitCheckers {


    /**
     * @param args
     */
    public SplitCheckers(String[] args) {

        SimpleMatrix sm = new SimpleMatrix(args[0]);

        try {
            ValueBlockList vbl = ValueBlockListMethods.readAny(args[1], false);

            ValueBlockList newout = new ValueBlockList();
            for (int i = 0; i < vbl.size(); i++) {

                //System.out.print(".");
                ValueBlock vb = (ValueBlock) vbl.get(i);

                vb.setDataAndMean(sm.data);

                double[] colm = Matrix.avgOverCols(vb.exp_data);
                double[] rowm = Matrix.avgOverRows(vb.exp_data);
                //MoreArray.printArray(colm);

                int[] posnegcol = new int[colm.length];
                int[] posnegrow = new int[rowm.length];

                for (int a = 0; a < colm.length; a++) {
                    if (colm[a] > 0)
                        posnegcol[a] = 1;
                    else
                        posnegcol[a] = -1;
                }
                for (int b = 0; b < rowm.length; b++) {
                    if (rowm[b] > 0)
                        posnegrow[b] = 1;
                    else
                        posnegrow[b] = -1;
                }
                //MoreArray.printArray(posnegcol);
                //MoreArray.printArray(posnegrow);

                int poscol = stat.countOccurence(posnegcol, 1);
                int posrow = stat.countOccurence(posnegrow, 1);

                System.out.println("poscol " + poscol + "\tposrow " + posrow);

                ValueBlock plus_topL = createCheckerPart(i, vb, posnegcol, posnegrow, true, true);
                ValueBlock minus_topR = createCheckerPart(i, vb, posnegcol, posnegrow, true, false);
                ValueBlock minus_bottomL = createCheckerPart(i, vb, posnegcol, posnegrow, false, true);
                ValueBlock plus_bottomR = createCheckerPart(i, vb, posnegcol, posnegrow, false, false);

                int[] done = new int[4];
                if (plus_topL != null) {
                    plus_topL.move_class = (i + 1) + "_plusplus";//plus_topL.index +
                    newout.add(plus_topL);
                    done[0] = 1;
                }
                if (minus_topR != null) {
                    minus_topR.move_class = (i + 1) + "_plusminus";//minus_topR.index +
                    newout.add(minus_topR);
                    done[1] = 1;
                }
                if (minus_bottomL != null) {
                    minus_bottomL.move_class = (i + 1) + "_minusplus";//minus_bottomL.index +
                    newout.add(minus_bottomL);
                    done[2] = 1;
                }
                if (plus_bottomR != null) {
                    plus_bottomR.move_class = (i + 1) + "_minusminus";//plus_bottomR.index +
                    newout.add(plus_bottomR);
                    done[3] = 1;
                }

                System.out.println(MoreArray.toString(done, "\t"));

            }
            System.out.println();

            String s2 = args[1].substring(0, args[1].lastIndexOf(".")) + "_splitcheckers.txt";
            String out2 = newout.toString(newout.header != null ? newout.header : "#" + MINER_STATIC.HEADER_VBL);//ValueBlock.toStringShortColumns());
            System.out.println("writing " + s2);

            util.TextFile.write(out2, s2);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    /**
     * @param posnegcol
     * @param posnegrow
     * @param top
     * @param left
     * @return
     */
    public ValueBlock createCheckerPart(int index, ValueBlock myvb, int[] posnegcol, int[] posnegrow, boolean top, boolean left) {

        ArrayList cols = new ArrayList();
        ArrayList rows = new ArrayList();

        if (top && left) {
            for (int i = 0; i < posnegrow.length; i++) {
                if (posnegrow[i] == 1)
                    rows.add(myvb.genes[i]);
            }
            for (int i = 0; i < posnegcol.length; i++) {
                if (posnegcol[i] == 1)
                    cols.add(myvb.exps[i]);
            }
        } else if (top && !left) {
            for (int i = 0; i < posnegrow.length; i++) {
                if (posnegrow[i] == 1)
                    rows.add(myvb.genes[i]);
            }
            for (int i = 0; i < posnegcol.length; i++) {
                if (posnegcol[i] == -1)
                    cols.add(myvb.exps[i]);
            }
        } else if (!top && left) {
            for (int i = 0; i < posnegrow.length; i++) {
                if (posnegrow[i] == -1)
                    rows.add(myvb.genes[i]);
            }
            for (int i = 0; i < posnegcol.length; i++) {
                if (posnegcol[i] == 1)
                    cols.add(myvb.exps[i]);
            }
        } else if (!top && !left) {
            for (int i = 0; i < posnegrow.length; i++) {
                if (posnegrow[i] == -1)
                    rows.add(myvb.genes[i]);
            }
            for (int i = 0; i < posnegcol.length; i++) {
                if (posnegcol[i] == -1)
                    cols.add(myvb.exps[i]);
            }
        }

        ValueBlock valueBlock = null;
        try {
            if (rows.size() > 0 && cols.size() > 0)
                valueBlock = new ValueBlock(rows, cols);
            else
                System.out.println(index + "\tsize(s) = 0 " + rows.size() + "\t" + cols.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return valueBlock;
    }


    /**
     * @param args
     */
    public final static void main(String[] args) {
        if (args.length == 2) {
            SplitCheckers rm = new SplitCheckers(args);
        } else {
            System.out.println("syntax: java DataMining.util.SplitCheckers\n" +
                    "<expr data>\n" +
                    "<value block list>"
            );
        }
    }
}
