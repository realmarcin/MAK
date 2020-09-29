package util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Log {

    long start;
    long end;


    public final static void writeLog(String s, String f) {

        try {
            PrintWriter out = new PrintWriter(new FileWriter(f));

            out.print(f);
            out.close();
        } catch (IOException e) {
        }


    }

}
