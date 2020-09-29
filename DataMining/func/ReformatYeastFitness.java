package DataMining.func;

import util.TabFile;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: Apr 7, 2011
 * Time: 3:03:27 PM
 */
public class ReformatYeastFitness {

    /**
     * @param args
     */
    public ReformatYeastFitness(String[] args) {

        String[][] data = TabFile.readtoArray(args[0]);
        for (int i = 1; i < data.length; i++) {
            int index = data[i][0].indexOf(":chr");
            System.out.println(index + "\t" + data[i][0]);
            data[i][0] = data[i][0].substring(0, index);
        }
        System.out.println("writing");
        TabFile.write(data, args[0] + "_reformat.txt");
        System.out.println("wrpte");
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 1) {
            ReformatYeastFitness rm = new ReformatYeastFitness(args);
        } else {
            System.out.println("syntax: java DataMining.func.ReformatYeastFitness\n" +
                    "<fitness file>"
            );
        }
    }
}
