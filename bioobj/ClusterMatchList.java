package bioobj;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * User: marcin
 * Date: Feb 18, 2007
 * Time: 1:46:12 PM
 */
public class ClusterMatchList extends ArrayList {

    /**
     * @return
     */
    public String[] toStringArray() {

        String[] ret = new String[size()];
        for (int i = 0; i < size(); i++) {
            ret[i] = ((String)get(i)).toString();
        }
        return ret;
    }

    /**
     * @param f
     */
    public void writeFile(String f) {

        try {
            PrintWriter out = new PrintWriter(new FileWriter(f), true);
            for (int j = 0; j < size(); j++) {
                out.println(((ClusterMatch)get(j)).toStringTrunc());
            }
            out.close();
        } catch (IOException e) {
            System.out.println("Error creating or writing file " + f);
            System.out.println("IOException: " + e.getMessage());
        }
    }
}

