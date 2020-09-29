package bioobj;

import dtype.Branch;

/**
 * Class for BinaryTree object
 * - due to original intepretation of a tree in wrtreeto
 * the coordinate system is reversed ie x =y, y =x ...
 */
public class BinaryTree extends Tree {


    public BinaryTree(double[][] data) {

        if (data.length == data[0].length) {
            elem = data.length;
            dists = data;
            deScale();
            makeTree();
        } else
            System.out.println("Not for use with nonsymmetric data sets (ie rect. matrices).");
    }


/* scales data for display in wtreeto
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

    /**
     * adds branches for each node
     * all distances must be scaled to 450/100
     */
    private void makeTree() {
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

        float curdist = (float) dists[miniseq][minjseq];
        Branch add = new Branch();
        add.y[0] = miniseq * 2 * yspacing;
        add.x[0] = 0;
        add.y[4] = minjseq * 2 * yspacing;
        add.x[4] = 0;


        for (int i = 0; i < dists.length; i++) {


        }

    }

}
