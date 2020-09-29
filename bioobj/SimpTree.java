package bioobj;

import dtype.Branch;
import dtype.Sens;
import util.StringUtil;

import java.util.ArrayList;


public class SimpTree extends ParentofTree {

    double[][] nodedist;
    int[] childindex;


    public SimpTree() {

        super();

    }

    public SimpTree(ArrayList a) {

        super(a);
    }

    public SimpTree(ArrayList a, ArrayList n) {

        super(a, n);
    }

    /**
     * @param i
     * @param b
     */
    public void set(int i, Sens b) {

        b.index = i;
        nodes.set(i, (Sens) b);
    }

    /*
     * Converts the dtype.Sens nodes to dtype.Branch nodes - rough conversion with improper distances and possibly node connections.
     * @return
     */
    private UTree toBranches() {

        ArrayList brar = new ArrayList();

        brar = util.PreLoadList.load(size(), null, brar);

        for (int i = size(); i > -0; i--) {
            if ((Sens) nodes.get(i) != null) {

                ////System.out.println("Sens@i "+i+"\t"+(Sens)ab.get(i));

                Sens cur = (Sens) nodes.get(i);
                //System.out.println("toBranches "+i+"\t"+cur.s);

                Branch b = new Branch();

                Sens l = (Sens) getLeftChild(i);
                Sens r = (Sens) getRightChild(i);

                //System.out.println("right "+r);
                //System.out.println("left: "+l);

                if (l != null && r != null) {

                    Sens[] lc = (Sens[]) getChildren(getLeftInd(i));
                    Sens[] rc = (Sens[]) getChildren(getRightInd(i));

                    double ld = l.x;
                    double rd = r.x;
                    double dist = (l.x + r.x) / (double) 2;

                    //System.out.println("dists "+ld+"\t"+rd);

                    if (lc[0] == null && lc[1] == null) {

                        l.y = getNameInd(l.s);
                    }
                    if (rc[0] == null && rc[1] == null) {

                        r.y = getNameInd(r.s);
                    }


                    //System.out.println("terms "+l.y+"\t"+r.y);

                    b.x[0] = (double) l.y;
                    b.y[0] = (double) ld;
                    b.x[1] = (double) l.y;
                    b.y[1] = dist;
                    b.x[2] = (double) r.y;
                    b.y[2] = dist;
                    b.x[3] = (double) r.y;
                    b.y[3] = (double) rd;
                    //System.out.println("constructed Branch "+b);

                    b.index = i;
                    brar.set(i, (Branch) b);
                }
                // else
                ////System.out.println("right "+r+"\tleft: "+l);

            } else {
                brar.set(i, null);
            }
        }

        return new UTree(brar);
    }

    public void childIndex() {

        int[] children = getChildrenBelow(0);
        childindex = new int[children.length];

        //starts at 1 to account for empty first name
        for (int i = 1; i < children.length; i++) {

            childindex[i] = getChildInd(i);
        }

    }

    /*
    * Converts the dtype.Sens nodes to dtype.Branch nodes - rough conversion with improper distances and possibly node connections.
    * @return
    */
    public UTree toBranchesUni(double d) {

        //System.out.println("started nodePairDist");
        nodedist = nodePairDist();
        //System.out.println("finished nodePairDist");

        childIndex();

        UTree brar = new UTree();

        try {
            brar.nodes = util.PreLoadList.load(size(), null, brar.nodes);

        } catch (Exception e) {
            //System.out.println("Failed to initialize ArrayList "+size());
        }

        for (int i = 0; i < size(); i++) {
            if ((Sens) nodes.get(i) != null) {

                //System.out.println("Sens@i "+i+"\t"+(Sens)nodes.get(i));

                Sens cur = (Sens) nodes.get(i);
                ////System.out.println("toBranchesUni "+i+"\t"+cur.s);

                int[] children = getChildrenBelow(i);
                ////System.out.println("finished getChildrenBelow" );
                double[] paths = sumPaths(children, i);
                ////System.out.println("finished sumPaths");

                Branch b = new Branch();
                b.s = cur.s;

                Sens l = (Sens) getLeftChild(i);
                Sens r = (Sens) getRightChild(i);

                ////System.out.println("right "+r);
                ////System.out.println("left: "+l);

                if (l != null && r != null) {

                    Sens[] lc = (Sens[]) getChildren(getLeftInd(i));
                    Sens[] rc = (Sens[]) getChildren(getRightInd(i));
                    ////System.out.println("finished getChildren");

                    double ld = l.x;
                    double rd = r.x;
                    double dist = mathy.stat.findMax(paths);

                    ////System.out.println("dist "+dist);//+ld+"\t"+rd);

                    if (lc[0] == null && lc[1] == null) {

                        l.y = getNameInd(l.s);
                        ld = 0;
                    }
                    if (rc[0] == null && rc[1] == null) {

                        r.y = getNameInd(r.s);
                        rd = 0;
                    }

                    ////System.out.println("terms "+l.y+"\t"+r.y);

                    b.x[0] = (double) l.y;
                    b.y[0] = (double) ld;
                    b.x[1] = (double) l.y;
                    b.y[1] = dist;
                    b.x[2] = (double) r.y;
                    b.y[2] = dist;
                    b.x[3] = (double) r.y;
                    b.y[3] = (double) rd;
                    ////System.out.println("constructed Branch "+b);

                    b.index = i;
                    brar.nodes.set(i, (Branch) b);
                }
            } else {
                brar.nodes.set(i, null);
            }
        }

        return brar;
    }


