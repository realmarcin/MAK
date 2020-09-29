package bioobj;

import dtype.Branch;
import dtype.Salact;
import mathy.Metrics;
import util.*;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;


/**
 * Class for Tree object. Includes parsers and various tools for construction
 * and modeling of dendrograms.
 *
 * @author Marcin Joachimiak.
 * @version 1.0 due to original intepretation of a tree in jevtrace the coordinate system is reversed ie x =y, y =x ...
 */
public class Tree {

    /**
     * Toggle for log activity mode.
     */
    private boolean logon;
    private LogFile lf;

    int[][] modif;
    /*MoreArray storing the order of names in the dendrogram.*/
    public int[] nameorder;
    /*MoreArray of indications whether a node has been added from raw to graphical dendrogram.*/
    boolean[] added;
    /*Variable storing the dendrogram in <code>Branch</code> format.*/
    public ArrayList points;
    /*Variable representing a <code>ArrayList</code> of subtrees, each of these in turn a <code>ArrayList</code>.*/
    private ArrayList dndtree;
    /*Variable storing the name labels for each terminal node of the dendrogram.*/
    public ArrayList names;
    public ArrayList openclose;

    ArrayList nodes;
    /*MoreArray storing the pairwise distances of a set of sequences. An independent instance of bioobj.Tree IN DEVELOPMENT.*/
    double[][] dists;

    int elem;
    /*Variable representing the graphic height of the line in the dendrogram.*/
    int yspacing;

    double scale;

    double min;
    double max;
    /*Variable used in the distance matrix instance.*/
    int miniseq;
    /*Variable used in the distance matrix instance.*/
    int minjseq;
    /*Variable used to scale the dendrogram data to graphical output.*/
    public double scaletree = (double) 4.3;
    /*Variable storing the minimum y coordinate of a node.*/
    public double absminfortree = (double) 5.0;

    private double lastxavg;
    private double lasty = -1;
    private double lastbranchlen;
    public double interMinY;
    public double clustscale = (double) 1000.0;
    /*Variable indicating whether a the dendrogram has a different order of sequences relative to the MSA.*/
    public boolean changenameorder;

    /*Variable storing the location of the CLUSTALW file. */
    public String dndfile;
    private String tmpdndfile;
    /*Variable storing the location of the MSF file. */
    String msffile;
    /*Variable encoding the graphical accuracy of the dendrogram.*/
    public double ACCURACY = (double) .00001;

    private boolean clustal = false;

    private int maxclose = -1;
    private int maxopen = -1;

    public String supfam;

    ArrayList curlist, sel;
    double minY;
    int[] curlist_array;

    /**
     *
     */
    public Tree() {

    }

    /**
     *
     */
    public Tree(ArrayList p) {

        points = ArrayListCopy.copy(p);
    }

    /**
     *
     */
    public Tree(ArrayList p, double m) {

        minY = m;
        points = ArrayListCopy.copy(p);
    }

    /**
     *
     */
    public Tree(ArrayList p, double m, ArrayList s) {

        minY = m;
        points = ArrayListCopy.copy(p);
        sel = ArrayListCopy.copy(s);
    }

    /**
     * Class encompassing data objects and methods for trees.
     *
     * @param data  distance matrix of sequences
     * @param accur value of dendrogram accuracy
     * @param lineh value of dendrogram lineHeight
     */
    public Tree(double[][] data, double accur, int lineh, boolean dnd) {

        ACCURACY = accur;
        clustal = dnd;

        if (data.length == data[0].length) {

            yspacing = lineh;
            elem = data.length;
            elem = data.length;
            dists = data;
            deScale();
            makeTree();
        }
//else
//System.out.println("Not for use with nonsymmetric data sets (ie rect. matrices).");
    }

    /**
     * One of current instantiations by JEvTrace, uses Tree public read_tree methods.
     *
     * @param accur <code>double</code> representing accuracy
     * @param lineh <code>int</code> representing height of a line
     */
    public Tree(double accur, int lineh, boolean dnd) {

        ACCURACY = accur;
        yspacing = lineh;
        clustal = dnd;
    }

    /**
     * One of current instantiations by JEvTrace, creates a demo dendrogram made of one Branch.
     *
     * @param accur <code>double</code> representing accuracy
     * @param lineh <code>int</code> representing height of a line
     * @param b     <code>Branch</code> representing a demo dendrogram with a single Branch
     * @see dtype.Branch
     */
    public Tree(double accur, int lineh, Branch b) {
        ACCURACY = accur;
        yspacing = lineh;
        points = new ArrayList();
        points.add((Branch) b);
    }

    /**
     * @param data
     */
    private void setAbsMinForTree(double[] data) {
        double ret = -1;

//        int count = 0;
//        Iterator it = points.iterator();
//        while (it.hasNext()) {
        for (int i = 0; i < points.size(); i++) {
//            Branch b = (Branch) it.next();
            Branch b = (Branch) (points.get(i));
            if (b != null) {
                ret = b.y[0];
                if (ret == data[2])
                    b.y[0] = absminfortree;
                ret = b.y[3];
                if (ret == data[2])
                    b.y[3] = absminfortree;
                points.set(i, b);
            }
//            count++;
        }

    }

    /**
     * Function to return the points as a new <code>ArrayList</code>.
     *
     * @return ArrayList ret new vector copy of points (even if NULL)
     */
    public ArrayList retPoints() {
        ArrayList ret = new ArrayList();
        if (points != null) {
//            Iterator it = points.iterator();
//            while (it.hasNext()) {
            for (int i = 0; i < points.size(); i++) {

//                ret.add((Branch) it.next());
                ret.add((Branch) points.get(i));
            }
        }
        return ret;
    }

    /**
     * Function to return values for coordinate min/max extents of the dendrogram.
     */
    public double[] treeExtents() {
        //0 = minX
        //1 = maxX
        //2 = minY
        //3 = maxY

        double[] ret = new double[4];

        if (points != null && points.size() > 0) {
            Branch b = (Branch) (points.get(0));
            ret[3] = b.y[1];
            ret[2] = b.y[0];
            ret[1] = b.x[1];
            ret[0] = b.x[0];

            int start = 0;
            if (clustal == true)
                start = 1;
            for (int i = start; i < points.size(); i++) {
                b = (Branch) (points.get(i));
                if (b == null)
                    continue;
                //minX
                if (ret[0] > b.x[0])
                    ret[0] = b.x[0];
                if (ret[0] > b.x[3])
                    ret[0] = b.x[3];

                //maxX
                if (ret[1] < b.x[1])
                    ret[1] = b.x[1];
                if (ret[1] < b.x[2])
                    ret[1] = b.x[2];

                //maxY
                if (ret[3] < b.y[1])
                    ret[3] = b.y[1];
                if (ret[3] < b.y[2])
                    ret[3] = b.y[2];

                //minY
                if (ret[2] > b.y[0])
                    ret[2] = b.y[0];
                if (ret[2] > b.y[3])
                    ret[2] = b.y[3];
            }
        }

        ret[3] += 1;
        return ret;
    }
/*
*/

    /**
     * scales raw distance data for display in jevtrace
     */
    private void deScale() {

        for (int i = 0; i < dists.length; i++)
            for (int j = 0; j < dists.length; j++) {
                if (dists[i][j] < min) {
                    min = dists[i][j];
                    if (i != j) {
                        miniseq = i;
                        minjseq = j;
                    }
                }
                if (dists[i][j] > max)
                    max = dists[i][j];
            }

// in this case want a number that when multiplied with max gives 1 (ie the
// maximum size of the canvas)
// and when multiplied by min gives 0 (ie min size of canvas)
// this is determined by digit requirements for graphics
        scale = (max - min);

        for (int i = 0; i < dists.length; i++)
            for (int j = 0; j < dists.length; j++) {
                dists[i][j] = dists[i][j] / scale;
            }
    }
/* scales raw clustalw tree for jevtrace
*/

    private final void scaleClustal() {

        if (points != null) {

//            int count = 0;
//            Iterator it = points.iterator();
//            while (it.hasNext()) {
            for (int i = 0; i < points.size(); i++) {

//                Branch b = (Branch) it.next();
                Branch b = (Branch) points.get(i);
                b.y[0] *= clustscale;
                b.y[1] *= clustscale;
                b.y[2] *= clustscale;
                b.y[3] *= clustscale;
                points.set(i, (Branch) b);
//                count++;
            }
        }

    }

    /**
     * Adds branches for each node.
     * all distances must be scaled to 450/100
     */
    private void makeTree() {
        nodes = new ArrayList();
        //adds leaves
        for (int i = 0; i < dists.length; i++) {
            Branch add = new Branch();
            for (int a = 0; a < 4; a++) {
                add.y[a] = i * 2 * yspacing;
                add.x[a] = 0;
            }
            nodes.add(add);
        }

        int[] done = new int[dists.length];

        double curdist = dists[miniseq][minjseq];
        Branch add = new Branch();
        add.y[0] = miniseq * 2 * yspacing;
        add.x[0] = 0;
        add.y[4] = minjseq * 2 * yspacing;
        add.x[4] = 0;
    }


    /**
     * Function which builds a binary tree structure form branches.
     * Right and left children are placed at (2*(parent+1)-1) and (2*parent)+1.
     * Leaves are assigned as null children.
     *
     * @param pos
     * @param src
     * @param dst
     * @param end
     */
    private void placeChildrenDND(int pos, ArrayList src, ArrayList dst, int end) {

        int placeleft = ((2 * (pos + 1)) - 1);
        int placeright = (2 * (pos + 1));
//System.out.println("PLACING children  "+pos+"\t at LEFT "+placeleft+"\t and or RIGHT "+placeright);
        Branch hej = (Branch) dst.get(pos);
        double midy = hej.y[1];
        double lefty = hej.y[0];
        double leftx = hej.x[0];
        double righty = hej.y[3];
        double rightx = hej.x[3];

        if (lefty == 0) {
            if (placeleft >= dst.size()) {
                dst.ensureCapacity(placeleft + 1);
            }
            //Branch work = new Branch
            dst.set(placeleft, null);
        } else {
            for (int j = 1; j < end; j++) {
                if (added[j] == false) {
                    Object o = src.get(j);
                    if (o == null)
                        continue;
                    Branch cur = (Branch) o;
                    double maybey = cur.y[1];
                    double maybexr = cur.x[0];
                    double maybexl = cur.x[3];

                    if (maybexr > maybexl) {
                        double tmp = maybexr;
                        maybexr = maybexl;
                        maybexl = tmp;
                    }
                    if (Math.abs(maybey - lefty) < ACCURACY && leftx >= maybexr && leftx <= maybexl) {
                        src.set(j, null);
                        added[j] = true;
                        if (placeleft >= dst.size())
                            dst.ensureCapacity(placeleft + 1);
                        dst.set(placeleft, cur);
//System.out.println("placing  "+pos+"  L  "+placeleft);
                        placeChildren(placeleft, src, dst, end);
                        break;
                    }
                }
            }

        }

        if (righty == 0) {
            if (placeright >= dst.size())
                dst.ensureCapacity(placeright + 1);
            else
                dst.set(placeright, null);
        } else {

            for (int j = 1; j < end; j++) {
                if (added[j] == false) {
                    Object o = src.get(j);
                    if (o == null)
                        continue;
                    Branch cur = (Branch) o;
                    double maybey = cur.y[1];
                    double maybexr = cur.x[0];
                    double maybexl = cur.x[3];
                    if (maybexr > maybexl) {
                        double tmp = maybexr;
                        maybexr = maybexl;
                        maybexl = tmp;
                    }
                    if (Math.abs(maybey - righty) < ACCURACY && rightx >= maybexr && rightx <= maybexl) {
                        src.set(j, null);
                        added[j] = true;
                        if (placeright >= dst.size())
                            dst.ensureCapacity(placeright + 1);
                        dst.set(placeright, cur);
//System.out.println("placing  "+pos+"   R  "+placeright);
                        placeChildren(placeright, src, dst, end);
                        break;
                    }
                }
            }

        }
    }

    /**
     * Function which builds a binary tree structure form branches.
     * Right and left children are placed at (2*(parent+1)-1) and (2*parent)+1.
     * Leaves are assigned as null children.
     */
    private void placeChildren(int pos, ArrayList src, ArrayList dst, int end) {

        int placeleft = ((2 * (pos + 1)) - 1);
        int placeright = (2 * (pos + 1));
//System.out.println("PLACING children  "+pos+"\t at LEFT "+placeleft+"\t and or RIGHT "+placeright);
        Branch hej = (Branch) dst.get(pos);
        double midy = hej.y[1];
        double lefty = hej.y[0];
        double leftx = hej.x[0];
        double righty = hej.y[3];
        double rightx = hej.x[3];

        if (lefty == 0) {

            if (placeleft >= dst.size()) {

                dst.ensureCapacity(placeleft + 1);
            }
            //Branch work = new Branch
            dst.set(placeleft, null);
        } else {
            for (int j = 1; j < end; j++) {

                if (added[j] == false) {

                    Object o = src.get(j);

                    if (o == null)
                        continue;
                    Branch cur = (Branch) o;
                    double maybey = cur.y[1];

                    if (Math.abs(maybey - lefty) < ACCURACY) {

                        src.set(j, null);
                        added[j] = true;

                        if (placeleft >= dst.size())
                            dst.ensureCapacity(placeleft + 2);

//System.out.println(dst.size()+"\tplaceleft "+placeleft);


                        if (placeleft >= dst.size()) {

                            int i = placeleft - dst.size();

                            while (i > -1) {

                                dst.add(null);
                                i++;
                            }
                        }

                        if (dst.size() > 0 && placeleft < dst.size())
                            dst.set(placeleft, cur);

//System.out.println("placing  "+pos+"  L  "+placeleft);
                        placeChildren(placeleft, src, dst, end);
                        break;
                    }
                }
            }
        }
        if (righty == 0) {

            if (placeright >= dst.size())
                dst.ensureCapacity(placeright + 1);
            else
                dst.set(placeright, null);
        } else {

            for (int j = 1; j < end; j++) {

                if (added[j] == false) {

                    Object o = src.get(j);
                    if (o == null)
                        continue;
                    Branch cur = (Branch) o;
                    double maybey = cur.y[1];

                    if (Math.abs(maybey - righty) < ACCURACY) {

                        src.set(j, null);
                        added[j] = true;
                        if (placeright >= dst.size())
                            dst.ensureCapacity(placeright + 1);

                        dst.set(placeright, cur);
//System.out.println("placing  "+pos+"   R  "+placeright);
                        placeChildren(placeright, src, dst, end);
                        break;
                    }
                }
            }
        }

    }

    /**
     * Makes PILEUP tree well-spaced, necessary due to PILEUP numerical thisjevtrace.accuracy issue.
     * Separates all branches into pixel distinct subfamilies.
     *
     * @param d
     * @return ArrayList d
     */
    private ArrayList nonRed(ArrayList d) {

        double addval = .001;

//        int count = 0;
//        Iterator it = d.iterator();
//        while (it.hasNext()) {
        for (int i = 0; i < d.size(); i++) {
//            Branch cur = (Branch) it.next();
            Branch cur = (Branch) d.get(i);
            if (cur.y[0] == cur.y[1] || cur.y[3] == cur.y[1]) {

                if (logon)
                    lf.aw("MAKING NONRED " + cur.fulltoString() + "\n");
                double nowlasty = cur.y[1];
                cur.y[1] += addval;
                cur.y[2] += addval;
                double rx = cur.x[0];
                double lx = cur.x[3];
                d.set(i, cur);

                if (logon)
                    lf.aw("MADE NONRED " + cur.toString() + "\n");

                d = searchFigureYandAdd(d, nowlasty, rx, lx, addval, i);
            }
//            count++;
        }

        return d;
    }

