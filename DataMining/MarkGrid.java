package DataMining;

/**
 * User: mjoachimiak
 * Date: Oct 23, 2009
 * Time: 4:12:20 PM
 */
public class MarkGrid {


    /**
     * @param data
     * @param ints
     * @param tlabel
     * @return
     */
    public final static String[][] markBlockonGridInt(String[][] data, int[][] ints, String tlabel) {
        for (int i = 0; i < ints.length; i++) {
            for (int j = 0; j < ints[0].length; j++) {
                boolean ref = false;
                int index = ints[i][j];
                //if it is a reference cell
                if (data[i][j].indexOf("true") != -1) {
                    ref = true;
                }
                //true positive
                if (index != 0 && ref) {
                    data[i][j] = "" + index;
                }
                // true and false negative
                else if (index != 0 && !ref) {
                    index = -index;
                    data[i][j] = "" + index;
                }
            }
        }
        return data;
    }

    /**
     * Label cells with location of the block.
     *
     * @param data
     * @param vb
     * @return
     */
    public final static String[][] markBlockonGrid(String[][] data, ValueBlock vb, String label) {
        for (int i = 0; i < vb.genes.length; i++) {
            for (int j = 0; j < vb.exps.length; j++) {
                int newi = vb.genes[i] - 1;
                int newj = vb.exps[j] - 1;
                //System.out.println("markBlockonGrid " + newi + "\t" + newj + "\t" + data[newi][newj] + "\t" + label);
                if (data[newi][newj].indexOf("true") != -1 && label.equals("start")) {
                    //System.out.println("markBlockonGrid true&start " + newi + "\t" + newj + "\t" + data[newi][newj] + "\t" + label);
                    data[newi][newj] = "true/start";
                } else
                    data[newi][newj] = label;
            }
        }
        return data;
    }

    /**
     * Label cells with location of the block.
     *
     * @param data
     * @param blockid
     * @param label
     * @return
     */
    public final static String[][] markBlockonGrid(String[][] data, String blockid, String label) {
        int[][] ids = BlockMethods.ijIDtoIcJc(blockid);
        for (int i = 0; i < ids[0].length; i++) {
            for (int j = 0; j < ids[1].length; j++) {
                int newi = ids[0][i] - 1;
                int newj = ids[1][j] - 1;
                //System.out.println("markBlockonGrid " + newi + "\t" + newj + "\t" + data[newi][newj] + "\t" + label);
                if (data[newi][newj].indexOf("true") != -1 && label.equals("start")) {
                    //System.out.println("markBlockonGrid true&start " + newi + "\t" + newj + "\t" + data[newi][newj] + "\t" + label);
                    data[newi][newj] = "true/start";
                } else
                    data[newi][newj] = label;
            }
        }
        return data;
    }

    /**
     * Increment each cell occupied by the block.
     *
     * @param data
     * @param vb
     * @return
     */
    public final static int[][] sumBlockonGrid(int[][] data, ValueBlock vb) {
        for (int i = 0; i < vb.genes.length; i++) {
            for (int j = 0; j < vb.exps.length; j++) {
                int newi = vb.genes[i] - 1;
                int newj = vb.exps[j] - 1;
                data[newi][newj]++;
            }
        }
        return data;
    }

    /**
     * Increment each cell occupied by the block.
     *
     * @param data
     * @param blockid
     * @return
     */
    public final static int[][] sumBlockonGrid(int[][] data, String blockid) {
        int[][] ids = BlockMethods.ijIDtoIcJc(blockid);
        for (int i = 0; i < ids[0].length; i++) {
            for (int j = 0; j < ids[1].length; j++) {
                int newi = ids[0][i] - 1;
                int newj = ids[1][j] - 1;
                data[newi][newj]++;
            }
        }
        return data;
    }


    /**
     * @param data
     * @param s
     * @param label
     * @return
     */
    public final static String[][] labelGrid(String[][] data, String s, String label) {
        int[][] ids = BlockMethods.ijIDtoIcJc(s);
        for (int i = 0; i < ids[0].length; i++) {
            for (int j = 0; j < ids[1].length; j++) {
                int newi = ids[0][i] - 1;
                int newj = ids[1][j] - 1;
                if (data[newi][newj].length() == 0)
                    data[newi][newj] += label;
                else
                    data[newi][newj] += "_" + label;
            }
        }
        return data;
    }

    /**
     * @param data
     * @return
     */
    /*public final static String[][] markBlockonGridStr(String[][] data, int[][] ints) {
        String[] labels = {
                "0",
                "true0",
                "true1",
                "true2",
                "true3",
                "true4",
                "any1",
                "any2",
                "any3",
                "any4"
        };
        for (int i = 0; i < ints.length; i++) {
            for (int j = 0; j < ints[0].length; j++) {
                boolean ref = false;
                int offset = 0;
                int index = ints[i][j];
                if (data[i][j].equals("true")) {
                    index = 1;
                    ref = true;
                }
                if (!ref) {
                    offset = 4;
                }
                index += offset;
                data[i][j] = labels[index];
            }
        }
        return data;
    }*/

}
