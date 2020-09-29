package util;

import java.io.File;
import java.util.ArrayList;

/**
 * User: marcin
 * Date: Jan 15, 2009
 * Time: 9:12:14 PM
 */
public class BindFiles {

    /**
     *
     */
    public BindFiles(String[] args) {
        File f = new File(args[0]);
        String[] list = f.list();
        String[][] master = null;
        for (int i = 0; i < list.length; i++) {
            if (!list[i].equalsIgnoreCase(".DS_Store")) {
                String[][] cur = TabFile.readtoArray(args[0] + "/" + list[i]);
                if (master == null) {
                    master = cur;
                    ArrayList a = new ArrayList();
                    //a.add(new Integer(1));
                    //a.add(new Integer(3));
                    master = MoreArray.removeColumns(master, a);
                } else {
                    master = MoreArray.insertColumnPad(master, MoreArray.extractColumnStr(cur, 2), master[0].length);
                }
            }
        }
        String header = "\t" + MoreArray.toString(list, "\t") + "\n";
        TabFile.writeWithHeader(master, args[1], header);
    }

    /**
     * @param args
     */

    public static void main(String[] args) {

        if (args.length == 2) {
            BindFiles ce = new BindFiles(args);
        } else {
            System.out.println("usage: java util.BindFiles\n" +
                    "<dir>\n" +
                    "<outfile>\n");
        }
    }

}
