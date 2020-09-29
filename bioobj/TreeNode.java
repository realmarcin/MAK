package bioobj;


/**
 * Tree node object, implemented in wtreeto and the canvii
 */
public class TreeNode {
    public String name;
    public double high;
    public double wide;
    public double dist;
    int childl;
    int childr;


    public TreeNode(double d, String n) {
        this.dist = d;
        if (n != null)
            this.name = n;
        high = (double) -1;
        wide = (double) -1;
    }

    public TreeNode(double x, double y, String n, int chl, int chr) {
        this.high = x;
        this.wide = y;
        this.name = new String(n);
        this.childl = chl;
        this.childr = chr;
    }

    public TreeNode(TreeNode oldone) {
        this.high = oldone.high;
        this.wide = oldone.wide;
        this.dist = oldone.dist;
        if (oldone.name != null)
            this.name = oldone.name;
        else
            this.name = null;
        this.childl = oldone.childl;
        this.childr = oldone.childr;
    }

    public String toString() {
        return ("y: " + this.high + "  x: " + this.wide + "  dist: " + this.dist + "  Lchild: " + childl + "  Rchild:  " + childr);
    }
}
