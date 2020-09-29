package bioobj;

import dtype.Branch;
import dtype.Sens;

import java.util.ArrayList;


public class UTree extends ParentofTree {

    public boolean[] added;
/*Variable encoding the graphical accuracy of the tree.*/
    public double ACCURACY = (double) .00001;
    double miny, maxy;
    double max;
    public int[][] index;

/*Variable used to scale the dendrogram data to graphical output.*/
    public double scaletree = (double) 5;


    /**
     *
     */
    public UTree() {

        super();
    }

    /**
     *
     */
    public UTree(ArrayList a) {

        super(a);
    }

    /**
     * Returns index of Branch.
     *
     * @param c
     * @return
     */
    public int getIndB(Branch c) {

        for (int i = 0; i < nodes.size(); i++) {

            Branch cur = ((Branch) nodes.get(i));

            if (cur != null && cur.index != -1) {
                if (cur.equals(c)) {

                    return i;
                }
            }

        }
        return -1;
    }

    /**
     * Returns parent Branch of Branch.
     *
     * @param c
     * @return
     */
    public Branch getParent(Branch c) {


        int ci = getIndB(c);
        ////System.out.println("got parent ind  "+ci);
        int pind = (int) (ci / (double) 2) - 1;
        ////System.out.println("getting parent "+c+"\t"+pind);
        if (pind >= 0)
            return (Branch) nodes.get(pind);
        //return (Branch)getNode(nodes,pind);
        return null;
    }

    /**
     * Retursn the right child.
     *
     * @param i
     * @return
     */
    public Branch getLeftChildB(int i) {
        if (2 * (i + 1) < nodes.size())
            return (Branch) nodes.get(2 * (i + 1));
        //return (Branch)getNode(nodes, 2*(i+1));
        else
            return null;
    }

    /**
     * Retursn the right child.
     *
     * @param i
     * @return
     */
    public Branch getRightChildB(int i) {

        if (2 * (i + 1) - 1 < nodes.size())
            return (Branch) nodes.get(2 * (i + 1) - 1);
        //return (Branch)getNode(nodes, 2*(i+1)-1);//
        else
            return null;
    }

    /**
     * Function which builds a binary tree structure form nodes.
     * Right and left children are placed at (2*(parent+1)-1) and (2*parent)+1.
     * Leaves are assigned as null children.
     */
    public void placeChildren(int pos, ArrayList src, ArrayList dst, int end) {

        int placeleft = ((2 * (pos + 1)));
        int placeright = (2 * (pos + 1) - 1);
//System.out.println("PLACING children for parent  "+pos+"\t at LEFT "+placeleft+"\t and or RIGHT "+placeright);
        //Branch hej = (Branch)getNode(dst,pos);
        Branch hej = (Branch) dst.get(pos);

        //double midy  = hej.y[1];
        double lefty = hej.y[0];
        //double leftx = hej.x[0];
        double righty = hej.y[3];
        //double rightx = hej.x[3];

        if (lefty == 0) {

            if (placeleft >= dst.size()) {

                dst = util.PreLoadList.load(placeleft + 2, null, dst);
            }

            dst.set(placeleft, null);

            Branch add = new Branch();
            add.index = placeleft;
        } else {
            for (int j = 1; j < end; j++) {

                if (added[j] == false) {

                    Branch cur = (Branch) src.get(j);
                    //Branch cur = (Branch)getNode(src, j);

                    if (cur.x == null || cur.index == -1)
                        continue;

                    double maybey = cur.y[1];

                    if (Math.abs(maybey - lefty) < ACCURACY) {

                        Branch add = new Branch();
                        add.index = j;
                        src.set(j, (Branch) add);
                        //src.add((Branch)add);
                        added[j] = true;


                        if (placeleft >= dst.size()) {

                            dst = util.PreLoadList.load(placeleft + 3, null, dst);
                        }

                        if (placeleft >= dst.size()) {

                            int i = placeleft - dst.size();

                            while (i > -1) {

                                dst.add(null);
                                i++;
                            }
                        }


                        if (dst.size() > 0 && placeleft < dst.size()) {
                            cur.index = placeleft;
                            dst.set(placeleft, cur);
                            //dst.add((Branch)cur);
                        }

////System.out.println("placing  "+pos+"  L  "+placeleft);
                        placeChildren(placeleft, src, dst, end);
                        break;
                    }
                }
            }
        }
        if (righty == 0) {

            if (placeright >= dst.size()) {

                dst = util.PreLoadList.load(placeleft + 2, null, dst);
            }
            //else

            Branch add = new Branch();
            add.index = placeright;

            dst.set(placeright, (Branch) add);
            //dst.add((Branch)add);
        } else {

            for (int j = 1; j < end; j++) {

                if (added[j] == false) {

                    Branch cur = (Branch) src.get(j);
                    //Branch cur = getNode(src, j);

                    if (cur == null)// || cur.index==-1)//x == null)
                        continue;

                    double maybey = cur.y[1];

                    if (Math.abs(maybey - righty) < ACCURACY) {

                        Branch add = new Branch();
                        add.index = j;
                        src.set(j, (Branch) add);
                        //src.add((Branch)add);
                        added[j] = true;

                        if (placeright >= dst.size()) {

                            dst = util.PreLoadList.load(placeleft + 2, null, dst);
                        }

                        cur.index = placeright;
                        dst.set(placeright, cur);
                        //dst.add((Branch)cur);
                        ////System.out.println("placing  "+pos+"   R  "+placeright);
                        placeChildren(placeright, src, dst, end);
                        break;
                    }
                }
            }
        }

    }


