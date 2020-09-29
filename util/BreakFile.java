package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * User: marcin
 * Date: Apr 9, 2009
 * Time: 6:25:26 PM
 */
public class BreakFile {
    boolean header = false;
    String header_str;

    /**
     * @param args
     */
    public BreakFile(String[] args) {
        String ret = "";
        int counter = 0;
        int index = 0;
        int lines = Integer.parseInt(args[1]);
        ParsePath pp = new ParsePath(args[0]);

        if (args.length == 3) {
            header = args[2].equals("y") ? true : false;
        }
        try {
            BufferedReader in = new BufferedReader(new FileReader(new File(args[0])));
            String line = in.readLine();
            if (header) {
                header_str = line;
                ret += header_str + "\n";
                line = in.readLine();
            }

            while (line != null) {
                System.out.println(line);
                System.out.println(index + "\t" + counter);
                while (counter < lines && line != null) {
                    ret += line + "\n";
                    line = in.readLine();
                    counter++;
                }

                if (counter == lines || line == null) {
                    String outpath = args[0] + "_" + index + "." + pp.getExt();
                    System.out.println("outpath " + outpath);
                    TextFile.write(ret, outpath);
                    ret = header_str + "\n";
                    counter = 0;
                    index++;
                }
            }
            in.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * @param args
     */
    public final static void main(String[] args) {
        if (args.length == 2 || args.length == 3) {
            BreakFile cde = new BreakFile(args);
        } else {
            System.out.println("syntax: util.BreakFile\n" +
                    "<input file>\n" +
                    "<lines>\n" +
                    "<OPTIONAL y/n use 1st line as repeated header>"
            );
        }
    }

}
