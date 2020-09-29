package bioobj;

import dtype.Branch;
import dtype.Sens;
import util.StringUtil;
import util.TextFile;

import java.util.ArrayList;
import java.util.Vector;


/**
 * Convert the String format of tree to data structure
 */
public class EnableTree {

    public boolean changenameorder;

    //a dummy node that acts as the root
    Node root = new Node("root", 0);


    String treefile;
    public ArrayList names;
    public ArrayList origlist;

    SimpTree binary;
    UTree branches;
    ArrayList testnames;
    public int[] nameorder;

    public int[][] index;

    boolean branch_length = false;

    /**
     * @param args
     */
    public EnableTree(String[] args) {

        treefile = args[0];
        String treetext = TextFile.read(args[0]);
        //System.out.println("read tree: "+args[0]+"\n"+treetext);
        int semic = treetext.indexOf(";");
        ////System.out.println("semic "+semic+"\t"+treetext.length());
        if (semic == treetext.length() - 1) {
            treetext = treetext.substring(0, treetext.length() - 1);
            ////System.out.println("chooped tree "+treetext);
        }

        run(treetext);
    }

    /**
     * @param n
     */
    public EnableTree(ArrayList n) {

        testnames = n;
    }

    /**
     * @param n
     */
    public EnableTree(ArrayList n, boolean branches) {

        testnames = n;
        branch_length = branches;
    }

    /**
     * Runs the full load procedure.
     *
     * @param tr
     * @return
     */
    public UTree runFull(String tr) {

        treefile = tr;
        String treetext = TextFile.read(treefile);

        //System.out.println("read tree: "+treefile+"\n"+treetext);

        if (treetext.indexOf("\\n") != -1) {

            treetext = StringUtil.replace(treetext, "\\n", "");
        }

        //System.out.println("read tree: "+treefile+"\n"+treetext);
        int semic = treetext.indexOf(";");
        //System.out.println("semic "+semic+"\t"+treetext.length());
        if (semic == treetext.length() - 1) {
            treetext = treetext.substring(0, treetext.length() - 1);
            //System.out.println("chopped tree "+treetext);
        }

        run(treetext);

        trimNames();
        //System.out.println("testing name order");

        if (testnames != null)
            changenameorder = testNameOrder(testnames);
        //System.out.println("finished testing name order");

        //printTree();

        return branches;
    }

    /**
     * Trims the names for display purposes.
     */
    private void trimNames() {


        for (int i = 0; i < names.size(); i++) {

            String s = (String) names.get(i);

            if (s.indexOf("( (") == -1) {

                s = StringUtil.replace(s, "(", "");
                s = StringUtil.replace(s, ")", "");
                s = StringUtil.trim(s, "'");
                s.trim();

                //System.out.println("names "+i+"\t"+s);
                names.set(i, (String) s);
            }
        }

    }

    /**
     * Run partial load procedure.
     *
     * @param treetext
     */
    public void run(String treetext) {

        //System.out.println("run0 "+treetext);
        treetext = testandAddRoot(treetext);
        //System.out.println("run1 "+treetext);

        root = parse(treetext);

        setNames();

        double[][] pathlens = root.retPathVector();


        if (!branch_length) {

            root.clearbranchLength();
            binary = new SimpTree(root.buildUniDistTree(pathlens, (double) 5), names);
        } else {
            ArrayList get = new ArrayList();
            get = root.getList(get);
            binary = new SimpTree(get, names);
        }


        index = root.index;

        /*
        for (int i = 0; i < binary.size(); i++) {
            if(binary.nodes.get(i)!=null)
                System.out.println("binary "+i+"\t"+(Sens)binary.nodes.get(i));
        }
        */
        //System.out.println("finished binary...");

        branches = binary.toBranchesUni((double) 5);
        /*
        for (int i = 0; i < branches.size(); i++) {
            if(branches.nodes.get(i)!=null)
                System.out.println("toBranchesUni "+i+"\t"+(Branch)branches.nodes.get(i));
        }
        */
        //System.out.println("finished toBranchesUni...");

        //branches.index();

        branches = detailTreeUniNew(branches, binary);
        /*
        for (int i = 0; i < branches.size(); i++) {
            if(branches.nodes.get(i)!=null)
            System.out.println("details "+i+"\t"+(Branch)branches.nodes.get(i));
        }
        */
        //System.out.println("\n");

        branches.names = names;
        int finalleaves = branches.countLeaves();
        //System.out.println("Loaded tree with "+finalleaves+" out of "+(names.size()-1)+" leaves.");

        /*if (finalleaves != names.size() - 1) {

            Frame f = new Frame();
            ErrorDialog er = new ErrorDialog(f, "Tree is not binary - has unresolved branches.Loaded tree with " + finalleaves + " out of " + (names.size() - 1) + " leaves.");
            er.show();
        }*/

        /*
         branches.verifyDist();
         for(int i=0;i<branches.size();i++) {
                if(branches.nodes.get(i)!=null)
                    //System.out.println("verify "+i+"\t"+(Branch)branches.nodes.get(i));
            }
         //System.out.println("\n");

            branches.sortTree();
            for(int i=0;i<branches.size();i++) {
                if(branches.nodes.get(i)!=null)
                    //System.out.println("sorted "+i+"\t"+(Branch)branches.nodes.get(i));
            }
            */

        ////System.out.println("started trimming");
        branches = branches.scaleTree();
        ////System.out.println("Finished trimming");
    }

