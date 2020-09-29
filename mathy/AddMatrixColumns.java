package mathy;

import util.MoreArray;
import util.TabFile;

/**
 * User: marcin
 * Date: May 22, 2008
 * Time: 3:34:04 PM
 */
public class AddMatrixColumns {
    int spacing, number;
    String[] filler;

    /**
     * @param args
     */
    public AddMatrixColumns(String[] args) {
        String file = args[0];
        spacing = Integer.parseInt(args[1]);
        number = Integer.parseInt(args[2]);
        run(file);
    }

    /**
     * @param file
     */
    private void run(String file) {
        String[][] in = TabFile.readtoArray(file);
        filler = MoreArray.initArray(in.length, "NA");
        int origsize = in[0].length;
        int added = 0;
        for (int j = 0; j < origsize; j++) {
            if ((j - added) % spacing == 0) {
                int count = 0;
                while (count < number) {
                    in = MoreArray.insertColumn(in, filler, j + 2 + added);
                    count++;
                    added++;
                }
            }
        }
        TabFile.write(in, file + "_addedcol.txt");
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 3) {
            AddMatrixColumns rm = new AddMatrixColumns(args);
        } else {
            System.out.println("syntax: java mathy.AddMatrixColumns <input file> <spacing> <number>");
        }
    }

}
