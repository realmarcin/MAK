package bioobj;

import dtype.Branch;
import dtype.Sens;

import java.util.ArrayList;

public class ParentofTree {

    public ArrayList names;
    public ArrayList nodes;
    int nonullsize;
    int[] nonnullindexforw, nonnullindexback;


    public ParentofTree() {

        names = new ArrayList();
        nodes = new ArrayList();

    }

    public ParentofTree(ArrayList a) {

        nodes = util.ArrayListCopy.copy(a);
    }

    public ParentofTree(ArrayList a, ArrayList n) {

        names = util.ArrayListCopy.copy(n);
        nodes = util.ArrayListCopy.copy(a);
    }


    public int size() {
        return nodes.size();
    }

    /**
     * Retursn the right child.
     *
     * @param i
     * @return
     */
    public Object getLeftChild(int i) {
        if (2 * (i + 1) < nodes.size())
            return nodes.get(2 * (i + 1));
        else
            return null;
    }

    /**
     * Retursn the right child.
     *
     * @param i
     * @return
     */
    public Object getRightChild(int i) {

        if (2 * (i + 1) < nodes.size())
            return nodes.get(2 * (i + 1) - 1);
        else
            return null;
    }

    /**
     * Returns both children.
     *
     * @param i
     * @return
     */
    public Object[] getChildren(int i) {

        Object[] ret = new Sens[2];
        ret[0] = getLeftChild(i);
        ret[1] = getRightChild(i);
        return ret;
    }

    /**
     * Returns the left child index.
     *
     * @param i
     * @return
     */
    public final static int getLeftInd(int i) {
        return (2 * (i + 1));
    }

    /**
     * Returns the right child index.
     *
     * @param i
     * @return
     */
    public final static int getRightInd(int i) {

        return (2 * (i + 1) - 1);
    }

    /**
     * Returns the parent index of the node at given index.
     * Right child is even (at 2(n+1)), left is odd (at 2(n+1)-1).
     *
     * @param k
     * @return
     */
    public final static int getParentInd(int k) {

        int p = -1;
        //System.out.println("\tcomputing parent for "+k);

        if (k > 0) {

            boolean left = false;

            if (k % 2 == 1)
                left = true;

            if (!left) {

                p = (int) ((double) k / (double) 2) - 1;
                //System.out.println("\t\tpar from right "+p);
                //System.out.println("\t\ttest odd "+(k%2));
            } else if (left) {

                p = (int) (((double) k + 1) / (double) 2) - 1;
                //System.out.println("\t\tpar from left "+p);
                //System.out.println("\t\ttest odd "+(k%2));
            }

            if (k == 1 || k == 2)
                p = 0;

        }
        //System.out.println("par "+p);
        return p;
    }

    /**
     * Searches for a nodex index in the Object fields of Branch.
     *
     * @param index
     * @return
     */
    public final static Branch getNode(ArrayList a, int index) {

        for (int i = 0; i < a.size(); i++) {

            //Branch cur = (Branch)a.get(i);
            Branch cur = (Branch) a.get(i);//getNode(a,i);
            if (cur != null && cur.index == index)
                return cur;
        }
        return null;
    }

    /**
     * Searches for a nodex index in the Object fields of Branch.
     *
     * @param a
     * @param cur
     * @return
     */
    public final static Branch setNode(ArrayList a, Branch cur) {

        for (int i = 0; i < a.size(); i++) {

            //Branch cur = (Branch)a.get(i);
            Branch c = (Branch) a.get(i);//getNode(a,i);
            if (c != null && c.index == cur.index)
                a.set(i, (Branch) cur);
        }
        return null;
    }


    /**
     * Returns the ArrayList index holding the node with tree index p.
     *
     * @param p
     * @param a
     * @return
     */
    public final static int getTrueInd(int p, ArrayList a) {

        for (int i = 0; i < a.size(); i++) {

            Branch c = (Branch) a.get(i);
            if (c.index == p)
                return i;
        }
        return -1;
    }

    /**
     * Is 'a' a parent of 'b'?
     *
     * @param a
     * @param b
     * @return
     */
    public final static boolean isParent(int a, int b) {

        while (b != -1) {

            b = getParentInd(b);
            if (b == a)
                return true;
        }

        return false;
    }


    /**
     * Returns the number of non-null nodes.
     *
     * @return
     */
    public int getNonNullSize() {

        nonullsize = 0;
        nonnullindexforw = new int[nodes.size()];

        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i) != null) {
                nonullsize++;
                nonnullindexforw[i] = nonullsize;
            } else
                nonnullindexforw[i] = -1;
        }

        nonnullindexback = new int[nonullsize + 1];
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i) != null) {

                nonnullindexback[nonnullindexforw[i]] = i;
            }
        }


        return nonullsize;
    }

}