    /**
     * Sets the leaf labels.
     */
    private void setNames() {

        names = new ArrayList();
        names.add((String) "");

        Vector v = root.getLeaves();
        for (int k = 0; k < v.size(); k++) {

            Node c = (Node) v.elementAt(k);
            //System.out.println("adding name "+k+"\t"+c.name);
            names.add((String) "(" + c.name + ")");
        }
    }

    /**
     * Tests for presence of a root, adds one if necessary.
     *
     * @param s
     * @return
     */
    private final static String testandAddRoot(String s) {

        int testrooted = s.lastIndexOf(",");
        int testrooted2 = s.lastIndexOf(")");
        int bf2 = StringUtil.lastIndexBefore(s, ")", testrooted2);
        int comma = s.indexOf(",", bf2);
        /*

        int bf = StringUtil.lastIndexBefore(s, ")", testrooted);
        int end = s.indexOf(":", testrooted+1);
        String ch = s.substring(testrooted+1, end);

        //System.out.println("Assessing root "+ch+"\n"+s);

        double test = Double.NaN;
        try {
            test = Double.parseDouble(ch);
        } catch (NumberFormatException e) {
            //System.out.println();
        }
        */

        //if (testrooted!=-1) {
        /*if (comma != -1) {

            //System.out.println("Unrooted");
            Frame mya = new Frame();
            dialog.ErrorDialog er = new dialog.ErrorDialog(mya, "Using outgroup " + s.substring(testrooted + 1, s.indexOf(":", testrooted + 1)) + " to root the tree.");
            er.show();

            s = "(" + s.substring(0, testrooted - 1) + "):0" + s.substring(testrooted);
            //System.out.println("Added root "+s);
        }*/
        //else
        //  System.out.println("tre is rooted\n"+s);

        return s;
    }

    /**
     * Adds proper distances and node connections.
     *
     * @param bra
     * @param bin
     * @return
     */

    private UTree detailTreeUniNew(UTree bra, SimpTree bin) {

        for (int i = bra.size() - 1; i >= 0; i--) {

            Branch cur = (Branch) bra.nodes.get(i);

            if (cur != null) {

                //Branch cur = (Branch)ParentofTree.getNode(bra.nodes,i);
                Sens curs = (Sens) bin.nodes.get(i);
                //System.out.println("detailTreeUniNew cur "+i+"\t"+cur);

                //int pind = bra.index[i][0];

                int pind = UTree.getParentInd(cur.index);

                ////System.out.println("parent "+pind);
                if (pind >= 0 && bra.nodes.get(pind) != null) {

                    Branch p = (Branch) bra.nodes.get(pind);
                    //Branch p = (Branch)ParentofTree.getNode(bra.nodes,pind);

                    ////System.out.println("parent "+pind+"\t"+p);

                    Sens pls, prs;

                    int lind = ParentofTree.getLeftInd(pind);
                    //Branch pl = (Branch)ParentofTree.getNode(bra.nodes,lind);

                    //int lind=bra.index[0][1];
                    Branch pl = (Branch) bra.nodes.get(lind);

                    int rind = ParentofTree.getRightInd(pind);
                    //Branch pr = (Branch) (Branch)bra.nodes.get(rind);

                    //int rind=bra.index[0][2];
                    Branch pr = (Branch) bra.nodes.get(rind);

                    ////System.out.println("\tleft "+lind+"\t"+pl);
                    ////System.out.println("\tright "+rind+"\t"+pr);

                    if (pl != null)
                        if (cur.equals(pl)) {

                            pls = (Sens) bin.nodes.get(lind);
                            ////System.out.println("\t\tbinary left "+pls);

                            p.x[0] = (((pl.x[0]) - pl.x[3]) / (double) 2) + pl.x[3];
                            p.x[1] = (((pl.x[0]) - pl.x[3]) / (double) 2) + pl.x[3];
                            p.y[0] = pl.y[1];

                            ////System.out.println("\t\tmod parent "+p);
                        }
                    if (pr != null)
                        if (cur.equals(pr)) {

                            prs = (Sens) bin.nodes.get(rind);
                            //System.out.println("\t\tbinary right "+prs+"\tcur "+cur);

                            p.x[3] = (((pr.x[0]) - pr.x[3]) / (double) 2) + pr.x[3];
                            p.x[2] = (((pr.x[0]) - pr.x[3]) / (double) 2) + pr.x[3];
                            p.y[3] = pr.y[1];
                            ////System.out.println("\t\tmod parent "+p);
                        }

                    if (pr == null && pl == null) {

                        p.y[0] = 0;//=pl.y[1]+p.y[1];
                        p.y[3] = 0;//pl.y[1]+p.y[2];
                        ////System.out.println("\t\tLEAF - setting to 0/");
                    }

                    p.index = pind;

                    //ParentofTree.setNode(bra.nodes,p);
                    bra.nodes.set(pind, (Branch) p);
                }
            }
        }

        return bra;
    }