    /**
     * Reverses the order of elements in (points) ArrayList.
     */
    private void reversePoints() {

        ArrayList tmp = new ArrayList();
//        Iterator it = points.iterator();
//        while (it.hasNext()) {
        for (int i = 0; i < points.size(); i++) {
//            Branch b = (Branch) it.next();
            Branch b = (Branch) points.get(i);
            tmp.add(new Branch(b));
        }

        points = new ArrayList();
//        it = tmp.iterator();
//        while (it.hasNext()) {
        for (int i = tmp.size() - 1; i > -1; i--) {
//            Branch b = (Branch) it.next();
            Branch b = (Branch) tmp.get(i);
            points.add(new Branch(b));
        }
    }

    /**
     * Sort points by distance to the 'root'.
     */
    private void sortNewick() {

        ArrayList tmp = new ArrayList();

        int k = 0;

        while (points != null && k != -1) {

            k = findMintoRootBranch(points, -1, -1);
            if (k != -1) {

                Branch b = (Branch) points.get(k);
                tmp.add(new Branch(b));
                points.remove(k);
            }
        }

//        int count = 0;
//        Iterator it = tmp.iterator();
//        while (it.hasNext()) {
        for (int i = 0; i < tmp.size(); i++) {

//            Branch b = (Branch) it.next();
            Branch b = (Branch) tmp.get(i);
            points.add(i, new Branch(b));
//            count++;
        }
        tmp = null;
    }

    /**
     * Function which calls a sort of the tree from parent to child nodes.
     *
     * @param p ArrayList of tree nodes
     */
    private ArrayList sortTree(ArrayList p) {

        //System.out.println("sortTree "+p.size());
        added = new boolean[p.size()];
        ArrayList sortedtree = new ArrayList();
        sortedtree.add(p.get(0));
        added[0] = true;
        if (clustal)
            placeChildrenDND(0, p, sortedtree, p.size());
        else
            placeChildren(0, p, sortedtree, p.size());
        //System.out.println("sorted tree size  "+sortedtree.size());
        return (sortedtree);
    }

    /**
     * Function which calls a sort of the tree from parent to child nodes.
     *
     * @param p ArrayList of tree nodes
     */
    private final static int findClustalRoot(ArrayList p) {

        int ret = -1;
        double thismin = 10000;

//        int count = 0;
//        Iterator it = p.iterator();
//        while (it.hasNext()) {
        for (int i = 0; i < p.size(); i++) {
//            Branch b = (Branch) it.next();
            Branch b = (Branch) p.get(i);
            thismin = (thismin > b.y[2]) ? thismin : b.y[2];
            ret = i;
            i++;
        }

        return ret;
    }

    /**
     * Searches for the shallowest node in a NEXUS dendrogram.
     *
     * @param c dendrogram data to search
     * @return int ret index of shallowest node
     */
    private static int findShallow(ArrayList c) {

        int ret = -1;
        double mindist = 100;
//        int count = 0;
//        Iterator it = c.iterator();
//        while (it.hasNext()) {
        for (int i = 0; i < c.size(); i++) {

//            ClustalNode now = (ClustalNode) it.next();
            ClustalNode now = (ClustalNode) c.get(i);
            double x = now.x.doubleValue();
            //System.out.println("node "+i+"\txval "+x);

            if (x < mindist) {

                mindist = x;
                ret = i;
            }
//            count++;
        }

        if (ret == -1 && c.size() == 1)
            ret = 0;
//System.out.println("SHALLOW  "+ret+"  "+mindist);
        return ret;
    }

    /**
     * Tests whether a Branch is connected in a binary dendrogram.
     *
     * @param p dendrogram data to search
     * @param c Branch in ArrayList p to test
     * @return int ret index of shallowest node
     */
    private static int testConnectBranch(ArrayList p, int c) {

        int ret = -1;
        Branch now = (Branch) p.get(c);

//        int count = 0;
//        Iterator it = p.iterator();
//        while (it.hasNext()) {
        for (int i = 0; i < p.size(); i++) {
//            Branch naaw = (Branch) it.next();
            if (i != c) {
                Branch naaw = (Branch) p.get(i);
                if (now.y[1] == naaw.y[0] || now.y[1] == naaw.y[3]) {
                    ret = i;
                    break;
                }
            }
//            count++;
        }

        return ret;
    }

    /**
     * Tests whether a Branch is right child connected in a binary dendrogram.
     *
     * @param p dendrogram data to search
     * @param c Branch in ArrayList p to test
     * @return int ret index of shallowest node
     */
    private static int testRightLooseBranch(ArrayList p, int c) {
        int ret = -1;
        Branch now = (Branch) p.get(c);

        if (now.x[2] != -1 && now.y[3] != -1) {
            ret = c;
        }

        return ret;
    }


    /**
     * Tests whether a Branch is left child connected in a binary dendrogram.
     *
     * @param p dendrogram data to search
     * @param c Branch in ArrayList p to test
     * @return int ret index of shallowest node
     */
    private static int testLeftLooseBranch(ArrayList p, int c) {
        int ret = -1;
        Branch now = (Branch) p.get(c);

        if (now.x[1] != -1 && now.y[0] != -1) {
            ret = c;
        }

        return ret;
    }

    /**
     * since the root in nexus tree is set at 1 (100% id )
     * the node closest to the root has the largest distance
     * to the single seq nodes
     */
    private static int findMintoRoot(ArrayList c) {

        int ret = -1;
        double maxdist = 0;

//        int count = 0;
//        Iterator it = c.iterator();
//        while (it.hasNext()) {
        for (int i = 0; i < c.size(); i++) {
//            ClustalNode now = (ClustalNode) it.next();
            ClustalNode now = (ClustalNode) c.get(i);
            double x = now.x.doubleValue();
            if (x > maxdist) {
                maxdist = x;
                ret = i;
            }
            i++;
        }
        return ret;
    }

    /**
     * since the root in nexus tree is set at 1 (100% id )
     * the node closest to the root has the largest distance
     * to the single seq nodes
     */
    private int findMintoRootBranch(ArrayList c, int start, int not) {

        if (logon)
            lf.aw("findMintoRootBranch vector size " + c.size() + "\tstart " + start + "\tnot " + not + "\n");

        int ret = -1;
        double maxdist = 0;
        int go = 0;
        if (start != -1)
            go = start;

//        int count = 0;
//        Iterator it = c.iterator();
//        while (it.hasNext()) {
        for (int i = go; i < c.size(); i++) {
//            Branch b = (Branch) it.next();
            Branch b = (Branch) c.get(i);
            if (i != not) {
                double x = b.y[1];
                if (x > maxdist) {
                    if (logon)
                        lf.aw("maxdist " + maxdist + "\tx " + x + "\n");
                    maxdist = x;
                    ret = i;
                }
            }
//            count++;
        }
        return ret;
    }

    /**
     * Trims tree to fit to canvas (scaling and shortening of terminal branches)
     */
    private ArrayList trimTree(ArrayList sortedtree) {

        min = (double) 10;
        double maxy = 0;

//        int count = 0;
//        Iterator it = sortedtree.iterator();
//        while (it.hasNext()) {
        for (int i = 0; i < sortedtree.size(); i++) {
//            Branch b = (Branch) it.next();
            Branch b = (Branch) sortedtree.get(i);

            if (b != null) {//sortedtree.get(i) != null) {

                if (b.y[1] < min)
                    min = b.y[1];
                if (b.y[1] > maxy)
                    maxy = b.y[1];
            }
        }

        if (min > (double) .1) {

//            count = 0;
//         it = sortedtree.iterator();
//        while (it.hasNext()) {
            for (int i = 0; i < sortedtree.size(); i++) {

//            Branch b = (Branch) it.next();
                Branch b = (Branch) sortedtree.get(i);

                if (b != null) {//sortedtree.get(i) != null) {

                    if (b.y[0] == 0)
                        b.y[0] = min - (double) .25;
                    if (b.y[3] == 0)
                        b.y[3] = min - (double) .25;
                }
            }
            min -= (double) .25;
        } else if (min < .1)
            min = 0;

        double diff = Math.abs(min - maxy);

        scaletree = scaletree / diff;

        for (int i = 0; i < sortedtree.size(); i++) {
            if (sortedtree.get(i) != null) {
                Branch b = (Branch) sortedtree.get(i);
                Branch ba = new Branch();
                for (int j = 0; j < 4; j++) {
                    ba.y[j] = b.y[j] * scaletree;
                    ba.x[j] = b.x[j];
                }

                sortedtree.set(i, (Branch) ba);
            }
        }
        min = min * scaletree;
        return sortedtree;
    }

    /**
     * parses the special case DND tree into two nodes
     */
    private void parseClustBranchSpecial2Node(String k) {

        if (logon)
            lf.aw("parseClustBranchSpecial2Node  " + k + "\n");

        int split1 = k.indexOf(":");
        int split2 = k.indexOf(":", split1 + 1);
        int comma = k.indexOf(",");

        int closeparen = k.indexOf(")");
        String namea = null;
        String nameb = null;

        if (split1 != -1)
            namea = k.substring(1, split1);
        if (split2 != -1)
            nameb = k.substring(comma + 1, split2);

        if (logon)
            lf.aw("parseClustBranchSpecial2Node splitting K " + k + " to namea " + namea + "\tnameb " + nameb + "\n");

        Double add1 = Double.valueOf(k.substring(split1 + 1, comma));
        Double add2 = Double.valueOf(k.substring(split2 + 1, closeparen));
//add1 = new Double(add1.doubleValue()+.1);
//add2 = new Double(add2.doubleValue()+.1);

        ClustalNode ret1 = new ClustalNode(namea, add1);
        ClustalNode ret2 = new ClustalNode(nameb, add2);

        ArrayList add = new ArrayList();

        add.add((ClustalNode) ret1);
        add.add((ClustalNode) ret2);

        dndtree = new ArrayList();
        dndtree.add((ArrayList) add);

        if (logon)
            lf.aw("adding K " + k + " to add1 " + add1 + "\tadd2 " + add2 + "\n");
    }


    /**
     * Parses a single NEXUS format node.
     */
    private ClustalNode parseClustBranch(String k) {

        if (logon)
            lf.aw("parsing Branch  " + k + "\n");

        int split = k.indexOf(":");
        //System.out.println("parseClustBranch "+k+"\t"+split);

        if (split == -1)
            split = k.indexOf(",");
        if (split == -1)
            split = k.length();

        int closeparen = k.indexOf(")");

        String name = null;

        if (split != -1)
            name = k.substring(0, split);
        else if (closeparen != -1)
            name = k.substring(0, closeparen);

//System.out.println("splitting K "+k+" to "+name+"\n");

        if (logon)
            lf.aw("splitting K " + k + " to " + name + "\n");

        //int ident = name.indexOf("|");
        //int slash = name.indexOf("|", ident+1);

        //if(ident != -1 && slash != -1)
        //	name = name.substring(ident+1,slash);

        int bootind = k.indexOf("[");
        Double add = null;

//System.out.println(k+"\t"+split);
        if (bootind == -1 && split < k.length())
            add = Double.valueOf(k.substring(split + 1));
        else if (split < k.length())
            add = Double.valueOf(k.substring(split + 1, bootind));

        if (add == null)
            add = new Double(1);

        ClustalNode ret = new ClustalNode(name, add);

        return ret;
    }

    /**
     * Reads the names in the DND tree
     */
    private ArrayList readClustalTreeNames(String f) {

        ArrayList ret = new ArrayList();

        ret.add((String) "");
        try {
            BufferedReader in = new BufferedReader(new FileReader(new File(f)));
//DataInputStream in = new DataInputStream(new FileInputStream(f));

            String line = in.readLine();

            while (line != null) {
                int colon = line.indexOf(":", 2);
                if (colon != -1) {
                    ClustalNode r = extractNodeLeft(line, 1, -1, -1);
                    if (logon)
                        lf.aw("Extracted node " + r.toString());
                    if (r.a != null) {
                        ret.add((String) r.a);
                    } else {
                        r = extractNodeRight(line, 1, -1, -1);
                        if (logon)
                            lf.aw("Extracted node " + r.toString());
                        if (r.a != null) {
                            ret.add((String) r.a);
                        }
                    }
                }
                line = in.readLine();
            }
        } catch (IOException ex) {
            if (logon)
                lf.aw("ERROR: No Input File <" + f + ">");
        }
//System.out.println("DND tree name size  "+ret.size());
        return ret;
    }

    /**
     * Checks for redundant names.
     */
    public boolean[] testNRNames(ArrayList al) {

        boolean[] ret = new boolean[al.size()];

        for (int i = 0; i < al.size(); i++) {

            String in = (String) al.get(i);

            for (int j = i + 1; j < al.size(); j++) {

                String jn = (String) al.get(j);
                if (in.equals(jn)) {

                    ret[j] = true;
                    System.out.println("Redundant names in phylogeny. i:" + i + "\t" + in + "\tj: " + j + "\t" + jn);
                }
            }
        }

        return ret;
    }

    /**
     * compares order of a name vector to the name vector in the Tree object
     */
    private boolean testNameOrder(ArrayList n) {
        //System.out.println("testing name order ...");
        boolean sameorder = setNameOrder(n);

        return sameorder;
    }

    /**
     * determines and stores the relative order of  and 'n' name vectors
     */
    private boolean setNameOrder(ArrayList n) {
        boolean ret = false;
        //System.out.println("setting name order ...");
        nameorder = new int[n.size()];
        for (int i = 0; i < nameorder.length; i++) {
            nameorder[i] = -1;
        }
        int count = 0;

        //for(int i=0;i<names.size();i++)
        //System.out.println("Tree "+i+"\t"+(String)names.get(i));


        for (int i = 0; i < names.size(); i++) {

            String a = (String) names.get(i);
            for (int j = 0; j < n.size(); j++) {

                String b = (String) n.get(j);

                if (a.equals(b)) {
                    nameorder[i] = j;
                    //System.out.println("name ordering\t"+i+"  "+j);'
                    count++;
                }

            }
        }
        if (count == names.size())
            ret = true;

        return ret;
    }

    public ArrayList retNames() {
        return names;
    }

    /**
     * converts the dnd elements to a binary Branch in the special case of sequences=2
     */
    private void dndTreeToPoints() {
        if (logon)
            lf.aw("IN dndTreeToPoints() :: dndtree.size() " + dndtree.size() + "\n");

        ArrayList now = (ArrayList) dndtree.get(0);
        if (logon)
            lf.aw("FIRST VECTOR IN dndtree.size() SIZE " + now.size() + "\n");

        ClustalNode a = (ClustalNode) now.get(0);
        ClustalNode b = (ClustalNode) now.get(1);
        String nama = a.a;
        String namb = b.a;

        int na = searchNames(nama);
        int nb = searchNames(namb);

        double ab = 0;

        double ka = a.x.doubleValue();
        double kb = b.x.doubleValue();


        if (logon)
            lf.aw("dndTreeToPoints a.open " + a.open + " a.close " + a.close + "\tb.open " + b.open + " b.close " + b.close + "\n");
        int nowopen = (int) (((double) a.open + (double) b.open) / (double) 2);
        int nowclose = (int) (((double) a.close + (double) b.close) / (double) 2);

        if (logon)
            lf.aw("SPECIAL CASE SEQ=2: pair to binary - " + nama + "\tnamea " + na + "\tdista " + ka + "\t" + namb + "\tname b " + nb + "\tdistb " + kb + "\n");

        if (ka != -1 && kb != -1)
            ab = (ka + kb) / (double) 2;

        addClustalPair(ab, na, ka, nb, kb, "children", nowopen, nowclose);
    }

    /**
     * function to read in the original DND file for special case of 2 SEQUENCES
     */
    private void read2NodeDND(String af) {
        try {
            BufferedReader in = new BufferedReader(new FileReader(new File(af)));
//DataInputStream in = new DataInputStream(new FileInputStream(abf));
            String line = new String();
            line = in.readLine();
            parseClustBranchSpecial2Node(line);
        } catch (IOException ex) {
//System.out.println("ERROR: No Input File <"+abf+">");
        }
    }