    /**
     *
     */
    public void findMinMax() {

        Branch test = (Branch) nodes.get(0);

        miny = test.y[1];
        maxy = test.y[1];

        for (int i = 0; i < nodes.size(); i++) {

            ////System.out.println("trimminyg "+i);

            Branch b = (Branch) nodes.get(i);

            if (b != null) {
                if (b.y[1] < miny)

                    miny = b.y[1];
                if (b.y[1] > maxy)
                    maxy = b.y[1];
            }
        }

    }

    /**
     * Trims tree to fit to canvas (scaling and shortening of terminyal nodes)
     */
    public UTree scaleTree() {


        findMinMax();

        /*
        if(miny > (double).1) {
            for (int i = 0; i <  nodes.size(); i++) {
                Branch b = (Branch)nodes.get(i);
                if(b != null)// && b.index!=-1)//if(nodes.get(i) != null)
                {
                if(b.y[0] == 0)
                    b.y[0] = miny-(double).25;
                if(b.y[3] == 0)
                    b.y[3] = miny-(double).25;
                }
            }
        miny-=(double).25;
        }
        else if(miny <.1)
            miny =0;
        */


        double diff = Math.abs(miny - maxy);

        scaletree = scaletree / diff;

        for (int i = 0; i < nodes.size(); i++) {

            Branch b = (Branch) nodes.get(i);

            if (b != null) {// && b.index!=-1) {

                for (int j = 0; j < 4; j++) {

                    ////System.out.println("rescaling "+b.y[j]+"\t"+b.y[j]*scaletree);
                    b.y[j] = b.y[j] * scaletree;
                }

                //ba.index=i;
                nodes.set(i, (Branch) b);
            }
        }
        miny = miny * scaletree;
        return this;
    }

    /**
     * Function which calls a sort of the tree from parent to child nodes.
     */
    public UTree sortTree() {

        ////System.out.println("sortTree "+p.size());
        added = new boolean[nodes.size()];
        ArrayList sortedtree = new ArrayList();
        //sortedtree.add(getNode(nodes,0));
        sortedtree.add(nodes.get(0));
        added[0] = true;

        placeChildren(0, nodes, sortedtree, nodes.size());
        ////System.out.println("sorted tree size  "+sortedtree.size());
        return this;
    }

