package bioobj;

import dtype.Sens;

import java.util.ArrayList;
import java.util.Vector;


public class Node extends Object {
    public String name;
    public double dist;
    public Node parent;
    public Vector children;
    public Vector ids;
    public boolean visited;
    /*the path distance between this node and some other node */
    public double pathDist;
    /*the prev node this path come from*/
    public Node prev;
    int[][] index;


    public Node(Node n) {

        //System.out.println("printing orig node "+n);

        if (n != null) {

            if (n.name != null)
                name = n.name;

            dist = n.dist;

            if (n.parent != null)
                parent = new Node(n.parent);

            children = util.VectorCopy.copy(n.getChildren());
            //ids=util.VectorCopy.copy(n.ids);
            visited = n.visited;
            pathDist = n.pathDist;
            prev = new Node(n.prev);
        }
    }


    public Node(String obj, double d) {

        name = obj;
        dist = d;
        parent = null;
        children = null;
    }

    public Node(String obj, double d, Node pr) {
        name = obj;
        dist = d;
        if (pr == null)
            parent = null;
        else
            parent = pr;
        children = null;
    }

    public Node(String nam, double d, Vector chldr) {
        name = nam;
        dist = d;
        children = chldr;
        parent = null;
    }

    public Node(String nam, double d, Vector chldr, Node par) {
        name = nam;
        dist = d;
        children = chldr;
        parent = par;
    }

    public String getName() {
        return name;
    }

    public void changeName(String n) {
        name = n;
    }

    public Node getParent() {
        return parent;
    }

    public void changeParent(Node n) {
        parent = n;
    }

    public Vector getChildren() {
        return children;
    }

    public void addChildren(Vector chdn) {
        if (children == null)
            children = chdn;
        else
            children.addAll(chdn);
    }

    public void addChildren(Node chdn) {
        if (children == null)
            children = new Vector();
        children.add(chdn);
    }


    public void addIDs(int id) {
        if (ids == null)
            ids = new Vector();
        ids.add(new Integer(id));
    }

    public Vector getIDs() {
        return ids;
    }

    public void removeChild(Node chd) {
        if (children != null) {
            children.remove(chd);
            if (children.size() == 0) children = null;
        }
    }

    public double getDist() {
        return dist;
    }

    public boolean hasSiblings() {
        if (parent != null) {
            Vector c = parent.getChildren();
            if (c != null && c.size() > 1)
                return true;
            else
                return false;
        }
        return true;
    }

    /**
     * Returns the sibling node, if any.
     *
     * @param c
     * @return
     */
    public final static Node getSibling(Node c) {
        if (c.parent != null) {
            Vector getch = c.parent.getChildren();
            if (getch != null && getch.size() > 1) {

                if (((Node) getch.elementAt(0)).equals(c))
                    return (Node) getch.elementAt(1);
                else if (((Node) getch.elementAt(1)).equals(c))
                    return (Node) getch.elementAt(0);

            }
        }
        return null;
    }

    public boolean hasMultipleChildren() {
        Vector c = getChildren();
        if (c != null && c.size() > 1)
            return true;
        else
            return false;
    }


    public boolean hasSingleChild() {
        Vector c = getChildren();
        return (c != null && c.size() == 1);
    }

    /**
     * Reset the distance variable, should be called from root.
     */
    public void clearDist() {

        pathDist = 0;
        if (children != null) {
            for (int i = 0; i < children.size(); i++) {
                ((Node) children.get(i)).clearDist();
            }
        }
    }

    /**
     * Reset Branch lengths to 0, should be called from root.
     */
    public void clearbranchLength() {

        ////System.out.println("Clearing Branch lengths "+this);

        dist = 0;
        if (children != null) {
            for (int i = 0; i < children.size(); i++) {
                ((Node) children.get(i)).clearbranchLength();
            }
        }
    }

    /**
     * Sets all distances to uniform val. In development and not fully functional.
     */
    public void setUniDist(double d) {

        Vector l = getLeaves();
        //System.out.println("leaves "+l.size());
        for (int i = 0; i < l.size(); i++) {

            Node cur = ((Node) l.get(i));
            cur.dist = d;
//System.out.println("child "+cur+"\t"+cur.dist);
            visited = true;

            Node par = cur.parent;

            while (par != null) {

                if (par.visited == false) {

                    par.dist = ((Node) l.get(i)).dist + d;
//System.out.println("parent "+par+"\t"+par.dist);
                    par.visited = true;
                }

                par = par.parent;
            }
        }
    }

    /**
     * Resets markers. Should be called from root.
     */
    public void clearMarks() {
        visited = false;
        prev = null;
        if (children != null) {
            for (int i = 0; i < children.size(); i++) {
                ((Node) children.get(i)).clearMarks();
            }
        }

    }

