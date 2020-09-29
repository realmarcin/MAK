package mathy;

import util.MoreArray;
import util.TabFile;

import java.util.ArrayList;

/**
 * User: marcinjoachimiak
 * Date: Jun 18, 2007
 * Time: 6:22:31 PM
 */
public class NormMatrix {

    String infile, outfile;

    public NormMatrix(String[] args) {
        infile = args[0];
        outfile = args[1];
        String[][] read = TabFile.readtoArray(infile);
        //System.out.println("readtoArray "+read[2][0]+"\t"+read[3][0]);
        String[] xlabels = read[0];
        String[] ylabels = MoreArray.extractColumnStr(read, 1);
        //System.out.println("extractColumnStr ylabels "+ylabels[2]+"\t"+ylabels[3]);
        ArrayList remove = new ArrayList();
        remove.add(new Integer(1));
        read = MoreArray.removeRows(read, remove);
        read = MoreArray.removeColumns(read, remove);

        double[][] data = MoreArray.convfromString(read);
        data = Matrix.setNegativetoZero(data);
        System.out.println("setNegativetoZero "+data[2][8]+"\t"+data[3][8]);
        data = Matrix.normbyCol(data);
        //System.out.println("normbyCol "+data[2][8]+"\t"+data[3][8]);
        read = MoreArray.toStringWithoutDelim(data);
        //System.out.println("toString "+read[2][8]+"\t"+read[3][8]);
        //System.out.println("extractColumnStr ylabels 2 "+ylabels[2]+"\t"+ylabels[3]);
        ArrayList ylist = MoreArray.convtoArrayList(ylabels);
        ylist.remove(0);
        ylabels = MoreArray.ArrayListtoString(ylist);
        read = MoreArray.insertColumn(read, ylabels, 1);
        //System.out.println("insertColumn "+read[2][8]+"\t"+read[3][8]);
        //System.out.println("insertColumn "+read[2][0]+"\t"+read[3][0]);
        read = MoreArray.insertRow(read, xlabels, 1);
        //System.out.println("insertRow "+read[2][8]+"\t"+read[3][8]);
        TabFile.write(read, infile + "_colnorm.txt");
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 2) {
            NormMatrix ce = new NormMatrix(args);
        } else {
            System.out.println("usage: java mathy.NormMatrix\n" +
                    "<infile>\n" +
                    "<outfile>\n");
        }
    }
}
