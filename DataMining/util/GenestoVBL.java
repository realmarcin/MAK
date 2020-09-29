package DataMining.util;

import DataMining.MINER_STATIC;
import DataMining.ValueBlock;
import DataMining.ValueBlockList;
import mathy.SimpleMatrix;
import util.StringUtil;
import util.TabFile;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: 9/27/15
 * Time: 12:09 AM
 */
public class GenestoVBL {

    /**
     * @param args
     */
    public GenestoVBL(String[] args) {

        File dir = new File(args[0]);
        String[] list = dir.list();

        SimpleMatrix data = new SimpleMatrix(args[1]);
        System.out.println("dim data " + data.data.length + "\t" + data.data[0].length);

        ValueBlockList vbl = new ValueBlockList();
        for (int i = 0; i < list.length; i++) {
            if (!list[i].equals(".DS_Store")) {
                String path = dir.getAbsolutePath() + "/" + list[i];
                String[][] sarray = TabFile.readtoArray(path);

                System.out.println("path " + path);
                System.out.println("dim " + sarray.length + "\t" + sarray[0].length);

                ArrayList gindex = new ArrayList();
                for (int j = 0; j < sarray.length; j++) {
                    String query = sarray[j][0].replace("\"", "");
                    if (!query.equalsIgnoreCase("x")) {
                        int index = StringUtil.getFirstEqualsIndex(data.ylabels, query);
                        if (index != -1) {
                            gindex.add(index + 1);
                        }
                        //to handle duplicate row names a la R
                        else if (query.endsWith(".1")) {
                            query = query.substring(0, query.length() - 2);
                            int[] indices = StringUtil.equalsIndex(data.ylabels, query);
                            //MoreArray.printArray(indices);
                            if (indices[1] != -1)
                                gindex.add(indices[1] + 1);
                            else
                                System.out.println("no match 1 " + query);
                        } else if (query.endsWith(".2")) {
                            query = query.substring(0, query.length() - 2);
                            int[] indices = StringUtil.equalsIndex(data.ylabels, query);
                            //MoreArray.printArray(indices);
                            if (indices[2] != -1)
                                gindex.add(indices[2] + 1);
                            else
                                System.out.println("no match 2 " + query);

                        } else
                            System.out.println("no match " + query);
                    }
                }

                ArrayList eindex = new ArrayList();
                for (int j = 0; j < sarray[0].length; j++) {
                    gindex.add(j + 1);
                }

                ValueBlock cur = new ValueBlock(gindex, eindex);
                vbl.add(cur);
            }
        }

        String header = "#Converted from sets of gene ids/names\n";
        try {
            String s = args[0] + ".vbl";
            System.out.println("writing " + s);
            String ss = vbl.toString(header + "\n" + MINER_STATIC.HEADER_VBL);
            util.TextFile.write(ss, s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 2) {
            GenestoVBL cbr = new GenestoVBL(args);
        } else {
            System.out.println("syntax: DataMining.util.GenestoVBL <dir of single column gene id/name files> <input data>");
        }
    }
}