    /**
     * @return
     */
    public double[][] nodePairDist() {

        getNonNullSize();
        double[][] ret = new double[nonullsize + 1][nonullsize + 1];

        for (int i = 0; i < nonullsize; i++) {
            if (nonnullindexback[i] != -1) {
                //if(nodes.get()!=null) {
                for (int j = i + 1; j < nonullsize; j++) {
                    if (nonnullindexback[j] != -1) {
                        //if(nodes.get(j)!=null) {
                        ret[i][j] = sumPath(nonnullindexback[i], nonnullindexback[j]);
                        ret[j][i] = ret[i][j];
                        ////System.out.println("pair dist "+i+"\t"+j+"\t"+ret[i][j]);
                    }
                }
            }
        }

        /*
        for(int i=0;i<nodes.size();i++) {
            if(nodes.get(i)!=null)
            for(int j=i+1;j<nodes.size();j++) {
                if(nodes.get(j)!=null) {
                    //System.out.println("pair dist "+i+"\t"+j+"\t"+ret[i][j]);
                    }
            }
        }
        */
        return ret;
    }

    /**
     * @param childs
     * @param p
     * @return
     */
    public double[] sumPaths(int[] childs, int p) {


        double[] ret = new double[childs.length];

        //starts at 1 to account for empty first name
        for (int i = 1; i < ret.length; i++) {
            if (childs[i] > 0) {
                ////System.out.println("parent "+p+"\tleaf "+i+"\tnode "+getChildInd(i));
                //ret[i]=sumPath(p, getChildInd(i));
                ////System.out.println(nonnullindexforw[p]+"\t"+nonnullindexforw[getChildInd(i)]+"\t\t"+nodedist.length);

                int x = nonnullindexforw[p];
                int y = nonnullindexforw[childindex[i]];//getChildInd(i)];
                ////System.out.println("finished    getChildInd "+i);
                ret[i] = nodedist[x][y];
                ////System.out.println("pathlen "+p+"\t"+getChildInd(i)+"\t"+ret[i]);
            }
        }
        /*
        for(int i=1;i<ret.length;i++) {

        //System.out.println("path sum "+p+"\tto "+getChildInd(i)+"\t"+ret[i]);//=sumPath(p, getChildInd(i));
        }
        */

        return ret;
    }

    /**
     * @param a
     * @param b
     * @return
     */
    public double sumPath(int a, int b) {

        double d = -1;
        /**two switches to test if a or b are parents of each other */
        boolean apar = isParent(a, b);
        boolean bpar = false;

        if (!apar) {
            bpar = isParent(b, a);
        }

        if (apar)
            return childPath(b, a);
        else if (bpar)
            return childPath(a, b);
        else {

            int last = lastCommonNode(a, b);
            return sumPathOld(a, b);
        }
    }

    /**
     * @param child
     * @param parent
     * @return
     */
    public double childPath(int child, int parent) {

        double d = 0;

        while (child != -1) {

            child = getParentInd(child);
            d += ((Sens) nodes.get(child)).x;

            if (child == parent)
                return d;
        }

        return -1;
    }


    /**
     * @param a
     * @param b
     * @return
     */
    public double sumPathOld(int a, int b) {

        double d = 0;

        boolean upa = true;
        boolean upb = false;


        ////System.out.println("summing "+a+"\t"+b);
        ////System.out.println("summing a,b "+d+"\t"+((Sens)nodes.get(a)).x+"\t"+((Sens)nodes.get(b)).x);
        //d=((Sens)nodes.get(a)).x+((Sens)nodes.get(b)).x;

        while (a != b) {

            ////System.out.println("\tsumming loop "+a+"\t"+b);

            if (upa && getParentInd(a) != -1) {

                ////System.out.println("\t\tsumming a go "+a+"\t"+d+"\t"+((Sens)nodes.get(a)).x);
                d += ((Sens) nodes.get(a)).x;
                a = getParentInd(a);
                upa = false;
                upb = true;
            } else if (upa && getParentInd(a) == -1) {

                ////System.out.println("\t\tsumming a dead "+a+"\t"+d+"\t"+((Sens)nodes.get(a)).x);
                d += ((Sens) nodes.get(a)).x;
                upa = false;
                upb = true;
            } else if (upb && getParentInd(b) != -1) {

                ////System.out.println("\t\tsumming b go "+b+"\t"+d+"\t"+((Sens)nodes.get(b)).x);
                d += ((Sens) nodes.get(b)).x;
                b = getParentInd(b);

                if (getParentInd(a) != -1) {
                    upa = true;
                    upb = false;
                }
                if (getParentInd(b) != -1) {
                    upa = false;
                    upb = true;
                } else
                    break;
            } else if (upb && getParentInd(b) == -1) {

                ////System.out.println("\t\tsumming b dead "+b+"\t"+d+"\t"+((Sens)nodes.get(b)).x);
                d += ((Sens) nodes.get(b)).x;
                break;
            }

            ////System.out.println("\tnext "+a+"\t"+b+"\t"+upa+"\t"+upb);
        }

        //d+=((Sens)nodes.get(a)).x;
        ////System.out.println("\tdist "+a+"\t"+d);
        return d;
    }