    /**
     * Method for exploring a node.
     * Currently this is for finding a path dist btw two nodes.
     * The function does not have to be called from a root node.
     */
    public void visit() {
        visited = true;
        if (prev != null) {
            //two cases: if prev is parent add own dist to path
            //ohterwise  add prev's dist to path
            if (prev == parent)
                pathDist = prev.pathDist + dist;
            else
                pathDist = prev.pathDist + prev.dist;
            ////System.out.println("entering this condition");
        } else {
            pathDist = 0;
        }
        //we need to visit parent also since, we do not neccessarily start from root
        if (parent != null && !parent.visited) {
            parent.prev = this;
            parent.visit();
        }
        if (children != null) {
            for (int i = 0; i < children.size(); i++) {
                Node child = (Node) children.get(i);
                if (!child.visited) {
                    child.prev = this;
                    child.visit();
                }
            }
        }
    }

    /**
     * Return all the leaves in the tree.
     */
    public Vector getLeaves() {

        Vector v = new Vector();
        if (children == null) {

            //System.out.println("children null "+this);
            v.add(this);
            return v;
        }
        for (int i = 0; i < children.size(); i++) {

            Vector l = ((Node) children.get(i)).getLeaves();
            //System.out.println("children "+l);
            v.addAll(l);
        }
        return v;

    }

    /**
     * Returns the index of the node in the leaf vector.
     *
     * @param n
     * @return
     */
    public int getLeafInd(Node n) {

        Vector l = getLeaves();

        for (int i = 0; i < l.size(); i++) {

            if (((Node) l.elementAt(i)).name == n.name)
                return i;
        }

        return -1;
    }

    /**
     * Returns the longest path of the tree.
     * Assumes non-negative Branch distances.
     * Note: this is a rather brute-force way to find longest path
     * one fast way will be use the all-pair shortest paths algorithm
     * since all edges are > 0.
     */

    public static double longestPath(Node tree) {
        Vector leaves = tree.getLeaves();
        double[] distns = new double[leaves.size()];
        for (int i = 0; i < leaves.size(); i++) {
            Node leaf = (Node) leaves.get(i);
            tree.clearDist();
            tree.clearMarks();
            leaf.visit();
            for (int j = 0; j < leaves.size(); j++) {
                Node l = (Node) leaves.get(j);
                distns[j] = (l.pathDist > distns[j] ? l.pathDist : distns[j]);
            }
        }
        double maxLen = 0;
        for (int i = 0; i < distns.length; i++)
            maxLen = (maxLen > distns[i] ? maxLen : distns[i]);
        return maxLen;
    }

    /**
     * Returns a String representation of this tree.
     *
     * @return
     */
    public String toString() {
        if (children == null)
            return "(" + name + ")";
        String chs = "";
        for (int i = 0; i < children.size(); i++) {
            chs += ((Node) children.get(i)) + "  ";
        }
        return "( " + chs + " )";
    }

    /**
     * Counts leaves.
     *
     * @return
     */
    public int countLeaves() {


        String tree = this.toString();
        ////System.out.println("Counting leaves "+tree);

        int count = 0;


        int close = tree.indexOf(")");
        int open = util.StringUtil.lastIndexBefore(tree, "(", close);
        int lastopen = 0;
        ////System.out.println(lastopen+"\t"+open+"\t"+close);

        while (open > lastopen && close != -1 && open != -1) {

            //System.out.println("countLeaves id'd "+tree.substring(open+1,close));
            count++;
            close = tree.indexOf(")", close + 1);
            lastopen = open;
            open = util.StringUtil.lastIndexBefore(tree, "(", close);
            ////System.out.println(lastopen+"\t"+open+"\t"+close);
        }

        return count;
    }

    /**
     * Prints a String list representation of this nodes children.
     */
    public ArrayList getList(ArrayList r) {

        //System.out.println("Creating new list and recursing ...");
        Sens cn = new Sens((double) dist, 0, toString());
        r.add((Sens) cn);
        //System.out.println("setting root, num nodes \t"+r.size()+"\t"+this);

        getListRecur(1, r, 1);
        return r;
    }


    /**
     * Recursive call to go down all branches and produce a String list representation of the nodes' children.
     */
    public void getListRecur(int curind, ArrayList r, int sum) {

        int origind = curind;

        if (children != null) {


            for (int i = 0; i < children.size(); i++) {

                Node cur = (Node) children.get(i);
                ////System.out.println("printing children: "+i+"\tnode "+cur);
            }


            for (int i = 0; i < children.size(); i++) {

                ////System.out.println("curind "+curind);

                Node cur = (Node) children.get(i);

                ////System.out.println("current round, node size "+r.size()+"\tind "+i+"\tnode "+cur);

                ////System.out.println("processing child of root "+curind+"\tdist "+dist+"\tnode size "+r.size()+"\t"+cur);

                Sens cn = new Sens((double) cur.dist, (double) sum, cur.toString());

                if (r.size() - 1 < curind) {
                    ////System.out.println("preloading list "+(curind+2));
                    r = util.PreLoadList.load(curind + 1, null, r);
                }

                if (i == 0) {
                    r.set(curind, (Sens) cn);
                    sum++;
                    ////System.out.println("\tset node LEFT "+curind+"\t"+cn);
                    if (cur.getChildren() != null) {

                        int setind = 2 * (curind + 1) - 1;
                        ////System.out.println("starting recur LEFT "+setind);
                        cur.getListRecur(setind, r, sum);
                    }
                    curind++;
                    ////System.out.println("FINISHED LEFT");
                } else if (i == 1) {

                    r.set(curind, (Sens) cn);
                    sum++;
                    ////System.out.println("\tset node RIGHT "+curind+"\t"+cn);

                    if (cur.getChildren() != null) {

                        int setind = 2 * (curind + 1) - 1;
                        ////System.out.println("starting recur RIGHT "+setind);
                        cur.getListRecur(setind, r, sum);
                    }
                    ////System.out.println("FINISHED RIGHT");
                }
                ////System.out.println("END LOOP curind "+curind);
            }
        }
    }


