package util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class writeFile {

    public final static void write(String name, String tdata) {
        System.out.println("writing   " + name);
        try {
            PrintWriter out = new PrintWriter(new FileWriter(name), true);
            out.println(tdata);
            out.close();
        } catch (IOException e) {
            System.out.println("error creating or writing file");
        }
    }

}