    /**
     * @param a
     * @return
     */
    public int getChildInd(int a) {

        String c = (String) names.get(a);

        if (c == null) {

            //System.out.println(a+"\tError - name is null.");
        } else {
            for (int i = 0; i < nodes.size(); i++) {

                Sens cur = (Sens) nodes.get(i);

                if (cur != null)
                    if (c.equals(cur.s))
                        return i;
            }
        }
        return -1;
    }

    /**
     * Prepares the names for display.
     */
    private void trimNames() {


        for (int i = 0; i < names.size(); i++) {

            String s = (String) names.get(i);
            if (s.indexOf("( (") == -1) {

                s = StringUtil.replace(s, "(", "");
                s = StringUtil.replace(s, ")", "");
                s = StringUtil.trim(s, "'");
                s.trim();

                names.set(i, (String) s);
            }
        }
    }

    /**
     * Returns the index of the String in the name ArrayList.
     *
     * @param s
     * @return
     */
    private int getNameInd(String s) {

        for (int i = 1; i < names.size(); i++) {

            ////System.out.println(":"+names.get(i)+":\t:"+s+":");
            if (((String) names.get(i)).equals(s)) {

                ////System.out.println("name "+i);
                return i;
            }
        }

        return -1;
    }

    /**
     * @param k
     * @return
     */
    public int[] getChildrenBelow(int k) {

        int[] ret = new int[names.size()];

        ret = testChild(k, ret);

        /*
        for(int i=1;i<ret.length;i++) {

        //System.out.println("getChildrenBelow "+i+"\t"+ret[i]);
        }
        */

        return ret;
    }


    /**
     * @param k
     * @param ret
     * @return
     */
    private int[] testChild(int k, int[] ret) {

        Sens l = (Sens) getLeftChild(k);
        int li = getLeftInd(k);
        Sens r = (Sens) getRightChild(k);
        int ri = getRightInd(k);

        if (l != null && (getLeftChild(li) != null || getRightChild(li) != null)) {

            ret = testChild(li, ret);
        } else if (l != null && getLeftChild(li) == null && getRightChild(li) == null) {

            ret[getNameInd(l.s)] = getNameInd(l.s);
        }

        if (r != null && (getLeftChild(ri) != null || getRightChild(ri) != null)) {

            ret = testChild(ri, ret);
        } else if (r != null && getLeftChild(ri) == null && getRightChild(ri) == null) {

            ret[getNameInd(r.s)] = getNameInd(r.s);
        }


        return ret;
    }

    /**
     * @return
     */
    public double[][] getpathLens() {

        double[][] d = new double[0][0];
        return d;
    }

    /**
     * Sums the left child path of the node.
     *
     * @param k
     * @param a
     * @return
     */
    private double sumLeftPath(int k, ArrayList a) {

        double d = 0;

        Sens cur = (Sens) a.get(k);
        Sens l = (Sens) getLeftChild(k);
        int left = getLeftInd(k);
        d += l.x;
        k = left;

        while (l != null) {

            l = (Sens) getLeftChild(left);
            left = getLeftInd(left);
            d += l.x;
        }

        return d;
    }

    /**
     * Returns the last node in common between the two nodes in question.
     *
     * @param a
     * @param b
     * @return
     */
    public int lastCommonNode(int a, int b) {

        int n = -1;

        boolean upa = true;
        boolean upb = false;

        while (a != b) {

            if (upa && getParentInd(a) != -1)
                a = getParentInd(a);
            else if (upb && getParentInd(b) != -1)
                b = getParentInd(b);

            if (upa || getParentInd(a) == -1) {
                upa = false;
                upb = true;
            } else if (upb || getParentInd(b) == -1) {
                upa = true;
                upb = false;
            }

            ////System.out.println("A "+a);
            ////System.out.println("B "+b);
        }

        if (a != -1)
            n = a;

        ////System.out.println("lastCommonNode "+n);
        return n;
    }

}