    /**
     * Returns parent of Sens.
     *
     * @param c
     * @param bin
     * @return
     */
    private Sens getParent(Sens c, ArrayList bin) {

        int ci = getInd(c, bin);
        int pind = (int) (ci / (double) 2) - 1;
        Sens p = (Sens) bin.get(pind);

        return p;
    }

    /**
     * Returns the index of the given Sens node.
     *
     * @param c
     * @param bin
     * @return
     */
    private int getInd(Sens c, ArrayList bin) {

        for (int i = 0; i < binary.size(); i++) {
            if (binary.nodes.get(i).equals(c)) {

                return i;
            }

        }
        return -1;
    }

    /**
     * Retursn the right child.
     *
     * @param i
     * @return
     */
    private final static int getLeftInd(int i) {
        return (2 * (i + 1));
    }

    /**
     * Retursn the right child.
     *
     * @param i
     * @return
     */
    private final static int getRightInd(int i) {

        return (2 * (i + 1) - 1);
    }

    /**
     * Parses the tree.
     */

    public Node parse(String tree) {

        int counter = 0;
        double d = 0.0; //generic distance, should be modify later
        //only root should have this val, if this show up
        //in internal node, something might gone wrong
        String nodeName = "internalNode_"; //generic name for internal node
        Node n = null;
        if (tree.charAt(0) == '(') {

            //System.out.println("parsing "+tree.substring(1, tree.length() - 1));
            Vector children = parseChildren(tree.substring(1, tree.length() - 1));
            n = new Node(nodeName + counter++, d, children);

            for (int i = 0; i < children.size(); i++) {

                ((Node) children.get(i)).parent = n;
            }
            //System.out.println("parse open paren node "+n);
            return n;
        } else {
            n = new Node(tree, d);
            //System.out.println("parse node "+n);
            return n;
        }
    }

    /**
     * parse the children of an internal node
     *
     * @param tree the unparenthesised tree
     * @return
     */
    public Vector parseChildren(String tree) {
        Vector nodes = new Vector();
        int[] ends = null;
        double dist = 0;
        String currComp = null; //the current component we will be parsing
        String remain = tree;   //the remaing part to be parsed
        Node curr = null;
        while (!remain.equals("")) {
            ends = nextComponent(remain);
            System.out.println("remain " + remain + "\t" + ends[0]);
                        
////System.out.println("parseChildren "+ends[0]+"\t"+ends[1]+"\t"+remain);
//if(ends[0]+1 != -1) {
            currComp = remain.substring(0, ends[0] + 1);
            curr = parse(currComp);
            System.out.println("parseChildren parsing double "+remain.substring(ends[0]+2, ends[1]+1));
            dist = Double.parseDouble(remain.substring(ends[0] + 2, ends[1] + 1));
            curr.dist = dist;

////System.out.println("Adding node:: name "+curr.name+"\tparent "+curr.parent+"\tdist "+curr.dist);
            nodes.add((Node) curr);
            //need to see if there is no more component left
            int t = (ends[1] + 2 > remain.length() ? remain.length() : ends[1] + 2);
            remain = remain.substring(t, remain.length());
//}
/*
            else {
                //System.out.println("problem w/ tree "+remain);//"The tree requires Branch lengths."+"\n"+remain);
                System.exit(0);
            }
            */
        }
        return nodes;
    }