    public void writePH(String o) {


        try {

            PrintWriter pif = new PrintWriter(new FileWriter(o));

            pif.close();
        } catch (IOException e) {
        }


    }

    /**
     * Tests the tree to see whether the data is in one line - as is the case in most phylogenetic tree output (e.g. Phylip or PAUP*).
     */
    private void testAndReformat(String f) {

        int count = 0, countcomma = 0, countcolon = 0, countparen = 0;
        boolean parencase = false;

        String tree = "";
        ArrayList tr = null;

        try {

            //DataInputStream in = new DataInputStream(new FileInputStream(f));
            BufferedReader in = new BufferedReader(new FileReader(f));
            String line = new String();
            line = in.readLine();

            while (line != null) {

                tree += line;
                count++;

                if (line.equals(","))
                    countcomma++;
                else if (line.equals(";"))
                    countcolon++;
                else if (line.equals(")"))
                    countparen++;
                else if (!parencase && line.indexOf("):") == 0)
                    parencase = true;

                line = in.readLine();
            }

//in = new DataInputStream(new FileInputStream(f));
            in = new BufferedReader(new FileReader(f));

            line = in.readLine();
            tr = new ArrayList();
            while (line != null) {

                tr.add((String) line);
                line = in.readLine();
            }

            //in = new DataInputStream(new FileInputStream(f));
            in = new BufferedReader(new FileReader(f));
        } catch (IOException ex) {
            //System.out.println("ERROR: No Input File <"+f+">");
        }

//System.out.println("COUNT of Tree lines "+count+"\t"+f);
        String newtree = f + "_tmp";
        System.out.print("Reformatting dendrogram file: " + newtree);
        tmpdndfile = new String(newtree);

        if (count == 1) {

            System.out.print(" - from single line Newick.\n");
            rewriteFromSingleLine(tree, f);
        } else if (countcomma > 1 || countcolon > 0 || countparen > 0 || parencase) {

            System.out.print(" - from comma line Newick.\n");
            rewriteFromLineCommaColonParenCase(tr, f);
        }

    }

    /**
     * Rewrites a single line Newick tree to node-per-line Newick.
     */
    private void rewriteFromSingleLine(String tree, String f) {

        try {

            PrintWriter pif = new PrintWriter(new FileWriter(tmpdndfile), true);

            int openpar = tree.indexOf("(");
            int comma = tree.indexOf(",");
            int colon = tree.indexOf("):");

            while (openpar != -1 || comma != -1 || colon != -1) {

                int minchop = -1;

                if (openpar != -1 && comma != -1 && colon != -1)
                    minchop = Math.min(Math.min(openpar, comma), colon);
                else if (openpar != -1 && comma != -1 && colon == -1)
                    minchop = Math.min(openpar, comma);
                else if (openpar == -1 && comma != -1 && colon != -1)
                    minchop = Math.min(comma, colon);
                else if (openpar != -1 && comma == -1 && colon != -1)
                    minchop = Math.min(openpar, colon);
                else if (openpar != -1 && comma == -1 && colon == -1)
                    minchop = openpar;
                else if (openpar == -1 && comma != -1 && colon == -1)
                    minchop = comma;
                else if (openpar == -1 && comma == -1 && colon != -1)
                    minchop = colon;

                if (logon)
                    lf.aw("testandreformat comma " + comma + "\t( " + openpar + "\t): " + colon + "\tminchop " + minchop + "\n");

                String now = tree.substring(0, minchop + 1);

                if (logon)
                    lf.aw("testandreformat line " + now + "\n");

                tree = tree.substring(minchop + 1);

                boolean go = true;
                try {

                    String test = now.substring(1, now.length() - 1);
                    int testparse = (Integer.parseInt(test));
                    go = false;

                } catch (Exception e) {

                    go = true;
                }

                if (go) {

                    pif.println(now);
                }
                openpar = tree.indexOf("(");
                comma = tree.indexOf(",");
                colon = tree.indexOf("):");

                if (openpar == -1 && comma == -1 && colon == -1 && tree.indexOf(");") != -1) {

                    try {

                        String test = tree.substring(0, tree.indexOf(")"));
                        int testparse = (Integer.parseInt(test));
                        tree = tree.substring(2);
                    } catch (Exception e) {

                    }

                    pif.println(tree);

                    break;
                }
            }

            pif.close();
        } catch (IOException e) {
            if (logon)
                lf.aw("Could not write to file " + f);
        }

    }

    /**
     * Rewrites a node-per-line Newick tree with allowed comma lines to node-per-line Newick w/o comma lines.
     */
    private void rewriteFromLineCommaColonParenCase(ArrayList tr, String f) {

        String tree = "";
        ArrayList newtr = new ArrayList();

        int count = 1;
        String l = (String) tr.get(0);
        newtr.add((String) l);

        for (int i = 1; i < tr.size(); i++) {

            l = (String) tr.get(i);

            if (l.equals(",")) {

                int last = newtr.size() - 1;
                newtr.set(last, ((String) newtr.get(last)) + ",");
            } else if (l.equals(";")) {

                int last = newtr.size() - 1;
                newtr.set(last, ((String) newtr.get(last)) + ";");
            } else if (l.equals(")")) {

                int last = newtr.size() - 1;
                newtr.set(last, ((String) newtr.get(last)) + ")");
            } else if (l.indexOf("):") == 0) {

                int last = newtr.size() - 1;
                newtr.set(last, ((String) newtr.get(last)) + ")");
                newtr.add((String) l.substring(1));
            } else {

                newtr.add((String) l);
                count++;
            }
//System.out.println(add);
        }

        for (int i = 0; i < newtr.size(); i++) {

            tree += (String) newtr.get(i) + "\n";
        }


        try {

            PrintWriter pif = new PrintWriter(new FileWriter(tmpdndfile), true);
            pif.println(tree);
            pif.close();
        } catch (IOException e) {
            if (logon)
                lf.aw("Could not write to file " + f);
        }

    }


    /**
     * Method under development - to parse a Newick format tree and create the object representation.
     */
    public String readPHTree(String inf) {

        String phtree = new String();
        dndfile = inf;

        ParsePath pp = new ParsePath(dndfile);
        if (!pp.getExt().equals("dnd"))
            testAndReformat(dndfile);

        names = readClustalTreeNames(tmpdndfile);

        try {
            //DataInputStream in = new DataInputStream(new FileInputStream(inf));
            BufferedReader in = new BufferedReader(new FileReader(inf));
            String line = new String();
            line = in.readLine();

            while (line != null) {
                phtree += line;
                line = in.readLine();
            }

        } catch (IOException ex) {
            //System.out.println("ERROR: No Input File <"+f+">");
        }

//System.out.println(phtree);
        return phtree;
    }


    /**
     * Prints the terminal node labels associated with this tree.
     */
    public void printNames() {

        for (int i = 0; i < names.size(); i++) {

            System.out.println(i + "\t" + (String) names.get(i));

        }
    }

    /**
     * Trims the names to the first space.
     */
    public void trimNamestoSpace() {

        for (int i = 0; i < names.size(); i++) {

            //System.out.println(i + "\t" + (string) names.get(i));

            String news = (String) names.get(i);
            int spa = news.indexOf(" ");
            news = news.substring(0, spa);
            names.set(i, news);
        }
    }

    /**
     * Trims the names of all illegal chars.
     */
    public void removeIllegalChar() {

        for (int i = 0; i < names.size(); i++) {

            //System.out.println(i + "\t" + (string) names.get(i));
            String news = (String) names.get(i);
            news = util.StringUtil.replace(news, "/", "_");
            news = util.StringUtil.replace(news, ":", "_");
            news = util.StringUtil.replace(news, ";", "_");
            news = util.StringUtil.replace(news, "[", "_");
            news = util.StringUtil.replace(news, "]", "_");
            news = util.StringUtil.replace(news, "{", "_");
            news = util.StringUtil.replace(news, "}", "_");
            news = util.StringUtil.replace(news, "'", "_");
            news = util.StringUtil.replace(news, ",", "_");
            news = util.StringUtil.replace(news, "=", "_");
        }
    }

    /**
     * Operates on an entire Newick format tree - still setup for SCOP superfamily defs.
     *
     * @param ids
     * @param i
     * @param tre
     * @return
     */
    public String replaceNamesbyID(String[] ids, int i, String tre) {

//System.out.println("replaceNamesbyID: "+tre);
        boolean replace = false;
        String n = (String) names.get(i);

        if (n.length() > 0) {

//System.out.println(n);

            int startind = n.indexOf("_") + 1;
            int endind = n.lastIndexOf("_");
            if (endind == -1 || startind >= endind)
                endind = n.length();

            String searchn = n;

            searchn = n.substring(0, 5);

            /*searchn = util.StringUtil.replace(searchn, "__", "");
   if (searchn.lastIndexOf("_") == searchn.length() - 1)
       searchn = searchn.substring(0, searchn.length() - 1);*/

            try {

//System.out.println(startind+"\t"+endind);
                searchn = n.substring(startind, endind);
                System.out.println("searchn " + searchn);

                int ij = -1;

                while (ij + 1 < ids.length && !replace) {

                    ij++;

//System.out.println(n+"\t"+searchn+"\t"+supfam);
                    boolean go = false;

                    if (supfam == null) {

//System.out.println("supfam is null");
                        if (ids[ij].indexOf(searchn) != -1) {

                            System.out.println("setting supfam " + n + "\t" + searchn + "\t" + supfam + "\t" + ids[ij]);
                            go = true;
                        }
                    } else if (ids[ij].indexOf(searchn) != -1 && ids[ij].indexOf(supfam) != -1) {

//System.out.println("satisfy "+searchn+"\t"+supfam);
                        go = true;
                    } else if (ids[ij].indexOf(searchn) != -1) {

                        int great = ids[ij].indexOf(">");
                        int und = ids[ij].indexOf("_", great + 1);
                        int spa1 = ids[ij].indexOf(" ", great + 1) + 1;
                        int spa2 = ids[ij].indexOf(" ", spa1);

                        try {
                            String cursupfam = ids[ij].substring(spa1, util.StringUtil.lastIndexBefore(ids[ij], ".", spa2));

//System.out.println("did not match supfam: "+searchn+"\tcur: "+cursupfam+"\t"+ids[ij]+"\tsupfam "+supfam);
                        } catch (StringIndexOutOfBoundsException e) {

                            System.out.println(great + "\t" + und + "\t" + spa1 + "\t" + spa2);
                            System.out.println("bad no supfam match ind " + ids[ij]);
                        }

                    }

                    if (go) {

//System.out.println("found "+ids[ij]);
                        int great = ids[ij].indexOf(">");
                        int und = ids[ij].indexOf("_", great + 1);
                        int spa1 = ids[ij].indexOf(" ", great + 1) + 1;
                        int spa2 = ids[ij].indexOf(" ", spa1);
//System.out.println(great+"\t"+und+"\t"+spa1+"\t"+spa2);
                        try {

                            if (supfam == null) {

                                supfam = ids[ij].substring(spa1, util.StringUtil.lastIndexBefore(ids[ij], ".", spa2));
//System.out.println("supfam "+supfam);
                            }
                            String fam = ids[ij].substring(spa1, spa2);
//System.out.println("fam "+fam);

                            String news = searchn + "_" + fam;//n.substring(0, n.length()-1)+fam;
                            news = util.StringUtil.replace(news, "__", "_");
                            System.out.println("old " + n + "\tnew " + news + "\tsearchn " + searchn);//+"\t"+tre);
                            tre = util.StringUtil.replace(tre, n, news);
                            replace = true;
                            names.set(i, news);

                        } catch (StringIndexOutOfBoundsException e) {

                            System.out.println(great + "\t" + und + "\t" + spa1 + "\t" + spa2);
                            System.out.println("bad fam match ind " + ids[ij]);
                        }


                    }
                }
                if (!replace)
                    System.out.println("could not identify " + n + "\t" + searchn + "\t" + supfam);


            } catch (StringIndexOutOfBoundsException e) {

                System.out.println("bad name trim " + n + "\t" + searchn);
            }

        }

        return tre;
    }


    private String parseNextTreeName(String tree) {

        String ret = null;
        int start = StringUtil.nexctCharNot(tree, "(),:");
        //System.out.println("start " + start);
        String smalltree = tree.substring(start);
        int stop = StringUtil.nexctChar(smalltree, "(),:");

        //System.out.println("start/stop " + start + "\t" + (stop + start));

        double test = Double.NaN;
        try {
            ret = tree.substring(start, stop + start);
            test = Double.parseDouble(ret);
            //System.out.println("test " + test);
        } catch (Exception e) {

        }
        if (ret == null)
            return null;

        if (!Double.isNaN(test) || ret.length() == 0) {

            smalltree = smalltree.substring(stop + 1);
            //System.out.println("recurse " + smalltree);
            parseNextTreeName(smalltree);
        }

        //smalltree = tree.substring(stop+start);
        return smalltree;
    }