    /**
     * @return
     */
    public UTree verifyDist() {

        for (int i = size() - 1; i >= 0; i--) { //<bra.size();i++) {

            Branch cur = (Branch) nodes.get(i);
            if (cur != null && cur.index != -1) {//cur.x !=null) {

                int pind = getIndB(cur);
                Branch p = getParent(cur);

                if (pind >= 0) {

                    Branch pl = getLeftChildB(i);
                    if (pl != null && pl.index != -1)
                        if (cur.y[1] < pl.y[1]) {

                            cur.y[1] = pl.y[1] + ACCURACY;
                            cur.y[2] = pl.y[1] + ACCURACY;

                        }

                    Branch pr = getRightChildB(i);
                    if (pr != null && pr.index != -1)
                        if (cur.y[1] < pr.y[1]) {

                            cur.y[1] = pr.y[1] + ACCURACY;
                            cur.y[2] = pr.y[1] + ACCURACY;

                        }

                    cur.index = i;
                    //nodes.set(i,(Branch)cur);
                    nodes.add((Branch) cur);
                }
            }
        }

        return this;
    }

    /**
     * Adds proper distances and node connections.
     *
     * @param bin
     * @return
     */

    public UTree detailTree(SimpTree bin) {

        for (int i = 0; i < size(); i++) {

            Branch cur = (Branch) nodes.get(i);
            //Branch cur =(Branch)getNode(nodes,i);

            if (cur != null) {// && cur.index!=-1) {//cur.x!=null) {
                ////System.out.println("Sens@i "+i+"\t"+(Branch)bra.get(i));

                Sens curs = (Sens) bin.nodes.get(i);

                Branch p = getParent(cur);

                ////System.out.println("parent "+p);
                int pind = getIndB(p);
                ////System.out.println("parent ind "+pind);

                Sens pls, prs;

                if (pind > 0) {

                    Branch pl = getLeftChildB(pind);
                    int lind = getIndB(pl);


                    Branch pr = getRightChildB(pind);
                    int rind = getIndB(pr);

                    //System.out.println("left "+pl+"\tright "+pr);


                    if (pl != null && cur.equals(pl)) {

                        pls = (Sens) bin.nodes.get(lind);
                        //System.out.println("binary left "+pls);

                        p.x[0] = (((pl.x[0]) - pl.x[3]) / (double) 2) + pl.x[3];
                        p.x[1] = (((pl.x[0]) - pl.x[3]) / (double) 2) + pl.x[3];

                        //p.y[0]=pl.y[1];
                        ////System.out.println("Could replace dist "+p.y[0]+"\tw/ "+pls.x);
                        ////System.out.println("Setting left end "+p.x[0]+"\t"+pl.x[0]+"\t"+pl.x[3]);
                    }
                    if (pr != null && cur.equals(pr)) {

                        prs = (Sens) bin.nodes.get(rind);
                        //System.out.println("binary right "+prs);

                        p.x[3] = (((pr.x[0]) - pr.x[3]) / (double) 2) + pr.x[3];
                        p.x[2] = (((pr.x[0]) - pr.x[3]) / (double) 2) + pr.x[3];

                        //p.y[3]=pr.y[1];
                        ////System.out.println("Could replace dist "+p.y[3]+"\tw/ "+prs.x);
                        ////System.out.println("Setting right end "+p.x[3]+"\t"+pr.x[0]+"\t"+pr.x[3]);
                    }

                    if (pl != null && pr != null) {

                        p.y[1] = pl.y[1] + p.y[1];
                        p.y[2] = pl.y[1] + p.y[2];
                    } else if (pl != null) {
                        p.y[1] = pl.y[1] + p.y[1];
                        p.y[2] = pl.y[1] + p.y[2];
                    } else if (pr != null) {
                        p.y[1] = pr.y[1] + p.y[1];
                        p.y[2] = pr.y[1] + p.y[2];
                    }

                    p.index = pind;
                    //nodes.set(pind,(Branch)p);
                    nodes.add((Branch) p);
                }
            }
        }

        return this;
    }

