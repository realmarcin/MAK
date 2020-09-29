package bioobj;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by marcin
 * Date: Jan 26, 2006
 * Time: 11:18:48 AM
 */
public class SearchMatch {

    public int pos;
    public int start;
    public int stop;
    public int total_hits;
    public double score;
    public String id;
    public String name;
    public String seq;

    /**
     *
     */
    public SearchMatch() {
        start = -1;
        stop = -1;
        total_hits = -1;
        score = Double.NaN;
        id = null;
        name = null;
        seq = null;
    }


    /**
     * 
     * @param s
     * @return
     */
    public int compareTo(Object s) {
        if (((SearchMatch) s).start > start)
            return -1;
        else if (((SearchMatch) s).start == start)
            return 0;
        else
            return 1;
    }

    /**
     * @return
     */
    public String toString() {
        return "" + id + "\t" + name + "\t" + start + "\t" + stop + "\t" + pos + "\t" +
                total_hits + "\t" + score + "\t" + seq;
    }

    /**
     * @param data
     * @param outpath
     */
    public final static void writeSearchMatchData(ArrayList data, String outpath) {

        try {
            PrintWriter out = new PrintWriter(new FileWriter(outpath), true);

            for (int j = 0; j < data.size(); j++) {
                SearchMatch sm = (SearchMatch) (data.get(j));
                out.println(sm.toString());
            }

            out.close();
        } catch (IOException e) {
            System.out.println("Error creating or writing file " + outpath);
            System.out.println("IOException: " + e.getMessage());
        }
    }
}