    /**
     * Returns a vector of path lengths in the child subtree - (one entry per leaf in the tree).
     *
     * @return
     */
    public double[][] retPathVector() {


        Vector l = getLeaves();

        double[][] lens = new double[l.size()][2];

        for (int i = 0; i < l.size(); i++) {

            Node cur = (Node) l.get(i);

            while (cur.parent != null) {

                lens[i][0]++;
                ////System.out.println(cur.name+"\t"+lens[i][1]+"\t"+cur.dist);
                lens[i][1] += cur.dist;
                cur = cur.parent;
            }
        }

        /*
        for(int i =0;i<l.size();i++) {
            //System.out.println("lens "+i+"\tbranches "+lens[i][0]+"\ttotal length "+lens[i][1]);
        }
        */

        return lens;
    }


    /**
     * Builds a tree with uniform distances.
     *
     * @param distdata
     * @return
     */
    public ArrayList buildUniDistTree(double[][] distdata, double d) {

        clearMarks();

        //index = new int[][];

        ArrayList storepc = new ArrayList();

        //double[] get=Node.findLongestPath(distdata);

        Vector l = getLeaves();
        boolean[] done = new boolean[l.size()];

        for (int i = 0; i < l.size(); i++) {

            if (!done[i]) {

                Node c = (Node) l.elementAt(i);

                ////System.out.println("do uni "+c);
                c.dist = d;//0;
                ////System.out.println("buildUniDistTree "+c.name+"\t"+c.dist);
                done[i] = true;

                Node csib = Node.getSibling(c);
                if (csib.getChildren() == null) {
                    csib.dist = d;
                    ////System.out.println("buildUniDistTree getSibling 2 "+csib.name+"\t"+csib.dist);
                    done[getLeafInd(csib)] = true;
                }


                while (c.parent != null) {
                    if (!c.parent.visited) {
                        ////System.out.println("b/f "+c.parent.dist+"\t"+c.parent);
                        c.parent.dist = d;//c.dist+newlen;
                        ////System.out.println("buildUniDistTree parent 2 "+c.parent.name+"\t"+c.parent.dist);
                        c.parent.visited = true;
                        ////System.out.println("Setting uni "+c.parent.dist+"\t"+c.parent);
                    }
                    c = c.parent;
                }
            }
        }

        clearMarks();
        return getList(new ArrayList());
    }

    /**
     * Returns the longest leaf-root path in the tree. [0] is the total distance, [1] is
     * the index of the maximal leaf..
     *
     * @param distdata
     * @return
     */
    public final static double[] findLongestPath(double[][] distdata) {


        double[] ret = new double[2];
        ret[0] = 0;
        ret[1] = -1;

        for (int i = 0; i < distdata.length; i++) {

            if (distdata[i][0] > ret[0]) {
                ret[0] = distdata[i][0];
                ret[1] = i;
            }
        }
        return ret;
    }


    /**
     * Returns total distance and number of branches between a child and parent node.
     *
     * @param child
     * @param parent
     * @return
     */
    public final static double[] getPath(Node child, Node parent) {

        double[] ret = new double[2];
        ret[0] = 0;
        ret[1] = 0;

        while (child != parent) {

            ret[0] += child.dist;
            ret[1]++;
            ////System.out.println("get path "+ret[0]+"\t"+ret[1]);
            child = child.parent;
        }
        ret[0] += child.dist;
        ret[1]++;
        ////System.out.println("get path "+ret[0]+"\t"+ret[1]);

        return ret;
    }

    /**
     * Returns the last node in common between the two nodes in question.
     *
     * @param a
     * @param b
     * @return
     */
    public final static Node lastCommonNode(Node a, Node b) {

        Node n = null;

        boolean upa = true;
        boolean upb = false;

        while (a != b) {

            if (upa && a.parent != null)
                a = a.parent;
            else if (upb && b.parent != null)
                b = b.parent;

            if (upa || a.parent == null) {
                upa = false;
                upb = true;
            } else if (upb || b.parent == null) {
                upa = true;
                upb = false;
            }

            ////System.out.println("A "+a);
            ////System.out.println("B "+b);
        }

        if (a != null)
            n = a;

        ////System.out.println("lastCommonNode "+n);
        return n;
    }

}



