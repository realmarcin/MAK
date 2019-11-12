package util;


import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class MakeRepeatText {

    String last;// = " | wc";
    int num;
    String out = "repeat.txt";
    String first = null;
    String[] list = null;
    boolean stdout = false;


    public MakeRepeatText(String[] a) {

        num = Integer.parseInt(a[0]);
        first = a[1];
        last = a[2];

        if (a.length >= 4) {
            //System.out.println("reading list");
            list = util.TextFile.readtoArray(a[3]);
            System.out.println(list.length + "\t" + list[0]);
        }
        if (a.length == 5) {
            stdout = true;
        }

        try {
            PrintWriter piw = new PrintWriter(new FileWriter(out));
            if (list == null) {
                for (int i = 0; i < num; i++) {
                    piw.println(first + i + last);
                }
            } else {
                for (int i = 0; i < list.length; i++) {
                    ParsePath pp = new ParsePath(list[i]);
                    String cmd = first + list[i] + last;// + " > " + pp.getName() + ".sql";
                    if (stdout) {
                        cmd += "" + list[i] + ".out";
                    }
                    System.out.println(cmd);
                    piw.println(cmd);
                }
            }
            piw.close();
        } catch (IOException e) {
            System.out.println("Problem writing file " + out);
        }
    }

    /**
     * @param args
     */
    public final static void main(String[] args) {

        if (args.length == 3 || args.length == 4 || args.length == 5) {

            MakeRepeatText mrt = new MakeRepeatText(args);
        } else {

            System.out.println("syntax: java util.MakeRepeatText <num repeats> <prefix in \"\"> <suffix in \"\"> <optional input list file> <optional y for custom stdout>");
        }

    }
}