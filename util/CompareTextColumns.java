package util;

import java.util.ArrayList;

/**
 * User: marcinjoachimiak
 * Date: Jun 20, 2007
 * Time: 6:44:47 PM
 */
public class CompareTextColumns {

    String[] names;

    public CompareTextColumns(String[] args) {

        String[][] data = TabFile.readtoArray(args[0]);
        System.out.println("normalize  " + data[2][4] + "\t" + data[2][6]);
        ArrayList list = new ArrayList();
        list.add(new Integer(1));
        names = data[0];
        data = MoreArray.removeRows(data, list);

        double[][] compare = compareColumns(data);
        String[][] outdata = MoreArray.toString(compare, "", "");
        //outdata = MoreArray.insertRow(outdata, names, 1);
        //outdata = MoreArray.insertColumn(outdata, names, 1);
                                                                             
        TabFile.write(outdata, args[0] + "_dists.txt");
    }


    /**
     * Returns a distance matrix based on pairwise column comparisons.
     *
     * @param data
     * @return
     */
    public double[][] compareColumns(String[][] data) {
        ArrayList[] all = new ArrayList[data[0].length];
        double[][] dist = MoreArray.initArray(all.length, all.length, 0.0);
        for (int i = 0; i < all.length; i++) {
            all[i] = MoreArray.convtoArrayList(MoreArray.extractColumnStr(data, i + 1));
        }

        for (int i = 0; i < all.length; i++) {
            ArrayList cur = all[i];

            int isize = MoreArray.getArrayNonEmptySize(cur);
            for (int j = i; j < all.length; j++) {
                System.out.println("search list " + names[i]);
                System.out.println("test list " + names[j]);
                if (i != j) {
                    ArrayList test = all[j];

                    int jsize = MoreArray.getArrayNonEmptySize(test);
                    double norm = Math.min(isize, jsize);
                    //System.out.println("normalize  "+i+"\t"+j+"\t"+norm);
                    for (int k = 0; k < cur.size(); k++) {
                        String curstr = (String) cur.get(k);
                        int ind = MoreArray.getArrayInd(test, curstr);
                        if (ind != -1)
                            dist[i][j]++;
                        else {
                            System.out.println("did not find " + curstr + "\tfrom " + names[i] + "\tto " + names[j]);
                        }
                    }
                    System.out.println("normalize  " + i + "\t" + j + "\t" + dist[i][j] + "\t" + norm);
                    dist[i][j] /= norm;
                    dist[j][i] = dist[i][j];
                } else
                    dist[i][i] = 1;
            }
        }
        return dist;
    }

    /**
     * Main function
     */
    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("SYNTAX: java util.CompareTextColumns <in file>");
        } else if (args.length == 1) {
            CompareTextColumns kr = new CompareTextColumns(args);
        }
    }
}
