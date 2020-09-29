package util;

import java.util.ArrayList;

/**
 * User: marcinjoachimiak
 * Date: May 2, 2007
 * Time: 11:31:14 AM
 */
public class JoinFiles {

    String in1, in2;
    int col1, col2;
    String[][] data1, data2;
    String path;

    boolean left = false;

    boolean update = false;

    /**
     * @param args
     */
    public JoinFiles(String[] args) {
        in1 = args[0];
        col1 = Integer.parseInt(args[1]);
        in2 = args[2];
        col2 = Integer.parseInt(args[3]);
        if (args.length == 5)
            left = true;
        run();
    }

    /**
     * @param i1
     * @param c1
     * @param i2
     * @param c2
     */
    public JoinFiles(String i1, int c1, String p, String i2, int c2) {
        in1 = i1;
        col1 = c1;
        path = p;
        in2 = i2;
        col2 = c2;

        run();
    }

    /**
     * @param i1
     * @param c1
     * @param i2
     * @param c2
     */
    public JoinFiles(String i1, int c1, String p, String i2, int c2, boolean type) {
        in1 = i1;
        col1 = c1;
        path = p;
        in2 = i2;
        col2 = c2;
        left = type;
        run();
    }

    /**
     * @param i2
     * @param type
     */
    public void update(String p, String i2, int c2, boolean type) {
        update = true;
        path = p;
        in2 = i2;
        col2 = c2;
        left = type;
        run();
    }

    /**
     *
     */
    private void run() {
        //System.out.println("JoinFiles left " + left);
        if (!update)
            data1 = TabFile.readtoArray(in1);
        if (path == null)
            data2 = TabFile.readtoArray(in2);
        else
            data2 = TabFile.readtoArray(path + "/" + in2);
        /*System.out.println("JoinFiles data1 " + data1);
        MoreArray.printArray(data1);*/
        System.out.println("JoinFiles 1 " + in1);
        System.out.println("JoinFiles 1 " + data1.length + "\t" + data1[0].length);
        System.out.println("JoinFiles 2 " + in2);
        System.out.println("JoinFiles 2 " + data2.length + "\t" + data2[0].length);
        ArrayList out = new ArrayList();


        String[] ar1 = MoreArray.extractColumnStr(data1, col1 + 1);
        String[] ar2 = MoreArray.extractColumnStr(data2, col2 + 1);
        System.out.println("ar1 " + col1 + "\t" + MoreArray.toString(ar1, ","));
        System.out.println("ar2 " + col2 + "\t" + MoreArray.toString(ar2, ","));

        for (int i = 0; i < data1.length; i++) {
            String s = data1[i][col1];
            s = StringUtil.replace(s, "\"", "");
            int index = searchColumn(s);
            //System.out.println("JoinFiles " + i + "\t" + index + "\t" + data1[i][col1]);
            if (index != -1) {
                String[] add = new String[data1[i].length + data2[index].length];
                System.arraycopy(data1[i], 0, add, 0, data1[i].length);
                System.arraycopy(data2[index], 0, add, data1[i].length, data2[index].length);
                out.add(add);
            } else {
                if (left) {
                    String[] add = new String[2 * data1[i].length];
                    System.arraycopy(data1[i], 0, add, 0, data1[i].length);
                    String[] blank = new String[data1[i].length];
                    System.arraycopy(blank, 0, add, data1[i].length, blank.length);
                    /*System.out.println("JoinFiles add");
                    MoreArray.printArray(add);*/
                    out.add(add);
                }
            }
        }

        ParsePath p1 = new ParsePath(in1);
        ParsePath p2 = new ParsePath(in2);
        String outfile = p1.getName() + "_" + p2.getName() + ".join.txt";
        if (left)
            outfile = p1.getName() + "_" + p2.getName() + ".leftjoin.txt";

        TextFile.writeStringArray(out, null, outfile);
    }

    /**
     * @return
     */
    private int searchColumn(String k) {
        for (int i = 0; i < data2.length; i++) {
            if (data2[i][col2].equals(k))
                return i;
        }
        return -1;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {

        if (args.length == 4 || args.length == 5) {
            JoinFiles ce = new JoinFiles(args);
        } else {
            System.out.println("usage: java util.JoinFiles\n" +
                    "<file 1>\n" +
                    " <column f1 (index starts at 0)>\n" +
                    " <file 2>\n" +
                    " <column f2 (index starts at 0)>\n" +
                    " <optional: leftjoin>\n");
            System.out.println("usage: Assumes that the specified column labels are unique in file 2");
        }
    }
}
