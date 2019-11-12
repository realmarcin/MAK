package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Created by marcin
 * Date: Feb 3, 2006
 * Time: 1:55:35 PM
 */
public class ASCII {

    public final static boolean detect(String f) {

        try {

            BufferedReader in = new BufferedReader(new FileReader(new File(f)));
            String line = in.readLine();
            char[] chars = new char[line.length()];
            line.getChars(0, line.length(), chars, 0);

            int k = 0;

            while (line.length() == 0) {
                line = in.readLine();
                chars = new char[line.length()];
                line.getChars(0, line.length(), chars, 0);
            }
            //System.out.println("detect "+line);
            for (char c = chars[k]; k < chars.length; k++) {
                if (c > 128) {
                    System.out.println("Non ASCII! " + c);
                    return false;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

}
