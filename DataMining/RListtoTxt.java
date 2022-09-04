package DataMining;

import util.StringUtil;
import util.TextFile;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: May 4, 2011
 * Time: 2:29:17 PM
 */
public class RListtoTxt {


    /**
     * @param args
     */
    public RListtoTxt(String[] args) {

        File dir = new File(args[0]);
        if (dir.isDirectory()) {
            String[] list = dir.list();

            for (int i = 0; i < list.length; i++) {

                String f = args[0] + "/" + list[i];
                if (list[i].indexOf(".") != 0) {
                    doOne(list[i], f);
                }
            }
        } else {
            doOne(args[0], args[0]);
        }

    }

    /**
     * @param s
     * @param f
     */
    private void doOne(String s, String f) {
        System.out.println(f);
        ArrayList pass = TextFile.readtoList(f);
        ArrayList out = new ArrayList();
        for (int j = 0; j < pass.size(); ) {

            String cur1 = (String) pass.get(j + 1);
            System.out.println("first " + j + "\t" + (j + 1) + "\t" + cur1);

            int start = cur1.indexOf("] ") + 2;
            String store1 = StringUtil.replace(cur1.substring(start, cur1.length()), "\"", "");
            store1 = StringUtil.replace(store1, "  ", " ");
            out.add(store1);

            String cur2 = (String) pass.get(j + 4);
            String cur2b = (String) pass.get(j + 5);
            System.out.println("second " + j + "\t" + (j + 4) + "\t" + cur2);
            System.out.println("secondB " + j + "\t" + (j + 5) + "\t" + cur2b);
            int offset = 0;

            start = cur2.indexOf("] ") + 2;

            String store2 = StringUtil.replace(cur2.substring(start, cur2.length()), "\"", "");
            if (cur2b.indexOf("[") == 0) {
                offset = 1;
                int start2 = cur2b.indexOf("] ") + 2;
                store2 += "," + StringUtil.replace(cur2b.substring(start2, cur2b.length()), "\"", "");
            }
            store2 = StringUtil.replace(store2, "  ", " ");
            store2 = StringUtil.replace(store2, "  ", " ");
            out.add(store2);

            String cur3 = (String) pass.get(j + 7 + offset);
            String cur3b = (String) pass.get(j + 8 + offset);
            System.out.println("third " + j + "\t" + (j + 7) + "\t" + cur3);
            start = cur3.indexOf("] ") + 2;

            String store3 = StringUtil.replace(cur3.substring(start, cur3.length()), "\"", "");
            store3 = StringUtil.replace(store3, "  ", " ");
            if (cur3b.indexOf("[") == 0) {
                offset = 2;
                int start3 = cur3b.indexOf("] ") + 2;
                store3 += "," + StringUtil.replace(cur3b.substring(start3, cur3b.length()), "\"", "");
            }
            store3 = StringUtil.replace(store3, "  ", " ");
            store3 = StringUtil.replace(store3, "  ", " ");
            out.add(store3);

            j += 9 + offset;
        }
        TextFile.write(out, s + ".reformat");
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 1) {
            RListtoTxt arg = new RListtoTxt(args);
        } else {
            System.out.println("syntax: java DataMining.RListtoTxt\n" +
                    "<dir of outputs or file>");
        }
    }
}