    /**
     * Operates on an entire Newick format tree - setup for GOS kingdom labels
     *
     * @param ids
     * @param ids2proc
     * @param ids2trunc
     * @param ids2
     * @param nameindex
     * @param tre
     * @return
     */
    public String replaceNamesby2ID(String[] ids, String[] ids2proc, String[] ids2trunc, String[] ids2, int nameindex, String tre) {

        ArrayList ids2proclist = StringUtil.stringListtoArray(ids2proc);
        ArrayList ids2trunclist = StringUtil.stringListtoArray(ids2trunc);
        String origtree = tre;
        tre = parseNextTreeName(tre);
        String treename = tre.substring(0, StringUtil.nexctChar(tre, "),:"));
        int goscount = 0;

        while (treename != null) {

            treename = StringUtil.replace(treename, "(", "");
            String searchn = treename;

            try {

                int ij = -1;

                while (ij + 1 < ids.length) {

                    ij++;
                    boolean gos = false;
                    boolean go = false;
                    //System.out.println(ids[ij].substring(0, 5) + "\t" + searchn.substring(0, 5a`      Z)+"\t"+ids[ij].substring(0, 5).equals(searchn.substring(0, 5)));
                    if (ids[ij].substring(0, 5).equals(searchn.substring(0, 5))) {

                        //System.out.println("FOUND ONE " + ids[ij].substring(0, 5) + "\t" + searchn);
                        String id1 = ids[ij];

                        String search_id1 = null;
                        if (id1.indexOf("gi|") != -1) {

                            int beginIndex = id1.indexOf("_") + 1;
                            int endindex = id1.indexOf("|");
                            endindex = id1.indexOf("|", endindex + 1);
                            search_id1 = id1.substring(beginIndex, endindex);
                        } else if (id1.indexOf("GOS_") != -1) {
                            search_id1 = id1.substring(id1.lastIndexOf("|") + 1, id1.length());
                            gos = true;
                        } else if (id1.indexOf("TIGR_") != -1) {
                            search_id1 = id1.substring(id1.lastIndexOf("|") + 1, id1.length());
                        } else if (id1.indexOf("NR_") != -1 || id1.indexOf("ENS_") != -1) {

                            if (id1.indexOf("NR_") != -1)
                                search_id1 = "gi|" + id1.substring(id1.lastIndexOf("_"), id1.length());
                            else
                                search_id1 = id1.substring(id1.lastIndexOf("_") + 1, id1.length());
                        }

                        //System.out.println("searching " + id1 + "\t:" + search_id1 + ":");//\t" + ids2proc[0]);
                        int k = MoreArray.getArrayInd(ids2proclist, search_id1);

                        String id2 = treename;
                        String num = id1.substring(0, id1.indexOf("_"));
                        int first_und = id1.indexOf("_");
                        String kingdom = ids2[ij].substring(ids2[ij].indexOf("_", first_und + 1) + 1, ids2[ij].indexOf("_", first_und + 1) + 2);

                        /*   String getproc = (String) ids2proclist.get(nameindex);
                                                String gettrunc = (String) ids2trunclist.get(nameindex);
                                                System.out.println("id compare: orig: " + ids[ij] + "\t" + id1 + "\tnew " + id2 + "\tsearchn " + searchn + "\tgetproc " + getproc + "\tgettrunc " + gettrunc + "\tgos " + gos);
                        */
                        if (k != -1) {
                            //id2 = ids2[k];
                            if (gos) {
                                goscount++;
                                id2 = "G_" + num + "_";// + kingdom;
                            } else {
                                kingdom = "E";
                                id2 = "P_" + num + "_";// + kingdom;
                            }
                            origtree = util.StringUtil.replace(origtree, treename, id2);
                            System.out.println(ij + "\t" + id2 + "\t" + kingdom);
                            names.set(nameindex, id2);
                        } else {
                            k = MoreArray.getArrayInd(ids2trunclist, search_id1);
                            id2 = treename;
                            num = id1.substring(0, id1.indexOf("_"));
                            first_und = id1.indexOf("_");
                            kingdom = ids2[ij].substring(ids2[ij].indexOf("_", first_und + 1) + 1, ids2[ij].indexOf("_", first_und + 1) + 2);

                            if (k != -1) {

                                if (gos) {
                                    goscount++;
                                    id2 = "G_" + num + "_";// + kingdom;
                                } else {
                                    kingdom = "E";
                                    id2 = "P_" + num + "_";// + kingdom;
                                }
                                origtree = util.StringUtil.replace(origtree, treename, id2);
                                System.out.println(ij + "\t" + id2 + "\t" + kingdom);
                                names.set(nameindex, id2);
                            } else {
                                num = id1.substring(0, id1.indexOf("_"));
                                kingdom = "E";
                                id2 = "P_" + num + "_";// + kingdom;
                                origtree = util.StringUtil.replace(origtree, treename, id2);
                                System.out.println(ij + "\t" + id2 + "\t" + kingdom);
                                names.set(nameindex, id2);
                            }


                        }

                    }
                }
            } catch (StringIndexOutOfBoundsException e) {
            }

            int start = StringUtil.nexctChar(tre, "(),:");

            if (start == -1) {
                //System.out.println("replaced, start -1  " + count + "\n*****" + origtree+"*****");
                return origtree;
            }

            tre = tre.substring(start);
            tre = parseNextTreeName(tre);
            //System.out.println("tre " + tre);

            if (tre == null) {
                //System.out.println("replaced, tre nulll  " + count + "\n*****" + origtree+"*****");
                return origtree;
            }

            int endIndex = StringUtil.nexctChar(tre, "),:");
            if (endIndex != -1)
                treename = tre.substring(0, endIndex);
        }

        //System.out.println("replaced end " + count + "\n*****" + origtree+"*****");
        return origtree;
    }

    /**
     * Function which reads clustalw dnd treefile; can read any file in pure NEXUS forma.
     *
     * @param f
     * @param nam
     * @return
     */
    public ArrayList readClustalTree(String f, ArrayList nam) {

        //System.out.println("Reading tree " + f);
        if (logon) {

            //log = new String("");
            try {
                lf = new LogFile(dndfile);
            } catch (IOException e) {
                System.out.println("Problem creating log file.\n\n" + e.getStackTrace());
            }
        }

        dndfile = f;


        ParsePath pp = new ParsePath(dndfile);
        if (!pp.getExt().equals("dnd"))
            testAndReformat(dndfile);

        openclose = new ArrayList();


        String readtree = dndfile;
        if (tmpdndfile != null)
            readtree = tmpdndfile;

//System.out.println("Reading tree "+readtree);

        names = readClustalTreeNames(readtree);

        //System.out.println(names.size() + " terminal nodes.");
        boolean[] getnamenr = testNRNames(names);

/*
for(int i =0; i < names.size(); i++) {

System.out.println("Tree names :"+i+":\t:"+(string)names.get(i)+":");
//System.out.println("Jevt names :"+i+":\t:"+(string)nam.get(i)+":");
}
*/
        if (nam != null) {

            changenameorder = testNameOrder(nam);

            if (!changenameorder) {

                names = new ArrayList();
                for (int i = 0; i < nam.size(); i++) {
                    names.add((String) nam.get(i));
                    if (logon)
                        lf.aw("NAME\t" + i + "\t" + (String) nam.get(i) + "\n");
                }
            }
        }
        if (logon)
            lf.aw("Preparing tree for " + (names.size() - 1) + " sequences\n");
        points = new ArrayList();
        dndtree = new ArrayList();
        try {
            //DataInputStream in = new DataInputStream(new FileInputStream(readtree));
            BufferedReader in = new BufferedReader(new FileReader(readtree));
            String line = new String();

            int openlevel = 0;
            int closelevel = 0;
            ArrayList tmp = new ArrayList();

            int subtreecount = 0;
            int totalopen = 0;
            int totalclose = 0;

            boolean opentree = false;
            boolean closetree = false;
            boolean opensubtree = false;
            boolean closesubtree = false;

            ArrayList subtrees = new ArrayList();

            line = in.readLine();
            int count = 1;
            int level = 0;

            while (line != null) {

//System.out.println("START\t"+count+"\tline\t"+line);

                int[] geti = getLineIndex(line);
                int open = geti[0];
                int close = geti[1];
                int comma = geti[2];
                int colon = geti[3];
                int end = geti[4];

                if (open != -1) {
                    totalopen++;
                }
                if (close != -1) {
                    totalclose++;
                }
                //System.out.println("colon\t"+colon);
                if (opentree == true) {
                    openlevel++;
                    opensubtree = true;
                } else if (openlevel == 0 && opentree == false)
                    opentree = true;
                if (open != -1 && close != -1)
                    opensubtree = true;
                if (end != -1)
                    closetree = true;

                level = (totalopen - totalclose);

//System.out.println("open:"+opentree+" close:"+closetree+" total open:"+totalopen+" total close:"+totalclose+" subtree:"+opensubtree+" level:"+level);

                if ((open == 0 && line.length() == 1 && subtrees.size() > 0) || (end != -1 && level == 0)) {
                    if (colon == 0) {
                        level = totalopen - totalclose;
                        int closethisparen = line.lastIndexOf(")");
                        //System.out.println("FIRST PARENT  ");
                        int max = Math.min(closethisparen, line.length() - 1);
                        double parsd = Double.valueOf(line.substring(1,
                                max)).doubleValue();

                        char state = 'o';
                        if (closethisparen != -1) {
                            state = 'c';
                        }

                        openCloseObj oco = new openCloseObj(totalopen, totalclose, parsd, state);
                        openclose.add((openCloseObj) oco);

                        ClustalNode q = extractParent(line, totalopen, totalclose);
                        if (q != null) {
                            subtrees.add((ClustalNode) q);
                            closesubtree = true;
                            if (logon) {
                                lf.aw("P subtree add " + q.toString() + "\n");
                                lf.aw("P subtree size " + subtrees.size() + "\n");
                            }
                        }
                        line = line.substring(0, 1);
                    }
                    int subsi = subtrees.size();
                    ArrayList tmp1 = new ArrayList();
                    for (int i = 0; i < subsi; i++) {
                        ClustalNode cn = new ClustalNode();
                        tmp1.add(cn);
                    }

                    for (int i = 0; i < subsi; i++) {

                        ClustalNode cn = (ClustalNode) subtrees.get(i);
                        //cn.branchlevel = level;
                        tmp1.set(i, new ClustalNode(cn));
                    }

                    dndtree.add((ArrayList) tmp1);

                    if (logon)
                        lf.aw(open + "\t" + totalopen + ", " + close + "\t" + totalclose + "\n");

                    if (subtrees.size() > 0)
                        subtreeToBinary(subtrees, null, totalopen, totalclose);

                    opensubtree = false;
                    closesubtree = false;
                    openlevel = 0;
                    closelevel = 0;
                    subtrees = new ArrayList();

                    ArrayList tmp11 = (ArrayList) dndtree.get(dndtree.size() - 1);
                    //System.out.println("tmp size "+tmp11.size());
                    for (int i = 0; i < tmp11.size(); i++) {

                        ClustalNode cn = (ClustalNode) tmp11.get(i);
                        if (logon)
                            lf.aw("STARTdndtree:" + subsi + "\telem:" + cn.toString() + "  dnd size " + dndtree.size() + "\n");
                    }

/* Old fix that should be removed. */
/*
		if(points.size() == 0 && dndtree.size() == 1)
		{
			read2NodeDND(readtree);

dndTreeToPoints();

if(logon)
    lf.aw("THIS DNDTREE.SIZE() "+dndtree.size();
			}
*/

                    if (logon)
                        lf.aw("FINISHED dndtree. " + points.size() + "\n");
                }

//> 1 limits it to non ')' && ')' lines
                if (line.length() > 1) {
                    if (close != -1)
                        closelevel++;

//System.out.println("open   "+openlevel+"   close  "+closelevel);
                    if (colon == 0) {
                        level = totalopen - totalclose;
                        if (logon)
                            lf.aw("FIRST PARENT  \n");
                        ClustalNode q = extractParent(line, totalopen, totalclose);
                        if (q != null) {
                            subtrees.add((ClustalNode) q);
                            closesubtree = true;
                            if (logon) {
                                lf.aw("FIRSTPARENT subtree add " + q.toString() + "\n");
                                lf.aw("FP subtree size " + subtrees.size() + "\n");
                            }
                        }
                    }
                    if (colon != 0 && close == -1) {
//System.out.println("extracting LEFT in LOOP  "+line);
                        level = (totalopen - totalclose);
                        ClustalNode r = extractNodeLeft(line, level - 1, totalopen, totalclose);
                        if (r != null) {

                            if (logon)
                                lf.aw("Extracted node " + r.toString());

                            subtrees.add((ClustalNode) r);
                            if (logon) {
                                lf.aw("L subtree add " + r.toString() + "\n");
                                lf.aw("L subtree size " + subtrees.size() + "\n");
                            }
                        }

                        if (close != -1 && end == -1) {
                            if (logon)
                                lf.aw("close != -1 && end == -1 >> " + close + "\t" + end + "\n");
                            line = in.readLine();
                            geti = getLineIndex(line);
                            open = geti[0];
                            close = geti[1];
                            comma = geti[2];
                            colon = geti[3];
                            end = geti[4];
                            if (open != -1) {
                                totalopen++;
                            }
                            if (close != -1) {
                                totalclose++;
                            }
                            if (logon)
                                lf.aw("LOOP PARENT  " + line + "\n");
                            count++;
                            level = (totalopen - totalclose);

                            ClustalNode q = extractParent(line, totalopen, totalclose);
                            if (q != null) {
                                subtrees.add((ClustalNode) q);
                                if (logon) {
                                    lf.aw("PARENT subtree add " + q.toString() + "\n");
                                    lf.aw("P subtree size " + subtrees.size() + "\n");
                                }
                                closesubtree = true;
                            }
                        }
                    }
                    if (colon != 0 && line.length() > line.indexOf(",") + 1) {
                        if (logon)
                            lf.aw("colon !=0 && line.length() > line.indexOf(comma)+1 >> " + colon + "\t" + line.length() + "\t" + (line.indexOf(",") + 1) + "\n");
                        if (comma != -1) {
                            if (line.indexOf(")") == -1) {
                                while (line.indexOf(")") == -1) {
                                    //System.out.println("line.indexOf(\")\") == -1)");
                                    line = in.readLine();
                                    geti = getLineIndex(line);
                                    open = geti[0];
                                    close = geti[1];
                                    comma = geti[2];
                                    colon = geti[3];
                                    end = geti[4];

                                    if (open != -1) {

                                        totalopen++;
                                    }
                                    if (close != -1) {

                                        closelevel++;
                                        totalclose++;
                                    }

                                    if (logon)
                                        lf.aw("LOOP:\t" + count + "\tline\t" + line + "\n");
                                    count++;

                                    if (line != null) {

                                        if (end != -1 && closetree == false) {

                                            closetree = true;

                                            if (logon)
                                                lf.aw("closed in while\")\" loop tree!\n");
                                        }
                                        if (logon)
                                            lf.aw("LOOP RIGHT ");

                                        level = (totalopen - totalclose);
                                        //int searchoc = searchOpenClose(totalopen, totalclose);
                                        ClustalNode q = extractNodeRight(line, level, totalopen, totalclose);
                                        if (q != null) {

                                            if (logon)
                                                lf.aw("Extracted node " + q.toString());

                                            subtrees.add((ClustalNode) q);
                                            if (logon) {
                                                lf.aw("R subtree add " + q.toString() + "\n");
                                                lf.aw("R subtree size " + subtrees.size() + "\n");
                                            }
                                        }

                                    }
                                }
                            }
                        } else if (comma == -1) {

                            if (line != null) {

                                close = line.indexOf(")");
                                level = (totalopen - totalclose);
                                //System.out.println("parsing  "+line);
                                ClustalNode q = extractNodeRight(line, level, totalopen, totalclose);
                                if (q != null) {
                                    if (logon)
                                        lf.aw("Extracted node " + q.toString());

                                    subtrees.add((ClustalNode) q);

                                    if (logon) {
                                        lf.aw("R subtree add " + q.toString() + "\n");
                                        lf.aw("R subtree size " + subtrees.size() + "\n");
                                    }
                                }
                            }
                        }

                        if (end != -1 && closetree == false) {

                            closetree = true;

                            if (logon)
                                lf.aw("out loop closed tree!\n");
                        }

                    }
                }

                if (logon) {

                    lf.aw("by line " + count + " subtree size is " + subtrees.size() + "\n");
                    lf.aw("totalopen: " + totalopen + ", openlevel:  " + openlevel + "\ttotalclose: " + totalclose + ", closelevel : " + closelevel + "\n");
                }

                if (closetree)
                    closesubtree = true;

                if (end != -1 || (opensubtree == true && closesubtree == true && subtrees.size() > 1)) {

                    int subsi = subtrees.size();

                    if (logon)
                        lf.aw("subtrees.size()  " + subtrees.size() + "\n");


                    ArrayList tmp1 = new ArrayList(subsi);
                    for (int i = 0; i < subsi; i++) {
                        ClustalNode cn = new ClustalNode();
                        tmp1.add(cn);
                    }

                    for (int i = 0; i < subsi; i++) {
                        ClustalNode cn = (ClustalNode) subtrees.get(i);
                        //cn.branchlevel = level;
                        //cn.open = totalopen;
                        //cn.close = totalclose;
                        tmp1.set(i, new ClustalNode(cn));
                    }

                    dndtree.add((ArrayList) tmp1);

//System.out.println(open+"\t"+totalopen+", "+close+"\t"+totalclose);

                    if (subtrees.size() > 0)
                        subtreeToBinary(subtrees, null, totalopen, totalclose);

//System.out.println("LAST subtree size "+dndtree.size());
                    opensubtree = false;
                    closesubtree = false;
                    openlevel = 0;
                    closelevel = 0;
                    subtrees = new ArrayList();
                }

                if (closetree) {

                    //System.out.println("CLOSING ALL!");
                    break;
                }

                line = in.readLine();

                if (line != null) {
                    count++;
                    geti = getLineIndex(line);
                    open = geti[0];
                    close = geti[1];
                    comma = geti[2];
                    colon = geti[3];
                    end = geti[4];
                }

                //System.out.println("RELOOP "+count+" line "+line);
            }
        } catch (IOException ex) {
            if (logon)
                lf.aw("ERROR: No Input File <" + f + ">");
        }
        int si = points.size();
        int dndsi = dndtree.size();

        if (logon)
            lf.aw("DND Tree size ::" + dndsi + " points size::" + si + "\n");

/*
if(logon)
lf.aw("Incrementing branches @ terminal nodes 0 -> .0001!\n");
		for(int i =0; i < points.size(); i++) {
		Branch cur = (Branch)points.get(i);
		if(points.get(i) != null) {

		if(cur.y[1] == 0) {
			    if(logon)
				lf.aw("incrementing "+i+" ::  "+cur.toString()+"\n");
				cur.y[1] = .00001;
				cur.y[2] = .00001;
				points.set(i,(Branch)cur);
			}
		}
		}
*/

//System.out.println("adding ROOT");

        if (points.size() > 0) {

            makeDistinctPixel();
            makeNonNeg();
            //scaleClustal();
            //reScale();
            newjoinPairs();

        } else {

            if (logon)
                lf.aw("Error parsing the tree file, no branches loaded!");
        }

//scales and sorts points binary tree
        finishClustal();
//System.out.println("finished TREE "+points.size());
        if (logon) {

            lf.aw("points SORTED! " + points.size() + "\n");
            for (int i = 0; i < points.size(); i++) {

                if (points.get(i) != null)
                    lf.aw(i + " ::  " + ((Branch) points.get(i)).toString() + "\n");
                else
                    lf.aw(i + " ::  is null\n");
            }
        }

        if (tmpdndfile != null) {

            File test = new File(tmpdndfile);
            if (test.exists())
                test.delete();
        }

        if (logon)
            lf.exit();

//printPoints();

        return points;
    }