    /**
     * Function to return values for coordinate miny/max extents of the dendrogram.
     */
    public double[] treeExtents() {
//0 = minyX
        //1 = maxX
        //2 = minyY
        //3 = maxY

        double[] ret = new double[4];

        if (size() > 0) {

            Branch b = (Branch) (nodes.get(0));
            //Branch b = (Branch)getNode(nodes,0);

            ret[3] = b.y[1];
            ret[2] = b.y[0];
            ret[1] = b.x[1];
            ret[0] = b.x[0];

            int start = 0;

            for (int i = start; i < size(); i++) {

                b = (Branch) (nodes.get(i));
                //b = (Branch)getNode(nodes,i);
                if (b == null)// || b.index == -1)// == null)
                    continue;
                //minyX
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

                //minyY
                if (ret[2] > b.y[0])
                    ret[2] = b.y[0];
                if (ret[2] > b.y[3])
                    ret[2] = b.y[3];
            }
        }

        ret[3] += 1;
        return ret;
    }


    /**
     * Counts the number of leaves in this tree.
     *
     * @return
     */
    public int countLeaves() {


        int[] r = getChildrenBelow(0);

        int c = 0;
        for (int i = 0; i < r.length; i++) {

            if (r[i] > 0)
                c++;
        }

        return c;
    }


    /**
     * @param k
     * @return
     */
    public int[] getChildrenBelow(int k) {

        int[] ret = new int[names.size()];

        //System.out.println("getChildrenBelow size "+names.size());

        ret = testChild(k, ret);

        /*
        for(int i=1;i<ret.length;i++) {

        //System.out.println("getChildrenBelow "+i+"\t"+ret[i]);
        }
        */

        return ret;
    }

    /**
     * Method to report no all leaves below a node.
     *
     * @param k
     * @param ret
     * @return
     */
    public int[] testChild(int k, int[] ret) {

        ////System.out.println("testChild 1 "+k);
        Branch c = (Branch) nodes.get(k);
        //Branch c = (Branch)getNode(nodes,k);

        ////System.out.println("testChild 2 "+c);
        int li = getLeftInd(k);
        int ri = getRightInd(k);

        ////System.out.println("testChild 3 "+li+"\t"+ri);
        ////System.out.println("UTree testChild C "+c);

        if (c != null && c.index != -1) {

            ////System.out.println("testChild "+k+"\t"+c);
            if (c.y[3] == 0) {

                ret[(int) c.x[3]] = (int) c.x[3];
                ////System.out.println("right "+ret[(int)c.x[3]]+"\t"+(int)c.x[3]);
            }
            if (c.y[0] == 0) {

                ret[(int) c.x[0]] = (int) c.x[0];
                ////System.out.println("left "+ret[(int)c.x[0]]+"\t"+(int)c.x[0]);
            }
        }

        //Branch l =(Branch)getNode(nodes,li);
        //Branch r =(Branch)getNode(nodes,ri);

        Branch l = (Branch) nodes.get(li);
        Branch r = (Branch) nodes.get(ri);

        if (l != null)// && l.index!=-1)
            ret = testChild(li, ret);
        if (r != null)// && r.index!=-1)
            ret = testChild(ri, ret);

        return ret;
    }


    /**
     * Returns the index of the String in the name ArrayList.
     *
     * @param s
     * @return
     */
    public int getNameInd(String s) {

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
     * Indexes the node parent-children into an array [3] --> 0-parent,1-left child,2-right child.
     */
    public void index() {

        index = new int[nodes.size()][3];

        for (int i = 0; i < nodes.size(); i++) {

            int p = getParentInd(i);
            int tp = getTrueInd(p, nodes);
            index[i][0] = tp;

            int l = getLeftInd(i);
            int tl = getTrueInd(l, nodes);
            index[i][1] = tl;

            int r = getParentInd(i);
            int tr = getTrueInd(r, nodes);
            index[i][2] = tr;

        }

    }

}