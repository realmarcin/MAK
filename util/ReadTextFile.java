package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Class with a method to read a text file into a String.
 */
public class ReadTextFile {


    public final static String read(String f) {

        String ret = new String("");

        try {

            BufferedReader in = new BufferedReader(new FileReader(new File(f)));
            String line = in.readLine();

            while (line != null) {

                ret += line;
                line = in.readLine();
            }

            in.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        return ret;
    }

    /**
     * Reads the file into an ArrayList, one String object per line.
     *
     * @param f
     * @return
     */
    public final static ArrayList readtoList(String f) {

        ArrayList ret = new ArrayList();

        int tot = 0;

//System.out.println("reading "+f);

        try {

            BufferedReader in = new BufferedReader(new FileReader(new File(f)));
            String line = in.readLine();

            while (line != null) {
                line = in.readLine();
                tot++;
            }

            in.close();

//System.out.println("number of lines "+tot);

            in = new BufferedReader(new FileReader(new File(f)));
            line = in.readLine();

            int c = 0;
            while (line != null) {

                ret.add((String) line);
//System.out.println(c+"\t"+line);
                line = in.readLine();
                c++;
            }

            in.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        return ret;
    }

    /**
     * @param f
     * @return
     */
    public final static String[] readtoArray(String f) {

        String[] ret = new String[0];
        int tot = 0;


        try {

            BufferedReader in = new BufferedReader(new FileReader(new File(f)));
            String line = in.readLine();

            while (line != null) {

                tot++;
            }

            in.close();
            ret = new String[tot];

            in = new BufferedReader(new FileReader(new File(f)));
            line = in.readLine();

            int c = 0;
            while (line != null) {

                ret[c] = line;
                line = in.readLine();
                c++;
            }

            in.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        return ret;
    }

}