    /**
     * Method to print the node contents of the current tree.
     */
    private void printPoints() {

        System.out.println("points size " + points.size());

        for (int i = 0; i < points.size(); i++) {

            System.out.println("Branch " + i + "\t" + ((Branch) points.get(i)).toString());

        }

    }


    /**
     * Searches for a specific open,close combination.
     */
    private int searchOpenClose(int o, int c) {

        int ret = 0;
        for (int i = 0; i < openclose.size(); i++) {
            openCloseObj oco = (openCloseObj) openclose.get(i);
            if (oco.open == 0 && oco.close == c) {
                if (oco.state == 'c')
                    ret = -1;
                break;
            }
        }

        return ret;
    }

    /**
     * Index a NEXUS format line by punctuation.
     *
     * @param l single line NEXUS format data
     * @return int[] ret integer array of length 5 containing the following index of chars in the input <code>string</code> to '('  ')' ',' ':' '):'
     */
    private int[] getLineIndex(String l) {
        int[] ret = new int[5];
        ret[0] = l.indexOf("(");
        ret[1] = l.indexOf(")");
        ret[2] = l.indexOf(",");
        ret[3] = l.indexOf(":");
        ret[4] = l.indexOf(");");
        return ret;
    }

    /**
     * For a given y[1] value (ly) searches the figuretree and normalizes all contracted nodes.
     *
     * @param now   to search y[1] Branch points
     * @param lasty to add to found y[1] Branch
     * @param newy
     * @param not   node to skip
     */
    private ArrayList searchFigureNorm(ArrayList now, double lasty, double newy, int not) {
        for (int b = 0; b < now.size(); b++) {
            if (b != not) {
                Branch nawbr = (Branch) now.get(b);

                if (nawbr.y[1] == lasty) {
                    while (nawbr.y[1] < newy)
                        nawbr.y[0] += .001;
                    now.set(b, nawbr);

                    if (logon)
                        lf.aw("NORMALIZING " + nawbr.toString() + "\n");
                }
            }
        }
        return now;
    }

    /**
     * For a given y[1] value (ly) searches the figuretree and adds a value (add).
     *
     * @param now
     * @param ly  y value to search y[1] Branch points
     * @param rx
     * @param lx
     * @param add double value to add to found y[1] Branch
     * @param not
     * @return
     */
    private ArrayList searchFigureYandAdd(ArrayList now, double ly, double rx, double lx, double add, int not) {
        for (int b = 0; b < now.size(); b++) {
            if (b != not) {
                boolean mod = false;
                Branch nawbr = (Branch) now.get(b);
                double nawxr = nawbr.x[0];
                double nawlr = nawbr.x[3];

                if (nawbr.y[0] == ly && nawxr >= rx && nawxr <= lx) {
                    nawbr.y[0] += add;
                    mod = true;
                } else if (nawbr.y[3] == ly && nawlr >= rx && nawlr <= lx) {
                    nawbr.y[3] += add;
                    mod = true;
                }
                if (mod) {
                    if (logon)
                        lf.aw("UPDATING " + nawbr.toString() + "\n");
                    now.set(b, nawbr);
                    break;
                }
            }
        }
        return now;
    }

    /**
     * for a given y[1] value (ly) searches the dndtree and adds a value (add)
     *
     * @param ly  y value to search y[1] Branch points
     * @param add double value to add to found y[1] Branch
     * @param ab  tree subtree to skip
     */
    private void searchTreeYandAdd(double ly, double add, int ab) {
        for (int a = 0; a < dndtree.size(); a++) {
            if (a != ab) {
                ArrayList cur = (ArrayList) dndtree.get(a);

                for (int b = 0; b < cur.size(); b++) {
                    ClustalNode naw = (ClustalNode) cur.get(b);
                    int nawname = searchNames(naw.a);
                    int nawelem = searchPointsBranch(nawname);
                    Branch nawbr = (Branch) points.get(nawelem);
                    if (nawbr.y[1] == ly) {
                        naw.x = new Double(naw.x.doubleValue() + add);
                        nawbr.y[1] += add;
                        nawbr.y[2] += add;
                        naw.br = new Branch(nawbr);
                        if (logon)
                            lf.aw("search tree summed new value for " + naw.br.toString() + "\n");
                        ClustalNode newnaw = new ClustalNode(naw);
                        cur.set(b, newnaw);
                        points.set(nawelem, nawbr);
                    }
                }
            }
        }
    }

    /**
     * Updates modif[][] for altered y[1]/y[2] branches.
     */
    private void searchSister(double newy, double add) {

        if (logon)
            lf.aw("search sister old " + newy + "\n");

        for (int a = 0; a < dndtree.size(); a++) {

            ArrayList cur = (ArrayList) dndtree.get(a);

            for (int i = 0; i < cur.size(); i++) {
                if (modif[a][i] != 100) {
                    //System.out.println("looking at tree "+a+" elem "+i);
                    ClustalNode now = (ClustalNode) cur.get(i);

                    int levelnow = now.branchlevel;
                    int nowname = searchNames(now.a);
                    int nowelem = searchPointsBranch(nowname);

                    if (nowelem != -1) {
                        Branch nowbr = (Branch) points.get(nowelem);
                        boolean gor = false;
                        if (nowbr.y[1] == newy)
                            gor = true;
                        if (gor) {
                            now.x = new Double(now.x.doubleValue() + add);
                            cur.set(i, now);
                            modif[a][i] = 100;
                            if (logon)
                                lf.aw("search sister " + a + "  " + i + "\n");
                        }
                    }
                }
            }
        }

    }

    /**
     * Searches points vector by the integer value of the terminal nodes in the binary Branch
     */
    private int searchPointsBranch(int xnode) {

        int ret = -1;

        for (int i = 0; i < points.size(); i++) {

            Branch now = (Branch) points.get(i);

            if (now.x[0] == xnode && now.y[0] == 0 || now.x[3] == xnode && now.y[3] == 0) {
                ret = i;
                break;
            }
        }
        return ret;
    }

    /**
     * finds a NEWICK node at the specified level
     *
     * @param level specified Branch/node level
     * @param tree  starting tree (0 if -1)
     * @param elema starting element (0 if -1)
     * @param done  (ArrayList object storing arrays encoding which nodes have been processed)
     * @return ret two integer array storing the node tree and elem positions
     */
    private int[] findNodeAtLevelPast(int level, int tree, int elema, ArrayList done) {

        int[] ret = {-1, -1};
        int dndsi = dndtree.size();
        //System.out.println("in find DND SIZE  "+dndsi+" avoiding tree:"+tree+" elem:"+elema);

        for (int b = 0; b < dndsi; b++) {
            ArrayList cur = (ArrayList) dndtree.get(b);
            int[] thisdone = (int[]) done.get(b);
            if (b == tree && cur.size() == 1) {
                b++;
                if (b < dndsi) {
                    cur = (ArrayList) dndtree.get(b);
                    thisdone = (int[]) done.get(b);
                }
            }

            for (int j = 0; j < cur.size(); j++) {
                if (j == elema && b == tree)
                    j++;
                if (j == cur.size())
                    break;
                ClustalNode n = (ClustalNode) cur.get(j);
                //System.out.println("DND searchtree:"+b+" elem:"+j+"  level:"+n.branchlevel);
                if (thisdone[j] != 100)
                    if (n.branchlevel == level && n.br != null) {
                        ret[0] = b;
                        ret[1] = j;
                        b = dndsi;
                        j = cur.size();
                    }
            }
        }
        return ret;
    }

    /**
     * Method that finds a node in the current tree at a given branching (paranthesis) level.
     */
    private int[] findNodeAtLevel(int level, int tree, int elema) {

        int[] ret = {-1, -1};

        for (int b = 0; b < dndtree.size(); b++) {
            ArrayList cur = (ArrayList) dndtree.get(b);
            for (int j = 0; j < cur.size(); j++) {
                if (j != elema && b != tree) {
                    ClustalNode n = (ClustalNode) cur.get(j);
                    if (n.branchlevel == level && n.br != null) {
                        ret[0] = b;
                        ret[1] = j;
                        break;
                    }
                }
            }
        }
        return ret;
    }


    /**
     * Searches for a <code>Branch</code> in dndtree - a tree of subtrees. In effect there are two searches performed: 1) find desired subtree and 2) find desired element.
     *
     * @param br    <code>Branch</code> to search with
     * @param tree  starting index for search in dndtree
     * @param elema index of node to skip in subtree search
     * @return int[] ret ret[0] = subtree index ret[1] = node index
     */
    private int[] findBranchInDNDTree(Branch br, int tree, int elema) {

        int[] ret = new int[2];

        int startt = 0;
        if (tree != -1)
            startt = tree;

        int starte = 0;
        if (elema != -1)
            starte = elema;

        if (br != null)
            if (startt < dndtree.size())
                for (int b = startt; b < dndtree.size(); b++) {
                    ArrayList cur = (ArrayList) dndtree.get(b);
                    if (tree != -1)
                        if (b == tree && cur.size() == 1) {
                            b++;
                            if (b < dndtree.size())
                                cur = (ArrayList) dndtree.get(b);
                        }
                    if (starte < cur.size())
                        for (int j = starte; j < cur.size(); j++) {
                            if (elema != -1 && tree != -1)
                                if (j == elema && b == tree) {
                                    j++;
                                }
                            if (j < cur.size()) {
                                ClustalNode n = (ClustalNode) cur.get(j);
                                if (n.br != null)
                                    if (n.br.equals(br)) {
                                        ret[0] = b;
                                        ret[1] = j;
                                        break;
                                    }
                            }
                        }
                }
            else if (logon)
                lf.aw("BRANCH is NULL!\n");

        return ret;
    }

    /**
     * Finds the minimum y Coord value of all nodes in the tree.
     */
    private double findMinY() {

        double min = 10000000;

        for (int a = 0; a < points.size(); a++) {
            Branch b = (Branch) points.get(a);

            if (b.y[0] != 0 & b.y[0] < min) {

                min = b.y[0];
            }
            if (b.y[3] != 0 & b.y[3] < min) {

                min = b.y[3];
            }


        }
        return min;
    }

    /**
     * Rescales all tree nodes with the specified scale.
     */
    private double reScale(double y) {

        double sub = y - 10;
//System.out.println("min "+y+"\tsub "+sub);

        for (int a = 0; a < points.size(); a++) {

            Branch b = (Branch) points.get(a);


            for (int i = 0; i < 4; i++) {
                if (b.y[i] != 0)
                    b.y[i] -= sub;
            }

            points.set(a, b);

        }
        return min;
    }

/*
Miscellaneous last steps in binary dendrogram construction from CLUSTALW .dnd input.
*/

    private void finishClustal() {

        scaleClustal();
        double y = findMinY();
        reScale(y);

        int k = findMintoRootBranch(points, -1, -1);

        if (logon)
            lf.aw("FINAL MIN TO ROOT " + k + "\n");

//System.out.println("sorting TREE");
        if (k == points.size() - 1)
            sortNewick();
        else
            points = sortTree(points);

        points = trimTree(points);

//System.out.println("finished sorting & trimming TREE");

        if (logon)
            lf.aw("Sorted tree size ::  " + points.size() + "\n");
//	//System.out.println("started treeExtents");
        double[] data = treeExtents();
//System.out.println("finished treeExtents");

    }

/*Converts a NEXUS subtree to a binary dendrogram.
*@param ArrayList sub <code>ArrayList</code> containing ClustalNode objects
*@param String join if this is NULL then uses NodeTripletoBinary
*@param int level the level at which this node resides in the dendrogram data

*/

    private void subtreeToBinary(ArrayList sub, String join, int open, int close) {


        ClustalNode a = null;
        ClustalNode b = null;
        ClustalNode c = null;
//System.out.println("Subtree size for binary conversion  "+sub.size());
        Double newx = null;

        if (join == null && sub.size() == 3) {
//System.out.println("subtreeToBinary 3!");
            a = (ClustalNode) sub.get(0);
            b = (ClustalNode) sub.get(1);
            c = (ClustalNode) sub.get(2);

            NodeTripletoBinary(a, b, c);
        } else {
            int round = 0;

            while (sub.size() > 0) {

//System.out.println("subtreeToBinary Subtree size pre "+sub.size());
//if(sub.size() > 2) {

                if (logon) {

                    lf.aw("subtreeToBinary round " + round + "\n");

                    for (int i = 0; i < sub.size(); i++) {

                        lf.aw("subtreeToBinary\t" + i + "\t" + ((ClustalNode) sub.get(i)).toString() + "\n");
                    }
                }

//}
                int k = findShallow(sub);

                if (k != -1) {
//System.out.println(k+"\tindex of shallow\t"+((ClustalNode)sub.get(k)));
                    a = new ClustalNode((ClustalNode) sub.get(k));
                    sub.remove(k);

                    int j = findShallow(sub);
                    if (j != -1) {

                        b = new ClustalNode((ClustalNode) sub.get(j));
                        sub.remove(j);
                    }
                    //System.out.println("Subtree size mid "+sub.size());
                    if (sub.size() > 0) {

                        newx = new Double((a.x.doubleValue() + b.x.doubleValue()) / (double) 2);
                        if (logon)
                            lf.aw("subtreeToBinary Newx val " + newx + "\n");

                        if (lasty == -1) {

                            lasty = newx.doubleValue();
                            if (logon)
                                lf.aw("subtreeToBinary Setting new lasty FROM -1 !! " + lasty + "\n");
                        }

                        ClustalNode addthis = new ClustalNode(null, newx, open, close);

                        if (addthis != null && sub.size() != 0)
                            sub.add(addthis);

                        lasty = NodePairtoBinary(a, b);
                        if (logon)
                            lf.aw("subtreeToBinary THIS.LASTY from NodePairtoBinary: " + lasty + "\n");
                    } else {

                        double templasty = NodePairtoBinary(a, b);
                        if (logon)
                            lf.aw("WOULD HAVE SET THISLASTY from NodePairtoBinary: " + templasty + "\n");
                        break;
                    }

                    if (logon) {

                        lf.aw("subtreeToBinary new lasty\t" + lasty + "\n");
                        lf.aw("subtreeToBinary Subtree size post " + sub.size() + "\n");
                    }

                }
            }

            round++;

            if (logon)
                lf.aw("subtreeToBinary " + round + "\t" + sub.size() + "\t" + open + "\t" + close);
        }


    }

