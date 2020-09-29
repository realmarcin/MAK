package util;

import java.util.ArrayList;

/**
 * Created by Marcin
 * Date: Oct 27, 2005
 * Time: 10:50:15 PM
 */
public class PadCountVector {


    int max = 34351;     // max length from real peps in 100% nonredundant ensembl+mg+nr+tigr

    public PadCountVector(String[] args) {

        String in = args[0];
        String out = args[1];

        String[][] get = TabFile.readtoArray(in);


        for (int i = 0; i < 4; i++) {

            ArrayList[] newsto = new ArrayList[2];
            double[] lengths = MoreArray.extractColumn(get, i * 2);
            double[] counts = MoreArray.extractColumn(get, i * 2 + 1);

            double count = lengths[0];

            while (count < max) {

                if (lengths[(int) count] > count) {


                } else {

                    //newsto[1].add()
                }

                //for(int j=0;j<lengths.length;j++) {

                count++;
            }
        }

    }


    public static void main(String[] args) {

        if (args.length == 2) {
            PadCountVector pcv = new PadCountVector(args);
        } else {
            System.out.println("syntax: bioutil.PadCountVector <in file> <out file>");
        }
    }
}