    /**
     * Return the next node from the String tree.
     *
     * @param trees
     * @return
     */
    public final static int[] nextComponent(String trees) {

        ////System.out.println("nextComponent "+trees);

        int closeInd = 0;
        int endInd = 0;
        if (trees.charAt(0) == '(') {
            int open = 0;
            //if beginning is a open paren, find the corresponding close
            //paren
            for (int i = 1; i < trees.length(); i++) {
                if (trees.charAt(i) == '(') {
                    open++;
                } else if (trees.charAt(i) == ')' && open == 0) {
                    closeInd = i;
                } else if (trees.charAt(i) == ')') {
                    open--;
                }
            }
        }
        //find the ending of this component, it must be end
        //with a ',' or the end of the String
        ////System.out.println("nextComponent close "+closeInd);
        closeInd = trees.indexOf(":", closeInd) - 1;
        endInd = trees.indexOf(",", closeInd);

        endInd = (endInd < 0 ? trees.length() - 1 : endInd - 1);
        closeInd = StringUtil.lastIndexBefore(trees, ":", endInd) - 1;

        ////System.out.println("nextComponent :"+closeInd+"\t"+endInd);

        return new int[]{closeInd, endInd};
    }

    /**
     * Function to return values for coordinate min/max extents of the dendrogram.
     *
     * @return
     */
    public double[] treeExtents() {
        //0 = minX
        //1 = maxX
        //2 = minY
        //3 = maxY

        double[] ret = new double[4];

        if (branches != null && branches.size() > 0) {
            Branch b = (Branch) (branches.nodes.get(0));
            ret[3] = b.y[1];
            ret[2] = b.y[0];
            ret[1] = b.x[1];
            ret[0] = b.x[0];

            int start = 0;

            for (int i = start; i < branches.size(); i++) {
                b = (Branch) (branches.nodes.get(i));
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

    /**
     * Prints the tree to stdout.
     */
    private void printTree() {

        Vector allnodes = root.getChildren();

        //System.out.println("started printing "+allnodes.size());
        try {
            for (int i = 0; i < allnodes.size(); i++) {

                Node c = (Node) allnodes.get(i);
                System.out.println("printTree " + i + "\t" + c.toString());
            }

            //System.out.println("finished printing");
        } catch (StackOverflowError e) {

            //System.out.println(e.getMessage());
            //System.out.println(e.getCause());

            StackTraceElement[] get = e.getStackTrace();

            //for(int i=0;i<get.length;i++)
            //System.out.println("printTree "+i+"\t"+get[i].toString());
        }

    }

    /**
     * compares order of a name vector to the name vector in the Tree object
     *
     * @param n
     * @return
     */
    private boolean testNameOrder(ArrayList n) {
        ////System.out.println("testing name order ...");
        boolean sameorder = setNameOrder(n);

        return sameorder;
    }

    /**
     * determines and stores the relative order of  and 'n' name vectors
     *
     * @param n
     * @return
     */
    private boolean setNameOrder(ArrayList n) {

        boolean ret = false;
        ////System.out.println("setting name order ...");
        nameorder = new int[n.size()];
        for (int i = 0; i < nameorder.length; i++) {
            nameorder[i] = -1;
        }
        int count = 0;

        //for(int i=0;i<names.size();i++)
        ////System.out.println("Tree "+i+"\t"+(string)names.get(i));


        for (int i = 0; i < names.size(); i++) {

            String a = (String) names.get(i);
            for (int j = 0; j < n.size(); j++) {

                String b = (String) n.get(j);

                if (a.equals(b)) {
                    nameorder[i] = j;
                    ////System.out.println("name ordering\t"+i+"  "+j);'
                    count++;
                }

            }
        }
        if (count == names.size())
            ret = true;

        return ret;
    }

    /**
     * Returns a tree w/ unfiform distances. Not implemented.
     *
     * @return
     */
    public ArrayList retUniTree() {

        ArrayList ret = new ArrayList();


        return ret;

    }

    /**
     * Returns a tree w/ true distances. Not implemented.
     *
     * @return
     */
    public ArrayList retDistTree() {

        ArrayList ret = new ArrayList();


        return ret;

    }

    /**
     * @param args
     */
    public static void main(String[] args) {

        if (args.length == 1) {
            //String tree = "(Q96UY4_480:54,(Q9TC93_384:21,(Q85FQ8_377:16,ATPA_NEPOL:18):15):28,Q8YAM8_371:41)";
            //String tree = "(0_Q961X7_4:28,(1_DMD_CHIC:28,2_SNE1_HUM:24):19,(3_O13728_1:32,4_Q9W204_6:28):25)";
            EnableTree parsedTr = new EnableTree(args);
        } else {

            //System.out.println("Syntax: java bioobj.EnableTree <tree file>");
        }
    }

}