    /**
     * converts a Triple of NEXUS nodes to a binary dendrogram
     *
     * @param a
     * @param b
     * @param c
     * @return ClustalNode retsf currently NULL
     */
    private ClustalNode NodeTripletoBinary(ClustalNode a, ClustalNode b, ClustalNode c) {
        //System.out.println("A:"+a.toString()+"\nB:"+b.toString()+"\nC);
        ClustalNode retsf = null;
        int na = -1;
        int nb = -1;
        String nama = a.a;

        if (nama != null) {
            na = searchNames(nama);
            if (na == -1)
                nama = null;
        }
        String namb = b.a;
        if (namb != null) {
            nb = searchNames(namb);
            if (nb == -1)
                namb = null;
        }

        if (nb != -1 && na != -1) {
            if (nb < na) {
                nama = a.a;
                namb = b.a;
                String tmp = nama;
                nama = namb;
                namb = tmp;
                int t = na;
                na = nb;
                nb = t;
            }
        }

        double kc = c.x.doubleValue();
        double ka = a.x.doubleValue();
        double kb = b.x.doubleValue();

//open/close
        int nowopen = Math.min(a.open, b.open);
        int nowclose = Math.max(a.close, b.close);

        if (logon) {
            lf.aw("NodeTripletoBinary: a.open " + a.open + " a.close " + a.close + ", b.open " + b.open + " b.close " + b.close + ", c.open " + c.open + " c.close " + c.close + ", nowopen " + nowopen + " nowclose " + nowclose + "\n");
            lf.aw("NodeTripletoBinary: " + nama + " : name a," + na + ", dist:" + ka + " , " + namb + ":name b, " + nb + ", dist: " + kb + ", parent dist:" + kc + "\n");
        }
        double ab = ((ka + kb) / (double) 2) + kc;
//System.out.println("lastXavg  b/f add "+lastxavg);
        if (nama != null && namb != null)
            addClustalPair(ab, na, ka, nb, kb, "children", nowopen, nowclose);

        lastxavg = ((double) na + (double) nb) / (double) 2;
        if ((int) lastxavg == 0)
            lastxavg = -1;
        lastbranchlen = kc;

        if (logon)
            lf.aw("!!SETTING lastbranchlen " + lastbranchlen + "\n");

        return retsf;
    }

    /**
     * converts a pair of NEXUS nodes to a binary dendrogram
     */
    private double NodePairtoBinary(ClustalNode a, ClustalNode b) {

        double ab = -1;
        ClustalNode retsf = null;

        int na = -1;
        int nb = -1;
        boolean deepnode = false;
        boolean alldeep = false;
        if (logon) {
            if (a == null)
                lf.aw("node a is null\n");
            if (b == null)
                lf.aw("node b is null\n");
        }

        if (a.a == null && b.a == null)
            alldeep = true;
        if (b != null)
            if (b.a == null) {
                ClustalNode tmp = new ClustalNode(a);

                a = new ClustalNode(b.a, b.x, b.open, b.close);
                b = new ClustalNode(tmp);
            }

        String nama = a.a;
        if (logon)
            lf.aw("name a\t" + nama + "\t" + na + "\n");
        if (nama == null)
            deepnode = true;
        else {
            na = searchNames(nama);
            if (na == -1)
                nama = null;
        }

        String namb = null;
        if (b != null) {
            namb = b.a;
            if (namb == null)
                deepnode = true;
            else {
                nb = searchNames(namb);
                if (nb == -1)
                    namb = null;
                if (logon)
                    lf.aw("name b\t" + namb + "\t" + nb + "\n");
            }
        }
        if (nb != -1 && na != -1) {
            if (na < nb) {
                ClustalNode tm = new ClustalNode(a);
                nama = a.a;
                namb = b.a;
                String tmp = nama;
                nama = namb;
                namb = tmp;
                int t = na;
                na = nb;
                nb = t;
            }
        }

        double ka = a.x.doubleValue();
        double kb = -1;
        if (b != null)
            kb = b.x.doubleValue();

        if (logon)
            lf.aw("NodePairtoBinary. name a: " + nama + ", na: " + na + ", dist: " + ka + " , name b:" + namb + ", nb: " + nb + ", dist: " + kb + "\n");

        int nowopen = -1;
        int nowclose = -1;
        if (nama != null && namb != null) {
            nowopen = Math.min(a.open, b.open);
            nowclose = Math.max(a.close, b.close);
        } else if (nama != null) {
            nowopen = a.open;
            nowclose = a.close;
        } else if (namb != null) {
            nowopen = b.open;
            nowclose = b.close;
        }

        if (nama == null)
            lastbranchlen = ka;
        else if (namb == null)
            lastbranchlen = kb;
        if (ka != -1 && kb != -1)
            ab = (ka + kb) / (double) 2;
        else if (ka != -1)
            ab = ka;
        else if (kb != -1)
            ab = kb;
        if (logon)
            lf.aw("NodePairtoBinary nowopen: " + nowopen + " nowclose: " + nowclose + "\n");
//System.out.println("lastXavg  "+lastxavg);

        if (!deepnode && !alldeep && nama != null && namb != null)
            addClustalPair(ab, na, ka, nb, kb, "children", nowopen, nowclose);
        else if (!alldeep && nama == null && namb != null) {
            addClustalPair(-1, -1, -1, nb, kb, "parentpluschild", nowopen, nowclose);
            //addClustalPair(lasty, lastxavg, ka, nb, kb, "parentpluschild", nowopen, nowclose);/*this line turned off in recent working versions*/
        } else if (!alldeep && namb == null && nama != null) {

            //if(Math.abs(ka-lastxavg) < 10)
            //addClustalPair(lasty, lastxavg, kb, na, ka, "childplusparent", nowopen, nowclose);

            if (logon) {
                lf.aw("nowopen: " + nowopen + "  nowclose: " + nowclose + "\n");
                lf.aw("PRE childplusparent -- lasty: " + lasty + "  lastxavg: " + lastxavg + "  kb:" + kb + "  na: " + na + "  ka: " + ka + "\n");
            }
            addClustalPair(lasty, lastxavg, kb, na, ka, "childplusparent", nowopen, nowclose);
            //else
            //	addClustalPair(lasty, -1, -1, na, ka, "childplusparent", nowopen, nowclose);/*this line turned off in recent working versions */

            if (logon)
                lf.aw("lasty:" + lasty + "  lastxavg:" + lastxavg + "  kb:" + kb + "  na:" + na + "  ka:" + ka + "\n");
        }
//else
//	addClustalPair(ab, na, ka, nb, kb, "parents", nowopen, nowclose);/*this line turned off in recent working versions */

        if (na != -1 && nb != -1) {
            lastxavg = ((double) na + (double) nb) / (double) 2;
            if (lastxavg == 0)
                lastxavg = -1;
            if (logon)
                lf.aw("new lasty " + lasty + " in nodepairtobinary\n");
        }
        return ab;
    }

    /**
     * extracts the parent node if any
     */
    private ClustalNode extractParent(String k, int o, int c) {
        if (k.indexOf(");") != -1)
            k = k.substring(0, k.length() - 2);
        else if (k.indexOf(")") != -1 || k.indexOf(",") != -1)
            k = k.substring(0, k.length() - 1);

        //System.out.println("PARENT parsing:: "+k);
        ClustalNode r = new ClustalNode();
        r = parseClustBranch(k);
        r.open = o;
        r.close = c;

        if (r.a == null) {
            r = null;
            if (logon)
                lf.aw("PARENT parsing leads to NULL!\n");
        } else if (logon)
            lf.aw("Parent ClustalNode\t" + r.toString() + "\n");

        return r;
    }

    /**
     * Extracts the rightmost node if any.
     *
     * @param k     text data to parse for node details
     * @param level the current dendrogram level
     * @return Clustalnode r new RIGHT (relative to parent) node
     */
    private ClustalNode extractNodeRight(String k, int level, int o, int c) {

        ClustalNode r = new ClustalNode();

//System.out.println("extracting right "+k);

        int comma = k.indexOf(",");
        int close = k.indexOf(")");

        int cutleft = comma;
        if (cutleft == -1 || close == -1)
            cutleft = 0;
        int cutright = -1;

        if (close != -1)
            cutright = close;
        else
            cutright = comma;

        if (cutright != -1) {
            if (cutleft != 0)
                cutleft += 1;
            String parse = k.substring(cutleft, cutright);
            r = parseClustBranch(parse);
            r.open = o;
            r.close = c;
        }
        if (r.a == null) {
            //r = null; /*line turned off in recent working versions */
            if (logon)
                lf.aw("RIGHT parsing leads to NULL!\n");
        } else if (logon)
            lf.aw("R ClustalNode\t" + r.toString() + "\n");


        return r;
    }

    /**
     * Extracts the leftmost node if any.
     *
     * @param k     text data to parse for node details
     * @param level the current dendrogram level
     * @return Clustalnode r new LEFT (relative to parent) node
     */
    private ClustalNode extractNodeLeft(String k, int level, int o, int c) {

        ClustalNode r = new ClustalNode();

//System.out.println("extracting left "+k);

        int colon = k.indexOf(":");
        //if(colon == 1)
        //	k = k.substring(1);

        int open = k.indexOf("(");

        int comma = k.indexOf(",");
        int close = k.indexOf(")");

        int cutleft = Math.max(0, open);
        int cutright = Math.min(comma, close);
        if (cutright == -1)
            cutright = Math.max(comma, close);

        if (cutleft != -1 && cutright != -1) {
            if (cutleft != 0)
                cutleft += 1;
            String parse = k.substring(cutleft, cutright);
            r = parseClustBranch(parse);
            r.open = o;
            r.close = c;
            if (logon)
                lf.aw("LEFT\t" + r.toString());
        }
        if (r.a == null) {
//r = null; /*line turned off in recent working versions */
            if (logon)
                lf.aw("LEFT parsing leads to NULL!\n");
        } else if (logon)
            lf.aw("L ClustalNode\t" + r.toString() + "\n");

        return r;
    }

    /**
     * the binary tree y-axis distance for the New Hampshire format tree
     */
    private double nodeDist(double ka, double kb) {
        double ret = -1;

        double ab = -1;

        if (ka != kb)
            ab = (double) Math.sqrt(Math.abs(ka * ka - ((1 - kb * kb + ka * ka) / (double) 2) * ((1 - kb * kb + ka * ka) / (double) 2)));
        else
            ab = (double) Math.sqrt(Math.abs(ka * ka - (double) .5 * (double) .5));

        if (logon)
            lf.aw("y dist  " + ab + "\n");

        return ret;
    }

    /**
     * sets Branch variable of global dndtree vector
     */
    private void setDNDTREEbranch(double dist1, double dist2, Branch b) {
        for (int i = 0; i < dndtree.size(); i++) {
            ArrayList cur = (ArrayList) dndtree.get(i);
            for (int j = 0; j < cur.size(); j++) {
                ClustalNode now = (ClustalNode) cur.get(j);
                double x = now.x.doubleValue();
                if (x == dist1 || x == dist2)
                    now.br = new Branch(b);
            }
        }
    }

    /**
     * adds a pair of sequences to the New Hampshire format tree
     */
    private void addClustalPair(double ab, double na, double ka, double nb, double kb, String type, int open, int close) {
        //System.out.println("clustal pair  "+type);
        Branch br = new Branch();
//System.out.println(ab+"\t"+na+"\t"+ka+"\t"+nb+"\t"+kb+"\t"+type);
        if (type.equals("children")) {
            if (logon)
                lf.aw("ADDING children\n");
            br.x[0] = (double) na;
            br.y[0] = (double) 0.0;
            br.x[1] = (double) na;
            br.y[1] = ab;
            br.x[2] = (double) nb;
            br.y[2] = ab;
            br.x[3] = (double) nb;
            br.y[3] = (double) 0.0;
            br.open = open;
            br.close = close;
            br.setDefaultMinMax();
            br.closemin--;
            setDNDTREEbranch(ka, kb, br);
            if (logon)
                lf.aw("CHILD " + br.fulltoString() + "\n");
            points.add(br);
            lasty = ab;
            if (logon)
                lf.aw("setting new lasty in ADD children " + lasty + "\n");
        } else if (type.equals("parentpluschild")) {
            if (logon)
                lf.aw("parentpluschild lasty\t" + ab + "\tlastxavg\t" + na + "\n");

            if (lastbranchlen != -1 && na != -1) {
                kb = ab + lastbranchlen;
                if (logon)
                    lf.aw("!!ADDING lastbranchlen " + lastbranchlen + "\t" + ab + "\n");
                lastbranchlen = -1;
            } else if (ab > kb && na != -1) {
                kb = ab + .005;
                if (logon)
                    lf.aw("!!INCREMENTING parent NODE " + kb + "\t" + ab + "\n");
            }
            br.x[0] = (double) na;
            br.y[0] = (double) ab;
            br.x[1] = (double) na;
            br.y[1] = kb;
            br.x[2] = (double) nb;
            br.y[2] = kb;
            br.x[3] = (double) nb;
            br.y[3] = (double) 0.0;
            br.open = open;
            br.close = close;
            br.setDefaultMinMax();
            points.add(br);
            if (logon)
                lf.aw("Added parentpluschild: " + br.fulltoString());
            lasty = kb;
            if (logon)
                lf.aw("setting new lasty in ADD parentpluschild " + lasty + "\n");
        } else if (type.equals("childplusparent")) {

            if (na == 0)
                na = -1;

            if (lastbranchlen != -1) {
                kb = ab + lastbranchlen;
                if (logon)
                    lf.aw("!!ADDING lastbranchlen " + lastbranchlen + "\t" + ab + "\n");
                lastbranchlen = -1;
            } else if (ab > kb) {
                kb = ab + .005;
                if (logon)
                    lf.aw("!!INCREMENTING parent NODE " + kb + "\t" + ab + "\n");
            }
            if (logon)
                lf.aw("na: " + na + "  nb: " + nb + "  ka: " + ka + "  kb: " + kb + "\n");

            if (nb != -1 && ka != -1)// && na != -1 && kb != -1 && ka != -1)
            {
                br.x[0] = (double) nb;
                br.y[0] = (double) 0.0;
                br.x[1] = (double) nb;
                br.y[1] = kb;
                br.x[2] = (double) na;
                br.y[2] = kb;
                br.x[3] = (double) na;
                br.y[3] = (double) ab;
                lasty = kb;

                if (logon)
                    lf.aw("setting new lasty in ADD childplusparent " + lasty + "\n");
            } else if (ka == -1) {
                br.x[0] = (double) -1;
                br.y[0] = (double) -1;
                br.x[1] = (double) -1;
                br.y[1] = (double) -1;
                br.x[2] = (double) nb;
                br.y[2] = (double) kb;
                br.x[3] = (double) nb;
                br.y[3] = (double) 0.0;
            }
            br.open = open;
            br.close = close;
            br.setDefaultMinMax();
            points.add(br);
        }

        if (logon)
            lf.aw("new points size\t" + points.size() + "\t" + br.fulltoString() + "\n");

        lastxavg = ((double) br.x[0] + (double) br.x[3]) / (double) 2;
        if (lastxavg == 0)
            lastxavg = -1;
        if (logon)
            lf.aw("new lastxavg in loadBRANCH " + lastxavg + "\n");
    }

    /**
     * searches a vector of tree labels with a <code>string</code>
     *
     * @param k search query
     * @return ret <code>int</code> representing the index in the *array (-1 if false)
     */
    public int searchNames(String k) {
        int ret = -1;
        for (int i = 1; i < names.size(); i++) {
            String l = (String) names.get(i);
            //System.out.println(i+"\tname\t"+l);
            if (l.equals(k)) {
                ret = i;
                break;
            }
        }
        return ret;
    }

