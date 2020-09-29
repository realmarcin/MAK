package mathy;

import util.MoreArray;
import util.StringUtil;
import util.TabFile;

import java.io.File;

/**
 * User: marcin
 * Date: May 22, 2008
 * Time: 3:01:19 PM
 */
public class DeleteNullColumns {

    /**
     * @param args
     */
    public DeleteNullColumns(String[] args) {
        File test = new File(args[0]);
        if (test.isDirectory()) {
            String[] list = test.list();
            for (int j = 0; j < list.length; j++) {
                run(args[0] + "/" + list[j]);
            }
        } else {
            run(args[0]);
        }
    }

    /**
     * @param file
     */
    private void run(String file) {
        String[][] in = TabFile.readtoArray(file);
        for (int j = 0; j < in[0].length; j++) {
            String[] col = MoreArray.extractColumnStr(in, j+1);
            /*System.out.println("run "+j);
            MoreArray.printArray(col);*/
            int count = StringUtil.countOccur(col, "NA");
            if (count == col.length) {
                in = MoreArray.removeColumn(in, j+1);
                j--;
            }
        }
        TabFile.write(in, file + "_nonullcol.txt");
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 1) {
            DeleteNullColumns rm = new DeleteNullColumns(args);
        } else {
            System.out.println("syntax: java mathy.DeleteNullColumns <input file>");
        }
    }
}
