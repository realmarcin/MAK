package util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


public class WriteTextFile {

    public final static void write(String data, String outpath) {

        try {
            PrintWriter out = new PrintWriter(new FileWriter(outpath));
            out.print(data);
            out.close();
        } catch (IOException e) {
            System.out.println("Error creating or writing file " + outpath);
            System.out.println("IOException: " + e.getMessage());
        }

    }


}