    /**
     * Returns the maximum open parenthesis value from all branches.
     */
    private void findMaxOpen() {
        int psize = points.size();
        maxopen = -1;
        for (int a = 0; a < psize; a++) {
            Branch ba = (Branch) points.get(a);
            if (ba.open > maxopen)
                maxopen = ba.open;
        }
    }

    /**
     * Returns the maximum close parenthesis value from all branches.
     */
    private void findMaxClose() {
        int psize = points.size();
        maxclose = -1;
        for (int a = 0; a < psize; a++) {
            Branch ba = (Branch) points.get(a);
            if (ba.close > maxclose)
                maxclose = ba.close;
        }
    }

    /**
     * Creates an array of the UNconnected elements in points.
     * If node is unjoined returns array value -1.
     *
     * @return double[] unjoined
     */
    private int[][] fillLoose() {
        int psize = points.size();
        int[][] loose = new int[psize][3];
        for (int a = 0; a < psize; a++) {
            Branch ba = (Branch) points.get(a);

            int testb = testRightLooseBranch(points, a);
            int testc = testLeftLooseBranch(points, a);

            if (testb != -1 && testc != -1) {
                loose[a][0] = testb;
            } else if (testb != -1 || testc != -1) {

                loose[a][0] = -10;
                loose[a][1] = ba.open;
                loose[a][2] = ba.close;
                //System.out.println("right loose "+testb+"\topen "+ba.open+"\tclose "+ba.close+"\n");
                if (logon)
                    lf.aw("loose " + testb + "\topen " + ba.open + "\tclose " + ba.close + "\n");
            }
        }
        return loose;
    }

    /**
     * Creates an array of the UNconnected elements in points.
     * If node is unjoined returns array value -1.
     *
     * @return double[] unjoined
     */
    private int[][] fillUnjoined() {
        int psize = points.size();
        int[][] unjoined = new int[psize][7];
        for (int a = 0; a < psize; a++) {
            Branch ba = (Branch) points.get(a);
            int testa = testConnectBranch(points, a);
            //int testb = testRightLooseBranch(points, a);

            if (testa != -1)// && testb != -1)
            {
                unjoined[a][0] = testa;
                unjoined[a][1] = ba.open;
                unjoined[a][2] = ba.close;
                unjoined[a][3] = ba.openmin;
                unjoined[a][4] = ba.openmax;
                unjoined[a][5] = ba.closemin;
                unjoined[a][6] = ba.closemax;
            } else if (testa == -1) {

                unjoined[a][0] = -1;
                unjoined[a][1] = ba.open;
                unjoined[a][2] = ba.close;
                unjoined[a][3] = ba.openmin;
                unjoined[a][4] = ba.openmax;
                unjoined[a][5] = ba.closemin;
                unjoined[a][6] = ba.closemax;

                //System.out.println("unjoined connect "+testa+"\topen "+ba.open+"\tclose "+ba.close+"\n");
                if (logon)
                    lf.aw(a + " unjoined connect " + testa + "  open " + ba.open + "  close " + ba.close + "  openmin " + ba.openmin + "  openmax " + ba.openmax + "  closemin " + ba.closemin + "  closemax " + ba.closemax + "\n");
            }
        }
        return unjoined;
    }

    /**
     * Joins remaining pairs of nodes in the NEXUS format by sorting unjoined branches.
     */
    private void newjoinPairs() {
        if (logon)
            lf.aw("STARTED newjoinPairs()\n");
        int countunjoin = 0;
//encoded as -1 in unjoined[x][0]
        int[][] unjoined = fillUnjoined();
        countunjoin = mathy.stat.countOccurence(unjoined, -1);
//encoded as -10 in loose[x][0]
        int[][] loose = fillLoose();
        findMaxClose();
        findMaxOpen();

        int limit = 0;

        int origsize = points.size();
        int origlimit = limit;

        while (countunjoin > 1) {

            origlimit = limit;

//System.out.println("PRE LIMIT "+size+"\tcountloose "+countunjoin+"\n");
//System.out.println("Unconnected branches remaining "+countunjoin+"\t"+size+"\n");

            if (logon)
                lf.aw("PRE LIMIT " + limit + "\tcountloose " + countunjoin + "\n");
//System.out.println("POST LIMIT "+size+"\tcountloose "+countunjoin+"\n");

            limit = searchLimitAndConnect(limit, unjoined, loose);
            unjoined = fillUnjoined();
            loose = fillLoose();
            countunjoin = mathy.stat.countOccurence(unjoined, -1);
//System.out.println("POST LIMIT "+size+"\tcountloose "+countunjoin+"\n");

            if (limit == origlimit) {

                limit++;
            }
        }
/*
//Connects the last unconnected Branch
if(countunjoin == 1) {

int poslp = mathy.stat.findMinLeftPosMinusOne(unjoined);
Branch bb = (Branch)points.get(poslp);
int k = findMintoRootBranch(points, -1, poslp);
Branch bl = (Branch)points.get(poslp);
Branch bk = (Branch)points.get(k);
//System.out.println("ROOT CONNECT LEFT "+bl.toString());
//System.out.println("ROOT CONNECT RIGHT "+bk.toString());
if(logon)
{
lf.aw("ROOT CONNECT RIGHT "+bl.toString()+"\n");
lf.aw("ROOT CONNECT RIGHT "+bk.toString()+"\n");
}
boolean get = connectBranches(bl, poslp, bk, k);
}
*/
    }

    /**
     * Tests if two branches share and edge by tracing the underlying terminal nodes.
     *
     * @param a
     * @param b
     * @return
     */
    private boolean testShareEdge(Branch a, Branch b) {
        boolean ret = false;

        Branch cura = new Branch(a);
        Branch curb = new Branch(b);
//if(logon)
//   lf.aw("testShareEdge a: "+a.toString()+"\nb: "+b.toString()+"\n");
/*
int ar =-1;//ab_lr_vals[0] a right
int al = -1;//ab_lr_vals[1]	a left
int br = -1;//ab_lr_vals[2] b right
int bl = -1;//ab_lr_vals[3] b left
*/
        int[] ab_lr_vals = determineChildren(a, b);

        int leftind, rightind = -1;
        while (ab_lr_vals[0] <= 0) {
            rightind = findRightChild(cura);
            if (rightind == -1)
                break;
            cura = (Branch) points.get(rightind);
            ab_lr_vals[0] = determineRightChild(cura);
        }
        cura = new Branch(a);
        while (ab_lr_vals[1] <= 0) {
            leftind = findLeftChild(cura);
            if (leftind == -1)
                break;
            cura = (Branch) points.get(leftind);
            ab_lr_vals[1] = determineLeftChild(cura);

            //if(logon)
            //    lf.aw("findLeftChild a:"+leftind+"\t"+ab_lr_vals[1]+"\n");
        }
        while (ab_lr_vals[2] <= 0) {
            rightind = findRightChild(curb);
            if (rightind == -1)
                break;
            curb = (Branch) points.get(rightind);
            ab_lr_vals[2] = determineRightChild(curb);
        }
        curb = new Branch(b);
        while (ab_lr_vals[3] <= 0) {
            leftind = findLeftChild(curb);
            if (leftind == -1)
                break;
            curb = (Branch) points.get(leftind);
            ab_lr_vals[3] = determineLeftChild(curb);

            //if(logon)
            //    lf.aw("findLeftChild b:"+leftind+"\t"+ab_lr_vals[3]+"\n");
        }
//if(logon)
//    lf.aw("share edge 1:"+ab_lr_vals[1]+" "+ab_lr_vals[0]+" 2:"+ab_lr_vals[3]+" "+ab_lr_vals[2]+"\n");

//if(ab_lr_vals[1] != 0)
        if (ab_lr_vals[0] == ab_lr_vals[2] - 1 || ab_lr_vals[0] == ab_lr_vals[3] - 1)
            ret = true;
//else if(ab_lr_vals[1] == 0)
//	if(ab_lr_vals[0] != ab_lr_vals[3]-1)
//		ret = false;

        if (ab_lr_vals[1] == 0 && ab_lr_vals[3] == 0)
            ret = false;

//if(logon)
//   lf.aw("edge test "+ret+"\n");

        return ret;
    }


    /**
     * Determines if there are any LEFT terminal nodes in ba.
     *
     * @param ba
     * @return
     */
    private int determineLeftChild(Branch ba) {
        int ret = -1;
        if (ba.y[0] == 0)
            ret = (int) ba.x[0];

        return ret;
    }


    /**
     * Determines if there are any LEFT terminal nodes in ba.
     *
     * @param ba
     * @return
     */
    private int determineRightChild(Branch ba) {
        int ret = -1;
        if (ba.y[3] == 0)
            ret = (int) ba.x[3];

        return ret;
    }


    /**
     * Determines if there are any terminal nodes in either ba or bb.
     * ab_lr_vals[0] a right
     * ab_lr_vals[1] a left
     * ab_lr_vals[2] b right
     * ab_lr_vals[3] b left
     *
     * @param ba
     * @param bb
     * @return
     */
    private int[] determineChildren(Branch ba, Branch bb) {
        int[] ab_lr_vals = new int[4];
        if (ba.y[3] == 0)
            ab_lr_vals[0] = (int) ba.x[3];
        if (ba.y[0] == 0)
            ab_lr_vals[1] = (int) ba.x[0];
        if (bb.y[3] == 0)
            ab_lr_vals[2] = (int) bb.x[3];
        if (bb.y[0] == 0)
            ab_lr_vals[3] = (int) bb.x[0];

        return ab_lr_vals;
    }

    /**
     * Finds the LEFT child for a parent Branch.
     *
     * @param naw
     * @return int b
     */
    private int findLeftChild(Branch naw) {
        int b = -1;
        for (int i = 0; i < points.size(); i++) {
            Branch now = (Branch) points.get(i);
            if (now != null) {

                if (Math.abs(now.y[1] - naw.y[0]) < ACCURACY && Math.abs(((now.x[1] + now.x[2]) / (double) 2) - naw.x[0]) < ACCURACY) {
                    b = i;
                    break;
                }
            }

        }

        return b;
    }

    /**
     * Finds the RIGHT child for a parent Branch.
     *
     * @param naw
     * @return
     */
    private final int findRightChild(Branch naw) {
        int b = -1;
        for (int i = 0; i < points.size(); i++) {
            Branch now = (Branch) points.get(i);
            if (now != null)
                if (Math.abs(now.y[1] - naw.y[3]) < ACCURACY && Math.abs(((now.x[1] + now.x[2]) / (double) 2) - naw.x[3]) < ACCURACY)
// && (naw.x[3] >= now.x[0] && naw.x[3] <= now.x[3]))
                {
                    b = i;
                    break;
                }
        }
        return b;
    }

    /**
     * Searches the (open, close) parenthesis statest for next Branch to join/
     */
    private int searchLimitAndConnect(int limit, int[][] unjoined, int[][] loose) {
        int countunjoin = mathy.stat.countOccurence(unjoined, -1);

        int poslp = -1;
        int posrp = -1;
        int origsize = points.size();

//begin fori
        for (int i = 0; i < points.size(); i++) {

            Branch ba = (Branch) points.get(i);
//begin littleIFi
            if (unjoined[i][0] == -1 || loose[i][0] == -10) {

//begin forj
                for (int j = i + 1; j < points.size(); j++) {

                    origsize = points.size();

                    Branch bb = (Branch) points.get(j);

//begin littleIFj
                    if (unjoined[j][0] == -1 || loose[j][0]
                            == -10) {
/*
int testopen = unjoined[j][1]-unjoined[i][1];
int testclose = unjoined[j][2]-unjoined[i][2];
int testJopenclose = unjoined[j][1]-unjoined[j][2];
int testcloseminmax = unjoined[j][5]-unjoined[i][6];
int testopenminmax = unjoined[j][3]-unjoined[i][4];
int testigap = unjoined[i][1]-unjoined[i][2];
int testjgap = unjoined[j][1]-unjoined[j][2];

int testiclose = unjoined[i][6]-unjoined[i][5];
int testiopen = unjoined[i][4]-unjoined[i][3];
int itest = testiclose-testiopen;
*/
                        int testformmax = unjoined[i][6] + unjoined[j][4] - unjoined[j][6];
                        int testformmin = unjoined[i][5] + unjoined[j][4] - unjoined[j][6];

                        boolean go = true;
                        if (loose[i][0] == -10 && loose[j][0] == -10)
                            go = false;

//begin looseIF - never join with an outlier as the first pair
//loose[i][0] != -10 &&
                        if (go) {
//begin BIGIF // testopen <= size &&

/*
case1: closeimax + openjmax-closejmax = openimax
case2: 1,2 & 1,3
case3: 3,5 & 4,5
*/
                            boolean gobig = false;
                            if (Math.abs(testformmax - unjoined[i][4]) < limit && Math.abs(unjoined[i][6] - unjoined[j][5]) < limit)
                                gobig = true;
                            else if (unjoined[j][3] - unjoined[i][4] == 0 && unjoined[j][5] - unjoined[i][6] == 1)
                                gobig = true;
                            else if (limit > 1 && unjoined[j][5] - unjoined[i][6] == 0 && unjoined[j][3] - unjoined[i][4] == 1)
                                gobig = true;


                            boolean sharedge = testShareEdge(ba, bb);

                            if (limit > 10) {
                                //System.out.println("searchLimitAndConnect: size > 10 "+i+"\t"+j);
                            }
                            if (countunjoin <= 3 && limit > 15) {
                                //break;
                                gobig = true;
                                //sharedge = true;
                            }

                            if (sharedge & gobig) {

                                if (logon) {
                                    lf.aw("testformmin: " + testformmin + "  testformmax: " + testformmax + "  i_open_min: " + unjoined[i][3] + "\n");
                                    lf.aw("i_close_min: " + unjoined[i][5] + "  j_open_max: " + unjoined[j][4] + "  j_open_min: " + unjoined[j][6] + "\n");
                                    lf.aw("searchLimitAndConnect i: " + i + "\tj: " + j + "\tlimit: " + limit + "\n");
                                    lf.aw(limit + " " + i + " LEFT " + ba.fulltoString() + "\n");
                                    lf.aw(limit + " " + j + " RIGHT " + bb.fulltoString() + "\n");
                                }

                                boolean change = connectBranches(ba, i, bb, j);

                                if (change) {

                                    checkLeftRight();
                                    unjoined = fillUnjoined();
                                    loose = fillLoose();
                                    countunjoin = mathy.stat.countOccurence(unjoined, -1);
                                    i = -1;
                                    if (logon)
                                        lf.aw("Resetting CONNECT loop 0\n");
                                    break;
                                }

                                unjoined = fillUnjoined();
                                loose = fillLoose();
                                countunjoin = mathy.stat.countOccurence(unjoined, -1);

                            }//end BIGIF
                        }//end looseIF
                    }//end litteIFj

                }//end forj
                if (i == -1) {
                    if (logon)
                        lf.aw("Resetting CONNECT loop FINAL\n");
                    i = 0;
                    limit = 0;
                }
            }//end litteIFi

        }//end fori

        if (logon)
            lf.aw("Return LIMIT " + limit + "\n");
        return limit;
    }


    /**
     * Checks for left-right node consistency left < right in jevtrace infrastructure.
     */
    private void checkLeftRight() {
        for (int i = 0; i < points.size(); i++) {

            Branch cur = (Branch) points.get(i);
            if (cur.x[0] > cur.x[3]) {
                Branch copy = new Branch(cur);
                copy.x[0] = cur.x[3];
                copy.y[0] = cur.y[3];
                copy.x[1] = cur.x[2];
                copy.x[2] = cur.x[1];
                copy.x[3] = cur.x[0];
                copy.y[3] = cur.y[0];
                points.set(i, (Branch) copy);
            }
        }
    }

    /**
     * Adds connecting Branch for two branches
     *
     * @param ba
     * @param a
     * @param bb
     * @param b
     * @return
     */
    private boolean connectBranches(Branch ba, int a, Branch bb, int b) {
        double maxy = Math.max(ba.y[1], bb.y[1]) + (double) .001;
        Branch br = new Branch();
        int rema = -1;
        int remb = -1;
        boolean addnode = false;

//case0 both nodes complete
        if (ba.x[1] != -1 && bb.x[1] != -1) {
            br.x[0] = (ba.x[1] + ba.x[2]) / (double) 2;
            br.y[0] = ba.y[1];
            br.x[1] = (ba.x[1] + ba.x[2]) / (double) 2;
            br.y[1] = maxy;
            br.x[2] = (bb.x[1] + bb.x[2]) / (double) 2;
            br.y[2] = maxy;
            br.x[3] = (bb.x[1] + bb.x[2]) / (double) 2;
            br.y[3] = bb.y[1];

            //System.out.println("No loose "+br.toString());

            if (logon) {
                lf.aw("No loose " + br.toString() + "\n");
                lf.aw("ba " + ba.toString() + "\n");
                lf.aw("bb " + bb.toString() + "\n");
            }
        }
        //case1 ba complete, bb loose left
        else if (ba.x[1] != -1 && bb.x[1] == -1 && bb.x[3] != -1) {
            Branch cura = new Branch();
            Branch curb = new Branch();
            remb = b;
            if (ba.x[3] > bb.x[3]) {
                cura = new Branch(bb);
                curb = new Branch(ba);
                ba = new Branch(cura);
                bb = new Branch(curb);
                remb = a;
            }

            br.x[0] = (ba.x[1] + ba.x[2]) / (double) 2;
            br.y[0] = ba.y[1];
            br.x[1] = (ba.x[1] + ba.x[2]) / (double) 2;
            ;
            br.y[1] = maxy;
            br.x[2] = bb.x[2];
            br.y[2] = maxy;
            br.x[3] = bb.x[3];
            br.y[3] = bb.y[3];
            points.remove(remb);
            //System.out.println("Right loose "+br.toString());

            if (logon) {
                lf.aw("Right loose " + br.toString() + "\n");
                lf.aw("ba " + ba.toString() + "\n");
                lf.aw("bb " + bb.toString() + "\n");
            }
        }
        //case1 ba loose left, bb complete
        else if (ba.x[1] == -1 && bb.x[1] != -1 && ba.x[3] != -1) {
            Branch cura = new Branch();
            Branch curb = new Branch();
            rema = a;
            if (ba.x[3] > bb.x[3]) {
                cura = new Branch(bb);
                curb = new Branch(ba);
                ba = new Branch(cura);
                bb = new Branch(curb);
                rema = b;
            }
            br.x[0] = ba.x[3];
            br.y[0] = ba.y[3];
            br.x[1] = ba.x[2];
            br.y[1] = maxy;
            br.x[2] = (bb.x[1] + bb.x[2]) / (double) 2;
            br.y[2] = maxy;
            br.x[3] = (bb.x[1] + bb.x[2]) / (double) 2;
            br.y[3] = bb.y[1];
            points.remove(rema);
            //System.out.println("Left loose "+br.toString());
            if (logon)
                lf.aw("Left loose " + br.toString() + "\n");

            if (logon) {
                lf.aw("ba " + ba.toString() + "\n");
                lf.aw("bb " + bb.toString() + "\n");
            }
        }

//open/close
        br.openmin = Math.min(ba.open, bb.open);

/*
//if(ba.closemin != bb.closemin-1)//ba.open != bb.open &&
{
if(br.openmin > 0)
	{
		br.openmin--;
		if(logon)
		    lf.aw("Modifying br.openmin from "+Math.min(ba.open, bb.open)+"\tto "+br.openmin+"\n";
	}
}
	else if(logon)
	    lf.aw("NOT Modifying br.openmin\n";
*/

        br.openmax = Math.max(ba.openmax, bb.openmax);
        br.closemin = Math.min(ba.closemin, bb.closemin);
        br.closemax = Math.max(ba.closemax, bb.closemax);

        br.open = br.openmin;
        br.close = br.closemax;

        if (logon) {
            lf.aw("ba.openmin " + ba.openmin + "\tba.openmax " + ba.openmax + "\t ba.closemin " + ba.closemin + "\tba.closemax " + ba.closemax + "\n");
            lf.aw("bb.openmin " + bb.openmin + "\tbb.openmax " + bb.openmax + "\t bb.closemin " + bb.closemin + "\tbb.closemax " + bb.closemax + "\n");
            lf.aw("JOINED PAIR " + br.fulltoString() + "\n");
            lf.aw("join pair ba.open " + ba.open + " ba.close " + ba.close + " bb.open " + bb.open + " bb.close " + bb.close + "\n");
        }

        if (rema != -1)
            b--;
        if (remb != -1)
            b--;

        addnode = true;
        points.add(b + 1, (Branch) br);
//points.add((Branch)br);
        if (br.close - br.open >= 0) {
            int next = checkNextClose(b + 1, br.openmax, br.closemax);
            if (logon)
                lf.aw("NEXT " + next + "\n");
            if (next != -1) {
                if (logon)
                    lf.aw("Incrementing " + br.closemax + " " + (br.closemax + 1) + "\n");
                br.closemax++;
                br.close = br.closemax;
                points.set(b + 1, (Branch) br);
            }
        }

        return addnode;
    }

    /**
     * Returns the next close parenthesis value for the next closest Branch in the tree data. Searches through all branches (except query) because of possible changes in order when connecting branches.
     *
     * @param previndex
     * @param prevopen
     * @param prevclose
     * @return
     */
    private int checkNextClose(int previndex, int prevopen, int prevclose) {
        int minopen = 10000000;
        int nextclose = -1;
        for (int i = 0; i < points.size(); i++) {
            if (i != previndex) {
                Branch cur = (Branch) points.get(i);
                if (cur.openmin - prevopen >= 0 && cur.closemin - prevclose >= 0 && minopen > prevopen + 1) {
                    minopen = cur.openmin;
                    nextclose = cur.closemin;
                    if (logon)
                        lf.aw("open " + cur.open + "\tclose " + cur.close + "\n");
                    if (cur.closemin - prevclose == 0) {
                        nextclose = -1;
                        i = points.size();
                        break;
                    }
                }

            }
        }

        if (nextclose != -1 && logon)
            lf.aw("NEXTMINOPEN " + minopen + " NEXTMINCLOSE " + nextclose + "\n");
        return nextclose;
    }

    /**
     * Finds first unjoined Branch.
     * Returns -1 if unjoined.
     *
     * @param copy data to search
     * @param not  index to omit in search
     * @return int ka
     */
    private int unJoined(ArrayList copy, int not) {
        int ka = -1;

        for (int i = 0; i < copy.size(); i++) {
            if (i != not) {
                int testa = testConnectBranch(copy, i);
                if (testa == -1) {
                    ka = i;
                    break;
                }
            }
        }
        return ka;
    }


    /**
     * Makes the CLUSTALW dendrogram distinct at pixel resolution.
     */
    private void makeDistinctPixel() {

        double disincr = .1;

        for (int a = 0; a < points.size(); a++) {
            Branch bra = (Branch) points.get(a);

            for (int b = a + 1; b < points.size(); b++) {
                Branch brb = (Branch) points.get(b);
                if (brb.y[1] == bra.y[1]) {

                    int parind = findParent(brb);
                    if (parind != -1) {
                        Branch par = (Branch) points.get(parind);

                        if (par.y[0] == brb.y[1])
                            par.y[0] += disincr;
                        else if (par.y[3] == brb.y[1])
                            par.y[3] += disincr;
                        points.set(parind, (Branch) par);
                    }

                    brb.y[1] += disincr;
                    brb.y[2] += disincr;

                    points.set(b, (Branch) brb);

                }
            }
        }

/*
	double scale = 1000;
	//scales all y-Coord
	for(int a =0; a < points.size();a++) {

		Branch bra = (Branch)points.get(a);
	for(int i=0; i < 4; i++)
			bra.y[i]*=scale;

	points.set(a, (Branch)bra);
	}
*/

    }

    /**
     * Finds the index of the Branch in the tree.
     */
    public final int findParent(Branch s) {

        int ret = -1;
        for (int i = 0; i < points.size(); i++) {

            Branch now = (Branch) points.get(i);
            if (now != null) {

                if (now.y[0] == s.y[1] && now.y[3] == s.y[1]) {

                    ret = i;
                    break;
                }

            }
        }
        return ret;
    }

    /**
     * Finds the index of the Branch in the tree.
     */
    public final int findIndex(Branch s) {

        int ret = -1;
        for (int i = 0; i < points.size(); i++) {

            Branch now = (Branch) points.get(i);
            if (now != null) {

                if (Metrics.euc2dist(now.x[0], now.y[0], s.x[0], s.y[0]) == 0 && Metrics.euc2dist(now.x[3], now.y[3], s.x[3], s.y[3]) == 0) {

                    ret = i;
                    break;
                }

            }
        }
        return ret;
    }

    /**
     * Makes the CLUSTALW dendrogram distinct at pixel resolution.
     */
    private void reScale() {

        for (int a = 0; a < points.size(); a++) {
            Branch bra = (Branch) points.get(a);
            for (int b = a + 1; b < points.size(); b++) {
                Branch brb = (Branch) points.get(b);
                double diff = Math.abs(brb.y[1] - bra.y[1]);
                if (diff < 1) {
                    bra.y[1] *= 5;
                    bra.y[2] *= 5;
                }
            }
        }
    }

    /**
     * Makes the CLUSTALW dendrogram distinct at pixel resolution.
     */
    private void makeNonNeg() {
        for (int a = 0; a < points.size(); a++) {
            Branch bra = (Branch) points.get(a);

            if (bra.y[0] < 0) {
                bra.y[0] = Math.abs(bra.y[0]);
            }
            if (bra.y[3] < 0) {
                bra.y[3] = Math.abs(bra.y[3]);
            }
        }
    }

    /**
     * @param s
     * @return
     */
    public ArrayList retNotChildNodes(Salact s) {

        curlist = retChildNodes(s);
        ArrayList retlist = new ArrayList();

        for (int j = 0; j < points.size(); j++) {

            Salact curpt = (Salact) points.get(j);
            int count = -1;
            if (curpt != null && curpt.y != minY) {
                for (int i = 0; i < curlist.size(); i++) {

                    Salact cur = (Salact) curlist.get(i);
                    if (curpt == cur) {
                        break;
                    } else
                        count++;
                }

                if (count == curlist.size())
                    retlist.add(curpt);
            }
        }
        return retlist;
    }

    /**
     * @param s
     * @return
     */
    public int[] retChildNodesArray(Salact s) {

        retChildNodes(s);

        int[] ret = new int[curlist.size()];
        for (int i = 0; i < curlist.size(); i++) {
            Salact now = (Salact) curlist.get(i);
            ret[i] = findinSel(now);
            //System.out.println("retChildNodesArray " + i + "\t" + ret[i] + "\t" + now);
        }
        return ret;
    }

    /**
     * Finds index of Salact in selectable nodes.
     *
     * @param s
     * @return
     */
    public final int findinSel(Salact s) {

        int ret = -1;

        for (int i = 0; i < sel.size(); i++) {
            Salact b = (Salact) sel.get(i);
            //if ((Math.useAbs(s.x - b.x) < jevtrace.ACCURACY) && (Math.useAbs(s.y - b.y) < jevtrace.ACCURACY)) {
            if (s.x == b.x && s.y == b.y) {
                return i;
            }
        }

        /*if (ret == -1) {

            for (int i = 0; i < sel.size(); i++) {
                Salact b = (Salact) sel.get(i);
                if ((Math.useAbs(s.x - b.x) < .1)) {
                    System.out.println("findinSel matched x "+s.x+"\t"+b.x+"\tnot y "+s.y+"\t"+b.y);
                }
                if ((Math.useAbs(s.x - b.x) < .1)) {
                    System.out.println("findinSel matched y "+s.y+"\t"+b.y+"\tnot x "+s.x+"\t"+b.x);
                }
            }
        }*/

        System.out.println("findinSel no match y " + s);
        return ret;
    }

    /**
     * @param s
     * @return
     */
    public ArrayList retChildNodes(Salact s) {

        curlist = new ArrayList();
        //curlist_array = new int[points.size()];

        int index = 0;
        int lastbig = -1;
        for (int j = 0; j < points.size(); j++) {

            //Branch ne = (Branch) it.next();
            Branch ne = (Branch) points.get(j);
            if (ne != null) {

                boolean go = false;

                if (s.y == ne.y[1])
                    go = true;

                if (go) {
                    //System.out.println("FOUND MATCHING "+s.y+"\t"+ne.y[1]);
                    if ((Math.abs(s.x - ((ne.x[2] + ne.x[1]) / (double) 2))) < ACCURACY) {

                        if (lastbig != -1) {

                            Branch last = (Branch) points.get(lastbig);
                            if (Math.abs(s.y - last.y[1]) > Math.abs(s.y - ne.y[1]))
                                lastbig = j;

                            //System.out.println("LASTBIG "+lastbig+"\tj "+j);
                        } else {
                            lastbig = j;
                            //System.out.println("LASTBIG "+lastbig+"\tj "+j);
                        }
                    }
                }
            }
        }
        index = lastbig;

        addMember(index, s.c);

        return curlist;
    }


    /**
     * Recursive call to find all children of a parent.
     *
     * @param index
     * @param x
     */
    public final void addMember(int index, Color x) {

        Branch left = null;
        Branch right = null;
        Branch now = null;
        int leftind = -1;
        int rightind = -1;
//System.out.println("addMember index "+index);
        if ((index < points.size()) && (index > -1)) {

            curlist.ensureCapacity(curlist.size() + 3);

            if (index < points.size())
                now = (Branch) points.get(index);

            leftind = ParentofTree.getLeftInd(index);//
//leftind =findLeftChild(now);
            rightind = ParentofTree.getRightInd(index);//
//rightind = findRightChild(now);
//System.out.println(" LEFT: "+leftind+"\tRIGHT: "+rightind);

            if (leftind == -1)
                if (((2 * (index + 1) - 1) > -1) && (2 * (index + 1) - 1 < points.size()))
                    left = (Branch) points.get(2 * (index + 1) - 1);
            if (rightind == -1)
                if (2 * (index + 1) < points.size())
                    right = (Branch) points.get(2 * (index + 1));
//System.out.println("*LEFT: "+leftind+"\tRIGHT: "+rightind);
//System.out.println("L "+leftind+"\tR "+rightind);
            if (leftind != -1)
                left = (Branch) points.get(leftind);
            if (rightind != -1)
                right = (Branch) points.get(rightind);

            if (now != null) {

                double addx = (now.x[1] + now.x[2]) / (double) 2.0;

                Salact addzero = new Salact(addx, now.y[1], x);
                if (addzero.y != minY) {
                    curlist.add(addzero);
                    //System.out.println("added "+index+"\t"+addzero);
                    //curlist_array[index] =1;
                }

                /*Salact addone = new Salact(now.x[0], now.y[0], x);
                if (addone.y != minY) {
                    curlist.add(addone);
                    //curlist_array[leftind] =1;
                }

                Salact addtwo = new Salact(now.x[3], now.y[3], x);
                if (addtwo.y != minY) {
                    curlist.add(addtwo);
                    //curlist_array[rightind] =1;
                }*/
            }
            if (left != null) {

                addMember(leftind, x);
            }

            if (right != null) {

                addMember(rightind, x);
            }
        }
    }

    /**
     * Counts the number of 'a' values in array. Uses only the [0] row of the array.
     *
     * @param data
     * @param a
     * @return
     */
    public final static int countMinusIneOccurence(int[][] data, int a) {
        int min = 0;
        for (int i = 0; i < data.length; i++)
            if (data[i][0] == a)
                min++;
        return min;
    }

}
